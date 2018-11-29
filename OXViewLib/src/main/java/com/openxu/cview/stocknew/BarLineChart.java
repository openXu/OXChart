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
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.stocknew.bean.BaseChartData;
import com.openxu.cview.stocknew.bean.BranchChartData;
import com.openxu.cview.stocknew.bean.GsyjChartData;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * autour : xiami
 * date : 2018/12/6 14:26
 * className : BarLineChart
 * version : 1.0
 * description : 公司业绩柱状折线图
 */
public class BarLineChart extends BaseChart {

    //设置数据
    private List<GsyjChartData> dataList = new ArrayList<>();

    /**可以设置的属性*/
    //设置Y轴刻度数量
    private int YMARK_NUM =  3;
    private int XMARK_NUM =  5;

    private int barColor = Color.parseColor("#7ecef4");
    private int barWidth = DensityUtil.dip2px(getContext(), 30); //柱子宽度
    private int lineColor = Color.parseColor("#ff7200");
    private int lineSize = DensityUtil.dip2px(getContext(), 2f);
    //设置坐标文字大小
    private int textSize = (int)getResources().getDimension(R.dimen.ts_chart_xy);
    //设置坐标文字颜色
    private int textColor = getResources().getColor(R.color.tc_chart_xy);
    //设置X坐标字体与横轴的距离
    private int textSpaceX = DensityUtil.dip2px(getContext(), 5);
    //设置Y坐标字体与横轴的距离
    private int textSpaceY = DensityUtil.dip2px(getContext(), 2);

    //y轴刻度数据类型（整数、小数）
    private YMARK_TYPE yMarkType = YMARK_TYPE.INTEGER;
    public enum YMARK_TYPE{
        DECIMAL,  /*百分比*/
        INTEGER
    }
    /**需要计算相关值*/
    private float oneWidth;
    private float lableLead, lableHeight;


    private float YMARK_BAR;    //Y轴刻度间隔
    private float YMARK_MAX_BAR =  Integer.MIN_VALUE;    //Y轴刻度最大值
    private float YMARK_MIN_BAR =  Integer.MAX_VALUE;    //Y轴刻度最小值

    private float YMARK_LINE;    //Y轴刻度间隔
    private float YMARK_MAX_LINE =  Integer.MIN_VALUE;    //Y轴刻度最大值
    private float YMARK_MIN_LINE =  Integer.MAX_VALUE;    //Y轴刻度最小值

    private List<Integer> XMARK_INDEX = new ArrayList<>();
    protected RectF drawRect;          //图表绘制范围矩形

