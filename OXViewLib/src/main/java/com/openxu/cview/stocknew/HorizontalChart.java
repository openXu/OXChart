package com.openxu.cview.stocknew;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.stocknew.bean.BaseChartData;
import com.openxu.cview.xmstock.BaseChart;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * autour : xiami
 * date : 2018/11/28 14:26
 * className : RadarChart
 * version : 1.0
 * description : 上涨下跌横向占比图
 */
public class HorizontalChart extends BaseChart {

    //设置数据
    private List<BaseChartData> dataList = new ArrayList<>();
    private float total;
    //直线高度
    private int barHeight = DensityUtil.dip2px(getContext(), 7);
    private int textSpaceT = DensityUtil.dip2px(getContext(), 12);
    private int textSpaceB = DensityUtil.dip2px(getContext(), 14);
    private int textSize = (int)getResources().getDimension(R.dimen.text_size_level_def);
    private int colors[] = {
            Color.parseColor("#ff0000"),
            Color.parseColor("#089c20")
    };
    private int textColors[] = {
            Color.parseColor("#bb080a"),
            Color.parseColor("#067518")
    };

    float textHeight, textLeading;

    public HorizontalChart(Context context) {
        this(context, null);
    }
    public HorizontalChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public HorizontalChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {

    }

    /***********************************设置属性set方法**********************************/
    /***********************************设置属性set方法over**********************************/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        paintLabel.setTextSize(textSize);
        textHeight = FontUtil.getFontHeight(paintLabel);
        textLeading = FontUtil.getFontLeading(paintLabel);
        setMeasuredDimension(widthSize, (int)(barHeight + textSpaceT + textSpaceB + textHeight*2));
        onMeasureAfter();
        postInvalidate();
    }

    /***************************************************/

    /***************************************************/

    /**
     * 设置数据
     */
    public void setData(List<BaseChartData> dataList){
        if(null==dataList)
            return;
        this.dataList.clear();
        this.dataList.addAll(dataList);
        total = 0;
        for(BaseChartData data : dataList){
            total += data.getNum();
        }
        if(getMeasuredWidth()>0) {
            startDraw = false;
            invalidate();
        }
    }
    /**绘制图表基本框架*/
    @Override
    public void drawDefult(Canvas canvas) {
        if(null==dataList || dataList.size()<=0)
            return;
    }

    /**绘制debug辅助线*/
    @Override
    public void drawDebug(Canvas canvas) {
        super.drawDebug(canvas);
        canvas.drawRect(getPaddingLeft(), getPaddingTop(),
                getMeasuredWidth() - getPaddingRight(),
                getMeasuredHeight() - getPaddingBottom(), paint);
    }

    /**绘制图表*/
    @Override
    public void drawChart(Canvas canvas) {
        if(null==dataList || dataList.size()<=0)
            return;
        paint.setStyle(Paint.Style.FILL);
        paintLabel.setTextSize(textSize);
        paintLabel.setTypeface(Typeface.DEFAULT_BOLD);
        float left = getPaddingLeft();
        float allW = getMeasuredWidth()-getPaddingRight()-getPaddingLeft();
        String text;
        for(int i = 0; i <dataList.size(); i++){
            float top = 0;
            float perW = dataList.get(i).getNum()/total * allW;
            paint.setColor(colors[i]);
            paintLabel.setColor(textColors[i]);
            text = (int)dataList.get(i).getNum()+"家";
            if(i==dataList.size()-1){
                float textW = FontUtil.getFontlength(paintLabel, text);
                canvas.drawText(text,
                        getMeasuredWidth()-getPaddingRight()-textW,
                        top+textLeading, paintLabel);
                canvas.drawRect(left, top+textHeight+textSpaceT,
                        left+(getMeasuredWidth()-getPaddingRight() - left) * animPro,
                        top+textHeight+textSpaceT+barHeight, paint);
                textW = FontUtil.getFontlength(paintLabel, dataList.get(i).getName());
                canvas.drawText(dataList.get(i).getName(), getMeasuredWidth()-getPaddingRight()-textW,
                        top+textHeight+textSpaceT+barHeight+textSpaceB+textLeading, paintLabel);
            }else{
                canvas.drawText(text, left, top+textLeading, paintLabel);
                canvas.drawRect(left, top+textHeight+textSpaceT,
                        left+perW * animPro, top+textHeight+textSpaceT+barHeight, paint);
                canvas.drawText(dataList.get(i).getName(), left,
                        top+textHeight+textSpaceT+barHeight+textSpaceB+textLeading, paintLabel);
            }
            left += perW;
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
