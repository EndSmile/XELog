package com.ldy.xelogSample;

import android.content.Context;

import com.ldy.xelog.config.XELogConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldy on 2017/3/3.
 */

public class NetLog extends XELogConfig {

    @Override
    public String getAuthor() {
        return "ldy2@xdja.com";
    }

    @Override
    public String getRemarks(String message) {
        return "这是一个网络日志";
    }

    @Override
    public String getSummary(String msg) {
        return "method:login";
    }

    @Override
    public boolean withThread() {
        return true;
    }

    @Override
    public List<String> getBaseTag() {
        ArrayList<String> tags = new ArrayList<>();
        tags.add("net");
        tags.add("login");
        return tags;
    }
}
