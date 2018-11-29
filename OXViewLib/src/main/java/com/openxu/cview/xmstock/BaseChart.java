package com.openxu.cview.xmstock;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.xmstock.bean.Constacts;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * autour : xiami
 * date : 2018/3/11 9:13
 * className : BaseChart
 * version : 1.0
 * description : 图表基类
 */
public abstract class BaseChart extends View {

    protected String TAG;
    protected int ScrWidth,ScrHeight;   //屏幕宽高
    protected RectF rectChart;          //图表矩形
    protected PointF centerPoint;       //chart中心点坐标

    protected Paint paint;
    protected Paint paintEffect;
    protected Paint paintLabel;

    /**可设置属性*/
    protected int backColor = Color.WHITE;
    //辅助线宽度
    protected int lineWidth = DensityUtil.dip2px(getContext(), 0.7f);
    //正在加载
    protected boolean isLoading = true;
    protected String loadingStr = "loading...";
    protected int defColor = Color.rgb(220, 220, 220);     //底色

    protected  boolean touchEnable = true;  //是否能获取事件
    protected boolean onFocus = false;      //是否正处于触摸焦点状态
    protected int focusPreTime = 500;       //获取焦点长按时间
    /**动画相关统一属性，也可以设置，需要写set方法*/
    protected long animDuration = 1000;
    protected ValueAnimator anim;
    protected boolean startDraw = false;

    public void setLoading(boolean loading) {
        isLoading = loading;
        invalidate();
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    protected Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 100) {
                onFocus = true;
                onTouchMoved(lastTouchPoint);
                Vibrator vibrator = (Vibrator) getContext().getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(50);
            }
        }
    };

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

        paint = new Paint();
        paint.setAntiAlias(true);

        paintEffect = new Paint();
        paintEffect.setAntiAlias(true);
        paintEffect.setStyle(Paint.Style.FILL);
        paintEffect.setStrokeWidth(lineWidth);
        paintEffect.setColor(Color.RED);

        paintLabel = new Paint();
        paintLabel.setAntiAlias(true);
    }

    public abstract void init(Context context, AttributeSet attrs, int defStyleAttr);
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        onMeasureAfter();
    }

    protected void onMeasureAfter(){
        centerPoint = new PointF(getMeasuredWidth()/2, getMeasuredHeight()/2);
        rectChart = new RectF(getPaddingLeft(),getPaddingTop(),getMeasuredWidth()-getPaddingRight(),
                getMeasuredHeight()-getPaddingBottom());
    }

    protected PointF downPoint = new PointF();
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downPoint.x = event.getX();
                downPoint.y = event.getY();
                if(touchEnable) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    handler.sendEmptyMessageDelayed(100, focusPreTime);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(!onFocus){
                    if(Math.abs(event.getY()-downPoint.y)>20 &&
                            Math.abs(event.getY()-downPoint.y)> Math.abs(event.getX() - downPoint.x)) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                        handler.removeMessages(100);
                        onFocus = false;
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }else
                    getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                handler.removeMessages(100);
                onFocus = false;
                LogUtil.e(TAG, "事件结束");
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    protected PointF lastTouchPoint;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!touchEnable)
            return false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchPoint = new PointF(event.getX(), event.getY());
                if(onFocus)
                    onTouchMoved(lastTouchPoint);
                return true;
            case MotionEvent.ACTION_MOVE:
                int move = (int)(event.getX() - lastTouchPoint.x);
                LogUtil.i(TAG, "MotionEvent.ACTION_MOVE"+move);
                lastTouchPoint.x = (int)event.getX();
                lastTouchPoint.y = (int)event.getY();
                if(onFocus)
                    onTouchMoved(lastTouchPoint);
                return true;
            case MotionEvent.ACTION_UP:
                lastTouchPoint.x = 0;
                lastTouchPoint.y = 0;
                onTouchMoved(null);
                return true;
        }
        return true;
    }

    protected void onTouchMoved(PointF point){
    }


    public void onDraw(Canvas canvas){
        //画布背景
//        canvas.drawColor(backColor);
        if(Constacts.DEBUG) {
            drawDebug(canvas);
        }
        drawDefult(canvas);
        if(isLoading){
            drawLoading(canvas);
            return;
        }
        if(!startDraw){
            startDraw = true;
            startAnimation(canvas);
        }else{
            drawChart(canvas);
        }
    }

    public void drawLoading(Canvas canvas) {
        paintLabel.setTextSize(35);
        float NullTextLead = FontUtil.getFontLeading(paintLabel);
        float NullTextHeight = FontUtil.getFontHeight(paintLabel);
        float textY = centerPoint.y-NullTextHeight/2+NullTextLead;
        paintLabel.setColor(getContext().getResources().getColor(R.color.tc_chart_xy));
        canvas.drawText(loadingStr, centerPoint.x- FontUtil.getFontlength(paintLabel, loadingStr)/2,  textY, paintLabel);
    }

    /**绘制图表基本框架*/
    public abstract void drawDefult(Canvas canvas);
    /**绘制debug辅助线*/
    public void drawDebug(Canvas canvas){
        paint.setStyle(Paint.Style.STROKE);
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
        anim = initAnim();
        if(anim==null){
            drawChart(canvas);
        }else{
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    evaluatorData(animation);
                    invalidate();
                }
            });
            anim.setDuration(animDuration);
            anim.start();
        }
    }



}
