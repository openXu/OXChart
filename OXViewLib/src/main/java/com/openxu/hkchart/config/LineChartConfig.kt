package com.openxu.hkchart.config

import android.content.Context
import android.graphics.Color
import com.openxu.hkchart.data.FocusPanelText
import com.openxu.utils.DensityUtil

/**
 * Author: openXu
 * Time: 2021/5/9 12:14
 * class: MultipartBarConfig
 * Description:
 */
class LineChartConfig(context: Context) : ChartConfigBase(){
    var lineType : LineType = LineType.BROKEN
    var lineWidth = DensityUtil.dip2px(context, 1f).toFloat()
    var lineColor = intArrayOf(
            Color.parseColor("#f46763"),
            Color.parseColor("#3cd595"),
            Color.parseColor("#4d7bff"),
            Color.parseColor("#4d7bff"))
    var pageShowNum  = 0 //第一次页面总数据量   没有设置展示数据量，则默认为全部展示
    /**设置焦点面板显示内容*/
    var focusPanelText: Array<FocusPanelText>? = null
}

enum class LineType {
    CURVE,  //曲线
    BROKEN //折线
}