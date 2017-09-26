package yy.zpy.cc.memo.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.AnkoLogger
import yy.zpy.cc.memo.App
import kotlin.properties.Delegates


@SuppressLint("Registered")

/**
 * Created by zpy on 2017/9/18.
 */
abstract class BaseActivity : AppCompatActivity(), AnkoLogger {
    @LayoutRes
    abstract fun getLayout(): Int

    var app: App by Delegates.notNull<App>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
        app = App.getInstance()
    }
}