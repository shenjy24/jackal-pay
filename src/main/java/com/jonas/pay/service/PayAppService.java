package com.jonas.pay.service;

import com.jonas.pay.domain.PayAppDomain;
import com.jonas.pay.repository.convert.app.PayAppConvert;
import com.jonas.pay.repository.entity.PayAppEntity;
import com.jonas.pay.repository.qo.app.PayAppCreateQo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 支付应用服务
 *
 * @author shenjy
 * @time 2024/1/27 15:23
 */
@Service
@RequiredArgsConstructor
public class PayAppService {

    private final PayAppDomain payAppDomain;

    /**
     * 创建支付应用
     * @param qo 请求体
     * @return 支付应用ID
     */
    public Long createPayApp(PayAppCreateQo qo) {
        PayAppEntity app = PayAppConvert.INSTANCE.convert(qo);
        payAppDomain.savePayApp(app);
        return app.getId();
    }
}
