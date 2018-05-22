package yy.zpy.cc.memo

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.StrictMode
import android.text.TextUtils
import android.util.Log
import com.tencent.bugly.crashreport.CrashReport
import yy.zpy.cc.greendaolibrary.bean.DaoMaster
import yy.zpy.cc.greendaolibrary.bean.FolderBeanDao
import yy.zpy.cc.greendaolibrary.bean.ImageBeanDao
import yy.zpy.cc.greendaolibrary.bean.MemoBeanDao
import yy.zpy.cc.greendaolibrary.helper.GreenDaoOpenHelper
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
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
    private var imageBeanDao: ImageBeanDao? = null

    private var sp: SharedPreferences by Delegates.notNull()

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
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        initBugly()
    }

    private val isMainProcess: Boolean
        get() {
            val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val processInfo = am.runningAppProcesses
            val mainProcessName = packageName
            val myPid = android.os.Process.myPid()
            return processInfo.any { it.pid == myPid && mainProcessName == it.processName }
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
        var instance: App by Delegates.notNull()
            private set
    }

    private fun initBugly() {
        val context = applicationContext
        // 获取当前包名
        val packageName = context.packageName
        // 获取当前进程名
        val processName = getProcessName(android.os.Process.myPid())
        // 设置是否为上报进程
        val strategy = CrashReport.UserStrategy(context)
        strategy.isUploadProcess = processName == null || processName == packageName
        try {
            strategy.appVersion = packageManager.getPackageInfo(packageName, 0).versionName
        } catch (ignored: Exception) {
        }

        strategy.appPackageName = packageName  //App的包名
        strategy.setCrashHandleCallback(object : CrashReport.CrashHandleCallback() {
            override fun onCrashHandleStart(crashType: Int, errorType: String?,
                                            errorMessage: String?, errorStack: String?): Map<String, String> {
                val map = LinkedHashMap<String, String>()
                map["Key"] = "Value"
                return map
            }

            override fun onCrashHandleStart2GetExtraDatas(crashType: Int, errorType: String?,
                                                          errorMessage: String?, errorStack: String?): ByteArray? {
                return try {
                    "Extra data.".toByteArray(charset("UTF-8"))
                } catch (e: Exception) {
                    null
                }

            }
        })
        CrashReport.initCrashReport(applicationContext, BuildConfig.BUGLY_APP_ID, BuildConfig.DEBUG, strategy)
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

fun getScreenWidth(context: Context) = context.resources.displayMetrics.widthPixels
fun getProcessName(pid: Int): String? {
    var reader: BufferedReader? = null
    try {
        reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
        var processName = reader.readLine()
        if (!TextUtils.isEmpty(processName)) {
            processName = processName.trim { it <= ' ' }
        }
        return processName
    } catch (throwable: Throwable) {
        throwable.printStackTrace()
    } finally {
        try {
            if (reader != null) {
                reader.close()
            }
        } catch (exception: IOException) {
            exception.printStackTrace()
        }

    }
    return null
}
