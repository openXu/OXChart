package com.openxu.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.openxu.chart.element.AxisMarkLableType;
import com.openxu.chart.element.DisplayConfig;
import com.openxu.chart.linechart.StandLinesChart;
import com.openxu.chart.element.AnimType;
import com.openxu.chart.element.AxisLine;
import com.openxu.chart.element.AxisLineType;
import com.openxu.chart.element.AxisMark;
import com.openxu.chart.linechart.element.Line;
import com.openxu.chart.element.Orientation;
import com.openxu.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class XmStockChartActivity20201030 extends AppCompatActivity {


    StandLinesChart linesChart1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wcxstock_chart20201030);

        linesChart1 = (StandLinesChart)findViewById(R.id.linesChart1);

        /**1、设置图表*/

        getData();

    }
    /**2、模拟接口获取数据*/
    private void getData(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
//                    JSONObject json = new JSONObject(Constacts.data);
//                    Gson gson = new Gson();
//                    String dataStr = json.getString("data");
                    //data = gson.fromJson(dataStr, ChartData.class);

                    Random random = new Random();
                    List<LinesData> datas = new ArrayList<>();
                    for(int i = 0; i<10; i++){
                        datas.add(new LinesData(i, random.nextInt(5)+i, "2020-"+i));
                    }
                    for(int i = 10; i<16; i++){
                        datas.add(new LinesData(i-random.nextInt(5), random.nextInt(5)-i, "2020-"+i));
                    }
                    for(int i = 16; i<16; i++){
                        datas.add(new LinesData(i+1, random.nextInt(5)-i, "2020-"+i));
                    }
                    //绑定
                    Log.w(getClass().getSimpleName(), "=================绑定数据"+datas.size()+"===============");
                    bindChartData(datas);
                }catch (Exception e){
                    e.printStackTrace();
                }



            }
        }, 500);
    }



    /**3、绑定数据*/
    private void bindChartData(List<LinesData> datas){
        linesChart1.builder()
                .datas(datas)
                .display(new DisplayConfig.Builder()
                        .dataTotal(datas.size())
                        .dataDisplay(datas.size()/2)
                        .displayIndex(1)
                        .build())
                //设置折线
//                .line(new Line.Builder<LinesData>(this)
//                        .lineColor(Color.RED)
//                        .lineWidth(2)
//                        .lineType(Line.LineType.CURVE)
//                        .orientation(Orientation.LEFT)
//                        .animType(AnimType.NONE)
//                        .field_x("xlable")
//                        .field_y("num1").build())
                .line(new Line.Builder(this)
                        .lineColor(Color.BLUE)
                        .lineType(Line.LineType.BROKEN)
                        .orientation(Orientation.LEFT)
//                        .orientation(Orientation.RIGHT)
                        .animType(AnimType.NONE)  //BOTTOM_TO_TOP
                        .field_x("xlable")
                        .field_y("num1").build())
                //设置x轴刻度
                .xAxisMark(new AxisMark.Builder(this)
                        .showLable(true)       //显示刻度lable（默认true）
                        .showMark(true)        //显示刻度线（默认true）
                        //lables() 和 datas()/field() 两者都是设置刻度值，二者中必选一个设置，同时设置时lables()优先级高
//                        .lables(new String[]{"9:00", "10:00", "11:00", "12:00"})  //手动设置坐标刻度值
                        .lableNum(4)
                        .field("xlable").build())
//                .xAxisMark(new AxisMark.Builder(this)
//                        .showLable(true)
//                        .lableNum(5)
//                        .lableOrientation(Orientation.BOTTOM)
//                        .datas(datas)
//                        .field("xlable").build())
                //设置左右y轴刻度
                .yLeftAxisMark(new AxisMark.Builder(this)
                        .showLable(true)
                        .lableNum(5)
                        .lableOrientation(Orientation.TOP)
                        .lableType(AxisMarkLableType.FLOAT)
                        .field("num1").build())
                .yRightAxisMark(new AxisMark.Builder(this)
                        .showLable(true)
                        .lableNum(5)
                        .lableOrientation(Orientation.RIGHT)
                        .lableType(AxisMarkLableType.FLOAT)
                        .field("num2").build())
                //默认坐标轴线水平方向最下方和最上方为实线，其他为虚线；垂直方向最左和最右为实线，中间不画线。
                //可以设置任意一条线，只需要传入指定的index ，并调用设置水平或者垂直线的方法，需要注意index不能超标
                //设置竖直方向上第3根线为红色实线，总共5根线（xAxisMark()确定）
                .verticalAxisLine(2, new AxisLine.Builder(this)
                        .lineType(AxisLineType.DASHE)
                        .lineWidth(DensityUtil.dip2px(this, 1))
                        .lineColor(Color.parseColor("#939393")).build())
                //设置水平方向上第3根线为红色实线，总共5根线（yLeftAxisMark() / yRightAxisMark()确定）
                .horizontalAxisLine(2, new AxisLine.Builder(this)
                        .lineType(AxisLineType.DASHE)
                        .lineWidth(DensityUtil.dip2px(this, 1))
                        .lineColor(Color.RED).build())
                .horizontalAxisLine(3, new AxisLine.Builder(this)
                        .lineType(AxisLineType.NONE).build())
                .build();
    }




    public class LinesData {

        private float num1;
        private float num2;
        private String xlable;

        public LinesData(float num1, float num2, String xlable) {
            this.num1 = num1;
            this.num2 = num2;
            this.xlable = xlable;
        }

        public float getNum1() {
            return num1;
        }

        public void setNum1(float num1) {
            this.num1 = num1;
        }

        public float getNum2() {
            return num2;
        }

        public void setNum2(float num2) {
            this.num2 = num2;
        }

        public String getXlable() {
            return xlable;
        }

        public void setXlable(String xlable) {
            this.xlable = xlable;
        }
    }

}
