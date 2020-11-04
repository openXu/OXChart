package com.openxu.cview.xmstock20201030;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
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
    //设置线条颜色
    private int[] barColor = new int[]{Color.parseColor("#3d7cc9"),
            Color.parseColor("#d74b3c")};
    private int bitmapID = R.mipmap.proicon;
    private int barSize = DensityUtil.dip2px(getContext(), 1.5f);
    private float total;
    private float progress;

    private int space = DensityUtil.dip2px(getContext(), 5);
    //计算
    private Bitmap bitmap;
    private PointF bitmapCenter;
    private int bigRadios;   //图片底部圆形半径
    private int paddingBottom;  //居中时需要用到的，图片与barsize差值


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
        bitmap = BitmapFactory.decodeResource(getResources(), bitmapID);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        bigRadios = bitmap.getWidth()/2;
        paddingBottom = bitmap.getHeight() - barSize;
        heightSize = bitmap.getHeight() + paddingBottom;
        bitmapCenter = new PointF(bigRadios, heightSize);
        setMeasuredDimension(widthSize, heightSize);
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
        canvas.drawBitmap(bitmap, rectChart.left, rectChart.top, paint);

        RectF rectF = new RectF(
                bitmapCenter.x -bigRadios - space,
                bitmapCenter.y -bigRadios - space,
                bitmapCenter.x +bigRadios + space,
                bitmapCenter.y +bigRadios + space
        );
        //添加路径信息
        Path path = new Path();
        //将指定的圆弧作为新轮廓添加到路径中
//        path.addArc(rectF, 90, 180);
//        blackPath.moveTo(center, 0);
//        blackPath.arcTo(smallTopRectF, 270, 180);
//        blackPath.moveTo(center, center);
//        blackPath.arcTo(smallBottomRectF, 270, -180);
//        //将黑色路径信息存入Region
//        Region region = new Region();
//        blackRegion.setPath(blackPath, totalRegion);

    }
    @Override
    public void drawChart(Canvas canvas) {
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
