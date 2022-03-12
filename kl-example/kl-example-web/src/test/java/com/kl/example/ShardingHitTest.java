//package com.ewp.examples.service.impl;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.ewp.examples.service.EwpExamplesServiceApplication;
//import com.ewp.examples.service.data.sharding.entity.TOrderForceEntity;
//import com.ewp.examples.service.data.sharding.mapper.TOrderForceMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.shardingsphere.api.hint.HintManager;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import javax.annotation.Resource;
//import java.util.List;
//
///**
// * 3.1 强制路由示例
// */
//@Slf4j
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(classes = EwpExamplesServiceApplication.class)
//public class ShardingHitTest {
//
//    @Resource
//    private TOrderForceMapper orderForceMapper;
//
//    @Test
//    public void testSysout() {
//        try (HintManager hintManager = HintManager.getInstance()) {
//            // 设置库和表的分片路由
//            hintManager.addDatabaseShardingValue("t_order_force", "examples0");
//            hintManager.addTableShardingValue("t_order_force", "t_order_0");
//
//            List<TOrderForceEntity> courseEntities = orderForceMapper.selectList(new LambdaQueryWrapper<>());
//            orderForceMapper.updateById(courseEntities.get(0));
//        }
//
//        try (HintManager hintManager = HintManager.getInstance()) {
//            // 设置库和表的分片路由
//            hintManager.addDatabaseShardingValue("t_order_force", "examples1");
//            hintManager.addTableShardingValue("t_order_force", "t_order_1");
//
//            List<TOrderForceEntity> courseEntities = orderForceMapper.selectList(new LambdaQueryWrapper<>());
//            orderForceMapper.updateById(courseEntities.get(0));
//        }
//    }
//}
