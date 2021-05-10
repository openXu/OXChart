package com.openxu.hkchart.config

/**
 * Author: openXu
 * Time: 2021/5/9 11:58
 * class: DisplayConfigBase
 * Description:
 */
enum class DisplayScheme {
    SHOW_ALL,    //第一次全部显示
    SHOW_BEGIN,  //从第一条数据开始展示固定条数
    SHOW_END     //从最后一条数据开始展示固定条数
}
open class DisplayConfigBase{

    //默认第一次全部展示
    open var displayScheme:DisplayScheme = DisplayScheme.SHOW_ALL



}

