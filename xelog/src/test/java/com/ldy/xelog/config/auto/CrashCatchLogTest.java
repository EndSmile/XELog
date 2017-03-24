package com.ldy.xelog.config.auto;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ldy on 2017/3/23.
 */
public class CrashCatchLogTest {
    @Test
    public void testGetSummary() {
        String msg = "io.reactivex.exceptions.OnErrorNotImplementedException:Att...";
        try {
            String[] strings = msg.split(":");
            assertEquals(strings[0],"io.reactivex.exceptions.OnErrorNotImplementedException");
            String[] fullClass = strings[0].split("\\.");
            for (String name:fullClass){
                System.out.println(name);
            }
            assertEquals(4,fullClass.length);
            assertEquals(fullClass[fullClass.length - 1], "OnErrorNotImplementedException");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}