package com.openxu.hkchart.bar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
 * date : 2017/7/24 10:46
 * className : BarChart
 * version : 1.0
 * description : 柱状图，支持多柱
 */
public class BarChart extends BaseChart {

    /**设置*/
    private List<List<Bar>> barData;
    private YAxisMark yAxisMark;
    private XAxisMark xAxisMark;
    private boolean scrollAble = true;  //是否支持滚动
    private boolean showBegin = true;    //当数据超出一屏宽度时，实现最后的数据
    private int barWidth = DensityUtil.dip2px(getContext(), 15);    //柱宽度
    private int barSpace = DensityUtil.dip2px(getContext(), 1);    //双柱间的间距
    private int groupSpace = DensityUtil.dip2px(getContext(), 25);//一组柱之间的间距（只有scrollAble==true时才生效）
    private int[] barColor = new int[]{
            Color.parseColor("#f46763"),
            Color.parseColor("#3cd595"),
            Color.parseColor("#4d7bff"),};               //柱颜色
    /**计算*/
    private float groupWidth;
    private float scrollXMax;      //最大滚动距离，是一个负值
    private float scrollx;      //当前滚动距离，默认从第一条数据绘制（scrollx==0），如果从最后一条数据绘制（scrollx==scrollXMax）

    protected GestureDetector mGestureDetector;
    protected Scroller mScroller;


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
        mGestureDetector = new GestureDetector(getContext(), new MyOnGestureListener());
        mScroller = new Scroller(context);
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
    public void setGroupSpace(int groupSpace) {
        this.groupSpace = groupSpace;
    }
    public void setScrollAble(boolean scrollAble) {
        this.scrollAble = scrollAble;
    }
    public void setShowBegin(boolean showBegin) {
        this.showBegin = showBegin;
    }

    public void setData(List<List<Bar>> barData) {
        this.barData = barData;
        if(showAnim)
            chartAnimStarted = false;
        calculate();
        setLoading(false);
    }
    /***********************************1. setting👆**********************************/

    /***********************************2. 计算👇**********************************/
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
        rectChart.top = rectChart.top + yAxisMark.textHeight/2;

