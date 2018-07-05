package com.openxu.cview.xmstock;

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
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.xmstock.bean.DataPoint;
import com.openxu.cview.xmstock.bean.FocusInfo;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;
import com.openxu.utils.NumberFormatUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * autour : xiami
 * date : 2018/3/13 14:26
 * className : LinesChart
 * version : 1.0
 * description : 折线图表
 */
public class LinesChart extends BaseChart {

    //设置数据
    private List<List<String>> dataList;
    private String[] lableXList = new String[]{"09:30", "10:30", "11:30/13:00", "14:00", "15:00"};
    private int dataNumCount = 60 * 4;  //60*4 一分钟一个，四小时
    //计算后的数据
    private List<DataPoint> lableXPointList;
    private List<ArrayList<DataPoint>> linePointList;

    /**可以设置的属性*/
    //设置Y轴刻度数量
    private int YMARK_NUM =  5;
    private YMARK_TYPE yMarkType = YMARK_TYPE.INTEGER;
    //设置线条颜色
    private int[] lineColor = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
    //设置曲线粗细
    private int lineSize = DensityUtil.dip2px(getContext(), 1.5f);
    //曲线数量
    private int LINE_NUM = 0;
    //设置坐标文字大小
    private int textSize = (int)getResources().getDimension(R.dimen.ts_chart_xy);
    //设置坐标文字颜色
    private int textColor = getResources().getColor(R.color.tc_chart_xy);
    //设置X坐标字体与横轴的距离
    private int textSpaceX = DensityUtil.dip2px(getContext(), 5);
    //设置Y坐标字体与横轴的距离
    private int textSpaceY = DensityUtil.dip2px(getContext(), 3);
    //设置动画类型
    private AnimType animType = AnimType.SLOW_DRAW;
    //设置焦点线颜色 及 粗细
    private int focusLineColor = getResources().getColor(R.color.tc_chart_focus_line);
    private int focusLineSize = DensityUtil.dip2px(getContext(), 0.8f);

    public enum YMARK_TYPE{
        PERCENTAGE,  /*百分比*/
        INTEGER
    }
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


