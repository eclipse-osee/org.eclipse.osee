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

import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 */
public class RelationLink {
   private int relationId;
   private int gammaId;
   private String rationale;
   private final RelationType relationType;
   private boolean dirty;
   private final int aArtifactId;
   private final int bArtifactId;
   private final Branch aBranch;
   private final Branch bBranch;
   private ModificationType modificationType;
   private static final boolean SET_DIRTY = true;
   private static final boolean SET_NOT_DIRTY = false;
   // Set to relationId to determine loading/caching of certain relationIds; set to 0 for production release
   public static int RELATION_ID_UNDER_TEST = 0;

   /**
    * Private constructor. Use getOrCreate().
    */
   private RelationLink(int aArtifactId, int bArtifactId, Branch aBranch, Branch bBranch, RelationType relationType, int relationId, int gammaId, String rationale, ModificationType modificationType) {
      this.relationType = relationType;
      this.relationId = relationId;
      this.gammaId = gammaId;
      this.rationale = rationale == null ? "" : rationale;
      this.dirty = false;
      this.aArtifactId = aArtifactId;
      this.bArtifactId = bArtifactId;
      this.aBranch = aBranch;
      this.bBranch = bBranch;
      this.modificationType = modificationType;
   }

   public static boolean isRelationUnderTest() {
      return RELATION_ID_UNDER_TEST != 0;
   }

   /**
    * Return existing RelationLink or create new one. This needs to be synchronized so two threads don't create the same
    * link object twice.
    * 
    * @param relationId 0 or relationId if already created
    */
   public static synchronized RelationLink getOrCreate(int aArtifactId, int bArtifactId, Branch aBranch, Branch bBranch, RelationType relationType, int relationId, int gammaId, String rationale, ModificationType modificationType) {
      RelationLink relation = null;
      if (relationId != 0) {
         relation = RelationManager.getLoadedRelationById(relationId, aArtifactId, bArtifactId, aBranch, bBranch);
      } else {
         relation = RelationManager.getLoadedRelation(relationType, aArtifactId, bArtifactId, aBranch, bBranch);
      }
      if (isRelationUnderTest() && relationId == RELATION_ID_UNDER_TEST) {
         System.out.println("RelationLink.getOrCreate relationId == " + RELATION_ID_UNDER_TEST);
      }

      if (relation == null || relation.modificationType != modificationType || relation.getRelationId() != relationId) {
         relation =
               new RelationLink(aArtifactId, bArtifactId, aBranch, bBranch, relationType, relationId, gammaId,
                     rationale, modificationType);
      }
      RelationManager.manageRelation(relation, RelationSide.SIDE_A);
      RelationManager.manageRelation(relation, RelationSide.SIDE_B);

      return relation;
   }

   public static RelationLink getOrCreate(Artifact aArtifact, Artifact bArtifact, RelationType relationType, String rationale, ModificationType modificationType) {
      return getOrCreate(aArtifact.getArtId(), bArtifact.getArtId(), aArtifact.getBranch(), bArtifact.getBranch(),
            relationType, 0, 0, rationale, modificationType);
   }

   public RelationSide getSide(Artifact artifact) {
      if (aArtifactId == artifact.getArtId()) {
         return RelationSide.SIDE_A;
      }
      if (bArtifactId == artifact.getArtId()) {
         return RelationSide.SIDE_B;
      }
      throw new IllegalArgumentException("The artifact " + artifact + " is on neither side of " + this);
   }

   /**
    * @return the aArtifactId
    */
   public int getAArtifactId() {
      return aArtifactId;
   }

   /**
    * @return the bArtifactId
    */
   public int getBArtifactId() {
      return bArtifactId;
   }

   public int getArtifactId(RelationSide relationSide) {
      return relationSide == RelationSide.SIDE_A ? aArtifactId : bArtifactId;
   }

   public Branch getABranch() {
      return aBranch;
   }

   public Branch getBBranch() {
      return bBranch;
   }

   public boolean isDeleted() {
      return modificationType.isDeleted();
   }

   public boolean isDirty() {
      return dirty;
   }

   public void delete(boolean reorderRelations) throws ArtifactDoesNotExist {
      internalDelete(reorderRelations, true);
   }

