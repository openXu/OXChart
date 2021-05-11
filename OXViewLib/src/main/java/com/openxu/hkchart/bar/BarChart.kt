package com.openxu.hkchart.bar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.ScaleGestureDetector
import com.openxu.hkchart.BaseChart
import com.openxu.hkchart.config.*
import com.openxu.utils.DensityUtil
import com.openxu.utils.FontUtil
import com.openxu.utils.LogUtil

/**
 * Author: openXu
 * Time: 2021/5/11 13:47
 * class: BarChart
 * Description:
 */
class BarChart : BaseChart<Bar> {

    constructor(context: Context) :this(context, null)
    constructor(context: Context, attrs: AttributeSet?) :this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int):super(context, attrs, defStyle){
    }

    /**设置 */
    private var barData = mutableListOf<Bar>()

    /**计算 */
    fun setData(barData: List<Bar>?) {
        this.barData.clear()
        barData?.let {
            this.barData.addAll(barData)
        }
        initial()
        chartConfig?.let {
            if (it.showAnim) chartAnimStarted = false
        }
        loading = false
    }


    private var groupWidth = 0f
    private lateinit var yAxisMark : YAxisMark
    private lateinit var xAxisMark : XAxisMark
    private var barColor = intArrayOf(
            Color.parseColor("#f46763"),
            Color.parseColor("#3cd595"),
            Color.parseColor("#4d7bff")) //柱颜色
    private var barWidth = DensityUtil.dip2px(context, 15f).toFloat() //柱宽度
    private var barSpace = DensityUtil.dip2px(context, 1f).toFloat() //双柱间的间距
    private var groupSpace = DensityUtil.dip2px(context, 25f).toFloat() //一组柱之间的间距（只有scrollAble==true时才生效）
    override fun initial(): Boolean {
        if(super.initial()) return true
        if(barData.isNullOrEmpty()) return true
        if(chartConfig==null)
            throw RuntimeException("---------请配置图表")
        val config = chartConfig as BarChartConfig
        if(null==config.xAxisMark)
            throw RuntimeException("---------请设置x坐标")
        if(null==config.yAxisMark)
            throw RuntimeException("---------请设置y坐标")
        xAxisMark = config.xAxisMark!!
        yAxisMark = config.yAxisMark!!
        barColor = config.barColor
        barWidth = config.barWidth
        barSpace = config.barSpace
        groupSpace = config.groupSpace

        paintText.textSize = xAxisMark.textSize.toFloat()
        xAxisMark.textHeight = FontUtil.getFontHeight(paintText)
        xAxisMark.textLead = FontUtil.getFontLeading(paintText)
        //确定图表最下放绘制位置
        rectChart.bottom = measuredHeight - paddingBottom - xAxisMark.textHeight - xAxisMark.textSpace
        xAxisMark.drawPointY = rectChart.bottom + xAxisMark.textSpace + xAxisMark.textLead
        calculateYMark()
        paintText.textSize = yAxisMark.textSize.toFloat()
        yAxisMark.textHeight = FontUtil.getFontHeight(paintText)
        yAxisMark.textLead = FontUtil.getFontLeading(paintText)
        val maxLable = yAxisMark.getMarkText(yAxisMark.cal_mark_max)
        rectChart.left = (paddingLeft + yAxisMark.textSpace + FontUtil.getFontlength(paintText, maxLable))
        rectChart.top = rectChart.top + yAxisMark.textHeight / 2

        val barNum: Int = barData[0].valuey.size
        if (config.scrollAble) {
            groupWidth = (config.barWidth * barNum + config.barSpace * (barNum - 1) + config.groupSpace).toFloat()
            val allWidth: Float = groupWidth * barData.size //总宽度
            scrollXMax = -(allWidth - rectChart.width())
            scrollx = if (config.displayScheme==DisplayScheme.SHOW_BEGIN) 0f else scrollXMax
        } else {
            config.groupSpace = (rectChart.width() - barData.size * (config.barWidth * barNum + config.barSpace * (barNum - 1))) / barData.size
            groupWidth = (config.barWidth * barNum + config.barSpace * (barNum - 1) + config.groupSpace).toFloat()
            scrollXMax = 0f
            scrollx = scrollXMax
        }
        Log.w(TAG, "计算groupWidth=$groupWidth   config.barWidth=$config.barWidth   scrollx=$scrollx")
        return true
    }
    
    override fun drawChart(canvas: Canvas?) {
        val yMarkSpace = (rectChart.bottom - rectChart.top) / (yAxisMark.lableNum - 1)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = yAxisMark.lineColor.toFloat()
        paint.color = yAxisMark.lineColor
        paintEffect.style = Paint.Style.STROKE
        paintEffect.strokeWidth = yAxisMark.lineWidth.toFloat()
        paintEffect.color = yAxisMark.lineColor
        paintText.textSize = yAxisMark.textSize.toFloat()
        paintText.color = yAxisMark.textColor
//        canvas.drawLine(rectChart.left, rectChart.top, rectChart.left, rectChart.bottom, paint);
        //        canvas.drawLine(rectChart.left, rectChart.top, rectChart.left, rectChart.bottom, paint);
        val effects: PathEffect = DashPathEffect(floatArrayOf(15f, 6f, 15f, 6f), 0f)
        paintEffect.pathEffect = effects
        for (i in 0 until yAxisMark.lableNum) {
            /**绘制横向线 */
            canvas!!.drawLine(rectChart.left, rectChart.bottom - yMarkSpace * i,
                    rectChart.right, rectChart.bottom - yMarkSpace * i, paint)
            /**绘制y刻度 */
            val text = yAxisMark.getMarkText(yAxisMark.cal_mark_min + i * yAxisMark.cal_mark)
            canvas!!.drawText(text,
                    rectChart.left - yAxisMark.textSpace - FontUtil.getFontlength(paintText, text),
                    rectChart.bottom - yMarkSpace * i - yAxisMark.textHeight / 2 + yAxisMark.textLead, paintText)
        }
        /**绘制柱状 */
        paint.style = Paint.Style.FILL
        val rect = RectF()
        val rectArc = RectF()
        val path = Path()
        //预算需要绘制的组的开始和结尾index，避免不必要的计算浪费性能
        val startIndex = (-scrollx / groupWidth).toInt()
        var endIndex = ((-scrollx + rectChart.width()) / groupWidth).toInt()
        endIndex = Math.min(endIndex, barData.size - 1)
//        for(int i = 0; i<barData.size(); i++){
        //        for(int i = 0; i<barData.size(); i++){
        for (i in startIndex..endIndex) {
            val group = barData[i]
            //一组
            /**绘制X刻度 */
            paintText.textSize = xAxisMark.textSize.toFloat()
            paintText.color = xAxisMark.textColor
            rect.left = scrollx + rectChart.left + i * groupWidth
            rect.right = rect.left + groupWidth
            //过滤掉绘制区域外的组
//            if(rect.right < rectChart.left || rect.left > rectChart.right) {
//                Log.w(TAG, "第"+i+"组超界了 "+rect.left+" "+rect.right);
//                continue;
//            }
            //裁剪画布，避免x刻度超出
            var restoreCount = canvas!!.save()
            canvas!!.clipRect(RectF(rectChart.left, rectChart.bottom,
                    rectChart.right, rectChart.bottom + xAxisMark.textSpace + xAxisMark.textHeight))
            canvas!!.drawText(group.valuex,
                    rect.left + groupWidth / 2 - FontUtil.getFontlength(paintText, group.valuex) / 2,
                    xAxisMark.drawPointY, paintText)
            canvas!!.restoreToCount(restoreCount)
            /**绘制柱状 */
            // 记录当前画布信息
            restoreCount = canvas!!.save()
            /**使用Canvas的clipRect和clipPath方法限制View的绘制区域 */
            canvas!!.clipRect(rectChart) //裁剪画布，只绘制rectChart的范围
            for (j in group.valuey.indices) {
                paint.color = barColor[j]
                //                float top = (zeroPoint.y - YMARK_ALL_H * (bean.getNum() / YMARK_MAX) * animPro);
                rect.left = rectChart.left + i * groupWidth + groupSpace / 2 + j * (barSpace + barWidth) + scrollx
                rect.right = rect.left + barWidth
                //过滤掉绘制区域外的柱
                if (rect.right < rectChart.left || rect.left > rectChart.right) continue
                if (group.valuey[j] == null) {
                    rect.top = rectChart.bottom
                } else {
                    rect.top = (rectChart.bottom - rectChart.height() / (yAxisMark.cal_mark_max - yAxisMark.cal_mark_min) *
                            (group.valuey[j] - yAxisMark.cal_mark_min) * chartAnimValue)
                }
                rect.bottom = rectChart.bottom
                rectArc.left = rect.left
                rectArc.top = rect.top
                rectArc.right = rect.right
                rectArc.bottom = rect.top + barWidth
                path.reset()
                path.moveTo(rect.left, rectChart.bottom)
                path.lineTo(rectArc.left, rectArc.bottom - rectArc.height() / 2)
                path.arcTo(rectArc, 180f, 180f)
                path.lineTo(rect.right, rect.bottom)
                path.close()
                //                Log.w(TAG, "---绘制"+i+"*"+j+" = "+group.get(j).getValuey()+" " +rect.top +"  "+rectChart.bottom);
                canvas!!.drawPath(path, paint)
                /**绘制y值 */
                if (group.valuey[j] != null) {
                    canvas!!.drawText(yAxisMark.getMarkText(group.valuey[j]),
                            rectArc.left + barWidth / 2 - FontUtil.getFontlength(paintText, yAxisMark.getMarkText(group.valuey[j])) / 2,
                            rectArc.top - yAxisMark.textSpace - yAxisMark.textHeight + yAxisMark.textLead, paintText)
                }
            }
            //恢复到裁切之前的画布
            canvas!!.restoreToCount(restoreCount)
        }
    }

    override fun onFocusTouch(point: PointF?) {
    }

    override fun onScaleBegin(detector: ScaleGestureDetector) {
    }

    override fun onScale(detector: ScaleGestureDetector, beginScrollx: Float) {
    }


    private fun calculateYMark() {
        val redundance = 1.01f //y轴最大和最小值冗余
        yAxisMark.cal_mark_max = -Float.MIN_VALUE //Y轴刻度最大值
        yAxisMark.cal_mark_min = Float.MAX_VALUE //Y轴刻度最小值
        for (data in barData) {
            for (valuey in data.valuey) {
                yAxisMark.cal_mark_max = Math.max(yAxisMark.cal_mark_max, valuey)
                yAxisMark.cal_mark_min = Math.min(yAxisMark.cal_mark_min, valuey)
            }
        }
        LogUtil.i(TAG, "Y轴真实cal_mark_min=" + yAxisMark.cal_mark_min.toString() + "  cal_mark_max=" + yAxisMark.cal_mark_max)
        if (yAxisMark.markType === MarkType.Integer) {
            val min = 0
            val max = yAxisMark.cal_mark_max.toInt()
            var mark = (max - min) / (yAxisMark.lableNum - 1) + if ((max - min) % (yAxisMark.lableNum - 1) > 0) 1 else 0
            val first = (mark.toString() + "").substring(0, 1).toInt() + 1
            LogUtil.i(TAG, "mark=$mark  first=$first")
            if ((mark.toString() + "").length == 1) {
                //YMARK = 1、2、5、10
                mark = if (mark == 3 || mark == 4 || mark == 6 || mark == 7 || mark == 8 || mark == 9) if (mark == 3 || mark == 4) 5 else 10 else mark
            } else if ((mark.toString() + "").length == 2) {
                mark = first * 10
            } else if ((mark.toString() + "").length == 3) {
                mark = first * 100
            } else if ((mark.toString() + "").length == 4) {
                mark = first * 1000
            } else if ((mark.toString() + "").length == 5) {
                mark = first * 10000
            } else if ((mark.toString() + "").length == 6) {
                mark = first * 100000
            }
            yAxisMark.cal_mark_min = 0f
            yAxisMark.cal_mark_max = (mark * (yAxisMark.lableNum - 1)).toFloat()
            yAxisMark.cal_mark = mark.toFloat()
        } else {   //Float   //Percent
            yAxisMark.cal_mark_max *= redundance
            yAxisMark.cal_mark_min /= redundance
            yAxisMark.cal_mark = (yAxisMark.cal_mark_max - yAxisMark.cal_mark_min) / (yAxisMark.lableNum - 1)
        }
        LogUtil.i(TAG, "  cal_mark_min=" + yAxisMark.cal_mark_min.toString() + "   cal_mark_max=" + yAxisMark.cal_mark_max.toString() + "  yAxisMark.cal_mark=" + yAxisMark.cal_mark)
    }
}