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
import org.eclipse.osee.framework.jdk.core.util.PersistenceMemo;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent.ModType;

/**
 * @author Robert A. Fisher
 */
public abstract class RelationLinkBase implements IRelationLink {

   private static int count = 0;
   public final int aaaSerialId = count++;

   private Artifact artA;
   private Artifact artB;
   private boolean deleted;
   private int aOrder;
   private int bOrder;
   private String rationale;
   private LinkPersistenceMemo memo;
   private IRelationLinkDescriptor descriptor;
   private static final RelationPersistenceManager relationManager = RelationPersistenceManager.getInstance();
   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();

   protected boolean dirty;

   protected RelationLinkBase(IRelationLinkDescriptor descriptor) {
      if (descriptor == null) throw new IllegalArgumentException("descriptor can not be null");

      this.descriptor = descriptor;
      this.deleted = false;
      this.dirty = true;
      this.rationale = "";
   }

   protected RelationLinkBase(Artifact artA, Artifact artB, IRelationLinkDescriptor descriptor, LinkPersistenceMemo memo, String rationale, int aOrder, int bOrder) {
      if (descriptor == null) throw new IllegalArgumentException("descriptor can not be null");
      if (artA != null && artA.getBranch() != descriptor.getTransactionId().getBranch()) throw new IllegalArgumentException(
            "artA must be on the same branch as the descriptor");
      if (artB != null && artB.getBranch() != descriptor.getTransactionId().getBranch()) throw new IllegalArgumentException(
            "artB must be on the same branch as the descriptor");

      this.artA = artA;
      this.artB = artB;
      this.descriptor = descriptor;
      this.memo = memo;
      this.rationale = rationale;
      this.aOrder = aOrder;
      this.bOrder = bOrder;
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
      checkDeleted();
      return dirty;
   }

   public void persist() throws SQLException {
      persist(false);
   }

   public void persist(boolean recurse) throws SQLException {
      checkDeleted();

      relationManager.makePersistent(this, recurse);
   }

   public void delete() throws SQLException {
      deleted = true;
      // There must be at least one link manager loaded to access delete
      if (!artA.isLinkManagerLoaded() && !artB.isLinkManagerLoaded()) throw new IllegalStateException(
            "Invalid state where neither link manager is loaded");
      // Only one of these needs to be called in order to delete a link
      if (artA.isLinkManagerLoaded()) artA.getLinkManager().deleteLink(this);
      if (artB.isLinkManagerLoaded()) artB.getLinkManager().deleteLink(this);
      kickDeleteLinkEvent();
   }

   private void kickDeleteLinkEvent() {
      eventManager.kick(new CacheRelationModifiedEvent(this, getLinkDescriptor().getName(), getASideName(),
            ModType.Deleted.name(), this, getBranch()));
   }

   protected void setDeleted() {
      deleted = true;
   }

   protected void setNotDeleted() {
      deleted = false;
   }

   protected void checkDeleted() {
      // TODO fix this to be uncommented
      // if (deleted)
      // throw new IllegalStateException("This RelationLink has been deleted");
   }

   public Artifact getArtifact(String sideName) {
      if (sideName == null) throw new IllegalArgumentException("sideName can not be null");

      if (descriptor.getSideAName().equals(sideName)) {
         return artA;
      } else if (descriptor.getSideBName().equals(sideName)) {
         return artB;
      } else {
         throw new IllegalArgumentException(
               "sideName '" + sideName + "' does not match '" + descriptor.getSideAName() + "' or '" + descriptor.getSideBName() + "' for link type " + descriptor.getName());
      }
   }

   public Artifact getArtifactA() {
      checkDeleted();
      return artA;
   }

   public Artifact getArtifactB() {
      checkDeleted();
      return artB;
   }

   public void setArtifact(Artifact art, boolean sideA) {
      if (sideA)
         setArtifactA(art);
      else
         setArtifactB(art);
   }

   public void setArtifactA(Artifact artA) {
      setArtifactA(artA, false);
   }

   public void setArtifactA(Artifact artA, boolean remoteEvent) {
      checkDeleted();

      if (artA == null) throw new IllegalArgumentException("artA can not be null.");
      if (this.artA != null && this.artA.getArtId() != artA.getArtId() && !remoteEvent) throw new IllegalStateException(
            "Artifact A has already been set.");
      if (artA.getBranch() != descriptor.getTransactionId().getBranch()) throw new IllegalArgumentException(
            "artA must be on the same branch as the link");

      this.artA = artA;
   }

   public void setArtifactB(Artifact artB) {
      setArtifactB(artB, false);
   }

   public void setArtifactB(Artifact artB, boolean remoteEvent) {
      checkDeleted();

      if (artB == null) throw new IllegalArgumentException("artB can not be null.");
      if (this.artB != null && this.artB.getArtId() != artB.getArtId() && !remoteEvent) throw new IllegalStateException(
            "Artifact B has already been set.");
      if (artB.getBranch() != descriptor.getTransactionId().getBranch()) throw new IllegalArgumentException(
            "artB must be on the same branch as the link");

      this.artB = artB;
   }

