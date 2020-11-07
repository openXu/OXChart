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

import com.openxu.chart.element.YAxisMark;
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
 * className : SyzsLinesChart
 * version : 1.0
 * description : 4. 收益走势图，贝塞尔曲线
 */
public class SyzsLinesChart extends BaseChart {
    //设置数据  每个元素表示：时间、实际累计跌涨浮、历史累计跌涨幅 ["20201016","0.51","0.55"]
    private List<List<String>> dataList;
    private String[] lableXList;
    //计算后的数据
    private List<DataPoint> lableXPointList;
    private List<List<DataPoint>> linePointList;
    //y轴刻度设置
    private YAxisMark yAxisMark;
    /**可以设置的属性*/
    //设置线条颜色
    private int[] lineColor = new int[]{Color.parseColor("#fb4051"),
            Color.parseColor("#ffcecb")};
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
    private float dotRadius = DensityUtil.dip2px(getContext(), 3); //焦点面板小圆点半径
    private float dotRadius1 = DensityUtil.dip2px(getContext(), 5); //最高收益小圆点半径
    private float foucsRectWidth;
    private float foucsRectHeight;

    public enum AnimType{
//        LEFT_TO_RIGHT,   //动画从左往右
//        BOTTOM_TO_TOP,   //动画从下网上上升
        SLOW_DRAW        //动画缓慢绘制
    }

