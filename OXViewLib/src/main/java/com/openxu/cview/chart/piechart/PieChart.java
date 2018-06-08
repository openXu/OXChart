package com.openxu.cview.chart.piechart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.chart.BaseChart;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.chart.bean.ChartLable;
import com.openxu.cview.chart.bean.PieChartBean;
import com.openxu.utils.FontUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.openxu.cview.chart.piechart.PieChartLayout.TAG_MODUL.MODUL_CHART;

/**
 * autour : openXu
 * date : 2017/7/24 10:46
 * className : ProgressPieChart
 * version : 1.0
 * description : 占比饼状图表
 */
public class PieChart extends BaseChart {

    private List<PieChartBean> dataList;
    private List<ChartLable> lableList;   //需要绘制的lable集合

    /**设置的属性*/
    private boolean showZeroPart = false;    //如果某部分占比为0， 是否显示
    private int centerLableSpace;
    //圆环宽度，如果值>0,则为空心圆环，内环为白色，可以在内环中绘制字
    private int ringWidth;
    private int lineLenth;
    private int outSpace;
    private int textSpace;

    private PieChartLayout.TAG_TYPE tagType;   //TAG展示类型
    private PieChartLayout.TAG_MODUL tagModul;   //TAG展示位置
    private int tagTextSize;   //tag文字大小
    private int tagTextColor;

    /**计算*/
    private int chartSize;            //饼状图大小
    private int chartRaidus;          //饼状图半径
    private float tagMaxW;

    private Paint paintSelected;

    //RGB颜色数组
   private int arrColorRgb[][];


    public PieChart(Context context) {
        super(context, null);
    }
    public PieChart(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }
    public PieChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        dataList = new ArrayList<>();
        startAngle = -90;  //开始的角度

        paintSelected = new Paint();
        paintSelected.setColor(Color.LTGRAY);
        paintSelected.setStyle(Paint.Style.STROKE);//设置空心
        paintSelected.setStrokeWidth(lineWidth*5);
        paintSelected.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        initSome();
        setMeasuredDimension(widthSize, heightSize);
    }

    private void initSome(){
        int widthSize = getMeasuredWidth();
        int heightSize = getMeasuredHeight();
        chartSize = widthSize>heightSize?heightSize:widthSize;
        //外圈半径=size/2-最大部分lble线长度-边距
        paintLabel.setTextSize(tagTextSize);
        tagMaxW = 0;
        if(MODUL_CHART == tagModul){
            switch (tagType){
                case TYPE_NUM:
                    tagMaxW = FontUtil.getFontlength(paintLabel, "0.0%");
                    break;
                case TYPE_PERCENT:
                    tagMaxW = FontUtil.getFontlength(paintLabel, "000");
                    break;
            }
        }
        chartRaidus = chartSize/2-lineLenth-(int)tagMaxW-textSpace-outSpace;
        centerPoint = new PointF(widthSize/2, heightSize/2);
        if(widthSize<heightSize){
            rectChart = new RectF(0,(heightSize-widthSize)/2,widthSize, (heightSize+widthSize)/2);
        }else{
            rectChart = new RectF((widthSize-heightSize)/2, 0, (widthSize+heightSize)/2, heightSize);
        }
    }


    /***********************************设置属性set方法**********************************/

    public void setArrColorRgb(int[][] arrColorRgb) {
        this.arrColorRgb = arrColorRgb;
    }

    public void setTagTextSize(int tagTextSize) {
        this.tagTextSize = tagTextSize;
        initSome();
    }
    public void setTagTextColor(int tagTextColor) {
        this.tagTextColor = tagTextColor;
    }

    public void setTagType(PieChartLayout.TAG_TYPE tagType) {
        this.tagType = tagType;
        initSome();
    }

    public void setTagModul(PieChartLayout.TAG_MODUL tagModul) {
        this.tagModul = tagModul;
        initSome();
    }

    public void setShowZeroPart(boolean showZeroPart) {
        this.showZeroPart = showZeroPart;
    }

    public void setCenterLableSpace(int centerLableSpace) {
        this.centerLableSpace = centerLableSpace;
    }

    public void setRingWidth(int ringWidth) {
        this.ringWidth = ringWidth;
    }

    public void setLineLenth(int lineLenth) {
        this.lineLenth = lineLenth;
    }

    public void setOutSpace(int outSpace) {
        this.outSpace = outSpace;
    }

    public void setTextSpace(int textSpace) {
        this.textSpace = textSpace;
    }

    /**
     * 设置数据
     */
    public void setData(List<PieChartBean> dataList, List<ChartLable> lableList){
        total = 0;
        this.lableList = lableList;
        this.dataList.clear();
        if(dataList!=null)
            this.dataList.addAll(dataList);
        for(PieChartBean bean : this.dataList){
            total += bean.getNum();
        }
        if(centerPoint!=null && centerPoint.x>0){
            startDraw = false;
            invalidate();
        }
    }
