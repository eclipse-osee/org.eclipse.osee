/*******************************************************************************
 * Copyright (c) 2014 Boeing.
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
import java.net.URI;
import java.net.URLConnection;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Roberto E. Escobar
 */
public class UriWatcher {

   public interface UriWatcherListener {
      void modificationDateChanged(Collection<URI> uris);

      void handleException(Exception ex);
   }

   private final Timer timer = new Timer();
   private final Map<URI, Long> urisToWatch = new ConcurrentHashMap<>(128);
   private final Set<UriWatcherListener> listeners = new CopyOnWriteArraySet<>();
   private final long interval;

   public UriWatcher(long time, TimeUnit unit) {
      interval = unit.toMillis(time);
   }

   public void start() {
      timer.schedule(new UriWatcherTimerTask(), interval, interval);
   }

   public void stop() {
      timer.cancel();
      listeners.clear();
      urisToWatch.clear();
   }

   /**
    * adds a {@link File} to the files to be monitored. This method can be called before or after the {@link #start()}
    * method is called.
    */
   public void addUri(URI uri) {
      urisToWatch.put(uri, -1L);
   }

   /**
    * removes a {@link File} from the set of files to be monitored. This method can be called before or after the
    * {@link #start()} method is called.
    * 
    * @return the last know timestamp of the file before it was removed or null if it was never being monitored in the
    * first place
    */
   public Long removeFile(URI uri) {
      return urisToWatch.remove(uri);
   }

   /**
    * registers a listener who will be notified of file change events. This method can be called before or after the
    * {@link #start()} method is called.
    */
   public void addListener(UriWatcherListener listener) {
      listeners.add(listener);
   }

   /**
    * unregisters a listener from receiving file change events. This method can be called before or after the
    * {@link #start()} method is called.
    */
   public void removeListener(UriWatcherListener listener) {
      listeners.remove(listener);
   }

   final class UriWatcherTimerTask extends TimerTask {

      @Override
      public void run() {
         try {
            LinkedList<URI> uris = new LinkedList<>();
            for (Map.Entry<URI, Long> entry : urisToWatch.entrySet()) {
               URLConnection connection = entry.getKey().toURL().openConnection();
               try {
                  Long latestLastModified = connection.getLastModified();
                  Long storedLastModified = entry.getValue();
                  if (!storedLastModified.equals(latestLastModified)) {
                     entry.setValue(latestLastModified);
                     if (storedLastModified != -1) {
                        uris.add(entry.getKey());
                     }
                  }
               } finally {
                  Lib.close(connection.getInputStream());
               }
            }
            if (!uris.isEmpty()) {
               // there is at least one file change event, notify listeners
               for (UriWatcherListener listener : listeners) {
                  listener.modificationDateChanged(uris);
               }
            }
         } catch (Exception ex) {
            for (UriWatcherListener listener : listeners) {
               listener.handleException(ex);
            }
         }
      }
   }
}
