package com.jonas.pay.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.jonas.pay.channel.PayClient;
import com.jonas.pay.channel.PayClientConfig;
import com.jonas.pay.channel.PayClientFactory;
import com.jonas.pay.config.response.BizException;
import com.jonas.pay.config.response.ErrorCode;
import com.jonas.pay.constant.CommonStatusEnum;
import com.jonas.pay.constant.channel.PayChannelEnum;
import com.jonas.pay.domain.PayChannelDomain;
import com.jonas.pay.repository.convert.channel.PayChannelConvert;
import com.jonas.pay.repository.entity.PayChannelEntity;
import com.jonas.pay.repository.qo.channel.PayChannelCreateQo;
import com.jonas.pay.util.GsonUtil;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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
    private final PayClientFactory payClientFactory;

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
        return channel.getChannelId();
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

        // todo 验证参数
        config.validate(validator);
        return config;
    }

    public PayChannelEntity getChannel(Long id) {
        if (null == id) {
            return null;
        }
        return payChannelDomain.getChannel(id);
    }

    public PayChannelEntity getChannelByAppIdAndCode(Long appId, String code) {
        if (null == appId || StringUtils.isBlank(code)) {
            return null;
        }
        return payChannelDomain.getChannelByAppIdAndCode(appId, code);
    }

    public List<PayChannelEntity> listByAppId(Long appId) {
        if (null == appId) {
            return Collections.emptyList();
        }
        return payChannelDomain.listByAppId(appId);
    }

    /**
     * 获得指定编号的支付客户端
     *
     * @param channelId 编号
     * @return 支付客户端
     */
    public PayClient getPayClient(Long channelId) {
        PayChannelEntity channel = payChannelDomain.getChannel(channelId);
        if (null != channel) {
            payClientFactory.createOrUpdatePayClient(channel.getChannelId(), channel.getCode(), channel.getConfig());
        }
        return payClientFactory.getPayClient(channelId);
    }

    public PayChannelEntity validPayChannel(Long appId, String code) {
        PayChannelEntity channel = payChannelDomain.getChannelByAppIdAndCode(appId, code);
        validPayChannel(channel);
        return channel;
    }

    public PayChannelEntity validPayChannel(Long id) {
        PayChannelEntity channel = payChannelDomain.getChannel(id);
        validPayChannel(channel);
        return channel;
    }

    private void validPayChannel(PayChannelEntity channel) {
        if (channel == null) {
            throw new BizException(ErrorCode.CHANNEL_ERROR2);
        }
        if (CommonStatusEnum.DISABLE.getStatus().equals(channel.getStatus())) {
            throw new BizException(ErrorCode.CHANNEL_ERROR3);
        }
    }
}
