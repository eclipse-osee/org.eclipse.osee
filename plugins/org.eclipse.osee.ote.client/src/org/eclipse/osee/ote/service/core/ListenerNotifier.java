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
package org.eclipse.osee.ote.service.core;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.service.Activator;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.service.ITestConnectionListener;

/**
 * @author Ken J. Aguilar
 */
class ListenerNotifier {
   private final ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactory() {
      private int count = 0;
      @Override
      public Thread newThread(Runnable r) {
         Thread th = new Thread(r);
         th.setName(String.format("OTE listener notifier [%d]", count++));
         return th;
      }
   });
   private final CopyOnWriteArraySet<ITestConnectionListener> testConnectionListeners =
      new CopyOnWriteArraySet<ITestConnectionListener>();

   boolean addTestConnectionListener(ITestConnectionListener listener) {
      return testConnectionListeners.add(listener);
   }

   boolean removeTestConnectionListener(ITestConnectionListener listener) {
      return testConnectionListeners.remove(listener);
   }

   void notifyPostConnection(final ConnectionEvent event) {
      executor.submit(new Runnable() {

         @Override
         public void run() {
            for (ITestConnectionListener listener : testConnectionListeners) {
               try {
                  listener.onPostConnect(event);
               } catch (Throwable ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, "exception notifying listener of post connect event", ex);
               }
            }
         }

      });
   }

   void notifyDisconnect(final ConnectionEvent event) {
      for (ITestConnectionListener listener : testConnectionListeners) {
         try {
            listener.onPreDisconnect(event);
         } catch (Throwable ex) {
            OseeLog.log(Activator.class, Level.SEVERE, "exception notifying listener of disconnect event", ex);
         }
      }
   }

   void notifyConnectionLost(final IServiceConnector connector) {
      executor.submit(new Runnable() {

         @Override
         public void run() {
            for (ITestConnectionListener listener : testConnectionListeners) {
               try {
                  listener.onConnectionLost(connector);
               } catch (Throwable ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, "exception notifying listener of connection error event",
                     ex);
               }
            }
         }
      });

   }

}
