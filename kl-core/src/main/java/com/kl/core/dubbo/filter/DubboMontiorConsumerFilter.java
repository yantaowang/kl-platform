package com.kl.core.dubbo.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * dubbo调用埋点
 */
@Slf4j
@Activate(group = CommonConstants.CONSUMER, order = 1)
public class DubboMontiorConsumerFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String target = RpcContext.getContext().getRemoteApplicationName();
        try {
            //上报调用时间
            Result result = MonitorUtil.timer(CommonMonitorEnum.CONSUMER_DUBBO_REQUEST_TIMER,
                    () -> invoker.invoke(invocation),
                    "resource", DubboUtil.getMethodResourceName(invoker, invocation, false),
            "origin",target
            );
            if (result != null && result.hasException() && result.getException() != null) {//上报异常
                MonitorUtil.reportCount(CommonMonitorEnum.CONSUMER_DUBBO_REQUEST_COUNT,
                        "exception", result.getException().getClass().getSimpleName(),
                        "type", InterfaceTypeEnum.RPC.getType(),
                        "resource", DubboUtil.getMethodResourceName(invoker, invocation, false),
                        "origin",target);
            }
            return result;
        }catch (Throwable t){
            //上报异常
            MonitorUtil.reportCount(CommonMonitorEnum.CONSUMER_DUBBO_REQUEST_COUNT,
                    "exception", t.getClass().getSimpleName(),
                    "type", InterfaceTypeEnum.RPC.getType(),
                    "resource", DubboUtil.getMethodResourceName(invoker, invocation, false),
                    "origin",target);
            throw t;
        }
    }
}

