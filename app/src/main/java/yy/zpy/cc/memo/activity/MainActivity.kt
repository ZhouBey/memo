package yy.zpy.cc.memo.activity

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import yy.zpy.cc.memo.R
import yy.zpy.cc.memo.interf.IBaseUI


class MainActivity : BaseActivity(), IBaseUI {
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
        fab.rippleColor = Color.parseColor("#ff00ff")
        fab.setOnClickListener {
            startActivity<MemoEditActivity>()
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_no);
        }
    }

    override fun initView() {

    }

    override fun show() {

    }
}
