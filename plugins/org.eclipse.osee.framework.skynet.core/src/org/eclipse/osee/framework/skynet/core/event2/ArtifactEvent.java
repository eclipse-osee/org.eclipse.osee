package org.eclipse.osee.framework.skynet.core.event2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.event.IBasicGuidArtifact;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.Activator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData.ChangeType;
import org.eclipse.osee.framework.skynet.core.event.msgs.NetworkSender;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventModType;
import org.eclipse.osee.framework.skynet.core.event2.filter.BranchGuidEventFilter;
import org.eclipse.osee.framework.skynet.core.event2.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;

public class ArtifactEvent extends FrameworkEvent {

   private String branchGuid;
   private int transactionId;
   private List<EventBasicGuidArtifact> artifacts;
   private List<EventBasicGuidRelation> relations;
   private NetworkSender networkSender;

   public String getBranchGuid() {
      return branchGuid;
   }

   public void setBranchGuid(String value) {
      this.branchGuid = value;
   }

   public int getTransactionId() {
      return transactionId;
   }

   public void setTransactionId(int value) {
      this.transactionId = value;
   }

   public List<EventBasicGuidArtifact> getArtifacts() {
      if (artifacts == null) {
         artifacts = new ArrayList<EventBasicGuidArtifact>();
      }
      return this.artifacts;
   }

   public List<EventBasicGuidRelation> getRelations() {
      if (relations == null) {
         relations = new ArrayList<EventBasicGuidRelation>();
      }
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
      return isChanged(guidArt) || isDeletedPurged(guidArt) || isRelChange(guidArt) || isRelDeletedPurged(guidArt) || isRelAdded(guidArt);
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

   public Collection<EventBasicGuidArtifact> getDeletedPurged() {
      return get(EventModType.Deleted, EventModType.Purged);
   }

   public Collection<EventBasicGuidArtifact> getModified() {
      return get(EventModType.Modified, EventModType.Reloaded);
   }

   public Collection<Artifact> getModifiedCacheArtifacts() {
      try {
         return ArtifactCache.getActive(getModified());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return java.util.Collections.emptyList();
   }

   public Collection<Artifact> getRelModifiedCacheArtifacts() {
      try {
         return ArtifactCache.getActive(getRelationsArts(RelationEventType.ModifiedRationale,
               RelationEventType.ReOrdered));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return java.util.Collections.emptyList();
   }

   public Collection<Artifact> getRelCacheArtifacts() {
      try {
         return ArtifactCache.getActive(getRelationsArts(RelationEventType.ModifiedRationale,
               RelationEventType.ReOrdered, RelationEventType.Added, RelationEventType.Deleted,
               RelationEventType.Purged, RelationEventType.Undeleted));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return java.util.Collections.emptyList();
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
      for (EventBasicGuidRelation guidRel : guidRels) {
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

   public boolean isChanged(Artifact artifact) {
      return isChanged(artifact.getBasicGuidArtifact());
   }

   public boolean isChanged(IBasicGuidArtifact guidArt) {
      return get(EventModType.Modified, EventModType.Reloaded).contains(guidArt);
   }

   /**
    * Relation rationale or order changed
    */
   public boolean isRelChange(Artifact artifact) {
      return isRelChange(artifact.getBasicGuidArtifact());
   }

   /**
    * Relation rationale or order changed
    */
   public boolean isRelChange(IBasicGuidArtifact guidArt) {
      for (EventBasicGuidRelation guidRel : getRelations(RelationEventType.ModifiedRationale,
            RelationEventType.ReOrdered)) {
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
         if (modTypes.contains(ChangeType.All) || modTypes.contains(guidRel.getModType())) {
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
