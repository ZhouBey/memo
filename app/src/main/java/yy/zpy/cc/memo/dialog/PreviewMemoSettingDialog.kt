package yy.zpy.cc.memo.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import kotlinx.android.synthetic.main.dialog_preview_memo_setting.*
import org.jetbrains.anko.matchParent
import yy.zpy.cc.memo.R

/**
 * Created by zpy on 2018/5/14.
 */
class PreviewMemoSettingDialog(context: Context, themeResId: Int) : Dialog(context, themeResId) {
    var memoSettingListener: IMemoSettingListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_preview_memo_setting)
        window.attributes.gravity = Gravity.BOTTOM
        window.attributes.width = matchParent
        bindClickListener()
    }

    private fun bindClickListener() {
        tv_font_size_add.setOnClickListener {
            memoSettingListener?.onFontSizeAdd()
        }
        tv_font_size_reduce.setOnClickListener {
            memoSettingListener?.onFontSizeReduce()
        }
        tv_font_line_add.setOnClickListener {
            memoSettingListener?.onLineHeightAdd()
        }
        tv_font_line_reduce.setOnClickListener {
            memoSettingListener?.onLineHeightReduce()
        }
        iv_font_alignment_left.setOnClickListener {
            memoSettingListener?.onAlignLeft()
        }
        iv_font_alignment_center.setOnClickListener {
            memoSettingListener?.onAlignCenter()
        }
        iv_font_alignment_right.setOnClickListener {
            memoSettingListener?.onAlignRight()
        }
        iv_draw_color.setOnClickListener {
//            memoSettingListener?.onDrawColor()
        }
    }

    interface IMemoSettingListener{
        fun onFontSizeAdd()
        fun onFontSizeReduce()
        fun onLineHeightAdd()
        fun onLineHeightReduce()
        fun onAlignLeft()
        fun onAlignCenter()
        fun onAlignRight()
        fun onDrawColor(color:String)
    }
}