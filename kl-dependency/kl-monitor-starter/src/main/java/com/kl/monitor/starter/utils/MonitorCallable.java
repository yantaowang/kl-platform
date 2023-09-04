package com.kl.monitor.starter.utils;//package com.ewp.starter.monitor.utils;
//
//import java.util.concurrent.Callable;
//
//public abstract class MonitorCallable<V> implements Callable<V> {
//    @Override
//    public V call() {
//
//        try{
//            return work();
//        }catch (Throwable t){
//            MonitorUtil.reportCount(CommonMonitorEnum.APP_REQUEST_EXCEPTION,"type","3","exception",t.getClass().getSimpleName());
//            throw  t;
//        }finally {
//            MonitorUtil.reportCount(CommonMonitorEnum.APP_REQUEST_COUNT,"type","3");
//        }
//    }
//
//    protected abstract V work();
//}
