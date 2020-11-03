package com.openxu.cview.xmstock20201030;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.xmstock.BaseChart;
import com.openxu.cview.xmstock20201030.build.AnimType;
import com.openxu.cview.xmstock20201030.build.AxisLine;
import com.openxu.cview.xmstock20201030.build.AxisLineType;
import com.openxu.cview.xmstock20201030.build.AxisMark;
import com.openxu.cview.xmstock20201030.build.ChartFocus;
import com.openxu.cview.xmstock20201030.build.DataPoint;
import com.openxu.cview.xmstock20201030.build.Line;
import com.openxu.cview.xmstock20201030.build.Orientation;
import com.openxu.utils.ChartCalUtil;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.ReflectUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * autour : openXu
 * date : 2020/10/31 14:26
 * className : StandLinesChart
 * version : 1.0
 * description : 通用的折线图表
 */
public class StandLinesChart extends BaseChart {


    //折线数据, 多少元素就有多少条线, 需要计算
    private List<Line> lineList;
    //坐标轴刻度
    private AxisMark xAxisMark;
    private AxisMark yLeftAxisMark;
    private AxisMark yRightAxisMark;
    //坐标轴线
    private AxisLine[] xAxisLines;
    private AxisLine[] yAxisLines;

    private Builder builder;
    public Builder builder(){
        if (builder==null)
            this.builder = new Builder();
        return builder;
    }
    public class Builder{
        /**
         * 设置一条曲线
         * @param line
         * @return
         */
        public Builder line(Line line){
            if(lineList == null)
                lineList = new ArrayList<>();
            lineList.add(line) ;
            return this;
        }

        /**
         * 设置X轴刻度
         * @param xAxisMark
         * @return
         */
        public Builder xAxisMark(AxisMark xAxisMark){
            if(xAxisMark.lableOrientation==null)
                xAxisMark.lableOrientation = Orientation.BOTTOM;
            //计算x轴刻度值
            if(xAxisMark.lables==null)
                ChartCalUtil.calXLable(xAxisMark);
            StandLinesChart.this.xAxisMark = xAxisMark;
            //上面计算了X轴刻度，说明确定了竖直方向有几根轴，下面给予默认的竖直方向轴样式
            yAxisLines = new AxisLine[xAxisMark.lableNum];
            for(int i = 0; i<xAxisMark.lableNum; i++){
                //第一条和最后一条线为实线，其他竖直方向不划线
                yAxisLines[i] = new AxisLine.Builder(getContext())
                        .lineType((i ==0 || i == xAxisMark.lableNum-1)?AxisLineType.SOLID:AxisLineType.NONE)
                        .build();
            }
            return this;
        }
        /**
         * 设置Y轴左侧刻度
         * @param yLeftAxisMark
         * @return
         */
        public Builder yLeftAxisMark(AxisMark yLeftAxisMark){
            if(yLeftAxisMark.lableOrientation==null)
                yLeftAxisMark.lableOrientation = Orientation.LEFT;
            //计算Y刻度
            if(yLeftAxisMark.lables==null)
                ChartCalUtil.calYLable(yLeftAxisMark);
            StandLinesChart.this.yLeftAxisMark = yLeftAxisMark;
            setXAxisLines(yLeftAxisMark);
            return this;
        }
        /**
         * 设置Y轴右侧刻度
         * @param yRightAxisMark
         * @return
         */
        public Builder yRightAxisMark(AxisMark yRightAxisMark){
            if(yRightAxisMark.lableOrientation==null)
                yRightAxisMark.lableOrientation = Orientation.RIGHT;
            if(yRightAxisMark.showLable && yRightAxisMark.lables==null)
                ChartCalUtil.calYLable(yRightAxisMark);
            StandLinesChart.this.yRightAxisMark = yRightAxisMark;
            setXAxisLines(yRightAxisMark);
            return this;
        }

