package yy.zpy.cc.memo.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import kotlinx.android.synthetic.main.activity_edit_memo.*
import kotlinx.android.synthetic.main.dialog_select_dialog.*
import org.jetbrains.anko.*
import org.jetbrains.anko.collections.forEachWithIndex
import yy.zpy.cc.greendaolibrary.bean.GreenDaoType
import yy.zpy.cc.greendaolibrary.bean.MemoBean
import yy.zpy.cc.memo.R
import yy.zpy.cc.memo.dialog.SelectFolderDialog
import yy.zpy.cc.memo.interf.IBaseUI
import yy.zpy.cc.memo.interf.IKeyboardShowChangeListener
import yy.zpy.cc.memo.util.Constant
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
    var isFinish = false

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
            inputStatus()
        }

        override fun keyboardHidden() {
            finishStatus()
        }
    }

    fun inputStatus() {
        isFinish = false
        ib_edit_submit_or_preview.imageResource = R.drawable.ic_edit_submit
        ib_image_or_delete.imageResource = R.drawable.ic_image
    }

    fun finishStatus() {
        isFinish = true
        ib_edit_submit_or_preview.imageResource = R.drawable.ic_memo_preview
        ib_image_or_delete.imageResource = R.drawable.ic_memo_delete
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
        ib_edit_submit_or_preview.setOnClickListener {
            val memoContent = et_memo_edit_content.text.trim().toString()
            if (isFinish) {
                if (TextUtils.isEmpty(memoContent)) {
                    toast("内容不能为空")
                    return@setOnClickListener
                }
            } else {
                finishStatus()
                hideKeyboard()
                if (TextUtils.isEmpty(memoContent)) {
                    return@setOnClickListener
                }
                val memoBean = MemoBean()
                memoBean.content = memoContent
                memoBean.createTime = System.currentTimeMillis()
                memoBean.planType = GreenDaoType.TEXT
                memoBean.folderID = 0
                app.memoBeanDao.insert(memoBean)
            }
        }
        ib_image_or_delete.setOnClickListener {
            if (isFinish) {
                alert("确定要删除这条便签吗？", "删除便签") {
                    okButton {
                        toast("删除")
                    }
                    cancelButton {
                        toast("取消")
                    }
                }.show()
                return@setOnClickListener
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Dexter.withActivity(this@MemoEditActivity)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(object : MultiplePermissionsListener {
                            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                            }

                            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {

                            }
                        }).check()
                return@setOnClickListener
            }
            Matisse.from(this@MemoEditActivity)
                    .choose(MimeType.allOf())
                    .countable(true)
                    .maxSelectable(9)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(GlideEngine())
                    .forResult(Constant.REQUEST_CODE_CHOOSE)
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
                    customView {
                        verticalLayout {
                            lparams(matchParent, wrapContent) {
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
                        }
                        okButton {
                            val folderName = etFolderName.text.trim().toString()
                            if (TextUtils.isEmpty(folderName)) {
                                toast("名字不能为空")
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

    private fun hideKeyboard() {
        val v = currentFocus
        if (v != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            val obtainResult = Matisse.obtainResult(data)
            obtainResult.forEach {
                error(it.toString())
            }
        }
    }
}