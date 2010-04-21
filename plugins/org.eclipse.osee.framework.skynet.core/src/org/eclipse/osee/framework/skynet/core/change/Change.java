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
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jeff C. Phillips
 */
public abstract class Change implements IAdaptable {
   private final int sourceGamma;
   private final int artId;
   private final TransactionDelta txDelta;
   private final Artifact toArtifact;
   private final Artifact fromArtifact;
   private ModificationType modType;
   private final IOseeBranch branch;
   private final ArtifactType artifactType;
   private final boolean isHistorical;

   public Change(IOseeBranch branch, ArtifactType artifactType, int sourceGamma, int artId, TransactionDelta txDelta, ModificationType modType, boolean isHistorical, Artifact toArtifact, Artifact fromArtifact) {
      super();
      this.branch = branch;
      this.sourceGamma = sourceGamma;
      this.artId = artId;
      this.txDelta = txDelta;
      this.modType = modType;
      this.artifactType = artifactType;
      this.isHistorical = isHistorical;
      this.toArtifact = toArtifact;
      this.fromArtifact = fromArtifact;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + artId;
      result = prime * result + (artifactType == null ? 0 : artifactType.hashCode());
      result = prime * result + (branch == null ? 0 : branch.hashCode());
      result = prime * result + (fromArtifact == null ? 0 : fromArtifact.hashCode());
      result = prime * result + (isHistorical ? 1231 : 1237);
      result = prime * result + (modType == null ? 0 : modType.hashCode());
      result = prime * result + sourceGamma;
      result = prime * result + (toArtifact == null ? 0 : toArtifact.hashCode());
      result = prime * result + (txDelta == null ? 0 : txDelta.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      Change other = (Change) obj;
      if (artId != other.artId) {
         return false;
      }
      if (artifactType == null) {
         if (other.artifactType != null) {
            return false;
         }
      } else if (!artifactType.equals(other.artifactType)) {
         return false;
      }
      if (branch == null) {
         if (other.branch != null) {
            return false;
         }
      } else if (!branch.equals(other.branch)) {
         return false;
      }
      if (fromArtifact == null) {
         if (other.fromArtifact != null) {
            return false;
         }
      } else if (!fromArtifact.equals(other.fromArtifact)) {
         return false;
      }
      if (isHistorical != other.isHistorical) {
         return false;
      }
      if (modType == null) {
         if (other.modType != null) {
            return false;
         }
      } else if (!modType.equals(other.modType)) {
         return false;
      }
      if (sourceGamma != other.sourceGamma) {
         return false;
      }
      if (toArtifact == null) {
         if (other.toArtifact != null) {
            return false;
         }
      } else if (!toArtifact.equals(other.toArtifact)) {
         return false;
      }
      if (txDelta == null) {
         if (other.txDelta != null) {
            return false;
         }
      } else if (!txDelta.equals(other.txDelta)) {
         return false;
      }
      return true;
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

   public Artifact getToArtifact() {
      return toArtifact;
   }

   public Artifact getFromArtifact() {
      return fromArtifact;
   }

   public String getArtifactName() {
      return getToArtifact().getName();
   }

   public int getGamma() {
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
