package com.jonas.pay.repository.convert.order;

import com.jonas.pay.repository.dto.order.PayOrderRespDTO;
import com.jonas.pay.repository.dto.order.PayOrderUnifiedReqDTO;
import com.jonas.pay.repository.entity.PayOrderEntity;
import com.jonas.pay.repository.entity.PayOrderExtensionEntity;
import com.jonas.pay.repository.qo.order.PayOrderSubmitQo;
import com.jonas.pay.repository.vo.order.PayOrderSubmitVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 支付订单 Convert
 *
 * @author aquan
 */
@Mapper
public interface PayOrderConvert {

    PayOrderConvert INSTANCE = Mappers.getMapper(PayOrderConvert.class);

    @Mapping(target = "extensionId", ignore = true)
    PayOrderExtensionEntity convert(PayOrderSubmitQo bean, String userIp);

    @Mapping(source = "order.status", target = "status")
    PayOrderSubmitVo convert(PayOrderEntity order, PayOrderRespDTO respDTO);

    PayOrderUnifiedReqDTO convert2(PayOrderSubmitQo bean, String userIp);
}
