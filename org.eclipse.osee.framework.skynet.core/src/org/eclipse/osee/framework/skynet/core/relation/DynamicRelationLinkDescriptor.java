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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.PersistenceMemo;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author Robert A. Fisher
 */
public class DynamicRelationLinkDescriptor implements IRelationLinkDescriptor {

   private String name;
   private String sideAName;
   private String sideBName;
   private String aToBPhrasing;
   private String bToAPhrasing;
   private String shortName;
   private LinkDescriptorPersistenceMemo memo;
   private Map<Integer, LinkSideRestriction> restrictions;
   private final TransactionId transactionId;

   public DynamicRelationLinkDescriptor(String name, String sideAName, String sideBName, String aToBPhrasing, String bToAPhrasing, String shortName, TransactionId transactionId) {
      super();

      this.name = name;
      this.sideAName = sideAName;
      this.sideBName = sideBName;
      this.aToBPhrasing = aToBPhrasing;
      this.bToAPhrasing = bToAPhrasing;
      this.shortName = shortName;
      this.restrictions = new HashMap<Integer, LinkSideRestriction>();
      this.transactionId = transactionId;
   }

   public DynamicRelationLink makeNewLink() {
      return new DynamicRelationLink(this);
   }

   /**
    * @return Returns the aToBPhrasing.
    */
   public String getAToBPhrasing() {
      return aToBPhrasing;
   }

   /**
    * @return Returns the bToAPhrasing.
    */
   public String getBToAPhrasing() {
      return bToAPhrasing;
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }

   public String getSideName(boolean sideA) {
      return sideA ? sideAName : sideBName;
   }

   /**
    * @return Returns the sideAName.
    */
   public String getSideAName() {
      return sideAName;
   }

   /**
    * @return Returns the sideBName.
    */
   public String getSideBName() {
      return sideBName;
   }

   /**
    * @return Returns the shortName.
    */
   public String getShortName() {
      return shortName;
   }

   /**
    * @return Returns the transactionId.
    */
   public TransactionId getTransactionId() {
      return transactionId;
   }

   public boolean isSideAName(String sideName) {

      if (!sideAName.equals(sideName) && !sideBName.equals(sideName)) throw new IllegalArgumentException(
            "sideName does not match either of the available side names");

      return sideAName.equals(sideName);
   }

   public LinkDescriptorPersistenceMemo getPersistenceMemo() {
      return memo;
   }

   public void setPersistenceMemo(PersistenceMemo memo) {
      this.memo = (LinkDescriptorPersistenceMemo) memo;
   }

   /**
    * @param linkSideRestriction
    */
   public void setLinkSideRestriction(int id, LinkSideRestriction linkSideRestriction) {
      restrictions.put(id, linkSideRestriction);
   }

   public boolean canLinkType(int id) {
      return restrictions.containsKey(id);
   }

   public int getRestrictionSizeFor(int id, boolean sideA) {
      LinkSideRestriction restriction = restrictions.get(id);
      if (restriction == null) return 0;
      return sideA ? restriction.getSideALinkMax() : restriction.getSideBLinkMax();
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Comparable#compareTo(T)
    */
   public int compareTo(IRelationLinkDescriptor descriptor) {
      return name.compareTo(descriptor.getName());
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof DynamicRelationLinkDescriptor) return name.equals(((DynamicRelationLinkDescriptor) obj).name);
      return false;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return name.hashCode();
   }

   /**
    * ensure that the given artifact can be added to the specified side of a new link of this type
    */
   public void ensureSideWillSupportArtifact(boolean sideA, Artifact artifact, int artifactCount) {
      try {
         int maxCount = getRestrictionSizeFor(artifact.getArtTypeId(), sideA);
         RelationLinkGroup group = artifact.getLinkManager().getSideGroup(this, !sideA);

         // if the artifact does not belong on that side at all
         if (maxCount == 0) {
            throw new IllegalArgumentException(String.format(
                  "Artifact \"%s\" of type \"%s\" does not belong on side \"%s\" of relation \"%s\"",
                  artifact.getDescriptiveName(), artifact.getArtifactTypeName(), getSideName(sideA), name));
         } else if (group == null) {
            // obvoiusly the current link count is zero, so this side will support a new link
         }
         // the artifact is allowed and a group exists, so check if there is space for another link.
         else if (group.getLinkCount() + 1 > maxCount) {
            throw new IllegalArgumentException(
                  String.format(
                        "Artifact \"%s\" of type \"%s\" can not be added to side \"%s\" of relation \"%s\" because doing so would exceed the side maximum of %d for this artifact type",
                        artifact.getDescriptiveName(), artifact.getArtifactTypeName(), getSideName(sideA), name,
                        maxCount));
         }
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   public String toString() {
      return name + " " + sideAName + " <--> " + sideBName;
   }
}
