package com.async;

import com.async.com.async.component.AsyncBasics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AsyncCommandLineRunner implements CommandLineRunner {

    @Autowired
    AsyncBasics asyncBasics;

    @Override
    public void run(String... args) throws Exception {
        asyncBasics.print();

        asyncBasics.getExeceptionWithVoid();

        asyncBasics.getExeceptionWithFuture().exceptionally(ex -> "Recovered from: " + ex.getMessage())
                .thenAccept(System.out::println);
    }
}
