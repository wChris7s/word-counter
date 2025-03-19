package util;

import java.util.concurrent.TimeUnit;

public class TimeMeter {
  private long startTime;
  private long endTime;

  public TimeMeter() {
    this.startTime = 0L;
    this.endTime = 0L;
  }

  public void start() {
    this.startTime = System.currentTimeMillis();
  }

  public void stop() {
    this.endTime = System.currentTimeMillis();
  }

  public String elapsedTime() {
    long elapsedTime = this.endTime - this.startTime;
    long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime);
    return seconds + "sec.";
  }
}
