package yy.zpy.cc.memo.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.Spanned
import android.text.TextUtils
import android.text.TextWatcher
import android.text.format.DateFormat
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.yalantis.ucrop.UCrop
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import kotlinx.android.synthetic.main.activity_edit_memo.*
import kotlinx.android.synthetic.main.dialog_select_dialog.*
import org.jetbrains.anko.*
import permissions.dispatcher.*
import yy.zpy.cc.greendaolibrary.bean.FolderBean
import yy.zpy.cc.greendaolibrary.bean.GreenDaoType
import yy.zpy.cc.greendaolibrary.bean.MemoBean
import yy.zpy.cc.memo.R
import yy.zpy.cc.memo.custom.MyGlideEngine
import yy.zpy.cc.memo.data.Folder
import yy.zpy.cc.memo.dialog.SelectFolderDialog
import yy.zpy.cc.memo.interf.IBaseUI
import yy.zpy.cc.memo.interf.IKeyboardShowChangeListener
import yy.zpy.cc.memo.logcat
import yy.zpy.cc.memo.util.Constant
import java.io.File
import java.util.*
import kotlin.properties.Delegates


/**
 * Created by zpy on 2017/9/11.
 */
@RuntimePermissions
class MemoEditActivity : BaseActivity(), IBaseUI {
    override fun getLayout() = R.layout.activity_edit_memo
    var lockStatus = false
    var keyboardShowChangeListener = KeyboardShowChangeListener()
    var globalListener by Delegates.notNull<ViewTreeObserver.OnGlobalLayoutListener>()
    private val MIN_KEYBOARD_HEIGHT_PX = 150
    var decorView: View? = null
    var selectFolderDialog by Delegates.notNull<SelectFolderDialog>()
    var isFinish = false
    var selectFolderID = 1L
    val folderDataList = mutableListOf<Folder>()
    var memoBean: MemoBean? = null
    var memoBeanID = -1L

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
        ll_select_fold.setOnClickListener {
            selectFolderDialog.show()
        }
        ib_edit_submit_or_preview.setOnClickListener {
            val memoContent = generateText()
            if (isFinish) {
                if (TextUtils.isEmpty(memoContent)) {
                    toast("内容不能为空")
                    return@setOnClickListener
                }
            } else {
                finishStatus()
                hideKeyboard()
                saveMemo(memoContent)
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
            pickerPictureWithPermissionCheck()
        }
    }

    fun saveMemo(content: String) {
        if (TextUtils.isEmpty(content)) {
            return
        }
        if (memoBeanID == -1L) {
            val memoBean = MemoBean()
            memoBean.content = content
            memoBean.greenDaoType = GreenDaoType.TEXT
            memoBean.folderID = selectFolderID
            memoBeanID = app.memoBeanDao?.insert(memoBean) ?: -1L
        } else {
            val memoBean = app.memoBeanDao?.load(memoBeanID)
            memoBean?.content = content
            memoBean?.updateTime = System.currentTimeMillis()
            memoBean?.folderID = selectFolderID
            app.memoBeanDao?.update(memoBean)
        }
    }

