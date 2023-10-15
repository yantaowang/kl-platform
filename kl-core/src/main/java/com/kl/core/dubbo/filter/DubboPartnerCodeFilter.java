package com.kl.core.dubbo.filter;
import com.kl.common.thread.KlThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;


/**
 * @ClassName DubboPartnerIdFilter
 * @Description 合作方id过滤处理
 * @Author 岳振宇
 * @Date 2020/11/12 7:05 下午
 * @Version 1.0
 */
@Activate(group = {CommonConstants.CONSUMER, CommonConstants.PROVIDER}, order = 2)
public class DubboPartnerCodeFilter implements Filter {


    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (RpcContext.getContext().isConsumerSide()) {
            // Consumer
            return doConsumerFilter(invoker, invocation);
        }
        if (RpcContext.getContext().isProviderSide()) {
            // Provider
            return doProviderFilter(invoker, invocation);
        }
        return invoker.invoke(invocation);
    }

    /**
     * 服务提供方过滤
     *
     * @param invoker
     * @param invocation
     * @return
     */
    public Result doProviderFilter(Invoker<?> invoker, Invocation invocation) {
        KlThreadLocal.remove();
        String partnerId = RpcContext.getContext().getAttachment(com.kl.common.constants.CommonConstants.PARTNER_CODE);
        if (!StringUtils.isBlank(partnerId)) {
            KlThreadLocal.setPartnerCode(Integer.parseInt(partnerId));
        }
        try {
            return invoker.invoke(invocation);
        } finally {
            // 调用完成后移除线程变量属性
            KlThreadLocal.remove();
        }
    }

    /**
     * 服务消费方过滤
     *
     * @param invoker
     * @param invocation
     * @return
     */
    public Result doConsumerFilter(Invoker<?> invoker, Invocation invocation) {
        Integer partnerId = KlThreadLocal.getPartnerCode();
        if (partnerId != null) {
            RpcContext.getContext().setAttachment(com.kl.common.constants.CommonConstants.PARTNER_CODE, String.valueOf(partnerId));
        }
        return invoker.invoke(invocation);
    }

}
