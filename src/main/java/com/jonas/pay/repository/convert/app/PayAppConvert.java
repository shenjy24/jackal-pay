package com.jonas.pay.repository.convert.app;

import com.jonas.pay.repository.entity.PayAppEntity;
import com.jonas.pay.repository.qo.app.PayAppCreateQo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PayAppConvert {

    PayAppConvert INSTANCE = Mappers.getMapper(PayAppConvert.class);

    PayAppEntity convert(PayAppCreateQo bean);

}
