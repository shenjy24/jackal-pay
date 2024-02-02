package com.jonas.pay.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.annotations.VisibleForTesting;
import com.jonas.pay.channel.PayClient;
import com.jonas.pay.config.pay.PayProperties;
import com.jonas.pay.config.response.BizException;
import com.jonas.pay.config.response.ErrorCode;
import com.jonas.pay.constant.notify.PayNotifyTypeEnum;
import com.jonas.pay.constant.order.PayOrderStatusEnum;
import com.jonas.pay.domain.PayOrderDomain;
import com.jonas.pay.repository.convert.order.PayOrderConvert;
import com.jonas.pay.repository.dto.order.PayOrderRespDTO;
import com.jonas.pay.repository.dto.order.PayOrderUnifiedReqDTO;
import com.jonas.pay.repository.entity.PayChannelEntity;
import com.jonas.pay.repository.entity.PayOrderEntity;
import com.jonas.pay.repository.entity.PayOrderExtensionEntity;
import com.jonas.pay.repository.qo.order.PayOrderSubmitQo;
import com.jonas.pay.repository.redis.PayNoRedisDAO;
import com.jonas.pay.repository.vo.order.PayOrderSubmitVo;
import com.jonas.pay.util.GsonUtil;
import com.jonas.pay.util.LocalDateTimeUtil;
import com.jonas.pay.util.MoneyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * PayOrderService
 *
 * @author shenjy
 * @time 2024/1/30 13:06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayOrderService {

    private final PayOrderDomain payOrderDomain;
    private final PayNoRedisDAO noRedisDAO;
    private final PayProperties payProperties;

    private final PayAppService payAppService;
    private final PayChannelService payChannelService;
    private final PayNotifyService payNotifyService;

    public PayOrderSubmitVo submitOrder(PayOrderSubmitQo qo, String userIp) {
        // 1.1 获得 PayOrder ，并校验其是否存在
        PayOrderEntity order = validateOrderCanSubmit(qo.getExtensionId());
        // 1.2 校验支付渠道是否有效
        PayChannelEntity channel = validateChannelCanSubmit(order.getAppId(), qo.getChannelCode());
        PayClient client = payChannelService.getPayClient(channel.getChannelId());

        // 2. 插入 PayOrderExtensionEntity
        String no = noRedisDAO.generate(payProperties.getOrderNoPrefix());
        PayOrderExtensionEntity orderExtension = PayOrderConvert.INSTANCE.convert(qo, userIp)
                .setPayOrderId(order.getPayOrderId()).setOutTradeNo(no).setChannelId(channel.getChannelId())
                .setChannelCode(channel.getCode()).setStatus(PayOrderStatusEnum.WAITING.getStatus());
        payOrderDomain.savePayOrderExtension(orderExtension);

        // 3. 调用三方接口
        PayOrderUnifiedReqDTO unifiedOrderReqDTO = PayOrderConvert.INSTANCE.convert2(qo, userIp)
                // 商户相关的字段
                .setOutTradeNo(orderExtension.getOutTradeNo()) // 注意，此处使用的是 PayOrderExtensionDO.no 属性！
                .setSubject(order.getSubject()).setBody(order.getBody())
                .setNotifyUrl(genChannelOrderNotifyUrl(channel))
                .setReturnUrl(qo.getReturnUrl())
                // 订单相关字段
                .setPrice(order.getPrice()).setExpireTime(order.getExpireTime());
        PayOrderRespDTO unifiedOrderResp = client.unifiedOrder(unifiedOrderReqDTO);

        // 4. 如果调用直接支付成功，则直接更新支付单状态为成功。例如说：付款码支付，免密支付时，就直接验证支付成功
        if (unifiedOrderResp != null) {
            getSelf().notifyOrder(channel, unifiedOrderResp);
            // 如有渠道错误码，则抛出业务异常，提示用户
            if (StrUtil.isNotEmpty(unifiedOrderResp.getChannelErrorCode())) {
                log.error("发起支付报错，错误码：{}，错误提示：{}", unifiedOrderResp.getChannelErrorCode(),
                        unifiedOrderResp.getChannelErrorMsg());
                throw new BizException(ErrorCode.ORDER_ERROR8);
            }
            // 此处需要读取最新的状态
            order = payOrderDomain.getPayOrderById(order.getPayOrderId());
        }
        return PayOrderConvert.INSTANCE.convert(order, unifiedOrderResp);
    }

    public void notifyOrder(Long channelId, PayOrderRespDTO notify) {
        // 校验支付渠道是否有效
        PayChannelEntity channel = payChannelService.validPayChannel(channelId);
        // 更新支付订单为已支付
        getSelf().notifyOrder(channel, notify);
    }

    /**
     * 通知并更新订单的支付结果
     *
     * @param channel 支付渠道
     * @param notify  通知
     */
    @Transactional(rollbackFor = Exception.class)
    public void notifyOrder(PayChannelEntity channel, PayOrderRespDTO notify) {
        // 情况一：支付成功的回调
        if (PayOrderStatusEnum.isSuccess(notify.getStatus())) {
            notifyOrderSuccess(channel, notify);
            return;
        }
        // 情况二：支付失败的回调
        if (PayOrderStatusEnum.isClosed(notify.getStatus())) {
            notifyOrderClosed(channel, notify);
        }
        // 情况三：WAITING：无需处理
        // 情况四：REFUND：通过退款回调处理
    }

    private void notifyOrderSuccess(PayChannelEntity channel, PayOrderRespDTO notify) {
        // 1. 更新 PayOrderExtension 支付成功
        PayOrderExtensionEntity orderExtension = updateOrderSuccess(notify);
        // 2. 更新 PayOrder 支付成功
        Boolean paid = updateOrderSuccess(channel, orderExtension, notify);
        if (paid) { // 如果之前已经成功回调，则直接返回，不用重复记录支付通知记录；例如说：支付平台重复回调
            return;
        }

        // 3. 插入支付通知记录
        payNotifyService.createPayNotifyTask(PayNotifyTypeEnum.ORDER.getType(), orderExtension.getPayOrderId());
    }

    /**
     * 更新 PayOrderExtensionDO 支付成功
     *
     * @param notify 通知
     * @return PayOrderExtensionDO 对象
     */
    private PayOrderExtensionEntity updateOrderSuccess(PayOrderRespDTO notify) {
        // 1. 查询 PayOrderExtension
        PayOrderExtensionEntity orderExtension = payOrderDomain.getPayOrderExtensionByOutTradeNo(notify.getOutTradeNo());
        if (orderExtension == null) {
            throw new BizException(ErrorCode.CHANNEL_ERROR1);
        }
        if (PayOrderStatusEnum.isSuccess(orderExtension.getStatus())) { // 如果已经是成功，直接返回，不用重复更新
            log.info("[updateOrderExtensionSuccess][orderExtension({}) 已经是已支付，无需更新]", orderExtension.getOrderExtensionId());
            return orderExtension;
        }
        if (ObjectUtil.notEqual(orderExtension.getStatus(), PayOrderStatusEnum.WAITING.getStatus())) { // 校验状态，必须是待支付
            throw new BizException(ErrorCode.ORDER_ERROR7);
        }

        // 2. 更新 PayOrderExtension
        PayOrderExtensionEntity payOrderExtension = new PayOrderExtensionEntity()
                .setStatus(PayOrderStatusEnum.SUCCESS.getStatus())
                .setChannelNotifyData(GsonUtil.toJson(notify));
        int updateCounts = payOrderDomain.updatePayOrderExtensionByIdAndStatus(orderExtension.getChannelId(),
                orderExtension.getStatus(), payOrderExtension);
        if (updateCounts == 0) { // 校验状态，必须是待支付
            throw new BizException(ErrorCode.ORDER_ERROR7);
        }
        log.info("[updateOrderExtensionSuccess][orderExtension({}) 更新为已支付]", orderExtension.getOrderExtensionId());
        return orderExtension;
    }

    /**
     * 更新 PayOrderDO 支付成功
     *
     * @param channel        支付渠道
     * @param orderExtension 支付拓展单
     * @param notify         通知回调
     * @return 是否之前已经成功回调
     */
    private Boolean updateOrderSuccess(PayChannelEntity channel, PayOrderExtensionEntity orderExtension,
                                       PayOrderRespDTO notify) {
        // 1. 判断 PayOrderDO 是否处于待支付
        PayOrderEntity order = payOrderDomain.getPayOrderById(orderExtension.getPayOrderId());
        if (order == null) {
            throw new BizException(ErrorCode.ORDER_ERROR1);
        }
        if (PayOrderStatusEnum.isSuccess(order.getStatus()) // 如果已经是成功，直接返回，不用重复更新
                && Objects.equals(order.getOrderExtensionId(), orderExtension.getOrderExtensionId())) {
            log.info("[updateOrderExtensionSuccess][order({}) 已经是已支付，无需更新]", order.getPayOrderId());
            return true;
        }
        if (!PayOrderStatusEnum.WAITING.getStatus().equals(order.getStatus())) { // 校验状态，必须是待支付
            throw new BizException(ErrorCode.ORDER_ERROR3);
        }

        // 2. 更新 PayOrder
        int updateCounts = payOrderDomain.updatePayOrderByIdAndStatus(order.getPayOrderId(), PayOrderStatusEnum.WAITING.getStatus(),
                new PayOrderEntity().setStatus(PayOrderStatusEnum.SUCCESS.getStatus())
                        .setChannelId(channel.getChannelId())
                        .setChannelCode(channel.getCode())
                        .setSuccessTime(notify.getSuccessTime())
                        .setOrderExtensionId(orderExtension.getOrderExtensionId())
                        .setOutTradeNo(orderExtension.getOutTradeNo())
                        .setChannelOrderNo(notify.getChannelOrderNo())
                        .setChannelUserId(notify.getChannelUserId())
                        .setChannelFeeRate(channel.getFeeRate())
                        .setChannelFeePrice(MoneyUtil.calculateRatePrice(order.getPrice(), channel.getFeeRate()))
        );
        if (updateCounts == 0) { // 校验状态，必须是待支付
            throw new BizException(ErrorCode.ORDER_ERROR3);
        }
        log.info("[updateOrderExtensionSuccess][order({}) 更新为已支付]", order.getPayOrderId());
        return false;
    }

    private void notifyOrderClosed(PayChannelEntity channel, PayOrderRespDTO notify) {
        updateOrderExtensionClosed(channel, notify);
    }

    private void updateOrderExtensionClosed(PayChannelEntity channel, PayOrderRespDTO notify) {
        // 1. 查询 PayOrderExtensionDO
        PayOrderExtensionEntity orderExtension = payOrderDomain.getPayOrderExtensionByOutTradeNo(notify.getOutTradeNo());
        if (orderExtension == null) {
            throw new BizException(ErrorCode.ORDER_ERROR6);
        }
        if (PayOrderStatusEnum.isClosed(orderExtension.getStatus())) { // 如果已经是关闭，直接返回，不用重复更新
            log.info("[updateOrderExtensionClosed][orderExtension({}) 已经是支付关闭，无需更新]", orderExtension.getOrderExtensionId());
            return;
        }
        // 一般出现先是支付成功，然后支付关闭，都是全部退款导致关闭的场景。这个情况，我们不更新支付拓展单，只通过退款流程，更新支付单
        if (PayOrderStatusEnum.isSuccess(orderExtension.getStatus())) {
            log.info("[updateOrderExtensionClosed][orderExtension({}) 是已支付，无需更新为支付关闭]", orderExtension.getOrderExtensionId());
            return;
        }
        if (ObjectUtil.notEqual(orderExtension.getStatus(), PayOrderStatusEnum.WAITING.getStatus())) { // 校验状态，必须是待支付
            throw new BizException(ErrorCode.ORDER_ERROR7);
        }

        // 2. 更新 PayOrderExtensionDO
        int updateCounts = payOrderDomain.updatePayOrderExtensionByIdAndStatus(orderExtension.getOrderExtensionId(),
                orderExtension.getStatus(),
                new PayOrderExtensionEntity()
                        .setStatus(PayOrderStatusEnum.CLOSED.getStatus())
                        .setChannelNotifyData(GsonUtil.toJson(notify))
                        .setChannelErrorCode(notify.getChannelErrorCode())
                        .setChannelErrorMsg(notify.getChannelErrorMsg()));
        if (updateCounts == 0) { // 校验状态，必须是待支付
            throw new BizException(ErrorCode.ORDER_ERROR7);
        }
        log.info("[updateOrderExtensionClosed][orderExtension({}) 更新为支付关闭]", orderExtension.getOrderExtensionId());
    }

    private PayOrderEntity validateOrderCanSubmit(Long id) {
        PayOrderEntity order = payOrderDomain.getPayOrderById(id);
        if (order == null) { // 是否存在
            throw new BizException(ErrorCode.ORDER_ERROR1);
        }
        if (PayOrderStatusEnum.isSuccess(order.getStatus())) { // 校验状态，发现已支付
            throw new BizException(ErrorCode.ORDER_ERROR2);
        }
        if (!PayOrderStatusEnum.WAITING.getStatus().equals(order.getStatus())) { // 校验状态，必须是待支付
            throw new BizException(ErrorCode.ORDER_ERROR3);
        }
        if (LocalDateTimeUtil.beforeNow(order.getExpireTime())) { // 校验是否过期
            throw new BizException(ErrorCode.ORDER_ERROR4);
        }

        // 【重要】校验是否支付拓展单已支付，只是没有回调、或者数据不正常
        validateOrderActuallyPaid(id);
        return order;
    }

    /**
     * 校验支付订单实际已支付
     *
     * @param id 支付编号
     */
    @VisibleForTesting
    void validateOrderActuallyPaid(Long id) {
        List<PayOrderExtensionEntity> orderExtensions = payOrderDomain.listPayOrderExtensionByOrderId(id);
        orderExtensions.forEach(orderExtension -> {
            // 情况一：校验数据库中的 orderExtension 是不是已支付
            if (PayOrderStatusEnum.isSuccess(orderExtension.getStatus())) {
                log.warn("[validateOrderCanSubmit][order({}) 的 extension({}) 已支付，可能是数据不一致]",
                        id, orderExtension.getOrderExtensionId());
                throw new BizException(ErrorCode.ORDER_ERROR5);
            }
            // 情况二：调用三方接口，查询支付单状态，是不是已支付
            PayClient payClient = payChannelService.getPayClient(orderExtension.getChannelId());
            if (payClient == null) {
                log.error("[validateOrderCanSubmit][渠道编号({}) 找不到对应的支付客户端]", orderExtension.getChannelId());
                return;
            }
            PayOrderRespDTO respDTO = payClient.getOrder(orderExtension.getOutTradeNo());
            if (respDTO != null && PayOrderStatusEnum.isSuccess(respDTO.getStatus())) {
                log.warn("[validateOrderCanSubmit][order({}) 的 PayOrderRespDTO({}) 已支付，可能是回调延迟]",
                        id, GsonUtil.toJson(respDTO));
                throw new BizException(ErrorCode.ORDER_ERROR5);
            }
        });
    }

    private PayChannelEntity validateChannelCanSubmit(Long appId, String channelCode) {
        // 校验 App
        payAppService.validPayApp(appId);
        // 校验支付渠道是否有效
        PayChannelEntity channel = payChannelService.validPayChannel(appId, channelCode);
        PayClient client = payChannelService.getPayClient(channel.getChannelId());
        if (client == null) {
            log.error("[validatePayChannelCanSubmit][渠道编号({}) 找不到对应的支付客户端]", channel.getChannelId());
            throw new BizException(ErrorCode.CHANNEL_ERROR2);
        }
        return channel;
    }

    /**
     * 根据支付渠道的编码，生成支付渠道的回调地址
     *
     * @param channel 支付渠道
     * @return 支付渠道的回调地址  配置地址 + "/" + channel id
     */
    private String genChannelOrderNotifyUrl(PayChannelEntity channel) {
        return payProperties.getOrderNotifyUrl() + "/" + channel.getChannelId();
    }

    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private PayOrderService getSelf() {
        return SpringUtil.getBean(getClass());
    }

    /**
     * 更新支付订单的退款金额
     *
     * @param orderId     编号
     * @param refundPrice 增加的退款金额
     */
    public void updateOrderRefundPrice(Long orderId, Integer incrRefundPrice) {
        PayOrderEntity order = payOrderDomain.getPayOrderById(orderId);
        if (order == null) {
            throw new BizException(ErrorCode.ORDER_ERROR1);
        }
        if (!PayOrderStatusEnum.isSuccessOrRefund(order.getStatus())) {
            throw new BizException(ErrorCode.REFUND_ERROR4);
        }
        if (order.getRefundPrice() + incrRefundPrice > order.getPrice()) {
            throw new BizException(ErrorCode.REFUND_ERROR3);
        }

        // 更新订单
        PayOrderEntity updateObj = new PayOrderEntity()
                .setRefundPrice(order.getRefundPrice() + incrRefundPrice)
                .setStatus(PayOrderStatusEnum.REFUND.getStatus());
        int updateCount = payOrderDomain.updatePayOrderByIdAndStatus(orderId, order.getStatus(), updateObj);
        if (updateCount == 0) {
            throw new BizException(ErrorCode.REFUND_ERROR4);
        }
    }
}
