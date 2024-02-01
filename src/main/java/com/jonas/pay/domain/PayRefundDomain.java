package com.jonas.pay.domain;

import com.jonas.pay.repository.entity.PayRefundEntity;
import com.jonas.pay.repository.mapper.PayRefundMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * PayRefundDomain
 *
 * @author shenjy
 * @time 2024/2/1 16:23
 */
@Component
@RequiredArgsConstructor
public class PayRefundDomain {
    private final PayRefundMapper payRefundMapper;

    public PayRefundEntity getRefund(Long refundId) {
        if (null == refundId) {
            return null;
        }
        return payRefundMapper.selectById(refundId);
    }
}
