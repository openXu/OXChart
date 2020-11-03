package com.openxu.chart.element;

import android.content.Context;
import android.graphics.Color;

import com.openxu.chart.linechart.element.Line;
import com.openxu.utils.DensityUtil;


/**
 * Author: openXu
 * Time: 2020/11/2 11:24
 * class: DisplayConfig
 * Description:
 */
public class DisplayConfig {

    /**
     * 分时图   ：
     *     dataTotal = 4 * 60
     *     dataDisplay = 0
     *     displayIndex = 0
     * 不可滚动  ：
     *     dataTotal = x
     *     dataDisplay = dataTotal
     *     displayIndex = 0
     * 可滚动 ：
     *     dataTotal = x
     *     dataDisplay = y   y<x
     *     displayIndex = i
     */
    //图表展示的总数据量，不一定=datas.size()  ,比如股票分时图
    public int dataTotal;
    //可滚动的图表需要设置，默认一篇展示的数据量
    public int dataDisplay;   //图表中显示的数据量，如果dataDisplay<dataTotal，则可以滚动
    //展示的第一条数据的位置。取值范围0 ~ dataTotal-dataDisplay-1，0表示从头展示，dataTotal-dataDisplay-1表示展示尾部
    public int displayIndex;
    /**以下不能设置，自动计算的值*/
    public float oneSpace;  //单个元素占据的x宽度
    public float scale;     //缩放值
    public float offset;      //偏移量
    public int offsetMin;   //手指左划最大偏移
    public int offsetMax;   //手指右滑最大偏移
    public static class Builder{
        DisplayConfig display;
        public Builder() {
            display = new DisplayConfig();
        }

        public Builder dataTotal(int dataTotal) {
            display.dataTotal = dataTotal;
            return this;
        }
        public Builder dataDisplay(int dataDisplay) {
            display.dataDisplay = dataDisplay;
            return this;
        }
        public Builder displayIndex(int displayIndex) {
            display.displayIndex = displayIndex;
            return this;
        }
        public DisplayConfig build(){
            //当设置的展示数据数量 > 数据总数时， 默认全部展示
            if(display.dataDisplay>display.dataTotal){
                display.dataDisplay = display.dataTotal;
                display.displayIndex = 0;
            }
            display.displayIndex = display.displayIndex<0?0:display.displayIndex;
            display.displayIndex = display.displayIndex>display.dataTotal-display.dataDisplay-1?
                    display.dataTotal-display.dataDisplay-1:display.displayIndex;
            return  display;
        }
    }
}
