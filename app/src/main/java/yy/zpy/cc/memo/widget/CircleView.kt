package yy.zpy.cc.memo.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import org.jetbrains.anko.dip


/**
 * Created by zpy on 16-9-28.
 */

class CircleView : View {
    private var paint = Paint()
    private var paintNum = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()
    private var txt = ""
    var color: Int = 0
    var image: Bitmap? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.isAntiAlias = true
        paint.color = color
        canvas.drawCircle((measuredWidth / 2).toFloat(), (measuredHeight / 2).toFloat(), (measuredWidth / 2).toFloat(), paint)
        if (image != null) {
            canvas.drawBitmap(image, width.minus(image!!.width).div(2).toFloat(), height.minus(image!!.height).div(2).toFloat(), null)
        } else {
            paintNum.color = Color.WHITE
            paintNum.textSize = dip(15).toFloat()
            paintNum.isAntiAlias = true
            rectF.set(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
            val fontMetrics = paintNum.fontMetricsInt
            val baselineY = (rectF.bottom + rectF.top - fontMetrics.bottom.toFloat() - fontMetrics.top.toFloat()) / 2
            paintNum.textAlign = Paint.Align.CENTER
            canvas.drawText(txt, rectF.centerX(), baselineY, paintNum)
        }
    }

    fun setText(text: String) {
        txt = text
        image = null
        invalidate()
    }

    fun setBitmap(image: Bitmap) {
        this.image = image
        txt = ""
        invalidate()
    }
}

