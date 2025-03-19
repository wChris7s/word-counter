package task;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentMap;

public class ProcessingTask implements Callable<Void> {
  private final BlockingQueue<String> queue;
  private final AtomicBoolean isReading;
  private final ConcurrentMap<String, Long> wordCounts;

  public ProcessingTask(BlockingQueue<String> queue, AtomicBoolean isReading, ConcurrentMap<String, Long> wordCounts) {
    this.queue = queue;
    this.isReading = isReading;
    this.wordCounts = wordCounts;
  }

  @Override
  public Void call() {
    try {
      while (isReading.get() || !queue.isEmpty()) {
        String line = queue.poll(100, TimeUnit.MILLISECONDS); // Availability
        if (line != null) {
          processLine(line);
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    return null;
  }

  private void processLine(String line) {
    List<String> words = Arrays.asList(line.trim().split("\\s+"));
    words.forEach(word -> wordCounts.merge(word.toLowerCase(), 1L, Long::sum));
  }
}