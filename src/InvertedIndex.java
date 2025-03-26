import util.TimeMeter;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class InvertedIndex {
  private final ConcurrentMap<String, Set<String>> index;
  private final ExecutorService executor;
  private final CompletionService<Void> completionService;
  private final Pattern SPACE_PATTERN = Pattern.compile("\\s+");

  public InvertedIndex(int numThreads) {
    this.index = new ConcurrentHashMap<>();
    this.executor = Executors.newFixedThreadPool(numThreads);
    this.completionService = new ExecutorCompletionService<>(executor);
  }

  public void processFiles(List<Path> filePaths) {
    List<Future<Void>> futures = new ArrayList<>();

    for (Path filePath : filePaths) {
      futures.add(completionService.submit(() -> { processFile(filePath); return null; }));
    }

    for (Future<Void> future : futures) {
      try {
        future.get();
      } catch (Exception e) {
        e.printStackTrace(System.out);
      }
    }

    executor.shutdown();
  }

  private void processFile(Path filePath) {
    File file = new File(filePath.toUri());
    try (BufferedReader reader = new BufferedReader(new FileReader(file), 1024 * 1024)) {
      String line;
      while ((line = reader.readLine()) != null) {
        processLine(line, filePath.toString());
      }
    } catch (IOException e) {
      e.printStackTrace(System.out);
    }
  }

  private void processLine(String line, String fileName) {
    Arrays.stream(SPACE_PATTERN.split(line.trim()))
      .map(String::toLowerCase)
      .forEach(word -> index.computeIfAbsent(word, k -> new CopyOnWriteArraySet<>()).add(fileName));
  }

  public static void main(String[] args) {
    TimeMeter timeMeter = new TimeMeter();
    List<Path> files = List.of(
      Paths.get("files/inverted-index-files/random_0_1GB.txt"),
      Paths.get("files/inverted-index-files/random_1_1GB.txt"));

    int availableThreads = Runtime.getRuntime().availableProcessors();
    InvertedIndex invertedIndex = new InvertedIndex(availableThreads);

    System.out.println("Available threads: " + availableThreads);

    timeMeter.start();
    invertedIndex.processFiles(files);
    timeMeter.stop();

    System.out.println("Elapsed time: " + timeMeter.elapsedTime());
  }
}


