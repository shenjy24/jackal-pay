package com.jonas.pay.service;

import com.jonas.pay.config.response.BizException;
import com.jonas.pay.config.response.ErrorCode;
import com.jonas.pay.constant.CommonStatusEnum;
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
        return app.getAppId();
    }

    public PayAppEntity validPayApp(Long appId) {
        PayAppEntity app = payAppDomain.getPayApp(appId);
        // 校验是否存在
        if (null == app) {
            throw new BizException(ErrorCode.APP_ERROR1);
        }
        // 校验是否禁用
        if (CommonStatusEnum.DISABLE.getStatus().equals(app.getStatus())) {
            throw new BizException(ErrorCode.APP_ERROR2);
        }
        return app;
    }
}
