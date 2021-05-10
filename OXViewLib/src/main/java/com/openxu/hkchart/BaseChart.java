package com.openxu.hkchart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.hkchart.loading.BallPulseIndicator;
import com.openxu.hkchart.loading.LoadingIndicator;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.LogUtil;
import com.openxu.utils.SharedData;

/**
 * Author: openXu
 * Time: 2020/11/6 9:32
 * class: BaseChart
 * Description:
 */
public abstract class BaseChart extends View {

    protected String TAG = getClass().getSimpleName();
//    protected boolean debug = BuildConfig.DEBUG;
    protected boolean debug = SharedData.getInstance().getData(SharedData.KEY_DEBUG, Boolean.class);

    //坐标轴辅助线宽度
    protected int axisLineWidth = DensityUtil.dip2px(getContext(), 0.8f);
    //画笔
    protected Paint paint;
    protected Paint paintText;
    protected Paint paintEffect;

    /**正在加载*/
    protected boolean loading = true;
    /**计算*/
    protected int screenWidth, screenHeight;  //屏幕宽高
    protected RectF rectChart;                //图表矩形
    protected Point centerPoint;             //chart中心点坐标
    /**动画*/
    protected boolean showAnim = true;
    protected ValueAnimator chartAnim;
    protected float chartAnimValue = 1;       //动画值
    protected boolean chartAnimStarted = false;
    //加载动画
    private LoadingIndicator loadingIndicator;

    public BaseChart(Context context) {
        this(context, null);
    }

    public BaseChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
        paint = new Paint();
        paint.setAntiAlias(true);
        paintText = new Paint();
        paintText.setAntiAlias(true);
        paintEffect = new Paint();
        paintEffect.setAntiAlias(true);

        //加载动画
        setLoadingIndicator("BallPulseIndicator");

        init(context, attrs, defStyle);

//        setClickable(true);
//        setOnClickListener(v->{
//            Toast.makeText(getContext(), "dianji",Toast.LENGTH_LONG).show();
//            setLoading(!loading);
//        });
    }
    public void init(Context context, AttributeSet attrs, int defStyleAttr){}

    public void setShowAnim(boolean showAnim) {
        this.showAnim = showAnim;
    }

    /***************************动画start***************************/
    public void setLoadingIndicator(String indicatorName){
        if (TextUtils.isEmpty(indicatorName))
            return;
        loadingIndicator = new BallPulseIndicator(getContext());
        indicatorName = "com.openxu.hkchart.loading."+indicatorName;
        try {
            loadingIndicator = (LoadingIndicator)Class.forName(indicatorName).getConstructor(Context.class).newInstance(getContext());
            loadingIndicator.setCallback(this);
        } catch (Exception e) {
            Log.e(TAG,"Didn't find your class , check the name again !");
        }
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        if(loading) {
            loadingIndicator.start();
        } else {
            loadingIndicator.stop();
            postInvalidate();
        }
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(loading)
            loadingIndicator.start();
    }
    @Override
    protected void onDetachedFromWindow() {
        loadingIndicator.stop();
        if(chartAnim!=null) {
            chartAnim.cancel();
        }
        super.onDetachedFromWindow();
    }
    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == loadingIndicator || super.verifyDrawable(who);
    }
    @Override
    public void invalidateDrawable(Drawable dr) {
        if (verifyDrawable(dr)) {
            final Rect dirty = dr.getBounds();
            invalidate(dirty);
        } else {
            super.invalidateDrawable(dr);
        }
    }

    private void startChartAnimation(Canvas canvas) {
        if(chartAnim == null){
            chartAnim = ValueAnimator.ofObject(new AngleEvaluator(), 0f, 1f);
            chartAnim.setDuration(1000);
            chartAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            chartAnim.addUpdateListener((ValueAnimator animation)->{
                chartAnimValue = (float)animation.getAnimatedValue();
                postInvalidate();
            });
        }
        chartAnim.reverse();
        chartAnim.start();
        LogUtil.w(TAG, "开始绘制动画");
    }
    /**************************动画end**************************/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerPoint = new Point(getMeasuredWidth()/2, getMeasuredHeight()/2);
        rectChart = new RectF(getPaddingLeft(),getPaddingTop(),getMeasuredWidth()-getPaddingRight(),
                getMeasuredHeight()-getPaddingBottom());
        loadingIndicator.setBounds((int)rectChart.left, (int)rectChart.top,
                (int)rectChart.right, (int)rectChart.bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        Log.e(TAG, "=================绘制图表");
        if(debug)
            drawDebug(canvas);
        if(loading){
            final int saveCount = canvas.save();
//            canvas.translate(getPaddingLeft(), getPaddingTop());
            loadingIndicator.draw(canvas);
            canvas.restoreToCount(saveCount);
            return;
        }

        if(showAnim && !chartAnimStarted){
            chartAnimStarted = true;
            startChartAnimation(canvas);
        }else{
            drawChart(canvas);
        }
    }

    /**绘制debug辅助线*/
    private void drawDebug(Canvas canvas){
        paint.setStyle(Paint.Style.STROKE);//设置空心
        paint.setStrokeWidth(axisLineWidth);
        //绘制边界--chart区域
        paint.setColor(Color.BLACK);
        RectF r = new RectF(0,0,getMeasuredWidth(), getMeasuredHeight());
        canvas.drawRect(r, paint);
        paint.setColor(Color.RED);
        r = new RectF(getPaddingLeft(),getPaddingTop(),getMeasuredWidth()-getPaddingRight(), getMeasuredHeight()-getPaddingBottom());
        canvas.drawRect(r, paint);
        paint.setColor(Color.GREEN);
        canvas.drawRect(rectChart, paint);
    }
    /**绘制图表*/
    public abstract void drawChart(Canvas canvas);



}
