package yy.zpy.cc.memo.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import kotlinx.android.synthetic.main.dialog_preview_memo_setting.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.matchParent
import yy.zpy.cc.memo.R

/**
 * Created by zpy on 2018/5/14.
 */
class PreviewMemoSettingDialog(context: Context, themeResId: Int) : Dialog(context, themeResId) {
    var memoSettingListener: IMemoSettingListener? = null
    private var flag = 0 //0代表所有设置界面，1代表颜色选择界面
    private var type = 0 //0代表设置文字颜色，1代表设置背景颜色
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_preview_memo_setting)
        window.attributes.gravity = Gravity.BOTTOM
        window.attributes.width = matchParent
        init()
        bindClickListener()
    }

    private fun init() {
        v_lightness_slider.setColorPicker(color_picker_view)
        v_alpha_slider.setColorPicker(color_picker_view)
        color_picker_view.addOnColorSelectedListener {
            val color = String.format("#%06X", 0xFFFFFF and it)
            memoSettingListener?.onDrawColor(color, type)
        }
        color_picker_view.addOnColorChangedListener {
            val color = String.format("#%06X", 0xFFFFFF and it)
            memoSettingListener?.onDrawColor(color, type)
        }
        tv_text_color_setting.backgroundResource = R.drawable.bg_oval_text_checked
        tv_background_color_setting.backgroundResource = R.drawable.bg_oval_text_normal
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
            ll_root_setting.visibility = View.GONE
            ll_draw_color.visibility = View.VISIBLE
            flag = 1
        }
        iv_cancel_draw_color.setOnClickListener {
            flag = 0
            ll_root_setting.visibility = View.VISIBLE
            ll_draw_color.visibility = View.GONE
        }
        tv_text_color_setting.setOnClickListener {
            if (type == 1) {
                type = 0
                tv_text_color_setting.backgroundResource = R.drawable.bg_oval_text_checked
                tv_background_color_setting.backgroundResource = R.drawable.bg_oval_text_normal
            }
        }
        tv_background_color_setting.setOnClickListener {
            if (type == 0) {
                type = 1
                tv_text_color_setting.backgroundResource = R.drawable.bg_oval_text_normal
                tv_background_color_setting.backgroundResource = R.drawable.bg_oval_text_checked
            }
        }
    }

    override fun onBackPressed() {
        if (flag == 1) {
            flag = 0
            ll_root_setting.visibility = View.VISIBLE
            ll_draw_color.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    interface IMemoSettingListener {
        fun onFontSizeAdd()
        fun onFontSizeReduce()
        fun onLineHeightAdd()
        fun onLineHeightReduce()
        fun onAlignLeft()
        fun onAlignCenter()
        fun onAlignRight()
        fun onDrawColor(color: String, type: Int)
    }
}