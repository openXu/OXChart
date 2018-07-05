package com.openxu.utils;

import java.text.NumberFormat;

/**
 * autour : xiami
 * date : 2018/3/15 10:52
 * className : NumberFormatUtil
 * version : 1.0
 * description : 数字格式化工具类
 */
public class NumberFormatUtil {
    public static String formattedDecimalToPercentage(double decimal){
        //获取格式化对象
        NumberFormat nt = NumberFormat.getPercentInstance();
        //设置百分数精确度2即保留两位小数
        nt.setMinimumFractionDigits(2);
        return nt.format(decimal);
    }
}
