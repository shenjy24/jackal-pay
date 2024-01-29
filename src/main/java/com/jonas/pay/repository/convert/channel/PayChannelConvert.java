package com.jonas.pay.repository.convert.channel;

import com.jonas.pay.repository.entity.PayChannelEntity;
import com.jonas.pay.repository.qo.channel.PayChannelCreateQo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PayChannelConvert {

    PayChannelConvert INSTANCE = Mappers.getMapper(PayChannelConvert.class);

    @Mapping(target = "config", ignore = true)
    PayChannelEntity convert(PayChannelCreateQo bean);

}
