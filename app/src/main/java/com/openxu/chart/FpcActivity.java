package com.openxu.chart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.openxu.chart.bar.BarChart;
import com.openxu.cview.xmstock20201030.SyzsLinesChart;
import com.openxu.cview.xmstock20201030.bean.CalendarDataStock;

public class FpcActivity extends AppCompatActivity {

    private String TAG = "FpcActivity";
    BarChart bartChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpc_chart);
        bartChart = (BarChart)findViewById(R.id.bartChart);
        getData();
    }

    private void getData(){


    }





}
