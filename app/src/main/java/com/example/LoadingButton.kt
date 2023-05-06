package com.example

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var label = ""

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 60f
        textAlign = Paint.Align.CENTER
    }

    private val valueAnimator = ValueAnimator()


    init {
        isClickable = true
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paint = Paint()

        when (buttonState) {
            ButtonState.Clicked -> {
                paint.color = context.getColor(R.color.colorPrimary)
                label = resources.getString(R.string.download)
            }
            ButtonState.Loading -> {
                paint.color = context.getColor(R.color.colorPrimaryDark)
                label = resources.getString(R.string.downloading)
            }
            ButtonState.Completed -> {
                paint.color = context.getColor(R.color.colorPrimary)
                label = resources.getString(R.string.download)
            }
        }
        invalidate()



        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rect, 10f, 10f, paint)

        val textHeight = textPaint.descent() - textPaint.ascent()
        val textOffset = textHeight / 2 - textPaint.descent()

        canvas.drawText(
            label, width.toFloat() / 2,
            height.toFloat() / 2 + textOffset, textPaint
        )


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}