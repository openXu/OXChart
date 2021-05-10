package com.openxu.hkchart;

import android.graphics.Color;
import android.os.Bundle;

import com.openxu.cview.chart.barchart.BarHorizontalChart;
import com.openxu.cview.chart.bean.BarBean;
import com.openxu.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

public class HorizontalBarActivity extends AppCompatActivity {

    private BarHorizontalChart chart1, chart2, chart3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontalbar);
        Random random = new Random();


        chart1 = (BarHorizontalChart)findViewById(R.id.chart1);
        chart1.setBarSpace(DensityUtil.dip2px(this, 1));  //双柱间距
        chart1.setBarItemSpace(DensityUtil.dip2px(this, 20));  //柱间距
        chart1.setBarNum(3);
        chart1.setBarColor(new int[]{Color.parseColor("#5F93E7"),Color.parseColor("#F28D02")});
        //X轴
        List<String> strXList = new ArrayList<>();
        //柱状图数据
        List<List<BarBean>> dataList = new ArrayList<>();
        for(int i = 0; i<100; i++){
            //此集合为柱状图上一条数据，集合中包含几个实体就是几个柱子
            List<BarBean> list = new ArrayList<>();
            list.add(new BarBean(random.nextInt(30), "lable1"));
            list.add(new BarBean(random.nextInt(20), "lable2"));
            dataList.add(list);
            strXList.add((i+1)+"月");
        }
        chart1.setLoading(false);
        chart1.setData(dataList, strXList);


        chart2 = (BarHorizontalChart)findViewById(R.id.chart2);
        chart2.setLoading(true);
        chart2.setBarNum(1);
        chart2.setBarColor(new int[]{Color.parseColor("#5F93E7")});
        strXList.clear();
        dataList.clear();
        for(int i = 0; i<12; i++){
            //此集合为柱状图上一条数据，集合中包含几个实体就是几个柱子
            List<BarBean> list = new ArrayList<>();
            list.add(new BarBean(random.nextInt(10), "接入系统"));
            dataList.add(list);
            strXList.add((i+1)+"月");
        }
        chart2.setLoading(false);
        chart2.setData(dataList, strXList);



    }


}
