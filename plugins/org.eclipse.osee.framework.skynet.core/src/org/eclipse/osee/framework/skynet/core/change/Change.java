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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionDelta;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jeff C. Phillips
 */
public abstract class Change implements IAdaptable {
   private final long sourceGamma;
   private final int artId;
   private final TransactionDelta txDelta;
   private final ArtifactDelta artifactDelta;
   private ModificationType modType;
   private final IOseeBranch branch;
   private final ArtifactType artifactType;
   private final boolean isHistorical;

   public Change(IOseeBranch branch, ArtifactType artifactType, long sourceGamma, int artId, TransactionDelta txDelta, ModificationType modType, boolean isHistorical, ArtifactDelta artifactDelta) {
      super();
      this.branch = branch;
      this.sourceGamma = sourceGamma;
      this.artId = artId;
      this.txDelta = txDelta;
      this.modType = modType;
      this.artifactType = artifactType;
      this.isHistorical = isHistorical;
      this.artifactDelta = artifactDelta;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Change) {
         Change change = (Change) obj;
         return change.getArtId() == getArtId() &&
         //
         change.getGamma() == getGamma() &&
         //
         change.getBranch() == getBranch() &&
         //
         change.getDelta().equals(getDelta()) &&
         //
         change.getModificationType() == getModificationType();
      }
      return false;
   }

   @Override
   public int hashCode() {
      int hashCode = 0;
      hashCode += 13 * getArtId();
      hashCode += 13 * getGamma();
      hashCode += getBranch() != null ? 13 * getBranch().hashCode() : 0;
      hashCode += getDelta() != null ? 13 * getDelta().hashCode() : 0;
      hashCode += getModificationType() != null ? 13 * getModificationType().hashCode() : 0;
      return hashCode;
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

   public ArtifactDelta getDelta() {
      return artifactDelta;
   }

   protected Artifact getSourceArtifact() {
      return artifactDelta.getStartArtifact();
   }

   public String getArtifactName() {
      return getSourceArtifact().getName();
   }

   public long getGamma() {
      return sourceGamma;
   }

   public int getArtId() {
      return artId;
   }

   public TransactionDelta getTxDelta() {
      return txDelta;
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

   public IOseeBranch getBranch() {
      return branch;
   }

   public abstract String getIsValue();

   public abstract String getWasValue();

   public abstract String getItemTypeName() throws OseeCoreException;

   public abstract String getName();

   public abstract String getItemKind();

   public abstract int getItemId();
}
