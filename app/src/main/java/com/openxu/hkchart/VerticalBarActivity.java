package com.openxu.hkchart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.openxu.cview.chart.barchart.BarVerticalChart;
import com.openxu.cview.chart.bean.BarBean;
import com.openxu.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VerticalBarActivity extends AppCompatActivity {

    private BarVerticalChart chart1, chart2, chart3,chart4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verticalbar);
        Random random = new Random();


        chart1 = (BarVerticalChart)findViewById(R.id.chart1);
        chart1.setLoading(true);
        chart1.setDebug(false);
        chart1.setBarNum(1);
        chart1.setBarColor(new int[]{Color.parseColor("#5F93E7")});
        //X轴
        List<String> strXList = new ArrayList<>();
        //柱状图数据
        List<List<BarBean>> dataList = new ArrayList<>();
        for(int i = 0; i<5; i++){
            //此集合为柱状图上一条数据，集合中包含几个实体就是几个柱子
            List<BarBean> list = new ArrayList<>();
            list.add(new BarBean(random.nextInt(10), "接入系统"));
            dataList.add(list);
            strXList.add((i+1)+"月");
        }
        chart1.setLoading(false);
        chart1.setData(dataList, strXList);


        chart2 = (BarVerticalChart)findViewById(R.id.chart2);
        chart2.setBarSpace(DensityUtil.dip2px(this, 1));  //双柱间距
        chart2.setBarItemSpace(DensityUtil.dip2px(this, 20));  //柱间距
        chart2.setDebug(false);
        chart2.setBarNum(2);   //一组柱子数量
        chart2.setBarColor(new int[]{Color.parseColor("#5F93E7"),Color.parseColor("#F28D02")});
        strXList.clear();
        dataList.clear();
        for(int i = 0; i<5; i++){
            //此集合为柱状图上一条数据，集合中包含几个实体就是几个柱子
            List<BarBean> list = new ArrayList<>();
            list.add(new BarBean(random.nextInt(30), "接入系统"));
            list.add(new BarBean(random.nextInt(20), "审核信息"));
            dataList.add(list);
            strXList.add((i+1)+"月");
        }
        chart2.setLoading(false);
        chart2.setData(dataList, strXList);

        chart3 = (BarVerticalChart)findViewById(R.id.chart3);
        chart3.setBarSpace(DensityUtil.dip2px(this, 1));  //双柱间距
        chart3.setBarItemSpace(DensityUtil.dip2px(this, 20));  //柱间距
        chart3.setShowEnd(true);      //内容超过时，初始显示是否是最后的数据
        chart3.setDebug(false);
        chart3.setBarNum(4);
        chart3.setBarColor(new int[]{Color.parseColor("#5F93E7"),Color.parseColor("#F28D02"),
                Color.parseColor("#157EFB"),Color.parseColor("#FED032")});
        strXList.clear();
        dataList.clear();
        for(int i = 0; i<5; i++){
            //此集合为柱状图上一条数据，集合中包含几个实体就是几个柱子
            List<BarBean> list = new ArrayList<>();
            list.add(new BarBean(random.nextInt(30), "lable1"));
            list.add(new BarBean(random.nextInt(20), "lable2"));
            list.add(new BarBean(random.nextInt(35), "lable3"));
            list.add(new BarBean(random.nextInt(28), "lable4"));
            dataList.add(list);
            strXList.add((i+1)+"月");
        }
        chart3.setLoading(false);
        chart3.setData(dataList, strXList);


        chart4 = (BarVerticalChart)findViewById(R.id.chart4);
        chart4.setBarSpace(DensityUtil.dip2px(this, 1));  //双柱间距
        chart4.setBarItemSpace(DensityUtil.dip2px(this, 20));  //柱间距
        chart4.setDebug(false);
        chart4.setBarNum(2);
        chart4.setBarColor(new int[]{Color.parseColor("#5F93E7"),Color.parseColor("#F28D02")});
        strXList.clear();
        dataList.clear();
        for(int i = 0; i<100; i++){
            //此集合为柱状图上一条数据，集合中包含几个实体就是几个柱子
            List<BarBean> list = new ArrayList<>();
            list.add(new BarBean(random.nextInt(30), "接入系统"));
            list.add(new BarBean(random.nextInt(20), "审核信息"));
            dataList.add(list);
            strXList.add((i+1)+"月");
        }
        chart4.setLoading(false);
        chart4.setData(dataList, strXList);
    }


}
