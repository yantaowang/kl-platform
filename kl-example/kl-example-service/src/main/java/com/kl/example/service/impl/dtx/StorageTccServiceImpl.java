//package com.kl.example.service.impl.dtx;
//
//import com.ewp.core.common.enums.ResponseCode;
//import com.ewp.examples.api.dversion.ExamplesApiVersion;
//import com.ewp.examples.api.service.StorageTccService;
//import com.ewp.examples.service.data.dtx.mapper.StorageMapper;
//import io.seata.rm.tcc.api.BusinessActionContext;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * 分布式事务-AT/XA-库存服务实现（假设放到 storage 服务）
// *
// * @author xinminghao
// */
//@Slf4j
//@RequiredArgsConstructor
//@DubboService(version = ExamplesApiVersion.VERSION_1, group = ExamplesApiVersion.GROUP_EWP)
//public class StorageTccServiceImpl implements StorageTccService {
//
//    private final StorageMapper storageMapper;
//
//    @Override
//    @Transactional
//    public Integer reduceStorage(BusinessActionContext actionContext, String commodityCode, int count) {
//        // TODO 未实现：解决悬挂问题
//        // TODO 解决方法：二阶段执行时插入一条事务控制记录，状态为已回滚，这样当一阶段执行时，先读取该记录，如果记录存在，就认为二阶段已经执行；否则二阶段没执行。
//        log.info("Storage Service prepare ... xid: {}, ", actionContext.getXid());
//        int result = storageMapper.lockStorage(count, commodityCode);
//        log.info("Storage Service prepare End ... ");
//        if (result == 1) {
//            return ResponseCode.SUCCESS.getCode();
//        }
//        return ResponseCode.SERVICE_ERROR.getCode();
//    }
//
//    @Override
//    @Transactional
//    public boolean commit(BusinessActionContext actionContext) {
//        log.info("Storage Service commit ... xid: {}, ", actionContext.getXid());
//
//        // TODO 未实现 幂等
//        // TODO 解决方法：记录每个分支事务的执行状态。在执行前状态，如果已执行，那就不再执行；否则，正常执行。使用事务控制表，事务控制表的每条记录关联一个分支事务，那我们完全可以在这张事务控制表上加一个状态字段，用来记录每个分支事务的执行状态。
//        String commodityCode = (String) actionContext.getActionContext("commodityCode");
//        Integer count = (Integer) actionContext.getActionContext("count");
//
//        int result = storageMapper.reduceStorageFromLock(count, commodityCode);
//        log.info("Storage Service commit End ... ");
//
//        return result == 1;
//    }
//
//    @Override
//    @Transactional
//    public boolean rollback(BusinessActionContext actionContext) {
//        log.info("Storage Service rollback ... xid: {}, ", actionContext.getXid());
//
//        // TODO 未实现 空回滚
//        // TODO 解决方法：需要一张额外的事务控制表，其中有分布式事务 ID 和分支事务 ID，第一阶段 Try 方法里会插入一条记录，表示一阶段执行了。Cancel 接口里读取该记录，如果该记录存在，则正常回滚；如果该记录不存在，则是空回滚。
//        // TODO 未实现 幂等
//        String commodityCode = (String) actionContext.getActionContext("commodityCode");
//        Integer count = (Integer) actionContext.getActionContext("count");
//
//        if (commodityCode == null || count == null) {
//            return true;
//        }
//        int result = storageMapper.releaseStorage(count, commodityCode);
//        log.info("Storage Service rollback End ... ");
//
//        return true;
//    }
//}
