package com.jonas.pay.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jonas.pay.constant.notify.PayNotifyStatusEnum;
import lombok.Data;

/**
 * 商户支付、退款等的通知 Log
 * 每次通知时，都会在该表中，记录一次 Log，方便排查问题
 *
 * @author shenjy
 */
@Data
@TableName("pay_notify_log")
public class PayNotifyLogEntity extends BaseEntity {

    /**
     * 日志编号，自增
     */
    private Long notifyLogId;
    /**
     * 通知任务编号
     * <p>
     * 关联 {@link PayNotifyTaskEntity#getTaskId()}
     */
    private Long taskId;
    /**
     * 第几次被通知
     * <p>
     * 对应到 {@link PayNotifyTaskEntity#getNotifyTimes()}
     */
    private Integer notifyTimes;
    /**
     * HTTP 响应结果
     */
    private String response;
    /**
     * 支付通知状态
     * <p>
     * 外键 {@link PayNotifyStatusEnum}
     */
    private Integer status;

}
