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
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.sss;
import com.openxu.cview.xmstock.BaseChart;
import com.openxu.cview.xmstock.bean.DataPoint;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;
import com.openxu.utils.NumberFormatUtil;

import java.util.ArrayList;
import java.util.List;

import static com.openxu.utils.NumberFormatUtil.formattedDecimal;

/**
 * autour : xiami
 * date : 2020/11/13 14:26
 * className : GlzsLinesChart
 * version : 1.0
 * description : 2. 概念走势图
 */
public class GlzsLinesChart extends BaseChart {
    //设置数据  概念走势图， 每个元素表示：时间、概念、热度 [20200803,"1582.08","30.00"],
    private List<List<Object>> dataList;
    private String[] lableXList;
    //计算后的数据
    private List<DataPoint> lableXPointList;
    private List<List<DataPoint>> linePointList;
    //y轴刻度设置
    private YAxisMark yLeft, yRight;
    /**可以设置的属性*/
    //设置线条颜色(蓝色，红色)
    private int[] lineColor = new int[]{Color.parseColor("#3d7cc9"),
            Color.parseColor("#d74b3c")};
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
    private int focusLineColor = Color.parseColor("#5E5E5E");
    private int focusLineSize = DensityUtil.dip2px(getContext(), 1f);
    //焦点面板矩形，只记录矩形宽高
    private int foucsRectTextSpace = DensityUtil.dip2px(getContext(), 3);
    private int foucsRectSpace = DensityUtil.dip2px(getContext(), 6);
    private float foucsRectWidth;
    private float foucsRectHeight;

    public enum AnimType{
        LEFT_TO_RIGHT,   //动画从左往右
        BOTTOM_TO_TOP,   //动画从下网上上升
        SLOW_DRAW        //动画缓慢绘制
    }
    /**需要计算相关值*/
//    private float lableLead, lableHeight;

    public GlzsLinesChart(Context context) {
        this(context, null);
    }
    public GlzsLinesChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public GlzsLinesChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        dataList = new ArrayList<>();
        touchEnable = true;
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
        //[20200803,"1582.08","30.00"],
        Log.v(TAG, "---->"+dataList.get(0));
        //重组X轴刻度, 取其中5个日期
        lableXList = new String[5];
        int part = dataList.size()/4;
        lableXList[0] = dataList.get(0).get(0).toString();    //第一天
//        Log.v(TAG, 0+ "---->"+Integer.parseInt(dataList.get(0).get(0).toString()));
        lableXList[1] = dataList.get(part).get(0).toString();    //
        lableXList[2] = dataList.get(dataList.size()/2).get(0).toString();   //中间一天
        lableXList[3] = dataList.get(part*3).get(0).toString();   //
        lableXList[4] = dataList.get(dataList.size()-1).get(0).toString();   //最后一天
        for(int i = 0; i<lableXList.length; i++){
            lableXList[i] = formatDate(lableXList[i]);
        }
        yLeft = new YAxisMark.Builder(getContext())
                .lableNum(4)
                .textSize(textSizeX)
                .textColor(lineColor[0])
                .build();
        yRight = new YAxisMark.Builder(getContext())
                .lableNum(4)
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
        //时间、概念、热度 [20200803,"1582.08","30.00"],
        //概念
        calYLable(yLeft, 1);
        //热度
        calYLable(yRight, 2);
        /**①、计算字体相关以及图表原点坐标*/
        paintLabel.setTextSize(textSizeX);
        float xlableHeight = FontUtil.getFontHeight(paintLabel);
        float xlableLead = FontUtil.getFontLeading(paintLabel);
        float xFoucsLenght = FontUtil.getFontlength(paintLabel, lableXList[0]);
        paintLabel.setTextSize(yLeft.textSize);
        float yLeftlableHeight = FontUtil.getFontHeight(paintLabel);
        float yLeftlableMaxLength = FontUtil.getFontlength(paintLabel, (int)yLeft.cal_mark_max+"");
        float yLeftFoucsLenght = FontUtil.getFontlength(paintLabel, "概念:"+ NumberFormatUtil.formattedDecimal(yLeft.cal_mark_max));
        Log.w(TAG, "计算面板最长字符串："+"概念:"+yLeft.cal_mark_max);
        paintLabel.setTextSize(yRight.textSize);
        float yRightlableHeight = FontUtil.getFontHeight(paintLabel);
        float yRightlableMaxLength = FontUtil.getFontlength(paintLabel, (int)yRight.cal_mark_max+"");
        float yRightFoucsLenght = FontUtil.getFontlength(paintLabel, "热度:"+NumberFormatUtil.formattedDecimal(yRight.cal_mark_max));
        Log.w(TAG, "计算面板最长字符串："+"热度:"+yRight.cal_mark_max);
        //图表主体矩形
        rectChart = new RectF(getPaddingLeft() + yLeft.textSpace + yLeftlableMaxLength,
                getPaddingTop() + Math.max(yLeftlableHeight, yRightlableHeight)/2,
                getMeasuredWidth()-getPaddingRight()-yRight.textSpace - yRightlableMaxLength,
                getMeasuredHeight()-getPaddingBottom() - xlableHeight - textSpaceX);
        //焦点面板
        //10-16
        //概念：1234.02
        //热度：45.00
        foucsRectWidth = Math.max(xFoucsLenght, Math.max(yLeftFoucsLenght, yRightFoucsLenght)) + foucsRectSpace*2;
        foucsRectHeight = xlableHeight + yLeftlableHeight + yRightlableHeight + foucsRectTextSpace*2 + foucsRectSpace*2;
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
        //两条线
        linePointList.add(new ArrayList<>());
        linePointList.add(new ArrayList<>());
        float oneSpace = (rectChart.right - rectChart.left) / (dataList.size()-1);
        for(int i = 0; i < dataList.size(); i++){
            //每个元素表示：时间、概念、热度 [20200803,"1582.08","30.00"],
            List<Object> onePart = dataList.get(i);
            try {
                //第一条线的数据
                float valueY = Float.parseFloat(onePart.get(1).toString());
                PointF point = new PointF(rectChart.left + i * oneSpace,
                        rectChart.bottom -
                                (rectChart.bottom-rectChart.top)/(yLeft.cal_mark_max - yLeft.cal_mark_min) * (valueY-yLeft.cal_mark_min));
                linePointList.get(0).add(new DataPoint(onePart.get(0).toString(), valueY, point));
                //第二条线的数据
                valueY = Float.parseFloat(onePart.get(2).toString());
//                Log.w(TAG, i+"计算右侧y值"+valueY);
                point = new PointF(rectChart.left + i * oneSpace,
                        rectChart.bottom - (rectChart.bottom-rectChart.top)/(yRight.cal_mark_max - yRight.cal_mark_min) * (valueY-yRight.cal_mark_min));
                linePointList.get(1).add(new DataPoint(onePart.get(0).toString(), valueY, point));
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
        onFocus = (null != point);
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
        invalidate();
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
        paintLabel.setColor(yRight.textColor);
        lableHeight = FontUtil.getFontHeight(paintLabel);
        lableLead = FontUtil.getFontLeading(paintLabel);
        for (int i = 0; i < yRight.lableNum; i++) {
            String text = (int) (yRight.cal_mark_min + i * yRight.cal_mark) + "";
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

        for(int j = 0; j < linePointList.size(); j++){
            List<DataPoint> lineList = linePointList.get(j);
            paint.setColor(lineColor[j]);
            //一条一条的绘制
            Path path = new Path();
            PointF lastPoint = null;
            for(int i = 0 ; i<lineList.size(); i++){
                DataPoint dataPoint = lineList.get(i);
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
                        if(i>lineList.size()*animPro)
                            break;
                        path.quadTo(lastPoint.x, lastPoint.y, dataPoint.getPoint().x, dataPoint.getPoint().y);
                    }else{
                        path.quadTo(lastPoint.x, lastPoint.y, dataPoint.getPoint().x, dataPoint.getPoint().y);
                    }
                }
                lastPoint = dataPoint.getPoint();
            }
            canvas.drawPath(path, paint);
        }
    }

