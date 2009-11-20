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
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jeff C. Phillips
 */
public abstract class Change implements IAdaptable {
   private final int sourceGamma;
   private final int artId;
   private final TransactionRecord toTransactionId;
   private TransactionRecord fromTransactionId;
   private final Artifact artifact;
   private ModificationType modType;
   private final ChangeType changeType;
   private final Branch branch;
   private final ArtifactType artifactType;
   private final boolean isHistorical;

   public Change(Branch branch, ArtifactType artifactType, int sourceGamma, int artId, TransactionRecord toTransactionId, TransactionRecord fromTransactionId, ModificationType modType, ChangeType changeType, boolean isHistorical, Artifact artifact) throws OseeDataStoreException, OseeTypeDoesNotExist, ArtifactDoesNotExist {
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

   public void setModType(ModificationType modType) {
      this.modType = modType;
   }

   /**
    * @return the modification type (New, Modified, Deleted)
    */
   public ModificationType getModificationType() {
      return modType;
   }

   public ChangeType getChangeType() {
      return changeType;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public String getArtifactName() throws IllegalArgumentException, ArtifactDoesNotExist, MultipleArtifactsExist {
      return getArtifact().getName();
   }

   public int getGamma() {
      return sourceGamma;
   }

   public int getArtId() {
      return artId;
   }

   public TransactionRecord getToTransactionId() {
      return toTransactionId;
   }

   public TransactionRecord getFromTransactionId() {
      return fromTransactionId;
   }

   /**
    * For an artifact change this is the artifact type id. For an attribute this is the attribute type id. For a
    * relation this is the relation type id.
    * 
    * @return typeId
    */
   public abstract int getItemTypeId();

   public ArtifactType getArtifactType() {
      return artifactType;
   }

   public Branch getBranch() {
      return branch;
   }

   public void setFromTransactionId(TransactionRecord fromTransactionId) {
      this.fromTransactionId = fromTransactionId;
   }

   public abstract String getIsValue();

   public abstract String getWasValue();

   public abstract String getItemTypeName() throws Exception;

   public abstract String getName() throws IllegalArgumentException, ArtifactDoesNotExist, MultipleArtifactsExist;

   public abstract String getItemKind();

   public abstract int getItemId();
}
