package com.customview.progressbar

import android.graphics.*


class AnimationModel(val animPath: Path, val bitmap:Bitmap?) {

    var pathMeasure:PathMeasure
    private set

    var pathLength: Float
    private set

    var bitmapOffsetX:Float
    private set

    var bitmapOffsetY:Float
        private set

    var pos=FloatArray(2)
    var tan=FloatArray(2)
    var matrix= Matrix()

    init {
        bitmapOffsetY=(bitmap?.height?:0)*0.9f
        bitmapOffsetX=(bitmap?.width?:0)*0.9f
        pathMeasure=PathMeasure(animPath, false)
        pathLength=pathMeasure.length
    }
}