package com.openxu.hkchart.line;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Scroller;

import com.openxu.cview.R;
import com.openxu.cview.xmstock.bean.DataPoint;
import com.openxu.cview.xmstock20201030.SyzsLinesChart;
import com.openxu.cview.xmstock20201030.build.AxisMark;
import com.openxu.cview.xmstock20201030.build.Line;
import com.openxu.hkchart.BaseChart;
import com.openxu.hkchart.bar.Bar;
import com.openxu.hkchart.element.FocusPanelText;
import com.openxu.hkchart.element.XAxisMark;
import com.openxu.hkchart.element.YAxisMark;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;
import com.openxu.utils.NumberFormatUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * autour : openXu
 * date : 2017/7/24 10:46
 * className : LineChart
 * version : 1.0
 * description : 曲线、折线图
 *
 */
public class LineChart extends BaseChart implements View.OnTouchListener {

    /**设置*/
    private List<List<LinePoint>> lineData;
    private YAxisMark yAxisMark;
    private XAxisMark xAxisMark;
    private boolean scaleAble = true;  //是否支持放大
    private boolean scrollAble = true;  //是否支持滚动
    private boolean showBegin = true;    //当数据超出一屏宽度时，实现最后的数据
    private float lineWidth = DensityUtil.dip2px(getContext(), 1.5f);
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
    private int pageShowNum;       //第一次页面总数据量
    private int maxPointNum;
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
    }

    /***********************************1. setting👇**********************************/
    public void setYAxisMark(YAxisMark yAxisMark) {
        this.yAxisMark = yAxisMark;
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
        Log.w(TAG, "设置数据，总共"+lineData.size()+"条线，每条线"+lineData.get(0).size()+"个点");
        this.lineData = lineData;
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
        rectChart.top = rectChart.top + yAxisMark.textHeight/2 +
                (TextUtils.isEmpty(yAxisMark.unit)?0:(yAxisMark.textHeight+yAxisMark.textSpace));

        for(List<LinePoint> list :lineData)
            maxPointNum = Math.max(maxPointNum, list.size());
        //没有设置展示数据量，则默认为全部展示
        if(pageShowNum<=0){
            pageShowNum = maxPointNum;
        }
        Log.w(TAG, "计算pageShowNum="+pageShowNum);
        pointWidthMin = rectChart.width() / (pageShowNum-1);
        pointWidth = pointWidthMin;
        pointWidthMax = rectChart.width() / (xAxisMark.lableNum-1) / 5;   //最大只能放大到每个标签显示5个点
        //数据没有展示完，说明可以滚动
        if(pageShowNum<maxPointNum)
            scrollXMax = -(pointWidth*(maxPointNum-1) - rectChart.width());      //最大滚动距离，是一个负值
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
            for(int i = 0; i< focusPanelText.length; i++){
                if(focusPanelText[i].show){
                    paintText.setTextSize(focusPanelText[i].textSize);
                    if(i == 0){
                        //x轴数据
//                        foucsRectWidth = Math.max(foucsRectWidth, FontUtil.getFontlength(paintText, lineData.get(0).get(0).getValuex()));
                        foucsRectHeight += FontUtil.getFontHeight(paintText);
                    }else{
//                        text = focusPanelText[i].text+maxLable+ yAxisMark.unit;
                        text = focusPanelText[i].text+maxLable+ yAxisMark.unit;
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
                        rectChart.left + j * pointWidth,
                        rectChart.bottom - (rectChart.bottom - rectChart.top) /
                                (yAxisMark.cal_mark_max - yAxisMark.cal_mark_min) * (linePoints.get(j).getValuey() - yAxisMark.cal_mark_min)
                ));
            }
        }*/

        Log.w(TAG, "计算scrollXMax="+scrollXMax+"   scrollx="+scrollx);
    }
    public List<String> xlables = new ArrayList<>();

    private void caculateXMark(){
        xlables.clear();
        if(xAxisMark.lables!=null){
            xlables.addAll(Arrays.asList(xAxisMark.lables));
            return;
        }
//        float markSpace = (-scrollXMax+rectChart.width())/(xAxisMark.lableNum-1);
        float markSpace = rectChart.width()/(xAxisMark.lableNum-1);
        //每隔多少展示一个标签
        int indexSpace = (int)(markSpace/pointWidth);
        indexSpace = Math.max(indexSpace, 1);
//        List<List<LinePoint>> lineData;
        if(showBegin){
            for(int i =0; i< lineData.get(0).size(); i++){
                if(i%indexSpace==0)
                    xlables.add(lineData.get(0).get(i).getValuex());
            }
        }else{
            for(int i =lineData.get(0).size()-1; i>=0 ; i--){
                if((i-(lineData.get(0).size()-1))%indexSpace==0)
                    xlables.add(lineData.get(0).get(i).getValuex());
            }
        }
        Log.w(TAG, "矩形区域需要展示"+xAxisMark.lableNum+"个标签，单个标签间距"+markSpace+"  每隔"+indexSpace+"个数据展示一个:"+xlables.size()+"   "+xlables);
    }

    private void calculateYMark() {
        float redundance = 1.01f;  //y轴最大和最小值冗余
        yAxisMark.cal_mark_max =  Float.MIN_VALUE;    //Y轴刻度最大值
        yAxisMark.cal_mark_min =  Float.MAX_VALUE;    //Y轴刻度最小值
        for(List<LinePoint> linePoints : lineData){
            for(LinePoint point : linePoints){
                yAxisMark.cal_mark_max = Math.max(yAxisMark.cal_mark_max, point.getValuey());
                yAxisMark.cal_mark_min = Math.min(yAxisMark.cal_mark_min, point.getValuey());
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

        long startTime =System.currentTimeMillis();
        float yMarkSpace = (rectChart.bottom - rectChart.top) / (yAxisMark.lableNum - 1);
        paintEffect.setStyle(Paint.Style.STROKE);
        paintEffect.setStrokeWidth(yAxisMark.lineWidth);
        paintEffect.setColor(yAxisMark.lineColor);
        paintText.setTextSize(yAxisMark.textSize);
        paintText.setColor(yAxisMark.textColor);
//        canvas.drawLine(rectChart.left, rectChart.top, rectChart.left, rectChart.bottom, paint);
        PathEffect effects = new DashPathEffect(new float[]{15, 6, 15, 6}, 0);
        paintEffect.setPathEffect(effects);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(yAxisMark.lineWidth);
        paint.setColor(yAxisMark.lineColor);
        for (int i = 0; i < yAxisMark.lableNum; i++) {
            /**绘制横向线*/
            canvas.drawLine(rectChart.left, rectChart.bottom - yMarkSpace * i,
                    rectChart.right, rectChart.bottom - yMarkSpace * i, paint);
            /**绘制y刻度*/
            String text = yAxisMark.getMarkText(yAxisMark.cal_mark_min + i * yAxisMark.cal_mark);
            canvas.drawText(text,
                    rectChart.left - yAxisMark.textSpace - FontUtil.getFontlength(paintText, text),
                    rectChart.bottom - yMarkSpace * i - yAxisMark.textHeight / 2 + yAxisMark.textLead, paintText);
        }
        //绘制Y轴单位
        if(!TextUtils.isEmpty(yAxisMark.unit)){
            canvas.drawText(yAxisMark.unit,
                    rectChart.left - yAxisMark.textSpace - FontUtil.getFontlength(paintText, yAxisMark.unit),
                    rectChart.top - yAxisMark.textSpace - yAxisMark.textHeight + yAxisMark.textLead, paintText);
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
        paintText.setTextSize(xAxisMark.textSize);
        paintText.setColor(xAxisMark.textColor);
        paint.setStrokeWidth(lineWidth);
        float radius = DensityUtil.dip2px(getContext(), 3);

        Path path = new Path();
        PointF lastPoint = new PointF();
        PointF currentPoint = new PointF();
        int startIndex = (int)(-scrollx/pointWidth);
        int endIndex = (int)((-scrollx+rectChart.width())/pointWidth+1);
        endIndex = Math.min(endIndex, maxPointNum-1);
//        Log.w(TAG, "绘制索引："+startIndex+" 至  "+endIndex+"   scrollx="+scrollx);

        RectF clipRect = new RectF(rectChart.left-radius-lineWidth/2, rectChart.top, rectChart.right+radius+lineWidth/2,
                rectChart.bottom + xAxisMark.textSpace + xAxisMark.textLead);
//        int restorePath = canvas.save();
//        canvas.clipRect(clipRect);
        canvas.saveLayer(clipRect.left, clipRect.top, clipRect.right, clipRect.bottom, paint, Canvas.ALL_SAVE_FLAG);
        for (int i = 0; i < lineData.size(); i++) {
            path.reset();
            List<LinePoint> linePoints = lineData.get(i);
            for(int j = startIndex; j<=endIndex; j++){
//            for (int j = 0; j < linePoints.size(); j++) {
                if(j>startIndex+(endIndex - startIndex)*chartAnimValue)
                    break;
                if (linePoints.get(j).getValuey() == null)
                    continue;
                currentPoint.x = scrollx + rectChart.left + j * pointWidth;
                currentPoint.y = rectChart.bottom - (rectChart.bottom - rectChart.top) /
                        (yAxisMark.cal_mark_max - yAxisMark.cal_mark_min) * (linePoints.get(j).getValuey() - yAxisMark.cal_mark_min);
                if (path.isEmpty()) {
                    path.moveTo(currentPoint.x, currentPoint.y);
                } else {
                    path.lineTo(currentPoint.x, currentPoint.y);
                }
               if(xlables.contains(linePoints.get(j).getValuex())){
                    if(i==0) {
//                    Log.v(TAG, "绘制x轴刻度"+linePoints.get(j).getValuex());
                        float x;
                        if (j == 0) {
                            x = currentPoint.x;
                        } else if (j == maxPointNum - 1) {
                            x = currentPoint.x - FontUtil.getFontlength(paintText, linePoints.get(j).getValuex());
                        } else {
                            x = currentPoint.x - FontUtil.getFontlength(paintText, linePoints.get(j).getValuex()) / 2;
                        }
                        canvas.drawText(linePoints.get(j).getValuex(), x,
                                rectChart.bottom + xAxisMark.textSpace + xAxisMark.textLead, paintText);
                    }
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(lineColor[i]);
                    canvas.drawCircle(currentPoint.x, currentPoint.y, radius, paint);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.WHITE);
                    canvas.drawCircle(currentPoint.x, currentPoint.y, radius - lineWidth/2, paint);
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
    }

    /**绘制焦点*/
    private void drawFocus(Canvas canvas){
        if(null==focusData)
            return;
        //绘制竖直虚线
        PathEffect effects = new DashPathEffect(new float[]{8,5,8,5},0);
        paintEffect.setStyle(Paint.Style.STROKE);
        paintEffect.setStrokeWidth(focusLineSize);
        paintEffect.setColor(focusLineColor);
        paintEffect.setPathEffect(effects);
        Path path = new Path();
        path.moveTo(focusData.getPoint().x, rectChart.bottom);
        path.lineTo(focusData.getPoint().x, rectChart.top);
        canvas.drawPath(path , paintEffect);
        //绘制焦点
//        paint.setAntiAlias(true);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(lineSize);
//        paint.setColor(lineColor[0]);
//        canvas.drawCircle(point1.x, point1.y, dotRadius, paint);
//        paint.setColor(lineColor[1]);
//        canvas.drawCircle(point2.x, point2.y, dotRadius, paint);
        //面板
        boolean showLeft = focusData.getPoint().x-rectChart.left > (rectChart.right - rectChart.left)/2;
        RectF rect = new RectF(
                showLeft?focusData.getPoint().x - foucsRectWidth - 30:focusData.getPoint().x + 30,
                rectChart.top /*+ (rectChart.bottom - rectChart.top)/2 - foucsRectHeight/2*/,
                showLeft? focusData.getPoint().x - 30 : focusData.getPoint().x + foucsRectWidth + 30,
                rectChart.top  + foucsRectHeight/*+ (rectChart.bottom - rectChart.top)/2 + foucsRectHeight/2*/
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
        String text;
        float top = rect.top+foucsRectSpace;
        for(int i = 0; i< focusPanelText.length; i++){
            if(focusPanelText[i].show){
                paintText.setTextSize(focusPanelText[i].textSize);
                paintText.setColor(focusPanelText[i].textColor);
                if(i == 0){
                    //x轴数据
                    text = focusData.getData().get(0).getValuex();
                }else{
                    top += foucsRectTextSpace;
                    text = focusPanelText[i].text+focusData.getData().get(i-1).getValuey() + yAxisMark.unit;
                }
                canvas.drawText(text,
                        rect.left+foucsRectSpace,
                        top + FontUtil.getFontLeading(paintText), paintText);
                top += FontUtil.getFontHeight(paintText);
            }
        }
    }


    /**绘制 XAxisMark.lables 设置的固定x刻度，*/
    private void drawFixedXLable(Canvas canvas){
        float oneWidth = (-scrollXMax+rectChart.width())/(xAxisMark.lables.length-1);
        Log.w(TAG, "最大滚动："+scrollXMax+ "  图表宽度"+rectChart.width()+"  lable数量"+xAxisMark.lables.length+"   单个跨度："+oneWidth);
        paintText.setTextSize(xAxisMark.textSize);
        paintText.setColor(xAxisMark.textColor);
        float x ;
        int restoreCount = canvas.save();
        canvas.clipRect(new RectF(rectChart.left, rectChart.bottom, rectChart.right, rectChart.bottom+ xAxisMark.textSpace+ xAxisMark.textHeight));
        for(int i = 0; i< xAxisMark.lables.length; i++){
            String text = xAxisMark.lables[i];
            if(i==0){
                x = scrollx + rectChart.left + i * oneWidth;
            }else if(i == xAxisMark.lables.length-1){
                x = scrollx + rectChart.left + i * oneWidth - FontUtil.getFontlength(paintText, text);
            }else {
                x = scrollx + rectChart.left + i * oneWidth - FontUtil.getFontlength(paintText, text) / 2;
            }
            canvas.drawText(text, x,
                    rectChart.bottom + xAxisMark.textSpace + xAxisMark.textLead, paintText);
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
//                Log.d(TAG, "滚动：" + distanceX + "   " + scrollx);
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
                Log.d(TAG, "滚动后计算：" + mScroller.getCurrX());
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
            pointWidth = Math.min(pointWidth, pointWidthMax);
            pointWidth = Math.max(pointWidth, pointWidthMin);
            //重新计算最大偏移量
            scrollXMax = -(pointWidth*(maxPointNum-1) - rectChart.width());      //最大滚动距离，是一个负值
            //计算当前偏移量
//            Log.i(TAG, "当前偏移："+scrollx+"    缩放中心数据索引 = " +index);
            //为了保证焦点对应的点位置不变，是使用公式： beginScrollx + rectChart.left + focusIndex*beginPointWidth = scrollx + rectChart.left + focusIndex*pointWidth
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
            focusIndex = (int)((-scrollx + (detector.getFocusX()-rectChart.left))/pointWidth);
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
    protected void onFocusTouch(PointF point) {
        if(null == point){
            focusData = null;
        }else if(null!=lineData && lineData.size()>0){
            //避免滑出
            point.x = Math.max(point.x, rectChart.left);
            point.x = Math.min(point.x, rectChart.right);
            //获取焦点对应的数据的索引
            int focusIndex = (int)((-scrollx + (point.x-rectChart.left))/pointWidth);
//            LogUtil.e(getClass().getSimpleName(), "========焦点索引："+focusIndex);
            focusIndex = Math.max(0, Math.min(focusIndex, maxPointNum - 1));
            point.x = rectChart.left+(focusIndex*pointWidth + scrollx);
            focusData = new FocusData();
            focusData.setPoint(point);
            List<LinePoint> data = new ArrayList<>();
            focusData.setData(data);
            for(List<LinePoint> line : lineData){
                data.add(line.get(focusIndex));
            }
        }
        postInvalidate();
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
