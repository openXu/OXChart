package com.openxu.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.openxu.cview.xmstock20201030.StandLinesChart;
import com.openxu.cview.xmstock20201030.build.AnimType;
import com.openxu.cview.xmstock20201030.build.AxisLine;
import com.openxu.cview.xmstock20201030.build.AxisLineType;
import com.openxu.cview.xmstock20201030.build.AxisMark;
import com.openxu.cview.xmstock20201030.build.Line;
import com.openxu.cview.xmstock20201030.build.Orientation;
import com.openxu.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class XmStockChartActivity20201030 extends AppCompatActivity {


    StandLinesChart linesChart1, linesChart2, linesChart3, linesChart4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wcxstock_chart20201030);

        linesChart1 = (StandLinesChart)findViewById(R.id.linesChart1);
        linesChart2 = (StandLinesChart)findViewById(R.id.linesChart2);
        linesChart3 = (StandLinesChart)findViewById(R.id.linesChart3);
        linesChart4 = (StandLinesChart)findViewById(R.id.linesChart4);

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
                    for(int i = 100; i<200; i++){
                        datas.add(new LinesData(random.nextInt(10)+i, random.nextInt(30)+i, "2020-"+i));
                    }
                    //绑定
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
                //设置折线
                .line(new Line.Builder<LinesData>(this)
                        .lineColor(Color.RED)
                        .lineWidth(2)
                        .lineType(Line.LineType.CURVE)
                        .orientation(Orientation.LEFT)
                        .animType(AnimType.LEFT_TO_RIGHT)
                        .datas(datas)
                        .field_x("xlable")
                        .field_y("num1").build())
                .line(new Line.Builder(this)
                        .lineColor(Color.BLUE)
                        .lineType(Line.LineType.BROKEN)
                        .orientation(Orientation.RIGHT)
                        .animType(AnimType.BOTTOM_TO_TOP)
                        .datas(datas)
                        .field_x("xlable")
                        .field_y("num2").build())
                //设置x轴刻度
                .xAxisMark(new AxisMark.Builder(this)
                        .showLable(true)
                        .lables(new String[]{"9:00", "10:00", "11:00", "12:00"})
                        .lableOrientation(Orientation.BOTTOM)
                        .datas(datas)
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
                        .lableType(AxisMark.LABLE_TYPE.FLOAT)
                        .datas(datas)
                        .field("num1").build())
                .yRightAxisMark(new AxisMark.Builder(this)
                        .showLable(true)
                        .lableNum(5)
                        .lableOrientation(Orientation.RIGHT)
                        .lableType(AxisMark.LABLE_TYPE.FLOAT)
                        .datas(datas)
                        .field("num2").build())
                //默认坐标轴线水平方向最下方和最上方为实线，其他为虚线；垂直方向最左和最右为实线，中间不画线。
                //可以设置任意一条线，只需要传入指定的index ，并调用设置水平或者垂直线的方法，需要注意index不能超标
                //设置竖直方向上第3根线为红色实线，总共5根线（xAxisMark()确定）
                .verticalAxisLine(2, new AxisLine.Builder(this)
                        .lineType(AxisLineType.DASHE)
                        .lineWidth(DensityUtil.dip2px(this, 1))
                        .lineColor(Color.parseColor("#939393")).build())
                //设置水平方向上第3根线为红色实线，总共5根线（yLeftAxisMark() / yRightAxisMark()确定）
                .horizontalAxisLine(0, new AxisLine.Builder(this)
                        .lineType(AxisLineType.SOLID)
                        .lineWidth(DensityUtil.dip2px(this, 3))
                        .lineColor(Color.parseColor("#939393")).build())
                .horizontalAxisLine(2, new AxisLine.Builder(this)
                        .lineType(AxisLineType.DASHE)
                        .lineWidth(DensityUtil.dip2px(this, 1))
                        .lineColor(Color.RED).build())
                .horizontalAxisLine(3, new AxisLine.Builder(this)
                        .lineType(AxisLineType.NONE).build())
                .build();

        //券商热点与走势对比
        linesChart2.builder()
                //设置折线
                .line(new Line.Builder<LinesData>(this)
                        .lineColor(Color.RED)
                        .lineWidth(2)
                        .lineType(Line.LineType.CURVE)
                        .orientation(Orientation.LEFT)
                        .animType(AnimType.LEFT_TO_RIGHT)
                        .datas(datas)
                        .field_x("xlable")
                        .field_y("num1").build())
                //设置x轴刻度
                .xAxisMark(new AxisMark.Builder(this)
                        .showLable(true)
                        .lableOrientation(Orientation.BOTTOM)
                        .lableNum(2)  //x轴显示2个日期
                        .datas(datas)
                        .field("xlable").build())
                //设置左右y轴刻度
                .yLeftAxisMark(new AxisMark.Builder(this)
                        .showLable(true)
                        .lableNum(3)
                        .lableOrientation(Orientation.LEFT)
                        .lableType(AxisMark.LABLE_TYPE.INTEGER)
                        .datas(datas)
                        .field("num1").build())
