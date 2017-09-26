package yy.zpy.cc.greendaolibrary.helper;

import android.content.Context;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;

/**
 * Created by zpy on 2017/9/26.
 */

public class GreenDaoOpenHelper extends DatabaseOpenHelper {
    public GreenDaoOpenHelper(Context context, String name) {
        super(context, name, 1);
    }

    @Override
    public void onCreate(Database db) {
//        createAllTables(db, true);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {

    }
}
