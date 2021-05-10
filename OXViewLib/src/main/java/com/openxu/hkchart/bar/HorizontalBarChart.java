package com.openxu.hkchart.bar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.widget.Scroller;

import com.openxu.hkchart.BaseChart;
import com.openxu.hkchart.element.XAxisMark;
import com.openxu.hkchart.element.YAxisMark;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;

import java.util.List;

/**
 * autour : openXu
 * date : 2021/3/2 10:46
 * className : HorizontalBarChart
 * version : 1.0
 * description : 柱状图，支持多柱
 */
public class HorizontalBarChart extends BaseChart {

    /**设置*/
    private List<HBar> barData;
    private YAxisMark yAxisMark;
    private XAxisMark xAxisMark;
    private boolean showBegin = true;    //当数据超出一屏宽度时，实现最后的数据
    private int barWidth = DensityUtil.dip2px(getContext(), 26);
    private int barSpace = DensityUtil.dip2px(getContext(), 10);
    private int barColor[] = {
            Color.parseColor("#F46863"),
            Color.parseColor("#2DD08A"),
            Color.parseColor("#567CF6"),
            Color.parseColor("#F5B802"),
            Color.parseColor("#CC71F7")
    };
    /**计算*/
    private RectF chartRect = new RectF();                //图表矩形

    protected GestureDetector mGestureDetector;
    protected Scroller mScroller;


    public HorizontalBarChart(Context context) {
        this(context, null);
    }