   /**
    * @return Returns the order.
    */
   public int getAOrder() {
      checkDeleted();
      return aOrder;
   }

   /**
    * @param order The order to set.
    */
   public void setAOrder(int order) {
      try {
         checkDeleted();
         this.aOrder = order;
         dirty = true;

         if (artA.isLinkManagerLoaded()) artA.getLinkManager().fixOrderingOf(this, true);
         if (artB.isLinkManagerLoaded()) artB.getLinkManager().fixOrderingOf(this, false);
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   /**
    * @return Returns the order.
    */
   public int getBOrder() {
      checkDeleted();
      return bOrder;
   }

   /**
    * @param order The order to set.
    */
   public void setBOrder(int order) {
      try {
         checkDeleted();
         this.bOrder = order;
         dirty = true;

         if (memo != null) {
            if (artA.isLinkManagerLoaded()) artA.getLinkManager().fixOrderingOf(this, true);
            if (artB.isLinkManagerLoaded()) artB.getLinkManager().fixOrderingOf(this, false);
         }
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   public void swapAOrder(IRelationLink link) {
      swapOrder(link, true);
   }

   public void swapBOrder(IRelationLink link) {
      swapOrder(link, false);
   }

   private void swapOrder(IRelationLink link, boolean sideA) {
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
      checkDeleted();
      return rationale;
   }

   /**
    * @param rationale The rationale to set.
    */
   public void setRationale(String rationale, boolean notify) {
      checkDeleted();

      if (rationale == null) throw new IllegalArgumentException("Rationale can not be null");

      if (this.rationale.equals(rationale)) return;

      this.rationale = rationale;

      dirty = true;

      if (notify) {
         eventManager.kick(new CacheRelationModifiedEvent(this, getLinkDescriptor().getName(), getASideName(),
               ModType.RationaleMod.name(), this, getBranch()));
      }
   }

   public IRelationLinkDescriptor getLinkDescriptor() {
      return descriptor;
   }

   public LinkPersistenceMemo getPersistenceMemo() {
      checkDeleted();
      return memo;
   }

   public void setPersistenceMemo(PersistenceMemo memo) {
      checkDeleted();
      if (!(memo instanceof LinkPersistenceMemo)) throw new IllegalArgumentException(
            "memo must be of type " + LinkPersistenceMemo.class);

      this.memo = (LinkPersistenceMemo) memo;
   }

   public String getSideNameFor(Artifact artifact) {
      checkDeleted();
      return processArtifactSideName(artifact, false);
   }

   public String getSideNameForOtherArtifact(Artifact artifact) {
      return processArtifactSideName(artifact, true);
   }

   private String processArtifactSideName(Artifact artifact, boolean otherArtifact) {
      String sideName = "";

      if (artifact == artA) {

         if (otherArtifact)
            sideName = descriptor.getSideBName();
         else
            sideName = descriptor.getSideAName();
      } else if (artifact == artB) {

         if (otherArtifact)
            sideName = descriptor.getSideAName();
         else
            sideName = descriptor.getSideBName();
      } else
         throw new IllegalArgumentException("Link does not contain the artifact.");

      return sideName;
   }

   public String getSidePhrasingFor(Artifact artifact) {
      checkDeleted();
      return processArtifactSidePhrasing(artifact, false);
   }

   public String getSidePhrasingForOtherArtifact(Artifact artifact) {
      return processArtifactSidePhrasing(artifact, true);
   }

   private String processArtifactSidePhrasing(Artifact artifact, boolean otherArtifact) {
      String sideName = "";

      if (artifact == artA) {

         if (otherArtifact)
            sideName = descriptor.getBToAPhrasing();
         else
            sideName = descriptor.getAToBPhrasing();
      } else if (artifact == artB) {

         if (otherArtifact)
            sideName = descriptor.getAToBPhrasing();
         else
            sideName = descriptor.getBToAPhrasing();
      } else
         throw new IllegalArgumentException("Link does not contain the artifact.");

      return sideName;
   }

   public String getASideName() {
      checkDeleted();
      return descriptor.getSideAName();
   }

   public String getBSideName() {
      checkDeleted();
      return descriptor.getSideBName();
   }

   public String getName() {
      checkDeleted();
      return descriptor.getName();
   }

   /**
    * @return Returns the aToBPhrasing.
    */
   public String getAToBPhrasing() {
      return descriptor.getAToBPhrasing();
   }

   /**
    * @return Returns the bToAPhrasing.
    */
   public String getBToAPhrasing() {
      return descriptor.getBToAPhrasing();
   }

   public String toString() {
      return String.format("%s: %s(%s)<-->%s(%s)", descriptor.getName(), artA.getDescriptiveName(),
            Float.toString(aOrder), artB.getDescriptiveName(), Float.toString(bOrder));
   }
}
