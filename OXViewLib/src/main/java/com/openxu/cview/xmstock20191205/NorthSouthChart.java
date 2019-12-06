package com.openxu.cview.xmstock20191205;

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
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.xmstock.BaseChart;
import com.openxu.cview.xmstock.bean.DataPoint;
import com.openxu.cview.xmstock.bean.FocusInfo;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;
import com.openxu.utils.NumberFormatUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class NorthSouthChart extends BaseChart {

    //设置数据
    private List<List<String>> dataList;
    // = new String[]{"09:30", "10:30", "11:30/13:00", "14:00", "15:00"};
    private String[] lableXArray;
    private String[] lableArray;   //指标文字
    //计算后的数据
    private List<DataPoint> lableXPointList = new ArrayList<>();
    private List<DataPoint> lableYLPointList = new ArrayList<>();
    private List<DataPoint> lableYRPointList = new ArrayList<>();
    private List<ArrayList<DataPoint>> linePointList;
    private float YMARK_L =  1;    //Y轴刻度间隔
    private float YMARK_MAX_L =  Float.MIN_VALUE;    //Y轴刻度最大值
    private float YMARK_MIN_L =  Float.MAX_VALUE;    //Y轴刻度最小值
    private float YMARK_R =  1;    //Y轴刻度间隔
    private float YMARK_MAX_R =  Float.MIN_VALUE;    //Y轴刻度最大值
    private float YMARK_MIN_R =  Float.MAX_VALUE;    //Y轴刻度最小值
    private float lableLead, lableHeight;

    public enum ChartType{
        TYPE_T,   //今日流向
        TYPE_DW,   //历史每日/周流向
    }
    /**可以设置的属性*/
    private ChartType chartType;
    //设置XY轴刻度数量
    private int XMARK_NUM =  5;
    private int YMARK_NUM =  5;
    //设置线条颜色
    private int[] lineColor = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
    //设置曲线粗细
    private int lineSize = DensityUtil.dip2px(getContext(), 1.5f);
    //设置柱子宽度
    private int barSize = DensityUtil.dip2px(getContext(), 7);
    private float ONE_BAR_WEDTH;   //(计算)单个柱状占的宽度（包含space)
    //指标文字大小
    private int lableTextSize = (int)getResources().getDimension(R.dimen.ts_chart_lable);
    private int textSpaceLable = DensityUtil.dip2px(getContext(), 12);
    //设置坐标文字大小
    private int textSize = (int)getResources().getDimension(R.dimen.ts_chart_xy);
    //设置坐标文字颜色
    private int textColor = getResources().getColor(R.color.tc_chart_xy);
    //设置X坐标字体与横轴的距离
    private int textSpaceX = DensityUtil.dip2px(getContext(), 5);
    //设置Y坐标字体与左右的距离
    private int textSpaceY = DensityUtil.dip2px(getContext(), 1);
    //设置焦点线颜色 及 粗细
    private int focusLineColor = getResources().getColor(R.color.tc_chart_focus_line);
    private int focusLineSize = DensityUtil.dip2px(getContext(), 0.8f);


    public NorthSouthChart(Context context) {
        this(context, null);
    }
    public NorthSouthChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public NorthSouthChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        dataList = new ArrayList<>();
        touchEnable = true;
    }



    /***********************************设置属性set方法**********************************/
    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }
    public void setYMARK_NUM(int YMARK_NUM) {
        this.YMARK_NUM = YMARK_NUM;
    }
    public void setXMARK_NUM(int XMARK_NUM) {
        this.XMARK_NUM = XMARK_NUM;
    }

    /**
     * 设置x刻度
     * @param lableXArray X轴显示的刻度，如果不设置，会自动配置，但是需要设置x刻度显示数量
     */
    public void setLableX(String[] lableXArray){
        if(null!=lableXArray && lableXArray.length>0)
            this.lableXArray = lableXArray;
    }
    public void setlableArray(String[] lableArray) {
        this.lableArray = lableArray;
    }
    public void setLineColor(int[] lineColor) {
        this.lineColor = lineColor;
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
        evaluatorByData();
        invalidate();
    }

    /**设置数据*/
    public void setData(List<List<String>> dataList){
        if(null==dataList)
            return;
        this.dataList.clear();
        this.dataList.addAll(dataList);
    }

    /**重绘制*/
    public void refresh(){
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
        /**①、计算字体相关以及图表原点坐标*/
        paintLabel.setTextSize(textSize);
        lableHeight = FontUtil.getFontHeight(paintLabel);
        lableLead =  FontUtil.getFontLeading(paintLabel);

        paintLabel.setTextSize(lableTextSize);
        //图表主体矩形
        rectChart = new RectF(getPaddingLeft(),
                getPaddingTop() + FontUtil.getFontHeight(paintLabel) +
                        textSpaceLable +lableHeight/2,
                getMeasuredWidth()-getPaddingRight(),
                getMeasuredHeight()-getPaddingBottom() - lableHeight - textSpaceX - lableHeight/2);

        /**②、计算X标签绘制坐标*/
        float lableXSpace = 0;
        if(lableXArray==null){
            //从数据中抽取x刻度
            if(dataList.size()<=XMARK_NUM)
                XMARK_NUM = dataList.size();
            lableXArray = new String[XMARK_NUM];
            if(dataList.size()==XMARK_NUM){
                for(int i = 0 ; i < XMARK_NUM; i++)
                    lableXArray[i] = dataList.get(i).get(0);
            }else{
                lableXArray[0] = dataList.get(0).get(0);   //取第一条数据的x坐标
                lableXArray[XMARK_NUM-1] = dataList.get(dataList.size()-1).get(0);
                float space = dataList.size()/XMARK_NUM;
                boolean m = dataList.size()%XMARK_NUM>0;
                float indexCount = 0;
                for(int i = 1; i < XMARK_NUM-1; i++){
                    indexCount  += space;
                    //四舍五入，均匀分布
                    lableXArray[i] = dataList.get(
                                ((indexCount - (int)indexCount)>0.5)?
                                (int)indexCount + 1 : (int)indexCount
                            ).get(0);
                }
            }
        }
        //计算x刻度坐标
        for(String lableX : lableXArray){
            lableXSpace += FontUtil.getFontlength(paintLabel, lableX);
        }
        lableXSpace = (rectChart.right - rectChart.left - lableXSpace)/(lableXArray.length -1);
        lableXPointList = new ArrayList<>();
        if(lableXSpace>0){
            float left = rectChart.left;
            for(int i = 0; i<lableXArray.length; i++){
                String lableX = lableXArray[i];
                lableXPointList.add(new DataPoint(lableX, 0, new PointF(
                        left,rectChart.bottom + textSpaceX + lableLead)));
                left += (FontUtil.getFontlength(paintLabel, lableXArray[i])+lableXSpace);
            }
        }else{
            //如果X轴标签字体过长的情况需要特殊处理
            float oneWidth = (rectChart.right - rectChart.left)/lableXArray.length;
            for(int i = 0; i<lableXArray.length; i++){
                String lableX = lableXArray[i];
                float xLen = FontUtil.getFontlength(paintLabel, lableX);
                lableXPointList.add(new DataPoint(lableX, 0, new PointF(
                        rectChart.left+i*oneWidth+(oneWidth-xLen)/2,rectChart.bottom + textSpaceX + lableLead)));
            }
        }

        /**③、计算Y刻度最大值和最小值以及幅度*/
        evaluatorYMark(true);
        evaluatorYMark(false);

        /**④、计算点的坐标，如果有动画的情况下，边绘制边计算会耗费性能，所以先计算*/
        linePointList = new ArrayList<>();
        linePointList.add(new ArrayList<DataPoint>());
        linePointList.add(new ArrayList<DataPoint>());
        if(chartType==ChartType.TYPE_T){
            float oneSpace = (rectChart.right - rectChart.left) / (60*4);   //分时图
            //今日 ["1558","27.3亿元","26208.240","+0.56%"]
            for(int i=0; i<dataList.size(); i++){
                //左Y刻度线
                List<String> itemList = dataList.get(i);
                float valueY = Float.valueOf(itemList.get(2));
                PointF point = new PointF(rectChart.left + i * oneSpace,
                        rectChart.bottom -
                                (rectChart.bottom-rectChart.top)/(YMARK_MAX_L - YMARK_MIN_L) * (valueY-YMARK_MIN_L));
                linePointList.get(0).add(new DataPoint(itemList.get(0), valueY, point));
                //右Y刻度线
                valueY = Float.valueOf(itemList.get(1).replace("亿元", ""));
                point = new PointF(rectChart.left + i * oneSpace, rectChart.bottom -
                        (rectChart.bottom-rectChart.top)/(YMARK_MAX_R - YMARK_MIN_R) * (valueY-YMARK_MIN_R));
                linePointList.get(1).add(new DataPoint(itemList.get(0), valueY, point));
            }
        }else{
            float oneSpace = (rectChart.right - rectChart.left) / (dataList.size());
            //历史 ["2019-07-05","37.60亿元","28774.83","+0.81%"]
            ONE_BAR_WEDTH = (rectChart.right - rectChart.left)/dataList.size();
            for(int i=0; i<dataList.size(); i++){
                //左Y刻度线
                List<String> itemList = dataList.get(i);
                float valueY = Float.valueOf(itemList.get(2));
                PointF point = new PointF(rectChart.left+ONE_BAR_WEDTH*i+ONE_BAR_WEDTH/2, rectChart.bottom -
                        (rectChart.bottom-rectChart.top)/(YMARK_MAX_L - YMARK_MIN_L) * (valueY-YMARK_MIN_L));
                linePointList.get(0).add(new DataPoint(itemList.get(0), valueY, point));
                //右Y刻度线
                valueY = Float.valueOf(itemList.get(1).replace("亿元", ""));
                point = new PointF(rectChart.left + i * oneSpace, rectChart.bottom -
                        (rectChart.bottom-rectChart.top)/(YMARK_MAX_R - YMARK_MIN_R) * (valueY-YMARK_MIN_R));
                linePointList.get(1).add(new DataPoint(itemList.get(0), valueY, point));
            }
        }
//        if(null!=onFocusChangeListener)
//            onFocusChangeListener.onfocus(focusInfo);
    }
    private void evaluatorYMark(boolean left){
        float YMARK;
        float YMARK_MAX = Float.MIN_VALUE;
        float YMARK_MIN = Float.MAX_VALUE;
        for(List<String> list : dataList){
            String str = left?list.get(2):list.get(1);
            if(str.contains("亿元"))
                str = str.replaceAll("亿元", "");
            YMARK = Float.parseFloat(str);
            if(YMARK>YMARK_MAX)
                YMARK_MAX = YMARK;
            if(YMARK<YMARK_MIN)
                YMARK_MIN = YMARK;
        }
        LogUtil.w(TAG, "Y轴真实YMARK_MIN="+YMARK_MIN+"   YMARK_MAX="+YMARK_MAX);
        float ce = (YMARK_MAX-YMARK_MIN)/10;
        YMARK_MAX += ce;
        YMARK_MIN -= ce;

        YMARK = (YMARK_MAX-YMARK_MIN)/(YMARK_NUM - 1);
        if(left){
            YMARK_L = YMARK;
            YMARK_MAX_L = YMARK_MAX;
            YMARK_MIN_L = YMARK_MIN;
        }else{
            YMARK_R = YMARK;
            YMARK_MAX_R = YMARK_MAX;
            YMARK_MIN_R = YMARK_MIN;
        }
        List<DataPoint> lableYPointList = left?lableYLPointList:lableYRPointList;
        lableYPointList.clear();
        float yMarkSpace = (rectChart.bottom - rectChart.top)/(YMARK_NUM-1);
        DecimalFormat df = new DecimalFormat("0.0");
        for (int i = 0; i < YMARK_NUM; i++) {
            float mark = Float.parseFloat(df.format(YMARK_MIN + i * YMARK));
            lableYPointList.add(new DataPoint("", mark,
                    new PointF(left?rectChart.left+textSpaceY:rectChart.right-textSpaceY-
                            FontUtil.getFontlength(paintLabel, mark+""),
                            rectChart.bottom - yMarkSpace * i  - lableHeight/2 + lableLead)));
        }
    }


  /*  private FocusInfo focusInfo;
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
*/

    /**绘制图表基本框架*/
    @Override
    public void drawDefult(Canvas canvas) {
        if(null==linePointList || linePointList.size()<=0)
            return;
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(defColor);
        drawTopLable(canvas);
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

    /*** 绘制上方lable*/
    private void drawTopLable(Canvas canvas) {
        if(null==lableArray)
            return;
        String lableL, lableR;
        if(chartType==ChartType.TYPE_T){
            lableL = "指数价格";
            lableR = "金额 (亿)";
        }else{
            lableL = "指数价格";
            lableR = "金额 (亿)";
        }
        paintLabel.setTextSize(lableTextSize);
        int itemSpace = DensityUtil.dip2px(getContext(), 12);   //指标lable间距
        int lableSpace = DensityUtil.dip2px(getContext(), 2);  //指标y与圆圈的距离
        int radis = DensityUtil.dip2px(getContext(), 3);    //圆圈半径
        float lableHeight = FontUtil.getFontHeight(paintLabel);
        float lableLead =  FontUtil.getFontLeading(paintLabel);
        int lableLengthCount = (lableSpace + radis*2) * lableArray.length + itemSpace * (lableArray.length-1);
        for(String lable : lableArray){
            lableLengthCount += FontUtil.getFontlength(paintLabel, lable);
        }

        float lableL_len =  FontUtil.getFontlength(paintLabel, lableL);
        float lableR_len =  FontUtil.getFontlength(paintLabel, lableR);
        //指标开始绘制x坐标
        float startX = rectChart.left + lableL_len +
                (rectChart.right - rectChart.left - lableLengthCount - lableL_len - lableR_len)/2;

        paintLabel.setColor(Color.parseColor("#5E5E5E"));
        paintLabel.setFakeBoldText(true);   //加粗
        canvas.drawText(lableL, rectChart.left, getPaddingTop()+lableLead, paintLabel);
        canvas.drawText(lableR, rectChart.right-lableR_len, getPaddingTop()+lableLead, paintLabel);
        paintLabel.setColor(Color.parseColor("#939393"));
        paintLabel.setFakeBoldText(false);
        for(int i = 0; i<lableArray.length; i++){
            paint.setColor(lineColor[i]);
            canvas.drawCircle(startX+radis, getPaddingTop()+lableHeight/2, radis, paint);
            startX += radis*2+lableSpace;
            canvas.drawText(lableArray[i],startX,getPaddingTop()+lableLead, paintLabel);
            startX += FontUtil.getFontlength(paintLabel, lableArray[i]) + itemSpace;
        }
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
        if(chartType==ChartType.TYPE_T){
            //今日图表，需要绘制x刻度线
            for(DataPoint lable : lableXPointList){
                canvas.drawLine(lable.getPoint().x, rectChart.bottom, lable.getPoint().x,
                        rectChart.bottom+DensityUtil.dip2px(getContext(), 3), paint);
            }
        }
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
        paintLabel.setTextSize(textSize);
        paintLabel.setColor(textColor);
        paintLabel.setTextSize(textSize);
        paintLabel.setColor(textColor);
        for(DataPoint lable : lableYLPointList){
            canvas.drawText(lable.getValueY()+"", lable.getPoint().x, lable.getPoint().y, paintLabel);
        }
        for(DataPoint lable : lableYRPointList){
            canvas.drawText(lable.getValueY()+"", lable.getPoint().x, lable.getPoint().y, paintLabel);
        }
    }
    /**绘制曲线*/
    private void drawDataPath(Canvas canvas) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(lineSize);
       if(chartType == ChartType.TYPE_T){
            //今日
            for(int j = 0; j < linePointList.size(); j++){ //一条一条的绘制
                List<DataPoint> lineList = linePointList.get(j);
                paint.setColor(lineColor[j]);
                Path path = new Path();
                PointF lastPoint = null;
                for(int i = 0 ; i<lineList.size(); i++){
                    if(i == 0){
                        path.moveTo(lineList.get(i).getPoint().x, lineList.get(i).getPoint().y);
                    }else{
                        //quadTo：二阶贝塞尔曲线连接前后两点，这样使得曲线更加平滑
                        path.quadTo(lastPoint.x, lastPoint.y, lineList.get(i).getPoint().x, lineList.get(i).getPoint().y);
                    }
                    lastPoint = lineList.get(i).getPoint();
                }
                canvas.drawPath(path, paint);
                paint.setStyle(Paint.Style.FILL);
                paint.setAlpha(100);  //设置alpha不透明度，范围为0~255
                canvas.drawCircle(lastPoint.x, lastPoint.y, DensityUtil.dip2px(getContext(), 5), paint);
                paint.setAlpha(255);  //设置alpha不透明度，范围为0~255
                canvas.drawCircle(lastPoint.x, lastPoint.y, DensityUtil.dip2px(getContext(), 2), paint);
                paint.setStyle(Paint.Style.STROKE);
            }
       }else{
           //历史 ["2019-07-05","37.60亿元","28774.83","+0.81%"]
           //绘制柱子
           List<DataPoint> barList = linePointList.get(0);
           paint.setStyle(Paint.Style.FILL);
           float inOut ;
           for(int i = 0 ;i<barList.size(); i++) {
               DataPoint dataPoint = barList.get(i);
               //净流入、流出颜色选择
               inOut = Float.parseFloat(dataList.get(i).get(3).replace("+", "").replace("%", ""));
               paint.setColor(inOut>0?lineColor[0]:lineColor[1]);
               canvas.drawRect(new Rect((int)(dataPoint.getPoint().x-barSize/2),
                       (int)dataPoint.getPoint().y,
                       (int)(dataPoint.getPoint().x+barSize/2),
                       (int)rectChart.bottom), paint);
           }
           //绘制指数线
           List<DataPoint> lineList = linePointList.get(1);
           paint.setStyle(Paint.Style.STROKE);
           paint.setColor(lineColor[2]);
           Path path = new Path();
           PointF lastPoint = null;
           for(int i = 0 ; i<lineList.size(); i++){
               if(i == 0){
                   path.moveTo(lineList.get(i).getPoint().x, lineList.get(i).getPoint().y);
               }else{
                   //quadTo：二阶贝塞尔曲线连接前后两点，这样使得曲线更加平滑
                   path.quadTo(lastPoint.x, lastPoint.y, lineList.get(i).getPoint().x, lineList.get(i).getPoint().y);
               }
               lastPoint = lineList.get(i).getPoint();
           }
           canvas.drawPath(path, paint);
       }
    }

    /**绘制焦点*/
    private void drawFocus(Canvas canvas){
       /* if(!onFocus || null==focusInfo)
            return;
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(focusLineSize);
        paint.setColor(focusLineColor);
        //竖直线
        canvas.drawLine(focusInfo.getPoint().x, rectChart.bottom, focusInfo.getPoint().x, rectChart.top, paint);
*/
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
