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
    
    // �Ƚϴ������������ʱ��Ĵ�С��
    // ������Ϊ yyyy-MM-dd hh:mm ��ʽ
    public static int dateTimeCmp(String dt1, String dt2) {
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        
        Date begin = null;
        try {
            begin = dfs.parse(dt1);
        } catch (ParseException e) {
            throw new IllegalArgumentException("��һ��������ʽ���Ϸ�");
        }

        Date end = null;
        try {
            end = dfs.parse(dt2);
        } catch (ParseException e) {
            throw new IllegalArgumentException("�ڶ���������ʽ���Ϸ�");
        }
        
        return begin.compareTo(end);
    }
    
    public static int dateTimeCmp(String dt) {
        // ֻ��һ��������Ĭ�Ϻ͵�ǰʱ���
        return dateTimeCmp(dt, new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(new Date()));
    }
    
    // ���㴫�����������ʱ��Ĳ
    // ������Ϊ yyyy-MM-dd hh:mm ��ʽ
    public static String calDateTimeDiff(String dt1, String dt2) {
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        
        Date begin = null;
        try {
            begin = dfs.parse(dt1);
        } catch (ParseException e) {
            throw new IllegalArgumentException("��һ��������ʽ���Ϸ�");
        }

        Date end = null;
        try {
            end = dfs.parse(dt2);
        } catch (ParseException e) {
            throw new IllegalArgumentException("�ڶ���������ʽ���Ϸ�");
        }

        long secondDiff = (end.getTime() - begin.getTime()) / 1000; // ����1000��Ϊ��ת������
        secondDiff = Math.abs(secondDiff);  

        long day = secondDiff / (24 * 3600);
        long hour = secondDiff % (24 * 3600) / 3600;
        long minute = secondDiff % 3600 / 60;
                
        StringBuilder builder = new StringBuilder();
        if(day > 0) {
            builder.append(day);
            builder.append("��");
        }
        if(hour > 0) {
            builder.append(hour);
            builder.append("Сʱ");
        }
        if(minute > 0) {
            builder.append(minute);
            builder.append("����");
        }
        
        return builder.toString();
    }
    
    public static String calDateTimeDiff(String dt) {
     // ֻ��һ��������Ĭ�Ϻ͵�ǰʱ������ֵ
        return calDateTimeDiff(dt, new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(new Date()));
    }
    
    public static String getUniqueFileName() {
        // ����   UUID.randomUUID();
        
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss", Locale.CHINA);
        return format.format(new Date());
    }  
}
