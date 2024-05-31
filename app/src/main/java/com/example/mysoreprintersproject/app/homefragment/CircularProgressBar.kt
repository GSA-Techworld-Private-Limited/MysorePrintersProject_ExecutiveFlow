package com.example.mysoreprintersproject.app.homefragment

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.mysoreprintersproject.R

class CircularProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var paint: Paint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.shade_blue) // Ensure you have a color resource named 'blue'
        style = Paint.Style.STROKE
        strokeWidth = 20f
    }

    private val rectF = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()
        val diameter = Math.min(width, height)
        val radius = diameter / 2

        val left = (width - diameter) / 2
        val top = (height - diameter) / 2
        val right = left + diameter
        val bottom = top + diameter

        rectF.set(left, top, right, bottom)
        canvas.drawArc(rectF, 180f, 180f, false, paint) // Draw half circle (180 degrees)
    }
}
