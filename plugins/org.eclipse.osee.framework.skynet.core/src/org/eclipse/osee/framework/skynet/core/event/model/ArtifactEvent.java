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
package org.eclipse.osee.framework.skynet.core.event.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.HasBranch;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.DefaultBasicUuidRelationReorder;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;

/**
 * @author Donald G. Dunne
 */
public class ArtifactEvent implements FrameworkEvent, HasNetworkSender, HasBranch {

   public static enum ArtifactEventType {
      RELOAD_ARTIFACTS,
      UPDATE_ARTIFACTS;
   }

   private final BranchId branch;
   private final TransactionToken transaction;
   private NetworkSender networkSender;
   private final List<EventBasicGuidArtifact> artifacts = new ArrayList<>();
   private final List<EventBasicGuidRelation> relations = new ArrayList<>();
   private final Set<DefaultBasicUuidRelationReorder> relationReorderRecords = new HashSet<>();
   private final ArtifactEventType reloadEvent;

   public ArtifactEvent(BranchId branch) {
      this(branch, ArtifactEventType.UPDATE_ARTIFACTS);
   }

   public ArtifactEvent(TransactionToken transaction) {
      this(transaction.getBranch(), transaction, ArtifactEventType.UPDATE_ARTIFACTS);
   }

   public ArtifactEvent(BranchId branch, ArtifactEventType reloadEvent) {
      this(branch, TransactionToken.SENTINEL, reloadEvent);
   }

   public ArtifactEvent(BranchId branch, TransactionToken transaction, ArtifactEventType reloadEvent) {
      this.branch = branch;
      this.transaction = transaction;
      this.reloadEvent = reloadEvent;
   }

   public boolean isReloadEvent() {
      return ArtifactEventType.RELOAD_ARTIFACTS == reloadEvent;
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }

   public Set<DefaultBasicUuidRelationReorder> getRelationOrderRecords() {
      return relationReorderRecords;
   }

   public TransactionId getTransactionId() {
      return transaction;
   }

   public List<EventBasicGuidArtifact> getArtifacts() {
      return this.artifacts;
   }

   public void addArtifact(EventBasicGuidArtifact eventGuidArt) {
      artifacts.add(eventGuidArt);
   }

   public List<EventBasicGuidRelation> getRelations() {
      return this.relations;
   }

   public void addRelation(EventBasicGuidRelation eventGuidRelation) {
      relations.add(eventGuidRelation);
   }

   @Override
   public NetworkSender getNetworkSender() {
      return networkSender;
   }

   @Override
   public void setNetworkSender(NetworkSender value) {
      this.networkSender = value;
   }

   public boolean isRelAddedChangedDeleted(Artifact artifact) {
      return isRelAddedChangedDeleted(artifact.getBasicGuidArtifact());
   }

   private boolean isRelAddedChangedDeleted(DefaultBasicGuidArtifact guidArt) {
      return isRelChange(guidArt) || isRelAdded(guidArt) || isRelDeletedPurged(guidArt);
   }

   public boolean isHasEvent(Artifact artifact) {
      return isHasEvent(artifact.getBasicGuidArtifact());
   }

   private boolean isHasEvent(DefaultBasicGuidArtifact guidArt) {
      return isModified(guidArt) || isDeletedPurged(guidArt) || isRelChange(guidArt) || isRelDeletedPurged(
         guidArt) || isRelAdded(guidArt);
   }

   public boolean isDeletedPurged(Artifact artifact) {
      return isDeletedPurged(artifact.getBasicGuidArtifact());
   }

   private boolean isDeletedPurged(DefaultBasicGuidArtifact guidArt) {
      for (EventBasicGuidArtifact gArt : artifacts) {
         if (gArt.is(EventModType.Deleted, EventModType.Purged) && gArt.equals(guidArt)) {
            return true;
         }
      }
      return false;
   }

