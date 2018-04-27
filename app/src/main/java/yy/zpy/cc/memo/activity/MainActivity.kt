package yy.zpy.cc.memo.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.yalantis.ucrop.UCrop
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.activity_main_drawer.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.*
import permissions.dispatcher.*
import yy.zpy.cc.greendaolibrary.bean.*
import yy.zpy.cc.memo.R
import yy.zpy.cc.memo.adapter.FolderAdapter
import yy.zpy.cc.memo.adapter.MemoAdapter
import yy.zpy.cc.memo.custom.MyGlideEngine
import yy.zpy.cc.memo.data.Folder
import yy.zpy.cc.memo.data.Memo
import yy.zpy.cc.memo.dialog.SelectFolderDialog
import yy.zpy.cc.memo.interf.IBaseUI
import yy.zpy.cc.memo.interf.IKeyboardShowChangeListener
import yy.zpy.cc.memo.logcat
import yy.zpy.cc.memo.util.Constant
import yy.zpy.cc.memo.util.Constant.Companion.MIN_KEYBOARD_HEIGHT_PX
import java.io.File
import kotlin.properties.Delegates

@RuntimePermissions
class MainActivity : BaseActivity(), IBaseUI, NavigationView.OnNavigationItemSelectedListener {
    override fun getLayout() = R.layout.activity_main

    companion object {
        const val MEMO_BROWSE_STATUS = 0
        const val MEMO_OPERATE_STATUS = 1
        const val MEMO_SEARCH_STATUS = 3
    }

