package com.example.mangareader.customView

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.recyclerview.widget.RecyclerView

class ZoomableRecyclerView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {
    var scaleFactor = 1f
    val zoomMatrix = Matrix() // renamed from 'matrix' to 'zoomMatrix'

    val scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true // ensure we handle the scale event
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = scaleFactor.coerceIn(0.1f, 5.0f)

            zoomMatrix.setScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY) // use 'zoomMatrix' here
            return true
        }
    })

    override fun onTouchEvent(e: MotionEvent): Boolean {
        super.onTouchEvent(e)
        return scaleGestureDetector.onTouchEvent(e)
    }
}