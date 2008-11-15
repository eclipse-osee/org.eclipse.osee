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

import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
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
   public RelationChanged(Branch branch, int aArtTypeId, int sourceGamma, int aArtId, TransactionId toTransactionId, TransactionId fromTransactionId, ModificationType modType, ChangeType changeType, int bArtId, int relLinkId, String rationale, int aLinkOrder, int bLinkOrder, RelationType relationType, boolean isHistorical) {
      super(branch, aArtTypeId, sourceGamma, aArtId, toTransactionId, fromTransactionId, modType, changeType,
            isHistorical);
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
      if (adapter == null) throw new IllegalArgumentException("adapter can not be null");

      try {
         // this is a temporary fix until the old change report goes away.
         if (adapter.isInstance(getArtifact())) {
            return getArtifact();
         }
      } catch (IllegalArgumentException ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      } catch (ArtifactDoesNotExist ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }
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
    * @throws ArtifactDoesNotExist
    */
   public Artifact getBArtifact() throws ArtifactDoesNotExist {
      if (bArtifact == null) {
         if (isHistorical()) {
            bArtifact = ArtifactCache.getHistorical(bArtId, getToTransactionId().getTransactionNumber());
         } else {
            bArtifact = ArtifactCache.getActive(bArtId, getBranch());
         }
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
   public String getName() throws IllegalArgumentException, ArtifactDoesNotExist, MultipleArtifactsExist {
      return getArtifactName() + " <-> " + getBArtifact().getInternalDescriptiveName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getTypeName()
    */
   @Override
   public String getItemTypeName() {
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
   public Image getItemKindImage() throws OseeCoreException {
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
