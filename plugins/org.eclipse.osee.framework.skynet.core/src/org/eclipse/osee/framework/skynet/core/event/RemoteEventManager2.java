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
package org.eclipse.osee.framework.skynet.core.event;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.IFrameworkEvent;
import org.eclipse.osee.framework.messaging.event.res.IFrameworkEventListener;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * Manages remote events from the SkynetEventService.
 * 
 * @author Donald G Dunne
 */
public class RemoteEventManager2 {
   private static final RemoteEventManager2 instance = new RemoteEventManager2();
   private final IFrameworkEventListener clientEventListener;

   private RemoteEventManager2() {
      super();
      clientEventListener = new IFrameworkEventListener() {

         private static final long serialVersionUID = 1L;

         @Override
         public void onEvent(IFrameworkEvent[] events) throws RemoteException {
            System.err.println("process incoming events");
         }
      };
   }

   public static void deregisterFromRemoteEventManager() {
   }

   public static void kick(Collection<IFrameworkEvent> events) {
      kick(events.toArray(new IFrameworkEvent[events.size()]));
   }

   public static boolean isConnected() {
      return false;
   }

   public static void kick(final IFrameworkEvent... events) {
      if (isConnected()) {
         Job job = new Job("Send Event2") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
               System.err.println("Do Work here");
               return Status.OK_STATUS;
            }
         };

         job.schedule();
      }
      /*
       * This will enable a testing loopback that will take the kicked remote events and
       * loop them back as if they came from an external client. It will allow for the testing
       * of the OEM -> REM -> OEM processing. In addition, this onEvent is put in a non-display
       * thread which will test that all handling by applications is properly handled by doing
       * all processing and then kicking off display-thread when need to update ui. SessionId needs
       * to be modified so this client doesn't think the events came from itself.
       */
      if (InternalEventManager.enableRemoteEventLoopback) {
         OseeLog.log(Activator.class, Level.INFO, "REM: Loopback enabled - Returning events as Remote event.");
         Thread thread = new Thread() {
            @Override
            public void run() {
               try {
                  String newSessionId = GUID.create();
                  for (IFrameworkEvent event : events) {
                     event.getNetworkSender().setSessionId(newSessionId);
                  }
                  instance.clientEventListener.onEvent(events);
               } catch (RemoteException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);

               }
            }
         };
         thread.start();
      }
   }

}
