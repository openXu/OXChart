package com.openxu.hkchart;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.openxu.cview.xmstock20201030.GlzsLinesChart;
import com.openxu.cview.xmstock20201030.MyCalendar;
import com.openxu.cview.xmstock20201030.ProgressBar;
import com.openxu.cview.xmstock20201030.QsrdLinesChart;
import com.openxu.cview.xmstock20201030.SshqLinesChart;
import com.openxu.cview.xmstock20201030.StandLinesChart;
import com.openxu.cview.xmstock20201030.bean.CalandarList;
import com.openxu.cview.xmstock20201030.bean.CalendarData;
import com.openxu.cview.xmstock20201030.bean.CalendarDetail;
import com.openxu.cview.xmstock20201030.bean.HotDetail;
import com.openxu.cview.xmstock20201030.bean.TopDetail;
import com.openxu.cview.xmstock20201030.bean.Constacts;
import com.openxu.cview.xmstock20201030.build.AnimType;
import com.openxu.cview.xmstock20201030.build.AxisLine;
import com.openxu.cview.xmstock20201030.build.AxisLineType;
import com.openxu.cview.xmstock20201030.build.AxisMark;
import com.openxu.cview.xmstock20201030.build.Line;
import com.openxu.cview.xmstock20201030.build.Orientation;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.LogUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

public class XmStockChartActivity20201030 extends AppCompatActivity {


    StandLinesChart linesChart1;
//    CustomCalendar cal;
    MyCalendar calendar;
    ProgressBar progress1, progress2, progress3, progress4;
    //1. 券商热点走势对比图
    QsrdLinesChart qsrdLinesChart;
    //2. 概念走势图
    GlzsLinesChart glzsLinesChart;
    //3. 实时行情图
    SshqLinesChart sshqLinesChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wcxstock_chart20201030);
        calendar = (MyCalendar)findViewById(R.id.calendar);
        linesChart1 = (StandLinesChart)findViewById(R.id.linesChart1);

        progress1 = (ProgressBar) findViewById(R.id.progress1);
        progress2 = (ProgressBar) findViewById(R.id.progress2);
        progress3 = (ProgressBar) findViewById(R.id.progress3);
        progress4 = (ProgressBar) findViewById(R.id.progress4);
        progress1.setData(100, 0);
        progress2.setData(100, 50);
        progress3.setData(100, 90);
        progress4.setData(100, 100);

        qsrdLinesChart = (QsrdLinesChart) findViewById(R.id.qsrdLinesChart);
        glzsLinesChart = (GlzsLinesChart) findViewById(R.id.glzsLinesChart);
        sshqLinesChart = (SshqLinesChart) findViewById(R.id.sshqLinesChart);

