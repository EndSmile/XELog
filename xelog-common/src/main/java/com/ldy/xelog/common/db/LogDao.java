package com.ldy.xelog.common.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.SparseArray;

import com.ldy.xelog.common.bean.ChildTabBean;
import com.ldy.xelog.common.bean.FiltrateParamsBean;
import com.ldy.xelog.common.bean.LogBean;
import com.ldy.xelog.common.bean.LogFiltrateBean;
import com.ldy.xelog.common.bean.TagBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ldy on 2017/4/10.
 */
public class LogDao {
    public static final String TABLE_NAME_LOG = "log";
    public static final String CHILD_TAB_TEXT = "text";

    public static final String ID = "id";
    public static final String TIME = "time";
    public static final String PACKAGE_NAME = "package_name";
    public static final String LEVEL = "level";
    public static final String THREAD = "thread";
    public static final String STACK_TRACE = "stack_trace";
    public static final String AUTHOR = "author";
    public static final String REMARKS = "remarks";
    public static final String SUMMARY = "summary";
    public static final String TAG = "tag";
    public static final String TAG_SELECT = "tag_select";
    public static final String CONTENT = "content";
    public static final String EXTRA_1 = "extra1";
    public static final String EXTRA_2 = "extra2";

    public static LogDao instance() {
        return Instance.logDao;
    }

    private LogDao() {
        database = DBCreator.instance().getWritableDatabase();
    }

    private static class Instance {
        private static final LogDao logDao = new LogDao();
    }

    public static List<String> getCreateSql() {
        final List<String> createSqlList = new ArrayList<>();

        String logCreateSql = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME_LOG
                + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TIME + " INTEGER,"
                + PACKAGE_NAME + " INTEGER,"
                + LEVEL + " INTEGER,"
                + THREAD + " INTEGER,"
                + STACK_TRACE + " INTEGER,"
                + AUTHOR + " INTEGER,"
                + REMARKS + " INTEGER,"
                + SUMMARY + " INTEGER,"
                + TAG + " INTEGER,"
                + CONTENT + " TEXT,"
                + EXTRA_1 + " INTEGER,"
                + EXTRA_2 + " INTEGER" +
                ");";
        createSqlList.add(logCreateSql);

        createSqlList.add(getChildCreateSql(PACKAGE_NAME));
        createSqlList.add(getChildCreateSql(LEVEL));
        createSqlList.add(getChildCreateSql(THREAD));
        createSqlList.add(getChildCreateSql(STACK_TRACE));
        createSqlList.add(getChildCreateSql(AUTHOR));
        createSqlList.add(getChildCreateSql(REMARKS));
        createSqlList.add(getChildCreateSql(SUMMARY));
        createSqlList.add(getChildCreateSql(EXTRA_1));
        createSqlList.add(getChildCreateSql(EXTRA_2));

        String tagCreateSql = "CREATE TABLE IF NOT EXISTS "
                + TAG
                + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CHILD_TAB_TEXT + " TEXT UNIQUE,"
                + TAG_SELECT + " INTEGER"
                + ");";
        createSqlList.add(tagCreateSql);
        return createSqlList;
    }

    private static String getChildCreateSql(String childTabName) {
        return "CREATE TABLE IF NOT EXISTS "
                + childTabName
                + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CHILD_TAB_TEXT + " TEXT UNIQUE" +
                ");";
    }

    final SQLiteDatabase database;


    // ===================== start 添加数据部分 =========================

    private Map<String, Map<String, Integer>> childTabMap = new HashMap<>();

