package com.openxu.cview.xmstock20201030;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

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
 * className : ProgressBar
 * version : 1.0
 * description : 不规则进度条
 */
public class ProgressBar extends BaseChart {
    /**可以设置的属性*/
    private int[] barColor = new int[]{Color.parseColor("#e9403b"),
            Color.parseColor("#f98683")};
    private float total = 100;
    private float progress = 0;
    //左边图
    private int bitmapID = R.mipmap.ic_concept_hot_more_fire;
    //进度条高度
    private int barHeight = DensityUtil.dip2px(getContext(), 8f);
    private int chonghe = DensityUtil.dip2px(getContext(), 3f);//重合度
    private float up = 0.2f;  //预留图片下方白色区域
    private Bitmap bitmap;

    public ProgressBar(Context context) {
        this(context, null);
    }
    public ProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        touchEnable = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if(bitmap==null)
            bitmap = BitmapFactory.decodeResource(getResources(), bitmapID);

        float uph = up*bitmap.getHeight();
        float bottom = bitmap.getHeight() - uph - barHeight;
        heightSize = bitmap.getHeight() + (int)bottom;   //让进度条居中
        setMeasuredDimension(widthSize, heightSize);
        rectChart = new RectF(getPaddingLeft(),getPaddingTop(),getMeasuredWidth()-getPaddingRight(),
                getMeasuredHeight()-getPaddingBottom());
        centerPoint = new PointF(getMeasuredWidth()/2, getMeasuredHeight()/2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        evaluatorByData();
        startDraw = false;
        isLoading = false;
        invalidate();
    }
    public void setData(float total, float progress){
        this.total = total;
        this.progress = progress;
        if(getMeasuredWidth()>0) {
            evaluatorByData();
            startDraw = false;
            isLoading = false;
            invalidate();
        }
    }
    private void evaluatorByData(){
    }


    @Override
    public void drawDefult(Canvas canvas) {
    }
    @Override
    public void drawChart(Canvas canvas) {
        float left = bitmap.getWidth()-chonghe;
        float proSize = (rectChart.right-left)/total*progress * animPro;
        //添加路径信息
        Path pathDef = new Path();
        Path pathPro = new Path();
        pathDef.moveTo(left , centerPoint.y-barHeight/2);  //左上
        pathPro.moveTo(left , centerPoint.y-barHeight/2);
        pathDef.lineTo(rectChart.right-barHeight/2, centerPoint.y-barHeight/2);  //右上
        pathPro.lineTo(left + proSize, centerPoint.y-barHeight/2);
        RectF rectFDef = new RectF(rectChart.right-barHeight, centerPoint.y-barHeight/2, rectChart.right, centerPoint.y+barHeight/2);
        RectF rectFPro = new RectF(left + proSize -barHeight, centerPoint.y-barHeight/2, left + proSize , centerPoint.y+barHeight/2);
        pathDef.arcTo(rectFDef, -90, 180);
        pathPro.arcTo(rectFPro, -90, 180);
//        canvas.drawArc(rectFDef, -90, 180, false ,paint);
        pathDef.lineTo(rectChart.right - barHeight/2, centerPoint.y+barHeight/2);  //右下
        pathPro.lineTo(rectChart.right - proSize - barHeight/2, centerPoint.y+barHeight/2);
        pathDef.lineTo(left, centerPoint.y+barHeight/2);  //左下
        pathPro.lineTo(left, centerPoint.y+barHeight/2);
        //1.绘制默认灰色
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(defColor);
        canvas.drawPath(pathDef, paint);
        //2.绘制进度
        //线性渐变：前两个参数是渐变开始的点坐标，第三四个参数是渐变结束的点的坐标；渐变的颜色，渐变颜色的分布，模式
        Shader mShader = new LinearGradient(left, centerPoint.y ,rectChart.right, centerPoint.y,
                new int[] {barColor[0],barColor[1]},null,Shader.TileMode.CLAMP);
        Log.w(TAG, "从"+left+"*"+centerPoint.y+"->"+rectChart.right+"*"+centerPoint.y+"渐变");
        paintEffect.setStyle(Paint.Style.FILL);
        paintEffect.setColor(defColor);
        paintEffect.setShader(mShader);
        canvas.drawPath(pathPro, paintEffect);
        paintEffect.setShader(null);
        //圆角矩形
//        canvas.drawRoundRect(new RectF(bitmap.getWidth()-chonghe, centerPoint.y+barHeight/2,
//                        rectChart.right, centerPoint.y-barHeight/2),
//                barHeight/2, barHeight/2, paint);
        canvas.drawBitmap(bitmap, rectChart.left, rectChart.top, paint);
    }

    private float animPro;       //动画计算的占比数量
    /**创建动画*/
    @Override
    protected ValueAnimator initAnim() {
        if(progress>0) {
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
