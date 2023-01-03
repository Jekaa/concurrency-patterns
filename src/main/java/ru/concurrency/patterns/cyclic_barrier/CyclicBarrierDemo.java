package ru.concurrency.patterns.cyclic_barrier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.random.RandomGenerator;

public class CyclicBarrierDemo {

    private CyclicBarrier cyclicBarrier;
    private final List<List<Integer>> partialResults = Collections.synchronizedList(new ArrayList<>());
    private final RandomGenerator rg = RandomGenerator.getDefault();
    private int NUM_PARTIAL_RESULTS;
    private int NUM_WORKERS;

    class AggregatorThread implements Runnable {

        @Override
        public void run() {

            String thisThreadName = Thread.currentThread().getName();

            System.out.println(thisThreadName + ": Computing sum of " + NUM_WORKERS
                    + " workers, having " + NUM_PARTIAL_RESULTS + " results each.");
            int sum = 0;

            for (List<Integer> threadResult : partialResults) {
                System.out.print("Adding ");
                for (Integer partialResult : threadResult) {
                    System.out.print(partialResult + " ");
                    sum += partialResult;
                }
                System.out.println();
            }
            System.out.println(thisThreadName + ": Final result = " + sum);
        }
    }

    class NumberCruncherThread implements Runnable {

        @Override
        public void run() {
            String thisThreadName = Thread.currentThread().getName();
            List<Integer> partialResult = new ArrayList<>();

            // Crunch some numbers and store the partial result
            for (int i = 0; i < NUM_PARTIAL_RESULTS; i++) {
                Integer num = rg.nextInt(10);
                System.out.println(thisThreadName + ": Crunching some numbers! Final result - " + num);
                partialResult.add(num);
            }

            partialResults.add(partialResult);
            try {
                System.out.println(thisThreadName
                        + " waiting for others to reach barrier.");
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void runSimulation(int numWorkers, int numberOfPartialResults) {
        NUM_PARTIAL_RESULTS = numberOfPartialResults;
        NUM_WORKERS = numWorkers;

        cyclicBarrier = new CyclicBarrier(NUM_WORKERS, new AggregatorThread());

        System.out.println("Spawning " + NUM_WORKERS
                + " worker threads to compute "
                + NUM_PARTIAL_RESULTS + " partial results each");

        for (int i = 0; i < NUM_WORKERS; i++) {
            Thread worker = new Thread(new NumberCruncherThread());
            worker.setName("Thread " + i);
            worker.start();
        }
    }

    public static void main(String[] args) {
        CyclicBarrierDemo demo = new CyclicBarrierDemo();
        demo.runSimulation(10, 5);
    }
}
