package com.jonas.pay.repository.qo.app;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * PayAppIdQo
 *
 * @author shenjy
 * @time 2024/1/30 12:32
 */
@Data
public class PayAppIdQo {
    @NotNull(message = "支付应用ID不能为空")
    private Long appId;
}
