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
    public static String formattedDecimalToPercentage(double decimal, int digits){
        //获取格式化对象
        NumberFormat format = NumberFormat.getPercentInstance();
//        format.setGroupingUsed(true);
        //设置百分数精确度2即保留两位小数
        format.setMinimumFractionDigits(digits);
        return format.format(decimal);
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
    /**2022309 --> 2,022,309*/
    public static String partitionNumber(float num, int digits){
        String pattern = "#,##0";  //不以科学计数法显示，并把结果用逗号隔开
        if(digits>0){
            pattern+=".";
            for(int i = 0; i<digits; i++){
                pattern += "0";
            }
        }
        DecimalFormat format = new DecimalFormat(pattern);
//        DecimalFormat format = new DecimalFormat("#,##0.00");//不以科学计数法显示，并把结果用逗号隔开保留两位小数
//                    BigDecimal bigDecimal = new BigDecimal(stt);//不以科学计数法显示，正常显示保留两位小数
//        NumberFormat format = NumberFormat.getNumberInstance();
        //整数部分每隔三个，就会有 " ,"
        format.setGroupingUsed(true);
        return format.format(num);
    }
}
