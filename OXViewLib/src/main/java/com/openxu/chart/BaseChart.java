package com.openxu.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.openxu.chart.loading.BallPulseIndicator;
import com.openxu.chart.loading.LoadingIndicator;
import com.openxu.cview.BuildConfig;
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
    protected Paint paintText;
    protected Paint paintEffect;

    /**正在加载*/
    protected boolean loading = true;
    /**计算*/
    protected int screenWidth, screenHeight;  //屏幕宽高
    protected Rect rectChart;                //图表矩形
    protected Point centerPoint;             //chart中心点坐标
    /**动画*/
//    protected long animDuration = 1000;
//    protected ValueAnimator anim;
//    protected boolean startDraw = false;

    //加载动画
    private LoadingIndicator loadingIndicator;


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
        paintText = new Paint();
        paintText.setAntiAlias(true);
        paintEffect = new Paint();
        paintEffect.setAntiAlias(true);
        mGestureDetector = new GestureDetector(getContext(), new MyOnGestureListener());
        //加载动画
        loadingIndicator = new BallPulseIndicator(getContext());
        loadingIndicator.setCallback(this);

        init(context, attrs, defStyle);

//        setClickable(true);
//        setOnClickListener(v->{
//            Toast.makeText(getContext(), "dianji",Toast.LENGTH_LONG).show();
//            setLoading(!loading);
//        });
    }
    public void init(Context context, AttributeSet attrs, int defStyleAttr){}


    /******************************************************/
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
    /****************************************************/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        centerPoint = new Point(getMeasuredWidth()/2, getMeasuredHeight()/2);
        rectChart = new Rect(getPaddingLeft(),getPaddingTop(),getMeasuredWidth()-getPaddingRight(),
                getMeasuredHeight()-getPaddingBottom());
        loadingIndicator.setBounds((int)rectChart.left, (int)rectChart.top,
                (int)rectChart.right, (int)rectChart.bottom);
        Log.w(TAG, "测量："+rectChart);
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
        drawChart(canvas);
    }

    /**绘制debug辅助线*/
    private void drawDebug(Canvas canvas){
        paint.setStyle(Paint.Style.STROKE);//设置空心
        paint.setStrokeWidth(axisLineWidth);
        //绘制边界--chart区域
        paint.setColor(Color.BLUE);
        RectF r = new RectF(0,0,getMeasuredWidth(), getMeasuredHeight());
        canvas.drawRect(r, paint);
        paint.setColor(Color.RED);
        canvas.drawRect(rectChart, paint);
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
