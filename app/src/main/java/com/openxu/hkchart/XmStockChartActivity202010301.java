package com.openxu.hkchart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.openxu.cview.xmstock20201030.SyzsLinesChart;
import com.openxu.cview.xmstock20201030.bean.CalendarDataStock;

public class XmStockChartActivity202010301 extends AppCompatActivity {

    private String TAG = "XmStockChartActivity202010301";
    SyzsLinesChart syzsLinesChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wcxstock_chart20201030_syzs);
        SyzsLinesChart   syzsLinesChart = (SyzsLinesChart)findViewById(R.id.syzsLinesChart);
        getData();
    }

    private void getData(){
        CalendarDataStock stock = getIntent().getParcelableExtra("stock");
        syzsLinesChart.setData(stock.getTrend_line());
        Log.w(TAG, "获取传递的股票数据："+stock);


    }





}
