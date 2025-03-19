import task.ProcessingTask;
import task.ReadingTask;
import util.TimeMeter;
import writer.ResultWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class WordCounter {
  private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>(1000);
  private static final ConcurrentMap<String, Long> wordCounts = new ConcurrentHashMap<>();
  private static final AtomicBoolean isReading = new AtomicBoolean(true);
  private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
  private static final int GB_SIZE = 1;

  public static void main(String[] args) {
    System.out.println(NUM_THREADS);
    File file = new File(String.format("random_%dGB.txt", GB_SIZE));
    TimeMeter timeMeter = new TimeMeter();

    ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS + 1);
    CompletionService<Void> completionService = new ExecutorCompletionService<>(executor);
    List<Future<Void>> futures = new ArrayList<>();

    timeMeter.start();
    futures.add(completionService.submit(() -> new ReadingTask(file, queue, isReading).call()));

    for (int i = 0; i < NUM_THREADS; i++) {
      futures.add(completionService.submit(() -> new ProcessingTask(queue, isReading, wordCounts).call()));
    }

    for (Future<Void> future : futures) {
      try {
        future.get();
      } catch (Exception e) {
        e.printStackTrace(System.out);
      }
    }
    executor.shutdown();

    timeMeter.stop();
    ResultWriter resultWriter = new ResultWriter(wordCounts);
    resultWriter.storeResult(String.format("result_%dGB.txt", GB_SIZE), timeMeter.elapsedTime());
    System.out.println(timeMeter.elapsedTime());
  }
}