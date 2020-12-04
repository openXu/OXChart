package com.openxu.hkchart;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

import com.google.gson.Gson;
import com.openxu.cview.chart.dashboard.DashBoardItem;
import com.openxu.cview.xmstock20191205.DashboardView;
import com.openxu.cview.xmstock20191205.LevelProgressView;
import com.openxu.cview.xmstock20191205.NorthSouthChart;
import com.openxu.cview.xmstock20191205.bean.Constacts;
import com.openxu.cview.xmstock20191205.bean.NorthSouth;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class XmStockChartActivity191205 extends AppCompatActivity {
    Button btn_d_north, btn_d_south, btn_y_north, btn_y_south;
    NorthSouth data;
    com.openxu.cview.chart.dashboard.DashBoardView dashboardViewOld;
    DashboardView dashboardView;

    LevelProgressView levelView1, levelView2,levelView3,levelView4;
    NorthSouthChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wcxstock_chart191205);
        dashboardViewOld = (com.openxu.cview.chart.dashboard.DashBoardView)findViewById(R.id.dashboardViewOld);
        dashboardView = (DashboardView)findViewById(R.id.dashboardView);

        levelView1 = (LevelProgressView)findViewById(R.id.levelView1);
        levelView2 = (LevelProgressView)findViewById(R.id.levelView2);
        levelView3 = (LevelProgressView)findViewById(R.id.levelView3);
        levelView4 = (LevelProgressView)findViewById(R.id.levelView4);

        btn_d_north = (Button)findViewById(R.id.btn_d_north);
        btn_d_south = (Button)findViewById(R.id.btn_d_south);
        btn_y_north = (Button)findViewById(R.id.btn_y_north);
        btn_y_south = (Button)findViewById(R.id.btn_y_south);
        chart = (NorthSouthChart)findViewById(R.id.chart);
        //北向资金今日
        btn_d_north.setOnClickListener(v->{
            getData(NorthSouthChart.ChartType.TYPE_T_NORTH);
        });
        //南向资金今日
        btn_d_south.setOnClickListener(v->{
            getData(NorthSouthChart.ChartType.TYPE_T_SOUTH);
        });
        //北向资金 历史每日每周
        btn_y_north.setOnClickListener(v->{
            getData(NorthSouthChart.ChartType.TYPE_DW_NORTH);
        });
        //南向资金 历史每日每周
        btn_y_south.setOnClickListener(v->{
            getData(NorthSouthChart.ChartType.TYPE_DW_SOUTH);
        });
        getData();

    }

    //模拟获取数据
    private void getData(NorthSouthChart.ChartType type){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(type == NorthSouthChart.ChartType.TYPE_T_NORTH){ //北向资金今日流向
                    data = new Gson().fromJson(Constacts.dataMap.get("north-t"), NorthSouth.class);
                }else if(type == NorthSouthChart.ChartType.TYPE_T_SOUTH){  //南向资金今日流向
                    data = new Gson().fromJson(Constacts.dataMap.get("north-t"), NorthSouth.class);
                } else if(type == NorthSouthChart.ChartType.TYPE_DW_NORTH){  //北向资金  历史每日/周流向
                    data = new Gson().fromJson(Constacts.dataMap.get("north-d"), NorthSouth.class);
                } else if(type == NorthSouthChart.ChartType.TYPE_DW_SOUTH){  //南向资金  历史每日/周流向
                    data = new Gson().fromJson(Constacts.dataMap.get("north-w"), NorthSouth.class);
                }
                setChartData(data, type);
            }
        }, 500);
    }

    /**设置图表数据*/
    private void setChartData(NorthSouth data, NorthSouthChart.ChartType type){
        chart.setLoading(false);
        chart.setChartType(type);
        //北向资金
        if(type == NorthSouthChart.ChartType.TYPE_T_NORTH || type == NorthSouthChart.ChartType.TYPE_DW_NORTH){
            chart.setFocusLableArray(new String[]{"时间", "净流入金额", "上证指数价格", "上证指数跌涨幅"});
            //南向资金
        }else if(type == NorthSouthChart.ChartType.TYPE_T_SOUTH || type == NorthSouthChart.ChartType.TYPE_DW_SOUTH){
            chart.setFocusLableArray(new String[]{"时间", "净流入金额", "恒生指数价格", "恒生指数跌涨幅"});
        }
        //今日流向
        if(type == NorthSouthChart.ChartType.TYPE_T_NORTH || type == NorthSouthChart.ChartType.TYPE_T_SOUTH){
            chart.setUpDownColor(new int[]{Color.parseColor("#FC4B4B"),
                    Color.parseColor("#1DAA3E")});  //涨跌颜色(红绿)
            //lable 和 color必须按照后台返回数据的顺序设置，比如["0930","1.0亿元","26300.510","+0.91%"]   金额在前，指数在后
            chart.setlableColor(new int[]{Color.parseColor("#DC1010"), Color.parseColor("#FEB271")});
            chart.setYMARK_NUM(5);
            // 历史每日每周
        }else if(type == NorthSouthChart.ChartType.TYPE_DW_NORTH || type == NorthSouthChart.ChartType.TYPE_DW_SOUTH){
            data = new Gson().fromJson(Constacts.dataMap.get("north-d"), NorthSouth.class);
            chart.setlableColor(new int[]{Color.parseColor("#FC4B4B"),
                    Color.parseColor("#1DAA3E"),
                    Color.parseColor("#C9D0DC")});
            chart.setUpDownColor(new int[]{Color.parseColor("#FC4B4B"),
                    Color.parseColor("#1DAA3E")});
            chart.setYMARK_NUM(5);
            chart.setXMARK_NUM(4);
        }
        /**今日流向数据结构一样，历史日周数据结构一样*/
        if(type == NorthSouthChart.ChartType.TYPE_T_NORTH){ //北向资金今日流向
            chart.setlableArray(new String[]{"总资金净流入", "上证指数价格"});
            chart.setLableX(new String[]{"9:30", "11:30/13:00", "15:00"});
        }else if(type == NorthSouthChart.ChartType.TYPE_T_SOUTH){  //南向资金今日流向
            chart.setlableArray(new String[]{"总资金净流入", "恒生指数价格"});
            chart.setLableX(new String[]{"9:30", "12:00/13:00", "16:00"});
        } else if(type == NorthSouthChart.ChartType.TYPE_DW_NORTH){  //北向资金  历史每日/周流向
            chart.setlableArray(new String[]{"净流入金额", "净流出金额", "上证指数价格"});
        } else if(type == NorthSouthChart.ChartType.TYPE_DW_SOUTH){  //南向资金  历史每日/周流向
            chart.setlableArray(new String[]{"净流入金额", "净流出金额", "恒生指数价格"});
        }
        chart.setData(data.getData());
        chart.refresh();
    }


    /**2、模拟接口获取数据*/
    private void getData(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dashboardViewOld.setLoading(false);
                List<DashBoardItem> list = new ArrayList<>();
                int[] colors = new int[]{Color.BLUE,Color.RED,Color.YELLOW};
                for(int i =0;i<3;i++){
                    list.add(new DashBoardItem(colors[i], "lable"+i, i+10));
                }
                dashboardViewOld.setData(list);
                dashboardViewOld.setPro(20);
                dashboardView.setLoading(false);
                dashboardView.setData(10000, 4563);

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

            }
        }, 500);
    }

}
