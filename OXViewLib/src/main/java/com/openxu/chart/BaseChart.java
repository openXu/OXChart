package com.openxu.chart;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import com.openxu.cview.BuildConfig;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.LogUtil;

/**
 * Author: openXu
 * Time: 2020/11/6 9:32
 * class: BaseChart
 * Description:
 */
public abstract class BaseChart extends View {

    protected String TAG = getClass().getSimpleName();
    protected boolean debug = BuildConfig.DEBUG;

    /**可设置属性*/
    //坐标轴辅助线宽度
    protected int axisLineWidth = DensityUtil.dip2px(getContext(), 0.8f);
    //画笔
    protected Paint paint;
    protected Paint textPaint;
    protected Paint effectPaint;

    /**正在加载*/
    protected boolean loading = true;
    /**计算*/
    protected int screenWidth, screenHeight;  //屏幕宽高
    protected RectF rectChart;                //图表矩形
    protected PointF centerPoint;             //chart中心点坐标
    /**动画*/
//    protected long animDuration = 1000;
//    protected ValueAnimator anim;
//    protected boolean startDraw = false;
    //加载中的状态
    private int[] loadCircleColors = new int[]{Color.parseColor("#DB4528")
                            ,Color.parseColor("#5f93e7")
                            ,Color.parseColor("#fda33c")};
    private float loadRadius = DensityUtil.dip2px(getContext(), 5);
    private float loadCircleSpace = DensityUtil.dip2px(getContext(), 4);
    private float loadingAnimValue;
    private float loadingAnimValueMax = 1;
    private float loadingAnimValueMin = 0.4f;
    private ValueAnimator loadingAnim;

    protected GestureDetector mGestureDetector;


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
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        effectPaint = new Paint();
        effectPaint.setAntiAlias(true);
        mGestureDetector = new GestureDetector(getContext(), new MyOnGestureListener());
        //加载动画
        loadingAnim = ValueAnimator.ofObject(new AngleEvaluator(), loadingAnimValueMin, loadingAnimValueMax);
        loadingAnim.setInterpolator(new DecelerateInterpolator());   //越来越慢
        loadingAnim.setRepeatMode(ValueAnimator.REVERSE);
        loadingAnim.setRepeatCount(ValueAnimator.INFINITE);
        loadingAnim.addUpdateListener((ValueAnimator animation)->{
            loadingAnimValue = (float)animation.getAnimatedValue();
//            Log.i(TAG, "动画："+loadingAnimValue);
            invalidate();
        });
        loadingAnim.setDuration(800);

        init(context, attrs, defStyle);
    }
    public void init(Context context, AttributeSet attrs, int defStyleAttr){}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        centerPoint = new PointF(getMeasuredWidth()/2, getMeasuredHeight()/2);
        rectChart = new RectF(getPaddingLeft(),getPaddingTop(),getMeasuredWidth()-getPaddingRight(),
                getMeasuredHeight()-getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(debug)
            drawDebug(canvas);
        if(loading){
            if(!loadingAnim.isStarted()) {
                Log.i(TAG, "开启正在加载动画...");
                loadingAnim.start();
            }
            drawLoading(canvas);
            return;
        }
    }
    /**绘制debug辅助线*/
    public void drawDebug(Canvas canvas){
        paint.setStyle(Paint.Style.STROKE);//设置空心
        paint.setStrokeWidth(axisLineWidth);
        //绘制边界--chart区域
        paint.setColor(Color.BLUE);
        RectF r = new RectF(0,0,getMeasuredWidth(), getMeasuredHeight());
        canvas.drawRect(r, paint);
        paint.setColor(Color.RED);
        canvas.drawRect(rectChart, paint);
    }
    public void drawLoading(Canvas canvas) {
        float diff = (loadingAnimValueMax - loadingAnimValueMin)/3;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(loadCircleColors[0]);
        canvas.drawCircle(centerPoint.x - loadRadius*2 - loadCircleSpace, centerPoint.y,
                loadRadius*loadingAnimValue,paint);
        paint.setColor(loadCircleColors[1]);
        canvas.drawCircle(centerPoint.x, centerPoint.y,
                loadRadius * loadingAnimValue, paint);
        paint.setColor(loadCircleColors[2]);
        canvas.drawCircle(centerPoint.x + loadRadius*2 + loadCircleSpace, centerPoint.y,
                loadRadius*(loadingAnimValueMin+loadingAnimValueMax-loadingAnimValue),paint);
    }
    /**绘制图表*/
    public abstract void drawChart(Canvas canvas);

//    protected boolean touchEnable = false;      //是否超界，控件的大小是否足以显示内容，是否需要滑动来展示。子控件根据计算赋值
//    protected float mDownX, mDownY;
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
//        if(!touchEnable){
//            getParent().requestDisallowInterceptTouchEvent(true);
//        }else{
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    mDownX = event.getX();
//                    mDownY = event.getY();
//                    if(null!=touchAnim && touchAnim.isRunning())
//                        touchAnim.cancel();
//                    getParent().requestDisallowInterceptTouchEvent(true);//ACTION_DOWN的时候，赶紧把事件hold住
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    break;
//                case MotionEvent.ACTION_CANCEL:
//                case MotionEvent.ACTION_UP:
//                    break;
//            }
//        }
        return super.dispatchTouchEvent(event);
    }

  /*  protected PointF lastTouchPoint;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!touchEnable)
            return false;
        boolean result = mGestureDetector.onTouchEvent(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchPoint = new PointF(event.getX(), event.getY());
                onTouchMoved(lastTouchPoint);
                return true;
            case MotionEvent.ACTION_MOVE:
                int move = 0;
                LogUtil.i(TAG, "MotionEvent.ACTION_MOVE"+move);
                lastTouchPoint.x = (int)event.getX();
                lastTouchPoint.y = (int)event.getY();
                onTouchMoved(lastTouchPoint);
                evaluatorFling(move);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                lastTouchPoint.x = 0;
                lastTouchPoint.y = 0;
                onTouchMoved(null);
                return true;
        }
        return result;
    }

    protected void onTouchMoved(PointF point){
    }


*/
  class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
      @Override
      public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
          LogUtil.e(TAG,"onFling------------>velocityX="+velocityX+"    velocityY="+velocityY);
          return false;
      }
  }










}