        int barNum = barData.get(0).size();
        if(scrollAble) {
            groupWidth = barWidth * barNum + barSpace * (barNum - 1) + groupSpace;
            float allWidth = groupWidth * barData.size();   //总宽度
            scrollXMax = -(allWidth - rectChart.width());
            scrollx = showBegin?0:scrollXMax;
        }else{
            groupSpace = (int)(rectChart.width() - (barData.size() * (barWidth * barNum + barSpace * (barNum - 1))))/barData.size();
            groupWidth = barWidth * barNum + barSpace * (barNum - 1) + groupSpace;
            scrollx = scrollXMax = 0;
        }
        Log.w(TAG, "计算groupWidth="+groupWidth+"   barWidth="+barWidth+"   scrollx="+scrollx);
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
        if(yAxisMark.markType == YAxisMark.MarkType.Integer){
            int min = 0;
            int max = (int)yAxisMark.cal_mark_max;
            int mark = (max-min)/(yAxisMark.lableNum - 1)+((max-min)%(yAxisMark.lableNum - 1)>0?1:0);
            int first = (Integer.parseInt((mark + "").substring(0, 1)) + 1);
            LogUtil.i(TAG, "mark="+mark+"  first="+first);

            if ((mark + "").length() == 1) {
                //YMARK = 1、2、5、10
                mark = (mark == 3 || mark == 4 || mark == 6 ||
                        mark == 7 || mark == 8 || mark == 9) ?
                        ((mark == 3 || mark == 4) ? 5 : 10)
                        : mark;
            } else if ((mark + "").length() == 2) {
                mark = first * 10;
            } else if ((mark + "").length() == 3) {
                mark = first * 100;
            } else if ((mark + "").length() == 4) {
                mark = first * 1000;
            } else if ((mark + "").length() == 5) {
                mark = first * 10000;
            } else if ((mark + "").length() == 6) {
                mark = first * 100000;
            }
            yAxisMark.cal_mark_min = 0;
            yAxisMark.cal_mark_max = mark * (yAxisMark.lableNum - 1);
            yAxisMark.cal_mark = mark;
        }else {   //Float   //Percent
            yAxisMark.cal_mark_max *= redundance;
            yAxisMark.cal_mark_min /= redundance;
            yAxisMark.cal_mark = (yAxisMark.cal_mark_max-yAxisMark.cal_mark_min)/(yAxisMark.lableNum - 1);
        }
        LogUtil.i(TAG, "  cal_mark_min="+yAxisMark.cal_mark_min+"   cal_mark_max="+yAxisMark.cal_mark_max+"  yAxisMark.cal_mark="+yAxisMark.cal_mark);
    }
    /***********************************2. 计算👆**********************************/

    /**********************************3. 测量和绘制👇***********************************/
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
        float yMarkSpace = (rectChart.bottom - rectChart.top)/(yAxisMark.lableNum-1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(yAxisMark.lineColor);
        paint.setColor(yAxisMark.lineColor);
        paintEffect.setStyle(Paint.Style.STROKE);
        paintEffect.setStrokeWidth(yAxisMark.lineWidth);
        paintEffect.setColor(yAxisMark.lineColor);
        paintText.setTextSize(yAxisMark.textSize);
        paintText.setColor(yAxisMark.textColor);
//        canvas.drawLine(rectChart.left, rectChart.top, rectChart.left, rectChart.bottom, paint);
        PathEffect effects = new DashPathEffect(new float[]{15,6,15,6},0);
        paintEffect.setPathEffect(effects);
        for (int i = 0; i < yAxisMark.lableNum; i++) {
            /**绘制横向线*/
            canvas.drawLine(rectChart.left, rectChart.bottom-yMarkSpace*i,
                    rectChart.right,rectChart.bottom-yMarkSpace*i, paint);
            /**绘制y刻度*/
            String text = yAxisMark.getMarkText(yAxisMark.cal_mark_min + i * yAxisMark.cal_mark);
            canvas.drawText(text,
                    rectChart.left - yAxisMark.textSpace - FontUtil.getFontlength(paintText, text),
                    rectChart.bottom - yMarkSpace * i - yAxisMark.textHeight/2 + yAxisMark.textLead, paintText);
        }
        /**绘制柱状*/
        paint.setStyle(Paint.Style.FILL);
        RectF rect = new RectF();
        RectF rectArc = new RectF();
        Path path = new Path();
        for(int i = 0; i<barData.size(); i++){
            List<Bar> group = barData.get(i);
            //一组
            /**绘制X刻度*/
            paintText.setTextSize(xAxisMark.textSize);
            paintText.setColor(xAxisMark.textColor);
            rect.left = scrollx + rectChart.left + i*groupWidth;
            rect.right = rect.left + groupWidth;
            //过滤掉绘制区域外的组
            if(rect.right < rectChart.left || rect.left > rectChart.right) {
//                Log.w(TAG, "第"+i+"组超界了 "+rect.left+" "+rect.right);
                continue;
            }
            //裁剪画布，避免x刻度超出
            int restoreCount = canvas.save();
            canvas.clipRect(new RectF(rectChart.left, rectChart.bottom,
                    rectChart.right, rectChart.bottom+xAxisMark.textSpace+xAxisMark.textHeight));
            canvas.drawText(group.get(0).getValuex(),
                    rect.left+groupWidth/2-FontUtil.getFontlength(paintText, group.get(0).getValuex())/2,
                    xAxisMark.drawPointY,paintText);
            canvas.restoreToCount(restoreCount);
            /**绘制柱状*/
            // 记录当前画布信息
            restoreCount = canvas.save();
            /**使用Canvas的clipRect和clipPath方法限制View的绘制区域*/
            canvas.clipRect(rectChart);   //裁剪画布，只绘制rectChart的范围
            for(int j = 0; j <group.size(); j++){
                paint.setColor(barColor[j]);
//                float top = (zeroPoint.y - YMARK_ALL_H * (bean.getNum() / YMARK_MAX) * animPro);
                rect.left = rectChart.left + i*groupWidth + groupSpace/2 + j*(barSpace+barWidth)+ scrollx;
                rect.right =  rect.left + barWidth;
                //过滤掉绘制区域外的柱
                if( rect.right < rectChart.left ||  rect.left > rectChart.right)
                    continue;
                rect.top = (int)(rectChart.bottom - rectChart.height() /(yAxisMark.cal_mark_max - yAxisMark.cal_mark_min) * (group.get(j).getValuey()-yAxisMark.cal_mark_min) * chartAnimValue);
                rect.bottom = rectChart.bottom;
                rectArc.left = rect.left;
                rectArc.top = rect.top;
                rectArc.right = rect.right;
                rectArc.bottom = rect.top + barWidth;

                path.reset();
                path.moveTo(rect.left, rectChart.bottom);
                path.lineTo(rectArc.left, rectArc.bottom-rectArc.height()/2);
                path.arcTo(rectArc, 180, 180);
                path.lineTo(rect.right, rect.bottom);
                path.close();
//                Log.w(TAG, "---绘制"+i+"*"+j+" = "+group.get(j).getValuey()+" " +rect.top +"  "+rectChart.bottom);
                canvas.drawPath(path, paint);
                /**绘制y值*/
                canvas.drawText(yAxisMark.getMarkText(group.get(j).getValuey()),
                        rectArc.left+barWidth/2-FontUtil.getFontlength(paintText, yAxisMark.getMarkText(group.get(j).getValuey()))/2,
                        rectArc.top-yAxisMark.textSpace-yAxisMark.textHeight+yAxisMark.textLead,paintText);
            }
            //恢复到裁切之前的画布
            canvas.restoreToCount(restoreCount);
        }
    }
    /**********************************3. 测量和绘制👆***********************************/

    /**************************4. 事件👇******************************/
    protected float mDownX, mDownY;

    /**
     * 重写dispatchTouchEvent，并调用requestDisallowInterceptTouchEvent申请父控件不要拦截事件，将事件处理权交给图表
     *
     * 这对图表来说是非常重要的，比如图表放在ScrollerView里面时，如果不调用requestDisallowInterceptTouchEvent(true)，
     * 图表接受的事件将由ScrollerView决定，一旦ScrollerView发现竖直滚动则会拦截事件，导致图表不能再接受到事件
     *
     * 此处首先申请父控件不要拦截事件，所有事件都将传到图表中，由图表决定自己是否处理事件，如果不需要处理（竖直方向滑动距离大于水平方向）则让父控件处理
     * 需要注意的是一旦放弃处理，剩下的事件将不会被收到
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(scrollAble){
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = event.getX();
                    mDownY = event.getY();
                    getParent().requestDisallowInterceptTouchEvent(true);//ACTION_DOWN的时候，赶紧把事件hold住
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(Math.abs(event.getY()-mDownY) > Math.abs(event.getX() - mDownX)) {
                        //竖直滑动的距离大于水平的时候，将事件还给父控件
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                    break;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            if(!mScroller.isFinished())
                mScroller.forceFinished(true);
            return true;   //事件被消费，下次才能继续收到事件
        }
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //          Log.d(TAG, "滚动："+distanceX+"   "+distanceY);
            scrollx -= distanceX;    //distanceX左正右负
            scrollx = Math.min(scrollx, 0);
            scrollx = Math.max(scrollXMax, scrollx);
            postInvalidate();
            return false;
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //          LogUtil.e(TAG,"onFling------------>velocityX="+velocityX+"    velocityY="+velocityY);
            /**
             * 从当前位置scrollx开始滚动，
             * 最小值为scrollXMax -- 滚动到最后
             * 最大值为0 -- 滚动到开始
             */
            mScroller.fling((int)scrollx, 0,
                    (int)velocityX, 0,
                    (int)scrollXMax, 0,
                    0, 0
            );
            return false;
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.isFinished())
            return;
        if(mScroller.computeScrollOffset()){
//            Log.d(TAG, "滚动后计算："+mScroller.getCurrX());
            scrollx = mScroller.getCurrX();
            postInvalidate();
        }
    }
    /**************************4. 事件👆******************************/

}
