package task;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessingTask implements Callable<Void> {
  private final BlockingQueue<String> queue;
  private final AtomicBoolean isReading;
  private final ConcurrentMap<String, LongAdder> wordCounts;
  private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+");

  public ProcessingTask(BlockingQueue<String> queue, AtomicBoolean isReading, ConcurrentMap<String, LongAdder> wordCounts) {
    this.queue = queue;
    this.isReading = isReading;
    this.wordCounts = wordCounts;
  }

  @Override
  public Void call() {
    try {
      while (isReading.get() || !queue.isEmpty()) {
        String line = queue.poll(10, TimeUnit.MILLISECONDS); // Availability
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
    Arrays.stream(SPACE_PATTERN.split(line.trim()))
      .map(String::toLowerCase)
      .forEach(word -> wordCounts.computeIfAbsent(word, k -> new LongAdder()).increment());
  }
}