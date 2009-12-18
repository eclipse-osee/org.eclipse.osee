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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkAccessControlArtifactsEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactAddedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactChangeTypeEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactModifiedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactPurgeEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkBroadcastEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkCommitBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkDeletedBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkMergeBranchConflictResolvedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkNewBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkPurgeBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkCreatedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkRationalModifiedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRenameBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkTransactionDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetArtifactEventBase;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetRelationLinkEventBase;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedRelation;

/**
 * Internal implementation of OSEE Event Manager that should only be accessed from RemoteEventManager and
 * OseeEventManager classes.
 * 
 * @author Donald G. Dunne
 */
public class InternalEventManager {

   private static final List<IEventListener> priorityListeners = new CopyOnWriteArrayList<IEventListener>();
   private static final List<IEventListener> listeners = new CopyOnWriteArrayList<IEventListener>();
   public static final Collection<UnloadedArtifact> EMPTY_UNLOADED_ARTIFACTS = Collections.emptyList();
   private static boolean disableEvents = false;

   private static final ThreadFactory threadFactory = new OseeEventThreadFactory("Osee Events");
   private static final ExecutorService executorService =
         Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);

   // This will disable all Local TransactionEvents and enable loopback routing of Remote TransactionEvents back
   // through the RemoteEventService as if they came from another client.  This is for testing purposes only and
   // should be reset to false before release.
   public static final boolean enableRemoteEventLoopback = false;

   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Events"));

   // Kick LOCAL "remote event manager" event
   static void kickRemoteEventManagerEvent(final Sender sender, final RemoteEventServiceEventType remoteEventServiceEventType) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      eventLog("OEM: kickRemoteEventManagerEvent: type: " + remoteEventServiceEventType + " - " + sender);
      Runnable runnable = new Runnable() {
         public void run() {
            // Kick LOCAL
            try {
               if (sender.isLocal() && remoteEventServiceEventType.isLocalEventType()) {
                  safelyInvokeListeners(IRemoteEventManagerEventListener.class, "handleRemoteEventManagerEvent",
                        sender, remoteEventServiceEventType);
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   /*
    * Kick LOCAL and REMOTE broadcast event
    */
   static void kickBroadcastEvent(final Sender sender, final BroadcastEventType broadcastEventType, final String[] userIds, final String message) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }

      if (!broadcastEventType.isPingOrPong()) {
         eventLog("OEM: kickBroadcastEvent: type: " + broadcastEventType.name() + " message: " + message + " - " + sender);
      }
      Runnable runnable = new Runnable() {
         public void run() {
            try {
               // Kick from REMOTE
               if (sender.isRemote() || sender.isLocal() && broadcastEventType.isLocalEventType()) {
                  safelyInvokeListeners(IBroadcastEventListener.class, "handleBroadcastEvent", sender,
                        broadcastEventType, userIds, message);
               }

               // Kick REMOTE (If source was Local and this was not a default branch changed event
               if (sender.isLocal() && broadcastEventType.isRemoteEventType()) {
                  RemoteEventManager.kick(new NetworkBroadcastEvent(broadcastEventType.name(), message,
                        sender.getNetworkSender()));
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   /*
    * Kick LOCAL and REMOTE branch events
    */
   static void kickBranchEvent(final Sender sender, final BranchEventType branchEventType, final int branchId) {
      if (isDisableEvents()) {
         return;
      }
      eventLog("OEM: kickBranchEvent: type: " + branchEventType + " id: " + branchId + " - " + sender);
      Runnable runnable = new Runnable() {
         public void run() {
            try {
               // Log if this is a loopback and what is happening
               if (enableRemoteEventLoopback) {
                  OseeLog.log(
                        InternalEventManager.class,
                        Level.WARNING,
                        "OEM: BranchEvent Loopback enabled" + (sender.isLocal() ? " - Ignoring Local Kick" : " - Kicking Local from Loopback"));
               }

               // Kick LOCAL
               if (!enableRemoteEventLoopback || enableRemoteEventLoopback && branchEventType.isRemoteEventType() && sender.isRemote()) {
                  if (sender.isRemote() || sender.isLocal() && branchEventType.isLocalEventType()) {
                     safelyInvokeListeners(IBranchEventListener.class, "handleBranchEvent", sender, branchEventType,
                           branchId);
                  }
               }
               // Kick REMOTE (If source was Local and this was not a default branch changed event

               if (sender.isLocal() && branchEventType.isRemoteEventType()) {
                  if (branchEventType == BranchEventType.Added) {
                     RemoteEventManager.kick(new NetworkNewBranchEvent(branchId, sender.getNetworkSender()));
                  } else if (branchEventType == BranchEventType.Deleted) {
                     RemoteEventManager.kick(new NetworkDeletedBranchEvent(branchId, sender.getNetworkSender()));
                  } else if (branchEventType == BranchEventType.Purged) {
                     RemoteEventManager.kick(new NetworkPurgeBranchEvent(branchId, sender.getNetworkSender()));
                  } else if (branchEventType == BranchEventType.Committed) {
                     RemoteEventManager.kick(new NetworkCommitBranchEvent(branchId, sender.getNetworkSender()));
                  } else if (branchEventType == BranchEventType.Renamed) {
                     Branch branch = null;
                     try {
                        branch = BranchManager.getBranch(branchId);
                        RemoteEventManager.kick(new NetworkRenameBranchEvent(branchId, sender.getNetworkSender(),
                              branch.getName(), branch.getShortName()));
                     } catch (OseeCoreException ex) {
                        // do nothing
                     }
                  }
               }
            } catch (OseeAuthenticationRequiredException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   // Kick LOCAL and REMOTE branch events
   static void kickMergeBranchEvent(final Sender sender, final MergeBranchEventType branchEventType, final int branchId) {
      eventLog("OEM: kickMergeBranchEvent: type: " + branchEventType + " id: " + branchId + " - " + sender);
      Runnable runnable = new Runnable() {
         public void run() {
            try {
               // Log if this is a loopback and what is happening
               if (enableRemoteEventLoopback) {
                  OseeLog.log(
                        InternalEventManager.class,
                        Level.WARNING,
                        "OEM: MergeBranchEvent Loopback enabled" + (sender.isLocal() ? " - Ignoring Local Kick" : " - Kicking Local from Loopback"));
               }

               // Kick LOCAL
               if (!enableRemoteEventLoopback || enableRemoteEventLoopback && branchEventType.isRemoteEventType() && sender.isRemote()) {
                  if (sender.isRemote() || sender.isLocal() && branchEventType.isLocalEventType()) {
                     safelyInvokeListeners(IMergeBranchEventListener.class, "handleMergeBranchEvent", sender,
                           branchEventType, branchId);
                  }
               }
               // Kick REMOTE (If source was Local and this was not a default branch changed event

               if (sender.isLocal() && branchEventType.isRemoteEventType()) {
                  if (branchEventType == MergeBranchEventType.ConflictResolved) {
                     RemoteEventManager.kick(new NetworkMergeBranchConflictResolvedEvent(branchId,
                           sender.getNetworkSender()));
                  }
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   private static void execute(Runnable runnable) {
      executorService.submit(runnable);
   }

   /*
    * Kick LOCAL and REMOTE access control events
    */
   static void kickAccessControlArtifactsEvent(final Sender sender, final AccessControlEventType accessControlEventType, final LoadedArtifacts loadedArtifacts) {
      if (sender == null) {
         throw new IllegalArgumentException("sender can not be null");
      }
      if (accessControlEventType == null) {
         throw new IllegalArgumentException("accessControlEventType can not be null");
      }
      if (loadedArtifacts == null) {
         throw new IllegalArgumentException("loadedArtifacts can not be null");
      }
      if (isDisableEvents()) {
         return;
      }
      eventLog("OEM: kickAccessControlEvent - type: " + accessControlEventType + sender + " loadedArtifacts: " + loadedArtifacts);
      Runnable runnable = new Runnable() {
         public void run() {
            // Kick LOCAL
            if (accessControlEventType.isLocalEventType()) {
               safelyInvokeListeners(IAccessControlEventListener.class, "handleAccessControlArtifactsEvent", sender,
                     accessControlEventType, loadedArtifacts);
            }
            // Kick REMOTE (If source was Local and this was not a default branch changed event
            try {
               if (sender.isLocal() && accessControlEventType.isRemoteEventType()) {
                  Integer branchId = null;
                  if (loadedArtifacts != null && !loadedArtifacts.getLoadedArtifacts().isEmpty()) {
                     branchId = loadedArtifacts.getLoadedArtifacts().iterator().next().getBranch().getId();
                  }
                  Collection<Integer> artifactIds;
                  Collection<Integer> artifactTypeIds;
                  if (loadedArtifacts != null) {
                     artifactIds = loadedArtifacts.getAllArtifactIds();
                     artifactTypeIds = loadedArtifacts.getAllArtifactTypeIds();
                  } else {
                     artifactIds = Collections.emptyList();
                     artifactTypeIds = Collections.emptyList();
                  }
                  RemoteEventManager.kick(new NetworkAccessControlArtifactsEvent(accessControlEventType.name(),
                        branchId == null ? -1 : branchId, artifactIds, artifactTypeIds, sender.getNetworkSender()));
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   /*
    * Kick LOCAL event to notify application that the branch to artifact cache has been updated; 
    * This event does NOT go external
    */
   static void kickLocalBranchToArtifactCacheUpdateEvent(final Sender sender) throws OseeCoreException {
      eventLog("OEM: kickLocalBranchToArtifactCacheUpdateEvent - " + sender);
      Runnable runnable = new Runnable() {
         public void run() {
            // Kick LOCAL
            safelyInvokeListeners(IBranchEventListener.class, "handleLocalBranchToArtifactCacheUpdateEvent", sender);
         }
      };
      execute(runnable);
   }

   // Kick LOCAL artifact modified event; This event does NOT go external
   static void kickArtifactModifiedEvent(final Sender sender, final ArtifactModType artifactModType, final Artifact artifact) throws OseeCoreException {
      eventLog("OEM: kickArtifactModifiedEvent - " + artifactModType + " - " + artifact.getGuid() + " - " + sender + " - " + artifact.getDirtySkynetAttributeChanges());
      Runnable runnable = new Runnable() {
         public void run() {
            // Kick LOCAL
            safelyInvokeListeners(IArtifactModifiedEventListener.class, "handleArtifactModifiedEvent", sender,
                  artifactModType, artifact);
         }
      };
      execute(runnable);
   }

   // Kick LOCAL relation modified event; This event does NOT go external
   static void kickRelationModifiedEvent(final Sender sender, final RelationEventType relationEventType, final RelationLink link, final Branch branch, final String relationType) throws OseeCoreException {
      eventLog("OEM: kickRelationModifiedEvent - " + relationEventType + " - " + link + " - " + sender);
      Runnable runnable = new Runnable() {
         public void run() {
            // Kick LOCAL
            safelyInvokeListeners(IRelationModifiedEventListener.class, "handleRelationModifiedEvent", sender,
                  relationEventType, link, branch, relationType);
         }
      };
      execute(runnable);
   }

   // Kick LOCAL and REMOTE purged event depending on sender
   static void kickArtifactsPurgedEvent(final Sender sender, final LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      eventLog("OEM: kickArtifactsPurgedEvent " + sender + " - " + loadedArtifacts);
      Runnable runnable = new Runnable() {
         public void run() {
            // Kick LOCAL
            safelyInvokeListeners(IArtifactsPurgedEventListener.class, "handleArtifactsPurgedEvent", sender,
                  loadedArtifacts);
            // Kick REMOTE (If source was Local and this was not a default branch changed event
            try {
               if (sender.isLocal()) {
                  RemoteEventManager.kick(new NetworkArtifactPurgeEvent(
                        loadedArtifacts.getLoadedArtifacts().iterator().next().getBranch().getId(),
                        loadedArtifacts.getAllArtifactIds(), loadedArtifacts.getAllArtifactTypeIds(),
                        sender.getNetworkSender()));
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   // Kick LOCAL and REMOTE artifact change type depending on sender
   static void kickArtifactsChangeTypeEvent(final Sender sender, final int toArtifactTypeId, final LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      eventLog("OEM: kickArtifactsChangeTypeEvent " + sender + " - " + loadedArtifacts);
      Runnable runnable = new Runnable() {
         public void run() {
            // Kick LOCAL
            safelyInvokeListeners(IArtifactsChangeTypeEventListener.class, "handleArtifactsChangeTypeEvent", sender,
                  toArtifactTypeId, loadedArtifacts);
            // Kick REMOTE (If source was Local and this was not a default branch changed event
            try {
               if (sender.isLocal()) {
                  RemoteEventManager.kick(new NetworkArtifactChangeTypeEvent(
                        loadedArtifacts.getLoadedArtifacts().iterator().next().getBranch().getId(),
                        loadedArtifacts.getAllArtifactIds(), loadedArtifacts.getAllArtifactTypeIds(), toArtifactTypeId,
                        sender.getNetworkSender()));
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   // Kick LOCAL and remote transaction deleted event
   static void kickTransactionsDeletedEvent(final Sender sender, final int[] transactionIds) throws OseeCoreException {
      //TODO This needs to be converted into the individual artifacts and relations that were deleted/modified
      if (isDisableEvents()) {
         return;
      }
      eventLog("OEM: kickTransactionsDeletedEvent " + sender + " - " + transactionIds.length);
      Runnable runnable = new Runnable() {
         public void run() {
            // Kick LOCAL
            safelyInvokeListeners(ITransactionsDeletedEventListener.class, "handleTransactionsDeletedEvent", sender,
                  transactionIds);
            // Kick REMOTE (If source was Local and this was not a default branch changed event
            try {
               if (sender.isLocal()) {
                  RemoteEventManager.kick(new NetworkTransactionDeletedEvent(sender.getNetworkSender(), transactionIds));
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   // Kick LOCAL and REMOTE TransactionEvent
   static void kickTransactionEvent(final Sender sender, Collection<ArtifactTransactionModifiedEvent> xModifiedEvents) {
      if (isDisableEvents()) {
         return;
      }
      eventLog("OEM: kickTransactionEvent #ModEvents: " + xModifiedEvents.size() + " - " + sender);
      final Collection<ArtifactTransactionModifiedEvent> xModifiedEventsCopy =
            new ArrayList<ArtifactTransactionModifiedEvent>();
      xModifiedEventsCopy.addAll(xModifiedEvents);
      Runnable runnable = new Runnable() {
         public void run() {
            // Roll-up change information
            FrameworkTransactionData transData = createTransactionDataRollup(xModifiedEventsCopy);
            try {
               // Log if this is a loopback and what is happening
               if (enableRemoteEventLoopback) {
                  OseeLog.log(
                        InternalEventManager.class,
                        Level.WARNING,
                        "OEM: TransactionEvent Loopback enabled" + (sender.isLocal() ? " - Ignoring Local Kick" : " - Kicking Local from Loopback"));
               }

               // Kick LOCAL
               if (!enableRemoteEventLoopback || enableRemoteEventLoopback && sender.isRemote()) {
                  safelyInvokeListeners(IFrameworkTransactionEventListener.class, "handleFrameworkTransactionEvent",
                        sender, transData);
               }

               // Kick REMOTE (If source was Local and this was not a default branch changed event
               if (sender.isLocal()) {
                  List<ISkynetEvent> events = generateNetworkSkynetEvents(sender, xModifiedEventsCopy);
                  RemoteEventManager.kick(events);
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   // Kick LOCAL ArtifactReloadEvent
   static void kickArtifactReloadEvent(final Sender sender, final Collection<? extends Artifact> artifacts) {
      if (isDisableEvents()) {
         return;
      }
      eventLog("OEM: kickArtifactReloadEvent #Reloads: " + artifacts.size() + " - " + sender);
      Runnable runnable = new Runnable() {
         public void run() {
            try {
               // Log if this is a loopback and what is happening
               if (enableRemoteEventLoopback) {
                  OseeLog.log(
                        InternalEventManager.class,
                        Level.WARNING,
                        "OEM: kickArtifactReloadEvent Loopback enabled" + (sender.isLocal() ? " - Ignoring Local Kick" : " - Kicking Local from Loopback"));
               }

               // Kick LOCAL
               if (!enableRemoteEventLoopback) {
                  safelyInvokeListeners(IArtifactReloadEventListener.class, "handleReloadEvent", sender, artifacts);
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   /**
    * Add a priority listener. This should only be done for caches where they need to be updated before all other
    * listeners are called.
    */
   static void addPriorityListener(IEventListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException("listener can not be null");
      }
      if (!priorityListeners.contains(listener)) {
         priorityListeners.add(listener);
      }
      eventLog("OEM: addPriorityListener (" + priorityListeners.size() + ") " + listener);
   }

   static void addListener(IEventListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException("listener can not be null");
      }
      if (!listeners.contains(listener)) {
         listeners.add(listener);
      }
      eventLog("OEM: addListener (" + listeners.size() + ") " + listener);
   }

   static void removeListeners(IEventListener listener) {
      eventLog("OEM: removeListener: (" + listeners.size() + ") " + listener);
      listeners.remove(listener);
      priorityListeners.remove(listener);
   }

   // This method clears all listeners. Should only be used for testing purposes.
   public static void removeAllListeners() {
      listeners.clear();
      priorityListeners.clear();
   }

   public static String getObjectSafeName(Object object) {
      try {
         return object.toString();
      } catch (Exception ex) {
         return object.getClass().getSimpleName() + " - exception on toString: " + ex.getLocalizedMessage();
      }
   }

   static boolean isDisableEvents() {
      return disableEvents;
   }

   static void setDisableEvents(boolean disableEvents) {
      InternalEventManager.disableEvents = disableEvents;
   }

   static String getListenerReport() {
      List<String> listenerStrs = new ArrayList<String>();
      for (IEventListener listener : priorityListeners) {
         listenerStrs.add("Priority: " + getObjectSafeName(listener));
      }
      for (IEventListener listener : listeners) {
         listenerStrs.add(getObjectSafeName(listener));
      }
      String[] listArr = listenerStrs.toArray(new String[listenerStrs.size()]);
      Arrays.sort(listArr);
      return org.eclipse.osee.framework.jdk.core.util.Collections.toString("\n", (Object[]) listArr);
   }

   private static List<ISkynetEvent> generateNetworkSkynetEvents(Sender sender, Collection<ArtifactTransactionModifiedEvent> xModifiedEvents) {
      List<ISkynetEvent> events = new ArrayList<ISkynetEvent>();
      for (ArtifactTransactionModifiedEvent xModifiedEvent : xModifiedEvents) {
         events.add(generateNetworkSkynetEvent(xModifiedEvent, sender));
      }
      return events;
   }

   private static ISkynetEvent generateNetworkSkynetEvent(ArtifactTransactionModifiedEvent xModifiedEvent, Sender sender) {
      ISkynetEvent ret = null;
      if (xModifiedEvent instanceof ArtifactModifiedEvent) {
         ret = generateNetworkSkynetArtifactEvent((ArtifactModifiedEvent) xModifiedEvent, sender);
      } else if (xModifiedEvent instanceof RelationModifiedEvent) {
         ret = generateNetworkSkynetRelationEvent((RelationModifiedEvent) xModifiedEvent, sender);
      }
      return ret;
   }

   private static ISkynetEvent generateNetworkSkynetArtifactEvent(ArtifactModifiedEvent artEvent, Sender sender) {
      SkynetArtifactEventBase eventBase = getArtifactEventBase(artEvent, sender);
      ISkynetEvent ret;
      if (artEvent.artifactModType == ArtifactModType.Changed) {
         ret = new NetworkArtifactModifiedEvent(eventBase, artEvent.dirtySkynetAttributeChanges);
      } else if (artEvent.artifactModType == ArtifactModType.Added) {
         ret = new NetworkArtifactAddedEvent(eventBase);
      } else if (artEvent.artifactModType == ArtifactModType.Deleted) {
         ret = new NetworkArtifactDeletedEvent(eventBase);
      } else {
         OseeLog.log(InternalEventManager.class, Level.SEVERE, "Unhandled xArtifactModifiedEvent event: " + artEvent);
         ret = null;
      }
      return ret;
   }

   private static SkynetArtifactEventBase getArtifactEventBase(ArtifactModifiedEvent artEvent, Sender sender) {
      Artifact artifact = artEvent.artifact;
      SkynetArtifactEventBase eventBase =
            new SkynetArtifactEventBase(artifact.getBranch().getId(), artEvent.transactionNumber, artifact.getArtId(),
                  artifact.getArtTypeId(), artifact.getFactory().getClass().getCanonicalName(),
                  artEvent.sender.getNetworkSender());

      return eventBase;
   }

   private static ISkynetEvent generateNetworkSkynetRelationEvent(RelationModifiedEvent relEvent, Sender sender) {
      RelationLink link = relEvent.link;
      SkynetRelationLinkEventBase eventBase = getRelationLinkEventBase(link, sender);
      SkynetRelationLinkEventBase networkEvent;

      String rationale = link.getRationale();
      String descriptorName = link.getRelationType().getName();

      if (relEvent.relationEventType == RelationEventType.RationaleMod) {
         networkEvent = new NetworkRelationLinkRationalModifiedEvent(eventBase, rationale);
      } else if (relEvent.relationEventType == RelationEventType.Deleted) {
         networkEvent = new NetworkRelationLinkDeletedEvent(eventBase);
      } else if (relEvent.relationEventType == RelationEventType.Added) {
         networkEvent = new NetworkRelationLinkCreatedEvent(eventBase, rationale, descriptorName);
      } else {
         OseeLog.log(InternalEventManager.class, Level.SEVERE, "Unhandled xRelationModifiedEvent event: " + relEvent);
         networkEvent = null;
      }
      return networkEvent;
   }

   private static SkynetRelationLinkEventBase getRelationLinkEventBase(RelationLink link, Sender sender) {
      Artifact left = link.getArtifactIfLoaded(RelationSide.SIDE_A);
      Artifact right = link.getArtifactIfLoaded(RelationSide.SIDE_B);
      SkynetRelationLinkEventBase ret = null;
      ret =
            new SkynetRelationLinkEventBase(link.getGammaId(), link.getBranch().getId(), link.getRelationId(),
                  link.getAArtifactId(), (left != null ? left.getArtTypeId() : -1), link.getBArtifactId(),
                  (right != null ? right.getArtTypeId() : -1), link.getRelationType().getId(),
                  sender.getNetworkSender());

      return ret;
   }

   private static FrameworkTransactionData createTransactionDataRollup(Collection<ArtifactTransactionModifiedEvent> xModifiedEvents) {
      // Roll-up change information
      FrameworkTransactionData transData = new FrameworkTransactionData();
      transData.setXModifiedEvents(xModifiedEvents);

      for (ArtifactTransactionModifiedEvent xModifiedEvent : xModifiedEvents) {
         if (xModifiedEvent instanceof ArtifactModifiedEvent) {
            ArtifactModifiedEvent xArtifactModifiedEvent = (ArtifactModifiedEvent) xModifiedEvent;
            if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Added) {
               if (xArtifactModifiedEvent.artifact != null) {
                  transData.cacheAddedArtifacts.add(xArtifactModifiedEvent.artifact);
                  if (transData.branchId == -1) {
                     transData.branchId = xArtifactModifiedEvent.artifact.getBranch().getId();
                  }
               } else {
                  transData.unloadedAddedArtifacts.add(xArtifactModifiedEvent.unloadedArtifact);
                  if (transData.branchId == -1) {
                     transData.branchId = xArtifactModifiedEvent.unloadedArtifact.getId();
                  }
               }
            }
            if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Deleted) {
               if (xArtifactModifiedEvent.artifact != null) {
                  transData.cacheDeletedArtifacts.add(xArtifactModifiedEvent.artifact);
                  if (transData.branchId == -1) {
                     transData.branchId = xArtifactModifiedEvent.artifact.getBranch().getId();
                  }
               } else {
                  transData.unloadedDeletedArtifacts.add(xArtifactModifiedEvent.unloadedArtifact);
                  if (transData.branchId == -1) {
                     transData.branchId = xArtifactModifiedEvent.unloadedArtifact.getId();
                  }
               }
            }
            if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Changed) {
               if (xArtifactModifiedEvent.artifact != null) {
                  transData.cacheChangedArtifacts.add(xArtifactModifiedEvent.artifact);
                  if (transData.branchId == -1) {
                     transData.branchId = xArtifactModifiedEvent.artifact.getBranch().getId();
                  }
               } else {
                  transData.unloadedChangedArtifacts.add(xArtifactModifiedEvent.unloadedArtifact);
                  if (transData.branchId == -1) {
                     transData.branchId = xArtifactModifiedEvent.unloadedArtifact.getId();
                  }
               }
            }
         }
         if (xModifiedEvent instanceof RelationModifiedEvent) {
            RelationModifiedEvent xRelationModifiedEvent = (RelationModifiedEvent) xModifiedEvent;
            UnloadedRelation unloadedRelation = xRelationModifiedEvent.unloadedRelation;
            LoadedRelation loadedRelation = null;
            // If link is loaded, get information from link
            if (xRelationModifiedEvent.link != null) {
               RelationLink link = xRelationModifiedEvent.link;
               // Get artifact A/B if loaded in artifact cache
               Artifact artA = ArtifactCache.getActive(link.getAArtifactId(), link.getABranch());
               Artifact artB = ArtifactCache.getActive(link.getBArtifactId(), link.getBBranch());
               try {
                  loadedRelation =
                        new LoadedRelation(artA, artB, xRelationModifiedEvent.link.getRelationType(),
                              xRelationModifiedEvent.branch, unloadedRelation);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
            // Else, get information from unloadedRelation (if != null)
            else if (unloadedRelation != null) {
               Artifact artA = ArtifactCache.getActive(unloadedRelation.getArtifactAId(), unloadedRelation.getId());
               Artifact artB = ArtifactCache.getActive(unloadedRelation.getArtifactBId(), unloadedRelation.getId());
               if (artA != null || artB != null) {
                  try {
                     loadedRelation =
                           new LoadedRelation(artA, artB, RelationTypeManager.getType(unloadedRelation.getTypeId()),
                                 artA != null ? artA.getBranch() : artB.getBranch(), unloadedRelation);
                  } catch (OseeCoreException ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex);
                  }
               }
            }
            if (xRelationModifiedEvent.relationEventType == RelationEventType.Added) {
               if (loadedRelation != null) {
                  transData.cacheAddedRelations.add(loadedRelation);
                  if (loadedRelation.getArtifactA() != null) {
                     transData.cacheRelationAddedArtifacts.add(loadedRelation.getArtifactA());
                     if (transData.branchId == -1) {
                        transData.branchId = loadedRelation.getArtifactA().getBranch().getId();
                     }
                  }
                  if (loadedRelation.getArtifactB() != null) {
                     transData.cacheRelationAddedArtifacts.add(loadedRelation.getArtifactB());
                     if (transData.branchId == -1) {
                        transData.branchId = loadedRelation.getArtifactB().getBranch().getId();
                     }
                  }
               }
               if (unloadedRelation != null) {
                  transData.unloadedAddedRelations.add(unloadedRelation);
               }
            }
            if (xRelationModifiedEvent.relationEventType == RelationEventType.Deleted) {
               if (loadedRelation != null) {
                  transData.cacheDeletedRelations.add(loadedRelation);
                  if (loadedRelation.getArtifactA() != null) {
                     transData.cacheRelationDeletedArtifacts.add(loadedRelation.getArtifactA());
                     if (transData.branchId == -1) {
                        transData.branchId = loadedRelation.getArtifactA().getBranch().getId();
                        loadedRelation.getBranch();
                     }
                  }
                  if (loadedRelation.getArtifactB() != null) {
                     transData.cacheRelationDeletedArtifacts.add(loadedRelation.getArtifactB());
                     if (transData.branchId == -1) {
                        transData.branchId = loadedRelation.getArtifactB().getBranch().getId();
                     }
                  }
               }
               if (unloadedRelation != null) {
                  transData.unloadedDeletedRelations.add(unloadedRelation);
                  if (transData.branchId == -1) {
                     transData.branchId = unloadedRelation.getId();
                  }
               }
            }
            if (xRelationModifiedEvent.relationEventType == RelationEventType.RationaleMod) {
               if (loadedRelation != null) {
                  transData.cacheChangedRelations.add(loadedRelation);
                  if (loadedRelation.getArtifactA() != null) {
                     transData.cacheRelationChangedArtifacts.add(loadedRelation.getArtifactA());
                     if (transData.branchId == -1) {
                        transData.branchId = loadedRelation.getArtifactA().getBranch().getId();
                     }
                  }
                  if (loadedRelation.getArtifactB() != null) {
                     transData.cacheRelationChangedArtifacts.add(loadedRelation.getArtifactB());
                     if (transData.branchId == -1) {
                        transData.branchId = loadedRelation.getArtifactB().getBranch().getId();
                     }
                  }
               }
               if (unloadedRelation != null) {
                  transData.unloadedChangedRelations.add(unloadedRelation);
                  if (transData.branchId == -1) {
                     transData.branchId = unloadedRelation.getId();
                  }
               }
            }
         }
      }

      // Clean out known duplicates
      transData.cacheChangedArtifacts.removeAll(transData.cacheDeletedArtifacts);
      transData.cacheAddedArtifacts.removeAll(transData.cacheDeletedArtifacts);

      return transData;
   }

   public static void safelyInvokeListeners(Class<? extends IEventListener> c, String methodName, Object... args) {
      for (IEventListener listener : priorityListeners) {
         try {
            if (c.isInstance(listener)) {
               for (Method m : c.getMethods()) {
                  if (m.getName().equals(methodName)) {
                     m.invoke(listener, args);
                  }
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      for (IEventListener listener : listeners) {
         try {
            if (c.isInstance(listener)) {
               for (Method m : c.getMethods()) {
                  if (m.getName().equals(methodName)) {
                     m.invoke(listener, args);
                  }
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   public static void eventLog(String output) {
      try {
         if (DEBUG) {
            OseeLog.log(InternalEventManager.class, Level.INFO, output);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.INFO, ex);
      }
   }
}
