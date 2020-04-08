package com.openxu.cview.xmstock20191205;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.xmstock.BaseChart;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;

/**
 * 仪表盘图表
 */
public class DashboardView extends BaseChart {

    private int total;           //总数量
    private int progress;        //占比数量
    private float animPro;       //动画计算的占比数量
    //文字大小颜色
    private int centerTextSize = (int)getResources().getDimension(R.dimen.ts_chart_lable);
    private int centerTextColor = Color.parseColor("#d55942");
    private int nameTextSize = (int)getResources().getDimension(R.dimen.text_size_level_def);
    private int nameTextColor = Color.parseColor("#dc3929");
    private int lableTextSize = (int)getResources().getDimension(R.dimen.ts_chart_lable);
    private int lableTextColor = Color.parseColor("#e2978a");
    private int lableSpace = DensityUtil.dip2px(getContext(), 5);
    //渐变色
    int[] colors = new int[]{Color.parseColor("#e7a255"),Color.parseColor("#db3622")};
    //渐变色分布
    float[] positions = new float[]{0,.5F};

    private int startAngle = 30;   //开始角度
    private int dashesSize = DensityUtil.dip2px(getContext(), 20);   //虚线宽度
    private int solidSize = DensityUtil.dip2px(getContext(), 10);     //实线宽度
    private int rSpace1 = DensityUtil.dip2px(getContext(), 20);      //中心白圈与 实线间隙
    private int rSpace2 = DensityUtil.dip2px(getContext(), 4);       //虚线 实线间隙
    private String nameText = "能量";
    private String lableLeft = "0";
    private String lableRight = "一万亿";

    /**计算*/
    private int centerRaidus;    //中间白色圆圈半径
    private int chartRaidus;          //半径
    protected RectF rectDashes;       //虚线扇形矩形框
    protected RectF rectSolid;       //实线扇形矩形框
    private PointF startPointLeft, startPointRight;
    private PointF startPointDashes, endPointDashes;
    private PointF startPointSolid, endPointSolid;

