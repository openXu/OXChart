package com.openxu.cview.xmstock;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.xmstock.bean.DataPoint;
import com.openxu.cview.xmstock.bean.FenbuInfo;
import com.openxu.cview.xmstock.bean.FenbuNum;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * autour : xiami
 * date : 2018/3/13 14:26
 * className : BarChart
 * version : 1.0
 * description : 股票涨跌分布图
 */
public class BarChart extends BaseChart {

    //设置数据
    private FenbuInfo fenbuData;
    //计算后的数据
    private List<DataPoint> itemCenterPointList;

    /**可以设置的属性*/
    //设置颜色(绿、黄、红)
    private int[] barColor = new int[]{
            Color.parseColor("#278d3a"),
            Color.parseColor("#ec9343"),
            Color.parseColor("#d03231")};
    //设置柱子宽度
    private int barSize = DensityUtil.dip2px(getContext(), 20);
    //设置文字大小
    private int textSize_X = (int)getResources().getDimension(R.dimen.ts_barchart_x);   //X坐标文字大小
    private int textSize_Y = (int)getResources().getDimension(R.dimen.ts_barchart_y);   //Y坐标文字大小
    private int lableTextSize = (int)getResources().getDimension(R.dimen.ts_barchart_lable);
    //设置字体距离
    private int textSpaceX = DensityUtil.dip2px(getContext(), 5);
    private int textSpaceY = DensityUtil.dip2px(getContext(), 6);
    private int textSpaceLable = DensityUtil.dip2px(getContext(), 8);
    private int textSpaceLableNum = DensityUtil.dip2px(getContext(), 12);  //涨停文字与数字之间的间隙（涨停  58家）
    //设置坐标文字颜色
    private int textColorLable = getResources().getColor(R.color.tc_chart_lable);
    private int textColorX = getResources().getColor(R.color.tc_chart_xy);

