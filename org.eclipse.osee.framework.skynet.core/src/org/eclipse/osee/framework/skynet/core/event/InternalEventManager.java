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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
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
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkNewBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkNewRelationLinkEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkOrderModifiedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkRationalModifiedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRenameBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkTransactionDeletedEvent;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationModType;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
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

   private static final Set<IEventListner> listeners = new CopyOnWriteArraySet<IEventListner>();
   public static final Collection<UnloadedArtifact> EMPTY_UNLOADED_ARTIFACTS = Collections.emptyList();
   private static boolean disableEvents = false;
   private static ExecutorService executorService = Executors.newFixedThreadPool(4);
   // This will disable all Local TransactionEvents and enable loopback routing of Remote TransactionEvents back
   // through the RemoteEventService as if they came from another client.  This is for testing purposes only and
   // should be reset to false before release.
   public static final boolean enableRemoteEventLoopback = false;

   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Events"));

   /**
    * Kick LOCAL "remote event manager" event
    * 
    * @param sender
    * @param remoteEventServiceEventType
    * @throws OseeCoreException
    */
   static void kickRemoteEventManagerEvent(final Sender sender, final RemoteEventServiceEventType remoteEventServiceEventType) throws OseeCoreException {
      if (isDisableEvents()) return;
      try {
         if (DEBUG) {
            OseeLog.log(InternalEventManager.class, Level.INFO,
                  "OEM: kickRemoteEventManagerEvent: type: " + remoteEventServiceEventType + " - " + sender);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.INFO, ex);
      }
      Runnable runnable = new Runnable() {
         public void run() {
            // Kick LOCAL
            try {
               if (sender.isLocal() && remoteEventServiceEventType.isLocalEventType()) {
                  for (IEventListner listener : listeners) {
                     if (listener instanceof IRemoteEventManagerEventListener) {
                        // Don't fail on any one listener's exception
                        try {
                           ((IRemoteEventManagerEventListener) listener).handleRemoteEventManagerEvent(sender,
                                 remoteEventServiceEventType);
                        } catch (Exception ex) {
                           OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
                        }
                     }
                  }
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   /**
    * Kick LOCAL and REMOTE broadcast event
    * 
    * @param sender
    * @param broadcastEventType
    * @param userIds (currently only used for disconnect_skynet)
    * @param message
    * @throws OseeCoreException
    */
   static void kickBroadcastEvent(final Sender sender, final BroadcastEventType broadcastEventType, final String[] userIds, final String message) throws OseeCoreException {
      if (isDisableEvents()) return;
      // Don't display ping/pong events 
      if (broadcastEventType != BroadcastEventType.Ping && broadcastEventType != BroadcastEventType.Pong) {
         try {
            if (DEBUG) {
               OseeLog.log(
                     InternalEventManager.class,
                     Level.INFO,
                     "OEM: kickBroadcastEvent: type: " + broadcastEventType.name() + " message: " + message + " - " + sender);
            }
         } catch (Exception ex) {
            OseeLog.log(SkynetActivator.class, Level.INFO, ex);
         }
      }
      Runnable runnable = new Runnable() {
         public void run() {
            try {
               // Kick from REMOTE
               if (sender.isRemote() || (sender.isLocal() && broadcastEventType.isLocalEventType())) {
                  for (IEventListner listener : listeners) {
                     if (listener instanceof IBroadcastEventListneer) {
                        // Don't fail on any one listener's exception
                        try {
                           ((IBroadcastEventListneer) listener).handleBroadcastEvent(sender, broadcastEventType,
                                 userIds, message);
                        } catch (Exception ex) {
                           OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
                        }
                     }
                  }
               }
               // Kick REMOTE (If source was Local and this was not a default branch changed event

               if (sender.isLocal() && broadcastEventType.isRemoteEventType()) {
                  RemoteEventManager.kick(new NetworkBroadcastEvent(broadcastEventType.name(), message,
                        sender.getNetworkSender()));
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   /**
    * Kick LOCAL and REMOTE branch events
    * 
    * @param sender
    * @param branchEventType
    * @param branchId
    * @throws OseeCoreException
    */
   static void kickBranchEvent(final Sender sender, final BranchEventType branchEventType, final int branchId) {
      if (isDisableEvents()) return;
      try {
         if (DEBUG) {
            OseeLog.log(InternalEventManager.class, Level.INFO,
                  "OEM: kickBranchEvent: type: " + branchEventType + " id: " + branchId + " - " + sender);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.INFO, ex);
      }
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
               if (!enableRemoteEventLoopback || (enableRemoteEventLoopback && branchEventType.isRemoteEventType() && sender.isRemote())) {
                  if (sender.isRemote() || (sender.isLocal() && branchEventType.isLocalEventType())) {
                     for (IEventListner listener : listeners) {
                        if (listener instanceof IBranchEventListener) {
                           // Don't fail on any one listener's exception
                           try {
                              ((IBranchEventListener) listener).handleBranchEvent(sender, branchEventType, branchId);
                           } catch (Exception ex) {
                              OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
                           }
                        }
                     }
                  }
               }
               // Kick REMOTE (If source was Local and this was not a default branch changed event

               if (sender.isLocal() && branchEventType.isRemoteEventType()) {
                  if (branchEventType == BranchEventType.Added) {
                     RemoteEventManager.kick(new NetworkNewBranchEvent(branchId, sender.getNetworkSender()));
                  } else if (branchEventType == BranchEventType.Deleted) {
                     RemoteEventManager.kick(new NetworkDeletedBranchEvent(branchId, sender.getNetworkSender()));
                  } else if (branchEventType == BranchEventType.Committed) {
                     RemoteEventManager.kick(new NetworkCommitBranchEvent(branchId, sender.getNetworkSender()));
                  } else if (branchEventType == BranchEventType.Renamed) {
                     Branch branch = null;
                     try {
                        branch = BranchManager.getBranch(branchId);
                        RemoteEventManager.kick(new NetworkRenameBranchEvent(branchId, sender.getNetworkSender(),
                              branch.getBranchName(), branch.getBranchShortName()));
                     } catch (Exception ex) {
                        // do nothing
                     }
                  }
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   private static void execute(Runnable runnable) {
      executorService.submit(runnable);
   }

   /**
    * Kick LOCAL and REMOTE access control events
    * 
    * @param sender
    * @param accessControlEventType
    * @param LoadedArtifacts
    */
   static void kickAccessControlArtifactsEvent(final Sender sender, final AccessControlEventType accessControlEventType, final LoadedArtifacts loadedArtifacts) {
      if (sender == null) throw new IllegalArgumentException("sender can not be null");
      if (accessControlEventType == null) throw new IllegalArgumentException("accessControlEventType can not be null");
      if (loadedArtifacts == null) throw new IllegalArgumentException("loadedArtifacts can not be null");
      if (isDisableEvents()) return;
      try {
         if (DEBUG) {
            OseeLog.log(
                  InternalEventManager.class,
                  Level.INFO,
                  "OEM: kickAccessControlEvent - type: " + accessControlEventType + sender + " loadedArtifacts: " + loadedArtifacts);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.INFO, ex);
      }
      Runnable runnable = new Runnable() {
         public void run() {
            // Kick LOCAL
            if (accessControlEventType.isLocalEventType()) {
               for (IEventListner listener : listeners) {
                  if (listener instanceof IAccessControlEventListener) {
                     // Don't fail on any one listener's exception
                     try {
                        ((IAccessControlEventListener) listener).handleAccessControlArtifactsEvent(sender,
                              accessControlEventType, loadedArtifacts);
                     } catch (Exception ex) {
                        OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
                     }
                  }
               }
            }
            // Kick REMOTE (If source was Local and this was not a default branch changed event
            try {
               if (sender.isLocal() && accessControlEventType.isRemoteEventType()) {
                  Integer branchId = null;
                  if (loadedArtifacts != null && loadedArtifacts.getLoadedArtifacts().size() > 0) {
                     branchId = loadedArtifacts.getLoadedArtifacts().iterator().next().getBranch().getBranchId();
                  }
                  RemoteEventManager.kick(new NetworkAccessControlArtifactsEvent(accessControlEventType.name(),
                        branchId == null ? 0 : branchId, loadedArtifacts.getAllArtifactIds(),
                        loadedArtifacts.getAllArtifactTypeIds(), sender.getNetworkSender()));
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   /**
    * Kick LOCAL event to notify application that the branch to artifact cache has been updated; This event does NOT go
    * external
    * 
    * @param sender
    * @param branchModType
    * @param branchId
    * @throws OseeCoreException
    */
   static void kickLocalBranchToArtifactCacheUpdateEvent(final Sender sender) throws OseeCoreException {
      if (isDisableEvents()) return;
      try {
         if (DEBUG) {
            OseeLog.log(InternalEventManager.class, Level.INFO,
                  "OEM: kickLocalBranchToArtifactCacheUpdateEvent - " + sender);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.INFO, ex);
      }
      Runnable runnable = new Runnable() {
         public void run() {
            // Kick LOCAL
            for (IEventListner listener : listeners) {
               if (listener instanceof IBranchEventListener) {
                  // Don't fail on any one listener's exception
                  try {
                     ((IBranchEventListener) listener).handleLocalBranchToArtifactCacheUpdateEvent(sender);
                  } catch (Exception ex) {
                     OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
                  }
               }
            }
         }
      };
      execute(runnable);
   }

   /**
    * Kick LOCAL artifact modified event; This event does NOT go external
    * 
    * @param sender local if kicked from internal; remote if from external
    * @param loadedArtifacts
    * @throws OseeCoreException
    */
   static void kickArtifactModifiedEvent(final Sender sender, final ArtifactModType artifactModType, final Artifact artifact) throws OseeCoreException {
      if (isDisableEvents()) return;
      try {
         if (DEBUG) {
            OseeLog.log(
                  InternalEventManager.class,
                  Level.INFO,
                  "OEM: kickArtifactModifiedEvent - " + artifactModType + " - " + artifact.getHumanReadableId() + " - " + sender + " - " + artifact.getDirtySkynetAttributeChanges());
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.INFO, ex);
      }
      Runnable runnable = new Runnable() {
         public void run() {
            // Kick LOCAL
            for (IEventListner listener : listeners) {
               if (listener instanceof IArtifactModifiedEventListener) {
                  // Don't fail on any one listener's exception
                  try {
                     ((IArtifactModifiedEventListener) listener).handleArtifactModifiedEvent(sender, artifactModType,
                           artifact);
                  } catch (Exception ex) {
                     OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
                  }
               }
            }
         }
      };
      execute(runnable);
   }

   /**
    * Kick LOCAL relation modified event; This event does NOT go external
    * 
    * @param sender local if kicked from internal; remote if from external
    * @param loadedArtifacts
    * @throws OseeCoreException
    */
   static void kickRelationModifiedEvent(final Sender sender, final RelationModType relationModType, final RelationLink link, final Branch branch, final String relationType) throws OseeCoreException {
      if (isDisableEvents()) return;
      try {
         if (DEBUG) {
            OseeLog.log(InternalEventManager.class, Level.INFO,
                  "OEM: kickRelationModifiedEvent - " + relationModType + " - " + link + " - " + sender);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.INFO, ex);
      }
      Runnable runnable = new Runnable() {
         public void run() {
            // Kick LOCAL
            for (IEventListner listener : listeners) {
               if (listener instanceof IRelationModifiedEventListener) {
                  // Don't fail on any one listener's exception
                  try {
                     ((IRelationModifiedEventListener) listener).handleRelationModifiedEvent(sender, relationModType,
                           link, branch, relationType);
                  } catch (Exception ex) {
                     OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
                  }
               }
            }
         }
      };
      execute(runnable);
   }

   /**
    * Kick LOCAL and REMOTE purged event depending on sender
    * 
    * @param sender local if kicked from internal; remote if from external
    * @param loadedArtifacts
    * @throws OseeCoreException
    */
   static void kickArtifactsPurgedEvent(final Sender sender, final LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      if (isDisableEvents()) return;
      try {
         if (DEBUG) {
            OseeLog.log(InternalEventManager.class, Level.INFO,
                  "OEM: kickArtifactsPurgedEvent " + sender + " - " + loadedArtifacts);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.INFO, ex);
      }
      Runnable runnable = new Runnable() {
         public void run() {
            // Kick LOCAL
            for (IEventListner listener : listeners) {
               if (listener instanceof IArtifactsPurgedEventListener) {
                  // Don't fail on any one listener's exception
                  try {
                     ((IArtifactsPurgedEventListener) listener).handleArtifactsPurgedEvent(sender, loadedArtifacts);
                  } catch (Exception ex) {
                     OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
                  }
               }
            }
            // Kick REMOTE (If source was Local and this was not a default branch changed event
            try {
               if (sender.isLocal()) {
                  RemoteEventManager.kick(new NetworkArtifactPurgeEvent(
                        loadedArtifacts.getLoadedArtifacts().iterator().next().getBranch().getBranchId(),
                        loadedArtifacts.getAllArtifactIds(), loadedArtifacts.getAllArtifactTypeIds(),
                        sender.getNetworkSender()));
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   /**
    * Kick LOCAL and REMOTE artifact change type depending on sender
    * 
    * @param sender local if kicked from internal; remote if from external
    * @param toArtifactTypeId
    * @param loadedArtifacts
    * @throws OseeCoreException
    */
   static void kickArtifactsChangeTypeEvent(final Sender sender, final int toArtifactTypeId, final LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      if (isDisableEvents()) return;
      try {
         if (DEBUG) {
            OseeLog.log(InternalEventManager.class, Level.INFO,
                  "OEM: kickArtifactsChangeTypeEvent " + sender + " - " + loadedArtifacts);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.INFO, ex);
      }
      Runnable runnable = new Runnable() {
         public void run() {
            // Kick LOCAL
            for (IEventListner listener : listeners) {
               if (listener instanceof IArtifactsChangeTypeEventListener) {
                  // Don't fail on any one listener's exception
                  try {
                     ((IArtifactsChangeTypeEventListener) listener).handleArtifactsChangeTypeEvent(sender,
                           toArtifactTypeId, loadedArtifacts);
                  } catch (Exception ex) {
                     OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
                  }
               }
            }
            // Kick REMOTE (If source was Local and this was not a default branch changed event
            try {
               if (sender.isLocal()) {
                  RemoteEventManager.kick(new NetworkArtifactChangeTypeEvent(
                        loadedArtifacts.getLoadedArtifacts().iterator().next().getBranch().getBranchId(),
                        loadedArtifacts.getAllArtifactIds(), loadedArtifacts.getAllArtifactTypeIds(), toArtifactTypeId,
                        sender.getNetworkSender()));
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   /**
    * Kick LOCAL and remote transaction deleted event
    * 
    * @param sender local if kicked from internal; remote if from external
    * @throws OseeCoreException
    */
   static void kickTransactionsDeletedEvent(final Sender sender, final int[] transactionIds) throws OseeCoreException {
      //TODO This needs to be converted into the individual artifacts and relations that were deleted/modified
      if (isDisableEvents()) return;
      try {
         if (DEBUG) {
            OseeLog.log(InternalEventManager.class, Level.INFO,
                  "OEM: kickTransactionsDeletedEvent " + sender + " - " + transactionIds.length);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.INFO, ex);
      }
      Runnable runnable = new Runnable() {
         public void run() {
            // Kick LOCAL
            for (IEventListner listener : listeners) {
               if (listener instanceof ITransactionsDeletedEventListener) {
                  // Don't fail on any one listener's exception
                  try {
                     ((ITransactionsDeletedEventListener) listener).handleTransactionsDeletedEvent(sender,
                           transactionIds);
                  } catch (Exception ex) {
                     OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
                  }
               }
            }
            // Kick REMOTE (If source was Local and this was not a default branch changed event
            try {
               if (sender.isLocal()) {
                  RemoteEventManager.kick(new NetworkTransactionDeletedEvent(sender.getNetworkSender(), transactionIds));
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   /**
    * Kick LOCAL and REMOTE TransactionEvent
    * 
    * @param sender
    * @param xModifiedEvents
    */
   static void kickTransactionEvent(final Sender sender, Collection<ArtifactTransactionModifiedEvent> xModifiedEvents) {
      if (isDisableEvents()) return;
      try {
         if (DEBUG) {
            OseeLog.log(InternalEventManager.class, Level.INFO,
                  "OEM: kickTransactionEvent #ModEvents: " + xModifiedEvents.size() + " - " + sender);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.INFO, ex);
      }
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
               if (!enableRemoteEventLoopback || (enableRemoteEventLoopback && sender.isRemote())) {
                  for (IEventListner listener : listeners) {
                     if (listener instanceof IFrameworkTransactionEventListener) {
                        // Don't fail on any one listener's exception
                        try {
                           ((IFrameworkTransactionEventListener) listener).handleFrameworkTransactionEvent(sender,
                                 transData);
                        } catch (Exception ex) {
                           OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
                        }
                     }
                  }
               }
               // Kick REMOTE (If source was Local and this was not a default branch changed event

               if (sender.isLocal()) {
                  List<ISkynetEvent> events = generateNetworkSkynetEvents(sender, xModifiedEventsCopy);
                  RemoteEventManager.kick(events);
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   /**
    * Add listeners
    * 
    * @param listener
    */
   static void addListener(IEventListner listener) {
      if (listener == null) throw new IllegalArgumentException("listener can not be null");
      listeners.add(listener);
      try {
         if (DEBUG) {
            OseeLog.log(InternalEventManager.class, Level.INFO,
                  "OEM: addListener (" + listeners.size() + ") " + listener);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.INFO, ex);
      }
   }

   static void removeListeners(IEventListner listener) {
      listeners.remove(listener);
      try {
         if (DEBUG) {
            OseeLog.log(InternalEventManager.class, Level.INFO,
                  "OEM: removeListener: (" + listeners.size() + ") " + listener);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.INFO, ex);
      }
   }

   public static String getObjectSafeName(Object object) {
      try {
         return object.toString();
      } catch (Exception ex) {
         return object.getClass().getSimpleName() + " - exception on toString: " + ex.getLocalizedMessage();
      }
   }

   /**
    * @return the disableEvents
    */
   static boolean isDisableEvents() {
      return disableEvents || SkynetDbInit.isDbInit();
   }

   /**
    * @param disableEvents the disableEvents to set
    */
   static void setDisableEvents(boolean disableEvents) {
      InternalEventManager.disableEvents = disableEvents;
   }

   static String getListenerReport() {
      List<String> listenerStrs = new ArrayList<String>();
      for (IEventListner listener : listeners) {
         listenerStrs.add(getObjectSafeName(listener));
      }
      String[] listArr = listenerStrs.toArray(new String[listenerStrs.size()]);
      Arrays.sort(listArr);
      return org.eclipse.osee.framework.jdk.core.util.Collections.toString("\n", (Object[]) listArr);
   }

   private static List<ISkynetEvent> generateNetworkSkynetEvents(Sender sender, Collection<ArtifactTransactionModifiedEvent> xModifiedEvents) {
      List<ISkynetEvent> events = new ArrayList<ISkynetEvent>();
      for (ArtifactTransactionModifiedEvent xModifiedEvent : xModifiedEvents) {
         if (xModifiedEvent instanceof ArtifactModifiedEvent) {
            ArtifactModifiedEvent xArtifactModifiedEvent = (ArtifactModifiedEvent) xModifiedEvent;
            if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Changed) {
               Artifact artifact = xArtifactModifiedEvent.artifact;
               events.add(new NetworkArtifactModifiedEvent(artifact.getBranch().getBranchId(),
                     xArtifactModifiedEvent.transactionNumber, artifact.getArtId(), artifact.getArtTypeId(),
                     artifact.getFactory().getClass().getCanonicalName(),
                     xArtifactModifiedEvent.dirtySkynetAttributeChanges,
                     xArtifactModifiedEvent.sender.getNetworkSender()));
            } else if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Added) {
               Artifact artifact = xArtifactModifiedEvent.artifact;
               events.add(new NetworkArtifactAddedEvent(artifact.getBranch().getBranchId(),
                     xArtifactModifiedEvent.transactionNumber, artifact.getArtId(), artifact.getArtTypeId(),
                     artifact.getFactory().getClass().getCanonicalName(),
                     xArtifactModifiedEvent.sender.getNetworkSender()));
            } else if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Deleted) {
               Artifact artifact = xArtifactModifiedEvent.artifact;
               events.add(new NetworkArtifactDeletedEvent(artifact.getBranch().getBranchId(),
                     xArtifactModifiedEvent.transactionNumber, artifact.getArtId(), artifact.getArtTypeId(),
                     artifact.getFactory().getClass().getCanonicalName(),
                     xArtifactModifiedEvent.sender.getNetworkSender()));
            } else {
               OseeLog.log(InternalEventManager.class, Level.SEVERE,
                     "Unhandled xArtifactModifiedEvent event: " + xArtifactModifiedEvent);
            }
         } else if (xModifiedEvent instanceof RelationModifiedEvent) {
            RelationModifiedEvent xRelationModifiedEvent = (RelationModifiedEvent) xModifiedEvent;
            if (xRelationModifiedEvent.relationModType == RelationModType.ReOrdered) {
               RelationLink link = xRelationModifiedEvent.link;
               Artifact aArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_A);
               Artifact bArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_B);
               NetworkRelationLinkOrderModifiedEvent networkRelationLinkModifiedEvent =
                     new NetworkRelationLinkOrderModifiedEvent(link.getGammaId(), link.getBranch().getBranchId(),
                           link.getRelationId(), link.getAArtifactId(),
                           (aArtifact != null ? aArtifact.getArtTypeId() : -1), link.getBArtifactId(),
                           (bArtifact != null ? bArtifact.getArtTypeId() : -1), link.getRationale(), link.getAOrder(),
                           link.getBOrder(), sender.getNetworkSender(), link.getRelationType().getRelationTypeId());
               events.add(networkRelationLinkModifiedEvent);
            }
            if (xRelationModifiedEvent.relationModType == RelationModType.RationaleMod) {
               RelationLink link = xRelationModifiedEvent.link;
               Artifact aArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_A);
               Artifact bArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_B);
               NetworkRelationLinkRationalModifiedEvent networkRelationLinkRationalModifiedEvent =
                     new NetworkRelationLinkRationalModifiedEvent(link.getGammaId(), link.getBranch().getBranchId(),
                           link.getRelationId(), link.getAArtifactId(),
                           (aArtifact != null ? aArtifact.getArtTypeId() : -1), link.getBArtifactId(),
                           (bArtifact != null ? bArtifact.getArtTypeId() : -1), link.getRationale(), link.getAOrder(),
                           link.getBOrder(), sender.getNetworkSender(), link.getRelationType().getRelationTypeId());
               events.add(networkRelationLinkRationalModifiedEvent);
            } else if (xRelationModifiedEvent.relationModType == RelationModType.Deleted) {
               RelationLink link = xRelationModifiedEvent.link;
               Artifact aArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_A);
               Artifact bArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_B);
               NetworkRelationLinkDeletedEvent networkRelationLinkModifiedEvent =
                     new NetworkRelationLinkDeletedEvent(link.getRelationType().getRelationTypeId(), link.getGammaId(),
                           link.getBranch().getBranchId(), link.getRelationId(),
                           link.getArtifactId(RelationSide.SIDE_A),
                           (aArtifact != null ? aArtifact.getArtTypeId() : -1),
                           link.getArtifactId(RelationSide.SIDE_B),
                           (bArtifact != null ? bArtifact.getArtTypeId() : -1), sender.getNetworkSender());
               events.add(networkRelationLinkModifiedEvent);
            } else if (xRelationModifiedEvent.relationModType == RelationModType.Added) {
               RelationLink link = xRelationModifiedEvent.link;
               Artifact aArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_A);
               Artifact bArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_B);
               NetworkNewRelationLinkEvent networkRelationLinkModifiedEvent =
                     new NetworkNewRelationLinkEvent(link.getGammaId(), link.getBranch().getBranchId(),
                           link.getRelationId(), link.getAArtifactId(),
                           (aArtifact != null ? aArtifact.getArtTypeId() : -1), link.getBArtifactId(),
                           (bArtifact != null ? bArtifact.getArtTypeId() : -1), link.getRationale(), link.getAOrder(),
                           link.getBOrder(), link.getRelationType().getRelationTypeId(),
                           link.getRelationType().getTypeName(), sender.getNetworkSender());
               events.add(networkRelationLinkModifiedEvent);
            } else {
               OseeLog.log(InternalEventManager.class, Level.SEVERE,
                     "Unhandled xRelationModifiedEvent event: " + xRelationModifiedEvent);
            }
         }
      }
      return events;
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
                  if (transData.branchId == -1) transData.branchId =
                        xArtifactModifiedEvent.artifact.getBranch().getBranchId();
               } else {
                  transData.unloadedAddedArtifacts.add(xArtifactModifiedEvent.unloadedArtifact);
                  if (transData.branchId == -1) transData.branchId =
                        xArtifactModifiedEvent.unloadedArtifact.getBranchId();
               }
            }
            if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Deleted) {
               if (xArtifactModifiedEvent.artifact != null) {
                  transData.cacheDeletedArtifacts.add(xArtifactModifiedEvent.artifact);
                  if (transData.branchId == -1) transData.branchId =
                        xArtifactModifiedEvent.artifact.getBranch().getBranchId();
               } else {
                  transData.unloadedDeletedArtifacts.add(xArtifactModifiedEvent.unloadedArtifact);
                  if (transData.branchId == -1) transData.branchId =
                        xArtifactModifiedEvent.unloadedArtifact.getBranchId();
               }
            }
            if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Changed) {
               if (xArtifactModifiedEvent.artifact != null) {
                  transData.cacheChangedArtifacts.add(xArtifactModifiedEvent.artifact);
                  if (transData.branchId == -1) transData.branchId =
                        xArtifactModifiedEvent.artifact.getBranch().getBranchId();
               } else {
                  transData.unloadedChangedArtifacts.add(xArtifactModifiedEvent.unloadedArtifact);
                  if (transData.branchId == -1) transData.branchId =
                        xArtifactModifiedEvent.unloadedArtifact.getBranchId();
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
                  OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
               }
            }
            // Else, get information from unloadedRelation (if != null)
            else if (unloadedRelation != null) {
               Artifact artA =
                     ArtifactCache.getActive(unloadedRelation.getArtifactAId(), unloadedRelation.getBranchId());
               Artifact artB =
                     ArtifactCache.getActive(unloadedRelation.getArtifactBId(), unloadedRelation.getBranchId());
               if (artA != null || artB != null) {
                  try {
                     loadedRelation =
                           new LoadedRelation(artA, artB,
                                 RelationTypeManager.getType(unloadedRelation.getRelationTypeId()),
                                 artA != null ? artA.getBranch() : artB.getBranch(), unloadedRelation);
                  } catch (OseeCoreException ex) {
                     OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
                  }
               }
            }
            if (xRelationModifiedEvent.relationModType == RelationModType.Added) {
               if (loadedRelation != null) {
                  transData.cacheAddedRelations.add(loadedRelation);
                  if (loadedRelation.getArtifactA() != null) {
                     transData.cacheRelationAddedArtifacts.add(loadedRelation.getArtifactA());
                     if (transData.branchId == -1) transData.branchId =
                           loadedRelation.getArtifactA().getBranch().getBranchId();
                  }
                  if (loadedRelation.getArtifactB() != null) {
                     transData.cacheRelationAddedArtifacts.add(loadedRelation.getArtifactB());
                     if (transData.branchId == -1) transData.branchId =
                           loadedRelation.getArtifactB().getBranch().getBranchId();
                  }
               }
               if (unloadedRelation != null) {
                  transData.unloadedAddedRelations.add(unloadedRelation);
               }
            }
            if (xRelationModifiedEvent.relationModType == RelationModType.Deleted) {
               if (loadedRelation != null) {
                  transData.cacheDeletedRelations.add(loadedRelation);
                  if (loadedRelation.getArtifactA() != null) {
                     transData.cacheRelationDeletedArtifacts.add(loadedRelation.getArtifactA());
                     if (transData.branchId == -1) transData.branchId =
                           loadedRelation.getArtifactA().getBranch().getBranchId();
                  }
                  if (loadedRelation.getArtifactB() != null) {
                     transData.cacheRelationDeletedArtifacts.add(loadedRelation.getArtifactB());
                     if (transData.branchId == -1) transData.branchId =
                           loadedRelation.getArtifactB().getBranch().getBranchId();
                  }
               }
               if (unloadedRelation != null) {
                  transData.unloadedDeletedRelations.add(unloadedRelation);
                  if (transData.branchId == -1) transData.branchId = unloadedRelation.getBranchId();
               }
            }
            if (xRelationModifiedEvent.relationModType == RelationModType.ReOrdered || xRelationModifiedEvent.relationModType == RelationModType.RationaleMod) {
               if (loadedRelation != null) {
                  transData.cacheChangedRelations.add(loadedRelation);
                  if (loadedRelation.getArtifactA() != null) {
                     transData.cacheRelationChangedArtifacts.add(loadedRelation.getArtifactA());
                     if (transData.branchId == -1) transData.branchId =
                           loadedRelation.getArtifactA().getBranch().getBranchId();
                  }
                  if (loadedRelation.getArtifactB() != null) {
                     transData.cacheRelationChangedArtifacts.add(loadedRelation.getArtifactB());
                     if (transData.branchId == -1) transData.branchId =
                           loadedRelation.getArtifactB().getBranch().getBranchId();
                  }
               }
               if (unloadedRelation != null) {
                  transData.unloadedChangedRelations.add(unloadedRelation);
                  if (transData.branchId == -1) transData.branchId = unloadedRelation.getBranchId();
               }
            }
         }
      }

      // Clean out known duplicates
      transData.cacheChangedArtifacts.removeAll(transData.cacheDeletedArtifacts);
      transData.cacheAddedArtifacts.removeAll(transData.cacheDeletedArtifacts);

      return transData;
   }

}