//                .yRightAxisMark(new AxisMark.Builder(this)
//                        .showLable(false)
//                        .build())
                //默认坐标轴线水平方向最下方和最上方为实线，其他为虚线；垂直方向最左和最右为实线，中间不画线。
                //可以设置任意一条线，只需要传入指定的index ，并调用设置水平或者垂直线的方法，需要注意index不能超标
                //设置竖直方向上第3根线为红色实线，总共5根线（xAxisMark()确定）
                .verticalAxisLine(0, new AxisLine.Builder(this)
                        .lineType(AxisLineType.NONE).build())
                .verticalAxisLine(1, new AxisLine.Builder(this)
                        .lineType(AxisLineType.NONE).build())
                //设置水平方向上第3根线为红色实线，总共5根线（yLeftAxisMark() / yRightAxisMark()确定）
                .horizontalAxisLine(0, new AxisLine.Builder(this)
                        .lineType(AxisLineType.SOLID)
                        .lineWidth(DensityUtil.dip2px(this, 1))
                        .lineColor(Color.parseColor("#939393")).build())
                .horizontalAxisLine(1, new AxisLine.Builder(this)
                        .lineType(AxisLineType.NONE).build())
                .horizontalAxisLine(2, new AxisLine.Builder(this)
                        .lineType(AxisLineType.NONE).build())
                .build();


        //概念走势图
        linesChart3.builder()
                //设置折线
                .line(new Line.Builder<LinesData>(this)
                        .lineColor(Color.BLUE)
                        .lineWidth(2)
                        .lineType(Line.LineType.CURVE)
                        .orientation(Orientation.LEFT)
                        .animType(AnimType.LEFT_TO_RIGHT)
                        .datas(datas)
                        .field_x("xlable")
                        .field_y("num1").build())
                .line(new Line.Builder(this)
                        .lineColor(Color.RED)
                        .lineType(Line.LineType.BROKEN)
                        .orientation(Orientation.RIGHT)
                        .animType(AnimType.BOTTOM_TO_TOP)
                        .datas(datas)
                        .field_x("xlable")
                        .field_y("num2").build())
                //设置x轴刻度
                .xAxisMark(new AxisMark.Builder(this)
                        .showLable(true)
                        .lableOrientation(Orientation.BOTTOM)
                        .lableNum(5)
                        .datas(datas)
                        .field("xlable").build())
                //设置左右y轴刻度
                .yLeftAxisMark(new AxisMark.Builder(this)
                        .showLable(true)
                        .lableNum(4)
                        .textColor(Color.BLUE)
                        .lableOrientation(Orientation.LEFT)
                        .lableType(AxisMark.LABLE_TYPE.FLOAT)
                        .datas(datas)
                        .field("num1").build())
                .yRightAxisMark(new AxisMark.Builder(this)
                        .showLable(true)
                        .lableNum(4)
                        .textColor(Color.RED)
                        .lableOrientation(Orientation.RIGHT)
                        .lableType(AxisMark.LABLE_TYPE.FLOAT)
                        .datas(datas)
                        .field("num2").build())
                //默认坐标轴线水平方向最下方和最上方为实线，其他为虚线；垂直方向最左和最右为实线，中间不画线。
                //可以设置任意一条线，只需要传入指定的index ，并调用设置水平或者垂直线的方法，需要注意index不能超标
                //设置竖直方向上第3根线为红色实线，总共5根线（xAxisMark()确定）
                .verticalAxisLine(0, new AxisLine.Builder(this)
                        .lineType(AxisLineType.NONE).build())
                .verticalAxisLine(4, new AxisLine.Builder(this)
                        .lineType(AxisLineType.NONE).build())
                //设置水平方向上第3根线为红色实线，总共5根线（yLeftAxisMark() / yRightAxisMark()确定）
                .horizontalAxisLine(0, new AxisLine.Builder(this)
                        .lineType(AxisLineType.DASHE)
                        .lineWidth(DensityUtil.dip2px(this, 1))
                        .lineColor(Color.parseColor("#939393")).build())
                .horizontalAxisLine(1, new AxisLine.Builder(this)
                        .lineType(AxisLineType.DASHE)
                        .lineWidth(DensityUtil.dip2px(this, 1))
                        .lineColor(Color.parseColor("#939393")).build())
                .horizontalAxisLine(2, new AxisLine.Builder(this)
                        .lineType(AxisLineType.DASHE)
                        .lineWidth(DensityUtil.dip2px(this, 1))
                        .lineColor(Color.parseColor("#939393")).build())
                .horizontalAxisLine(3, new AxisLine.Builder(this)
                        .lineType(AxisLineType.DASHE)
                        .lineWidth(DensityUtil.dip2px(this, 1))
                        .lineColor(Color.parseColor("#939393")).build())
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
