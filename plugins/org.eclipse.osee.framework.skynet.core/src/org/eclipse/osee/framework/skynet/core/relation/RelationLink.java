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
package org.eclipse.osee.framework.skynet.core.relation;

import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.HasBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderFactory;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 */
public class RelationLink implements HasBranch {
   private int relationId;
   private int gammaId;
   private String rationale;
   private final RelationTypeToken relationType;
   private boolean dirty;
   private final ArtifactToken artifactA;
   private final ArtifactToken artifactB;
   private final BranchId branch;
   private ModificationType modificationType;
   private final ApplicabilityId applicabilityId;

   private static final boolean SET_DIRTY = true;
   private static final boolean SET_NOT_DIRTY = false;

   private boolean useBackingData;

   public RelationLink(ArtifactToken artifactA, ArtifactToken artifactB, BranchId branch, RelationTypeToken relationType, int relationId, int gammaId, String rationale, ModificationType modificationType, ApplicabilityId applicabilityId) {
      this.relationType = relationType;
      this.relationId = relationId;
      this.gammaId = gammaId;
      this.rationale = rationale == null ? "" : rationale;
      this.dirty = false;
      this.artifactA = artifactA;
      this.artifactB = artifactB;
      this.branch = branch;
      this.applicabilityId = applicabilityId;
      internalSetModType(modificationType, false, false);
   }

   public void internalSetModType(ModificationType modificationType, boolean useBackingData, boolean dirty) {
      this.modificationType = modificationType;
      this.useBackingData = useBackingData;
      this.dirty = dirty;
   }

   public RelationSide getSide(ArtifactId artifact) {
      if (artifactA.equals(artifact)) {
         return RelationSide.SIDE_A;
      }
      if (artifactB.equals(artifact)) {
         return RelationSide.SIDE_B;
      }
      throw new IllegalArgumentException("The artifact " + artifact + " is on neither side of " + this);
   }

   public RelationSide getOppositeSide(ArtifactId artifact) {
      return getSide(artifact).oppositeSide();
   }

   /**
    * artifact.persist(); artifact.reloadAttributesAndRelations(); Will need to be called afterwards to see replaced
    * data in memory
    */
   public void replaceWithVersion(int gammaId) {
      internalSetPersistenceData(gammaId, ModificationType.REPLACED_WITH_VERSION);
   }

   private void internalSetPersistenceData(int gammaId, ModificationType modType) {
      internalSetModType(modType, true, true);
      internalSetGammaId(gammaId);
   }

   public ArtifactToken getArtifactId(RelationSide relationSide) {
      return relationSide == RelationSide.SIDE_A ? artifactA : artifactB;
   }

   public boolean isDeleted() {
      return modificationType.isDeleted();
   }

   public boolean isUnDeleted() {
      return modificationType.isUnDeleted();
   }

   public boolean isDirty() {
      return dirty;
   }

   public ApplicabilityId getApplicabilityId() {
      return applicabilityId;
   }

   public void delete(boolean reorderRelations) {
      delete(reorderRelations, null);
   }

   public void delete(boolean reorderRelations, SkynetTransaction transaction) {
      internalDelete(reorderRelations, true);

      deleteEmptyRelationOrder(transaction);
   }

