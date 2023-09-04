package com.kl.core.util;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.kl.core.enums.ResponseCode;
import com.kl.core.exception.KlException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;

import static com.kl.core.constants.CommonConstants.JWTModelKeyHeader.SIGNATURE;

@Slf4j
public class SignUtil {

    /**
     * 干扰编码
     */
    private static final String NOISE = "1fe18d439ffb1548ae29e6e9f3c995739278";

    /**
     * 生成签名 签名规则如下： str=请求参数进行顺序排列再进行拼接 sign=md5(base64(timestamp)+token+私密+param+body)
     * @param bodyContent HTTP请求bodyContent
     */
    public static void signatureGenAndCheck(Map<String, String> queryParam, String bodyContent, String timestamp, String token, String signature, boolean isGetReq, String url) {
        boolean isCheckOk = genSignature(queryParam, bodyContent, timestamp, token, signature, (signBefore, signGen) -> {
            log.error("[checkSignError]签名错误(URI:{},timestamp:{}, token:{}, 请求签名:{},加密之前字符串:{}, 签名结果:{})", url, timestamp, token, signature,
                    signBefore, signGen);
        });
        if (!isCheckOk) {
            throw new KlException(ResponseCode.SIGNATURE_ERROR.getCode(), ResponseCode.SIGNATURE_ERROR.getDescription());
        }
    }

    /**
     * 处理签名
     *
     * @param queryParam
     * @param bodyContent
     * @param timestamp
     * @param token
     * @param reqSignature
     * @param errCallback
     * @return
     */
    private static boolean genSignature(Map<String, String> queryParam, String bodyContent, String timestamp, String token, String reqSignature,
                                        BiConsumer<String, String> errCallback) {
        StringBuilder sb = new StringBuilder();

        final Base64.Encoder encode = Base64.getEncoder();
        sb.append(encode.encodeToString(timestamp.getBytes(StandardCharsets.UTF_8)));
        if (StringUtils.isNotBlank(token)) {
            sb.append(token);
        }
        sb.append(NOISE);
        appendQuery(sb, queryParam);
        appendBody(sb, bodyContent);
        // 处理特殊字符
        String md5Signature = sb.toString().replace("[", "").replace("]", "").replace("\"", "");
        /// log.info("=========待签名串========" + md5Signature);
        // 计算并返回签名
        String md5Gen = EncryptHelper.toMD5(md5Signature);
        if (!StringUtils.equalsIgnoreCase(reqSignature, md5Gen)) {
            errCallback.accept(md5Signature, md5Gen);
            return false;
        }
        return true;
    }

    /**
     * 处理map参数
     *
     * @param sb
     * @param queryParam url参数
     * @return
     */
    private static StringBuilder appendQuery(StringBuilder sb, Map<String, String> queryParam) {
        if (CollectionUtil.isEmpty(queryParam)) {
            return sb;
        }
        List<String> queryParamNameList = new ArrayList<>();
        // 进行名称排序
        queryParamNameList.addAll(queryParam.keySet());
        Collections.sort(queryParamNameList);
        // 组装签名字符串
        for (String name : queryParamNameList) {
            if (!SIGNATURE.equals(name)) {
                sb.append(name);
                sb.append(queryParam.get(name));
            }
        }
        return sb;
    }

    /**
     * 处理map参数
     *
     * @param sb
     * @param bodyContent body参数
     * @return
     */
    private static StringBuilder appendBody(StringBuilder sb, String bodyContent) {
        if (StringUtils.isEmpty(bodyContent)) {
            return sb;
        }
        // 初始化变量
        List<String> nameList = null;
        JSONObject jsonObject = JSONObject.parseObject(bodyContent);
        if (jsonObject != null && !jsonObject.isEmpty()) {
            nameList = new ArrayList<>(jsonObject.keySet());
            Collections.sort(nameList);
            // 组装签名字符串
            for (String name : nameList) {
                if (!SIGNATURE.equals(name)) {
                    sb.append(name);
                    sb.append(jsonObject.get(name));
                }
            }
        }
        return sb;
    }

    private static boolean genSignatureForPost(String bodyContent, String timestamp, String token, String reqSignature,
                                               BiConsumer<String, String> errCallback) {
        // 初始化变量
        List<String> nameList = null;
        StringBuilder sb = new StringBuilder();

        final Base64.Encoder encode = Base64.getEncoder();
        sb.append(encode.encodeToString(timestamp.getBytes(StandardCharsets.UTF_8)));
        if (StringUtils.isNotBlank(token)) {
            sb.append(token);
        }
        sb.append(NOISE);
        JSONObject jsonObject = JSONObject.parseObject(bodyContent);
        if (jsonObject != null && !jsonObject.isEmpty()) {
            nameList = new ArrayList<>(jsonObject.keySet());
            Collections.sort(nameList);
            // 组装签名字符串
            for (String name : nameList) {
                if (!SIGNATURE.equals(name)) {
                    sb.append(name);
                    sb.append(jsonObject.get(name));
                }
            }
        }
        // 处理特殊字符
        String md5Signature = sb.toString().replace("[", "").replace("]", "").replace("\"", "");
        /// log.info("=========待签名串========" + md5Signature);
        // 计算并返回签名
        String md5Gen = EncryptHelper.toMD5(md5Signature);
        if (!StringUtils.equalsIgnoreCase(reqSignature, md5Gen)) {
            errCallback.accept(md5Signature, md5Gen);
            return false;
        }
        return true;
    }

    /**
     * 生成签名 签名规则如下： str=请求参数进行顺序排列再进行拼接 sign=md5(base64(timestamp)+token+私密+str)
     */
    private static boolean genSignatureForGet(Map<String, String> map, String timestamp, String token, String reqSignature,
                                              BiConsumer<String, String> errCallback) {
        // 初始化变量
        StringBuilder sb = new StringBuilder();

        final Base64.Encoder encode = Base64.getEncoder();
        sb.append(encode.encodeToString(timestamp.getBytes(StandardCharsets.UTF_8)));
        if (StringUtils.isNotBlank(token)) {
            sb.append(token);
        }
        sb.append(NOISE);

        List<String> nameList = new ArrayList<>();
        // 进行名称排序
        nameList.addAll(map.keySet());
        Collections.sort(nameList);
        // 组装签名字符串
        for (String name : nameList) {
            if (!SIGNATURE.equals(name)) {
                sb.append(name);
                sb.append(map.get(name));
            }
        }

        // 处理特殊字符
        String md5Signature = sb.toString().replace("[", "").replace("]", "").replace("\"", "");
        /// log.info("=========待签名串========" + md5Signature);
        // 计算并返回签名
        String md5Gen = EncryptHelper.toMD5(md5Signature);
        if (!StringUtils.equalsIgnoreCase(reqSignature, md5Gen)) {
            errCallback.accept(md5Signature, md5Gen);
            return false;
        }
        return true;
    }


}

