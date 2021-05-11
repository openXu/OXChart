package com.openxu.hkchart.bar

import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ScaleGestureDetector
import com.openxu.hkchart.BaseChart
import com.openxu.hkchart.config.*
import com.openxu.utils.DensityUtil
import com.openxu.utils.FontUtil
import com.openxu.utils.LogUtil

/**
 * Author: openXu
 * Time: 2021/5/11 14:07
 * class: HorizontalBarChart
 * Description:
 */
class HorizontalBarChart  : BaseChart<HBar> {

    /**计算 */
    private val chartRect = RectF() //图表矩形

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
    }

    /**设置 */
    private val barData = mutableListOf<HBar>()
    private lateinit var yAxisMark: YAxisMark
    private lateinit var xAxisMark: XAxisMark
    private var barWidth = DensityUtil.dip2px(context, 26f)
    private var barSpace = DensityUtil.dip2px(context, 10f)
    private var barColor = intArrayOf(
            Color.parseColor("#F46863"),
            Color.parseColor("#2DD08A"),
            Color.parseColor("#567CF6"),
            Color.parseColor("#F5B802"),
            Color.parseColor("#CC71F7"))
    fun setData(barData: List<HBar>?) {
        if(chartConfig==null)
            throw RuntimeException("---------请配置图表")
        LogUtil.w(TAG, "设置数据：$barData")
        this.barData.clear()
        barData?.let {
            this.barData.addAll(barData)
        }
        var config = chartConfig as HorizontalBarConfig
        if(null==config.xAxisMark)
            throw RuntimeException("---------请设置x坐标")
        if(null==config.yAxisMark)
            throw RuntimeException("---------请设置y坐标")
        xAxisMark = config.xAxisMark!!
        yAxisMark = config.yAxisMark!!
        barWidth = config.barWidth
        barSpace = config.barSpace
        barColor = config.barColor

        if (config.showAnim) chartAnimStarted = false
        calculateYMark()
        loading = false
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        LogUtil.v(TAG, "测量建议：" + MeasureSpec.UNSPECIFIED + "    " + MeasureSpec.EXACTLY + "    " + MeasureSpec.AT_MOST)
        LogUtil.v(TAG, "宽测量建议：$widthSize*$widthMode")
        LogUtil.v(TAG, "高度测量建议：$heightSize*$heightMode")
        scrollXMax = 0f
        scrollx = 0f
        var height = 0
        when (heightMode) {
            MeasureSpec.EXACTLY -> height = heightSize
            MeasureSpec.UNSPECIFIED, MeasureSpec.AT_MOST ->                 //计算需要的高度
                if (!barData.isNullOrEmpty()) {
                    height += paddingTop
                    height += paddingBottom
                    paintText.textSize = yAxisMark.textSize.toFloat()
                    paintText.typeface = yAxisMark.numberTypeface
                    yAxisMark.textHeight = FontUtil.getFontHeight(paintText)
                    yAxisMark.textLead = FontUtil.getFontLeading(paintText)
                    height += yAxisMark.textSpace
                    height += yAxisMark.textHeight.toInt()
                    height += (barWidth + barSpace) * barData.size
                    //
                    /*  if(height>heightSize){

                    }
                    scrollAble = false;
                    scrollXMax = 0;
                    scrollx = 0;*/LogUtil.v(TAG, "实际需要高度：$heightSize")
                } else {
                    //默认最小高度用于显示正在加载中
                    height = DensityUtil.dip2px(context, 150f)
                }
        }
        LogUtil.v(TAG, "测量：$widthSize*$height")
        setMeasuredDimension(widthSize, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (barData.isNullOrEmpty())
            return
        paintText.textSize = xAxisMark.textSize.toFloat()
        paintText.typeface = Typeface.DEFAULT
        xAxisMark.textHeight = FontUtil.getFontHeight(paintText)
        xAxisMark.textLead = FontUtil.getFontLeading(paintText)
        var lableMax = 0f
        for (hbar in barData) {
            lableMax = Math.max(lableMax, FontUtil.getFontlength(paintText, hbar.lable))
        }
        chartRect.left = paddingLeft + lableMax + xAxisMark.textSpace
        chartRect.top = yAxisMark.textSpace + yAxisMark.textHeight
        paintText.textSize = yAxisMark.textSize.toFloat()
        paintText.typeface = yAxisMark.numberTypeface
        chartRect.right = rectChart.right - FontUtil.getFontlength(paintText, yAxisMark.getMarkText(yAxisMark.cal_mark_max)) / 2
        chartRect.bottom = rectChart.bottom
        LogUtil.w(TAG, "重新计算绘制范围:$chartRect")
    }

    private var rect = RectF() //备用，后面绘制矩形
    private var path = Path()
    override fun drawChart(canvas: Canvas?) {
        if (barData.isNullOrEmpty()) return
        var axis0x = 0f //y轴0刻度的x坐标

        var top: Float = chartRect.top + barSpace / 2 //绘制柱状开始的最上方位置

        paintText.textSize = xAxisMark.textSize.toFloat()
        paintText.color = xAxisMark.textColor
        paintText.typeface = Typeface.DEFAULT
        xAxisMark.textHeight = FontUtil.getFontHeight(paintText)
        xAxisMark.textLead = FontUtil.getFontLeading(paintText)

        paint.style = Paint.Style.FILL
        for (i in 0 until barData.size) {
            val bar = barData[i]
            //绘制x标签
            val lable: String = getXValue(bar.lable)
            val textWidth = FontUtil.getFontlength(paintText, lable)
            canvas!!.drawText(lable,
                    chartRect.left - xAxisMark.textSpace - textWidth,
                    top + barWidth / 2 - xAxisMark.textHeight / 2 + xAxisMark.textLead, paintText)
            //绘制底色
            paint.color = Color.parseColor("#f0f0f0")
            rect.left = chartRect.left
            rect.top = top
            rect.right = chartRect.right
            rect.bottom = top + barWidth
            canvas!!.drawRect(rect, paint)
            top += barWidth + barSpace
        }

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = yAxisMark.lineWidth.toFloat()
        paint.color = yAxisMark.lineColor
        paintEffect.style = Paint.Style.STROKE
        paintEffect.strokeWidth = yAxisMark.lineWidth.toFloat()
        paintEffect.color = yAxisMark.lineColor
        val effects: PathEffect = DashPathEffect(floatArrayOf(15f, 6f, 15f, 6f), 0f)

        paintEffect.pathEffect = effects
        paintText.textSize = yAxisMark.textSize.toFloat()
        paintText.color = yAxisMark.textColor
        paintText.typeface = yAxisMark.numberTypeface
        val yMarkSpace = chartRect.width() / (yAxisMark.lableNum - 1)
        for (i in 0 until yAxisMark.lableNum) {
            /**竖直线 */
            if (index0 === i) axis0x = chartRect.left + yMarkSpace * i

            //canvas.drawLine()给paint设置DashPathEffect(虚线)无效。后面发现是硬件加速的锅。 解决方法就是，在view层关闭硬件加速
//            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//            canvas.drawLine(chartRect.left + yMarkSpace*i, chartRect.top,
//                    chartRect.left + yMarkSpace*i,chartRect.bottom, index0 == i?paint:paintEffect);
            //canvas.drawPath()可以绘制虚线，不用关闭硬件加速
            path.reset()
            path.moveTo(chartRect.left + yMarkSpace * i, chartRect.top)
            path.lineTo(chartRect.left + yMarkSpace * i, chartRect.bottom)
            canvas!!.drawPath(path, if (index0 === i) paint else paintEffect)
            /**绘制y刻度 */
            val text = yAxisMark.getMarkText(yAxisMark.cal_mark_min + i * yAxisMark.cal_mark)
            canvas!!.drawText(text,
                    chartRect.left + yMarkSpace * i - FontUtil.getFontlength(paintText, text) / 2,
                    rectChart.top + yAxisMark.textLead, paintText)
        }
        /**绘制数据*/
        /**绘制数据 */
        paint.style = Paint.Style.FILL
        paintText.textSize = xAxisMark.textSize.toFloat()
        paintText.color = xAxisMark.textColor
        paintText.typeface = yAxisMark.numberTypeface
        //两种不同的字体设置后要重新计算 文字高度和基线，否则会出现意想不到的效果
        //两种不同的字体设置后要重新计算 文字高度和基线，否则会出现意想不到的效果
        xAxisMark.textHeight = FontUtil.getFontHeight(paintText)
        xAxisMark.textLead = FontUtil.getFontLeading(paintText)
        //y值每一份对应的宽度
        //y值每一份对应的宽度
        val once = chartRect.width() / (yAxisMark.cal_mark_max - yAxisMark.cal_mark_min)
        top = chartRect.top + barSpace / 2
        for (i in 0 until barData.size) {
            val bar = barData[i]
            //绘制颜色柱子
            rect.top = top
            rect.bottom = top +barWidth
            if (bar.value >= 0) {
                rect.left = axis0x
                rect.right = rect.left + once * bar.value * chartAnimValue
            } else {
                rect.right = axis0x
                rect.left = rect.right + once * bar.value * chartAnimValue
            }
            paint.color = barColor.get(i % barColor.size)
            canvas!!.drawRect(rect, paint)
            //绘制文字
            val lable = yAxisMark.getMarkText(bar.value) + yAxisMark.unit
            val textWidth = FontUtil.getFontlength(paintText, lable)
            var x = if (rect.width() < textWidth) if (bar.value >= 0) rect.left else rect.right - textWidth else rect.left + rect.width() / 2 - textWidth / 2
            if (x < chartRect.left + xAxisMark.textSpace) x = chartRect.left + xAxisMark.textSpace
            if (x + textWidth > chartRect.right) x = chartRect.right - textWidth - xAxisMark.textSpace
            paintText.typeface = yAxisMark.numberTypeface
            canvas!!.drawText(lable,
                    x,
                    top + barWidth / 2 - xAxisMark.textHeight / 2 + xAxisMark.textLead, paintText)
            top += barWidth + barSpace
        }
    }

    override fun onFocusTouch(point: PointF?) {
    }

    override fun onScaleBegin(detector: ScaleGestureDetector) {
    }

    override fun onScale(detector: ScaleGestureDetector, beginScrollx: Float) {
    }


    //截取
    private fun getXValue(value: String): String {
        var value = value
        if (TextUtils.isEmpty(value)) return ""
        if (xAxisMark.splitSubLen > 0 && value.length > xAxisMark.splitSubLen) {
            value = value.substring(0, xAxisMark.splitSubLen)
        }
        return value
    }
    private var index0 = 0   //0刻度对应的标签索引
    private fun calculateYMark() {
        if (barData == null || barData.size <= 0) return
        yAxisMark.cal_mark_max = barData[0].value
        yAxisMark.cal_mark_min = barData[0].value
        for (data in barData) {
            yAxisMark.cal_mark_max = Math.max(yAxisMark.cal_mark_max, data.value)
            yAxisMark.cal_mark_min = Math.min(yAxisMark.cal_mark_min, data.value)
        }
        LogUtil.w(TAG, "真实的最小值=" + yAxisMark.cal_mark_min + "    最大值=" + yAxisMark.cal_mark_max + "    y刻度数量:" + yAxisMark.lableNum)
        if (yAxisMark.cal_mark_min > 0 && yAxisMark.cal_mark_max > 0) {
            yAxisMark.cal_mark_min = 0f
        } else if (yAxisMark.cal_mark_min < 0 && yAxisMark.cal_mark_max < 0) {
            yAxisMark.cal_mark_max = 0f
        }
        val z = ((yAxisMark.cal_mark_max - yAxisMark.cal_mark_min) / (yAxisMark.lableNum - 1)).toInt()
        val y = ((yAxisMark.cal_mark_max - yAxisMark.cal_mark_min) % (yAxisMark.lableNum - 1)).toInt()
        var mark = z + if (y > 0) 1 else 0
        LogUtil.w(TAG, "取整 $z   余 $y   计算mark=$mark")
        mark = if (mark == 0) 1 else mark //最大值和最小值都为0的情况
        LogUtil.w(TAG, yAxisMark.cal_mark_min.toString() + "~" + yAxisMark.cal_mark_max + "计算mark=" + mark)
        if (mark <= 10) {
            //YMARK = 1、2、5、10
            mark = if (mark == 3 || mark == 4 || mark == 6 || mark == 7 || mark == 8 || mark == 9) if (mark == 3 || mark == 4) 5 else 10 else mark
        } else {
            //mark前两位，比如 4549 取mark1=4 mark2=5
            var mark1 = (mark.toString() + "").substring(0, 1).toInt()
            var mark2 = (mark.toString() + "").substring(1, 2).toInt()
            LogUtil.w(TAG, "mark前两位=$mark1  $mark2")
            if (mark2 < 5) {
                mark2 = 5
            } else {
                mark2 = 0
                mark1 += 1
            }
            val ws = (mark.toString() + "").length
            LogUtil.w(TAG, "mark前两位=$mark1  $mark2   位数：$ws")
            mark = mark1 * getWs(ws) + mark2 * getWs(ws - 1)
        }
        LogUtil.w(TAG, "取值mark=$mark")
        if (yAxisMark.cal_mark_min < 0 && yAxisMark.cal_mark_max > 0) {
            //需要显示0
            index0 = (-yAxisMark.cal_mark_min / mark).toInt()+ if (-yAxisMark.cal_mark_min % mark != 0f) 1 else 0
            while (checkMark(index0, mark, yAxisMark.cal_mark_max)) {
                yAxisMark.lableNum++
                LogUtil.w(TAG, "检测到正值可能越界，增加标签数量=" + yAxisMark.lableNum)
            }
            LogUtil.w(TAG, "一正一负的情况mark=" + mark + "  index0=" + index0 + "   lableNum=" + yAxisMark.lableNum)
            yAxisMark.cal_mark_min = -mark * index0.toFloat()
            yAxisMark.cal_mark_max = yAxisMark.cal_mark_min + mark * (yAxisMark.lableNum - 1)
        } else if (yAxisMark.cal_mark_min == 0f) {
            index0 = 0
            yAxisMark.cal_mark_max = mark * (yAxisMark.lableNum - 1).toFloat()
        } else if (yAxisMark.cal_mark_max == 0f) {
            index0 = yAxisMark.lableNum - 1
            yAxisMark.cal_mark_min = -mark * (yAxisMark.lableNum - 1).toFloat()
        }
        yAxisMark.cal_mark = mark.toFloat()
        LogUtil.w(TAG, "最终取值=" + yAxisMark.cal_mark_min + "~" + yAxisMark.cal_mark_max + "   mark=" + mark)
    }

    private fun getWs(ws: Int): Int {
        return if (ws == 1) 1 else if (ws == 2) 10 else if (ws == 3) 100 else if (ws == 4) 1000 else if (ws == 5) 10000 else if (ws == 6) 100000 else if (ws == 7) 1000000 else if (ws == 8) 10000000 else if (ws == 9) 100000000 else 1
    }

    private fun checkMark(index0: Int, mark: Int, max: Float): Boolean {
        return (yAxisMark.lableNum - 1 - index0) * mark < max
    }






}