    public void addData(LogBean logBean) {
        try {
            database.beginTransaction();
            ContentValues contentValues = new ContentValues();

            contentValues.put(TIME, logBean.getTime());
            contentValues.put(CONTENT, logBean.getContent());
            if (logBean.getTag() != null) {
                contentValues.put(TAG, getOrPutChildItemId(TAG, logBean.getTag(), logBean.isTagSelect()));
            } else {
                contentValues.put(TAG, getOrPutChildItemId(TAG, "", logBean.isTagSelect()));
            }

            putChildItem(contentValues, PACKAGE_NAME, logBean.getPackageName());
            putChildItem(contentValues, LEVEL, logBean.getLevel());
            putChildItem(contentValues, THREAD, logBean.getThread());
            putChildItem(contentValues, STACK_TRACE, logBean.getStackTrace());
            putChildItem(contentValues, AUTHOR, logBean.getAuthor());
            putChildItem(contentValues, REMARKS, logBean.getRemarks());
            putChildItem(contentValues, SUMMARY, logBean.getSummary());
            putChildItem(contentValues, EXTRA_1, logBean.getExtra1());
            putChildItem(contentValues, EXTRA_2, logBean.getExtra2());

            database.insert(TABLE_NAME_LOG, null, contentValues);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }

    }

    private void putChildItem(ContentValues values, String childTabName, String childItem) {
        if (childItem == null) {
            childItem = "";
        }
        values.put(childTabName, getOrPutChildItemId(childTabName, childItem));
    }

    private int getOrPutChildItemId(String childTabName, String childItem) {
        return getOrPutChildItemId(childTabName, childItem, null);
    }

    /**
     * 获取内容对应的id，如果不存在则将其插入数据库中并返回
     *
     * @param childTabName 子表的名字
     * @param childItem    子表对应项的内容
     * @param tagSelect    tag表的{@link #TAG_SELECT}字段，为null时忽略
     * @return 子表对应项的主键
     */
    private int getOrPutChildItemId(String childTabName, String childItem, Boolean tagSelect) {
        Map<String, Integer> childMap = childTabMap.get(childTabName);
        if (childMap == null) {
            childMap = new HashMap<>();
            Cursor cursor = database.rawQuery("select * from " + childTabName, null);
            updateMap(childMap, cursor);
            childTabMap.put(childTabName, childMap);
            cursor.close();
        }
        if (childMap.containsKey(childItem)) {
            return childMap.get(childItem);
        } else {
            ContentValues values = new ContentValues();
            values.put(CHILD_TAB_TEXT, childItem);
            if (tagSelect != null) {
                values.put(TAG_SELECT, tagSelect ? 1 : 0);
            }
            database.insert(childTabName, null, values);
            Cursor cursor = database.rawQuery("select * from " + childTabName + " where " + CHILD_TAB_TEXT + " = '" + childItem + "'", null);
            updateMap(childMap, cursor);
            cursor.close();
            return childMap.get(childItem);
        }
    }

    private void updateMap(Map<String, Integer> childMap, Cursor cursor) {
        while (cursor.moveToNext()) {
            childMap.put(cursor.getString(cursor.getColumnIndex(CHILD_TAB_TEXT)),
                    cursor.getInt(cursor.getColumnIndex(ID)));
        }
    }

    //===================== end 添加数据部分 =========================**/

    //===================== start 查询数据部分 =========================
    private Map<String, SparseArray<String>> childTabMapByFind = new HashMap<>();


    public LogFiltrateBean findAllLogFiltrate() {
        childTabMapByFind.clear();

        LogFiltrateBean logFiltrateBean = new LogFiltrateBean();
        logFiltrateBean.setAuthors(findChildTab(AUTHOR));
        logFiltrateBean.setLevels(findChildTab(LEVEL));
        logFiltrateBean.setPackageNames(findChildTab(PACKAGE_NAME));
        logFiltrateBean.setThreads(findChildTab(THREAD));
        logFiltrateBean.setExtra1s(findChildTab(EXTRA_1));
        logFiltrateBean.setExtra2s(findChildTab(EXTRA_2));

        Set childTab = findChildTab(TAG);
        logFiltrateBean.setTagBeans(childTab);

        Cursor cursor = database.rawQuery("select time from " + TABLE_NAME_LOG + " limit 1", null);
        while (cursor.moveToNext()) {
            logFiltrateBean.setStartTime(cursor.getInt(cursor.getColumnIndex(TIME)));
        }
        cursor.close();
        cursor = database.rawQuery("select time from " + TABLE_NAME_LOG + " order by " + ID + " desc" + " limit 1", null);
        while (cursor.moveToNext()) {
            logFiltrateBean.setEndTime(cursor.getInt(cursor.getColumnIndex(TIME)));
        }

        return logFiltrateBean;
    }

