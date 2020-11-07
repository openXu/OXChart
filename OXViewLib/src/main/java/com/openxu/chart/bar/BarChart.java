package com.openxu.chart.bar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.openxu.chart.BaseChart;
import com.openxu.chart.element.XAxisMark;
import com.openxu.chart.element.YAxisMark;
import com.openxu.cview.chart.bean.BarBean;
import com.openxu.cview.xmstock20201030.build.Orientation;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;

import java.util.List;

/**
 * autour : openXu
 * date : 2017/7/24 10:46
 * className : BarChart
 * version : 1.0
 * description : 柱状图，支持多柱
 */
public class BarChart extends BaseChart {

    private List<List<Bar>> barData;
    private YAxisMark yAxisMark;
    private XAxisMark xAxisMark;

    private int groupWidth;
    private boolean scroll = true;  //是否支持滚动
    private int scrollXMax, scrollx;      //最大滚动距离

    private boolean showBegin = true;    //当数据超出一屏宽度时，实现最后的数据

    private int barWidth = DensityUtil.dip2px(getContext(), 15);    //柱宽度
    private int barSpace = DensityUtil.dip2px(getContext(), 1);    //双柱间的间距
    private int groupSpace = DensityUtil.dip2px(getContext(), 25);//一组柱之间的间距
    private int[] barColor = new int[]{Color.BLUE, Color.YELLOW, Color.RED};               //柱颜色


    public BarChart(Context context) {
        this(context, null);
    }

