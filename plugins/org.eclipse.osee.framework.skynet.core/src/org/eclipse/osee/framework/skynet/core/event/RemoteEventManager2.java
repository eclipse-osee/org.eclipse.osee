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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.data.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.IFrameworkEvent;
import org.eclipse.osee.framework.messaging.event.res.IFrameworkEventListener;
import org.eclipse.osee.framework.messaging.event.res.event.NetworkArtifactAddedEvent;
import org.eclipse.osee.framework.messaging.event.res.event.NetworkArtifactChangeTypeEvent;
import org.eclipse.osee.framework.messaging.event.res.event.NetworkArtifactDeletedEvent;
import org.eclipse.osee.framework.messaging.event.res.event.NetworkArtifactModifiedEvent;
import org.eclipse.osee.framework.messaging.event.res.event.NetworkArtifactPurgeEvent;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.event.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.artifact.EventModType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;

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
      public void onEvent(final IFrameworkEvent[] events) throws RemoteException {
         final List<EventBasicGuidArtifact> artifactChanges = new ArrayList<EventBasicGuidArtifact>();
         Job job = new Job("Receive Event2") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {

               Sender lastArtifactRelationModChangeSender = null;
               for (IFrameworkEvent event : events) {

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

                  if (event instanceof NetworkArtifactAddedEvent) {
                     try {
                        artifactChanges.addAll(EventBasicGuidArtifact.get(EventModType.Added,
                              ((NetworkArtifactAddedEvent) event).getDefaultBasicGuidArtifacts()));
                        lastArtifactRelationModChangeSender = sender;
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkArtifactDeletedEvent) {
                     try {
                        for (DefaultBasicGuidArtifact guidArt : ((NetworkArtifactDeletedEvent) event).getDefaultBasicGuidArtifacts()) {
                           artifactChanges.add(new EventBasicGuidArtifact(EventModType.Deleted, guidArt));
                           Artifact cacheArtifact = ArtifactCache.getActive(guidArt);
                           RemoteEventManager.internalHandleRemoteArtifactDeleted(cacheArtifact);
                        }
                        lastArtifactRelationModChangeSender = sender;
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkArtifactModifiedEvent) {
                     try {
                        for (DefaultBasicGuidArtifact guidArt : ((NetworkArtifactModifiedEvent) event).getDefaultBasicGuidArtifacts()) {
                           artifactChanges.add(new EventBasicGuidArtifact(EventModType.Modified, guidArt));
                           Artifact cacheArtifact = ArtifactCache.getActive(guidArt);
                           RemoteEventManager.internalHandleRemoteArtifactModified(cacheArtifact);
                        }
                        lastArtifactRelationModChangeSender = sender;
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkArtifactChangeTypeEvent) {
                     try {
                        // TODO do work here to reload change type artifact if loaded
                        InternalEventManager2.kickArtifactsChangeTypeEvent(sender, EventBasicGuidArtifact.get(
                              EventModType.ChangeType,
                              ((NetworkArtifactChangeTypeEvent) event).getDefaultBasicGuidArtifacts()),
                              ((NetworkArtifactChangeTypeEvent) event).getToArtTypeGuid());
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkArtifactPurgeEvent) {
                     try {
                        for (DefaultBasicGuidArtifact guidArt : ((NetworkArtifactPurgeEvent) event).getDefaultBasicGuidArtifacts()) {
                           Artifact artifact = ArtifactCache.getActive(guidArt);
                           if (artifact != null) {
                              //This is because applications may still have a reference to the artifact
                              for (RelationLink link : RelationManager.getRelationsAll(artifact.getArtId(),
                                    artifact.getBranch().getId(), false)) {
                                 link.internalRemoteEventDelete();
                              }
                              ArtifactCache.deCache(artifact);
                              artifact.internalSetDeleted();
                           }
                        }
                        InternalEventManager2.kickArtifactsPurgedEvent(sender, EventBasicGuidArtifact.get(
                              EventModType.Purged, ((NetworkArtifactPurgeEvent) event).getDefaultBasicGuidArtifacts()));
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  }
               }

               if (artifactChanges.size() > 0) {
                  /*
                   * Since transaction events are a collection of ArtifactModfied and RelationModified
                   * events, create a new Sender based on the last sender for these events.
                   */
                  Sender transactionSender =
                        new Sender("RemoteEventManager", lastArtifactRelationModChangeSender.getOseeSession());
                  InternalEventManager2.kickTransactionEvent(transactionSender, artifactChanges);
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

   public static void kick(Collection<IFrameworkEvent> events) {
      kick(events.toArray(new IFrameworkEvent[events.size()]));
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

      if (InternalEventManager2.isEnableRemoteEventLoopback()) {
         OseeLog.log(Activator.class, Level.INFO, "REM2: Loopback enabled - Returning events as Remote event.");
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
