package com.jonas.pay.repository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.jonas.pay.constant.channel.PayChannelEnum;
import com.jonas.pay.constant.transfer.PayTransferStatusEnum;
import com.jonas.pay.constant.transfer.PayTransferTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 转账单 DO
 *
 * @author jason
 */
@Data
@TableName(value ="pay_transfer", autoResultMap = true)
public class PayTransferEntity extends BaseEntity {

    /**
     * 编号
     */
    @TableId
    private Long transferId;

    /**
     * 转账单号
     *
     */
    private String outTradeNo;

    /**
     * 应用编号
     *
     * 关联 {@link PayAppEntity#getAppId()}
     */
    private Long appId;

    /**
     * 转账渠道编号
     *
     * 关联 {@link PayChannelEntity#getChannelId()}
     */
    private Long channelId;

    /**
     * 转账渠道编码
     *
     * 枚举 {@link PayChannelEnum}
     */
    private String channelCode;

    // ========== 商户相关字段 ==========
    /**
     * 商户转账单编号
     *
     * 例如说，内部系统 A 的订单号，需要保证每个 PayAppDO 唯一
     */
    private String merchantTransferId;

    // ========== 转账相关字段 ==========

    /**
     * 类型
     *
     * 枚举 {@link PayTransferTypeEnum}
     */
    private Integer type;

    /**
     * 转账标题
     */
    private String subject;

    /**
     * 转账金额，单位：分
     */
    private Integer price;

    /**
     * 收款人姓名
     */
    private String userName;

    /**
     * 转账状态
     *
     * 枚举 {@link PayTransferStatusEnum}
     */
    private Integer status;

    /**
     * 订单转账成功时间
     */
    private LocalDateTime successTime;

    // ========== 支付宝转账相关字段 ==========
    /**
     * 支付宝登录号
     */
    private String alipayLogonId;


    // ========== 微信转账相关字段 ==========
    /**
     * 微信 openId
     */
    private String openid;

    // ========== 其它字段 ==========

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 用户 IP
     */
    private String userIp;

    /**
     * 渠道的额外参数
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> channelExtras;

    /**
     * 渠道转账单号
     */
    private String channelTransferNo;

    /**
     * 调用渠道的错误码
     */
    private String channelErrorCode;
    /**
     * 调用渠道的错误提示
     */
    private String channelErrorMsg;

    /**
     * 渠道的同步/异步通知的内容
     *
     */
    private String channelNotifyData;

}