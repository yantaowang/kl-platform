package com.kl.example;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    public static void main(String[] args) {
        Integer pageNo = 3;
        Integer pageSize = 2;
        List<String> newList = Arrays.asList("1","2","ds","fds","fdd","1d").stream()
                .skip(pageNo*pageSize)
                .limit(pageSize).collect(Collectors.toList());
        System.out.println(JSON.toJSONString(newList));
    }

//    public void pageTest() {
//        Page<OrderEntity> page = new Page<OrderEntity>(1, 1);
//        List<OrderEntity> result = orderMapper.selectTableBypage(page, testTableVo.getPhoneModel());
//
//        PageBean pageBean = new PageBean<>(result, page.getTotal(), page.getCurrent(), page.getSize());
//    }
}
