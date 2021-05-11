package com.openxu.hkchart.config

import android.content.Context
import android.graphics.Color
import android.graphics.Region
import com.openxu.utils.DensityUtil

/**
 * Author: openXu
 * Time: 2021/5/9 12:14
 * class: MultipartBarConfig
 * Description:
 */
class HorizontalBarConfig(context: Context) : ChartConfigBase(){

    var barWidth = DensityUtil.dip2px(context, 26f)
    var barSpace = DensityUtil.dip2px(context, 10f)
    var barColor = intArrayOf(
            Color.parseColor("#F46863"),
            Color.parseColor("#2DD08A"),
            Color.parseColor("#567CF6"),
            Color.parseColor("#F5B802"),
            Color.parseColor("#CC71F7"))

}

class HBar(val lable: String,val value: Float){
}