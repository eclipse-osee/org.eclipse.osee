/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal.event.handlers;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.messaging.event.res.AttributeEventModificationType;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemotePersistEvent1;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.ChangeArtifactType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AttributeChange;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventChangeTypeBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.EventModifiedBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.internal.event.EventHandlerRemote;
import org.eclipse.osee.framework.skynet.core.internal.event.Transport;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactRemoteEventHandler implements EventHandlerRemote<RemotePersistEvent1> {

   @Override
   public void handle(Transport transport, Sender sender, RemotePersistEvent1 remoteEvent) throws OseeCoreException {
      RemotePersistEvent1 event1 = remoteEvent;
      ArtifactEvent transEvent = FrameworkEventUtil.getPersistEvent(event1);
      updateArtifacts(sender, transEvent.getArtifacts(), remoteEvent.getTransaction());
      updateRelations(sender, transEvent.getRelations());
      transport.send(sender, transEvent);
   }

   private void updateArtifacts(Sender sender, Collection<EventBasicGuidArtifact> artifacts, TransactionToken transactionId) throws OseeCoreException {
      // Don't crash on any one artifact update problem (no update method throughs exceptions)
      for (EventBasicGuidArtifact guidArt : artifacts) {
         EventUtil.eventLog(String.format("REM: updateArtifact -> [%s]", guidArt));
         EventModType eventModType = guidArt.getModType();
         switch (eventModType) {
            case Added:
               // Handle Added Artifacts
               // Nothing to do for added cause they're not in cache yet.  Apps will load if they need them.
               // do nothing cause not in cache
               break;
            case Modified:
               updateModifiedArtifact((EventModifiedBasicGuidArtifact) guidArt, transactionId);
               break;
            case ChangeType:
               ChangeArtifactType.handleRemoteChangeType((EventChangeTypeBasicGuidArtifact) guidArt);
               break;
            case Deleted:
            case Purged:
               updateDeletedArtifact(guidArt);
               break;
            default:
               // Unknown mod type
               EventUtil.eventLog(
                  String.format("REM: updateArtifacts - Unhandled mod type [%s]", guidArt.getModType()));
               break;
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
         EventUtil.eventLog("REM: updateDeletedArtifact", ex);
      }
   }

   private void updateModifiedArtifact(EventModifiedBasicGuidArtifact guidArt, TransactionToken transactionId) {
      try {
         Artifact artifact = ArtifactCache.getActive(guidArt);
         if (artifact == null) {
            // do nothing, artifact not in cache, so don't need to update
         } else if (!artifact.isHistorical()) {
            artifact.setTransactionId(transactionId);
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
                                 "REM: updateModifiedArtifact - Can't get mod type for %s's attribute %d.",
                                 artifact.getArtifactTypeName(), attrChange.getAttributeId()));
                              continue;
                           }
                           if (modificationType.isDeleted()) {
                              attribute.internalSetModType(modificationType, false, false);
                           } else {
                              attribute.getAttributeDataProvider().loadData(attrChange.getDataArray());
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
                           EventUtil.eventLog(String.format("REM: Can't get mod type for %s's attribute %d.",
                              artifact.getArtifactTypeName(), attrChange.getAttributeId()));
                           continue;
                        }
                        artifact.internalInitializeAttribute(attributeType, attrChange.getAttributeId(),
                           attrChange.getGammaId(), modificationType, attrChange.getApplicabilityId(), false,
                           attrChange.getDataArray());
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

   private void updateRelations(Sender sender, Collection<EventBasicGuidRelation> relations) {
      for (EventBasicGuidRelation guidArt : relations) {
         // Don't crash on any one relation update problem
         try {
            EventUtil.eventLog(String.format("REM: updateRelation -> [%s]", guidArt));

            RelationType relationType = RelationTypeManager.getTypeByGuid(guidArt.getRelTypeGuid());
            Artifact aArtifact = ArtifactCache.getActive(guidArt.getArtA());
            Artifact bArtifact = ArtifactCache.getActive(guidArt.getArtB());
            // Nothing in cache, ignore this relation only
            if (aArtifact == null && bArtifact == null) {
               continue;
            }
            boolean aArtifactLoaded = aArtifact != null;
            boolean bArtifactLoaded = bArtifact != null;

            if (aArtifactLoaded || bArtifactLoaded) {
               BranchId branch = guidArt.getArtA().getBranch();
               RelationLink relation = RelationManager.getLoadedRelationById(guidArt.getRelationId(),
                  guidArt.getArtAId(), guidArt.getArtBId(), branch);

               RelationEventType eventType = guidArt.getModType();
               switch (eventType) {
                  case Added:
                     if (relation == null || relation.getModificationType() == ModificationType.DELETED || relation.getModificationType() == ModificationType.ARTIFACT_DELETED) {
                        ApplicabilityId appId = relation == null ? ApplicabilityId.BASE : relation.getApplicabilityId();
                        relation = RelationManager.getOrCreate(guidArt.getArtAId(), guidArt.getArtBId(), branch,
                           relationType, guidArt.getRelationId(), guidArt.getGammaId(), guidArt.getRationale(),
                           ModificationType.NEW, appId);
                     }
                     break;
                  case ModifiedRationale:
                     if (relation != null) {
                        relation.internalSetRationale(guidArt.getRationale());
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
