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
 * log的数据库相关操作，分为一个主表和
 * {@link #PACKAGE_NAME},{@link #LEVEL},{@link #THREAD},{@link #STACK_TRACE},
 * {@link #AUTHOR},{@link #REMARKS},{@link #SUMMARY},{@link #TAG},
 * {@link #EXTRA_1},{@link #EXTRA_2}共计10个子表。</p>
 * 每个子表的结构均为{id,text},其中{@link #TAG}表较为特殊，多出了{@link #TAG_SELECT}字段，
 * 代表这个tag是否默认被选中，值为非0时代表选中</p>
 *
 * 主表用过外键连接这些子表，且列名与子表表名相同</p>
 *
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

    private static List<String> childTabNameList = new ArrayList<>();

    static {
        childTabNameList.add(PACKAGE_NAME);
        childTabNameList.add(LEVEL);
        childTabNameList.add(THREAD);
        childTabNameList.add(STACK_TRACE);
        childTabNameList.add(AUTHOR);
        childTabNameList.add(REMARKS);
        childTabNameList.add(SUMMARY);
        childTabNameList.add(TAG);
        childTabNameList.add(EXTRA_1);
        childTabNameList.add(EXTRA_2);
    }

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

        for (String childTabName:childTabNameList){
            if (childTabName.equals(TAG)){
                String tagCreateSql = "CREATE TABLE IF NOT EXISTS "
                        + TAG
                        + " ("
                        + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + CHILD_TAB_TEXT + " TEXT UNIQUE,"
                        + TAG_SELECT + " INTEGER"
                        + ");";
                createSqlList.add(tagCreateSql);
            }else {
                createSqlList.add(getChildCreateSql(childTabName));
            }
        }
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

    /**
     * 增加一条数据，更新{@link #TABLE_NAME_LOG}表，并更新相关子表，子表内的数据不重复，主表在引用子表时通过
     * {@link #ID}引用
     * @param logBean 插入的数据
     */
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

    /**
     * 根据参数查找数据，注意,为了提高性能，当其中的某些参数为null时则视为查找全部，具体可参考
     * {@link FiltrateParamsBean#isValidFiltrate()}
     */
    public List<LogBean> find(FiltrateParamsBean filtrateParamsBean) {
        if (!filtrateParamsBean.isValidFiltrate()) {
            //如果筛选条件无效，会导致结果为empty
            return new ArrayList<>();
        }

        StringBuilder sqlBuilder = new StringBuilder("select * from ");
        sqlBuilder.append(TABLE_NAME_LOG);
        sqlBuilder.append(" where ");
        int length = sqlBuilder.length();

        //根据筛选数据构造sql语句
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
            //匹配字符串
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

        //查找按添加入表顺序的倒序
        sqlBuilder.append(" order by "+ID+" desc ");
        sqlBuilder.append(" limit ");
        //// TODO: 2017/4/11 为了避免分页时因数据库更新导致的数据错误，每次查找时更新全部数据
        sqlBuilder.append(FiltrateParamsBean.pageSize * (filtrateParamsBean.getPageNo()+1));
        sqlBuilder.append(" offset ");
//        sqlBuilder.append(filtrateParamsBean.getPageNo() * FiltrateParamsBean.pageSize);
        sqlBuilder.append(0);


        String sql = sqlBuilder.toString();
        System.out.println(sql);
        Cursor cursor = database.rawQuery(sql, null);
        return getLogBeenByCursor(cursor);
    }

    private List<LogBean> getLogBeenByCursor(Cursor cursor) {
        List<LogBean> logBeanList = new ArrayList<>();
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

    /**
     * 根据筛选集和子表名构造sql语句
     */
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
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME_LOG, null);
        return getLogBeenByCursor(cursor);
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

    //===================== start 删除数据部分 =========================**/
    public void deleteAll() {
        database.beginTransaction();
        database.execSQL("delete from " + TABLE_NAME_LOG);
        for (String childTabName:childTabNameList){
            database.execSQL("delete from " + childTabName);
        }
        childTabMap.clear();
        childTabMapByFind.clear();
        database.setTransactionSuccessful();
        database.endTransaction();
    }
}
