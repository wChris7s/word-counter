package writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InvertedIndexResultWriter implements ResultWriter {
  private final Map<String, ConcurrentLinkedQueue<String>> result;

  public InvertedIndexResultWriter(Map<String, ConcurrentLinkedQueue<String>> result) {
    this.result = result;
  }

  @Override
  public void store(String outPath, String elapsedTime) {
    System.out.println("Storing results ...");
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outPath))) {
      writer.write(elapsedTime);
      writer.newLine();
      result.forEach((key, value) -> writeResult(key, value, writer));
      System.out.println("Results saved.");
    } catch (IOException e) {
      e.printStackTrace(System.out);
    }
  }

  private void writeResult(String s, ConcurrentLinkedQueue<String> value, BufferedWriter writer) {
    try {
      StringBuilder sb = new StringBuilder(s).append(" - (");
      boolean first = true;
      for (String v : value) {
        if (!first) {
          sb.append(",");
        }
        sb.append(v);
        first = false;
      }
      sb.append(")");
      writer.write(sb.toString());
      writer.newLine();
    } catch (IOException e) {
      e.printStackTrace(System.out);
    }
  }
}
