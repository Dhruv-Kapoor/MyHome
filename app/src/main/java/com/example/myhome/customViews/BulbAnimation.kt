package com.example.myhome.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.myhome.R
import kotlin.math.cos
import kotlin.math.sin

class BulbAnimation(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var currentLevel = 0
    private val maxRayLength = 40f.dp()
    private val distBetweenBulbAndRay = 24f.dp()

    private val bulbOff by lazy {
        ContextCompat.getDrawable(context, R.drawable.bulb_off)
    }
    private val bulbOn by lazy {
        ContextCompat.getDrawable(context, R.drawable.bulb_on)
    }

    private val rayAngles =
        arrayListOf(150, 170, 190, 210, 230, 250, 270, 290, 310, 330, 350, 10, 30)

    private var rayCenterX = 0f
    private var rayCenterY = 0f

    private val rayPaint = Paint()

    init {
        rayPaint.style = Paint.Style.STROKE
        rayPaint.color = ContextCompat.getColor(context, R.color.yellow)
        rayPaint.strokeWidth = 1f.dp()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rayCenterX = w / 2f
        bulbOff?.let {
            it.bounds = Rect(
                w / 2 - it.minimumWidth / 2,
                (h - it.minimumHeight),
                w / 2 + it.minimumWidth / 2,
                (h)
            )
        }
        bulbOn?.let {
            it.bounds = Rect(
                w / 2 - it.minimumWidth / 2,
                (h - it.minimumHeight),
                w / 2 + it.minimumWidth / 2,
                (h)
            )
            rayCenterY = (h - 0.7 * it.minimumHeight).toFloat()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            if (currentLevel == 0) {
                bulbOff?.draw(canvas)
            } else {
                bulbOn?.draw(canvas)
                drawRays(canvas)
            }
        }
    }

    fun setLevel(level: Int) {
        currentLevel = level
        invalidate()
    }

    private fun drawRays(canvas: Canvas) {
        val rayLength = currentLevel * maxRayLength / 100
        rayAngles.forEach {
            val start = angleToPointOnSlider(it.toFloat(), distBetweenBulbAndRay)
            val end = angleToPointOnSlider(it.toFloat(), distBetweenBulbAndRay + rayLength)
            canvas.drawLine(start[0], start[1], end[0], end[1], rayPaint)
        }
    }

    private fun angleToPointOnSlider(angleInDegree: Float, radius: Float): FloatArray {
        val angleInRadians = Math.toRadians(angleInDegree.toDouble())
        val x = rayCenterX + (radius * cos(angleInRadians)).toFloat()
        val y = rayCenterY + (radius * sin(angleInRadians)).toFloat()
        return floatArrayOf(x, y)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        bulbOff?.let {
            setMeasuredDimension(
                (2 * (distBetweenBulbAndRay + maxRayLength)).toInt(),
                (0.7 * it.minimumHeight + distBetweenBulbAndRay + maxRayLength).toInt()
            )
        }
    }

    private fun Float.dp(): Float = this * context.resources.displayMetrics.density
}