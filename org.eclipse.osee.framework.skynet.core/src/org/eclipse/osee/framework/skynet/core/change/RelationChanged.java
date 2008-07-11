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

package org.eclipse.osee.framework.skynet.core.change;

import java.sql.SQLException;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class RelationChanged extends Change {

   private int bArtId;
   private String bArtName;
   private Artifact bArtifact;
   private int relLinkId;
   private String rationale;
   private int aLinkOrder;
   private int bLinkOrder;
   private RelationType relationType;

   /**
    * @param aArtTypeId
    * @param sourceGamma
    * @param aArtId
    * @param toTransactionId
    * @param fromTransactionId
    * @param modType
    * @param changeType
    * @param bArtId
    * @param bArtifact
    * @param relLinkId
    * @param rationale
    * @param aLinkOrder
    * @param relationType
    */
   public RelationChanged(Branch branch, int aArtTypeId, int sourceGamma, int aArtId, TransactionId toTransactionId, TransactionId fromTransactionId, ModificationType modType, ChangeType changeType, int bArtId, int relLinkId, String rationale, int aLinkOrder, int bLinkOrder, RelationType relationType) {
      super(branch, aArtTypeId, sourceGamma, aArtId, toTransactionId, fromTransactionId, modType, changeType);
      this.bArtId = bArtId;
      this.relLinkId = relLinkId;
      this.rationale = rationale;
      this.aLinkOrder = aLinkOrder;
      this.bLinkOrder = bLinkOrder;
      this.relationType = relationType;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getImage()
    */
   @Override
   public Image getItemTypeImage() {
      return RelationChangeIcons.getImage(getChangeType(), getModificationType());
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      return null;
   }

   /**
    * @return the bArtId
    */
   public int getBArtId() {
      return bArtId;
   }

   /**
    * @return the bArtifact
    * @throws SQLException
    * @throws ArtifactDoesNotExist
    */
   public Artifact getBArtifact() throws ArtifactDoesNotExist {
      if (bArtifact == null) {
         bArtifact = ArtifactCache.getActive(bArtId, getBranch());
      }

      if (bArtifact == null) {
         throw new ArtifactDoesNotExist(
               "Artifact: " + bArtId + " Does not exist on branch: " + getBranch().getBranchName() + " branch id: " + getBranch().getBranchId());
      }

      return bArtifact;
   }

   /**
    * @return the relLinkId
    */
   public int getRelLinkId() {
      return relLinkId;
   }

   /**
    * @return the rationale
    */
   public String getRationale() {
      return rationale;
   }

   /**
    * @return the linkOrder
    */
   public int getLinkOrder() {
      return aLinkOrder;
   }

   /**
    * @return the relationType
    */
   public RelationType getRelationType() {
      return relationType;
   }

   /**
    * @return the bArtName
    */
   public String getBArtName() {
      return bArtName;
   }

   /**
    * @param artName the bArtName to set
    */
   public void setBArtName(String artName) {
      bArtName = artName;
   }

   /**
    * @return the bLinkOrder
    */
   public int getBLinkOrder() {
      return bLinkOrder;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getName()
    */
   @Override
   public String getName() throws IllegalArgumentException, ArtifactDoesNotExist, MultipleArtifactsExist, SQLException {
      return getArtifactName() + " <-> " + getBArtifact().getInternalDescriptiveName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getTypeName()
    */
   @Override
   public String getItemTypeName() throws SQLException {
      return relationType.getTypeName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getValue()
    */
   @Override
   public String getIsValue() {
      return getRationale();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getArtifactImage()
    */
   @Override
   public Image getItemKindImage() throws IllegalArgumentException, SQLException {
      return getItemTypeImage();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getItemKind()
    */
   @Override
   public String getItemKind() {
      return "Relation";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getWasValue()
    */
   @Override
   public String getWasValue() {
      return null;
   }

}