   private void deleteEmptyRelationOrder(SkynetTransaction transaction) {
      try {
         Artifact aArtifact = getArtifactA();

         if (aArtifact.getAttributeCount(CoreAttributeTypes.RelationOrder) == 1) {
            RelationOrderData relationOrderData = new RelationOrderFactory().createRelationOrderData(aArtifact);
            if (!relationOrderData.hasEntries()) {
               aArtifact.getSoleAttribute(CoreAttributeTypes.RelationOrder).delete();
               if (transaction == null) {
                  aArtifact.persist("Delete empty relation order attribute for artifact: " + aArtifact.getGuid());
               } else {
                  aArtifact.persist(transaction);
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(this.getClass(), Level.INFO, ex.toString(), ex);
      }
   }

   public void undelete() {
      internalUnDelete();
   }

   public void internalUnDelete() {
      internalSetModType(ModificationType.UNDELETED, false, true);
   }

   /**
    * This is only for remote events because it does not </br>
    * 1) reorder relations, cause the attribute change came over and was already applied</br>
    * 2) set the relation dirty
    */
   public void internalRemoteEventDelete() {
      internalDelete(false, false);
   }

   private void internalDelete(boolean reorderRelations, boolean setDirty) {
      if (!isDeleted()) {
         if (reorderRelations) {
            Artifact aArtifact = getArtifactA();
            Artifact bArtifact = getArtifactB();
            RelationTypeSideSorter aSorter = RelationManager.createTypeSideSorter(aArtifact, relationType, SIDE_B);
            aSorter.removeItem(bArtifact);

            RelationTypeSideSorter bSorter = RelationManager.createTypeSideSorter(bArtifact, relationType, SIDE_A);
            bSorter.removeItem(aArtifact);
         }
         internalSetModType(ModificationType.DELETED, true, setDirty);
      }
   }

   public void markAsPurged() {
      internalSetModType(ModificationType.DELETED, false, SET_NOT_DIRTY);
   }

   public Artifact getArtifact(RelationSide relationSide) {
      return getArtifact(getArtifactId(relationSide));
   }

   private Artifact getArtifact(ArtifactToken artifact) {
      return ArtifactQuery.getArtifactFromToken(artifact, DeletionFlag.INCLUDE_DELETED);
   }

   public ArtifactToken getOtherSideArtifact(ArtifactId artifact) {
      return artifact.equals(artifactB) ? artifactA : artifactB;
   }

   public Artifact getArtifactOnOtherSide(ArtifactId artifact)  {
      return getArtifact(getOtherSideArtifact(artifact));
   }

   public Artifact getArtifactA()  {
      return getArtifact(artifactA);
   }

   public Artifact getArtifactB()  {
      return getArtifact(artifactB);
   }

   public ArtifactToken getArtifactIdA() {
      return artifactA;
   }

   public ArtifactToken getArtifactIdB() {
      return artifactB;
   }

   public String getRationale() {
      return rationale;
   }

   public void setRationale(String rationale) {
      if (rationale == null) {
         rationale = "";
      }
      if (this.rationale.equals(rationale)) {
         return;
      }
      internalSetRationale(rationale);
      internalSetModType(ModificationType.MODIFIED, false, SET_DIRTY);

   }

   public void internalSetRationale(String rationale) {
      if (rationale == null) {
         rationale = "";
      }
      if (this.rationale.equals(rationale)) {
         return;
      }
      this.rationale = rationale;
   }

   public boolean isOfType(IRelationType oseeType) {
      return relationType.equals(oseeType);
   }

   public RelationTypeToken getRelationType() {
      return relationType;
   }

   public String getSidePhrasingFor(Artifact artifact)  {
      return getSidePhrasingFor(artifact, false);
   }

   public String getSidePhrasingForOtherArtifact(Artifact artifact)  {
      return getSidePhrasingFor(artifact, true);
   }

   private String getSidePhrasingFor(Artifact artifact, boolean isOtherArtifact)  {
      RelationSide side;
      if (artifact.equals(getArtifact(RelationSide.SIDE_A))) {
         side = RelationSide.SIDE_A;
      } else if (artifact.equals(getArtifact(RelationSide.SIDE_B))) {
         side = RelationSide.SIDE_B;
      } else {
         throw new OseeArgumentException("Link does not contain the artifact.");
      }
      if (isOtherArtifact) {
         side = side.oppositeSide();
      }
      return "has (" + RelationTypeManager.getType(relationType).getMultiplicity().asLimitLabel(side) + ")";
   }

   @Override
   public String toString() {
      return String.format("type[%s] id[%s] modType[%s] [%s]: aId[%s] <--> bId[%s]", relationType.getName(), relationId,
         getModificationType(), isDirty() ? "dirty" : "not dirty", artifactA.getIdString(), artifactB.getIdString());
   }

   public void setNotDirty() {
      setDirtyFlag(false);
   }

   public void setDirty() {
      setDirtyFlag(true);
   }

   private void setDirtyFlag(boolean dirty) {
      this.dirty = dirty;
      ArtifactCache.updateCachedArtifact(artifactA);
      ArtifactCache.updateCachedArtifact(artifactB);
   }

   public void internalSetRelationId(int relationId) {
      this.relationId = relationId;
   }

   public int getId() {
      return relationId;
   }

   public int getGammaId() {
      return gammaId;
   }

   public boolean isInDb() {
      return getId() > 0;
   }

   void internalSetGammaId(int gammaId) {
      this.gammaId = gammaId;
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }

   public ModificationType getModificationType() {
      return modificationType;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof RelationLink) {
         RelationLink other = (RelationLink) obj;
         // This should eventually be removed once DB cleanup occurs
         return equalsConceptually(other) && relationId == other.relationId;
      }
      return false;
   }

   /**
    * Same as equals except don't check relationIds. This is what equals should become once database is cleaned
    * (permanently) of duplicate "conceptual" relations. ex same artA, artB and relationType
    */
   public boolean equalsConceptually(RelationLink other) {
      return artifactA.equals(other.artifactA) && artifactB.equals(other.artifactB) && isOnSameBranch(
         other) && other.modificationType == modificationType && relationType.equals(other.relationType);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + branch.hashCode();
      result = prime * result + artifactA.hashCode();
      result = prime * result + artifactB.hashCode();
      result = prime * result + relationType.hashCode();
      result = prime * result + modificationType.hashCode();
      return result;
   }

   public boolean isUserDefined()  {
      RelationOrderFactory factory = new RelationOrderFactory();
      Artifact aArtifact = getArtifactA();
      Artifact bArtifact = getArtifactB();

      RelationOrderData leftData = factory.createRelationOrderData(aArtifact);
      RelationOrderData rightData = factory.createRelationOrderData(bArtifact);

      RelationType relType = RelationTypeManager.getType(relationType);

      RelationSorter leftSorter = leftData.getCurrentSorterGuid(relType, getSide(aArtifact));
      RelationSorter rightSorter = rightData.getCurrentSorterGuid(relType, getSide(bArtifact));

      return rightSorter.equals(USER_DEFINED) && leftSorter.equals(USER_DEFINED);
   }

   public void introduce(int sourceGamma, ModificationType sourceModificationType) {
      internalSetPersistenceData(sourceGamma, sourceModificationType);
   }

   public boolean isUseBackingData() {
      return useBackingData;
   }

   public String getIdString() {
      return String.valueOf(getId());
   }
}