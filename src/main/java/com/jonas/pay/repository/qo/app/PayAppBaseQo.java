package com.jonas.pay.repository.qo.app;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

/**
 * 支付应用信息 Base VO，提供给添加、修改、详细的子 VO 使用
 *
 * @author shenjy
 * @time 2024/1/27 15:35
 */
@Data
public class PayAppBaseQo {
    @NotNull(message = "应用名不能为空")
    private String name;

    @NotNull(message = "开启状态不能为空")
    private Integer status;

    // 备注
    private String remark;

    @NotNull(message = "支付结果的回调地址不能为空")
    @URL(message = "支付结果的回调地址必须为 URL 格式")
    private String orderNotifyUrl;

    @NotNull(message = "退款结果的回调地址不能为空")
    @URL(message = "退款结果的回调地址必须为 URL 格式")
    private String refundNotifyUrl;

    @NotNull(message = "转账结果的回调地址不能为空")
    @URL(message = "转账结果的回调地址必须为 URL 格式")
    private String transferNotifyUrl;
}
