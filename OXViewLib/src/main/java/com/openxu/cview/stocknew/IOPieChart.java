package com.openxu.cview.stocknew;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.chart.bean.ChartLable;
import com.openxu.cview.stocknew.bean.BaseChartData;
import com.openxu.cview.xmstock.BaseChart;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * autour : xiami
 * date : 2018/11/28 14:26
 * className : ProgressPieChart
 * version : 1.0
 * description : 收支饼状图表
 */
public class IOPieChart extends BaseChart {


    /**设置的属性*/
    private int centerLableSpace = DensityUtil.dip2px(getContext(), 2);   //中间文字行距
    private int ringWidth = DensityUtil.dip2px(getContext(), 34);
    //饼状图半径
    private int chartRaidus = DensityUtil.dip2px(getContext(), 69);
    private int textSize = (int)getResources().getDimension(R.dimen.text_size_level_mid);     //饼状图占比指示文字大小
    //中间背景色
    private int colorCenterBg = Color.parseColor("#292933");
    //颜色数组
    private int colors[] = {
            Color.parseColor("#ff0000"),
            Color.parseColor("#089c20")
    };
    private int textColors[] = {
            Color.parseColor("#bb080a"),
            Color.parseColor("#067518")
    };

    private List<BaseChartData> dataList = new ArrayList<>();
    private List<ChartLable> lableList;   //需要绘制的lable集合
    private float total;
    protected int startAngle = 100;  //开始的角度

