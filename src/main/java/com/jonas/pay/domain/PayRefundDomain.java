package com.jonas.pay.domain;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.jonas.pay.repository.entity.PayRefundEntity;
import com.jonas.pay.repository.mapper.PayRefundMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
    private final PayRefundMapper refundMapper;

    public PayRefundEntity getRefund(Long refundId) {
        if (null == refundId) {
            return null;
        }
        return refundMapper.selectById(refundId);
    }

    public PayRefundEntity getRefundByAppIdAndNo(Long appId, String outRefundNo) {
        if (null == appId || StringUtils.isBlank(outRefundNo)) {
            return null;
        }
        return new LambdaQueryChainWrapper<>(refundMapper)
                .eq(PayRefundEntity::getAppId, appId)
                .eq(PayRefundEntity::getOutTradeNo, outRefundNo)
                .one();
    }

    public int updateByIdAndStatus(Long refundId, Integer status, PayRefundEntity refund) {
        if (null == refundId || null == status || null == refund) {
            return 0;
        }
        return refundMapper.update(refund, new LambdaQueryWrapper<PayRefundEntity>()
                .eq(PayRefundEntity::getRefundId, refundId)
                .eq(PayRefundEntity::getStatus, status));
    }
}
