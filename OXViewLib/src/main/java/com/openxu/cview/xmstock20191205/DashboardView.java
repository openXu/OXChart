package com.openxu.cview.xmstock20191205;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
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
    //开始角度  （正常情况正下方为90度，这里的30是指正下方向左偏30度，也就是120度）
    private int startAngle = 30;
    private int dashesSize = DensityUtil.dip2px(getContext(), 20);    //虚线宽度
    private int solidSize = DensityUtil.dip2px(getContext(), 9);     //实线宽度
    private int rSpace1 = DensityUtil.dip2px(getContext(), 25);      //中心白圈与 实线间隙
    private int rSpace2 = DensityUtil.dip2px(getContext(), 4);       //虚线 实线间隙
    //指针颜色和长度（长度应该比rSpace1小）
    private int pointerColor = Color.parseColor("#d55942");
    private int pointerRaidus = DensityUtil.dip2px(getContext(), 20);
    private int pointerSize = DensityUtil.dip2px(getContext(), 10);
    /**文字大小颜色*/
    //中间文字 (5000亿)
    private int centerTextSize = DensityUtil.sp2px(getContext(), 18);
    private int centerTextColor = Color.parseColor("#d55942");
    //仪表名称 （量能）
    private String nameText = "能量";
    private int nameTextSize = DensityUtil.sp2px(getContext(), 20);
    private int nameTextColor = Color.parseColor("#dc3929");
    //仪表刻度 （0  一万亿）
    private int lableTextSize = DensityUtil.sp2px(getContext(), 18);
    private int lableTextColor = Color.parseColor("#e2978a");
    private String lableLeft = "0";
    private String lableRight = "一万亿";
    //0  一万亿 文字与扇形间距
    private int lableSpace = DensityUtil.dip2px(getContext(), 4);
    //渐变色
    int[] colors = new int[]{
            Color.parseColor("#e7ac6a"),   //初始颜色
            Color.parseColor("#e47f3f"),   //中间颜色
            Color.parseColor("#df422c")};  //最终颜色
    float[] positions = new float[]{0.3f, .5f, .7f};
    SweepGradient mColorShader;

    //虚线间隔
    PathEffect effects = new DashPathEffect(new float[]{9,11,9,11},0);

    /**计算*/
    private int centerRaidus;         //中间白色圆圈半径
    private int chartRaidus;          //半径
    protected RectF rectDashes;       //虚线扇形矩形框(位于扇形中心)
    protected RectF rectSolid;        //实线扇形矩形框(位于扇形中心)
    private PointF startPointLeft, startPointRight;    //0  一万亿 lable绘制点
    private PointF startPointName;    //量能   绘制点

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
        int textWidth = (int)FontUtil.getFontlength(paintLabel, "100000");
