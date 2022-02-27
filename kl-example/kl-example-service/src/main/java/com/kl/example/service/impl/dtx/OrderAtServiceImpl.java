//package com.kl.example.service.impl.dtx;
//
//import com.baomidou.dynamic.datasource.annotation.DS;
//import com.ewp.core.common.dto.EwpResponse;
//import com.ewp.core.common.exception.EwpException;
//import com.ewp.examples.api.dversion.ExamplesApiVersion;
//import com.ewp.examples.api.service.OrderAtService;
//import com.ewp.examples.service.data.dtx.entity.OrderEntity;
//import com.ewp.examples.service.data.dtx.mapper.OrderMapper;
//import com.ewp.starter.databases.IdGeneratorUtil;
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
// * 分布式事务-AT/XA-订单服务实现（假设放到 order 服务）
// *
// * @author xinminghao
// */
//@Slf4j
//@RequiredArgsConstructor
//@DubboService(version = ExamplesApiVersion.VERSION_1, group = ExamplesApiVersion.GROUP_EWP)
//public class OrderAtServiceImpl implements OrderAtService {
//
//    private final Random random = new Random();
//
//    private final OrderMapper orderMapper;
//
//    @DS("first")// 要指定数据源
//    @Transactional(propagation = Propagation.REQUIRES_NEW)// 要设置成 Propagation.REQUIRES_NEW 事务
//    @Override
//    public EwpResponse<Integer> submitOrder(String userId, String commodityCode, int orderCount) {
//        log.info("Order Service Begin ... xid: " + RootContext.getXID());
//
//        int orderMoney = calculate(commodityCode, orderCount);
//
//        OrderEntity order = new OrderEntity();
//        order.setId(IdGeneratorUtil.nextId());
//        order.setUserId(userId);
//        order.setCommodityCode(commodityCode);
//        order.setCount(orderCount);
//        order.setMoney(orderMoney);
//        order.setStatus(2);
//
//        int result = orderMapper.insert(order);
//
//        if (random.nextBoolean()) {
//            throw new EwpException("this is a mock Exception");
//        }
//
//        log.info("Order Service End ... Created " + order);
//
//        if (result == 1) {
//            // 返回订单金额
//            return EwpResponse.success(orderMoney);
//        }
//        return EwpResponse.failure();
//    }
//
//    /**
//     * 获取订单金额
//     *
//     * @param commodityId 商品编号
//     * @param orderCount  商品数量
//     * @return 订单金额
//     */
//    private int calculate(String commodityId, int orderCount) {
//        // 假定商品单价为 2
//        return 2 * orderCount;
//    }
//}
