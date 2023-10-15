package com.kl.core.dubbo.filter;

import com.kl.common.util.TracIdUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.MDC;


/**
 * @ClassName DubboPartnerIdFilter
 * @Description dubbo traceId 过滤处理
 * @Version 1.0
 */
@Activate(group = {CommonConstants.CONSUMER, CommonConstants.PROVIDER}, order = 1)
public class DubboTraceIdFilter implements Filter {

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
        String traceId = RpcContext.getContext().getAttachment(TracIdUtil.LOGGER_ID_PARAM_NAME);
        if (StringUtils.isBlank(traceId)) {
            traceId = TracIdUtil.creaTracId();

        }

        MDC.put(TracIdUtil.LOGGER_ID_PARAM_NAME, traceId);
        try {
            return invoker.invoke(invocation);
        } finally {
            // 调用完成后移除MDC属性
            MDC.remove(TracIdUtil.LOGGER_ID_PARAM_NAME);
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
        String traceId = MDC.get(TracIdUtil.LOGGER_ID_PARAM_NAME);
        if (!StringUtils.isEmpty(traceId)) {
            RpcContext.getContext().setAttachment(TracIdUtil.LOGGER_ID_PARAM_NAME, traceId);
        }
        return invoker.invoke(invocation);
    }

}
