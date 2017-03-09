/*
 * Copyright 2015 Elvis Hew
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ldy.xelog.jsonFile;

import com.elvishew.xlog.flattener.Flattener;
import com.elvishew.xlog.internal.DefaultsFactory;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.backup.BackupStrategy;
import com.elvishew.xlog.printer.file.naming.FileNameGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Log {@link Printer} using file system. When print a log, it will print it to the specified file.
 * <p>
 * Use the {@link Builder} to construct a {@link JsonFilePrinter} object.
 */
public class JsonFilePrinter implements Printer {

  /**
   * Use worker, write logs asynchronously.
   */
  private static final boolean USE_WORKER = true;

  /**
   * The folder path of log file.
   */
  private final String folderPath;

  /**
   * The file name generator for log file.
   */
  private final FileNameGenerator fileNameGenerator;

  /**
   * The backup strategy for log file.
   */
  private final BackupStrategy backupStrategy;

  /**
   * The log flattener when print a log.
   */
  private Flattener flattener;

  /**
   * Log writer.
   */
  private Writer writer;

  private volatile Worker worker;

  /*package*/ JsonFilePrinter(Builder builder) {
    folderPath = builder.folderPath;
    fileNameGenerator = builder.fileNameGenerator;
    backupStrategy = builder.backupStrategy;
    flattener = builder.flattener;

    writer = new Writer();
    if (USE_WORKER) {
      worker = new Worker();
    }

    checkLogFolder();
  }

  /**
   * Make sure the folder of log file exists.
   */
  private void checkLogFolder() {
    File folder = new File(folderPath);
    if (!folder.exists()) {
      folder.mkdirs();
    }
  }

  @Override
  public void println(int logLevel, String tag, String msg) {
    if (USE_WORKER) {
      if (!worker.isStarted()) {
        worker.start();
      }
      worker.enqueue(new LogItem(logLevel, tag, msg));
    } else {
      doPrintln(logLevel, tag, msg);
    }
  }

  /**
   * Do the real job of writing log to file.
   */
  void doPrintln(int logLevel, String tag, String msg) {
    String lastFileName = writer.getLastFileName();
    if (lastFileName == null || fileNameGenerator.isFileNameChangeable()) {
      String newFileName = fileNameGenerator.generateFileName(logLevel, System.currentTimeMillis());
      if (newFileName == null || newFileName.trim().length() == 0) {
        throw new IllegalArgumentException("File name should not be empty.");
      }
      if (!newFileName.equals(lastFileName)) {
        if (writer.isOpened()) {
          writer.close();
        }
        if (!writer.open(newFileName)) {
          return;
        }
        lastFileName = newFileName;
      }
    }

    File lastFile = writer.getFile();
    if (backupStrategy.shouldBackup(lastFile)) {
      // Backup the log file, and create a new log file.
      writer.close();
      File backupFile = new File(folderPath, lastFileName + ".bak");
      if (backupFile.exists()) {
        backupFile.delete();
      }
      lastFile.renameTo(backupFile);
      if (!writer.open(lastFileName)) {
        return;
      }
    }
    String flattenedLog = flattener.flatten(logLevel, tag, msg).toString();
    writer.appendLog(flattenedLog);
  }

  /**
   * Builder for {@link JsonFilePrinter}.
   */
  public static class Builder {

    /**
     * The folder path of log file.
     */
    String folderPath;

    /**
     * The file name generator for log file.
     */
    FileNameGenerator fileNameGenerator;

    /**
     * The backup strategy for log file.
     */
    BackupStrategy backupStrategy;

    /**
     * The log flattener when print a log.
     */
    Flattener flattener;

    /**
     * Construct a builder.
     *
     * @param folderPath the folder path of log file
     */
    public Builder(String folderPath) {
      this.folderPath = folderPath;
    }

    /**
     * Set the file name generator for log file.
     *
     * @param fileNameGenerator the file name generator for log file
     * @return the builder
     */
    public Builder fileNameGenerator(FileNameGenerator fileNameGenerator) {
      this.fileNameGenerator = fileNameGenerator;
      return this;
    }

    /**
     * Set the backup strategy for log file.
     *
     * @param backupStrategy the backup strategy for log file
     * @return the builder
     */
    public Builder backupStrategy(BackupStrategy backupStrategy) {
      this.backupStrategy = backupStrategy;
      return this;
    }

