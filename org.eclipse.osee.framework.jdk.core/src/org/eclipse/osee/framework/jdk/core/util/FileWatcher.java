/*
 * Created on Jun 17, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.jdk.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author b1529404
 */
public class FileWatcher extends TimerTask {
   private final long interval;
   private final Timer timer = new Timer();

   private final HashMap<File, Long> filesToWatch = new HashMap<File, Long>(128);
   private final HashSet<IFileWatcherListener> listeners = new HashSet<IFileWatcherListener>();

   public FileWatcher(long time, TimeUnit unit) {
      interval = unit.toMillis(time);
   }

   public void start() {
      timer.schedule(this, interval, interval);
   }

   public synchronized Long addFile(File file) {
      return filesToWatch.put(file, file.lastModified());
   }

   public synchronized Long removeFile(File file) {
      return filesToWatch.remove(file);
   }

   public synchronized void addListener(IFileWatcherListener listener) {
      listeners.add(listener);
   }

   public synchronized void removeListener(IFileWatcherListener listener) {
      listeners.remove(listener);
   }

   /* (non-Javadoc)
    * @see java.util.TimerTask#run()
    */
   @Override
   public synchronized void run() {
      try {
         LinkedList<FileChangeEvent> changedFiles = new LinkedList<FileChangeEvent>();
         for (Map.Entry<File, Long> entry : filesToWatch.entrySet()) {
            Long latestLastModified = entry.getKey().lastModified();
            Long storedLastModifiedValue = entry.getValue();
            if (!storedLastModifiedValue.equals(latestLastModified)) {
               entry.setValue(latestLastModified);
               if (storedLastModifiedValue == 0) {
                  // created
                  changedFiles.add(new FileChangeEvent(entry.getKey(), FileChangeType.CREATED));
               } else if (latestLastModified == 0) {
                  // deleted
                  changedFiles.add(new FileChangeEvent(entry.getKey(), FileChangeType.DELETED));
               } else {
                  // modified
                  changedFiles.add(new FileChangeEvent(entry.getKey(), FileChangeType.MODIFIED));
               }

            }
         }
         if (!changedFiles.isEmpty()) {
            for (IFileWatcherListener listener : listeners) {
               listener.filesModified(changedFiles);
            }
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   public static void main(String[] args) {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      FileWatcher watcher = new FileWatcher(5, TimeUnit.SECONDS);
      watcher.addListener(new IFileWatcherListener() {

         @Override
         public void filesModified(Collection<FileChangeEvent> events) {
            System.out.println("\nchanges detected in:");
            for (FileChangeEvent event : events) {
               System.out.println(event.getChangeType().name() + ": " + event.getFile().getAbsolutePath());
            }
         }

      });
      File directory = new File("C:\\Documents and Settings\\b1529404\\Desktop\\watcher_test");
      watcher.addFile(new File(directory, "f1.txt"));
      watcher.addFile(new File(directory, "f2.txt"));
      watcher.addFile(new File(directory, "f3.txt"));
      watcher.start();
      try {
         while (!reader.readLine().equals("QUIT")) {

         }
      } catch (IOException ex) {
         ex.printStackTrace();
      }
      watcher.dispose();
   }

   public void dispose() {
      timer.cancel();
      listeners.clear();
      filesToWatch.clear();
   }

}
