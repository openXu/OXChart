package com.openxu.chart.loading;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * https://github.com/81813780/AVLoadingIndicatorView/blob/master/library/src/main/java/com/wang/avi/Indicator.java
 *
 *
 */
public abstract class LoadingIndicator extends Drawable implements Animatable {


    protected  String TAG = getClass().getSimpleName();
    protected List<ValueAnimator> mAnimators;
    private HashMap<ValueAnimator,ValueAnimator.AnimatorUpdateListener> mUpdateListeners;
//    protected Rect drawBounds;

    private Paint mPaint;

    public LoadingIndicator(Context context) {
        mUpdateListeners = new HashMap<>();
//        drawBounds = new Rect();
        mPaint=new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas) {
        draw(canvas,mPaint);
    }
    /**绘制loading*/
    protected abstract void draw(Canvas canvas, Paint paint);

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        //没有透明格式
        return PixelFormat.OPAQUE;
    }







    public abstract ArrayList<ValueAnimator> onCreateAnimators();
    /**
     *  Your should use this to add AnimatorUpdateListener when
     *  create animator , otherwise , animator doesn't work when
     *  the animation restart .
     * @param updateListener
     */
    public void addUpdateListener(ValueAnimator animator, ValueAnimator.AnimatorUpdateListener updateListener){
        mUpdateListeners.put(animator,updateListener);
    }
    @Override
    public void start() {
        if(mAnimators==null || mAnimators.size()<=0)
            mAnimators = onCreateAnimators();
        if (mAnimators == null)
            return;
        // If the animators has not ended, do nothing.
        if (isStarted()) {
            return;
        }
        Log.w(TAG, "执行加载动画");
        for (int i = 0; i < mAnimators.size(); i++) {
            ValueAnimator animator = mAnimators.get(i);
            //when the animator restart , add the updateListener again because they
            // was removed by animator stop .
            ValueAnimator.AnimatorUpdateListener updateListener=mUpdateListeners.get(animator);
            if (updateListener!=null)
                animator.addUpdateListener(updateListener);
            animator.start();
        }
    }

    private boolean isStarted() {
        for (ValueAnimator animator : mAnimators)
            return animator.isStarted();
        return false;
    }

    @Override
    public boolean isRunning() {
        for (ValueAnimator animator : mAnimators)
            return animator.isRunning();
        return false;
    }

    @Override
    public void stop() {
        if (mAnimators!=null){
            for (ValueAnimator animator : mAnimators) {
                if (animator != null && animator.isStarted()) {
                    animator.removeAllUpdateListeners();
                    animator.end();
                }
            }
        }
    }

}
