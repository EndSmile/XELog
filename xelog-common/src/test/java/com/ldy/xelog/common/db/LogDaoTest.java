package com.ldy.xelog.common.db;

import com.ldy.xelog.common.BuildConfig;
import com.ldy.xelog.common.bean.LogBean;
import com.ldy.xelog.common.XELogCommon;
import com.ldy.xelog.common.bean.LogFiltrateBean;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by ldy on 2017/4/10.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class LogDaoTest {

    private LogDao logDao;

    @Before
    public void setup() {
        XELogCommon.context = RuntimeEnvironment.application;
        logDao = LogDao.instance();
    }

    @Test
    public void addData() throws Exception {
        LogBean logBean = new LogBean();
        logBean.setLevel("error");
        logBean.setTag("net");
        logBean.setAuthor("ldy");
        logBean.setContent("action");
        logDao.addData(logBean);
        List<LogBean> logBeen = logDao.findAll();
        assertEquals(1, logBeen.size());
        logBean = logBeen.get(0);
        assertEquals("error", logBean.getLevel());
        assertEquals("net", logBean.getTag());
        assertEquals("ldy", logBean.getAuthor());
        assertEquals("action", logBean.getContent());
    }

    @Test
    public void findAll() throws Exception {
    }

    @Test
    public void find() throws Exception {
        logDao.deleteAll();

        LogBean logBean = new LogBean();
        logBean.setLevel("error");
        logBean.setTag("net");
        logBean.setAuthor("ldy");
        logBean.setContent("action");
        logDao.addData(logBean);

        logBean = new LogBean();
        logBean.setLevel("v");
        logBean.setTag("activity");
        logBean.setAuthor("ldy2");
        logBean.setContent("xxxx");
        logDao.addData(logBean);

//        LogFiltrateBean allLogFiltrate = logDao.findAllLogFiltrate();
//        FiltrateParamsBean filtrateParamsBean = new FiltrateParamsBean();
//        List<ChildTabBean> authors = allLogFiltrate.getAuthors();
//        authors.remove(0);
//        filtrateParamsBean.setAuthors(authors);
//        filtrateParamsBean.setLevels(allLogFiltrate.getLevels());
//        List<LogBean> logBeen = logDao.find(filtrateParamsBean);
//        assertEquals(1, logBeen.size());
    }

    @Test
    public void findAllLogFiltrate() throws Exception {
        logDao.deleteAll();

        LogBean logBean = new LogBean();
        logBean.setLevel("error");
        logBean.setTag("net");
        logBean.setAuthor("ldy");
        logBean.setContent("action");
        logBean.setTime(11111);
        logDao.addData(logBean);

        logBean = new LogBean();
        logBean.setLevel("v");
        logBean.setTag("activity");
        logBean.setAuthor("ldy2");
        logBean.setContent("xxxx");
        logBean.setTime(11113);
        logDao.addData(logBean);

        LogFiltrateBean allLogFiltrate = logDao.findAllLogFiltrate();
        assertEquals(2,allLogFiltrate.getAuthors().size());
        assertEquals(2,allLogFiltrate.getLevels().size());
        assertEquals(11111,allLogFiltrate.getStartTime());
        assertEquals(11113,allLogFiltrate.getEndTime());

    }


}