    private float ONE_BAR_WEDTH;   //单个柱状占的宽度（包含space)
    private int YMARK_MAX =  Integer.MIN_VALUE;    //Y轴刻度最大值

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
        touchEnable = true;
    }
    /***********************************设置属性set方法**********************************/
    public void setBarColor(int[] barColor) {
        this.barColor = barColor;
    }
    public void setBarSize(int barSize) {
        this.barSize = barSize;
    }
    /***********************************设置属性set方法over**********************************/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        evaluatorByData();
        invalidate();
    }

    /***************************************************/

    /***************************************************/

    /**设置数据*/
    public void setData(FenbuInfo fenbuData){
        this.fenbuData = fenbuData;
        if(null==fenbuData)
            return;
        if(getMeasuredWidth()>0) {
            evaluatorByData();
            startDraw = false;
            invalidate();
        }
    }
    /**设置数据后，计算相关值*/
    private void evaluatorByData(){
        if(null==fenbuData)
            return;
        /**①、计算表体矩形区域*/
        paintLabel.setTextSize(lableTextSize);
        float lableHeight = FontUtil.getFontHeight(paintLabel);
        paintLabel.setTextSize(textSize_Y);
        float yMarkHeight = FontUtil.getFontHeight(paintLabel);
        paintLabel.setTextSize(textSize_X);
        float xLableHeight = FontUtil.getFontHeight(paintLabel);
        //图表主体矩形
        rectChart = new RectF(getPaddingLeft(),
                getPaddingTop() + yMarkHeight + textSpaceY,
                getMeasuredWidth()-getPaddingRight(),
                getMeasuredHeight() - getPaddingBottom() - lableHeight*2 - textSpaceLable*3
                        - xLableHeight - textSpaceX);
        LogUtil.w(TAG, "表体矩形区域"+rectChart);

        /**②、计算item的中点坐标（X轴）*/
        List<List<String>> fenbu_line = fenbuData.getFenbu_line();
        ONE_BAR_WEDTH = (rectChart.right - rectChart.left)/fenbu_line.size();
        itemCenterPointList = new ArrayList<>();
        for(int i = 0; i < fenbu_line.size() ; i++){
            List<String> group = fenbu_line.get(i);
            String valueX = group.get(0);
            int valueY = Integer.parseInt(group.get(1));
            if(YMARK_MAX<valueY)   //计算Y刻度最大值
                YMARK_MAX = valueY;
            PointF point = new PointF(rectChart.left+ONE_BAR_WEDTH*i+ONE_BAR_WEDTH/2, rectChart.bottom);
            LogUtil.w(TAG, valueX+"   "+valueY+"    point="+point);
            itemCenterPointList.add(new DataPoint(valueX, valueY, point));
        }

        LogUtil.i(TAG, "Y轴YMARK_MAX="+YMARK_MAX  +"   ONE_BAR_WEDTH="+ONE_BAR_WEDTH);
    }

    private boolean onFocus = false;  //是否正处于触摸焦点状态
    private int focusIndex;
    @Override
    protected void onTouchMoved(PointF point) {
        if(null==fenbuData)
            return;
        onFocus = (null != point);
        List<List<String>> fenbu_line = fenbuData.getFenbu_line();
        if(null != point && null!=fenbuData && fenbu_line.size()>0) {
            //获取焦点对应的数据的索引
            focusIndex = (int) ((point.x - rectChart.left) *
                    fenbu_line.size() / (rectChart.right - rectChart.left));
            focusIndex = Math.max(0, Math.min(focusIndex, fenbu_line.size() - 1));
        }
        invalidate();
    }



    /**绘制图表基本框架*/
    @Override
    public void drawDefult(Canvas canvas) {
        if(null==fenbuData)
            return;
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(defColor);
        canvas.drawLine(rectChart.left, rectChart.bottom,
                rectChart.right, rectChart.bottom, paint);

        //绘制下面的文字
        FenbuNum numData = fenbuData.getFenbu_num();
        paintLabel.setTextSize(lableTextSize);
        float doubleTextLength = FontUtil.getFontlength(paintLabel, "上涨");
        float downTextLength = FontUtil.getFontlength(paintLabel, numData.getDown()+"家");
        float downStopTextLength = FontUtil.getFontlength(paintLabel, numData.getDown_stop()+"家");
        float equalTextLength = FontUtil.getFontlength(paintLabel, numData.getEqual()+"家");
        float stopTextLength = FontUtil.getFontlength(paintLabel, numData.getStop()+"家");
        float centerStart =rectChart.left + (rectChart.right-rectChart.left -
                (doubleTextLength+textSpaceLableNum+(downTextLength>downStopTextLength?downTextLength:downStopTextLength)))/2;
        float rightStart = rectChart.right-doubleTextLength-textSpaceLableNum-
                (equalTextLength>stopTextLength?equalTextLength:stopTextLength);
        float lableHeight = FontUtil.getFontHeight(paintLabel);
        float lableLead = FontUtil.getFontHeight(paintLabel);
        //第一行
        float top = getMeasuredHeight() - getPaddingBottom() - (lableHeight+textSpaceLable)*2;
        paintLabel.setColor(textColorLable);
        canvas.drawText("上涨", rectChart.left,top + lableLead, paintLabel);
        paintLabel.setColor(barColor[2]);
        canvas.drawText(numData.getUp()+"家",
                rectChart.left+doubleTextLength+textSpaceLableNum,top + lableLead, paintLabel);

        paintLabel.setColor(textColorLable);
        canvas.drawText("下跌", centerStart,top + lableLead, paintLabel);
        paintLabel.setColor(barColor[0]);
        canvas.drawText(numData.getDown()+"家",
                centerStart+doubleTextLength+textSpaceLableNum,top + lableLead, paintLabel);

        paintLabel.setColor(textColorLable);
        canvas.drawText("平家", rightStart,top + lableLead, paintLabel);
        paintLabel.setColor(barColor[1]);
        canvas.drawText(numData.getEqual()+"家",
                rightStart+doubleTextLength+textSpaceLableNum,top + lableLead, paintLabel);

        //第二行
        top = getMeasuredHeight() - getPaddingBottom() - (lableHeight+textSpaceLable);
        paintLabel.setColor(textColorLable);
        canvas.drawText("涨停", rectChart.left,top + lableLead, paintLabel);
        paintLabel.setColor(barColor[2]);
        canvas.drawText(numData.getUp_stop()+"家",
                rectChart.left+doubleTextLength+textSpaceLableNum,top + lableLead, paintLabel);

        paintLabel.setColor(textColorLable);
        canvas.drawText("跌停", centerStart,top + lableLead, paintLabel);
        paintLabel.setColor(barColor[0]);
        canvas.drawText(numData.getDown_stop()+"家",
                centerStart+doubleTextLength+textSpaceLableNum,top + lableLead, paintLabel);

        paintLabel.setColor(textColorLable);
        canvas.drawText("停牌", rightStart,top + lableLead, paintLabel);
        paintLabel.setColor(Color.BLACK);
        canvas.drawText(numData.getStop()+"家",
                rightStart+doubleTextLength+textSpaceLableNum,top + lableLead, paintLabel);

    }

    /**绘制debug辅助线*/
    @Override
    public void drawDebug(Canvas canvas) {
        super.drawDebug(canvas);
    }

    /**绘制图表*/
    @Override
    public void drawChart(Canvas canvas) {
        if(null==fenbuData)
            return;
        paintLabel.setTextSize(textSize_X);
        float xLableLead = FontUtil.getFontLeading(paintLabel);
        paintLabel.setTextSize(textSize_Y);
        float yLableHeight = FontUtil.getFontHeight(paintLabel);
        float yLableLead = FontUtil.getFontLeading(paintLabel);
        int barColorIndex = 0;
        for(int i = 0 ;i<itemCenterPointList.size(); i++){
            DataPoint dataPoint = itemCenterPointList.get(i);

            if(onFocus && i == focusIndex){
                //触摸焦点加粗
                Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);//粗体
                paintLabel.setTypeface(font);
            }

            //①、绘制X轴文字
            paintLabel.setTextSize(textSize_X);
            paintLabel.setColor(textColorX);
            float xLableLength = FontUtil.getFontlength(paintLabel, dataPoint.getValueX());
            canvas.drawText(dataPoint.getValueX(), dataPoint.getPoint().x-xLableLength/2,
                    dataPoint.getPoint().y + textSpaceX + xLableLead, paintLabel);

            //②、绘制柱子
            barColorIndex = (i==(itemCenterPointList.size()-1)/2)?1:(barColorIndex == 1 || barColorIndex == 2)?2:0;
            paint.setColor(barColor[barColorIndex]);
            int left = (int)(dataPoint.getPoint().x-barSize/2);
            int top = (int)((rectChart.top+
                    (rectChart.bottom-rectChart.top)-
                    (rectChart.bottom-rectChart.top)/YMARK_MAX*dataPoint.getValueY()*animPro
            ));
            canvas.drawRect(new Rect(left, top,left+barSize, (int)rectChart.bottom), paint);

            //③、绘制柱子上的数字
            paintLabel.setTextSize(textSize_Y);
            paintLabel.setColor(barColor[barColorIndex]);
            xLableLength = FontUtil.getFontlength(paintLabel, (int)dataPoint.getValueY()+"");

            canvas.drawText((int) dataPoint.getValueY() + "", dataPoint.getPoint().x - xLableLength / 2,
                    top - textSpaceY - yLableHeight + yLableLead, paintLabel);
            Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);//常规
            paintLabel.setTypeface(font);
        }
    }

    private float animPro;       //动画计算的占比数量
    /**创建动画*/
    @Override
    protected ValueAnimator initAnim() {
        if(null!=fenbuData) {
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
