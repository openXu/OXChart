package com.openxu.cview.stocknew;

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
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.stocknew.bean.BaseChartData;
import com.openxu.cview.xmstock.BaseChart;
import com.openxu.cview.xmstock.LinesChart;
import com.openxu.cview.xmstock.bean.DataPoint;
import com.openxu.cview.xmstock.bean.FocusInfo;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;
import com.openxu.utils.NumberFormatUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * autour : xiami
 * date : 2018/12/07 14:26
 * className : ShadowLineChart
 * version : 1.0
 * description : 阴影折现图表
 */
public class ShadowLineChart extends BaseChart {

    //设置数据
    private List<BaseChartData> dataList = new ArrayList<>();

    /**可以设置的属性*/
    //设置Y轴刻度数量
    private int YMARK_NUM =  3;
    private int XMARK_NUM =  5;
    //阴影颜色
    private int[] shadowColor = {Color.TRANSPARENT, Color.parseColor("#33ec6941"), Color.parseColor("#99ec6941")};
    private float[] shadowPositions = new float[]{0, .3f, .65F};
    //折线颜色宽度
    private int lineColor = Color.parseColor("#ec6941");
    private int lineSize = DensityUtil.dip2px(getContext(), 2f);
    //设置坐标文字大小
    private int textSize = (int)getResources().getDimension(R.dimen.ts_chart_xy);
    //设置坐标文字颜色
    private int textColor = getResources().getColor(R.color.tc_chart_xy);
    //设置X坐标字体与横轴的距离
    private int textSpaceX = DensityUtil.dip2px(getContext(), 5);
    //设置Y坐标字体与横轴的距离
    private int textSpaceY = DensityUtil.dip2px(getContext(), 2);

    /**需要计算相关值*/
    private float oneWidth;
    private float lableLead, lableHeight;
    /*字体绘制相关*/
    private float YMARK =  1;    //Y轴刻度间隔
    private float YMARK_MAX;    //Y轴刻度最大值
    private float YMARK_MIN;    //Y轴刻度最小值
    private List<Integer> XMARK_INDEX = new ArrayList<>();
    protected RectF drawRect;          //图表绘制范围矩形

