package com.openxu.cview.chart.barchart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.chart.BaseChart;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.chart.bean.BarBean;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import static com.openxu.cview.chart.BaseChart.TOUCH_EVENT_TYPE.EVENT_Y;


/**
 * autour : openXu
 * date : 2017/7/24 10:46
 * className : BarHorizontalChart
 * version : 1.0
 * description : 横向(柱子水平) 柱状图，支持多柱
 */
public class BarHorizontalChart extends BaseChart {

    private List<List<BarBean>> dataList;
    private List<String> strList;

    /**可以设置的属性*/

    private int barNum = 2;   //柱子数量
    private int XMARK_NUM =  5;    //X轴刻度数量

    private int barWidth = DensityUtil.dip2px(getContext(), 15);    //柱宽度
    private int barSpace = DensityUtil.dip2px(getContext(), 1);    //双柱间的间距
    private int barItemSpace = DensityUtil.dip2px(getContext(), 25);//一组柱之间的间距
    private int[] barColor = new int[]{Color.BLUE, Color.YELLOW};               //柱颜色

    private int textSizeCoordinate = (int)getResources().getDimension(R.dimen.text_size_level_small); //坐标文字大小
    private int textColorCoordinate = getResources().getColor(R.color.text_color_light_gray);
    private int textSizeTag = (int)getResources().getDimension(R.dimen.text_size_level_small); //数值字体
    private int textColorTag = getResources().getColor(R.color.text_color_light_gray);

    private int textSpace = DensityUtil.dip2px(getContext(), 3);         //默认的字与其他的间距

    /**需要计算相关值*/
    private int oneBarW;            //单个宽度,需要计算
    private int YMARK_MAX_WIDTH = DensityUtil.dip2px(getContext(), 30);    //Y轴刻度最大值的宽度
    private int XMARK_H, XMARK_ALL_H;            //Y轴刻度间距值
    private int topStartPointY;    //从左侧开始绘制的X坐标
    private int minTopPointY;      //滑动到最右侧时的X坐标

    private PointF zeroPoint = new PointF();    //柱状图图体圆点坐标

    /*字体绘制相关*/
    private int XMARK =  1;         //X刻度间隔
    private int XMARK_MAX =  1;    //X轴刻度最大值（根据设置的数据自动赋值）

    private int heightCoordinate;
    private int leadCoordinate;
    private int heightTag;
    private int leadTag;


