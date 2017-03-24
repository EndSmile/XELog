package com.ldy.xelog_read;

import org.junit.Test;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testContainsAll() {
        List<String> strings1 = new ArrayList<>();
        List<String> strings2 = new LinkedList<>();

        strings1.add("ldy1");
        strings1.add("ldy2");
        strings2.add("ldy1");
        strings2.add("ldy2");
        assertTrue(strings1.containsAll(strings2));
    }
}