package com.openxu.hkchart.config

/**
 * Author: openXu
 * Time: 2021/5/9 11:58
 * class: DisplayScheme
 * Description: 首次展示模式
 */
enum class DisplayScheme {
    SHOW_ALL,    //第一次全部显示
    SHOW_BEGIN,  //从第一条数据开始展示固定条数
    SHOW_END     //从最后一条数据开始展示固定条数
}
/**
 * Author: openXu
 * Time: 2021/5/9 11:58
 * class: ChartConfigBase
 * Description: 图表配置基类
 */
open class ChartConfigBase{

    //默认第一次展示全部数据
    open var displayScheme:DisplayScheme = DisplayScheme.SHOW_ALL
    //柱颜色
    open var xAxisMark: XAxisMark?=null
    open var yAxisMark: YAxisMark?=null

    //是否展示动画
    open var showAnim = true

    //设置数据总数，可选并且设置的数据总数可以大于data集合总数，如果没有设置默认dataTotalCount=_data.size
    //有些图表的数据总数和设置的数据可能不同，比如股票分时图：总数据量 = 一天交易时间分钟数，当前返回的数据量 = 开盘时间到当前时间分钟的数据
    open var dataTotalCount : Int = -1



}