    public BarHorizontalChart(Context context) {
        this(context, null);
    }
    public BarHorizontalChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public BarHorizontalChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        touchEventType = EVENT_Y;
        dataList = new ArrayList<>();
        strList = new ArrayList<>();
    }
    /***********************************设置属性set方法**********************************/
    public void setBarNum(int barNum) {
        this.barNum = barNum;
    }

    public void setXMARK_NUM(int XMARK_NUM) {
        this.XMARK_NUM = XMARK_NUM;
    }

    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
    }

    public void setBarSpace(int barSpace) {
        this.barSpace = barSpace;
    }

    public void setBarItemSpace(int barItemSpace) {
        this.barItemSpace = barItemSpace;
    }

    public void setBarColor(int[] barColor) {
        this.barColor = barColor;
    }

    public void setTextSizeCoordinate(int textSizeCoordinate) {
        this.textSizeCoordinate = textSizeCoordinate;
    }

    public void setTextColorCoordinate(int textColorCoordinate) {
        this.textColorCoordinate = textColorCoordinate;
    }

    public void setTextSizeTag(int textSizeTag) {
        this.textSizeTag = textSizeTag;
    }

    public void setTextColorTag(int textColorTag) {
        this.textColorTag = textColorTag;
    }

    public void setTextSpace(int textSpace) {
        this.textSpace = textSpace;
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
    /**滑动*/
    @Override
    protected void evaluatorFling(float fling) {
        LogUtil.i(TAG, "fling = "+fling);
        //已经滚动到顶部或者底部
        if(topStartPointY + mMoveLen + fling <= minTopPointY){
            mMoveLen = minTopPointY-topStartPointY;
            moveOver = true;
            if(null!=touchAnim && touchAnim.isRunning())
                touchAnim.cancel();
        }else if(topStartPointY + mMoveLen+ fling>=topStartPointY){
            mMoveLen = 0;
            moveOver = true;
            if(null!=touchAnim && touchAnim.isRunning())
                touchAnim.cancel();
        }else{
            moveOver = false;
            mMoveLen += fling;
        }
    }
    /***************************************************/

    /**
     * 设置数据
     * @param dataList 柱状图数据
     * @param strYList Y轴坐标数据
     */
    public void setData(List<List<BarBean>> dataList, List<String> strYList){
        this.strList.clear();
        this.dataList.clear();
        if(dataList!=null)
            this.dataList.addAll(dataList);
        if(strYList!=null)
            this.strList.addAll(strYList);
        if(getMeasuredWidth()>0) {
            evaluatorByData();
            startDraw = false;
            invalidate();
        }
    }
    /**设置数据后，计算相关值*/
    private void evaluatorByData(){
        total = 0;
        //三种字体计算
        paintLabel.setTextSize(textSizeCoordinate);
        heightCoordinate = (int) FontUtil.getFontHeight(paintLabel);
        leadCoordinate = (int)FontUtil.getFontLeading(paintLabel);
        paintLabel.setTextSize(textSizeTag);
        heightTag = (int)FontUtil.getFontHeight(paintLabel);
        leadTag = (int)FontUtil.getFontLeading(paintLabel);
        //zeroPoint坐标
        if(strList!=null && strList.size()>0){
            YMARK_MAX_WIDTH = 0;
            paintLabel.setTextSize(textSizeCoordinate);
            for(String xStr : strList){
                int tw = (int)FontUtil.getFontlength(paintLabel, xStr);
                YMARK_MAX_WIDTH = tw>YMARK_MAX_WIDTH?tw:YMARK_MAX_WIDTH;
            }
        }
        zeroPoint.x = rectChart.left + YMARK_MAX_WIDTH + textSpace;
        zeroPoint.y = heightCoordinate + textSpace;

        /*计算X刻度最大值*/
        XMARK_MAX =  1;
        for(List<BarBean> list : dataList){
            for(BarBean bean : list){
                total += bean.getNum();
                XMARK_MAX = (int)bean.getNum()>XMARK_MAX?(int)bean.getNum():XMARK_MAX;
            }
        }

        LogUtil.i(TAG, "真实XMARK_MAX="+XMARK_MAX);
        if (XMARK_MAX<=5)
            XMARK_MAX = 5;
        XMARK = XMARK_MAX/XMARK_NUM + 1;

        int MARK = (Integer.parseInt((XMARK+"").substring(0,1))+1);

        if((XMARK+"").length()==1){
            //XMARK = 1、2、5、10
            XMARK = (XMARK==3||XMARK==4||XMARK==6||XMARK==7||XMARK==8||XMARK==9)?((XMARK==3||XMARK==4)?5:10):XMARK;
        }else if((XMARK+"").length()==2){
            XMARK = MARK*10;
        }else if((XMARK+"").length()==3){
            XMARK = MARK*100;
        }else if((XMARK+"").length()==4){
            XMARK = MARK*1000;
        }else if((XMARK+"").length()==5){
            XMARK = MARK*10000;
        }else if((XMARK+"").length()==6){
            XMARK = MARK*100000;
        }
        XMARK_MAX = XMARK*XMARK_NUM;
        LogUtil.i(TAG, "计算XMARK_MAX="+XMARK_MAX+"   XMARK="+XMARK);

        //Y轴刻度间距值
        XMARK_ALL_H = (int)(rectChart.right-zeroPoint.x);
        XMARK_H = (int)((float)XMARK_ALL_H/XMARK_NUM);

        //单份柱子（包含多个及其间距）宽度
        oneBarW = (barWidth*barNum)+(barSpace*(barNum-1));

        topStartPointY = (int)zeroPoint.y+barItemSpace/2;  //从左侧开始绘制的X坐标

        int allW = (oneBarW+barItemSpace)*dataList.size();   //每份柱子宽度+item间距（其中包含第一个item左边和最后一个item右边的半份间距）
        int contentW = (int)(rectChart.bottom-zeroPoint.y);
        touchEnable = allW>contentW;

        if(touchEnable){
            //超出总宽度了
            minTopPointY = -allW+(int)(rectChart.bottom-topStartPointY-barItemSpace);
        }


        LogUtil.w(TAG, "柱状图表宽高："+getMeasuredWidth()+"*"+getMeasuredHeight()+
                "  图表范围"+rectChart+"   圆点坐标zeroPoint="+zeroPoint);

    }

    /**绘制图表基本框架*/
    @Override
    public void drawDefult(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(defColor);

        canvas.drawLine(zeroPoint.x, zeroPoint.y, rectChart.right, zeroPoint.y, paint);
        if(touchEnable){
            //超出高度了
            canvas.drawLine(zeroPoint.x, zeroPoint.y,zeroPoint.x , rectChart.bottom, paint);
        }else{
            canvas.drawLine(zeroPoint.x, zeroPoint.y,zeroPoint.x , zeroPoint.y+(oneBarW+barItemSpace)*dataList.size(), paint);
        }
    }
    /**绘制debug辅助线*/
    @Override
    public void drawDebug(Canvas canvas) {
        super.drawDebug(canvas);
    }
    /**绘制图表*/
    @Override
    public void drawChart(Canvas canvas) {

        int topStart = topStartPointY + mMoveLen;

//        LogUtil.i(TAG, "leftStart："+leftStart+"   zezeroPoint"+zeroPoint+"   barItemSpace="+barItemSpace+"   barWidth="+barWidth +"   oneBarW="+oneBarW);

        paint.setStyle(Paint.Style.FILL);
        LogUtil.w(TAG, "");

        for(int i = 0; i<dataList.size(); i++){
            String str  = strList.get(i);
            List<BarBean> list = dataList.get(i);
            //柱子开始绘制的Y坐标
            int topY = topStart +(oneBarW+barItemSpace)*i;
            //绘制X刻度
            paintLabel.setTextSize(textSizeCoordinate);
            paintLabel.setColor(textColorCoordinate);
            float tw = FontUtil.getFontlength(paintLabel, str);
            canvas.drawText(str, zeroPoint.x-textSpace-tw, topY + oneBarW/2-heightCoordinate/2+leadCoordinate,paintLabel);

            //绘制辅助线
            paint.setStrokeWidth(0.5f);
            paint.setColor(defColor);
            canvas.drawLine(zeroPoint.x, topY-15, rectChart.right, topY-15, paint);

            paintLabel.setTextSize(textSizeTag);
            paintLabel.setColor(textColorTag);
           for(int j = 0 ;j <list.size(); j++){
               BarBean bean = list.get(j);

               //绘制辅助线
               paint.setStrokeWidth(0.5f);
               paint.setColor(defColor);
               canvas.drawLine(zeroPoint.x, topY+barWidth/2, rectChart.right, topY+barWidth/2, paint);

               //绘制柱子
               paint.setColor(barColor[j]);
               float right = (zeroPoint.x+XMARK_ALL_H*(bean.getNum()/XMARK_MAX)*animPro);
                RectF br = new RectF(zeroPoint.x , topY , right, topY+barWidth);
                canvas.drawRect(br, paint);

               //绘制柱子占比
               str = (int)bean.getNum()+"";
               canvas.drawText(str, right+textSpace, topY +barWidth/2 -heightTag/2+ leadTag,paintLabel);

//                LogUtil.d(TAG, "绘制bar："+(br)+"    "+getMeasuredWidth()+"*"+getMeasuredHeight());
               topY += (barWidth+barSpace);
            }

            //绘制辅助线
            paint.setStrokeWidth(0.5f);
            paint.setColor(defColor);
            canvas.drawLine(zeroPoint.x, topY-barSpace+15, rectChart.right, topY-barSpace+15, paint);

        }

        drawYmark(canvas);
    }

    private void drawYmark(Canvas canvas) {
        //遮盖超出的柱状
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(backColor);
        canvas.drawRect(new RectF(0,0,rectChart.right, zeroPoint.y-lineWidth), paint);
        canvas.drawRect(new RectF(0,rectChart.bottom,rectChart.right, getMeasuredHeight()), paint);

        paintLabel.setTextSize(textSizeCoordinate);
        paintLabel.setColor(textColorCoordinate);
        for(int i = 0; i<=XMARK_MAX/XMARK; i++){
            //绘制X刻度
            String textX = (XMARK*i)+"";
            float tw = FontUtil.getFontlength(paintLabel, textX);
//            canvas.drawText(textX, getPaddingLeft()+(YMARK_MAX_WIDTH-tw), zeroPoint.y-(i*XMARK_H) - heightCoordinate/2+leadCoordinate,paintLabel);
            canvas.drawText(textX, (int)(zeroPoint.x + (i*XMARK_H) - tw/2), zeroPoint.y-textSpace-heightCoordinate+leadCoordinate,paintLabel);
        }
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
