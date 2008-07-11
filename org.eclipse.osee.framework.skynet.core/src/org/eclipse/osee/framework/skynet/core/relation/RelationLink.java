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

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent.ModType;

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
   private RelationType relationType;
   private boolean dirty;
   private int aArtifactId;
   private int bArtifactId;
   private Branch aBranch;
   private Branch bBranch;

   public RelationLink(int aArtifactId, int bArtifactId, Branch aBranch, Branch bBranch, RelationType relationType, int relationId, int gammaId, String rationale, int aOrder, int bOrder) {
      this.relationType = relationType;
      this.relationId = relationId;
      this.gammaId = gammaId;
      this.rationale = rationale == null ? "" : rationale;
      this.aOrder = aOrder;
      this.bOrder = bOrder;
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

   @Deprecated
   public boolean isOnSideA(Artifact artifact) {
      if (aArtifactId == artifact.getArtId()) {
         return true;
      }
      if (bArtifactId == artifact.getArtId()) {
         return false;
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

   @Deprecated
   public void persist() throws SQLException {
      if (dirty) {
         RelationPersistenceManager.makePersistent(this);
      }
   }

   public void delete() throws ArtifactDoesNotExist, SQLException {
      if (!deleted) {
         markAsDeleted();
         dirty = true;
         RelationManager.setOrderValuesBasedOnCurrentMemoryOrder(this, false);
         SkynetEventManager.getInstance().kick(
               new CacheRelationModifiedEvent(this, getRelationType().getTypeName(), getASideName(),
                     ModType.Deleted.name(), this, getBranch()));
      }
   }

   public void deleteWithoutDirtyAndEvent() throws ArtifactDoesNotExist, SQLException {
      if (!deleted) {
         markAsDeleted();
         RelationManager.setOrderValuesBasedOnCurrentMemoryOrder(this, true);
      }
   }

   public void markAsDeleted() {
      deleted = true;
   }

   public void markAsPurged() {
      markAsDeleted();
      dirty = false;
   }

   public Artifact getArtifact(RelationSide relationSide) throws ArtifactDoesNotExist, SQLException {
      Artifact relatedArtifact = getArtifactIfLoaded(relationSide);
      if (relatedArtifact == null) {
         return ArtifactQuery.getArtifactFromId(getArtifactId(relationSide), getBranch(relationSide));
      }
      return relatedArtifact;
   }

   public Artifact getArtifactIfLoaded(RelationSide relationSide) {
      return ArtifactCache.getActive(getArtifactId(relationSide), getBranch(relationSide));
   }

   public Artifact getArtifactOnOtherSide(Artifact artifact) throws ArtifactDoesNotExist, SQLException {
      return getArtifact(getSide(artifact).oppositeSide());
   }

   public Artifact getArtifactOnOtherSideIfLoaded(Artifact artifact) throws ArtifactDoesNotExist, SQLException {
      return getArtifactIfLoaded(getSide(artifact).oppositeSide());
   }

   @Deprecated
   public Artifact getArtifactA() throws ArtifactDoesNotExist, SQLException {
      return getArtifact(RelationSide.SIDE_A);
   }

   @Deprecated
   public Artifact getArtifactB() throws ArtifactDoesNotExist, SQLException {
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
      dirty = true;
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
      dirty = true;
   }

   @Deprecated
   public void swapAOrder(RelationLink link) {
      swapOrder(link, true);
   }

   @Deprecated
   public void swapBOrder(RelationLink link) {
      swapOrder(link, false);
   }

   private void swapOrder(RelationLink link, boolean sideA) {
      if (link == null) throw new IllegalArgumentException("link can not be null.");

      // Swapping a link with itself has no effect.
      if (link == this) return;

      if (sideA) {
         int tmp = aOrder;
         if (link.getAOrder() == aOrder)
            setAOrder(link.getAOrder() + 1);
         else
            setAOrder(link.getAOrder());
         link.setAOrder(tmp);
      } else {
         int tmp = bOrder;
         setBOrder(link.getBOrder());
         link.setBOrder(tmp);
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
      dirty = true;

      if (notify) {
         SkynetEventManager.getInstance().kick(
               new CacheRelationModifiedEvent(this, getRelationType().getTypeName(), getASideName(),
                     ModType.RationaleMod.name(), this, getABranch()));
      }
   }

   public RelationType getRelationType() {
      return relationType;
   }

   public String getSideNameFor(Artifact artifact) {
      return processArtifactSideName(artifact, false);
   }

   public String getSideNameForOtherArtifact(Artifact artifact) {
      return processArtifactSideName(artifact, true);
   }

   @Deprecated
   private String processArtifactSideName(Artifact artifact, boolean otherArtifact) {
      for (RelationSide side : RelationSide.getSides()) {
         try {
            Artifact linkArt = getArtifact(side);
            if (linkArt == artifact) {
               if (otherArtifact) {
                  return relationType.getSideName(side.oppositeSide());
               } else {
                  return relationType.getSideName(side);
               }
            }
         } catch (ArtifactDoesNotExist ex) {

         } catch (SQLException ex) {

         }
      }
      throw new IllegalArgumentException("Link does not contain the artifact.");
   }

   @Deprecated
   public String getSidePhrasingFor(Artifact artifact) {
      try {
         return processArtifactSidePhrasing(artifact, false);
      } catch (ArtifactDoesNotExist ex) {
         return "Unknown - " + ex.getMessage();
      } catch (SQLException ex) {
         return "Unknown - " + ex.getMessage();
      }
   }

   @Deprecated
   public String getSidePhrasingForOtherArtifact(Artifact artifact) throws ArtifactDoesNotExist, SQLException {
      return processArtifactSidePhrasing(artifact, true);
   }

   @Deprecated
   private String processArtifactSidePhrasing(Artifact artifact, boolean otherArtifact) throws ArtifactDoesNotExist, SQLException {
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

   @Deprecated
   public String getASideName() {
      return relationType.getSideAName();
   }

   @Deprecated
   public String getBSideName() {
      return relationType.getSideBName();
   }

   public String toString() {
      return String.format("%s: A [%d](%d) <--> B [%s](%d)", relationType.getTypeName(), aArtifactId, aOrder,
            bArtifactId, bOrder);
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

   public void setPersistenceIds(int relationId, int gammaId) {
      this.relationId = relationId;
      this.gammaId = gammaId;
   }

   public int getRelationId() {
      return relationId;
   }

   public int getGammaId() {
      return gammaId;
   }

   public boolean isInDb() {
      return gammaId > 0;
   }

   /**
    * @param gammaId
    */
   public void setGammaId(int gammaId) {
      this.gammaId = gammaId;
   }

   /**
    * @return
    */
   public Branch getBranch() {
      return getBranch(RelationSide.SIDE_A);
   }

   /**
    * @param side
    * @param i
    */
   public void setOrder(RelationSide side, int order) {
      if (RelationSide.SIDE_A == side) {
         setAOrder(order);
      } else if (RelationSide.SIDE_B == side) {
         setBOrder(order);
      }
   }

}