        /**
         * 设置竖直方向第index条网格线，0 <= index<= xAxisMark.lableNum-1
         * @param index
         * @param axisLine
         * @return
         */
        public Builder verticalAxisLine(int index, AxisLine axisLine){
            if(xAxisMark==null) {
                //设置竖直方向坐标轴网格线样式时，必须线调用xAxisMark()方法设置x轴刻度
                throw new RuntimeException("please call xAxisMark() to determine the vertical line before this");
            }
            if(index>=yAxisLines.length){
                throw new RuntimeException("index over size");
            }
            yAxisLines[index] = axisLine;
            return this;
        }
        /**
         * 设置水平方向第index条网格线，0 <= index<= yAxisMark.lableNum-1
         * @param index
         * @param axisLine
         * @return
         */
        public Builder horizontalAxisLine(int index, AxisLine axisLine){
            if(yLeftAxisMark==null && yRightAxisMark==null) {
                //设置水平方向坐标轴网格线样式时，必须线调用yLeftAxisMark()或者yRightAxisMark()设置y轴刻度
                throw new RuntimeException("please call yLeftAxisMark() or yRightAxisMark() to determine the horizontal line before this");
            }
            if(index>=xAxisLines.length){
                throw new RuntimeException("index over size");
            }
            xAxisLines[index] = axisLine;
            return this;
        }

        private void setXAxisLines(AxisMark yAxisMark){
            if(xAxisLines!=null)
                return;
            xAxisLines = new AxisLine[yAxisMark.lableNum];
            for(int i = 0; i<yAxisMark.lableNum; i++){
                //第一条和最后一条线为实线，其他竖直方向不划线
                xAxisLines[i] = new AxisLine.Builder(getContext())
                        .lineType((i ==0 || i == xAxisMark.lableNum-1)?AxisLineType.SOLID:AxisLineType.DASHE)
                        .build();
            }
        }

        public void build(){
            isLoading = false;
            evaluatorByData();
            startDraw = false;
            invalidate();
        }
    }