/***********************************设置属性set方法over**********************************/
    /**绘制图表基本框架*/
    @Override
    public void drawDefult(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(defColor);
        canvas.drawCircle(centerPoint.x,centerPoint.y,chartRaidus, paint);
        if(ringWidth>0){   //绘制空心圆圈
            paint.setColor(backColor);
            canvas.drawCircle(centerPoint.x,centerPoint.y,chartRaidus-ringWidth, paint);
        }
    }
    /**绘制debug辅助线*/
    @Override
    public void drawDebug(Canvas canvas) {
        super.drawDebug(canvas);
        canvas.drawCircle(centerPoint.x, centerPoint.y, chartRaidus, paint);
        canvas.drawCircle(centerPoint.x, centerPoint.y, chartRaidus+lineLenth, paint);
        canvas.drawCircle(centerPoint.x, centerPoint.y, chartRaidus+lineLenth+textSpace, paint);
        canvas.drawCircle(centerPoint.x, centerPoint.y, chartRaidus+lineLenth+textSpace+(int)tagMaxW, paint);
        canvas.drawCircle(centerPoint.x, centerPoint.y, chartRaidus+lineLenth+textSpace+(int)tagMaxW+outSpace, paint);
    }
    private int selectedIndex = -1;   //被选中的索引
    /**绘制图表*/
    @Override
    public void drawChart(Canvas canvas) {
        int index = -1;
        for(int i = 0; i < dataList.size(); i++){
            PieChartBean bean = dataList.get(i);
            if(bean.getNum() == 0){
                if(showZeroPart)
                    index++;
                continue;
            }
            index++;
            paint.setARGB(255, arrColorRgb[index%arrColorRgb.length][0], arrColorRgb[index%arrColorRgb.length][1], arrColorRgb[index%arrColorRgb.length][2]);
            paintLabel.setARGB(255, arrColorRgb[index%arrColorRgb.length][0], arrColorRgb[index%arrColorRgb.length][1], arrColorRgb[index%arrColorRgb.length][2]);

            if(selectedIndex == i){
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                paintLabel.setTypeface(Typeface.DEFAULT_BOLD);
            }else{
                paint.setTypeface(Typeface.DEFAULT);
                paintLabel.setTypeface(Typeface.DEFAULT);
            }

            /**1、绘制扇形*/
            paint.setStyle(Paint.Style.FILL);//设置实心
            canvas.drawArc(bean.getArcRect(), bean.getStartAngle(), bean.getSweepAngle(), true, paint);
            if(selectedIndex == i){
                //被选中的，绘制边界
                paintSelected.setStyle(Paint.Style.STROKE);//设置空心
                canvas.drawArc(bean.getArcRect(), bean.getStartAngle(), bean.getSweepAngle(), true, paintSelected);
            }
            if(MODUL_CHART == tagModul){
                /**2、绘制直线*/
                List<PointF> tagLinePoints = bean.getTagLinePoints();
                if (tagLinePoints != null && tagLinePoints.size() > 0) {
                    for (int p = 1; p < tagLinePoints.size(); p++) {
                        canvas.drawLine(tagLinePoints.get(p - 1).x, tagLinePoints.get(p - 1).y,
                                tagLinePoints.get(p).x, tagLinePoints.get(p).y, paint);
                    }
                }
                /**3、绘制指示标签*/
                paintLabel.setTextSize(tagTextSize);
                if (tagTextColor != 0) {
                    paintLabel.setColor(tagTextColor);
                }
                canvas.drawText(bean.getTagStr() + "", bean.getTagTextPoint().x, bean.getTagTextPoint().y, paintLabel);
            }

        }

        //绘制中心内圆
        if(ringWidth>0){
            paint.setColor(backColor);
            paint.setStyle(Paint.Style.FILL);//设置实心
            canvas.drawCircle(centerPoint.x, centerPoint.y, chartRaidus-ringWidth, paint);
        }


        /**4 绘制中间文字*/
        if(ringWidth>0 && lableList!=null &&lableList.size()>0){
            float textAllHeight = 0;
            float textW, textH, textL;
            for(ChartLable lable : lableList){
                paintLabel.setTextSize(lable.getTextSize());
                textH = FontUtil.getFontHeight(paintLabel);
                textAllHeight += (textH+centerLableSpace);
            }
            textAllHeight -= centerLableSpace;
            int top = (int)(centerPoint.y-textAllHeight/2);
            for(ChartLable lable : lableList){
                paintLabel.setColor(lable.getTextColor());
                paintLabel.setTextSize(lable.getTextSize());
                textW = FontUtil.getFontlength(paintLabel, lable.getText());
                textH = FontUtil.getFontHeight(paintLabel);
                textL = FontUtil.getFontLeading(paintLabel);
                canvas.drawText(lable.getText(), centerPoint.x-textW/2, top + textL, paintLabel);
                top += (textH+centerLableSpace);
            }
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
        float percentage = (float) animation.getAnimatedValue();
        evaluatorData(percentage);
    }

    /**计算各种绘制坐标*/
    private void evaluatorData(float animPre){
        paintLabel.setTextSize(tagTextSize);
        float tagTextLead = FontUtil.getFontLeading(paintLabel);
        float tagTextHeight = FontUtil.getFontHeight(paintLabel);
        float oneStartAngle = startAngle;
        for(int i = 0; i < dataList.size(); i++) {
            PieChartBean bean = dataList.get(i);
           /* if(bean.getNum() == 0 && !showZeroPart){
                continue;
            }*/
            /**1、绘制扇形*/
            float arcLeft = centerPoint.x - chartRaidus;  //扇形半径
            float arcTop = centerPoint.y - chartRaidus;
            float arcRight = centerPoint.x + chartRaidus;
            float arcBottom = centerPoint.y + chartRaidus;

//            float percentage = 360.0f / total * bean.getNum();
            float percentage = (bean.getNum()==0?0:(360.0f / total * bean.getNum())*animPre);
            bean.setArcRect(new RectF(arcLeft, arcTop, arcRight, arcBottom));
            bean.setStartAngle(oneStartAngle);
            bean.setSweepAngle(percentage);

            /**2、计算扇形区域*/
            arcLeft = centerPoint.x - chartSize;
            arcTop = centerPoint.y - chartSize;
            arcRight = centerPoint.x + chartSize;
            arcBottom = centerPoint.y + chartSize;
            Path allPath = new Path();
            allPath.moveTo(centerPoint.x, centerPoint.y);//添加原始点
            float ovalX = centerPoint.x + (float) (chartRaidus * Math.cos(Math.toRadians(oneStartAngle)));
            float ovalY = centerPoint.y + (float) (chartRaidus * Math.sin(Math.toRadians(oneStartAngle)));
            allPath.lineTo(ovalX, ovalY);
            RectF touchOval = new RectF(arcLeft, arcTop, arcRight, arcBottom);
            allPath.addArc(touchOval, oneStartAngle, percentage);
            allPath.lineTo(centerPoint.x, centerPoint.y);
            allPath.close();
            RectF r = new RectF();
            allPath.computeBounds(r, true);
            Region region = new Region();
            region.setPath(allPath, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
            bean.setRegion(region);

            if(MODUL_CHART == tagModul) {
                /**3、绘制直线*/
                //确定直线的起始和结束的点的位置
                float startX = centerPoint.x + (float) (chartRaidus * Math.cos(Math.toRadians(oneStartAngle + percentage / 2)));
                float startY = centerPoint.y + (float) (chartRaidus * Math.sin(Math.toRadians(oneStartAngle + percentage / 2)));
                float endX = centerPoint.x + (float) ((chartRaidus + lineLenth - 20) * Math.cos(Math.toRadians(oneStartAngle + percentage / 2)));
                float endY = centerPoint.y + (float) ((chartRaidus + lineLenth - 20) * Math.sin(Math.toRadians(oneStartAngle + percentage / 2)));
                boolean isRight = true;
                float lineAngle = oneStartAngle + percentage / 2;
                if (lineAngle > 90 && lineAngle < 270) {
                    isRight = false;
                }
//            LogUtil.i(TAG, "直线坐标：start=("+startX+","+startY +")  end=("+endX+","+endY+")"+"   lineAngle="+lineAngle+"  isRight="+isRight);
                List<PointF> tagLinePoints = new ArrayList<>();
                tagLinePoints.add(new PointF(startX, startY));
                tagLinePoints.add(new PointF(endX, endY));
                float textX = isRight ? (endX + 20) : (endX - 20);
                tagLinePoints.add(new PointF(textX, endY));
                bean.setTagLinePoints(tagLinePoints);

                /**3、绘制指示标签*/
                String tagText = "";
                paintLabel.setTextSize(tagTextSize);
                if (tagType == PieChartLayout.TAG_TYPE.TYPE_NUM) {
                    tagText = bean.getNum() + "";
                } else if (tagType == PieChartLayout.TAG_TYPE.TYPE_PERCENT) {
                    DecimalFormat decimalFormat = new DecimalFormat("0.0%");
                    tagText = (total==0?"/":decimalFormat.format(((float) bean.getNum() / (float) total)));
                }
                float textW = FontUtil.getFontlength(paintLabel, tagText);
                textX = isRight ? textX + textSpace : (textX - textW - textSpace);
                float textY = endY - tagTextHeight / 2 + tagTextLead;
                bean.setTagStr(tagText);
                bean.setTagTextPoint(new PointF(textX, textY));
            }

            /*开始角度累加*/
            oneStartAngle += percentage;
        }

    }


}
