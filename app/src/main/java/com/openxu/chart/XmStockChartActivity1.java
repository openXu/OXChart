package com.openxu.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.openxu.chart.bean.PieBean;
import com.openxu.cview.chart.bean.ChartLable;
import com.openxu.cview.stocknew.BarLineChart;
import com.openxu.cview.stocknew.BranchChart;
import com.openxu.cview.stocknew.DongxiangChart;
import com.openxu.cview.stocknew.HorizontalChart;
import com.openxu.cview.stocknew.IOPieChart;
import com.openxu.cview.stocknew.RadarChart;
import com.openxu.cview.stocknew.ShadowLineChart;
import com.openxu.cview.stocknew.SpeekButton;
import com.openxu.cview.stocknew.bean.BaseChartData;
import com.openxu.cview.stocknew.bean.BranchChartData;
import com.openxu.cview.stocknew.bean.GsyjChartData;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.LogUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class XmStockChartActivity1 extends AppCompatActivity {

    private String TAG = "XmStockChartActivity1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wcxstock_chart1);

        SpeekButton speekBtn = (SpeekButton)findViewById(R.id.speekBtn);
        speekBtn.setListener(new SpeekButton.Listener() {
            @Override
            public void start() {
                LogUtil.i(TAG, "开始录音");
            }
            @Override
            public void stop() {
                LogUtil.i(TAG, "结束录音");
            }
            @Override
            public void progress(int time) {
                LogUtil.i(TAG, "录音时长"+time+"ms");
                if(time > 5000){  //最长录制5s
                    speekBtn.stop();
                }
            }
        });


        //1、雷达图
        RadarChart radarChart = (RadarChart)findViewById(R.id.radarChart);
        radarChart.setLoading(true);
        List<BaseChartData> list = new ArrayList<>();
        list.add(new BaseChartData("资金面", 75));
        list.add(new BaseChartData("基本面", 25));
        list.add(new BaseChartData("行业面", 50));
        list.add(new BaseChartData("技术面", 75));
        list.add(new BaseChartData("消息面", 50));
        //设置最大值
        radarChart.setMaxValue(100);
        radarChart.setLoading(false);
        radarChart.setLineWidth(DensityUtil.dip2px(this, 1f));
        radarChart.setData(list);

        //2、资金流向
        IOPieChart  ioPieChart = (IOPieChart)findViewById(R.id.ioPieChart);
        ioPieChart.setLoading(true);
        //请求数据
        float in = 1756.23f;
        float out = 8745.89f;
        DecimalFormat decimalFormat = new DecimalFormat("0.00%");
        List<PieBean> datalist = new ArrayList<>();
        //注意此处流入要设置为第一个（对应红色），对应的数据需要计算后设置
        datalist.add(new PieBean(in, "流入 "+in+" 占比"+decimalFormat.format(in / (in+out))));
        datalist.add(new PieBean(out, "流出 "+out+" 占比"+decimalFormat.format(out / (in+out))));
        //显示在中间的文字
        List<ChartLable> tableList = new ArrayList<>();
        tableList.add(new ChartLable("净流入", DensityUtil.sp2px(this, 14), Color.WHITE));
        tableList.add(new ChartLable(
                new DecimalFormat("0.00").format(in-out)+"",  //格式化两位小数
                DensityUtil.sp2px(this, 12),
                Color.parseColor("#089c20")));
        //设置中间圆圈背景色(页面背景色一致)
        ioPieChart.setColorCenterBg(Color.parseColor("#292933"));
        ioPieChart.setLoading(false);
        //参数1：数据类型   参数2：数量字段名称   参数3：名称字段   参数4：数据集合   参数5:lable集合
        ioPieChart.setChartData(PieBean.class, "Numner", "Name",datalist ,tableList);

        //3、 市场环境
        HorizontalChart horizontalChart = (HorizontalChart)findViewById(R.id.horizontalChart);
        horizontalChart.setLoading(true);
        List<BaseChartData> list1 = new ArrayList<>();
        list1.add(new BaseChartData("上涨股票", 52));
        list1.add(new BaseChartData("下跌股票", 2801));
        horizontalChart.setLoading(false);
        horizontalChart.setData(list1);

        ShadowLineChart gjzsChart = (ShadowLineChart)findViewById(R.id.gjzsChart);
        gjzsChart.setLoading(true);
        List<BaseChartData> gjzslist = new ArrayList<>();
        gjzslist.add(new BaseChartData("07-01", 1500));
        gjzslist.add(new BaseChartData("07-02", 1700.4f));
        gjzslist.add(new BaseChartData("07-03", 1200));
        gjzslist.add(new BaseChartData("07-04", 1300));
        gjzslist.add(new BaseChartData("07-05", 1500));
        gjzslist.add(new BaseChartData("07-06", 1260));
        gjzslist.add(new BaseChartData("07-07", 1420));
        gjzslist.add(new BaseChartData("07-08", 900.4f));
        gjzslist.add(new BaseChartData("07-09", 2000.4f));
        gjzslist.add(new BaseChartData("07-10", 600));
        gjzslist.add(new BaseChartData("07-11", 700));
        gjzslist.add(new BaseChartData("07-12", 900));
        gjzslist.add(new BaseChartData("07-13", 1100));
        gjzslist.add(new BaseChartData("07-14", 1123));
        gjzslist.add(new BaseChartData("07-15", 789));
        gjzslist.add(new BaseChartData("07-16", 962));
        gjzslist.add(new BaseChartData("07-17", 365.4f));
        gjzslist.add(new BaseChartData("07-18", 536));
        gjzsChart.setLoading(false);
        gjzsChart.setData(gjzslist);
        //4、 主力动向
        DongxiangChart  dongxiangChart = (DongxiangChart)findViewById(R.id.dongxiangChart);
        dongxiangChart.setLoading(true);
        List<BaseChartData> dxlist = new ArrayList<>();
        dxlist.add(new BaseChartData("07-01", 1500));
        dxlist.add(new BaseChartData("07-02", 1500));
        dxlist.add(new BaseChartData("07-03", 1500));
        dxlist.add(new BaseChartData("07-04", 1500));
        dxlist.add(new BaseChartData("07-05", 1500));
        dxlist.add(new BaseChartData("07-06", 150));
        dxlist.add(new BaseChartData("07-07", 110));
        dxlist.add(new BaseChartData("07-08", -100));
        dxlist.add(new BaseChartData("07-09", -1000));
        dxlist.add(new BaseChartData("07-10", 600));
        dxlist.add(new BaseChartData("07-11", -700));
        dxlist.add(new BaseChartData("07-12", -900));
        dxlist.add(new BaseChartData("07-13", 1100));
        dxlist.add(new BaseChartData("07-14", -1123));
        dxlist.add(new BaseChartData("07-15", -789));
        dxlist.add(new BaseChartData("07-16", 962));
        dxlist.add(new BaseChartData("07-17", -365));
        dxlist.add(new BaseChartData("07-18", 536));
        dongxiangChart.setLoading(false);
        dongxiangChart.setData(dxlist);

        /**5、公司业绩*/
        /*营业收入*/
        BarLineChart barLineChart = (BarLineChart)findViewById(R.id.barLineChart);
        barLineChart.setLoading(true);
        barLineChart.setyMarkType(BarLineChart.YMARK_TYPE.INTEGER);  //左侧刻度显示整数
        List<GsyjChartData> yysrlist = new ArrayList<>();
        yysrlist.add(new GsyjChartData("2011", 1500, 0.01f));
        yysrlist.add(new GsyjChartData("2012", 1100, 0.02f));
        yysrlist.add(new GsyjChartData("2013", 600, 0.03f));
        yysrlist.add(new GsyjChartData("2014", 2000, 0.04f));
        yysrlist.add(new GsyjChartData("2015", 1789, 0.05f));
        barLineChart.setLoading(false);
        barLineChart.setData(yysrlist);
        /*基本每股收益*/
        BarLineChart barLineChart1 = (BarLineChart)findViewById(R.id.barLineChart1);
        barLineChart1.setLoading(true);
        barLineChart1.setyMarkType(BarLineChart.YMARK_TYPE.DECIMAL); //左侧刻度显示小数
        List<GsyjChartData> sylist = new ArrayList<>();
        sylist.add(new GsyjChartData("2011", 6.57f, 0.156f));
        sylist.add(new GsyjChartData("2012", 2.65f, 0.06f));
        sylist.add(new GsyjChartData("2013", 5.27f, -0.036f));
        sylist.add(new GsyjChartData("2014", 8.14f, -0.01f));
        sylist.add(new GsyjChartData("2015", 4.25f, 0.05f));
        barLineChart1.setLoading(false);
        barLineChart1.setData(sylist);

        //3、 相关产业
        BranchChart  branchChart = (BranchChart)findViewById(R.id.branchChart);
        branchChart.setLoading(true);
        BranchChartData branchChartData = new BranchChartData();
        branchChartData.setCompany("中国平安");
        List<String> industryList = new ArrayList<>();
        industryList.add("银行");
        industryList.add("信托业务");
        industryList.add("证券业务");
        industryList.add("人身保险业务");
        industryList.add("财产保险业务");
        industryList.add("电子银行业生生世世务啊啊 啊");
        branchChartData.setIndustryList(industryList);
        List<String> companyList = new ArrayList<>();
        companyList.add("中国人寿1");
        companyList.add("中国人寿22");
        companyList.add("中国人寿333");
        companyList.add("中国人寿4444");
        companyList.add("中国人寿55555");
        companyList.add("中国人寿666666");
        companyList.add("中国人寿7777777");
        companyList.add("中国人寿88888888");
        companyList.add("中国人寿999999999");
        branchChartData.setCompanyList(companyList);
        branchChart.setLoading(false);
        branchChart.setData(branchChartData);

    }
    /**3、绑定数据*/
    private void bindChartData(){

    }
}
