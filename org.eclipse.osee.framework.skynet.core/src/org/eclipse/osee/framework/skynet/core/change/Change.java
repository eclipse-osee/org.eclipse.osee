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
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public abstract class Change implements IAdaptable {

   private int sourceGamma;
   private int artId;
   private TransactionId toTransactionId;
   private TransactionId fromTransactionId;
   private Artifact artifact;
   private ModificationType modType;
   private ChangeType changeType;
   private Branch branch;
   protected int artTypeId;

   /**
    * @param sourceGamma
    * @param destGamma
    * @param artId
    * @param toTransactionId
    * @param fromTransactionId
    * @param modType
    * @param changeType
    */
   public Change(Branch branch, int artTypeId, int sourceGamma, int artId, TransactionId toTransactionId, TransactionId fromTransactionId, ModificationType modType, ChangeType changeType) {
      super();
      this.branch = branch;
      this.sourceGamma = sourceGamma;
      this.artId = artId;
      this.toTransactionId = toTransactionId;
      this.fromTransactionId = fromTransactionId;
      this.modType = modType;
      this.changeType = changeType;
      this.artTypeId = artTypeId;
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

   /**
    * @return the artifact
    * @throws ArtifactDoesNotExist
    * @throws SQLException
    * @throws IllegalArgumentException
    * @throws MultipleArtifactsExist
    * @throws ArtifactDoesNotExist
    */
   public Artifact getArtifact() throws ArtifactDoesNotExist {
      if (artifact == null) {
         artifact = ArtifactCache.getActive(artId, branch);
      }

      if (artifact == null) {
         throw new ArtifactDoesNotExist(
               "Artifact: " + artId + " Does not exist on branch: " + branch.getBranchName() + " branch id: " + branch.getBranchId());
      }
      return artifact;
   }

   public String getArtifactName() throws IllegalArgumentException, ArtifactDoesNotExist, MultipleArtifactsExist, SQLException {
      return getArtifact().getInternalDescriptiveName();
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
    * @return the artTypeId
    */
   public int getArtTypeId() {
      return artTypeId;
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

   public abstract Image getItemKindImage() throws IllegalArgumentException, SQLException;

   public abstract Image getItemTypeImage();

   public abstract String getIsValue();

   public abstract String getWasValue();

   public abstract String getItemTypeName() throws Exception;

   public abstract String getName() throws IllegalArgumentException, ArtifactDoesNotExist, MultipleArtifactsExist, SQLException;

   public abstract String getItemKind();

   /**
    * @param branch the branch to set
    */
   public void setBranch(Branch branch) {
      this.branch = branch;
   }
}
