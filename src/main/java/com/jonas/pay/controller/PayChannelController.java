package com.jonas.pay.controller;

import com.jonas.pay.repository.convert.channel.PayChannelConvert;
import com.jonas.pay.repository.entity.PayChannelEntity;
import com.jonas.pay.repository.qo.app.PayAppIdQo;
import com.jonas.pay.repository.qo.channel.PayChannelCreateQo;
import com.jonas.pay.repository.qo.channel.PayChannelGetQo;
import com.jonas.pay.repository.qo.channel.PayChannelIdQo;
import com.jonas.pay.repository.vo.channel.PayChannelVo;
import com.jonas.pay.service.PayChannelService;
import com.jonas.pay.util.CollectionUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * 支付渠道控制器
 *
 * @author shenjy
 * @time 2024/1/29 14:57
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/pay/channel")
public class PayChannelController {
    private final PayChannelService payChannelService;

    /**
     * 创建支付渠道
     *
     * @param qo 请求参数
     * @return 支付渠道ID
     */
    @PostMapping("/create")
    public Long createPayChannel(@Valid @RequestBody PayChannelCreateQo qo) {
        return payChannelService.createPayChannel(qo);
    }

    @PostMapping("/getById")
    public PayChannelVo getChannel(@Valid @RequestBody PayChannelIdQo qo) {
        PayChannelEntity channel = payChannelService.getChannel(qo.getChannelId());
        return PayChannelConvert.INSTANCE.convert(channel);
    }

    @PostMapping("/getByAppIdAndCode")
    public PayChannelVo getChannel(@Valid @RequestBody PayChannelGetQo qo) {
        PayChannelEntity channel = payChannelService.getChannelByAppIdAndCode(qo.getAppId(), qo.getCode());
        return PayChannelConvert.INSTANCE.convert(channel);
    }

    /**
     * 获取应用的支付列表渠道
     *
     * @param qo 请求参数
     * @return 应用的支付列表渠道
     */
    @PostMapping("/listAppChannel")
    public Set<String> listAppChannel(@Valid @RequestBody PayAppIdQo qo) {
        List<PayChannelEntity> channels = payChannelService.listByAppId(qo.getAppId());
        return CollectionUtil.convertSet(channels, PayChannelEntity::getCode);
    }
}
