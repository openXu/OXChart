package com.openxu.cview.chart.dashboard;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.chart.BaseChart;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * autour : openXu
 * date : 2018/4/20 15:12
 * className : DashBoardView
 * version : 1.0
 * description : 仪表
 */
public class DashBoardView extends BaseChart {

    private List<DashBoardItem> dataList;

    /**设置的属性*/

    private int ringWidth = DensityUtil.dip2px(getContext(), 20);//圆环宽度

    private int textSizeCoordinate = (int)getResources().getDimension(R.dimen.ts_barchart_x);
    private int textSizeLable = (int)getResources().getDimension(R.dimen.ts_barchart_lable);
    private int textColorCoordinate = getResources().getColor(R.color.text_color_light_gray);
    private int textColorLable = getResources().getColor(R.color.text_color_def);


    private int textXSpace = DensityUtil.dip2px(getContext(), 6);
    private int textLableSpace = DensityUtil.dip2px(getContext(), 10);
    //小圆圈半径
    private int lableCircleSize = DensityUtil.dip2px(getContext(), 4);
    //指针半径
    private int centerPointSize = DensityUtil.dip2px(getContext(), 5);

    /**常量*/
    private final float startAngle = 180;
    private final float endAngle = 360;

    /**计算*/
    private PointF arcCenter;
    private float arcRaidus;          //饼状图半径
    private float tagMaxW;


    public DashBoardView(Context context) {
        super(context, null);
    }
    public DashBoardView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }
    public DashBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        dataList = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        calculatData();
    }

    /***********************************设置属性set方法**********************************/
    public void setData(List<DashBoardItem> dataList) {
        this.dataList.clear();
        if(dataList!=null)
            this.dataList.addAll(dataList);
        calculatData();
    }
    private float progress;
    private float progressAnim;
    public void setPro(float progress){
        this.progress = progress;
        invalidate();
    }
