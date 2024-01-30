package com.jonas.pay.domain;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.jonas.pay.repository.entity.PayChannelEntity;
import com.jonas.pay.repository.mapper.PayChannelMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * PayChannelDomain
 *
 * @author shenjy
 * @time 2024/1/29 14:58
 */
@Component
@RequiredArgsConstructor
public class PayChannelDomain {
    private final PayChannelMapper payChannelMapper;

    public PayChannelEntity getChannelByAppIdAndCode(Long appId, String code) {
        if (null == appId || StringUtils.isBlank(code)) {
            return null;
        }
        return new LambdaQueryChainWrapper<>(payChannelMapper)
                .eq(PayChannelEntity::getAppId, appId)
                .eq(PayChannelEntity::getCode, code)
                .one();
    }

    public void savePayChannel(PayChannelEntity channel) {
        payChannelMapper.insert(channel);
    }

    public PayChannelEntity getChannel(Long id) {
        if (null == id) {
            return null;
        }
        return payChannelMapper.selectById(id);
    }

    public List<PayChannelEntity> listByAppId(Long appId) {
        if (null == appId) {
            return Collections.emptyList();
        }
        return new LambdaQueryChainWrapper<>(payChannelMapper)
                .eq(PayChannelEntity::getAppId, appId)
                .list();
    }
}
