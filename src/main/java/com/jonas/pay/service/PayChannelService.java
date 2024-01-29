package com.jonas.pay.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.jonas.pay.channel.PayClientConfig;
import com.jonas.pay.config.response.BizException;
import com.jonas.pay.config.response.ErrorCode;
import com.jonas.pay.constant.channel.PayChannelEnum;
import com.jonas.pay.domain.PayChannelDomain;
import com.jonas.pay.repository.convert.channel.PayChannelConvert;
import com.jonas.pay.repository.entity.PayChannelEntity;
import com.jonas.pay.repository.qo.channel.PayChannelCreateQo;
import com.jonas.pay.util.GsonUtil;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * PayChannelService
 *
 * @author shenjy
 * @time 2024/1/29 14:58
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayChannelService {

    private final Validator validator;
    private final PayChannelDomain payChannelDomain;

    public Long createPayChannel(PayChannelCreateQo qo) {
        // 断言是否有重复的
        PayChannelEntity dbChannel = payChannelDomain.getChannelByAppIdAndCode(qo.getAppId(), qo.getCode());
        if (null == dbChannel) {
            throw new BizException(ErrorCode.CHANNEL_ERROR1);
        }
        // 新增渠道
        PayChannelEntity channel = PayChannelConvert.INSTANCE.convert(qo);
        channel.setConfig(parseConfig(qo.getCode(), qo.getConfig()));
        payChannelDomain.savePayChannel(channel);
        return channel.getId();
    }

    /**
     * 解析并校验配置
     *
     * @param code      渠道编码
     * @param configStr 配置
     * @return 支付配置
     */
    private PayClientConfig parseConfig(String code, String configStr) {
        // 解析配置
        Class<? extends PayClientConfig> payClass = PayChannelEnum.getByCode(code).getConfigClass();
        if (ObjectUtil.isNull(payClass)) {
            throw new BizException(ErrorCode.CHANNEL_ERROR2);
        }
        PayClientConfig config = GsonUtil.toBean(configStr, payClass);
        Assert.notNull(config);

        // 验证参数
        config.validate(validator);
        return config;
    }
}
