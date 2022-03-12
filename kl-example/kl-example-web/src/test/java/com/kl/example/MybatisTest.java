package com.kl.example;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kl.core.thread.KlThreadLocal;
import com.kl.db.starter.IdGeneratorUtil;
import com.kl.example.service.data.dtx.entity.OrderEntity;
import com.kl.example.service.data.dtx.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KlExamplesWebApplication.class)
@Slf4j
public class MybatisTest {

    @Resource
    private OrderMapper orderMapper;

    @Test
    public void selectTest() {
        OrderEntity tOrderEntity = orderMapper.selectById(505460450654175235l);
    }

    @Test
    public void insert() {
//        KlThreadLocal.setTenantId(10000);
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(IdGeneratorUtil.nextId());
        orderEntity.setUserId("12");
        orderEntity.setCommodityCode("d");
        orderEntity.setCount(1);
        orderEntity.setMoney(2);
        orderEntity.setStatus(2);
        orderMapper.insert(orderEntity);
    }
//    public void pageTest() {
//        Page<OrderEntity> page = new Page<OrderEntity>(1, 1);
//        List<OrderEntity> result = orderMapper.selectTableBypage(page, testTableVo.getPhoneModel());
//
//        PageBean pageBean = new PageBean<>(result, page.getTotal(), page.getCurrent(), page.getSize());
//    }
}
