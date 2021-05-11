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
class MultipartBarConfig(context: Context) : ChartConfigBase(){

    //默认柱宽度15dp
    var barWidth = DensityUtil.dip2px(context, 15f).toFloat()
    //默认柱间的间距占比，间距 = barWidth*spacingRatio
    var spacingRatio = 1f


    //柱颜色
    var barColor: IntArray = intArrayOf(
            Color.parseColor("#f46763"),
            Color.parseColor("#3cd595"),
            Color.parseColor("#4d7bff"))

    /**设置焦点面板显示内容*/
    var focusPanelText: Array<FocusPanelText>? = null
}
/**
 * Author: openXu
 * Time: 2021/5/9 12:14
 * class: MultipartBarData
 * Description: 数据
 */
class MultipartBarData(val valuey: List<Float>, val valuex: String)