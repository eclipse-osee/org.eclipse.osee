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

package org.eclipse.osee.framework.skynet.core.event.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.HasBranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.EventTopicTransferType;
import org.eclipse.osee.framework.core.event.NetworkSender;
import org.eclipse.osee.framework.core.event.TopicEvent;
import org.eclipse.osee.framework.core.model.event.DefaultBasicUuidRelationReorder;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent.ArtifactEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;

/**
 * @author David W. Miller, Torin Grenda
 */
public class ArtifactTopicEvent extends TopicEvent implements HasBranchId {

   public static final String ARTIFACT_TOPIC = "artifact.topic.event";

   private final BranchId branch;
   private NetworkSender networkSender;
   private final List<EventTopicArtifactTransfer> artifacts = new ArrayList<>();
   private final List<EventTopicRelationTransfer> relations = new ArrayList<>();
   private final Set<EventTopicRelationReorderTransfer> relationReorderRecords = new HashSet<>();
   private final ArtifactEventType reloadEvent;
   public EventTopicTransferType transferType = EventTopicTransferType.BASE;

   public ArtifactTopicEvent(BranchId branch) {
      this(branch, ArtifactEventType.UPDATE_ARTIFACTS);
   }

   public ArtifactTopicEvent(TransactionToken transaction) {
      this(transaction.getBranch(), transaction, ArtifactEventType.UPDATE_ARTIFACTS);
   }

   public ArtifactTopicEvent(BranchId branch, ArtifactEventType reloadEvent) {
      this(branch, TransactionToken.SENTINEL, reloadEvent);
   }

   public ArtifactTopicEvent(BranchId branch, TransactionToken transaction, ArtifactEventType reloadEvent) {
      super(ARTIFACT_TOPIC);
      super.setTransaction(transaction);
      this.branch = branch;
      this.reloadEvent = reloadEvent;
   }

   public boolean isReloadEvent() {
      return ArtifactEventType.RELOAD_ARTIFACTS == reloadEvent;
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }

   public Set<EventTopicRelationReorderTransfer> getRelationOrderRecords() {
      return relationReorderRecords;
   }

   public TransactionId getTransactionId() {
      return super.getTransaction();
   }

   public List<EventBasicGuidArtifact> getLegacyArtifacts() {
      List<EventBasicGuidArtifact> toReturn = new ArrayList<>();
      for (EventTopicArtifactTransfer transferArt : artifacts) {
         toReturn.add(convertTransferArtToEventBasicArt(transferArt));
      }
      return toReturn;
   }

   public List<EventTopicArtifactTransfer> getArtifacts() {
      return artifacts;
   }

   private EventBasicGuidArtifact convertTransferArtToEventBasicArt(EventTopicArtifactTransfer transferArt) {
      return new EventBasicGuidArtifact(transferArt.getEventModType(), transferArt.getArtifactToken());
   }

   public List<EventTopicArtifactTransfer> getTransferArtifacts() {
      return this.artifacts;
   }

   public void addArtifact(EventTopicArtifactTransfer transferArt) {
      artifacts.add(transferArt);
   }

   public List<EventTopicRelationTransfer> getRelations() {
      return this.relations;
   }

   public void addRelation(EventTopicRelationTransfer topicRel) {
      relations.add(topicRel);
   }

   @Override
   public NetworkSender getNetworkSender() {
      return networkSender;
   }

   @Override
   public void setNetworkSender(NetworkSender value) {
      this.networkSender = value;
   }

   public void addRelationReorder(EventTopicRelationReorderTransfer relationReorder) {
      relationReorderRecords.add(relationReorder);
   }

   public boolean isRelAddedChangedDeleted(Artifact artifact) {
      return isRelChange(artifact) || isRelAdded(artifact) || isRelDeletedPurged(artifact);
   }

   public boolean isHasEvent(Artifact artifact) {
      return isModified(artifact) || isDeletedPurged(artifact) || isRelChange(artifact) || isRelDeletedPurged(
         artifact) || isRelAdded(artifact);
   }

   public EventModType getEventModType(Artifact artifact) {
      if (isHasEvent(artifact)) {
         if (isModified(artifact)) {
            return EventModType.Modified;
         } else if (isDeletedPurged(artifact)) {
            for (EventTopicArtifactTransfer gArt : artifacts) {
               if (gArt.is(EventModType.Deleted) && gArt.getArtifactToken().getId().equals(artifact.getId())) {
                  return EventModType.Deleted;
               } else {
                  return EventModType.Purged;
               }
            }
         }
      } else if (isReloaded(artifact)) {
         return EventModType.Reloaded;
      }
      return EventModType.Added;
   }

