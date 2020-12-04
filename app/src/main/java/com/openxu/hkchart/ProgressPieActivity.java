package com.openxu.hkchart;

import android.graphics.Color;
import android.os.Bundle;

import com.openxu.cview.chart.ProgressPieChart;
import com.openxu.cview.chart.bean.ChartLable;
import com.openxu.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class ProgressPieActivity extends AppCompatActivity {

    private ProgressPieChart chart1, chart2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progresspie);

        chart1 = (ProgressPieChart)findViewById(R.id.chart1);
        chart1.setProSize(DensityUtil.dip2px(this, 5));  //圆环宽度
        chart1.setDebug(false);
        chart1.setLoading(false);
        chart1.setProColor(Color.parseColor("#ff0000"));  //进度颜色
        //环形中间显示的lable
        List<ChartLable> lables = new ArrayList<>();
        lables.add(new ChartLable("60.0%",
                DensityUtil.sp2px(this, 12), Color.parseColor("#ff0000")));
        lables.add(new ChartLable("完成率",
                DensityUtil.sp2px(this, 8), getResources().getColor(R.color.text_color_light_gray)));
        chart1.setData(100, 60, lables);


        lables = new ArrayList<>();
        lables.add(new ChartLable("60.0%",
                DensityUtil.sp2px(this, 12), Color.parseColor("#ff0000")));
        chart2 = (ProgressPieChart)findViewById(R.id.chart2);
        chart2.setProSize(DensityUtil.dip2px(this, 20));  //圆环宽度
        chart2.setDebug(false);
        chart2.setLoading(false);
        chart2.setProColor(Color.parseColor("#F28D02"));  //进度颜色
        chart2.setData(100, 60, lables);


    }


}
