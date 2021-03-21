package com.customview.progressbar

import android.animation.ValueAnimator
import android.animation.ValueAnimator.RESTART
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


class SemiCircleView(context: Context, attributeSet: AttributeSet):View(context, attributeSet) ,
    ValueAnimator.AnimatorUpdateListener{


    private val rimPath= Path()
    private val rimPaint= Paint().apply {
        style=Paint.Style.STROKE
        color= Color.LTGRAY
        isAntiAlias=true
    }
    private val rimRect= RectF()


    private var widthView:Float = 0f
    private var thickness:Float = 0f

    private val frontPath= Path()
    private val frontPaint= Paint().apply {
        style=Paint.Style.FILL
        color= Color.CYAN
        isAntiAlias=true
    }
    private val frontRect= RectF()

    private  var mAnimator: ValueAnimator = ValueAnimator.ofInt(0, 180)


    init {
        mAnimator.apply {
            duration= 1000L
            repeatCount = ValueAnimator.INFINITE
            repeatMode = RESTART
            addUpdateListener(this@SemiCircleView)
        }
    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        thickness = w /8f
        //makes the current width available to our other methods
        widthView = w.toFloat()

        // Set the Rectangle coordinates to the full size of the View
        rimRect.set(0f, 0f, widthView, widthView);

        //This makes sure the Path is empty
        rimPath.reset();

        rimPath.moveTo(thickness, widthView / 2);
        //Draw exterior arc
        rimPath.arcTo(rimRect, 180f, 180f);

        // Draw right closing line
        rimPath.rLineTo(-thickness, 0f);

        // Move the side of rectangle inward (dx positive)
        rimRect.inset(thickness, thickness);
        // Create & Add interior arc based on narrowed rectangle
        rimPath.addArc(rimRect, 0f, -180f);

        // Draw left closing line
        rimPath.rLineTo(-thickness, 0f)


    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(rimPath, rimPaint)
        canvas.drawPath(frontPath, frontPaint)

    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        //gets the current value of our animation
        //gets the current value of our animation
        val value = animation?.animatedValue as Int

        //makes sure the path is empty

        //makes sure the path is empty
        frontPath.reset()



        //sets the rectangle for our arc

        //sets the rectangle for our arc
        frontRect.set(0f, 0f, width.toFloat(),width.toFloat())

        // starts our drawing on the middle left

        // starts our drawing on the middle left
        frontPath.moveTo(0f, (width / 2).toFloat())

        //draws an arc starting at 180° and moving clockwise for the corresponding value

        //draws an arc starting at 180° and moving clockwise for the corresponding value
        frontPath.arcTo(frontRect, 180f, value.toFloat())

        //moves our rectangle inward in order to draw the interior arc

        //moves our rectangle inward in order to draw the interior arc
        frontRect.inset(thickness, thickness)

        //draws the interior arc starting at(180+value)° and moving counter-clockwise for the corresponding value

        //draws the interior arc starting at(180+value)° and moving counter-clockwise for the corresponding value
        frontPath.arcTo(frontRect, (180 + value).toFloat(), -value.toFloat())

        //draws the closing line

        //draws the closing line
        frontPath.rLineTo(-thickness, 0f)

        // Forces the view to reDraw itself

        // Forces the view to reDraw itself



        invalidate()

    }

    fun startAnim() {
        mAnimator.start()

    }
}