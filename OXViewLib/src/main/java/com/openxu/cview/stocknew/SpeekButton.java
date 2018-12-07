package com.openxu.cview.stocknew;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.LogUtil;

public class SpeekButton  extends View {

    private String TAG = "SpeekButton";

    private int speekIcon = R.mipmap.icon_speek;
    //中间圆圈颜色
    private int color_bg = Color.parseColor("#139fff");
    //圆环色
    private int color_ring = Color.parseColor("#5fb0f3");
    //圆圈半径
    protected int radius = DensityUtil.dip2px(getContext(), 45);
    protected int ringSize = DensityUtil.dip2px(getContext(), 1);
    protected int btnSize = DensityUtil.dip2px(getContext(), 180);

    private Bitmap bitmap;
    protected Paint paint, ringPaint;


    public SpeekButton(Context context) {
        this(context, null);
    }
    public SpeekButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public SpeekButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        bitmap = BitmapFactory.decodeResource(getResources(), speekIcon);
        paint = new Paint();
        paint.setAntiAlias(true);
        ringPaint = new Paint();
        ringPaint.setAntiAlias(true);
    }

    private Listener listener;
    public void setListener(Listener listener){
        this.listener = listener;
    }
    public interface Listener{
        public void start();
        public void stop();
        public void progress(int time);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(btnSize, btnSize);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(event);
    }

    private boolean onTouched = false;
    private long startTime;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if(inCircle(event.getX(), event.getY())){
                    onTouched = true;
                    startTime = System.currentTimeMillis();
                    if(listener!=null)
                        listener.start();
                    startAnimation();
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if(!inCircle(event.getX(), event.getY()) && onTouched)
                    stop();
                return true;
            case MotionEvent.ACTION_UP:
                if(onTouched)
                    stop();
                return true;
        }
        return true;
    }

    /**判断手指是否在圆环内*/
    private boolean inCircle(float x, float y){
        //点击位置x坐标与圆心的x坐标的距离
        int distanceX = (int)Math.abs(x - btnSize/2);
        //点击位置y坐标与圆心的y坐标的距离
        int distanceY = (int)Math.abs(y - btnSize/2);
        //点击位置与圆心的直线距离
        int distance = (int) Math.sqrt(Math.pow(distanceX,2)+Math.pow(distanceY,2));
//        LogUtil.w(TAG, "距离distance："+distance+"   半径："+radius);
        //如果点击位置与圆心的距离大于圆的半径，证明点击位置没有在圆内
        return distance<=radius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(color_bg);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(btnSize/2, btnSize/2, radius, paint);
        //绘制图片
        canvas.drawBitmap(bitmap, btnSize/2 - bitmap.getWidth()/2,
                btnSize/2 - bitmap.getHeight()/2, paint);
        if(!onTouched){
            //正常情况下绘制半透明圆环
            paint.setColor(color_ring);
            int w = DensityUtil.dip2px(getContext(), 3);
            paint.setStrokeWidth(w);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(btnSize/2, btnSize/2, radius+w/2, paint);
        }else{
            //半透明实心圆环  大圆环  小圆环
//            LogUtil.w(TAG, animEnd1+"   "+animEnd2+"   "+animEnd3);
            if(!animEnd1){
                ringPaint.setColor(color_ring);
                float w = (btnSize/2 - radius) * animPro1;
                ringPaint.setStrokeWidth(w);
                ringPaint.setStyle(Paint.Style.STROKE);
                ringPaint.setAlpha((int)(255 - 255*animPro1));
                canvas.drawCircle(btnSize/2, btnSize/2, radius+w/2, ringPaint);
            }
            if(!animEnd2){
                ringPaint.setColor(color_ring);
                ringPaint.setStrokeWidth(ringSize);
                ringPaint.setStyle(Paint.Style.STROKE);
                ringPaint.setAlpha((int)(255 - 255*animPro2));
                canvas.drawCircle(btnSize/2, btnSize/2, radius + (btnSize/2 - radius - ringSize/2) * animPro2, ringPaint);
            }
            if(!animEnd3){
                ringPaint.setColor(color_ring);
                ringPaint.setStrokeWidth(ringSize);
                ringPaint.setStyle(Paint.Style.STROKE);
                ringPaint.setAlpha((int)(255 - 255*animPro3));
                canvas.drawCircle(btnSize/2, btnSize/2, radius + (btnSize/2 - radius)/2 * animPro3, ringPaint);
            }
        }
    }

    private float animPro1, animPro2, animPro3;
    private boolean animEnd1, animEnd2, animEnd3;
    AnimatorSet animSet;
    protected void startAnimation() {
        if(animSet!=null){
            animSet.cancel();
        }
        animSet= new AnimatorSet();
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
//                LogUtil.i(TAG, "animSet  onAnimationStart");
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                LogUtil.i(TAG, "animSet  onAnimationEnd");
                if(onTouched)
                    animSet.start();
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
//                LogUtil.i(TAG, "animSet  onAnimationRepeat");
//                animType = (animType+1)%3;
            }
        });
        ValueAnimator anim1 = ValueAnimator.ofObject(new AngleEvaluator(), 0f, 1f);
        anim1.setInterpolator(new LinearInterpolator());  //匀速
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animPro1 = (float)animation.getAnimatedValue();
                if(listener!=null && onTouched)
                    listener.progress((int)(System.currentTimeMillis() - startTime));
                invalidate();
            }
        });
        anim1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
