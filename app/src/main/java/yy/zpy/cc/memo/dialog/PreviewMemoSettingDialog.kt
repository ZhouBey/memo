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
    var memoFontSizeSettingListener: IMemoFontSizeSettingListener? = null
    var memoLineHeightSettingListener: IMemoLineHeightSettingListener? = null
    var memoAlignSettingListener: IMemoAlignSettingListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_preview_memo_setting)
        window.attributes.gravity = Gravity.BOTTOM
        window.attributes.width = matchParent
        bindClickListener()
    }

    private fun bindClickListener() {
        tv_font_size_add.setOnClickListener {
            memoFontSizeSettingListener?.onFontSizeAdd()
        }
        tv_font_size_reduce.setOnClickListener {
            memoFontSizeSettingListener?.onFontSizeReduce()
        }
        tv_font_line_add.setOnClickListener {
            memoLineHeightSettingListener?.onLineHeightAdd()
        }
        tv_font_line_reduce.setOnClickListener {
            memoLineHeightSettingListener?.onLineHeightReduce()
        }
        iv_font_alignment_left.setOnClickListener {
            memoAlignSettingListener?.onAlignLeft()
        }
        iv_font_alignment_center.setOnClickListener {
            memoAlignSettingListener?.onAlignCenter()
        }
        iv_font_alignment_right.setOnClickListener {
            memoAlignSettingListener?.onAlignRight()
        }
    }

    interface IMemoFontSizeSettingListener {
        fun onFontSizeAdd()
        fun onFontSizeReduce()
    }

    interface IMemoLineHeightSettingListener {
        fun onLineHeightAdd()
        fun onLineHeightReduce()
    }

    interface IMemoAlignSettingListener {
        fun onAlignLeft()
        fun onAlignCenter()
        fun onAlignRight()
    }
}