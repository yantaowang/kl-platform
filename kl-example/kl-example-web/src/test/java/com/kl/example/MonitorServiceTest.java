//package com.ewp.examples.service.impl;
//
//import com.ewp.core.common.thread.EwpThreadLocal;
//import com.ewp.examples.api.service.MonitorService;
//import com.ewp.examples.service.EwpExamplesServiceApplication;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
///**
// * @author : yuezhenyu
// * @className : MonitorServiceTest
// * @since : 2021/09/15 下午6:11
// */
//@Slf4j
//@SpringBootTest(classes = EwpExamplesServiceApplication.class)
//public class MonitorServiceTest {
//
//    @Autowired
//    private MonitorService monitorService;
//
//    @Test
//    public void testService() {
//        EwpThreadLocal.tempAssignPartner(44422, () -> monitorService.testService(114L));
//    }
//}