package com.ldy.xelog_read.control;

import android.content.Context;

import com.ldy.xelog.common.bean.LogBean;
import com.ldy.xelog.common.bean.LogFiltrateBean;
import com.ldy.xelog.common.bean.TagBean;
import com.ldy.xelog.common.db.LogDao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by ldy on 2017/3/6.
 */
public class XELogReadControl {
    private Context context;

    private long startTime;
    private long checkedTime;
    private long endTime;
    private Set<String> packageNames;
    private Set<String> checkedPackage;
    private Set<String> levels;
    private Set<String> checkedLevels;
    private Tag tag;
    private Set<String> authors;
    private Set<String> checkedAuthors;
    private Set<String> threads;
    private Set<String> checkedThreads;

    private List<LogBean> dataList;
    private ExecutorService executor;
    private DataLoadListener dataLoadListener;
    private LogDao logDao;
    private LogFiltrateBean logFiltrate;

    public XELogReadControl(Context context) {
        this.context = context;
        executor = Executors.newSingleThreadExecutor();
        logDao = LogDao.instance();

    }

    public void init(DataLoadListener dataLoadListener) {
        this.dataLoadListener = dataLoadListener;
        executor.execute(() -> {
            dataList = XELogReadControl.this.readFile();

            initState();
//            initState(dataList);

            dataLoadListener.loadFirst(dataList);
        });

    }

    private void initState() {
        logFiltrate = logDao.findAllLogFiltrate();
        tag = new Tag("xelog-read");
        for (TagBean tagBean: logFiltrate.getTagBeans()){
            tag.addTag(tagBean.getTagList(),tagBean.isTagSelect());
        }
        tag.trim();

    }

//    private void initState(List<LogBean> dataList) {
//        if (dataList == null || dataList.isEmpty()) {
//            return;
//        }
//
//
//        packageNames = new HashSet<>();
//        levels = new HashSet<>();
//        threads = new HashSet<>();
//        tag = new Tag("xelog-read");
//        authors = new HashSet<>();
//
//        startTime = -1;
//        endTime = -1;
//
//        for (int i = 0, length = dataList.size(); i < length; i++) {
//            LogBean logBean = dataList.get(i);
//            updateTime(logBean);
//
//            packageNames.add(logBean.getPackageName());
//            levels.add(logBean.getLevel());
//            tag.addTag(logBean.getTagList(), logBean.isTagSelect());
//            authors.add(logBean.getAuthor());
//            threads.add(logBean.getThread());
//        }
//
//        checkedTime = endTime;
//        checkedPackage = new HashSet<>(packageNames);
//        checkedLevels = new HashSet<>(levels);
//        checkedThreads = new HashSet<>(threads);
//        checkedAuthors = new HashSet<>(authors);
//
//        tag.trim();
//    }

    private void updateTime(LogBean logBean) {
        if (logBean == null) {
            return;
        }
        long time = logBean.getTime();
        if (endTime == -1) {
            endTime = time;
        } else if (startTime > time) {
            startTime = time;
        }
        if (startTime == -1) {
            startTime = time;
        } else if (endTime < time) {
            endTime = time;
            checkedTime = endTime;
        }
    }

    public void jump2Time(long time) {
        executor.execute(() -> {
            long dif = Math.abs(dataList.get(0).getTime() - time);
            int position = 0;
            for (int i = 0, length = dataList.size(); i < length; i++) {
                LogBean logBean = dataList.get(i);
                long temp = Math.abs(logBean.getTime() - time);
                if (temp < dif) {
                    dif = temp;
                    position = i;
                }
            }
            checkedTime = dataList.get(position).getTime();
            dataLoadListener.jumpPosition(position);
        });
    }

    public void filtrate(List<String> levels,
                         List<String> threads,
                         List<String> authors,
                         String regular) {
        startTime = -1;
        endTime = -1;

        executor.execute(() -> {
            //view的check改变时tag会被改变
            List<List<String>> selectPaths = tag.getAllPath();

            for (List<String> path : selectPaths) {
                //移除统一添加的根节点
                path.remove(0);
            }

            List<LogBean> result = new ArrayList<>();
            for (LogBean logBean : dataList) {
                if (!isContain(levels, logBean.getLevel())) {
                    continue;
                }
                if (!isContain(threads, logBean.getThread())) {
                    continue;
                }
                if (!isContain(authors, logBean.getAuthor())) {
                    continue;
                }
                if (!isContainPath(selectPaths, logBean.getTagList())) {
                    continue;
                }
                if (!logBean.getContent().contains(regular)) {
                    continue;
                }
                updateTime(logBean);
                result.add(logBean);
            }
            dataLoadListener.loadFinish(result);
        });


    }

    private boolean isContain(List<String> list, String item) {
        return list.contains(item);
    }

    private boolean isContainPath(List<List<String>> selectPaths, List<String> tag) {
        for (List<String> path : selectPaths) {
            if (path.containsAll(tag)) {
                return true;
            }
        }
        return false;
    }

    private List<LogBean> readFile() {
//        StringBuilder content = new StringBuilder("["); //文件内容字符串
//        String xelog = XELogRead.getXelogDirPath() + "/log";
//        content.append(FileUtils.readFile(xelog));
//        content.append("]");
//
//        List<LogBean> list = new Gson().fromJson(content.toString(), new TypeToken<List<LogBean>>() {
//
//        }.getType());
//        //最后一个字符是",",所以解析出的最后一条数据是null,要删除最后一个数据
//        if (list.size() >= 1) {
//            list.remove(list.size() - 1);
//        }
        return logDao.findAll();
    }

    public long getStartTime() {
        return startTime;
    }

    public long getCheckedTime() {
        return checkedTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public Set<String> getPackageNames() {
        return packageNames;
    }

    public Set<String> getCheckedPackage() {
        return checkedPackage;
    }

    public Set<String> getLevels() {
        return levels;
    }

    public Set<String> getCheckedLevels() {
        return checkedLevels;
    }

    public Tag getTag() {
        return tag;
    }

    public Set<String> getAuthors() {
        return authors;
    }

    public Set<String> getCheckedAuthors() {
        return checkedAuthors;
    }

    public Set<String> getThreads() {
        return threads;
    }

    public Set<String> getCheckedThreads() {
        return checkedThreads;
    }

    public interface DataLoadListener {
        void loadFirst(List<LogBean> jsonFileBeen);

        void loadFinish(List<LogBean> jsonFileBeen);

        void jumpPosition(int position);
    }
}
