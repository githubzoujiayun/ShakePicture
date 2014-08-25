package com.lucas.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JavaSEUtil {           
    
    public static String getCurrDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return format.format(new Date());
    }
    
    public static String getCurrTimeNoSecond() {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm", Locale.CHINA);
        return format.format(new Date());
    }
    
    public static String getCurrDateTimeNoSecond() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.CHINA);
        return format.format(new Date());
    }
    
    public static String getCurrDateTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);
        return format.format(new Date());
    }
    
    public static long parseDateTime(String dateTime) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.CHINA).parse(dateTime).getTime();
    }
    
    // 比较传入的两个日期时间的大小。
    // 参数需为 yyyy-MM-dd hh:mm 形式
    public static int dateTimeCmp(String dt1, String dt2) {
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        
        Date begin = null;
        try {
            begin = dfs.parse(dt1);
        } catch (ParseException e) {
            throw new IllegalArgumentException("第一个参数格式不合法");
        }

        Date end = null;
        try {
            end = dfs.parse(dt2);
        } catch (ParseException e) {
            throw new IllegalArgumentException("第二个参数格式不合法");
        }
        
        return begin.compareTo(end);
    }
    
    public static int dateTimeCmp(String dt) {
        // 只传一个参数，默认和当前时间比
        return dateTimeCmp(dt, new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(new Date()));
    }
    
    // 计算传入的两个日期时间的差。
    // 参数需为 yyyy-MM-dd hh:mm 形式
    public static String calDateTimeDiff(String dt1, String dt2) {
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        
        Date begin = null;
        try {
            begin = dfs.parse(dt1);
        } catch (ParseException e) {
            throw new IllegalArgumentException("第一个参数格式不合法");
        }

        Date end = null;
        try {
            end = dfs.parse(dt2);
        } catch (ParseException e) {
            throw new IllegalArgumentException("第二个参数格式不合法");
        }

        long secondDiff = (end.getTime() - begin.getTime()) / 1000; // 除以1000是为了转换成秒
        secondDiff = Math.abs(secondDiff);  

        long day = secondDiff / (24 * 3600);
        long hour = secondDiff % (24 * 3600) / 3600;
        long minute = secondDiff % 3600 / 60;
                
        StringBuilder builder = new StringBuilder();
        if(day > 0) {
            builder.append(day);
            builder.append("天");
        }
        if(hour > 0) {
            builder.append(hour);
            builder.append("小时");
        }
        if(minute > 0) {
            builder.append(minute);
            builder.append("分钟");
        }
        
        return builder.toString();
    }
    
    public static String calDateTimeDiff(String dt) {
     // 只传一个参数，默认和当前时间计算差值
        return calDateTimeDiff(dt, new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(new Date()));
    }
    
    public static String getUniqueFileName() {
        // 试试   UUID.randomUUID();
        
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss", Locale.CHINA);
        return format.format(new Date());
    }  
}
