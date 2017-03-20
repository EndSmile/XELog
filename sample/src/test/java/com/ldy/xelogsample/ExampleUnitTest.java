package com.ldy.xelogsample;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

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
    public void reduce() {
        Observable.just(1, 2, 3, 4, 5).reduce(10, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(@NonNull Integer a, @NonNull Integer b) throws Exception {
                System.out.println("a:" + a + ",b:" + b);
                return a;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer x) throws Exception {
                System.out.println(x);
            }
        });
    }
}