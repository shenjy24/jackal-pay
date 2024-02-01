package com.jonas.pay.repository.qo.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.util.Map;

/**
 * 支付订单提交
 */
@Data
public class PayOrderSubmitQo {

    @NotNull(message = "支付单编号不能为空")
    private Long extensionId;

    @NotEmpty(message = "支付渠道不能为空")
    private String channelCode;

    // 支付渠道的额外参数，例如说，微信公众号需要传递 openid 参数
    private Map<String, String> channelExtras;

    // 展示模式, 参见 {@link PayDisplayModeEnum} 枚举。如果不传递，则每个支付渠道使用默认的方式
    private String displayMode;

    @URL(message = "回跳地址的格式必须是 URL")
    private String returnUrl;

}
