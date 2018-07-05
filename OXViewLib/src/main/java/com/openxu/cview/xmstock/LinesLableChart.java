package com.openxu.cview.xmstock;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.openxu.cview.R;
import com.openxu.cview.chart.BaseChart;
import com.openxu.cview.xmstock.bean.DataPoint;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * autour : xiami
 * date : 2018/3/13 14:26
 * className : UpDownChart
 * version : 1.0
 * description : 股票涨跌图
 */
public class LinesLableChart extends BaseChart {

    //设置数据
    private String timeStr;    //时间
    private String[] lableArr; //标签

    private int columnNum = 1;   //展示的列数
    //设置线条颜色
    private int[] lineColor = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
    //设置坐标文字大小
    private int timeTextSize = (int)getResources().getDimension(R.dimen.ts_chart_time);
    private int lableTextSize = (int)getResources().getDimension(R.dimen.ts_chart_lable);
    //设置lable文字颜色
    private int textColorLable = getResources().getColor(R.color.tc_chart_lable);
    private int textColorTime = getResources().getColor(R.color.tc_chart_time);
    //设置字体与小圆点的距离
    private int textSpace = DensityUtil.dip2px(getContext(), 3);  //圆圈 space 上证指数
    private int itemSpace = DensityUtil.dip2px(getContext(), 12); //圆圈 上证指数  space  圆圈 上证指数
    private int rowsSpace = DensityUtil.dip2px(getContext(), 12); //行距
    private int radius = DensityUtil.dip2px(getContext(), 3);   //小圆圈半径

    /**计算*/
    private float lableTextHeight,lableTextLead;
    private float timeTextHeight,timeTextLead;
    //数据分组
    private List<List<DataPoint>> linePointList;
    private Map<String, Float> columnMaxLenMap;

    public LinesLableChart(Context context) {
        this(context, null);
    }
    public LinesLableChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public LinesLableChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
    }
    /***********************************设置属性set方法**********************************/
    public void setColumnNum(int columnNum) {
        this.columnNum = columnNum;
    }
    public void setLineColor(int[] lineColor) {
        this.lineColor = lineColor;
    }

    /***********************************设置属性set方法over**********************************/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        paintLabel.setTextSize(timeTextSize);
        timeTextHeight = FontUtil.getFontHeight(paintLabel);
        timeTextLead = FontUtil.getFontLeading(paintLabel);
        paintLabel.setTextSize(lableTextSize);
        lableTextHeight = FontUtil.getFontHeight(paintLabel);
        lableTextLead = FontUtil.getFontLeading(paintLabel);
        int rowsNum;
        if(null==lableArr || lableArr.length<=0){
            rowsNum = TextUtils.isEmpty(timeStr)?0:1;
        }else
            rowsNum = lableArr.length<=columnNum?1:(lableArr.length/columnNum+(lableArr.length%columnNum>0?1:0));

        int height = rowsNum==0?
                (int)timeTextHeight:
                (int)(lableTextHeight>timeTextHeight?lableTextHeight:timeTextHeight)*rowsNum + rowsSpace*(rowsNum-1);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
//        LogUtil.w(TAG, "时间文字高度"+timeTextHeight+"   lable文字高度"+lableTextHeight);
//        LogUtil.w(TAG, "lable总数"+lableArr.length+"   列数"+columnNum+"  行数"+rowsNum);
//        LogUtil.w(TAG, "高度"+height);
        setMeasuredDimension(widthSize, height+getPaddingTop()+getPaddingBottom());
        evaluatorByData();
    }


    /***************************************************/

    /**设置数据*/
    public void setData(String[] lableArr, String timeStr){
        this.lableArr = lableArr;
        this.timeStr = timeStr;
        if(getMeasuredWidth()>0) {
            evaluatorByData();
            startDraw = false;
            requestLayout();
        }
    }


    private void evaluatorByData() {
        if(null==lableArr || lableArr.length<=0)
            return;
        //①、分行
        linePointList = new ArrayList<>();
        List<DataPoint> rowData = null;
        int column = 0;
        int rowNum = 1;
        for(int i = 0; i<lableArr.length; i++){
//            LogUtil.i(TAG, lableArr[i]+ "   第"+i+"个lable分组，到第"+rowNum+"行 "+column+"列");
            if(column == 0)
                rowData = new ArrayList<>();
            rowData.add(new DataPoint(lableArr[i], 0, null));
            if(column == columnNum-1) {
                rowNum ++;
                column = 0;
                linePointList.add(rowData);
            }else
                column++;
        }

        columnMaxLenMap = new HashMap<>();
        for(int i = 0; i<columnNum;i++){
            columnMaxLenMap.put("c"+i, 0.0f);
        }

        //②、获取每列的最长字的长度
        paintLabel.setTextSize(lableTextSize);
        for(int i = 0 ; i< linePointList.size(); i++){   //遍历行
            rowData = linePointList.get(i);
            for(int j = 0; j< rowData.size(); j++){     //遍历列
                float textLength = FontUtil.getFontlength(paintLabel, rowData.get(j).getValueX());
                columnMaxLenMap.put("c"+j, textLength>columnMaxLenMap.get("c"+j)?textLength:columnMaxLenMap.get("c"+j));
//                LogUtil.w(TAG, "第"+j+"列:"+rowData.get(j).getValueX()+"   文字长度"+textLength);
            }
        }

    }

    @Override
    public void drawDefult(Canvas canvas) {
    }
    @Override
    public void drawDebug(Canvas canvas) {
        super.drawDebug(canvas);
    }

    /**绘制图表*/
    @Override
    public void drawChart(Canvas canvas) {
        if(null==lableArr || lableArr.length<=0)
            return;
        float rowHeight = lableTextHeight>timeTextHeight?lableTextHeight:timeTextHeight;
        paintLabel.setColor(textColorTime);
        //绘制时间
        paintLabel.setTextSize(timeTextSize);
        canvas.drawText(timeStr, getPaddingRight(),
                getPaddingTop() + (rowHeight- timeTextHeight)/2 + timeTextLead, paintLabel);

        paintLabel.setTextSize(lableTextSize);
        paintLabel.setColor(textColorLable);
        float rowTextMaxLen = 0;

        for(Float f : columnMaxLenMap.values()){
            rowTextMaxLen += f;
        }
        float left = getMeasuredWidth()-getPaddingRight()-rowTextMaxLen
                -(radius*2+textSpace)*columnNum-itemSpace*(columnNum-1);

        paint.setStyle(Paint.Style.FILL);
        for(int i = 0 ; i< linePointList.size(); i++){   //遍历行
            float startX = left;
            List<DataPoint> rowData = linePointList.get(i);
            for(int j = 0; j< rowData.size(); j++){     //遍历列
                //点
                paint.setColor(lineColor[i*columnNum+j]);
                canvas.drawCircle(startX+radius, getPaddingTop() +
                        i*(rowHeight+rowsSpace)+rowHeight/2, radius, paint);
                startX += (radius*2+textSpace);
                //文字
                canvas.drawText(rowData.get(j).getValueX(), startX,getPaddingTop() +
                                i*(rowHeight+rowsSpace)+(rowHeight-lableTextHeight)/2+lableTextLead, paintLabel);
                startX += (columnMaxLenMap.get("c"+j)+itemSpace);
            }
        }

    }


    @Override
    protected ValueAnimator initAnim() {
        return null;
    }

    @Override
    protected void evaluatorData(ValueAnimator animation) {

    }



}