    override fun initView() {
        setSupportActionBar(toolbar_edit_memo)
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
                        saveMemo(generateText())
                    }
                }
                lastVisibleDecorViewHeight = visibleDecorViewHeight
            }
        }
        decorView?.viewTreeObserver?.addOnGlobalLayoutListener(globalListener)
        val memo = intent?.extras?.get("memo")
        if (memo != null) {
            memoBean = memo as MemoBean
            memoBeanID = memoBean?.id ?: -1L
        }
        val folderBeanList = app.folderBeanDao?.loadAll()
        folderBeanList?.forEach {
            val folder = Folder()
            folder.name = it.name
            folder.id = it.id
            folderDataList.add(folder)
        }
        if (memoBean == null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            folderDataList[0].check = true
            tv_select_fold.text = folderDataList[0].name
            tv_memo_time.text = String.format("今天 %s", DateFormat.format("HH:mm", Calendar.getInstance()).toString())
            selectFolderID = folderDataList[0].id
        } else {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            finishStatus()
            val folderBean = app.folderBeanDao?.load(memoBean?.folderID)
            tv_select_fold.text = folderBean?.name
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = folderBean?.createTime ?: System.currentTimeMillis()
            val dateDesc = getDateDesc(calendar)
            if (TextUtils.isEmpty(dateDesc)) {
                tv_memo_time.text = DateFormat.format("MM月dd日 HH:mm", calendar).toString()
            } else {
                tv_memo_time.text = String.format("%s%s", dateDesc, DateFormat.format("HH:mm", calendar).toString())
            }
            folderDataList.forEach continuing@ {
                if (it.name == folderBean?.name) {
                    it.check = true
                    return@continuing
                }
            }
        }
        initSelectFolderDialog()

        val editFirst = getEditText()
        editAddTextChangeListener(editFirst)
        if (memoBean != null) {
            editFirst.setText(memoBean?.content)
        }
        ll_root_memo_content.addView(editFirst)
    }

    override fun show() {

    }

    fun initSelectFolderDialog() {
        selectFolderDialog = SelectFolderDialog(this, R.style.WhiteDialog, folderDataList, object : SelectFolderDialog.OnClickListener {
            override fun itemClick(position: Int, type: Int) {
                selectFolderID = folderDataList[position].id ?: 1L
                folderDataList.forEach {
                    it.check = false
                }
                folderDataList[position].check = true
                selectFolderDialog.rv_dialog_folder.adapter.notifyDataSetChanged()
                val name = folderDataList[position].name
                tv_select_fold.text = name
                selectFolderDialog.dismiss()
                saveMemo(generateText())
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
                            folderDataList.forEach {
                                it.check = false
                            }
                            val folder = Folder()
                            folder.name = folderName
                            folder.check = true
                            folderDataList.add(folder)
                            val folderBean = FolderBean()
                            folderBean.createTime = System.currentTimeMillis()
                            folderBean.greenDaoType = GreenDaoType.TEXT
                            folderBean.name = folderName
                            folderBean.isLock = false
                            val folderID = app.folderBeanDao?.insert(folder)
                            selectFolderDialog.rv_dialog_folder.adapter.notifyDataSetChanged()
                            tv_select_fold.text = folderName
                            selectFolderID = folderID ?: 1L
                            saveMemo(generateText())
                        }
                        cancelButton {

                        }
                    }
                }.show()
            }

        })
    }

    fun editAddTextChangeListener(textView: TextView) {
        textView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val content = editable?.toString()
                val split = content?.split("\n")
                val firstLine = split?.get(0)
                if (firstLine != null) {
                    editable.setSpan(ForegroundColorSpan(resources.getColor(R.color.colorFontDark)),
                            0,
                            firstLine.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    editable.setSpan(AbsoluteSizeSpan(dip(18)),
                            0,
                            firstLine.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
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
        if (resultCode == RESULT_OK && requestCode == Constant.REQUEST_CODE_CHOOSE) {
            val obtainResult = Matisse.obtainResult(data)
            val uri = obtainResult[0]
            val RESULT_CROP_IMAGE_PATH = Environment.getExternalStorageDirectory().toString() + "/MemoCropPictures"
            val folderFile = File(RESULT_CROP_IMAGE_PATH)
            if (!folderFile.exists()) {
                folderFile.mkdirs()
            }
            val file = File(folderFile, System.currentTimeMillis().toString() + ".png")
            UCrop.of(uri, Uri.parse(file.path))
                    .withOptions(UCrop.Options().apply {
                        setToolbarColor(resources.getColor(R.color.colorPrimary))
                        setStatusBarColor(resources.getColor(R.color.colorPrimaryDark))
                        setActiveWidgetColor(resources.getColor(R.color.colorPrimary))
                    })
                    .useSourceImageAspectRatio()
                    .start(this@MemoEditActivity)
            return
        }
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == UCrop.RESULT_ERROR) {
                logcat("出错了=" + data?.getSerializableExtra(UCrop.EXTRA_ERROR))
                return
            }
            if (data != null) {
                val resultUri = UCrop.getOutput(data)
                val imageView = getImageView()
                val path = resultUri?.path
                val imageID = path?.substring(path.lastIndexOf("/") + 1, path.length - 4)
                imageView.setTag(R.id.tag_image_view_uri, imageID)
                Glide.with(this@MemoEditActivity).load(resultUri).into(imageView)
                ll_root_memo_content.addView(imageView)
                val editText = getEditText()
                ll_root_memo_content.addView(editText)
                editText.requestFocus()
                val firstChild = ll_root_memo_content.getChildAt(0)
                if (firstChild is EditText && TextUtils.isEmpty(firstChild.text.trim())) ll_root_memo_content.removeViewAt(0)
            } else {
                logcat("data is null")
            }
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun pickerPicture() {
        Matisse.from(this@MemoEditActivity)
                .choose(MimeType.allOf())
                .theme(R.style.Memo_Zhihu)
                .countable(true)
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(MyGlideEngine())
                .forResult(Constant.REQUEST_CODE_CHOOSE)
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showRationaleForPickerPicture(request: PermissionRequest) {
        alert("选择图片需要您的相册权限", "获取权限") {
            positiveButton("获取") {
                request.proceed()
            }
            cancelButton {
                request.cancel()
            }
        }.show()
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onExternalStorageDenied() {
        toast("选择图片需要您的相册权限")
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onCameraNeverAskAgain() {
        alert("您需要去设置里面开启相册权限", "提示") {
            positiveButton("设置") {

            }
            negativeButton("取消") {

            }
        }.show()
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    fun getEditText(): EditText {
        return EditText(this@MemoEditActivity).apply {
            layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
            gravity = Gravity.TOP
            setLineSpacing(0f, 1.1f)
            textColor = R.color.colorFont
            textSize = 15f
            background = null
        }
    }

    fun getTextView(): TextView {
        return TextView(this@MemoEditActivity).apply {
            layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
            setLineSpacing(0f, 1.1f)
            textColor = R.color.colorFont
            textSize = 15f
        }
    }

    fun getImageView(): ImageView {
        return ImageView(this@MemoEditActivity).apply {
            layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
            scaleType = ImageView.ScaleType.FIT_XY
        }
    }

    fun generateText(): String {
        val content = StringBuilder()
        ll_root_memo_content.forEachChild {
            when (it) {
                is EditText -> {
                    content.append(it.text.trim())
                }
                is ImageView -> {
                    content.append("<img id=\"").append(it.getTag(R.id.tag_image_view_uri)).append("\"/>")
                }
            }
        }
        return content.toString()
    }

    override fun finish() {
        if (memoBeanID == -1L) {
            saveMemo(generateText())
        }
        super.finish()
    }
}

fun getDateDesc(calendar: Calendar): String {
    val nowCal = Calendar.getInstance()
    if (nowCal.timeInMillis < calendar.timeInMillis) {
        return ""
    }
    if (nowCal[Calendar.YEAR] != calendar[Calendar.YEAR]) {
        return ""
    }
    val dayDiff = nowCal[Calendar.DAY_OF_YEAR] - calendar[Calendar.DAY_OF_YEAR]
    if (dayDiff == 2) {
        return "前天"
    }
    if (dayDiff == 1) {
        return "昨天"
    }
    if (dayDiff == 0) {
        return "今天"
    }
    return ""
}