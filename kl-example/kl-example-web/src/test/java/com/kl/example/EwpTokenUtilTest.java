//package com.ewp.examples.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import com.ewp.core.common.util.DateHelper;
//import com.ewp.core.common.util.EwpTokenUtil;
//import org.apache.commons.lang3.RandomUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.junit.Test;
//
//import java.util.Date;
//import java.util.LinkedList;
//import java.util.Objects;
//
///**
// * @author : yuezhenyu
// * @className : EwpTokenUtilTest
// * @since : 2021/09/22 下午1:59
// */
//
//public class EwpTokenUtilTest {
//
//    /**
//     * 测试创建token
//     */
//    @Test
//    public void testCreateJwtToken() {
//        EwpTokenUtil.initJwtConfig(EwpTokenUtil.SECRET);
//        String customerId = "123456";
//        String token = EwpTokenUtil.create(customerId, 1, new Date(), EwpTokenUtilTest.getVaildityTime());
//        System.out.println(token);
//    }
//
//    /**
//     * 测试刷新token
//     */
//    @Test
//    public void testRefreshJwtToken() {
//        EwpTokenUtil.initJwtConfig(EwpTokenUtil.SECRET);
//        String customerId = "123456";
////        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTYiLCJpc3MiOiJld3AiLCJwYXJ0bmVyX2NvZGUiOjEsImV4cCI6MTYzMjI5NjM0MiwiaWF0IjoxNjMyMjk2MjgyfQ.0cbEpV8pbh5Bl9Ba8nozTi4T4QdGrHsMlJLVn9kAHHc";
//        String token = EwpTokenUtil.create(customerId, 1, new Date(), 20);
//        DecodedJWT decodedJWT = EwpTokenUtil.decode(token);
//        System.out.println(decodedJWT.getExpiresAt());
//        token = EwpTokenUtil.getRefreshToken(token, 60 * 24 * 3, 0);
//        decodedJWT = EwpTokenUtil.decode(token);
//        System.out.println(decodedJWT.getExpiresAt());
//    }
//
//    /**
//     * 验证token
//     */
//    @Test
//    public void testVerifyJwtToken() {
//        EwpTokenUtil.initJwtConfig(EwpTokenUtil.SECRET);
//        String token = EwpTokenUtil.create("11111", 1, new Date(), EwpTokenUtilTest.getVaildityTime());
//        EwpTokenUtil.initJwtConfig(EwpTokenUtil.SECRET + "11");
//        EwpTokenUtil.verify(token);
//    }
//
//    @Test
//    public void testJwtToken() {
//        LinkedList<String> tokenList = new LinkedList<>();
//        String customerId = "123456";
//        Date now = new Date();
//        String token = EwpTokenUtil.create(customerId, 1, new Date(), EwpTokenUtilTest.getVaildityTime());
//        tokenList.add(token);
//        System.out.println("初始化用户123456，创建token：");
//        DecodedJWT jwt = EwpTokenUtil.decode(token);
//        System.out.println(JSON.toJSONString(jwt));
//        System.out.println("exp:" + DateHelper.dateTimeStr(jwt.getExpiresAt()));
//        System.out.println("iat:" + DateHelper.dateTimeStr(jwt.getIssuedAt()));
//
//        while (true) {
//            Date temp = new Date();
//            System.out.println("当前时间：" + DateHelper.dateTimeStr(temp));
//            System.out.println("解析token：" + tokenList.getLast());
//            Long customer = EwpTokenUtil.decodeCustomerId(tokenList.getLast());
//            System.out.println("解析结果：" + customer);
//            if (Objects.isNull(customer)) {
//                String newtoken = EwpTokenUtil.create(customerId, 1, temp, EwpTokenUtilTest.getVaildityTime());
//                tokenList.add(newtoken);
//            }
//            System.out.println("尝试刷新token：" + tokenList.getLast());
//            String refreshToken = EwpTokenUtil.getRefreshToken(tokenList.getLast());
//            System.out.println("刷新结果：" + refreshToken);
//            if (StringUtils.isNotBlank(refreshToken)) {
//                DecodedJWT decodedJwt = EwpTokenUtil.decode(refreshToken);
//                System.out.println("刷新结果:" + JSON.toJSONString(decodedJwt));
//                System.out.println("refreshToken-exp:" + DateHelper.dateTimeStr(decodedJwt.getExpiresAt()));
//                System.out.println("refreshToken-iat:" + DateHelper.dateTimeStr(decodedJwt.getIssuedAt()));
//            }
//
//
//            try {
//                Thread.sleep(1000 * 10);
//            } catch (InterruptedException interruptedException) {
//                interruptedException.printStackTrace();
//            }
//
//        }
//
//    }
//
//    /**
//     * 获取一个失效的分钟数
//     * 保证该用户token在三天之后失效
//     *
//     * @return
//     */
//    public static int getVaildityTime() {
//        int diff = RandomUtils.nextInt(60, 120);
//        return EwpTokenUtil.TOKEN_VAILDITY_TIME + diff;
//    }
//}