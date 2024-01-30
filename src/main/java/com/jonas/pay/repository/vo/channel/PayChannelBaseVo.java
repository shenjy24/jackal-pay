package com.jonas.pay.repository.vo.channel;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 支付渠道 Base VO，提供给添加、修改、详细的子 VO 使用
 */
@Data
public class PayChannelBaseVo {
    @NotNull(message = "开启状态不能为空")
    private Integer status;

    private String remark;

    @NotNull(message = "渠道费率，单位：百分比不能为空")
    private Double feeRate;

    @NotNull(message = "应用编号不能为空")
    private Long appId;
}
