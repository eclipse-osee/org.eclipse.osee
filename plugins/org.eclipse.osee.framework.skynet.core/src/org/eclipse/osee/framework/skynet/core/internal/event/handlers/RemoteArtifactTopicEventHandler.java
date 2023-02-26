/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.internal.event.handlers;

import java.util.Collection;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.event.EventUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.messaging.event.res.RemoteArtifactTopicEvent;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.ChangeArtifactType;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicAttributeChangeTransfer;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicRelationTransfer;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.internal.event.EventHandlerRemote;
import org.eclipse.osee.framework.skynet.core.internal.event.Transport;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Torin Grenda, David Miller
 */
public class RemoteArtifactTopicEventHandler implements EventHandlerRemote<RemoteArtifactTopicEvent> {
   private final OrcsTokenService tokenService;

   public RemoteArtifactTopicEventHandler(OrcsTokenService tokenService) {
      this.tokenService = tokenService;
   }

   @Override
   public void handle(Transport transport, Sender sender, RemoteArtifactTopicEvent remoteEvent) {
      RemoteArtifactTopicEvent event1 = remoteEvent;
      ArtifactTopicEvent transEvent = FrameworkEventUtil.getPersistTopicEvent(event1, tokenService);
      updateArtifacts(sender, transEvent.getArtifacts(), TransactionId.valueOf(remoteEvent.getTransactionId()));
      updateRelations(sender, transEvent.getRelations());
      transport.send(sender, transEvent);
   }

   private void updateArtifacts(Sender sender, Collection<EventTopicArtifactTransfer> artifacts, TransactionId transactionId) {
      // Don't crash on any one artifact update problem (no update method throughs exceptions)
      for (EventTopicArtifactTransfer transArt : artifacts) {
         EventUtil.eventLog(String.format("REM: updateArtifact -> [%s]", transArt.toString()));
         EventModType eventModType = transArt.getEventModType();
         switch (eventModType) {
            case Added:
               // Handle Added Artifacts
               // Nothing to do for added cause they're not in cache yet.  Apps will load if they need them.
               // do nothing cause not in cache
               break;
            case Modified:
               updateModifiedArtifact(transArt, transactionId);
               break;
            case ChangeType:
               ArtifactTypeToken type = tokenService.getArtifactType(transArt.getArtifactTypeId().getId());
               Artifact art = ArtifactCache.getActive(transArt.getArtifactToken());
               ChangeArtifactType.handleRemoteChangeByArtAndType(art, type);
               break;
            case Deleted:
            case Purged:
               updateDeletedArtifact(ArtifactCache.getActive(transArt.getArtifactToken()));
               break;
            default:
               // Unknown mod type
               EventUtil.eventLog(
                  String.format("REM: updateArtifacts - Unhandled mod type [%s]", transArt.getEventModType()));
               break;
         }
      }
   }

   private void updateDeletedArtifact(Artifact artifact) {
      try {
         if (artifact == null) {
            // do nothing, artifact not in cache, so don't need to update
         } else if (!artifact.isHistorical()) {
            artifact.internalSetDeletedFromRemoteEvent();
         }
      } catch (OseeCoreException ex) {
         EventUtil.eventLog("REM: updateDeletedArtifact", ex);
      }
   }

