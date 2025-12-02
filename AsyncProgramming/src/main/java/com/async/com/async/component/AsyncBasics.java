package com.async.com.async.component;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class AsyncBasics {

    @Async
    public void print() throws InterruptedException {
        for (int i = 0; i <=5 ; i++) {
            Thread.sleep(10);
            System.out.println(Thread.currentThread().getName()+" : "+i);
        }
    }

    @Async
    public void getExeceptionWithVoid() {
        throw new RuntimeException("Exception inside the async void method");
    }

    @Async
    public CompletableFuture<String> getExeceptionWithFuture() {
        throw new RuntimeException("Exception inside the async future method");
    }
}
