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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidRelationReorder;
import org.eclipse.osee.framework.core.model.event.IBasicGuidArtifact;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkSender;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchGuidEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;

public class ArtifactEvent extends FrameworkEvent {

   private final String branchGuid;
   private int transactionId;
   private NetworkSender networkSender;
   private final List<EventBasicGuidArtifact> artifacts = new ArrayList<EventBasicGuidArtifact>();
   private final List<EventBasicGuidRelation> relations = new ArrayList<EventBasicGuidRelation>();
   private final Set<DefaultBasicGuidRelationReorder> relationReorderRecords =
      new HashSet<DefaultBasicGuidRelationReorder>();

   public ArtifactEvent(Branch branch) {
      branchGuid = branch.getGuid();
   }

   public ArtifactEvent(IOseeBranch branch) {
      branchGuid = branch.getGuid();
   }

   public String getBranchGuid() {
      return branchGuid;
   }

   public Set<DefaultBasicGuidRelationReorder> getRelationOrderRecords() {
      return relationReorderRecords;
   }

   public boolean isForBranch(Branch branch) {
      return getBranchGuid().equals(branch.getGuid());
   }

   public int getTransactionId() {
      return transactionId;
   }

   public void setTransactionId(int value) {
      this.transactionId = value;
   }

   public List<EventBasicGuidArtifact> getArtifacts() {
      return this.artifacts;
   }

   public List<EventBasicGuidRelation> getRelations() {
      return this.relations;
   }

   public NetworkSender getNetworkSender() {
      return networkSender;
   }

   public void setNetworkSender(NetworkSender value) {
      this.networkSender = value;
   }

   public boolean isRelAddedChangedDeleted(Artifact artifact) {
      return isRelAddedChangedDeleted(artifact.getBasicGuidArtifact());
   }

   public boolean isRelAddedChangedDeleted(IBasicGuidArtifact guidArt) {
      return isRelChange(guidArt) || isRelAdded(guidArt) || isRelDeletedPurged(guidArt);
   }

   public boolean isHasEvent(Artifact artifact) {
      return isHasEvent(artifact.getBasicGuidArtifact());
   }

   public boolean isHasEvent(IBasicGuidArtifact guidArt) {
      return isModified(guidArt) || isDeletedPurged(guidArt) || isRelChange(guidArt) || isRelDeletedPurged(guidArt) || isRelAdded(guidArt);
   }

   public boolean isDeletedPurged(Artifact artifact) {
      return isDeletedPurged(artifact.getBasicGuidArtifact());
   }

   public boolean isDeletedPurged(IBasicGuidArtifact guidArt) {
      for (EventBasicGuidArtifact gArt : artifacts) {
         if (gArt.is(EventModType.Deleted, EventModType.Purged) && gArt.equals(guidArt)) {
            return true;
         }
      }
      return false;
   }

