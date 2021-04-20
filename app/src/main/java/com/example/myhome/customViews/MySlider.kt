package com.example.myhome.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.myhome.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MySlider(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private val radius = 100f.dp()
    private val thickness = 8f.dp()

    private val basePath = Path()
    private val activePath = Path()
    private val basePaint = Paint()
    private val knobPaint = Paint()
    private val textPaint = Paint()
    private val activePaint = Paint()

    private val angleBelowHorizontal = 30f
    private val startAngle = 180f - angleBelowHorizontal
    private val endAngle = angleBelowHorizontal
    private val sweepAngle = 180f + 2 * angleBelowHorizontal

    private var centerX = 0f
    private var centerY = 0f
    private var innerRadius = 0f
    private var outerRadius = 0f

    private var knobX = 0f
    private var knobY = 0f
    private var knobRadius = thickness + 8f.dp()

    private var currentLevel = 0

    private val verticalCenterOffset = 20f.dp()
    private val textVerticalCenterOffset = radius - 44f.dp()

    private var onLevelChangeCallback: OnLevelChangeCallback? = null

    init {
        basePaint.style = Paint.Style.FILL
        basePaint.color = resources.getColor(R.color.grey)

        knobPaint.style = Paint.Style.FILL
        knobPaint.color = resources.getColor(R.color.green)

        activePaint.style = Paint.Style.FILL
        activePaint.color = resources.getColor(R.color.green)

        textPaint.style = Paint.Style.FILL
        textPaint.color = resources.getColor(R.color.black)
        textPaint.textSize = 24f.sp()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        centerX = w / 2f
        centerY = h / 2f + verticalCenterOffset

        innerRadius = radius - thickness / 2
        outerRadius = radius + thickness / 2

        basePath.reset()
        val innerRect = RectF(
            centerX - innerRadius,
            centerY - innerRadius,
            centerX + innerRadius,
            centerY + innerRadius
        )
        basePath.arcTo(innerRect, startAngle, sweepAngle)

        val endPoint = angleToPointOnSlider(endAngle)
        val endRect = RectF(
            endPoint[0] - thickness / 2,
            endPoint[1] - thickness / 2,
            endPoint[0] + thickness / 2,
            endPoint[1] + thickness / 2
        )
        basePath.arcTo(endRect, endAngle + 182f, -182f)

        val outerRect = RectF(
            centerX - outerRadius,
            centerY - outerRadius,
            centerX + outerRadius,
            centerY + outerRadius
        )
        basePath.arcTo(outerRect, endAngle, -sweepAngle)
        val startPoint = angleToPointOnSlider(startAngle)
        val startRect = RectF(
            startPoint[0] - thickness / 2,
            startPoint[1] - thickness / 2,
            startPoint[0] + thickness / 2,
            startPoint[1] + thickness / 2
        )
        basePath.arcTo(startRect, startAngle, -180f)

        //Knob
        setLevel(currentLevel)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(basePath, basePaint)
        canvas?.drawPath(activePath, activePaint)
        canvas?.drawCircle(knobX, knobY, knobRadius, knobPaint)
        val level = currentLevel.toString()
        val x = centerX - textPaint.measureText(level) / 2
        canvas?.drawText(currentLevel.toString(), x, centerY + textVerticalCenterOffset, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = 2 * radius + thickness + 36f.dp()
        setMeasuredDimension(size.toInt(), (size - 24f.dp()).toInt())
    }

    private var isKnobSelected = false
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.x <= knobX + knobRadius &&
                    event.x >= knobX - radius &&
                    event.y <= knobY + radius &&
                    event.y >= knobY - radius
                ) {
                    isKnobSelected = true
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isKnobSelected) {
                    val knobPos = getClosestPointOnSlider(event.x, event.y)
                    val knobAngle = pointToAngleOnSlider(knobPos[0], knobPos[1])
                    setLevel(getLevel(knobAngle))
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                if (isKnobSelected) {
                    isKnobSelected = false
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getClosestPointOnSlider(px: Float, py: Float): FloatArray {
        val vx = px - centerX
        val vy = py - centerY
        val magV = sqrt(vx * vx + vy * vy)
        val x = centerX + vx / magV * radius
        val y = centerY + vy / magV * radius
        return floatArrayOf(x, y)
    }

    fun setLevel(l: Int) {
        val level = if (l < 0) 0 else if (l > 100) 100 else l
        val knobAngle = (level.toFloat() / 100) * sweepAngle + startAngle
        val knobPos = angleToPointOnSlider(knobAngle)
        knobX = knobPos[0]
        knobY = knobPos[1]
        
        setActivePath(knobAngle)
        invalidate()

        if (currentLevel != level) {
            onLevelChangeCallback?.onLevelChange(level)
            currentLevel = level
        }
    }

    private fun setActivePath(knobAngle: Float) {
        val sa = knobAngle-startAngle
        activePath.reset()
        val innerRect = RectF(
            centerX - innerRadius,
            centerY - innerRadius,
            centerX + innerRadius,
            centerY + innerRadius
        )
        activePath.arcTo(innerRect, startAngle, sa)

        val outerRect = RectF(
            centerX - outerRadius,
            centerY - outerRadius,
            centerX + outerRadius,
            centerY + outerRadius
        )
        activePath.arcTo(outerRect, knobAngle, -sa)
        val startPoint = angleToPointOnSlider(startAngle)
        val startRect = RectF(
            startPoint[0] - thickness / 2,
            startPoint[1] - thickness / 2,
            startPoint[0] + thickness / 2,
            startPoint[1] + thickness / 2
        )
        activePath.arcTo(startRect, startAngle, -180f)

    }

    private fun angleToPointOnSlider(angleInDegree: Float): FloatArray {
        val angleInRadians = Math.toRadians(angleInDegree.toDouble())
        val x = centerX + (radius * cos(angleInRadians)).toFloat()
        val y = centerY + (radius * sin(angleInRadians)).toFloat()
        return floatArrayOf(x, y)
    }

    private fun pointToAngleOnSlider(x: Float, y: Float): Float {
        val angle =
            (Math.toDegrees(atan2(y - centerY, x - centerX).toDouble()).toFloat() + 360f) % 360f
        return if (angle <= 90f) angle + 360f - startAngle else angle - startAngle
    }

    private fun getLevel(currentAngle: Float): Int {
        val percent = currentAngle * 100f / sweepAngle
        return percent.toInt()
    }

    fun setOnLevelChangeCallback(onLevelChangeCallback: OnLevelChangeCallback) {
        this.onLevelChangeCallback = onLevelChangeCallback
    }

    fun removeOnLevelChangeCallback() {
        this.onLevelChangeCallback = null
    }

    private fun Float.dp(): Float = this * context.resources.displayMetrics.density
    private fun Float.sp(): Float = this * context.resources.displayMetrics.scaledDensity

    interface OnLevelChangeCallback {
        fun onLevelChange(level: Int)
    }
}