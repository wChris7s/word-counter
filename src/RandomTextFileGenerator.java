import java.io.*;
import java.util.Random;

public class RandomTextFileGenerator {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final int WORD_LENGTH = 5;
    private static final int WORDS_PER_LINE = 20;

    public static void generateRandomTextFile(String filePath, int sizeInGB) throws IOException {
        long sizeInBytes = (long) sizeInGB * 1024 * 1024 * 1024;
        Random random = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            long writtenBytes = 0;
            while (writtenBytes < sizeInBytes) {
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < WORDS_PER_LINE; i++) {
                    StringBuilder word = new StringBuilder(WORD_LENGTH);
                    for (int j = 0; j < WORD_LENGTH; j++) {
                        word.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
                    }
                    line.append(word).append(" ");
                }
                writer.write(line.toString().trim());
                writer.newLine();
                writtenBytes += line.length() + System.lineSeparator().length();
            }
        }
    }

    public static void main(String[] args) {
        try {
              generateRandomTextFile("random_20GB.txt", 20);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}