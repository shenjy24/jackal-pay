package com.jonas.pay.controller;

import com.jonas.pay.repository.qo.app.PayAppCreateQo;
import com.jonas.pay.service.PayAppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付应用控制器
 *
 * @author shenjy
 * @time 2024/1/27 15:21
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/pay/app")
public class PayAppController {

    private final PayAppService payAppService;

    /**
     * 创建支付应用
     *
     * @param qo 请求参数
     * @return 应用ID
     */
    @PostMapping("/create")
    public Long createPayApp(@Valid @RequestBody PayAppCreateQo qo) {
        return payAppService.createPayApp(qo);
    }
}
