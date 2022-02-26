package com.kl.core.dubbo.filter;


import com.alibaba.fastjson.JSON;
import com.kl.core.constants.CommonConstants;
import com.kl.core.thread.KlThreadLocal;
import com.kl.core.util.TracIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.util.Arrays;

import static com.kl.core.constants.CommonConstants.THE_LOG_DATA_IS_TOO_LONG;

/**
 * @ClassName RequestLogFilter
 * @Description 请求相应日志
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
        log.info("[DUBBO_LOG] [DUBBO_REQUEST] DubboFrom:[{}] partnerCode:[{}] Interface:[{}], MethodName:[{}], Arguments:[{}] ", dubboFrom, KlThreadLocal.getTenantId(), invoker.getInterface(), invocation.getMethodName(), argumentsStr);
        Result result = invoker.invoke(invocation);
        try {
            long elapsed = System.currentTimeMillis() - start;
            if (invoker.getUrl() != null && invoker.getUrl().getPath() != null
                    && invoker.getUrl().getPath().startsWith(CommonConstants.BasePackageName)) {
                String resultJson = "";
                if (result.getValue() != null) {
                    resultJson = JSON.toJSONString(result.getValue());
                    if (resultJson.length() > CommonConstants.MAX_LOG_BODY_LENGTH) {
                        resultJson = THE_LOG_DATA_IS_TOO_LONG;
                    }
                }

                if (result.getValue() != null && resultJson.length() < TracIdUtil.CONTENTSIZE) {
                    log.info("[DUBBO_LOG] [DUBBO_RESPONSE] Interface:[{}], MethodName:[{}], response result:{}, Exception:[{}], takeTime:[{}]   ", invoker.getInterface(), invocation.getMethodName(),
                            resultJson,
                            result.getException(), elapsed);
                } else {
                    log.info("[DUBBO_LOG] [DUBBO_RESPONSE] Interface:[{}], MethodName:[{}], Exception:[{}], takeTime:[{}]   ", invoker.getInterface(), invocation.getMethodName(),
                            result.getException(), elapsed);
                }
            }
        } catch (Exception e) {
            log.error("[DUBBO_LOG] RequestLogFilter error", e);
            return result;
        }

        return result;
    }
}
