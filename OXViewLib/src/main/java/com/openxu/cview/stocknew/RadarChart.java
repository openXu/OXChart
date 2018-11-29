package com.openxu.cview.stocknew;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.stocknew.bean.BaseChartData;
import com.openxu.cview.xmstock.BaseChart;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * autour : xiami
 * date : 2018/11/28 14:26
 * className : RadarChart
 * version : 1.0
 * description : 雷达图
 */
public class RadarChart extends BaseChart {

    //设置数据
    private List<BaseChartData> dataList = new ArrayList<>();
    private float maxValue = 100;

    /**可以设置的属性*/
    //设置线条颜色
    private int[] lineColor = new int[]{Color.parseColor("#fc4530"), Color.parseColor("#ffea01")};
    //设置曲线粗细
    private int lineSize = DensityUtil.dip2px(getContext(), 1.5f);
    //曲线数量
    private int LINE_NUM = 0;
    //设置文字大小
    private int textSize = (int)getResources().getDimension(R.dimen.ts_chart_lable);
    //设置文字颜色
    private int textColor = getResources().getColor(R.color.tc_chart_lable);
    //设置蜘蛛网线条颜色
    private int spiderLineColor = Color.parseColor("#ffffff");
    //设置底色
    private int fillColor = Color.parseColor("#eeeeee");
    //设置字体距离
    private int textSpace = DensityUtil.dip2px(getContext(), 8);

    /**需要计算相关值*/
    private float radius;//半径
    private int count;
    private float angle;   //平分角度
    private float lableLead, lableHeight;

