package com.openxu.chart.bar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.chart.BaseChart;
import com.openxu.cview.R;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.chart.bean.BarBean;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * autour : openXu
 * date : 2017/7/24 10:46
 * className : BarChart
 * version : 1.0
 * description : 柱状图，支持多柱
 */
public class BarChart extends BaseChart {

    private List<List<BarBean>> dataList;
    private List<String> strList;

    /**
     * 可以设置的属性
     */

    private boolean showLable = true;   //是否显示下面的lable

    private boolean showEnd = true;    //当数据超出一屏宽度时，实现最后的数据

    private int barNum = 2;   //柱子数量
    private int YMARK_NUM = 5;    //Y轴刻度数量

    private int barWidth = DensityUtil.dip2px(getContext(), 15);    //柱宽度
    private int barSpace = DensityUtil.dip2px(getContext(), 1);    //双柱间的间距
    private int barItemSpace = DensityUtil.dip2px(getContext(), 25);//一组柱之间的间距
    private int[] barColor = new int[]{Color.BLUE, Color.YELLOW};               //柱颜色

    private int rectW = DensityUtil.dip2px(getContext(), 10);   //lable矩形宽高
    private int rectH = DensityUtil.dip2px(getContext(), 10);

    private int textSizeCoordinate = (int) getResources().getDimension(R.dimen.text_size_level_small); //坐标文字大小
    private int textColorCoordinate = getResources().getColor(R.color.text_color_light_gray);
    private int textSizeTag = (int) getResources().getDimension(R.dimen.text_size_level_small); //数值字体
    private int textColorTag = getResources().getColor(R.color.text_color_light_gray);
    private int textSizeLable = (int) getResources().getDimension(R.dimen.text_size_level_small); //lable字体
    private int textColorLable = getResources().getColor(R.color.text_color_light_gray);

    private int textSpace = DensityUtil.dip2px(getContext(), 3);         //默认的字与其他的间距
    private int textLableSpace = DensityUtil.dip2px(getContext(), 10);   //默认的lable字与其他的间距
    private int lableItemSpace = DensityUtil.dip2px(getContext(), 30);   //默认的lable字与其他的间距
    private int lableTopSpace = DensityUtil.dip2px(getContext(), 20);   //默认的lable字与其他的间距

    /**
     * 需要计算相关值
     */
    private int oneBarW;            //单个宽度,需要计算
    private int YMARK_MAX_WIDTH;    //Y轴刻度最大值的宽度
    private int YMARK_H, YMARK_ALL_H;            //Y轴刻度间距值
    private int leftStartPointX;    //从左侧开始绘制的X坐标
    private int minLeftPointX;      //滑动到最右侧时的X坐标

    private PointF zeroPoint = new PointF();    //柱状图图体圆点坐标

    private RectF lableRect;

    /*字体绘制相关*/
    private int YMARK = 1;    //Y轴刻度最大值（根据设置的数据自动赋值）
    private int YMARK_MAX = 1;    //Y轴刻度最大值（根据设置的数据自动赋值）

    private int heightCoordinate;
    private int leadCoordinate;
    private int heightTag;
    private int leadTag;
    private int heightLable;
    private int leadLable;
    private float animPro;       //动画计算的占比数量

    public BarChart(Context context) {
        this(context, null);
    }

    public BarChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        dataList = new ArrayList<>();
        strList = new ArrayList<>();
    }

    /***********************************设置属性set方法over**********************************/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    public void drawChart(Canvas canvas) {

    }


}
