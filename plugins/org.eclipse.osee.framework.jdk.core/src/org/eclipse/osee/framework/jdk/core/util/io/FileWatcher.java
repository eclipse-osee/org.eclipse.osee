/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.util.io;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

/**
 * @author Ken J. Aguilar
 */
public final class FileWatcher {
   private final Timer timer = new Timer();
   private final Map<File, Long> filesToWatch = new ConcurrentHashMap<>(128);
   private final Set<IFileWatcherListener> listeners = new CopyOnWriteArraySet<>();
   private final long interval;

   public FileWatcher(long time, TimeUnit unit) {
      interval = unit.toMillis(time);
   }

   public void start() {
      timer.schedule(new FileWatcherTimerTask(filesToWatch, listeners), interval, interval);
   }

   public void stop() {
      timer.cancel();
      listeners.clear();
      filesToWatch.clear();
   }

   /**
    * adds a {@link File} to the files to be monitored. This method can be called before or after the {@link #start()}
    * method is called.
    */
   public void addFile(File file) {
      filesToWatch.put(file, file.lastModified());
   }

   /**
    * removes a {@link File} from the set of files to be monitored. This method can be called before or after the
    * {@link #start()} method is called.
    * 
    * @return the last know timestamp of the file before it was removed or null if it was never being monitored in the
    * first place
    */
   public Long removeFile(File file) {
      return filesToWatch.remove(file);
   }

   /**
    * registers a listener who will be notified of file change events. This method can be called before or after the
    * {@link #start()} method is called.
    */
   public void addListener(IFileWatcherListener listener) {
      listeners.add(listener);
   }

   /**
    * unregisters a listener from receiving file change events. This method can be called before or after the
    * {@link #start()} method is called.
    */
   public void removeListener(IFileWatcherListener listener) {
      listeners.remove(listener);
   }
}
