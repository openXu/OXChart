package com.openxu.cview.stocknew;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.stocknew.bean.BranchChartData;
import com.openxu.cview.xmstock.BaseChart;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;

/**
 * autour : xiami
 * date : 2018/11/28 14:26
 * className : RadarChart
 * version : 1.0
 * description : 分支图
 */
public class BranchChart extends BaseChart {

    //设置数据
    private BranchChartData branchChartData;

    /**可以设置的属性*/
    private int lineColor = Color.parseColor("#ec6941");
    private int lineWidth = DensityUtil.dip2px(getContext(), 1.5f); //线段宽度
    private int lineLength = DensityUtil.dip2px(getContext(), 12); //线段长度
    private float radius = DensityUtil.dip2px(getContext(), 42);   //中间圆圈半径
    private float titleSpace = DensityUtil.dip2px(getContext(), 17); //标题字体与图标距离
    private float textSpace = DensityUtil.dip2px(getContext(), 3);  //白色字体与分支线的距离
    private float itemSpace = DensityUtil.dip2px(getContext(), 10); //长方形上下间距
    private float itemWidth = DensityUtil.dip2px(getContext(), 101); //长方形长度
    private float itemHeight = DensityUtil.dip2px(getContext(), 28); //长方形高度
    //设置底色
    private int fillColor = Color.parseColor("#aaec6941");
    //设置文字大小颜色
    private int titletextColor = Color.parseColor("#ec6941");
    private int titleTextSize = (int)DensityUtil.sp2px(getContext(), 14);
    private int centerTextSize = (int)DensityUtil.sp2px(getContext(), 16);
    private int textSize = (int)DensityUtil.sp2px(getContext(), 12);
    //若文字超过7个，缩小文字来适应这个宽度
    private int textSizeSmall = (int)DensityUtil.sp2px(getContext(), 9);

    /**需要计算相关值*/
    private PointF center;
    private PointF leftPoint;
    private PointF rightPoint;