   public void undelete() {
      markedAsChanged(ModificationType.UNDELETED, true);
      try {
         OseeEventManager.kickRelationModifiedEvent(RelationManager.class, RelationEventType.Undeleted, this,
               getABranch(), relationType.getName());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void internalRemoteEventDelete() throws ArtifactDoesNotExist {
      internalDelete(true, false);
   }

   private void internalDelete(boolean reorderRelations, boolean setDirty) {
      if (!isDeleted()) {

         markAsDeleted(setDirty);

         if (setDirty) {
            try {
               OseeEventManager.kickRelationModifiedEvent(RelationManager.class, RelationEventType.Deleted, this,
                     getABranch(), relationType.getName());
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
   }

   public void markAsDeleted() {
      markAsDeleted(SET_DIRTY);
   }

   private void markAsDeleted(boolean setDirty) {
      markedAsChanged(ModificationType.DELETED, setDirty);
   }

   public void markAsPurged() {
      markAsDeleted(SET_NOT_DIRTY);
   }

   public Artifact getArtifact(RelationSide relationSide) throws OseeCoreException {
      Artifact relatedArtifact = getArtifactIfLoaded(relationSide);
      if (relatedArtifact == null) {
         return ArtifactQuery.getArtifactFromId(getArtifactId(relationSide), getBranch(relationSide));
      }
      return relatedArtifact;
   }

   public Artifact getArtifactIfLoaded(RelationSide relationSide) {
      return ArtifactCache.getActive(getArtifactId(relationSide), getBranch(relationSide));
   }

   public Artifact getArtifactOnOtherSide(Artifact artifact) throws OseeCoreException {
      return getArtifact(getSide(artifact).oppositeSide());
   }

   public Artifact getArtifactOnOtherSideIfLoaded(Artifact artifact) throws ArtifactDoesNotExist {
      return getArtifactIfLoaded(getSide(artifact).oppositeSide());
   }

   public Artifact getArtifactA() throws OseeCoreException {
      return getArtifact(RelationSide.SIDE_A);
   }

   public Artifact getArtifactB() throws OseeCoreException {
      return getArtifact(RelationSide.SIDE_B);
   }

   /**
    * @return Returns the rationale.
    */
   public String getRationale() {
      return rationale;
   }

   /**
    * @param rationale The rationale to set.
    */
   public void setRationale(String rationale, boolean notify) {
      if (rationale == null) {
         rationale = "";
      }

      if (this.rationale.equals(rationale)) {
         return;
      }

      this.rationale = rationale;
      markedAsChanged(ModificationType.MODIFIED, SET_DIRTY);

      if (notify) {
         try {
            OseeEventManager.kickRelationModifiedEvent(RelationManager.class, RelationEventType.RationaleMod, this,
                  getABranch(), relationType.getName());
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   public boolean isOfType(IRelationType oseeType) throws OseeCoreException {
      return relationType.equals(oseeType);
   }

   public RelationType getRelationType() {
      return relationType;
   }

   public String getSideNameFor(Artifact artifact) {
      return relationType.getSideName(getSide(artifact));
   }

   public String getSidePhrasingFor(Artifact artifact) throws OseeCoreException {
      return getSidePhrasingFor(artifact, false);
   }

   public String getSidePhrasingForOtherArtifact(Artifact artifact) throws OseeCoreException {
      return getSidePhrasingFor(artifact, true);
   }

   private String getSidePhrasingFor(Artifact artifact, boolean isOtherArtifact) throws OseeCoreException {
      RelationSide side;
      if (artifact == getArtifact(RelationSide.SIDE_A)) {
         side = RelationSide.SIDE_A;
      } else if (artifact == getArtifact(RelationSide.SIDE_B)) {
         side = RelationSide.SIDE_B;
      } else {
         throw new OseeArgumentException("Link does not contain the artifact.");
      }
      if (isOtherArtifact) {
         side = side.oppositeSide();
      }
      return "has (" + getRelationType().getMultiplicity().asLimitLabel(side) + ")";
   }

   @Override
   public String toString() {
      String artAName = "Unloaded";
      String artBName = "Unloaded";
      try {
         Artifact artA = ArtifactCache.getActive(getAArtifactId(), getABranch());
         if (artA != null) {
            artAName = artA.getSafeName();
         }
         Artifact artB = ArtifactCache.getActive(getBArtifactId(), getBBranch());
         if (artB != null) {
            artBName = artB.getSafeName();
         }
      } catch (Exception ex) {
         // do nothing
      }
      return String.format("type[%s] id[%d] modType[%s] [%s]: aName[%s] aId[%d] <--> bName[%s] bId[%s]",
            relationType.getName(), relationId, getModificationType(), (isDirty() ? "dirty" : "not dirty"), artAName,
            aArtifactId, artBName, bArtifactId);
   }

   public boolean isExplorable() {
      return true;
   }

   public void setNotDirty() {
      setDirtyFlag(false);
   }

   public void setDirty() {
      setDirtyFlag(true);
   }

   private void setDirtyFlag(boolean dirty) {
      this.dirty = dirty;
      try {
         ArtifactCache.updateCachedArtifact(aArtifactId, aBranch.getId());
         ArtifactCache.updateCachedArtifact(bArtifactId, bBranch.getId());
      } catch (OseeStateException ex) {
         OseeLog.log(RelationLink.class, Level.SEVERE, ex.toString(), ex);
      } catch (OseeCoreException ex) {
         OseeLog.log(RelationLink.class, Level.SEVERE, ex.toString(), ex);
      }
   }

   public boolean isVersionControlled() {
      return true;
   }

   public Branch getBranch(RelationSide relationSide) {
      return relationSide == RelationSide.SIDE_A ? aBranch : bBranch;
   }

   public void internalSetRelationId(int relationId) {
      this.relationId = relationId;
   }

   public int getRelationId() {
      return relationId;
   }

   public int getGammaId() {
      return gammaId;
   }

   public boolean isInDb() {
      return getRelationId() > 0;
   }

   /**
    * @param gammaId
    */
   void internalSetGammaId(int gammaId) {
      this.gammaId = gammaId;
   }

   /**
    * @return Branch
    */
   public Branch getBranch() {
      return getBranch(RelationSide.SIDE_A);
   }

   private void markedAsChanged(ModificationType modificationType, boolean setDirty) {
      //Because deletes can reorder links and we want the final mod type to be the delete and not the modify.
      if (modificationType != ModificationType.DELETED || modificationType != ModificationType.ARTIFACT_DELETED) {
         this.modificationType = modificationType;
      }

      if (setDirty) {
         setDirty();
      } else {
         setNotDirty();
      }
   }

   /**
    * @return the modificationType
    */
   public ModificationType getModificationType() {
      return modificationType;
   }

   public void setArtifactDeleted() {
      markedAsChanged(ModificationType.ARTIFACT_DELETED, SET_DIRTY);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof RelationLink) {
         RelationLink other = (RelationLink) obj;
         boolean result = aArtifactId == other.aArtifactId && aBranch.equals(other.aBranch) &&
         //
         bArtifactId == other.bArtifactId && bBranch.equals(other.bBranch) &&
         //
         other.modificationType == modificationType &&
         //
         relationType.equals(other.relationType);

         // This should eventually be removed once DB cleanup occurs
         return result && relationId == other.relationId;
      }
      return false;
   }

   /**
    * Same as equals except don't check relationIds. This is what equals should become once database is cleaned
    * (permanently) of duplicate "conceptual" relations. ex same artA, artB and relationType
    */
   public boolean equalsConceptually(Object obj) {
      if (obj instanceof RelationLink) {
         RelationLink other = (RelationLink) obj;
         boolean result = aArtifactId == other.aArtifactId && aBranch.equals(other.aBranch) &&
         //
         bArtifactId == other.bArtifactId && bBranch.equals(other.bBranch) &&
         //
         other.modificationType == modificationType &&
         //
         relationType.equals(other.relationType);

         return result;
      }
      return false;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + aArtifactId;
      result = prime * result + aBranch.hashCode();
      result = prime * result + bArtifactId;
      result = prime * result + bBranch.hashCode();
      result = prime * result + relationType.hashCode();
      result = prime * result + modificationType.hashCode();
      return result;
   }

}