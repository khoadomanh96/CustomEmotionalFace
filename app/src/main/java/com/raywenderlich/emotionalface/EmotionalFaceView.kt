package com.raywenderlich.emotionalface

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.min

/**
 * Created by khoado on 27,July,2020
 */

class EmotionalFaceView(context: Context, attrs: AttributeSet) : View(context, attrs) {


    // 2
    private var faceColor = DEFAULT_FACE_COLOR
    private var eyesColor = DEFAULT_EYES_COLOR
    private var mouthColor = DEFAULT_MOUTH_COLOR
    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = DEFAULT_BORDER_WIDTH

    private val paint = Paint()
    private val mouthPath = Path()
    private var size = 0
    private lateinit var point: Point
    private var pointHappy = Point((size * 0.80f).toInt(), (size * 0.90f).toInt()) // to size * 0.50f, size * 0.78f
    private var pointSad= Point((size * 0.50f).toInt(), (size * 0.60f).toInt()) // to size * 0.50f, size * 0.78f
    // 3
    var happinessState = HAPPY
        set(state) {
            field = state
            // 4
            ValueAnimator.ofInt(0, 100).apply {
                duration = 650
                interpolator = LinearInterpolator()
                addUpdateListener { valueAnimator ->
                    val value = valueAnimator.animatedValue as Int
                    point = when (state) {
                        HAPPY -> {
                            val x = pointSad.x + (pointHappy.x - pointSad.x)* value/100
                            val y = pointSad.y + (pointHappy.y - pointSad.y)* value/100
                            Log.e("khoado", "x = " + x + " y = " + y)
                            Point(x,y)
                        }
                        else -> {
                            val x = pointHappy.x - (pointHappy.x - pointSad.x)* value/100
                            val y = pointHappy.y - (pointHappy.y - pointSad.y)* value/100
                            Log.e("khoado", "x = " + x + " y = " + y)
                            Point(x,y)
                        }
                    }
                    invalidate()
                }
            }.start()
        }

    // 5
    init {
        paint.isAntiAlias = true
        setupAttributes(attrs)

    }


    private fun setupAttributes(attrs: AttributeSet) {
        // 6
        // Obtain a typed array of attributes
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.EmotionalFaceView,
                0, 0)

        // 7
        // Extract custom attributes into member variables
        happinessState = typedArray.getInt(R.styleable.EmotionalFaceView_state, HAPPY.toInt()).toLong()
        faceColor = typedArray.getColor(R.styleable.EmotionalFaceView_faceColor, DEFAULT_FACE_COLOR)
        eyesColor = typedArray.getColor(R.styleable.EmotionalFaceView_eyesColor, DEFAULT_EYES_COLOR)
        mouthColor = typedArray.getColor(R.styleable.EmotionalFaceView_mouthColor, DEFAULT_MOUTH_COLOR)
        borderColor = typedArray.getColor(R.styleable.EmotionalFaceView_borderColor,
                DEFAULT_BORDER_COLOR)
        borderWidth = typedArray.getDimension(R.styleable.EmotionalFaceView_borderWidth,
                DEFAULT_BORDER_WIDTH)
        when (happinessState) {
            HAPPY -> {
                point = pointHappy
            }
            else -> {
                point = pointSad

            }
        }
        // 8
        // TypedArray objects are shared and must be recycled.
        typedArray.recycle()
    }


    override fun onDraw(canvas: Canvas) {
        // call the super method to keep any drawing from the parent side.
        super.onDraw(canvas)

        drawFaceBackground(canvas)
        drawEyes(canvas)
        drawMouth(canvas)
    }

    private fun drawFaceBackground(canvas: Canvas) {
        //1
        paint.color = faceColor
        //2
        val radius = size / 2f
        //3
        canvas.drawCircle(size / 2f, size / 2f, radius, paint)
        //4
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth
        //5
        canvas.drawCircle(size/2f, size/2f, radius - borderWidth /2f, paint)
    }

    private fun drawEyes(canvas: Canvas) {
        //1
        paint.color = eyesColor
        paint.style = Paint.Style.FILL
        //2
        val leftEyeRect = RectF(size*0.32f,size*0.23f,size*0.43f,size*0.50f)
        canvas.drawOval(leftEyeRect, paint)
        //3
        val rightEyeRect = RectF(size * 0.57f, size * 0.23f, size * 0.68f, size *0.5f)
        canvas.drawOval(rightEyeRect,paint)

    }

    private fun drawMouth(canvas: Canvas) {
        mouthPath.reset()
        //1
        mouthPath.moveTo(size * 0.22f, size * 0.7f)
        if (happinessState == HAPPY) {
            // 1
//            mouthPath.quadTo(size * 0.5f, size * 0.80f, size * 0.78f, size * 0.7f)
//            mouthPath.quadTo(size * 0.5f, size * 0.90f, size * 0.22f, size * 0.7f)
            mouthPath.quadTo(size * 0.5f, point.x.toFloat(), size * 0.78f, size * 0.7f)
            mouthPath.quadTo(size * 0.5f, point.y.toFloat(), size * 0.22f, size * 0.7f)
        } else {
            // 2
//            mouthPath.quadTo(size * 0.5f, size * 0.50f, size * 0.78f, size * 0.7f)
//            mouthPath.quadTo(size * 0.5f, size * 0.60f, size * 0.22f, size * 0.7f)
            mouthPath.quadTo(size * 0.5f, point.x.toFloat(), size * 0.78f, size * 0.7f)
            mouthPath.quadTo(size * 0.5f, point.y.toFloat(), size * 0.22f, size * 0.7f)
        }
        paint.color = mouthColor
        paint.style = Paint.Style.FILL
        //5
        canvas.drawPath(mouthPath, paint)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 1
        size = min(measuredWidth, measuredHeight)
        // 2
        pointHappy = Point((size * 0.80f).toInt(), (size * 0.90f).toInt())
        pointSad= Point((size * 0.50f).toInt(), (size * 0.60f).toInt())
        setMeasuredDimension(size, size)
    }
    // 1
    companion object {
        private const val DEFAULT_FACE_COLOR = Color.YELLOW
        private const val DEFAULT_EYES_COLOR = Color.BLACK
        private const val DEFAULT_MOUTH_COLOR = Color.BLACK
        private const val DEFAULT_BORDER_COLOR = Color.BLACK
        private const val DEFAULT_BORDER_WIDTH = 4.0f

        const val HAPPY = 0L
        const val SAD = 1L
    }

}