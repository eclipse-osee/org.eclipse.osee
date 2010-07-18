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
package org.eclipse.osee.framework.skynet.core.event;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event2.AccessControlEvent;
import org.eclipse.osee.framework.skynet.core.event2.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event2.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event2.BroadcastEvent;
import org.eclipse.osee.framework.skynet.core.event2.FrameworkEventManager;
import org.eclipse.osee.framework.skynet.core.event2.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event2.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;

/**
 * Internal implementation of OSEE Event Manager that should only be accessed from RemoteEventManager and
 * OseeEventManager classes.
 * 
 * @author Donald G. Dunne
 */
public class InternalEventManager2 {

   public static final Collection<UnloadedArtifact> EMPTY_UNLOADED_ARTIFACTS = Collections.emptyList();
   private static boolean disableEvents = false;

   private static final ThreadFactory threadFactory = new OseeEventThreadFactory("Osee Events2");
   private static final ExecutorService executorService = Executors.newFixedThreadPool(
         Runtime.getRuntime().availableProcessors(), threadFactory);

   // This will disable all Local TransactionEvents and enable loopback routing of Remote TransactionEvents back
   // through the RemoteEventService as if they came from another client.  This is for testing purposes only and
   // should be reset to false before release.
   private static boolean enableRemoteEventLoopback = false;

   private static void execute(Runnable runnable) {
      executorService.submit(runnable);
   }

   /*
    * Kick LOCAL and REMOTE access control events
    */
   static void kickAccessControlArtifactsEvent(final Sender sender, final AccessControlEvent accessControlEvent) {
      if (sender == null) {
         throw new IllegalArgumentException("sender can not be null");
      }
      if (accessControlEvent.getEventType() == null) {
         throw new IllegalArgumentException("accessControlEventType can not be null");
      }
      if (isDisableEvents()) {
         return;
      }
      OseeEventManager.eventLog("IEM2: kickAccessControlEvent - type: " + accessControlEvent + sender + " artifacts: " + accessControlEvent.getArtifacts());
      Runnable runnable = new Runnable() {
         public void run() {
            try {
               // Kick LOCAL
               boolean normalOperation = !enableRemoteEventLoopback;
               boolean loopbackTestEnabledAndRemoteEventReturned = enableRemoteEventLoopback && sender.isRemote();
               if ((normalOperation && sender.isLocal()) || loopbackTestEnabledAndRemoteEventReturned) {
                  FrameworkEventManager.processAccessControlEvent(sender, accessControlEvent);
               }
               // Kick REMOTE
               if (sender.isLocal() && accessControlEvent.getEventType().isRemoteEventType()) {
                  RemoteEventManager2.getInstance().kick(
                        FrameworkEventUtil.getRemoteAccessControlEvent(accessControlEvent));
               }
            } catch (Exception ex) {
               OseeEventManager.eventLog("IEM2 kickAccessControlEvent", ex);
            }
         }
      };
      execute(runnable);
   }

