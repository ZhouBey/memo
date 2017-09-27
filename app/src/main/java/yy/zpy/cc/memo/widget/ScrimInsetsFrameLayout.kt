package yy.zpy.cc.memo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.widget.FrameLayout
import yy.zpy.cc.memo.R

/**
 * Created by zpy on 2017/9/27.
 */
class ScrimInsetsFrameLayout : FrameLayout {
    private var mInsetForeground: Drawable? = null

    private var mInsets: Rect? = null
    private val mTempRect = Rect()
    private var mOnInsetsCallback: OnInsetsCallback? = null

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs, defStyle)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs,
                R.styleable.ScrimInsetsView, defStyle, 0) ?: return
        mInsetForeground = a.getDrawable(R.styleable.ScrimInsetsView_insetForeground)
        a.recycle()
        setWillNotDraw(true)
    }

    override fun fitSystemWindows(insets: Rect): Boolean {
        mInsets = Rect(insets)
        setWillNotDraw(mInsetForeground == null)
        ViewCompat.postInvalidateOnAnimation(this)
        if (mOnInsetsCallback != null) {
            mOnInsetsCallback!!.onInsetsChanged(insets)
        }
        return true // consume insets
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        val width = width
        val height = height
        if (mInsets != null && mInsetForeground != null) {
            val sc = canvas.save()
            canvas.translate(scrollX.toFloat(), scrollY.toFloat())

            // Top
            mTempRect.set(0, 0, width, mInsets!!.top)
            mInsetForeground!!.bounds = mTempRect
            mInsetForeground!!.draw(canvas)

            // Bottom
            mTempRect.set(0, height - mInsets!!.bottom, width, height)
            mInsetForeground!!.bounds = mTempRect
            mInsetForeground!!.draw(canvas)

            // Left
            mTempRect.set(0, mInsets!!.top, mInsets!!.left, height - mInsets!!.bottom)
            mInsetForeground!!.bounds = mTempRect
            mInsetForeground!!.draw(canvas)

            // Right
            mTempRect.set(width - mInsets!!.right, mInsets!!.top, width, height - mInsets!!.bottom)
            mInsetForeground!!.bounds = mTempRect
            mInsetForeground!!.draw(canvas)

            canvas.restoreToCount(sc)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mInsetForeground != null) {
            mInsetForeground!!.callback = this
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mInsetForeground != null) {
            mInsetForeground!!.callback = null
        }
    }

    /**
     * Allows the calling container to specify a callback for custom processing when insets change (i.e. when
     * [.fitSystemWindows] is called. This is useful for setting padding on UI elements based on
     * UI chrome insets (e.g. a Google Map or a ListView). When using with ListView or GridView, remember to set
     * clipToPadding to false.
     */
    fun setOnInsetsCallback(onInsetsCallback: OnInsetsCallback) {
        mOnInsetsCallback = onInsetsCallback
    }

    interface OnInsetsCallback {
        fun onInsetsChanged(insets: Rect)
    }
}