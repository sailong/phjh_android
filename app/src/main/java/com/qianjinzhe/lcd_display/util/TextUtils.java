package com.qianjinzhe.lcd_display.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              TextUtils.java
 * Description：
 *              字符串操作工具类
 * Author:
 *              youngHu
 * Finished：
 *              2018年05月31日
 ********************************************************/
public class TextUtils {

    public static boolean isEmpty(String str) {
        boolean isEmpty = false;
        if (str == null || str.equals("") || "null".equalsIgnoreCase(str)) {
            isEmpty = true;
        }
        return isEmpty;
    }

    public static String parseText(String... params) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < params.length; i++) {
            if (i == 0) {
                buffer.append(params[i]);
                continue;
            }
            if (TextUtils.isEmpty(params[i])) {
                break;
            }
            buffer.append("," + params[i]);
        }
        return buffer.toString();
    }

    public static String parse2Double(Double price) {
        return String.format("%.2f", price);
    }

    public static String parseDouble(Double price) {
        return String.format("%.1f", price);
    }

    public static String parseMark(String... ids) {
        StringBuffer mark = new StringBuffer();
        for (int i = 0; i < ids.length; i++) {
            if (i == ids.length - 1) {
                mark.append("?");
                continue;
            }
            mark.append("?,");
        }
        return mark.toString();
    }

//    public static boolean isEmail(String strEmail) {
//        String strPattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
//        Pattern p = Pattern.compile(strPattern);
//        Matcher m = p.matcher(strEmail);
//        if (m.matches()) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    public static boolean isCellphone(String str) {
        Pattern pattern = Pattern.compile("1[0-9]{10}");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static String getHideCall(String str) {
        return str.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    public static String getHideEmail(String str) {
        return str.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4");
    }


    public static String[] parseImage(String image) {
        if (TextUtils.isEmpty(image)) {
            return null;
        }
        String[] images = image.split(",");
        return images;
    }

    public static String showName(String nikeName, String acount) {
        if (TextUtils.isEmpty(nikeName)) {
            if (TextUtils.isEmpty(acount)) {
                return acount;
            }
            if (acount.length() > 4) {
                return acount.substring(0, 3) + "...";
            }
            return acount;
        }
        if (nikeName.length() > 4) {
            return nikeName.substring(0, 3) + "...";
        }
        return nikeName;
    }
}
