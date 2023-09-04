package com.kl.core.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class JsonUtils {
    private static final ConcurrentHashMap<String, ObjectMapper> map = new ConcurrentHashMap();

    /**
     * 对象 -> Json
     *
     * @param obj
     * @return
     */
    public static String objectToJson(Object obj) {
        return objectToJson(obj, DateFormatStr.NORMAL);
    }

    /**
     * 对象 -> Json
     *
     * @param obj
     * @param dateFormat 日期格式化，默认yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String objectToJson(Object obj, DateFormatStr dateFormat) {
        String jsonStr = "";
        if (null != obj) {
            try {
                return getObjectMapper(dateFormat).writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                log.error("obj=" + obj + ", dateFormat=" + dateFormat.getDateFormat(), e);
                return jsonStr;
            }
        }
        return jsonStr;
    }

    /**
     * Json -> 对象
     *
     * @param json
     * @param classType  类型
     * @return
     */
    public static <T> T jsonToObject(String json, Class<T> classType) {
        return jsonToObject(json, classType, DateFormatStr.NORMAL);
    }

    /**
     * Json -> 对象
     *
     * @param json
     * @param classType  类型
     * @param dateFormat 日期格式化，默认yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static <T> T jsonToObject(String json, Class<T> classType, DateFormatStr dateFormat) {
        try {
            return getObjectMapper(dateFormat).readValue(json, classType);
        } catch (IOException e) {
            log.error("json=" + json + ",classType=" + classType + ", dateFormat=" + dateFormat.getDateFormat(), e);
        }
        return null;
    }

    public static <T> List<T> jsonToList(String json, Class classType, DateFormatStr dateFormat) {
        try {
            JavaType javaType =
                    getObjectMapper(dateFormat).getTypeFactory().constructParametricType(ArrayList.class, classType);
            return getObjectMapper().readValue(json, javaType);
        } catch (IOException e) {
            log.error("json=" + json + ",classType=" + classType + ", dateFormat=" + dateFormat.getDateFormat(), e);
        }
        return null;
    }

    public static <K, V> Map<K, V> jsonToMap(String json, Class<K> keyClassType, Class<V> valueClassType,
                                             DateFormatStr dateFormat) {
        try {
            JavaType javaType = getObjectMapper(dateFormat).getTypeFactory().constructParametricType(HashMap.class, keyClassType, valueClassType);
            return getObjectMapper().readValue(json, javaType);
        } catch (IOException e) {
            log.error("json=" + json + ",keyClassType=" + keyClassType + ", valueClassType=" + valueClassType
                    + ", dateFormat=" + dateFormat.getDateFormat(), e);
        }
        return null;
    }

    /**
     * 获取默认 ObjectMapper
     * @return
     */
    private static ObjectMapper getObjectMapper() {
        DateFormatStr dateFormat = DateFormatStr.NORMAL;
        return getObjectMapper(dateFormat);
    }

    /**
     * 根据 dateFormat 获取 ObjectMapper
     * @param dateFormat
     * @return
     */
    private static ObjectMapper getObjectMapper(DateFormatStr dateFormat) {
        ObjectMapper objectMapper = map.get(dateFormat.name());
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            // 序列化非空属性
//            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateFormat.getDateFormat())));
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(dateFormat.getDateFormat())));
            objectMapper.registerModule(javaTimeModule);
            DateFormat DATE_FORMAT = new SimpleDateFormat(dateFormat.getDateFormat());
            objectMapper.setDateFormat(DATE_FORMAT);
            map.putIfAbsent(dateFormat.name(), objectMapper);
        }
        return objectMapper;
    }

    /**
     * jackson日期转换枚举
     */
    public static enum DateFormatStr{
        NORMAL("yyyy-MM-dd HH:mm:ss"),NORMAL_MILLISECOND("yyyy-MM-dd HH:mm:ss.SSS");
        String dateFormat;

        DateFormatStr(String dateFormat) {
            this.dateFormat = dateFormat;
        }

        public String getDateFormat() {
            return dateFormat;
        }
    }
    /**
     * 判断字符串是否可以转化为json对象
     * @param content
     * @return
     */
    public static boolean isJsonObject(String content) {
        if (StringUtils.isEmpty(content)){
            return false;
        }
        if(content.trim().startsWith("{")){
            return true;
        }
        return false;

    }

    /**
     * 判断字符串是否可以转化为json数组对象
     * @param content
     * @return
     */
    public static boolean isJsonArray(String content) {
        if (StringUtils.isEmpty(content)){
            return false;
        }
        if((content.trim().startsWith("["))){
            return true;
        }
        return false;
    }

}
