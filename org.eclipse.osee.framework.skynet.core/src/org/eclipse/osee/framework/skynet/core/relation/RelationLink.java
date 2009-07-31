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
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
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
   private int aOrder;
   private int bOrder;
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

   public RelationLink(int aArtifactId, int bArtifactId, Branch aBranch, Branch bBranch, RelationType relationType, int relationId, int gammaId, String rationale, int aOrder, int bOrder, ModificationType modificationType) {
      this.relationType = relationType;
      this.relationId = relationId;
      this.gammaId = gammaId;
      this.rationale = rationale == null ? "" : rationale;
      if (relationType.isOrdered()) {
         this.aOrder = aOrder;
         this.bOrder = bOrder;
      }
      this.dirty = false;
      this.aArtifactId = aArtifactId;
      this.bArtifactId = bArtifactId;
      this.aBranch = aBranch;
      this.bBranch = bBranch;
      this.modificationType = modificationType;
   }

   /**
    * This constructor creates new relations that does not already exist in the data store.
    * 
    * @param modificationType TODO
    */
   public RelationLink(Artifact aArtifact, Artifact bArtifact, RelationType relationType, String rationale, ModificationType modificationType) {
      this(aArtifact.getArtId(), bArtifact.getArtId(), aArtifact.getBranch(), bArtifact.getBranch(), relationType, 0,
            0, rationale, 0, 0, modificationType);
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

   /**
    * @return the aBranch
    */
   public Branch getABranch() {
      return aBranch;
   }

   /**
    * @return the bBranch
    */
   public Branch getBBranch() {
      return bBranch;
   }

   /**
    * @return Returns the deleted.
    */
   public boolean isDeleted() {
      return modificationType.isDeleted();
   }

   /**
    * @return Returns the dirty.
    */
   public boolean isDirty() {
      return dirty;
   }

   public void delete(boolean reorderRelations) throws ArtifactDoesNotExist {
      internalDelete(reorderRelations, true);
   }

   public void internalRemoteEventDelete() throws ArtifactDoesNotExist {
      internalDelete(true, false);
   }

   private void internalDelete(boolean reorderRelations, boolean setDirty) {
      if (!isDeleted()) {
         Artifact aArt = null;
         Artifact bArt = null;
         if (reorderRelations) {
            aArt = preloadArtifactForDelete(RelationSide.SIDE_A);
            bArt = preloadArtifactForDelete(RelationSide.SIDE_B);
         }

         markAsDeleted(setDirty);

         if (aArt != null) {
            RelationManager.setOrderValues(aArt, getRelationType(), RelationSide.SIDE_B, setDirty);
         }
         if (bArt != null) {
            RelationManager.setOrderValues(bArt, getRelationType(), RelationSide.SIDE_A, setDirty);
         }

         if (setDirty) {
            try {
               OseeEventManager.kickRelationModifiedEvent(RelationManager.class, RelationEventType.Deleted, this,
                     getABranch(), relationType.getTypeName());
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
   }

   private Artifact preloadArtifactForDelete(RelationSide side) {
      Artifact artifact = null;
      try {
         artifact = ArtifactQuery.getArtifactFromId(getArtifactId(side), getBranch(side), false);
      } catch (OseeCoreException ex) {
         OseeLog.log(RelationManager.class, Level.SEVERE, ex);
      }
      return artifact;
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
    * @return Returns the order.
    */
   public int getAOrder() {
      return aOrder;
   }

   /**
    * @param order The order to set.
    */
   public void setAOrder(int order) {
      if (aOrder != order) {
         aOrder = order;
         markedAsChanged(ModificationType.MODIFIED, SET_DIRTY);
      }
   }

   /**
    * @return Returns the order.
    */
   public int getBOrder() {
      return bOrder;
   }

   public int getOrder(RelationSide relationSide) {
      return relationSide == RelationSide.SIDE_A ? aOrder : bOrder;
   }

   /**
    * @param order The order to set.
    */
   public void setBOrder(int order) {
      if (bOrder != order) {
         bOrder = order;
         markedAsChanged(ModificationType.MODIFIED, SET_DIRTY);
      }
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

      if (this.rationale.equals(rationale)) return;

      this.rationale = rationale;
      markedAsChanged(ModificationType.MODIFIED, SET_DIRTY);

      if (notify) {
         try {
            OseeEventManager.kickRelationModifiedEvent(RelationManager.class, RelationEventType.RationaleMod, this,
                  getABranch(), relationType.getTypeName());
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   public RelationType getRelationType() {
      return relationType;
   }

   public String getSideNameFor(Artifact artifact) {
      return relationType.getSideName(getSide(artifact));
   }

   @Deprecated
   public String getSidePhrasingFor(Artifact artifact) {
      try {
         return processArtifactSidePhrasing(artifact, false);
      } catch (OseeCoreException ex) {
         return "Unknown - " + ex.getLocalizedMessage();
      }
   }

   @Deprecated
   public String getSidePhrasingForOtherArtifact(Artifact artifact) throws OseeCoreException {
      return processArtifactSidePhrasing(artifact, true);
   }

   @Deprecated
   private String processArtifactSidePhrasing(Artifact artifact, boolean otherArtifact) throws OseeCoreException {
      String sideName = "";

      if (artifact == getArtifact(RelationSide.SIDE_A)) {

         if (otherArtifact)
            sideName = relationType.getBToAPhrasing();
         else
            sideName = relationType.getAToBPhrasing();
      } else if (artifact == getArtifact(RelationSide.SIDE_B)) {

         if (otherArtifact)
            sideName = relationType.getAToBPhrasing();
         else
            sideName = relationType.getBToAPhrasing();
      } else
         throw new IllegalArgumentException("Link does not contain the artifact.");

      return sideName;
   }

   @Override
   public String toString() {
      return String.format("%s id[%d] modType[%s] [%s]: aId[%d] aOrder[%d] <--> bId[%s] bOrder[%d]",
            relationType.getTypeName(), relationId, getModificationType(), (isDirty() ? "dirty" : "not dirty"),
            aArtifactId, aOrder, bArtifactId, bOrder);
   }

   public boolean isExplorable() {
      return true;
   }

   public void setNotDirty() {
      dirty = false;
   }

   public void setDirty() {
      dirty = true;
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

   public void setOrder(RelationSide side, int order) {
      if (RelationSide.SIDE_A == side) {
         setAOrder(order);
      } else if (RelationSide.SIDE_B == side) {
         setBOrder(order);
      }
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
         relationType.equals(other.relationType);

         // This should eventually be removed once DB cleanup occurs
         return result && relationId == other.relationId;
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
      return result;
   }

}