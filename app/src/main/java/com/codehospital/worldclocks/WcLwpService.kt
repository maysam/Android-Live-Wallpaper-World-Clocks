package com.codehospital.worldclocks

import android.content.SharedPreferences
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.preference.PreferenceManager
import kotlin.Boolean
import kotlin.Int


class WcLwpService : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return MyWallpaperEngine()
    }

    private inner class MyWallpaperEngine : Engine() {
        private val handler: Handler = Handler(Looper.getMainLooper())
        private val drawRunner = Runnable { draw() }
        private val circles: MutableList<WcPoint>
        private val paint = Paint()
        var width = 1080
        var height = 1920
        private var visible = true
        private val maxNumber: Int
        private val touchEnabled: Boolean
        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                handler.post(drawRunner)
            } else {
                handler.removeCallbacks(drawRunner)
            }
        }

        init {
            val prefs: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this@WcLwpService)
            maxNumber = Integer.valueOf(prefs.getString("numberOfCircles", "40")!!)
            touchEnabled = prefs.getBoolean("touch", false)
            circles = ArrayList()
            val timezones = listOf("Australia/Darwin", "Asia/Tehran", "Europe/Amsterdam", "America/Los_Angeles")
            var counter = 0
            for (dx in 1..2) {
                for (dy in 1..2) {
                    val x = width*(dx/2f - 0.25f)
                    val y = height*(dy/3f - 0.05f)
                    circles.add(WcPoint(timezones[counter++], x-200, y-200, paint, resources.displayMetrics))
                }
            }

            paint.isAntiAlias = true
            paint.color = Color.WHITE
            paint.style = Paint.Style.STROKE
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeWidth = 10f
            handler.post(drawRunner)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            visible = false
            handler.removeCallbacks(drawRunner)
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?, format: Int,
            _width: Int, _height: Int
        ) {
            width = _width
            height = _height
            super.onSurfaceChanged(holder, format, width, height)
        }

        override fun onTouchEvent(event: MotionEvent) {
            if (touchEnabled) {
                val x = event.x
                val y = event.y
                val holder: SurfaceHolder = surfaceHolder
                var canvas: Canvas? = null
                try {
                    canvas = holder.lockCanvas()
                    if (canvas != null) {
                        canvas.drawColor(Color.BLACK)
//                        circles.clear()
                        circles.add(WcPoint((circles.size + 1).toString(), x, y, paint, resources.displayMetrics))
                        drawCircles(canvas, circles)
                    }
                } finally {
                    if (canvas != null) holder.unlockCanvasAndPost(canvas)
                }
                super.onTouchEvent(event)
            }
        }

        private fun draw() {
            val holder: SurfaceHolder = surfaceHolder
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    drawCircles(canvas, circles)
                }
            } finally {
                if (canvas != null) holder.unlockCanvasAndPost(canvas)
            }
            handler.removeCallbacks(drawRunner)
            if (visible) {
                handler.postDelayed(drawRunner, 1000)
            }
        }

        // Surface view requires that all elements are drawn completely
        private fun drawCircles(canvas: Canvas, circles: MutableList<WcPoint>) {
            canvas.drawColor(Color.BLACK)
            for (point in circles) {
                point.draw(canvas)
            }
        }
    }
}