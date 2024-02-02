package com.jonas.pay.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jonas.pay.constant.channel.PayChannelEnum;
import com.jonas.pay.constant.order.PayOrderStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 支付订单实体
 *
 * @author shenjy
 */
@Data
@TableName("pay_order")
public class PayOrderEntity extends BaseEntity {

    /**
     * 订单编号，数据库自增
     */
    @TableId
    private Long payOrderId;
    /**
     * 应用编号
     * <p>
     * 关联 {@link PayAppEntity#getAppId()}
     */
    private Long appId;
    /**
     * 渠道编号
     * <p>
     * 关联 {@link PayChannelEntity#getChannelId()}
     */
    private Long channelId;
    /**
     * 渠道编码
     * <p>
     * 枚举 {@link PayChannelEnum}
     */
    private String channelCode;

    // ========== 商户相关字段 ==========

    /**
     * 商户订单编号
     * <p>
     * 例如说，内部系统 A 的订单号，需要保证每个 PayAppDO 唯一
     */
    private String merchantOrderId;
    /**
     * 商品标题
     */
    private String subject;
    /**
     * 商品描述信息
     */
    private String body;
    /**
     * 异步通知地址
     */
    private String notifyUrl;

    // ========== 订单相关字段 ==========

    /**
     * 支付金额，单位：分
     */
    private Integer price;
    /**
     * 渠道手续费，单位：百分比
     * <p>
     * 冗余 {@link PayChannelEntity#getFeeRate()}
     */
    private Double channelFeeRate;
    /**
     * 渠道手续金额，单位：分
     */
    private Integer channelFeePrice;
    /**
     * 支付状态
     * <p>
     * 枚举 {@link PayOrderStatusEnum}
     */
    private Integer status;
    /**
     * 用户 IP
     */
    private String userIp;
    /**
     * 订单失效时间
     */
    private LocalDateTime expireTime;
    /**
     * 订单支付成功时间
     */
    private LocalDateTime successTime;
    /**
     * 支付成功的订单拓展单编号
     * <p>
     * 关联 {@link PayOrderExtensionEntity#getOrderExtensionId()}
     */
    private Long orderExtensionId;
    /**
     * 支付成功的外部订单号
     * <p>
     * 关联 {@link PayOrderExtensionEntity#getOutTradeNo()}
     */
    private String outTradeNo;

    // ========== 退款相关字段 ==========
    /**
     * 退款总金额，单位：分
     */
    private Integer refundPrice;

    // ========== 渠道相关字段 ==========
    /**
     * 渠道用户编号
     * <p>
     * 例如说，微信 openid、支付宝账号
     */
    private String channelUserId;
    /**
     * 渠道订单号
     */
    private String channelOrderNo;

}
