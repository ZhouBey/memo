package yy.zpy.cc.memo.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.activity_main_drawer.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.*
import yy.zpy.cc.greendaolibrary.bean.*
import yy.zpy.cc.memo.R
import yy.zpy.cc.memo.adapter.FolderAdapter
import yy.zpy.cc.memo.adapter.MemoAdapter
import yy.zpy.cc.memo.data.Folder
import yy.zpy.cc.memo.data.Memo
import yy.zpy.cc.memo.dialog.SelectFolderDialog
import yy.zpy.cc.memo.interf.IBaseUI
import yy.zpy.cc.memo.logcat
import yy.zpy.cc.memo.util.Constant
import kotlin.properties.Delegates


class MainActivity : BaseActivity(), IBaseUI, NavigationView.OnNavigationItemSelectedListener {
    override fun getLayout() = R.layout.activity_main
    var drawerToggle by Delegates.notNull<ActionBarDrawerToggle>()
    var memoAdapter by Delegates.notNull<MemoAdapter>()
    var memoData = mutableListOf<Memo>()
    var folderData = mutableListOf<Folder>()
    var folderAdapter by Delegates.notNull<FolderAdapter>()
    var hasBrowseStatus = true
    var folderName = Constant.ALL_MEMO
    var selectFolderDialog by Delegates.notNull<SelectFolderDialog>()


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
            showMemoList(folderName)
            if (View.VISIBLE == fab.visibility && resources.getString(R.string.wastebasket) == folderName) hideFloatingActionButton()
        }
        iv_cancel_memo_operate.setOnClickListener {
            memoBrowseStatus()
        }
        tv_memo_move.setOnClickListener {
            folderData.clear()
            folderData.addAll(getFolderAllData())
            selectFolderDialog.data = folderData
            selectFolderDialog.show()
        }
        tv_memo_delete.setOnClickListener {
            alert("确定要删除" + tv_select_folder_name.text + "条便签吗？", "删除") {
                okButton {
                    try {
                        memoData.forEach {
                            if (it.check) {
                                val memoBean = app.memoBeanDao?.queryBuilder()?.where(MemoBeanDao.Properties.Id.eq(it.memoBean.id))?.unique()
                                memoBean?.deleteTime = System.currentTimeMillis()
                                app.memoBeanDao?.update(memoBean)
                            }
                        }
                        //更新便签列表
                        showMemoList(folderName)
                        //更新文件夹列表
                        folderData.clear()
                        folderData.addAll(getFolderAllData())
                        folderAdapter.notifyDataSetChanged()
                        memoBrowseStatus()
                    } catch (e: Exception) {
                        logcat(e.toString())
                    }
                }
                cancelButton {

                }
            }.show()
        }
    }

    override fun initView() {
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
        drawer_layout.setDrawerListener(drawerToggle)
        drawerToggle.isDrawerIndicatorEnabled = true
        tv_select_folder_name.text = folderName
        val linearLayoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, linearLayoutManager.orientation)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.divider_item_decoration, theme))
        rv_drawer_folder.layoutManager = linearLayoutManager
        folderAdapter = FolderAdapter(folderData, false, { position, type ->
            drawer_layout.closeDrawers()
            folderName = folderData[position].folderBean.name
            tv_select_folder_name.text = folderName
            showMemoList(folderName)
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
        rv_drawer_folder.addItemDecoration(dividerItemDecoration)
        rv_drawer_folder.adapter = folderAdapter
        memoAdapter = MemoAdapter(memoData,
                { position, _ ->
                    if (hasBrowseStatus) {
                        startActivity<MemoEditActivity>(
                                "memo" to (memoData[position].memoBean)
                        )
                        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_no)
                    } else {
                        memoAdapterNotifyDataSetChanged(position)
                    }
                },
                { position, _ ->
                    if (hasBrowseStatus) {
                        memoOperateStatus()
                    }
                    memoAdapterNotifyDataSetChanged(position)
                })
        rv_memo_list.layoutManager = LinearLayoutManager(this@MainActivity)
        rv_memo_list.adapter = memoAdapter
        rv_memo_list.addItemDecoration(dividerItemDecoration)
        initSelectFolderDialog()


        fab.rippleColor = Color.parseColor("#E1E1E1")
        doAsync {
            Thread.sleep(500)
            uiThread {
                if (View.INVISIBLE == fab.visibility && resources.getString(R.string.wastebasket) != folderName) showFloatingActionButton()
            }
        }
    }

    fun initSelectFolderDialog() {
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
                showMemoList(tv_select_folder_name.text.toString())
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

    fun memoAdapterNotifyDataSetChanged(position: Int) {
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
        hasBrowseStatus = true
        invalidateOptionsMenu()
        iv_memo_search.visibility = View.VISIBLE
        ll_memo_operate.visibility = View.GONE
        iv_cancel_memo_operate.visibility = View.GONE
        drawerToggle.isDrawerIndicatorEnabled = true
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
        window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        memoAdapter.hasSelect = false
        memoData.forEach {
            it.check = false
        }
        memoAdapter.notifyDataSetChanged()
        tv_select_folder_name.text = folderName
        if (View.INVISIBLE == fab.visibility && resources.getString(R.string.wastebasket) != folderName) showFloatingActionButton()
    }

    fun memoOperateStatus() {
        hasBrowseStatus = false
        invalidateOptionsMenu()
        iv_memo_search.visibility = View.GONE
        ll_memo_operate.visibility = View.VISIBLE
        iv_cancel_memo_operate.visibility = View.VISIBLE
        drawerToggle.isDrawerIndicatorEnabled = false
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorActionBarSelect)))
        window.statusBarColor = resources.getColor(R.color.colorStatusBarSelect)
        memoAdapter.hasSelect = true
        if (View.VISIBLE == fab.visibility && resources.getString(R.string.wastebasket) == folderName) hideFloatingActionButton()
    }

    fun showFloatingActionButton() {
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

    fun hideFloatingActionButton() {
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
        showMemoList(tv_select_folder_name.text.toString())
    }

    fun showDrawerFolderList() {
        folderData.clear()
        folderData.addAll(getFolderAllData())
        folderAdapter.notifyDataSetChanged()
    }

    fun getFolderAllData(): MutableList<Folder> {
        var loadAll = app.folderBeanDao?.loadAll()
        loadAll?.forEach {
            logcat(it.toString())
        }
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

    fun showMemoList(folderName: String) {
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
        if (!hasBrowseStatus) {
            memoBrowseStatus()
            return
        }
        super.onBackPressed()
    }

    fun getMemoData(folderName: String): MutableList<Memo> {
        val data = mutableListOf<MemoBean>()
        if (Constant.ALL_MEMO == folderName) {
            val allMemo = app.memoBeanDao?.queryBuilder()
                    ?.where(MemoBeanDao.Properties.DeleteTime.eq(0))
                    ?.orderDesc(MemoBeanDao.Properties.CreateTime)
                    ?.list()
            allMemo?.let { data.addAll(allMemo) }
        } else if (resources.getString(R.string.wastebasket) == folderName) {
            val deleteMemo = app.memoBeanDao?.queryBuilder()
                    ?.where(MemoBeanDao.Properties.DeleteTime.notEq(0))
                    ?.list()
            deleteMemo?.let { data.addAll(deleteMemo) }
        } else {
            val folder = app.folderBeanDao?.queryBuilder()?.where(FolderBeanDao.Properties.Name.eq(folderName))?.list()
            if (folder != null && folder.size != 0) {
                val result = app.memoBeanDao?.queryBuilder()?.where(MemoBeanDao.Properties.FolderID.eq(folder[0].id))
                        ?.where(MemoBeanDao.Properties.DeleteTime.eq(0))
                        ?.orderDesc(MemoBeanDao.Properties.CreateTime)
                        ?.list()
                result?.let { data.addAll(result) }
            }
        }
        val result = mutableListOf<Memo>()
        data.forEach {
            val memo = Memo()
            memo.memoBean = it
            logcat(it.deleteTime)
            result.add(memo)
        }
        return result
    }
}