    public ShadowLineChart(Context context) {
        this(context, null);
    }
    public ShadowLineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ShadowLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        textColor = Color.parseColor("#999999");
        defColor = Color.parseColor("#eeeeee");
    }

    /***********************************设置属性set方法**********************************/

    /***********************************设置属性set方法over**********************************/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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
            postInvalidate();
        }
    }
    /**设置数据后，计算相关值*/
    private void evaluatorByData(){
        if(dataList.size()<=0)
            return;
        int count = dataList.size();
        int step = 1;
        int startIndex = 0;
        if(count>XMARK_NUM){
            step = count/XMARK_NUM;
            startIndex = count%XMARK_NUM;
        }else{
            XMARK_NUM = count;
        }
        //获取标记x刻度的索引
        XMARK_INDEX.clear();
        for(int i = dataList.size()-1; i>0; i-=step){
            if(XMARK_INDEX.size()>=XMARK_NUM)
                break;
            XMARK_INDEX.add(i);
        }
        LogUtil.w(TAG, "总共："+count+"条数据，X轴标签"+XMARK_NUM+"个， 步长"+step+" 开始索引："+startIndex);

        /**③、计算Y刻度最大值和最小值以及幅度*/
        YMARK_MAX =  Integer.MIN_VALUE;    //Y轴刻度最大值
        YMARK_MIN =  Integer.MAX_VALUE;    //Y轴刻度最小值
        for(BaseChartData data : dataList){
            YMARK_MAX = YMARK_MAX>Math.abs(data.getNum())?YMARK_MAX:Math.abs(data.getNum());
            YMARK_MIN = YMARK_MIN<Math.abs(data.getNum())?YMARK_MIN:Math.abs(data.getNum());
        }
        LogUtil.i(TAG, "真是YMARK_MAX="+YMARK_MAX+"   YMARK_MIN="+YMARK_MIN);
        //取最大值
        if(YMARK_MAX>0)
            YMARK_MAX *= 1.3f;
        else
            YMARK_MAX /= 1.3f;
        if(YMARK_MIN>0)
            YMARK_MIN /= 2.5f;
        else
            YMARK_MIN *= 2.5f;

        YMARK = (YMARK_MAX-YMARK_MIN)/(YMARK_NUM - 1);
        LogUtil.i(TAG, "Y轴YMARK_MIN="+YMARK_MIN+"   YMARK_MAX="+YMARK_MAX+"   YMARK="+YMARK);

        paintLabel.setTextSize(textSize);
        lableLead = FontUtil.getFontLeading(paintLabel);
        lableHeight = FontUtil.getFontHeight(paintLabel);
        DecimalFormat f = new DecimalFormat("0.00");
        float lableYLength = FontUtil.getFontlength(paintLabel,
                f.format(YMARK_MAX).length()>f.format(YMARK_MIN).length()?
                        f.format(YMARK_MAX):f.format(YMARK_MIN));
        drawRect = new RectF(getPaddingLeft()+lableYLength+textSpaceY,
                getPaddingTop() + lableHeight/2,getMeasuredWidth()-getPaddingRight(),
                getMeasuredHeight()-getPaddingBottom()-lableHeight-textSpaceX);
        centerPoint = new PointF(drawRect.left, drawRect.top+(drawRect.bottom-drawRect.top)/2);
        oneWidth = (drawRect.right - drawRect.left)/count;
    }

    /**绘制图表基本框架*/
    @Override
    public void drawDefult(Canvas canvas) {
    }

    /**绘制debug辅助线*/
    @Override
    public void drawDebug(Canvas canvas) {
        super.drawDebug(canvas);
    }

    /**绘制图表*/
    @Override
    public void drawChart(Canvas canvas) {
        if(null==dataList || dataList.size()<=0)
            return;
        float yMarkSpace = (drawRect.bottom - drawRect.top)/(YMARK_NUM-1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth*1.5f);
        paint.setColor(defColor);
        //绘制X轴直线
        canvas.drawLine(drawRect.left, drawRect.bottom, drawRect.right, drawRect.bottom, paint);
        //绘制Y刻度
        paintLabel.setTextSize(textSize);
        paintLabel.setColor(textColor);
        float lableLength;
        for (int i = 0; i < YMARK_NUM; i++) {
            String text = new DecimalFormat("0.00").format(YMARK_MIN + i * YMARK);
            lableLength = FontUtil.getFontlength(paintLabel, text);
            canvas.drawText(text,
                    drawRect.left - textSpaceY - lableLength,
                    drawRect.bottom - yMarkSpace * i - lableHeight/2+ lableLead, paintLabel);
        }

        //绘制数据 和 X刻度
        Path path = new Path();
        PointF firstPoint = null;
        PointF lastPoint = null;
        float pointYMin= Integer.MAX_VALUE;
        for(int i = 0 ; i<dataList.size(); i++) {
            //计算当前数据在图表上的坐标
            float x = drawRect.left + oneWidth * i + oneWidth / 2;
            float y = drawRect.bottom -
                    (drawRect.bottom - drawRect.top) / (YMARK_MAX - YMARK_MIN) * (dataList.get(i).getNum() - YMARK_MIN);
            if (i == 0) {
                firstPoint = new PointF(x, y);
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
                //quadTo：二阶贝塞尔曲线连接前后两点，这样使得曲线更加平滑
//                path.quadTo(lastPoint.x, lastPoint.y, x, y);
            }
            lastPoint = new PointF(x, y);
            pointYMin = pointYMin>y?y:pointYMin;
            //绘制X刻度
            if(XMARK_INDEX.contains(i)){
                lableLength = FontUtil.getFontlength(paintLabel, dataList.get(i).getName());
                paint.setColor(defColor);
                paint.setStrokeWidth(lineWidth);
                canvas.drawLine(x, drawRect.bottom,
                        x, drawRect.bottom-DensityUtil.dip2px(getContext(), 3), paint);
                canvas.drawText(dataList.get(i).getName(),
                        x - lableLength/2,
                        drawRect.bottom + textSpaceX + lableLead, paintLabel);
            }
        }
        paint.setStrokeWidth(lineSize);
        paint.setColor(lineColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        canvas.drawPath(path,paint);
        path.lineTo(lastPoint.x, drawRect.bottom);
        path.lineTo(firstPoint.x, drawRect.bottom);
        path.lineTo(firstPoint.x, firstPoint.y);
        path.close(); //闭合覆盖区域
//        LogUtil.w(TAG, "阴影："+pointYMin + "   "+drawRect.bottom);
        LinearGradient linearGradient=new LinearGradient(getMeasuredWidth()/2, drawRect.bottom,
                getMeasuredWidth()/2,pointYMin, shadowColor,  shadowPositions,Shader.TileMode.CLAMP);
        //new float[]{},中的数据表示相对位置，将150,50,150,300，划分10个单位，.3，.6，.9表示它的绝对位置。300到400，将直接画出rgb（0,232,210）
        paint.setShader(linearGradient);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path,paint);
        paint.setShader(null);  //重置

    }

    @Override
    protected void onTouchMoved(PointF point) {
        if(null==dataList)
            return;
        onFocus = (null != point);
        if(null != point && null!=dataList && dataList.size()>0) {
            if(point.x>drawRect.right || point.x<drawRect.left)
                return;
            try {
                //获取焦点对应的数据的索引
                int index = (int) (((point.x - drawRect.left) / oneWidth) + (((point.x - drawRect.left) % oneWidth) > 0 ? 1 : 0));
                index -= 1;
                BaseChartData focous = dataList.get(index);
                LogUtil.w(TAG, "焦点坐标：" + point.x + "," + point.y + "   索引：" + index);
                LogUtil.w(TAG, "焦点：" + focous);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        invalidate();
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
