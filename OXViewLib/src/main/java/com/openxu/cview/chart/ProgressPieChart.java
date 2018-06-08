package com.openxu.cview.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.chart.bean.ChartLable;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;

import java.util.List;


/**
 * autour : openXu
 * date : 2017/7/24 10:46
 * className : ProgressPieChart
 * version : 1.0
 * description : 进度图表
 */
public class ProgressPieChart extends BaseChart {

    private int total;           //总数量
    private int progress;        //占比数量
    private float animPro;       //动画计算的占比数量
    private List<ChartLable> lableList;   //需要绘制的lable集合
    private int textSpace = DensityUtil.dip2px(getContext(), 3);    //文字间距

    private int raidus;          //圆环半径
    private int proSize = DensityUtil.dip2px(getContext(), 8);  //圈宽度
    private int proColor = Color.rgb(0, 255, 0);         //进度环颜色


    public ProgressPieChart(Context context) {
        super(context, null);
    }
    public ProgressPieChart(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }
    public ProgressPieChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        startAngle = -90;  //开始的角度
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if(width<height){
            rectChart = new RectF(0,(height-width)/2,width, (height+width)/2);
        }else{
            rectChart = new RectF((width-height)/2, 0, (width+height)/2, height);
        }

        raidus = (width<height?width:height)/2;
    }

    public void setProSize(int proSize) {
        this.proSize = proSize;
    }

    public void setDefColor(int defColor) {
        this.defColor = defColor;
    }
    public void setProColor(int proColor) {
        this.proColor = proColor;
    }

    public void setData(int total, int progress, List<ChartLable> lableList){
        this.total = total;
        this.progress = progress;
        this.lableList = lableList;
        startDraw = false;
        invalidate();
    }

    /**绘制图表基本框架*/
    @Override
    public void drawDefult(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);//设置空心
        paint.setStrokeWidth(proSize);
        paint.setColor(defColor);
        canvas.drawCircle(centerPoint.x,centerPoint.y,raidus-proSize/2, paint);
    }

    /**绘制图表*/
    @Override
    public void drawChart(Canvas canvas) {
        if(progress>0) {
            paint.setStrokeWidth(proSize);
            paint.setColor(proColor);
            //1绘制开始圆点
//            paint.setStyle(Paint.Style.FILL);
//            canvas.drawCircle(centerPoint.x, centerPoint.y - raidus + proSize / 2, proSize / 2, paint);
            //2绘制进度圆弧
            paint.setStyle(Paint.Style.STROKE);//设置空心
            int r = raidus - proSize / 2;
            float arcLeft = centerPoint.x - r;
            float arcTop = centerPoint.y - r;
            float arcRight = centerPoint.x + r;
            float arcBottom = centerPoint.y + r;
            canvas.drawArc(new RectF(arcLeft, arcTop, arcRight, arcBottom), startAngle, animPro, false, paint);
            //3绘制结束圆点
//            paint.setStyle(Paint.Style.FILL);
//            float endX = centerPoint.x + (float) (r * Math.cos(Math.toRadians(startAngle + animPro)));
//            float endY = centerPoint.y + (float) (r * Math.sin(Math.toRadians(startAngle + animPro)));
//            canvas.drawCircle(endX, endY, proSize / 2, paint);
        }
        //4绘制文字
        if(lableList!=null &&lableList.size()>0){
            float textAllHeight = 0;
            float textW, textH, textL;
            for(ChartLable lable : lableList){
                paintLabel.setTextSize(lable.getTextSize());
                textH = FontUtil.getFontHeight(paintLabel);
                textAllHeight += (textH+textSpace);
            }
            textAllHeight -= textSpace;
            int top = (int)(centerPoint.y-textAllHeight/2);
            for(ChartLable lable : lableList){
                paintLabel.setColor(lable.getTextColor());
                paintLabel.setTextSize(lable.getTextSize());
                textW = FontUtil.getFontlength(paintLabel, lable.getText());
                textH = FontUtil.getFontHeight(paintLabel);
                textL = FontUtil.getFontLeading(paintLabel);
                canvas.drawText(lable.getText(), centerPoint.x-textW/2, top + textL, paintLabel);
                top += (textH+textSpace);
            }
        }

    }
    /**创建动画*/
    @Override
    protected ValueAnimator initAnim() {
        if(progress>0) {
            float angle = (float) progress / (float) total * 360;
            ValueAnimator anim = ValueAnimator.ofObject(new AngleEvaluator(), 0f, angle);
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