   public Collection<Artifact> getRelCacheArtifacts() {
      try {
         return ArtifactCache.getActive(getRelationsArts(RelationEventType.ModifiedRationale, RelationEventType.Added,
            RelationEventType.Deleted, RelationEventType.Purged, RelationEventType.Undeleted));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return java.util.Collections.emptyList();
   }

   public Collection<Artifact> getCacheArtifacts(EventModType... eventModTypes) {
      try {
         return ArtifactCache.getActive(get(eventModTypes));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return java.util.Collections.emptyList();
   }

   public Collection<Artifact> getRelationOrderArtifacts(RelationTypeId relationType, ArtifactTypeId artifactType) {
      Collection<Artifact> reordered = new HashSet<>(relationReorderRecords.size());
      for (DefaultBasicUuidRelationReorder reorder : relationReorderRecords) {
         if (relationType == null || relationType.equals(reorder.getRelTypeGuid())) {
            Artifact artifact = ArtifactCache.getActive(reorder.getParentArt());
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
      EventModifiedBasicGuidArtifact guidArt = new EventModifiedBasicGuidArtifact(artifact.getBranch(),
         artifact.getArtifactType(), artifact.getGuid(), artifact.getDirtyFrameworkAttributeChanges());
      artifacts.add(guidArt);
      relationReorderRecords.addAll(artifact.getRelationOrderRecords());
   }

   public Collection<EventBasicGuidArtifact> get(EventModType... eventModTypes) {
      Set<EventBasicGuidArtifact> guidArts = new HashSet<>();
      for (EventBasicGuidArtifact guidArt : artifacts) {
         for (EventModType modType : eventModTypes) {
            if (guidArt.getModType() == modType) {
               guidArts.add(guidArt);
            }
         }
      }
      return guidArts;
   }

   public boolean containsArtifact(Artifact artifact, EventModType... eventModTypes) {
      for (EventBasicGuidArtifact guidArt : artifacts) {
         for (EventModType modType : eventModTypes) {
            if (guidArt.getModType() == modType && guidArt.equals(artifact)) {
               return true;
            }
         }
      }
      return false;
   }

   private Collection<DefaultBasicGuidArtifact> getRelationsArts(RelationEventType... eventModTypes) {
      Set<DefaultBasicGuidArtifact> guidArts = new HashSet<>();
      for (EventBasicGuidRelation guidRel : getRelations(eventModTypes)) {
         guidArts.add(guidRel.getArtA());
         guidArts.add(guidRel.getArtB());
      }
      return guidArts;
   }

   public Collection<EventBasicGuidRelation> getRelations(RelationEventType... eventModTypes) {
      Set<EventBasicGuidRelation> guidRels = new HashSet<>();
      for (EventBasicGuidRelation guidRel : relations) {
         for (RelationEventType modType : eventModTypes) {
            if (guidRel.getModType() == modType) {
               guidRels.add(guidRel);
            }
         }
      }
      return guidRels;
   }

   public boolean isReloaded(Artifact artifact) {
      return isReloaded(artifact.getBasicGuidArtifact());
   }

   private boolean isReloaded(DefaultBasicGuidArtifact guidArt) {
      return get(EventModType.Reloaded).contains(guidArt);
   }

   public boolean isModified(Artifact artifact) {
      return isModified(artifact.getBasicGuidArtifact());
   }

   private boolean isModified(DefaultBasicGuidArtifact guidArt) {
      return get(EventModType.Modified).contains(guidArt);
   }

   public boolean isModifiedReloaded(Artifact artifact) {
      return isModifiedReloaded(artifact.getBasicGuidArtifact());
   }

   private boolean isModifiedReloaded(DefaultBasicGuidArtifact guidArt) {
      return get(EventModType.Modified, EventModType.Reloaded).contains(guidArt);
   }

   /**
    * Relation rationale changed
    */
   private boolean isRelChange(DefaultBasicGuidArtifact guidArt) {
      for (EventBasicGuidRelation guidRel : getRelations(RelationEventType.ModifiedRationale)) {
         if (guidRel.getArtA().equals(guidArt) || guidRel.getArtB().equals(guidArt)) {
            return true;
         }
      }
      return false;
   }

   private boolean isRelDeletedPurged(DefaultBasicGuidArtifact guidArt) {
      for (EventBasicGuidRelation guidRel : getRelations(RelationEventType.Deleted, RelationEventType.Purged)) {
         if (guidRel.getArtA().equals(guidArt) || guidRel.getArtB().equals(guidArt)) {
            return true;
         }
      }
      return false;
   }

   private boolean isRelAdded(DefaultBasicGuidArtifact guidArt) {
      for (EventBasicGuidRelation guidRel : getRelations(RelationEventType.Added, RelationEventType.Undeleted)) {
         if (guidRel.getArtA().equals(guidArt) || guidRel.getArtB().equals(guidArt)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public String toString() {
      try {
         return String.format("ArtifactEvent: BG[%s] TrId[%s] ARTS[%s] RELS[%s] Sender[%s]", branch.getId(),
            transaction, getArtifactsString(artifacts), getRelationsString(relations), networkSender);
      } catch (Exception ex) {
         return String.format("ArtifactEvent exception: " + ex.getLocalizedMessage());
      }
   }

   private String getArtifactsString(List<EventBasicGuidArtifact> artifacts) {
      if (artifacts.size() <= 10) {
         return artifacts.toString();
      } else {
         return String.format(" %d Artifacts (data hidden)", artifacts.size());
      }
   }

   private String getRelationsString(List<EventBasicGuidRelation> relations) {
      if (relations.size() <= 10) {
         return relations.toString();
      } else {
         return String.format(" %d Relations (data hidden)", relations.size());
      }
   }
}