//        int textHeight = (int)FontUtil.getFontHeight(paintLabel);
        centerRaidus = textWidth/2 + DensityUtil.dip2px(getContext(), 5);
        //最大半径
        chartRaidus = centerRaidus + rSpace1 + solidSize + rSpace2 + dashesSize;
        paintLabel.setTextSize(lableTextSize);
        LogUtil.d(TAG, "centerRaidus："+centerRaidus);
        LogUtil.d(TAG, "chartRaidus："+chartRaidus);
        //计算控件宽高
        int raidus = chartRaidus;
        int sectionRaidus = (int)(Math.cos(Math.toRadians(startAngle)) * raidus);//下切面高度  Math.toRadians将角度转换成弧度值
        widthSize = getPaddingLeft() + getPaddingRight() + chartRaidus*2;
        heightSize = getPaddingTop()+getPaddingBottom() + chartRaidus + sectionRaidus + lableSpace + (int)FontUtil.getFontHeight(paintLabel);
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
        LogUtil.i(TAG, "宽高："+getMeasuredWidth() + " * "+getMeasuredHeight());
        centerPoint = new PointF(getPaddingLeft()+chartRaidus, getPaddingTop()+chartRaidus);
        rectChart = new RectF(getPaddingLeft(),getPaddingTop(),
                getMeasuredWidth()-getPaddingRight(), getPaddingTop()+chartRaidus*2);
        //计算实线扇形矩形区域-----------------
        raidus = centerRaidus+rSpace1+solidSize/2;
        rectSolid = new RectF(centerPoint.x-raidus,centerPoint.y-raidus,
                centerPoint.x+raidus, centerPoint.y+raidus);
        //量能 绘制点
        startPointName = new PointF(centerPoint.x, rectSolid.bottom);
        //虚线部分--------------------------
        raidus = centerRaidus+rSpace1+solidSize +rSpace2+dashesSize/2;
        rectDashes = new RectF(centerPoint.x-raidus,centerPoint.y-raidus,
                centerPoint.x+raidus, centerPoint.y+raidus);
        //lable绘制开始和结束点 ----------------
        raidus = centerRaidus+rSpace1+solidSize +rSpace2+dashesSize;
        sectionRaidus = (int)(Math.cos(Math.toRadians(startAngle)) * raidus);
        startPointLeft = new PointF((int)(centerPoint.x-Math.sin(Math.toRadians(startAngle))*raidus), centerPoint.y+sectionRaidus);
        startPointRight = new PointF((int)(centerPoint.x+Math.sin(Math.toRadians(startAngle))*raidus), centerPoint.y+sectionRaidus);
        //渐变色
        mColorShader = new SweepGradient(centerPoint.x, centerPoint.y,colors,positions);
        LogUtil.d(TAG, "centerPoint："+centerPoint);
        LogUtil.d(TAG, "rectChart："+rectChart);
        invalidate();
    }
    public void setData(int total, int progress){
        if(progress>total)
            progress = total;
        this.total = total;
        this.progress = progress;
        startDraw = false;
        invalidate();
    }

    @Override
    public void drawDebug(Canvas canvas) {
        super.drawDebug(canvas);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        canvas.drawRect(rectChart.left, rectChart.top, rectChart.right, rectChart.bottom, paint);
        paint.setColor(Color.BLACK);
        canvas.drawRect(rectDashes.left, rectDashes.top, rectDashes.right, rectDashes.bottom, paint);
        paint.setColor(Color.GRAY);
        canvas.drawRect(rectSolid.left, rectSolid.top, rectSolid.right, rectSolid.bottom, paint);
    }

    /**绘制图表基本框架*/
    @Override
    public void drawDefult(Canvas canvas) {
        //量能
        paintLabel.setTextSize(nameTextSize);
        paintLabel.setColor(nameTextColor);
        canvas.drawText(nameText, startPointName.x - FontUtil.getFontlength(paintLabel, nameText)/2,
                startPointName.y-FontUtil.getFontHeight(paintLabel)/3*2+FontUtil.getFontLeading(paintLabel), paintLabel);
        //0 一万亿
        paintLabel.setTextSize(lableTextSize);
        paintLabel.setColor(lableTextColor);
        canvas.drawText(lableLeft, startPointLeft.x - FontUtil.getFontlength(paintLabel, lableLeft)/2,
                startPointLeft.y+lableSpace+FontUtil.getFontLeading(paintLabel), paintLabel);
        canvas.drawText(lableRight, startPointRight.x - FontUtil.getFontlength(paintLabel, lableRight)/2,
                startPointRight.y+lableSpace+FontUtil.getFontLeading(paintLabel), paintLabel);
        //实线扇形
        //由于渐变色shader是从正东方开始绘制，导致颜色排版错误，这里将画布旋转90度，正下方为0度
        canvas.rotate(90, centerPoint.x, centerPoint.y);
        paint.setShader(mColorShader);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(solidSize);
        canvas.drawArc(rectSolid, startAngle, 360.0f  - startAngle*2, false, paint);
        paint.setShader(null);

        //绘制虚线
        paintEffect.setShader(null);
        paintEffect.setStyle(Paint.Style.STROKE);
        paintEffect.setPathEffect(effects);
        paintEffect.setColor(defColor);
        paintEffect.setStrokeWidth(dashesSize);
        canvas.drawArc(rectDashes, startAngle, 360.0f  - startAngle*2, false, paintEffect);
        //中间白色圆圈
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(centerPoint.x, centerPoint.y, centerRaidus, paint);
        //反转90度还原
        canvas.rotate(-90, centerPoint.x, centerPoint.y);
    }

    /**绘制图表*/
    @Override
    public void drawChart(Canvas canvas) {
        if(progress>0) {
            //数量文字
            paintLabel.setTextSize(centerTextSize);
            paintLabel.setColor(centerTextColor);
            String text = ((int)animPro)+"";
            canvas.drawText(text, centerPoint.x - FontUtil.getFontlength(paintLabel, text)/2,
                    centerPoint.y-FontUtil.getFontHeight(paintLabel)+FontUtil.getFontLeading(paintLabel), paintLabel);
            text = "亿";
            canvas.drawText(text, centerPoint.x - FontUtil.getFontlength(paintLabel, text)/2,
                    centerPoint.y+DensityUtil.dip2px(getContext(), 2)+FontUtil.getFontLeading(paintLabel), paintLabel);
            //绘制彩色虚线
            //再次寻转90度
            canvas.rotate(90, centerPoint.x, centerPoint.y);
            float proAngle = (360.0f  - startAngle*2) * (animPro/total*1.0f);
            paintEffect.setStyle(Paint.Style.STROKE);
            paintEffect.setPathEffect(effects);
            paintEffect.setShader(mColorShader);
            paintEffect.setStrokeWidth(dashesSize);
            //消除由于虚线间隔导致视觉上仪表进度不够的情况，默认多绘制2.5度
            canvas.drawArc(rectDashes, startAngle, proAngle<(360.0f - startAngle*2-5)? proAngle+2.5f:proAngle, false, paintEffect);
            paintEffect.setShader(null);

            //绘制指针
            /*画一个实心三角形*/
            //以正东面为0度起点计算指定角度所对应的圆周上的点的坐标：
            double asin = Math.toDegrees(Math.asin(pointerSize/2f/centerRaidus));
            float centerArcX1 = centerPoint.x + (float) (centerRaidus* Math.cos(Math.toRadians(startAngle-asin+proAngle)));
            float centerArcY1 = centerPoint.y + (float) (centerRaidus* Math.sin(Math.toRadians(startAngle-asin+proAngle)));
//            LogUtil.i(TAG, "角度   "+(startAngle-asin+proAngle)+"   弧度："+Math.toRadians(startAngle-asin+proAngle));
//            LogUtil.i(TAG, "cos   "+Math.cos(Math.toRadians(startAngle-asin+proAngle)));
//            LogUtil.i(TAG, "sin   "+Math.sin(Math.toRadians(startAngle-asin+proAngle)));
            float endx2 = centerPoint.x + (float) ((pointerRaidus+centerRaidus)* Math.cos(Math.toRadians(startAngle+proAngle)));
            float endy2 = centerPoint.y + (float) ((pointerRaidus+centerRaidus)* Math.sin(Math.toRadians(startAngle+proAngle)));
            float centerArcX2 = centerPoint.x  + (float) (centerRaidus* Math.cos(Math.toRadians(startAngle+asin+proAngle)));
            float centerArcY2 = centerPoint.y  + (float) (centerRaidus* Math.sin(Math.toRadians(startAngle+asin+proAngle)));
//            LogUtil.d(TAG, "角度   "+(startAngle+asin+proAngle)+"   弧度："+Math.toRadians(startAngle+asin+proAngle));
//            LogUtil.d(TAG, "cos   "+Math.cos(Math.toRadians(startAngle+asin+proAngle)));
//            LogUtil.d(TAG, "sin   "+Math.sin(Math.toRadians(startAngle+asin+proAngle)));
            Path path2=new Path();
            path2.moveTo(centerArcX1,centerArcY1);
            path2.lineTo(endx2,endy2);
            path2.lineTo(centerArcX2,centerArcY2);
            path2.close();
            paint.setColor(pointerColor);
            canvas.drawPath(path2, paint);

        }
    }
    /**创建动画*/
    @Override
    protected ValueAnimator initAnim() {
        anim = ValueAnimator.ofObject(new AngleEvaluator(), 0f, 1.0f);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        return anim;
    }
    /**动画值变化之后计算数据*/
    @Override
    protected void evaluatorData(ValueAnimator animation) {
        animPro = (float)animation.getAnimatedValue()*progress;
    }


}
