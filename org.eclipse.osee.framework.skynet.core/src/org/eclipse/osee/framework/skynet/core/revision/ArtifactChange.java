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
package org.eclipse.osee.framework.skynet.core.revision;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * A modified artifact description. Used by the Change Report View, this supplies a way to describe an Artifact whether
 * or not it has been deleted, and supplies appropriate images according to the modification type.
 * 
 * @author Robert A. Fisher
 */
public class ArtifactChange extends RevisionChange {

   private static final long serialVersionUID = 1L;
   private TransactionId baseParentTransactionId;
   private TransactionId headParentTransactionId;
   private TransactionId baselineTransactionId;
   private TransactionId fromTransactionId;
   private TransactionId toTransactionId;
   transient private Artifact artifact;
   transient private Artifact conflictingModArtifact;
   private boolean isHistorical;

   @Override
   public String toString() {
      return " Type: " + getChangeType() + " Gamma: " + getGammaId() + " - " + getName();
   }

   /**
    * Constructor for serialization.
    */
   protected ArtifactChange() {
      this.artifact = null;
      this.conflictingModArtifact = null;
   }

   /**
    * @param artifact
    * @param baselineTransactionId TODO
    * @param fromTransactionId TODO
    * @param toTransactionId TODO
    * @param isHistorical TODO
    * @param name
    * @param descriptor
    * @param artId
    * @param lastGoodTransactionId TODO
    */
   public ArtifactChange(ChangeType changeType, ModificationType modtype, Artifact artifact, TransactionId baseParentTransactionId, TransactionId headParentTransactionId, TransactionId baselineTransactionId, TransactionId fromTransactionId, TransactionId toTransactionId, int gammaId, boolean isHistorical) {
      super(changeType, modtype, gammaId);
      this.artifact = artifact;
      this.baseParentTransactionId = baseParentTransactionId;
      this.headParentTransactionId = headParentTransactionId;
      this.baselineTransactionId = baselineTransactionId == null ? toTransactionId : baselineTransactionId;
      this.fromTransactionId = fromTransactionId;
      this.toTransactionId = toTransactionId;
      this.isHistorical = isHistorical;
   }

   /**
    * @return the isHistorical
    */
   public boolean isHistorical() {
      return isHistorical;
   }

   /**
    * @return Returns the artifact.
    */
   public Artifact getArtifact() {
      return artifact;
   }

   /**
    * @return Returns the baselineTransactionId. If the modtype is deleted, this will always be null.
    */
   public TransactionId getBaselineTransactionId() {
      return baselineTransactionId;
   }

   /**
    * @return Returns the toTransactionId. If the modtype is deleted, this will always be null.
    */
   public TransactionId getToTransactionId() {
      return toTransactionId;
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      return artifact != null ? artifact.getInternalDescriptiveName() : "Null";
   }

   /**
    * @return true if conflictingModArtifact is not null else false.
    */
   public boolean hasConflictingModArtifact() {
      return getConflictingModArtifact() != null;
   }

   /**
    * @return Returns the conflictingModArtifact.
    */
   public Artifact getConflictingModArtifact() {
      return conflictingModArtifact;
   }

   /**
    * @param conflictingModArtifact The conflictingModArtifact to set.
    */
   public void setConflictingModArtifact(Artifact conflictingModArtifact) {
      this.conflictingModArtifact = conflictingModArtifact;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.revision.RevisionChange#getChange()
    */
   @Override
   public String getChange() {
      return "Artifact - " + getModificationType().getDisplayName();
   }

   /**
    * @return Returns the baseParentTransactionId.
    */
   public TransactionId getBaseParentTransactionId() {
      return baseParentTransactionId;
   }

   /**
    * @return Returns the headParentTransactionId.
    */
   public TransactionId getHeadParentTransactionId() {
      return headParentTransactionId;
   }

   /**
    * @return the fromTransactionId
    */
   public TransactionId getFromTransactionId() {
      return fromTransactionId;
   }

   public Object getAdapter(Class adapter) {
      if (adapter == null) throw new IllegalArgumentException("adapter can not be null");

      if (adapter.isInstance(artifact)) {
         return artifact;
      } else if (adapter.isInstance(this)) {
         return this;
      }
      return null;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof ArtifactChange) {
         ArtifactChange otherArtifactChnage = (ArtifactChange) obj;
         return getArtifact().equals(otherArtifactChnage.getArtifact());
      } else {
         return false;
      }
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return getArtifact().hashCode();
   }
}