//                LogUtil.i(TAG, "11111   onAnimationStart");
                animEnd1 = false;
            }
            @Override
            public void onAnimationEnd(Animator animation) {
//                LogUtil.i(TAG, "11111   onAnimationEnd");
                animEnd1 = true;
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        anim1.setDuration(animDuration1);
//        anim1.setRepeatCount(ValueAnimator.INFINITE);//无限循环

        ValueAnimator anim2 = ValueAnimator.ofObject(new AngleEvaluator(), 0f, 1f);
        anim2.setInterpolator(new DecelerateInterpolator());//其变化开始速率较快，后面减速
        anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animPro2 = (float)animation.getAnimatedValue();
                if(listener!=null && onTouched)
                    listener.progress((int)(System.currentTimeMillis() - startTime));
                invalidate();
            }
        });
        anim2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
//                LogUtil.i(TAG, "-----22222   onAnimationStart");
                animEnd2 = false;
            }
            @Override
            public void onAnimationEnd(Animator animation) {
//                LogUtil.i(TAG, "-----22222   onAnimationEnd");
                animEnd2 = true;
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        anim2.setDuration(animDuration2);

        ValueAnimator anim3 = ValueAnimator.ofObject(new AngleEvaluator(), 0f, 1f);
        anim3.setInterpolator(new DecelerateInterpolator());//其变化开始速率较快，后面减速
        anim3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animPro3 = (float)animation.getAnimatedValue();
                if(listener!=null && onTouched)
                    listener.progress((int)(System.currentTimeMillis() - startTime));
                invalidate();
            }
        });
        anim3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
//                LogUtil.i(TAG, "----------33333   onAnimationStart");
                animEnd3 = false;
            }
            @Override
            public void onAnimationEnd(Animator animation) {
//                LogUtil.i(TAG, "-----------33333   onAnimationEnd");
                animEnd3 = true;
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        anim3.setDuration(animDuration3);
//        anim2.setRepeatCount(ValueAnimator.INFINITE);//无限循环

        //这是2个空的动画，主要是控制800毫秒的动画间隔时间的，等下贴出代码
        ValueAnimator nullAnim1 = ValueAnimator.ofFloat (1.0F , 0F );
        ValueAnimator nullAnim2 = ValueAnimator.ofFloat (1.0F , 0F );
        nullAnim1.setDuration(800) ;
        nullAnim2.setDuration(350) ;
        animSet.play(anim1).with(nullAnim1) ;
        animSet.play(nullAnim1).before(anim2) ;
        animSet.play(nullAnim1).before(nullAnim2) ;
        animSet.play(nullAnim2).with(anim2) ;
        animSet.play(nullAnim2).before(anim3) ;
        animSet.start();
    }
    private int animDuration1 = 1000;
    private int animDuration2 = 800;
    private int animDuration3 = 1000;

    public void stop(){
        onTouched = false;
        if(animSet!=null)
            animSet.cancel();
        if(listener!=null)
            listener.stop();
        invalidate();
    }

}
