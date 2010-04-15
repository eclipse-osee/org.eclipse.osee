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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.IFrameworkEventListener;
import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;
import org.eclipse.osee.framework.messaging.event.res.ResEventManager;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidArtifact1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteChangeTypeArtifactsEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemotePurgedArtifactsEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTransactionEvent1;
import org.eclipse.osee.framework.skynet.core.event.msgs.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event2.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventModType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * Manages remote events from the SkynetEventService.
 * 
 * @author Donald G Dunne
 */
public class RemoteEventManager2 implements IFrameworkEventListener {
   private static final RemoteEventManager2 instance = new RemoteEventManager2();
   private static final boolean enabled = false;

   private RemoteEventManager2() {
      super();
   }

   public static RemoteEventManager2 getInstance() {
      return instance;
   }

   @Override
   public void onEvent(final RemoteEvent remoteEvent) throws RemoteException {
      Job job = new Job("Receive Event2") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {

            Sender sender = null;
            try {
               sender = new Sender(remoteEvent.getNetworkSender());
               // If the sender's sessionId is the same as this client, then this event was
               // created in this client and returned by remote event manager; ignore and continue
               if (sender.isLocal()) {
                  return Status.OK_STATUS;
               }
            } catch (OseeAuthenticationRequiredException ex1) {
               OseeLog.log(Activator.class, Level.SEVERE, ex1);
               new Status(Status.ERROR, Activator.PLUGIN_ID, -1, ex1.getLocalizedMessage(), ex1);
            }

            if (remoteEvent instanceof RemoteTransactionEvent1) {
               try {
                  RemoteTransactionEvent1 event1 = (RemoteTransactionEvent1) remoteEvent;
                  TransactionEvent transEvent = FrameworkEventUtil.getTransactionEvent(event1);
                  InternalEventManager2.kickTransactionEvent(sender, transEvent);
                  // TODO process transaction event by updating artifact/relation caches
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            } else if (remoteEvent instanceof RemotePurgedArtifactsEvent1) {
               try {
                  RemotePurgedArtifactsEvent1 event1 = (RemotePurgedArtifactsEvent1) remoteEvent;
                  Set<EventBasicGuidArtifact> artifactChanges = new HashSet<EventBasicGuidArtifact>();
                  for (RemoteBasicGuidArtifact1 guidArt : event1.getArtifacts()) {
                     artifactChanges.add(new EventBasicGuidArtifact(EventModType.Purged,
                           FrameworkEventUtil.getBasicGuidArtifact(guidArt)));
                  }
                  // TODO process purge event by updating artifact/relation caches
                  InternalEventManager2.kickArtifactsPurgedEvent(sender, artifactChanges);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            } else if (remoteEvent instanceof RemoteChangeTypeArtifactsEvent1) {
               try {
                  RemoteChangeTypeArtifactsEvent1 event1 = (RemoteChangeTypeArtifactsEvent1) remoteEvent;
                  Set<EventBasicGuidArtifact> artifactChanges = new HashSet<EventBasicGuidArtifact>();
                  for (RemoteBasicGuidArtifact1 guidArt : event1.getArtifacts()) {
                     artifactChanges.add(new EventBasicGuidArtifact(EventModType.Purged,
                           FrameworkEventUtil.getBasicGuidArtifact(guidArt)));
                  }
                  // TODO process change type event by updating artifact/relation caches
                  InternalEventManager2.kickArtifactsChangeTypeEvent(sender, artifactChanges, event1.getToArtTypeGuid());
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
            return Status.OK_STATUS;
         }
      };
      job.setSystem(true);
      job.setUser(false);
      job.schedule();
   }

   public void deregisterForRemoteEvents() throws OseeCoreException {
      ResEventManager.getInstance().stop();
   }

   public void registerForRemoteEvents() throws OseeCoreException {
      if (!enabled) {
         OseeLog.log(Activator.class, Level.INFO, "REM2 Disabled");
      } else {
         ResEventManager.getInstance().start(this);
      }
   }

   public boolean isConnected() {
      return enabled && ResEventManager.getInstance().isConnected();
   }

   /**
    * InternalEventManager.enableRemoteEventLoopback will enable a testing loopback that will take the kicked remote
    * events and loop them back as if they came from an external client. It will allow for the testing of the OEM -> REM
    * -> OEM processing. In addition, this onEvent is put in a non-display thread which will test that all handling by
    * applications is properly handled by doing all processing and then kicking off display-thread when need to update
    * ui. SessionId needs to be modified so this client doesn't think the events came from itself.
    */
   public void kick(final RemoteEvent remoteEvent) {
      if (enabled && isConnected()) {
         Job job = new Job("Send Event2") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
               try {
                  ResEventManager.getInstance().kick(remoteEvent);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
                  return new Status(Status.ERROR, Activator.PLUGIN_ID, -1, ex.getLocalizedMessage(), ex);
               }
               return Status.OK_STATUS;
            }
         };

         job.schedule();
      }

      if (InternalEventManager2.isEnableRemoteEventLoopback()) {
         OseeLog.log(Activator.class, Level.INFO, "REM2: Loopback enabled - Returning events as Remote event.");
         Thread thread = new Thread() {
            @Override
            public void run() {
               try {
                  String newSessionId = GUID.create();
                  remoteEvent.getNetworkSender().setSessionId(newSessionId);
                  instance.onEvent(remoteEvent);
               } catch (RemoteException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);

               }
            }
         };
         thread.start();
      }
   }
}