//        cal = (CustomCalendar)findViewById(R.id.cal);
        //TODO 模拟请求当月数据
       /* final List<DayFinish> list = new ArrayList<>();
        list.add(new DayFinish(1,2,2));
        list.add(new DayFinish(2,1,2));
        list.add(new DayFinish(3,0,2));
        list.add(new DayFinish(4,2,2));
        list.add(new DayFinish(5,2,2));
        list.add(new DayFinish(6,2,2));
        list.add(new DayFinish(7,2,2));
        list.add(new DayFinish(8,0,2));
        list.add(new DayFinish(9,1,2));
        list.add(new DayFinish(10,2,2));
        list.add(new DayFinish(11,5,2));
        list.add(new DayFinish(12,2,2));
        list.add(new DayFinish(13,2,2));
        list.add(new DayFinish(14,3,2));
        list.add(new DayFinish(15,2,2));
        list.add(new DayFinish(16,1,2));
        list.add(new DayFinish(17,0,2));
        list.add(new DayFinish(18,2,2));
        list.add(new DayFinish(19,2,2));
        list.add(new DayFinish(20,0,2));
        list.add(new DayFinish(21,2,2));
        list.add(new DayFinish(22,1,2));
        list.add(new DayFinish(23,2,0));
        list.add(new DayFinish(24,0,2));
        list.add(new DayFinish(25,2,2));
        list.add(new DayFinish(26,2,2));
        list.add(new DayFinish(27,2,2));
        list.add(new DayFinish(28,2,2));
        list.add(new DayFinish(29,2,2));
        list.add(new DayFinish(30,2,2));
        list.add(new DayFinish(31,2,2));

        cal.setRenwu("2017年1月", list);
        cal.setOnClickListener(new CustomCalendar.onClickListener() {
            @Override
            public void onLeftRowClick() {
                Toast.makeText(XmStockChartActivity20201030.this, "点击减箭头", Toast.LENGTH_SHORT).show();
                cal.monthChange(-1);
                new Thread(){
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(1000);
                            XmStockChartActivity20201030.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cal.setRenwu(list);
                                }
                            });
                        }catch (Exception e){
                        }
                    }
                }.start();
            }

            @Override
            public void onRightRowClick() {
                Toast.makeText(XmStockChartActivity20201030.this, "点击加箭头", Toast.LENGTH_SHORT).show();
                cal.monthChange(1);
                new Thread(){
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(1000);
                            XmStockChartActivity20201030.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cal.setRenwu(list);
                                }
                            });
                        }catch (Exception e){
                        }
                    }
                }.start();
            }

            @Override
            public void onTitleClick(String monthStr, Date month) {
                Toast.makeText(XmStockChartActivity20201030.this, "点击了标题："+monthStr, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWeekClick(int weekIndex, String weekStr) {
                Toast.makeText(XmStockChartActivity20201030.this, "点击了星期："+weekStr, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDayClick(int day, String dayStr, DayFinish finish) {
                Toast.makeText(XmStockChartActivity20201030.this, "点击了日期："+dayStr, Toast.LENGTH_SHORT).show();
                Log.w("", "点击了日期:"+dayStr);
            }
        });
*/
        getData();

    }

    private void getData(){
        //1. 日历

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    CalandarList calandarList = new Gson().fromJson(Constacts.data4, CalandarList.class);
                    //设置监听
                    calendar.setItemClickListener(new MyCalendar.ItemClickListener() {
                        @Override
                        public void onDayClick(CalendarData calendar, List<CalendarDetail> details) {
                            String str = "点击的日期为"+calendar.getDate()+"\n"+"股票信息数量"+details.size();
                            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                            Log.w("XmStockChartActivity", str);

                            Intent intent = new Intent(XmStockChartActivity20201030.this, XmStockChartActivity202010301.class);
                            intent.putExtra("stock", details.get(0).getStock_list().get(0));
                            startActivity(intent);
                        }
                    });
                    //绑定数据
                    calendar.setData(calandarList.getData().getCalendar(), calandarList.getData().getCalendar_detail());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);


        //1. 券商热点与走势对比
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                JSONObject json = null;
                try {
//                    json = new JSONObject(Constacts.data);
//                    Gson gson = new Gson();
//                    String dataStr = json.getString("data");
                    TopDetail topDetail = new Gson().fromJson(Constacts.data1, TopDetail.class);
                    //触摸回调
                    qsrdLinesChart.setOnFocusChangeListener(new QsrdLinesChart.OnFocusChangeListener() {
                        @Override
                        public void onfocus(QsrdLinesChart.FocusData focusData) {
                            //手指触摸后回调焦点数据，
                            //focusData.getData() --> ["2020-09-30", 2245.38]
                            //focusData.getNews() --> {"title":"国办:协调推动智能路网设施建设","publishdate":"2020-11-03 08:00:00"}
                            LogUtil.v(getClass().getSimpleName(), "焦点数据："+focusData.getData().get(0)+":"+focusData.getData().get(1));
                            if(focusData.getNews()==null){
                                LogUtil.w(getClass().getSimpleName(), "--------当天没有新闻");
                            }else{
                                Toast.makeText(getApplicationContext(), "焦点新闻："+focusData.getNews().getPublishdate()+
                                        " "+focusData.getNews().getTitle(), Toast.LENGTH_SHORT).show();
                                LogUtil.e(getClass().getSimpleName(), "========焦点新闻："+focusData.getNews().getTitle());
                            }
                        }
                    });
                    //绑定数据
                    qsrdLinesChart.setData(topDetail.getData().getTrend_line(), topDetail.getData().getNews());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);


        //2. 概念走势图
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                JSONObject json = null;
                try {
                    HotDetail hotDetail = new Gson().fromJson(Constacts.data2, HotDetail.class);
                    //概念走势图， 每个元素表示：时间、概念、热度 [20200803,"1582.08","30.00"],
                    glzsLinesChart.setData(hotDetail.getData().getTrend_line());

                    //实时行情
                    sshqLinesChart.setData(hotDetail.getData().getReal_trend_line());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);


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
                        .lineType(Line.LineType.CURVE)
                        .orientation(Orientation.LEFT)
//                        .orientation(Orientation.RIGHT)
                        .animType(AnimType.NONE)  //BOTTOM_TO_TOP
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
