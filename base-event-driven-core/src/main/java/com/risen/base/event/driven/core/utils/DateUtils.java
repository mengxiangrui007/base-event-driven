package com.risen.base.event.driven.core.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author mengxr
 */
public class DateUtils {

    public static final String DATE_FMT_0 = "yyyyMMdd";
    public static final String DATE_FMT_1 = "yyyy/MM/dd";
    public static final String DATE_FMT_2 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FMT_3 = "yyyy-MM-dd";
    public static final String DATE_FMT_4 = "yyyy-MM";
    public static final String FORMAT_NO_SPLITTER = "yyyyMMddHHmmss";


    /**
     * 解析时间
     *
     * @param time
     * @param format
     * @return
     */
    public static Date parse(String time, String format) {
        return asDateTime(LocalDateTime.parse(time, DateTimeFormatter.ofPattern(format)));
    }

    /**
     * 解析日期
     *
     * @param time
     * @param format
     * @return
     */
    public static Date parseDate(String time, String format) {
        return asDate(LocalDate.parse(time, DateTimeFormatter.ofPattern(format)));
    }


    public static Date asDate(LocalDate localDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zoneId).toInstant();
        return Date.from(instant);
    }

    public static Date asDateTime(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static String today() {
        return  DateFormatUtils.format(new Date(), DATE_FMT_3);
    }
    public static String toDateTime() {
        return  DateFormatUtils.format(new Date(), DATE_FMT_2);
    }
}