    public BranchChart(Context context) {
        this(context, null);
    }
    public BranchChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public BranchChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        touchEnable = true;
    }

    /***********************************设置属性set方法**********************************/
    /***********************************设置属性set方法over**********************************/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        paintLabel.setTextSize(titleTextSize);
        float titleHeight = FontUtil.getFontHeight(paintLabel);
        float industryH = 0;
        float companyH = 0;
        if (branchChartData == null){
            setMeasuredDimension(widthSize, (int)(titleHeight+titleSpace*3+radius*2));
        }else{
            int industrySize = branchChartData.getIndustryList().size();
            int companySize = branchChartData.getCompanyList().size();
            industryH = industrySize*itemHeight+(industrySize-1)*itemSpace;
            companyH = companySize*itemHeight+(companySize-1)*itemSpace;
            float maxHeight = companyH>industryH?companyH:industryH;
            maxHeight = maxHeight>radius*2?maxHeight:radius*2;
            setMeasuredDimension(widthSize, (int)(titleHeight+titleSpace*3+maxHeight));
        }
        center = new PointF(getMeasuredWidth()/2, titleHeight+titleSpace*2+
                (getMeasuredHeight()-titleHeight-titleSpace*3)/2);
        if (branchChartData != null){
            //连接圆圈的直线长度
            int centerLineW = DensityUtil.dip2px(getContext(), 16);
            leftPoint = new PointF(center.x-radius-centerLineW, center.y-industryH/2);
            rightPoint = new PointF(center.x+radius+centerLineW, center.y-companyH/2);
        }
        onMeasureAfter();
        postInvalidate();
    }

    /***************************************************/

    /***************************************************/

    /**
     * 设置数据
     */
    public void setData(BranchChartData branchChartData){
        if(null==branchChartData)
            return;
        this.branchChartData = branchChartData;
        startDraw = false;
        requestLayout();
    }

    /**绘制图表基本框架*/
    @Override
    public void drawDefult(Canvas canvas) {
        //绘制标题
        paintLabel.setTextSize(titleTextSize);
        paintLabel.setColor(titletextColor);
        paintLabel.setTypeface(Typeface.DEFAULT_BOLD);
        float titleLength = FontUtil.getFontlength(paintLabel, "相关产业");
        float titleLeading = FontUtil.getFontLeading(paintLabel);
        canvas.drawText("相关产业",
                center.x-radius-lineLength-(itemWidth-titleLength)/2 - titleLength,
                titleSpace + titleLeading, paintLabel);
        canvas.drawText("相关公司",
                center.x+radius+lineLength+(itemWidth-titleLength)/2,
                titleSpace + titleLeading, paintLabel);

        //绘制中心圆
        paint.setColor(fillColor);
        paint.setStyle(Paint.Style.FILL);//设置实心
        canvas.drawCircle(center.x, center.y, radius, paint);
        paint.setColor(lineColor);
        paint.setStyle(Paint.Style.STROKE);//设置实心
        paint.setStrokeWidth(lineWidth);
        canvas.drawCircle(center.x, center.y, radius, paint);
    }

    /**绘制debug辅助线*/
    @Override
    public void drawDebug(Canvas canvas) {
        super.drawDebug(canvas);
        canvas.drawCircle(center.x, center.y, radius, paint);
    }

    /**绘制图表*/
    @Override
    public void drawChart(Canvas canvas) {
        if(null==branchChartData)
            return;
        //绘制中间公司名称
        paintLabel.setTextSize(centerTextSize);
        paintLabel.setColor(Color.WHITE);
        paintLabel.setTypeface(Typeface.DEFAULT_BOLD);
        String text = branchChartData.getCompany();
        float textLength = FontUtil.getFontlength(paintLabel, text);
        float textLeading = FontUtil.getFontLeading(paintLabel);
        float textheight= FontUtil.getFontHeight(paintLabel);
        canvas.drawText(text,center.x - textLength/2,
                center.y - textheight/2 + textLeading, paintLabel);

        //绘制item
        paintEffect.setStyle(Paint.Style.STROKE);
        paintEffect.setStrokeWidth(DensityUtil.dip2px(getContext(),.7f));
        paintEffect.setColor(lineColor);
        PathEffect effects = new DashPathEffect(new float[]{6,12,6,12},0);
        paintEffect.setPathEffect(effects);

        /**绘制相关产业分支*/
        float top = leftPoint.y;
        for(int i = 0; i< branchChartData.getIndustryList().size(); i++){
            text = branchChartData.getIndustryList().get(i);
            //若文字超过7个，缩小文字来适应这个宽度
            paintLabel.setTextSize(text.length()>7?textSizeSmall:textSize);
            textLeading = FontUtil.getFontLeading(paintLabel);
            textheight= FontUtil.getFontHeight(paintLabel);

            paint.setColor(fillColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(leftPoint.x-itemWidth, top,leftPoint.x, top+itemHeight, paint);
            //虚线框
            Path path = new Path();
            path.moveTo(leftPoint.x-itemWidth, top);
            path.lineTo(leftPoint.x,top);
            path.lineTo(leftPoint.x,top+itemHeight);
            path.lineTo(leftPoint.x-itemWidth,top+itemHeight);
            path.lineTo(leftPoint.x-itemWidth, top);
            path.close();
            canvas.drawPath(path, paintEffect);
            //绘制分支线段
            paint.setColor(lineColor);
            paint.setStrokeWidth(lineWidth);
            canvas.drawLine(leftPoint.x-lineLength, top + itemHeight/2,
                    leftPoint.x, top + itemHeight/2, paint);
            //绘制文字
            textLength = FontUtil.getFontlength(paintLabel, text);

            canvas.drawText(text,leftPoint.x-lineLength-textSpace - textLength,
                    top + (itemHeight-textheight)/2 + textLeading, paintLabel);

            top += (itemHeight+itemSpace);
        }

        if(branchChartData.getIndustryList().size()>0){
            //绘制左边竖直方向线段
            paint.setColor(lineColor);
            paint.setStrokeWidth(lineWidth);
            canvas.drawLine(leftPoint.x, leftPoint.y + itemHeight/2,
                    leftPoint.x, top - itemHeight-itemSpace +itemHeight/2, paint);
            canvas.drawLine(leftPoint.x, center.y,
                    center.x-radius, center.y, paint);
        }

        /**绘制相关公司分支*/
        top = rightPoint.y;
        for(int i = 0; i< branchChartData.getCompanyList().size(); i++){
            text = branchChartData.getCompanyList().get(i);
            //若文字超过7个，缩小文字来适应这个宽度
            paintLabel.setTextSize(text.length()>7?textSizeSmall:textSize);
            textLeading = FontUtil.getFontLeading(paintLabel);
            textheight= FontUtil.getFontHeight(paintLabel);

            paint.setColor(fillColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(rightPoint.x, top,rightPoint.x+itemWidth, top+itemHeight, paint);
            //虚线框
            Path path = new Path();
            path.moveTo(rightPoint.x, top);
            path.lineTo(rightPoint.x+itemWidth,top);
            path.lineTo(rightPoint.x+itemWidth,top+itemHeight);
            path.lineTo(rightPoint.x,top+itemHeight);
            path.lineTo(rightPoint.x, top);
            path.close();
            canvas.drawPath(path, paintEffect);
            //绘制分支线段
            paint.setColor(lineColor);
            paint.setStrokeWidth(lineWidth);
            canvas.drawLine(rightPoint.x, top + itemHeight/2,
                    rightPoint.x+lineLength, top + itemHeight/2, paint);
            //绘制文字
            textLength = FontUtil.getFontlength(paintLabel, text);
            canvas.drawText(text,rightPoint.x+lineLength+textSpace,
                    top + (itemHeight-textheight)/2 + textLeading, paintLabel);
            top += (itemHeight+itemSpace);
        }

        if(branchChartData.getIndustryList().size()>0){
            //绘制左边竖直方向线段
            paint.setColor(lineColor);
            paint.setStrokeWidth(lineWidth);
            canvas.drawLine(rightPoint.x, rightPoint.y + itemHeight/2,
                    rightPoint.x, top - itemHeight-itemSpace +itemHeight/2, paint);
            canvas.drawLine(rightPoint.x, center.y,
                    center.x+radius, center.y, paint);
        }

    }



    private float animPro;       //动画计算的占比数量
    /**创建动画*/
    @Override
    protected ValueAnimator initAnim() {
        return null;
    }
    /**动画值变化之后计算数据*/
    @Override
    protected void evaluatorData(ValueAnimator animation) {
        animPro = (float)animation.getAnimatedValue();
    }


}