    public BarChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
    }



    /***********************************设置属性set方法start**********************************/
    public void setYAxisMark(YAxisMark yAxisMark) {
        this.yAxisMark = yAxisMark;
    }

    public void setXAxisMark(XAxisMark xAxisMark) {
        this.xAxisMark = xAxisMark;
    }
    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
    }
    public void setBarSpace(int barSpace) {
        this.barSpace = barSpace;
    }
    public void setGroupSpace(int groupSpace) {
        this.groupSpace = groupSpace;
    }

    public void setData(List<List<Bar>> barData) {
        this.barData = barData;
        calculate();
        setLoading(false);
    }
    /***********************************设置属性set方法over**********************************/
    private void calculate() {
        paintText.setTextSize(xAxisMark.textSize);
        xAxisMark.textHeight = (int)FontUtil.getFontHeight(paintText);
        xAxisMark.textLead = (int)FontUtil.getFontLeading(paintText);
        //确定图表最下放绘制位置
        rectChart.bottom = getMeasuredHeight() - getPaddingBottom() - xAxisMark.textHeight - xAxisMark.textSpace;
        xAxisMark.drawPointY = rectChart.bottom + xAxisMark.textSpace + xAxisMark.textLead;


        calculateYMark();
        paintText.setTextSize(yAxisMark.textSize);
        yAxisMark.textHeight = FontUtil.getFontHeight(paintText);
        yAxisMark.textLead = FontUtil.getFontLeading(paintText);
        String maxLable = yAxisMark.getMarkText(yAxisMark.cal_mark_max);
        rectChart.left =  (int)(getPaddingLeft() + yAxisMark.textSpace + FontUtil.getFontlength(paintText, maxLable));


        int barNum = barData.get(0).size();
        if(scroll) {
            groupWidth = barWidth * barNum + barSpace * (barNum - 1) + groupSpace;
            int allWidth = groupWidth * barData.size();   //总宽度
//            setScrollX(allWidth - rectChart.width());
        }else{
            groupSpace = (rectChart.width() - (barData.size() * (barWidth * barNum + barSpace * (barNum - 1))))/barData.size();
            groupWidth = barWidth * barNum + barSpace * (barNum - 1) + groupSpace;
        }
        int allWidth = groupWidth * barData.size();   //总宽度
        scrollXMax = -(allWidth - rectChart.width());
        if(scroll && !showBegin){
            scrollx = scrollXMax;
        }
        for(int i = 0; i<barData.size(); i++){
            List<Bar> group = barData.get(i);
            //一组
            for(int j = 0; j <group.size(); j++){
                Bar bar = group.get(j);
                int left = rectChart.left + i*groupWidth + groupSpace/2 + j*(barSpace+barWidth);
                bar.setRect(new Rect(left,
                        (int)(rectChart.bottom - rectChart.height()/(yAxisMark.cal_mark_max - yAxisMark.cal_mark_min) * (bar.getValuey()-yAxisMark.cal_mark_min)),
                        left+barWidth, rectChart.bottom));
            }
        }
    }
    private void calculateYMark() {
        float redundance = 1.01f;  //y轴最大和最小值冗余
        yAxisMark.cal_mark_max =  Float.MIN_VALUE;    //Y轴刻度最大值
        yAxisMark.cal_mark_min =  Float.MAX_VALUE;    //Y轴刻度最小值
        for(List<Bar> data : barData){
            for(Bar bar : data){
                yAxisMark.cal_mark_max = Math.max(yAxisMark.cal_mark_max, bar.getValuey());
                yAxisMark.cal_mark_min = Math.min(yAxisMark.cal_mark_min, bar.getValuey());
            }
        }
        LogUtil.i(TAG, "Y轴真实cal_mark_min="+yAxisMark.cal_mark_min+"  cal_mark_max="+yAxisMark.cal_mark_max);
        yAxisMark.cal_mark_max *= redundance;
        yAxisMark.cal_mark_min /= redundance;
        yAxisMark.cal_mark = (yAxisMark.cal_mark_max-yAxisMark.cal_mark_min)/(yAxisMark.lableNum - 1);
        LogUtil.i(TAG, "  cal_mark_min="+yAxisMark.cal_mark_min+"   cal_mark_max="+yAxisMark.cal_mark_max+"  yAxisMark.cal_mark="+yAxisMark.cal_mark);
    }


    /*********************************************************************/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    public void drawChart(Canvas canvas) {

        //绘制横向线
        float yMarkSpace = (rectChart.bottom - rectChart.top)/(yAxisMark.lableNum-1);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(yAxisMark.lineWidth);
        paint.setColor(yAxisMark.lineColor);
        paintEffect.setStyle(Paint.Style.STROKE);
        paintEffect.setStrokeWidth(yAxisMark.lineWidth);
        paintEffect.setColor(yAxisMark.lineColor);
        paintText.setTextSize(yAxisMark.textSize);
        paintText.setColor(yAxisMark.textColor);
        canvas.drawLine(rectChart.left, rectChart.top, rectChart.left, rectChart.bottom, paint);
//        canvas.drawLine(rectChart.right, rectChart.top, rectChart.right, rectChart.bottom, paint);
        PathEffect effects = new DashPathEffect(new float[]{15,6,15,6},0);
        Path path = new Path();
        for (int i = 0; i < yAxisMark.lableNum; i++) {
            path.reset();
            path.moveTo(rectChart.left, rectChart.bottom-yMarkSpace*i);
            path.lineTo(rectChart.right,rectChart.bottom-yMarkSpace*i);
            paintEffect.setPathEffect(effects);
            canvas.drawPath(path, paintEffect);

            String text = yAxisMark.getMarkText(yAxisMark.cal_mark_min + i * yAxisMark.cal_mark);
            canvas.drawText(text,
                    rectChart.left - yAxisMark.textSpace - FontUtil.getFontlength(paintText, text),
                    rectChart.bottom - yMarkSpace * i - yAxisMark.textHeight/2 + yAxisMark.textLead, paintText);
        }


        for(int i = 0; i<barData.size(); i++){
            List<Bar> group = barData.get(i);
            //一组
            //绘制X
            paintText.setTextSize(xAxisMark.textSize);
            paintText.setColor(xAxisMark.textColor);
            canvas.drawText(group.get(0).getValuex(),
                    scrollx + rectChart.left + i*groupWidth + groupWidth/2,
                    xAxisMark.drawPointY,paintText);
//            LogUtil.i(TAG, "绘制X刻度：leftX="+leftX   +"    "+(leftX + oneBarW/2-tw/2)+"*"+(zeroPoint.y+textSpace + leadCoordinate));
//            LogUtil.i(TAG, "leftStartPointX="+leftStartPointX+"  leftStart="+leftStart+ "   oneBarW="+oneBarW   +"    barWidth="+barWidth+"   barSpace="+barSpace);
            for(int j = 0; j <group.size(); j++){
                paint.setColor(barColor[j]);
//                float top = (zeroPoint.y - YMARK_ALL_H * (bean.getNum() / YMARK_MAX) * animPro);
                Rect rect = new Rect(group.get(j).getRect().left + scrollx,
                        group.get(j).getRect().top, group.get(j).getRect().left + scrollx + barWidth, group.get(j).getRect().bottom);
                canvas.drawRect(rect, paint);
            }
        }
    }


}
