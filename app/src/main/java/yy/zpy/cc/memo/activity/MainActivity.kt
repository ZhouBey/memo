package yy.zpy.cc.memo.activity

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.activity_main_drawer.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import yy.zpy.cc.greendaolibrary.bean.FolderBeanDao
import yy.zpy.cc.greendaolibrary.bean.MemoBean
import yy.zpy.cc.greendaolibrary.bean.MemoBeanDao
import yy.zpy.cc.memo.App
import yy.zpy.cc.memo.R
import yy.zpy.cc.memo.adapter.FolderAdapter
import yy.zpy.cc.memo.adapter.MemoAdapter
import yy.zpy.cc.memo.data.Folder
import yy.zpy.cc.memo.interf.IBaseUI
import yy.zpy.cc.memo.util.Constant
import kotlin.properties.Delegates


class MainActivity : BaseActivity(), IBaseUI, NavigationView.OnNavigationItemSelectedListener {
    override fun getLayout() = R.layout.activity_main
    var drawerToggle by Delegates.notNull<ActionBarDrawerToggle>()
    var memoAdapter by Delegates.notNull<MemoAdapter>()
    var memoData = mutableListOf<MemoBean>()
    var folderData = mutableListOf<Folder>()
    var folderAdapter by Delegates.notNull<FolderAdapter>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar_main)
        initView()
        viewListener()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                toast("设置")
                return true
            }
            R.id.action_about -> {
                toast("关于")
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun viewListener() {
//        fab.rippleColor = Color.parseColor("#ff00ff")
        fab.setOnClickListener {
            startActivity<MemoEditActivity>()
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_no)
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        tv_select_folder_name.text = Constant.ALL_MEMO
        val linearLayoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, linearLayoutManager.orientation)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.divider_item_decoration, theme))
        rv_drawer_folder.layoutManager = linearLayoutManager
        folderAdapter = FolderAdapter(folderData, false) { position, _ ->
            drawer_layout.closeDrawers()
            val name = folderData[position].name
            tv_select_folder_name.text = name
            showMemoList(name)
        }
        rv_drawer_folder.addItemDecoration(dividerItemDecoration)
        rv_drawer_folder.adapter = folderAdapter
        memoAdapter = MemoAdapter(memoData) { position, type ->
            startActivity<MemoEditActivity>(
                    "memo" to memoData[position]
            )
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_no)
        }
        rv_memo_list.layoutManager = LinearLayoutManager(this@MainActivity)
        rv_memo_list.adapter = memoAdapter
        rv_memo_list.addItemDecoration(dividerItemDecoration)
    }

    override fun show() {
        showDrawerFolderList()
        showMemoList(tv_select_folder_name.text.toString())
    }

    fun showDrawerFolderList() {
        val folders = App.instance.folderBeanDao?.loadAll()
        val data = mutableListOf<Folder>()
        folders?.forEach {
            val folder = Folder()
            val list: MutableList<MemoBean>?
            if (Constant.ALL_MEMO == it.name) {
                list = app.memoBeanDao?.loadAll()
            } else {
                list = app.memoBeanDao?.queryBuilder()?.where(MemoBeanDao.Properties.FolderID.eq(it.id))?.list()
            }
            val size = list?.size ?: 0
            folder.name = it.name
            folder.count = size
            data.add(folder)
        }
        folderData.clear()
        folderData.addAll(data)
        folderAdapter.notifyDataSetChanged()
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
        } else {
            super.onBackPressed()
        }
    }

    fun getMemoData(folderName: String): MutableList<MemoBean> {
        val data = mutableListOf<MemoBean>()
        if (Constant.ALL_MEMO == folderName) {
            val allMemo = app.memoBeanDao?.queryBuilder()?.orderDesc(MemoBeanDao.Properties.CreateTime)?.list()
            if (allMemo != null) {
                data.addAll(allMemo)
            }
        } else {
            val folder = app.folderBeanDao?.queryBuilder()?.where(FolderBeanDao.Properties.Name.eq(folderName))?.list()
            if (folder != null) {
                val result = app.memoBeanDao?.queryBuilder()?.where(MemoBeanDao.Properties.FolderID.eq(folder[0].id))?.list()
                if (result != null) {
                    data.addAll(result)
                }
            }
        }
        return data
    }
}
