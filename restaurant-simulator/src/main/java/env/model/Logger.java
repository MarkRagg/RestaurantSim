package env.model;

import java.util.ArrayList;
import java.util.List;

public class Logger {
  private final List<String> logs;
  private boolean newLog;
  
  public Logger() {
    this.logs = new ArrayList<>();
    newLog = false;
  }

  public List<String> getAllLogs() {
    synchronized (this) {
      return List.copyOf(this.logs);
    }
  }

  public void appendLog(String log) {
    synchronized (this) {
      this.logs.add(log + "\n");
      this.newLog = true;
    }
  }

  public boolean thereIsNewLog() {
    synchronized (this) {
      return this.newLog;
    }
  }

  public String getLastLog() {
    synchronized (this) {
      this.newLog = false;
      return this.logs.get(this.logs.size() - 1);
    }
  }
}
