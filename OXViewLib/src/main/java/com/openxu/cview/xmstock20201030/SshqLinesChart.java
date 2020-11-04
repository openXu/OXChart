package com.openxu.cview.xmstock20201030;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.xmstock.BaseChart;
import com.openxu.cview.xmstock.bean.DataPoint;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;
import com.openxu.utils.NumberFormatUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * autour : xiami
 * date : 2020/11/13 14:26
 * className : SshqLinesChart
 * version : 1.0
 * description : 3. 实时行情图
 */
public class SshqLinesChart extends BaseChart {

    //实时行情走势图，每个元素字段分别是：时间、现价、均价 、跌涨浮["0930",1639.83,1625.58,"0.10"]
    private List<List<Object>> dataList;

    private String[] lableXList = new String[]{"09:30", "11:30/13:00", "15:00"};
    //计算后的数据
    private List<DataPoint> lableXPointList;
    private List<DataPoint> linePointList;
    //y轴刻度设置
    private YAxisMark yLeft, yRight;
    /**可以设置的属性*/
    //设置线条颜色(蓝色，渐变色开始)
    private int[] lineColor = new int[]{Color.parseColor("#3d7cc9"),
            Color.parseColor("#3d7cc9")};
    //设置曲线粗细
    private int lineSize = DensityUtil.dip2px(getContext(), 1.5f);
    //设置坐标文字大小
    private int textSizeX = (int)getResources().getDimension(R.dimen.ts_chart_xy);
    //设置坐标文字颜色
    private int textColorX = getResources().getColor(R.color.tc_chart_xy);
    //设置X坐标字体与横轴的距离
    private int textSpaceX = DensityUtil.dip2px(getContext(), 5);
    //设置动画类型
    private AnimType animType = AnimType.SLOW_DRAW;
    //设置焦点线颜色 及 粗细
    private int focusLineColor = getResources().getColor(R.color.tc_chart_focus_line);
    private int focusLineSize = DensityUtil.dip2px(getContext(), 0.8f);

    public enum AnimType{
        LEFT_TO_RIGHT,   //动画从左往右
        BOTTOM_TO_TOP,   //动画从下网上上升
        SLOW_DRAW        //动画缓慢绘制
    }
    /**需要计算相关值*/
//    private float lableLead, lableHeight;

