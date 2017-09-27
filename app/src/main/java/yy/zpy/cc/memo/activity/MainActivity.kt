package yy.zpy.cc.memo.activity

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.activity_main_drawer.*
import org.jetbrains.anko.error
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import yy.zpy.cc.memo.App
import yy.zpy.cc.memo.R
import yy.zpy.cc.memo.interf.IBaseUI
import kotlin.properties.Delegates


class MainActivity : BaseActivity(), IBaseUI, NavigationView.OnNavigationItemSelectedListener {
    var drawerToggle by Delegates.notNull<ActionBarDrawerToggle>()
    override fun getLayout() = R.layout.activity_main
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar_main)
        initView()
        viewListener()
        show()
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
        fab.rippleColor = Color.parseColor("#ff00ff")
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
        val list_header = layoutInflater.inflate(R.layout.drawer_header, null)
        list_drawer.addHeaderView(list_header)
        val navigationDrawerAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_expandable_list_item_1, leftSliderData)
        list_drawer.adapter = navigationDrawerAdapter
    }

    override fun show() {
        val folders = App.getInstance().folderBeanDao.loadAll()
        folders.forEach {
            error(it.name)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean = false
    private val leftSliderData = arrayOf("Home", "Android", "Sitemap", "About", "Contact Me")
}
