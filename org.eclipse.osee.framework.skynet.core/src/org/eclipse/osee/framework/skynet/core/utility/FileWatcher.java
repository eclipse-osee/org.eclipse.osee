/*
 * Created on Jun 17, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.utility;

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
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;

/**
 * @author Ken J. Aguilar
 */
public class FileWatcher extends TimerTask {
   private final long interval;
   private final Timer timer = new Timer();

   private final HashMap<File, Long> filesToWatch = new HashMap<File, Long>(128);
   private final HashSet<IFileWatcherListener> listeners = new HashSet<IFileWatcherListener>();

   public FileWatcher(long time, TimeUnit unit) {
      interval = unit.toMillis(time);
   }

   /**
    * Starts the file watcher monitoring of the file system
    */
   public void start() {
      timer.schedule(this, interval, interval);
   }

   /**
    * adds a {@link File} to the files to be monitored. This method can be called before or after the {@link #start()}
    * method is called.
    * 
    * @param file
    */
   public synchronized void addFile(File file) {
      filesToWatch.put(file, file.lastModified());
   }

   /**
    * removes a {@link File} from the set of files to be monitored. This method can be called before or after the
    * {@link #start()} method is called.
    * 
    * @param file
    * @return returns the last know timestamp of the file before it was removed or null if it was never being monitored
    *         in the first place
    */
   public synchronized Long removeFile(File file) {
      return filesToWatch.remove(file);
   }

   /**
    * registers a listener who will be notified of file change events. This method can be called before or after the
    * {@link #start()} method is called.
    * 
    * @param listener
    */
   public synchronized void addListener(IFileWatcherListener listener) {
      listeners.add(listener);
   }

   /**
    * unregisters a listener from receiving file change events. This method can be called before or after the
    * {@link #start()} method is called.
    * 
    * @param listener
    */
   public synchronized void removeListener(IFileWatcherListener listener) {
      listeners.remove(listener);
   }

   /* (non-Javadoc)
    * @see java.util.TimerTask#run()
    */
   @Override
   public synchronized void run() {
      try {
         LinkedList<FileChangeEvent> fileChangeEvents = new LinkedList<FileChangeEvent>();
         for (Map.Entry<File, Long> entry : filesToWatch.entrySet()) {
            Long latestLastModified = entry.getKey().lastModified();
            Long storedLastModified = entry.getValue();
            if (!storedLastModified.equals(latestLastModified)) {
               entry.setValue(latestLastModified);
               if (storedLastModified == 0) {
                  // created
                  assert entry.getKey().exists() : "file doesn't exist";
                  fileChangeEvents.add(new FileChangeEvent(entry.getKey(), FileChangeType.CREATED));
               } else if (latestLastModified == 0) {
                  // deleted
                  assert !entry.getKey().exists() : "file still exist";
                  fileChangeEvents.add(new FileChangeEvent(entry.getKey(), FileChangeType.DELETED));
               } else {
                  // modified
                  assert entry.getKey().exists() : "file doesn't exist";
                  fileChangeEvents.add(new FileChangeEvent(entry.getKey(), FileChangeType.MODIFIED));
               }

            }
         }
         if (!fileChangeEvents.isEmpty()) {
            // there is at least one file change event, notify listeners
            for (IFileWatcherListener listener : listeners) {
               listener.filesModified(fileChangeEvents);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }
   }

   public static void main(String[] args) {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      FileWatcher watcher = new FileWatcher(2, TimeUnit.SECONDS);
      watcher.addListener(new IFileWatcherListener() {

         @Override
         public void filesModified(Collection<FileChangeEvent> events) {
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