    private var drawerToggle by Delegates.notNull<ActionBarDrawerToggle>()
    private var memoAdapter by Delegates.notNull<MemoAdapter>()
    var memoData = mutableListOf<Memo>()
    var folderData = mutableListOf<Folder>()
    private var folderAdapter by Delegates.notNull<FolderAdapter>()
    private var memoStatus = MEMO_BROWSE_STATUS
    private var folderName = Constant.ALL_MEMO
    var selectFolderDialog by Delegates.notNull<SelectFolderDialog>()
    var keyboardShowChangeListener = KeyboardShowChangeListener()
    private var decorView: View? = null
    private var globalListener by Delegates.notNull<ViewTreeObserver.OnGlobalLayoutListener>()
    private var linearLayoutManager by Delegates.notNull<LinearLayoutManager>()
    private var dividerItemDecoration by Delegates.notNull<DividerItemDecoration>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar_main)
        initView()
        viewListener()
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return hasBrowseStatus
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_settings -> {
//                toast("设置")
//                return true
//            }
//            R.id.action_about -> {
//                toast("关于")
//                return true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    override fun viewListener() {
        fab.setOnClickListener {
            startActivity<MemoEditActivity>("folderName" to folderName)
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_no)
        }
        rl_drawer_create_folder.setOnClickListener {
            createNewFolder {
                toast("创建成功")
            }
        }
        rl_drawer_wastebasket.setOnClickListener {
            drawer_layout.closeDrawers()
            folderName = resources.getString(R.string.wastebasket)
            tv_select_folder_name.text = folderName
            showMemoList()
            if (View.VISIBLE == fab.visibility && resources.getString(R.string.wastebasket) == folderName) hideFloatingActionButton()
        }
        iv_cancel_memo_operate.setOnClickListener {
            memoBrowseStatus()
        }
        tv_memo_move_or_recover.setOnClickListener {
            if (resources.getString(R.string.wastebasket) == folderName) {
                val memoDataCheck = memoData.filter {
                    it.check
                }
                memoDataCheck.forEach {
                    val memoBean = app.memoBeanDao?.load(it.memoBean.id)
                    memoBean?.folderID?.run {
                        val folderBean = app.folderBeanDao?.load(memoBean.folderID)
                        if (folderBean?.deleteTime != 0L) {
                            folderBean?.deleteTime = 0L
                            app.folderBeanDao?.update(folderBean)
                        }
                    }
                    memoBean?.deleteTime = 0L
                    app.memoBeanDao?.update(memoBean)
                }
                showMemoList()
                showDrawerFolderList()
                memoBrowseStatus()
            } else {
                folderData.clear()
                folderData.addAll(getFolderAllData())
                selectFolderDialog.data = folderData
                selectFolderDialog.show()
            }
        }
        tv_memo_delete.setOnClickListener {
            alert("确定要删除" + tv_select_folder_name.text + "条便签吗？", "删除") {
                okButton {
                    memoData.forEach {
                        if (it.check) {
                            if (resources.getString(R.string.wastebasket) == folderName) {
                                app.memoBeanDao?.deleteByKey(it.memoBean.id)
                            } else {
                                val memoBean = app.memoBeanDao?.queryBuilder()?.where(MemoBeanDao.Properties.Id.eq(it.memoBean.id))?.unique()
                                memoBean?.deleteTime = System.currentTimeMillis()
                                app.memoBeanDao?.update(memoBean)
                            }
                        }
                    }
                    //更新便签列表
                    showMemoList()
                    //更新文件夹列表
                    folderData.clear()
                    folderData.addAll(getFolderAllData())
                    folderAdapter.notifyDataSetChanged()
                    memoBrowseStatus()
                }
                cancelButton {

                }
            }.show()
        }
        iv_cover_image.setOnClickListener {
            val options = listOf("更改相册封面")
            selector(null, options) { _, items ->
                when (items) {
                    0 -> {
                        pickerPictureWithPermissionCheck()
                    }
                }

            }
        }
        rv_memo_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                logcat(newState.toString())
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.visibility = View.VISIBLE
                } else {
                    fab.visibility = View.INVISIBLE
                }
            }
        })
        iv_memo_search.setOnClickListener {
            memoSearchStatus()
        }
        et_search_content.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    memoData.clear()
                    memoData.addAll(getMemoData(folderName, it.toString()))
                    memoAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    override fun initView() {
        initDrawerToggle()
        tv_select_folder_name.text = folderName
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        linearLayoutManager = LinearLayoutManager(this)
        dividerItemDecoration = DividerItemDecoration(this, linearLayoutManager.orientation)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.divider_item_decoration, theme))
        initRecyclerViewDrawFolder()
        initRecyclerViewMemo()
        initSelectFolderDialog()
        memoBrowseStatus()
        decorView = window.decorView
        initGlobalLayoutListener()
        decorView?.viewTreeObserver?.addOnGlobalLayoutListener(globalListener)
        fab.rippleColor = Color.parseColor("#E1E1E1")
        doAsync {
            Thread.sleep(500)
            uiThread {
                if (View.INVISIBLE == fab.visibility && resources.getString(R.string.wastebasket) != folderName) showFloatingActionButton()
            }
        }
    }

    private fun initGlobalLayoutListener() {
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
    }

    private fun initDrawerToggle() {
        drawerToggle = object : ActionBarDrawerToggle(
                this,
                drawer_layout,
                toolbar_main,
                R.string.open,
                R.string.close
        ) {
            override fun onDrawerClosed(view: View) {
                Log.d("MainActivity", "OnDrawerClosed")
                super.onDrawerClosed(view)
                invalidateOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View) {
                Log.d("MainActivity", "OnDrawerOpened")
                super.onDrawerOpened(drawerView)
                invalidateOptionsMenu()
            }
        }
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.isDrawerIndicatorEnabled = true
    }

    private fun initRecyclerViewDrawFolder() {
        folderAdapter = FolderAdapter(folderData, false, { position, _ ->
            drawer_layout.closeDrawers()
            folderName = folderData[position].folderBean.name
            tv_select_folder_name.text = folderName
            showMemoList()
            if (View.INVISIBLE == fab.visibility && resources.getString(R.string.wastebasket) != folderName) showFloatingActionButton()
        }, { position, _ ->
            val folderBean = folderData[position].folderBean
            val options = listOf("重命名", "删除")
            selector("操作", options) { _, items ->
                when (items) {
                    0 -> {
                        alert {
                            var etFolderName by Delegates.notNull<EditText>()
                            customView {
                                verticalLayout {
                                    lparams(matchParent, wrapContent) {
                                        verticalPadding = dip(20)
                                        horizontalPadding = dip(15)
                                    }
                                    textView("重命名") {
                                        textSize = 15f
                                        textColor = R.color.colorFont
                                    }.lparams(wrapContent, wrapContent) {
                                        marginStart = dip(3)
                                    }
                                    etFolderName = editText {
                                        singleLine = true
                                        textSize = 14f
                                        textColor = R.color.colorFont
                                        setText(folderBean.name)
                                        selectAll()
                                    }.lparams(matchParent, wrapContent) {
                                        topMargin = dip(15)
                                    }
                                }
                                okButton {
                                    val folderName = etFolderName.text.trim().toString()
                                    if (TextUtils.isEmpty(folderName)) {
                                        toast("名字不能为空")
                                        return@okButton
                                    }
                                    folderBean.name = folderName
                                    app.folderBeanDao?.update(folderBean)
                                    showDrawerFolderList()
                                }
                                cancelButton {

                                }
                            }
                        }.show()
                    }
                    1 -> {
                        alert("确定要删除吗？", "删除") {
                            okButton {
                                folderBean.deleteTime = System.currentTimeMillis()
                                app.folderBeanDao?.update(folderBean)
                                showDrawerFolderList()
                            }
                            cancelButton {

                            }
                        }.show()
                    }
                }

            }
        })
        rv_drawer_folder.layoutManager = linearLayoutManager
        rv_drawer_folder.addItemDecoration(dividerItemDecoration)
        rv_drawer_folder.adapter = folderAdapter
    }

    private fun initRecyclerViewMemo() {
        memoAdapter = MemoAdapter(memoData,
                { position, _ ->
                    if (MEMO_BROWSE_STATUS == memoStatus || MEMO_SEARCH_STATUS == memoStatus) {
                        if (resources.getString(R.string.wastebasket) == folderName) {
                            alert("恢复这条便签？") {
                                okButton {
                                    val memo = memoData[position]
                                    memo.memoBean.deleteTime = 0
                                    app.memoBeanDao?.update(memo.memoBean)
                                    showMemoList()
                                    showDrawerFolderList()
                                }
                                cancelButton {

                                }
                            }.show()
                        } else {
                            startActivity<MemoEditActivity>(
                                    "memo" to (memoData[position].memoBean)
                            )
                            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_no)
                        }
                    } else {
                        memoAdapterNotifyDataSetChanged(position)
                    }
                },
                { position, _ ->
                    if (MEMO_BROWSE_STATUS == memoStatus || MEMO_SEARCH_STATUS == memoStatus) {
                        memoOperateStatus()
                    }
                    memoAdapterNotifyDataSetChanged(position)
                })
        rv_memo_list.layoutManager = LinearLayoutManager(this@MainActivity)
        rv_memo_list.adapter = memoAdapter
        rv_memo_list.addItemDecoration(dividerItemDecoration)
    }

    private fun initSelectFolderDialog() {
        selectFolderDialog = SelectFolderDialog(this, R.style.WhiteDialog)
        selectFolderDialog.onClickListener = object : SelectFolderDialog.OnClickListener {
            override fun itemClick(position: Int, type: Int) {
                val folderID = selectFolderDialog.data[position].folderBean.id
                val memoDataCheck = memoData.filter {
                    it.check
                }
                memoDataCheck.forEach {
                    val memoBean = app.memoBeanDao?.load(it.memoBean.id)
                    memoBean?.folderID = folderID
                    app.memoBeanDao?.update(memoBean)
                }
                selectFolderDialog.dismiss()
                memoBrowseStatus()
                toast("移动完成")
                //更新侧边栏adapter
                showDrawerFolderList()
                //更新主页便签数据
                showMemoList()
            }

            override fun newFolderClick() {
                selectFolderDialog.dismiss()
                createNewFolder {
                    selectFolderDialog.data = folderData
                    selectFolderDialog.show()
                }
            }

        }
        selectFolderDialog.y = dimen(R.dimen.actionBarHeight)
    }

    fun createNewFolder(afterCreateEvent: () -> Unit) {
        alert {
            var etFolderName by Delegates.notNull<EditText>()
            customView {
                verticalLayout {
                    lparams(matchParent, wrapContent) {
                        verticalPadding = dip(20)
                        horizontalPadding = dip(15)
                    }
                    textView("新建文件夹") {
                        textSize = 15f
                        textColor = R.color.colorFont
                    }.lparams(wrapContent, wrapContent) {
                        marginStart = dip(3)
                    }
                    etFolderName = editText {
                        singleLine = true
                        textSize = 14f
                        textColor = R.color.colorFont
                    }.lparams(matchParent, wrapContent) {
                        topMargin = dip(15)
                    }
                }
                okButton {
                    val folderName = etFolderName.text.trim().toString()
                    if (TextUtils.isEmpty(folderName)) {
                        toast("名字不能为空")
                        return@okButton
                    }
                    val folderBean = FolderBean()
                    folderBean.createTime = System.currentTimeMillis()
                    folderBean.greenDaoType = GreenDaoType.TEXT
                    folderBean.name = folderName
                    folderBean.isLock = false
                    app.folderBeanDao?.insert(folderBean)
                    showDrawerFolderList()
                    afterCreateEvent()
                }
                cancelButton {

                }
            }
        }.show()
    }

    private fun memoAdapterNotifyDataSetChanged(position: Int) {
        memoData[position].check = !memoData[position].check
        memoAdapter.notifyDataSetChanged()
        var count = 0
        memoData.forEach {
            if (it.check) {
                count++
            }
        }
        if (count != 0) {
            tv_select_folder_name.text = count.toString()
        } else {
            memoBrowseStatus()
        }
    }

    fun memoBrowseStatus() {
        memoStatus = MEMO_BROWSE_STATUS
        iv_memo_search.visibility = View.VISIBLE
        ll_memo_operate.visibility = View.GONE
        iv_cancel_memo_operate.visibility = View.GONE
        et_search_content.visibility = View.GONE
        drawerToggle.isDrawerIndicatorEnabled = true
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
        window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        memoAdapter.hasSelect = false
        memoData.forEach {
            it.check = false
        }
        memoAdapter.notifyDataSetChanged()
        tv_select_folder_name.visibility = View.VISIBLE
        tv_select_folder_name.text = folderName
        if (View.INVISIBLE == fab.visibility && resources.getString(R.string.wastebasket) != folderName) showFloatingActionButton()
        hideKeyboard(et_search_content)
    }

    private fun memoSearchStatus() {
        memoStatus = MEMO_SEARCH_STATUS
        drawerToggle.isDrawerIndicatorEnabled = false
        et_search_content.visibility = View.VISIBLE
        iv_memo_search.visibility = View.GONE
        ll_memo_operate.visibility = View.GONE
        tv_select_folder_name.visibility = View.GONE
        iv_cancel_memo_operate.visibility = View.VISIBLE
        showKeyboard(et_search_content)
    }

    private fun memoOperateStatus() {
        memoStatus = MEMO_OPERATE_STATUS
        iv_memo_search.visibility = View.GONE
        ll_memo_operate.visibility = View.VISIBLE
        iv_cancel_memo_operate.visibility = View.VISIBLE
        et_search_content.visibility = View.GONE
        if (resources.getString(R.string.wastebasket) == folderName) {
            tv_memo_move_or_recover.text = "恢复"
        } else {
            tv_memo_move_or_recover.text = "移动"
        }
        drawerToggle.isDrawerIndicatorEnabled = false
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorActionBarSelect)))
        window.statusBarColor = resources.getColor(R.color.colorStatusBarSelect)
        memoAdapter.hasSelect = true
        if (View.VISIBLE == fab.visibility && resources.getString(R.string.wastebasket) == folderName) hideFloatingActionButton()
    }

    private fun showFloatingActionButton() {
        val objectAnimatorY = ObjectAnimator.ofFloat(fab, "scaleY", 0f, 0.3f, 0.5f, 0.7f, 1.0f)
        val objectAnimatorX = ObjectAnimator.ofFloat(fab, "scaleX", 0f, 0.3f, 0.5f, 0.7f, 1.0f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(objectAnimatorX, objectAnimatorY)
        animatorSet.duration = 250
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                fab.visibility = View.VISIBLE
            }

        })
        animatorSet.start()
    }

    private fun hideFloatingActionButton() {
        val objectAnimatorY = ObjectAnimator.ofFloat(fab, "scaleY", 1f, 0.7f, 0.5f, 0.3f, 0f)
        val objectAnimatorX = ObjectAnimator.ofFloat(fab, "scaleX", 1f, 0.7f, 0.5f, 0.3f, 0f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(objectAnimatorX, objectAnimatorY)
        animatorSet.duration = 250
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                fab.visibility = View.INVISIBLE
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {

            }

        })
        animatorSet.start()
    }

    override fun show() {
        showDrawerFolderList()
        showMemoList()
        val coverPath = app.getSpValue(Constant.SP_COVER_PATH, "")
        Glide.with(this@MainActivity).load(coverPath)
                .apply(RequestOptions().error(R.drawable.img_drawer_background)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                ).into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        iv_cover_image.setImageDrawable(resource)
                    }
                })
    }

    fun showDrawerFolderList() {
        folderData.clear()
        folderData.addAll(getFolderAllData())
        folderAdapter.notifyDataSetChanged()
    }

    private fun getFolderAllData(): MutableList<Folder> {
        val folders = app.folderBeanDao?.queryBuilder()?.where(FolderBeanDao.Properties.DeleteTime.eq(0))?.list()
        val data = mutableListOf<Folder>()
        folders?.forEach {
            val folder = Folder()
            val list: MutableList<MemoBean>?
            if (Constant.ALL_MEMO == it.name) {
                list = app.memoBeanDao?.queryBuilder()?.where(MemoBeanDao.Properties.DeleteTime.eq(0))?.list()
            } else {
                list = app.memoBeanDao?.queryBuilder()?.where(MemoBeanDao.Properties.FolderID.eq(it.id))?.where(MemoBeanDao.Properties.DeleteTime.eq(0))?.list()
            }
            val size = list?.size ?: 0
            folder.folderBean.id = it.id
            folder.folderBean.name = it.name
            folder.count = size
            data.add(folder)
        }
        return data
    }

    fun showMemoList() {
        memoData.clear()
        memoData.addAll(getMemoData(folderName))
        memoAdapter.notifyDataSetChanged()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onResume() {
        super.onResume()
        show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean = false
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawers()
            return
        }
        if (MEMO_BROWSE_STATUS != memoStatus) {
            memoBrowseStatus()
            return
        }
        super.onBackPressed()
    }

    private fun getMemoData(folderName: String, keyword: String = ""): MutableList<Memo> {
        logcat(keyword)
        val data = mutableListOf<MemoBean>()
        if (Constant.ALL_MEMO == folderName) {
            val allMemo = if (TextUtils.isEmpty(keyword)) {
                app.memoBeanDao?.queryBuilder()
                        ?.where(MemoBeanDao.Properties.DeleteTime.eq(0))
                        ?.orderDesc(MemoBeanDao.Properties.CreateTime)
                        ?.list()
            } else {
                app.memoBeanDao?.queryBuilder()
                        ?.where(MemoBeanDao.Properties.DeleteTime.eq(0))
                        ?.where(MemoBeanDao.Properties.Content.like(keyword))
                        ?.orderDesc(MemoBeanDao.Properties.CreateTime)
                        ?.list()
            }
            allMemo?.let { data.addAll(allMemo) }
        } else if (resources.getString(R.string.wastebasket) == folderName) {
            val deleteMemo = if (TextUtils.isEmpty(keyword)) {
                app.memoBeanDao?.queryBuilder()
                        ?.where(MemoBeanDao.Properties.DeleteTime.notEq(0))
                        ?.orderDesc(MemoBeanDao.Properties.DeleteTime)
                        ?.list()
            } else {
                app.memoBeanDao?.queryBuilder()
                        ?.where(MemoBeanDao.Properties.DeleteTime.notEq(0))
                        ?.where(MemoBeanDao.Properties.Content.like(keyword))
                        ?.orderDesc(MemoBeanDao.Properties.DeleteTime)
                        ?.list()
            }
            deleteMemo?.let { data.addAll(deleteMemo) }
        } else {
            val folder = app.folderBeanDao?.queryBuilder()?.where(FolderBeanDao.Properties.Name.eq(folderName))?.list()
            if (folder != null && folder.size != 0) {
                val result = if (TextUtils.isEmpty(keyword)) {
                    app.memoBeanDao?.queryBuilder()?.where(MemoBeanDao.Properties.FolderID.eq(folder[0].id))
                            ?.where(MemoBeanDao.Properties.DeleteTime.eq(0))
                            ?.orderDesc(MemoBeanDao.Properties.CreateTime)
                            ?.list()
                } else {
                    app.memoBeanDao?.queryBuilder()?.where(MemoBeanDao.Properties.FolderID.eq(folder[0].id))
                            ?.where(MemoBeanDao.Properties.DeleteTime.eq(0))
                            ?.where(MemoBeanDao.Properties.Content.like("%$keyword%"))
                            ?.orderDesc(MemoBeanDao.Properties.CreateTime)
                            ?.list()
                }
                result?.let { data.addAll(result) }
            }
        }
        val result = mutableListOf<Memo>()
        data.forEach {
            logcat(it.content)
            val memo = Memo()
            memo.memoBean = it
            result.add(memo)
        }
        return result
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    fun pickerPicture() {
        Matisse.from(this@MainActivity)
                .choose(MimeType.allOf())
                .theme(R.style.Memo_Zhihu)
                .countable(true)
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(MyGlideEngine())
                .capture(true)
                .captureStrategy(CaptureStrategy(true, "yy.zpy.cc.memo.file.provider"))
                .forResult(Constant.REQUEST_CODE_CHOOSE)
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
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

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    fun onExternalStorageDenied() {
        toast("选择图片需要您的相册权限")
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    fun onExternalStorageNeverAskAgain() {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == Constant.REQUEST_CODE_CHOOSE) {
            val obtainResult = Matisse.obtainResult(data)
            val uri = obtainResult[0]
            val RESULT_CROP_IMAGE_PATH = Environment.getExternalStorageDirectory().toString() + "/" + Constant.MEMO_PICTURES
            val folderFile = File(RESULT_CROP_IMAGE_PATH)
            if (!folderFile.exists()) {
                folderFile.mkdirs()
            }
            val file = File(folderFile, System.currentTimeMillis().toString() + ".png")
            UCrop.of(uri, Uri.parse("file://" + file.path))
                    .withOptions(UCrop.Options().apply {
                        setToolbarColor(resources.getColor(R.color.colorPrimary))
                        setStatusBarColor(resources.getColor(R.color.colorPrimaryDark))
                        setActiveWidgetColor(resources.getColor(R.color.colorPrimary))
                    })
                    .useSourceImageAspectRatio()
                    .start(this@MainActivity)
            return
        }
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == UCrop.RESULT_ERROR) {
                logcat("出错了=" + data?.getSerializableExtra(UCrop.EXTRA_ERROR))
                return
            }
            if (data != null) {
                val resultUri = UCrop.getOutput(data)
                resultUri?.path?.run { app.putSpValue(Constant.SP_COVER_PATH, resultUri.path) }
                Glide.with(this@MainActivity).load(resultUri)
                        .apply(RequestOptions().error(R.drawable.img_drawer_background)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                        ).into(object : SimpleTarget<Drawable>() {
                            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                iv_cover_image.setImageDrawable(resource)
                            }
                        })
            } else {
                logcat("data is null")
            }
        }
    }

    inner class KeyboardShowChangeListener : IKeyboardShowChangeListener {
        override fun keyboardShow() {
            memoSearchStatus()
        }

        override fun keyboardHidden() {
            memoBrowseStatus()
        }
    }
}

fun showKeyboard(view: View) {
    view.requestFocus()
    val systemService = view.context.getSystemService(Context.INPUT_METHOD_SERVICE)
    systemService?.let {
        val inputManager = it as InputMethodManager
        inputManager.showSoftInput(view, 0)
    }
}

fun hideKeyboard(view: View) {
    val systemService = view.context.getSystemService(Context.INPUT_METHOD_SERVICE)
    systemService?.let {
        val imm = it as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
