package com.mine;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static volatile Integer integer = -1;

    private static final Lock lock = new ReentrantLock(true);
    private static final Condition condition = lock.newCondition();

    public static void main(String[] args) throws Exception {
        mainCountDownLatch();
    }

    public static void mainCountDownLatch() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(new MyRunnableCountDownLatch(latch)).start();
        latch.await();
        System.out.println(integer);
    }

//    public static void mainLock() throws InterruptedException {
//        new Thread(new MyRunnable1()).start();
//        while (lock.tryLock()){
//            System.out.println(integer);
//            lock.unlock();
//            return;
//        }
//    }

//    public static void mainCondition() throws InterruptedException {
//        condition.await();
//    }

    public static void mainCompletableFutureSupply(int sleepSecond){
        CompletableFuture<Integer> c1 = CompletableFuture.supplyAsync(()->{
            try {
                TimeUnit.SECONDS.sleep(sleepSecond);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return sleepSecond;
        });

        System.out.println(c1.join());
    }

    public static void mainCompletableFutureRun(int sleepSecond){
        CompletableFuture<Void> c1 = CompletableFuture.runAsync(new MyRunnable());
        c1.join();
        System.out.println(integer);
    }

//    public static void mainHappensBeforeVolatile() {
//        Thread thread = new Thread(new MyRunnable());
//        thread.start();
//        System.out.println(integer);
//    }

    public static void mainThreadPoolIsDone() {
        ExecutorService executorService= Executors.newCachedThreadPool();
        try {
            Future<Integer> submit = executorService.submit(new MyCallable(1));
            if(!submit.isDone()){

            }
            System.out.println(submit.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    public static void mainRunnableJoin() throws InterruptedException {
        Thread thread = new Thread(new MyRunnable());
        thread.start();
        thread.join();
        System.out.println(integer);
    }

    public static void mainRunnableWhile() throws InterruptedException {
        Thread thread = new Thread(new MyRunnable());
        thread.start();
        while (integer==-1){

        }
        System.out.println(integer);
    }

    public static void mainCallableIsDone() throws ExecutionException, InterruptedException {
        FutureTask<Integer> submit = new FutureTask<>(new MyCallable(1));
        Thread thread = new Thread(submit);
        thread.start();
        if(!submit.isDone()){

        }
        System.out.println(submit.get());
    }

    static class MyRunnable implements Runnable{

        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            integer = 1;
        }
    }

    static class MyRunnableCountDownLatch implements Runnable{
        CountDownLatch latch;

        MyRunnableCountDownLatch(CountDownLatch latch){
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
            integer = 1;
        }
    }

    static class MyRunnable1 implements Runnable{

        @Override
        public void run() {
            try {
                lock.lock();
                TimeUnit.SECONDS.sleep(2);
                integer = 1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
    }

    static class MyRunnable2 implements Runnable{

        @Override
        public void run() {
            try {
                condition.await();
                integer = 1;
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                condition.notifyAll();
            }
        }
    }

}
class MyCallable implements Callable<Integer>{
    private int sleepSecond;
    public MyCallable(int sleepSecond){
        this.sleepSecond = sleepSecond;
    }

    @Override
    public Integer call() throws Exception {
        TimeUnit.SECONDS.sleep(sleepSecond);
        return sleepSecond;
    }
}
