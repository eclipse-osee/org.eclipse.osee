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
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

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
    * @throws OseeTypeDoesNotExist
    * @throws OseeDataStoreException
    */
   public RelationChanged(Branch branch, ArtifactType aArtType, int sourceGamma, int aArtId, TransactionId toTransactionId, TransactionId fromTransactionId, ModificationType modType, ChangeType changeType, int bArtId, int relLinkId, String rationale, int aLinkOrder, int bLinkOrder, RelationType relationType, boolean isHistorical) throws OseeDataStoreException, OseeTypeDoesNotExist {
      super(branch, aArtType, sourceGamma, aArtId, toTransactionId, fromTransactionId, modType, changeType,
            isHistorical);
      this.bArtId = bArtId;
      this.relLinkId = relLinkId;
      this.rationale = rationale;
      this.aLinkOrder = aLinkOrder;
      this.bLinkOrder = bLinkOrder;
      this.relationType = relationType;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      if (adapter == null) throw new IllegalArgumentException("adapter can not be null");

      try {
         if (adapter.isInstance(getArtifact())) {
            return getArtifact();
         }
         else if (adapter.isInstance(getToTransactionId()) && isHistorical()) {
            return getToTransactionId();
         }
         else if (adapter.isInstance(this)) {
            return this;
         }
      } catch (IllegalArgumentException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      } catch (ArtifactDoesNotExist ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
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
               "Artifact: " + bArtId + " Does not exist on branch: " + getBranch().getName() + " branch id: " + getBranch().getBranchId());
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

   @Override
   public String getName() throws IllegalArgumentException, ArtifactDoesNotExist, MultipleArtifactsExist {
      return getArtifactName() + " <-> " + getBArtifact().getName();
   }

   @Override
   public String getItemTypeName() {
      return relationType.getName();
   }

   @Override
   public String getIsValue() {
      return getRationale();
   }

   @Override
   public String getItemKind() {
      return "Relation";
   }

   @Override
   public String getWasValue() {
      return null;
   }

   @Override
   public int getItemTypeId() {
      return relationType.getTypeId();
   }

   @Override
   public int getItemId() {
      return relLinkId;
   }
}
