package task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReadingTask implements Callable<Void> {
  private final File file;
  private final BlockingQueue<String> queue;
  private final AtomicBoolean isReading;

  public ReadingTask(File file, BlockingQueue<String> queue, AtomicBoolean isReading) {
    this.file = file;
    this.queue = queue;
    this.isReading = isReading;
  }

  @Override
  public Void call() {
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = br.readLine()) != null) {
        queue.put(line);
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace(System.out);
    } finally {
      isReading.set(false);
    }
    return null;
  }
}
