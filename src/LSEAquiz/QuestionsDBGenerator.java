/**
 * @author Michał Bereza
 * @version 0.3
 * @since 09.04.2020
 */

package LSEAquiz;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * The class QuestionsDBGenerator. Its main role is to generate big sets of random questions. Generated questions have
 * correct and meaningful categories from enum Category, but questions and answers are random strings.
 */
public class QuestionsDBGenerator{

    /**
     * Generates set of questions and writes them to the textfile.
     *
     * @param n Number of threads involved
     * @param k Number of questions to generate
     * @param path Path to the file
     * @throws ExecutionException   the execution exception
     * @throws InterruptedException the interrupted exception
     */
    public void generate(int n, int k, String path) throws ExecutionException, InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(n); //creating fixed pool of threads
        List<Callable<String>> callables = new ArrayList<>();
        for (int i = 0; i < k; i++)
            callables.add(new StringGenerator(path)); //adding tasks to the list
        List<Future<String>> result = executor.invokeAll(callables); //executing tasks
    }

    /**
     * Method used to test time of executing tasks.
     *
     * @param n Number of threads involved
     * @param k Number of questions to generate
     * @return Time needed to complete the task (in milliseconds)
     * @throws ExecutionException   the execution exception
     * @throws InterruptedException the interrupted exception
     */
    public long testGenerate(int n, int k) throws ExecutionException, InterruptedException {
        long start_time = System.currentTimeMillis();
        generate(n,k, "testQuestions");
        long end_time = System.currentTimeMillis();
        return (end_time-start_time);
    }

    /**
     * Full test of multithreading.
     *
     * @param maxNumberOfThreads The max number of threads
     * @param writings           Number of questions to generate in each iteration
     * @param iterations         Number of iterations
     * @throws ExecutionException   the execution exception
     * @throws InterruptedException the interrupted exception
     */
    public void test(int maxNumberOfThreads, int writings, int iterations) throws ExecutionException, InterruptedException {
        QuestionsDBGenerator generator = new QuestionsDBGenerator();
        long[] results = new long[maxNumberOfThreads];
        for (int i = 0; i < maxNumberOfThreads; i++) {
            long sum = 0;
            for (int j = 0; j < iterations; j++)
                sum += generator.testGenerate(i + 1, writings);
            results[i] = sum / iterations; //Calculates the average time of completion for each number of thrreads
            System.out.print("Liczba watkow: " + (i + 1) + "; Sredni czas wykonania zadania: " + results[i] + " milisekund.\n");
        }
    }
}
