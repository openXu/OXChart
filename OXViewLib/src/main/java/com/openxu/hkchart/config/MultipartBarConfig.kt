package com.openxu.hkchart.config

/**
 * Author: openXu
 * Time: 2021/5/9 12:14
 * class: MultipartBarConfig
 * Description:
 */
class MultipartBarConfig : DisplayConfigBase(){

    var barWidth = 15f //默认柱宽度15dp
    var spacingRatio = 1f  //默认柱间的间距占比，间距= barWidth*spacingRatio

    val barSpace : Float
        get() {return barWidth * spacingRatio }

}