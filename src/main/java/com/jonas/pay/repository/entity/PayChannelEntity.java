package com.jonas.pay.repository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.jonas.pay.channel.PayClientConfig;
import com.jonas.pay.constant.CommonStatusEnum;
import com.jonas.pay.constant.channel.PayChannelEnum;
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
    private Long channelId;
    /**
     * 渠道编码
     * <p>
     * 枚举 {@link PayChannelEnum}
     */
    private String code;
    /**
     * 状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 渠道费率，单位：百分比
     */
    private Double feeRate;
    /**
     * 备注
     */
    private String remark;
    /**
     * 应用编号
     * <p>
     * 关联 {@link PayAppEntity#getAppId()}
     */
    private Long appId;
    /**
     * 支付渠道配置
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private PayClientConfig config;
}
