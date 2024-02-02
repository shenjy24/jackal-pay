package com.jonas.pay.domain;

import com.jonas.pay.repository.entity.PayNotifyLogEntity;
import com.jonas.pay.repository.entity.PayNotifyTaskEntity;
import com.jonas.pay.repository.mapper.PayNotifyLogMapper;
import com.jonas.pay.repository.mapper.PayNotifyTaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * PayNotifyDomain
 *
 * @author shenjy
 * @time 2024/2/1 16:23
 */
@Component
@RequiredArgsConstructor
public class PayNotifyDomain {
    private final PayNotifyLogMapper notifyLogMapper;
    private final PayNotifyTaskMapper notifyTaskMapper;

    public void saveNotifyTask(PayNotifyTaskEntity notifyTask) {
        if (null == notifyTask) {
            return;
        }
        notifyTaskMapper.insert(notifyTask);
    }

    public PayNotifyTaskEntity getNotifyTask(Long notifyTaskId) {
        if (null == notifyTaskId) {
            return null;
        }
        return notifyTaskMapper.selectById(notifyTaskId);
    }

    public void saveNotifyLog(PayNotifyLogEntity notifyLog) {
        if (null == notifyLog) {
            return;
        }
        notifyLogMapper.insert(notifyLog);
    }

    public void updateNotifyTaskById(PayNotifyTaskEntity task) {
        if (null == task || null == task.getTaskId()) {
            return;
        }
        notifyTaskMapper.updateById(task);
    }
}