/***********************************设置属性set方法over**********************************/

    public void calculatData() {
        total = 0;
        float maxLableLength = 0;   //lable最长的长度
        paintLabel.setTextSize(textSizeLable);
        for(DashBoardItem bean : dataList){
            total += bean.getNum();
            float l = FontUtil.getFontlength(paintLabel, bean.getLable());
            maxLableLength = maxLableLength>l?maxLableLength:l;
        }
        //计算每个部分的角度
        float allAngle = endAngle - startAngle;
        for(DashBoardItem bean : dataList){
            bean.setAngle(allAngle * (bean.getNum()/total*1.0f));
        }

        //lable所占用的宽度
        maxLableLength = maxLableLength + textLableSpace + lableCircleSize * 2;

        int widthSize = getMeasuredWidth();
        int heightSize = getMeasuredHeight();

        paintLabel.setTextSize(textSizeCoordinate);
        float xLableSize = FontUtil.getFontHeight(paint);

        arcCenter = new PointF(
                getPaddingLeft()+(widthSize - getPaddingLeft() - maxLableLength -getPaddingRight())/2,
                heightSize - xLableSize - textXSpace- getPaddingBottom()
        );
        float raidusX = arcCenter.x - getPaddingLeft();
        float raidusY = arcCenter.y - getPaddingTop();
        arcRaidus = raidusX<raidusY?raidusX:raidusY;

        rectChart = new RectF(arcCenter.x - arcRaidus,arcCenter.y - arcRaidus,
                arcCenter.x + arcRaidus,arcCenter.y + arcRaidus);

        if(centerPoint!=null && centerPoint.x>0){
            startDraw = false;
            invalidate();
        }
    }

    /**绘制图表基本框架*/
    @Override
    public void drawDefult(Canvas canvas) {
      /*  paint.setStyle(Paint.Style.FILL);
        paint.setColor(defColor);
        canvas.drawCircle(centerPoint.x,centerPoint.y,chartRaidus, paint);
        if(ringWidth>0){   //绘制空心圆圈
            paint.setColor(backColor);
            canvas.drawCircle(centerPoint.x,centerPoint.y,chartRaidus-ringWidth, paint);
        }*/
    }
    /**绘制debug辅助线*/
    @Override
    public void drawDebug(Canvas canvas) {
        super.drawDebug(canvas);
        paint.setColor(Color.RED);
        canvas.drawRect(rectChart, paint);
    }
    @Override
    public void drawChart(Canvas canvas) {
//        paint.setStrokeWidth(ringWidth);
        if(null==dataList || dataList.size()<=0)
            return;

        paint.setStyle(Paint.Style.FILL);//设置空心
        float angle = startAngle;

        float lableTop = getPaddingTop();
        paintLabel.setTextSize(textSizeLable);
        paintLabel.setColor(textColorLable);
        float lableLead = FontUtil.getFontLeading(paintLabel);
        float lableHeight = FontUtil.getFontHeight(paintLabel);
        float lableCrc = (lableHeight - lableCircleSize*2)/2;
//        canvas.drawArc(rectChart, 180, 180, false, paint);
        for(DashBoardItem bean : dataList){
            paint.setColor(bean.getColor());
            LogUtil.w(TAG, "绘制扇形"+bean);
            canvas.drawArc(rectChart, angle, bean.getAngle(), true, paint);
            angle += bean.getAngle();

            //绘制lable
            canvas.drawCircle(rectChart.right+lableCircleSize, lableTop+lableCrc+lableCircleSize, lableCircleSize, paint);
            canvas.drawText(bean.getLable(), rectChart.right+lableCircleSize*2+textLableSpace, lableTop + lableLead, paintLabel);
            lableTop += lableHeight+DensityUtil.dip2px(getContext(),5);
        }

        //覆盖
        paint.setColor(Color.WHITE);
        canvas.drawCircle(arcCenter.x, arcCenter.y, arcRaidus - ringWidth, paint);

        //绘制x坐标
        paintLabel.setTextSize(textSizeCoordinate);
        paintLabel.setColor(textColorCoordinate);
        float xLead = FontUtil.getFontLeading(paintLabel);
        float xLength = FontUtil.getFontlength(paintLabel, "0");

        canvas.drawText("0", rectChart.left+(ringWidth-xLength)/2,
                arcCenter.y + textXSpace+xLead, paintLabel);
        xLength = FontUtil.getFontlength(paintLabel, "0分钟");
        canvas.drawText("0分钟",
                rectChart.right-(Math.abs((ringWidth-xLength)/2))-(xLength<ringWidth?xLength:ringWidth),
                arcCenter.y + textXSpace+xLead, paintLabel);

        //绘制虚线
        paintEffect.setStyle(Paint.Style.STROKE);
        paintEffect.setStrokeWidth(lineWidth);
        paintEffect.setColor(defColor);
        PathEffect effects = new DashPathEffect(new float[]{15,8,15,8},0);
        paintEffect.setPathEffect(effects);
        Path path = new Path();
        path.moveTo(rectChart.left, arcCenter.y);
        path.lineTo(rectChart.right, arcCenter.y);
        canvas.drawPath(path, paintEffect);
        path = new Path();
        path.moveTo(arcCenter.x, rectChart.top+ringWidth);
        path.lineTo(arcCenter.x, arcCenter.y);
        canvas.drawPath(path, paintEffect);

        //绘制指针
        paint.setColor(Color.BLACK);
        canvas.drawCircle(arcCenter.x, arcCenter.y, centerPointSize, paint);
         /*画一个实心三角形*/
        float proAndle =  (endAngle - startAngle) * (progressAnim/total*1.0f);
        //以正东面为0度起点计算指定角度所对应的圆周上的点的坐标：
        float centerArcX1 = arcCenter.x + (float) (centerPointSize* Math.cos(Math.toRadians(startAngle+proAndle - 90)));
        float centerArcY1 = arcCenter.y + (float) (centerPointSize* Math.sin(Math.toRadians(startAngle+proAndle - 90)));

        float endx2 = arcCenter.x + (float) (arcRaidus* Math.cos(Math.toRadians(startAngle+proAndle)));
        float endy2 = arcCenter.y + (float) (arcRaidus* Math.sin(Math.toRadians(startAngle+proAndle)));

        float centerArcX2 = arcCenter.x + (float) (centerPointSize* Math.cos(Math.toRadians(startAngle+proAndle + 90)));
        float centerArcY2 = arcCenter.y + (float) (centerPointSize* Math.sin(Math.toRadians(startAngle+proAndle + 90)));

        Path path2=new Path();
        path2.moveTo(centerArcX1,centerArcY1);
        path2.lineTo(endx2,endy2);
        path2.lineTo(centerArcX2,centerArcY2);
        path2.close();
        canvas.drawPath(path2, paint);
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
        float percentage = (float) animation.getAnimatedValue();
        evaluatorData(percentage);
    }

    /**计算各种绘制坐标*/
    private void evaluatorData(float animPre){
        progressAnim = animPre*progress;
    }


}
