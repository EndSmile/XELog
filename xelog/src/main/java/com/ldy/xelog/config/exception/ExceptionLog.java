package com.ldy.xelog.config.exception;

import com.ldy.xelog.config.XELogConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldy on 2017/3/10.
 */

public class ExceptionLog extends XELogConfig {

    @Override
    public boolean withThread() {
        return true;
    }

    @Override
    public List<String> getBaseTag() {
        ArrayList<String> tags = new ArrayList<>();
        tags.add("exception");
        return tags;
    }
}
