package com.openxu.chart.linechart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.chart.BaseChart;
import com.openxu.chart.element.DisplayConfig;
import com.openxu.chart.element.AnimType;
import com.openxu.chart.element.AxisLine;
import com.openxu.chart.element.AxisLineType;
import com.openxu.chart.element.AxisMark;
import com.openxu.chart.element.DataPoint;
import com.openxu.chart.linechart.element.Line;
import com.openxu.chart.element.Orientation;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.utils.ChartCalUtil;
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

    private List datas;
    //折线数据, 多少元素就有多少条线, 需要计算
    private List<Line> lineList;
    //坐标轴刻度
    private AxisMark xAxisMark;
    private AxisMark yLeftAxisMark;
    private AxisMark yRightAxisMark;
    //坐标轴线
    private AxisLine[] xAxisLines;
    private AxisLine[] yAxisLines;
    //
    private DisplayConfig display;

    private Builder builder;
    public Builder builder(){
        if (builder==null)
            this.builder = new Builder();
        return builder;
    }
    public class Builder{
        public Builder datas(List datas){
            StandLinesChart.this.datas = datas;
            return this;
        }
        public Builder display(DisplayConfig displayConfig){
            display = displayConfig;
            return this;
        }
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
            StandLinesChart.this.xAxisMark = xAxisMark;
            //确定了竖直方向有几根轴，下面给予默认的竖直方向轴样式
            yAxisLines = new AxisLine[xAxisMark.lables==null?xAxisMark.lableNum:xAxisMark.lables.length];
            for(int i = 0; i<xAxisMark.lableNum; i++){
                //第一条和最后一条线为实线，其他竖直方向不划线
                yAxisLines[i] = new AxisLine.Builder(getContext())
                        .lineType((i ==0 || i == xAxisMark.lableNum-1)?AxisLineType.SOLID:AxisLineType.DASHE)
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
                ChartCalUtil.calYLable(display,datas,  yLeftAxisMark);
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
            if(yRightAxisMark.lables==null)
                ChartCalUtil.calYLable(display, datas, yRightAxisMark);
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
        /**1. 确定图表绘制矩形，由于坐标轴刻度显示原因，图表矩形需要重新计算预留lable显示位置*/
        paintLabel.setTextSize(xAxisMark.textSize);
        lableHeight = FontUtil.getFontHeight(paintLabel);
        //确定图表最下放绘制位置
        if (xAxisMark.showLable && xAxisMark.lableOrientation == Orientation.BOTTOM) {
            rectChart.bottom = getMeasuredHeight() - getPaddingBottom() - lableHeight - xAxisMark.textSpace;
        }
        //计算Y刻度
        if (yLeftAxisMark != null) {
            if (yLeftAxisMark.showLable){
                paintLabel.setTextSize(yLeftAxisMark.textSize);
                if (yLeftAxisMark.lableOrientation == Orientation.LEFT) {
                    String maxLable = "";
                    for (String lable : yLeftAxisMark.lables) {
                        if (maxLable.length() < lable.length())
                            maxLable = lable;
                    }
                    rectChart.left = getPaddingLeft() + yLeftAxisMark.textSpace + FontUtil.getFontlength(paintLabel, maxLable);
                }else if(yLeftAxisMark.lableOrientation == Orientation.TOP){
                    lableHeight = FontUtil.getFontHeight(paintLabel);
                    rectChart.top = getPaddingTop() + yLeftAxisMark.textSpace + lableHeight;
                }
            }
        }
        if (yRightAxisMark != null) {
            if (yRightAxisMark.showLable){
                paintLabel.setTextSize(yRightAxisMark.textSize);
                if (yRightAxisMark.lableOrientation == Orientation.RIGHT) {
                    String maxLable = "";
                    for (String lable : yRightAxisMark.lables) {
                        if (maxLable.length() < lable.length())
                            maxLable = lable;
                    }
                    rectChart.right = getMeasuredWidth() - getPaddingRight() - yRightAxisMark.textSpace -
                            FontUtil.getFontlength(paintLabel, maxLable);
                }else if(yRightAxisMark.lableOrientation == Orientation.TOP){
                    lableHeight = FontUtil.getFontHeight(paintLabel);
                    rectChart.top = getPaddingTop() + yRightAxisMark.textSpace + lableHeight;
                }
            }

        }
        Log.e(TAG, "计算图表矩形框：" + rectChart);

        /**2. 计算X轴绘制点坐标*/
        //每个元素的宽度 = 总宽度/（展示数量-1）  分成多少等分
        if(display.dataDisplay == 0){
            //分时图 dataTotal = 4 * 60  dataDisplay = 0  displayIndex = 0
            display.oneSpace = (rectChart.right - rectChart.left) / (display.dataTotal - 1);
        }else if(display.dataTotal == display.dataDisplay){
            //不可滚动  dataTotal = x   dataDisplay = dataTotal  displayIndex = 0
            display.oneSpace = (rectChart.right - rectChart.left) / (display.dataTotal - 1);
        }else if(display.dataDisplay < display.dataTotal){
            display.oneSpace = (rectChart.right - rectChart.left) / (display.dataDisplay - 1);
        }
        float oneMarkSpaceX = (rectChart.right - rectChart.left) * 1.0f / (xAxisMark.lableNum - 1);
        for (int i = 0; i < xAxisMark.lableNum; i++) {
            /**x轴刻度位置确定竖直方向的轴线*/
            yAxisLines[i].pointStart = new PointF(rectChart.left + i * oneMarkSpaceX, rectChart.bottom);
            yAxisLines[i].pointEnd = new PointF(rectChart.left + i * oneMarkSpaceX, rectChart.top);
        }
        xAxisMark.markPointList.clear();
        paintLabel.setTextSize(xAxisMark.textSize);
        lableHeight = FontUtil.getFontHeight(paintLabel);
        lableLead = FontUtil.getFontHeight(paintLabel);
        if(display.dataTotal == display.dataDisplay){
            //不可滚动  dataTotal = x   dataDisplay = dataTotal  displayIndex = 0
            //从数据中均匀取出x刻度
            ChartCalUtil.calXLable(display, datas, xAxisMark);
        }
        float left = rectChart.left;
        if(display.dataDisplay == 0 || display.dataTotal == display.dataDisplay){
            //分时图、或者不可滚动
            for (int i = 0; i < xAxisMark.lableNum; i++) {
                if (i >= 1 && i < xAxisMark.lableNum - 1) {
                    left = rectChart.left + i * oneMarkSpaceX - FontUtil.getFontlength(paintLabel, xAxisMark.lables[i]) / 2;
                } else if (i == xAxisMark.lableNum - 1) {
                    left = rectChart.right - FontUtil.getFontlength(paintLabel, xAxisMark.lables[i]);
                }
                xAxisMark.markPointList.add(new AxisMark.MarkPoint(xAxisMark.lables[i],
                        new PointF(left, rectChart.bottom + xAxisMark.textSpace + lableLead),
                        new PointF(rectChart.left + i * oneMarkSpaceX, rectChart.bottom) ));
            }
        } else if(display.dataDisplay < display.dataTotal){
            xAxisMark.markPointList.clear();
            paintLabel.setTextSize(xAxisMark.textSize);
            lableHeight = FontUtil.getFontHeight(paintLabel);
            lableLead = FontUtil.getFontHeight(paintLabel);
            String lable;

            int part = display.dataDisplay / xAxisMark.lableNum;
            Log.w(TAG, "每隔"+part+"个绘制一个x刻度");
            //每隔part个绘制一个点
            for (int i = 0; i < datas.size(); i++) {
                if(i == 0 || i == datas.size()-1 || i%part==0){
                    lable = ReflectUtil.getField(datas.get(i), xAxisMark.field).toString();
                    left = rectChart.left + ((i-display.displayIndex)*display.oneSpace) - FontUtil.getFontlength(paintLabel, lable) / 2;
                    xAxisMark.markPointList.add(new AxisMark.MarkPoint(lable,
                            new PointF(left, rectChart.bottom + xAxisMark.textSpace + lableLead),
                            new PointF(rectChart.left + ((i-display.displayIndex)*display.oneSpace), rectChart.bottom)));
                }
            }
        }
        /**3. 计算Y轴绘制点坐标*/
        if (yLeftAxisMark != null) {
            float oneMarkSpaceYLeft = (rectChart.bottom - rectChart.top) / (yLeftAxisMark.lableNum - 1);
            yLeftAxisMark.markPointList.clear();
            paintLabel.setTextSize(yLeftAxisMark.textSize);
            lableHeight = FontUtil.getFontHeight(paintLabel);
            lableLead = FontUtil.getFontHeight(paintLabel);
            for (int i = 0; i < yLeftAxisMark.lableNum; i++) {
                /**y轴刻度位置确定水平方向的轴线*/
                xAxisLines[i].pointStart = new PointF(rectChart.left, rectChart.bottom - i * oneMarkSpaceYLeft);
                xAxisLines[i].pointEnd = new PointF(rectChart.right, rectChart.bottom - i * oneMarkSpaceYLeft);
                switch (yLeftAxisMark.lableOrientation) {
                    case LEFT:   //左外居中
                        yLeftAxisMark.markPointList.add(new AxisMark.MarkPoint(yLeftAxisMark.lables[i],
                                new PointF(rectChart.left - FontUtil.getFontlength(paintLabel, yLeftAxisMark.lables[i]) - yLeftAxisMark.textSpace,
                                        rectChart.bottom - i * oneMarkSpaceYLeft - lableHeight / 2 + lableLead),
                                new PointF(rectChart.left, rectChart.bottom - i * oneMarkSpaceYLeft) ));
                        break;
                    case TOP:    //左内靠上
                        yLeftAxisMark.markPointList.add(new AxisMark.MarkPoint(yLeftAxisMark.lables[i],
                                new PointF(rectChart.left + yLeftAxisMark.textSpace,
                                        rectChart.bottom - i * oneMarkSpaceYLeft - lableHeight - yLeftAxisMark.textSpace + lableLead ),
                                new PointF(rectChart.left, rectChart.bottom - i * oneMarkSpaceYLeft)));
                        break;
                }
            }
        }
        if (yRightAxisMark != null) {
            float oneMarkSpaceYRight = (rectChart.bottom - rectChart.top) / (yLeftAxisMark.lableNum - 1);
            yRightAxisMark.markPointList.clear();
            paintLabel.setTextSize(yRightAxisMark.textSize);
            lableHeight = FontUtil.getFontHeight(paintLabel);
            lableLead = FontUtil.getFontHeight(paintLabel);
            for (int i = 0; i < yRightAxisMark.lableNum; i++) {
                /**y轴刻度位置确定水平方向的轴线*/
                xAxisLines[i].pointStart = new PointF(rectChart.left, rectChart.bottom - i * oneMarkSpaceYRight);
                xAxisLines[i].pointEnd = new PointF(rectChart.right, rectChart.bottom - i * oneMarkSpaceYRight);
                switch (yRightAxisMark.lableOrientation) {
                    case RIGHT:   //右外居中
                        yRightAxisMark.markPointList.add(new AxisMark.MarkPoint(yRightAxisMark.lables[i],
                                new PointF(rectChart.right + yRightAxisMark.textSpace,
                                        rectChart.bottom - i * oneMarkSpaceYRight - lableHeight / 2 + lableLead ),
                                new PointF(rectChart.right, rectChart.bottom - i * oneMarkSpaceYRight)));
                        break;
                    case TOP:    //右内靠上
                        yRightAxisMark.markPointList.add(new AxisMark.MarkPoint(yRightAxisMark.lables[i],
                                new PointF(rectChart.right + yRightAxisMark.textSpace,
                                        rectChart.bottom - i * oneMarkSpaceYRight - lableHeight - yRightAxisMark.textSpace + lableLead ),
                                new PointF(rectChart.right, rectChart.bottom - i * oneMarkSpaceYRight)));
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
                //new DisplayConfig.Builder()
                //                        .dataTotal(250)
                //                        .dataDisplay(250)
                //                        .displayIndex(0)
                //                        .build())
//                line.dataNumCount = line.dataNumCount == 0 ? line.datas.size() : line.dataNumCount;
                /**计算最大最小偏移量*/
                display.offsetMin = - (int) ((display.dataTotal-display.displayIndex-display.dataDisplay) * display.oneSpace);
                display.offsetMax = (int) (display.displayIndex * display.oneSpace);
                for (int i = 0; i < datas.size(); i++) {
                    String str_y = ReflectUtil.getField(datas.get(i), line.field_y).toString();
                    String str_x = ReflectUtil.getField(datas.get(i), line.field_x).toString();
                    float valueY;
                    if (str_y.contains("%"))
                        valueY = Float.parseFloat(str_y.substring(0, str_y.indexOf("%"))) / 100.0f;
                    else
                        valueY = Float.parseFloat(str_y);
                    PointF pointF = new PointF(
                            rectChart.left + ((i-display.displayIndex)*display.oneSpace),
                            //根据最大值和最小值，计算当前数据在图表上Y轴的坐标
                            rectChart.bottom -(rectChart.bottom - rectChart.top) / (axis.cal_mark_max - axis.cal_mark_min) * (valueY - axis.cal_mark_min)
                    );
                    Log.i(TAG, "计算曲线点坐标："+valueY+"  min:"+axis.cal_mark_min+"   max="+axis.cal_mark_max);
                    Log.w(TAG, "计算曲线点坐标：i = "+i + "  oneSpace="+display.oneSpace+"   坐标 "+pointF);
                    line.linePointList.add(new DataPoint(str_x, str_y, pointF));
                }
            }
        }
    }

    /**绘制图表基本框架*/
    @Override
    public void drawDefult(Canvas canvas) {
        drawGrid(canvas);
        drawXAxisLable(canvas, xAxisMark);
        drawYAxisLable(canvas, yLeftAxisMark);
        drawYAxisLable(canvas, yRightAxisMark);
    }

    /**绘制图表*/
    @Override
    public void drawChart(Canvas canvas) {
        drawLinePath(canvas);
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
    private void drawXAxisLable(Canvas canvas, AxisMark axisMark){
        if(axisMark==null || !axisMark.showLable)
            return;
        paintLabel.setTextSize(axisMark.textSize);
        paintLabel.setColor(axisMark.textColor);
        paint.setColor(axisMark.textColor);
        //分情况绘制
        if(display.dataDisplay == 0 || display.dataTotal == display.dataDisplay){
            //分时图、或者不可滚动
            for(AxisMark.MarkPoint point : axisMark.markPointList){
                canvas.drawText(point.value, point.textPoint.x, point.textPoint.y, paintLabel);
                canvas.drawLine(point.markPoint.x, point.markPoint.y, point.markPoint.x, point.markPoint.y-6, paint);
            }
        } else if(display.dataDisplay < display.dataTotal){
            //可以滚动的图
            AxisMark.MarkPoint markPoint;
            float oneMarkSpaceX = (rectChart.right - rectChart.left) * 1.0f / (xAxisMark.lableNum - 1);
            Log.w(TAG, "滑动类型的图表，x轴刻度每隔"+oneMarkSpaceX+"绘制一个");
            for(int i = 0; i< axisMark.markPointList.size(); i++){
                markPoint = axisMark.markPointList.get(i);
                //判断刻度有没有在图表外面
                if(markPoint.markPoint.x + display.offset < rectChart.left-1){
                    Log.w(TAG, "----"+i+"x轴刻度落在图表外侧了:"+(markPoint.markPoint.x + display.offset));
                    continue;
                }
                if(markPoint.markPoint.x + display.offset > rectChart.right+1){
                    Log.w(TAG, "----"+i+"x轴刻度落在图表外侧了:"+(markPoint.markPoint.x + display.offset));
                    return;
                }
                Log.w(TAG, "----"+i+"绘制x刻度:"+(markPoint.markPoint.x + display.offset));
                //float startX, float startY, float stopX, float stopY,
                canvas.drawLine(markPoint.markPoint.x+display.offset, markPoint.markPoint.y,
                        markPoint.markPoint.x+display.offset, markPoint.markPoint.y - 6, paint);
                canvas.drawText(markPoint.value, markPoint.textPoint.x+display.offset, markPoint.textPoint.y, paintLabel);
            }
        }
    }
    private void drawYAxisLable(Canvas canvas, AxisMark axisMark){
        if(axisMark==null || !axisMark.showLable)
            return;
        paintLabel.setTextSize(axisMark.textSize);
        paintLabel.setColor(axisMark.textColor);
        for(AxisMark.MarkPoint point : axisMark.markPointList){
            canvas.drawText(point.value, point.textPoint.x, point.textPoint.y, paintLabel);
        }
    }

    /********************************************/
    @Override
    protected void onMove(float x, float y) {
        super.onMove(x, y);
        display.offset += x;
        if(display.offset > display.offsetMax){
            display.offset = display.offsetMax;
        }
        if(display.offset < display.offsetMin){
            display.offset = display.offsetMin;
        }
        Log.w(TAG, "当前偏移量"+display.offset+"   min="+display.offsetMin+"   max="+display.offsetMax);
        invalidate();
    }
    /*********************************************/

    /**绘制曲线*/
    private float smoothness=0.15f;
    private void drawLinePath(Canvas canvas) {
        if(lineList==null || lineList.size()==0)
            return;
        paint.setAntiAlias(true);
        if(null==lineList || lineList.size()<=0)
            return;
        //真正绘制的点
        PointF drawPoint = new PointF();  //偏移之后的坐标
        PointF preOffsetPoint = new PointF();
        int diffx, diffy;
        //temp
        PointF prePoint = new PointF();   //上一个点
        PointF nextPoint = new PointF();  //下一个点
        for(int lineNum = 0; lineNum < lineList.size(); lineNum ++){
            Log.w(TAG, "------------开始绘制曲线:"+lineNum+"  宽容值："+display.oneSpace);
            Line line = lineList.get(lineNum);
            //一条一条的绘制
            paint.setStrokeWidth(line.lineWidth);
            paint.setColor(line.lineColor);
            Path path = new Path();
            for(int i = 0; i <line.linePointList.size(); i++){
                PointF point = ((DataPoint)line.linePointList.get(i)).point;
                drawPoint.x = (int)point.x + display.offset;
                drawPoint.y = point.y;
                Log.d(TAG, i+"当前点"+drawPoint);
                //判断点有没有在图表外面
                /**处理边界点绘制问题*/
                if(drawPoint.x < rectChart.left){
                    if(drawPoint.x + display.oneSpace < rectChart.left){
//                        Log.w(TAG, i+"点落在图表矩形左外侧了:"+drawPoint);
                        continue;
                    }
                    Log.d(TAG, i+"点落在图表矩形左外侧了，但是需要绘制:"+drawPoint);
                    point = ((DataPoint)line.linePointList.get(i+1)).point;
                    nextPoint.x = (int)point.x + display.offset;
                    nextPoint.y = point.y;
                    Log.d(TAG, i+"下一个点"+nextPoint);
                    //测试
                    canvas.drawCircle(drawPoint.x, drawPoint.y, 5.0f, paint);
                    //计算与左侧y轴交汇点
                    drawPoint.y = (int)(nextPoint.y+(drawPoint.y-nextPoint.y)*(nextPoint.x-rectChart.left)/(nextPoint.x-drawPoint.x));
                    drawPoint.x = (int)rectChart.left;
                    canvas.drawCircle(drawPoint.x, drawPoint.y, 5.0f, paint);
                    Log.d(TAG, i+"交汇点:"+drawPoint);
                }else if(drawPoint.x > rectChart.right){
                    if(drawPoint.x - display.oneSpace> rectChart.right){
//                        Log.w(TAG, i+"点落在图表矩形右外侧了:"+drawPoint);
                        continue;
                    }
                    Log.d(TAG, i+"点落在图表矩形右外侧了，但是需要绘制:"+drawPoint+"*"+point.y);
                    point = ((DataPoint)line.linePointList.get(i-1)).point;
                    prePoint.x = (int)point.x + display.offset;
                    prePoint.y = point.y;
                    drawPoint.y = (int)(prePoint.y+(drawPoint.y-prePoint.y)*(rectChart.right-prePoint.x)/(drawPoint.x-prePoint.x));
                    drawPoint.x = (int)rectChart.right;
                    canvas.drawCircle(drawPoint.x, drawPoint.y, 5.0f, paint);
                    Log.d(TAG, i+"交汇点:"+drawPoint);
                }

                if(path.isEmpty()){
                    Log.i(TAG, i+"====绘制的第一个点:"+drawPoint);
                    if(line.animType == AnimType.LEFT_TO_RIGHT){
                        path.moveTo(rectChart.left+(drawPoint.x-rectChart.left)*animPro, drawPoint.y);
                    }else if(line.animType == AnimType.BOTTOM_TO_TOP){
                        path.moveTo(drawPoint.x, rectChart.bottom-(rectChart.bottom - drawPoint.y)* animPro);
                    }else{
                        path.moveTo(drawPoint.x, drawPoint.y);
                    }
                    canvas.drawCircle(drawPoint.x, drawPoint.y, 20.0f, paint);
//                    path.moveTo(drawPoint.x, drawPoint.y);
//                    Log.w(TAG, lineNum+"起点："+point);
                }else{
//                    Log.i(TAG, i+"绘制剩下的点:"+pointX+"*"+point.y);
                    if(line.lineType == Line.LineType.CURVE) {
                        //quadTo：二阶贝塞尔曲线连接前后两点，这样使得曲线更加平滑
                        //  * @param x1 The x-coordinate of the control point on a quadratic curve
                        //     * @param y1 The y-coordinate of the control point on a quadratic curve
                        //     * @param x2 The x-coordinate of the end point on a quadratic curve
                        //     * @param y2 The y-coordinate of the end point on a quadratic curve
                        //cubicTo : 三阶
                        diffx = (int) ((drawPoint.x - preOffsetPoint.x) * smoothness);
                        if (line.animType == AnimType.LEFT_TO_RIGHT) {
                            path.cubicTo(rectChart.left + (preOffsetPoint.x + diffx - rectChart.left) * animPro,
                                    preOffsetPoint.y,
                                    rectChart.left + (drawPoint.x - diffx - rectChart.left) * animPro,
                                    drawPoint.y,
                                    rectChart.left + (drawPoint.x - rectChart.left) * animPro,
                                    drawPoint.y);
                        } else if (line.animType == AnimType.BOTTOM_TO_TOP) {
                            path.cubicTo(preOffsetPoint.x + diffx, rectChart.bottom - (rectChart.bottom - preOffsetPoint.y) * animPro,
                                    drawPoint.x - diffx, rectChart.bottom - (rectChart.bottom - drawPoint.y) * animPro,
                                    drawPoint.x, rectChart.bottom - (rectChart.bottom - drawPoint.y) * animPro);
                        } else if (line.animType == AnimType.SLOW_DRAW) {
                            if (i > lineList.size() * animPro)
                                break;
                            path.cubicTo(preOffsetPoint.x + diffx, preOffsetPoint.y,
                                    drawPoint.x - diffx, drawPoint.y,
                                    drawPoint.x, drawPoint.y);
                        } else if (line.animType == AnimType.NONE) {
                            path.cubicTo(preOffsetPoint.x + diffx, preOffsetPoint.y,
                                    drawPoint.x - diffx, drawPoint.y,
                                    drawPoint.x, drawPoint.y);
                        }
//                    path.cubicTo(preOffsetPoint.x+diffx, preOffsetPoint.y,
//                            pointX-diffx, drawPoint.y,
//                                drawPoint.x, drawPoint.y);
                    }else if(line.lineType == Line.LineType.BROKEN){
                        if (line.animType == AnimType.LEFT_TO_RIGHT) {
                            path.lineTo(rectChart.left + (drawPoint.x - rectChart.left) * animPro, drawPoint.y);
                        } else if (line.animType == AnimType.BOTTOM_TO_TOP) {
                            path.lineTo(drawPoint.x, rectChart.bottom - (rectChart.bottom - drawPoint.y) * animPro);
                        } else if (line.animType == AnimType.SLOW_DRAW) {
                            if (i > lineList.size() * animPro)
                                break;
                            path.lineTo(drawPoint.x, drawPoint.y);
                        } else if (line.animType == AnimType.NONE) {
                            path.lineTo(drawPoint.x, drawPoint.y);
                        }
                    }
//                    path.lineTo(drawPoint.x, drawPoint.y);
                    canvas.drawCircle(drawPoint.x, drawPoint.y, 20.0f, paint);
                }
                preOffsetPoint.x = drawPoint.x;
                preOffsetPoint.y = drawPoint.y;
            }
            canvas.drawPath(path, paint);
        }
    }

    /**绘制焦点*/
    private void drawFocus(Canvas canvas){
//        if(!onFocus || null==focusInfo)
//            return;
//        paint.setAntiAlias(true);
//        paint.setStyle(Paint.Style.FILL);
//        paint.setStrokeWidth(focusLineSize);
//        paint.setColor(focusLineColor);
//        //竖直线
//        canvas.drawLine(focusInfo.getPoint().x, rectChart.bottom, focusInfo.getPoint().x, rectChart.top, paint);

      /*
        paintLabel.setTextSize(focusTextSize);
        paintLabel.setColor(focusTextColor);
        float focusTextLead = FontUtil.getFontLeading(paintLabel);
        float focusTextHeight = FontUtil.getFontHeight(paintLabel);
        try {

            for (Object obj : focusInfo.getObjs()) {
                DataPoint dataPoint = (DataPoint)obj;
                //水平参考线
                canvas.drawLine(rectChart.left, point.y, rectChart.right, point.y, paint);
                //中心点
                canvas.drawCircle(point.x, point.y, 2.0f, paint);
                //绘制刻度值
                String str = "("+ dataPoint.getValueX()+",";
                if(yMarkType == YMARK_TYPE.INTEGER) {
                    str += dataPoint.getValueY()+")";
                }else if(yMarkType == YMARK_TYPE.PERCENTAGE){
                    str += formattedDecimalToPercentage(dataPoint.getValueY())+")";
                }
                float textLength = FontUtil.getFontlength(paintLabel,str);
                if((rectChart.right- point.x)>textLength){
                    canvas.drawText(str, point.x + 10,
                            point.y-focusTextHeight+focusTextLead - 10, paintLabel);
                }else{
                    canvas.drawText(str, point.x - 10 - textLength,
                            point.y-focusTextHeight+focusTextLead - 10, paintLabel);
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
