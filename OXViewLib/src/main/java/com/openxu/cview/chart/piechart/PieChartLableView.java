package com.openxu.cview.chart.piechart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.openxu.cview.chart.BaseChart;
import com.openxu.cview.chart.bean.PieChartBean;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.openxu.cview.chart.BaseChart.TOUCH_EVENT_TYPE.EVENT_Y;
import static com.openxu.cview.chart.piechart.PieChartLayout.TAG_MODUL.MODUL_LABLE;

/**
 * autour : openXu
 * date : 2017/7/24 10:46
 * className : PieChartLableView
 * version : 1.0
 * description : 占比饼状图表
 */
public class PieChartLableView extends BaseChart {

    private List<PieChartBean> dataList;

    private boolean showZeroPart = false;    //如果某部分占比为0， 是否显示

    protected RectF rectLable;     //图表矩形
    private int minTopPointY;      //滑动到最上面时的y坐标
    private int top;               //绘制的y坐标
    private int oneLableHeight;    //右侧标签单个高度,需要计算

    /**设置的属性*/
    private int rectW;
    private int rectH;
    private int rectRaidus;     //矩形圆角
    private int rectSpace;   //右侧标签上下间距
    private int leftSpace;
    private int textSize = 90;  //文字大小
    private int textColor;
    private int arrColorRgb[][];

    private PieChartLayout.TAG_TYPE tagType;   //TAG展示类型
    private PieChartLayout.TAG_MODUL tagModul;   //TAG展示位置

