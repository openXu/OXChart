package com.openxu.utils;

import android.graphics.Paint;


/**
 * autour : openXu
 * date : 2016/7/24 14:12
 * className : FontUtil
 * version : 1.0
 * description : 文字相关处理帮助类(自定义控件专用)
 */
public class FontUtil {
    /**
     * @param paint
     * @param str
     * @return 返回指定笔和指定字符串的长度
     */
    public static float getFontlength(Paint paint, String str) {
        return paint.measureText(str);
    }
    /**
     * @return 返回指定笔的文字高度
     */
    public static float getFontHeight(Paint paint)  {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }
    /**
     * @return 返回指定笔离文字顶部的基准距离
     */
    public static float getFontLeading(Paint paint)  {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.leading- fm.ascent;
    }


}
