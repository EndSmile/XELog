package com.ldy.xelog.common.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ldy.xelog.common.XELogCommon;

/**
 * Created by ldy on 2017/4/10.
 */

public class DBCreator extends SQLiteOpenHelper{
    private static final int DB_VERSION = 1;

    private DBCreator() {
        super(XELogCommon.context, XELogCommon.getDBDirPath(), null, DB_VERSION);
    }

    private static class Instance{
        private static final DBCreator dbCreator = new DBCreator();
    }

    public static DBCreator instance(){
        return Instance.dbCreator;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String createSql:LogDao.getCreateSql()){
            db.execSQL(createSql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
