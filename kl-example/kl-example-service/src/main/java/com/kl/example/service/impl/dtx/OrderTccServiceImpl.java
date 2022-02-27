//package com.kl.example.service.impl.dtx;
//
//import com.ewp.core.common.dto.EwpResponse;
//import com.ewp.core.common.exception.EwpException;
//import com.ewp.examples.api.dversion.ExamplesApiVersion;
//import com.ewp.examples.api.service.OrderTccService;
//import com.ewp.examples.service.data.dtx.entity.OrderEntity;
//import com.ewp.examples.service.data.dtx.mapper.OrderMapper;
//import com.ewp.starter.databases.IdGeneratorUtil;
//import io.seata.core.context.RootContext;
//import io.seata.rm.tcc.TwoPhaseResult;
//import io.seata.rm.tcc.api.BusinessActionContext;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.DubboService;
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
//public class OrderTccServiceImpl implements OrderTccService {
//
//    private final Random random = new Random();
//
//    private final OrderMapper orderMapper;
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
//
//    @Override
//    public Long generateOrderId() {
//        return IdGeneratorUtil.nextId();
//    }
//
//    @Override
//    @Transactional
//    public EwpResponse<Integer> submitOrder(BusinessActionContext actionContext,
//                                            Long orderId, String userId, String commodityCode, int orderCount) {
//        log.info("Order Service prepare Begin ... xid: " + RootContext.getXID());
//
//        int orderMoney = calculate(commodityCode, orderCount);
//
//        OrderEntity order = new OrderEntity();
//        order.setId(orderId);
//        order.setUserId(userId);
//        order.setCommodityCode(commodityCode);
//        order.setCount(orderCount);
//        order.setMoney(orderMoney);
//        order.setStatus(1);
//
//        int result = orderMapper.insert(order);
//
//        if (random.nextBoolean()) {
//            throw new EwpException("this is a mock Exception");
//        }
//
//        log.info("Order Service prepare End ... Created " + order);
//
//        if (result == 1) {
//            // 返回订单金额
//            return EwpResponse.success(orderMoney);
//        }
//        return EwpResponse.failure();
//    }
//
//    @Override
//    @Transactional
//    public TwoPhaseResult commit(BusinessActionContext actionContext) {
//        log.info("Order Service commit Begin ... xid: " + RootContext.getXID());
//        // TODO 未实现 幂等
//        // TODO 解决方法：记录每个分支事务的执行状态。在执行前状态，如果已执行，那就不再执行；否则，正常执行。使用事务控制表，事务控制表的每条记录关联一个分支事务，那我们完全可以在这张事务控制表上加一个状态字段，用来记录每个分支事务的执行状态。
//        if (random.nextBoolean()) {
//            return new TwoPhaseResult(false, "this is a mock Exception");
//        }
//        Long orderId = (Long) actionContext.getActionContext("orderId");
//        OrderEntity order = new OrderEntity();
//        order.setId(orderId);
//        order.setStatus(2);
//        orderMapper.updateById(order);
//        log.info("Order Service commit End ... Created " + order);
//        return new TwoPhaseResult(true, "订单提交成功");
//    }
//
//    @Override
//    @Transactional
//    public TwoPhaseResult rollback(BusinessActionContext actionContext) {
//        log.info("Order Service commit Begin ... xid: " + RootContext.getXID());
//        // TODO 未实现 空回滚
//        // TODO 解决方法：需要一张额外的事务控制表，其中有分布式事务 ID 和分支事务 ID，第一阶段 Try 方法里会插入一条记录，表示一阶段执行了。Cancel 接口里读取该记录，如果该记录存在，则正常回滚；如果该记录不存在，则是空回滚。
//        // TODO 未实现 幂等
//        if (random.nextBoolean()) {
//            return new TwoPhaseResult(false, "this is a mock Exception");
//        }
//        Long orderId = (Long) actionContext.getActionContext("orderId");
//        OrderEntity order = new OrderEntity();
//        order.setId(orderId);
//        order.setStatus(3);
//        orderMapper.updateById(order);
//        log.info("Order Service commit End ... Created " + order);
//        return new TwoPhaseResult(true, "订单回滚成功");
//    }
//}
