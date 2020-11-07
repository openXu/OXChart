package com.openxu.chart.loading;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.openxu.utils.DensityUtil;

import java.util.ArrayList;

public class BallPulseIndicator extends LoadingIndicator {

    private float loadRadius;
    private float loadCircleSpace;
    private float loadingAnimValueMax = 1;
    private float loadingAnimValueMin = 0.4f;
    private int[] loadCircleColors = new int[]{
            Color.parseColor("#DB4528"),
            Color.parseColor("#5f93e7"),
            Color.parseColor("#fda33c")};
    private float[] scaleFloats = new float[]{loadingAnimValueMax, loadingAnimValueMax, loadingAnimValueMax};

    public BallPulseIndicator(Context context) {
        super();
        loadRadius = DensityUtil.dip2px(context, 5);
        loadCircleSpace = DensityUtil.dip2px(context, 4);
    }


    @Override
    public void draw(Canvas canvas, Paint paint) {

        Rect rect = getBounds();
        paint.setStyle(Paint.Style.FILL);
        for(int i = 0; i<loadCircleColors.length; i++){
            paint.setColor(loadCircleColors[i]);
            canvas.drawCircle(rect.left+rect.width()/2 - loadRadius*2 - loadCircleSpace + i*(loadRadius*2 + loadCircleSpace),
                    rect.top+rect.height()/2,
                    loadRadius*scaleFloats[i],paint);
        }
       /* float circleSpacing = 4;
        float radius=(rect.width()-circleSpacing*(loadCircleColors.length-1))/(loadCircleColors.length*2);
        float x = rect.width()/ 2-(radius*2+circleSpacing);
        float y = rect.height() / 2;
        for (int i = 0; i < loadCircleColors.length; i++) {
            paint.setColor(loadCircleColors[i]);
            canvas.save();
            float translateX=x+(radius*2)*i+circleSpacing*i;
            canvas.translate(translateX, y);
            canvas.scale(scaleFloats[i], scaleFloats[i]);
            canvas.drawCircle(0, 0, radius, paint);
            canvas.restore();
        }*/
    }

    @Override
    public ArrayList<ValueAnimator> onCreateAnimators() {
        ArrayList<ValueAnimator> animators=new ArrayList<>();
        int[] delays=new int[]{120,240,360};
        for (int i = 0; i < 3; i++) {
            final int index=i;
            ValueAnimator scaleAnim=ValueAnimator.ofFloat(loadingAnimValueMax, loadingAnimValueMin,loadingAnimValueMax);
            scaleAnim.setDuration(900);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.setStartDelay(delays[i]);

            addUpdateListener(scaleAnim, animation -> {
                scaleFloats[index] = (float) animation.getAnimatedValue();
//                Log.w(TAG, index + "刷新loading "+scaleFloats[index]);
                invalidateSelf();
            });
            animators.add(scaleAnim);
        }
        return animators;
    }


}