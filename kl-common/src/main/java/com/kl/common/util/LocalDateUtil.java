package com.kl.common.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @ClassName LocalDateUtil
 * @Author yzy
 * @Date 2021/9/18 5:22 下午
 * @Version 1.0
 */
public class LocalDateUtil {
    public static DateTimeFormatter NORMAL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter YMinute = DateTimeFormatter.ofPattern("HHmm");
    public static DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final ZoneOffset zoneOffset = ZoneOffset.of("+8");

    public static String dateToYMinute(LocalDateTime localDateTime) {
        return YMinute.format(localDateTime);
    }

    /**
     * 获取指定时间时间戳 单位s
     *
     * @param localDateTime
     * @return
     */
    public static long LocalDateTimeToSecond(LocalDateTime localDateTime) {
        return localDateTime.toEpochSecond(zoneOffset);
    }

    /**
     * 获取指定时间时间戳 单位ms
     *
     * @param localDateTime
     * @return
     */
    public static long LocalDateTimeToMiniSecond(LocalDateTime localDateTime) {
        return localDateTime.toInstant(zoneOffset).toEpochMilli();
    }

    /**
     * 毫秒时间戳转LocalDateTime
     *
     * @param miniSecond
     * @return
     */
    public static LocalDateTime miniSecondToLocalDateTime(long miniSecond) {
        return Instant.ofEpochMilli(miniSecond).atZone(zoneOffset).toLocalDateTime();
    }

    /**
     * 获取当天开始时间LocalDateTime
     *
     * @return
     */
    public static LocalDateTime startDayToLocalDateTime() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    }

    /**
     * 获取当天结束时间LocalDateTime
     *
     * @return
     */
    public static LocalDateTime endDayToLocalDateTime() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
    }

    /**
     * 获取指定时间(毫秒时间戳)的日期
     *
     * @return
     */
    public static LocalDate startOneDayToLocalDate(long miniSecond) {
        return miniSecondToLocalDateTime(miniSecond).toLocalDate();
    }

    /**
     * 获取指定时间(毫秒时间戳)的当天开始时间LocalDateTime
     *
     * @return
     */
    public static LocalDateTime startOneDayToLocalDateTime(long miniSecond) {
        return miniSecondToLocalDateTime(miniSecond).toLocalDate().atStartOfDay();
    }

    /**
     * 获取指定时间(字符串)的LocalDateTime
     *
     * @return
     */
    public static LocalDateTime strToLocalDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, NORMAL);
    }

    /**
     * LocalDateTime 转 yyyy-MM-dd HH:mm:ss 字符串
     *
     * @param dateTime
     * @return
     */
    public static String dateToStr(LocalDateTime dateTime) {
        return NORMAL.format(dateTime);
    }

    /**
     * LocalDateTime 转 date
     *
     * @param localDateTime
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * date 转 LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * LocalDate 转 date
     *
     * @param localDate
     * @return
     */
    public static Date localDateToDate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * date 转 LocalDate
     *
     * @param date
     * @return
     */
    public static LocalDate dateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalDate();
    }

}
