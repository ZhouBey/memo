package yy.zpy.cc.memo.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_edit_memo.*
import org.jetbrains.anko.imageResource
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
        selectFolderDialog = SelectFolderDialog(this, R.style.WhiteDialog)
    }

    override fun show() {

    }
}