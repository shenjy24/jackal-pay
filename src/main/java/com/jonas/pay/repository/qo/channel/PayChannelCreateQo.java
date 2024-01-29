package com.jonas.pay.repository.qo.channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PayChannelCreateQo extends PayChannelBaseQo {

    @NotNull(message = "渠道编码不能为空")
    private String code;

    @NotBlank(message = "渠道配置不能为空")
    private String config;
}
