//package com.kl.example.service.impl;
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.thread.NamedThreadFactory;
//import com.alibaba.csp.sentinel.annotation.SentinelResource;
//import com.alibaba.csp.sentinel.slots.block.BlockException;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.ewp.core.base.monitor.healthcheck.HealthCheck;
//import com.ewp.core.common.dto.model.GitVersion;
//import com.ewp.core.common.dto.model.ServerStatus;
//import com.ewp.core.common.exception.EwpException;
//import com.ewp.core.common.thread.EwpThreadLocal;
//import com.ewp.core.common.thread.threadproxy.RunnableProxy;
//import com.ewp.core.common.util.JsonUtils;
//import com.ewp.examples.api.dto.PageBean;
//import com.ewp.examples.api.dto.TestTableDto;
//import com.ewp.examples.api.dversion.ExamplesApiVersion;
//import com.ewp.examples.api.service.MonitorService;
//import com.ewp.examples.api.vo.TestTableVo;
//import com.ewp.examples.service.biz.LiveCourseBiz;
//import com.ewp.examples.service.data.live.entity.TCustomerClickLogEntity;
//import com.ewp.examples.service.data.live.mapper.TCustomerClickLogMapper;
//import com.ewp.examples.service.data.test.entity.TTableTestEntity;
//import com.ewp.examples.service.data.test.mapper.TTableTestMapper;
//import com.ewp.examples.service.enums.RedisKeyConstant;
//import com.ewp.examples.service.po.TCustomerClickLogEntityJson;
//import com.ewp.starter.databases.IdGeneratorUtil;
//import com.ewp.starter.kafka.producer.KafkaManagerBiz;
//import com.ewp.starter.redis.lock.LockService;
//import com.ewp.starter.redis.service.RedisCommand;
//import com.ewp.starter.rocketmq.producer.RocketmqManagerBiz;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.info.GitProperties;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.context.ApplicationContext;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//@DubboService(version = ExamplesApiVersion.VERSION_1, group = ExamplesApiVersion.GROUP_EWP)
//@Slf4j
//@RefreshScope
//public class MonitorServiceImpl implements MonitorService {
//
//    private static ExecutorService executor = Executors.newFixedThreadPool(20, new NamedThreadFactory("ServerStatus-", false));
//
//    @Autowired
//    private ApplicationContext applicationContext;
//
//    @Autowired
//    TCustomerClickLogMapper tCustomerClickLogMapper;
//
//    @Value("${ewp.test}")
//    private String testConfig;
//
//    @Value("${kafka.topic.heartBeat}")
//    private String kafkaHeartBeat;
//
//    @Value("${rocketmq.topic.heartBeat}")
//    private String rocketmqHeartBeat;
//
//    @Autowired
//    private GitProperties git;
//
//    @Autowired
//    private KafkaManagerBiz kafkaManagerBiz;
//
//    @Autowired
//    private RocketmqManagerBiz rocketmqManagerBiz;
//
//    @Autowired
//    private LiveCourseBiz liveCourseBiz;
//
//    @Autowired
//    ValueOperations<String, String> valueOperations;
//
//    @Autowired
//    TTableTestMapper tableTestMapper;
//
//    @Autowired
//    LockService lockService;
//
//    @Autowired
//    RedisCommand redisCommand;
//
//    @Autowired
//    StringRedisTemplate stringRedisTemplate;
//
//    Map<String, HealthCheck> map = null;
//
//
//    /**
//     * 展示git相关信息
//     *
//     * @return
//     */
//    @Override
//    public GitVersion showGitVersion() {
//        GitVersion gitVersion = new GitVersion();
//        gitVersion.setCommitId(git.getCommitId());
//        gitVersion.setBranch(git.getBranch());
//        gitVersion.setBuildTime(git.get("build.time"));
//        gitVersion.setCommitTime(git.get("commit.time"));
//        return gitVersion;
//    }
//
//    public String getBlockHandler(Long customerId, BlockException throwable) {
//        log.error("--------", throwable);
//        return "customerData";
//    }
//
//
//    /**
//     * 服务测试
//     *
//     * @return
//     */
//    @Override
//    @SentinelResource(blockHandler = "getBlockHandler")
//    public String testService(Long customerId) {
//        // 测试redis
//        testRedis(customerId);
//        // 测试合作方自动插入
//        TCustomerClickLogEntity tCustomerClickLogEntity = buildTCustomerClickLogEntity(customerId);
//        tCustomerClickLogMapper.insert(tCustomerClickLogEntity);
//        // 测试获取当前合作方
//        Integer partnerCode = EwpThreadLocal.getPartnerCode();
//        log.info("[testpartnerCode]partnerCode:{}", partnerCode);
//        // 测试动态数据源
//        TTableTestEntity tableTestEntityFirst = liveCourseBiz.selectFromFirst();
//        log.info("[testDb]selectFromFirst:{}", tableTestEntityFirst);
//        // 测试异步动态数据源
//        liveCourseBiz.selectFromSecond();
//        // 测试tidb
//        TTableTestEntity tableTestEntityTidb = liveCourseBiz.selectFromTidb();
//        log.info("[testDb]selectFromTidb:{}", tableTestEntityTidb);
////        // 测试rocketmq
////        rocketmqManagerBiz.sendAsyncMessage(rocketmqHeartBeat, EwpThreadLocal.getPartnerCode(), System.currentTimeMillis());
////        rocketmqManagerBiz.sendAsyncMessage(rocketmqHeartBeat, EwpThreadLocal.getPartnerCode(), IdGeneratorUtil.nextId());
////        // 测试kafka
////        kafkaManagerBiz.sendMessage(kafkaHeartBeat, EwpThreadLocal.getPartnerCode(), new KafkaCommonData(System.currentTimeMillis(), System.currentTimeMillis(), KafkaCMDTypeEnum.INSERT.name()));
//        return testConfig;
//    }
//
//    private void testRedis(Long customerId) {
//        lockService.lockAutoRelease(RedisKeyConstant.LOCK_KEY_TEST.getKeyName(), RedisKeyConstant.LOCK_KEY_TEST.getExpireTime(), () -> {
//            log.info("测试自动释放锁");
//            return null;
//        });
//
//        // 测试分布式锁
//        if (!lockService.lock(RedisKeyConstant.LOCK_KEY_TEST.getKeyName(), "aaa", RedisKeyConstant.LOCK_KEY_TEST.getTimeUnit().toMillis(RedisKeyConstant.LOCK_KEY_TEST.getExpireTime()))) {
//            throw new EwpException("加锁失败");
//        }
//        // 测试分布式锁
//        if (!lockService.lock(RedisKeyConstant.LOCK_KEY_TEST.getKeyName() + "111", "bbb", RedisKeyConstant.LOCK_KEY_TEST.getTimeUnit().toMillis(RedisKeyConstant.LOCK_KEY_TEST.getExpireTime()))) {
//            throw new EwpException("加锁失败");
//        }
//        try {
//            TCustomerClickLogEntity tCustomerClickLogEntity = buildTCustomerClickLogEntity(customerId);
//            stringRedisTemplate.opsForValue().set(RedisKeyConstant.STRING_TEST.getKeyName(), "redistest");
//            // 测试json字符串,序列化和反序列化可以不是同一个class 适合简单对象
//            redisCommand.setToJsonStr(RedisKeyConstant.JSON_TEST.getKeyName(), tCustomerClickLogEntity);
//            TCustomerClickLogEntityJson customerClickLogEntity = redisCommand.getFromJsonStr(RedisKeyConstant.JSON_TEST.getKeyName(), TCustomerClickLogEntityJson.class);
//            log.info("[testRedis] JSON_TEST:{}", customerClickLogEntity);
//            // 测试对象-序列化和反序列化必须是同一个class 适合复杂对象
//            redisCommand.set(RedisKeyConstant.OBJECT_TEST.getKeyName(), tCustomerClickLogEntity);
//            redisCommand.set(RedisKeyConstant.OBJECT_TEST.getKeyName(), tCustomerClickLogEntity, RedisKeyConstant.OBJECT_TEST.getExpireTime(), RedisKeyConstant.OBJECT_TEST.getTimeUnit());
//            TCustomerClickLogEntity logEntity = redisCommand.get(RedisKeyConstant.OBJECT_TEST.getKeyName());
//            log.info("[testRedis] stringRedis:{}", logEntity);
//
//            // 测试set
//            redisCommand.sAdd(RedisKeyConstant.SET_TEST.getKeyName(), tCustomerClickLogEntity);
//            tCustomerClickLogEntity.setCreatedTime(LocalDateTime.now());
//            redisCommand.sAdd(RedisKeyConstant.SET_TEST.getKeyName(), tCustomerClickLogEntity);
//            Set<TCustomerClickLogEntity> sMembers = redisCommand.sMembers(RedisKeyConstant.SET_TEST.getKeyName());
//            log.info("[testRedis] setRedis:{}", sMembers);
//            // 测试hash
//            redisCommand.hSet(RedisKeyConstant.HASH_TEST.getKeyName(), "name", tCustomerClickLogEntity);
//            redisCommand.hSet(RedisKeyConstant.HASH_TEST.getKeyName(), "sex", tCustomerClickLogEntity);
//            TCustomerClickLogEntity entity = redisCommand.hGet(RedisKeyConstant.HASH_TEST.getKeyName(), "sex");
//            log.info("[testRedis] hashRedis:{}", logEntity);
//            // 测试list
//            redisCommand.lPush(RedisKeyConstant.LIST_TEST.getKeyName(), tCustomerClickLogEntity);
//            redisCommand.lPush(RedisKeyConstant.LIST_TEST.getKeyName(), tCustomerClickLogEntity);
//            redisCommand.lPush(RedisKeyConstant.LIST_TEST.getKeyName(), tCustomerClickLogEntity);
//            String time = System.currentTimeMillis() + "";
//            // 测试redis
//            valueOperations.set(String.format(RedisKeyConstant.SERVICE_STATUS_TEST.getKeyName(), time),
//                    time, 10, TimeUnit.MINUTES);
//            String value = valueOperations.get(String.format(RedisKeyConstant.SERVICE_STATUS_TEST.getKeyName(), time));
//            log.info("timeSystem:{}.timeRedis:{}", time, value);
//        } finally {
//            // 释放redis分布式锁
//            lockService.releaseLock(RedisKeyConstant.LOCK_KEY_TEST.getKeyName() + "111", "bbb");
//            lockService.releaseLock(RedisKeyConstant.LOCK_KEY_TEST.getKeyName(), "aaa");
//        }
//    }
//
//    private TCustomerClickLogEntity buildTCustomerClickLogEntity(Long customerId) {
//        TCustomerClickLogEntity tCustomerClickLogEntity = new TCustomerClickLogEntity();
//        tCustomerClickLogEntity.setId(IdGeneratorUtil.nextId());
//        tCustomerClickLogEntity.setCreatedTime(LocalDateTime.now());
//        tCustomerClickLogEntity.setCustomerId(customerId);
//        return tCustomerClickLogEntity;
//    }
//
//    /**
//     * 测试分页查询
//     *
//     * @return
//     */
//    public PageBean<TestTableDto> selectPage(TestTableVo testTableVo) {
//        Page<TTableTestEntity> page = new Page<TTableTestEntity>(testTableVo.getPageIndex(), testTableVo.getPageSize());
//        List<TTableTestEntity> result = tableTestMapper.selectTableBypage(page, testTableVo.getPhoneModel());
//        List<TestTableDto> collect = result.stream().map(o -> {
//            TestTableDto testTableDto = new TestTableDto();
//            BeanUtil.copyProperties(o, testTableDto);
//            return testTableDto;
//        }).collect(Collectors.toList());
//        return new PageBean<>(collect, page.getTotal(), page.getCurrent(), page.getSize());
//    }
//
//    /**
//     * 展示服务状态信息信息
//     *
//     * @return
//     */
//    @Override
//    public ServerStatus validServerStatus(Map<String, String> params) {
//        if (CollectionUtil.isEmpty(map)) {
//            map = applicationContext.getBeansOfType(HealthCheck.class);
//        }
//        ServerStatus serverStatus = new ServerStatus();
//        if (CollectionUtil.isEmpty(map)) {
//            return serverStatus;
//        }
//        CountDownLatch countDownLatch = new CountDownLatch(map.size());
//        map.forEach((s, healthCheck) -> {
//            serverStatus.put(s, ServerStatus.TIMEOUT);
//            executor.execute(new RunnableProxy(() -> {
//                try {
//                    long startTime = System.currentTimeMillis();
//                    serverStatus.put(s, healthCheck.doHealthCheck(params));
//                    long takeTime = System.currentTimeMillis() - startTime;
//                    log.info("[ServerStatus] {},服务检测耗时：{}", s, takeTime);
//                } finally {
//                    countDownLatch.countDown();
//                }
//            }).creatRunnableProxy());
//        });
//        try {
//            boolean done = countDownLatch.await(2, TimeUnit.SECONDS);
//            if (!done) {
//                log.error("[ServerStatus] 等待超时,result:{}", JsonUtils.objectToJson(serverStatus));
//            }
//        } catch (InterruptedException interruptedException) {
//            log.error("[ServerStatus] 等待超时, result:{}", JsonUtils.objectToJson(serverStatus), interruptedException);
//        }
//        return serverStatus;
//    }
//}