    /**
     * Set the log flattener when print a log.
     *
     * @param flattener the log flattener when print a log
     * @return the builder
     */
    public Builder logFlattener(Flattener flattener) {
      this.flattener = flattener;
      return this;
    }

    /**
     * Build configured {@link JsonFilePrinter} object.
     *
     * @return the built configured {@link JsonFilePrinter} object
     */
    public JsonFilePrinter build() {
      fillEmptyFields();
      return new JsonFilePrinter(this);
    }

    private void fillEmptyFields() {
      if (fileNameGenerator == null) {
        fileNameGenerator = DefaultsFactory.createFileNameGenerator();
      }
      if (backupStrategy == null) {
        backupStrategy = DefaultsFactory.createBackupStrategy();
      }
      if (flattener == null) {
        flattener = DefaultsFactory.createFlattener();
      }
    }
  }

  private class LogItem {

    int level;
    String tag;
    String msg;

    LogItem(int level, String tag, String msg) {
      this.level = level;
      this.tag = tag;
      this.msg = msg;
    }
  }

  /**
   * Work in background, we can enqueue the logs, and the worker will dispatch them.
   */
  private class Worker implements Runnable {

    private BlockingQueue<LogItem> logs = new LinkedBlockingQueue<>();

    private volatile boolean started;

    /**
     * Enqueue the log.
     *
     * @param log the log to be written to file
     */
    void enqueue(LogItem log) {
      try {
        logs.put(log);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    /**
     * Whether the worker is started.
     *
     * @return true if started, false otherwise
     */
    boolean isStarted() {
      synchronized (this) {
        return started;
      }
    }

    /**
     * Start the worker.
     */
    void start() {
      synchronized (this) {
        new Thread(this).start();
        started = true;
      }
    }

    @Override
    public void run() {
      LogItem log;
      try {
        while ((log = logs.take()) != null) {
          doPrintln(log.level, log.tag, log.msg);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
        synchronized (this) {
          started = false;
        }
      }
    }
  }

  /**
   * Used to write the flattened logs to the log file.
   */
  private class Writer {

    /**
     * The file name of last used log file.
     */
    private String lastFileName;

    /**
     * The current log file.
     */
    private File logFile;

    private BufferedWriter bufferedWriter;

    /**
     * Whether the log file is opened.
     *
     * @return true if opened, false otherwise
     */
    boolean isOpened() {
      return bufferedWriter != null;
    }

    /**
     * Get the name of last used log file.
     * @return the name of last used log file, maybe null
     */
    String getLastFileName() {
      return lastFileName;
    }

    /**
     * Get the current log file.
     *
     * @return the current log file, maybe null
     */
    File getFile() {
      return logFile;
    }

    /**
     * Open the file of specific name to be written into.
     *
     * @param newFileName the specific file name
     * @return true if opened successfully, false otherwise
     */
    boolean open(String newFileName) {
      lastFileName = newFileName;
      logFile = new File(folderPath, newFileName);

      // Create log file if not exists.
      if (!logFile.exists()) {
        try {
          File parent = logFile.getParentFile();
          if (!parent.exists()) {
            parent.mkdirs();
          }
          logFile.createNewFile();
        } catch (IOException e) {
          e.printStackTrace();
          lastFileName = null;
          logFile = null;
          return false;
        }
      }

      // Create buffered writer.
      try {
        bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
      } catch (Exception e) {
        e.printStackTrace();
        lastFileName = null;
        logFile = null;
        return false;
      }
      return true;
    }

    /**
     * Close the current log file if it is opened.
     *
     * @return true if closed successfully, false otherwise
     */
    boolean close() {
      if (bufferedWriter != null) {
        try {
          bufferedWriter.close();
        } catch (IOException e) {
          e.printStackTrace();
          return false;
        } finally {
          bufferedWriter = null;
          lastFileName = null;
          logFile = null;
        }
      }
      return true;
    }

    /**
     * Append the flattened log to the end of current opened log file.
     *
     * @param flattenedLog the flattened log
     */
    void appendLog(String flattenedLog) {
      try {
        //// TODO: 2017/3/6 应该是最后一行添加"]",再写时删除,这样直接产出的文件就是json格式的
        bufferedWriter.write(flattenedLog+",");
        bufferedWriter.newLine();
        bufferedWriter.flush();
      } catch (IOException e) {
      }
    }
  }
}
