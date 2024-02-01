package com.jonas.pay.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jonas.pay.constant.notify.PayNotifyStatusEnum;
import com.jonas.pay.constant.notify.PayNotifyTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 支付通知
 * 在支付系统收到支付渠道的支付、退款的结果后，需要不断的通知到业务系统，直到成功。
 *
 * @author shenjy
 */
@Data
@TableName("pay_notify_task")
public class PayNotifyTaskEntity extends BaseEntity {

    /**
     * 通知频率，单位为秒。
     * <p>
     * 算上首次的通知，实际是一共 1 + 8 = 9 次。
     */
    public static final Integer[] NOTIFY_FREQUENCY = new Integer[]{
            15, 15, 30, 180,
            1800, 1800, 1800, 3600
    };

    /**
     * 编号，自增
     */
    @TableId
    private Long notifyTaskId;
    /**
     * 应用编号
     * <p>
     * 关联 {@link PayAppEntity#getAppId()}
     */
    private Long appId;
    /**
     * 通知类型
     * <p>
     * 外键 {@link PayNotifyTypeEnum}
     */
    private Integer type;
    /**
     * 数据编号，根据不同 type 进行关联：
     * <p>
     * 1. {@link PayNotifyTypeEnum#ORDER} 时，关联 {@link PayOrderEntity#getPayOrderId()}
     * 2. {@link PayNotifyTypeEnum#REFUND} 时，关联 {@link PayRefundEntity#getRefundId()}
     */
    private Long dataId;
    /**
     * 商户订单编号
     */
    private String merchantOrderId;
    /**
     * 商户转账单编号
     */
    private String merchantTransferId;
    /**
     * 通知状态
     * <p>
     * 外键 {@link PayNotifyStatusEnum}
     */
    private Integer status;
    /**
     * 下一次通知时间
     */
    private LocalDateTime nextNotifyTime;
    /**
     * 最后一次执行时间
     */
    private LocalDateTime lastExecuteTime;
    /**
     * 当前通知次数
     */
    private Integer notifyTimes;
    /**
     * 最大可通知次数
     */
    private Integer maxNotifyTimes;
    /**
     * 通知地址
     */
    private String notifyUrl;

}
