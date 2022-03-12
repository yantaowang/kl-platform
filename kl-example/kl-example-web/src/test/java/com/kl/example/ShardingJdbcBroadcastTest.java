//package com.ewp.examples.service.impl;
//
//import com.baomidou.mybatisplus.core.conditions.Wrapper;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.ewp.examples.service.EwpExamplesServiceApplication;
//import com.ewp.examples.service.data.sharding.entity.TSystemConfigureEntity;
//import com.ewp.examples.service.data.sharding.mapper.TSystemConfigureMapper;
//import com.ewp.starter.databases.IdGeneratorUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import javax.annotation.Resource;
//import java.time.LocalDateTime;
//
///**
// * 2 广播表示例
// */
//@Slf4j
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(classes = EwpExamplesServiceApplication.class)
//class ShardingJdbcBroadcastTest {
//
//    @Resource
//    private TSystemConfigureMapper tSystemConfigureMapper;
//
//    /**
//     * 广播表-单表-查询分片字段聚合
//     */
//    @Test
//    void testBroadcastSearch() {
//        Page<TSystemConfigureEntity> page = new Page<>(1, 20);
//        Page<TSystemConfigureEntity> tSystemConfigureEntityPage = tSystemConfigureMapper.selectPage(page, new QueryWrapper<>());
//        tSystemConfigureEntityPage.getRecords().forEach(System.out::println);
//    }
//
//    /**
//     * 广播表-单表-insert
//     */
//    @Test
//    void testBroadcastAdd() {
//        TSystemConfigureEntity entity = new TSystemConfigureEntity();
//        entity.setId(IdGeneratorUtil.nextId());
//        entity.setConfigureCode(String.valueOf(IdGeneratorUtil.nextId()));
//        entity.setConfigureName("分库分表测试");
//        entity.setConfigureType("string");
//        entity.setConfigureValue("hello");
//        entity.setConfigureDesc("分库分表测试");
//        entity.setCreatedTime(LocalDateTime.now());
//        entity.setModifiedTime(LocalDateTime.now());
//        entity.setOpen(1);
//        tSystemConfigureMapper.insert(entity);
//    }
//
//    /**
//     * 广播表-单表-update
//     */
//    @Test
//    void testBroadcastUpdate() {
//        TSystemConfigureEntity entity = new TSystemConfigureEntity();
//        entity.setConfigureDesc("分库分表测试：update：" + IdGeneratorUtil.nextId());
//        Wrapper<TSystemConfigureEntity> wrapper = new LambdaQueryWrapper<TSystemConfigureEntity>()
//                .eq(TSystemConfigureEntity::getConfigureName, "分库分表测试");
//        tSystemConfigureMapper.update(entity, wrapper);
//    }
//
//    /**
//     * 广播表-单表-delete
//     */
//    @Test
//    void testBroadcastDelete() {
//        Wrapper<TSystemConfigureEntity> wrapper = new LambdaQueryWrapper<TSystemConfigureEntity>()
//                .eq(TSystemConfigureEntity::getConfigureName, "分库分表测试");
//        tSystemConfigureMapper.delete(wrapper);
//    }
//}
