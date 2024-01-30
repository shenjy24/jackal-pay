package com.jonas.pay.repository.qo.channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * PayChannelGetQo
 *
 * @author shenjy
 * @time 2024/1/30 12:35
 */
@Data
public class PayChannelGetQo {
    @NotNull(message = "支付应用ID不能为空")
    private Long appId;

    @NotBlank(message = "支付渠道编码不能为空")
    private String code;
}