    public DashboardView(Context context) {
        super(context, null);
    }
    public DashboardView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }
    public DashboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(widthMeasureSpec);
        LogUtil.i(TAG, "建议宽高："+widthSize + " * "+heightSize);
        //根据中间文字大小计算白色圆圈半径
        paintLabel.setTextSize(centerTextSize);
        int textWidth = (int)FontUtil.getFontlength(paintLabel, "10000");
        int textHeight = (int)FontUtil.getFontHeight(paintLabel);
        centerRaidus = textWidth>textHeight*2?textWidth:textHeight*2 + DensityUtil.dip2px(getContext(), 5);
        //最大半径
        chartRaidus = centerRaidus + rSpace1 + solidSize + rSpace2 + dashesSize;
        paintLabel.setTextSize(lableTextSize);
        LogUtil.d(TAG, "centerRaidus："+centerRaidus);
        LogUtil.d(TAG, "chartRaidus："+chartRaidus);
        //计算控件宽高
        int r = chartRaidus;
        int sectionRaidus = (int)(Math.cos(Math.toRadians(startAngle)) * r);//下切面高度  Math.toRadians将角度转换成弧度值
        widthSize = getPaddingLeft() + getPaddingRight() + chartRaidus*2;
        heightSize =  getPaddingTop()+getPaddingBottom() + chartRaidus + sectionRaidus + lableSpace + (int)FontUtil.getFontHeight(paintLabel);
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
        LogUtil.i(TAG, "宽高："+getMeasuredWidth() + " * "+getMeasuredHeight());
        centerPoint = new PointF(getPaddingLeft()+chartRaidus, getPaddingTop()+chartRaidus);
        rectChart = new RectF(getPaddingLeft(),getPaddingTop(),
                getMeasuredWidth()-getPaddingRight(), getPaddingTop()+chartRaidus*2);
        int addRaidus = centerRaidus+rSpace1+solidSize/2;
        rectSolid = new RectF(centerPoint.x-addRaidus,centerPoint.y-addRaidus,
                centerPoint.x+addRaidus, centerPoint.y+addRaidus);
        addRaidus = centerRaidus+rSpace1+solidSize +rSpace2+dashesSize/2;
        rectDashes = new RectF(centerPoint.x-addRaidus,centerPoint.y-addRaidus,
                centerPoint.x+addRaidus, centerPoint.y+addRaidus);

        //lable绘制开始和结束点
        startPointLeft = new PointF((int)(centerPoint.x-Math.sin(Math.toRadians(startAngle))*r), centerPoint.y+sectionRaidus);
        startPointRight = new PointF((int)(centerPoint.x+Math.sin(Math.toRadians(startAngle))*r), centerPoint.y+sectionRaidus);
        // 虚线扇形绘制开始和结束点
        r = chartRaidus-dashesSize-dashesSize/2;
        sectionRaidus = (int)(Math.cos(Math.toRadians(startAngle)) * r);//下切面高度
        startPointDashes = new PointF((int)(centerPoint.x-Math.sin(Math.toRadians(startAngle))*r), centerPoint.y+sectionRaidus);
        endPointDashes = new PointF((int)(centerPoint.x+Math.sin(Math.toRadians(startAngle))*r), centerPoint.y+sectionRaidus);
        // 实线扇形绘制开始和结束点
        r = chartRaidus-dashesSize-rSpace2-solidSize/2;
        sectionRaidus = (int)(Math.cos(Math.toRadians(startAngle)) * r);//下切面高度
        startPointSolid = new PointF((int)(centerPoint.x-Math.sin(Math.toRadians(startAngle))*r), centerPoint.y+sectionRaidus);
        endPointSolid = new PointF((int)(centerPoint.x+Math.sin(Math.toRadians(startAngle))*r), centerPoint.y+sectionRaidus);
        LogUtil.d(TAG, "centerPoint："+centerPoint);
        LogUtil.d(TAG, "rectChart："+rectChart);
        invalidate();
    }
    public void setData(int total, int progress){
        this.total = total;
        this.progress = progress;
        startDraw = false;
        invalidate();
    }

    @Override
    public void drawDebug(Canvas canvas) {
        super.drawDebug(canvas);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rectChart.left, rectChart.top, rectChart.right, rectChart.bottom, paint);
        paint.setColor(Color.BLUE);
        canvas.drawRect(rectDashes.left, rectDashes.top, rectDashes.right, rectDashes.bottom, paint);
        paint.setColor(Color.RED);
        canvas.drawRect(rectSolid.left, rectSolid.top, rectSolid.right, rectSolid.bottom, paint);
    }

    /**绘制图表基本框架*/
    @Override
    public void drawDefult(Canvas canvas) {
        paintLabel.setTextSize(lableTextSize);
        paintLabel.setColor(lableTextColor);
        //0 一万亿
        canvas.drawText(lableLeft, startPointLeft.x - FontUtil.getFontlength(paintLabel, lableLeft)/2,
                startPointLeft.y+lableSpace-FontUtil.getFontHeight(paintLabel)+FontUtil.getFontLeading(paintLabel), paintLabel);
        canvas.drawText(lableRight, startPointRight.x - FontUtil.getFontlength(paintLabel, lableRight)/2,
                startPointRight.y+lableSpace-FontUtil.getFontHeight(paintLabel)+FontUtil.getFontLeading(paintLabel), paintLabel);
        //量能
        paintLabel.setTextSize(nameTextSize);
        paintLabel.setColor(nameTextColor);
        canvas.drawText(nameText, centerPoint.x - FontUtil.getFontlength(paintLabel, nameText)/2,
                centerPoint.y+centerRaidus+ rSpace1+FontUtil.getFontLeading(paintLabel), paintLabel);
        //实线扇形
        LinearGradient linearGradient = new LinearGradient(
                startPointSolid.x,startPointSolid.y,
                endPointSolid.x,endPointSolid.y,
                colors, positions, Shader.TileMode.CLAMP);
        //new float[]{},中的数据表示相对位置，将150,50,150,300，划分10个单位，.3，.6，.9表示它的绝对位置。300到400，将直接画出rgb（0,232,210）
        paint.setShader(linearGradient);
//        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(solidSize);
        canvas.drawArc(rectSolid, 90+startAngle, 360.0f  - startAngle*2, false, paint);
        paint.setShader(null);

        //绘制虚线
        paintEffect.setShader(linearGradient);
        paintEffect.setStyle(Paint.Style.STROKE);
        paintEffect.setStrokeWidth(dashesSize);
        PathEffect effects = new DashPathEffect(new float[]{15,8,15,8},0);
        paintEffect.setPathEffect(effects);
        canvas.drawArc(rectDashes, 90+startAngle, 360.0f  - startAngle*2, false, paintEffect);
        paintEffect.setShader(null);
        //中间白色圆圈
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(centerPoint.x, centerPoint.y, centerRaidus, paint);

    }

    /**绘制图表*/
    @Override
    public void drawChart(Canvas canvas) {
        if(progress>0) {
            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(proColor);
//            float itemWidth = rectChart.width()/(total-1);
//            Log.w(TAG, "动画进度："+animPro);
        }
    }
    /**创建动画*/
    @Override
    protected ValueAnimator initAnim() {
        if(progress>0) {
            ValueAnimator anim = ValueAnimator.ofObject(new AngleEvaluator(), 1f, Float.valueOf(progress+""));
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            return anim;
        }
        return null;
    }
    /**动画值变化之后计算数据*/
    @Override
    protected void evaluatorData(ValueAnimator animation) {
        animPro = (float)animation.getAnimatedValue() - 1;
    }




}
