package com.openxu.cview.xmstock20201030;

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
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.xmstock.BaseChart;
import com.openxu.cview.xmstock.bean.DataPoint;
import com.openxu.cview.xmstock20201030.bean.TopDetailNew;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * autour : xiami
 * date : 2020/11/13 14:26
 * className : QsrdLinesChart
 * version : 1.0
 * description : 1. 券商热点走势对比图
 */
public class QsrdLinesChart extends BaseChart {

    /**可以设置的属性*/
    //设置Y轴刻度数量
    private int YMARK_NUM = 3;
    //设置线条颜色  粗细
    private int lineColor = Color.parseColor("#358ce5");  //蓝色
    private int lineSize = DensityUtil.dip2px(getContext(), 1.5f);
    //新闻圆点颜色
    private int newCircleColor = Color.parseColor("#e7413d");
    //设置坐标文字大小
    private int textSize = (int)getResources().getDimension(R.dimen.ts_chart_xy);
    //设置坐标文字颜色
    private int textColor = Color.parseColor("#5E5E5E");
    //设置X坐标字体与横轴的距离
    private int textSpaceX = DensityUtil.dip2px(getContext(), 5);
    //设置Y坐标字体与横轴的距离
    private int textSpaceY = DensityUtil.dip2px(getContext(), 3);
    //设置动画类型
    private AnimType animType = AnimType.SLOW_DRAW;
    //设置焦点线颜色 及 粗细
    private int focusLineColor = Color.parseColor("#5E5E5E");
    private int focusLineSize = DensityUtil.dip2px(getContext(), 1f);

    /**设置数据*/
    private List<List<Object>> dataList = new ArrayList<>();
    //需要标点的新闻日期
    private List<TopDetailNew> newsList = new ArrayList<>();
    private List<String> newsDate = new ArrayList<>();

    //计算后的数据
    private String[] lableXList;
    private List<DataPoint> lableXPointList;
    private List<DataPoint> linePointList;

    public enum AnimType{
        LEFT_TO_RIGHT,   //动画从左往右
        BOTTOM_TO_TOP,   //动画从下网上上升
        SLOW_DRAW        //动画缓慢绘制
    }
    /**需要计算相关值*/
    private float lableLead, lableHeight;
    /*字体绘制相关*/
    private float YMARK =  1;    //Y轴刻度间隔
    private float YMARK_MAX =  Float.MIN_VALUE;    //Y轴刻度最大值
    private float YMARK_MIN =  Float.MAX_VALUE;    //Y轴刻度最小值


