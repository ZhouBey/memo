package yy.zpy.cc.memo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import org.greenrobot.greendao.database.Database;

import java.util.List;

import yy.zpy.cc.greendaolibrary.bean.DaoMaster;
import yy.zpy.cc.greendaolibrary.bean.DaoSession;
import yy.zpy.cc.greendaolibrary.bean.FolderBeanDao;
import yy.zpy.cc.greendaolibrary.bean.MemoBeanDao;
import yy.zpy.cc.greendaolibrary.helper.GreenDaoOpenHelper;

/**
 * Created by zpy on 2017/9/25.
 */

public class App extends Application {

    private MemoBeanDao memoBeanDao;
    private FolderBeanDao folderBeanDao;
    private static App INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        if (isMainProcess()) {
            GreenDaoOpenHelper greenDaoOpenHelper = new GreenDaoOpenHelper(this, "memo-db");
            Database db = greenDaoOpenHelper.getWritableDb();
            DaoSession daoSession = new DaoMaster(db).newSession();
            memoBeanDao = daoSession.getMemoBeanDao();
            folderBeanDao = daoSession.getFolderBeanDao();
        }
    }

    public MemoBeanDao getMemoBeanDao() {
        return memoBeanDao;
    }

    public FolderBeanDao getFolderBeanDao() {
        return folderBeanDao;
    }

    private boolean isMainProcess() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
    public static App getInstance() {
        return INSTANCE;
    }
}
