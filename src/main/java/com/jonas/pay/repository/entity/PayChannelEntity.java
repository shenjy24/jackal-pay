package com.jonas.pay.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 支付渠道实体
 * 一个应用下，会有多种支付渠道，例如说微信支付、支付宝支付等等
 *
 * @author shenjy
 * @time 2024/1/29 10:56
 */
@Data
@TableName("pay_channel")
@EqualsAndHashCode(callSuper = true)
public class PayChannelEntity extends BaseEntity {
    /**
     * 渠道编号
     */
    @TableId
    private Long id;
    /**
     * 渠道编码
     *
     * 枚举 {@link PayChannelEnum}
     */
    private String code;
}
