package com.jonas.pay.repository.vo.order;

import lombok.Data;

/**
 * 支付订单提交 VO
 */
@Data
public class PayOrderSubmitVo {

    // 支付状态, 参见 PayOrderStatusEnum 枚举
    private Integer status;

    // 展示模式, 参见 PayDisplayModeEnum 枚举
    private String displayMode;

    // 展示内容
    private String displayContent;

}
