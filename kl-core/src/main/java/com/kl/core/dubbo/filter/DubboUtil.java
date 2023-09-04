package com.kl.core.dubbo.filter;

import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;

/**
 * dubbo相关工具类
 */
public class DubboUtil {

    public static String getMethodResourceName(Invoker<?> invoker, Invocation invocation, Boolean useGroupAndVersion) {
        try {
            StringBuilder buf = new StringBuilder(64);
            String interfaceResource = useGroupAndVersion ? invoker.getUrl().getColonSeparatedKey() : invoker.getInterface().getName();
            buf.append(interfaceResource)
                    .append(":")
                    .append(invocation.getMethodName())
                    .append("(");
            boolean isFirst = true;
            for (Class<?> clazz : invocation.getParameterTypes()) {
                if (!isFirst) {
                    buf.append(",");
                }
                buf.append(clazz.getSimpleName());
                isFirst = false;
            }
            buf.append(")");
            return buf.toString();
        }catch (Throwable t){
            return "unknow";
        }
    }
}
