package com.jonas.pay.domain;

import com.jonas.pay.repository.entity.PayAppEntity;
import com.jonas.pay.repository.mapper.PayAppMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * PayAppDomain
 *
 * @author shenjy
 * @time 2024/1/27 15:47
 */
@Component
@RequiredArgsConstructor
public class PayAppDomain {
    private final PayAppMapper payAppMapper;

    public void savePayApp(PayAppEntity entity) {
        payAppMapper.insert(entity);
    }
}
