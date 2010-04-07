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

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Jeff C. Phillips
 */
public final class RelationChange extends Change {
   private final int bArtId;
   private String bArtName;
   private final Artifact bArtifact;
   private final int relLinkId;
   private final String rationale;
   private int aLinkOrder;
   private int bLinkOrder;
   private final RelationType relationType;

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
    * @throws ArtifactDoesNotExist
    */
   public RelationChange(IOseeBranch branch, ArtifactType aArtType, int sourceGamma, int aArtId, TransactionRecord toTransactionId, TransactionRecord fromTransactionId, ModificationType modType, int bArtId, int relLinkId, String rationale, RelationType relationType, boolean isHistorical, Artifact toArtifact, Artifact bArtifact, Artifact fromArtifact) throws OseeDataStoreException, OseeTypeDoesNotExist, ArtifactDoesNotExist {
      super(branch, aArtType, sourceGamma, aArtId, toTransactionId, fromTransactionId, modType, isHistorical, toArtifact, fromArtifact);
      this.bArtId = bArtId;
      this.relLinkId = relLinkId;
      this.rationale = rationale;
      this.relationType = relationType;
      this.bArtifact = bArtifact;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      if (adapter == null) {
         throw new IllegalArgumentException("adapter can not be null");
      }

      try {
         if (adapter.isInstance(getToArtifact())) {
            return getToArtifact();
         } else if (adapter.isInstance(getToTransactionId()) && isHistorical()) {
            return getToTransactionId();
         } else if (adapter.isInstance(this)) {
            return this;
         }
      } catch (IllegalArgumentException ex) {
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

   public Artifact getBArtifact() throws ArtifactDoesNotExist {
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
      return relationType.getId();
   }

   @Override
   public int getItemId() {
      return relLinkId;
   }
}
