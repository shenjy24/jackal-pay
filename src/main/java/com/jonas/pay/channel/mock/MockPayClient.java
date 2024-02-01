package com.jonas.pay.channel.mock;

import com.jonas.pay.channel.AbstractPayClient;
import com.jonas.pay.channel.NonePayClientConfig;
import com.jonas.pay.constant.channel.PayChannelEnum;
import com.jonas.pay.constant.transfer.PayTransferTypeEnum;
import com.jonas.pay.repository.dto.order.PayOrderRespDTO;
import com.jonas.pay.repository.dto.order.PayOrderUnifiedReqDTO;
import com.jonas.pay.repository.dto.refund.PayRefundRespDTO;
import com.jonas.pay.repository.dto.refund.PayRefundUnifiedReqDTO;
import com.jonas.pay.repository.dto.transfer.PayTransferRespDTO;
import com.jonas.pay.repository.dto.transfer.PayTransferUnifiedReqDTO;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 模拟支付的 PayClient 实现类
 * <p>
 * 模拟支付返回结果都是成功，方便大家日常流畅
 *
 * @author shenjy
 */
public class MockPayClient extends AbstractPayClient<NonePayClientConfig> {

    private static final String MOCK_RESP_SUCCESS_DATA = "MOCK_SUCCESS";

    public MockPayClient(Long channelId, NonePayClientConfig config) {
        super(channelId, PayChannelEnum.MOCK.getCode(), config);
    }

    @Override
    protected void doInit() {
    }

    @Override
    protected PayOrderRespDTO doUnifiedOrder(PayOrderUnifiedReqDTO reqDTO) {
        return PayOrderRespDTO.successOf("MOCK-P-" + reqDTO.getOutTradeNo(), "", LocalDateTime.now(),
                reqDTO.getOutTradeNo(), MOCK_RESP_SUCCESS_DATA);
    }

    @Override
    protected PayOrderRespDTO doGetOrder(String outTradeNo) {
        return PayOrderRespDTO.successOf("MOCK-P-" + outTradeNo, "", LocalDateTime.now(),
                outTradeNo, MOCK_RESP_SUCCESS_DATA);
    }

    @Override
    protected PayRefundRespDTO doUnifiedRefund(PayRefundUnifiedReqDTO reqDTO) {
        return PayRefundRespDTO.successOf("MOCK-R-" + reqDTO.getOutRefundNo(), LocalDateTime.now(),
                reqDTO.getOutRefundNo(), MOCK_RESP_SUCCESS_DATA);
    }

    @Override
    protected PayRefundRespDTO doGetRefund(String outTradeNo, String outRefundNo) {
        return PayRefundRespDTO.successOf("MOCK-R-" + outRefundNo, LocalDateTime.now(),
                outRefundNo, MOCK_RESP_SUCCESS_DATA);
    }

    @Override
    protected PayRefundRespDTO doParseRefundNotify(Map<String, String> params, String body) {
        throw new UnsupportedOperationException("模拟支付无退款回调");
    }

    @Override
    protected PayOrderRespDTO doParseOrderNotify(Map<String, String> params, String body) {
        throw new UnsupportedOperationException("模拟支付无支付回调");
    }

    @Override
    protected PayTransferRespDTO doUnifiedTransfer(PayTransferUnifiedReqDTO reqDTO) {
        throw new UnsupportedOperationException("待实现");
    }

    @Override
    protected PayTransferRespDTO doGetTransfer(String outTradeNo, PayTransferTypeEnum type) {
        throw new UnsupportedOperationException("待实现");
    }

}
