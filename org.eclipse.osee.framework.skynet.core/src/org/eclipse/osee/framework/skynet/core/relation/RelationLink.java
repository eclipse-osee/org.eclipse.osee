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
import java.util.logging.Level;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent.ModType;

/**
 * @author Jeff C. Phillips
 */
public class RelationLink {

   private Artifact artA;
   private Artifact artB;
   private boolean deleted;
   private int aOrder;
   private int bOrder;
   private String rationale;
   private LinkPersistenceMemo memo;
   private IRelationType relationType;
   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();
   protected boolean dirty;
   private int artAId;
   private int artBId;

   private RelationLink(IRelationType relationType, LinkPersistenceMemo memo, String rationale, int aOrder, int bOrder, boolean dirty) {
      this.relationType = relationType;
      this.memo = memo;
      this.rationale = rationale;
      this.aOrder = aOrder;
      this.bOrder = bOrder;
      this.deleted = false;
      this.dirty = false;
      this.dirty = dirty;
   }

   public RelationLink(Artifact artA, Artifact artB, IRelationType relationType, LinkPersistenceMemo memo, String rationale, int aOrder, int bOrder, boolean dirty) {
      this(relationType, memo, rationale, aOrder, bOrder, dirty);
      this.artA = artA;
      this.artB = artB;
   }

   public RelationLink(int artAId, int artBId, IRelationType relationType, LinkPersistenceMemo memo, String rationale, int aOrder, int bOrder, boolean dirty) {
      this(relationType, memo, rationale, aOrder, bOrder, dirty);
      this.artAId = artAId;
      this.artBId = artBId;
   }

   /**
    * Do not call this method from application code. Only designed for active artifacts (the <artifact, branch> cache is
    * used)
    * 
    * @param branch
    * @throws SQLException
    */
   public void loadArtifacts(Branch branch) throws SQLException {
      artA = ArtifactCache.get(artAId, branch);
      if (artA != null && artA.isLinksLoaded()) {
         artA.getLinkManager().addLink(this);
      }

      artB = ArtifactCache.get(artBId, branch);
      if (artB != null && artB.isLinksLoaded()) {
         artB.getLinkManager().addLink(this);
      }
   }

   public Artifact getOtherSideAritfactIfAvailable(Artifact artifact) {
      if (artifact == artA) {
         return artB;
      }
      return artA;
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

   public void persist() throws SQLException {
      RelationPersistenceManager.getInstance().makePersistent(this);
   }

   public void delete() throws SQLException {
      deleted = true;
      // There must be at least one link manager loaded to access delete
      if (!artA.isLinksLoaded() && !artB.isLinksLoaded()) throw new IllegalStateException(
            "Invalid state where neither link manager is loaded");
      // Only one of these needs to be called in order to delete a link
      if (artA.isLinksLoaded()) artA.getLinkManager().deleteLink(this);
      if (artB.isLinksLoaded()) artB.getLinkManager().deleteLink(this);
      kickDeleteLinkEvent();
   }

   private void kickDeleteLinkEvent() {
      eventManager.kick(new CacheRelationModifiedEvent(this, getRelationType().getTypeName(), getASideName(),
            ModType.Deleted.name(), this, getBranch()));
   }

   public Artifact getArtifactA() {
      if (artA == null) {
         try {
            artA = ArtifactQuery.getArtifactFromId(artAId, artB.getBranch());
         } catch (Exception ex) {
         }
      }
      return artA;
   }

   public Artifact getArtifactAIfAvailable() {
      return artA;
   }

   public Artifact getArtifactBIfAvailable() {
      return artB;
   }

   public Artifact getArtifactB() {
      if (artB == null) {
         try {
            artB = ArtifactQuery.getArtifactFromId(artBId, artA.getBranch());
         } catch (Exception ex) {
         }
      }
      return artB;
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
      try {
         this.aOrder = order;
         dirty = true;

         if (artA.isLinksLoaded()) artA.getLinkManager().fixOrderingOf(this, true);
         if (artB.isLinksLoaded()) artB.getLinkManager().fixOrderingOf(this, false);
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   /**
    * @return Returns the order.
    */
   public int getBOrder() {
      return bOrder;
   }

   /**
    * @param order The order to set.
    */
   public void setBOrder(int order) {
      try {
         this.bOrder = order;
         dirty = true;

         if (memo != null) {
            if (artA.isLinksLoaded()) artA.getLinkManager().fixOrderingOf(this, true);
            if (artB.isLinksLoaded()) artB.getLinkManager().fixOrderingOf(this, false);
         }
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   public void swapAOrder(RelationLink link) {
      swapOrder(link, true);
   }

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
      if (rationale == null) throw new IllegalArgumentException("Rationale can not be null");

      if (this.rationale.equals(rationale)) return;

      this.rationale = rationale;

      dirty = true;

      if (notify) {
         eventManager.kick(new CacheRelationModifiedEvent(this, getRelationType().getTypeName(), getASideName(),
               ModType.RationaleMod.name(), this, getBranch()));
      }
   }

   public IRelationType getRelationType() {
      return relationType;
   }

   public LinkPersistenceMemo getPersistenceMemo() {
      return memo;
   }

   public void setPersistenceMemo(LinkPersistenceMemo memo) {
      this.memo = (LinkPersistenceMemo) memo;
   }

   public String getSideNameFor(Artifact artifact) {
      return processArtifactSideName(artifact, false);
   }

   public String getSideNameForOtherArtifact(Artifact artifact) {
      return processArtifactSideName(artifact, true);
   }

   private String processArtifactSideName(Artifact artifact, boolean otherArtifact) {
      String sideName = "";

      if (artifact == artA) {

         if (otherArtifact)
            sideName = relationType.getSideBName();
         else
            sideName = relationType.getSideAName();
      } else if (artifact == artB) {

         if (otherArtifact)
            sideName = relationType.getSideAName();
         else
            sideName = relationType.getSideBName();
      } else
         throw new IllegalArgumentException("Link does not contain the artifact.");

      return sideName;
   }

   public String getSidePhrasingFor(Artifact artifact) {
      return processArtifactSidePhrasing(artifact, false);
   }

   public String getSidePhrasingForOtherArtifact(Artifact artifact) {
      return processArtifactSidePhrasing(artifact, true);
   }

   private String processArtifactSidePhrasing(Artifact artifact, boolean otherArtifact) {
      String sideName = "";

      if (artifact == artA) {

         if (otherArtifact)
            sideName = relationType.getBToAPhrasing();
         else
            sideName = relationType.getAToBPhrasing();
      } else if (artifact == artB) {

         if (otherArtifact)
            sideName = relationType.getAToBPhrasing();
         else
            sideName = relationType.getBToAPhrasing();
      } else
         throw new IllegalArgumentException("Link does not contain the artifact.");

      return sideName;
   }

   public String getASideName() {
      return relationType.getSideAName();
   }

   public String getBSideName() {
      return relationType.getSideBName();
   }

   public String toString() {
      return String.format("%s: %s(%s)<-->%s(%s)", relationType.getTypeName(), artA.getDescriptiveName(),
            Float.toString(aOrder), artB.getDescriptiveName(), Float.toString(bOrder));
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

   public Branch getBranch() {
      return getArtifactA().getBranch();
   }

   public void setDirty(boolean isDirty) {
      dirty = isDirty;
   }
}
