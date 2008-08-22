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

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.swt.graphics.Image;

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
    * @param name
    * @param descriptor
    * @param artifact
    * @param baselineTransactionId TODO
    * @param fromTransactionId TODO
    * @param toTransactionId TODO
    * @param artId
    * @param lastGoodTransactionId TODO
    */
   public ArtifactChange(ChangeType changeType, ModificationType modtype, Artifact artifact, TransactionId baseParentTransactionId, TransactionId headParentTransactionId, TransactionId baselineTransactionId, TransactionId fromTransactionId, TransactionId toTransactionId, int gammaId) {
      super(changeType, modtype, gammaId);
      this.artifact = artifact;
      this.baseParentTransactionId = baseParentTransactionId;
      this.headParentTransactionId = headParentTransactionId;
      this.baselineTransactionId = baselineTransactionId == null ? toTransactionId : baselineTransactionId;
      this.fromTransactionId = fromTransactionId;
      this.toTransactionId = toTransactionId;
   }

   /**
    * @return Returns the image for the artifact type overlayed with the modtype image.
    */
   public Image getImage() {
      return artifact.getArtifactType().getImage(getChangeType(), getModType());
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
      return artifact != null? artifact.getInternalDescriptiveName(): "Null";
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
    * @throws SQLException
    * @throws BranchDoesNotExist
    */
   public void setConflictingModArtifact(Artifact conflictingModArtifact) throws SQLException, BranchDoesNotExist {
      this.conflictingModArtifact = conflictingModArtifact;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.revision.RevisionChange#getChange()
    */
   @Override
   public String getChange() {
      return null;
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
}