package avlyakulov.timur.lesson_atomic_variables;

import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.stream.IntStream;

public class GeneratorRunner {

    public static void main(String[] args) {
        EventNumberGenerator eventNumberGenerator = new EventNumberGenerator();

        int taskGenerationCounts = 10_000;
        final Runnable generatingTask = () -> IntStream.range(0, taskGenerationCounts).forEach(i -> eventNumberGenerator.generate());

        int amountOfGeneratingThreads = 5;
        final Thread[] generatingThreads = createThreads(generatingTask, amountOfGeneratingThreads);

        startThreads(generatingThreads);
        waitUntilFinish(generatingThreads);

        int expectedGeneratorValue = amountOfGeneratingThreads * taskGenerationCounts * 2;
        int actualGeneratorValue = eventNumberGenerator.getValue();

        if (expectedGeneratorValue != actualGeneratorValue)
            throw new RuntimeException(String.format("Expected value is %d but actual was %d", expectedGeneratorValue, actualGeneratorValue));
    }

    private static Thread[] createThreads(Runnable runnable, int amountOfThreads) {
        return IntStream.range(0, amountOfThreads)
                .mapToObj(i -> new Thread(runnable))
                .toArray(Thread[]::new);
    }

    private static void startThreads(Thread[] threads) {
        Arrays.stream(threads).forEach(Thread::start);
    }

    private static void waitUntilFinish(Thread[] threads) {
        Arrays.stream(threads).forEach(GeneratorRunner::waitUntilFinish);
    }

    @SneakyThrows
    private static void waitUntilFinish(Thread thread) {
        thread.join();
    }
}
