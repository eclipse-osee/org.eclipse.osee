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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
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
import org.eclipse.osee.framework.skynet.core.artifact.BranchEventType;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationModType;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedRelation;

/**
 * @author Donald G. Dunne
 */
public class InternalEventManager {

   private static final HashCollection<Object, IEventListner> listenerMap =
         new HashCollection<Object, IEventListner>(false, HashSet.class, 100);
   public static final Collection<UnloadedArtifact> EMPTY_UNLOADED_ARTIFACTS = Collections.emptyList();
   private static final boolean debug = true;
   private static boolean disableEvents = false;
   private static ExecutorService executorService = Executors.newFixedThreadPool(4);

   /**
    * Kick local remote event manager event
    * 
    * @param sender
    * @param remoteEventModType
    * @throws OseeCoreException
    */
   static void kickRemoteEventManagerEvent(Sender sender, RemoteEventModType remoteEventModType) throws OseeCoreException {
      if (isDisableEvents()) return;
      if (debug) System.out.println("kickRemoteEventManagerEvent " + sender.getNetworkSender() + " remoteEventModType: " + remoteEventModType);
      // Kick Local
      for (IEventListner listener : listenerMap.getValues()) {
         if (listener instanceof IRemoteEventManagerEventListener) {
            // Don't fail on any one listener's exception
            try {
               ((IRemoteEventManagerEventListener) listener).handleRemoteEventManagerEvent(sender, remoteEventModType);
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      }
   }

   /**
    * Kick Local and Remote broadcast event
    * 
    * @param sender
    * @param broadcastEventType
    * @param userIds (currently only used for disconnect_skynet)
    * @param message
    * @throws OseeCoreException
    */
   static void kickBroadcastEvent(final Sender sender, final BroadcastEventType broadcastEventType, final String[] userIds, final String message) throws OseeCoreException {
      if (isDisableEvents()) return;
      if (debug) System.out.println("kickBroadcastEvent " + sender + " message: " + message);
      Runnable runnable = new Runnable() {
         public void run() {
            // Kick Local
            if (broadcastEventType == BroadcastEventType.Message) {
               for (IEventListner listener : listenerMap.getValues()) {
                  if (listener instanceof IBroadcastEventListneer) {
                     // Don't fail on any one listener's exception
                     try {
                        ((IBroadcastEventListneer) listener).handleBroadcastEvent(sender, broadcastEventType, userIds,
                              message);
                     } catch (Exception ex) {
                        SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
                     }
                  }
               }
            }
            // Kick Remote (If source was Local and this was not a default branch changed event
            try {
               RemoteEventManager.kick(new NetworkBroadcastEvent(message, broadcastEventType.name(),
                     sender.getNetworkSender()));
            } catch (Exception ex) {
               throw new OseeCoreException(ex);
            }
         }
      };
      execute(runnable);
   }

   /**
    * Kick local and remote branch events
    * 
    * @param sender
    * @param branchModType
    * @param branchId
    * @throws OseeCoreException
    */
   static void kickBranchEvent(final Sender sender, final BranchEventType branchModType, final int branchId) {
      if (isDisableEvents()) return;
      if (debug) System.out.println("kickBranchEvent - type: " + branchModType + " id: " + branchId);
      Runnable runnable = new Runnable() {
         public void run() {
            Branch branch = null;
            try {
               branch = BranchPersistenceManager.getBranch(branchId);
            } catch (Exception ex) {
               // do nothing
            }
            // Kick Local
            for (IEventListner listener : listenerMap.getValues()) {
               if (listener instanceof IBranchEventListener) {
                  // Don't fail on any one listener's exception
                  try {
                     ((IBranchEventListener) listener).handleBranchEvent(sender, branchModType, branchId);
                  } catch (Exception ex) {
                     SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
                  }
               }
            }
            // Kick Remote (If source was Local and this was not a default branch changed event
            try {
               if (sender.isLocal() && branchModType != BranchEventType.DefaultBranchChanged) {
                  if (branchModType == BranchEventType.Added) {
                     RemoteEventManager.kick(new NetworkNewBranchEvent(branchId, sender.getNetworkSender()));
                  } else if (branchModType == BranchEventType.Deleted) {
                     RemoteEventManager.kick(new NetworkDeletedBranchEvent(branchId, sender.getNetworkSender()));
                  } else if (branchModType == BranchEventType.Committed) {
                     RemoteEventManager.kick(new NetworkCommitBranchEvent(branchId, sender.getNetworkSender()));
                  } else if (branchModType == BranchEventType.Renamed) {
                     RemoteEventManager.kick(new NetworkRenameBranchEvent(branchId, sender.getNetworkSender(),
                           branch.getBranchName(), branch.getBranchShortName()));
                  }
               }
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      };
      execute(runnable);
   }

   private static void execute(Runnable runnable) {
      executorService.submit(runnable);
   }

   /**
    * Kick local and remote access control events
    * 
    * @param sender
    * @param branchModType
    * @param branchId
    * @throws OseeCoreException
    */
   static void kickAccessControlArtifactsEvent(final Sender sender, final AccessControlModType accessControlModType, final LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      if (sender == null) throw new IllegalArgumentException("sender can not be null");
      if (accessControlModType == null) throw new IllegalArgumentException("accessControlModType can not be null");
      if (loadedArtifacts == null) throw new IllegalArgumentException("loadedArtifacts can not be null");
      if (isDisableEvents()) return;
      if (debug) System.out.println("kickAccessControlEvent - type: " + accessControlModType + " sender: " + sender + " loadedArtifacts: " + loadedArtifacts);
      // Kick Local
      for (IEventListner listener : listenerMap.getValues()) {
         if (listener instanceof IAccessControlEventListener) {
            // Don't fail on any one listener's exception
            try {
               ((IAccessControlEventListener) listener).handleAccessControlArtifactsEvent(sender, accessControlModType,
                     loadedArtifacts);
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      }
      // Kick Remote (If source was Local and this was not a default branch changed event
      try {
         if (sender.isLocal()) {
            Integer branchId = null;
            if (loadedArtifacts != null && loadedArtifacts.getLoadedArtifacts().size() > 0) {
               branchId = loadedArtifacts.getLoadedArtifacts().iterator().next().getBranch().getBranchId();
            }
            RemoteEventManager.kick(new NetworkAccessControlArtifactsEvent(accessControlModType.name(),
                  branchId == null ? 0 : branchId, loadedArtifacts.getAllArtifactIds(),
                  loadedArtifacts.getAllArtifactTypeIds(), sender.getNetworkSender()));
         }
      } catch (Exception ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   /**
    * Kick local event to notify application that the branch to artifact cache has been updated
    * 
    * @param sender
    * @param branchModType
    * @param branchId
    * @throws OseeCoreException
    */
   static void kickLocalBranchToArtifactCacheUpdateEvent(Sender sender) throws OseeCoreException {
      if (isDisableEvents()) return;
      if (debug) System.out.println("kickLocalBranchToArtifactCacheUpdateEvent " + sender.getNetworkSender());
      // Kick Local
      for (IEventListner listener : listenerMap.getValues()) {
         if (listener instanceof IBranchEventListener) {
            // Don't fail on any one listener's exception
            try {
               ((IBranchEventListener) listener).handleLocalBranchToArtifactCacheUpdateEvent(sender);
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      }
   }

   /**
    * Kick local artifact modified event; This event does NOT go external
    * 
    * @param sender local if kicked from internal; remote if from external
    * @param loadedArtifacts
    * @throws OseeCoreException
    */
   static void kickArtifactModifiedEvent(Sender sender, ArtifactModType artifactModType, Artifact artifact) throws OseeCoreException {
      if (isDisableEvents()) return;
      if (debug) System.out.println("kickArtifactModifiedEvent " + sender + " - " + artifactModType + " - " + artifact.getHumanReadableId() + " - " + artifact.getDirtySkynetAttributeChanges());
      // Kick Local
      for (IEventListner listener : listenerMap.getValues()) {
         if (listener instanceof IArtifactModifiedEventListener) {
            // Don't fail on any one listener's exception
            try {
               ((IArtifactModifiedEventListener) listener).handleArtifactModifiedEvent(sender, artifactModType,
                     artifact);
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      }
   }

   /**
    * Kick local relation modified event; This event does NOT go external
    * 
    * @param sender local if kicked from internal; remote if from external
    * @param loadedArtifacts
    * @throws OseeCoreException
    */
   static void kickRelationModifiedEvent(Sender sender, RelationModType relationModType, RelationLink link, Branch branch, String relationType, String relationSide) throws OseeCoreException {
      if (isDisableEvents()) return;
      if (debug) System.out.println("kickRelationModifiedEvent " + sender + " - " + relationType + " - " + link.getRelationType());
      // Kick Local
      for (IEventListner listener : listenerMap.getValues()) {
         if (listener instanceof IRelationModifiedEventListener) {
            // Don't fail on any one listener's exception
            try {
               ((IRelationModifiedEventListener) listener).handleRelationModifiedEvent(sender, relationModType, link,
                     branch, relationType, relationSide);
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      }
   }

   /**
    * Kick local and remote purged event depending on sender
    * 
    * @param sender local if kicked from internal; remote if from external
    * @param loadedArtifacts
    * @throws OseeCoreException
    */
   static void kickArtifactsPurgedEvent(Sender sender, LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      if (isDisableEvents()) return;
      if (debug) System.out.println("kickArtifactsPurgedEvent " + sender + " - " + loadedArtifacts);
      // Kick Local
      for (IEventListner listener : listenerMap.getValues()) {
         if (listener instanceof IArtifactsPurgedEventListener) {
            // Don't fail on any one listener's exception
            try {
               ((IArtifactsPurgedEventListener) listener).handleArtifactsPurgedEvent(sender,
                     loadedArtifacts.getLoadedArtifacts(), EMPTY_UNLOADED_ARTIFACTS);
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      }
      // Kick Remote (If source was Local)
      try {
         if (sender.isLocal()) {
            RemoteEventManager.kick(new NetworkArtifactPurgeEvent(
                  loadedArtifacts.getLoadedArtifacts().iterator().next().getBranch().getBranchId(),
                  loadedArtifacts.getAllArtifactIds(), loadedArtifacts.getAllArtifactTypeIds(),
                  sender.getNetworkSender()));
         }
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   /**
    * Kick local and remote artifact change type depending on sender
    * 
    * @param sender local if kicked from internal; remote if from external
    * @param toArtifactTypeId
    * @param loadedArtifacts
    * @throws OseeCoreException
    */
   static void kickArtifactsChangeTypeEvent(Sender sender, int toArtifactTypeId, LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      if (isDisableEvents()) return;
      if (debug) System.out.println("kickArtifactsChangeTypeEvent " + sender + " - " + loadedArtifacts);
      // Kick Local
      for (IEventListner listener : listenerMap.getValues()) {
         if (listener instanceof IArtifactsChangeTypeEventListener) {
            // Don't fail on any one listener's exception
            try {
               ((IArtifactsChangeTypeEventListener) listener).handleArtifactsChangeTypeEvent(sender, toArtifactTypeId,
                     loadedArtifacts.getLoadedArtifacts(), EMPTY_UNLOADED_ARTIFACTS);
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      }
      // Kick Remote (If source was Local)
      try {
         if (sender.isLocal()) {
            RemoteEventManager.kick(new NetworkArtifactChangeTypeEvent(
                  loadedArtifacts.getLoadedArtifacts().iterator().next().getBranch().getBranchId(),
                  loadedArtifacts.getAllArtifactIds(), loadedArtifacts.getAllArtifactTypeIds(), toArtifactTypeId,
                  sender.getNetworkSender()));
         }
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   /**
    * Kick local and remote transaction deleted event
    * 
    * @param sender local if kicked from internal; remote if from external
    * @throws OseeCoreException
    */
   static void kickTransactionsDeletedEvent(Sender sender, int[] transactionIds) throws OseeCoreException {
      if (isDisableEvents()) return;
      if (debug) System.out.println("kickTransactionsDeletedEvent " + sender + " - " + transactionIds.length);
      // Kick Local
      for (IEventListner listener : listenerMap.getValues()) {
         if (listener instanceof IArtifactsChangeTypeEventListener) {
            // Don't fail on any one listener's exception
            try {
               ((ITransactionsDeletedEventListener) listener).handleTransactionsDeletedEvent(sender, transactionIds);
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      }
      // Kick Remote (If source was Local)
      try {
         if (sender.isLocal()) {
            RemoteEventManager.kick(new NetworkTransactionDeletedEvent(sender.getNetworkSender(), transactionIds));
         }
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   static void kickTransactionEvent(Sender sender, Collection<ArtifactTransactionModifiedEvent> xModifiedEvents) {
      if (isDisableEvents()) return;
      if (debug) System.out.println("kickTransactionEvent " + sender + " #ModEvents: " + xModifiedEvents.size());
      // Roll-up change information
      FrameworkTransactionData transData = new FrameworkTransactionData();

      for (ArtifactTransactionModifiedEvent xModifiedEvent : xModifiedEvents) {
         if (xModifiedEvent instanceof ArtifactModifiedEvent) {
            ArtifactModifiedEvent xArtifactModifiedEvent = (ArtifactModifiedEvent) xModifiedEvent;
            if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Added) {
               if (xArtifactModifiedEvent.artifact != null) {
                  transData.cacheAddedArtifacts.add(xArtifactModifiedEvent.artifact);
                  if (transData.branchId == null) transData.branchId =
                        xArtifactModifiedEvent.artifact.getBranch().getBranchId();
               } else {
                  transData.unloadedAddedArtifacts.add(xArtifactModifiedEvent.unloadedArtifact);
                  if (transData.branchId == null) transData.branchId =
                        xArtifactModifiedEvent.unloadedArtifact.getBranchId();
               }
            }
            if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Deleted) {
               if (xArtifactModifiedEvent.artifact != null) {
                  transData.cacheDeletedArtifacts.add(xArtifactModifiedEvent.artifact);
                  if (transData.branchId == null) transData.branchId =
                        xArtifactModifiedEvent.artifact.getBranch().getBranchId();
               } else {
                  transData.unloadedDeletedArtifacts.add(xArtifactModifiedEvent.unloadedArtifact);
                  if (transData.branchId == null) transData.branchId =
                        xArtifactModifiedEvent.unloadedArtifact.getBranchId();
               }
            }
            if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Changed) {
               if (xArtifactModifiedEvent.artifact != null) {
                  transData.cacheChangedArtifacts.add(xArtifactModifiedEvent.artifact);
                  if (transData.branchId == null) transData.branchId =
                        xArtifactModifiedEvent.artifact.getBranch().getBranchId();
               } else {
                  transData.unloadedChangedArtifacts.add(xArtifactModifiedEvent.unloadedArtifact);
                  if (transData.branchId == null) transData.branchId =
                        xArtifactModifiedEvent.unloadedArtifact.getBranchId();
               }
            }
         }
         if (xModifiedEvent instanceof RelationModifiedEvent) {
            RelationModifiedEvent xRelationModifiedEvent = (RelationModifiedEvent) xModifiedEvent;
            UnloadedRelation unloadedRelation = xRelationModifiedEvent.unloadedRelation;
            LoadedRelation loadedRelation = null;
            if (xRelationModifiedEvent.link != null) {
               try {
                  loadedRelation =
                        new LoadedRelation(xRelationModifiedEvent.link.getArtifactA(),
                              xRelationModifiedEvent.link.getArtifactB(),
                              xRelationModifiedEvent.link.getRelationType(), xRelationModifiedEvent.branch,
                              unloadedRelation);
               } catch (Exception ex) {
                  SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
               }
            }
            if (unloadedRelation != null) {
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
                  } catch (Exception ex) {
                     SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
                  }
               }
            }
            if (xRelationModifiedEvent.relationModType == RelationModType.Added) {
               if (loadedRelation != null) {
                  transData.cacheAddedRelations.add(loadedRelation);
                  if (loadedRelation.getArtifactA() != null) {
                     transData.cacheRelationAddedArtifacts.add(loadedRelation.getArtifactA());
                     if (transData.branchId == null) transData.branchId =
                           loadedRelation.getArtifactA().getBranch().getBranchId();
                  }
                  if (loadedRelation.getArtifactB() != null) {
                     transData.cacheRelationAddedArtifacts.add(loadedRelation.getArtifactB());
                     if (transData.branchId == null) transData.branchId =
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
                     if (transData.branchId == null) transData.branchId =
                           loadedRelation.getArtifactA().getBranch().getBranchId();
                  }
                  if (loadedRelation.getArtifactB() != null) {
                     transData.cacheRelationDeletedArtifacts.add(loadedRelation.getArtifactB());
                     if (transData.branchId == null) transData.branchId =
                           loadedRelation.getArtifactB().getBranch().getBranchId();
                  }
               }
               if (unloadedRelation != null) {
                  transData.unloadedDeletedRelations.add(unloadedRelation);
                  if (transData.branchId == null) transData.branchId = unloadedRelation.getBranchId();
               }
            }
            if (xRelationModifiedEvent.relationModType == RelationModType.ReOrdered || xRelationModifiedEvent.relationModType == RelationModType.RationaleMod) {
               if (loadedRelation != null) {
                  transData.cacheChangedRelations.add(loadedRelation);
                  if (loadedRelation.getArtifactA() != null) {
                     transData.cacheRelationChangedArtifacts.add(loadedRelation.getArtifactA());
                     if (transData.branchId == null) transData.branchId =
                           loadedRelation.getArtifactA().getBranch().getBranchId();
                  }
                  if (loadedRelation.getArtifactB() != null) {
                     transData.cacheRelationChangedArtifacts.add(loadedRelation.getArtifactB());
                     if (transData.branchId == null) transData.branchId =
                           loadedRelation.getArtifactB().getBranch().getBranchId();
                  }
               }
               if (unloadedRelation != null) {
                  transData.unloadedChangedRelations.add(unloadedRelation);
                  if (transData.branchId == null) transData.branchId = unloadedRelation.getBranchId();
               }
            }
         }
      }

      // Clean out known duplicates
      transData.cacheChangedArtifacts.removeAll(transData.cacheDeletedArtifacts);
      transData.cacheAddedArtifacts.removeAll(transData.cacheDeletedArtifacts);

      // Kick Local
      for (IEventListner listener : listenerMap.getValues()) {
         if (listener instanceof IFrameworkTransactionEventListener) {
            // Don't fail on any one listener's exception
            try {
               ((IFrameworkTransactionEventListener) listener).handleFrameworkTransactionEvent(sender, transData);
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      }
      // Kick Remote (If sender was Local)
      try {
         if (sender.isLocal()) {
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
                     SkynetActivator.getLogger().log(Level.SEVERE,
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
                                 (bArtifact != null ? bArtifact.getArtTypeId() : -1), link.getRationale(),
                                 link.getAOrder(), link.getBOrder(), sender.getNetworkSender(),
                                 link.getRelationType().getRelationTypeId());
                     events.add(networkRelationLinkModifiedEvent);
                  }
                  if (xRelationModifiedEvent.relationModType == RelationModType.RationaleMod) {
                     RelationLink link = xRelationModifiedEvent.link;
                     Artifact aArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_A);
                     Artifact bArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_B);
                     NetworkRelationLinkRationalModifiedEvent networkRelationLinkRationalModifiedEvent =
                           new NetworkRelationLinkRationalModifiedEvent(link.getGammaId(),
                                 link.getBranch().getBranchId(), link.getRelationId(), link.getAArtifactId(),
                                 (aArtifact != null ? aArtifact.getArtTypeId() : -1), link.getBArtifactId(),
                                 (bArtifact != null ? bArtifact.getArtTypeId() : -1), link.getRationale(),
                                 link.getAOrder(), link.getBOrder(), sender.getNetworkSender(),
                                 link.getRelationType().getRelationTypeId());
                     events.add(networkRelationLinkRationalModifiedEvent);
                  } else if (xRelationModifiedEvent.relationModType == RelationModType.Deleted) {
                     RelationLink link = xRelationModifiedEvent.link;
                     Artifact aArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_A);
                     Artifact bArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_B);
                     NetworkRelationLinkDeletedEvent networkRelationLinkModifiedEvent =
                           new NetworkRelationLinkDeletedEvent(link.getRelationType().getRelationTypeId(),
                                 link.getGammaId(), link.getBranch().getBranchId(), link.getRelationId(),
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
                                 (bArtifact != null ? bArtifact.getArtTypeId() : -1), link.getRationale(),
                                 link.getAOrder(), link.getBOrder(), link.getRelationType().getRelationTypeId(),
                                 link.getRelationType().getTypeName(), sender.getNetworkSender());
                     events.add(networkRelationLinkModifiedEvent);
                  } else {
                     SkynetActivator.getLogger().log(Level.SEVERE,
                           "Unhandled xRelationModifiedEvent event: " + xRelationModifiedEvent);
                  }
               }
            }
         }
      } catch (Exception ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   /**
    * Add listeners
    * 
    * @param key unique object that will allow for removing all or specific listeners in removeListners
    * @param listener
    */
   static void addListener(Object key, IEventListner listener) {
      if (key == null) throw new IllegalArgumentException("key can not be null");
      if (listener == null) throw new IllegalArgumentException("listener can not be null");
      if (debug) System.out.println("addListener " + key + " - " + listener);
      listenerMap.put(key, listener);
   }

   static void removeListener(Object key, IEventListner listener) {
      if (key == null) throw new IllegalArgumentException("key can not be null");
      if (listener == null) throw new IllegalArgumentException("listener can not be null");
      if (debug) System.out.println("removeListener " + key + " - " + listener);
      listenerMap.removeValue(key, listener);
   }

   static void removeListeners(Object key) {
      if (key == null) throw new IllegalArgumentException("key can not be null");
      if (debug) System.out.println("removeListeners ALL " + key);
      Set<IEventListner> listenersToRemove = new HashSet<IEventListner>();
      for (IEventListner listener : listenerMap.getValues(key)) {
         listenersToRemove.add(listener);
      }
      // Done to avoid concurrent modification
      for (IEventListner listener : listenersToRemove) {
         listenerMap.removeValue(key, listener);
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

}
