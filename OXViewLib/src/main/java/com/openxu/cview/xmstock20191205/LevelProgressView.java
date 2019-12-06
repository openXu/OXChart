package com.openxu.cview.xmstock20191205;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.xmstock.BaseChart;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;


public class LevelProgressView extends BaseChart {

    private int total;           //总数量
    private int progress;        //占比数量
    private float animPro;       //动画计算的占比数量
    private String lvStr = "Lv";
    private String lable;
    protected PointF startPoint;
    //文字大小
    private int lableTextSize = (int)getResources().getDimension(R.dimen.ts_chart_lable);
    private int lvTextSize = (int)getResources().getDimension(R.dimen.ts_chart_xy);
    private int textSpace = DensityUtil.dip2px(getContext(), 3);    //文字间距
    private int raidus = DensityUtil.dip2px(getContext(), 5);          //圆环半径
    private int barSize = DensityUtil.dip2px(getContext(), 5);  //宽度
    //默认Lv字体颜色（白色）
    private int textColor = Color.WHITE;
    //进度颜色（黄色）
    private int proColor = Color.parseColor("#fda33c");

    public LevelProgressView(Context context) {
        super(context, null);
    }
    public LevelProgressView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }
    public LevelProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        float height = getMeasuredHeight();
        paintLabel.setTextSize(lvTextSize);
        int centerY = (int)(getPaddingBottom()+FontUtil.getFontHeight(paintLabel) + textSpace + raidus);
        height = getPaddingBottom()+FontUtil.getFontHeight(paintLabel) + textSpace*2 + raidus*2+getPaddingTop();
        paintLabel.setTextSize(lvTextSize);
        height += FontUtil.getFontHeight(paintLabel);
        float lv0 = FontUtil.getFontlength(paintLabel, lvStr+"0");
        float lv10 = FontUtil.getFontlength(paintLabel, lvStr+"10");
        startPoint = new PointF(getPaddingLeft() + lv0/2, centerY);
        rectChart = new RectF(startPoint.x, getPaddingTop(), width - getPaddingRight() - lv10/2, height-getPaddingBottom());
        setMeasuredDimension(width, (int)height);
        centerPoint = new PointF(width/2, height/2);
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public void setData(int total, int progress){
        this.total = total;
        this.progress = progress;
        startDraw = false;
        invalidate();
    }

    /**绘制图表基本框架*/
    @Override
    public void drawDefult(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(defColor);
        canvas.drawRect(rectChart.left, startPoint.y-barSize/2,
                rectChart.right, startPoint.y+barSize/2, paint);
        float itemWidth = rectChart.width()/(total-1);
        float x;
        paintLabel.setTextSize(lvTextSize);
        paintLabel.setColor(textColor);
        float lvHeight = FontUtil.getFontHeight(paintLabel);
        float lvLead = FontUtil.getFontLeading(paintLabel);
        for(int i = 0; i < total; i++){
            x = rectChart.left + i*itemWidth;
            canvas.drawCircle(x, startPoint.y, raidus, paint);
            //绘制文字
            canvas.drawText(lvStr+i, x - FontUtil.getFontlength(paintLabel, lvStr+i)/2,
                    rectChart.bottom-lvHeight+lvLead, paintLabel);
        }

    }

    /**绘制图表*/
    @Override
    public void drawChart(Canvas canvas) {
        if(progress>0) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(proColor);
            float itemWidth = rectChart.width()/(total-1);
            Log.w(TAG, "动画进度："+animPro);
            canvas.drawRect(rectChart.left, startPoint.y-barSize/2,
                    rectChart.left+animPro*itemWidth, startPoint.y+barSize/2, paint);
            for(int i = 0; i <= (int)animPro; i++){
                canvas.drawCircle(rectChart.left + i*itemWidth, startPoint.y, raidus, paint);
                if(i!=0 && i==animPro){
                    //绘制颜色Lv文字
                    paintLabel.setTextSize(lvTextSize);
                    paintLabel.setColor(proColor);
                    float textHeight = FontUtil.getFontHeight(paintLabel);
                    float textLead = FontUtil.getFontLeading(paintLabel);
                    canvas.drawText(lvStr+i, rectChart.left + i*itemWidth
                                    - FontUtil.getFontlength(paintLabel, lvStr+i)/2,
                            rectChart.bottom-textHeight+textLead, paintLabel);
                    //绘制上方文字
                    if(null!=lable){
                        paintLabel.setTextSize(lableTextSize);
                        textHeight = FontUtil.getFontHeight(paintLabel);
                        textLead = FontUtil.getFontLeading(paintLabel);
                        float textLength = FontUtil.getFontlength(paintLabel, lable);
                        canvas.drawText(lable,
                                (rectChart.left + i*itemWidth + textLength/2)<=getWidth() - getPaddingRight()?
                                        rectChart.left + i*itemWidth-textLength/2 :
                                        getWidth() - getPaddingRight() - textLength,
                                startPoint.y -raidus - textSpace - textHeight+textLead, paintLabel);
                    }
                }
            }
        }
    }
    /**创建动画*/
    @Override
    protected ValueAnimator initAnim() {
        if(progress>0) {
            ValueAnimator anim = ValueAnimator.ofObject(new AngleEvaluator(), 1f, Float.valueOf(progress+""));
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            return anim;
        }
        return null;
    }
    /**动画值变化之后计算数据*/
    @Override
    protected void evaluatorData(ValueAnimator animation) {
        animPro = (float)animation.getAnimatedValue() - 1;
    }




}