    public SshqLinesChart(Context context) {
        this(context, null);
    }
    public SshqLinesChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public SshqLinesChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        dataList = new ArrayList<>();
        touchEnable = false;
    }

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
        isLoading = false;
        invalidate();
    }

    //20201028 --> 10-28
    private String formatDate(String date){
        String str = date.substring(4, 6)+"-"+date.substring(6);
        Log.v(TAG, date+ "---->"+str);
        return str;
    }
    /**
     * 设置数据
     */
    public void setData(List<List<Object>> dataList){
        if(null==dataList)
            return;
        this.dataList.clear();
        this.dataList.addAll(dataList);
        //实时行情走势图，每个元素字段分别是：时间、现价、均价 、跌涨浮["0930",1639.83,1625.58,"0.10"]
        yLeft = new YAxisMark.Builder(getContext())
                .lableNum(3)
                .textSize(textSizeX)
                .textColor(lineColor[0])
                .build();
        yRight = new YAxisMark.Builder(getContext())
                .lableNum(3)
                .textSize(textSizeX)
                .textColor(lineColor[1])
                .build();
        if(getMeasuredWidth()>0) {
            evaluatorByData();
            startDraw = false;
            isLoading = false;
            invalidate();
        }
    }
    /**设置数据后，计算相关值*/
    private void evaluatorByData(){
        if(dataList.size()<=0)
            return;
        /**③、计算Y刻度最大值和最小值以及幅度*/
        //实时行情走势图，每个元素字段分别是：时间、现价、均价 、跌涨浮["0930",1639.83,1625.58,"0.10"]
        calYLable();
        /**①、计算字体相关以及图表原点坐标*/
        paintLabel.setTextSize(textSizeX);
        float xlableHeight = FontUtil.getFontHeight(paintLabel);
        float xlableLead = FontUtil.getFontLeading(paintLabel);
        paintLabel.setTextSize(yLeft.textSize);
        float yLeftlableHeight = FontUtil.getFontHeight(paintLabel);
        float yLeftlableMaxLength = FontUtil.getFontlength(paintLabel, (int)yLeft.cal_mark_max+"");
        paintLabel.setTextSize(yRight.textSize);
        float yRightlableHeight = FontUtil.getFontHeight(paintLabel);
        String text = NumberFormatUtil.formattedDecimalToPercentage(yRight.cal_mark_min);
        float yRightlableMaxLength = FontUtil.getFontlength(paintLabel, text);
        //图表主体矩形
        rectChart = new RectF(getPaddingLeft() + yLeft.textSpace + yLeftlableMaxLength,
                getPaddingTop() + Math.max(yLeftlableHeight, yRightlableHeight)/2,
                getMeasuredWidth()-getPaddingRight()-yRight.textSpace - yRightlableMaxLength,
                getMeasuredHeight()-getPaddingBottom() - xlableHeight - textSpaceX);
        Log.w(TAG, "计算图表矩形："+rectChart);
        /**②、计算X标签绘制坐标*/
        float lableXSpace = 0;
        for(String lableX : lableXList){
            lableXSpace += FontUtil.getFontlength(paintLabel, lableX);
        }
        lableXSpace = (rectChart.right - rectChart.left - lableXSpace)/(lableXList.length -1);
        lableXPointList = new ArrayList<>();
        if(lableXSpace>0){
            float left = rectChart.left;
            for(int i = 0; i<lableXList.length; i++){
                String lableX = lableXList[i];
                lableXPointList.add(new DataPoint(lableX, 0, new PointF(
                        left,rectChart.bottom + textSpaceX + xlableLead)));
                left += (FontUtil.getFontlength(paintLabel, lableXList[i])+lableXSpace);
            }
        }else{
            //如果X轴标签字体过长的情况需要特殊处理
            float oneWidth = (rectChart.right - rectChart.left)/lableXList.length;
            for(int i = 0; i<lableXList.length; i++){
                String lableX = lableXList[i];
                float xLen = FontUtil.getFontlength(paintLabel, lableX);
                lableXPointList.add(new DataPoint(lableX, 0, new PointF(
                        rectChart.left+i*oneWidth+(oneWidth-xLen)/2,rectChart.bottom + textSpaceX + xlableLead)));
            }
        }
        /**④、计算点的坐标，如果有动画的情况下，边绘制边计算会耗费性能，所以先计算*/
        linePointList = new ArrayList<>();
        float oneSpace = (rectChart.right - rectChart.left) / (dataList.size()-1);
        for(int i = 0; i < dataList.size(); i++){
            // //实时行情走势图，每个元素字段分别是：时间、现价、均价 、跌涨浮["0930",1639.83,1625.58,"0.10"]
            List<Object> onePart = dataList.get(i);
            try {
                //第一条线的数据
                float valueY = Float.parseFloat(onePart.get(1).toString());
                PointF point = new PointF(rectChart.left + i * oneSpace,
                        rectChart.bottom -
                          (rectChart.bottom-rectChart.top)/(yLeft.cal_mark_max - yLeft.cal_mark_min) * (valueY-yLeft.cal_mark_min));
                linePointList.add(new DataPoint(onePart.get(0).toString(), valueY, point));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private FocusData focusData;
    @Override
    protected void onTouchMoved(PointF point) {
        if(null==dataList)
            return;
       /* onFocus = (null != point);
        if(null != point && null!=dataList && dataList.size()>0) {
            //避免滑出
            if(point.x > linePointList.get(0).get(dataList.size()-1).getPoint().x)
                point.x = linePointList.get(0).get(dataList.size()-1).getPoint().x;
            if(point.x < linePointList.get(0).get(0).getPoint().x)
                point.x = linePointList.get(0).get(0).getPoint().x;
            //获取焦点对应的数据的索引
            int index = (int) ((point.x - rectChart.left) * dataList.size() / (rectChart.right - rectChart.left));
            LogUtil.e(getClass().getSimpleName(), "========焦点索引："+index+"   数据总数："+dataList.size()+"  线条数量："+linePointList.size());
            index = Math.max(0, Math.min(index, dataList.size() - 1));
            focusData = new FocusData();
            focusData.setData(dataList.get(index));
            focusData.setPoints(new ArrayList<>());
            focusData.getPoints().add(linePointList.get(0).get(index));
            focusData.getPoints().add(linePointList.get(1).get(index));
            if(null!=onFocusChangeListener)
                onFocusChangeListener.onfocus(focusData);
        }
        invalidate();*/
    }

    private OnFocusChangeListener onFocusChangeListener;
    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
    }
    public interface OnFocusChangeListener{
        public void onfocus(FocusData focusData);
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
        float yMarkSpace = (rectChart.bottom - rectChart.top)/(yLeft.lableNum-1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(defColor);
        paintEffect.setStyle(Paint.Style.STROKE);
        paintEffect.setStrokeWidth(lineWidth);
        paintEffect.setColor(defColor);
//        canvas.drawLine(rectChart.left, rectChart.top, rectChart.left, rectChart.bottom, paint);
//        canvas.drawLine(rectChart.right, rectChart.top, rectChart.right, rectChart.bottom, paint);
        PathEffect effects = new DashPathEffect(new float[]{15,6,15,6},0);
        Path path = new Path();
        for (int i = 0; i < yLeft.lableNum; i++) {
            path.reset();
            path.moveTo(rectChart.left, rectChart.bottom-yMarkSpace*i);
            path.lineTo(rectChart.right,rectChart.bottom-yMarkSpace*i);
            paintEffect.setPathEffect(effects);
            canvas.drawPath(path, paintEffect);
        }
    }
    /**绘制X轴刻度*/
    private void drawXLable(Canvas canvas){
        paintLabel.setTextSize(textSizeX);
        paintLabel.setColor(textColorX);
        for(DataPoint lable : lableXPointList){
            canvas.drawText(lable.getValueX(), lable.getPoint().x, lable.getPoint().y, paintLabel);
        }
    }
    /**绘制Y轴刻度*/
    private void drawYLable(Canvas canvas){
        float yMarkSpace = (rectChart.bottom - rectChart.top)/(yLeft.lableNum-1);
        paintLabel.setTextSize(yLeft.textSize);
        paintLabel.setColor(yLeft.textColor);
        float lableHeight = FontUtil.getFontHeight(paintLabel);
        float lableLead = FontUtil.getFontLeading(paintLabel);
        for (int i = 0; i < yLeft.lableNum; i++) {
            String text = (int) (yLeft.cal_mark_min + i * yLeft.cal_mark) + "";
            canvas.drawText(text,
                    rectChart.left - yLeft.textSpace - FontUtil.getFontlength(paintLabel, text),
                    rectChart.bottom - yMarkSpace * i - lableHeight/2 + lableLead, paintLabel);
        }
        paintLabel.setTextSize(yRight.textSize);
        lableHeight = FontUtil.getFontHeight(paintLabel);
        lableLead = FontUtil.getFontLeading(paintLabel);
        for (int i = 0; i < yRight.lableNum; i++) {
            //最后一个涨幅值刻度为红色
            paintLabel.setColor(i==yRight.lableNum-1?Color.RED: yRight.textColor);
            String text = NumberFormatUtil.formattedDecimal(yRight.cal_mark_min + i * yRight.cal_mark)+"%";
            canvas.drawText(text,
                    rectChart.right + yRight.textSpace,
                    rectChart.bottom - yMarkSpace * i - lableHeight/2 + lableLead, paintLabel);
        }
    }
    /**绘制曲线*/
    private void drawDataPath(Canvas canvas) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(lineSize);
        paint.setColor(lineColor[0]);
        //曲线路径
        Path path = new Path();
        //渐变色路径
        Path jianBianPath = new Path();
        PointF lastPoint = null;
        PointF lastDrawPoint = new PointF();
        float minYValue = 0;
        for(int i = 0 ; i<linePointList.size(); i++){
            DataPoint dataPoint = linePointList.get(i);
            if(i == 0){
                jianBianPath.moveTo(rectChart.left, rectChart.bottom);
                if(animType == AnimType.LEFT_TO_RIGHT){
                    lastDrawPoint.x = rectChart.left+(dataPoint.getPoint().x-rectChart.left)*animPro;
                    lastDrawPoint.y = dataPoint.getPoint().y;
                }else if(animType == AnimType.BOTTOM_TO_TOP){
                    lastDrawPoint.x = dataPoint.getPoint().x;
                    lastDrawPoint.y = rectChart.bottom-(rectChart.bottom- dataPoint.getPoint().y)* animPro;
                }else{
                    lastDrawPoint.x = dataPoint.getPoint().x;
                    lastDrawPoint.y = dataPoint.getPoint().y;
                }
                minYValue = Math.min(lastDrawPoint.y,minYValue);
                path.moveTo(lastDrawPoint.x, lastDrawPoint.y);
                jianBianPath.lineTo(lastDrawPoint.x, lastDrawPoint.y);
            }else{
                //quadTo：二阶贝塞尔曲线连接前后两点，这样使得曲线更加平滑
                if(animType == AnimType.LEFT_TO_RIGHT){
                    lastDrawPoint.x = rectChart.left+(dataPoint.getPoint().x-rectChart.left)*animPro;
                    lastDrawPoint.y = dataPoint.getPoint().y;
                    path.quadTo(rectChart.left+(lastPoint.x-rectChart.left)*animPro, lastPoint.y,
                            lastDrawPoint.x, lastDrawPoint.y);
                }else if(animType == AnimType.BOTTOM_TO_TOP){
                    lastDrawPoint.x =  dataPoint.getPoint().x;
                    lastDrawPoint.y = rectChart.bottom-(rectChart.bottom- dataPoint.getPoint().y)* animPro;
                    path.quadTo(lastPoint.x, rectChart.bottom-(rectChart.bottom-lastPoint.y)* animPro,
                            lastDrawPoint.x, lastDrawPoint.y);
                }else if(animType == AnimType.SLOW_DRAW){
                    if(i>linePointList.size()*animPro)
                        break;
                    lastDrawPoint.x =  dataPoint.getPoint().x;
                    lastDrawPoint.y = dataPoint.getPoint().y;
                    path.quadTo(lastPoint.x, lastPoint.y, lastDrawPoint.x, lastDrawPoint.y);
                }else{
                    lastDrawPoint.x =  dataPoint.getPoint().x;
                    lastDrawPoint.y = dataPoint.getPoint().y;
                    path.quadTo(lastPoint.x, lastPoint.y, lastDrawPoint.x, lastDrawPoint.y);
                }
                minYValue = Math.min(lastDrawPoint.y,minYValue);
                path.moveTo(lastDrawPoint.x, lastDrawPoint.y);
                jianBianPath.lineTo(lastDrawPoint.x, lastDrawPoint.y);
            }
            lastPoint = dataPoint.getPoint();
        }
        canvas.drawPath(path, paint);
        //https://blog.csdn.net/s297165331/article/details/52875624
        jianBianPath.lineTo(lastDrawPoint.x, rectChart.bottom);
        jianBianPath.close();
        //线性渐变：前两个参数是渐变开始的点坐标，第三四个参数是渐变结束的点的坐标；渐变的颜色，渐变颜色的分布，模式
        Shader mShader = new LinearGradient(rectChart.left, minYValue ,rectChart.left, rectChart.bottom,
                new int[] {lineColor[1],Color.TRANSPARENT},null,Shader.TileMode.CLAMP);
        Log.w(TAG, "从"+rectChart.left+"*"+minYValue+"->"+rectChart.left+"*"+rectChart.bottom+"渐变");
        paintEffect.setStyle(Paint.Style.FILL);
        paintEffect.setColor(defColor);
        paintEffect.setShader(mShader);
        canvas.drawPath(jianBianPath, paintEffect);
        paintEffect.setShader(null);
    }

    /**绘制焦点*/
    private void drawFocus(Canvas canvas){
        if(!onFocus || null==focusData)
            return;
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(focusLineSize);
        paint.setColor(focusLineColor);
        canvas.drawLine(focusData.getPoints().get(0).getPoint().x, rectChart.bottom, focusData.getPoints().get(0).getPoint().x, rectChart.top, paint);
        canvas.drawLine(rectChart.left, focusData.getPoints().get(0).getPoint().y, rectChart.right, focusData.getPoints().get(0).getPoint().y, paint);
        canvas.drawLine(rectChart.left, focusData.getPoints().get(1).getPoint().y, rectChart.right, focusData.getPoints().get(1).getPoint().y, paint);
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
        private List<DataPoint> points;
        private List<Object> data;

        public List<DataPoint> getPoints() {
            return points;
        }

        public void setPoints(List<DataPoint> points) {
            this.points = points;
        }

        public List<Object> getData() {
            return data;
        }
        public void setData(List<Object> data) {
            this.data = data;
        }
    }

    /**
     * 根据传入的Y轴信息，从数据中获取最小最大值，并获取到Y刻度点值
     */
    private void calYLable(){
        float redundance = 1.01f;  //y轴最大和最小值冗余
        yLeft.cal_mark_max =  Float.MIN_VALUE;    //Y轴刻度最大值
        yLeft.cal_mark_min =  Float.MAX_VALUE;    //Y轴刻度最小值
        yRight.cal_mark_max =  Float.MIN_VALUE;    //记录最大涨幅
        yRight.cal_mark_min =  Float.MAX_VALUE;    //记录最大跌幅
        //实时行情走势图，每个元素字段分别是：时间、现价、均价 、跌涨浮["0930",1639.83,1625.58,"0.10"]
        float price, downUp;
        for(List<Object> data : dataList){
            price = Float.parseFloat(data.get(1).toString());
            downUp = Float.parseFloat(data.get(3).toString());
            yLeft.cal_mark_max = Math.max(yLeft.cal_mark_max, price);
            yLeft.cal_mark_min = Math.min(yLeft.cal_mark_min, price);
            yRight.cal_mark_max = Math.max(yRight.cal_mark_max, downUp);
            yRight.cal_mark_min = Math.min(yRight.cal_mark_min, downUp);
        }
        LogUtil.i(TAG, "Y轴真实yLeft.cal_mark_min="+yLeft.cal_mark_min+"   yLeft.cal_mark_max="+yLeft.cal_mark_max);
        LogUtil.i(TAG, "Y轴真实yRight.cal_mark_min="+yRight.cal_mark_min+"   yRight.cal_mark_max="+yRight.cal_mark_max);
        yLeft.cal_mark_max *= redundance;
        yLeft.cal_mark_min /= redundance;
        yLeft.cal_mark = (yLeft.cal_mark_max-yLeft.cal_mark_min)/(yLeft.lableNum - 1);
        //获取正负跌涨浮绝对值最大值
        downUp = Math.max(Math.abs(yRight.cal_mark_max), Math.abs(yRight.cal_mark_min));
        downUp *= redundance;
        yRight.cal_mark_max = downUp;
        yRight.cal_mark_min = -downUp;
        yRight.cal_mark = downUp;
        LogUtil.i(TAG, "yLeft.cal_mark_min="+yLeft.cal_mark_min+"   yLeft.cal_mark_max="+yLeft.cal_mark_max+"  yLeft.cal_mark="+yLeft.cal_mark);
        LogUtil.i(TAG, "yRight.cal_mark_min="+yRight.cal_mark_min+"   yRight.cal_mark_max="+yRight.cal_mark_max+"   yRight.cal_mark="+yRight.cal_mark);
    }

}