    public HorizontalBarChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
//        mGestureDetector = new GestureDetector(getContext(), new MyOnGestureListener());
//        mScroller = new Scroller(context);
    }

    /***********************************1. setting👇**********************************/
    public void setYAxisMark(YAxisMark yAxisMark) {
        this.yAxisMark = yAxisMark;
    }
    public void setXAxisMark(XAxisMark xAxisMark) {
        this.xAxisMark = xAxisMark;
    }
    public void setBarColor(int[] barColor) {
        this.barColor = barColor;
    }
    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
    }
    public void setBarSpace(int barSpace) {
        this.barSpace = barSpace;
    }
    public void setShowBegin(boolean showBegin) {
        this.showBegin = showBegin;
    }

    public void setData(List<HBar> barData) {
        LogUtil.w(TAG, "设置数据："+barData);
        this.barData = barData;
        if(showAnim)
            chartAnimStarted = false;
        calculateYMark();
        setLoading(false);
        requestLayout();
    }
    /***********************************1. setting👆**********************************/

    /***********************************2. 计算👇**********************************/
    int index0;   //0刻度对应的标签索引
    private void calculateYMark() {
        if(barData==null || barData.size()<=0)
            return;
        yAxisMark.cal_mark_max = barData.get(0).getValue();
        yAxisMark.cal_mark_min = barData.get(0).getValue();
        for(HBar data : barData){
            yAxisMark.cal_mark_max = Math.max(yAxisMark.cal_mark_max, data.getValue());
            yAxisMark.cal_mark_min = Math.min(yAxisMark.cal_mark_min, data.getValue());
        }
        LogUtil.w(TAG, "真实的最小值="+yAxisMark.cal_mark_min + "    最大值="+yAxisMark.cal_mark_max +"    y刻度数量:"+yAxisMark.lableNum);
        if(yAxisMark.cal_mark_min >0 && yAxisMark.cal_mark_max>0){
            yAxisMark.cal_mark_min = 0;
        }else if(yAxisMark.cal_mark_min <0 && yAxisMark.cal_mark_max<0){
            yAxisMark.cal_mark_max = 0;
        }
        int z =  (int)((yAxisMark.cal_mark_max-yAxisMark.cal_mark_min)/(yAxisMark.lableNum - 1));
        int y =  (int)((yAxisMark.cal_mark_max-yAxisMark.cal_mark_min)%(yAxisMark.lableNum - 1));
        int mark = z + (y>0?1:0);
        LogUtil.w(TAG, "取整 "+z+"   余 "+y +"   计算mark="+mark);
        mark = mark==0?1:mark;   //最大值和最小值都为0的情况
        LogUtil.w(TAG, yAxisMark.cal_mark_min+"~"+yAxisMark.cal_mark_max+"计算mark="+mark);
        if (mark<=10) {
            //YMARK = 1、2、5、10
            mark = (mark == 3 || mark == 4 || mark == 6 ||
                    mark == 7 || mark == 8 || mark == 9) ?
                    ((mark == 3 || mark == 4) ? 5 : 10)
                    : mark;
        }else{
            //mark前两位，比如 4549 取mark1=4 mark2=5
            int mark1 = Integer.parseInt((mark+"").substring(0,1));
            int mark2 = Integer.parseInt((mark+"").substring(1,2));
            LogUtil.w(TAG, "mark前两位="+mark1+"  "+mark2);
            if(mark2<5){
                mark2 = 5;
            }else{
                mark2 = 0;
                mark1 += 1;
            }
            int ws = (mark + "").length();
            LogUtil.w(TAG, "mark前两位="+mark1+"  "+mark2 +"   位数："+ws);
            mark = mark1*getWs(ws) + mark2*getWs(ws-1);
        }
        LogUtil.w(TAG, "取值mark="+mark);
        if(yAxisMark.cal_mark_min<0 && yAxisMark.cal_mark_max>0){
            //需要显示0
            index0 = (int)(-yAxisMark.cal_mark_min/mark) +(-yAxisMark.cal_mark_min%mark!=0?1:0);
            while(checkMark(index0, mark, yAxisMark.cal_mark_max)){
                yAxisMark.lableNum ++;
                LogUtil.w(TAG, "检测到正值可能越界，增加标签数量="+yAxisMark.lableNum);
            }
            LogUtil.w(TAG, "一正一负的情况mark="+mark +"  index0="+index0+"   lableNum="+yAxisMark.lableNum);
            yAxisMark.cal_mark_min = -mark *index0;
            yAxisMark.cal_mark_max = yAxisMark.cal_mark_min + mark *(yAxisMark.lableNum-1);
        }else if(yAxisMark.cal_mark_min == 0){
            index0 = 0;
            yAxisMark.cal_mark_max = mark * (yAxisMark.lableNum - 1);
        }else if(yAxisMark.cal_mark_max == 0){
            index0 = yAxisMark.lableNum-1;
            yAxisMark.cal_mark_min = -mark * (yAxisMark.lableNum - 1);
        }
        yAxisMark.cal_mark = mark;
        LogUtil.w(TAG, "最终取值="+yAxisMark.cal_mark_min +"~"+yAxisMark.cal_mark_max+"   mark="+mark);
    }

    private int getWs(int ws){
        if(ws == 1)
            return 1;
        else if(ws == 2)
            return 10;
        else if(ws == 3)
            return 100;
        else if(ws == 4)
            return 1000;
        else if(ws == 5)
            return 10000;
        else if(ws == 6)
            return 100000;
        else if(ws == 7)
            return 1000000;
        else if(ws == 8)
            return 10000000;
        else if(ws == 9)
            return 100000000;
        else
            return 1;
    }
    private boolean checkMark(int index0, int mark, float max){
        return (yAxisMark.lableNum-1-index0)*mark < max;
    }

    /***********************************2. 计算👆**********************************/

    /**********************************3. 测量和绘制👇***********************************/
    public boolean scrollAble = false;  //是否支持滚动
    private float scrollXMax;      //最大滚动距离，是一个负值
    private float scrollx;      //当前滚动距离，默认从第一条数据绘制（scrollx==0），如果从最后一条数据绘制（scrollx==scrollXMax）

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        LogUtil.v(TAG, "测量建议："+MeasureSpec. UNSPECIFIED+"    "+MeasureSpec. EXACTLY+"    "+MeasureSpec. AT_MOST);
        LogUtil.v(TAG, "宽测量建议："+widthSize+"*"+widthMode);
        LogUtil.v(TAG, "高度测量建议："+heightSize+"*"+heightMode);
        scrollAble = false;
        scrollXMax = 0;
        scrollx = 0;
        int height = 0;
        switch (heightMode) {
            case MeasureSpec. EXACTLY:  //子控件如果是具体值，约束尺寸就是这个值，模式为确定的；子控件为填充父窗体，约束尺寸是父控件剩余大小，模式为确定的。
                height = heightSize;
                break;
            case MeasureSpec. UNSPECIFIED:
            case MeasureSpec. AT_MOST:   //子控件如果是包裹内容
                //计算需要的高度
                if(barData!=null && barData.size()>0) {
                    height += getPaddingTop();
                    height += getPaddingBottom();
                    paintText.setTextSize(yAxisMark.textSize);
                    paintText.setTypeface(yAxisMark.numberTypeface);
                    yAxisMark.textHeight = (int) FontUtil.getFontHeight(paintText);
                    yAxisMark.textLead = (int) FontUtil.getFontLeading(paintText);
                    height += yAxisMark.textSpace;
                    height += yAxisMark.textHeight;
                    height += ((barWidth+barSpace)*barData.size());
                    //
                  /*  if(height>heightSize){

                    }
                    scrollAble = false;
                    scrollXMax = 0;
                    scrollx = 0;*/
                    LogUtil.v(TAG, "实际需要高度："+heightSize);

                }else{
                    //默认最小高度用于显示正在加载中
                    height = DensityUtil.dip2px(getContext(), 150);
                }
                break;
        }
        LogUtil.v(TAG, "测量："+widthSize+"*"+height);
        setMeasuredDimension(widthSize, height);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(xAxisMark==null || yAxisMark==null || barData == null) {
            return;
        }
        paintText.setTextSize(xAxisMark.textSize);
        paintText.setTypeface(Typeface.DEFAULT);
        xAxisMark.textHeight = FontUtil.getFontHeight(paintText);
        xAxisMark.textLead = FontUtil.getFontLeading(paintText);
        float lableMax = 0;
        for(HBar hbar : barData){
            lableMax = Math.max(lableMax, FontUtil.getFontlength(paintText, hbar.getLable()));
        }
        chartRect.left = getPaddingLeft() + lableMax + xAxisMark.textSpace;
        chartRect.top = yAxisMark.textSpace + yAxisMark.textHeight;
        paintText.setTextSize(yAxisMark.textSize);
        paintText.setTypeface(yAxisMark.numberTypeface);
        chartRect.right = rectChart.right - FontUtil.getFontlength(paintText, yAxisMark.getMarkText(yAxisMark.cal_mark_max))/2;
        chartRect.bottom = rectChart.bottom;
        LogUtil.w(TAG, "重新计算绘制范围:"+chartRect);
    }

    RectF rect = new RectF();  //备用，后面绘制矩形
    Path path = new Path();
    @Override
    public void drawChart(Canvas canvas) {
        if(barData==null || barData.size()==0)
            return;
        float axis0x = 0;   //y轴0刻度的x坐标
        float top = chartRect.top + barSpace/2;  //绘制柱状开始的最上方位置
        paintText.setTextSize(xAxisMark.textSize);
        paintText.setColor(xAxisMark.textColor);
        paintText.setTypeface(Typeface.DEFAULT);
        xAxisMark.textHeight = FontUtil.getFontHeight(paintText);
        xAxisMark.textLead = FontUtil.getFontLeading(paintText);

        paint.setStyle(Paint.Style.FILL);
        for(int i = 0; i < barData.size(); i++){
            HBar bar = barData.get(i);
            //绘制x标签
            String lable = getXValue(bar.getLable());
            float textWidth = FontUtil.getFontlength(paintText, lable);
            canvas.drawText(lable,
                    chartRect.left - xAxisMark.textSpace - textWidth,
                    top + barWidth/2 - xAxisMark.textHeight/2 + xAxisMark.textLead, paintText);
            //绘制底色
            paint.setColor(Color.parseColor("#f0f0f0"));
            rect.left = chartRect.left;
            rect.top = top;
            rect.right = chartRect.right;
            rect.bottom = top + barWidth;
            canvas.drawRect(rect, paint);

            top += barWidth+barSpace;
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(yAxisMark.lineWidth);
        paint.setColor(yAxisMark.lineColor);
        paintEffect.setStyle(Paint.Style.STROKE);
        paintEffect.setStrokeWidth(yAxisMark.lineWidth);
        paintEffect.setColor(yAxisMark.lineColor);
//        paint.setColor(Color.RED);
//        canvas.drawRect(rectChart, paint);
//        paint.setColor(Color.BLUE);
//        canvas.drawRect(chartRect, paint);

        PathEffect effects = new DashPathEffect(new float[]{15,6,15,6},0);

        paintEffect.setPathEffect(effects);
        paintText.setTextSize(yAxisMark.textSize);
        paintText.setColor(yAxisMark.textColor);
        paintText.setTypeface(yAxisMark.numberTypeface);
        float yMarkSpace = chartRect.width()/(yAxisMark.lableNum-1);
        for (int i = 0; i < yAxisMark.lableNum; i++) {
            /**竖直线*/
            if(index0 == i)
                axis0x = chartRect.left + yMarkSpace * i;

            //canvas.drawLine()给paint设置DashPathEffect(虚线)无效。后面发现是硬件加速的锅。 解决方法就是，在view层关闭硬件加速
//            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//            canvas.drawLine(chartRect.left + yMarkSpace*i, chartRect.top,
//                    chartRect.left + yMarkSpace*i,chartRect.bottom, index0 == i?paint:paintEffect);
            //canvas.drawPath()可以绘制虚线，不用关闭硬件加速
            path.reset();
            path.moveTo(chartRect.left + yMarkSpace*i, chartRect.top);
            path.lineTo(chartRect.left + yMarkSpace*i, chartRect.bottom);
            canvas.drawPath(path ,index0 == i?paint:paintEffect);

            /**绘制y刻度*/
            String text = yAxisMark.getMarkText(yAxisMark.cal_mark_min + i * yAxisMark.cal_mark);
            canvas.drawText(text,
                    chartRect.left + yMarkSpace*i - FontUtil.getFontlength(paintText, text)/2,
                    rectChart.top + yAxisMark.textLead, paintText);
        }
        /**绘制数据*/
        paint.setStyle(Paint.Style.FILL);
        paintText.setTextSize(xAxisMark.textSize);
        paintText.setColor(xAxisMark.textColor);
        paintText.setTypeface(yAxisMark.numberTypeface);
        //两种不同的字体设置后要重新计算 文字高度和基线，否则会出现意想不到的效果
        xAxisMark.textHeight = FontUtil.getFontHeight(paintText);
        xAxisMark.textLead = FontUtil.getFontLeading(paintText);
        //y值每一份对应的宽度
        float once = chartRect.width() / (yAxisMark.cal_mark_max - yAxisMark.cal_mark_min);
        top = chartRect.top + barSpace/2;
        for(int i = 0; i < barData.size(); i++){
            HBar bar = barData.get(i);
            //绘制颜色柱子
            rect.top = top;
            rect.bottom = top + barWidth;
            if(bar.getValue()>=0){
                rect.left = axis0x;
                rect.right = rect.left + once*bar.getValue()* chartAnimValue;
            }else{
                rect.right = axis0x;
                rect.left = rect.right + once*bar.getValue()* chartAnimValue;
            }
            paint.setColor(barColor[i%barColor.length]);
            canvas.drawRect(rect, paint);
            //绘制文字
            String lable = yAxisMark.getMarkText(bar.getValue()) + yAxisMark.unit;
            float textWidth = FontUtil.getFontlength(paintText, lable);

            float x = rect.width() < textWidth?
                    (
                            bar.getValue()>=0?rect.left:
                                    rect.right - textWidth
                            ):
                    rect.left + rect.width()/2 - textWidth/2;
            if(x<chartRect.left+xAxisMark.textSpace)
                x = chartRect.left+xAxisMark.textSpace;
            if(x + textWidth>chartRect.right)
                x = chartRect.right - textWidth - xAxisMark.textSpace;
            paintText.setTypeface(yAxisMark.numberTypeface);
            canvas.drawText(lable,
                    x,
                    top + barWidth/2 - xAxisMark.textHeight/2 + xAxisMark.textLead, paintText);

            top += barWidth+barSpace;
        }
    }

    //截取
    private String getXValue(String value){
        if(TextUtils.isEmpty(value))
            return "";
        if(xAxisMark.splitSubLen>0 && value.length()>xAxisMark.splitSubLen){
            value = value.substring(0, xAxisMark.splitSubLen);
        }
        return value;
    }
    /**********************************3. 测量和绘制👆***********************************/

    /**************************4. 事件👇******************************/
    /**************************4. 事件👆******************************/

}
