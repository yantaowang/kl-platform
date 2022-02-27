//package com.kl.example.service.impl.dtx;
//
//import com.baomidou.dynamic.datasource.annotation.DS;
//import com.ewp.core.common.enums.ResponseCode;
//import com.ewp.examples.api.dversion.ExamplesApiVersion;
//import com.ewp.examples.api.service.StorageAtService;
//import com.ewp.examples.service.data.dtx.mapper.StorageMapper;
//import io.seata.core.context.RootContext;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * 分布式事务-AT/XA-库存服务实现（假设放到 storage 服务）
// * @author xinminghao
// */
//@Slf4j
//@RequiredArgsConstructor
//@DubboService(version = ExamplesApiVersion.VERSION_1, group = ExamplesApiVersion.GROUP_EWP)
//public class StorageAtServiceImpl implements StorageAtService {
//
//    private final StorageMapper storageMapper;
//
//    @DS("first")// 要指定数据源
//    @Transactional(propagation = Propagation.REQUIRES_NEW)// 要设置成 Propagation.REQUIRES_NEW 事务
//    @Override
//    public Integer reduceStorage(String commodityCode, int count) {
//        log.info("Storage Service Begin ... xid: " + RootContext.getXID());
//
//        int result = storageMapper.reduceStorage(count, commodityCode);
//
//        log.info("Storage Service End ... ");
//        if (result == 1) {
//            return ResponseCode.SUCCESS.getCode();
//        }
//        return ResponseCode.SERVICE_ERROR.getCode();
//    }
//}
