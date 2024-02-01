package com.jonas.pay.service;

import com.jonas.pay.domain.PayRefundDomain;
import com.jonas.pay.repository.entity.PayRefundEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 退款服务类
 *
 * @author shenjy
 * @time 2024/2/1 16:25
 */
@Service
@RequiredArgsConstructor
public class PayRefundService {

    private final PayRefundDomain payRefundDomain;

    public PayRefundEntity getPayRefund(Long refundId) {
        if (null == refundId) {
            return null;
        }
        return payRefundDomain.getRefund(refundId);
    }
}
