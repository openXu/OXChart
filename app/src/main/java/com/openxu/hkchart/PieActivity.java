package com.openxu.hkchart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.openxu.hkchart.bean.PieBean;
import com.openxu.cview.chart.bean.ChartLable;
import com.openxu.cview.chart.piechart.PieChartLayout;
import com.openxu.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

public class PieActivity extends AppCompatActivity {

    private PieChartLayout pieChart1, pieChart2, pieChart3,pieChart4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie);
        pieChart1 = (PieChartLayout)findViewById(R.id.pieChart1);
        /*
         * 圆环宽度
         * ringWidth > 0 :空心圆环，内环为白色，可以在内环中绘制字
         * ringWidth <=0 :实心
         */
        pieChart1.setRingWidth(DensityUtil.dip2px(this, 15));
        pieChart1.setLineLenth(DensityUtil.dip2px(this, 8)); // //指示线长度
        pieChart1.setTagModul(PieChartLayout.TAG_MODUL.MODUL_CHART);       //在扇形图上显示tag
        pieChart1.setDebug(false);
        pieChart1.setLoading(true);
        //请求数据
        List<PieBean> datalist = new ArrayList<>();
        datalist.add(new PieBean(20, "理发屋"));
        datalist.add(new PieBean(20, "KTV"));
        //显示在中间的lable
        List<ChartLable> tableList = new ArrayList<>();
        tableList.add(new ChartLable("建筑", DensityUtil.sp2px(this, 12), getResources().getColor(R.color.text_color_light_gray)));
        tableList.add(new ChartLable("性质", DensityUtil.sp2px(this, 12), getResources().getColor(R.color.text_color_light_gray)));
        pieChart1.setLoading(false);
        //参数1：数据类型   参数2：数量字段名称   参数3：名称字段   参数4：数据集合   参数5:lable集合
        pieChart1.setChartData(PieBean.class, "Numner", "Name",datalist ,tableList);


        pieChart2 = (PieChartLayout)findViewById(R.id.pieChart2);
        pieChart2.setRingWidth(DensityUtil.dip2px(this, 20));
        pieChart2.setTagModul(PieChartLayout.TAG_MODUL.MODUL_LABLE);      //在lable后面显示tag
        pieChart2.setDebug(false);
        pieChart2.setLoading(true);
        //请求数据
        datalist.clear();
        datalist.add(new PieBean(20, "IT"));
        datalist.add(new PieBean(10, "销售"));
        datalist.add(new PieBean(30, "金融"));
        datalist.add(new PieBean(8, "林木业"));
        datalist.add(new PieBean(15, "制造"));
        datalist.add(new PieBean(15, "农业"));
        pieChart2.setLoading(false);
        pieChart2.setChartData(PieBean.class, "Numner", "Name",datalist ,null);


        pieChart3 = (PieChartLayout)findViewById(R.id.pieChart3);
        //圆环宽度，如果值>0,则为空心圆环，内环为白色，可以在内环中绘制字
        pieChart3.setRingWidth(DensityUtil.dip2px(this, 0));
        pieChart3.setTagModul(PieChartLayout.TAG_MODUL.MODUL_LABLE);
        pieChart3.setDebug(false);
        pieChart3.setLoading(false);
        pieChart3.setChartData(PieBean.class, "Numner", "Name",datalist ,null);

        pieChart4 = (PieChartLayout)findViewById(R.id.pieChart4);
        //圆环宽度，如果值>0,则为空心圆环，内环为白色，可以在内环中绘制字
        pieChart4.setRingWidth(DensityUtil.dip2px(this, 0));
        pieChart4.setTagModul(PieChartLayout.TAG_MODUL.MODUL_CHART);
        pieChart4.setDebug(false);
        datalist.add(new PieBean(20, "IT"));
        datalist.add(new PieBean(10, "销售"));
        datalist.add(new PieBean(30, "金融"));
        datalist.add(new PieBean(8, "林木业"));
        datalist.add(new PieBean(15, "制造"));
        datalist.add(new PieBean(15, "农业"));
        datalist.add(new PieBean(20, "IT"));
        datalist.add(new PieBean(10, "销售"));
        datalist.add(new PieBean(30, "金融"));
        datalist.add(new PieBean(8, "林木业"));
        datalist.add(new PieBean(15, "制造"));
        datalist.add(new PieBean(15, "农业"));
        pieChart4.setLoading(false);
        pieChart4.setChartData(PieBean.class, "Numner", "Name",datalist ,null);

    }
}
