package yy.zpy.cc.greendaolibrary.helper;

import android.content.Context;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;

import yy.zpy.cc.greendaolibrary.bean.DaoMaster;
import yy.zpy.cc.greendaolibrary.bean.FolderBean;
import yy.zpy.cc.greendaolibrary.bean.GreenDaoType;

import static yy.zpy.cc.greendaolibrary.bean.DaoMaster.createAllTables;

/**
 * Created by zpy on 2017/9/26.
 */

public class GreenDaoOpenHelper extends DatabaseOpenHelper {
    public GreenDaoOpenHelper(Context context, String name) {
        super(context, name, 1);
    }

    @Override
    public void onCreate(Database db) {
        createAllTables(db, true);
        FolderBean folderBean = new FolderBean();
        folderBean.setCreateTime(System.currentTimeMillis());
        folderBean.setGreenDaoType(GreenDaoType.TEXT);
        folderBean.setName("全部便签");
        folderBean.setIsLock(false);
        new DaoMaster(db).newSession().getFolderBeanDao().insert(folderBean);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {

    }
}
