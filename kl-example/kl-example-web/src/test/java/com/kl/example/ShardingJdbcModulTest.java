//package com.ewp.examples.service.impl;
//
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
//import com.ewp.examples.service.EwpExamplesServiceApplication;
//import com.ewp.examples.service.data.sharding.entity.TOrderEntity;
//import com.ewp.examples.service.data.sharding.entity.TRefundOrderEntity;
//import com.ewp.examples.service.data.sharding.mapper.TOrderMapper;
//import com.ewp.examples.service.data.sharding.mapper.TRefundOrderMapper;
//import com.ewp.examples.service.data.sharding.mapper.TSystemConfigureMapper;
//import com.ewp.starter.databases.IdGeneratorUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import javax.annotation.Resource;
//import java.time.Instant;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//
///**
// * 取模分片单元测试
// */
//@Slf4j
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(classes = EwpExamplesServiceApplication.class)
//class ShardingJdbcModulTest {
//
//    @Resource
//    private TOrderMapper tOrderMapper;
//
//    @Resource
//    private TRefundOrderMapper tRefundOrderMapper;
//
//    /**
//     * 分片-单表-查询非分片主键
//     */
//    @Test
//    void select() {
//        // TODO 反例：全表扫
//        tOrderMapper.selectById(208375752114311204L);
//    }
//
//    /**
//     * 分片-单表-插入数据
//     */
//    @Test
//    void insert() {
//        TOrderEntity entity = new TOrderEntity();
//        entity.setId(IdGeneratorUtil.nextId());
//        entity.setCourseId(208618576911007744L);
//        entity.setCampDateId(208651775422758913L);
//        entity.setClassId(208664954085318665L);
//        entity.setTeacherId(208660678973849600L);
//
//        entity.setCustomerId(384007631502311669L);
//        entity.setPayOrderId(216965963714928674L);
//
//        entity.setProCode(null);
//        entity.setName("分库分表测试");
//        entity.setPriceAmount(1L);
//        entity.setPayAmount(1L);
//        entity.setStatus(2);
//        entity.setRemark("分库分表测试");
//        entity.setIsDeleted(false);
//        entity.setInvalidDate(Instant.now());
//        entity.setCreatedTime(LocalDateTime.now());
//        entity.setModifiedTime(LocalDate.now());
//        entity.setTime(LocalTime.now());
//
//        tOrderMapper.insert(entity);
//    }
//
//    /**
//     * 分片-单表-查询非分片字段
//     */
//    @Test
//    void search() {
//        // TODO 反例：全表扫
//        Page<TOrderEntity> page = new Page<>(1, 20);
//        Page<TOrderEntity> entityPage = tOrderMapper.selectPage(page, Wrappers.lambdaQuery(TOrderEntity.class)
//                .eq(TOrderEntity::getRemark, "分库分表测试"));
//        entityPage.getRecords().forEach(System.out::println);
//    }
//
//    /**
//     * 分片-单表-查询分片字段
//     */
//    @Test
//    void query() {
//        Page<TOrderEntity> page = new Page<>(1, 20);
//        Page<TOrderEntity> entityPage = ChainWrappers.lambdaQueryChain(tOrderMapper)
//                .eq(TOrderEntity::getCustomerId, 384007631502311669L)
//                .eq(TOrderEntity::getPayOrderId, 216965963714928674L)
//                .page(page);
//        entityPage.getRecords().forEach(System.out::println);
//    }
//
//    /**
//     * 分片-单表-查询分片字段聚合
//     */
//    @Test
//    void count() {
//        int count = ChainWrappers.lambdaQueryChain(tOrderMapper)
//                .eq(TOrderEntity::getCustomerId, 384007631502311669L)
//                .eq(TOrderEntity::getPayOrderId, 216965963714928674L)
//                .count();
//        System.out.println(count);
//    }
//
//    /**
//     * 分片-单表-查询非分片字段聚合
//     */
//    @Test
//    void countAll() {
//        // TODO 反例：全表扫
//        int count = ChainWrappers.lambdaQueryChain(tOrderMapper).count();
//        System.out.println(count);
//    }
//
//    // /**
//    //  * 强制路由-单表-查询聚合
//    //  * TODO 需要用户自行实现
//    //  */
//    // @Test
//    // @DSTransactional
//    // void forceRoute() {
//    //     try (HintManager hintManager = HintManager.getInstance()) {
//    //         hintManager.addDatabaseShardingValue("t_order", 0);
//    //         hintManager.addTableShardingValue("t_order", 0);
//    //         PageHelper.startPage(0, 20, false);
//    //         List<TOrderForceEntity> ss = tOrderForceMapper.selectList(Wrappers.lambdaQuery(TOrderForceEntity.class)
//    //                 .eq(TOrderForceEntity::getRemark, "分库分表测试"));
//    //         ss.forEach(System.out::println);
//    //     }
//    // }
//
//    /**
//     * 分片-单表-关联表防止笛卡尔积现象
//     * 注意：关联时，表要使用别名
//     */
//    @Test
//    void joinSearch() {
//        TRefundOrderEntity tRefundOrderEntity = tRefundOrderMapper.selectOrderInfoByRefundId(306796432949780534L);
//        System.out.println(tRefundOrderEntity);
//    }
//}
