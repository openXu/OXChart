package com.openxu.hkchart.config

import android.content.Context
import android.graphics.Color
import com.openxu.utils.DensityUtil

/**
 * Author: openXu
 * Time: 2021/5/9 12:14
 * class: MultipartBarConfig
 * Description:
 */
class BarChartConfig(context: Context) : ChartConfigBase(){
    var scrollAble = true //是否支持滚动
    var barWidth = DensityUtil.dip2px(context, 15f).toFloat() //柱宽度
    var barSpace = DensityUtil.dip2px(context, 1f).toFloat() //双柱间的间距
    var groupSpace = DensityUtil.dip2px(context, 25f).toFloat() //一组柱之间的间距（只有scrollAble==true时才生效）
    var barColor = intArrayOf(
            Color.parseColor("#f46763"),
            Color.parseColor("#3cd595"),
            Color.parseColor("#4d7bff")) //柱颜色
}

class Bar(val valuex: String,val valuey: List<Float>)