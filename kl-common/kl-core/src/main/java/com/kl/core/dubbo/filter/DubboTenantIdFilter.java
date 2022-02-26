package com.kl.core.dubbo.filter;

import com.kl.core.thread.KlThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

@Activate(group = {CommonConstants.CONSUMER, CommonConstants.PROVIDER}, order = 2)
public class DubboTenantIdFilter implements Filter {


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
        String partnerId = RpcContext.getContext().getAttachment(com.kl.core.constants.CommonConstants.TENANT_ID);
        if (!StringUtils.isBlank(partnerId)) {
            KlThreadLocal.setTenantId(Integer.parseInt(partnerId));
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
        Integer tenantId = KlThreadLocal.getTenantId();
        if (tenantId != null) {
            RpcContext.getContext().setAttachment(com.kl.core.constants.CommonConstants.TENANT_ID, String.valueOf(tenantId));
        }
        return invoker.invoke(invocation);
    }

}
