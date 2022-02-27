package com.kl.example.service.data.dtx.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kl.example.service.data.dtx.entity.OrderEntity;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ewp
 * @since 2021-09-29
 */
@DS("first")
public interface OrderMapper extends BaseMapper<OrderEntity> {

}
