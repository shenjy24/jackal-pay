package com.jonas.pay.channel;

import com.jonas.pay.constant.transfer.PayTransferTypeEnum;
import com.jonas.pay.repository.dto.order.PayOrderRespDTO;
import com.jonas.pay.repository.dto.order.PayOrderUnifiedReqDTO;
import com.jonas.pay.repository.dto.refund.PayRefundRespDTO;
import com.jonas.pay.repository.dto.refund.PayRefundUnifiedReqDTO;
import com.jonas.pay.repository.dto.transfer.PayTransferRespDTO;
import com.jonas.pay.repository.dto.transfer.PayTransferUnifiedReqDTO;

import java.util.Map;

/**
 * 支付客户端，用于对接各支付渠道的 SDK，实现发起支付、退款等功能
 *
 * @author shenjy
 */
public interface PayClient {

    /**
     * 获得渠道编号
     *
     * @return 渠道编号
     */
    Long getId();

    // ============ 支付相关 ==========

    /**
     * 调用支付渠道，统一下单
     *
     * @param reqDTO 下单信息
     * @return 支付订单信息
     */
    PayOrderRespDTO unifiedOrder(PayOrderUnifiedReqDTO reqDTO);

    /**
     * 解析 order 回调数据
     *
     * @param params HTTP 回调接口 content type 为 application/x-www-form-urlencoded 的所有参数
     * @param body HTTP 回调接口的 request body
     * @return 支付订单信息
     */
    PayOrderRespDTO parseOrderNotify(Map<String, String> params, String body);

    /**
     * 获得支付订单信息
     *
     * @param outTradeNo 外部订单号
     * @return 支付订单信息
     */
    PayOrderRespDTO getOrder(String outTradeNo);

    // ============ 退款相关 ==========

    /**
     * 调用支付渠道，进行退款
     *
     * @param reqDTO  统一退款请求信息
     * @return 退款信息
     */
    PayRefundRespDTO unifiedRefund(PayRefundUnifiedReqDTO reqDTO);

    /**
     * 解析 refund 回调数据
     *
     * @param params HTTP 回调接口 content type 为 application/x-www-form-urlencoded 的所有参数
     * @param body HTTP 回调接口的 request body
     * @return 支付订单信息
     */
    PayRefundRespDTO parseRefundNotify(Map<String, String> params, String body);

    /**
     * 获得退款订单信息
     *
     * @param outTradeNo 外部订单号
     * @param outRefundNo 外部退款号
     * @return 退款订单信息
     */
    PayRefundRespDTO getRefund(String outTradeNo, String outRefundNo);

    /**
     * 调用渠道，进行转账
     *
     * @param reqDTO 统一转账请求信息
     * @return 转账信息
     */
    PayTransferRespDTO unifiedTransfer(PayTransferUnifiedReqDTO reqDTO);

    /**
     * 获得转账订单信息
     *
     * @param outTradeNo 外部订单号
     * @param type 转账类型
     * @return 转账信息
     */
    PayTransferRespDTO getTransfer(String outTradeNo, PayTransferTypeEnum type);
}