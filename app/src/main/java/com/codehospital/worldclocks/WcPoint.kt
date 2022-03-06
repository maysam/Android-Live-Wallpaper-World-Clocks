package com.codehospital.worldclocks

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.TypedValue
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class WcPoint(private var text: String, private var x: Float, private var y: Float, private val paint: Paint, private val dm: DisplayMetrics) {

    private var timeString: String = ""
    private var height: Int = 400
    private  var width:Int = 400
    private var padding = 0
    private var fontSize = 0f
    private val numeralSpacing = 0
    private var handTruncation = 0
    private  var hourHandTruncation:Int = 0
    private var radius = 0

    private var isInit = false
    private val numbers = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    private val rect: Rect = Rect()

    private fun initClock() {
        padding = numeralSpacing + 50
        fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13f, dm)
        val min = min(height, width)
        radius = min / 2 - padding
        handTruncation = min / 20
        hourHandTruncation = min / 7
        isInit = true
    }

    fun draw(canvas: Canvas) {
        if (!isInit) {
            initClock()
        }
        drawCircle(canvas)
        drawCenter(canvas)
        drawHands(canvas)
        drawNumeral(canvas)
    }

    private fun drawHand(canvas: Canvas, loc: Float, isHour: Boolean) {
        val angle = Math.PI * loc / 30 - Math.PI / 2
        val handRadius =
            if (isHour) radius - handTruncation - hourHandTruncation else radius - handTruncation
        canvas.drawLine(
            x + (width / 2.0).toFloat(),
            y + (height / 2.0).toFloat(),
            x + (width / 2 + cos(angle) * handRadius).toFloat(),
            y + (height / 2 + sin(angle) * handRadius).toFloat(),
            paint
        )
    }

    private fun drawHands(canvas: Canvas) {
        val c: Calendar = Calendar.getInstance(TimeZone.getTimeZone(text))
        var hour: Float = c.get(Calendar.HOUR_OF_DAY).toFloat()
        val minute = c.get(Calendar.MINUTE).toFloat()
        val second = c.get(Calendar.SECOND).toFloat()
        timeString = "${hour.toInt()}:${minute.toInt()}:${second.toInt()}"
        hour = hour % 12
        hour = (hour + minute / 60f) * 5f
        drawHand(canvas, hour, true)
        drawHand(canvas, minute, false)
        drawHand(canvas, second, false)
    }

    private fun drawNumeral(canvas: Canvas) {
        paint.textSize = fontSize
        for (number in numbers) {
            val tmp = number.toString()
            paint.getTextBounds(tmp, 0, tmp.length, rect)
            val angle = Math.PI / 6 * (number - 3)
            val xn = (width / 2 + cos(angle) * radius - rect.width() / 2).toFloat()
            val yn = (height / 2 + sin(angle) * radius + rect.height() / 2).toFloat()
            canvas.drawText(tmp, x+xn, y+yn, paint)
        }
        canvas.drawText(text, x, y+height+padding, paint)
        canvas.drawText(timeString, x, y+height+50+padding, paint)
    }

    private fun drawCenter(canvas: Canvas) {
        paint.style = Paint.Style.FILL
        canvas.drawCircle(x+width / 2f, y+height / 2f, 12f, paint)
    }

    private fun drawCircle(canvas: Canvas) {
        paint.reset()
        paint.color = Color.WHITE
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        canvas.drawCircle(x+width / 2f, y+height / 2f, radius + padding - 10f, paint)
    }
}