package com.natan.klinik.utils

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ZoomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val matrix = Matrix()
    private val savedMatrix = Matrix()

    // States
    private var mode = NONE
    private val start = PointF()
    private val mid = PointF()
    private var oldDist = 1f
    private var savedScale = 1f

    // Scale limits
    private val minScale = 1f
    private val maxScale = 5f

    // Scale detector
    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())

    companion object {
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
    }

    init {
        scaleType = ScaleType.MATRIX
        imageMatrix = matrix

        setOnTouchListener { _, event ->
            scaleDetector.onTouchEvent(event)
            handleTouch(event)
            true
        }
    }

    private fun handleTouch(event: MotionEvent) {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(matrix)
                start.set(event.x, event.y)
                mode = DRAG
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                if (oldDist > 10f) {
                    savedMatrix.set(matrix)
                    midPoint(mid, event)
                    mode = ZOOM
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
            }

            MotionEvent.ACTION_MOVE -> {
                if (mode == DRAG) {
                    matrix.set(savedMatrix)
                    val dx = event.x - start.x
                    val dy = event.y - start.y
                    matrix.postTranslate(dx, dy)
                } else if (mode == ZOOM) {
                    val newDist = spacing(event)
                    if (newDist > 10f) {
                        matrix.set(savedMatrix)
                        val scale = newDist / oldDist
                        matrix.postScale(scale, scale, mid.x, mid.y)
                    }
                }
            }
        }

        // Apply matrix and check bounds
        checkAndSetImageMatrix()
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor
            val newScale = savedScale * scaleFactor

            if (newScale in minScale..maxScale) {
                matrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                checkAndSetImageMatrix()
            }
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mode = ZOOM
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            super.onScaleEnd(detector)
            savedScale = getCurrentScale()
        }
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return kotlin.math.sqrt(x * x + y * y)
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)
    }

    private fun checkAndSetImageMatrix() {
        val drawable = drawable ?: return

        val values = FloatArray(9)
        matrix.getValues(values)

        val scaleX = values[Matrix.MSCALE_X]
        val scaleY = values[Matrix.MSCALE_Y]
        val transX = values[Matrix.MTRANS_X]
        val transY = values[Matrix.MTRANS_Y]

        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight

        val scaledWidth = drawableWidth * scaleX
        val scaledHeight = drawableHeight * scaleY

        var deltaX = 0f
        var deltaY = 0f

        // Check horizontal bounds
        if (scaledWidth <= width) {
            deltaX = (width - scaledWidth) / 2 - transX
        } else {
            if (transX > 0) deltaX = -transX
            else if (transX + scaledWidth < width) deltaX = width - scaledWidth - transX
        }

        // Check vertical bounds
        if (scaledHeight <= height) {
            deltaY = (height - scaledHeight) / 2 - transY
        } else {
            if (transY > 0) deltaY = -transY
            else if (transY + scaledHeight < height) deltaY = height - scaledHeight - transY
        }

        matrix.postTranslate(deltaX, deltaY)
        imageMatrix = matrix
    }

    private fun getCurrentScale(): Float {
        val values = FloatArray(9)
        matrix.getValues(values)
        return values[Matrix.MSCALE_X]
    }

    // Method untuk reset zoom
    fun resetZoom() {
        matrix.reset()
        savedScale = 1f
        imageMatrix = matrix
        invalidate()
    }
}