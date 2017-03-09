package com.ldy.xelog.config.auto;

import android.content.Context;

import com.ldy.xelog.config.XELogConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldy on 2017/3/8.
 */

public class AbstractAutoLog extends XELogConfig {
    private String author = "";
    public AbstractAutoLog(Context context) {
        super(context);
    }

    public AbstractAutoLog(Context context, String author) {
        super(context);
        this.author = author;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public boolean withStackTrace() {
        return false;
    }

    @Override
    public boolean isPrintConsole() {
        return false;
    }

    @Override
    public List<String> getTag() {
        ArrayList<String> tags = new ArrayList<>();
        tags.add("auto");
        return tags;
    }

    @Override
    public boolean withThread() {
        return false;
    }
}
