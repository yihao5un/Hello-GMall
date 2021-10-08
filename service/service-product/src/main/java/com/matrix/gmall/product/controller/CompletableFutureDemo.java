package com.matrix.gmall.product.controller;

import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @Author: yihaosun
 * @Date: 2021/10/8 14:05
 */
public class CompletableFutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /**
         * runAsync 不支持返回值
         * supplyAsync 支持返回值
         */
//        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(new Supplier<Integer>() {
//            @Override
//            public Integer get() {
//                System.out.println(Thread.currentThread().getName() + " Matrix");
////                int i = 1 / 0;
//                return 1024;
//            }
//        }).thenApply(new Function<Integer, Integer>() {
//            // 串行化 当一个线程依赖上一个线程的时候 获取上一个任务的返回结果 并返回当前任务的返回值
//            @Override
//            public Integer apply(Integer integer) {
//                System.out.println("ThenApply:\t" + integer);
//                return integer * 2;
//            }
//        }).whenComplete(new BiConsumer<Integer, Throwable>() {
//            @Override
//            public void accept(Integer integer, Throwable throwable) {
//                System.out.println("Integer:\t" + integer);
//                System.out.println("Throwable:\t" + throwable);
//            }
//        }).exceptionally(new Function<Throwable, Integer>() {
//            @Override
//            public Integer apply(Throwable throwable) {
//                System.out.println("Throwable:\t" + throwable);
//                return 404;
//            }
//        });
//
//        System.out.println(integerCompletableFuture.get());

        // 创建线程池 注意 有七大核心参数
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                2,
                5,
                3L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(3)
        );

        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> "hello\t");

        CompletableFuture<Void> futureB = futureA.thenAcceptAsync((t) -> {
            delaySec(3);
            printCurrTime(t + "futureB");
        }, threadPoolExecutor);

        CompletableFuture<Void> futureC = futureA.thenAcceptAsync((t) -> {
            delaySec(1);
            printCurrTime(t + "futureC");
        }, threadPoolExecutor);

        futureB.get();
        futureC.get();

        // 关闭线程池 否则会一直阻塞
        threadPoolExecutor.shutdown();
    }

    private static void printCurrTime(String s) {
        System.out.println(s);
    }

    private static void delaySec(int i) {
        try {
            Thread.sleep(i * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
