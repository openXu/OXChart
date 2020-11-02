package com.openxu.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * autour : xiami
 * date : 2018/3/15 10:52
 * className : NumberFormatUtil
 * version : 1.0
 * description : 数字格式化工具类
 */
public class NumberFormatUtil {
    /**
     * double 类型转换成保留2位小数百分数    0.1 -> 10.00%
     * @param decimal
     * @return
     */
    public static String formattedDecimalToPercentage(double decimal){
        //获取格式化对象
        NumberFormat nt = NumberFormat.getPercentInstance();
        //设置百分数精确度2即保留两位小数
        nt.setMinimumFractionDigits(2);
        return nt.format(decimal);
    }

    /**
     * double 类型转换成保留2位小数    0.1 -> 0.10
     * @param decimal
     * @return
     */
    public static String formattedDecimal(double decimal){
        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String strPrice = decimalFormat.format(decimal);//返回字符串
        return strPrice;
    }
}
