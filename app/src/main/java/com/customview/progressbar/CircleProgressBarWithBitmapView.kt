package com.customview.progressbar

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import kotlin.math.atan2


class CircleProgressBarWithBitmapView(context: Context, attributeSet: AttributeSet) :
    View(context, attributeSet){


    private val bitmap = getBitmapFromVectorDrawable(R.drawable.ic_baby_boy)

    private val bitmapPath = Path()

    private val paintAnimPath = Paint().apply {
        color = Color.TRANSPARENT
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }

    private lateinit var animationModel: AnimationModel

    private lateinit var bitmapAnimator: ValueAnimator

    private  var circleAnimator: ValueAnimator = ValueAnimator.ofInt(0,360)

    private var centerX=0f
    private var centerY=0f

    private var radius=0f

    private var thickness= 0f

    private val progressPath= Path()

    private val paintCircleProgress= Paint().apply {
        style=Paint.Style.FILL
        isAntiAlias=true
    }
    private val circleProgressBoundRect= RectF()


    init {
        circleAnimator.apply {
            repeatCount = INFINITE
            addUpdateListener { animation->
                val value = animation?.animatedValue as Int
                drawCircleProgress(value)
            }
        }

        val ta: TypedArray = getContext().obtainStyledAttributes(attributeSet,
            R.styleable.CircleProgressBarWithBitmapView
        )
        try {
            val color = ta.getColor(R.styleable.CircleProgressBarWithBitmapView_progressColor, Color.RED)
            thickness=ta.getDimension(R.styleable.CircleProgressBarWithBitmapView_progressThickness,10f)
            paintCircleProgress.color=color
        }
        finally {
            ta.recycle()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX=w/2f
        centerY=h/2f
        radius=h/4f
        initValueBitmapAnim()
        startAnim()
    }

    private fun initValueBitmapAnim(){
        bitmapPath.addCircle(centerX, centerY, radius, Path.Direction.CW)
        animationModel = AnimationModel(bitmapPath,bitmap)
        bitmapAnimator=ValueAnimator.ofFloat(0f, animationModel.pathLength)
        bitmapAnimator.apply {
            repeatCount = INFINITE
            addUpdateListener {animation->
                val value = animation?.animatedValue as Float
                drawBitmapOverPath(value)
            }
        }
    }

    private fun drawCircleProgress(value:Int){
        progressPath.reset()
       // frontRect.set(centerX-radius-thickness, centerY-radius-thickness, centerX+radius+thickness,centerY+radius+thickness)
        circleProgressBoundRect.set(centerX-radius, centerY-radius, centerX+radius,centerY+radius)
        progressPath.arcTo(circleProgressBoundRect, 0f, value.toFloat())
        circleProgressBoundRect.inset(thickness, thickness)
        progressPath.arcTo(circleProgressBoundRect, (0 + value).toFloat(), -value.toFloat())
        progressPath.rLineTo(-thickness, 0f)
        invalidate()
    }

    private fun drawBitmapOverPath(value: Float){
        animationModel.pathMeasure.getPosTan(value, animationModel.pos, animationModel.tan)
        animationModel.matrix.reset()
        val degrees =
            (atan2(animationModel.tan[1].toDouble(), animationModel.tan[0].toDouble()) * 180.0 / Math.PI).toFloat()
        animationModel.matrix.postRotate(degrees, animationModel.bitmapOffsetX, animationModel.bitmapOffsetY)
        animationModel.matrix.postTranslate(animationModel.pos[0]-(animationModel.bitmapOffsetY) , animationModel.pos[1]-(animationModel.bitmapOffsetY) )
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawPath(animationModel.animPath,paintAnimPath)

        animationModel.bitmap?.let { bitmap ->
            canvas.drawBitmap(bitmap, animationModel.matrix, null)
        }

        canvas.drawPath(progressPath, paintCircleProgress)

    }

    companion object {
        const val TAG = "BitmapMovePathView"
    }

    private fun startAnim(){
        val animationSet=AnimatorSet()
        animationSet.apply {
            interpolator=LinearInterpolator()
            duration=3000
            playTogether(bitmapAnimator,circleAnimator)
        }.start()
    }

    fun cancelAnim(){
        bitmapAnimator.cancel()
    }

    private fun getBitmapFromVectorDrawable(drawableId:Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context,drawableId)
        drawable?.let { drw->
            var drawableTemp=drw
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                drawableTemp = DrawableCompat.wrap(drw).mutate()
            }
            val bitmap = Bitmap.createBitmap(
                drawableTemp.intrinsicWidth,
                drawableTemp.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawableTemp.setBounds(0, 0, canvas.width, canvas.height)
            drawableTemp.draw(canvas)
            return bitmap
        }
        return null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        bitmapAnimator.cancel()
    }
}