    public PieChartLableView(Context context) {
        super(context, null);
    }
    public PieChartLableView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }
    public PieChartLableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        touchEventType = EVENT_Y;
        dataList = new ArrayList<>();
        setClickable(true);
    }
    /***********************************设置属性set方法**********************************/
    public void setArrColorRgb(int[][] arrColorRgb) {
        this.arrColorRgb = arrColorRgb;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
    public void setShowZeroPart(boolean showZeroPart) {
        this.showZeroPart = showZeroPart;
    }
    public void setRectW(int rectW) {
        this.rectW = rectW;
    }

    public void setRectH(int rectH) {
        this.rectH = rectH;
    }

    public void setRectRaidus(int rectRaidus) {
        this.rectRaidus = rectRaidus;
    }

    public void setTagType(PieChartLayout.TAG_TYPE tagType) {
        this.tagType = tagType;
    }

    public void setTagModul(PieChartLayout.TAG_MODUL tagModul) {
        this.tagModul = tagModul;
    }

    public void setRectSpace(int rectSpace) {
        this.rectSpace = rectSpace;
    }

    public void setLeftSpace(int leftSpace) {
        this.leftSpace = leftSpace;
    }
    /***********************************设置属性set方法over**********************************/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        centerPoint = new PointF(widthSize/2, heightSize/2);
        rectLable = new RectF(0,0,widthSize,  heightSize);
        setMeasuredDimension(widthSize, heightSize);
        evaluatorLable();
    }

    @Override
    public void dispatchTouchEvent1(MotionEvent event) {
        int move = (int)(event.getY() - lastTouchPoint.y);
        if(top + mMoveLen <= minTopPointY){
            if(move<0){
                LogUtil.e(TAG, "已经到最下面了，还往上滑，不行");
                getParent().requestDisallowInterceptTouchEvent(false);
            }else{
                getParent().requestDisallowInterceptTouchEvent(true);
            }
        }else if(top + mMoveLen>=rectSpace){
            if(move>0){
                LogUtil.w(TAG, "已经划到头了，还往下滑，不行");
                getParent().requestDisallowInterceptTouchEvent(false);
            }else{
                getParent().requestDisallowInterceptTouchEvent(true);
            }
        }else{
            //请求所有父控件及祖宗控件不要拦截事件
            getParent().requestDisallowInterceptTouchEvent(true);
            LogUtil.i(TAG, "正常请求事件");
        }
    }

    @Override
    protected void evaluatorFling(float fling) {
        if(top + mMoveLen + fling <= minTopPointY){
            mMoveLen = minTopPointY-rectSpace;
        }else if(top + mMoveLen+ fling>=rectSpace){
            mMoveLen = 0;
        } else{
            mMoveLen += fling;
        }
    }

    /**
     * 设置数据
     */
    public void setData(List<PieChartBean> dataList){
        this.dataList.clear();
        if(dataList!=null)
            this.dataList.addAll(dataList);
        evaluatorLable();
        startDraw = false;
        invalidate();
    }
    /**计算右侧标签相关坐标值*/
    private void evaluatorLable(){
        paintLabel.setTextSize(textSize);
        oneLableHeight = (int) FontUtil.getFontHeight(paintLabel);
        //字和矩形中高度的较大值
        oneLableHeight = oneLableHeight>rectH?oneLableHeight:rectH;
        int allHeight = 0 ;
        //总高度
        for(PieChartBean bean : dataList){
            if(bean.getNum() == 0 && !showZeroPart){
                continue;
            }
            allHeight += oneLableHeight+rectSpace;
        }
        if(allHeight>0)
            allHeight -= rectSpace;

//        int allHeight = (oneLableHeight+rectSpace)*dataList.size() - rectSpace;
        int contentH = getMeasuredHeight() - rectSpace*2;
        touchEnable = allHeight>contentH;
        if(touchEnable){
            //超出总高度了
            top = rectSpace;
            minTopPointY = -allHeight - rectSpace + getMeasuredHeight();
            if(debug)
            LogUtil.i(TAG, "边界"+minTopPointY+"     "+getMeasuredHeight());
        }else{
            top = (getMeasuredHeight()-allHeight)/2;
        }
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

        int topY = top + mMoveLen;

        paintLabel.setTextSize(textSize);
        int lableH =(int) FontUtil.getFontHeight(paintLabel);
        int lableL =(int) FontUtil.getFontLeading(paintLabel);

        paint.setStyle(Paint.Style.FILL);

        int index = -1;
        for(int i = 0; i < dataList.size(); i++){
            PieChartBean bean = dataList.get(i);
            if(bean.getNum() == 0 && !showZeroPart){
                continue;
            }
            index++;
            paint.setARGB(255, arrColorRgb[index%arrColorRgb.length][0], arrColorRgb[index%arrColorRgb.length][1], arrColorRgb[index%arrColorRgb.length][2]);
            paintLabel.setARGB(255, arrColorRgb[index%arrColorRgb.length][0], arrColorRgb[index%arrColorRgb.length][1], arrColorRgb[index%arrColorRgb.length][2]);

            int rectTop = topY+(oneLableHeight-rectH)/2;
            RectF rect = new RectF(leftSpace, rectTop, leftSpace+rectW, rectTop+rectH);
            canvas.drawRoundRect(rect, rectRaidus, rectRaidus, paint);


            if(textColor!=0) {
                paintLabel.setColor(textColor);
            }
            rectTop = topY+(oneLableHeight-lableH)/2;
            canvas.drawText(bean.getName(), leftSpace+rectW+leftSpace , rectTop+lableL , paintLabel);
            float textW = FontUtil.getFontlength(paintLabel, bean.getName());
            /**5、绘制指示标签*/
            if(MODUL_LABLE == tagModul){
                String tagText = "";
                if (tagType == PieChartLayout.TAG_TYPE.TYPE_NUM) {
                    tagText = bean.getNum() + "";
                } else if (tagType == PieChartLayout.TAG_TYPE.TYPE_PERCENT) {
                    DecimalFormat decimalFormat = new DecimalFormat("0.0%");
                    tagText = decimalFormat.format(((float) bean.getNum() / (float) total));
                }
                float tagW = FontUtil.getFontlength(paintLabel, tagText);
                canvas.drawText(tagText, getMeasuredWidth()-tagW- DensityUtil.dip2px(getContext(),3), rectTop+lableL , paintLabel);

            /*    DashPathEffect dashPathEffect = new DashPathEffect(new float[]{8,10,8,10}, 0);
                paint.setColor(Color.RED);
//                paint.setPathEffect(dashPathEffect);
                paint.setStrokeWidth(4);
                Path path = new Path();
                path.moveTo(leftSpace+rectW+leftSpace+textW, rectTop);
                path.lineTo(getMeasuredWidth()-tagW- DensityUtil.dip2px(getContext(),3), rectTop);
                LogUtil.i(TAG, "绘制虚线"+path);
                canvas.drawPath(path,paint);*/
            }
            topY += oneLableHeight + rectSpace;
        }

    }
    /**创建动画*/
    @Override
    protected ValueAnimator initAnim() {
        return null;
    }
    /**动画值变化之后计算数据*/
    @Override
    protected void evaluatorData(ValueAnimator animation) {

    }


}
