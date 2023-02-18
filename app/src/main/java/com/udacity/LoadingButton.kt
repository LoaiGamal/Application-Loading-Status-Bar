package com.udacity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var buttonText: String? = null

    private var valueAnimator = ValueAnimator()
    private var loadingProgress = 0f

    private var circleAnimator = ValueAnimator()
    private var angle = 0f

    private var defaultButtonColor = 0
    private var progressColor = 0
    private var circleColor = 0

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 60.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                buttonText = resources.getString(R.string.button_loading)
                valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
                valueAnimator.duration = 4000
                valueAnimator.addUpdateListener {
                    loadingProgress = valueAnimator.animatedValue as Float
                    invalidate()
                }
                valueAnimator.repeatMode = ValueAnimator.REVERSE
                valueAnimator.repeatCount = ValueAnimator.INFINITE
                valueAnimator.start()

                circleAnimator = ValueAnimator.ofFloat(-360f, 0f, angle)
                circleAnimator.duration = 3000
                circleAnimator.addUpdateListener {
                    angle = circleAnimator.animatedValue as Float
                    valueAnimator.repeatCount = ValueAnimator.INFINITE

                    invalidate()
                }
                circleAnimator.start()
            }

            ButtonState.Completed -> {
                buttonText = resources.getString(R.string.button_name)
                valueAnimator.cancel()
                loadingProgress = 0.0f
                invalidate()
            }
        }
    }


    init {
        isClickable = true

        buttonState = ButtonState.Completed

        context.withStyledAttributes(attrs, R.styleable.LoadingButton){
            defaultButtonColor = getColor(R.styleable.LoadingButton_defaultButtonColor, 0)
            progressColor = getColor(R.styleable.LoadingButton_progressColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
        }
    }


    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        widthSize = width
        heightSize = height
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.color = defaultButtonColor
        canvas.drawRect(
            (width / 2 - widthSize / 2).toFloat(),
            (height / 2 - heightSize / 2).toFloat(),
            (width / 2 + widthSize / 2).toFloat(),
            (height / 2 + heightSize / 2).toFloat(),
            paint
        )

        paint.color = progressColor
        canvas.drawRect(
            (width / 2 - widthSize / 2).toFloat(),
            (height / 2 - heightSize / 2).toFloat(),
            (width / 2 - widthSize / 2 + loadingProgress).toFloat(),
            (height / 2 + heightSize / 2).toFloat(),
            paint
        )

        paint.color = Color.WHITE
        buttonText?.let {
            canvas.drawText(
                it,
                width / 2.toFloat(),
                (height / 1.5).toFloat(),
                paint
            )
        }

        paint.color = circleColor
        val arcBounds: RectF =
            RectF(
                (width * 0.85 - 20).toFloat(),
                (height / 2 - 20).toFloat(),
                (width * 0.85 + 20).toFloat(),
                (height / 2 + 20).toFloat()
            )
        canvas.drawArc(arcBounds, 0f, angle, true, paint)
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