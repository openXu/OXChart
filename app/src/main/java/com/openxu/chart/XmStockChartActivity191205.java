package com.openxu.chart;


import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.openxu.cview.xmstock.bean.ChartData;
import com.openxu.cview.xmstock.bean.DataPoint;
import com.openxu.cview.xmstock.bean.FocusInfo;
import com.openxu.cview.xmstock20191205.NorthSouthChart;
import com.openxu.cview.xmstock20191205.bean.Constacts;
import com.openxu.cview.xmstock20191205.bean.NorthSouth;
import com.openxu.utils.NumberFormatUtil;

import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

public class XmStockChartActivity191205 extends AppCompatActivity {
    NorthSouth tData, dData, wData;
    NorthSouthChart tChart, dChart, wChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wcxstock_chart191205);

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
                //今日流向
                tData = new Gson().fromJson(Constacts.dataMap.get("north-t"), NorthSouth.class);
                //历史每日流向
                dData = new Gson().fromJson(Constacts.dataMap.get("north-d"), NorthSouth.class);
                //历史每轴流向
                wData = new Gson().fromJson(Constacts.dataMap.get("north-w"), NorthSouth.class);



                tChart.setLoading(false);
                tChart.setData(tData.getData());
                tChart.setLableX(new String[]{"9:30", "11:30/13:00", "15:30"});
                tChart.setYMARK_NUM(5);
                tChart.refresh();

                dChart.setLoading(false);
                dChart.setYMARK_NUM(5);
                dChart.setXMARK_NUM(4);
                dChart.setData(dData.getData());
                dChart.refresh();

                wChart.setLoading(false);
                wChart.setYMARK_NUM(5);
                wChart.setXMARK_NUM(4);
                wChart.setData(wData.getData());
                wChart.refresh();

            }
        }, 500);
    }

}
