package com.ldy.xelog_read.control;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldy.xelog_read.bean.JsonFileBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.subjects.PublishSubject;

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

    private DataLoadListener dataLoadListener;
    private List<JsonFileBean> dataList;
    private Observable<List<JsonFileBean>> observable;

    public XELogReadControl(Context context) {
        this.context = context;
    }

    public Observable<List<JsonFileBean>> init(final DataLoadListener dataLoadListener) {
        this.dataLoadListener = dataLoadListener;
        observable = Observable.create(new ObservableOnSubscribe<List<JsonFileBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<JsonFileBean>> e) throws Exception {
                dataList = readFile();
                initState(dataList);
                e.onNext(dataList);
//                dataLoadListener.loadFinish(dataList);
            }
        });
        return observable;

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                dataList = readFile();
//                initState(dataList);
//                dataLoadListener.loadFinish(dataList);
//            }
//        }).start();
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

    public int jumpTime(long time) {
//        Observable.fromArray(dataList.toArray(new JsonFileBean[dataList.size()])).reduce((a,b)->{
//
//        });

        long dif = Math.abs(dataList.get(0).getTime() - time);
        int position = 0;
        for (int i = 0, length = dataList.size(); i < length; i++) {
            JsonFileBean jsonFileBean = dataList.get(i);
            long temp = Math.abs(jsonFileBean.getTime() - time);
            if (temp < dif) {
                dif = temp;
                position = i;
            }
        }
        checkedTime = dataList.get(position).getTime();
        return position;
    }



    private List<JsonFileBean> readFile() {
        StringBuilder content = new StringBuilder("["); //文件内容字符串
        String xelog = new File(Environment.getExternalStorageDirectory(), "xelog").getPath() + "/log";
        Log.d("XELogReadControl", xelog);
        File file = new File(xelog);
        try {
            InputStream instream = new FileInputStream(file);//读取输入流
            InputStreamReader inputreader = new InputStreamReader(instream);//设置流读取方式
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line;
            while ((line = buffreader.readLine()) != null) {
                content.append(line).append("\n");//读取的文件内容
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    public interface DataLoadListener {
        void loadFinish(List<JsonFileBean> jsonFileBeen);
    }
}
