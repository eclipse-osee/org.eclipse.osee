/*******************************************************************************
 * Copyright (c) 2010 Boeing.
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
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

/**
 * @author Ken J. Aguilar
 * @author Ryan D. Brooks
 */
final class FileWatcherTimerTask extends TimerTask {
   private final Map<File, Long> filesToWatch;
   private final Set<IFileWatcherListener> listeners;

   public FileWatcherTimerTask(Map<File, Long> filesToWatch, Set<IFileWatcherListener> listeners) {
      super();
      this.filesToWatch = filesToWatch;
      this.listeners = listeners;
   }

   @Override
   public void run() {
      try {
         LinkedList<FileChangeEvent> fileChangeEvents = new LinkedList<>();
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
         for (IFileWatcherListener listener : listeners) {
            listener.handleException(ex);
         }
      }
   }
}
