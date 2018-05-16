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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_preview_memo_setting)
        window.attributes.gravity = Gravity.BOTTOM
        window.attributes.width = matchParent
        bindClickListener()
    }

    private fun bindClickListener() {
        iv_font_size_add.setOnClickListener {
            memoFontSizeSettingListener?.onFontSizeAdd()
        }
        iv_font_size_reduce.setOnClickListener {
            memoFontSizeSettingListener?.onFontSizeReduce()
        }
    }

    interface IMemoFontSizeSettingListener {
        fun onFontSizeAdd()
        fun onFontSizeReduce()
    }
}