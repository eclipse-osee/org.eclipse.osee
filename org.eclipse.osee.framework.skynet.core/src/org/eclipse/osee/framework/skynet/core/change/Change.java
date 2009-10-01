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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author Jeff C. Phillips
 */
public abstract class Change implements IAdaptable {
   private final int sourceGamma;
   private final int artId;
   private final TransactionId toTransactionId;
   private TransactionId fromTransactionId;
   private Artifact artifact;
   private ModificationType modType;
   private final ChangeType changeType;
   private Branch branch;
   private final ArtifactType artifactType;
   private final boolean isHistorical;

   /**
    * @param sourceGamma
    * @param destGamma
    * @param artId
    * @param toTransactionId
    * @param fromTransactionId
    * @param modType
    * @param changeType
    * @throws OseeTypeDoesNotExist
    * @throws OseeDataStoreException
    * @throws ArtifactDoesNotExist
    */
   public Change(Branch branch, ArtifactType artifactType, int sourceGamma, int artId, TransactionId toTransactionId, TransactionId fromTransactionId, ModificationType modType, ChangeType changeType, boolean isHistorical, Artifact artifact) throws OseeDataStoreException, OseeTypeDoesNotExist, ArtifactDoesNotExist {
      super();
      this.branch = branch;
      this.sourceGamma = sourceGamma;
      this.artId = artId;
      this.toTransactionId = toTransactionId;
      this.fromTransactionId = fromTransactionId;
      this.modType = modType;
      this.changeType = changeType;
      this.artifactType = artifactType;
      this.isHistorical = isHistorical;
      this.artifact = artifact;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Change) {
         Change change = (Change) obj;
         return change.getArtId() == artId &&
         //
         change.getGamma() == sourceGamma &&
         //
         change.getBranch() == branch &&
         //
         change.getToTransactionId() == toTransactionId &&
         //
         change.getFromTransactionId() == fromTransactionId &&
         //
         change.getModificationType() == modType &&
         //
         change.getChangeType() == changeType;
      }
      return false;
   }

   @Override
   public int hashCode() {
      if (branch != null && toTransactionId != null && fromTransactionId != null && changeType != null) {
         return artId + sourceGamma + branch.hashCode() + toTransactionId.hashCode() + fromTransactionId.hashCode() + modType.hashCode() + changeType.hashCode();
      }
      return 0;
   }

   public boolean isHistorical() {
      return isHistorical;
   }

   /**
    * @param modType the modType to set
    */
   public void setModType(ModificationType modType) {
      this.modType = modType;
   }

   /**
    * @return the modification type (New, Modified, Deleted)
    */
   public ModificationType getModificationType() {
      return modType;
   }

   /**
    * @return the changeType
    */
   public ChangeType getChangeType() {
      return changeType;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public String getArtifactName() throws IllegalArgumentException, ArtifactDoesNotExist, MultipleArtifactsExist {
      return getArtifact().getName();
   }

   /**
    * @return the sourceGamma
    */
   public int getGamma() {
      return sourceGamma;
   }

   /**
    * @return the artId
    */
   public int getArtId() {
      return artId;
   }

   /**
    * @return the toTransactionId
    */
   public TransactionId getToTransactionId() {
      return toTransactionId;
   }

   /**
    * @return the fromTransactionId
    */
   public TransactionId getFromTransactionId() {
      return fromTransactionId;
   }

   /**
    * For an artifact change this is the artifact type id. For an attribute this is the attribute type id. For a
    * relation this is the relation type id.
    * 
    * @return typeId
    */
   public abstract int getItemTypeId();

   /**
    * @return the artifactType
    */
   public ArtifactType getArtifactType() {
      return artifactType;
   }

   /**
    * @return the branch
    */
   public Branch getBranch() {
      return branch;
   }

   /**
    * @param fromTransactionId the fromTransactionId to set
    */
   public void setFromTransactionId(TransactionId fromTransactionId) {
      this.fromTransactionId = fromTransactionId;
   }

   public abstract String getIsValue();

   public abstract String getWasValue();

   public abstract String getItemTypeName() throws Exception;

   public abstract String getName() throws IllegalArgumentException, ArtifactDoesNotExist, MultipleArtifactsExist;

   public abstract String getItemKind();

   public abstract int getItemId();
}
