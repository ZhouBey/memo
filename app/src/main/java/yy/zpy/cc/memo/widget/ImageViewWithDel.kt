package yy.zpy.cc.memo.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import org.jetbrains.anko.dip
import yy.zpy.cc.memo.R
import kotlin.properties.Delegates


/**
 * Created by zpy on 2017/10/12.
 */
class ImageViewWithDel : ImageView {
    var isSelect = false
    var bitmap: Bitmap by Delegates.notNull<Bitmap>()
    var onDeleteClickListener: OnDeleteClickListener? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, -1)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        init()
    }

    fun init() {
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_del_picture)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isSelect) {
            canvas?.drawBitmap(bitmap, width.minus(bitmap.width.toFloat().plus(dip(10).toFloat())), dip(10).toFloat(), null)
            drawable?.setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY)
        } else {
            drawable?.clearColorFilter()
        }
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            val xDiff = width.minus(bitmap.width).minus(dip(10).toFloat())
            val yDiff = bitmap.height.plus(dip(20).toFloat())
            if (event.x > xDiff && event.y < yDiff && isSelect) {
                onDeleteClickListener?.delete()
            } else {
                performClick()
            }
        }
        return true
    }
}

interface OnDeleteClickListener {
    fun delete()
}