package com.openxu.hkchart.view;

import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Line;

/**
 * Author: openXu
 * Time: 2020/11/12 11:17
 * class: EchartOptionUtil
 * Description:
 */
public class EchartOptionUtil {

    public static GsonOption getLineChartOptions(Object[] xAxis, Object[] yAxis) {
        GsonOption option = new GsonOption();
        option.title("折线图");
        option.legend("销量");
        option.tooltip().trigger(Trigger.axis);

        ValueAxis valueAxis = new ValueAxis();
        option.yAxis(valueAxis);

        CategoryAxis categorxAxis = new CategoryAxis();
        categorxAxis.axisLine().onZero(false);
        categorxAxis.boundaryGap(true);
        categorxAxis.data(xAxis);
        option.xAxis(categorxAxis);

        Line line = new Line();
        line.smooth(false).name("销量").data(yAxis).itemStyle().normal().lineStyle().shadowColor("rgba(0,0,0,0.4)");
        option.series(line);
        return option;
    }

}
