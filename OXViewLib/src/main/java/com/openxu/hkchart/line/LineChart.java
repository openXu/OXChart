package com.openxu.hkchart.line;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Scroller;

import com.openxu.hkchart.BaseChart;
import com.openxu.hkchart.element.FocusPanelText;
import com.openxu.hkchart.element.MarkType;
import com.openxu.hkchart.element.XAxisMark;
import com.openxu.hkchart.element.YAxisMark;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;
import com.openxu.utils.NumberFormatUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * autour : openXu
 * date : 2017/7/24 10:46
 * className : LineChart
 * version : 1.0
 * description : 曲线、折线图
 *
 */
public class LineChart extends BaseChart implements View.OnTouchListener {

    public enum LineType{
        CURVE,   //曲线
        BROKEN   //折线
    }
    /**设置*/
    private List<List<LinePoint>> lineData;
    private YAxisMark yAxisMark;
    private XAxisMark xAxisMark;
    private LineType lineType = LineType.BROKEN;
    private boolean scaleAble = true;  //是否支持放大
    private boolean scrollAble = true;  //是否支持滚动
    private boolean showBegin = true;    //当数据超出一屏宽度时，实现最后的数据
//    private float lineWidth = DensityUtil.dip2px(getContext(), 1.5f);
    private float lineWidth = DensityUtil.dip2px(getContext(), 1f);
    private int[] lineColor = new int[]{
            Color.parseColor("#f46763"),
            Color.parseColor("#3cd595"),
            Color.parseColor("#4d7bff"),
            Color.parseColor("#4d7bff")};
    //设置焦点线颜色 及 粗细
    private FocusPanelText[] focusPanelText;
    private int focusLineColor = Color.parseColor("#319A5A");
    private int focusLineSize = DensityUtil.dip2px(getContext(), 1f);
    private int foucsRectTextSpace = DensityUtil.dip2px(getContext(), 3);
    private int foucsRectSpace = DensityUtil.dip2px(getContext(), 6);
    //焦点面板矩形宽高
    private float foucsRectWidth;
    private float foucsRectHeight;

    /**计算*/
    private RectF chartRect = new RectF();                //图表矩形
    private int pageShowNum;       //第一次页面总数据量
    private int maxPointNum;       //点最多的线的点数量
    private int maxPointIndex;     //点最多的线的索引
    private float pointWidthMin;   //最初的每个点占据的宽度，最小缩放值
    private float pointWidthMax;   //最初的每个点占据的宽度，最大放大值
    private float pointWidth;      //每个点占据的宽度
    private float scrollXMax;      //最大滚动距离，是一个负值
    private float scrollx;         //当前滚动距离，默认从第一条数据绘制（scrollx==0），如果从最后一条数据绘制（scrollx==scrollXMax）

    protected GestureDetector mGestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    protected Scroller mScroller;

    public LineChart(Context context) {
        this(context, null);
    }

    public LineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setOnTouchListener(this);
        mGestureDetector = new GestureDetector(getContext(), new MyOnGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(context, new MyOnScaleGestureListener());
        mScroller = new Scroller(context);
//        showAnim = false;
        //默认的XY轴刻度设置
        xAxisMark = new XAxisMark.Builder(getContext())
                .lableNum(5)
                .build();
        yAxisMark = new YAxisMark.Builder(getContext())
                .lableNum(6)
                .markType(MarkType.Integer)
                .unit("")
                .build();
    }

    /***********************************1. setting👇**********************************/
    public void setYAxisMark(YAxisMark yAxisMark) {
        this.yAxisMark = yAxisMark;
    }
    public YAxisMark getyAxisMark() {
        return yAxisMark;
    }

    public void setLineType(LineType lineType) {
        this.lineType = lineType;
    }

    public void setXAxisMark(XAxisMark xAxisMark) {
        this.xAxisMark = xAxisMark;
    }
    public void setShowBegin(boolean showBegin) {
        this.showBegin = showBegin;
    }
    public void setPageShowNum(int pageShowNum) {
        this.pageShowNum = pageShowNum;
    }
    public void setScaleAble(boolean scaleAble) {
        this.scaleAble = scaleAble;
        if(scaleAble)   //支持缩放的一定支持滚动
            this.scrollAble = true;
    }
    public void setScrollAble(boolean scrollAble) {
        this.scrollAble = scrollAble;
    }

    public void setLineColor(int[] lineColor) {
        this.lineColor = lineColor;
    }
    public void setFocusPanelText(FocusPanelText[] focusPanelText) {
        this.focusPanelText = focusPanelText;
    }

