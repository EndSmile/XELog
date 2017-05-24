package com.ldy.xelog.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldy on 2017/5/23.
 */

public class QuickLog extends XELogConfig {
    @Override
    public List<String> getBaseTag() {
        ArrayList<String> list = new ArrayList<>();
        list.add("quick");
        return list;
    }

    @Override
    public String getSummary(String msg) {
        return msg;
    }

}
