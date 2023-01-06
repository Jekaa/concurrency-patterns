package ru.concurrency.patterns.exchanger;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Exchanger;

import static java.util.concurrent.CompletableFuture.runAsync;

public class ExchangerDemo {

    public static void main(String[] args) {

        Exchanger<Queue<String>> readerExchanger = new Exchanger<>();
        Exchanger<Queue<String>> writerExchanger = new Exchanger<>();

        Reader reader = new Reader(readerExchanger, 32);
        Processor processor = new Processor(readerExchanger, writerExchanger);
        Writer writer = new Writer(writerExchanger);

        CompletableFuture.allOf(
                runAsync(reader),
                runAsync(processor),
                runAsync(writer)).join();
    }
}
