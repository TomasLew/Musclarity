package com.example.musclarity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class GradientProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var percentage: Int = 0
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = context.resources.displayMetrics.density * 2 // 2dp to pixels
    }
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
    }
    private val cornerRadius = context.resources.displayMetrics.density * 10 // 10dp to pixels

    fun setPercentage(percentage: Int) {
        this.percentage = percentage.coerceIn(0, 100)
        invalidate() // Request a redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        // Draw the black background
        rect.set(0f, 0f, width, height)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, backgroundPaint)

        // Get the interpolated color based on the percentage
        val color = getInterpolatedColor(percentage)

        // Set the paint color
        paint.color = color

        // Draw the progress bar
        rect.set(0f, 0f, width * (percentage / 100f), height)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)

        // Draw the border
        rect.set(0f, 0f, width, height)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, borderPaint)
    }

    private fun getInterpolatedColor(percentage: Int): Int {
        val red = ContextCompat.getColor(context, R.color.red)
        val yellow = ContextCompat.getColor(context, R.color.yellow)
        val green = ContextCompat.getColor(context, R.color.green)

        return when {
            percentage <= 50 -> {
                // Interpolate between red and yellow
                val factor = percentage / 50f
                interpolateColor(red, yellow, factor)
            }
            else -> {
                // Interpolate between yellow and green
                val factor = (percentage - 50) / 50f
                interpolateColor(yellow, green, factor)
            }
        }
    }

    private fun interpolateColor(colorStart: Int, colorEnd: Int, factor: Float): Int {
        val startAlpha = (colorStart shr 24) and 0xff
        val startRed = (colorStart shr 16) and 0xff
        val startGreen = (colorStart shr 8) and 0xff
        val startBlue = colorStart and 0xff

        val endAlpha = (colorEnd shr 24) and 0xff
        val endRed = (colorEnd shr 16) and 0xff
        val endGreen = (colorEnd shr 8) and 0xff
        val endBlue = colorEnd and 0xff

        val alpha = (startAlpha + (endAlpha - startAlpha) * factor).toInt()
        val red = (startRed + (endRed - startRed) * factor).toInt()
        val green = (startGreen + (endGreen - startGreen) * factor).toInt()
        val blue = (startBlue + (endBlue - startBlue) * factor).toInt()

        return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
    }
}
