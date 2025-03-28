package writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;

public class WordCounterResultWriter implements ResultWriter {
  private final ConcurrentMap<String, LongAdder> wordCounts;

  public WordCounterResultWriter(ConcurrentMap<String, LongAdder> wordCounts) {
    this.wordCounts = wordCounts;
  }

  public void store(String outPath, String elapsedTime) {
    System.out.println("Storing results...");
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outPath))) {
      writer.write(elapsedTime);
      writer.newLine();
      wordCounts.forEach((key, count) -> writeResult(key, count, writer));
      System.out.println("Results saved.");
    } catch (IOException e) {
      e.printStackTrace(System.out);
    }
  }

  private void writeResult(String s, LongAdder aLong, BufferedWriter writer) {
    try {
      writer.write(String.format("%s - %d", s, aLong.longValue()));
      writer.newLine();
    } catch (IOException e) {
      e.printStackTrace(System.out);
    }
  }
}