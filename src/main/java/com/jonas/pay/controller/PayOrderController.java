package com.jonas.pay.controller;

import com.jonas.pay.repository.qo.order.PayOrderSubmitQo;
import com.jonas.pay.repository.vo.order.PayOrderSubmitVo;
import com.jonas.pay.service.PayOrderService;
import com.jonas.pay.util.RequestUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付订单控制器
 *
 * @author shenjy
 * @time 2024/1/30 13:03
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/pay/order")
public class PayOrderController {
    private final PayOrderService payOrderService;

    /**
     * 提交支付订单
     *
     * @param qo 请求参数
     * @return 订单提交信息
     */
    @PostMapping("/submit")
    public PayOrderSubmitVo submitPayOrder(@Valid @RequestBody PayOrderSubmitQo qo) {
        return payOrderService.submitOrder(qo, RequestUtil.getClientIP());
    }
}