    public IOPieChart(Context context) {
        super(context, null);
    }
    public IOPieChart(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }
    public IOPieChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        onMeasureAfter();
        centerPoint = new PointF(widthSize/2, heightSize/2);
        if(widthSize<heightSize){
            rectChart = new RectF(0,(heightSize-widthSize)/2,widthSize, (heightSize+widthSize)/2);
        }else{
            rectChart = new RectF((widthSize-heightSize)/2, 0, (widthSize+heightSize)/2, heightSize);
        }
    }

    /***********************************设置属性set方法**********************************/
    public void setRingWidth(int ringWidth) {
        this.ringWidth = ringWidth;
    }

    public void setChartRaidus(int chartRaidus) {
        this.chartRaidus = chartRaidus;
    }

    public void setColorCenterBg(int colorCenterBg) {
        this.colorCenterBg = colorCenterBg;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public void setTextColors(int[] textColors) {
        this.textColors = textColors;
    }
/***********************************设置属性set方法**********************************/

    /**
     * 设置数据
     */
    public void setChartData(Class clazz, String per, String name, List<? extends Object> dataList, List<ChartLable> lableList){
        this.lableList = lableList;
        this.dataList.clear();
        total = 0;
        if(dataList!=null){
            try{
                Field filedPer = clazz.getDeclaredField(per);
                Field filedName = clazz.getDeclaredField(name);
                filedPer.setAccessible(true);
                filedName.setAccessible(true);
                for(Object obj : dataList){
                    String perStr = filedPer.get(obj).toString();
                    BaseChartData bean = new BaseChartData((String)filedName.get(obj), Float.parseFloat(perStr));
                    total += bean.getNum();
                    this.dataList.add(bean);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if(centerPoint!=null && centerPoint.x>0){
            startDraw = false;
            invalidate();
        }
    }

/***********************************设置属性set方法over**********************************/
    /**绘制图表基本框架*/
    @Override
    public void drawDefult(Canvas canvas) {
    }
    /**绘制debug辅助线*/
    @Override
    public void drawDebug(Canvas canvas) {
        super.drawDebug(canvas);
    }

    /**创建动画*/
    @Override
    protected ValueAnimator initAnim() {
        anim = ValueAnimator.ofObject(new AngleEvaluator(), 0f, 1.0f);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        return anim;
    }
    private float animPro;       //动画计算的占比数量
    /**动画值变化之后计算数据*/
    @Override
    protected void evaluatorData(ValueAnimator animation) {
        animPro = (float) animation.getAnimatedValue();
    }

    /**绘制图表*/
    @Override
    public void drawChart(Canvas canvas) {
        float oneStartAngle = startAngle;

        RectF arcRect = new RectF(centerPoint.x - chartRaidus,
                centerPoint.y - chartRaidus,
                centerPoint.x + chartRaidus,
                centerPoint.y + chartRaidus);
        int lineLenth = DensityUtil.dip2px(getContext(), 8);
        paintLabel.setTextSize(textSize);
        float lableHeight = FontUtil.getFontHeight(paintLabel);
        float lableLeading = FontUtil.getFontLeading(paintLabel);

        for(int i = 0; i < dataList.size(); i++){
            BaseChartData bean = dataList.get(i);
            paint.setColor(colors[i]);
            paintLabel.setColor(textColors[i]);
            paint.setTypeface(Typeface.DEFAULT);
            paintLabel.setTypeface(Typeface.DEFAULT);

            /**1、绘制空心扇形*/
            paint.setStyle(Paint.Style.FILL);//设置实心
            float percentage = (bean.getNum()==0?0:(360.0f / total * bean.getNum())*animPro);
            canvas.drawArc(arcRect, oneStartAngle, percentage, true, paint);
            /**2、绘制直线*/
            float lineAngle = oneStartAngle + 15; //第一条数据，直线角度为开始角度+20度
            if(i == 1)
                lineAngle = oneStartAngle + percentage - 30;
            boolean isRight = !(lineAngle > 90 && lineAngle < 270);
            //确定直线的起始和结束的点的位置
            float startX = centerPoint.x + (float) ((chartRaidus - ringWidth/2) * Math.cos(Math.toRadians(lineAngle)));
            float startY = centerPoint.y + (float) ((chartRaidus - ringWidth/2) * Math.sin(Math.toRadians(lineAngle)));
            //绘制小圆点
            paint.setColor(textColors[i]);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(DensityUtil.dip2px(getContext(),1));
            canvas.drawCircle(startX,startY,DensityUtil.dip2px(getContext(),2), paint);
            //绘制拐角线
            canvas.drawLine(startX, startY, isRight?startX+lineLenth:startX-lineLenth, startY+lineLenth, paint);
            startX = isRight?startX+lineLenth:startX-lineLenth;
            startY = startY+lineLenth;
            float lableLength = FontUtil.getFontlength(paintLabel, dataList.get(i).getName());
            float endX = isRight? startX+DensityUtil.dip2px(getContext(), 20) + lableLength:
                    startX-DensityUtil.dip2px(getContext(), 20) - lableLength;
            canvas.drawLine(startX, startY, endX, startY, paint);
            /*绘制指示标签*/
            canvas.drawText(dataList.get(i).getName(),
                    isRight?endX-lableLength:endX,
                    startY - 4 - lableHeight + lableLeading, paintLabel);

            /*开始角度累加*/
            oneStartAngle += percentage;
        }

        //绘制中心内圆
        if(ringWidth>0){
            paint.setColor(colorCenterBg);
            paint.setStyle(Paint.Style.FILL);//设置实心
            canvas.drawCircle(centerPoint.x, centerPoint.y, chartRaidus-ringWidth, paint);
        }

        /**绘制中间文字*/
        if(ringWidth>0 && lableList!=null &&lableList.size()>0){
            float textAllHeight = 0;
            float textW, textH, textL;
            for(ChartLable lable : lableList){
                paintLabel.setTextSize(lable.getTextSize());
                textH = FontUtil.getFontHeight(paintLabel);
                textAllHeight += (textH+centerLableSpace);
            }
            textAllHeight -= centerLableSpace;
            int top = (int)(centerPoint.y-textAllHeight/2);
            for(ChartLable lable : lableList){
                paintLabel.setColor(lable.getTextColor());
                paintLabel.setTextSize(lable.getTextSize());
                textW = FontUtil.getFontlength(paintLabel, lable.getText());
                textH = FontUtil.getFontHeight(paintLabel);
                textL = FontUtil.getFontLeading(paintLabel);
                canvas.drawText(lable.getText(), centerPoint.x-textW/2, top + textL, paintLabel);
                top += (textH+centerLableSpace);
            }
        }

    }
}
