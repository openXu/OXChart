package com.openxu.cview.xmstock20191205;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author: openXu
 * Time: 2020/4/9 14:13
 * class: OilTableLine
 * Description:
 */
public class OilTableLine extends View {
    public static final int[] SWEEP_GRADIENT_COLORS = new int[]{Color.GREEN, Color.GREEN, Color.BLUE, Color.RED, Color.RED};
    private int tableWidth = 50;
    private Paint mPaint;
    private Path mPath;
    private RectF mTableRectF;
    //把路径分成虚线段的
    private DashPathEffect dashPathEffect;
    //给路径上色
    private SweepGradient mColorShader;
    //指针的路径
    private Path mPointerPath;
    private float mCurrentDegree = 60;

    public OilTableLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPath = new Path();
        mPointerPath = new Path();
        startAnimator();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float size = Math.min(w, h) - tableWidth * 2;
        //油表的位置方框
        mTableRectF = new RectF(0, 0, size, size);
        mPath.reset();
        //在油表路径中增加一个从起始弧度
        mPath.addArc(mTableRectF, 60, 240);
        //计算路径的长度
        PathMeasure pathMeasure = new PathMeasure(mPath, false);
        float length = pathMeasure.getLength();
        float step = length / 60;
        dashPathEffect = new DashPathEffect(new float[]{step / 3, step * 2 / 3}, 0);

        float radius = size / 2;
        mColorShader = new SweepGradient(radius, radius,SWEEP_GRADIENT_COLORS,null);
        //设置指针的路径位置
        mPointerPath.reset();
        mPointerPath.moveTo(radius, radius - 20);
        mPointerPath.lineTo(radius, radius + 20);
        mPointerPath.lineTo(radius * 2 - tableWidth, radius);
        mPointerPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float dx = (getWidth() - mTableRectF.width()) / 2;
        float dy = (getHeight() - mTableRectF.height()) / 2;
        //把油表的方框平移到正中间
        canvas.translate(dx, dy);
        canvas.save();
        //旋转画布
        canvas.rotate(90, mTableRectF.width() / 2, mTableRectF.height() / 2);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(tableWidth);
        mPaint.setPathEffect(dashPathEffect);
        mPaint.setShader(mColorShader);
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
        //还原画笔
        mPaint.setPathEffect(null);
        mPaint.setShader(null);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(tableWidth / 10);
        canvas.save();
        canvas.rotate(150 + mCurrentDegree, mTableRectF.width() / 2, mTableRectF.height() / 2);
        canvas.drawPath(mPointerPath, mPaint);
        canvas.restore();
    }

    public void startAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 240);
        animator.setDuration(40000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentDegree = (int) (0 + (Float) animation.getAnimatedValue());
                invalidate();
            }
        });
        animator.start();
    }
}