    public StandLinesChart(Context context) {
        this(context, null);
    }
    public StandLinesChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public StandLinesChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
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
        Log.w(TAG, "======onMeasure:"+widthSize+" * "+heightSize);
    }

    /**设置数据后，计算相关值*/
    private void evaluatorByData() {

        float lableHeight;
        float lableLead;

        /**1. 确定图表绘制矩形*/
        if (xAxisMark != null) {
            paintLabel.setTextSize(xAxisMark.textSize);
            lableHeight = FontUtil.getFontHeight(paintLabel);
            //确定图表最下放绘制位置
            if (xAxisMark.showLable && xAxisMark.lableOrientation == Orientation.BOTTOM) {
                rectChart.bottom = getMeasuredHeight() - getPaddingBottom() - lableHeight - xAxisMark.textSpace;
            }
        }
        //计算Y刻度
        if (yLeftAxisMark != null) {
            if (yLeftAxisMark.showLable && yLeftAxisMark.lableOrientation == Orientation.LEFT) {
                paintLabel.setTextSize(yLeftAxisMark.textSize);
                String maxLable = "";
                for (String lable : yLeftAxisMark.lables) {
                    if (maxLable.length() < lable.length())
                        maxLable = lable;
                }
                rectChart.left = getPaddingLeft() + yLeftAxisMark.textSpace +
                        FontUtil.getFontlength(paintLabel, maxLable);
            }
        }
        if (yRightAxisMark != null) {
            if (yRightAxisMark.showLable && yRightAxisMark.lableOrientation == Orientation.RIGHT) {
                paintLabel.setTextSize(yRightAxisMark.textSize);
                String maxLable = "";
                for (String lable : yRightAxisMark.lables) {
                    if (maxLable.length() < lable.length())
                        maxLable = lable;
                }
                rectChart.right = getMeasuredWidth() - getPaddingRight() - yRightAxisMark.textSpace -
                        FontUtil.getFontlength(paintLabel, maxLable);
            }
        }
        Log.v(TAG, "计算图表矩形框：" + rectChart);

        /**2. 计算X轴绘制点坐标*/
        float markSpace;
        if (xAxisMark != null) {
            markSpace = (rectChart.right - rectChart.left) * 1.0f / (xAxisMark.lableNum - 1);
            xAxisMark.markPointList.clear();
            paintLabel.setTextSize(xAxisMark.textSize);
            lableHeight = FontUtil.getFontHeight(paintLabel);
            lableLead = FontUtil.getFontHeight(paintLabel);
            float left = rectChart.left;
            for (int i = 0; i < xAxisMark.lableNum; i++) {
                if (i >= 1 && i < xAxisMark.lableNum - 1) {
                    left = rectChart.left + i * markSpace - FontUtil.getFontlength(paintLabel, xAxisMark.lables[i]) / 2;
                } else if (i == xAxisMark.lableNum - 1) {
                    left = rectChart.right - FontUtil.getFontlength(paintLabel, xAxisMark.lables[i]);
                }
                /**x轴刻度位置确定竖直方向的轴线*/
                yAxisLines[i].pointStart = new PointF(rectChart.left + i * markSpace, rectChart.bottom);
                yAxisLines[i].pointEnd = new PointF(rectChart.left + i * markSpace, rectChart.top);
                xAxisMark.markPointList.add(new AxisMark.MarkPoint(xAxisMark.lables[i],
                        new PointF(
                                left,
                                rectChart.bottom + xAxisMark.textSpace + lableLead
                        )));
            }
        }

        /**3. 计算Y轴绘制点坐标*/
        if (yLeftAxisMark != null) {
            markSpace = (rectChart.bottom - rectChart.top) / (yLeftAxisMark.lableNum - 1);
            yLeftAxisMark.markPointList.clear();
            paintLabel.setTextSize(yLeftAxisMark.textSize);
            lableHeight = FontUtil.getFontHeight(paintLabel);
            lableLead = FontUtil.getFontHeight(paintLabel);
            for (int i = 0; i < yLeftAxisMark.lableNum; i++) {
                /**y轴刻度位置确定水平方向的轴线*/
                xAxisLines[i].pointStart = new PointF(rectChart.left, rectChart.bottom - i * markSpace);
                xAxisLines[i].pointEnd = new PointF(rectChart.right, rectChart.bottom - i * markSpace);
                switch (yLeftAxisMark.lableOrientation) {
                    case LEFT:   //左外居中
                        yLeftAxisMark.markPointList.add(new AxisMark.MarkPoint(yLeftAxisMark.lables[i],
                                new PointF(
                                        rectChart.left - FontUtil.getFontlength(paintLabel, yLeftAxisMark.lables[i]) - yLeftAxisMark.textSpace,
                                        rectChart.bottom - i * markSpace - lableHeight / 2 + lableLead
                                )));
                        break;
                    case TOP:    //左内靠上
                        yLeftAxisMark.markPointList.add(new AxisMark.MarkPoint(yLeftAxisMark.lables[i],
                                new PointF(
                                        rectChart.left + yLeftAxisMark.textSpace,
                                        rectChart.bottom - i * markSpace - lableHeight - yLeftAxisMark.textSpace + lableLead
                                )));
                        break;
                }
            }
        }

        if (yRightAxisMark != null) {
            markSpace = (rectChart.bottom - rectChart.top) / (yLeftAxisMark.lableNum - 1);
            yRightAxisMark.markPointList.clear();
            paintLabel.setTextSize(yRightAxisMark.textSize);
            lableHeight = FontUtil.getFontHeight(paintLabel);
            lableLead = FontUtil.getFontHeight(paintLabel);
            for (int i = 0; i < yRightAxisMark.lableNum; i++) {
                /**y轴刻度位置确定水平方向的轴线*/
                xAxisLines[i].pointStart = new PointF(rectChart.left, rectChart.bottom - i * markSpace);
                xAxisLines[i].pointEnd = new PointF(rectChart.right, rectChart.bottom - i * markSpace);
                switch (yRightAxisMark.lableOrientation) {
                    case RIGHT:   //右外居中
                        yRightAxisMark.markPointList.add(new AxisMark.MarkPoint(yRightAxisMark.lables[i],
                                new PointF(
                                        rectChart.right + yRightAxisMark.textSpace,
                                        rectChart.bottom - i * markSpace - lableHeight / 2 + lableLead
                                )));
                        break;
                    case TOP:    //右内靠上
                        yRightAxisMark.markPointList.add(new AxisMark.MarkPoint(yRightAxisMark.lables[i],
                                new PointF(
                                        rectChart.right + yRightAxisMark.textSpace,
                                        rectChart.bottom - i * markSpace - lableHeight - yRightAxisMark.textSpace + lableLead
                                )));
                        break;
                }
            }
        }
        /**4. 计算曲线*/
        if (lineList != null && lineList.size() > 0) {
            for (Line line : lineList) {
                line.linePointList.clear();
                AxisMark axis = line.orientation == Orientation.LEFT ? yLeftAxisMark : yRightAxisMark;
                //该折线上点的总数，不能直接取datas.size()，有些图表可能规定有数量，比如股票分时图，如果datas.size()<line.dataNumCount，则这个折线图不能绘制到终点
                line.dataNumCount = line.dataNumCount == 0 ? line.datas.size() : line.dataNumCount;
                float oneSpace = (rectChart.right - rectChart.left) / (line.dataNumCount - 1);
                for (int i = 0; i < line.datas.size(); i++) {
                    String str_y = ReflectUtil.getField(line.datas.get(i), line.field_y).toString();
                    String str_x = ReflectUtil.getField(line.datas.get(i), line.field_x).toString();
                    float valueY;
                    if (str_y.contains("%"))
                        valueY = Float.parseFloat(str_y.substring(0, str_y.indexOf("%"))) / 100.0f;
                    else
                        valueY = Float.parseFloat(str_y);
                    line.linePointList.add(new DataPoint(str_x, str_y, new PointF(
                            rectChart.left + i * oneSpace,
                            //根据最大值和最小值，计算当前数据在图表上Y轴的坐标
                            rectChart.bottom -
                                    (rectChart.bottom - rectChart.top) / (axis.cal_mark_max - axis.cal_mark_min)
                                            * (valueY - axis.cal_mark_min)
                    )));

                }
            }
        }

    }

    /**绘制图表基本框架*/
    @Override
    public void drawDefult(Canvas canvas) {
        drawGrid(canvas);
        drawXYLable(canvas);
    }

    /**绘制图表*/
    @Override
    public void drawChart(Canvas canvas) {
        drawDataPath(canvas);
        drawFocus(canvas);
    }

    /**1. 绘制坐标轴网格*/
    private void drawGrid(Canvas canvas){
        //绘制水平轴线
        if(xAxisLines==null || xAxisLines.length==0)
            return;
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paintEffect.setStyle(Paint.Style.STROKE);
        for (AxisLine axisLine : xAxisLines) {
            if(axisLine.type == AxisLineType.NONE)
                continue;
            drawAxisLine(canvas, axisLine);
        }
        //绘制Y轴方向辅助网格  上下
        for (AxisLine axisLine : yAxisLines) {
            if(axisLine.type == AxisLineType.NONE)
                continue;
            drawAxisLine(canvas, axisLine);
        }

    }
    private void drawAxisLine(Canvas canvas, AxisLine axisLine){
        if(axisLine.type == AxisLineType.SOLID){  //实线
            paint.setStrokeWidth(axisLine.lineWidth);
            paint.setColor(axisLine.lineColor);
            canvas.drawLine(axisLine.pointStart.x, axisLine.pointStart.y,
                    axisLine.pointEnd.x, axisLine.pointEnd.y, paint);
        }
        if(axisLine.type == AxisLineType.DASHE){   //虚线
            paintEffect.setStrokeWidth(axisLine.lineWidth);
            paintEffect.setColor(axisLine.lineColor);
            Path path = new Path();
            path.moveTo(axisLine.pointStart.x, axisLine.pointStart.y);
            path.lineTo(axisLine.pointEnd.x,axisLine.pointEnd.y);
            PathEffect effects = new DashPathEffect(new float[]{15,8,15,8},0);
            paintEffect.setPathEffect(effects);
            canvas.drawPath(path, paintEffect);
        }
    }

    /**绘制X轴刻度*/
    private void drawXYLable(Canvas canvas){
        drawAxisLable(canvas, xAxisMark);
        drawAxisLable(canvas, yLeftAxisMark);
        drawAxisLable(canvas, yRightAxisMark);
    }
    private void drawAxisLable(Canvas canvas, AxisMark axisMark){
        if(axisMark==null)
            return;
        if(axisMark.showLable){
            paintLabel.setTextSize(axisMark.textSize);
            paintLabel.setColor(axisMark.textColor);
            for(AxisMark.MarkPoint point : axisMark.markPointList){
                canvas.drawText(point.value, point.point.x, point.point.y, paintLabel);
            }
        }
    }
    /**绘制曲线*/
    private float smoothness=0.15f;
    private void drawDataPath(Canvas canvas) {
        if(lineList==null || lineList.size()==0)
            return;
//        animPro = .5f;
        paint.setAntiAlias(true);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeCap(Paint.Cap.ROUND);
//        paint.setStrokeJoin(Paint.Join.ROUND);
        if(null==lineList || lineList.size()<=0)
            return;
        PointF prePoint = null;
        int diffx, diffy;
        for(int lineNum = 0; lineNum < lineList.size(); lineNum ++){
            Line line = lineList.get(lineNum);
            //一条一条的绘制
            paint.setStrokeWidth(line.lineWidth);
            paint.setColor(line.lineColor);
            Path path = new Path();
            for(int i = 0; i <line.dataNumCount; i++){
                PointF point = ((DataPoint)line.linePointList.get(i)).point;
                //下一个点
//                nextPoint = i+1<line.dataNumCount?((DataPoint)line.linePointList.get(i+1)).point:point;

                if(i == 0){
                    if(line.animType == AnimType.LEFT_TO_RIGHT){
                        path.moveTo(rectChart.left+(point.x-rectChart.left)*animPro, point.y);
                    }else if(line.animType == AnimType.BOTTOM_TO_TOP){
                        path.moveTo(point.x, rectChart.bottom-(rectChart.bottom - point.y)* animPro);
                    }else{
                        path.moveTo(point.x, point.y);
                    }
//                    canvas.drawCircle(point.x, point.y, 5.0f, paint);
//                    path.moveTo(point.x, point.y);
//                    Log.w(TAG, lineNum+"起点："+point);
                }else{
                    if(line.lineType == Line.LineType.CURVE) {
                        //quadTo：二阶贝塞尔曲线连接前后两点，这样使得曲线更加平滑
                        //  * @param x1 The x-coordinate of the control point on a quadratic curve
                        //     * @param y1 The y-coordinate of the control point on a quadratic curve
                        //     * @param x2 The x-coordinate of the end point on a quadratic curve
                        //     * @param y2 The y-coordinate of the end point on a quadratic curve
                        //cubicTo : 三阶

                        diffx = (int) ((point.x - prePoint.x) * smoothness);

                        if (line.animType == AnimType.LEFT_TO_RIGHT) {
                            path.cubicTo(rectChart.left + (prePoint.x + diffx - rectChart.left) * animPro,
                                    prePoint.y,
                                    rectChart.left + (point.x - diffx - rectChart.left) * animPro,
                                    point.y,
                                    rectChart.left + (point.x - rectChart.left) * animPro,
                                    point.y);
                        } else if (line.animType == AnimType.BOTTOM_TO_TOP) {
                            path.cubicTo(prePoint.x + diffx, rectChart.bottom - (rectChart.bottom - prePoint.y) * animPro,
                                    point.x - diffx, rectChart.bottom - (rectChart.bottom - point.y) * animPro,
                                    point.x, rectChart.bottom - (rectChart.bottom - point.y) * animPro);
                        } else if (line.animType == AnimType.SLOW_DRAW) {
                            if (i > lineList.size() * animPro)
                                break;
                            path.cubicTo(prePoint.x + diffx, prePoint.y,
                                    point.x - diffx, point.y,
                                    point.x, point.y);
                        } else if (line.animType == AnimType.NONE) {
                            path.cubicTo(prePoint.x + diffx, prePoint.y,
                                    point.x - diffx, point.y,
                                    point.x, point.y);
                        }
//                    path.cubicTo(prePoint.x+diffx, prePoint.y,
//                            point.x-diffx, point.y,
//                                point.x, point.y);
                    }else if(line.lineType == Line.LineType.BROKEN){
                        if (line.animType == AnimType.LEFT_TO_RIGHT) {
                            path.lineTo(rectChart.left + (point.x - rectChart.left) * animPro,
                                    point.y);
                        } else if (line.animType == AnimType.BOTTOM_TO_TOP) {
                            path.lineTo(point.x, rectChart.bottom - (rectChart.bottom - point.y) * animPro);
                        } else if (line.animType == AnimType.SLOW_DRAW) {
                            if (i > lineList.size() * animPro)
                                break;
                            path.lineTo(point.x, point.y);
                        } else if (line.animType == AnimType.NONE) {
                            path.lineTo(point.x, point.y);
                        }
                    }
//                    canvas.drawCircle(prePoint.x+diffx, prePoint.y, 10.0f, paint);
//                    canvas.drawCircle(point.x-diffx, point.y, 10.0f, paint);
//                    canvas.drawCircle(point.x, point.y, 5.0f, paint);
                }
                prePoint = point;
            }
            canvas.drawPath(path, paint);
        }
    }

    private ChartFocus focusInfo;
    @Override
    protected void onTouchMoved(PointF point) {
        if(null==lineList || lineList.size()<=0)
            return;
        onFocus = (null != point);
        if(null != point) {
            //获取焦点对应的数据的索引
            int index = (int) ((point.x - rectChart.left) * lineList.get(0).dataNumCount
                    / (rectChart.right - rectChart.left));

            //避免滑出
            List<DataPoint> dataPoints = lineList.get(0).linePointList;
            if(point.x > dataPoints.get(dataPoints.size()-1).point.x)
                point.x = dataPoints.get(dataPoints.size()-1).point.x;
            if(point.x < dataPoints.get(0).point.x)
                point.x = dataPoints.get(0).point.x;
            index = Math.max(0, Math.min(index, dataPoints.size() - 1));

            focusInfo = new ChartFocus();
            List<Object> objs = new ArrayList<>();
            List<PointF> points = new ArrayList<>();
            for(Line line : lineList){
                objs.add(line.datas.get(index));
                points.add(((DataPoint)line.linePointList.get(index)).point);
            }
            focusInfo.setFocusData(objs);
            focusInfo.setPoints(points);
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
        public void onfocus(ChartFocus focusInfo);
    }

    //设置焦点线颜色 及 粗细
    private int focusLineColor = getResources().getColor(R.color.tc_chart_focus_line);
    private int focusLineSize = DensityUtil.dip2px(getContext(), 0.8f);
    /**绘制焦点*/
    private void drawFocus(Canvas canvas){
        if(!onFocus || null==focusInfo)
            return;
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(focusLineSize);
        paint.setColor(focusLineColor);
        //竖直线
        canvas.drawLine(focusInfo.getPoints().get(0).x, rectChart.bottom,
                focusInfo.getPoints().get(0).x, rectChart.top, paint);

//        paintLabel.setTextSize(focusTextSize);
//        paintLabel.setColor(focusTextColor);
//        float focusTextLead = FontUtil.getFontLeading(paintLabel);
//        float focusTextHeight = FontUtil.getFontHeight(paintLabel);
        try {
            for (PointF point : focusInfo.getPoints()) {
                //水平参考线
                canvas.drawLine(rectChart.left, point.y, rectChart.right, point.y, paint);
                //中心点
                canvas.drawCircle(point.x, point.y, 10.0f, paint);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private float animPro;       //动画计算的占比数量
    /**创建动画*/
    @Override
    protected ValueAnimator initAnim() {
        if(lineList!=null && lineList.size()>0) {
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
