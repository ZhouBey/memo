package yy.zpy.cc.memo.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Created by zpy on 2017/10/12.
 */
class ImageViewWithDel : ImageView {
    var isSelect = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        isSelect = !isSelected
        if (isSelect) {
            alpha = 0.7f
        } else {
            alpha = 0.0f
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}