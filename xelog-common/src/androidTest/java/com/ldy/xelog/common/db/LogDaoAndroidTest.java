package com.ldy.xelog.common.db;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ldy.xelog.common.bean.LogBean;
import com.ldy.xelog.common.XELogCommon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by ldy on 2017/4/10.
 */
@RunWith(AndroidJUnit4.class)
public class LogDaoAndroidTest {

    private LogDao logDao;

    @Before
    public void setup() {
        XELogCommon.context = InstrumentationRegistry.getTargetContext();
        logDao = LogDao.instance();
    }
    @Test
    public void addData() throws Exception {
        logDao.deleteAll();
        LogBean logBean = new LogBean();
        logBean.setLevel("error");
        logBean.setTag("net");
        logBean.setContent("ldy");
        logDao.addData(logBean);
        assertEquals(1, logDao.findAll().size());
    }

    @Test
    public void findAll() throws Exception {

    }

}