    /**绘制焦点*/
    private void drawFocus(Canvas canvas){
        if(!onFocus || null==focusData)
            return;
        PointF point1 = focusData.getPoints().get(0).getPoint();
        PointF point2 = focusData.getPoints().get(1).getPoint();
        //绘制竖直虚线
        PathEffect effects = new DashPathEffect(new float[]{15,10,15,10},0);
        paintEffect.setStyle(Paint.Style.STROKE);
        paintEffect.setStrokeWidth(focusLineSize);
        paintEffect.setColor(focusLineColor);
        paintEffect.setPathEffect(effects);
        Path path = new Path();
        path.moveTo(point1.x, rectChart.bottom);
        path.lineTo(point1.x, rectChart.top);
        canvas.drawPath(path , paintEffect);
//        canvas.drawLine(focusData.getPoints().get(0).getPoint().x, rectChart.bottom, focusData.getPoints().get(0).getPoint().x, rectChart.top, paint);
        //绘制水平线
//        canvas.drawLine(rectChart.left, focusData.getPoints().get(0).getPoint().y, rectChart.right, focusData.getPoints().get(0).getPoint().y, paint);
//        canvas.drawLine(rectChart.left, focusData.getPoints().get(1).getPoint().y, rectChart.right, focusData.getPoints().get(1).getPoint().y, paint);
        //绘制焦点
        paint.setAntiAlias(true);
        //半透明圆圈
        int radius = DensityUtil.dip2px(getContext(), 8);
        int strokeWidth = DensityUtil.dip2px(getContext(), 3);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(lineColor[0]);
        paint.setAlpha(80);  //透明度
        canvas.drawCircle(point1.x, point1.y, radius,paint);
        paint.setColor(lineColor[1]);
        paint.setAlpha(80);
        canvas.drawCircle(point2.x, point2.y, radius,paint);
        //里面
        int radius1 = radius-strokeWidth/2 - 4;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(lineColor[0]);
        paint.setAlpha(255);
        canvas.drawCircle(point1.x, point1.y, radius1, paint);
        paint.setColor(lineColor[1]);
        paint.setAlpha(255);
//        canvas.rotate(-90);
//        canvas.drawRect(new Rect((int), (int)point2.y - radius1, (int)point2.x+radius1, (int)point2.y+radius1), paint);
        radius1 = radius-strokeWidth/2;
        path.reset();
        path.moveTo(point2.x - radius1, point2.y);
        path.lineTo(point2.x, point2.y - radius1);
        path.lineTo(point2.x + radius1, point2.y);
        path.lineTo(point2.x, point2.y + radius1);
        canvas.drawPath(path , paint);
        //面板
        radius1 = radius*2;
        boolean showLeft = point1.x-rectChart.left > (rectChart.right - rectChart.left)/2;
        RectF rect = new RectF(
            showLeft?point1.x - foucsRectWidth - radius1:point1.x + radius1,
                rectChart.top + (rectChart.bottom - rectChart.top)/2 - foucsRectHeight/2,
                showLeft? point1.x - radius1 : point1.x + foucsRectWidth + radius1,
                rectChart.top + (rectChart.bottom - rectChart.top)/2 + foucsRectHeight/2
        );
        paint.setColor(lineColor[0]);
        strokeWidth = DensityUtil.dip2px(getContext(), 0.8f);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        path.reset();
        path.moveTo(rect.left, rect.top);
        path.lineTo(rect.right, rect.top);
        path.lineTo(rect.right, rect.bottom);
        path.lineTo(rect.left, rect.bottom);
        path.close();
        canvas.drawPath(path , paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setAlpha(210);
        canvas.drawRect(rect , paint);
        //面板中的文字
        paintLabel.setTextSize(textSizeX);
        paintLabel.setColor(textColorX);
        float lableHeight = FontUtil.getFontHeight(paintLabel);
        float lableLead = FontUtil.getFontLeading(paintLabel);
        float top = rect.top + foucsRectSpace;
        float left = showLeft?rect.left + foucsRectSpace:rect.left + foucsRectSpace;
        canvas.drawText(formatDate(focusData.getPoints().get(0).getValueX()),
                left, top + lableLead, paintLabel);
        top += (lableHeight+foucsRectTextSpace);
        paintLabel.setTextSize(yLeft.textSize);
        paintLabel.setColor(yLeft.textColor);
        lableHeight = FontUtil.getFontHeight(paintLabel);
        lableLead = FontUtil.getFontLeading(paintLabel);
        canvas.drawText("概念:"+NumberFormatUtil.formattedDecimal(focusData.getPoints().get(0).getValueY()),
                left, top + lableLead, paintLabel);
        top += (lableHeight+foucsRectTextSpace);
        paintLabel.setTextSize(yRight.textSize);
        paintLabel.setColor(yRight.textColor);
        lableLead = FontUtil.getFontLeading(paintLabel);
        canvas.drawText("热度:"+NumberFormatUtil.formattedDecimal(focusData.getPoints().get(1).getValueY()),
                left, top + lableLead, paintLabel);
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
    private void calYLable(YAxisMark axisMark, int index){
        float redundance = 1.01f;  //y轴最大和最小值冗余
        axisMark.cal_mark_max =  Float.MIN_VALUE;    //Y轴刻度最大值
        axisMark.cal_mark_min =  Float.MAX_VALUE;    //Y轴刻度最小值
        //设置数据  概念走势图， 每个元素表示：时间、概念、热度 [20200803,"1582.08","30.00"],
        for(List<Object> data : dataList){
            try {
                axisMark.cal_mark = Float.parseFloat(data.get(index).toString());
                if(axisMark.cal_mark>axisMark.cal_mark_max)
                    axisMark.cal_mark_max = axisMark.cal_mark;
                if(axisMark.cal_mark<axisMark.cal_mark_min)
                    axisMark.cal_mark_min = axisMark.cal_mark;
            }catch (Exception e){
            }
        }
        LogUtil.i(TAG, "Y轴真实axisMark.cal_mark_min="+axisMark.cal_mark_min+"   axisMark.cal_mark_max="+axisMark.cal_mark_max);
        if(axisMark.cal_mark_max>0)
            axisMark.cal_mark_max *= redundance;
        else
            axisMark.cal_mark_max /= redundance;
        if(axisMark.cal_mark_min>0)
            axisMark.cal_mark_min /= redundance;
        else
            axisMark.cal_mark_min *= redundance;
        if(index == 2)
            axisMark.cal_mark_min = 0;
        axisMark.cal_mark = (axisMark.cal_mark_max-axisMark.cal_mark_min)/(axisMark.lableNum - 1);
        LogUtil.i(TAG, "Y轴axisMark.cal_mark_min="+axisMark.cal_mark_min+"   axisMark.cal_mark_max="+axisMark.cal_mark_max+"   axisMark.cal_mark="+axisMark.cal_mark);
    }

}
