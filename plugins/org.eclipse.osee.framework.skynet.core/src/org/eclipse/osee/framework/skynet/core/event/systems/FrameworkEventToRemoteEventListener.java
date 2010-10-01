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

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.AttributeEventModificationType;
import org.eclipse.osee.framework.messaging.event.res.IFrameworkEventListener;
import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBranchEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemotePersistEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTransactionEvent1;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.ChangeArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeTransactionOperation;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AttributeChange;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventChangeTypeBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.EventModifiedBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public final class FrameworkEventToRemoteEventListener implements IFrameworkEventListener {

   private final ISchedulingRule mutexRule;
   private InternalEventManager eventManager;

   public FrameworkEventToRemoteEventListener(InternalEventManager eventManager) {
      this.eventManager = eventManager;
      this.mutexRule = new ISchedulingRule() {

         @Override
         public boolean contains(ISchedulingRule rule) {
            return rule == this;
         }

         @Override
         public boolean isConflicting(ISchedulingRule rule) {
            return rule == this;
         }
      };
   }

   @Override
   public void onEvent(final RemoteEvent remoteEvent) {
      final Runnable runnable = new Runnable() {
         private void handleTransactionEvent(Sender sender, TransactionEvent transEvent) {
            try {
               if (transEvent.getEventType() == TransactionEventType.Purged) {
                  PurgeTransactionOperation.handleRemotePurgeTransactionEvent(transEvent);
                  eventManager.kickTransactionEvent(sender, transEvent);
               } else {
                  EventUtil.eventLog("REM2: handleTransactionEvent - unhandled mod type " + transEvent.getEventType());
               }
            } catch (Exception ex) {
               EventUtil.eventLog("REM2: handleTransactionEvent", ex);
            }
         }

         @Override
         public void run() {
            Sender sender = new Sender(remoteEvent.getNetworkSender());
            // If the sender's sessionId is the same as this client, then this event was
            // created in this client and returned by remote event manager; ignore and continue
            if (sender.isLocal()) {
               return;
            }
            // Handles TransactionEvents, ArtifactChangeTypeEvents, ArtifactPurgeEvents
            if (remoteEvent instanceof RemotePersistEvent1) {
               try {
                  RemotePersistEvent1 event1 = (RemotePersistEvent1) remoteEvent;
                  ArtifactEvent transEvent = FrameworkEventUtil.getPersistEvent(event1);
                  updateArtifacts(sender, transEvent);
                  updateRelations(sender, transEvent);
                  eventManager.kickArtifactEvent(sender, transEvent);
               } catch (Exception ex) {
                  EventUtil.eventLog("REM2: RemoteTransactionEvent1", ex);
               }
            } else if (remoteEvent instanceof RemoteBranchEvent1) {
               try {
                  BranchEvent branchEvent = FrameworkEventUtil.getBranchEvent((RemoteBranchEvent1) remoteEvent);
                  updateBranches(sender, branchEvent);
                  eventManager.kickBranchEvent(sender, branchEvent);
               } catch (Exception ex) {
                  EventUtil.eventLog("REM2: RemoteBranchEvent1", ex);
               }
            } else if (remoteEvent instanceof RemoteTransactionEvent1) {
               try {
                  TransactionEvent transEvent =
                     FrameworkEventUtil.getTransactionEvent((RemoteTransactionEvent1) remoteEvent);
                  handleTransactionEvent(sender, transEvent);
               } catch (Exception ex) {
                  EventUtil.eventLog("REM2: RemoteBranchEvent1", ex);
               }
            }
         }
      };

      if (OseeEventManager.getPreferences().isPendRunning()) {
         runnable.run();
      } else {
         Job job =
            new Job(String.format("[%s] - receiving [%s]", getClass().getSimpleName(),
               remoteEvent.getClass().getSimpleName())) {

               @Override
               protected IStatus run(IProgressMonitor monitor) {

                  try {
                     runnable.run();
                  } catch (Exception ex) {
                     // don't want exceptions poping up; just log and return nicely
                     OseeLog.log(Activator.class, Level.SEVERE, "REM2 Receive Event Exception", ex);
                  }
                  monitor.done();
                  return Status.OK_STATUS;
               }
            };
         job.setSystem(true);
         job.setRule(mutexRule);
         job.setUser(false);
         job.schedule();
      }
   }

   /**
    * Updates local cache
    **/
   private void updateArtifacts(Sender sender, ArtifactEvent transEvent) {
      // Don't crash on any one artifact update problem (no update method throughs exceptions)
      for (EventBasicGuidArtifact guidArt : transEvent.getArtifacts()) {
         EventUtil.eventLog(String.format("REM2: updateArtifact -> [%s]", guidArt));
         // Handle Added Artifacts
         // Nothing to do for added cause they're not in cache yet.  Apps will load if they need them.
         if (guidArt.getModType() == EventModType.Added) {
            // do nothing cause not in cache
         }
         // Handle Deleted Artifacts
         else if (guidArt.getModType() == EventModType.Deleted || guidArt.getModType() == EventModType.Purged) {
            updateDeletedArtifact(guidArt);
         }
         // Handle Modified Artifacts
         else if (guidArt.getModType() == EventModType.Modified) {
            updateModifiedArtifact((EventModifiedBasicGuidArtifact) guidArt);
         }
         // Handle Change Type Artifacts
         else if (guidArt.getModType() == EventModType.ChangeType) {
            ChangeArtifactType.handleRemoteChangeType((EventChangeTypeBasicGuidArtifact) guidArt);
         }
         // Unknown mod type
         else {
            EventUtil.eventLog(String.format("REM2: updateArtifacts - Unhandled mod type [%s]", guidArt.getModType()));
         }
      }
   }

   /**
    * Updates local cache
    */
   private void updateBranches(Sender sender, BranchEvent branchEvent) {
      BranchEventType eventType = branchEvent.getEventType();
      try {
         if (eventType == BranchEventType.Committed) {
            TransactionManager.clearCommitArtifactCacheForAssociatedArtifact(BranchManager.getAssociatedArtifact(BranchManager.getBranchByGuid(branchEvent.getBranchGuid())));
         }
         BranchManager.refreshBranches();
      } catch (Exception ex) {
         EventUtil.eventLog("REM2: updateBranches", ex);
      }
   }

   private void updateRelations(Sender sender, ArtifactEvent transEvent) {
      for (EventBasicGuidRelation guidArt : transEvent.getRelations()) {
         // Don't crash on any one relation update problem
         try {
            EventUtil.eventLog(String.format("REM2: updateRelation -> [%s]", guidArt));
            RelationEventType eventType = guidArt.getModType();
            Branch branch = BranchManager.getBranch(guidArt.getArtA());
            RelationType relationType = RelationTypeManager.getTypeByGuid(guidArt.getRelTypeGuid());
            Artifact aArtifact = ArtifactCache.getActive(guidArt.getArtA());
            Artifact bArtifact = ArtifactCache.getActive(guidArt.getArtB());
            // Nothing in cache, ignore
            if (aArtifact == null && bArtifact == null) {
               return;
            }
            boolean aArtifactLoaded = aArtifact != null;
            boolean bArtifactLoaded = bArtifact != null;

            if (aArtifactLoaded || bArtifactLoaded) {
               if (eventType == RelationEventType.Added) {
                  RelationLink relation =
                     RelationManager.getLoadedRelationById(guidArt.getRelationId(), guidArt.getArtAId(),
                        guidArt.getArtBId(), branch);

                  if (relation == null || relation.getModificationType() == ModificationType.DELETED || relation.getModificationType() == ModificationType.ARTIFACT_DELETED) {
                     relation =
                        RelationManager.getOrCreate(guidArt.getArtAId(), guidArt.getArtBId(), branch, relationType,
                           guidArt.getRelationId(), guidArt.getGammaId(), guidArt.getRationale(), ModificationType.NEW);

                  }
               } else if (eventType == RelationEventType.Deleted || eventType == RelationEventType.Purged) {
                  RelationLink relation =
                     RelationManager.getLoadedRelationById(guidArt.getRelationId(), guidArt.getArtAId(),
                        guidArt.getArtBId(), branch);
                  if (relation != null) {
                     relation.internalRemoteEventDelete();
                  }
               } else if (eventType == RelationEventType.ModifiedRationale) {
                  RelationLink relation =
                     RelationManager.getLoadedRelationById(guidArt.getRelationId(), guidArt.getArtAId(),
                        guidArt.getArtBId(), branch);
                  if (relation != null) {
                     relation.internalSetRationale(guidArt.getRationale());
                     relation.setNotDirty();
                  }
               } else if (eventType == RelationEventType.Undeleted) {
                  RelationLink relation =
                     RelationManager.getLoadedRelationById(guidArt.getRelationId(), guidArt.getArtAId(),
                        guidArt.getArtBId(), branch);
                  if (relation != null) {
                     relation.undelete();
                     relation.setNotDirty();
                  }
               } else {
                  EventUtil.eventLog(String.format("REM2: updateRelations - Unhandled mod type [%s]", eventType));
               }
            }
         } catch (OseeCoreException ex) {
            EventUtil.eventLog("REM2: updateRelations", ex);
         }
      }
   }

   private void updateDeletedArtifact(DefaultBasicGuidArtifact guidArt) {
      try {
         Artifact artifact = ArtifactCache.getActive(guidArt);
         if (artifact == null) {
            // do nothing, artifact not in cache, so don't need to update
         } else if (!artifact.isHistorical()) {
            artifact.internalSetDeletedFromRemoteEvent();
         }
      } catch (OseeCoreException ex) {
         EventUtil.eventLog("REM2: updateDeletedArtifact", ex);
      }
   }

   private void updateModifiedArtifact(EventModifiedBasicGuidArtifact guidArt) {
      try {
         Artifact artifact = ArtifactCache.getActive(guidArt);
         if (artifact == null) {
            // do nothing, artifact not in cache, so don't need to update
         } else if (!artifact.isHistorical()) {
            for (AttributeChange attrChange : guidArt.getAttributeChanges()) {
               if (!OseeEventManager.getPreferences().isEnableRemoteEventLoopback()) {
                  ModificationType modificationType =
                     AttributeEventModificationType.getType(attrChange.getModTypeGuid()).getModificationType();
                  AttributeType attributeType = AttributeTypeManager.getTypeByGuid(attrChange.getAttrTypeGuid());
                  try {
                     Attribute<?> attribute = artifact.getAttributeById(attrChange.getAttributeId(), true);
                     // Attribute already exists (but may be deleted), process update
                     // Process MODIFIED / DELETED attribute
                     if (attribute != null) {
                        if (attribute.isDirty()) {
                           EventUtil.eventLog(String.format("%s's attribute %d [/n%s/n] has been overwritten.",
                              artifact.getSafeName(), attribute.getId(), attribute.toString()));
                        }
                        try {
                           if (modificationType == null) {
                              EventUtil.eventLog(String.format(
                                 "REM2: updateModifiedArtifact - Can't get mod type for %s's attribute %d.",
                                 artifact.getArtifactTypeName(), attrChange.getAttributeId()));
                              continue;
                           }
                           if (modificationType.isDeleted()) {
                              attribute.internalSetModificationType(modificationType);
                           } else {
                              attribute.getAttributeDataProvider().loadData(
                                 attrChange.getData().toArray(new Object[attrChange.getData().size()]));
                           }
                           attribute.internalSetGammaId(attrChange.getGammaId());
                           attribute.setNotDirty();
                        } catch (OseeCoreException ex) {
                           EventUtil.eventLog(
                              String.format("REM2: Exception updating %s's attribute %d [/n%s/n].",
                                 artifact.getSafeName(), attribute.getId(), attribute.toString()), ex);
                        }
                     }
                     // Otherwise, attribute needs creation
                     // Process NEW attribute
                     else {
                        if (modificationType == null) {
                           EventUtil.eventLog(String.format("REM2: Can't get mod type for %s's attribute %d.",
                              artifact.getArtifactTypeName(), attrChange.getAttributeId()));
                           continue;
                        }
                        artifact.internalInitializeAttribute(attributeType, attrChange.getAttributeId(),
                           attrChange.getGammaId(), modificationType, false,
                           attrChange.getData().toArray(new Object[attrChange.getData().size()]));
                     }
                  } catch (OseeCoreException ex) {
                     EventUtil.eventLog(String.format(
                        "REM2: Exception updating %s's attribute change for attributeTypeId %d.",
                        artifact.getSafeName(), attributeType.getId()), ex);
                  }
               }
            }
         }
      } catch (OseeCoreException ex) {
         EventUtil.eventLog("REM2: updateModifiedArtifact", ex);
      }
   }
}