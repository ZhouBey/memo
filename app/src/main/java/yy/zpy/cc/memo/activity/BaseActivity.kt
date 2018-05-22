package yy.zpy.cc.memo.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import yy.zpy.cc.memo.App
import kotlin.properties.Delegates


@SuppressLint("Registered")

/**
 * Created by zpy on 2017/9/18.
 */
abstract class BaseActivity : AppCompatActivity() {
    @LayoutRes
    abstract fun getLayout(): Int

    var app: App by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
        app = App.instance
    }
}

//fun getStatusBarHeight(context: Context): Int {
//    val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
//    return if (resourceId > 0) {
//        context.resources.getDimensionPixelSize(resourceId)
//    } else {
//        0
//    }
//}