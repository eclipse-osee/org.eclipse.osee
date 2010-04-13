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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.IFrameworkEventListener;
import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidArtifact1;
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
public class RemoteEventManager2 {
   private static final RemoteEventManager2 instance = new RemoteEventManager2();
   private final IFrameworkEventListener clientEventListener;

   private RemoteEventManager2() {
      super();
      clientEventListener = new ClientEventListener();
   }

   private static class ClientEventListener implements IFrameworkEventListener {

      private static final long serialVersionUID = 1L;

      @Override
      public void onEvent(final RemoteEvent[] events) throws RemoteException {
         Job job = new Job("Receive Event2") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {

               for (RemoteEvent event : events) {

                  Sender sender = null;
                  try {
                     sender = new Sender(event.getNetworkSender());
                     // If the sender's sessionId is the same as this client, then this event was
                     // created in this client and returned by remote event manager; ignore and continue
                     if (sender.isLocal()) {
                        continue;
                     }
                  } catch (OseeAuthenticationRequiredException ex1) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex1);
                     continue;
                  }

                  if (event instanceof RemoteTransactionEvent1) {
                     try {
                        RemoteTransactionEvent1 event1 = (RemoteTransactionEvent1) event;
                        TransactionEvent transEvent = FrameworkEventUtil.getTransactionEvent(event1);
                        InternalEventManager2.kickTransactionEvent(sender, transEvent);
                        // TODO process transaction event by updating artifact/relation caches
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof RemotePurgedArtifactsEvent1) {
                     try {
                        RemotePurgedArtifactsEvent1 event1 = (RemotePurgedArtifactsEvent1) event;
                        Set<EventBasicGuidArtifact> artifactChanges = new HashSet<EventBasicGuidArtifact>();
                        for (RemoteBasicGuidArtifact1 guidArt : event1.getArtifacts()) {
                           artifactChanges.add(new EventBasicGuidArtifact(EventModType.Purged,
                                 FrameworkEventUtil.getBasicGuidArtifact(guidArt)));
                        }
                        // TODO process transaction event by updating artifact/relation caches
                        InternalEventManager2.kickArtifactsPurgedEvent(sender, artifactChanges);
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  }
                  //                  } else if (event instanceof NetworkArtifactChangeTypeEvent) {
                  //                     try {
                  //                        // TODO do work here to reload change type artifact if loaded
                  //                        InternalEventManager2.kickArtifactsChangeTypeEvent(sender, EventBasicGuidArtifact.get(
                  //                              EventModType.ChangeType,
                  //                              ((NetworkArtifactChangeTypeEvent) event).getDefaultBasicGuidArtifacts()),
                  //                              ((NetworkArtifactChangeTypeEvent) event).getToArtTypeGuid());
                  //                     } catch (Exception ex) {
                  //                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                  //                     }
                  //                  } else if (event instanceof NetworkArtifactPurgeEvent) {
                  //                     try {
                  //                        for (DefaultBasicGuidArtifact guidArt : ((NetworkArtifactPurgeEvent) event).getDefaultBasicGuidArtifacts()) {
                  //                           Artifact artifact = ArtifactCache.getActive(guidArt);
                  //                           if (artifact != null) {
                  //                              //This is because applications may still have a reference to the artifact
                  //                              for (RelationLink link : RelationManager.getRelationsAll(artifact.getArtId(),
                  //                                    artifact.getBranch().getId(), false)) {
                  //                                 link.internalRemoteEventDelete();
                  //                              }
                  //                              ArtifactCache.deCache(artifact);
                  //                              artifact.internalSetDeleted();
                  //                           }
                  //                        }
                  //                        InternalEventManager2.kickArtifactsPurgedEvent(sender, EventBasicGuidArtifact.get(
                  //                              EventModType.Purged, ((NetworkArtifactPurgeEvent) event).getDefaultBasicGuidArtifacts()));
                  //                     } catch (Exception ex) {
                  //                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                  //                     }
                  //                  }
               }
               return Status.OK_STATUS;
            }
         };
         job.setSystem(true);
         job.setUser(false);
         job.schedule();
      }
   };

   public static void deregisterFromRemoteEventManager() {
   }

   public static void kick(RemoteEvent remoteEvent) {
      kick(Collections.singleton(remoteEvent));
   }

   public static void kick(Collection<RemoteEvent> events) {
      kick(events.toArray(new RemoteEvent[events.size()]));
   }

   public static boolean isConnected() {
      // TODO return if connected to event service
      return true;
   }

   /**
    * InternalEventManager.enableRemoteEventLoopback will enable a testing loopback that will take the kicked remote
    * events and loop them back as if they came from an external client. It will allow for the testing of the OEM -> REM
    * -> OEM processing. In addition, this onEvent is put in a non-display thread which will test that all handling by
    * applications is properly handled by doing all processing and then kicking off display-thread when need to update
    * ui. SessionId needs to be modified so this client doesn't think the events came from itself.
    */
   public static void kick(final RemoteEvent... events) {
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

      if (InternalEventManager2.isEnableRemoteEventLoopback()) {
         OseeLog.log(Activator.class, Level.INFO, "REM2: Loopback enabled - Returning events as Remote event.");
         Thread thread = new Thread() {
            @Override
            public void run() {
               try {
                  String newSessionId = GUID.create();
                  for (RemoteEvent event : events) {
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
