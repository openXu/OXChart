package com.openxu.hkchart;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.openxu.cview.xmstock20201030.build.Line;
import com.openxu.hkchart.bar.Bar;
import com.openxu.hkchart.bar.BarChart;
import com.openxu.hkchart.bar.HBar;
import com.openxu.hkchart.bar.HorizontalBarChart;
import com.openxu.hkchart.bar.MultipartBarChart;
import com.openxu.hkchart.bar.MultipartBarData;
import com.openxu.hkchart.config.DisplayScheme;
import com.openxu.hkchart.config.MultipartBarConfig;
import com.openxu.hkchart.element.DataTransform;
import com.openxu.hkchart.element.FocusPanelText;
import com.openxu.hkchart.element.MarkType;
import com.openxu.hkchart.element.XAxisMark;
import com.openxu.hkchart.element.YAxisMark;
import com.openxu.hkchart.line.LineChart;
import com.openxu.hkchart.line.LinePoint;
import com.openxu.hkchart.view.EchartOptionUtil;
import com.openxu.hkchart.view.EchartView;
import com.openxu.utils.DensityUtil;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

public class FpcActivity extends AppCompatActivity {

    private String TAG = "FpcActivity";

    WebView webview;
    private EchartView lineChart;
    LineChart lineChart1, lineChart2, lineChart3;
    HorizontalBarChart horizontalBarChart;
    MultipartBarChart multipartBarChart, multipartBarChart1, multipartBarChart2, multipartBarChart3;
    BarChart bartChart1, bartChart2,bartChart3, bartChart4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpc_chart);
        //Android自带的主题有些会有个纯色背景，如果我们程序有自己的背景，那么这个window的背景是不需要的。去掉window的背景可以在onCreate()中setContentView()之后调用
