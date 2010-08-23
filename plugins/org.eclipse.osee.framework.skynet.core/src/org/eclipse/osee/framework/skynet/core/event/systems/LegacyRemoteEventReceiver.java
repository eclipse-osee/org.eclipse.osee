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
package org.eclipse.osee.framework.skynet.core.event.systems;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.skynet.ASkynetEventListener;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetArtifactEvent;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetRelationLinkEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkAccessControlArtifactsEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactChangeTypeEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactModifiedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactPurgeEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkBroadcastEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkCommitBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkDeletedBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkNewBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkPurgeBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkCreatedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkRationalModifiedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRenameBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkTransactionDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetAttributeChange;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.event.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.event.ArtifactTransactionModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.BroadcastEventType;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.AccessControlEvent;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedRelation;

/**
 * @author Donald G. Dunne
 */
public final class LegacyRemoteEventReceiver extends ASkynetEventListener {
   private static final long serialVersionUID = -3017349745450262540L;

   private static final ISchedulingRule mutexRule = new ISchedulingRule() {

      @Override
      public boolean contains(ISchedulingRule rule) {
         return rule == this;
      }

      @Override
      public boolean isConflicting(ISchedulingRule rule) {
         return rule == this;
      }
   };

   private LegacyEventManager legacyEventManager;

   public LegacyRemoteEventReceiver() {
      super();
   }

   public void setInternalEventManager(LegacyEventManager legacyEventManager) {
      this.legacyEventManager = legacyEventManager;
   }