    public LinesChart(Context context) {
        this(context, null);
    }
    public LinesChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public LinesChart(Context context, AttributeSet attrs, int defStyle) {
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

    public void setyMarkType(YMARK_TYPE yMarkType) {
        this.yMarkType = yMarkType;
    }

    /***********************************设置属性set方法**********************************/
    /**设置数据的数量，默认是60*4*/
    public void setDataNumCount(int dataNumCount) {
        this.dataNumCount = dataNumCount;
    }
    public void setLineColor(int[] lineColor) {
        this.lineColor = lineColor;
    }
    public void setAnimType(AnimType animType) {
        this.animType = animType;
    }

    public void setLINE_NUM(int LINE_NUM) {
        this.LINE_NUM = LINE_NUM;
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

    /**
     * 设置数据
     * @param dataList 折线数据
     * @param lableXList X轴显示的刻度，默认是9:30-15:00（传null）
     *                    如果需要显示其他的数据，需要重新设置
     */
    public void setData(List<List<String>> dataList, String[] lableXList){
        if(null==dataList)
            return;
        this.dataList.clear();
        this.dataList.addAll(dataList);
        if(null!=lableXList && lableXList.length>0)
            this.lableXList = lableXList;

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
        dataNumCount = dataList.size();
        LogUtil.w(TAG, "总共"+dataNumCount+"条数据");
        /**①、计算字体相关以及图表原点坐标*/
        paintLabel.setTextSize(textSize);
        lableHeight = FontUtil.getFontHeight(paintLabel);
        lableLead =  FontUtil.getFontLeading(paintLabel);
        //图表主体矩形
        rectChart = new RectF(getPaddingLeft(),getPaddingTop() + lableHeight + textSpaceY,
                getMeasuredWidth()-getPaddingRight(),
                getMeasuredHeight()-getPaddingBottom() - lableHeight - textSpaceX);

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
                        left,rectChart.bottom + textSpaceX + lableLead)));
                left += (FontUtil.getFontlength(paintLabel, lableXList[i])+lableXSpace);
            }
        }else{
            //如果X轴标签字体过长的情况需要特殊处理
            float oneWidth = (rectChart.right - rectChart.left)/lableXList.length;
            for(int i = 0; i<lableXList.length; i++){
                String lableX = lableXList[i];
                float xLen = FontUtil.getFontlength(paintLabel, lableX);
                lableXPointList.add(new DataPoint(lableX, 0, new PointF(
                        rectChart.left+i*oneWidth+(oneWidth-xLen)/2,rectChart.bottom + textSpaceX + lableLead)));
            }
        }

        /**③、计算Y刻度最大值和最小值以及幅度*/
        int lineNum = LINE_NUM==0?(dataList.get(0).size()-1):LINE_NUM;
        YMARK_MAX =  Float.MIN_VALUE;    //Y轴刻度最大值
        YMARK_MIN =  Float.MAX_VALUE;    //Y轴刻度最小值
        for(List<String> list : dataList){
            for(int i = 1; i< lineNum+1 ; i++){
                String str = list.get(i);
                try {
                    if(str.contains("%")){
                        str = str.substring(0,str.indexOf("%"));//百分数不能直接强转
                        YMARK = Float.parseFloat(str) /100.0f;
                    }else{
                        YMARK = Float.parseFloat(str);
                    }
//                    LogUtil.d(TAG, str+" Y轴 被识别为Float = "+YMARK);
                    if(YMARK>YMARK_MAX)
                        YMARK_MAX = YMARK;
                    if(YMARK<YMARK_MIN)
                        YMARK_MIN = YMARK;
                }catch (Exception e){
                }
            }

        }
        LogUtil.w(TAG, "Y轴真实YMARK_MIN="+YMARK_MIN+"   YMARK_MAX="+YMARK_MAX);
        if(YMARK_MAX>0)
            YMARK_MAX *= 1.1f;
        else
            YMARK_MAX /= 1.1f;
        if(YMARK_MIN>0)
            YMARK_MIN /= 1.1f;
        else
            YMARK_MIN *= 1.1f;
        if(YMARK_MIN>0)
            YMARK_MIN = 0;

        YMARK = (YMARK_MAX-YMARK_MIN)/(YMARK_NUM - 1);

        if(yMarkType == YMARK_TYPE.INTEGER){
            YMARK = (int)YMARK + 1;
            YMARK_MIN = (int)YMARK_MIN;
            YMARK_MAX = YMARK_MIN+YMARK*(YMARK_NUM-1);
        }else if(yMarkType == YMARK_TYPE.PERCENTAGE){
        }
        LogUtil.i(TAG, "Y轴YMARK_MIN="+YMARK_MIN+"   YMARK_MAX="+YMARK_MAX+"   YMARK="+YMARK);

        /**④、计算点的坐标，如果有动画的情况下，边绘制边计算会耗费性能，所以先计算*/
        //创建集合，用于存放每条线上每个点的坐标数据
        List<String> group = dataList.get(0);
        linePointList = new ArrayList<>();
        for(int i = 0; i<group.size()-1; i++)
            linePointList.add(new ArrayList<DataPoint>());
        float oneSpace = (rectChart.right - rectChart.left) / (dataNumCount-1);
        List<DataPoint> lastDataGroup = null;   //最后一组数据
        for(int i = 0; i < dataList.size(); i++){
            //一组数据，包含每条线的一个item数据
            group  = dataList.get(i);
            for(int j = 1; j < group.size(); j++){
                try {
                    float valueY;
                    if(group.get(j).contains("%"))
                        valueY = Float.parseFloat(group.get(j).substring(0,group.get(j).indexOf("%"))) /100.0f;
                    else
                        valueY = Float.parseFloat(group.get(j));

                    PointF point = new PointF();
                    //只有需要绘制的线才计算坐标
                    if(j<lineNum+1){
                        point.x = rectChart.left + i * oneSpace;
                        //根据最高价和最低价，计算当前数据在图表上Y轴的坐标
                        point.y = rectChart.bottom -
                                (rectChart.bottom-rectChart.top)/(YMARK_MAX - YMARK_MIN) * (valueY-YMARK_MIN);
                    }
                    linePointList.get(j-1).add(new DataPoint(group.get(0), valueY, point));
//                    LogUtil.w(TAG, "绘制"+j+"曲线"+(int)point.x+", "+(int)point.y);

                    /**默认最后一组数据为显示的lable数据*/
                    if(null!=onFocusChangeListener && i==dataList.size()-1){
                        if(null==focusInfo) {
                            focusInfo = new FocusInfo();
                            lastDataGroup = new ArrayList<>();
                            focusInfo.setPoint(point);
                            focusInfo.setFocusData(lastDataGroup);
                        }
                        lastDataGroup.add(new DataPoint(group.get(0), valueY, point));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        if(null!=onFocusChangeListener)
            onFocusChangeListener.onfocus(focusInfo);
    }

    private FocusInfo focusInfo;
    @Override
    protected void onTouchMoved(PointF point) {
        if(null==dataList)
            return;
        onFocus = (null != point);
        if(null != point && null!=dataList && dataList.size()>0) {
            //获取焦点对应的数据的索引
            int index = (int) ((point.x - rectChart.left) * dataNumCount / (rectChart.right - rectChart.left));

            //避免滑出
            List<DataPoint> dataPoints = linePointList.get(0);
            if(point.x > dataPoints.get(dataPoints.size()-1).getPoint().x)
                point.x = dataPoints.get(dataPoints.size()-1).getPoint().x;
            if(point.x < dataPoints.get(0).getPoint().x)
                point.x = dataPoints.get(0).getPoint().x;
            index = Math.max(0, Math.min(index, dataPoints.size() - 1));

            focusInfo = new FocusInfo();
            focusInfo.setPoint(point);
            List<DataPoint> objs = new ArrayList<>();
            for(List<DataPoint> points : linePointList){
                objs.add(points.get(index));
            }
            focusInfo.setFocusData(objs);
            if(null!=onFocusChangeListener)
                onFocusChangeListener.onfocus(focusInfo);
        }
        invalidate();
    }

    private OnFocusChangeListener onFocusChangeListener;
    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
    }
    public interface OnFocusChangeListener{
        public void onfocus(FocusInfo focusInfo);
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
        float yMarkSpace = (rectChart.bottom - rectChart.top)/(YMARK_NUM-1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(defColor);
        paintEffect.setStyle(Paint.Style.STROKE);
        paintEffect.setStrokeWidth(lineWidth);
        paintEffect.setColor(defColor);
        canvas.drawLine(rectChart.left, rectChart.top, rectChart.left, rectChart.bottom, paint);
        canvas.drawLine(rectChart.right, rectChart.top, rectChart.right, rectChart.bottom, paint);

        for (int i = 0; i < YMARK_NUM; i++) {
            if(i==0||i ==YMARK_NUM-1) {   //实线
                canvas.drawLine(rectChart.left, rectChart.bottom-yMarkSpace*i,
                        rectChart.right, rectChart.bottom-yMarkSpace*i, paint);
            } else {              //虚线
                Path path = new Path();
                path.moveTo(rectChart.left, rectChart.bottom-yMarkSpace*i);
                path.lineTo(rectChart.right,rectChart.bottom-yMarkSpace*i);
                PathEffect effects = new DashPathEffect(new float[]{15,8,15,8},0);
                paintEffect.setPathEffect(effects);
                canvas.drawPath(path, paintEffect);
            }
        }
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
            if(yMarkType == YMARK_TYPE.INTEGER) {
                canvas.drawText((int) (YMARK_MIN + i * YMARK) + "", rectChart.left,
                        rectChart.bottom - yMarkSpace * i - lableHeight - textSpaceY + lableLead, paintLabel);
            }else if(yMarkType == YMARK_TYPE.PERCENTAGE){
                canvas.drawText(NumberFormatUtil.formattedDecimalToPercentage((YMARK_MIN + i * YMARK)), rectChart.left,
                        rectChart.bottom - yMarkSpace * i - lableHeight - textSpaceY + lableLead, paintLabel);
            }
        }
    }
    /**绘制曲线*/
    private void drawDataPath(Canvas canvas) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(lineSize);

        int lineNum = LINE_NUM==0?(dataList.get(0).size()-1):LINE_NUM;

        for(int j = 0; j < lineNum; j++){
//        for(int j = 0; j < linePointList.size(); j++){
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
        if(!onFocus || null==focusInfo)
            return;
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(focusLineSize);
        paint.setColor(focusLineColor);
        //竖直线
        canvas.drawLine(focusInfo.getPoint().x, rectChart.bottom, focusInfo.getPoint().x, rectChart.top, paint);

      /*
        paintLabel.setTextSize(focusTextSize);
        paintLabel.setColor(focusTextColor);
        float focusTextLead = FontUtil.getFontLeading(paintLabel);
        float focusTextHeight = FontUtil.getFontHeight(paintLabel);
        try {

            for (Object obj : focusInfo.getObjs()) {
                DataPoint dataPoint = (DataPoint)obj;
                //水平参考线
                canvas.drawLine(rectChart.left, dataPoint.getPoint().y, rectChart.right, dataPoint.getPoint().y, paint);
                //中心点
                canvas.drawCircle(dataPoint.getPoint().x, dataPoint.getPoint().y, 2.0f, paint);
                //绘制刻度值
                String str = "("+ dataPoint.getValueX()+",";
                if(yMarkType == YMARK_TYPE.INTEGER) {
                    str += dataPoint.getValueY()+")";
                }else if(yMarkType == YMARK_TYPE.PERCENTAGE){
                    str += formattedDecimalToPercentage(dataPoint.getValueY())+")";
                }
                float textLength = FontUtil.getFontlength(paintLabel,str);
                if((rectChart.right- dataPoint.getPoint().x)>textLength){
                    canvas.drawText(str, dataPoint.getPoint().x + 10,
                            dataPoint.getPoint().y-focusTextHeight+focusTextLead - 10, paintLabel);
                }else{
                    canvas.drawText(str, dataPoint.getPoint().x - 10 - textLength,
                            dataPoint.getPoint().y-focusTextHeight+focusTextLead - 10, paintLabel);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        */
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