   private void updateModifiedArtifact(EventTopicArtifactTransfer transArt, TransactionId transactionId) {
      try {
         Artifact artifact = ArtifactCache.getActive(transArt.getArtifactToken());
         if (artifact == null) {
            // do nothing, artifact not in cache, so don't need to update
         } else if (!artifact.isHistorical()) {

            artifact.setTransactionId(TransactionManager.getTransaction(transactionId));
            for (EventTopicAttributeChangeTransfer attrChange : transArt.getAttributeChanges()) {
               if (!OseeEventManager.getPreferences().isEnableRemoteEventLoopback()) {
                  ModificationType modificationType = ModificationType.valueOf(attrChange.getModType());
                  AttributeTypeToken attributeType = tokenService.getAttributeType(attrChange.getAttrTypeId().getId());
                  try {
                     Attribute<?> attribute = artifact.getAttributeById(attrChange.getAttrId(), true);
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
                                 "REM: updateModifiedArtifact - Can't get mod type for %s's attribute %s.",
                                 artifact.getArtifactTypeName(), attrChange.getAttrId()));
                              continue;
                           }
                           if (modificationType.isDeleted()) {
                              attribute.internalSetModType(modificationType, false, false);
                           } else {
                              attribute.getAttributeDataProvider().loadData(attrChange.getDataContent(),
                                 attrChange.getDataLocator());
                           }
                           attribute.setNotDirty();
                           attribute.internalSetGammaId(attrChange.getGammaId());
                        } catch (OseeCoreException ex) {
                           EventUtil.eventLog(String.format("REM: Exception updating %s's attribute %d [/n%s/n].",
                              artifact.getSafeName(), attribute.getId(), attribute.toString()), ex);
                        }
                     }
                     // Otherwise, attribute needs creation
                     // Process NEW attribute
                     else {
                        if (modificationType == null) {
                           EventUtil.eventLog(String.format("REM: Can't get mod type for %s's attribute %s.",
                              artifact.getArtifactTypeName(), attrChange.getAttrId()));
                           continue;
                        }
                        artifact.internalInitializeAttribute(attributeType, attrChange.getAttrId(),
                           attrChange.getGammaId(), modificationType, attrChange.getApplicabilityId(), false,
                           attrChange.getDataContent(), attrChange.getDataLocator());
                     }
                  } catch (OseeCoreException ex) {
                     EventUtil.eventLog(
                        String.format("REM: Exception updating %s's attribute change for attributeTypeId %d.",
                           artifact.getSafeName(), attributeType.getId()),
                        ex);
                  }
               }
            }
         }
      } catch (OseeCoreException ex) {
         EventUtil.eventLog("REM: updateModifiedArtifact", ex);
      }
   }

   private void updateRelations(Sender sender, Collection<EventTopicRelationTransfer> relations) {
      for (EventTopicRelationTransfer transRel : relations) {
         // Don't crash on any one relation update problem
         try {
            EventUtil.eventLog(String.format("REM: updateRelation -> [%s]", transRel.toString()));

            RelationTypeToken relationType = tokenService.getRelationType(transRel.getRelTypeId());
            Artifact aArtifact = ArtifactCache.getActive(transRel.getArtAToken());
            Artifact bArtifact = ArtifactCache.getActive(transRel.getArtBToken());
            // Nothing in cache, ignore this relation only
            if (aArtifact == null && bArtifact == null) {
               continue;
            }
            boolean aArtifactLoaded = aArtifact != null;
            boolean bArtifactLoaded = bArtifact != null;

            if (aArtifactLoaded || bArtifactLoaded) {
               BranchToken branch = BranchManager.getBranchToken(transRel.getArtAToken().getBranch());
               ArtifactToken artifactIdA = transRel.getArtAToken();
               ArtifactToken artifactIdB = transRel.getArtBToken();
               RelationLink relation =
                  RelationManager.getLoadedRelationById(transRel.getRelationId(), artifactIdA, artifactIdB, branch);

               RelationEventType eventType = transRel.getRelationEventType();
               switch (eventType) {
                  case Added:
                     if (relation == null || relation.getModificationType() == ModificationType.DELETED || relation.getModificationType() == ModificationType.ARTIFACT_DELETED) {
                        ApplicabilityId appId = relation == null ? ApplicabilityId.BASE : relation.getApplicabilityId();
                        relation = RelationManager.getOrCreate(artifactIdA, artifactIdB, relationType,
                           transRel.getRelationId(), transRel.getGammaId(), transRel.getRationale(),
                           ModificationType.NEW, appId, 0, ArtifactId.SENTINEL);
                     }
                     break;
                  case ModifiedRationale:
                     if (relation != null) {
                        relation.internalSetRationale(transRel.getRationale());
                        relation.setNotDirty();
                     }
                     break;
                  case Deleted:
                  case Purged:
                     if (relation != null) {
                        relation.internalRemoteEventDelete();
                     }
                     break;
                  case Undeleted:
                     if (relation != null) {
                        relation.undelete();
                        relation.setNotDirty();
                     }
                     break;
                  default:
                     EventUtil.eventLog(String.format("REM: updateRelations - Unhandled mod type [%s]", eventType));
                     break;
               }
            }
         } catch (OseeCoreException ex) {
            EventUtil.eventLog("REM: updateRelations", ex);
         }
      }
   }
}
