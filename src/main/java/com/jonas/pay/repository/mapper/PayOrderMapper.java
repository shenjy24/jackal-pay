package com.jonas.pay.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jonas.pay.repository.entity.PayOrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * PayOrderMapper
 *
 * @author shenjy
 * @time 2023/12/17 15:26
 */
@Mapper
public interface PayOrderMapper extends BaseMapper<PayOrderEntity> {
}
