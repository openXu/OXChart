package com.openxu.chart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.openxu.chart.bean.RoseBean;
import com.openxu.cview.chart.rosechart.NightingaleRoseChart;

import java.util.ArrayList;
import java.util.List;

public class RoseActivity extends AppCompatActivity {

    private NightingaleRoseChart roseChartEmpty, roseChartOne, roseChartSmall,roseChartMany;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rose);
        roseChartEmpty = (NightingaleRoseChart)findViewById(R.id.roseChartEmpty);
        roseChartEmpty.setLoading(false);

        roseChartOne = (NightingaleRoseChart)findViewById(R.id.roseChartOne);
        roseChartOne.setShowChartLable(false);   //是否在图表上显示指示lable
        roseChartOne.setShowChartNum(true);      //是否在图表上显示指示num
        roseChartOne.setShowNumTouched(true);    //点击显示数量
        roseChartOne.setShowRightNum(false);      //右侧显示数量
        List<Object> roseList = new ArrayList<>();
        roseList.add(new RoseBean(10, "单条数据"));
        //参数1：数据对象class， 参数2：数量属性字段名称， 参数3：名称属性字段名称， 参数4：数据集合
        roseChartOne.setData(RoseBean.class, "count", "ClassName", roseList);
        roseChartOne.setLoading(false);   //是否正在加载，数据加载完毕后置为false


        roseChartSmall = (NightingaleRoseChart)findViewById(R.id.roseChartSmall);
        roseChartSmall.setShowChartLable(true);    //是否在图表上显示指示lable
        roseChartSmall.setShowChartNum(false);     //是否在图表上显示指示num
        roseChartSmall.setShowNumTouched(false);   //点击显示数量
        roseChartSmall.setShowRightNum(true);      //右侧显示数量
        roseList.add(new RoseBean(10, "数据1"));
        roseList.add(new RoseBean(13, "数据2"));
        roseList.add(new RoseBean(31, "数据3"));
        roseList.add(new RoseBean(8, "数据4"));
        roseList.add(new RoseBean(21, "数据5"));
        //参数1：数据对象class， 参数2：数量属性字段名称， 参数3：名称属性字段名称， 参数4：数据集合
        roseChartSmall.setData(RoseBean.class, "count", "ClassName", roseList);
        roseChartSmall.setLoading(false);//是否正在加载，数据加载完毕后置为false

        roseChartMany = (NightingaleRoseChart)findViewById(R.id.roseChartMany);
        roseChartMany.setShowChartLable(false);
        roseChartMany.setShowChartNum(true);
        roseChartMany.setShowNumTouched(true);
        roseChartMany.setShowRightNum(false);
        roseList.add(new RoseBean(10, "数据1"));
        roseList.add(new RoseBean(13, "数据2"));
        roseList.add(new RoseBean(31, "数据3"));
        roseList.add(new RoseBean(8, "数据4"));
        roseList.add(new RoseBean(21, "数据5"));
        roseList.add(new RoseBean(21, "数据6"));
        roseList.add(new RoseBean(25, "数据7"));
        roseList.add(new RoseBean(14, "数据8"));
        roseList.add(new RoseBean(39, "数据9"));
        roseList.add(new RoseBean(39, "数据10"));
        roseList.add(new RoseBean(39, "数据11"));
        roseList.add(new RoseBean(1, "数据12"));
        roseList.add(new RoseBean(3, "数据13"));
        roseList.add(new RoseBean(8, "数据14"));
        roseList.add(new RoseBean(8, "数据14"));
        roseChartMany.setData(RoseBean.class, "count", "ClassName", roseList);
        roseChartMany.setLoading(false);
    }
}