   // Kick LOCAL "remote event manager" event
   static void kickLocalRemEvent(final Sender sender, final RemoteEventServiceEventType remoteEventServiceEventType) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      OseeEventManager.eventLog("IEM2: kickLocalRemEvent: type: " + remoteEventServiceEventType + " - " + sender);
      Runnable runnable = new Runnable() {
         public void run() {
            // Kick LOCAL
            try {
               if (sender.isLocal() && remoteEventServiceEventType.isLocalEventType()) {
                  FrameworkEventManager.processRemoteEventManagerEvent(sender, remoteEventServiceEventType);
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   // Kick LOCAL ArtifactReloadEvent
   static void kickLocalArtifactReloadEvent(final Sender sender, final ArtifactEvent artifactEvent) {
      if (isDisableEvents()) {
         return;
      }
      OseeEventManager.eventLog("IEM2: kickArtifactReloadEvent [" + artifactEvent + "] - " + sender);
      Runnable runnable = new Runnable() {
         public void run() {
            try {
               // Kick LOCAL
               boolean normalOperation = !enableRemoteEventLoopback;
               boolean loopbackTestEnabledAndRemoteEventReturned = enableRemoteEventLoopback && sender.isRemote();
               if ((normalOperation && sender.isLocal()) || loopbackTestEnabledAndRemoteEventReturned) {
                  FrameworkEventManager.processEventArtifactsAndRelations(sender, artifactEvent);
               }

               // NO REMOTE KICK
            } catch (Exception ex) {
               OseeEventManager.eventLog("IEM2 kickArtifactReloadEvent", ex);
            }
         }
      };
      execute(runnable);
   }

   /*
    * Kick LOCAL and REMOTE branch events
    */
   static void kickBranchEvent(final Sender sender, final BranchEvent branchEvent) {
      if (branchEvent.getNetworkSender() == null) {
         OseeEventManager.eventLog("IEM2: kickBranchEvent - ERROR networkSender can't be null.");
         return;
      }
      if (isDisableEvents()) {
         return;
      }
      OseeEventManager.eventLog("IEM2: kickBranchEvent: type: " + branchEvent.getEventType() + " guid: " + branchEvent.getBranchGuid() + " - " + sender);
      Runnable runnable = new Runnable() {
         public void run() {
            try {
               // Log if this is a loopback and what is happening
               if (enableRemoteEventLoopback) {
                  OseeEventManager.eventLog("IEM2: BranchEvent Loopback enabled" + (sender.isLocal() ? " - Ignoring Local Kick" : " - Kicking Local from Loopback"));
               }
               BranchEventType branchEventType = branchEvent.getEventType();

               // Kick LOCAL
               boolean normalOperation = !enableRemoteEventLoopback;
               boolean loopbackTestEnabledAndRemoteEventReturned = enableRemoteEventLoopback && sender.isRemote();
               if ((normalOperation && sender.isLocal() && branchEventType.isLocalEventType()) || loopbackTestEnabledAndRemoteEventReturned) {
                  FrameworkEventManager.processBranchEvent(sender, branchEvent);
               }

               // Kick REMOTE (If source was Local and this was not a default branch changed event
               if (sender.isLocal() && branchEventType.isRemoteEventType()) {
                  RemoteEventManager2.getInstance().kick(FrameworkEventUtil.getRemoteBranchEvent(branchEvent));
               }
            } catch (OseeAuthenticationRequiredException ex) {
               OseeEventManager.eventLog("IEM2 kickBranchEvent", ex);
            }
         }
      };
      execute(runnable);
   }

   // Kick LOCAL and REMOTE ArtifactEvent
   static void kickPersistEvent(final Sender sender, final ArtifactEvent artifactEvent) {
      if (artifactEvent.getNetworkSender() == null) {
         OseeEventManager.eventLog("IEM2: kickPersistEvent - ERROR networkSender can't be null.");
         return;
      }
      if (isDisableEvents()) {
         return;
      }
      OseeEventManager.eventLog("IEM2: kickPersistEvent [" + artifactEvent + "] - " + sender);
      Runnable runnable = new Runnable() {
         public void run() {
            // Roll-up change information
            try {
               // Log if this is a loopback and what is happening
               if (enableRemoteEventLoopback) {
                  OseeEventManager.eventLog("IEM2: ArtifactEvent Loopback enabled" + (sender.isLocal() ? " - Ignoring Local Kick" : " - Kicking Local from Loopback"));
               }

               // Kick LOCAL
               boolean normalOperation = !enableRemoteEventLoopback;
               boolean loopbackTestEnabledAndRemoteEventReturned = enableRemoteEventLoopback && sender.isRemote();
               if (normalOperation || loopbackTestEnabledAndRemoteEventReturned) {
                  FrameworkEventManager.processEventArtifactsAndRelations(sender, artifactEvent);
               }

               // Kick REMOTE (If source was Local and this was not a default branch changed event
               if (sender.isLocal()) {
                  RemoteEventManager2.getInstance().kick(FrameworkEventUtil.getRemotePersistEvent(artifactEvent));
               }
            } catch (Exception ex) {
               OseeEventManager.eventLog("IEM2 kickPersistEvent", ex);
            }
         }
      };
      execute(runnable);
   }

   // Kick LOCAL and REMOTE ArtifactEvent
   static void kickTransactionEvent(final Sender sender, final TransactionEvent transEvent) {
      if (transEvent.getNetworkSender() == null) {
         OseeEventManager.eventLog("IEM2: kickTransactionEvent - ERROR networkSender can't be null.");
         return;
      }
      if (isDisableEvents()) {
         return;
      }
      OseeEventManager.eventLog("IEM2: kickTransactionEvent [" + transEvent + "] - " + sender);
      Runnable runnable = new Runnable() {
         public void run() {
            // Roll-up change information
            try {
               // Log if this is a loopback and what is happening
               if (!enableRemoteEventLoopback) {
                  OseeEventManager.eventLog("IEM2: TransactionEvent Loopback enabled" + (sender.isLocal() ? " - Ignoring Local Kick" : " - Kicking Local from Loopback"));
               }

               // Kick LOCAL
               boolean normalOperation = !enableRemoteEventLoopback;
               boolean loopbackTestEnabledAndRemoteEventReturned = enableRemoteEventLoopback && sender.isRemote();
               if (normalOperation || loopbackTestEnabledAndRemoteEventReturned) {
                  FrameworkEventManager.processTransactionEvent(sender, transEvent);
               }

               // Kick REMOTE (If source was Local and this was not a default branch changed event
               if (sender.isLocal()) {
                  // Kick REMOTE
                  RemoteEventManager2.getInstance().kick(FrameworkEventUtil.getRemoteTransactionEvent(transEvent));
               }
            } catch (Exception ex) {
               OseeEventManager.eventLog("IEM2: kickTransactionEvent", ex);
            }
         }
      };
      execute(runnable);
   }

   /*
    * Kick LOCAL and REMOTE broadcast event
    */
   static void kickBroadcastEvent(final Sender sender, final BroadcastEvent broadcastEvent) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }

      if (!broadcastEvent.getBroadcastEventType().isPingOrPong()) {
         OseeEventManager.eventLog("IEM2: kickBroadcastEvent: type: " + broadcastEvent.getBroadcastEventType().name() + " message: " + broadcastEvent.getMessage() + " - " + sender);
      }
      Runnable runnable = new Runnable() {
         public void run() {
            try {
               // Kick from REMOTE
               if (sender.isRemote() || sender.isLocal() && broadcastEvent.getBroadcastEventType().isLocalEventType()) {
                  FrameworkEventManager.processEventBroadcastEvent(sender, broadcastEvent);
               }

               // Kick REMOTE (If source was Local and this was not a default branch changed event
               if (sender.isLocal() && broadcastEvent.getBroadcastEventType().isRemoteEventType()) {
                  RemoteEventManager2.getInstance().kick(FrameworkEventUtil.getRemoteBroadcastEvent(broadcastEvent));
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   static boolean isDisableEvents() {
      return disableEvents;
   }

   public static void setDisableEvents(boolean disableEvents) {
      InternalEventManager2.disableEvents = disableEvents;
   }

   public static boolean isEnableRemoteEventLoopback() {
      return enableRemoteEventLoopback;
   }

   public static void setEnableRemoteEventLoopback(boolean enableRemoteEventLoopback) {
      InternalEventManager2.enableRemoteEventLoopback = enableRemoteEventLoopback;
   }
}
