package com.openxu.cview.stocknew;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.stocknew.bean.BaseChartData;
import com.openxu.cview.stocknew.bean.BranchChartData;
import com.openxu.cview.xmstock.BaseChart;
import com.openxu.cview.xmstock.LinesChart;
import com.openxu.cview.xmstock.bean.DataPoint;
import com.openxu.cview.xmstock.bean.FocusInfo;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;
import com.openxu.utils.NumberFormatUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * autour : xiami
 * date : 2018/12/6 14:26
 * className : BarLineChart
 * version : 1.0
 * description : 公司业绩柱状折线图
 */
public class BarLineChart extends BaseChart {

    //设置数据
    private List<BaseChartData> dataList = new ArrayList<>();

    /**可以设置的属性*/
    //设置Y轴刻度数量
    private int YMARK_NUM =  5;
    private int XMARK_NUM =  5;
    private LinesChart.YMARK_TYPE yMarkType = LinesChart.YMARK_TYPE.INTEGER;
    private int lineColor = Color.parseColor("#ff7200");
    private int barColor = Color.parseColor("#7ecef4");
    private int lineSize = DensityUtil.dip2px(getContext(), 1.5f);
    private int barSize;
    private int barSpace = DensityUtil.dip2px(getContext(), 1.5f);
    //设置曲线粗细
    //设置坐标文字大小
    private int textSize = (int)getResources().getDimension(R.dimen.ts_chart_xy);
    //设置坐标文字颜色
    private int textColor = getResources().getColor(R.color.tc_chart_xy);
    //设置X坐标字体与横轴的距离
    private int textSpaceX = DensityUtil.dip2px(getContext(), 5);
    //设置Y坐标字体与横轴的距离
    private int textSpaceY = DensityUtil.dip2px(getContext(), 3);

    /**需要计算相关值*/
    private float lableLead, lableHeight;
    /*字体绘制相关*/
    private float YMARK =  1;    //Y轴刻度间隔
    private float YMARK_MAX =  Float.MIN_VALUE;    //Y轴刻度最大值
    private float YMARK_MIN =  Float.MAX_VALUE;    //Y轴刻度最小值


    public BarLineChart(Context context) {
        this(context, null);
    }
    public BarLineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public BarLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
    }

    /***********************************设置属性set方法**********************************/

    /***********************************设置属性set方法over**********************************/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        onMeasureAfter();
        evaluatorByData();
        invalidate();
    }


    /***************************************************/

    /***************************************************/

    /**
     * 设置数据
     */
    public void setData(List<BaseChartData> dataList){
        if(null==dataList)
            return;
        this.dataList.clear();
        this.dataList.addAll(dataList);

        if(getMeasuredWidth()>0) {
            evaluatorByData();
            startDraw = false;
            invalidate();
        }
    }
    /**设置数据后，计算相关值*/
    private void evaluatorByData(){
        if(dataList.size()<=0)
            return;
        int count = dataList.size();
        BaseChartData data;
        int step = 1;
        int startIndex = 0;
        if(count>XMARK_NUM){
            step = count/XMARK_NUM;
            startIndex = (count%XMARK_NUM)/2;
        }else{
            XMARK_NUM = count;
        }
        LogUtil.w(TAG, "总共："+count+"条数据，X轴标签"+XMARK_NUM+"个， 步长"+step+" 开始索引："+startIndex);


    }

    /**绘制图表基本框架*/
    @Override
    public void drawDefult(Canvas canvas) {
    }

    /**绘制debug辅助线*/
    @Override
    public void drawDebug(Canvas canvas) {
        super.drawDebug(canvas);
    }

    /**绘制图表*/
    @Override
    public void drawChart(Canvas canvas) {
        if(null==dataList || dataList.size()<=0)
            return;

        for(int i = 0; i<dataList.size(); i++){

        }
    }


    private float animPro;       //动画计算的占比数量
    /**创建动画*/
    @Override
    protected ValueAnimator initAnim() {
        if(dataList.size()>0) {
            ValueAnimator anim = ValueAnimator.ofObject(new AngleEvaluator(), 0f, 1f);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            return anim;
        }
        return null;
    }
    /**动画值变化之后计算数据*/
    @Override
    protected void evaluatorData(ValueAnimator animation) {
        animPro = (float)animation.getAnimatedValue();
    }


}
