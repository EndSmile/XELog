package com.ldy.xelog_read.control;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ldy.xelog.common.bean.FiltrateParamsBean;
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
    public static final String ROOT_TAG = "xelog";
    private Context context;

    private long startTime;
    private long checkedTime;
    private long endTime;
    private Set<String> packageNames;
    private Set<String> checkedPackage;
    private Set<String> levels;
    private Set<String> checkedLevels;
    private Tag tag;
    private TagTreeNode tagRootNode;
    private Set<String> authors;
    private Set<String> checkedAuthors;
    private Set<String> threads;
    private Set<String> checkedThreads;
    private Set<String> extra1s;
    private Set<String> checkedExtra1s;
    private Set<String> extra2s;
    private Set<String> checkedExtra2s;

    private List<LogBean> dataList;
    private ExecutorService executor;
    private DataLoadListener dataLoadListener;
    private LogDao logDao;
    private LogFiltrateBean logFiltrate;
    private FiltrateParamsBean filtrateParamsBean;

    public XELogReadControl(Context context) {
        this.context = context;
        executor = Executors.newSingleThreadExecutor();
        logDao = LogDao.instance();

    }

    public void init(DataLoadListener dataLoadListener) {
        this.dataLoadListener = dataLoadListener;
        executor.execute(() -> {
            initState();
            if (filtrateParamsBean == null) {
                filtrateParamsBean = new FiltrateParamsBean();
                filtrateParamsBean.setTagBeans(logFiltrate.getTagBeans());
            } else {
                filtrateParamsBean.setPageNo(0);
            }
            dataList = logDao.find(filtrateParamsBean);
            dataLoadListener.loadFirst(dataList);
        });
    }

    public void filtrate(int pageNo
            , List<String> levels, List<String> threads, List<String> authors, List<String> packages
            , List<String> extra1s, List<String> extra2s, String regular) {
        executor.execute(() -> {
            filtrateParamsBean = new FiltrateParamsBean();
            filtrateParamsBean.setLevels(LogFiltrateBean.getChildTabSet(logFiltrate.getLevels(), levels));
            filtrateParamsBean.setAuthors(LogFiltrateBean.getChildTabSet(logFiltrate.getAuthors(), authors));
            filtrateParamsBean.setThreads(LogFiltrateBean.getChildTabSet(logFiltrate.getThreads(), threads));
            filtrateParamsBean.setPackageNames(LogFiltrateBean.getChildTabSet(logFiltrate.getPackageNames(), packages));

            filtrateParamsBean.setTagBeans(TagBean.getTagTabSet(logFiltrate.getTagBeans(), getTagStrList()));
            filtrateParamsBean.setExtra1s(LogFiltrateBean.getChildTabSet(logFiltrate.getExtra1s(), extra1s));
            filtrateParamsBean.setExtra2s(LogFiltrateBean.getChildTabSet(logFiltrate.getExtra2s(), extra2s));
            filtrateParamsBean.setMatchText(regular);

            filtrateParamsBean.setPageNo(pageNo);
            dataList = logDao.find(filtrateParamsBean);
            dataLoadListener.loadFinish(dataList);
        });
    }

    public void deleteAllLog() {
        executor.execute(() -> {
            logDao.deleteAll();
            init(dataLoadListener);
//            dataList = new ArrayList<>();
//            dataLoadListener.loadFinish(dataList);
        });
    }

    @NonNull
    private ArrayList<String> getTagStrList() {
        //view的check改变时tag会被改变
//        List<List<String>> selectPaths = tag.getAllPath();
        List temp = tagRootNode.getAllSelectedPath();
        List<List<String>> selectPaths = temp;
//        for (List<String> path : selectPaths) {
//            //移除统一添加的根节点
//            path.remove(0);
//        }
        ArrayList<String> selectStrPath = new ArrayList<>();
        for (List<String> path : selectPaths) {
            selectStrPath.add(LogBean.getStrByList(path, LogBean.TAG_SEPARATOR));
        }
        return selectStrPath;
    }

    private void initState() {
        logFiltrate = logDao.findAllLogFiltrate();
        tag = new Tag(ROOT_TAG);
        tagRootNode = TagTreeNode.root();
        for (TagBean tagBean : logFiltrate.getTagBeans()) {
            tag.addTag(tagBean.getTagList(), tagBean.isTagSelect());
            tagRootNode.addChildByValuePath(tagBean.getTagList(), tagBean.isTagSelect());
        }
        tag.trim();
        tagRootNode.trim();

        packageNames = LogFiltrateBean.getTextSet(logFiltrate.getPackageNames());
        levels = LogFiltrateBean.getTextSet(logFiltrate.getLevels());
        threads = LogFiltrateBean.getTextSet(logFiltrate.getThreads());
        authors = LogFiltrateBean.getTextSet(logFiltrate.getAuthors());
        extra1s = LogFiltrateBean.getTextSet(logFiltrate.getExtra1s());
        extra2s = LogFiltrateBean.getTextSet(logFiltrate.getExtra2s());

        startTime = logFiltrate.getStartTime();
        endTime = logFiltrate.getStartTime();
        checkedTime = endTime;

        checkedPackage = new HashSet<>(packageNames);
        checkedLevels = new HashSet<>(levels);
        checkedThreads = new HashSet<>(threads);
        checkedAuthors = new HashSet<>(authors);
        checkedExtra1s = new HashSet<>(extra1s);
        checkedExtra2s = new HashSet<>(extra2s);

    }

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

    public Set<String> getExtra1s() {
        return extra1s;
    }

    public Set<String> getCheckedExtra1s() {
        return checkedExtra1s;
    }

    public Set<String> getExtra2s() {
        return extra2s;
    }

    public Set<String> getCheckedExtra2s() {
        return checkedExtra2s;
    }

    public TagTreeNode getTagRootNode() {
        return tagRootNode;
    }

    public interface DataLoadListener {
        void loadFirst(List<LogBean> dataList);

        void loadFinish(List<LogBean> dataList);

        void jumpPosition(int position);
    }
}
