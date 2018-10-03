package com.spring.utils;

import org.apache.commons.lang3.StringUtils;

public class Utils {

    public static String maskLeftWithTail4(String str) {
        return StringUtils.leftPad(StringUtils.right(str, 4),str.length(), "*");
    }

}
