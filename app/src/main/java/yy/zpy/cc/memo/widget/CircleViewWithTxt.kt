package yy.zpy.cc.memo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import org.jetbrains.anko.dip

/**
 * Created by zpy on 16-9-28.
 */

class CircleViewWithTxt : View {
    var paint = Paint()
    var paintNum = Paint(Paint.ANTI_ALIAS_FLAG)
    val rectF = RectF()
    var txt = "0"
    var color: Int = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.isAntiAlias = true
        paint.color = color
        val width = measuredWidth
        val height = measuredHeight
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), (width / 2).toFloat(), paint)

        paintNum.color = Color.WHITE
        paintNum.textSize = dip(15).toFloat()
        paintNum.isAntiAlias = true

        rectF.set(0f, 0f, width.toFloat(), height.toFloat())

        val fontMetrics = paintNum.fontMetricsInt

        val baselineY = (rectF.bottom + rectF.top - fontMetrics.bottom.toFloat() - fontMetrics.top.toFloat()) / 2

        paintNum.textAlign = Paint.Align.CENTER
        canvas.drawText(txt, rectF.centerX(), baselineY, paintNum)
    }
}

