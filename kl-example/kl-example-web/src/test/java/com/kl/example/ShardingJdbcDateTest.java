//package com.ewp.examples.service.impl;
//
//import com.baomidou.mybatisplus.core.conditions.Wrapper;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.ewp.core.common.util.LocalDateUtil;
//import com.ewp.examples.service.EwpExamplesServiceApplication;
//import com.ewp.examples.service.data.sharding.entity.TSectionBrowseRecordEntity;
//import com.ewp.examples.service.data.sharding.mapper.TSectionBrowseRecordMapper;
//import com.ewp.starter.databases.IdGeneratorUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import javax.annotation.Resource;
//import java.time.LocalDateTime;
//import java.util.Random;
//
///**
// * 3.3 日期分片（自定义分片）的示例
// */
//@Slf4j
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(classes = EwpExamplesServiceApplication.class)
//class ShardingJdbcDateTest {
//
//    @Resource
//    private TSectionBrowseRecordMapper tSectionBrowseRecordMapper;
//
//    /**
//     * 时间分片-单表-等于查询-=
//     */
//    @Test
//    void findByEqual() {
//        Page<TSectionBrowseRecordEntity> page = new Page<>(1, 20);
//        LambdaQueryWrapper<TSectionBrowseRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(TSectionBrowseRecordEntity::getCreatedTime, LocalDateUtil.strToLocalDateTime("2019-08-27 19:12:03"));
//        Page<TSectionBrowseRecordEntity> tSectionBrowseRecordEntityPage = tSectionBrowseRecordMapper.selectPage(page, queryWrapper);
//        tSectionBrowseRecordEntityPage.getRecords().forEach(System.out::println);
//    }
//
//    /**
//     * 时间分片-单表-等于查询-IN
//     */
//    @Test
//    void findByIn() {
//        Page<TSectionBrowseRecordEntity> page = new Page<>(1, 20);
//        LambdaQueryWrapper<TSectionBrowseRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.in(TSectionBrowseRecordEntity::getCreatedTime,
//                LocalDateUtil.strToLocalDateTime("2019-08-27 19:09:56"),
//                LocalDateUtil.strToLocalDateTime("2019-08-27 19:24:21"),
//                LocalDateUtil.strToLocalDateTime("2019-08-27 19:25:25")
//        );
//        Page<TSectionBrowseRecordEntity> tSectionBrowseRecordEntityPage = tSectionBrowseRecordMapper.selectPage(page, queryWrapper);
//        tSectionBrowseRecordEntityPage.getRecords().forEach(System.out::println);
//    }
//
//    /**
//     * 时间分片-单表-范围查询-包前 & 包后
//     */
//    @Test
//    void searchByBetween() {
//        Page<TSectionBrowseRecordEntity> page = new Page<>(1, 20);
//        Wrapper<TSectionBrowseRecordEntity> wrapper = new LambdaQueryWrapper<TSectionBrowseRecordEntity>()
//                // 查询的时候必须携带分片字段的 时间范围 或 值
//                // 包前 & 包后
//                .between(TSectionBrowseRecordEntity::getCreatedTime,
//                        LocalDateUtil.strToLocalDateTime("2019-08-01 00:00:00"),
//                        LocalDateUtil.strToLocalDateTime("2019-09-01 00:00:00"));
//        Page<TSectionBrowseRecordEntity> tSectionBrowseRecordEntityPage = tSectionBrowseRecordMapper.selectPage(page, wrapper);
//        tSectionBrowseRecordEntityPage.getRecords().forEach(System.out::println);
//    }
//
//    /**
//     * 时间分片-单表-范围查询-包前 & 不包后，携带无效条件
//     */
//    @Test
//    void searchByGtAndLt() {
//        Page<TSectionBrowseRecordEntity> page = new Page<>(1, 20);
//        Wrapper<TSectionBrowseRecordEntity> wrapper = new LambdaQueryWrapper<TSectionBrowseRecordEntity>()
//                // 查询的时候必须携带分片字段的 时间范围 或 值
//                // 包前 & 不包后，携带无效条件
//                .ge(TSectionBrowseRecordEntity::getCreatedTime, LocalDateUtil.strToLocalDateTime("2019-08-01 00:00:00"))
//                .ge(TSectionBrowseRecordEntity::getCreatedTime, LocalDateUtil.strToLocalDateTime("2019-09-01 00:00:00"))
//                .lt(TSectionBrowseRecordEntity::getCreatedTime, LocalDateUtil.strToLocalDateTime("2019-10-01 00:00:00"));
//        Page<TSectionBrowseRecordEntity> tSectionBrowseRecordEntityPage = tSectionBrowseRecordMapper.selectPage(page, wrapper);
//        tSectionBrowseRecordEntityPage.getRecords().forEach(System.out::println);
//    }
//
//    /**
//     * 时间分片-单表-insert
//     */
//    @Test
//    void add() {
//        TSectionBrowseRecordEntity entity = new TSectionBrowseRecordEntity();
//        entity.setId(IdGeneratorUtil.nextId());
//        entity.setCustomerId(207104926928867331L);
//        entity.setCampDateId(211874424382816256L);
//        entity.setCourseId(208989449174122496L);
//        entity.setChapterId(208993993954164736L);
//        entity.setSectionId(208994826154409984L);
//        entity.setStayTime(1243L);
//        entity.setOsName("单元测试");
//        entity.setCreatedTime(LocalDateUtil.strToLocalDateTime("2019-08-27 19:09:56"));
//        entity.setModifiedTime(LocalDateTime.now());
//
//        tSectionBrowseRecordMapper.insert(entity);
//    }
//
//    /**
//     * 时间分片-单表-update
//     */
//    @Test
//    void update() {
//        TSectionBrowseRecordEntity entity = new TSectionBrowseRecordEntity();
//        entity.setOsName("单元测试：update：" + new Random().nextInt(100));
//        Wrapper<TSectionBrowseRecordEntity> wrapper = new LambdaQueryWrapper<TSectionBrowseRecordEntity>()
//                .between(TSectionBrowseRecordEntity::getCreatedTime,
//                        LocalDateUtil.strToLocalDateTime("2019-08-01 00:00:00"),
//                        LocalDateUtil.strToLocalDateTime("2019-08-31 23:59:59"))
//                .like(TSectionBrowseRecordEntity::getOsName, "单元测试");
//        tSectionBrowseRecordMapper.update(entity, wrapper);
//    }
//
//    /**
//     * 时间分片-单表-delete
//     */
//    @Test
//    void delete() {
//        Wrapper<TSectionBrowseRecordEntity> wrapper = new LambdaQueryWrapper<TSectionBrowseRecordEntity>()
//                .between(TSectionBrowseRecordEntity::getCreatedTime,
//                        LocalDateUtil.strToLocalDateTime("2019-08-01 00:00:00"),
//                        LocalDateUtil.strToLocalDateTime("2019-08-31 23:59:59"))
//                .like(TSectionBrowseRecordEntity::getOsName, "单元测试");
//        tSectionBrowseRecordMapper.delete(wrapper);
//    }
//}
