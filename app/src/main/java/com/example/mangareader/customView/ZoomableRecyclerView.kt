package com.example.mangareader.customView

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.recyclerview.widget.RecyclerView

class ZoomableRecyclerView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {
    private var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f

    init {
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(e)
        return super.onTouchEvent(e)
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor ?: 1.0f
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f)) // Limit scale factor

            // Apply scale to RecyclerView items
            scaleX = scaleFactor
            scaleY = scaleFactor
            return true
        }


    }
}