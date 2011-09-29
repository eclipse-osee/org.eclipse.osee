/*
 * Created on Sep 28, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal.relation;

import javax.management.relation.RelationType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.orcs.data.WritableRelation;

public class Relation implements WritableRelation {

   private final int relationId;
   private final int gammaId;
   private final String rationale;
   private final RelationType relationType;
   private final boolean dirty;
   private final int aArtifactId;
   private final int bArtifactId;
   private ModificationType modificationType;

   private static final boolean SET_DIRTY = true;
   private static final boolean SET_NOT_DIRTY = false;

   Relation(int aArtifactId, int bArtifactId, RelationType relationType, int relationId, int gammaId, String rationale, ModificationType modificationType) {
      this.relationType = relationType;
      this.relationId = relationId;
      this.gammaId = gammaId;
      this.rationale = rationale == null ? "" : rationale;
      this.dirty = false;
      this.aArtifactId = aArtifactId;
      this.bArtifactId = bArtifactId;
      this.modificationType = modificationType;
   }

   public void internalSetModificationType(ModificationType modificationType) {
      this.modificationType = modificationType;
   }

   @Override
   public int getAArtifactId() {
      return aArtifactId;
   }

   @Override
   public int getBArtifactId() {
      return bArtifactId;
   }

   public boolean isDirty() {
      return dirty;
   }

   public void delete(boolean reorderRelations) {
      //      internalDelete(reorderRelations, true);
   }

   public void undelete() {
      //      internalUnDelete();
   }

   public void internalUnDelete() {
      //      markedAsChanged(ModificationType.UNDELETED, true);
   }

   public void internalRemoteEventDelete() {
      //      internalDelete(true, false);
   }

   private void internalDelete(boolean reorderRelations, boolean setDirty) {
      //      if (!isDeleted()) {
      //         if (reorderRelations) {
      //            try {
      //               artifactLinker.deleteFromRelationOrder(getArtifactA(), getArtifactB(), getRelationType());
      //            } catch (OseeCoreException e) {
      //               OseeLog.log(Activator.class, Level.SEVERE, e.getMessage());
      //            }
      //         }
      //
      //         markAsDeleted(setDirty);
      //      }
   }

   private void markAsDeleted(boolean setDirty) {
      //      markedAsChanged(ModificationType.DELETED, setDirty);
   }

   public void markAsPurged() {
      //      markAsDeleted(SET_NOT_DIRTY);
   }

   @Override
   public String getRationale() {
      return rationale;
   }

   public void setRationale(String rationale) {
      //      if (rationale == null) {
      //         rationale = "";
      //      }
      //      if (this.rationale.equals(rationale)) {
      //         return;
      //      }
      //      internalSetRationale(rationale);
      //      markedAsChanged(ModificationType.MODIFIED, SET_DIRTY);
   }

   public void internalSetRationale(String rationale) {
      //      if (rationale == null) {
      //         rationale = "";
      //      }
      //      if (this.rationale.equals(rationale)) {
      //         return;
      //      }
      //      this.rationale = rationale;
   }

   @Override
   public RelationType getRelationType() {
      return relationType;
   }

   //   @Override
   //   public String toString() {
   //      String artAName = artifactLinker.getLazyArtifactName(getAArtifactId(), getBranch());
   //      String artBName = artifactLinker.getLazyArtifactName(getBArtifactId(), getBranch());
   //      return String.format("type[%s] id[%d] modType[%s] [%s]: aName[%s] aId[%d] <--> bName[%s] bId[%s]",
   //         relationType.getName(), relationId, getModificationType(), (isDirty() ? "dirty" : "not dirty"), artAName,
   //         aArtifactId, artBName, bArtifactId);
   //   }

   public void setNotDirty() {
      //      setDirtyFlag(false);
   }

   public void setDirty() {
      //      setDirtyFlag(true);
   }

   private void setDirtyFlag(boolean dirty) {
      //      this.dirty = dirty;
      //      artifactLinker.updateCachedArtifact(aArtifactId, getBranch());
      //      artifactLinker.updateCachedArtifact(bArtifactId, getBranch());
   }

   public void internalSetRelationId(int relationId) {
      //      this.relationId = relationId;
   }

   @Override
   public int getId() {
      return relationId;
   }

   @Override
   public int getGammaId() {
      return gammaId;
   }

   void internalSetGammaId(int gammaId) {
      //      this.gammaId = gammaId;
   }

   private void markedAsChanged(ModificationType modificationType, boolean setDirty) {
      //Because deletes can reorder links and we want the final mod type to be the delete and not the modify.
      //      if (modificationType != ModificationType.DELETED || modificationType != ModificationType.ARTIFACT_DELETED) {
      //         this.modificationType = modificationType;
      //      }
      //
      //      if (setDirty) {
      //         setDirty();
      //      } else {
      //         setNotDirty();
      //      }
   }

   @Override
   public ModificationType getModificationType() {
      return modificationType;
   }

   //   @Override
   //   public boolean equals(Object obj) {
   //      if (obj instanceof RelationLink) {
   //         RelationLink other = (RelationLink) obj;
//         //@formatter:off
//         boolean result = aArtifactId == other.aArtifactId && 
//         branch.equals(other.branch) &&
//         bArtifactId == other.bArtifactId && 
//         other.modificationType == modificationType &&
//         relationType.equals(other.relationType);
//         //@formatter:on
   //
   //         // This should eventually be removed once DB cleanup occurs
   //         return result && relationId == other.relationId;
   //      }
   //      return false;
   //   }

   /**
    * Same as equals except don't check relationIds. This is what equals should become once database is cleaned
    * (permanently) of duplicate "conceptual" relations. ex same artA, artB and relationType
    */
   //   public boolean equalsConceptually(Object obj) {
   //      if (obj instanceof RelationLink) {
   //         RelationLink other = (RelationLink) obj;
//         //@formatter:off
//         return aArtifactId == other.aArtifactId && 
//         branch.equals(other.branch) && 
//         bArtifactId == other.bArtifactId && 
//         other.modificationType == modificationType && 
//         relationType.equals(other.relationType);
//         //@formatter:on
   //      }
   //      return false;
   //   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + relationId;
      result = prime * result + aArtifactId;
      result = prime * result + bArtifactId;
      result = prime * result + relationType.hashCode();
      result = prime * result + modificationType.hashCode();
      return result;
   }

}
