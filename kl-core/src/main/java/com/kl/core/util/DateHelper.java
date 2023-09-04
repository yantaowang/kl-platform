package com.kl.core.util;


import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 日期工具类
 */
public class DateHelper extends org.apache.commons.lang3.time.DateUtils {

    /**
     * 常用日期 格式 pattern
     */
    private static String[] DEFAULT_DATE_FORMAT_PATTERNS = new String[] {"yyyy-MM-dd", "yyyy/MM/dd", "yyyyMMdd",
            "yyyy年MM月dd日", "yyyy.MM.dd", "yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss", "yyyyMMdd HH:mm:ss"};

    /**
     * 转换日期格式字符串 (yyyy-MM-dd)
     *
     * @param obj
     * @return
     */
    public static String dateStr(Object obj) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(obj);
    }

    public static String nowTime() {
        return dateTimeStr(new Date());
    }

    /**
     * 转换日期格式字符串 (yyyyMMdd)
     *
     * @param obj
     * @return
     */
    public static String dateStrYMD(Object obj) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(obj);
    }

    /**
     * 转换日期格式字符串 (yyyy年MM月dd日 HH:mm)
     *
     * @param obj
     * @return
     */
    public static String dateStrYMDSF(Object obj) {
        DateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        return df.format(obj);
    }

    /**
     * 转换日期格式字符串 (yyyy年MM月dd日 HH:mm)
     *
     * @param obj
     * @return
     */
    public static String dateStrPay(Object obj) {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(obj);
    }

    /**
     * 转换日期格式字符串 (yyyy-MM-dd HH:mm:ss)
     *
     * @param obj
     * @return
     */
    public static String dateTimeStr(Object obj) {
        if (null == obj) {
            return "";
        }

        if ("0000-00-00 00:00:00".equals(obj.toString())) {
            return "";
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(obj);
    }

    /**
     * 转换日期格式字符串
     *
     * @param obj
     * @param pattern
     * @return
     */
    public static String dateStr(Object obj, String pattern) {
        if (null == obj) {
            return "";
        }
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(obj);
    }

    /**
     * 将字符串转换成java.util.Date (yyyyMMdd)
     *
     * @param str (yyyyMMdd)
     * @return
     */
    public static Date strToDateYMD(String str) {
        Date date = null;
        DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    /**
     * 将字符串转换成java.util.Date (yyyyMMddHHmmss)
     *
     * @param str (yyyyMMddHHmmss)
     * @return
     */
    public static Date strToDateYMDHMS(String str) {
        Date date = null;
        DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static boolean compare(Date time1, Date time2) {
        // Date类的一个方法，如果a早于b返回true，否则返回false  
        if (time1.before(time2))
            return true;
        else
            return false;
    }

    public static Date getMinTime(Date dt) {
        Date dt1 = null;
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
            dt1 = sf.parse(sf.format(dt));
        } catch (ParseException e) {
            e.printStackTrace();
//            System.out.println("date formate error ：" + dt + ".   " + e.getMessage());
        }
        return dt1;
    }

    /**
     * 计算两个日期相差的天数
     *
     * @param beforeDate
     * @param afterDate
     * @return
     */
    public static long getDistinceDay(Date beforeDate, Date afterDate) {
        long dayCount = 0;
        dayCount = (afterDate.getTime() - beforeDate.getTime()) / (24 * 60 * 60 * 1000);
        return dayCount;
    }

    /**
     * 计算两个日期相差的秒数
     *
     * @param beforeDate
     * @param afterDate
     * @return
     */
    public static long getDistinceSecond(Date beforeDate, Date afterDate) {
        long dayCount = 0;
        dayCount = (afterDate.getTime() - beforeDate.getTime()) / (1000);
        return dayCount;
    }

    /**
     * 计算两个日期相差的年数
     *
     * @param beforeDate
     * @param afterDate
     * @return
     */
    public static int getDistinceYear(Date beforeDate, Date afterDate) {
        try {
            String beforeYear = DateFormatUtils.format(beforeDate, "yyyy");
            String afterYear = DateFormatUtils.format(afterDate, "yyyy");
            if (StringUtils.isNotBlank(beforeYear) && StringUtils.isNotBlank(afterYear)) {
                return Math.abs(Integer.parseInt(afterYear) - Integer.parseInt(beforeYear));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 当天到第二天12点的秒数
     *
     * @return
     */
    public static int exipireTime() {
        Calendar curDate = Calendar.getInstance();
        Calendar tommorowDate = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH),
                curDate.get(Calendar.DATE) + 1, 0, 0, 0);
        long timeCap = tommorowDate.getTimeInMillis() - System.currentTimeMillis();
        return (int)timeCap / 1000;
    }

    /**
     * 返回当前时间前指定年数列表
     *
     * @param count
     * @return
     * @author
     */
    public static List<Integer> getBeforeYear(int count) {
        Calendar curDate = Calendar.getInstance();
        int nowYear = curDate.get(Calendar.YEAR);
        List<Integer> yearList = new ArrayList<Integer>();
        for (int i = nowYear; i > (nowYear - count); i--) {
            yearList.add(i);
        }
        return yearList;
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @param dt
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static int checkTimeDis(Date begin, Date end, int distance) {
        if (begin == null) {
            // 时间未选择
            return -1;
        }

        if (end == null) {
            end = new Date();
        }

        if (getDistinceDay(begin, end) > distance) {
            return -2;
        }

        return 1;
    }

    public static Date getDayByCount(Date curDatetime, int count) {
        // 设置日期格式
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        long longTime = 24 * 60 * 60 * 1000 * count;
        try {
            Date changeDate = formatter.parse(dateStr(curDatetime));
            longTime += changeDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 根据毫秒获得日期
        Date newDate = new Date(longTime);
        // 日期格式转换为字符串
        return newDate;
    }

    // 获得昨天日期
    public static Date getYesterday(Date date) {
        date = null == date ? new Date() : date;
        return addDays(date, -1);
    }

    // 获得本周一的日期
    public static Date getWeekBegin(Date date) {
        date = null == date ? new Date() : date;

        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(date);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    // 客服部刘立丹提的 每周第一天是上周四
    public static Date getWeekBeginForLiuLiDan(Date date) {
        int delay = -3;
        if (getWeekNum(date) >= 5) {
            delay = 4;
        }
        return addDays(getWeekBegin(date), delay);
    }

    // 获得本周日的日期
    public static Date getWeekEnd(Date date) {
        date = null == date ? new Date() : date;
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(getWeekBegin(date));
        cal.add(Calendar.DAY_OF_WEEK, 6);
        return cal.getTime();
    }

    // 客服部刘立丹提的 每周第一天是本周五
    public static Date getWeekEndForLiuLiDan(Date date) {
        int delay = -3;
        if (getWeekNum(date) >= 5) {
            delay = 4;
        }
        return addDays(getWeekEnd(date), delay);
    }

    // 获得本月第一天日期
    public static Date getMonthBegin(Date date) {
        date = null == date ? new Date() : date;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    // 获得本月最后一天日期
    public static Date getMonthEnd(Date date) {
        date = null == date ? new Date() : date;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        return cal.getTime();
    }

    public static int getWeekNum(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int num = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (num == 0) {
            num = 7;
        }
        return num;
    }

    /**
     * 返回1时（dt1>dt2）;0是相等；-1时dt1<dt2
     *
     * @param dt1
     * @param dt2
     * @return
     */
    public static int compareDate(Date dt1, Date dt2) {
        if (dt1 == null && dt2 == null)
            return 0;
        if (dt1 == null)
            return -1;
        if (dt2 == null)
            return 1;
        if (dt1.getTime() > dt2.getTime()) {
            return 1;
        } else if (dt1.getTime() < dt2.getTime()) {
            return -1;
        } else {// 相等
            return 0;
        }
    }

    /**
     * 返回当前剩余秒数
     *
     * @return
     */
    public static long getRemainingSeconds() {
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now();
        LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);
        LocalDateTime tomorrowMidnight = todayMidnight.plusDays(1);
        long seconds =
                TimeUnit.NANOSECONDS.toSeconds(Duration.between(LocalDateTime.now(), tomorrowMidnight).toNanos());
        return seconds;
    }

    /**
     * 将字符串转换成java.util.Date (yyyyMMddHH)
     *
     * @param time (yyyyMMddHHmmss)
     * @return
     */
    public static String strToDateYMDH(Date time) {
        String dateStr = null;
        DateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        dateStr = sdf.format(time);
        return dateStr;
    }

    /**
     * 将字符串转换成java.util.Date (yyyyMMddHH)
     *
     * @param time (yyyyMMddHHmmss)
     * @return
     */
    public static String strToDateYM(Date time) {
        String dateStr = null;
        DateFormat sdf = new SimpleDateFormat("yyyyMM");
        dateStr = sdf.format(time);
        return dateStr;
    }

    /**
     * 未知的字符串日期转换为时间格式
     *
     * @param StrDate
     * @return
     */
    public static Date unKnowStr2Date(String StrDate) {
        Date detectionDate = null;
        if (StringUtils.isBlank(StrDate)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        try {
            detectionDate = sdf.parse(StrDate);
        } catch (Exception e) {
            try {
                sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
                detectionDate = sdf.parse(StrDate);
            } catch (Exception e1) {
                try {
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    detectionDate = sdf.parse(StrDate);
                } catch (Exception e2) {
                    try {
                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                        detectionDate = sdf.parse(StrDate);
                    } catch (Exception e3) {
                        try {
                            sdf = new SimpleDateFormat("yyyyMMdd");
                            detectionDate = sdf.parse(StrDate);
                        } catch (Exception e4) {
                            try {
                                sdf = new SimpleDateFormat("yyyy/MM/dd");
                                detectionDate = sdf.parse(StrDate);
                            } catch (Exception e5) {

                            }
                        }
                    }
                }
            }
        }
        return detectionDate;
    }

    /**
     * 日期 字符串 类型 转为 Date类型<br>
     * 支持多种常用格式, 但是 注意 不支持 jdk8的日期格式
     *
     * @param dateStr 字符串类型
     * @return Date类型
     * @throws ParseException 转换异常
     */
    public static Date tryParseDate(final String dateStr, final String... datePatterns) throws ParseException {
        return parseDateStrictly(dateStr,
                ArrayUtils.isEmpty(datePatterns) ? DEFAULT_DATE_FORMAT_PATTERNS : datePatterns);
    }

    public static void main(String[] args) throws ParseException {
        // System.out.println(dateStr(getWeekBegin(new Date())));
        // System.out.println(dateStr(getWeekEnd(new Date())));
        // System.out.println(dateStr(getWeekBeginForLiuLiDan(new Date())));
        // System.out.println(dateStr(getWeekEndForLiuLiDan(new Date())));
        //
        // System.out.println(dateStr(getWeekBeginForLiuLiDan(strToDate("2016-07-31"))));
        // System.out.println(dateStr(getWeekEndForLiuLiDan(strToDate("2016-07-31"))));
        //
        // System.out.println(getWeekNum(strToDate("2016-10-21")));
        // System.out.println(strToDateYMDH(new Date()));
        System.out.println(unKnowStr2Date("2019-04-26").toString());
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate1 = spf.parse("2019-10-10");
        Date endDate1 = spf.parse("2019-10-15");
        Date startDate2 = spf.parse("2019-10-16");
        Date endDate2 = spf.parse("2019-10-20");
        System.out.println(checkCampDateConflict(startDate1, endDate1, startDate2, endDate2));

    }

    /**
     * 判断两个日期是否有重合
     * @param startDate1
     * @param endDate1
     * @param startDate2
     * @param endDate2
     * @return
     */
    public static boolean checkCampDateConflict(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            startDate1 = simpleDateFormat.parse(simpleDateFormat.format(startDate1));
            endDate1 = simpleDateFormat.parse(simpleDateFormat.format(endDate1));
            startDate2 = simpleDateFormat.parse(simpleDateFormat.format(startDate2));
            endDate2 = simpleDateFormat.parse(simpleDateFormat.format(endDate2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ((startDate1.before(endDate2) || startDate1.equals(endDate2)) && (endDate1.after(startDate2) || endDate1.equals(startDate2)));
    }

    /**
     * unitDate转为LocalDateTime
     * @param date
     * @return
     */
    public static LocalDateTime uDate2LocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime lDate = LocalDateTime.ofInstant(instant, zone);
        return lDate;
    }

    /**
     * 获取当天结束时间
     * @param date
     * @return
     */
    public static Date getEndTime(Date date) {
        Calendar dateEnd = Calendar.getInstance();
        dateEnd.setTime(date);
        dateEnd.set(Calendar.HOUR_OF_DAY, 23);
        dateEnd.set(Calendar.MINUTE, 59);
        dateEnd.set(Calendar.SECOND, 59);
        return dateEnd.getTime();
    }

    /**
     * 获取当天结束时间
     * @param date
     * @return
     */
    public static Date getStartTime(Date date) {
        Calendar dateEnd = Calendar.getInstance();
        dateEnd.setTime(date);
        dateEnd.set(Calendar.HOUR_OF_DAY, 00);
        dateEnd.set(Calendar.MINUTE, 00);
        dateEnd.set(Calendar.SECOND, 00);
        return dateEnd.getTime();
    }
}
