package yy.zpy.cc.memo.activity

import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_edit_memo.*
import kotlinx.android.synthetic.main.dialog_select_dialog.*
import org.jetbrains.anko.*
import org.jetbrains.anko.collections.forEachWithIndex
import yy.zpy.cc.memo.R
import yy.zpy.cc.memo.dialog.SelectFolderDialog
import yy.zpy.cc.memo.interf.IBaseUI
import yy.zpy.cc.memo.interf.IKeyboardShowChangeListener
import kotlin.properties.Delegates




/**
 * Created by zpy on 2017/9/11.
 */
class MemoEditActivity : BaseActivity(), IBaseUI {
    override fun getLayout() = R.layout.activity_edit_memo
    var lockStatus = false
    var keyboardShowChangeListener = KeyboardShowChangeListener()
    var globalListener by Delegates.notNull<ViewTreeObserver.OnGlobalLayoutListener>()
    private val MIN_KEYBOARD_HEIGHT_PX = 150
    var decorView: View? = null
    var selectFolderDialog by Delegates.notNull<SelectFolderDialog>()

    val names = mutableListOf("aa", "bb", "cc", "dd", "ee", "ff", "gg")
    val data = mutableListOf<MutableMap<String, Any>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        viewListener()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_slide_no, R.anim.anim_slide_out_right)
    }

    inner class KeyboardShowChangeListener : IKeyboardShowChangeListener {
        override fun keyboardShow() {
            ib_edit_submit_or_preview.imageResource = R.drawable.ic_edit_submit
            ib_image_or_delete.imageResource = R.drawable.ic_image
        }

        override fun keyboardHidden() {
            ib_edit_submit_or_preview.imageResource = R.drawable.ic_memo_preview
            ib_image_or_delete.imageResource = R.drawable.ic_memo_delete
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        decorView?.viewTreeObserver?.removeGlobalOnLayoutListener(globalListener)
    }

    override fun viewListener() {
        ib_back.setOnClickListener {
            this@MemoEditActivity.finish()
            overridePendingTransition(R.anim.anim_slide_no, R.anim.anim_slide_out_right)
        }
        iv_lock_status.setOnClickListener {
            lockStatus = !lockStatus
            iv_lock_status.isSelected = lockStatus
        }
        btn_select_fold.setOnClickListener {
            selectFolderDialog.show()
        }
    }

    override fun initView() {
        setSupportActionBar(toolbar_edit_memo)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        decorView = window.decorView
        globalListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            private val windowVisibleDisplayFrame = Rect()
            private var lastVisibleDecorViewHeight: Int = 0

            override fun onGlobalLayout() {
                decorView?.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame)
                val visibleDecorViewHeight = windowVisibleDisplayFrame.height()
                if (lastVisibleDecorViewHeight != 0) {
                    if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
                        keyboardShowChangeListener.keyboardShow()
                    } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                        keyboardShowChangeListener.keyboardHidden()
                    }
                }
                lastVisibleDecorViewHeight = visibleDecorViewHeight
            }
        }
        decorView?.viewTreeObserver?.addOnGlobalLayoutListener(globalListener)
        names.forEachWithIndex { index, item ->
            data.add(mutableMapOf("name" to item, "isCheck" to (index == 0)))
        }
        btn_select_fold.text = names[0]
        selectFolderDialog = SelectFolderDialog(this, R.style.WhiteDialog, data, object : SelectFolderDialog.OnClickListener {
            override fun itemClick(position: Int, type: Int) {
                Log.e("aa", position.toString())
                data.forEach {
                    it["isCheck"] = false
                }
                data[position]["isCheck"] = true
                selectFolderDialog.rv_dialog_folder.adapter.notifyDataSetChanged()
                val name = data[position]["name"] as String
                btn_select_fold.text = name
                selectFolderDialog.dismiss()
            }

            override fun newFolderClick() {
                selectFolderDialog.dismiss()
                alert {
                    var etFolderName by Delegates.notNull<EditText>()
                    var tvTip by Delegates.notNull<TextView>()
                    customView {
                        verticalLayout {
                            lparams(matchParent, wrapContent) {
                                //                                val padding = dip(15)
//                                setPadding(padding, padding, padding, 0)
//                                horizontalMargin = dip(15)
                                verticalPadding = dip(20)
                                horizontalPadding = dip(15)
                            }
                            textView("新建文件夹") {
                                textSize = sp(9).toFloat()
                                textColor = R.color.colorFont
                            }.lparams(wrapContent, wrapContent) {
                                marginStart = dip(3)
                            }
                            etFolderName = editText {
                                singleLine = true
                                textSize = sp(9).toFloat()
                                textColor = R.color.colorFont
                            }.lparams(matchParent, wrapContent) {
                                topMargin = dip(20)
                            }
                            tvTip = textView {
                                textSize = sp(3).toFloat()
                                textColor = R.color.colorAccent
                                visibility = View.INVISIBLE
                            }.lparams(wrapContent, wrapContent) {
                                topMargin = dip(3)
                            }
                        }
                        okButton {
                            val folderName = etFolderName.text.trim().toString()
                            Log.e("bbbbbb",folderName)
                            if (TextUtils.isEmpty(folderName)) {
                                tvTip.visibility = View.VISIBLE
                                tvTip.text = "名字不能为空"
                                Log.e("aa","名字不能为空")
                                return@okButton
                            }
                            data.forEach {
                                it["isCheck"] = false
                            }
                            data.add(mutableMapOf("name" to folderName, "isCheck" to true))
                            selectFolderDialog.rv_dialog_folder.adapter.notifyDataSetChanged()
                            btn_select_fold.text = folderName
                        }
                        cancelButton {

                        }
                    }
                }.show()
            }

        })
    }

    override fun show() {

    }
}