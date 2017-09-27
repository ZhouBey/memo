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
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import yy.zpy.cc.greendaolibrary.bean.FolderBeanDao
import yy.zpy.cc.greendaolibrary.bean.MemoBeanDao
import yy.zpy.cc.memo.App
import yy.zpy.cc.memo.R
import yy.zpy.cc.memo.adapter.FolderAdapter
import yy.zpy.cc.memo.data.Folder
import yy.zpy.cc.memo.interf.IBaseUI
import yy.zpy.cc.memo.util.Constant
import kotlin.properties.Delegates


class MainActivity : BaseActivity(), IBaseUI, NavigationView.OnNavigationItemSelectedListener {
    var drawerToggle by Delegates.notNull<ActionBarDrawerToggle>()
    override fun getLayout() = R.layout.activity_main
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
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_no);
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
//        val list_header = layoutInflater.inflate(R.layout.drawer_header, null)
//        rv_drawer_folder.addHeaderView(list_header)
    }

    override fun show() {
        var selectFolderName = "全部标签"
        val selectFolderIndex = app.getSpValue(Constant.SP_SELECT_FOLDER, -1.toLong())
        if (selectFolderIndex != -1.toLong()) {
            val list = app.folderBeanDao?.queryBuilder()?.where(FolderBeanDao.Properties.Id.eq(selectFolderIndex))?.list()
            if (list != null && list.size != 0) {
                selectFolderName = list[0].name
            }
        }
        tv_select_folder_name.text = selectFolderName
        val folders = App.instance.folderBeanDao?.loadAll()
        val folderData = mutableListOf<Folder>()
        folders?.forEach {
            val folder = Folder()
            var list = app.memoBeanDao?.queryBuilder()?.where(MemoBeanDao.Properties.FolderID.eq(it.id))?.list()
            val size = if (list == null) 0 else list.size
            folder.name = it.name
            folder.count = size
            folderData.add(folder)
        }
        val linearLayoutManager = LinearLayoutManager(this)
        rv_drawer_folder.layoutManager = linearLayoutManager
        rv_drawer_folder.adapter = FolderAdapter(folderData, false) { position, _ ->
            drawer_layout.closeDrawers()
            tv_select_folder_name.text = folderData[position].name
        }
        val dividerItemDecoration = DividerItemDecoration(this, linearLayoutManager.orientation)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.divider_item_decoration, theme))
        rv_drawer_folder.addItemDecoration(dividerItemDecoration)
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
}
