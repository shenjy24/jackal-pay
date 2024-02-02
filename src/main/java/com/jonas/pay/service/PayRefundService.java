package com.jonas.pay.service;

import cn.hutool.extra.spring.SpringUtil;
import com.jonas.pay.config.response.BizException;
import com.jonas.pay.config.response.ErrorCode;
import com.jonas.pay.constant.notify.PayNotifyTypeEnum;
import com.jonas.pay.constant.refund.PayRefundStatusEnum;
import com.jonas.pay.domain.PayRefundDomain;
import com.jonas.pay.repository.dto.refund.PayRefundRespDTO;
import com.jonas.pay.repository.entity.PayChannelEntity;
import com.jonas.pay.repository.entity.PayRefundEntity;
import com.jonas.pay.util.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 退款服务类
 *
 * @author shenjy
 * @time 2024/2/1 16:25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayRefundService {

    private final PayRefundDomain refundDomain;
    private final PayChannelService channelService;
    private final PayOrderService orderService;
    private final PayNotifyService notifyService;

    public PayRefundEntity getPayRefund(Long refundId) {
        if (null == refundId) {
            return null;
        }
        return refundDomain.getRefund(refundId);
    }

    public void notifyRefund(Long channelId, PayRefundRespDTO notify) {
        // 校验支付渠道是否有效
        PayChannelEntity channel = channelService.validPayChannel(channelId);
        // 更新退款订单
        getSelf().notifyRefund(channel, notify);
    }

    /**
     * 通知并更新订单的退款结果
     *
     * @param channel 支付渠道
     * @param notify  通知
     */
    @Transactional(rollbackFor = Exception.class)
    // 注意，如果是方法内调用该方法，需要通过 getSelf().notifyRefund(channel, notify) 调用，否则事务不生效
    public void notifyRefund(PayChannelEntity channel, PayRefundRespDTO notify) {
        // 情况一：退款成功
        if (PayRefundStatusEnum.isSuccess(notify.getStatus())) {
            notifyRefundSuccess(channel, notify);
            return;
        }
        // 情况二：退款失败
        if (PayRefundStatusEnum.isFailure(notify.getStatus())) {
            notifyRefundFailure(channel, notify);
        }
    }

    private void notifyRefundSuccess(PayChannelEntity channel, PayRefundRespDTO notify) {
        // 1.1 查询 PayRefundDO
        PayRefundEntity refund = refundDomain.getRefundByAppIdAndNo(
                channel.getAppId(), notify.getOutRefundNo());
        if (refund == null) {
            throw new BizException(ErrorCode.REFUND_ERROR1);
        }
        if (PayRefundStatusEnum.isSuccess(refund.getStatus())) { // 如果已经是成功，直接返回，不用重复更新
            log.info("[notifyRefundSuccess][退款订单({}) 已经是退款成功，无需更新]", refund.getRefundId());
            return;
        }
        if (!PayRefundStatusEnum.WAITING.getStatus().equals(refund.getStatus())) {
            throw new BizException(ErrorCode.REFUND_ERROR2);
        }
        // 1.2 更新 PayRefundDO
        PayRefundEntity updateRefundObj = new PayRefundEntity()
                .setSuccessTime(notify.getSuccessTime())
                .setChannelRefundNo(notify.getChannelRefundNo())
                .setStatus(PayRefundStatusEnum.SUCCESS.getStatus())
                .setChannelNotifyData(GsonUtil.toJson(notify));
        int updateCounts = refundDomain.updateByIdAndStatus(refund.getRefundId(), refund.getStatus(), updateRefundObj);
        if (updateCounts == 0) { // 校验状态，必须是等待状态
            throw new BizException(ErrorCode.REFUND_ERROR2);
        }
        log.info("[notifyRefundSuccess][退款订单({}) 更新为退款成功]", refund.getRefundId());

        // 2. 更新订单
        orderService.updateOrderRefundPrice(refund.getOrderId(), refund.getRefundPrice());

        // 3. 插入退款通知记录
        notifyService.createPayNotifyTask(PayNotifyTypeEnum.REFUND.getType(), refund.getRefundId());
    }

    private void notifyRefundFailure(PayChannelEntity channel, PayRefundRespDTO notify) {
        // 1.1 查询 PayRefundDO
        PayRefundEntity refund = refundDomain.getRefundByAppIdAndNo(channel.getAppId(), notify.getOutRefundNo());
        if (refund == null) {
            throw new BizException(ErrorCode.REFUND_ERROR1);
        }
        if (PayRefundStatusEnum.isFailure(refund.getStatus())) { // 如果已经是成功，直接返回，不用重复更新
            log.info("[notifyRefundSuccess][退款订单({}) 已经是退款关闭，无需更新]", refund.getRefundId());
            return;
        }
        if (!PayRefundStatusEnum.WAITING.getStatus().equals(refund.getStatus())) {
            throw new BizException(ErrorCode.REFUND_ERROR2);
        }
        // 1.2 更新 PayRefund
        PayRefundEntity updateRefundObj = new PayRefundEntity()
                .setChannelRefundNo(notify.getChannelRefundNo())
                .setStatus(PayRefundStatusEnum.FAILURE.getStatus())
                .setChannelNotifyData(GsonUtil.toJson(notify))
                .setChannelErrorCode(notify.getChannelErrorCode())
                .setChannelErrorMsg(notify.getChannelErrorMsg());
        int updateCounts = refundDomain.updateByIdAndStatus(refund.getRefundId(), refund.getStatus(), updateRefundObj);
        if (updateCounts == 0) { // 校验状态，必须是等待状态
            throw new BizException(ErrorCode.REFUND_ERROR2);
        }
        log.info("[notifyRefundFailure][退款订单({}) 更新为退款失败]", refund.getRefundId());

        // 2. 插入退款通知记录
        notifyService.createPayNotifyTask(PayNotifyTypeEnum.REFUND.getType(), refund.getRefundId());
    }

    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private PayRefundService getSelf() {
        return SpringUtil.getBean(getClass());
    }
}
