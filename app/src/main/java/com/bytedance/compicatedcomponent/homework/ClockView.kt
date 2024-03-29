package com.bytedance.compicatedcomponent.homework

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

/**
 *  author : neo
 *  time   : 2021/10/25
 *  desc   :
 */
class ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val FULL_ANGLE = 360

        private const val CUSTOM_ALPHA = 140
        private const val FULL_ALPHA = 255

        private const val POINTER_TYPE_SECOND = 2
        private const val POINTER_TYPE_MINUTES = 1
        private const val POINTER_TYPE_HOURS = 0

        private const val DEFAULT_PRIMARY_COLOR: Int = Color.WHITE
        private const val DEFAULT_SECONDARY_COLOR: Int = Color.LTGRAY

        private const val DEFAULT_DEGREE_STROKE_WIDTH = 0.010f

        private const val RIGHT_ANGLE = 90

        private const val UNIT_DEGREE = (6 * Math.PI / 180).toFloat() // 一个小格的度数
    }

    private var panelRadius = 200.0f // 表盘半径

    private var hourPointerLength = 0f // 指针长度

    private var minutePointerLength = 0f
    private var secondPointerLength = 0f

    private var resultWidth = 0
    private  var centerX: Int = 0
    private  var centerY: Int = 0
    private  var radius: Int = 0

    private var degreesColor = 0
    private var valuesColor = 0

    private val needlePaint: Paint

    init {
        degreesColor = DEFAULT_PRIMARY_COLOR
        valuesColor = Color.WHITE
        needlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        needlePaint.style = Paint.Style.FILL_AND_STROKE
        needlePaint.strokeCap = Paint.Cap.ROUND
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size: Int
        val width = measuredWidth
        val height = measuredHeight
        val widthWithoutPadding = width - paddingLeft - paddingRight
        val heightWithoutPadding = height - paddingTop - paddingBottom
        size = if (widthWithoutPadding > heightWithoutPadding) {
            heightWithoutPadding
        } else {
            widthWithoutPadding
        }
        setMeasuredDimension(size + paddingLeft + paddingRight, size + paddingTop + paddingBottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        resultWidth = if (height > width) width else height
        val halfWidth = resultWidth / 2
        centerX = halfWidth
        centerY = halfWidth
        radius = halfWidth
        panelRadius = radius.toFloat()
        hourPointerLength = panelRadius - 400
        minutePointerLength = panelRadius - 300
        secondPointerLength = panelRadius - 200
        drawDegrees(canvas)
        drawHoursValues(canvas)
        drawEleClock(canvas)
        drawNeedles(canvas)

        // todo 1: 每一秒刷新一次，让指针动起来
        invalidate()
    }

    private fun drawEleClock(canvas: Canvas) {
        val calendar: Calendar = Calendar.getInstance()
        val now: Date = calendar.time
        val nowHours: Int = now.hours - 4
        val nowMinutes: Int = now.minutes
        val nowSeconds: Int = now.seconds

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = DEFAULT_SECONDARY_COLOR
            textSize = 50f
        }
        val startX = centerX
        val startY = centerY + 300
        val vPadded = 100

        val hOffset = 1
        val vOffset = 0

        val path = Path()
        path.moveTo((startX - vPadded).toFloat(), startY.toFloat())
        path.lineTo((startX + vPadded).toFloat(), startY.toFloat())

        var secondText = if (nowSeconds < 10) {
            "0$nowSeconds"
        } else {
            nowSeconds.toString()
        }
        var minutesText = if (nowMinutes < 10) {
            "0$nowMinutes"
        } else {
            nowMinutes.toString()
        }
        var hourText = if (nowHours < 10) {
            "0$nowHours"
        } else {
            nowHours.toString()
        }

        canvas.drawTextOnPath("$hourText:$minutesText:$secondText", path, hOffset.toFloat(), vOffset.toFloat(), paint)
    }

    private fun drawDegrees(canvas: Canvas) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = resultWidth * DEFAULT_DEGREE_STROKE_WIDTH
            color = degreesColor
        }
        val rPadded: Int = centerX - (resultWidth * 0.09f).toInt()
        val rEnd: Int = centerX - (resultWidth * 0.13f).toInt()
        var i = 0
        while (i < FULL_ANGLE) {
            if (i % RIGHT_ANGLE != 0 && i % 15 != 0) {
                paint.alpha = CUSTOM_ALPHA
            } else {
                paint.alpha = FULL_ALPHA
            }
            val startX = (centerX + rPadded * cos(Math.toRadians(i.toDouble())))
            val startY = (centerY - rPadded * sin(Math.toRadians(i.toDouble())))
            val stopX = (centerX + rEnd * cos(Math.toRadians(i.toDouble())))
            val stopY = (centerY - rEnd * sin(Math.toRadians(i.toDouble())))
            canvas.drawLine(
                startX.toFloat(),
                startY.toFloat(),
                stopX.toFloat(),
                stopY.toFloat(),
                paint
            )
            i += 6
        }
    }

    /**
     * Draw Hour Text Values, such as 1 2 3 ...
     *
     * @param canvas
     */
    private fun drawHoursValues(canvas: Canvas) {
        // Default Color:
        // - hoursValuesColor
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = valuesColor
            textSize = 50f
        }
        val rPadded: Int = centerX - (resultWidth * 0.05f).toInt()
        val vPadded: Int = 20
        var i = 90
        while (i > -270) {
            val startX = (centerX + rPadded * cos(Math.toRadians(i.toDouble())))
            val startY = (centerY - rPadded * sin(Math.toRadians(i.toDouble())))

            val path = Path()
            path.moveTo((startX - vPadded).toFloat(), startY.toFloat())
            path.lineTo((startX + vPadded).toFloat(), startY.toFloat())

            val hOffset = 1
            val vOffset = 10
            var hourValue = 3 - i / 30
            if (hourValue == 0) {
                hourValue = 12
            }

            canvas.drawTextOnPath(hourValue.toString(), path, hOffset.toFloat(), vOffset.toFloat(), paint)
            i -= 30
        }
    }

    /**
     * Draw hours, minutes needles
     * Draw progress that indicates hours needle disposition.
     *
     * @param canvas
     */
    private fun drawNeedles(canvas: Canvas) {
        val calendar: Calendar = Calendar.getInstance()
        val now: Date = calendar.time
        val nowHours: Int = now.hours - 4
        val nowMinutes: Int = now.minutes
        val nowSeconds: Int = now.seconds
        // 画秒针
        drawPointer(canvas, POINTER_TYPE_SECOND, nowSeconds)
        // 画分针
        // todo 2: 画分针
        drawPointer(canvas, POINTER_TYPE_MINUTES, nowMinutes)
        // 画时针
        val part = nowMinutes / 12
        drawPointer(canvas, POINTER_TYPE_HOURS, 5 * nowHours + part)
    }

    private fun drawPointer(canvas: Canvas, pointerType: Int, value: Int) {
        val degree: Float
        var pointerHeadXY = FloatArray(2)
        needlePaint.strokeWidth = resultWidth * DEFAULT_DEGREE_STROKE_WIDTH
        when (pointerType) {
            POINTER_TYPE_HOURS -> {
                degree = value * UNIT_DEGREE
                needlePaint.color = Color.BLUE
                pointerHeadXY = getPointerHeadXY(hourPointerLength, degree)
            }
            POINTER_TYPE_MINUTES -> {
                degree = value * UNIT_DEGREE
                needlePaint.color = Color.RED
                pointerHeadXY = getPointerHeadXY(minutePointerLength, degree)
            }
            POINTER_TYPE_SECOND -> {
                degree = value * UNIT_DEGREE
                needlePaint.color = Color.GREEN
                pointerHeadXY = getPointerHeadXY(secondPointerLength, degree)
            }
        }
        canvas.drawLine(
            centerX.toFloat(), centerY.toFloat(),
            pointerHeadXY[0], pointerHeadXY[1], needlePaint
        )
    }

    private fun getPointerHeadXY(pointerLength: Float, degree: Float): FloatArray {
        val xy = FloatArray(2)
        xy[0] = centerX + pointerLength * sin(degree)
        xy[1] = centerY - pointerLength * cos(degree)
        return xy
    }
}