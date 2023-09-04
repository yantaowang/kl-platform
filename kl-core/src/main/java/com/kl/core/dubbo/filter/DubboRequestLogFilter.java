package com.kl.core.dubbo.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.util.Arrays;

/**
 * @ClassName RequestLogFilter
 * @Description 请求相应日志
 * @Author yzy
 * @Date 2021/8/28 5:16 下午
 * @Version 1.0
 */
@Slf4j
@Activate(group = org.apache.dubbo.common.constants.CommonConstants.PROVIDER, order = 3)
public class DubboRequestLogFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        long start = System.currentTimeMillis();
        String dubboFrom = RpcContext.getContext().getRemoteApplicationName();
        String argumentsStr = Arrays.toString(invocation.getArguments());
        if (argumentsStr.length() > CommonConstants.MAX_LOG_BODY_LENGTH) {
            argumentsStr = THE_LOG_DATA_IS_TOO_LONG;
        }
        log.info("[DUBBO_LOG] [DUBBO_REQUEST] DubboFrom:[{}] partnerCode:[{}] Interface:[{}], MethodName:[{}], Arguments:[{}] ", dubboFrom, EwpThreadLocal.getPartnerCode(), invoker.getInterface(), invocation.getMethodName(), argumentsStr);
        //上报dubbo响应时间
        Result result = MonitorUtil.timer(CommonMonitorEnum.BASE_DUBBO_REQUEST_TIMER, () -> invoker.invoke(invocation),"resource",DubboUtil.getMethodResourceName(invoker, invocation, false));
        try {
            long elapsed = System.currentTimeMillis() - start;
            if (invoker.getUrl() != null && invoker.getUrl().getPath() != null
                    && invoker.getUrl().getPath().startsWith(CommonConstants.BASE_PACKAGE_NAME)) {
                String resultJson = "";
                if (result.getValue() != null) {
                    resultJson = JSON.toJSONString(result.getValue());
                    if (resultJson.length() > CommonConstants.MAX_LOG_BODY_LENGTH) {
                        resultJson = THE_LOG_DATA_IS_TOO_LONG;
                    }
                }

                if (result.getValue() != null && resultJson.length() < TracIdUtil.CONTENTSIZE) {
                    log.info("[DUBBO_LOG] [DUBBO_RESPONSE] Interface:[{}], MethodName:[{}], response result:{}, expt:[{}], takeTime:[{}]   ", invoker.getInterface(), invocation.getMethodName(),
                            resultJson,
                            result.getException(), elapsed);
                } else {
                    log.info("[DUBBO_LOG] [DUBBO_RESPONSE] Interface:[{}], MethodName:[{}], expt:[{}], takeTime:[{}]   ", invoker.getInterface(), invocation.getMethodName(),
                            result.getException(), elapsed);
                }
            }
        } catch (Exception e) {
            log.error("[DUBBO_LOG] RequestLogFilter error", e);
            return result;
        }finally {
            try {
                MonitorUtil.requestBucket(InterfaceTypeEnum.RPC, start);
                String code;
                if (result == null || result.getValue() == null || !(result.getValue() instanceof EwpResponse)) {
                    code = "unknow";
                } else {
                    code = ((EwpResponse) result.getValue()).getCode() + "";
                }
                //上报dubbo调用次数和返回code码
                MonitorUtil.reportCount(CommonMonitorEnum.APP_REQUEST_COUNT, "resource", DubboUtil.getMethodResourceName(invoker, invocation, false), "type", InterfaceTypeEnum.RPC.getType(), "code", code);
            }catch (Throwable t){
                log.warn("上报埋点异常",t);
            }
        }

        return result;
    }


}
