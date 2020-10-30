package com.example.bitrapezoidfillview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Color
import android.graphics.RectF
import android.app.Activity
import android.content.Context

val colors : Array<Int> = arrayOf(
    "#F44336",
    "#4CAF50",
    "#673AB7",
    "#FFC107",
    "#2196F3"
).map {
    Color.parseColor(it)
}.toTypedArray()
val parts : Int = 7
val strokeFactor : Float = 90f
val sizeFactor : Float = 2.9f
val scGap : Float = 0.02f / parts
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val rot : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawTrapezoidFill(scale : Float, size : Float, paint : Paint) {
    save()
    val path : Path = Path()
    path.moveTo(0f, 0f)
    path.lineTo(-size, 0f)
    path.lineTo(-size, -size / 2)
    path.lineTo(-size / 2, -size / 2)
    path.lineTo(0f, 0f)
    drawRect(RectF(-size, -size * 0.5f * scale, 0f, 0f), paint)
    restore()
}

fun Canvas.drawBiTrapezoidFill(scale : Float, w : Float, h : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val size : Float = Math.min(w, h) / sizeFactor
    save()
    translate(w / 2, h / 2)
    rotate(rot * sf.divideScale(5, parts))
    drawLine(0f, 0f, -size * sf.divideScale(0, parts), 0f, paint)
    drawLine(-size, 0f, -size, -size * 0.5f * sf.divideScale(1, parts), paint)
    drawLine(-size, -size / 2, -size + size * 0.5f * sf.divideScale(2, parts), -size / 2, paint)
    drawLine(-size / 2, -size / 2, -size / 2 * (1 - sf.divideScale(3, parts)), -size / 2 * (1 - sf.divideScale(3, parts)), paint)
    drawTrapezoidFill(sf.divideScale(4, parts), size, paint)
    restore()
}

fun Canvas.drawBTFNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawBiTrapezoidFill(scale, w, h, paint)
}

class BiTrapezoidFillView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }
}