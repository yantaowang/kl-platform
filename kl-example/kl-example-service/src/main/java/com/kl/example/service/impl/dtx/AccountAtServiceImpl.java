//package com.kl.example.service.impl.dtx;
//
//import com.baomidou.dynamic.datasource.annotation.DS;
//import com.ewp.core.common.enums.ResponseCode;
//import com.ewp.core.common.exception.EwpException;
//import com.ewp.examples.api.dversion.ExamplesApiVersion;
//import com.ewp.examples.api.service.AccountAtService;
//import com.ewp.examples.service.data.dtx.mapper.AccountMapper;
//import io.seata.core.context.RootContext;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Random;
//
///**
// * 分布式事务-AT/XA-用户服务实现（假设放到 order 服务）
// *
// * @author xinminghao
// */
//@Slf4j
//@RequiredArgsConstructor
//@DubboService(version = ExamplesApiVersion.VERSION_1, group = ExamplesApiVersion.GROUP_EWP)
//public class AccountAtServiceImpl implements AccountAtService {
//
//    private final Random random = new Random();
//
//    private final AccountMapper accountMapper;
//
//    @DS("first")// 要指定数据源
//    @Transactional(propagation = Propagation.REQUIRES_NEW)// 要设置成 Propagation.REQUIRES_NEW 事务
//    @Override
//    public Integer reduceMoney(String userId, int money) {
//        log.info("Account Service ... xid: " + RootContext.getXID());
//
//        if (random.nextBoolean()) {
//            throw new EwpException("this is a mock Exception");
//        }
//
//        int result = accountMapper.reduceMoney(userId, money);
//        log.info("Account Service End ... ");
//        if (result == 1) {
//            return ResponseCode.SUCCESS.getCode();
//        }
//        return ResponseCode.SERVICE_ERROR.getCode();
//    }
//}