    public QsrdLinesChart(Context context) {
        this(context, null);
    }
    public QsrdLinesChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public QsrdLinesChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        dataList = new ArrayList<>();
        touchEnable = true;
    }

    public void setYMARK_NUM(int YMARK_NUM) {
        this.YMARK_NUM = YMARK_NUM;
    }

    /***********************************设置属性set方法**********************************/
    public void setAnimType(AnimType animType) {
        this.animType = animType;
    }
    /**
     * 设置数据
     */
    public void setData(List<List<Object>> dataList, List<TopDetailNew> newsList){
        if(null==dataList)
            return;
        this.dataList.clear();
        this.dataList.addAll(dataList);
        this.newsList.clear();
        this.newsList.addAll(newsList);
        this.newsDate.clear();
        for(TopDetailNew news : newsList){
            //新闻日期：[2020-11-03 08:00:00, 2020-10-21 08:00:00, 2020-08-03 07:54:00, 2020-10-14 08:00:00]
            newsDate.add(news.getPublishdate().contains(" ")?
                    news.getPublishdate().substring(0, news.getPublishdate().indexOf(" "))
                    : news.getPublishdate());
        }
        LogUtil.v(TAG, "新闻日期："+newsDate);
        //获取X轴刻度
        lableXList = new String[]{dataList.get(0).get(0).toString(), dataList.get(dataList.size()-1).get(0).toString()};
        if(getMeasuredWidth()>0) {
            evaluatorByData();
            startDraw = false;
            isLoading = false;
            invalidate();
        }
    }
    /***********************************设置属性set方法over**********************************/
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        evaluatorByData();
        startDraw = false;
        invalidate();
    }

    /**设置数据后，计算相关值*/
    private void evaluatorByData(){
        if(dataList.size()<=0)
            return;

        /**③、计算Y刻度最大值和最小值以及幅度*/
        YMARK_MAX =  Float.MIN_VALUE;    //Y轴刻度最大值
        YMARK_MIN =  Float.MAX_VALUE;    //Y轴刻度最小值
        float redundance = 1.01f;  //y轴最大和最小值冗余
        for(List<Object> list : dataList){
            //["2020-07-10", 2491.7 ]
            try {
                YMARK = Float.parseFloat(list.get(1).toString());
//                    LogUtil.d(TAG, list.get(1)+" Y轴 被识别为Float = "+YMARK);
                if(YMARK>YMARK_MAX)
                    YMARK_MAX = YMARK;
                if(YMARK<YMARK_MIN)
                    YMARK_MIN = YMARK;
            }catch (Exception e){
            }
        }
        LogUtil.w(TAG, "Y轴真实YMARK_MIN="+YMARK_MIN+"   YMARK_MAX="+YMARK_MAX);
        if(YMARK_MAX>0)
            YMARK_MAX *= redundance;
        else
            YMARK_MAX /= redundance;
        if(YMARK_MIN>0)
            YMARK_MIN /= redundance;
        else
            YMARK_MIN *= redundance;
        YMARK = (YMARK_MAX-YMARK_MIN)/(YMARK_NUM - 1);
        YMARK = (int)YMARK + 1;
        YMARK_MIN = (int)YMARK_MIN;
        YMARK_MAX = (int)(YMARK_MIN+YMARK*(YMARK_NUM-1));
        LogUtil.i(TAG, "Y轴YMARK_MIN="+YMARK_MIN+"   YMARK_MAX="+YMARK_MAX+"   YMARK="+YMARK);

        /**①、计算字体相关以及图表原点坐标*/
        paintLabel.setTextSize(textSize);
        lableHeight = FontUtil.getFontHeight(paintLabel);
        lableLead =  FontUtil.getFontLeading(paintLabel);
        //图表主体矩形
        rectChart = new RectF(getPaddingLeft() + textSpaceY + FontUtil.getFontlength(paintLabel, (int)YMARK_MAX+""),
                getPaddingTop(),
                getMeasuredWidth()-getPaddingRight(),
                getMeasuredHeight()-getPaddingBottom() - lableHeight - textSpaceX);
        /**②、计算X标签绘制坐标*/
        float lableXSpace = 0;
        for(String lableX : lableXList){
            lableXSpace += FontUtil.getFontlength(paintLabel, lableX);
        }
        lableXSpace = (rectChart.right - rectChart.left - lableXSpace)/(lableXList.length -1);
        lableXPointList = new ArrayList<>();
        float left = rectChart.left;
        for(int i = 0; i<lableXList.length; i++){
            String lableX = lableXList[i];
            lableXPointList.add(new DataPoint(lableX, 0, new PointF(
                    left,rectChart.bottom + textSpaceX + lableLead)));
            left += (FontUtil.getFontlength(paintLabel, lableXList[i])+lableXSpace);
        }
        /**④、计算点的坐标，如果有动画的情况下，边绘制边计算会耗费性能，所以先计算*/
        linePointList = new ArrayList<>();
        float oneSpace = (rectChart.right - rectChart.left) / (dataList.size()-1);
        List<DataPoint> lastDataGroup = null;   //最后一组数据
        for(int i = 0; i < dataList.size(); i++){
//            List<String> oneData = dataList.get(0);
            try {
                ////["2020-07-10", 2491.7 ]
                float valueY = Float.parseFloat(dataList.get(i).get(1).toString());
                PointF point = new PointF();
                point.x = rectChart.left + i * oneSpace;
                point.y = rectChart.bottom -
                        (rectChart.bottom-rectChart.top)/(YMARK_MAX - YMARK_MIN) * (valueY-YMARK_MIN);
                linePointList.add(new DataPoint(dataList.get(i).get(0).toString(), valueY, point));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onTouchMoved(PointF point) {
        if(null==dataList)
            return;
        onFocus = (null != point);
        if(null != point && null!=dataList && dataList.size()>0) {
            //避免滑出
            if(point.x > linePointList.get(linePointList.size()-1).getPoint().x)
                point.x = linePointList.get(linePointList.size()-1).getPoint().x;
            if(point.x < linePointList.get(0).getPoint().x)
                point.x = linePointList.get(0).getPoint().x;
            //获取焦点对应的数据的索引
            int index = (int) ((point.x - rectChart.left) * dataList.size() / (rectChart.right - rectChart.left));
//            LogUtil.e(getClass().getSimpleName(), "========焦点索引："+index+"   数据总数："+dataList.size()+"  绘制点总数："+linePointList.size());
            index = Math.max(0, Math.min(index, dataList.size() - 1));
            focusData = new FocusData();
            focusData.setPoint(linePointList.get(index));
            focusData.setData(dataList.get(index));
            //获取该日期对应的新闻数据
            if(newsDate.contains(dataList.get(index).get(0))) {
                for(TopDetailNew news : newsList)
                    if(news.getPublishdate().contains(dataList.get(index).get(0).toString()))
                        focusData.setNews(news);
            }
            if(null!=onFocusChangeListener)
                onFocusChangeListener.onfocus(focusData);
        }
        invalidate();
    }

    private FocusData focusData;
    private OnFocusChangeListener onFocusChangeListener;
    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
    }
    public interface OnFocusChangeListener{
        void onfocus(FocusData focusData);
    }

    /**绘制图表基本框架*/
    @Override
    public void drawDefult(Canvas canvas) {
        if(null==linePointList || linePointList.size()<=0)
            return;
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(defColor);
        drawGrid(canvas);
        drawXLable(canvas);
        drawYLable(canvas);
    }

    /**绘制debug辅助线*/
    @Override
    public void drawDebug(Canvas canvas) {
        super.drawDebug(canvas);
    }

    /**绘制图表*/
    @Override
    public void drawChart(Canvas canvas) {
        if(null==linePointList || linePointList.size()<=0)
            return;
        drawDataPath(canvas);
        drawFocus(canvas);
    }

    /**绘制X轴方向辅助网格*/
    private void drawGrid(Canvas canvas){
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(defColor);
        canvas.drawLine(rectChart.left, rectChart.bottom, rectChart.right, rectChart.bottom, paint);
    }
    /**绘制X轴刻度*/
    private void drawXLable(Canvas canvas){
        paintLabel.setTextSize(textSize);
        paintLabel.setColor(textColor);
        for(DataPoint lable : lableXPointList){
            canvas.drawText(lable.getValueX(), lable.getPoint().x, lable.getPoint().y, paintLabel);
        }
    }
    /**绘制Y轴刻度*/
    private void drawYLable(Canvas canvas){
        float yMarkSpace = (rectChart.bottom - rectChart.top)/(YMARK_NUM-1);
        paintLabel.setTextSize(textSize);
        paintLabel.setColor(textColor);
        for (int i = 0; i < YMARK_NUM; i++) {
            canvas.drawText((int) (YMARK_MIN + i * YMARK) + "",
                    rectChart.left - textSpaceY - FontUtil.getFontlength(paintLabel, (int) (YMARK_MIN + i * YMARK) + ""),
                    rectChart.bottom - yMarkSpace * i - lableHeight/2 + lableLead, paintLabel);
        }
    }
    /**绘制曲线*/
    private void drawDataPath(Canvas canvas) {
        Path path = new Path();
        PointF lastPoint = null;
        List<DataPoint> newDatePoint = new ArrayList<>();
        for(int i = 0 ; i<linePointList.size(); i++){
            DataPoint dataPoint = linePointList.get(i);
            if(i == 0){
                if(animType == AnimType.LEFT_TO_RIGHT){
                    path.moveTo(rectChart.left+(dataPoint.getPoint().x-rectChart.left)*animPro, dataPoint.getPoint().y);
                }else if(animType == AnimType.BOTTOM_TO_TOP){
                    path.moveTo(dataPoint.getPoint().x, rectChart.bottom-(rectChart.bottom- dataPoint.getPoint().y)* animPro);
                }else{
                    path.moveTo(dataPoint.getPoint().x, dataPoint.getPoint().y);
                }
            }else{
                //quadTo：二阶贝塞尔曲线连接前后两点，这样使得曲线更加平滑
                if(animType == AnimType.LEFT_TO_RIGHT){
                    path.quadTo(rectChart.left+(lastPoint.x-rectChart.left)*animPro, lastPoint.y,
                            rectChart.left+(dataPoint.getPoint().x-rectChart.left)*animPro , dataPoint.getPoint().y);
                }else if(animType == AnimType.BOTTOM_TO_TOP){
                    path.quadTo(lastPoint.x, rectChart.bottom-(rectChart.bottom-lastPoint.y)* animPro,
                            dataPoint.getPoint().x, rectChart.bottom-(rectChart.bottom- dataPoint.getPoint().y)* animPro);
                }else if(animType == AnimType.SLOW_DRAW){
                    if(i>linePointList.size()*animPro)
                        break;
                    path.quadTo(lastPoint.x, lastPoint.y, dataPoint.getPoint().x, dataPoint.getPoint().y);
                }else{
                    path.quadTo(lastPoint.x, lastPoint.y, dataPoint.getPoint().x, dataPoint.getPoint().y);
                }
            }
            lastPoint = dataPoint.getPoint();
            //绘制新闻点
            //["2020-09-03",2359.21]
            if(newsDate.contains(dataList.get(i).get(0))){
                newDatePoint.add(dataPoint);
            }
        }
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(lineSize);
        paint.setColor(lineColor);
        canvas.drawPath(path, paint);
        //绘制新闻点
        int radius = DensityUtil.dip2px(getContext(), 4);
        for(DataPoint point : newDatePoint){
//            LogUtil.v(TAG, "绘制新闻点："+point.getPoint());
            paint.setStyle(Paint.Style.STROKE);
            //新闻点圆圈宽度
            paint.setStrokeWidth(DensityUtil.dip2px(getContext(), 3));
            paint.setColor(newCircleColor);
            canvas.drawCircle(point.getPoint().x, point.getPoint().y, radius,paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(DensityUtil.dip2px(getContext(), 3));
            paint.setColor(Color.WHITE);
            canvas.drawCircle(point.getPoint().x, point.getPoint().y, radius/2,paint);
        }
    }
    /**绘制焦点*/
    private void drawFocus(Canvas canvas){
        if(!onFocus || null==focusData)
            return;
        //竖直线
        PathEffect effects = new DashPathEffect(new float[]{15,10,15,10},0);
        paintEffect.setStyle(Paint.Style.STROKE);
        paintEffect.setStrokeWidth(focusLineSize);
        paintEffect.setColor(focusLineColor);
        paintEffect.setPathEffect(effects);
        Path path = new Path();
        path.moveTo(focusData.getPoint().getPoint().x, rectChart.bottom);
        path.lineTo(focusData.getPoint().getPoint().x, rectChart.top);
        canvas.drawPath(path , paintEffect);
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

    /**焦点数据*/
    public static class FocusData {
        private DataPoint point;
        private List<Object> data;
        private TopDetailNew news;

        public DataPoint getPoint() {
            return point;
        }

        public void setPoint(DataPoint point) {
            this.point = point;
        }

        public List<Object> getData() {
            return data;
        }

        public void setData(List<Object> data) {
            this.data = data;
        }

        public TopDetailNew getNews() {
            return news;
        }

        public void setNews(TopDetailNew news) {
            this.news = news;
        }
    }
}
