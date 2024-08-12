package com.example.mysoreprintersproject.app.homefragment

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class DonutChartView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val completedPaint: Paint = Paint().apply {
        color = Color.parseColor("#0342C4") // shade_blue
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val uncompletedPaint: Paint = Paint().apply {
        color = Color.GRAY // uncompleted color
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val rectF: RectF = RectF()

    // Variables to store the percentages
    private var completedPercentage = 0f
    private var uncompletedPercentage = 0f

    // Method to set the completed percentage
    fun setCompletedPercentage(percentage: Float) {
        completedPercentage = percentage
        uncompletedPercentage = 100 - completedPercentage
        invalidate() // Request to redraw the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = (width / 2).toFloat()
        val centerY = (height / 2).toFloat()
        val radius = Math.min(centerX, centerY)
        val holeRadius = radius * 0.6f // Adjust this value to control the size of the hole

        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        val totalAngle = 360f

        val completedAngle = totalAngle * completedPercentage / 100
        val uncompletedAngle = totalAngle * uncompletedPercentage / 100

        // Draw the uncompleted hours segment
        canvas.drawArc(rectF, 180 - totalAngle / 2, uncompletedAngle, true, uncompletedPaint)

        // Draw the completed hours segment
        canvas.drawArc(rectF, 180 - totalAngle / 2 + uncompletedAngle, completedAngle, true, completedPaint)

        // Draw the inner circle (hole)
        val whiteCirclePaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
        }
        canvas.drawCircle(centerX, centerY, holeRadius, whiteCirclePaint)
    }
}