   public Collection<Artifact> getRelModifiedCacheArtifacts() {
      try {
         return ArtifactCache.getActive(getRelationsArts(RelationEventType.ModifiedRationale));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return java.util.Collections.emptyList();
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

   public Collection<DefaultBasicGuidArtifact> getRelOrderChangedArtifacts() {
      return getRelOrderChangedArtifacts((IRelationType[]) null);
   }

   public Collection<DefaultBasicGuidArtifact> getRelOrderChangedArtifacts(IRelationType... relationTypes) {
      Set<DefaultBasicGuidArtifact> guidArts = new HashSet<DefaultBasicGuidArtifact>();
      for (DefaultBasicGuidRelationReorder record : relationReorderRecords) {
         if (relationTypes == null) {
            guidArts.add(record.getParentArt());
         } else {
            for (IRelationType type : relationTypes) {
               if (record.getRelTypeGuid().equals(type.getGuid())) {
                  guidArts.add(record.getParentArt());
               }
            }
         }
      }
      return guidArts;
   }

   public Collection<EventBasicGuidArtifact> get(EventModType... eventModTypes) {
      Set<EventBasicGuidArtifact> guidArts = new HashSet<EventBasicGuidArtifact>();
      for (EventBasicGuidArtifact guidArt : artifacts) {
         for (EventModType modType : eventModTypes) {
            if (guidArt.getModType() == modType) {
               guidArts.add(guidArt);
            }
         }
      }
      return guidArts;
   }

   public Collection<IBasicGuidArtifact> getRelationsArts(RelationEventType... eventModTypes) {
      Set<IBasicGuidArtifact> guidArts = new HashSet<IBasicGuidArtifact>();
      for (EventBasicGuidRelation guidRel : getRelations(eventModTypes)) {
         guidArts.add(guidRel.getArtA());
         guidArts.add(guidRel.getArtB());
      }
      return guidArts;
   }

   public Collection<EventBasicGuidRelation> getRelations(RelationEventType... eventModTypes) {
      Set<EventBasicGuidRelation> guidRels = new HashSet<EventBasicGuidRelation>();
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

   public boolean isReloaded(IBasicGuidArtifact guidArt) {
      return get(EventModType.Reloaded).contains(guidArt);
   }

   public boolean isModified(Artifact artifact) {
      return isModified(artifact.getBasicGuidArtifact());
   }

   public boolean isModified(IBasicGuidArtifact guidArt) {
      return get(EventModType.Modified).contains(guidArt);
   }

   public boolean isModifiedReloaded(Artifact artifact) {
      return isModifiedReloaded(artifact.getBasicGuidArtifact());
   }

   public boolean isModifiedReloaded(IBasicGuidArtifact guidArt) {
      return get(EventModType.Modified, EventModType.Reloaded).contains(guidArt);
   }

   /**
    * Relation rationale changed
    */
   public boolean isRelChange(Artifact artifact) {
      return isRelChange(artifact.getBasicGuidArtifact());
   }

   /**
    * Relation rationale changed
    */
   public boolean isRelChange(IBasicGuidArtifact guidArt) {
      for (EventBasicGuidRelation guidRel : getRelations(RelationEventType.ModifiedRationale)) {
         if (guidRel.getArtA().equals(guidArt) || guidRel.getArtB().equals(guidArt)) {
            return true;
         }
      }
      return false;
   }

   public boolean isRelDeletedPurged(Artifact artifact) {
      return isRelDeletedPurged(artifact.getBasicGuidArtifact());
   }

   public boolean isRelDeletedPurged(IBasicGuidArtifact guidArt) {
      for (EventBasicGuidRelation guidRel : getRelations(RelationEventType.Deleted, RelationEventType.Purged)) {
         if (guidRel.getArtA().equals(guidArt) || guidRel.getArtB().equals(guidArt)) {
            return true;
         }
      }
      return false;
   }

   public boolean isRelAdded(Artifact artifact) {
      return isRelAdded(artifact.getBasicGuidArtifact());
   }

   public boolean isRelAdded(IBasicGuidArtifact guidArt) {
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
         return String.format("ArtifactEvent: BG[%s] TrId[%d] ARTS[%s] RELS[%s] Sender[%s]", branchGuid, transactionId,
            artifacts, relations, networkSender);
      } catch (Exception ex) {
         return String.format("ArtifactEvent exception: " + ex.getLocalizedMessage());
      }
   }

   /**
    * Returns cached artifacts given type
    */
   public Collection<Artifact> getArtifactsInRelations(IRelationType relationType, RelationEventType... relationEventTypes) throws OseeCoreException {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      Collection<RelationEventType> modTypes = Collections.getAggregate(relationEventTypes);
      for (EventBasicGuidRelation guidRel : relations) {
         if (modTypes.contains(guidRel.getModType())) {
            artifacts.addAll(ArtifactCache.getActive(guidRel));
         }
      }
      return artifacts;
   }

   public boolean isMatch(Collection<IEventFilter> eventFilters) {
      for (IEventFilter eventFilter : eventFilters) {
         for (EventBasicGuidArtifact guidArt : artifacts) {
            if (!((BranchGuidEventFilter) eventFilter).isMatch(guidArt.getBranchGuid())) {
               break;
            }
         }
      }
      return false;
   }

}