   @Override
   public void onEvent(final ISkynetEvent[] events) {
      final List<ArtifactTransactionModifiedEvent> xModifiedEvents = new LinkedList<ArtifactTransactionModifiedEvent>();
      Job job = new Job("Receive Event") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               Sender lastArtifactRelationModChangeSender = null;

               for (ISkynetEvent event : events) {

                  Sender sender = new Sender(event.getNetworkSender());
                  // If the sender's sessionId is the same as this client, then this event was
                  // created in this client and returned by remote event manager; ignore and continue
                  if (sender.isLocal()) {
                     continue;
                  }

                  if (event instanceof NetworkAccessControlArtifactsEvent) {
                     try {
                        AccessControlEvent accessControlEvent = new AccessControlEvent();
                        AccessControlEventType accessControlModType =
                           AccessControlEventType.valueOf(((NetworkAccessControlArtifactsEvent) event).getAccessControlModTypeName());
                        accessControlEvent.setEventType(accessControlModType);
                        NetworkAccessControlArtifactsEvent accessEvent = (NetworkAccessControlArtifactsEvent) event;
                        Integer[] artIds =
                           accessEvent.getArtifactIds().toArray(new Integer[accessEvent.getArtifactIds().size()]);
                        Branch branch = BranchManager.getBranch(accessEvent.getId());
                        for (int x = 0; x < accessEvent.getArtifactIds().size(); x++) {
                           Artifact cachedArt = ArtifactQuery.getArtifactFromId(artIds[x], branch);
                           if (cachedArt != null) {
                              accessControlEvent.getArtifacts().add(cachedArt.getBasicGuidArtifact());
                           }
                        }
                        LoadedArtifacts loadedArtifacts =
                           new LoadedArtifacts(accessEvent.getId(), accessEvent.getArtifactIds(),
                              accessEvent.getArtifactTypeIds());
                        OseeEventManager.kickAccessControlArtifactsEvent(sender, accessControlEvent, loadedArtifacts);
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkRenameBranchEvent) {
                     int branchId = ((NetworkRenameBranchEvent) event).getId();
                     try {
                        Branch branch = BranchManager.getBranch(branchId);
                        branch.setName(((NetworkRenameBranchEvent) event).getBranchName());
                        branch.clearDirty();
                        try {
                           legacyEventManager.kickBranchEvent(sender, BranchEventType.Renamed, branchId);
                        } catch (Exception ex) {
                           OseeLog.log(Activator.class, Level.SEVERE, ex);
                        }
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkNewBranchEvent) {
                     int branchId = ((NetworkNewBranchEvent) event).getId();
                     try {
                        legacyEventManager.kickBranchEvent(sender, BranchEventType.Added, branchId);
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkDeletedBranchEvent) {
                     int branchId = ((NetworkDeletedBranchEvent) event).getId();
                     try {
                        Branch branch =
                           Activator.getInstance().getOseeCacheService().getBranchCache().getById(branchId);
                        if (branch != null) {
                           branch.setBranchState(BranchState.DELETED);
                           branch.clearDirty();
                        }
                        legacyEventManager.kickBranchEvent(sender, BranchEventType.Deleted, branchId);
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkPurgeBranchEvent) {
                     int branchId = ((NetworkPurgeBranchEvent) event).getId();
                     try {
                        BranchCache cache = Activator.getInstance().getOseeCacheService().getBranchCache();
                        Branch branch = cache.getById(branchId);
                        if (branch != null) {
                           cache.decache(branch);
                        }
                        legacyEventManager.kickBranchEvent(sender, BranchEventType.Purged, branchId);
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkCommitBranchEvent) {
                     int branchId = ((NetworkCommitBranchEvent) event).getId();
                     try {
                        try {
                           TransactionManager.clearCommitArtifactCacheForAssociatedArtifact(BranchManager.getAssociatedArtifact(BranchManager.getBranch(branchId)));
                        } catch (OseeCoreException ex) {
                           OseeLog.log(Activator.class, Level.SEVERE, ex);
                        }
                        legacyEventManager.kickBranchEvent(sender, BranchEventType.Committed, branchId);
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkBroadcastEvent) {
                     try {
                        final BroadcastEventType broadcastEventType =
                           BroadcastEventType.valueOf(((NetworkBroadcastEvent) event).getBroadcastEventTypeName());
                        if (broadcastEventType == null) {
                           OseeLog.log(
                              Activator.class,
                              Level.SEVERE,
                              "Unknown broadcast event type \"" + ((NetworkBroadcastEvent) event).getBroadcastEventTypeName() + "\"",
                              new IllegalArgumentException());
                        } else {
                           legacyEventManager.kickBroadcastEvent(sender, broadcastEventType,
                              ((NetworkBroadcastEvent) event).getUserIds(),
                              ((NetworkBroadcastEvent) event).getMessage());
                        }
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof ISkynetArtifactEvent) {
                     try {
                        updateArtifacts(sender, (ISkynetArtifactEvent) event, xModifiedEvents);
                        lastArtifactRelationModChangeSender = sender;
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof ISkynetRelationLinkEvent) {
                     try {
                        updateRelations(sender, (ISkynetRelationLinkEvent) event, xModifiedEvents);
                        lastArtifactRelationModChangeSender = sender;
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkArtifactChangeTypeEvent) {
                     try {
                        LoadedArtifacts loadedArtifacts =
                           new LoadedArtifacts(((NetworkArtifactChangeTypeEvent) event).getId(),
                              ((NetworkArtifactChangeTypeEvent) event).getArtifactIds(),
                              ((NetworkArtifactChangeTypeEvent) event).getArtifactTypeIds());
                        legacyEventManager.kickArtifactsChangeTypeEvent(sender,
                           ((NetworkArtifactChangeTypeEvent) event).getToArtifactTypeId(), loadedArtifacts);
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkArtifactPurgeEvent) {
                     try {
                        LoadedArtifacts loadedArtifacts =
                           new LoadedArtifacts(((NetworkArtifactPurgeEvent) event).getId(),
                              ((NetworkArtifactPurgeEvent) event).getArtifactIds(),
                              ((NetworkArtifactPurgeEvent) event).getArtifactTypeIds());
                        for (Artifact artifact : loadedArtifacts.getLoadedArtifacts()) {
                           //This is because applications may still have a reference to the artifact
                           for (RelationLink link : RelationManager.getRelationsAll(artifact.getArtId(),
                              artifact.getBranch().getId(), false)) {
                              link.internalRemoteEventDelete();
                           }
                           ArtifactCache.deCache(artifact);
                           artifact.internalSetDeleted();
                        }
                        legacyEventManager.kickArtifactsPurgedEvent(sender, loadedArtifacts);
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkTransactionDeletedEvent) {
                     try {
                        legacyEventManager.kickTransactionsPurgedEvent(sender,
                           ((NetworkTransactionDeletedEvent) event).getTransactionIds());
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  }
               }

               if (xModifiedEvents.size() > 0) {
                  /*
                   * Since transaction events are a collection of ArtifactModfied and RelationModified events, create a
                   * new Sender based on the last sender for these events.
                   */
                  Sender transactionSender =
                     new Sender("RemoteEventManager", lastArtifactRelationModChangeSender.getOseeSession());
                  legacyEventManager.kickPersistEvent(transactionSender, xModifiedEvents);
               }
            } catch (Exception ex) {
               // don't want exceptions poping up; just log and return nicely
               OseeLog.log(Activator.class, Level.SEVERE, "REM Receive Event Exception", ex);
            }
            return Status.OK_STATUS;
         }

      };
      job.setSystem(true);
      job.setUser(false);
      job.setRule(mutexRule);
      job.schedule();
   }

   /**
    * Updates local cache
    * 
    * @param event
    */
   private void updateArtifacts(Sender sender, ISkynetArtifactEvent event, Collection<ArtifactTransactionModifiedEvent> xModifiedEvents) {
      if (event == null) {
         return;
      }

      try {
         int artId = event.getArtId();
         int artTypeId = event.getArtTypeId();
         List<String> dirtyAttributeName = new LinkedList<String>();

         if (event instanceof NetworkArtifactModifiedEvent) {
            int branchId = ((NetworkArtifactModifiedEvent) event).getId();
            Artifact artifact = ArtifactCache.getActive(artId, branchId);
            if (artifact == null) {
               UnloadedArtifact unloadedArtifact = new UnloadedArtifact(branchId, artId, artTypeId);
               xModifiedEvents.add(new ArtifactModifiedEvent(sender, ArtifactModType.Changed, unloadedArtifact));
            } else if (!artifact.isHistorical()) {
               for (SkynetAttributeChange skynetAttributeChange : ((NetworkArtifactModifiedEvent) event).getAttributeChanges()) {
                  if (!OseeEventManager.getPreferences().isEnableRemoteEventLoopback()) {
                     try {
                        Attribute<?> attribute =
                           artifact.getAttributeById(skynetAttributeChange.getAttributeId(), true);
                        // Attribute already exists (but may be deleted), process update
                        // Process MODIFIED / DELETED attribute
                        if (attribute != null) {
                           if (attribute.isDirty()) {
                              dirtyAttributeName.add(attribute.getNameValueDescription());
                              EventUtil.eventLog(String.format("%s's attribute %d [/n%s/n] has been overwritten.",
                                 artifact.getSafeName(), attribute.getId(), attribute.toString()));
                           }
                           try {
                              ModificationType modificationType = skynetAttributeChange.getModificationType();
                              if (modificationType == null) {
                                 OseeLog.log(
                                    Activator.class,
                                    Level.SEVERE,
                                    String.format("MOD1: Can't get mod type for %s's attribute %d.",
                                       artifact.getArtifactTypeName(), skynetAttributeChange.getAttributeId()));
                                 continue;
                              }
                              if (modificationType.isDeleted()) {
                                 attribute.internalSetModificationType(modificationType);
                              } else {
                                 attribute.getAttributeDataProvider().loadData(skynetAttributeChange.getData());
                              }
                              attribute.internalSetGammaId(skynetAttributeChange.getGammaId());
                              attribute.setNotDirty();
                           } catch (OseeCoreException ex) {
                              EventUtil.eventLog(String.format("Exception updating %s's attribute %d [/n%s/n].",
                                 artifact.getSafeName(), attribute.getId(), attribute.toString()), ex);
                           }
                        }
                        // Otherwise, attribute needs creation
                        // Process NEW attribute
                        else {
                           ModificationType modificationType = skynetAttributeChange.getModificationType();
                           if (modificationType == null) {
                              OseeLog.log(
                                 Activator.class,
                                 Level.SEVERE,
                                 String.format("MOD2: Can't get mod type for %s's attribute %d.",
                                    artifact.getArtifactTypeName(), skynetAttributeChange.getAttributeId()));
                              continue;
                           }
                           artifact.internalInitializeAttribute(
                              AttributeTypeManager.getType(skynetAttributeChange.getTypeId()),
                              skynetAttributeChange.getAttributeId(), skynetAttributeChange.getGammaId(),
                              modificationType, false, skynetAttributeChange.getData());
                        }
                     } catch (OseeCoreException ex) {
                        EventUtil.eventLog(
                           String.format("Exception updating %s's attribute change for attributeTypeId %d.",
                              artifact.getSafeName(), skynetAttributeChange.getTypeId()), ex);
                     }
                  }
               }

               xModifiedEvents.add(new ArtifactModifiedEvent(sender, ArtifactModType.Changed, artifact,
                  ((NetworkArtifactModifiedEvent) event).getTransactionId(),
                  ((NetworkArtifactModifiedEvent) event).getAttributeChanges()));

            }
         } else if (event instanceof NetworkArtifactDeletedEvent) {
            int branchId = ((NetworkArtifactDeletedEvent) event).getId();
            Artifact artifact = ArtifactCache.getActive(artId, branchId);
            if (artifact == null) {
               UnloadedArtifact unloadedArtifact = new UnloadedArtifact(branchId, artId, artTypeId);
               xModifiedEvents.add(new ArtifactModifiedEvent(sender, ArtifactModType.Deleted, unloadedArtifact));
            } else if (!artifact.isHistorical()) {
               internalHandleRemoteArtifactDeleted(artifact);

               xModifiedEvents.add(new ArtifactModifiedEvent(sender, ArtifactModType.Deleted, artifact,
                  ((NetworkArtifactDeletedEvent) event).getTransactionId(), new ArrayList<SkynetAttributeChange>()));
            }
         }
      } catch (OseeCoreException ex) {
         EventUtil.eventLog("Update Artifacts", ex);
      }
   }

   private void updateRelations(Sender sender, ISkynetRelationLinkEvent event, Collection<ArtifactTransactionModifiedEvent> xModifiedEvents) {
      if (event == null) {
         return;
      }

      try {
         RelationType relationType = RelationTypeManager.getType(event.getRelTypeId());
         Branch branch = BranchManager.getBranch(event.getId());
         Artifact aArtifact = ArtifactCache.getActive(event.getArtAId(), branch.getId());
         Artifact bArtifact = ArtifactCache.getActive(event.getArtBId(), branch.getId());
         boolean aArtifactLoaded = aArtifact != null;
         boolean bArtifactLoaded = bArtifact != null;

         if (!aArtifactLoaded && !bArtifactLoaded) {
            if (event instanceof NetworkRelationLinkDeletedEvent) {
               UnloadedRelation unloadedRelation =
                  new UnloadedRelation(branch.getId(), event.getArtAId(), event.getArtATypeId(), event.getArtBId(),
                     event.getArtBTypeId(), event.getRelTypeId());
               xModifiedEvents.add(new RelationModifiedEvent(sender, RelationEventType.Deleted, unloadedRelation));
            } else if (event instanceof NetworkRelationLinkRationalModifiedEvent) {
               UnloadedRelation unloadedRelation =
                  new UnloadedRelation(branch.getId(), event.getArtAId(), event.getArtATypeId(), event.getArtBId(),
                     event.getArtBTypeId(), event.getRelTypeId());
               xModifiedEvents.add(new RelationModifiedEvent(sender, RelationEventType.ModifiedRationale,
                  unloadedRelation));
            } else if (event instanceof NetworkRelationLinkCreatedEvent) {
               UnloadedRelation unloadedRelation =
                  new UnloadedRelation(branch.getId(), event.getArtAId(), event.getArtATypeId(), event.getArtBId(),
                     event.getArtBTypeId(), event.getRelTypeId());
               xModifiedEvents.add(new RelationModifiedEvent(sender, RelationEventType.Added, unloadedRelation));
            }
         }
         if (aArtifactLoaded || bArtifactLoaded) {
            if (event instanceof NetworkRelationLinkDeletedEvent) {
               RelationLink relation =
                  RelationManager.getLoadedRelationById(event.getRelId(), event.getArtAId(), event.getArtBId(), branch,
                     branch);
               if (relation != null) {
                  relation.internalRemoteEventDelete();

                  xModifiedEvents.add(new RelationModifiedEvent(sender, RelationEventType.Deleted, relation,
                     relation.getBranch(), relation.getRelationType().getName()));
               }
            } else if (event instanceof NetworkRelationLinkCreatedEvent) {
               RelationLink relation =
                  RelationManager.getLoadedRelationById(event.getRelId(), event.getArtAId(), event.getArtBId(), branch,
                     branch);

               if (relation == null || relation.getModificationType() == ModificationType.DELETED) {
                  relation =
                     RelationLink.getOrCreate(event.getArtAId(), event.getArtBId(), branch, branch, relationType,
                        event.getRelId(), event.getGammaId(), ((NetworkRelationLinkCreatedEvent) event).getRationale(),
                        ModificationType.NEW);

                  xModifiedEvents.add(new RelationModifiedEvent(sender, RelationEventType.Added, relation,
                     relation.getBranch(), relation.getRelationType().getName()));
               }
            }
         }
      } catch (OseeCoreException ex) {
         EventUtil.eventLog("Update Relations", ex);
      }
   }

   private void internalHandleRemoteArtifactDeleted(Artifact artifact) throws OseeCoreException {
      if (artifact == null) {
         return;
      } else {
         artifact.internalSetDeletedFromRemoteEvent();
      }
   }

}
