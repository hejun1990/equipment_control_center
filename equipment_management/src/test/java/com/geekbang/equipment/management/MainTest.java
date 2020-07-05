package com.geekbang.equipment.management;

import com.geekbang.equipment.management.util.ThreadPoolFactory;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import org.assertj.core.util.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainTest {

    public static void main(String[] args) {
        int count = 10;
        CompletableFuture<List<String>>[] futureArray = new CompletableFuture[count];
        int x = 0;
        for (int i = 0; i < count; i++) {
            int y = x = x + 2;
            CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
                List<String> list = new ArrayList<>(2);
                String threadName = Thread.currentThread().getName();
                for (int j = y - 2; j < y; j++) {
                    list.add(threadName + ":" + j);
                }
                return list;
            }, ThreadPoolFactory.CACHED.getPool());
            futureArray[i] = future;
        }
        CompletableFuture<List<String>> combineFuture = CompletableFuture.allOf(futureArray)
                .thenApply(v ->
                        Stream.of(futureArray)
                                .flatMap(listFuture -> {
                                    List<String> strings = listFuture.join();
                                    return strings.stream();
                                })
                                .collect(Collectors.toList()));
        try {
            List<String> strings = combineFuture.get();
            for (String string : strings) {
                System.out.println(string);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Observable.create((ObservableOnSubscribe<Integer>) observableEmitter -> {
            for (int i = 1; i <= 5; i++) {
                System.out.println(Thread.currentThread().getName() + "，发射" + i);
                observableEmitter.onNext(i);
            }
        }).flatMap(integer -> {
            System.out.println(Thread.currentThread().getName() + "，查询表" + integer);
            List<String> list = Lists.newArrayList();
            int num = (integer - 1) * 2;
            for (int i = 0; i < 3; i++) {
                list.add("data_" + (integer + num + i));
            }
            return Observable.fromIterable(list);
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(s -> System.out.println(Thread.currentThread().getName() + "，" + s));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("完毕");
    }
}
