package com.jonas.pay.service;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.google.common.annotations.VisibleForTesting;
import com.jonas.pay.config.response.JsonResult;
import com.jonas.pay.constant.notify.PayNotifyStatusEnum;
import com.jonas.pay.constant.notify.PayNotifyTypeEnum;
import com.jonas.pay.domain.PayNotifyDomain;
import com.jonas.pay.domain.PayOrderDomain;
import com.jonas.pay.domain.PayRefundDomain;
import com.jonas.pay.domain.PayTransferDomain;
import com.jonas.pay.repository.dto.notify.PayOrderNotifyReqDTO;
import com.jonas.pay.repository.dto.notify.PayRefundNotifyReqDTO;
import com.jonas.pay.repository.dto.notify.PayTransferNotifyReqDTO;
import com.jonas.pay.repository.entity.*;
import com.jonas.pay.repository.redis.PayNotifyLockRedisDAO;
import com.jonas.pay.util.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.jonas.pay.util.LocalDateTimeUtil.addTime;

/**
 * PayNotifyService
 *
 * @author shenjy
 * @time 2024/1/30 17:03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayNotifyService {

    // 使用domain为了避免循环依赖
    private final PayOrderDomain orderDomain;
    private final PayRefundDomain refundDomain;
    private final PayTransferDomain transferDomain;
    private final PayNotifyDomain notifyDomain;
    private final PayNotifyLockRedisDAO notifyLockRedisDAO;

    /**
     * 通知超时时间，单位：秒
     */
    public static final int NOTIFY_TIMEOUT = 120;
    /**
     * {@link #NOTIFY_TIMEOUT} 的毫秒
     */
    public static final long NOTIFY_TIMEOUT_MILLIS = 120 * 1000;

    /**
     * 创建回调通知任务
     *
     * @param type   类型
     * @param dataId 数据编号
     */
    @Transactional(rollbackFor = Exception.class)
    public void createPayNotifyTask(Integer type, Long dataId) {
        PayNotifyTaskEntity task = new PayNotifyTaskEntity().setType(type).setDataId(dataId);
        task.setStatus(PayNotifyStatusEnum.WAITING.getStatus()).setNextNotifyTime(LocalDateTime.now())
                .setNotifyTimes(0).setMaxNotifyTimes(PayNotifyTaskEntity.NOTIFY_FREQUENCY.length + 1);
        // 补充 appId + notifyUrl 字段
        if (Objects.equals(task.getType(), PayNotifyTypeEnum.ORDER.getType())) {
            PayOrderEntity order = orderDomain.getPayOrderById(task.getDataId()); // 不进行非空判断，有问题直接异常
            task.setAppId(order.getAppId()).
                    setMerchantOrderId(order.getMerchantOrderId()).setNotifyUrl(order.getNotifyUrl());
        } else if (Objects.equals(task.getType(), PayNotifyTypeEnum.REFUND.getType())) {
            PayRefundEntity refundDO = refundDomain.getRefund(task.getDataId());
            task.setAppId(refundDO.getAppId())
                    .setMerchantOrderId(refundDO.getMerchantOrderId()).setNotifyUrl(refundDO.getNotifyUrl());
        } else if (Objects.equals(task.getType(), PayNotifyTypeEnum.TRANSFER.getType())) {
            PayTransferEntity transfer = transferDomain.getTransfer(task.getDataId());
            task.setAppId(transfer.getAppId()).setMerchantTransferId(transfer.getMerchantTransferId())
                    .setNotifyUrl(transfer.getNotifyUrl());
        }

        // 执行插入
        notifyDomain.saveNotifyTask(task);

        // 必须在事务提交后，在发起任务，否则 PayNotifyTask 还没入库，就提前回调接入的业务
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                executeNotify(task);
            }
        });
    }

    /**
     * 同步执行单个支付通知
     *
     * @param task 通知任务
     */
    public void executeNotify(PayNotifyTaskEntity task) {
        // 分布式锁，避免并发问题
        notifyLockRedisDAO.lock(task.getNotifyTaskId(), NOTIFY_TIMEOUT_MILLIS, () -> {
            // 校验，当前任务是否已经被通知过
            // 虽然已经通过分布式加锁，但是可能同时满足通知的条件，然后都去获得锁。此时，第一个执行完后，第二个还是能拿到锁，然后会再执行一次。
            // 因此，此处我们通过第 notifyTimes 通知次数是否匹配来判断
            PayNotifyTaskEntity dbTask = notifyDomain.getNotifyTask(task.getNotifyTaskId());
            if (ObjectUtil.notEqual(task.getNotifyTimes(), dbTask.getNotifyTimes())) {
                log.warn("[executeNotifySync][task({}) 任务被忽略，原因是它的通知不是第 ({}) 次，可能是因为并发执行了]",
                        GsonUtil.toJson(task), dbTask.getNotifyTimes());
                return;
            }

            // 执行通知
            getSelf().executeNotify0(dbTask);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void executeNotify0(PayNotifyTaskEntity task) {
        // 发起回调
        JsonResult<?> invokeResult = null;
        Throwable invokeException = null;
        try {
            invokeResult = executeNotifyInvoke(task);
        } catch (Throwable e) {
            invokeException = e;
        }

        // 处理结果
        Integer newStatus = processNotifyResult(task, invokeResult, invokeException);

        // 记录 PayNotifyLog 日志
        String response = invokeException != null ? ExceptionUtil.getRootCauseMessage(invokeException) :
                GsonUtil.toJson(invokeResult);
        notifyDomain.saveNotifyLog(new PayNotifyLogEntity().setTaskId(task.getNotifyTaskId())
                .setNotifyTimes(task.getNotifyTimes() + 1)
                .setStatus(newStatus)
                .setResponse(response));
    }

    /**
     * 处理并更新通知结果
     *
     * @param task            通知任务
     * @param invokeResult    通知结果
     * @param invokeException 通知异常
     * @return 最终任务的状态
     */
    @VisibleForTesting
    Integer processNotifyResult(PayNotifyTaskEntity task, JsonResult<?> invokeResult, Throwable invokeException) {
        // 设置通用的更新 PayNotifyTaskDO 的字段
        PayNotifyTaskEntity updateTask = new PayNotifyTaskEntity()
                .setNotifyTaskId(task.getNotifyTaskId())
                .setLastExecuteTime(LocalDateTime.now())
                .setNotifyTimes(task.getNotifyTimes() + 1);

        // 情况一：调用成功
        if (invokeResult != null && JsonResult.isSuccess(invokeResult.getCode())) {
            updateTask.setStatus(PayNotifyStatusEnum.SUCCESS.getStatus());
            notifyDomain.updateNotifyTaskById(updateTask);
            return updateTask.getStatus();
        }

        // 情况二：调用失败、调用异常
        // 2.1 超过最大回调次数
        if (updateTask.getNotifyTimes() >= PayNotifyTaskEntity.NOTIFY_FREQUENCY.length) {
            updateTask.setStatus(PayNotifyStatusEnum.FAILURE.getStatus());
            notifyDomain.updateNotifyTaskById(updateTask);
            return updateTask.getStatus();
        }
        // 2.2 未超过最大回调次数
        updateTask.setNextNotifyTime(addTime(Duration.ofSeconds(PayNotifyTaskEntity.NOTIFY_FREQUENCY[updateTask.getNotifyTimes()])));
        updateTask.setStatus(invokeException != null ? PayNotifyStatusEnum.REQUEST_FAILURE.getStatus()
                : PayNotifyStatusEnum.REQUEST_SUCCESS.getStatus());
        notifyDomain.updateNotifyTaskById(updateTask);
        return updateTask.getStatus();
    }

    /**
     * 执行单个支付任务的 HTTP 调用
     *
     * @param task 通知任务
     * @return HTTP 响应
     */
    private JsonResult<?> executeNotifyInvoke(PayNotifyTaskEntity task) {
        // 拼接 body 参数
        Object request;
        if (Objects.equals(task.getType(), PayNotifyTypeEnum.ORDER.getType())) {
            request = PayOrderNotifyReqDTO.builder().merchantOrderId(task.getMerchantOrderId())
                    .payOrderId(task.getDataId()).build();
        } else if (Objects.equals(task.getType(), PayNotifyTypeEnum.REFUND.getType())) {
            request = PayRefundNotifyReqDTO.builder().merchantOrderId(task.getMerchantOrderId())
                    .payRefundId(task.getDataId()).build();
        } else if (Objects.equals(task.getType(), PayNotifyTypeEnum.TRANSFER.getType())) {
            request = new PayTransferNotifyReqDTO().setMerchantTransferId(task.getMerchantTransferId())
                    .setPayTransferId(task.getDataId());
        } else {
            throw new RuntimeException("未知的通知任务类型：" + GsonUtil.toJson(task));
        }

        // 发起请求
        try (HttpResponse response = HttpUtil.createPost(task.getNotifyUrl())
                .body(GsonUtil.toJson(request))
                .timeout((int) NOTIFY_TIMEOUT_MILLIS)
                .execute()) {
            // 解析结果
            return GsonUtil.toBean(response.body(), JsonResult.class);
        }
    }

    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private PayNotifyService getSelf() {
        return SpringUtil.getBean(getClass());
    }
}
