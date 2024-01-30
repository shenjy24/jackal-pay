package com.jonas.pay.repository.qo.channel;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * PayChannelIdQo
 *
 * @author shenjy
 * @time 2024/1/30 12:32
 */
@Data
public class PayChannelIdQo {
    @NotNull(message = "支付渠道ID不能为空")
    private Long channelId;
}
