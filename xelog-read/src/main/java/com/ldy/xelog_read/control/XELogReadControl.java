package com.ldy.xelog_read.control;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldy.xelog.common.JsonFileBean;
import com.ldy.xelog_read.XelogRead;
import com.ldy.xelog_read.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

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

    private List<JsonFileBean> dataList;

    public XELogReadControl(Context context) {
        this.context = context;
    }

    public Observable<List<JsonFileBean>> init() {
        return Observable.create((ObservableOnSubscribe<List<JsonFileBean>>) e -> {
            dataList = XELogReadControl.this.readFile();
            initState(dataList);
            e.onNext(dataList);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    private void initState(List<JsonFileBean> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return;
        }

        packageNames = new HashSet<>();
        levels = new HashSet<>();
        threads = new HashSet<>();
        tag = new Tag("xelog-read");
        authors = new HashSet<>();

        startTime = -1;
        endTime = -1;

        for (int i = 0, length = dataList.size(); i < length; i++) {
            JsonFileBean jsonFileBean = dataList.get(i);
            updateTime(jsonFileBean);

            packageNames.add(jsonFileBean.getPackageName());
            levels.add(jsonFileBean.getLevel());
            tag.addTag(jsonFileBean.getTag(), jsonFileBean.isTagSelect());
            authors.add(jsonFileBean.getAuthor());
            threads.add(jsonFileBean.getThread());
        }

        checkedTime = endTime;
        checkedPackage = new HashSet<>(packageNames);
        checkedLevels = new HashSet<>(levels);
        checkedThreads = new HashSet<>(threads);
        checkedAuthors = new HashSet<>(authors);

        tag.trim();
    }

    private void updateTime(JsonFileBean jsonFileBean) {
        if (jsonFileBean == null) {
            return;
        }
        long time = jsonFileBean.getTime();
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

    public Maybe<Integer> jumpTime(final long time) {
        return Observable.fromIterable(dataList)
                .reduce((a, b) -> {
                    long temp1 = Math.abs(a.getTime() - time);
                    long temp2 = Math.abs(b.getTime() - time);
                    return temp1 < temp2 ? a : b;
                })
                .map(a -> dataList.indexOf(a))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<JsonFileBean>> filtrate(List<String> levels,
                                               List<String> threads,
                                               List<String> authors,
                                               String regular) {
        startTime = -1;
        endTime = -1;
        //view的check改变时tag会被改变
        List<List<String>> selectPaths = tag.getAllPath();

        for (List<String> path : selectPaths) {
            //移除统一添加的根节点
            path.remove(0);
        }

        return Observable.fromIterable(dataList)
                .filter(jsonFileBean -> isContain(levels, jsonFileBean.getLevel()))
                .filter(jsonFileBean -> isContain(threads, jsonFileBean.getThread()))
                .filter(jsonFileBean -> isContain(authors, jsonFileBean.getAuthor()))
                .filter(jsonFileBean -> {
                    for (List<String> path : selectPaths) {
                        if (path.containsAll(jsonFileBean.getTag())) {
                            return true;
                        }
                    }
                    return false;
                })
                .filter(jsonFileBean -> jsonFileBean.getContent().contains(regular))
                .doOnEach(notification -> updateTime(notification.getValue()))
                .toList()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private boolean isContain(List<String> list, String item) {
        return list.contains(item);
    }


    private List<JsonFileBean> readFile() {
        StringBuilder content = new StringBuilder("["); //文件内容字符串
        String xelog = XelogRead.xelogDirPath + "/log";
        content.append(FileUtils.readFile(xelog));
        content.append("]");

        List<JsonFileBean> list = new Gson().fromJson(content.toString(), new TypeToken<List<JsonFileBean>>() {

        }.getType());
        //最后一个字符是",",所以解析出的最后一条数据是null,要删除最后一个数据
        if (list.size() >= 1) {
            list.remove(list.size() - 1);
        }
        return list;
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

}
