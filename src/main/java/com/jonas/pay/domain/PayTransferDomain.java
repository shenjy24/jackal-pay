package com.jonas.pay.domain;

import com.jonas.pay.repository.entity.PayTransferEntity;
import com.jonas.pay.repository.mapper.PayTransferMapper;
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
public class PayTransferDomain {
    private final PayTransferMapper payTransferMapper;

    public PayTransferEntity getTransfer(Long transferId) {
        if (null == transferId) {
            return null;
        }
        return payTransferMapper.selectById(transferId);
    }
}
