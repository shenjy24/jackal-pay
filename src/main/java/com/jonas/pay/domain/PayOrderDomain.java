package com.jonas.pay.domain;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.jonas.pay.repository.entity.PayOrderEntity;
import com.jonas.pay.repository.entity.PayOrderExtensionEntity;
import com.jonas.pay.repository.mapper.PayOrderExtensionMapper;
import com.jonas.pay.repository.mapper.PayOrderMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * PayOrderDomain
 *
 * @author shenjy
 * @time 2024/1/30 13:06
 */
@Component
@RequiredArgsConstructor
public class PayOrderDomain {
    private final PayOrderMapper payOrderMapper;
    private final PayOrderExtensionMapper payOrderExtensionMapper;


    public PayOrderEntity getPayOrderById(Long payOrderId) {
        if (null == payOrderId) {
            return null;
        }
        return payOrderMapper.selectById(payOrderId);
    }

    public List<PayOrderExtensionEntity> listPayOrderExtensionByOrderId(Long id) {
        if (null == id) {
            return Collections.emptyList();
        }
        return new LambdaQueryChainWrapper<>(payOrderExtensionMapper)
                .eq(PayOrderExtensionEntity::getOrderId, id)
                .list();
    }

    public void savePayOrderExtension(PayOrderExtensionEntity orderExtension) {
        if (null == orderExtension) {
            return;
        }
        payOrderExtensionMapper.insert(orderExtension);
    }

    public PayOrderExtensionEntity getPayOrderExtensionByOutTradeNo(String outTradeNo) {
        if (StringUtils.isBlank(outTradeNo)) {
            return null;
        }
        return new LambdaQueryChainWrapper<>(payOrderExtensionMapper)
                .eq(PayOrderExtensionEntity::getOutTradeNo, outTradeNo)
                .one();
    }

    public int updatePayOrderExtensionByIdAndStatus(Long payOrderExtensionId, Integer status, PayOrderExtensionEntity update) {
        return payOrderExtensionMapper.update(update, new LambdaQueryWrapper<PayOrderExtensionEntity>()
                .eq(PayOrderExtensionEntity::getExtensionId, payOrderExtensionId)
                .eq(PayOrderExtensionEntity::getStatus, status));
    }

    public int updatePayOrderByIdAndStatus(Long id, Integer status, PayOrderEntity update) {
        return payOrderMapper.update(update, new LambdaQueryWrapper<PayOrderEntity>()
                .eq(PayOrderEntity::getPayOrderId, id).eq(PayOrderEntity::getStatus, status));
    }
}
