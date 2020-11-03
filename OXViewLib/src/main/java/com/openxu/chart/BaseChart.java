package com.openxu.chart;

import android.animation.ValueAnimator;
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
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.openxu.cview.BuildConfig;
import com.openxu.cview.R;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;

import static com.openxu.cview.chart.BaseChart.TOUCH_EVENT_TYPE.EVENT_NULL;
import static com.openxu.cview.chart.BaseChart.TOUCH_EVENT_TYPE.EVENT_X;
import static com.openxu.cview.chart.BaseChart.TOUCH_EVENT_TYPE.EVENT_XY;
import static com.openxu.cview.chart.BaseChart.TOUCH_EVENT_TYPE.EVENT_Y;


/**
 * autour : openXu
 * date : 2017/7/24 10:46
 * className : BaseChart
 * version : 1.0
 * description : 图表基类
 */
public abstract class BaseChart extends View {

    protected String TAG = "BaseChart";
    protected int ScrWidth,ScrHeight;   //屏幕宽高
    protected RectF rectChart;    //图表矩形

    protected PointF centerPoint;    //chart中心点坐标

    protected int total;           //总数量

    /**可设置属性*/
    protected int startAngle = -90;  //开始的角度
    protected int backColor = Color.WHITE;
    protected int lineWidth = 1;     //辅助线宽度
    protected String loadingStr = "loading...";
    protected int defColor = Color.rgb(220, 220, 220);     //底色


    protected Paint paint;
    protected Paint paintEffect;
    protected Paint paintLabel;

    /**动画相关统一属性，也可以设置，需要写set方法*/
    protected long animDuration = 1000;
    protected ValueAnimator anim;
    protected boolean startDraw = false;

    /**正在加载*/
    protected boolean isLoading = true;
    protected boolean drawLine = true;
    protected boolean drawBottomLine = true;
    protected boolean debug = BuildConfig.DEBUG;

    /**手指抬起后，动画*/
    protected ValueAnimator touchAnim;
    /**
     * GestureDetector 检测各种手势和事件
     */
    protected GestureDetector mGestureDetector;

    public void setLoading(boolean loading) {
        isLoading = loading;
        invalidate();
    }
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public BaseChart(Context context) {
        this(context, null);
    }

    public BaseChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        init(context, attrs, defStyle);
    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
    }

    public void init() {
        TAG = getClass().getSimpleName();

        DisplayMetrics dm = getResources().getDisplayMetrics();
        ScrHeight = dm.heightPixels;
        ScrWidth = dm.widthPixels;

        //画笔初始化
        paint = new Paint();
        paint.setAntiAlias(true);

        paintLabel = new Paint();
        paintLabel.setAntiAlias(true);

        paintEffect = new Paint();
        paintEffect.setAntiAlias(true);
        paintEffect.setStyle(Paint.Style.FILL);
        paintEffect.setStrokeWidth(lineWidth);
        paintEffect.setColor(Color.RED);

        mGestureDetector = new GestureDetector(getContext(), new MyOnGestureListener());

    }

    public abstract void init(Context context, AttributeSet attrs, int defStyleAttr);
    public int getTotal() {
        return total;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        centerPoint = new PointF(getMeasuredWidth()/2, getMeasuredHeight()/2);
        rectChart = new RectF(getPaddingLeft(),getPaddingTop(),getMeasuredWidth()-getPaddingRight(),
                getMeasuredHeight()-getPaddingBottom());
    }
    protected PointF lastTouchPoint;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mGestureDetector.onTouchEvent(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchPoint = new PointF(event.getX(), event.getY());
                return true;
            case MotionEvent.ACTION_MOVE:
                int move = 0;
                onMove(event.getX() - lastTouchPoint.x, event.getY() - lastTouchPoint.y);
                lastTouchPoint.x = (int)event.getX();
                lastTouchPoint.y = (int)event.getY();
                evaluatorFling(move);

//                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                lastTouchPoint.x = 0;
                lastTouchPoint.y = 0;
                return true;
        }
        return result;
    }
    class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        //手指按下
        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }
        //按压事件
        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }
        //长按
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }
        //手指抬起
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }
        //
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            Log.w(TAG, "===================滚动");
//            Log.w(TAG, "event1："+e1.getAction()+"  "+e1.getX()+"  "+e1.getY());
//            Log.w(TAG, "event2："+e2.getAction()+"  "+e2.getX()+"  "+e2.getY());
//            Log.w(TAG, "distanceX："+distanceX+"   distanceY="+distanceY);
            return true;
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            LogUtil.e(TAG,"onFling------------>velocityX="+velocityX+"    velocityY="+velocityY);
            return true;
        }
    }

    //Scroller本身不会去移动View，它只是一个移动计算辅助类，用于跟踪控件滑动的轨迹，只相当于一个滚动轨迹记录工具，最终还是通过View的scrollTo、scrollBy方法完成View的移动的
