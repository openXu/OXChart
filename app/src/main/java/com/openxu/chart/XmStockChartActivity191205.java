package com.openxu.chart;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.openxu.cview.xmstock.bean.ChartData;
import com.openxu.cview.xmstock.bean.DataPoint;
import com.openxu.cview.xmstock.bean.FocusInfo;
import com.openxu.cview.xmstock20191205.LevelProgressView;
import com.openxu.cview.xmstock20191205.NorthSouthChart;
import com.openxu.cview.xmstock20191205.bean.Constacts;
import com.openxu.cview.xmstock20191205.bean.NorthSouth;
import com.openxu.utils.NumberFormatUtil;

import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

public class XmStockChartActivity191205 extends AppCompatActivity {
    NorthSouth tData, dData, wData;
    LevelProgressView levelView1, levelView2,levelView3,levelView4;
    NorthSouthChart tChart, dChart, wChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wcxstock_chart191205);

        levelView1 = (LevelProgressView)findViewById(R.id.levelView1);
        levelView2 = (LevelProgressView)findViewById(R.id.levelView2);
        levelView3 = (LevelProgressView)findViewById(R.id.levelView3);
        levelView4 = (LevelProgressView)findViewById(R.id.levelView4);
        tChart = (NorthSouthChart)findViewById(R.id.tChart);
        dChart = (NorthSouthChart)findViewById(R.id.dChart);
        wChart = (NorthSouthChart)findViewById(R.id.wChart);

        getData();

    }
    /**2、模拟接口获取数据*/
    private void getData(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //注意：等级是从0开始的，所以总等级和当前等级值在设置时应该+1
                levelView1.setLoading(false);
                levelView1.setLable("9折+100优惠卷");
                levelView1.setData(11, 11);  //总共Lv10，当前Lv10

                levelView2.setLoading(false);
                levelView2.setLable("96折");
                levelView2.setData(11, 5);//总共Lv10，当前Lv4

                levelView3.setLoading(false);
                levelView3.setLable("93折");
                levelView3.setData(16, 7);//总共Lv15，当前Lv6

                levelView4.setLoading(false);
                levelView4.setLable("91折+50优惠卷");
                levelView4.setData(11, 10);//总共Lv10，当前Lv9

                //今日流向
                tData = new Gson().fromJson(Constacts.dataMap.get("north-t"), NorthSouth.class);
                //历史每日流向
                dData = new Gson().fromJson(Constacts.dataMap.get("north-d"), NorthSouth.class);
                //历史每轴流向
                wData = new Gson().fromJson(Constacts.dataMap.get("north-w"), NorthSouth.class);


                tChart.setLoading(false);
                tChart.setChartType(NorthSouthChart.ChartType.TYPE_T);
                //涨跌颜色(红绿)
                tChart.setUpDownColor(new int[]{Color.parseColor("#FC4B4B"),
                        Color.parseColor("#1DAA3E")});
                //lable 和 color必须按照后台返回数据的顺序设置，比如["0930","1.0亿元","26300.510","+0.91%"]   金额在前，指数在后
                tChart.setlableColor(new int[]{Color.parseColor("#DC1010"), Color.parseColor("#FEB271")});
                tChart.setlableArray(new String[]{"总资金净流入", "上证指数价格"});
                tChart.setLableX(new String[]{"9:30", "11:30/13:00", "15:30"});
                tChart.setYMARK_NUM(5);
                tChart.setData(tData.getData());
                tChart.refresh();

                dChart.setLoading(false);
                dChart.setChartType(NorthSouthChart.ChartType.TYPE_DW);
                dChart.setlableArray(new String[]{"净流入金额", "净流出金额", "恒生指数价格"});
                dChart.setlableColor(new int[]{Color.parseColor("#FC4B4B"),
                        Color.parseColor("#1DAA3E"),
                        Color.parseColor("#C9D0DC")});
                dChart.setUpDownColor(new int[]{Color.parseColor("#FC4B4B"),
                        Color.parseColor("#1DAA3E")});
                dChart.setYMARK_NUM(5);
                dChart.setXMARK_NUM(4);

                dChart.setData(dData.getData());
                dChart.refresh();

                wChart.setLoading(false);
                wChart.setChartType(NorthSouthChart.ChartType.TYPE_DW);
                wChart.setlableArray(new String[]{"净流入金额", "净流出金额", "恒生指数价格"});
                wChart.setYMARK_NUM(5);
                wChart.setXMARK_NUM(4);
                wChart.setlableColor(new int[]{Color.parseColor("#FC4B4B"),
                        Color.parseColor("#1DAA3E"),
                        Color.parseColor("#C9D0DC")});
                wChart.setUpDownColor(new int[]{Color.parseColor("#FC4B4B"),
                        Color.parseColor("#1DAA3E")});
                wChart.setData(wData.getData());
                wChart.refresh();

            }
        }, 500);
    }

}
