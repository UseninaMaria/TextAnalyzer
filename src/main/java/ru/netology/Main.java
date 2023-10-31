package ru.netology;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final int QUEUE_CAPACITY = 100;
    private static BlockingQueue<String> namesA = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static BlockingQueue<String> namesB = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static BlockingQueue<String> namesC = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    private static AtomicInteger countA = new AtomicInteger(0);
    private static AtomicInteger countB = new AtomicInteger(0);
    private static AtomicInteger countC = new AtomicInteger(0);

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void countingSymbol(String text, char a) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == a) {
                switch (a) {
                    case 'a':
                        countA.incrementAndGet();
                        break;
                    case 'b':
                        countB.incrementAndGet();
                        break;
                    case 'c':
                        countC.incrementAndGet();
                        break;
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Thread textThread = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                String generatedText = generateText("abc", 100_000);
                try {
                    namesA.put(generatedText);
                    namesB.put(generatedText);
                    namesC.put(generatedText);

                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        textThread.start();
        Thread.sleep(10000);

        Thread threadA = new Thread(() -> {
            try {
                while (!namesA.isEmpty()) {
                    countingSymbol(namesA.take(), 'a');
                }
            } catch (InterruptedException e) {
                return;
            }
        });

        Thread threadB = new Thread(() -> {
            try {
                while (!namesB.isEmpty()) {
                    countingSymbol(namesB.take(), 'b');
                }
            } catch (InterruptedException e) {
                return;
            }
        });

        Thread threadC = new Thread(() -> {
            try {
                while (!namesC.isEmpty()) {
                    countingSymbol(namesC.take(), 'c');
                }
            } catch (InterruptedException e) {
                return;
            }
        });


        threadA.start();
        threadB.start();
        threadC.start();


        threadA.join();
        threadB.join();
        threadC.join();

        System.out.println("символов 'a': " + countA.get());
        System.out.println("символов 'b': " + countB.get());
        System.out.println("символов 'c': " + countC.get());

        textThread.interrupt();
    }
}