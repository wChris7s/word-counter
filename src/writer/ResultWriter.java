package writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

public class ResultWriter {
  private final ConcurrentMap<String, Long> wordCounts;

  public ResultWriter(ConcurrentMap<String, Long> wordCounts) {
    this.wordCounts = wordCounts;
  }

  public void storeResult(String filePath, String elapsedTime) {
    System.out.println("Storing results...");
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
      writer.write(elapsedTime);
      writer.newLine();
      wordCounts.forEach((key, count) -> writeResult(key, count, writer));
    } catch (IOException e) {
      e.printStackTrace(System.out);
    }
  }

  private void writeResult(String s, Long aLong, BufferedWriter writer) {
    try {
      writer.write(String.format("%s - %d", s, aLong));
      writer.newLine();
    } catch (IOException e) {
      e.printStackTrace(System.out);
    }
  }
}