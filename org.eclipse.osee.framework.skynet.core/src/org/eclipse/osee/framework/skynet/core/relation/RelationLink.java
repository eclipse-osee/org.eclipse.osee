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
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
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
   private boolean deleted;
   private int aOrder;
   private int bOrder;
   private String rationale;
   private final RelationType relationType;
   private boolean dirty;
   private final int aArtifactId;
   private final int bArtifactId;
   private final Branch aBranch;
   private final Branch bBranch;

   public RelationLink(int aArtifactId, int bArtifactId, Branch aBranch, Branch bBranch, RelationType relationType, int relationId, int gammaId, String rationale, int aOrder, int bOrder) {
      this.relationType = relationType;
      this.relationId = relationId;
      this.gammaId = gammaId;
      this.rationale = rationale == null ? "" : rationale;
      if (relationType.isOrdered()) {
         this.aOrder = aOrder;
         this.bOrder = bOrder;
      }
      this.deleted = false;
      this.dirty = false;
      this.aArtifactId = aArtifactId;
      this.bArtifactId = bArtifactId;
      this.aBranch = aBranch;
      this.bBranch = bBranch;
   }

   public RelationLink(Artifact aArtifact, Artifact bArtifact, RelationType relationType, String rationale) {
      this(aArtifact.getArtId(), bArtifact.getArtId(), aArtifact.getBranch(), bArtifact.getBranch(), relationType, 0,
            0, rationale, 0, 0);
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
      return deleted;
   }

   /**
    * @return Returns the dirty.
    */
   public boolean isDirty() {
      return dirty;
   }

   public void delete(boolean reorderRelations) throws ArtifactDoesNotExist {
      if (!deleted) {
         Artifact aArt = null;
         Artifact bArt = null;
         if (reorderRelations) {
            aArt = preloadArtifactForDelete(RelationSide.SIDE_A);
            bArt = preloadArtifactForDelete(RelationSide.SIDE_B);
         }
         markAsDeleted();
         setDirty();
         if (reorderRelations) {
            RelationManager.setOrderValuesBasedOnCurrentMemoryOrder(this, aArt, bArt, false);
         }

         try {
            OseeEventManager.kickRelationModifiedEvent(RelationManager.class, RelationModType.Deleted, this,
                  getABranch(), relationType.getTypeName());
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   public void deleteWithoutDirtyAndEvent() throws ArtifactDoesNotExist {
      if (!deleted) {
         Artifact aArt = preloadArtifactForDelete(RelationSide.SIDE_A);
         Artifact bArt = preloadArtifactForDelete(RelationSide.SIDE_B);
         markAsDeleted();
         RelationManager.setOrderValuesBasedOnCurrentMemoryOrder(this, aArt, bArt, true);
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
      deleted = true;
   }

   public void markAsPurged() {
      markAsDeleted();
      setNotDirty();
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
      this.aOrder = order;
      setDirty();
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
      this.bOrder = order;
      setDirty();
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
      setDirty();

      if (notify) {
         try {
            OseeEventManager.kickRelationModifiedEvent(RelationManager.class, RelationModType.RationaleMod, this,
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
      return String.format("%s: A id[%d] order(%d) <--> B id[%s] order(%d) - %s", relationType.getTypeName(),
            aArtifactId, aOrder, bArtifactId, bOrder, isDirty() ? "dirty" : "not dirty");
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

   /**
    * @param side
    * @param order
    */
   public void setOrder(RelationSide side, int order) {
      if (RelationSide.SIDE_A == side) {
         setAOrder(order);
      } else if (RelationSide.SIDE_B == side) {
         setBOrder(order);
      }
   }
}