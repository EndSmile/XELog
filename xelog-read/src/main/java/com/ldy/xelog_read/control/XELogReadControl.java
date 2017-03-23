package com.ldy.xelog_read.control;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldy.xelog_read.bean.JsonFileBean;
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
            XELogReadControl.this.initState(dataList);
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
//        tag = new Tag("xelog");
        authors = new HashSet<>();

        for (int i = 0, length = dataList.size(); i < length; i++) {
            JsonFileBean jsonFileBean = dataList.get(i);
            long time = jsonFileBean.getTime();
            if (startTime == 0) {
                startTime = time;
            } else if (startTime > time) {
                startTime = time;
            }
            if (endTime == 0) {
                endTime = time;
            } else if (endTime < time) {
                endTime = time;
            }

            packageNames.add(jsonFileBean.getPackageName());
            levels.add(jsonFileBean.getLevel());
//            tag.addTag(jsonFileBean.getTag());
            authors.add(jsonFileBean.getAuthor());
            threads.add(jsonFileBean.getThread());
        }

        checkedTime = endTime;
        checkedPackage = new HashSet<>(packageNames);
        checkedLevels = new HashSet<>(levels);
        checkedThreads = new HashSet<>(threads);
        checkedAuthors = new HashSet<>(authors);
    }

    public Maybe<Integer> jumpTime(final long time) {
        return Observable.fromArray(dataList.toArray(new JsonFileBean[dataList.size()]))
                .reduce((a, b) -> {
                    long temp1 = Math.abs(a.getTime() - time);
                    long temp2 = Math.abs(b.getTime() - time);
                    return temp1 < temp2 ? a : b;
                })
                .map(a -> dataList.indexOf(a))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ArrayList<JsonFileBean>> filtrate(List<String> levels,
                                                   List<String> threads,
                                                   List<String> authors) {

        ArrayList<JsonFileBean> jsonFileBeen = new ArrayList<>();
        return Observable.fromArray(dataList.toArray(new JsonFileBean[dataList.size()]))
                .filter(jsonFileBean -> levels.contains(jsonFileBean.getLevel()))
                .filter(jsonFileBean -> threads.contains(jsonFileBean.getThread()))
                .filter(jsonFileBean -> authors.contains(jsonFileBean.getAuthor()))
                .collectInto(jsonFileBeen, ArrayList::add);
    }


    private List<JsonFileBean> readFile() {
        StringBuilder content = new StringBuilder("["); //文件内容字符串
        String xelog = new File(Environment.getExternalStorageDirectory(), "xelog").getPath() + "/log";
        content.append(FileUtils.readFile(xelog));
        content.append("]");

        List<JsonFileBean> list = new Gson().fromJson(content.toString(), new TypeToken<List<JsonFileBean>>() {

        }.getType());
        //最后一个字符是",",所以解析出的最后一条数据是null,要删除最后一个数据
        list.remove(list.size() - 1);
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