    public BarLineChart(Context context) {
        this(context, null);
    }
    public BarLineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public BarLineChart(Context context, AttributeSet attrs, int defStyle) {
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
    public void setyMarkType(YMARK_TYPE yMarkType) {
        this.yMarkType = yMarkType;
    }
/***************************************************/

    /**
     * 设置数据
     */
    public void setData(List<GsyjChartData> dataList){
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

        /**计算Y刻度最大值和最小值以及幅度*/
        YMARK_MAX_BAR =  Integer.MIN_VALUE;    //Y轴刻度最大值
        YMARK_MIN_BAR = 0.0f;    //Y轴刻度最小值
        YMARK_MAX_LINE =  Integer.MIN_VALUE;
        YMARK_MIN_LINE = Integer.MAX_VALUE;
        for(GsyjChartData data : dataList) {
            YMARK_MAX_BAR = YMARK_MAX_BAR > data.getBarNum() ? YMARK_MAX_BAR :data.getBarNum();
            YMARK_MAX_LINE = YMARK_MAX_LINE > data.getLineNum() ? YMARK_MAX_LINE :data.getLineNum();
            YMARK_MIN_LINE = YMARK_MIN_LINE < data.getLineNum() ? YMARK_MIN_LINE : data.getLineNum();
        }

        LogUtil.i(TAG, "====YMARK_MAX_BAR="+YMARK_MAX_BAR+"   YMARK_MIN_BAR="+YMARK_MIN_BAR+"   YMARK_BAR="+YMARK_BAR);
        LogUtil.i(TAG, "----YMARK_MAX_LINE="+YMARK_MAX_LINE+"   YMARK_MIN_LINE="+YMARK_MIN_LINE+"   YMARK_LINE="+YMARK_LINE);

        //取最大值
        YMARK_MAX_BAR *= 1.1f;
        YMARK_BAR = (YMARK_MAX_BAR-YMARK_MIN_BAR)/(YMARK_NUM - 1);

        if(YMARK_MAX_LINE>0)
            YMARK_MAX_LINE *= 1.1f;
        else
            YMARK_MAX_LINE /= 1.1f;
        if(YMARK_MIN_LINE>0)
            YMARK_MIN_LINE /= 1.5f;
        else
            YMARK_MIN_LINE *= 1.5f;
        YMARK_LINE = (YMARK_MAX_LINE-YMARK_MIN_LINE)/(YMARK_NUM - 1);
        LogUtil.w(TAG, "====YMARK_MAX_BAR="+YMARK_MAX_BAR+"   YMARK_MIN_BAR="+YMARK_MIN_BAR+"   YMARK_BAR="+YMARK_BAR);
        LogUtil.w(TAG, "----YMARK_MAX_LINE="+YMARK_MAX_LINE+"   YMARK_MIN_LINE="+YMARK_MIN_LINE+"   YMARK_LINE="+YMARK_LINE);
        /**计算图标绘制范围*/
        paintLabel.setTextSize(textSize);
        lableLead = FontUtil.getFontLeading(paintLabel);
        lableHeight = FontUtil.getFontHeight(paintLabel);
        float lableBarYLength, lableLineYLength;
        if(yMarkType == YMARK_TYPE.INTEGER){
            lableBarYLength = FontUtil.getFontlength(paintLabel,
                    ((int)YMARK_MAX_BAR+"").length()>((int)YMARK_MIN_BAR+"").length()?(int)YMARK_MAX_BAR+"":(int)YMARK_MIN_BAR+"");
        }else{
            DecimalFormat f = new DecimalFormat("0.00");
            lableBarYLength = FontUtil.getFontlength(paintLabel,f.format(YMARK_MAX_BAR).length()>f.format(YMARK_MIN_BAR).length()?
                            f.format(YMARK_MAX_BAR):f.format(YMARK_MIN_BAR));
        }
        lableLineYLength = FontUtil.getFontlength(paintLabel,
                NumberFormatUtil.formattedDecimalToPercentage(YMARK_MAX_LINE).length()>
                        NumberFormatUtil.formattedDecimalToPercentage(YMARK_MIN_LINE).length()?
                        NumberFormatUtil.formattedDecimalToPercentage(YMARK_MAX_LINE):
                        NumberFormatUtil.formattedDecimalToPercentage(YMARK_MIN_LINE));

        drawRect = new RectF(getPaddingLeft()+lableBarYLength+textSpaceY,
                getPaddingTop() + lableHeight/2,getMeasuredWidth()-getPaddingRight()-lableLineYLength-textSpaceY,
                getMeasuredHeight()-getPaddingBottom()-lableHeight-textSpaceX);
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
        //绘制右侧Y刻度
        paintLabel.setTextSize(textSize);
        paintLabel.setColor(textColor);
        float lableLength;
        String yText;
        DecimalFormat f = new DecimalFormat("0.00");
        for (int i = 0; i < YMARK_NUM; i++) {
            if(yMarkType == YMARK_TYPE.INTEGER){
                yText = (int)(YMARK_MIN_BAR + i * YMARK_BAR) + "";
            }else{
                yText = f.format(YMARK_MIN_BAR + i * YMARK_BAR);
            }
            lableLength = FontUtil.getFontlength(paintLabel, yText);
            canvas.drawText(yText,drawRect.left - textSpaceY - lableLength,
                    drawRect.bottom - yMarkSpace * i - lableHeight/2+ lableLead, paintLabel);

            yText = NumberFormatUtil.formattedDecimalToPercentage(YMARK_MIN_LINE + i * YMARK_LINE);
            canvas.drawText(yText,drawRect.right + textSpaceY,
                    drawRect.bottom - yMarkSpace * i - lableHeight/2 + lableLead, paintLabel);
        }

        //绘制数据 和 X刻度
        float allHeight = drawRect.bottom-drawRect.top;
        float perHeight;
        paint.setStyle(Paint.Style.FILL);
        Path linePath = new Path();
        for(int i = 0; i<dataList.size(); i++){
            float centerX = drawRect.left+oneWidth*i + oneWidth/2;
            perHeight = dataList.get(i).getBarNum()/YMARK_MAX_BAR * allHeight * animPro;
            paint.setColor(barColor);
            canvas.drawRect(centerX-barWidth/2,drawRect.bottom-perHeight,
                    centerX+barWidth/2,drawRect.bottom, paint);
            //绘制折线小圆圈
            paint.setColor(lineColor);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(DensityUtil.dip2px(getContext(),1));
            perHeight = (dataList.get(i).getLineNum()-YMARK_MIN_LINE)/(YMARK_MAX_LINE-YMARK_MIN_LINE) * allHeight * animPro;
            canvas.drawCircle(centerX,drawRect.bottom-perHeight,DensityUtil.dip2px(getContext(),3), paint);
            if(i == 0)
                linePath.moveTo(centerX,drawRect.bottom-perHeight);
            else
                linePath.lineTo(centerX,drawRect.bottom-perHeight);

            //绘制X刻度
            if(XMARK_INDEX.contains(i)){
                lableLength = FontUtil.getFontlength(paintLabel, dataList.get(i).getName());
                canvas.drawText(dataList.get(i).getName(),
                        centerX - lableLength/2,
                        drawRect.bottom + textSpaceX + lableLead, paintLabel);
            }
        }
        //绘制折线
        paint.setStrokeWidth(lineSize);
        paint.setColor(lineColor);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(linePath,paint);
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
                GsyjChartData focous = dataList.get(index);
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