   public boolean isDeletedPurged(Artifact artifact) {
      for (EventTopicArtifactTransfer gArt : artifacts) {
         if (gArt.is(EventModType.Deleted,
            EventModType.Purged) && gArt.getArtifactToken().getId().equals(artifact.getId())) {
            return true;
         }
      }
      return false;
   }

   public Collection<Artifact> getRelCacheArtifacts() {
      try {
         return ArtifactCache.getActive(getRelationsArts(RelationEventType.ModifiedRationale, RelationEventType.Added,
            RelationEventType.Deleted, RelationEventType.Purged, RelationEventType.Undeleted), this.getBranch());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return java.util.Collections.emptyList();
   }

   public Collection<Artifact> getCacheArtifacts(EventModType... eventModTypes) {
      try {
         return ArtifactCache.getActive(getAsArtifactIds(eventModTypes), branch);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return java.util.Collections.emptyList();
   }

   public Collection<Artifact> getRelationOrderArtifacts(RelationTypeToken relationType, ArtifactTypeToken artifactType) {
      Collection<Artifact> reordered = new HashSet<>(relationReorderRecords.size());
      for (EventTopicRelationReorderTransfer reorder : relationReorderRecords) {
         if (relationType == null || relationType.equals(reorder.getRelTypeUuid())) {
            Artifact artifact = ArtifactCache.getActive(reorder.getParentArt().getArtifactToken());
            if (artifact != null) {
               if (artifactType == null || artifact.isOfType(artifactType)) {
                  reordered.add(artifact);
               }
            }
         }
      }
      return reordered;
   }

   public Collection<Artifact> getRelationOrderArtifacts() {
      return getRelationOrderArtifacts(null, null);
   }

   public void addArtifact(Artifact artifact) {
      Collection<EventTopicAttributeChangeTransfer> attrChanges = new ArrayList<EventTopicAttributeChangeTransfer>();
      for (AttributeChange attrChange : artifact.getDirtyFrameworkAttributeChanges()) {
         attrChanges.add(FrameworkEventUtil.attributeChangeToTransfer(attrChange));
      }
      EventTopicArtifactTransfer transferArt =
         FrameworkEventUtil.artifactTransferFactory(artifact.getBranch(), artifact, artifact.getArtifactType(),
            EventModType.Modified, null, attrChanges, EventTopicTransferType.MODIFICATION);

      artifacts.add(transferArt);
      for (DefaultBasicUuidRelationReorder uuidRelationReorder : artifact.getRelationOrderRecords()) {
         EventTopicRelationReorderTransfer transfer =
            FrameworkEventUtil.relationReorderBasicToTransfer(uuidRelationReorder, getEventModType(artifact));
         relationReorderRecords.add(transfer);
      }
   }

   public Collection<EventBasicGuidArtifact> get(EventModType... eventModTypes) {
      Set<EventBasicGuidArtifact> guidArts = new HashSet<>();
      for (EventTopicArtifactTransfer transferArt : getTransfer(eventModTypes)) {
         guidArts.add(convertTransferArtToEventBasicArt(transferArt));
      }
      return guidArts;
   }

   public Collection<EventTopicArtifactTransfer> getTransfer(EventModType... eventModTypes) {
      Set<EventTopicArtifactTransfer> guidArts = new HashSet<>();
      for (EventTopicArtifactTransfer transferArt : artifacts) {
         for (EventModType modType : eventModTypes) {
            if (transferArt.getEventModType() == modType) {
               guidArts.add(transferArt);
            }
         }
      }
      return guidArts;
   }

   public Collection<ArtifactId> getAsArtifactIds(EventModType... eventModTypes) {
      Set<ArtifactId> arts = new HashSet<>();
      for (EventTopicArtifactTransfer transferArt : artifacts) {
         for (EventModType modType : eventModTypes) {
            if (transferArt.getEventModType() == modType) {
               arts.add(transferArt.getArtifactToken());
            }
         }
      }
      return arts;
   }

   public boolean containsArtifact(Artifact artifact, EventModType... eventModTypes) {
      for (EventTopicArtifactTransfer transferArt : artifacts) {
         for (EventModType modType : eventModTypes) {
            if (transferArt.getEventModType() == modType && transferArt.getArtifactToken().equals(artifact)) {
               return true;
            }
         }
      }
      return false;
   }

   private Collection<ArtifactId> getRelationsArts(RelationEventType... eventModTypes) {
      Set<ArtifactId> arts = new HashSet<>();
      for (EventTopicRelationTransfer transferRel : getRelations(eventModTypes)) {
         arts.add(transferRel.getArtAId());
         arts.add(transferRel.getArtBId());
      }
      return arts;
   }

   public Collection<EventTopicRelationTransfer> getRelations(RelationEventType... eventModTypes) {
      Set<EventTopicRelationTransfer> transferRels = new HashSet<>();
      for (EventTopicRelationTransfer transferRel : relations) {
         for (RelationEventType modType : eventModTypes) {
            if (transferRel.getRelationEventType().equals(modType)) {
               transferRels.add(transferRel);
            }
         }
      }
      return transferRels;
   }

   public boolean isReloaded(Artifact artifact) {
      return matchesModTypes(artifact, EventModType.Reloaded);
   }

   public boolean isModified(Artifact artifact) {
      return matchesModTypes(artifact, EventModType.Modified);
   }

   public boolean isModifiedReloaded(Artifact artifact) {
      return matchesModTypes(artifact, EventModType.Modified, EventModType.Reloaded);
   }

   private boolean matchesModTypes(Artifact artifact, EventModType... eventModTypes) {
      Collection<EventTopicArtifactTransfer> transferArts = getTransfer(eventModTypes);
      for (EventTopicArtifactTransfer transferArt : transferArts) {
         if (transferArt.getArtifactToken().getId().equals(artifact.getId())) {
            return true;
         }
      }
      return false;
   }

   /**
    * Relation rationale changed
    */
   private boolean isRelChange(Artifact artifact) {
      for (EventTopicRelationTransfer guidRel : getRelations(RelationEventType.ModifiedRationale)) {
         if (guidRel.getArtAId().getId().equals(artifact.getId()) || guidRel.getArtBId().getId().equals(
            artifact.getId())) {
            return true;
         }
      }
      return false;
   }

   private boolean isRelDeletedPurged(Artifact artifact) {
      for (EventTopicRelationTransfer guidRel : getRelations(RelationEventType.Deleted, RelationEventType.Purged)) {
         if (guidRel.getArtAId().getId().equals(artifact.getId()) || guidRel.getArtBId().getId().equals(
            artifact.getId())) {
            return true;
         }
      }
      return false;
   }

   private boolean isRelAdded(Artifact artifact) {
      for (EventTopicRelationTransfer guidRel : getRelations(RelationEventType.Added, RelationEventType.Undeleted)) {
         if (guidRel.getArtAId().getId().equals(artifact.getId()) || guidRel.getArtBId().getId().equals(
            artifact.getId())) {
            return true;
         }
      }
      return false;
   }

   @Override
   public String toString() {
      try {
         return String.format("ArtifactTopicEvent: BG[%s] TrId[%s] ARTS[%s] RELS[%s]", branch.getId(),
            super.getTransaction().getIdString(), getArtifactsString(artifacts), getRelationsString(relations));
      } catch (Exception ex) {
         return String.format("ArtifactEvent exception: " + ex.getLocalizedMessage());
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((artifacts == null) ? 0 : artifacts.hashCode());
      result = prime * result + ((branch == null) ? 0 : branch.hashCode());
      result = prime * result + ((networkSender == null) ? 0 : networkSender.hashCode());
      result = prime * result + ((relationReorderRecords == null) ? 0 : relationReorderRecords.hashCode());
      result = prime * result + ((relations == null) ? 0 : relations.hashCode());
      result = prime * result + ((reloadEvent == null) ? 0 : reloadEvent.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      ArtifactTopicEvent other = (ArtifactTopicEvent) obj;
      if (artifacts == null) {
         if (other.artifacts != null) {
            return false;
         }
      } else if (!artifacts.equals(other.artifacts)) {
         return false;
      }
      if (branch == null) {
         if (other.branch != null) {
            return false;
         }
      } else if (!branch.getId().equals(other.branch.getId())) {
         return false;
      }
      if (networkSender == null) {
         if (other.networkSender != null) {
            return false;
         }
      }
      if (relationReorderRecords == null) {
         if (other.relationReorderRecords != null) {
            return false;
         }
      } else if (!relationReorderRecords.equals(other.relationReorderRecords)) {
         return false;
      }
      if (relations == null) {
         if (other.relations != null) {
            return false;
         }
      } else if (!relations.equals(other.relations)) {
         return false;
      }
      if (reloadEvent != other.reloadEvent) {
         return false;
      }
      return true;
   }

   private String getArtifactsString(List<EventTopicArtifactTransfer> artifacts) {
      if (artifacts.size() <= 10) {
         return artifacts.toString();
      } else {
         return String.format(" %d Artifacts (data hidden)", artifacts.size());
      }
   }

   private String getRelationsString(List<EventTopicRelationTransfer> relations) {
      if (relations.size() <= 10) {
         return relations.toString();
      } else {
         return String.format(" %d Relations (data hidden)", relations.size());
      }
   }
}
