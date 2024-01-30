package com.jonas.pay.repository.vo.channel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PayChannelVo extends PayChannelBaseVo {

    // 商户编号
    private Long id;

    // 创建时间
    private LocalDateTime createTime;

    // 渠道编码
    private String code;

    // 支付渠道配置
    private String config;

}
