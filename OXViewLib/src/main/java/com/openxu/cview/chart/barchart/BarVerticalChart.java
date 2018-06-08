package com.openxu.cview.chart.barchart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;
import com.openxu.cview.chart.BaseChart;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.cview.chart.bean.BarBean;
import com.openxu.cview.R;

import java.util.ArrayList;
import java.util.List;

import static com.openxu.cview.chart.BaseChart.TOUCH_EVENT_TYPE.EVENT_X;

/**
 * autour : openXu
 * date : 2017/7/24 10:46
 * className : BarVerticalChart
 * version : 1.0
 * description : 竖向(柱子竖直) 柱状图，支持多柱
 */
public class BarVerticalChart extends BaseChart {

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

    public BarVerticalChart(Context context) {
        this(context, null);
    }

    public BarVerticalChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarVerticalChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        touchEventType = EVENT_X;
        dataList = new ArrayList<>();
        strList = new ArrayList<>();
    }

    public void setShowEnd(boolean showEnd) {
        this.showEnd = showEnd;
    }

    /***********************************设置属性set方法over**********************************/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        evaluatorByData();
        invalidate();
    }

    /**
     * 设置数据后，计算相关值
     */
    private void evaluatorByData() {
        total = 0;
        //三种字体计算
        paintLabel.setTextSize(textSizeCoordinate);
        heightCoordinate = (int) FontUtil.getFontHeight(paintLabel);
        leadCoordinate = (int) FontUtil.getFontLeading(paintLabel);
        paintLabel.setTextSize(textSizeTag);
        heightTag = (int) FontUtil.getFontHeight(paintLabel);
        leadTag = (int) FontUtil.getFontLeading(paintLabel);
        paintLabel.setTextSize(textSizeLable);
        heightLable = (int) FontUtil.getFontHeight(paintLabel);
        leadLable = (int) FontUtil.getFontLeading(paintLabel);
        //下方lable范围计算
        int lableH = 0;
        if (showLable) {
            lableH = (heightLable > rectH ? heightLable : rectH) + lableTopSpace;
            lableRect = new RectF(rectChart.left, getMeasuredHeight() - getPaddingBottom() - lableH,
                    rectChart.right, rectChart.bottom);
        }
        //Y轴开始绘制的坐标 = 控件高度-padingB-lable高度-X轴刻度字高度-X刻度间距
        zeroPoint.y = getMeasuredHeight() - getPaddingBottom() - lableH - heightCoordinate - textSpace;

        LogUtil.w(TAG, "lableRect：" + lableRect + "  lableH=" + lableH + "   heightCoordinate=" + heightCoordinate + "   textSpace=" + textSpace);
        YMARK_MAX = 1;
        /*计算Y刻度最大值*/
        for (List<BarBean> list : dataList) {
            for (BarBean bean : list) {
                total += bean.getNum();
                YMARK_MAX = (int) bean.getNum() > YMARK_MAX ? (int) bean.getNum() : YMARK_MAX;
            }
        }
        LogUtil.i(TAG, "真实YMARK_MAX=" + YMARK_MAX);
        if (YMARK_MAX <= 5)
            YMARK_MAX = 5;
        YMARK = YMARK_MAX / YMARK_NUM + 1;

        int MARK = (Integer.parseInt((YMARK + "").substring(0, 1)) + 1);

        if ((YMARK + "").length() == 1) {
            //YMARK = 1、2、5、10
            YMARK = (YMARK == 3 || YMARK == 4 || YMARK == 6 || YMARK == 7 || YMARK == 8 || YMARK == 9) ? ((YMARK == 3 || YMARK == 4) ? 5 : 10) : YMARK;
        } else if ((YMARK + "").length() == 2) {
            YMARK = MARK * 10;
        } else if ((YMARK + "").length() == 3) {
            YMARK = MARK * 100;
        } else if ((YMARK + "").length() == 4) {
            YMARK = MARK * 1000;
        } else if ((YMARK + "").length() == 5) {
            YMARK = MARK * 10000;
        } else if ((YMARK + "").length() == 6) {
            YMARK = MARK * 100000;
        }
        YMARK_MAX = YMARK * YMARK_NUM;

        LogUtil.i(TAG, "计算YMARK_MAX=" + YMARK_MAX + "   YMARK=" + YMARK);

        //Y轴刻度间距值
        YMARK_ALL_H = (int) (zeroPoint.y - getPaddingTop() - heightCoordinate);
        YMARK_H = (int) ((float) YMARK_ALL_H / YMARK_NUM);

        //单份柱子（包含多个及其间距）宽度
        oneBarW = (barWidth * barNum) + (barSpace * (barNum - 1));

        int allW = (oneBarW + barItemSpace) * dataList.size();   //每份柱子宽度+item间距（其中包含第一个item左边和最后一个item右边的半份间距）
        paintLabel.setTextSize(textSizeCoordinate);
        if(drawLine){
            YMARK_MAX_WIDTH = (int) FontUtil.getFontlength(paintLabel, YMARK_MAX + "");
        }else{
            YMARK_MAX_WIDTH = 0;
        }

        int contentW = (int) (rectChart.right - rectChart.left - YMARK_MAX_WIDTH - textSpace);
        touchEnable = allW > contentW;

        zeroPoint.x = (int) rectChart.left + YMARK_MAX_WIDTH + textSpace;
        leftStartPointX = (int) zeroPoint.x + barItemSpace / 2;  //从左侧开始绘制的X坐标

        if (touchEnable) {
            //超出总宽度了
            minLeftPointX = -allW + (int) rectChart.right;
            mMoveLen = showEnd?(minLeftPointX - leftStartPointX):0;
        } else {
            minLeftPointX = 0;
            mMoveLen = 0;
        }

        LogUtil.w(TAG, "柱状图表宽高：" + getMeasuredWidth() + "*" + getMeasuredHeight() +
                "  图表范围" + rectChart + "   圆点坐标zeroPoint=" + zeroPoint);
        LogUtil.w(TAG, "YMARK_MAX=" + YMARK_MAX + "   YMARK=" + YMARK + "  YMARK_H=" + YMARK_H + "   YMARK_MAX_WIDTH=" + YMARK_MAX_WIDTH);
        LogUtil.w(TAG, "minLeftPointX=" + minLeftPointX + "   mMoveLen=" + mMoveLen + "  leftStartPointX=" + leftStartPointX);
    }

    /**
     * 绘制图表基本框架
     */
    @Override
    public void drawDefult(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(defColor);
        //float startX, float startY, float stopX, float stopY
        for (int i = 0; i <= YMARK_MAX / YMARK; i++) {
            //绘制横向X轴刻度
            float startX = zeroPoint.x;
            float startY = zeroPoint.y - (i * YMARK_H);
            float stopX = rectChart.right;
            float stopY = startY;
            if (drawLine) {
                canvas.drawLine(startX, startY, stopX, stopY, paint);
            }
//            绘制最下面的基线
            if(drawBottomLine&&i==0){
                canvas.drawLine(startX, startY, stopX, stopY, paint);
            }


//            LogUtil.w(TAG, "绘制直线start="+startX+"*"+startY+"  stop="+stopX+"*"+stopY);

            //绘制Y刻度
//            paintLabel.setTextSize(textSizeCoordinate);
//            String textY = (YMARK*i)+"";
//            float tw = FontUtil.getFontlength(paintLabel, textY);
//            canvas.drawText(textY, getPaddingLeft()+(YMARK_MAX_WIDTH-tw), startY - heightCoordinate/2+leadCoordinate,paintLabel);
        }

        //绘制左侧Y轴
        if (drawLine) {
            canvas.drawLine(zeroPoint.x + lineWidth / 2, zeroPoint.y, zeroPoint.x + lineWidth / 2, getPaddingTop(), paint);
        }

        if (showLable) {
            //绘制lable
            List<String> lableList = new ArrayList<>();
            if (dataList.size() > 0) {
                for (BarBean bean : dataList.get(0)) {
                    lableList.add(bean.getName());
                }
            }

            paintLabel.setTextSize(textSizeLable);
            paintLabel.setColor(textColorLable);
            if (lableList != null && lableList.size() > 0) {
                int lableAllW = 0;
                for (int i = 0; i < lableList.size(); i++) {
                    lableAllW += (rectW + textLableSpace + FontUtil.getFontlength(paintLabel, lableList.get(i)));
                }
                lableAllW += (lableItemSpace * lableList.size() - 1);
                int lableStart = 0;
                if (lableAllW > ((int) (lableRect.right - lableRect.left))) {
                    //超过了，从left开始画
                    lableStart = (int) lableRect.left;
                } else {
                    //没超过，话中间
                    lableStart = (int) (lableRect.left + ((lableRect.right - lableRect.left) / 2 - lableAllW / 2));
                }
                int left = lableStart;

                for (int i = 0; i < lableList.size(); i++) {
                    String lable = lableList.get(i);
                    int tw = (int) FontUtil.getFontlength(paintLabel, lable);
                    paint.setColor(barColor[i]);

                    float rectTop = lableRect.bottom - rectH;
                    RectF rect = new RectF(left, rectTop, left + rectW, rectTop + rectH);
                    canvas.drawRect(rect, paint);

                    left += (rectW + textLableSpace);

                    rectTop = rectTop + rectH / 2 - heightLable / 2 + leadLable;
                    canvas.drawText(lable, left, rectTop, paintLabel);

                    left += (tw + lableItemSpace);
                }
            }
        }

    }
    //    是否绘制最基础的线
    public void setBaseLineAndText(boolean drawLine) {
        this.drawLine = drawLine;
    }


    //    是否绘制最下面的基线
    public void setBottomLine(boolean drawBottomLine) {
        this.drawBottomLine = drawBottomLine;
    }


    /**
     * 绘制debug辅助线
     */
    @Override
    public void drawDebug(Canvas canvas) {
        super.drawDebug(canvas);
        paint.setStyle(Paint.Style.STROKE);//设置空心
        paint.setStrokeWidth(lineWidth);
        paint.setColor(Color.BLACK);
        canvas.drawRect(lableRect, paint);
    }

    /**
     * 绘制图表
     */
    @Override
    public void drawChart(Canvas canvas) {

        int leftStart = leftStartPointX + mMoveLen;

//        LogUtil.i(TAG, "leftStart："+leftStart+"   zezeroPoint"+zeroPoint+"   barItemSpace="+barItemSpace+"   barWidth="+barWidth +"   oneBarW="+oneBarW);

        paint.setStyle(Paint.Style.FILL);
        LogUtil.w(TAG, "");

        for (int i = 0; i < dataList.size(); i++) {
            String str = strList.get(i);
            List<BarBean> list = dataList.get(i);
            //柱子开始绘制的X坐标
            int leftX = leftStart + (oneBarW + barItemSpace) * i;
            //绘制X刻度
            paintLabel.setTextSize(textSizeCoordinate);
            paintLabel.setColor(textColorCoordinate);
            float tw = FontUtil.getFontlength(paintLabel, str);
            canvas.drawText(str, leftX + oneBarW / 2 - tw / 2, zeroPoint.y + textSpace + leadCoordinate, paintLabel);
//            LogUtil.i(TAG, "绘制X刻度：leftX="+leftX   +"    "+(leftX + oneBarW/2-tw/2)+"*"+(zeroPoint.y+textSpace + leadCoordinate));
//            LogUtil.i(TAG, "leftStartPointX="+leftStartPointX+"  leftStart="+leftStart+ "   oneBarW="+oneBarW   +"    barWidth="+barWidth+"   barSpace="+barSpace);
            paintLabel.setTextSize(textSizeTag);
            paintLabel.setColor(textColorTag);
            for (int j = 0; j < list.size(); j++) {
                BarBean bean = list.get(j);
                paint.setColor(barColor[j]);
                float top = (zeroPoint.y - YMARK_ALL_H * (bean.getNum() / YMARK_MAX) * animPro);
                RectF br = new RectF(leftX, top, leftX + barWidth, zeroPoint.y);
                canvas.drawRect(br, paint);

                str = (int) bean.getNum() + "";
                tw = FontUtil.getFontlength(paintLabel, str);
                canvas.drawText(str, leftX + barWidth / 2 - tw / 2, top - textSpace - heightTag + leadTag, paintLabel);

//                LogUtil.d(TAG, "绘制bar："+(br)+"    "+getMeasuredWidth()+"*"+getMeasuredHeight());
                leftX += (barWidth + barSpace);
            }
        }
        if (drawLine) {
            drawYmark(canvas);
        }

    }

    private void drawYmark(Canvas canvas) {
        //遮盖超出的柱状
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(backColor);
        canvas.drawRect(new RectF(0, 0, zeroPoint.x, lableRect.top), paint);
        canvas.drawRect(new RectF(rectChart.right, 0, getMeasuredWidth(), lableRect.top), paint);
//        LogUtil.d(TAG, "遮盖超出的柱状："+new RectF(0,0,zeroPoint.x, lableRect.top));
//        LogUtil.d(TAG, "遮盖超出的柱状："+new RectF(rectChart.right,0,getMeasuredWidth(), lableRect.top));

        paintLabel.setTextSize(textSizeCoordinate);
        paintLabel.setColor(textColorCoordinate);
        for (int i = 0; i <= YMARK_MAX / YMARK; i++) {
            //绘制Y刻度
            String textY = (YMARK * i) + "";
            float tw = FontUtil.getFontlength(paintLabel, textY);
            canvas.drawText(textY, getPaddingLeft() + (YMARK_MAX_WIDTH - tw), zeroPoint.y - (i * YMARK_H) - heightCoordinate / 2 + leadCoordinate, paintLabel);
        }
    }

    /**
     * 创建动画
     */
    @Override
    protected ValueAnimator initAnim() {
        if (dataList.size() > 0) {
            ValueAnimator anim = ValueAnimator.ofObject(new AngleEvaluator(), 0f, 1f);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            return anim;
        }
        return null;
    }

    /**
     * 动画值变化之后计算数据
     */
    @Override
    protected void evaluatorData(ValueAnimator animation) {
        animPro = (float) animation.getAnimatedValue();
    }

    /***************************************************/

    @Override
    protected void evaluatorFling(float fling) {
        LogUtil.i(TAG, "fling = " + fling);
        if (leftStartPointX + mMoveLen + fling <= minLeftPointX) {
            //最右侧
            mMoveLen = minLeftPointX - leftStartPointX;
            if (null != touchAnim && touchAnim.isRunning())
                touchAnim.cancel();
        } else if (leftStartPointX + mMoveLen + fling >= leftStartPointX) {
            //最左侧
            mMoveLen = 0;
            if (null != touchAnim && touchAnim.isRunning())
                touchAnim.cancel();
        } else {
            mMoveLen += fling;
        }
    }

    /***********************************设置属性set方法**********************************/
    public void setRectW(int rectW) {
        this.rectW = rectW;
    }

    public void setRectH(int rectH) {
        this.rectH = rectH;
    }

    public void setShowLable(boolean showLable) {
        this.showLable = showLable;
    }

    public void setBarNum(int barNum) {
        this.barNum = barNum;
    }

    public void setYMARK_NUM(int YMARK_NUM) {
        this.YMARK_NUM = YMARK_NUM;
    }

    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
    }

    public void setBarSpace(int barSpace) {
        this.barSpace = barSpace;
    }

    public void setBarItemSpace(int barItemSpace) {
        this.barItemSpace = barItemSpace;
    }

    public void setBarColor(int[] barColor) {
        this.barColor = barColor;
    }

    public void setTextSizeCoordinate(int textSizeCoordinate) {
        this.textSizeCoordinate = textSizeCoordinate;
    }

    public void setTextColorCoordinate(int textColorCoordinate) {
        this.textColorCoordinate = textColorCoordinate;
    }

    /***************************************************/

    public void setTextSizeTag(int textSizeTag) {
        this.textSizeTag = textSizeTag;
    }

    public void setTextColorTag(int textColorTag) {
        this.textColorTag = textColorTag;
    }

    public void setTextSizeLable(int textSizeLable) {
        this.textSizeLable = textSizeLable;
    }

    public void setTextColorLable(int textColorLable) {
        this.textColorLable = textColorLable;
    }

    public void setTextSpace(int textSpace) {
        this.textSpace = textSpace;
    }

    public void setTextLableSpace(int textLableSpace) {
        this.textLableSpace = textLableSpace;
    }

    public void setLableItemSpace(int lableItemSpace) {
        this.lableItemSpace = lableItemSpace;
    }

    public void setLableTopSpace(int lableTopSpace) {
        this.lableTopSpace = lableTopSpace;
    }

    /**
     * 设置数据
     *
     * @param dataList 柱状图数据
     * @param strXList X轴坐标数据
     */
    public void setData(List<List<BarBean>> dataList, List<String> strXList) {
        LogUtil.w(TAG, "柱状图设置数据" + dataList);
        this.strList.clear();
        this.dataList.clear();
        if (dataList != null)
            this.dataList.addAll(dataList);
        if (strXList != null)
            this.strList.addAll(strXList);
        if (getMeasuredWidth() > 0) {
            evaluatorByData();
            startDraw = false;
            invalidate();
        }
    }


}
