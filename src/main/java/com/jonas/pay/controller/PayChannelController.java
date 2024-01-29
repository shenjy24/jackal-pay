package com.jonas.pay.controller;

import com.jonas.pay.repository.qo.channel.PayChannelCreateQo;
import com.jonas.pay.service.PayChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/create")
    public Long createPayChannel(@Valid @RequestBody PayChannelCreateQo qo) {
        return payChannelService.createPayChannel(qo);
    }
}