//        getWindow().setBackgroundDrawable(null);

        webview = (WebView)findViewById(R.id.webview);
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setJavaScriptEnabled(true);
        /**
         * js方法的调用必须在html页面加载完成之后才能调用。
         * 用webview加载html还是需要耗时间的，必须等待加载完，在执行代用js方法的代码。
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(2000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();
        webview.loadUrl("file:///android_asset/echarts1.html");

        lineChart = (EchartView)findViewById(R.id.lineChart);
        lineChart.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //最好在h5页面加载完毕后再加载数据，防止html的标签还未加载完成，不能正常显示
                Object[] x = new Object[]{
                        "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
                };
                Object[] y = new Object[]{
                        820, 932, 901, 934, 1290, 1330, 1320
                };
                lineChart.refreshEchartsWithOption(EchartOptionUtil.getLineChartOptions(x, y));
            }
        });

        lineChart1 = (LineChart)findViewById(R.id.lineChart1);
        if(lineChart1.getVisibility() == View.VISIBLE) {
            lineChart1.setYAxisMark(new YAxisMark.Builder(this).lableNum(6).markType(MarkType.Integer).unit("(KW)").build());
            lineChart1.setXAxisMark(new XAxisMark.Builder(this)
                    .lableNum(5)
//                    .lables(new String[]{"00:00", "03:00", "06:00", "09:00", "12:00", "15:00", "18:00", "21:00", "24:00"})
                    .build());
            lineChart1.setShowAnim(true);   //是否显示动画
            lineChart1.setShowBegin(false);  //是否从开头显示，默认true
            lineChart1.setScaleAble(true);   //是否支持缩放
            lineChart1.setPageShowNum(100);  //默认一页显示的数据量
            lineChart1.setLineType(LineChart.LineType.CURVE);   //曲线图
            lineChart1.setLineColor(new int[]{
                    Color.parseColor("#000000"),
                    Color.parseColor("#3cd595"),
                    Color.parseColor("#4d7bff"),
                    Color.parseColor("#4d7bff")});
            //设置焦点面板上的文字信息
            lineChart1.setFocusPanelText(new FocusPanelText[]{
                    new FocusPanelText(true,
                            DensityUtil.sp2px(this, 12),
                            Color.parseColor("#000000"),
                            ""),
                    new FocusPanelText(true,
                            DensityUtil.sp2px(this, 10),
                            Color.parseColor("#333333"),
                            "零序电流："),
                    new FocusPanelText(true,
                            DensityUtil.sp2px(this, 10),
                            Color.parseColor("#333333"),
                            "222222A相电流："),
                    new FocusPanelText(true,
                            DensityUtil.sp2px(this, 10),
                            Color.parseColor("#333333"),
                            "B相电流：")
            });
        }
        lineChart2 = (LineChart)findViewById(R.id.lineChart2);
        if(lineChart2.getVisibility() == View.VISIBLE) {
            lineChart2.setYAxisMark(new YAxisMark.Builder(this).lableNum(6).markType(MarkType.Integer).build());
            lineChart2.setXAxisMark(new XAxisMark.Builder(this).lableNum(4).build());
        }
        lineChart3 = (LineChart)findViewById(R.id.lineChart3);
        if(lineChart3.getVisibility() == View.VISIBLE) {
            lineChart3.setYAxisMark(new YAxisMark.Builder(this).lableNum(6).markType(MarkType.Integer).build());
            lineChart3.setXAxisMark(new XAxisMark.Builder(this).lableNum(4).build());
        }
        horizontalBarChart = (HorizontalBarChart)findViewById(R.id.horizontalBarChart);
        Typeface typeface = Typeface.createFromAsset(getResources().getAssets(), "hk_number_text_type.ttf");
        horizontalBarChart.setYAxisMark(new YAxisMark.Builder(this)
                .lableNum(5)
                .numberTypeface(typeface)
                .markType(MarkType.Float)
                .unit("元")
                .build());
        horizontalBarChart.setXAxisMark(new XAxisMark.Builder(this)
                .textSize(DensityUtil.sp2px(this, 12))
                .build());


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                multipartBarChart = findViewById(R.id.multipartBarChart);
                multipartBarChart1 = findViewById(R.id.multipartBarChart1);
                multipartBarChart2 = findViewById(R.id.multipartBarChart2);
                multipartBarChart3 = findViewById(R.id.multipartBarChart3);
                MultipartBarConfig config = new MultipartBarConfig();
                config.setDisplayScheme(DisplayScheme.SHOW_ALL);
                config.setBarWidth(DensityUtil.dip2px(FpcActivity.this, 20));
                config.setSpacingRatio(1.5f);
                multipartBarChart.setDisplayConfig(config);
                multipartBarChart1.setDisplayConfig(config);
                config = new MultipartBarConfig();
                config.setDisplayScheme(DisplayScheme.SHOW_BEGIN);
                config.setBarWidth(DensityUtil.dip2px(FpcActivity.this, 15));
                config.setSpacingRatio(0.5f);
                multipartBarChart2.setDisplayConfig(config);
                config = new MultipartBarConfig();
                config.setDisplayScheme(DisplayScheme.SHOW_END);
                config.setBarWidth(DensityUtil.dip2px(FpcActivity.this, 15));
                config.setSpacingRatio(0.5f);
                multipartBarChart3.setDisplayConfig(config);
                YAxisMark yAxisMark = new YAxisMark.Builder(FpcActivity.this)
                        .lableNum(5)   //y刻度数量
                        .numberTypeface(typeface)
                        .markType(MarkType.Float)
                        .digits(1)
                        .unit("元")
                        .build();
                XAxisMark xAxisMark = new XAxisMark.Builder(FpcActivity.this)
                        .lableNum(10)   //最多显示的x刻度，并非一定显示10个，图表会自动根据x刻度文字长短适配
                        .textSize(DensityUtil.sp2px(FpcActivity.this, 12))
                        .build();
                multipartBarChart.setYAxisMark(yAxisMark);
                multipartBarChart.setXAxisMark(xAxisMark);
                multipartBarChart.setShowAnim(true);
                multipartBarChart1.setYAxisMark(yAxisMark);
                multipartBarChart1.setXAxisMark(xAxisMark);
                multipartBarChart1.setShowAnim(true);
                multipartBarChart2.setYAxisMark(yAxisMark);
                multipartBarChart2.setXAxisMark(xAxisMark);
                multipartBarChart2.setShowAnim(false);
                multipartBarChart3.setYAxisMark(yAxisMark);
                multipartBarChart3.setXAxisMark(xAxisMark);
                multipartBarChart3.setShowAnim(false);
                List<MultipartBarData> datas = new ArrayList<>();
                for(int i = 1; i<=3; i++){
                    List<Float> valueys = new ArrayList<>();
                    valueys.add(10f*i);
                    valueys.add(10f*i + 5);
                    valueys.add(10f*i+10);
                    datas.add(new MultipartBarData(valueys,
                            "2010-"+i));
                }
                multipartBarChart.setDatas(datas);
                datas = new ArrayList<>();
                for(int i = 1; i<=10; i++){
                    List<Float> valueys = new ArrayList<>();
                    valueys.add(10f*i);
                    valueys.add(10f*i + 5);
                    valueys.add(10f*i+10);
                    datas.add(new MultipartBarData(valueys,
                            "2010-"+i));
                }
                for(int i = 11; i>0; i--){
                    List<Float> valueys = new ArrayList<>();
                    valueys.add(10f*i);
                    valueys.add(10f*i + 5);
                    valueys.add(10f*i+10);
                    datas.add(new MultipartBarData(valueys,
                            "2010-"+i));
                }
                multipartBarChart1.setDatas(datas);
                multipartBarChart2.setDatas(datas);
                multipartBarChart3.setDatas(datas);
            }
        },1000);

        bartChart1 = (BarChart)findViewById(R.id.bartChart1);
        if(bartChart1.getVisibility() == View.VISIBLE) {
            bartChart1.setYAxisMark(new YAxisMark.Builder(this).lableNum(6).markType(MarkType.Integer).build());
            bartChart1.setXAxisMark(new XAxisMark.Builder(this).lableNum(4).build());
            bartChart1.setScrollAble(false);
        }

        bartChart2 = (BarChart)findViewById(R.id.bartChart2);
        if(bartChart2.getVisibility() == View.VISIBLE) {
            bartChart2.setYAxisMark(new YAxisMark.Builder(this).lableNum(6).markType(MarkType.Integer).build());
            bartChart2.setXAxisMark(new XAxisMark.Builder(this).lableNum(4).build());
            bartChart2.setBarWidth(DensityUtil.dip2px(this, 16));
            bartChart2.setBarSpace(DensityUtil.dip2px(this, 5));
            bartChart2.setGroupSpace(DensityUtil.dip2px(this, 30));
            bartChart2.setScrollAble(true);
            bartChart2.setShowBegin(true);
        }
        bartChart3 = (BarChart)findViewById(R.id.bartChart3);
        if(bartChart3.getVisibility() == View.VISIBLE) {
            bartChart3.setYAxisMark(new YAxisMark.Builder(this).lableNum(6).markType(MarkType.Integer).build());
            bartChart3.setXAxisMark(new XAxisMark.Builder(this).lableNum(4).build());
            bartChart3.setScrollAble(true);
            bartChart3.setShowBegin(false);
        }
        bartChart4 = (BarChart)findViewById(R.id.bartChart4);
        if(bartChart4.getVisibility() == View.VISIBLE) {
            bartChart4.setYAxisMark(new YAxisMark.Builder(this).lableNum(6).markType(MarkType.Float).build());
            bartChart4.setXAxisMark(new XAxisMark.Builder(this).lableNum(4).build());
            bartChart4.setScrollAble(false);
        }
        getLineData();
        getHorizontalBarData();
        getBarData();
    }


    class BarData{

        public String key;
        public float value;

        public BarData(String key, float value) {
            this.key = key;
            this.value = value;
        }
    }

    private void getBarData(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                DataTransform<List<List<BarData>>, List<Bar>> dataTransform = new DataTransform<List<List<BarData>>,
                        List<Bar>>() {
                    @Override
                    public List<Bar> transform(List<List<BarData>> datas) {
                        List<Bar> lists = new ArrayList<>();
                        for(List<BarData> datagroup : datas){
                            List<Float> valuey = new ArrayList<>();
                            Bar bar = new Bar(datagroup.get(0).key, valuey);
                            lists.add(bar);
                            for(BarData data : datagroup)
                                valuey.add(data.value);
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

    private void getHorizontalBarData(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<HBar> lists = new ArrayList<>();
                lists.add(new HBar("电量电费", 100f));
                lists.add(new HBar("基本电费", -30f));
                lists.add(new HBar("力调电费", 25.89f));
                lists.add(new HBar("附加费", 73.1f));
                horizontalBarChart.setData(lists);
            }
        },2000);

    }


    private void getLineData(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DataTransform<LineData, List<List<LinePoint>>> dataTransform = new DataTransform<LineData,
                        List<List<LinePoint>>>() {
                    @Override
                    public List<List<LinePoint>> transform(LineData datas) {
                        List<List<LinePoint>> lines = new ArrayList<>();
                        List<LinePoint> line1 = new ArrayList<>();
                        List<LinePoint> line2 = new ArrayList<>();
                        List<LinePoint> line3 = new ArrayList<>();
                        List<LinePoint> line4 = new ArrayList<>();
                        lines.add(line1);
                        lines.add(line2);
                        lines.add(line3);
                        lines.add(line4);
                        SimpleDateFormat sdf=new SimpleDateFormat("MMdd HH:mm");
                        for(LineOnePoint point : datas.getDataPoints()){
                            //时间格式,HH是24小时制，hh是AM PM12小时制
                            //比如timestamp=1449210225945；  绘制x轴刻度1603564200000
                            String date_string = sdf.format(new Date(Long.valueOf(point.getTimestamp()) * 1000L));
                            line1.add(new LinePoint(date_string, point.getValue()));
                            line2.add(new LinePoint(date_string, point.getValue() + 30));
                            line3.add(new LinePoint(date_string, point.getValue()+20));
                            line4.add(new LinePoint(date_string, point.getValue()+10));
                        }
                        return lines;
                    }
                };
                if(lineChart1.getVisibility() == View.VISIBLE) {
                    JSONObject json = null;
                    try {
                        json = new JSONObject(lineJson);
                        Gson gson = new Gson();
                        String dataStr = json.getString("data");
                        LineData lineData = new Gson().fromJson(dataStr, LineData.class);
                        lineChart1.setData(dataTransform.transform(lineData));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        },2000);

    }

    class LineData{
        List<LineOnePoint> dataPoints;

        public List<LineOnePoint> getDataPoints() {
            return dataPoints;
        }

        public void setDataPoints(List<LineOnePoint> dataPoints) {
            this.dataPoints = dataPoints;
        }
    }

    class LineOnePoint{
        String timestamp;
        Float value;

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public Float getValue() {
            return value;
        }

        public void setValue(Float value) {
            this.value = value;
        }
    }

    private String lineJson = "{" +
            "  \"respCode\": 100," +
            "  \"data\": {" +
            "    \"sampleSize\": 610," +
            "    \"metric\": \"Ua\"," +
            "    \"tags\": {" +
            "      \"Cmd\": [" +
            "        \"Real\"" +
            "      ]," +
            "      \"RealEquipCode\": [" +
            "        \"099-00111\"" +
            "      ]" +
            "    }," +
            "    \"dataPoints\": [" +
            "      {" +
            "        \"timestamp\": 1603518600000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603518900000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603519200000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603519500000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603519800000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603520100000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603520400000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603520700000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603521000000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603521300000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603521600000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603521900000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603522200000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603522500000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603522800000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603523100000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603523400000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603523700000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603524000000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603524300000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603524600000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603524900000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603525200000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603525500000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603525800000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603526100000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603526400000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603526700000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603527000000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603527300000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603527600000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603527900000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603528200000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603528500000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603528800000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603529100000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603529400000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603529700000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603530000000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603530300000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603530600000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603530900000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603531200000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603531500000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603531800000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603532100000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603532400000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603532700000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603533000000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603533300000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603533600000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603533900000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603534200000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603534500000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603534800000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603535100000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603535400000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603535700000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603536000000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603536300000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603536600000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603536900000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603537200000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603537500000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603537800000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603538100000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603538400000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603538700000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603539000000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603539300000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603539600000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603539900000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603540200000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603540500000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603540800000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603541100000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603541400000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603541700000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603542000000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603542300000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603542600000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603542900000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603543200000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603543500000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603543800000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603544100000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603544400000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603544700000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603545000000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603545300000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603545600000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603545900000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603546200000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603546500000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603546800000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603547100000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603547400000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603547700000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603548000000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603548300000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603548600000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603548900000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603549200000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603549500000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603549800000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603550100000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603550400000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603550700000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603551000000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603551300000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603551600000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603551900000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603552200000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603552500000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603552800000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603553100000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603553400000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603553700000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603554000000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603554300000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603554600000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603554900000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603555200000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603555500000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603555800000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603556100000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603556400000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603556700000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603557000000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603557300000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603557600000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603557900000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603558200000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603558500000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603558800000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603559100000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603559400000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603559700000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603560000000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603560300000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603560600000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603560900000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603561200000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603561500000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603561800000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603562100000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603562400000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603562700000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603563000000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603563300000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603563600000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603563900000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603564200000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603564500000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603564800000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603565100000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603565400000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603565700000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603566000000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603566300000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603566600000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603566900000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603567200000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603567500000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603567800000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603568100000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603568400000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603568700000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603569000000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603569300000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603569600000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603569900000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603570200000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603570500000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603570800000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603571100000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603571400000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603571700000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603572000000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603572300000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603572600000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603572900000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603573200000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603573500000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603573800000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603574100000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603574400000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603574700000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603575000000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603575300000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603575600000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603575900000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603576200000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603576500000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603576800000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603577100000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603577400000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603577700000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603578000000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603578300000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603578600000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603578900000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603579200000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603579500000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603579800000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603580100000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603580400000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603580700000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603581000000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603581300000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603581600000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603581900000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603582200000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603582500000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603582800000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603583100000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603583400000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603583700000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603584000000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603584300000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603584600000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603584900000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603585200000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603585500000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603585800000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603586100000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603586400000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603586700000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603587000000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603587300000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603587600000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603587900000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603588200000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603588500000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603588800000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603589100000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603589400000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603589700000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603590000000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603590300000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603590600000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603590900000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603591200000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603591500000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603591800000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603592100000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603592400000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603592700000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603593000000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603593300000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603593600000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603593900000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603594200000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603594500000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603594800000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603595100000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603595400000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603595700000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603596000000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603596300000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603596600000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603596900000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603597200000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603597500000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603597800000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603598100000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603598400000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603598700000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603599000000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603599300000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603599600000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603599900000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603600200000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603600500000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603600800000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603601100000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603601400000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603601700000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603602000000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603602300000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603602600000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603602900000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603603200000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603603500000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603603800000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603604100000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603604400000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603604700000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603605000000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603605300000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603605600000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603605900000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603606200000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603606500000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603606800000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603607100000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603607400000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603607700000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603608000000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603608300000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603608600000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603608900000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603609200000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603609500000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603609800000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603610100000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603610400000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603610700000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603611000000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603611300000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603611600000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603611900000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603612200000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603612500000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603612800000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603613100000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603613400000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603613700000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603614000000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603614300000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603614600000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603614900000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603615200000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603615500000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603615800000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603616100000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603616400000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603616700000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603617000000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603617300000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603617600000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603617900000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603618200000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603618500000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603618800000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603619100000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603619400000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603619700000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603620000000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603620300000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603620600000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603620900000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603621200000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603621500000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603621800000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603622100000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603622400000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603622700000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603623000000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603623300000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603623600000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603623900000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603624200000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603624500000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603624800000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603625100000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603625400000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603625700000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603626000000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603626300000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603626600000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603626900000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603627200000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603627500000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603627800000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603628100000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603628400000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603628700000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603629000000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603629300000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603629600000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603629900000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603630200000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603630500000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603630800000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603631100000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603631400000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603631700000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603632000000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603632300000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603632600000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603632900000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603633200000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603633500000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603633800000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603634100000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603634400000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603634700000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603635000000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603635300000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603635600000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603635900000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603636200000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603636500000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603636800000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603637100000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603637400000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603637700000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603638000000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603638300000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603638600000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603638900000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603639200000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603639500000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603639800000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603640100000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603640400000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603640700000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603641000000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603641300000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603641600000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603641900000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603642200000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603642500000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603642800000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603643100000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603643400000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603643700000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603644000000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603644300000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603644600000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603644900000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603645200000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603645500000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603645800000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603646100000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603646400000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603646700000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603647000000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603647300000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603647600000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603647900000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603648200000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603648500000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603648800000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603649100000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603649400000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603649700000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603650000000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603650300000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603650600000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603650900000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603651200000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603651500000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603651800000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603652100000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603652400000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603652700000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603653000000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603653300000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603653600000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603653900000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603654200000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603654500000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603654800000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603655100000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603655400000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603655700000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603656000000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603656300000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603656600000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603656900000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603657200000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603657500000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603657800000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603658100000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603658400000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603658700000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603659000000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603659300000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603659600000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603659900000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603660200000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603660500000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603660800000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603661100000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603661400000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603661700000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603662000000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603662300000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603662600000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603662900000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603663200000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603663500000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603663800000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603664100000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603664400000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603664700000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603665000000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603665300000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603665600000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603665900000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603666200000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603666500000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603666800000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603667100000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603667400000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603667700000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603668000000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603668300000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603668600000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603668900000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603669200000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603669500000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603669800000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603670100000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603670400000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603670700000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603671000000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603671300000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603671600000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603671900000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603672200000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603672500000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603672800000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603673100000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603673400000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603673700000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603674000000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603674300000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603674600000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603674900000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603675200000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603675500000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603675800000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603676100000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603676400000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603676700000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603677000000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603677300000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603677600000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603677900000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603678200000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603678500000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603678800000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603679100000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603679400000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603679700000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603680000000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603680300000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603680600000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603680900000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603681200000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603681800000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603682100000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603682400000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603682700000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603683000000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603683300000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603683600000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603683900000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603684200000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603684500000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603684800000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603685100000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603685400000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603685700000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603686000000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603686300000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603686600000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603686900000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603687200000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603687500000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603687800000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603688100000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603688400000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603688700000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603689000000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603689300000," +
            "        \"value\": 215.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603689600000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603689900000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603690200000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603690500000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603690800000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603691100000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603691400000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603691700000," +
            "        \"value\": 239.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603692300000," +
            "        \"value\": 235.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603692600000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603692900000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603693200000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603693500000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603693800000," +
            "        \"value\": 200.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603694100000," +
            "        \"value\": 220" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603694400000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603694700000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603695000000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603695300000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603695600000," +
            "        \"value\": 228.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603695900000," +
            "        \"value\": 204.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603696200000," +
            "        \"value\": 224.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603696500000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603696800000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603697100000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603697400000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603698000000," +
            "        \"value\": 206.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603698300000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603698600000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603698900000," +
            "        \"value\": 213.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603699200000," +
            "        \"value\": 233.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603699500000," +
            "        \"value\": 209" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603699800000," +
            "        \"value\": 226.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603700100000," +
            "        \"value\": 202.4" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603700400000," +
            "        \"value\": 222.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603700700000," +
            "        \"value\": 198" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603701000000," +
            "        \"value\": 217.8" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603701300000," +
            "        \"value\": 237.6" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603701600000," +
            "        \"value\": 211.2" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603701900000," +
            "        \"value\": 231" +
            "      }," +
            "      {" +
            "        \"timestamp\": 1603702200000," +
            "        \"value\": 206.8" +
            "      }" +
            "    ]" +
            "  }," +
            "  \"respMsg\": \"操作成功！\"" +
            "}";

}
