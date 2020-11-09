package com.openxu.chart;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.openxu.chart.bar.Bar;
import com.openxu.chart.bar.BarChart;
import com.openxu.chart.element.DataTransform;
import com.openxu.chart.element.XAxisMark;
import com.openxu.chart.element.YAxisMark;
import com.openxu.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

public class FpcActivity extends AppCompatActivity {

    private String TAG = "FpcActivity";
    BarChart bartChart1, bartChart2,bartChart3, bartChart4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpc_chart);
        //Android自带的主题有些会有个纯色背景，如果我们程序有自己的背景，那么这个window的背景是不需要的。去掉window的背景可以在onCreate()中setContentView()之后调用
//        getWindow().setBackgroundDrawable(null);
        bartChart1 = (BarChart)findViewById(R.id.bartChart1);
        if(bartChart1.getVisibility() == View.VISIBLE) {
            bartChart1.setYAxisMark(new YAxisMark.Builder(this).lableNum(6).markType(YAxisMark.MarkType.Integer).build());
            bartChart1.setXAxisMark(new XAxisMark.Builder(this).lableNum(4).build());
            bartChart1.setScrollAble(false);
        }

        bartChart2 = (BarChart)findViewById(R.id.bartChart2);
        if(bartChart2.getVisibility() == View.VISIBLE) {
            bartChart2.setYAxisMark(new YAxisMark.Builder(this).lableNum(6).markType(YAxisMark.MarkType.Integer).build());
            bartChart2.setXAxisMark(new XAxisMark.Builder(this).lableNum(4).build());
            bartChart2.setBarWidth(DensityUtil.dip2px(this, 16));
            bartChart2.setBarSpace(DensityUtil.dip2px(this, 5));
            bartChart2.setGroupSpace(DensityUtil.dip2px(this, 30));
            bartChart2.setScrollAble(true);
            bartChart2.setShowBegin(true);
        }
        bartChart3 = (BarChart)findViewById(R.id.bartChart3);
        if(bartChart3.getVisibility() == View.VISIBLE) {
            bartChart3.setYAxisMark(new YAxisMark.Builder(this).lableNum(6).markType(YAxisMark.MarkType.Integer).build());
            bartChart3.setXAxisMark(new XAxisMark.Builder(this).lableNum(4).build());
            bartChart3.setScrollAble(true);
            bartChart3.setShowBegin(false);
        }
        bartChart4 = (BarChart)findViewById(R.id.bartChart4);
        if(bartChart4.getVisibility() == View.VISIBLE) {
            bartChart4.setYAxisMark(new YAxisMark.Builder(this).lableNum(6).markType(YAxisMark.MarkType.Float).build());
            bartChart4.setXAxisMark(new XAxisMark.Builder(this).lableNum(4).build());
            bartChart4.setScrollAble(false);
        }

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

                DataTransform<List<List<BarData>>, List<List<Bar>>> dataTransform = new DataTransform<List<List<BarData>>,
                        List<List<Bar>>>() {
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
                };
                List<List<BarData>> datas1 = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                    List<BarData> group = new ArrayList<>();
                    for (int j = 1; j < 4; j++) {
                        group.add(new BarData("组" + i, i * j));
                    }
                    datas1.add(group);
                }
                List<List<BarData>> datas2 = new ArrayList<>();
                for (int i = 1; i <= 100; i++) {
                    List<BarData> group = new ArrayList<>();
                    for (int j = 1; j <= 3; j++) {
                        group.add(new BarData("组" + i, i * j));
                    }
                    datas2.add(group);
                }
                if(bartChart1.getVisibility() == View.VISIBLE) {
                    bartChart1.setClickable(true);
                    bartChart1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bartChart1.setData(dataTransform.transform(datas1));
                        }
                    });
                    bartChart1.setData(dataTransform.transform(datas1));
                }
                if(bartChart2.getVisibility() == View.VISIBLE)
                    bartChart2.setData(dataTransform.transform(datas2));
                if(bartChart3.getVisibility() == View.VISIBLE)
                    bartChart3.setData(dataTransform.transform(datas2));
                if(bartChart4.getVisibility() == View.VISIBLE)
                    bartChart4.setData(dataTransform.transform(datas1));
            }
        },2000);

    }





}
