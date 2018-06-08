package com.openxu.cview.chart.anim;


import android.animation.TypeEvaluator;

/**
 * autour : openXu
 * date : 2017/7/28 9:12
 * className : AngleEvaluator
 * version : 1.0
 * description : 角度计算器
 */
public class AngleEvaluator implements TypeEvaluator {

    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        float start = (float)startValue;
        float end = (float)endValue;
        return start+fraction*(end-start);
    }

}