    private Set<ChildTabBean> findChildTab(String childTabName) {
        SparseArray<String> childMap = new SparseArray<>();
        Set<ChildTabBean> hashSet = new HashSet<>();

        Cursor cursor = database.rawQuery("select * from " + childTabName, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(ID));
            String text = cursor.getString(cursor.getColumnIndex(CHILD_TAB_TEXT));
            childMap.put(id, text);
            if (childTabName.equals(TAG)) {
                int tagSelect = cursor.getInt(cursor.getColumnIndex(TAG_SELECT));
                hashSet.add(new TagBean(id, text, tagSelect != 0));
            } else {
                hashSet.add(new ChildTabBean(id, text));
            }

        }
        cursor.close();
        childTabMapByFind.put(childTabName, childMap);
        return hashSet;
    }

    public List<LogBean> find(FiltrateParamsBean filtrateParamsBean) {
        if (!filtrateParamsBean.isValidFiltrate()) {
            //如果筛选条件无效，会导致结果为empty
            return new ArrayList<>();
        }

        List<LogBean> logBeanList = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("select * from ");
        sqlBuilder.append(TABLE_NAME_LOG);
        sqlBuilder.append(" where ");
        int length = sqlBuilder.length();

        if (filtrateParamsBean.getTagBeans() != null) {
            Set<TagBean> tagBeans = new HashSet<>();
            for (TagBean tagBean : filtrateParamsBean.getTagBeans()) {
                if (tagBean.isTagSelect()) {
                    tagBeans.add(tagBean);
                }
            }
            buildFiltrateSql(TAG, sqlBuilder, tagBeans);
        }
        buildFiltrateSql(AUTHOR, sqlBuilder, filtrateParamsBean.getAuthors());
        buildFiltrateSql(LEVEL, sqlBuilder, filtrateParamsBean.getLevels());
        buildFiltrateSql(PACKAGE_NAME, sqlBuilder, filtrateParamsBean.getPackageNames());
        buildFiltrateSql(THREAD, sqlBuilder, filtrateParamsBean.getThreads());
        buildFiltrateSql(EXTRA_1, sqlBuilder, filtrateParamsBean.getExtra1s());
        buildFiltrateSql(EXTRA_2, sqlBuilder, filtrateParamsBean.getExtra2s());

        if (!TextUtils.isEmpty(filtrateParamsBean.getMatchText())) {
            sqlBuilder.append(CONTENT);
            sqlBuilder.append(" like ");
            sqlBuilder.append(" '%");
            sqlBuilder.append(filtrateParamsBean.getMatchText());
            sqlBuilder.append("%' ");
        } else if (sqlBuilder.length() != length) {
            //如果添加筛选条件，删除最后的"and"
            sqlBuilder.delete(sqlBuilder.length() - 5, sqlBuilder.length());
        } else {
            //没有添加筛选条件,删除where
            sqlBuilder.delete(sqlBuilder.length() - 7, sqlBuilder.length());
        }

        sqlBuilder.append(" limit ");
        sqlBuilder.append(FiltrateParamsBean.pageSize * (filtrateParamsBean.getPageNo()+1));
        sqlBuilder.append(" offset ");
//        sqlBuilder.append(filtrateParamsBean.getPageNo() * FiltrateParamsBean.pageSize);
        sqlBuilder.append(0);


        String sql = sqlBuilder.toString();
        System.out.println(sql);
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            LogBean logBean = new LogBean();
            logBean.setId(cursor.getLong(cursor.getColumnIndex(ID)));
            logBean.setTime(cursor.getLong(cursor.getColumnIndex(TIME)));
            logBean.setContent(cursor.getString(cursor.getColumnIndex(CONTENT)));

            logBean.setPackageName(getChildItemText(PACKAGE_NAME, cursor));
            logBean.setLevel(getChildItemText(LEVEL, cursor));
            logBean.setThread(getChildItemText(THREAD, cursor));
            logBean.setStackTrace(getChildItemText(STACK_TRACE, cursor));
            logBean.setAuthor(getChildItemText(AUTHOR, cursor));
            logBean.setRemarks(getChildItemText(REMARKS, cursor));
            logBean.setSummary(getChildItemText(SUMMARY, cursor));
            logBean.setTag(getChildItemText(TAG, cursor));
            logBean.setExtra1(getChildItemText(EXTRA_1, cursor));
            logBean.setExtra2(getChildItemText(EXTRA_2, cursor));

            logBeanList.add(logBean);
        }
        return logBeanList;
    }

    private void buildFiltrateSql(String childTableName, StringBuilder sqlBuilder, Set<? extends ChildTabBean> childTabBeen) {
        if (childTabBeen == null || childTabBeen.isEmpty()) {
            return;
        }
        sqlBuilder.append(childTableName);
        sqlBuilder.append(" in ");

        sqlBuilder.append("(");
        for (ChildTabBean childTabBean : childTabBeen) {
            sqlBuilder.append(childTabBean.getId());
            sqlBuilder.append(",");
        }
        sqlBuilder.delete(sqlBuilder.length() - 1, sqlBuilder.length());
        sqlBuilder.append(")");
        sqlBuilder.append(" and ");
    }

    public List<LogBean> findAll() {
        ArrayList<LogBean> logBeanList = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME_LOG, null);
        while (cursor.moveToNext()) {
            LogBean logBean = new LogBean();
            logBean.setId(cursor.getLong(cursor.getColumnIndex(ID)));
            logBean.setTime(cursor.getLong(cursor.getColumnIndex(TIME)));
            logBean.setContent(cursor.getString(cursor.getColumnIndex(CONTENT)));

            logBean.setPackageName(getChildItemText(PACKAGE_NAME, cursor));
            logBean.setLevel(getChildItemText(LEVEL, cursor));
            logBean.setThread(getChildItemText(THREAD, cursor));
            logBean.setStackTrace(getChildItemText(STACK_TRACE, cursor));
            logBean.setAuthor(getChildItemText(AUTHOR, cursor));
            logBean.setRemarks(getChildItemText(REMARKS, cursor));
            logBean.setSummary(getChildItemText(SUMMARY, cursor));
            logBean.setTag(getChildItemText(TAG, cursor));
            logBean.setExtra1(getChildItemText(EXTRA_1, cursor));
            logBean.setExtra2(getChildItemText(EXTRA_2, cursor));

            logBeanList.add(logBean);
        }
        return logBeanList;
    }

    private String getChildItemText(String childTabName, Cursor parentCursor) {
        int childItemId = parentCursor.getInt(parentCursor.getColumnIndex(childTabName));

        SparseArray<String> childMap = childTabMapByFind.get(childTabName);
        if (childMap == null) {
            childMap = new SparseArray<>();
            Cursor cursor = database.rawQuery("select * from " + childTabName, null);
            while (cursor.moveToNext()) {
                childMap.put(cursor.getInt(cursor.getColumnIndex(ID)),
                        cursor.getString(cursor.getColumnIndex(CHILD_TAB_TEXT)));
            }
            cursor.close();
            childTabMapByFind.put(childTabName, childMap);
        }
        return childMap.get(childItemId);
    }

    //===================== end 查询数据部分 =========================**/

    public void delete() {
        database.beginTransaction();
        database.execSQL("delete from " + TABLE_NAME_LOG);
        database.setTransactionSuccessful();
        database.endTransaction();
    }
}
