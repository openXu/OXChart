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
import com.openxu.cview.xmstock20191205.bean.NSFocusInfo;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;
import com.openxu.utils.NumberFormatUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class NorthSouthChart extends BaseChart {

    //计算后的数据
    private int dataNumCount = 60*4;// 今日数据（60分钟*4小时）  ，历史日周数据按照接口返回数量
    private List<DataPoint> lableXPointList = new ArrayList<>();   //计算X轴坐标点容器
    private List<DataPoint> lableYLPointList = new ArrayList<>();  //计算左侧Y轴坐标点容器
    private List<DataPoint> lableYRPointList = new ArrayList<>();  //计算右侧Y轴坐标点容器
    private List<ArrayList<DataPoint>> linePointList;
    private float YMARK_MAX_L =  Float.MIN_VALUE;    //Y轴刻度最大值
    private float YMARK_MIN_L =  Float.MAX_VALUE;    //Y轴刻度最小值
    private float YMARK_MAX_R =  Float.MIN_VALUE;    //Y轴刻度最大值
    private float YMARK_MIN_R =  Float.MAX_VALUE;    //Y轴刻度最小值
    private float lableLead, lableHeight;

    public enum ChartType{
        TYPE_T,   //今日流向
        TYPE_DW,   //历史每日/周流向
    }
    /**可以设置的属性 ★表示必设置*/
    //设置数据
    private List<List<String>> dataList;   //★图表数据
    private String[] lableXArray;   // X坐标刻度文字设置 new String[]{"09:30", "10:30", "11:30/13:00", "14:00", "15:00"};
    private String[] lableArray;    //★指标文字
    private ChartType chartType;    //★图表类型
    private int XMARK_NUM =  5;     //X轴刻度数量   和lableXArray互斥（必须设置其中一个）
    private int YMARK_NUM =  5;     //Y轴刻度数量
    private int[] lableColor;//★设置lable对应折线颜色
    private int[] upDownColor;//★设置涨跌颜色(注意顺序，第一个为涨（红色）， 第二个为跌（绿色）)
    private int lineSize = DensityUtil.dip2px(getContext(), 1.2f);//设置曲线粗细
    private int barSize = DensityUtil.dip2px(getContext(), 7);  //设置柱子宽度
    private float ONE_BAR_WEDTH;   //不需要设置 (计算)单个柱状占的宽度（包含space)
    private int lableTextSize = (int)getResources().getDimension(R.dimen.ts_chart_lable); //指标文字大小
    private int textSpaceLable = DensityUtil.dip2px(getContext(), 12);  //指标文字间距
    private int textSize = (int)getResources().getDimension(R.dimen.ts_chart_xy);//坐标刻度文字大小
    private int textColor = getResources().getColor(R.color.tc_chart_xy);//设置坐标文字颜色
    private int textSpaceX = DensityUtil.dip2px(getContext(), 5);//设置X坐标字体与横轴的距离
    private int textSpaceY = DensityUtil.dip2px(getContext(), 1);  //设置Y坐标字体与左右的距离
    //设置焦点相关
    private int focusLineColor = getResources().getColor(R.color.tc_chart_focus_line);//焦点交互线颜色
    private int focusLineSize = DensityUtil.dip2px(getContext(), 0.8f); //焦点交互线宽度
    private int focusPanelColor = Color.parseColor("#5f93e7");     //焦点面板背景色
    private int focusTextColor = Color.parseColor("#ffffff");      //焦点面板上文字颜色
    private int focusTextSize =  DensityUtil.sp2px(getContext(), 8);  //焦点面板上文字大小


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
    public void setlableColor(int[] lableColor) {
        this.lableColor = lableColor;
    }
    public void setUpDownColor(int[] upDownColor) {
        this.upDownColor = upDownColor;
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
        if(chartType == ChartType.TYPE_T){
            dataNumCount = 60*4;
        }else{
            dataNumCount = dataList.size();
            lableXArray = null;
        }
            dataNumCount = chartType == ChartType.TYPE_T ? 60*4 : dataList.size();
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
        centerPoint = new PointF(rectChart.left+(rectChart.right-rectChart.left)/2,
                rectChart.top+(rectChart.bottom-rectChart.top)/2);
        /**②、计算X标签绘制坐标*/
        float lableXSpace = 0;
        if(lableXArray==null){
            //从数据中抽取x刻度
            if(dataNumCount<=XMARK_NUM)
                XMARK_NUM = dataNumCount;
            lableXArray = new String[XMARK_NUM];
            if(dataNumCount==XMARK_NUM){
                for(int i = 0 ; i < XMARK_NUM; i++)
                    lableXArray[i] = dataList.get(i).get(0);
            }else{
                lableXArray[0] = dataList.get(0).get(0);   //取第一条数据的x坐标
                lableXArray[XMARK_NUM-1] = dataList.get(dataNumCount-1).get(0);
                float space = dataNumCount/XMARK_NUM;
                boolean m = dataNumCount%XMARK_NUM>0;
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
            float oneSpace = (rectChart.right - rectChart.left) / dataNumCount;   //分时图
            //今日 ["1558","27.3亿元","26208.240","+0.56%"]
            for(int i=0; i<dataList.size(); i++){
                List<String> itemList = dataList.get(i);
                //右Y刻度线
                float valueY = Float.valueOf(itemList.get(1).replace("亿元", ""));
                PointF point = new PointF(rectChart.left + i * oneSpace, rectChart.bottom -
                        (rectChart.bottom-rectChart.top)/(YMARK_MAX_R - YMARK_MIN_R) * (valueY-YMARK_MIN_R));
                linePointList.get(0).add(new DataPoint(itemList.get(0), valueY, point));
                //左Y刻度线
                valueY = Float.valueOf(itemList.get(2));
                point = new PointF(rectChart.left + i * oneSpace,
                        rectChart.bottom -
                                (rectChart.bottom-rectChart.top)/(YMARK_MAX_L - YMARK_MIN_L) * (valueY-YMARK_MIN_L));
                linePointList.get(1).add(new DataPoint(itemList.get(0), valueY, point));
            }
        }else{
            //历史 ["2019-07-05","37.60亿元","28774.83","+0.81%"]
            ONE_BAR_WEDTH = (rectChart.right - rectChart.left)/dataList.size();
            for(int i=0; i<dataList.size(); i++){
                List<String> itemList = dataList.get(i);
                //右Y刻度线
                float valueY = Float.valueOf(itemList.get(1).replace("亿元", ""));
                PointF point = new PointF(rectChart.left+ONE_BAR_WEDTH*i+ONE_BAR_WEDTH/2,
                        valueY>0? centerPoint.y - (centerPoint.y-rectChart.top)/YMARK_MAX_R * valueY :
                                centerPoint.y + (rectChart.bottom-centerPoint.y)/YMARK_MIN_R * valueY);
                linePointList.get(0).add(new DataPoint(itemList.get(0), valueY, point));
                //左Y刻度线
                valueY = Float.valueOf(itemList.get(2));
                point = new PointF(rectChart.left+ONE_BAR_WEDTH*i+ONE_BAR_WEDTH/2, rectChart.bottom -
                        (rectChart.bottom-rectChart.top)/(YMARK_MAX_L - YMARK_MIN_L) * (valueY-YMARK_MIN_L));
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
        if(chartType == ChartType.TYPE_DW && !left){
            //历史日、周，计算净流入资金中点值0
            float YMARK_ABS = Math.abs(YMARK_MAX) > Math.abs(YMARK_MIN)?Math.abs(YMARK_MAX):Math.abs(YMARK_MIN);
            YMARK_MAX = YMARK_ABS;
            YMARK_MIN = -YMARK_ABS;
            YMARK = (YMARK_MAX-YMARK_MIN)/(YMARK_NUM - 1);
        }else{
            YMARK = (YMARK_MAX-YMARK_MIN)/(YMARK_NUM - 1);
        }
        if(left){
            YMARK_MAX_L = YMARK_MAX;
            YMARK_MIN_L = YMARK_MIN;
        }else{
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

   private NSFocusInfo focusInfo;
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

            focusInfo = new NSFocusInfo();
            focusInfo.setPoint(point);
            List<DataPoint> objs = new ArrayList<>();
            for(List<DataPoint> points : linePointList)
                objs.add(points.get(index));
            //交点处坐标数据
            focusInfo.setDataPoints(objs);
            //焦点处后台原始数据
            focusInfo.setFocusData(dataList.get(index));
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
        void onfocus(NSFocusInfo focusInfo);
    }

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
            paint.setColor(lableColor[i]);
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
           //今日 ["1558","27.3亿元","26208.240","+0.56%"]
            for(int j = 0; j < linePointList.size(); j++){ //先画右边刻度线，再画左边
                List<DataPoint> lineList = linePointList.get(j);
                paint.setColor(lableColor[j]);
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
           for(int i = 0 ;i<barList.size(); i++) {
               DataPoint dataPoint = barList.get(i);
               //"37.60亿元"  净流入、流出颜色选择
               //以中心点0为分割线，红色向上，绿色向下
               if(dataPoint.getValueY()>0){
                   paint.setColor(upDownColor[0]);  //红色
                   canvas.drawRect(new Rect((int)(dataPoint.getPoint().x-barSize/2),
                           (int)dataPoint.getPoint().y,
                           (int)(dataPoint.getPoint().x+barSize/2),
                           (int)centerPoint.y), paint);
               }else{
                   paint.setColor(upDownColor[1]);  //绿色
                   canvas.drawRect(new Rect((int)(dataPoint.getPoint().x-barSize/2),
                           (int)centerPoint.y,
                           (int)(dataPoint.getPoint().x+barSize/2),
                           (int)dataPoint.getPoint().y), paint);
               }
           }
           //绘制指数线
           List<DataPoint> lineList = linePointList.get(1);
           paint.setStyle(Paint.Style.STROKE);
           paint.setColor(lableColor[2]);
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
        if(!onFocus || null==focusInfo)
            return;
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(focusLineSize);
        paint.setColor(focusLineColor);
        //垂直方向焦点线
        canvas.drawLine(focusInfo.getDataPoints().get(0).getPoint().x, rectChart.bottom,
                focusInfo.getDataPoints().get(0).getPoint().x, rectChart.top, paint);
        int radis = DensityUtil.dip2px(getContext(), 2);
        if(chartType==ChartType.TYPE_T){
            //横向焦点线
            canvas.drawLine(rectChart.left, focusInfo.getDataPoints().get(0).getPoint().y,
                    rectChart.right, focusInfo.getDataPoints().get(0).getPoint().y, paint);
            //折线上面焦点圆圈
            for(int i = 0; i< focusInfo.getDataPoints().size(); i++){
                DataPoint dataPoint = focusInfo.getDataPoints().get(i);
                paint.setColor(lableColor[i]);
                canvas.drawCircle(dataPoint.getPoint().x, dataPoint.getPoint().y, radis, paint);
            }
        }else{
            //历史图，指数焦点
            DataPoint dataPoint = focusInfo.getDataPoints().get(1);   //指数
            canvas.drawLine(rectChart.left, dataPoint.getPoint().y,
                    rectChart.right, dataPoint.getPoint().y, paint);
            paint.setColor(lableColor[2]);
            canvas.drawCircle(dataPoint.getPoint().x, dataPoint.getPoint().y, radis, paint);
        }

        //绘制焦点数据显示面板
        paintLabel.setTextSize(focusTextSize);
        String maxStr = "恒生指数跌涨浮      00.00亿元";
        float maxStrLength = FontUtil.getFontlength(paintLabel, maxStr);
        float txSpace = DensityUtil.dip2px(getContext(), 2);
        float txHeight = FontUtil.getFontHeight(paintLabel);
        float txLead = FontUtil.getFontLeading(paintLabel);
        float panelWidth = maxStrLength+txSpace*4;
        float panelHeight = FontUtil.getFontHeight(paintLabel)*4+txSpace*3+txSpace*4;//四行字，3个字行距，2个2倍上下间距
        paint.setColor(focusPanelColor);
        RectF rectF;
        if(focusInfo.getPoint().x<=centerPoint.x) //绘制在左上角
            rectF = new RectF(rectChart.left, rectChart.top, rectChart.left+panelWidth, rectChart.top+panelHeight);
        else
            rectF = new RectF(rectChart.right-panelWidth, rectChart.top, rectChart.right, rectChart.top+panelHeight);
        //绘制面板背景
        paint.setColor(focusPanelColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rectF, paint);
        //绘制文字
        if(chartType == ChartType.TYPE_T){
            //["0930","1.0亿元","26300.510","+0.91%"]
            drawFocusText(canvas, "时间", focusInfo.getFocusData().get(0), focusTextColor, focusTextColor,1, rectF, txSpace, txHeight, txLead);
            drawFocusText(canvas, "净流入金额", focusInfo.getFocusData().get(1), focusTextColor, focusTextColor,2, rectF, txSpace, txHeight, txLead);
            int udColor = Float.parseFloat(focusInfo.getFocusData().get(3).replace("%", ""))>0?upDownColor[0]:upDownColor[1];
            drawFocusText(canvas, "上证指数价格", focusInfo.getFocusData().get(2), focusTextColor, udColor,3, rectF, txSpace, txHeight, txLead);
            drawFocusText(canvas, "上证指数跌涨幅", focusInfo.getFocusData().get(3), focusTextColor, udColor,4, rectF, txSpace, txHeight, txLead);
        }else{
            //["2019-11-14","22.85亿元","2909.87","+0.16%"]
            drawFocusText(canvas, "时间", focusInfo.getFocusData().get(0), focusTextColor, focusTextColor,1, rectF, txSpace, txHeight, txLead);
            drawFocusText(canvas, "净流入金额", focusInfo.getFocusData().get(1), focusTextColor, focusTextColor,2, rectF, txSpace, txHeight, txLead);
            int udColor = Float.parseFloat(focusInfo.getFocusData().get(3).replace("%", ""))>0?upDownColor[0]:upDownColor[1];
            drawFocusText(canvas, "恒生指数价格", focusInfo.getFocusData().get(2), focusTextColor, udColor,3, rectF, txSpace, txHeight, txLead);
            drawFocusText(canvas, "恒生指数跌涨幅", focusInfo.getFocusData().get(3), focusTextColor, udColor,4, rectF, txSpace, txHeight, txLead);
        }
    }

    private void drawFocusText(Canvas canvas, String text, String value, int txColor1, int txColor2,
                               int line, RectF rectF, float txSpace, float txHeight, float txLead){
        float y = rectF.top + txSpace*2 + (line-1)*(txSpace+txHeight) + txLead;
        paintLabel.setColor(txColor1);
        canvas.drawText(text, rectF.left+txSpace*2, y, paintLabel);
        paintLabel.setColor(txColor2);
        canvas.drawText(value, rectF.right - txSpace*2 - FontUtil.getFontlength(paintLabel, value), y, paintLabel);
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
