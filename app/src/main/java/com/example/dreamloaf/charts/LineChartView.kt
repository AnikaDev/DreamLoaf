package com.example.dreamloaf.charts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.TreeSet
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.dropLastWhile
import kotlin.collections.indices
import kotlin.collections.toTypedArray
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class LineChartView : View {
    private var lineChartData: MutableMap<String, MutableMap<String, Int>> = HashMap()
    private var pieChartData: MutableMap<String, Int> = HashMap()

    private var width = 0
    private var height = 0
    private val padding = 70
    private var maxValue = 1f
    private var chartHeight = 0

    private var linePaint: Paint? = null
    private var pointPaint: Paint? = null
    private var textPaint: Paint? = null
    private var axisPaint: Paint? = null
    private var gridPaint: Paint? = null
    private var fillPaint: Paint? = null


    private val colors = intArrayOf(
        Color.parseColor("#FF6200EE"),
        Color.parseColor("#FF03DAC5"),
        Color.parseColor("#FF018786"),
        Color.parseColor("#FFBB86FC"),
        Color.parseColor("#FF3700B3"),
        Color.parseColor("#FFFF5722"),
        Color.parseColor("#FFE91E63"),
        Color.parseColor("#FF8BC34A"),
        Color.parseColor("#FF795548"),
        Color.parseColor("#FF9C27B0"),
        Color.parseColor("#FF3F51B5"),
        Color.parseColor("#FF00BCD4"),
        Color.parseColor("#FFCDDC39"),
        Color.parseColor("#FFFF9800"),
        Color.parseColor("#FF607D8B")
    )

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        linePaint!!.setStyle(Paint.Style.STROKE)
        linePaint!!.setStrokeWidth(4f)

        pointPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        pointPaint!!.setStyle(Paint.Style.FILL)

        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint!!.setColor(Color.BLACK)
        textPaint!!.setTextSize(28f)

        axisPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        axisPaint!!.setColor(Color.BLACK)
        axisPaint!!.setStrokeWidth(3f)

        gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        gridPaint!!.setColor(Color.LTGRAY)
        gridPaint!!.setStrokeWidth(1f)

        fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        fillPaint!!.setStyle(Paint.Style.FILL)
    }

    fun setLineData(lineData: MutableMap<String, MutableMap<String, Int>>?) {
        this.lineChartData = lineData ?: HashMap()
        calculateMaxValue()
        invalidate()
    }

    fun setPieData(pieData: MutableMap<String, Int>?) {
        this.pieChartData = pieData ?: HashMap()
        invalidate()
    }

    private fun calculateMaxValue() {
        maxValue = 1f
        for (productData in lineChartData.values) {
            for (value in productData.values) {
                if (value > maxValue) maxValue = value.toFloat()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.width = w
        this.height = h

        val totalPaddings = padding * 3
        val legendSpace = 0
        this.chartHeight = (height - totalPaddings - legendSpace) / 2

        if (chartHeight < 100) chartHeight = 100
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)

        if (lineChartData.isEmpty() && pieChartData.isEmpty()) {
            drawNoDataMessage(canvas)
            return
        }

        if (height <= 3 * padding || width <= 2 * padding) {
            Log.w("LineChart", "Not enough space to draw chart")
            return
        }

        var currentTop = padding

        if (!pieChartData.isEmpty()) {
            drawPieChart(
                canvas,
                padding.toFloat(),
                currentTop.toFloat(),
                (width - padding).toFloat(),
                (currentTop + chartHeight).toFloat()
            )
            currentTop += chartHeight + padding
        }

        val remainingHeight = height - currentTop - padding
        val lineChartHeight = min(remainingHeight.toDouble(), chartHeight.toDouble()).toInt()

        if (!lineChartData.isEmpty()) {
            drawLineChart(
                canvas,
                padding.toFloat(),
                currentTop.toFloat(),
                (width - padding).toFloat(),
                (currentTop + lineChartHeight).toFloat()
            )
        }
    }

    private fun drawPieChart(canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        val total = this.totalPieValue
        if (total == 0) return

        val centerX = (left + right) / 2
        val centerY = (top + bottom) / 2
        val radius: Double =
            min((right - left).toDouble(), (bottom - top).toDouble()) / 2 - padding / 2

        var startAngle = 0f
        var colorIndex = 0
        for (entry in pieChartData.entries) {
            var sweepAngle = 360f * (entry.value?.toFloat() ?: 0f) / total.toFloat()

            fillPaint!!.setColor(colors[colorIndex % colors.size])
            canvas.drawArc(
                RectF(
                    (centerX - radius).toFloat(), (centerY - radius).toFloat(),
                    (centerX + radius).toFloat(), (centerY + radius).toFloat()
                ),
                startAngle, sweepAngle, true, fillPaint!!
            )

            if (sweepAngle > 15) {
                var percent = 100f * (entry.value?.toFloat() ?: 0f) / total.toFloat()
                val midAngle = startAngle + sweepAngle / 2
                val textX =
                    centerX + (radius * 0.7f) * cos(Math.toRadians(midAngle.toDouble())).toFloat()
                val textY =
                    centerY + (radius * 0.7f) * sin(Math.toRadians(midAngle.toDouble())).toFloat()

                textPaint!!.setColor(Color.WHITE)
                canvas.drawText(String.format("%.1f%%", percent), textX.toFloat(),
                    textY.toFloat(), textPaint!!)
            }

            startAngle += sweepAngle
            colorIndex++
        }

        fillPaint!!.setStyle(Paint.Style.STROKE)
        fillPaint!!.setColor(Color.BLACK)
        fillPaint!!.setStrokeWidth(2f)
        canvas.drawCircle(centerX, centerY, radius.toFloat(), fillPaint!!)
        fillPaint!!.setStyle(Paint.Style.FILL)
    }

    private fun drawLineChart(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ) {
        val axisPadding = 20f
        val chartBottom = bottom - axisPadding

        canvas.drawLine(left, top, left, chartBottom, axisPaint!!)
        canvas.drawLine(left, chartBottom, right, chartBottom, axisPaint!!)
        val gridLines = 5
        for (i in 0..gridLines) {
            val y = chartBottom - i * (chartBottom - top) / gridLines
            canvas.drawLine(left, y, right, y, gridPaint!!)
            canvas.drawText(
                (maxValue * i / gridLines).toInt().toString(),
                left - 10, y + 5, textPaint!!
            )
        }

        val dates = this.allDates.filterNotNull()
        if (dates.isEmpty()) return
        val sortedDates: MutableList<String> = ArrayList(dates)
        sortedDates.sort()

        val xStep = (right - left) / (sortedDates.size - 1)

        var colorIndex = 0
        for (entry in lineChartData.entries) {
            drawDataLine(
                canvas,
                entry.value, sortedDates, left, top, chartBottom, xStep,
                colors[colorIndex % colors.size]
            )
            colorIndex++
        }

        drawDateLabels(canvas, sortedDates, left, chartBottom, xStep)
    }

    private fun drawDataLine(
        canvas: Canvas, data: MutableMap<String, Int>, dates: MutableList<String>,
        left: Float, top: Float, bottom: Float, xStep: Float, color: Int
    ) {
        linePaint!!.setColor(color)
        pointPaint!!.setColor(color)

        val path = Path()
        var firstPoint = true

        for (i in dates.indices) {
            val date: String = dates.get(i)
            if (data.containsKey(date)) {
                val x = left + i * xStep
                val y = bottom - (data.get(date)!! / maxValue) * (bottom - top)

                canvas.drawCircle(x, y, 8f, pointPaint!!)

                if (firstPoint) {
                    path.moveTo(x, y)
                    firstPoint = false
                } else {
                    path.lineTo(x, y)
                }
            }
        }

        canvas.drawPath(path, linePaint!!)
    }

    private fun drawDateLabels(
        canvas: Canvas,
        dates: MutableList<String>,
        left: Float,
        bottom: Float,
        xStep: Float
    ) {
        textPaint!!.setTextAlign(Paint.Align.CENTER)
        var i = 0
        while (i < dates.size) {
            val date = dates.get(i)
            val x = left + i * xStep
            canvas.drawText(formatDate(date)!!, x, bottom + 35, textPaint!!)
            i += 2
        }
    }

    private val allDates: MutableSet<String?>
        get() {
            val dates: MutableSet<String?> =
                TreeSet<String?>()
            for (data in lineChartData.values) {
                dates.addAll(data.keys)
            }
            return dates
        }

    private val totalPieValue: Int
        get() {
            var total = 0
            for (value in pieChartData.values) {
                total += value ?: 0
            }
            return total
        }

    private fun formatDate(dateStr: String): String? {
        try {
            val parts: Array<String?> =
                dateStr.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size >= 3) {
                return parts[2] + "." + parts[1]
            }
            return dateStr
        } catch (e: Exception) {
            return dateStr
        }
    }

    private fun drawNoDataMessage(canvas: Canvas) {
        textPaint!!.setTextAlign(Paint.Align.CENTER)
        textPaint!!.setTextSize(36f)
        canvas.drawText(
            "Нет данных для отображения",
            (width / 2).toFloat(), (height / 2).toFloat(), textPaint!!
        )
    }
}