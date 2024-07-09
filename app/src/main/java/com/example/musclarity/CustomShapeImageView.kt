package com.example.musclarity

/*import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class CustomShapeImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val path = Path()
    private val rect = RectF()

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()

        // Define the shape's path
        rect.set(0f, 0f, width, height)
        path.reset()

        val edgeSize = width * 0.15f // Adjust this value to change the size of the edges

        // Define the heptagonal shape
        path.moveTo(edgeSize, 0f)
        path.lineTo(width - edgeSize, 0f)
        path.lineTo(width, height / 3f)
        path.lineTo(width - edgeSize, height)
        path.lineTo(edgeSize, height)
        path.lineTo(0f, height / 3f)
        path.close()

        // Clip the canvas to the path
        canvas.clipPath(path)

        super.onDraw(canvas)
    }
}
 */

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class CustomShapeImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val path = Path()
    private val rect = RectF()

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()

        // Define the shape's path
        rect.set(0f, 0f, width, height)
        path.reset()

        val bottomEdgeSize = height * 0.275f // Adjust this value to change the size of the edges
        val upperEdgeSizeY = height * 0.175f // Adjust this value to change the size of the edges
        val upperEdgeSizeX = height * 0.10f // Adjust this value to change the size of the edges

        path.moveTo(width / 2f, height)
        path.lineTo(0f, height - bottomEdgeSize)
        path.lineTo(0f, upperEdgeSizeY)
        path.lineTo(upperEdgeSizeX, 0f)
        path.lineTo(width - upperEdgeSizeX, 0f)
        path.lineTo(width, upperEdgeSizeY)
        path.lineTo(width, height - bottomEdgeSize)
        path.moveTo(width / 2f, height)
        path.close()

        // Clip the canvas to the path
        canvas.clipPath(path)

        super.onDraw(canvas)
    }
}
