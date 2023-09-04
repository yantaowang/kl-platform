package com.kl.monitor.starter.utils;//package com.ewp.starter.monitor.utils;
//
//
//import com.ewp.core.common.util.TracIdUtil;
//
//public abstract class MonitorRunnable implements Runnable {
//    @Override
//    public void run() {
//        try{
//            work();
//        }catch (Throwable t){
//            MonitorUtil.reportCount(CommonMonitorEnum.APP_REQUEST_EXCEPTION,"type","3","exception",t.getClass().getSimpleName());
//            throw t;
//        }finally {
//            MonitorUtil.reportCount(CommonMonitorEnum.APP_REQUEST_COUNT,"type","3");
//        }
//    }
//
//    protected abstract void work();
//}