    public RadarChart(Context context) {
        this(context, null);
    }
    public RadarChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public RadarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        touchEnable = true;
    }

    /***********************************设置属性set方法**********************************/
    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
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
        onMeasureAfter();
        evaluatorByData();
        postInvalidate();
    }

    /***************************************************/

    /***************************************************/

    /**
     * 设置数据
     */
    public void setData(List<BaseChartData> dataList){
        if(null==dataList)
            return;
        this.dataList.clear();
        this.dataList.addAll(dataList);
        if(getMeasuredWidth()>0) {
            evaluatorByData();
            startDraw = false;
            invalidate();
        }
    }
    /**设置数据后，计算相关值*/
    private void evaluatorByData() {
        if (dataList.size() <= 0)
            return;

        paintLabel.setTextSize(textSize);
        paintLabel.setColor(textColor);
        lableLead = (int) FontUtil.getFontLeading(paintLabel);
        lableHeight = (int) FontUtil.getFontHeight(paintLabel);

        //半径 = （宽高小者 - 上下字体及间隙） / 2
        radius = (Math.min(getMeasuredWidth(),getMeasuredHeight()) - (textSpace+lableHeight)*2)/2;
        centerPoint = new PointF(getMeasuredWidth()/2.0f, getMeasuredHeight()/2.0f);

        count = dataList.size();
        //1度=1*PI/180   360度=2*PI   那么我们每旋转一次的角度为2*PI/内角个数
        //中心与相邻两个内角相连的夹角角度
        angle = (float) (2 * Math.PI / count);
        LogUtil.i(TAG, "夹角angle："+angle);
        /**①、计算字体相关以及图表原点坐标*/

    }

    /**绘制图表基本框架*/
    @Override
    public void drawDefult(Canvas canvas) {
        if(null==dataList || dataList.size()<=0)
            return;
        drawSpider(canvas);
        drawTitle(canvas);
    }

    /**绘制debug辅助线*/
    @Override
    public void drawDebug(Canvas canvas) {
        super.drawDebug(canvas);
        canvas.drawCircle(centerPoint.x, centerPoint.y, radius, paint);
    }

    /**绘制图表*/
    @Override
    public void drawChart(Canvas canvas) {
        if(null==dataList || dataList.size()<=0)
            return;

        drawRegion(canvas);
    }


    private PointF point;
    /**
     * 绘制蜘蛛网
     * @param canvas
     */
    private void drawSpider(Canvas canvas) {
        Path path = new Path();
        //每个蛛丝之间的间距
        float r = radius / (count - 1);
        for (int i = count - 1; i >=0 ; i--) {   //一次遍历就是一个多边形
            //当前半径
            float curR = r * i;
            path.reset();
            //从外到内绘制多边形
            for (int j = 0; j < count; j++) {
                if (j == 0) {
                    //起始点(以12点方向为起始点)
                    path.moveTo(centerPoint.x, centerPoint.y - curR);
                } else {
//                    float x = (float) (centerPoint.x + curR * Math.sin(angle * j));
//                    float y = (float) (centerPoint.y - curR * Math.cos(angle * j));
                    point = getAnglePoint(curR, j);
                    path.lineTo(point.x, point.y);
                }
            }
            path.close();
            if(i == count-1){
                //最外围
                paint.setStyle(Paint.Style.FILL);
                paint.setStrokeWidth(lineWidth);
                paint.setColor(fillColor);
            }else{
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(lineWidth);
                paint.setColor(spiderLineColor);
            }
            canvas.drawPath(path, paint);
        }
        //中心点 与 外围点连线
        for (int i = 0; i < count; i++) {
            path.reset();
            path.moveTo(centerPoint.x,centerPoint.y);
            point = getAnglePoint(radius, i);
            path.lineTo(point.x, point.y);
            canvas.drawPath(path,paint);
        }

    }

    /**
     * 根据半径和序号，获取环上的点
     * @param radius
     * @param i
     * @return
     */
    private PointF getAnglePoint(float radius, int i){
        return new PointF((float) (centerPoint.x + radius * Math.sin(angle * i)),
                (float) (centerPoint.y - radius * Math.cos(angle * i)));
    }

    /**
     * 绘制标题文字
     * @param canvas
     */
    private void drawTitle(Canvas canvas) {
        float textRadius = radius + textSpace;
        double pi = Math.PI;
        String lable;
        float lableLength;
        for (int i = 0; i < count; i++) {
            lable = dataList.get(i).getName();
            lableLength = FontUtil.getFontlength(paintLabel, lable);
            point = getAnglePoint(textRadius, i);
            //当前绘制标题所在顶点角度
            float degrees = angle * i;
            if(degrees == 0){   //顶上
                canvas.drawText(lable, point.x-lableLength/2, point.y - lableHeight + lableLead, paintLabel);
            }else if(degrees == (float)pi){  //下角
                canvas.drawText(lable, point.x-lableLength/2, point.y + lableLead, paintLabel);
            }else if (degrees >= 0 && degrees < pi / 2) {//第一象限（右上）
                canvas.drawText(lable, point.x, point.y  - lableHeight/2 + lableLead, paintLabel);
            } else if (degrees >= pi / 2 && degrees < pi) {//第四象限（右下）
                canvas.drawText(lable, point.x, point.y  - lableHeight/2 + lableLead, paintLabel);
            } else if (degrees > pi && degrees < 3 * pi / 2) {//第三象限（左下）
                canvas.drawText(lable, point.x - lableLength, point.y  - lableHeight/2 + lableLead, paintLabel);
            } else if (degrees >= 3 * pi / 2 && degrees <= 2 * pi) {//第二象限（左上）
                canvas.drawText(lable, point.x - lableLength, point.y  - lableHeight/2 + lableLead, paintLabel);
            }
        }
    }

    /**
     * 绘制覆盖区域
     */
    private void drawRegion(Canvas canvas){
        Path path=new Path();
        float pointYMin= Integer.MAX_VALUE;
        float pointYMax = Integer.MIN_VALUE;
        for (int i = 0; i < count; i++) {
            //计算该数值与最大值比例
            float percenterPoint = dataList.get(i).getNum()/maxValue;
            //小圆点所在位置距离圆心的距离
            float perRadius = percenterPoint * radius * animPro;
            point = getAnglePoint(perRadius, i);
            if(i == 0){
                path.moveTo(point.x, point.y);
            }else {
                path.lineTo(point.x, point.y);
            }
            pointYMin = pointYMin>point.y?point.y:pointYMin;
            pointYMax = pointYMax<point.y?point.y:pointYMax;
            //绘制小圆点
//            canvas.drawCircle(point.x, point.y,10, paint);
        }
        //闭合覆盖区域
        path.close();
        LinearGradient linearGradient=new LinearGradient(getMeasuredWidth()/2, pointYMax,
                getMeasuredWidth()/2,pointYMin,
                lineColor,  new float[]{0,.5F},Shader.TileMode.CLAMP);
        //new float[]{},中的数据表示相对位置，将150,50,150,300，划分10个单位，.3，.6，.9表示它的绝对位置。300到400，将直接画出rgb（0,232,210）
        paint.setShader(linearGradient);
        //填充覆盖区域
//        paint.setAlpha(128);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path,paint);
        paint.setShader(null);  //重置
    }


    private float animPro;       //动画计算的占比数量
    /**创建动画*/
    @Override
    protected ValueAnimator initAnim() {
        if(dataList.size()>0) {
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
