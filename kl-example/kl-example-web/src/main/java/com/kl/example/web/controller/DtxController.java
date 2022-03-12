package com.kl.example.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分布式事务演示示例
 * 注意：示例中 OrderService StorageService AccountService 在实际应用中应该为三个服务器上，这里仅做演示，不做区分
 */
@Slf4j
@RestController
@RequestMapping("/api/dtx")
public class DtxController {

    private static final String SUCCESS = "SUCCESS";
    /**
     * 示例商品编码
     */
    private static final String COMMODITY_CODE = "C00321";
    /**
     * 示例商品购买数量
     */
    private static final int ORDER_COUNT = 2;
    /**
     * 示例用户ID
     */
    private static final String USER_ID = "U100001";

/**
     * AT/XA 模式的示例
     * 注意：
     * 1. AT/XA 模式的使用区别在于 spring.datasource.dynamic.seata-mode 配置（Biz层）
     * 2. Service层入口要加：@DS("first")、@Transactional(propagation = Propagation.REQUIRES_NEW)，以兼容动态数据源
     * 3. 事务最外层要加  @GlobalTransactional(timeoutMills = 300000, name = "exampleDtxAt", rollbackFor = Exception.class)，name要唯一
     * 4. 此处配置和seata官方配置不一致，主要为了兼容动态数据源
     */
    @Transactional
//    @GlobalTransactional(timeoutMills = 300000, name = "exampleDtxAt", rollbackFor = Exception.class)
    @GetMapping(value = "/buyByAt")
    public String buyByAt() {
//
//        // 扣减库存
//        Integer storageResult = storageAtService.reduceStorage(COMMODITY_CODE, ORDER_COUNT);
//        if (ResponseCode.SUCCESS.getCode() != storageResult) {
//            throw new EwpException("扣减库存失败");
//        }
//
//        // 下单
//        EwpResponse<Integer> orderReturn = orderAtService.submitOrder(USER_ID, COMMODITY_CODE, ORDER_COUNT);
//        if (ResponseCode.SUCCESS.getCode() != orderReturn.getCode()) {
//            throw new EwpException("下单失败");
//        }
//
//        // 扣款
//        Integer money = orderReturn.getData();
//        Integer moneyResult = accountAtService.reduceMoney(USER_ID, money);
//        if (ResponseCode.SUCCESS.getCode() != moneyResult) {
//            throw new EwpException("扣款失败");
//        }
        return SUCCESS;
    }

    /**
     * TCC 模式的示例
     * 设计注意事项：
     * 1、幂等控制：用户在实现TCC服务时，需要考虑幂等控制，即Try、Confirm、Cancel 执行次和执行多次的业务结果是一样的。
     * 2、允许空回滚：即TCC服务在未收到Try请求的情况下收到Cancel请求（try请求丢失导致的事务回滚），这种场景被称为空回滚；
     * 3、防资源悬挂（seata自己控制）：Cancel比Try先执行（try请求超时导致的事务回滚，后来try请求又被收到，导致数据修改固定在init状态），会形成资源悬挂。
     * 4、业务数据可见性控制：修改的中间状态的业务数据该如何向用户展示，需要业务在实现时考虑清楚；通常的设计原则是“宁可不展示、少展示，也不多展示、错展示”；
     * 5、业务数据并发访问控制：用户在实现TCC服务时，需要考虑业务数据的并发控制，尽量将逻辑锁粒度降到最低，以最大限度的提高分布式事务的并发性；
     */
//    @GlobalTransactional(timeoutMills = 300000, name = "exampleDtxTcc", rollbackFor = Exception.class)
    @GetMapping(value = "/buyByTcc")
    public String buyByTcc() {
//        log.info("xid: {}", RootContext.getXID());
//        // 扣减库存
//        Integer storageResult = storageTccService.reduceStorage(null, COMMODITY_CODE, ORDER_COUNT);
//        if (ResponseCode.SUCCESS.getCode() != storageResult) {
//            throw new EwpException("扣减库存失败");
//        }
//
//        // 下单
//        EwpResponse<Integer> orderReturn = orderTccService.submitOrder(
//                null, orderTccService.generateOrderId(), USER_ID, COMMODITY_CODE, ORDER_COUNT);
//        if (ResponseCode.SUCCESS.getCode() != orderReturn.getCode()) {
//            throw new EwpException("下单失败");
//        }
//
//        // 扣款
//        Integer money = orderReturn.getData();
//        Integer moneyResult = accountTccService.reduceMoney(null, USER_ID, money);
//        if (ResponseCode.SUCCESS.getCode() != moneyResult) {
//            throw new EwpException("扣款失败");
//        }
        return SUCCESS;
    }
}