//    Scroller scroller = new Scroller();

    /**滑动*/
    protected void onMove(float x, float y){
        Log.w(TAG, "===================滑动了"+x+"  "+y);
    }

    public void onDraw(Canvas canvas){
        //画布背景
//        canvas.drawColor(backColor);
        if(debug) {
            drawDebug(canvas);
        }
        drawDefult(canvas);
        if(isLoading){
            drawLoading(canvas);
            return;
        }
        drawChart(canvas);
//        if(!startDraw){
//            startDraw = true;
//            startAnimation(canvas);
//        }else{
//            drawChart(canvas);
//        }
    }

    public void drawLoading(Canvas canvas) {
        paintLabel.setTextSize(35);
        float NullTextLead = FontUtil.getFontLeading(paintLabel);
        float NullTextHeight = FontUtil.getFontHeight(paintLabel);
        float textY = centerPoint.y-NullTextHeight/2+NullTextLead;
        paintLabel.setColor(getContext().getResources().getColor(R.color.text_color_def));
        canvas.drawText(loadingStr, centerPoint.x- FontUtil.getFontlength(paintLabel, loadingStr)/2,  textY, paintLabel);
    }

    /**绘制图表基本框架*/
    public abstract void drawDefult(Canvas canvas);
    /**绘制debug辅助线*/
    public void drawDebug(Canvas canvas){
        paint.setStyle(Paint.Style.STROKE);//设置空心
        paint.setStrokeWidth(lineWidth);
        //绘制边界--chart区域
        paint.setColor(Color.BLUE);
        RectF r = new RectF(0,0,getMeasuredWidth(), getMeasuredHeight());
        canvas.drawRect(r, paint);
        paint.setColor(Color.RED);
        canvas.drawRect(rectChart, paint);
    }
    /**绘制图表*/
    public abstract void drawChart(Canvas canvas);
    /**创建动画*/
    protected abstract ValueAnimator initAnim();
    /**动画值变化之后计算数据*/
    protected abstract void evaluatorData(ValueAnimator animation);

    public void setAnimDuration(long duration){
        this.animDuration = duration;
    }
    protected void startAnimation(Canvas canvas) {
        if(anim!=null){
            anim.cancel();
        }
        LogUtil.w(TAG, "开始绘制动画");
        anim = initAnim();
        if(anim==null){
            drawChart(canvas);
        }else{
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.addUpdateListener((ValueAnimator animation)->{
                evaluatorData(animation);
                invalidate();
            });
            anim.setDuration(animDuration);
            anim.start();
        }
    }


    /**动画值变化之后计算数据*/
    protected void evaluatorFling(float velocityAnim){}
    protected int mMoveLen = 0;       //滚动距离
    protected boolean moveOver = false; //是否滚动到头了
    /**手指松开后滑动动画效果*/
    protected void startFlingAnimation(float velocity) {
        if(touchAnim!=null){
            touchAnim.cancel();
        }
        touchAnim = ValueAnimator.ofObject(new AngleEvaluator(), 1f, 0f);
        touchAnim.setInterpolator(new DecelerateInterpolator());   //越来越慢
        touchAnim.addUpdateListener((ValueAnimator animation)->{
            float velocityAnim = (float)animation.getAnimatedValue();
            evaluatorFling(velocity /100 * velocityAnim);
            invalidate();
        });
        touchAnim.setDuration(1000+(int)+Math.abs(velocity)/4);
        touchAnim.start();
    }






}