    public void setData(List<List<LinePoint>> lineData) {
//        Log.w(TAG, "设置数据，总共"+lineData.size()+"条线，每条线"+lineData.get(0).size()+"个点");
        this.lineData = lineData;
        if(showAnim)
            chartAnimStarted = false;
        try {
            calculate();
        }catch (Exception e){
            e.printStackTrace();
        }
        setLoading(false);
    }
    /***********************************1. setting👆**********************************/

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        try {
            calculate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /***********************************2. 计算👇**********************************/

    private void calculate() {
        if(lineData==null || lineData.size()==0)
            return;

        maxPointNum = 0;
        maxPointIndex = 0;
        for(int i = 0; i< lineData.size(); i++){
            if(maxPointNum<=lineData.get(i).size()){
                maxPointNum = lineData.get(i).size();
                maxPointIndex = i;
            }
        }
        Log.w(TAG, "点最多的线索引为："+maxPointIndex+"    点数："+maxPointNum);

        paintText.setTextSize(xAxisMark.textSize);
        xAxisMark.textHeight = FontUtil.getFontHeight(paintText);
        xAxisMark.textLead = FontUtil.getFontLeading(paintText);
        Log.w(TAG, "x轴字体高度："+xAxisMark.textHeight+"   textLead："+ xAxisMark.textLead+"  xAxisMark.textSpace"+xAxisMark.textSpace);
        //确定图表最下放绘制位置
        chartRect.bottom = rectChart.bottom - xAxisMark.textHeight - xAxisMark.textSpace;
        Log.w(TAG, "rectChart.bottom："+rectChart.bottom + "    chartRect.bottom="+chartRect.bottom);
        xAxisMark.drawPointY = chartRect.bottom + xAxisMark.textSpace + xAxisMark.textLead;
        calculateYMark();
        paintText.setTextSize(yAxisMark.textSize);
        yAxisMark.textHeight = FontUtil.getFontHeight(paintText);
        yAxisMark.textLead = FontUtil.getFontLeading(paintText);
        Log.w(TAG, "y轴字体高度："+yAxisMark.textHeight+"   textLead："+ yAxisMark.textLead);
        String maxLable = yAxisMark.getMarkText(yAxisMark.cal_mark_max).length() > yAxisMark.getMarkText(yAxisMark.cal_mark_min).length()?
                yAxisMark.getMarkText(yAxisMark.cal_mark_max) : yAxisMark.getMarkText(yAxisMark.cal_mark_min);
        LogUtil.w(TAG, "Y刻度最大字符串："+maxLable);
        if(!TextUtils.isEmpty(yAxisMark.unit))
            maxLable = yAxisMark.unit.length()>maxLable.length()?yAxisMark.unit:maxLable;
        LogUtil.w(TAG, "Y刻度最大字符串："+maxLable);
        chartRect.left =  (int)(getPaddingLeft() + yAxisMark.textSpace + FontUtil.getFontlength(paintText, maxLable));
        chartRect.top = rectChart.top + yAxisMark.textHeight/2 +
                (TextUtils.isEmpty(yAxisMark.unit)?0:(yAxisMark.textHeight+yAxisMark.textSpace));
        chartRect.right = rectChart.right;

        //没有设置展示数据量，则默认为全部展示
        if(pageShowNum<=0)
            pageShowNum = maxPointNum;
        if(maxPointNum < pageShowNum)    //最多的点小于需要显示的点，则全部展示
            pageShowNum = maxPointNum;
        Log.w(TAG, "计算pageShowNum="+pageShowNum);
        pointWidthMin = chartRect.width() / (maxPointNum-1);   //缩小到全部显示
        pointWidth = chartRect.width() / (pageShowNum-1);
        pointWidthMax = chartRect.width() / 4;   //最大只能放大到每个标签显示5个点
//        pointWidthMax = chartRect.width() / (xAxisMark.lableNum-1) / 5;   //最大只能放大到每个标签显示5个点
        Log.w(TAG, "缩放最小最大宽度="+pointWidthMin+"     "+pointWidthMax);
        //数据没有展示完，说明可以滚动
        scrollXMax = 0;
        if(pageShowNum<maxPointNum)
            scrollXMax = -(pointWidth*(maxPointNum-1) - chartRect.width());      //最大滚动距离，是一个负值
        scrollx = showBegin?0:scrollXMax;

        caculateXMark();

        if(focusPanelText!=null){
            //计算焦点面板
            //2020-10-16 06：00
            //零序电流:15.2KW
            //A相电流:15.2KW
            //A相电流:15.2KW
            //A相电流:15.2KW
            foucsRectWidth = 0;
            foucsRectHeight = foucsRectSpace * 2;
            String text;
            maxLable = (yAxisMark.getMarkText(yAxisMark.cal_mark_max).length() > yAxisMark.getMarkText(yAxisMark.cal_mark_min).length()?
                    yAxisMark.getMarkText(yAxisMark.cal_mark_max) : yAxisMark.getMarkText(yAxisMark.cal_mark_min))
                    +(TextUtils.isEmpty(yAxisMark.unit)?"":yAxisMark.unit);
            for(int i = 0; i< focusPanelText.length; i++){
                if(focusPanelText[i].show){
                    paintText.setTextSize(focusPanelText[i].textSize);
                    if(i == 0){
                        //x轴数据
                        foucsRectWidth = Math.max(foucsRectWidth, FontUtil.getFontlength(paintText, focusPanelText[i].text));
                        foucsRectHeight += FontUtil.getFontHeight(paintText);
                    }else{
//                        text = focusPanelText[i].text+maxLable+ yAxisMark.unit;
                        text = focusPanelText[i].text+maxLable;
                        foucsRectWidth = Math.max(foucsRectWidth, FontUtil.getFontlength(paintText, text));
                        Log.w(TAG, "计算面板："+text+"    "+foucsRectWidth);
                        foucsRectHeight += foucsRectTextSpace+FontUtil.getFontHeight(paintText);
                    }
                }
            }
            foucsRectWidth += foucsRectSpace * 4;
        }

        /**计算点坐标*/
      /*  for (int i = 0; i < lineData.size(); i++) {
            List<LinePoint> linePoints = lineData.get(i);
            for (int j = 0; j < linePoints.size(); j++) {
                if (linePoints.get(j).getValuey() == null)
                    continue;
                linePoints.get(j).setPoint(new PointF(
                        chartRect.left + j * pointWidth,
                        chartRect.bottom - (chartRect.bottom - chartRect.top) /
                                (yAxisMark.cal_mark_max - yAxisMark.cal_mark_min) * (linePoints.get(j).getValuey() - yAxisMark.cal_mark_min)
                ));
            }
        }*/

        Log.w(TAG, "计算scrollXMax="+scrollXMax+"   scrollx="+scrollx);
    }

    private int xindexSpace;
    private List<String> xlables = new ArrayList<>();
    private void caculateXMark(){
        xlables.clear();
        if(xAxisMark.lables!=null && xAxisMark.lables.length>0){
            xlables.addAll(Arrays.asList(xAxisMark.lables));
            return;
        }
//        float markSpace = (-scrollXMax+chartRect.width())/(xAxisMark.lableNum-1);
        float markSpace = chartRect.width()/(xAxisMark.lableNum-1);
        //每隔多少展示一个标签
        xindexSpace = (int)(markSpace/pointWidth);
        xindexSpace = Math.max(xindexSpace, 1);
//        List<List<LinePoint>> lineData;
    /*    if(showBegin){
            for(int i =0; i< lineData.get(maxPointIndex).size(); i++){
                if(i%indexSpace==0)
                    xlables.add(lineData.get(maxPointIndex).get(i).getValuex());
            }
        }else{
            for(int i = maxPointNum-1; i>=0 ; i--){
                if((i-(maxPointNum-1))%indexSpace==0) {
//                    LogUtil.w(TAG,  "标签索引"+i);
                    xlables.add(lineData.get(maxPointIndex).get(i).getValuex());
                }
            }
        }*/
//        Log.w(TAG, "矩形区域需要展示"+xAxisMark.lableNum+"个标签，"+"点宽度"+pointWidth+", 单个标签间距"+markSpace+"  每隔"+indexSpace+"个数据展示一个:"+xlables.size()+"   "+xlables);
    }

    private void calculateYMark() {
        if(maxPointNum==0){
            //没有数据
            yAxisMark.cal_mark_min = 0;
            yAxisMark.cal_mark_max = yAxisMark.lableNum - 1;
            yAxisMark.cal_mark = 1;
            return;
        }

        float redundance = 1.01f;  //y轴最大和最小值冗余
        yAxisMark.cal_mark_max =  -Float.MAX_VALUE;    //Y轴刻度最大值
        yAxisMark.cal_mark_min =  Float.MAX_VALUE;    //Y轴刻度最小值
        for(List<LinePoint> linePoints : lineData){
            for(LinePoint point : linePoints){
                yAxisMark.cal_mark_max = Math.max(yAxisMark.cal_mark_max, point.getValuey());
                yAxisMark.cal_mark_min = Math.min(yAxisMark.cal_mark_min, point.getValuey());
            }
        }
        LogUtil.w(TAG, "真实最小最大值："+yAxisMark.cal_mark_min+"  "+yAxisMark.cal_mark_max);
        //只有一个点的时候
        if(yAxisMark.cal_mark_min == yAxisMark.cal_mark_max){
            if(yAxisMark.cal_mark_min > 0) {
                yAxisMark.cal_mark_min = 0;
            }else if(yAxisMark.cal_mark_min ==0){
                yAxisMark.cal_mark_max = 1;
            }else if(yAxisMark.cal_mark_min < 0){
                yAxisMark.cal_mark_max = 0;
            }
        }
        if(yAxisMark.markType == MarkType.Integer){
            int min = yAxisMark.cal_mark_min>0?0:(int)yAxisMark.cal_mark_min;
            int max = (int)yAxisMark.cal_mark_max;
            int mark = (max-min)/(yAxisMark.lableNum - 1)+((max-min)%(yAxisMark.lableNum - 1)>0?1:0);
            mark = mark==0?1:mark;   //最大值和最小值都为0的情况
            int first = (Integer.parseInt((mark + "").substring(0, 1)) + 1);
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
            yAxisMark.cal_mark_max = yAxisMark.cal_mark_max<0?yAxisMark.cal_mark_max/redundance:yAxisMark.cal_mark_max*redundance;
            yAxisMark.cal_mark_min = yAxisMark.cal_mark_min<0?yAxisMark.cal_mark_min*redundance:yAxisMark.cal_mark_min/redundance;
            yAxisMark.cal_mark = (yAxisMark.cal_mark_max-yAxisMark.cal_mark_min)/(yAxisMark.lableNum - 1);
        }
        //小数点位
        if(yAxisMark.digits==0) {
            float mark = (yAxisMark.cal_mark_max - yAxisMark.cal_mark_min)/(yAxisMark.lableNum - 1);
            if(mark<1){
                String pattern = "[1-9]";
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(mark+""); // 获取 matcher 对象
                m.find();
                int index = m.start();
                yAxisMark.digits = index-1;
                LogUtil.w(TAG, mark+"第一个大于0的数字位置："+index+"   保留小数位数："+yAxisMark.digits);
            }
        }
        LogUtil.w(TAG, "最终最小最大值："+yAxisMark.cal_mark_min+"  "+yAxisMark.cal_mark_max + "   " +yAxisMark.cal_mark);
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
        LogUtil.v(TAG, "--------------------------开始绘制");
        try {

        if(lineData==null || lineData.size()==0)
            return;
        long startTime =System.currentTimeMillis();
        float yMarkSpace = (chartRect.bottom - chartRect.top) / (yAxisMark.lableNum - 1);
        paintEffect.setStyle(Paint.Style.STROKE);
        paintEffect.setStrokeWidth(yAxisMark.lineWidth);
        paintEffect.setColor(yAxisMark.lineColor);
        paintText.setTextSize(yAxisMark.textSize);
        paintText.setColor(yAxisMark.textColor);
//        canvas.drawLine(chartRect.left, chartRect.top, chartRect.left, chartRect.bottom, paint);
        PathEffect effects = new DashPathEffect(new float[]{15, 6, 15, 6}, 0);
        paintEffect.setPathEffect(effects);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(yAxisMark.lineColor);
        for (int i = 0; i < yAxisMark.lableNum; i++) {
            /**绘制横向线*/
            paint.setStrokeWidth(i==0?yAxisMark.lineWidth*2.5f:yAxisMark.lineWidth);
            canvas.drawLine(chartRect.left, chartRect.bottom - yMarkSpace * i,
                    chartRect.right, chartRect.bottom - yMarkSpace * i, paint);
            /**绘制y刻度*/
            String text = yAxisMark.getMarkText(yAxisMark.cal_mark_min + i * yAxisMark.cal_mark);
            canvas.drawText(text,
                    chartRect.left - yAxisMark.textSpace - FontUtil.getFontlength(paintText, text),
                    chartRect.bottom - yMarkSpace * i - yAxisMark.textHeight / 2 + yAxisMark.textLead, paintText);
        }
        //绘制Y轴单位
        if(!TextUtils.isEmpty(yAxisMark.unit)){
            canvas.drawText(yAxisMark.unit,
                    chartRect.left - yAxisMark.textSpace - FontUtil.getFontlength(paintText, yAxisMark.unit),
                    chartRect.top - yAxisMark.textSpace - yAxisMark.textHeight*3/2 + yAxisMark.textLead, paintText);
        }

        /**绘制x轴刻度*/
//        if(xAxisMark.lables!=null){
//            //绘制固定的
//            drawFixedXLable(canvas);
//            lables = xAxisMark.lables;
//        }else{
//            drawXLable(canvas);
//        }

        /**绘制折线*/
        if(maxPointNum<=0)
            return;
        paintText.setTextSize(xAxisMark.textSize);
        paintText.setColor(xAxisMark.textColor);
        paint.setStrokeWidth(lineWidth);
        float radius = DensityUtil.dip2px(getContext(), 3);

        Path path = new Path();
        PointF lastPoint = new PointF();
        PointF currentPoint = new PointF();
        int startIndex = (int)(-scrollx/pointWidth);
        int endIndex = (int)((-scrollx+chartRect.width())/pointWidth+1);
        startIndex = Math.max(startIndex, 0);
        endIndex = Math.min(endIndex, maxPointNum-1);
//        Log.w(TAG, "绘制索引："+startIndex+" 至  "+endIndex+"   scrollx="+scrollx);
        RectF clipRect = new RectF(chartRect.left-radius-lineWidth/2, chartRect.top, chartRect.right+radius+lineWidth/2,
                chartRect.bottom + xAxisMark.textSpace + xAxisMark.textHeight);
//        int restorePath = canvas.save();
//        canvas.clipRect(clipRect);
        //saveLayer的时候都会新建一个透明的图层（离屏Bitmap-离屏缓冲），并且会将saveLayer之前的一些Canvas操作延续过来
        //当我们调用restore 或者 restoreToCount 时 更新到对应的图层和画布上
            //save、 restore方法来保存和还原变换操作Matrix以及Clip剪裁
        canvas.saveLayer(clipRect.left, clipRect.top, clipRect.right, clipRect.bottom, paint, Canvas.ALL_SAVE_FLAG);
        boolean drawXLable = false;
        for (int i = 0; i < lineData.size(); i++) {
            List<LinePoint> linePoints = lineData.get(i);
            if(linePoints.size()<=0)
                break;
            path.reset();
            for(int j = startIndex; j<=endIndex; j++){
                if(j>startIndex+(endIndex - startIndex)*chartAnimValue)
                    break;
                //每条线的点数量可能不一样
                if (j >= linePoints.size()/* || linePoints.get(j).getValuey() == null*/)
                    continue;
                currentPoint.x = scrollx + chartRect.left + j * pointWidth;
                currentPoint.y = chartRect.bottom - (chartRect.bottom - chartRect.top) /
                        (yAxisMark.cal_mark_max - yAxisMark.cal_mark_min) * (linePoints.get(j).getValuey() - yAxisMark.cal_mark_min);

                if(lineType == LineType.BROKEN){
                    if (path.isEmpty()) {
                        path.moveTo(currentPoint.x, currentPoint.y);
                    } else {
                        path.lineTo(currentPoint.x, currentPoint.y);
                    }
                }else{
                    if(j == startIndex){
                        path.moveTo(currentPoint.x, currentPoint.y);
                    }else if(j == startIndex+1){   //二阶
                        float x = lastPoint.x + (currentPoint.x - lastPoint.x)/2;
                        float y = currentPoint.y;
                        path.quadTo(x,y,currentPoint.x,currentPoint.y);
                    }else if(j<=endIndex-1){  //三阶
                        float x1 = lastPoint.x + (currentPoint.x - lastPoint.x)/2;
                        float y1 = lastPoint.y;
                        float x2 = x1;
                        float y2 = currentPoint.y;
                        path.cubicTo(x1, y1, x2, y2, currentPoint.x,currentPoint.y);
                    }else if(j == endIndex){   //最后一个 二阶
                        float x = lastPoint.x + (currentPoint.x - lastPoint.x)/2;
                        float y = lastPoint.y;
                        path.quadTo(x,y,currentPoint.x,currentPoint.y);
                    }
                    lastPoint.x = currentPoint.x;
                    lastPoint.y = currentPoint.y;
                }
                drawXLable = false;
                if(i == maxPointIndex){
                    if(xlables.size()>0 && xlables.contains(linePoints.get(j).getValuex())){
                        drawXLable = true;
                    }else{
                        if(showBegin){
                            drawXLable = j%xindexSpace==0;
                        }else{
                            drawXLable = (j-(maxPointNum-1))%xindexSpace==0;
                        }
                    }
                    if(drawXLable){
//                        FLog.v(xlables.size()+"绘制x轴刻度"+linePoints.get(j).getValuex());
                        float x;
                        if (j == 0) {
                            x = currentPoint.x;
                        } else if (j == maxPointNum - 1) {
                            x = currentPoint.x - FontUtil.getFontlength(paintText, linePoints.get(j).getValuex());
                        } else {
                            x = currentPoint.x - FontUtil.getFontlength(paintText, linePoints.get(j).getValuex()) / 2;
                        }
                        canvas.drawText(linePoints.get(j).getValuex(), x,
                                chartRect.bottom + xAxisMark.textSpace + xAxisMark.textLead, paintText);
                    }
                }

            }
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(lineWidth);
            paint.setColor(lineColor[i]);
            /**
             * Xfermode 有三个实现类: AvoidXfermode,PixelXorXfermode,PorterDuffXfermode
             *
             * 1. AvoidXfermode：对原来的像素进行处理，AvoidXfermode不支持硬件加速，使用它需要关闭硬件加速。其次，最好在新建图层上绘制. 构造方法参数分别代表:
             * opColor被处理的像素颜色
             * 容差值（原像素在一定范围内与传入的像素相似则处理）
             * 模式: TARGET模式判断画布上是否有与opColor相似（容差）的颜色，如果有，则把该区域“染”上一层我们”画笔的颜色“，
             *      AVOID与TARGET相反，将画布上与传入opColor不相似的染上画笔颜色
             * 比如下面的代码中首先绘制一个图片，然后对图片上的白色像素进行处理，染色为画笔的红色
             * canvas.drawBitmap(mBmp,null,new Rect(0,0,width,height),mPaint);
             * mPaint.setXfermode(new AvoidXfermode(Color.WHITE,100, AvoidXfermode.Mode.TARGET));
             * mPaint.setColor(Color.RED)
             * canvas.drawRect(0,0,width,height,mPaint);
             *
             * 2. PixelXofXermode 没设么用，不支持硬件加速
             *
             * 3. PorterDuffXfermode是最常用的，它用于描述2D图像图像合成的模式，一共有12中模式描述数字图像合成的基本手法，包括
             * Clear、Source Only、Destination Only、Source Over、Source In、Source
             * Out、Source Atop、Destination Over、Destination In、Destination
             * Out、Destination Atop、XOR。通过组合使用 Porter-Duff 操作，可完成任意 2D
             * 图像的合成。在绘图时会先检查该画笔Paint对象有没有设置Xfermode，如果没有设置Xfermode，那么直接将绘制的图形覆盖Canvas对应位置原有的像素；
             * 如果设置了Xfermode，那么会按照Xfermode具体的规则来更新Canvas中对应位置的像素颜色。
             *
             * 使用时通常结合canvas.saveLayer(clipRect.left, clipRect.top, clipRect.right, clipRect.bottom, paint, Canvas.ALL_SAVE_FLAG)创建一个全透明的layer层，否则会产生不可预期的结果
             *
             * 使用它时要搞清楚两个概念，DST表示在画笔设置它之前画布上已经绘制的内容，SRC表示设置之后绘制的内容，PorterDuffXfermode就是将两个部分的像素按照一定的模式进行合并
             */
            //这里设置DST_OVER，目的是将绘制path之前已经绘制的线上的点显示的线之上，要不然线会遮住小圆点
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
            canvas.drawPath(path, paint);
            paint.setXfermode(null);
        }
//        canvas.restore();
//        Log.w(TAG, "绘制一次需要："+(System.currentTimeMillis() - startTime)+ " ms");

        drawFocus(canvas);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**绘制焦点*/
    private void drawFocus(Canvas canvas){
        try {
        if(null==focusData || maxPointNum == 0)
            return;
        //绘制竖直虚线
        PathEffect effects = new DashPathEffect(new float[]{8,5,8,5},0);
        paintEffect.setStyle(Paint.Style.STROKE);
        paintEffect.setStrokeWidth(focusLineSize);
        paintEffect.setColor(focusLineColor);
        paintEffect.setPathEffect(effects);
        Path path = new Path();
        path.moveTo(focusData.getPoint().x, chartRect.bottom);
        path.lineTo(focusData.getPoint().x, chartRect.top);
        canvas.drawPath(path , paintEffect);

        //面板
        boolean showLeft = focusData.getPoint().x-chartRect.left > (chartRect.right - chartRect.left)/2;
        RectF rect = new RectF(
                showLeft?focusData.getPoint().x - foucsRectWidth - 30:focusData.getPoint().x + 30,
                chartRect.top /*+ (chartRect.bottom - chartRect.top)/2 - foucsRectHeight/2*/,
                showLeft? focusData.getPoint().x - 30 : focusData.getPoint().x + foucsRectWidth + 30,
                chartRect.top  + foucsRectHeight/*+ (chartRect.bottom - chartRect.top)/2 + foucsRectHeight/2*/
        );
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setAlpha(230);
        canvas.drawRect(rect , paint);
        //面板中的文字
        //2020-10-16 06：00
        //零序电流:15.2KW
        //A相电流:15.2KW
        //A相电流:15.2KW
        //A相电流:15.2KW
        String text = "";
        float top = rect.top+foucsRectSpace;
        PointF currentPoint = new PointF();
        float radius = DensityUtil.dip2px(getContext(), 2.5f);
        for(int i = 0; i< focusPanelText.length; i++){
            if(focusPanelText[i].show){
                paintText.setTextSize(focusPanelText[i].textSize);
                paintText.setColor(focusPanelText[i].textColor);
                if(i == 0){
                    //x轴数据
                    for(LinePoint point : focusData.getData()){
                        if(point!=null){
                            text = TextUtils.isEmpty(point.getValuexfocus())?point.getValuex():point.getValuexfocus();
                            break;
                        }
                    }
                }else{
                    top += foucsRectTextSpace;
                 /*   text = focusPanelText[i].text+
                            (focusData.getData().get(i-1)==null?"":getFocusYText(focusData.getData().get(i-1).getValuey()))
                            + yAxisMark.unit;*/
                    text = focusPanelText[i].text+
                            (focusData.getData().get(i-1)==null?"":
                                    YAxisMark.formattedDecimal(focusData.getData().get(i-1).getValuey(),2))
                            + yAxisMark.unit;

                    //绘制焦点圆圈
                    if(focusData.getData().get(i-1)!=null) {
                        currentPoint.x = focusData.getPoint().x;
                        currentPoint.y = chartRect.bottom - (chartRect.bottom - chartRect.top) /
                                (yAxisMark.cal_mark_max - yAxisMark.cal_mark_min) * (focusData.getData().get(i-1).getValuey() - yAxisMark.cal_mark_min);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setColor(lineColor[i - 1]);
                        canvas.drawCircle(currentPoint.x, currentPoint.y, radius, paint);
                        paint.setStyle(Paint.Style.FILL);
                        paint.setColor(Color.WHITE);
                        canvas.drawCircle(currentPoint.x, currentPoint.y, radius - lineWidth / 2, paint);
                    }
                }
                canvas.drawText(text,
                        rect.left+foucsRectSpace,
                        top + FontUtil.getFontLeading(paintText), paintText);
                top += FontUtil.getFontHeight(paintText);
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**获取焦点面板上数值展示字符串*/
    public String getFocusYText(float value){
        switch (yAxisMark.markType){
            case Integer:
                return (long)value+"";
            case Float:
                return NumberFormatUtil.formattedDecimal(value);
            case Percent:
                return NumberFormatUtil.formattedDecimalToPercentage(value, 2);
        }
        return value+"";
    }

    /**绘制 XAxisMark.lables 设置的固定x刻度，*/
    private void drawFixedXLable(Canvas canvas){
        float oneWidth = (-scrollXMax+chartRect.width())/(xAxisMark.lables.length-1);
        Log.w(TAG, "最大滚动："+scrollXMax+ "  图表宽度"+chartRect.width()+"  lable数量"+xAxisMark.lables.length+"   单个跨度："+oneWidth);
        paintText.setTextSize(xAxisMark.textSize);
        paintText.setColor(xAxisMark.textColor);
        float x ;
        int restoreCount = canvas.save();
        canvas.clipRect(new RectF(chartRect.left, chartRect.bottom, chartRect.right, chartRect.bottom+ xAxisMark.textSpace+ xAxisMark.textHeight));
        for(int i = 0; i< xAxisMark.lables.length; i++){
            String text = xAxisMark.lables[i];
            if(i==0){
                x = scrollx + chartRect.left + i * oneWidth;
            }else if(i == xAxisMark.lables.length-1){
                x = scrollx + chartRect.left + i * oneWidth - FontUtil.getFontlength(paintText, text);
            }else {
                x = scrollx + chartRect.left + i * oneWidth - FontUtil.getFontlength(paintText, text) / 2;
            }
            canvas.drawText(text, x,
                    chartRect.bottom + xAxisMark.textSpace + xAxisMark.textLead, paintText);
        }
        canvas.restoreToCount(restoreCount);
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
                    if(!scaleing && Math.abs(event.getY()-mDownY) > Math.abs(event.getX() - mDownX)*1.5) {
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
        return super.onTouchEvent(event);
    }

    PointF focusPoint = new PointF();
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(scaleAble) {
            scaleGestureDetector.onTouchEvent(event);
            mGestureDetector.onTouchEvent(event);
        }else if(scrollAble) {
            mGestureDetector.onTouchEvent(event);
        }
        if(focusPanelText!=null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    focusPoint.x = event.getX();
                    focusPoint.y = event.getY();
                    onFocusTouch(focusPoint);
                    break;
                case MotionEvent.ACTION_MOVE:
                    focusPoint.x = event.getX();
                    focusPoint.y = event.getY();
                    onFocusTouch(focusPoint);
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    onFocusTouch(null);
                    break;
            }
        }
        return true;
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
            if(!scaleing) {
                scrollx -= distanceX;    //distanceX左正右负
                scrollx = Math.min(scrollx, 0);
                scrollx = Math.max(scrollXMax, scrollx);
                Log.d(TAG, "=================滚动：" + distanceX + "   " + scrollx);
                postInvalidate();
            }
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
        if(scrollAble) {
            if (mScroller.isFinished())
                return;
            if (mScroller.computeScrollOffset()) {
                LogUtil.d(TAG, "滚动后计算：" + mScroller.getCurrX());
                scrollx = mScroller.getCurrX();
                postInvalidate();
            }
        }
    }

    boolean scaleing = false;
    class MyOnScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        private float focusIndex;
        private float beginScrollx;
        private float beginPointWidth;
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            pointWidth *= detector.getScaleFactor();
            //缩放范围约束
//            pointWidthMin = chartRect.width() / (maxPointNum-1);   //缩小到全部显示
//            pointWidth = chartRect.width() / (pageShowNum-1);
//            pointWidthMax = chartRect.width() / 4;   //最大只能放大到每个标签显示5个点
            pointWidth = Math.min(pointWidth, pointWidthMax);
            pointWidth = Math.max(pointWidth, pointWidthMin);
            //重新计算最大偏移量
            scrollXMax = -(pointWidth*(maxPointNum-1) - chartRect.width());      //最大滚动距离，是一个负值
            //计算当前偏移量
            Log.i(TAG, "=============================当前偏移："+scrollx+"    两点宽度 = " +pointWidth);
            //为了保证焦点对应的点位置不变，是使用公式： beginScrollx + chartRect.left + focusIndex*beginPointWidth = scrollx + chartRect.left + focusIndex*pointWidth
            scrollx = beginScrollx + focusIndex*(beginPointWidth - pointWidth);
            scrollx = Math.min(scrollx, 0);
            scrollx = Math.max(scrollXMax, scrollx);
            caculateXMark();
//            Log.i(TAG, "缩放后偏移："+scrollx);
            postInvalidate();
            return true;
        }
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            focusIndex = (int)((-scrollx + (detector.getFocusX()-chartRect.left))/pointWidth);
            beginScrollx = scrollx;
            beginPointWidth = pointWidth;
            Log.i(TAG, "缩放开始了，焦点索引为"+ focusIndex);   // 缩放因子
            scaleing = true;
            return true;
        }
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            scaleing = false;
        }
    }

    /**************************4. 事件👆******************************/


    /*****************************焦点*******************************/

    private FocusData focusData;
    private int focusIndex;
    protected void onFocusTouch(PointF point) {
        try {
            focusData = null;
            if (point != null && lineData != null && lineData.size() > 0) {
                //避免滑出
                point.x = Math.max(point.x, chartRect.left);
                point.x = Math.min(point.x, chartRect.right);
                //获取焦点对应的数据的索引
                focusIndex = (int) ((-scrollx + (point.x - chartRect.left)) / pointWidth);
                if ((-scrollx + (point.x - chartRect.left)) - focusIndex * pointWidth > pointWidth / 2) {
                    LogUtil.e(TAG, "========焦点在下一个点范围了：" + focusIndex);
                    focusIndex += 1;
                }
                LogUtil.e(TAG, "========焦点索引：" + focusIndex);
                focusIndex = Math.max(0, Math.min(focusIndex, maxPointNum - 1));
                point.x = chartRect.left + (focusIndex * pointWidth + scrollx);
                focusData = new FocusData();
                focusData.setPoint(point);
                List<LinePoint> data = new ArrayList<>();
                focusData.setData(data);
                for (List<LinePoint> line : lineData) {
                    if (focusIndex < line.size())
                        data.add(line.get(focusIndex));
                    else
                        data.add(null);
                }
            }
            postInvalidate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**焦点数据*/
    public static class FocusData {
        private List<LinePoint> data;
        private PointF point;

        public List<LinePoint> getData() {
            return data;
        }

        public void setData(List<LinePoint> data) {
            this.data = data;
        }

        public PointF getPoint() {
            return point;
        }

        public void setPoint(PointF point) {
            this.point = point;
        }
    }

}
