package com.jonas.pay.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 支付应用实体
 * 一个商户下，可能会有多个支付应用。例如说，京东有京东商城、京东到家等等
 *
 * @author shenjy
 * @time 2024/1/27 14:44
 */
@Data
@TableName("pay_app")
@EqualsAndHashCode(callSuper = true)
public class PayAppEntity extends BaseEntity {
    /**
     * 应用编号
     */
    @TableId
    private Long id;
    /**
     * 应用名
     */
    private String name;
    /**
     * 状态
     * 枚举 {@link com.jonas.pay.constant.YesNoEnum}
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 支付结果的回调地址
     */
    private String orderNotifyUrl;
    /**
     * 退款结果的回调地址
     */
    private String refundNotifyUrl;
    /**
     * 转账结果的回调地址
     */
    private String transferNotifyUrl;
}
