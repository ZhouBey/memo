package yy.zpy.cc.memo

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import yy.zpy.cc.greendaolibrary.bean.DaoMaster
import yy.zpy.cc.greendaolibrary.bean.FolderBeanDao
import yy.zpy.cc.greendaolibrary.bean.ImageBeanDao
import yy.zpy.cc.greendaolibrary.bean.MemoBeanDao
import yy.zpy.cc.greendaolibrary.helper.GreenDaoOpenHelper
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by zpy on 2017/9/25.
 */

class App : Application() {

    var memoBeanDao: MemoBeanDao? = null
        private set
    var folderBeanDao: FolderBeanDao? = null
        private set
    var imageBeanDao: ImageBeanDao? = null
        private set

    var sp: SharedPreferences by Delegates.notNull<SharedPreferences>()

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (isMainProcess) {
            val greenDaoOpenHelper = GreenDaoOpenHelper(this, "memo-db")
            val db = greenDaoOpenHelper.writableDb
            val daoSession = DaoMaster(db).newSession()
            memoBeanDao = daoSession.memoBeanDao
            folderBeanDao = daoSession.folderBeanDao
            imageBeanDao = daoSession.imageBeanDao
        }
        sp = getSharedPreferences("memo-data", MODE_PRIVATE)
    }

    private val isMainProcess: Boolean
        get() {
            val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val processInfos = am.runningAppProcesses
            val mainProcessName = packageName
            val myPid = android.os.Process.myPid()
            return processInfos.any { it.pid == myPid && mainProcessName == it.processName }
        }

    @SuppressLint("CommitPrefEdits")
    fun putSpValue(name: String, value: Any) = with(sp.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("exception")
        }.apply()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getSpValue(name: String, default: T): T = with(sp) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("exception")
        }
        res as T
    }

    companion object {
        var instance: App by Delegates.notNull<App>()
            private set
    }
}

fun logcat(log: Any?) {
    Log.e(generateTag(), log.toString())
}

fun generateTag(): String {
    val caller = Throwable().stackTrace[2]
    var tag = "%s.%s(L:%d)"
    var callerClazzName = caller.className
    callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1)
    tag = String.format(tag, callerClazzName, caller.methodName, caller.lineNumber)
    return tag
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun <T : RecyclerView.ViewHolder> T.itemClickListen(event: (position: Int, type: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(adapterPosition, itemViewType)
    }
    return this
}