    public SyzsLinesChart(Context context) {
        this(context, null);
    }
    public SyzsLinesChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public SyzsLinesChart(Context context, AttributeSet attrs, int defStyle) {
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

    //20201028 --> 2020-10-28
    private String formatDate(String date){
        String str = date.substring(0, 4) + "-"+date.substring(4, 6)+"-"+date.substring(6);
        Log.v(TAG, date+ "---->"+str);
        return str;
    }
    /**
     * 设置数据
     */
    public void setData(List<List<String>> dataList){
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
        yAxisMark = new YAxisMark.Builder(getContext())
                .lableNum(5)
                .textSize(textSizeX)
                .textColor(textColorX)
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
        //时间、实际累计跌涨浮、历史累计跌涨幅 ["20201016","0.51","0.55"]
        calYLable(yAxisMark);
        /**①、计算字体相关以及图表原点坐标*/
        paintLabel.setTextSize(textSizeX);
        float xlableHeight = FontUtil.getFontHeight(paintLabel);
        float xlableLead = FontUtil.getFontLeading(paintLabel);
        paintLabel.setTextSize(yAxisMark.textSize);
        float ylableHeight = FontUtil.getFontHeight(paintLabel);
        float ylableMaxLength = Math.max(FontUtil.getFontlength(paintLabel, (int)yAxisMark.cal_mark_max+"%"),
                FontUtil.getFontlength(paintLabel, (int)yAxisMark.cal_mark_min+"%"));
        float yFoucsLenght = FontUtil.getFontlength(paintLabel, "实际累计涨跌幅：-10.54%");
        //图表主体矩形
        rectChart = new RectF(getPaddingLeft() + yAxisMark.textSpace + ylableMaxLength,
                getPaddingTop() + ylableMaxLength/2,
                getMeasuredWidth()-getPaddingRight(),
                getMeasuredHeight()-getPaddingBottom() - xlableHeight - textSpaceX);
        //焦点面板
        //2020-10-16
        //实际累计涨跌幅:-10.54%
        //历史累计涨跌幅:-10.54%
        foucsRectWidth = dotRadius*2 + foucsRectTextSpace +yFoucsLenght + foucsRectSpace*2;
        foucsRectHeight = xlableHeight + ylableHeight * 2 + foucsRectTextSpace*2 + foucsRectSpace*2;
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
            //时间、实际累计跌涨浮、历史累计跌涨幅 ["20201016","0.51","0.55"]
            List<String> onePart = dataList.get(i);
            try {
                //第一条线的数据
                float valueY = Float.parseFloat(onePart.get(1));
                PointF point = new PointF(rectChart.left + i * oneSpace,
                        rectChart.bottom - (rectChart.bottom-rectChart.top)/(yAxisMark.cal_mark_max - yAxisMark.cal_mark_min) * (valueY-yAxisMark.cal_mark_min));
                linePointList.get(0).add(new DataPoint(onePart.get(0), valueY, point));
                //第二条线的数据
                valueY = Float.parseFloat(onePart.get(2));
//                Log.w(TAG, i+"计算右侧y值"+valueY);
                point = new PointF(rectChart.left + i * oneSpace,
                        rectChart.bottom - (rectChart.bottom-rectChart.top)/(yAxisMark.cal_mark_max - yAxisMark.cal_mark_min) * (valueY-yAxisMark.cal_mark_min));
                linePointList.get(1).add(new DataPoint(onePart.get(0), valueY, point));
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
    private float yMark0;  //y轴0对应的y坐标
    private void drawGrid(Canvas canvas){
        float yMarkSpace = (rectChart.bottom - rectChart.top)/(yAxisMark.lableNum-1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        //竖直黑色实线
        paint.setColor(Color.parseColor("#3d3d3d"));
        canvas.drawLine(rectChart.left, rectChart.top, rectChart.left, rectChart.bottom, paint);
        paintEffect.setStyle(Paint.Style.STROKE);
        paintEffect.setStrokeWidth(lineWidth);
        paintEffect.setColor(defColor);
//        canvas.drawLine(rectChart.left, rectChart.top, rectChart.left, rectChart.bottom, paint);
//        canvas.drawLine(rectChart.right, rectChart.top, rectChart.right, rectChart.bottom, paint);
        PathEffect effects = new DashPathEffect(new float[]{15,6,15,6},0);
        Path path = new Path();
        for (int i = 0; i < yAxisMark.lableNum; i++) {
            if(yAxisMark.cal_mark_min+i*yAxisMark.cal_mark == 0){
                //实线
                yMark0 = rectChart.bottom-yMarkSpace*i;
                canvas.drawLine(rectChart.left, yMark0, rectChart.right, yMark0, paint);
            }else{
                path.reset();
                path.moveTo(rectChart.left, rectChart.bottom-yMarkSpace*i);
                path.lineTo(rectChart.right,rectChart.bottom-yMarkSpace*i);
                paintEffect.setPathEffect(effects);
                canvas.drawPath(path, paintEffect);
            }
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
        float yMarkSpace = (rectChart.bottom - rectChart.top)/(yAxisMark.lableNum-1);
        paintLabel.setTextSize(yAxisMark.textSize);
        paintLabel.setColor(yAxisMark.textColor);
        float lableHeight = FontUtil.getFontHeight(paintLabel);
        float lableLead = FontUtil.getFontLeading(paintLabel);
        for (int i = 0; i < yAxisMark.lableNum; i++) {
            String text = (int) (yAxisMark.cal_mark_min + i * yAxisMark.cal_mark) + "%";
            canvas.drawText(text,
                    rectChart.left - yAxisMark.textSpace - FontUtil.getFontlength(paintLabel, text),
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
        PointF pointMax = null;   //最高收益点
        for(int j = 0; j < linePointList.size(); j++){
            List<DataPoint> lineList = linePointList.get(j);
            if(lineList==null || lineList.size()<=0)   //有可能只有一条???
                continue;
            if(j == 0) {
                Log.w(TAG, "当前绘制的是第1条实际跌涨幅，需要标注最高收益");
                pointMax = new PointF(0, Integer.MAX_VALUE);
            }
            //一条一条的绘制
            Path path = new Path();
            PointF lastPoint = null;
            float minYValue = 0;
            for(int i = 0 ; i<lineList.size()-1; i++){
                if(i>lineList.size()*animPro)   //动画
                    break;
                lastPoint = lineList.get(i+1).getPoint();
                minYValue = Math.min(lastPoint.y,minYValue);
                if(j == 0  && lastPoint.y<pointMax.y){
                    pointMax.x = lastPoint.x;
                    pointMax.y = lastPoint.y;
                }
                if(i == 0){   //二阶
                    path.moveTo(lineList.get(i).getPoint().x, lineList.get(i).getPoint().y);
                    float x = lineList.get(i).getPoint().x + (lineList.get(i+1).getPoint().x - lineList.get(i).getPoint().x)/2;
                    float y = lineList.get(i+1).getPoint().y;
                    path.quadTo(x,y,lastPoint.x,lastPoint.y);
                }else if(i<lineList.size()-2){  //三阶
                    float x1 = lineList.get(i).getPoint().x + (lineList.get(i+1).getPoint().x - lineList.get(i).getPoint().x)/2;
                    float y1 = lineList.get(i).getPoint().y;
                    float x2 = lineList.get(i).getPoint().x + (lineList.get(i+1).getPoint().x - lineList.get(i).getPoint().x)/2;
                    float y2 = lineList.get(i+1).getPoint().y;
                    path.cubicTo(x1, y1, x2, y2, lastPoint.x,lastPoint.y);
                }else if(i == lineList.size()-2){   //最后一个 二阶
                    float x = lineList.get(i).getPoint().x + (lineList.get(i+1).getPoint().x - lineList.get(i).getPoint().x)/2;
                    float y = lineList.get(i).getPoint().y;
                    path.quadTo(x,y,lastPoint.x,lastPoint.y);
                }
            }
            paint.setColor(lineColor[j]);
            canvas.drawPath(path, paint);
            //绘制阴影
            path.lineTo(lastPoint.x, yMark0);
            path.close();
            //线性渐变：前两个参数是渐变开始的点坐标，第三四个参数是渐变结束的点的坐标；渐变的颜色，渐变颜色的分布，模式
            Shader mShader = new LinearGradient(rectChart.left, rectChart.top ,rectChart.left, rectChart.bottom,
                    new int[] {lineColor[j],Color.TRANSPARENT},null,Shader.TileMode.CLAMP);
            paintEffect.setStyle(Paint.Style.FILL);
            paintEffect.setShader(mShader);
            canvas.drawPath(path, paintEffect);
            paintEffect.setShader(null);
        }
        Log.w(TAG, "绘制最高收益："+pointMax);
        //绘制最高收益
        if(pointMax!=null){
            paintLabel.setTextSize(yAxisMark.textSize);
            paintLabel.setColor(lineColor[0]);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(lineColor[0]);
            canvas.drawCircle(pointMax.x , pointMax.y, dotRadius1, paint);
            float textlenght = FontUtil.getFontlength(paintLabel, "最高收益");
            float x = pointMax.x - textlenght;
            x = Math.max(x, rectChart.left);
            x = Math.min(x, rectChart.right - textlenght);
            canvas.drawText("最高收益", x,
                    pointMax.y - dotRadius1 - foucsRectTextSpace - FontUtil.getFontHeight(paintLabel) + FontUtil.getFontLeading(paintLabel), paintLabel);
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
        //绘制焦点
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineSize);
        paint.setColor(lineColor[0]);
        canvas.drawCircle(point1.x, point1.y, dotRadius, paint);
        paint.setColor(lineColor[1]);
        canvas.drawCircle(point2.x, point2.y, dotRadius, paint);
        //面板
        boolean showLeft = point1.x-rectChart.left > (rectChart.right - rectChart.left)/2;
        RectF rect = new RectF(
            showLeft?point1.x - foucsRectWidth - 30:point1.x + 30,
                rectChart.top + (rectChart.bottom - rectChart.top)/2 - foucsRectHeight/2,
                showLeft? point1.x - 30 : point1.x + foucsRectWidth + 30,
                rectChart.top + (rectChart.bottom - rectChart.top)/2 + foucsRectHeight/2
        );
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setAlpha(230);
        canvas.drawRect(rect , paint);
        //面板中的文字
        //2020-10-16
        //实际累计涨跌幅:+10.54%
        //历史累计涨跌幅:-10.54%
        paintLabel.setTextSize(textSizeX);
        paintLabel.setColor(textColorX);
        float lableHeight = FontUtil.getFontHeight(paintLabel);
        float lableLead = FontUtil.getFontLeading(paintLabel);
        float top = rect.top + foucsRectSpace;
        float left = rect.left + foucsRectSpace;
        canvas.drawText(formatDate(focusData.getPoints().get(0).getValueX()), left, top + lableLead, paintLabel);
        top += (lableHeight+foucsRectTextSpace);
        paintLabel.setTextSize(yAxisMark.textSize);
        paintLabel.setColor(lineColor[0]);
        paint.setColor(lineColor[0]);
        lableHeight = FontUtil.getFontHeight(paintLabel);
        lableLead = FontUtil.getFontLeading(paintLabel);
        canvas.drawCircle(left + dotRadius, top + lableHeight/2, dotRadius, paint);
        String dzf = NumberFormatUtil.formattedDecimal(focusData.getPoints().get(0).getValueY());
        canvas.drawText("实际累计涨跌幅："+(focusData.getPoints().get(0).getValueY()>0?"+":"")+dzf+"%",
                left+dotRadius*2+foucsRectTextSpace, top + lableLead, paintLabel);
        top += (lableHeight+foucsRectTextSpace);
        paintLabel.setTextSize(yAxisMark.textSize);
        paintLabel.setColor(lineColor[1]);
        paint.setColor(lineColor[1]);
        lableLead = FontUtil.getFontLeading(paintLabel);
        canvas.drawCircle(left + dotRadius, top + lableHeight/2, dotRadius, paint);
        dzf = NumberFormatUtil.formattedDecimal(focusData.getPoints().get(1).getValueY());
        canvas.drawText("历史累计涨跌幅："+(focusData.getPoints().get(1).getValueY()>0?"+":"")+dzf+"%",
                left+dotRadius*2+foucsRectTextSpace, top + lableLead, paintLabel);
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
        private List<String> data;

        public List<DataPoint> getPoints() {
            return points;
        }

        public void setPoints(List<DataPoint> points) {
            this.points = points;
        }

        public List<String> getData() {
            return data;
        }
        public void setData(List<String> data) {
            this.data = data;
        }
    }

    /**
     * 根据传入的Y轴信息，从数据中获取最小最大值，并获取到Y刻度点值
     */
    private void calYLable(YAxisMark axisMark){
        //时间、实际累计跌涨浮、历史累计跌涨幅 ["20201016","0.51","0.55"]
        axisMark.cal_mark_max =  Float.MIN_VALUE;    //Y轴刻度最大值
        axisMark.cal_mark_min =  Float.MAX_VALUE;    //Y轴刻度最小值
        for(List<String> data : dataList){
            try {
                axisMark.cal_mark_max = Math.max(axisMark.cal_mark_max, Float.parseFloat(data.get(1)));
                axisMark.cal_mark_max = Math.max(axisMark.cal_mark_max, Float.parseFloat(data.get(2)));
                axisMark.cal_mark_min = Math.min(axisMark.cal_mark_min, Float.parseFloat(data.get(1)));
                axisMark.cal_mark_min = Math.min(axisMark.cal_mark_min, Float.parseFloat(data.get(2)));
            }catch (Exception e){
            }
        }
        LogUtil.i(TAG, "Y轴真实axisMark.cal_mark_min="+axisMark.cal_mark_min+"   axisMark.cal_mark_max="+axisMark.cal_mark_max);
        //保证有一个0刻度
        if(axisMark.cal_mark_max > 0 && axisMark.cal_mark_min>0){
            axisMark.cal_mark_min = 0;
            axisMark.cal_mark = (int)(axisMark.cal_mark_max/axisMark.lableNum);
            if(axisMark.cal_mark_max%axisMark.lableNum!=0)
                axisMark.lableNum+=1;
        }
        if(axisMark.cal_mark_max < 0 && axisMark.cal_mark_min<0){
            axisMark.cal_mark_max = 0;
            axisMark.cal_mark = (int)(-axisMark.cal_mark_min/axisMark.lableNum);
            if(axisMark.cal_mark_min%axisMark.lableNum!=0)
                axisMark.lableNum+=1;
        }
        if(axisMark.cal_mark_max > 0 && axisMark.cal_mark_min<0){
            axisMark.cal_mark = (int)((axisMark.cal_mark_max-axisMark.cal_mark_min)/axisMark.lableNum);
            axisMark.lableNum = 1;   //从0刻度开始
            int a = 0;
            while(true){
                a += axisMark.cal_mark;
                axisMark.lableNum ++;
                if(a>=axisMark.cal_mark_max){
                    axisMark.cal_mark_max = a;
                    break;
                }
            }
            a = 0;
            while(true){
                a -= axisMark.cal_mark;
                axisMark.lableNum ++;
                if(a<=axisMark.cal_mark_min){
                    axisMark.cal_mark_min = a;
                    break;
                }
            }
        }
        LogUtil.i(TAG, "Y轴axisMark.cal_mark_min="+axisMark.cal_mark_min+"   axisMark.cal_mark_max="+axisMark.cal_mark_max+"   axisMark.cal_mark="+axisMark.cal_mark+"  总共"+axisMark.lableNum+"条横线");
    }

}
