package com.openxu.chart;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.openxu.chart.bar.Bar;
import com.openxu.chart.bar.BarChart;
import com.openxu.chart.bar.DataTransform;
import com.openxu.chart.element.XAxisMark;
import com.openxu.chart.element.YAxisMark;
import com.openxu.cview.xmstock20201030.SyzsLinesChart;
import com.openxu.cview.xmstock20201030.bean.CalendarDataStock;

import java.util.ArrayList;
import java.util.List;

public class FpcActivity extends AppCompatActivity {

    private String TAG = "FpcActivity";
    BarChart bartChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpc_chart);
        bartChart = (BarChart)findViewById(R.id.bartChart);
        bartChart.setYAxisMark(new YAxisMark.Builder(this).lableNum(6).build());
        bartChart.setXAxisMark(new XAxisMark.Builder(this).lableNum(4).build());

        getData();
    }


    class BarData{

        public String key;
        public float value;

        public BarData(String key, float value) {
            this.key = key;
            this.value = value;
        }
    }

    private void getData(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<List<BarData>> datas = new ArrayList<>();
                for(int i = 0; i < 10; i++){
                    List<BarData> group = new ArrayList<>();
                    for(int j = 0; j < 3; j++){
                        group.add(new BarData("组"+i, i*j));
                    }
                    datas.add(group);
                }


                bartChart.setData(new DataTransform<List<List<BarData>>>() {
                    @Override
                    public List<List<Bar>> transform(List<List<BarData>> datas) {
                        List<List<Bar>> lists = new ArrayList<>();
                        for(List<BarData> datagroup : datas){
                            List<Bar> bars = new ArrayList<>();
                            lists.add(bars);
                            for(BarData data : datagroup)
                                bars.add(new Bar(data.key, data.value));
                        }
                        return lists;
                    }
                }.transform(datas));
            }
        },2000);

    }





}
