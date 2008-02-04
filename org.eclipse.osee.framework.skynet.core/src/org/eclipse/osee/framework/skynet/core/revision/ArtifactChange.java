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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.DELETE;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;
import org.eclipse.swt.graphics.Image;

/**
 * A modified artifact description. Used by the Change Report View, this supplies a way to describe an Artifact whether
 * or not it has been deleted, and supplies appropriate images according to the modification type.
 * 
 * @author Robert A. Fisher
 */
public class ArtifactChange extends RevisionChange {
   private static final long serialVersionUID = 1L;
   static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactChange.class);
   private static final ArtifactPersistenceManager artifactPersistenceManager =
         ArtifactPersistenceManager.getInstance();
   private String name;
   private TransactionId baseParentTransactionId;
   private TransactionId headParentTransactionId;
   private TransactionId baselineTransactionId;
   private TransactionId fromTransactionId;
   private TransactionId toTransactionId;
   private TransactionId lastGoodTransactionId; // Only for deleted artifacts
   private ArtifactSubtypeDescriptor descriptor;

   transient private Artifact artifact;
   private int artId;

   transient private Artifact conflictingModArtifact;
   private int conflictingArtId;
   private TransactionId conflictingArtTransactionId;
   private TransactionId deletedTransactionId;

   @Override
   public String toString() {
      return "ArtId: " + getArtId() + " Type: " + getChangeType() + " Gamma: " + getGammaId() + " - " + getName();
   }

   /**
    * Constructor for serialization.
    */
   protected ArtifactChange() {
      this.artifact = null;
      this.conflictingModArtifact = null;
   }

   /**
    * When using this constructor only the gammaId, artId and modification values will not be null for this object.
    * 
    * @param artId
    * @param modificationId
    * @param gammaId
    */
   public ArtifactChange(ChangeType changeType, int artId, int modificationId, int gammaId, TransactionId toTransactionId, TransactionId fromTransactionId, ArtifactSubtypeDescriptor descriptor) {
      this(changeType, ModificationType.getMod(modificationId), null, descriptor, null, null, null, null,
            toTransactionId, fromTransactionId, artId, gammaId, null);
   }

   /**
    * Create an <code>ArtifactChange</code> object for a deleted artifact.
    * 
    * @param name The last name for the Artifact before it was deleted.
    * @param descriptor The descriptor for the Artifact.
    */
   public ArtifactChange(ChangeType changeType, String name, ArtifactSubtypeDescriptor descriptor, int artId, int gammaId, TransactionId baseParentTransactionId, TransactionId headParentTransactionId, TransactionId lastGoodTransactionId, TransactionId deletedTransactionId) {
      this(changeType, DELETE, name, descriptor, null, baseParentTransactionId, headParentTransactionId,
            lastGoodTransactionId, lastGoodTransactionId, lastGoodTransactionId, artId, gammaId, lastGoodTransactionId);

      this.deletedTransactionId = deletedTransactionId;
   }

   /**
    * Create an <code>ArtifactChange</code> object for a new or modified artifact.
    * 
    * @param modtype The type of artifact modification to create.
    * @param artifact The artifact this artifact change describes.
    * @param fromTransactionId TODO
    */
   public ArtifactChange(ChangeType changeType, ModificationType modtype, Artifact artifact, TransactionId baseParentTransactionId, TransactionId headParentTransactionId, TransactionId baselineTransactionId, TransactionId fromTransactionId, TransactionId toTransactionId, int gammaId) {
      this(changeType, modtype, artifact.getDescriptiveName(), artifact.getDescriptor(), artifact,
            baseParentTransactionId, headParentTransactionId, baselineTransactionId, fromTransactionId,
            toTransactionId, artifact.getArtId(), gammaId, null);
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
   private ArtifactChange(ChangeType changeType, ModificationType modtype, String name, ArtifactSubtypeDescriptor descriptor, Artifact artifact, TransactionId baseParentTransactionId, TransactionId headParentTransactionId, TransactionId baselineTransactionId, TransactionId fromTransactionId, TransactionId toTransactionId, int artId, int gammaId, TransactionId lastGoodTransactionId) {
      super(changeType, modtype, gammaId);

      this.name = name;
      this.descriptor = descriptor;
      this.artifact = artifact;
      this.baseParentTransactionId = baseParentTransactionId;
      this.headParentTransactionId = headParentTransactionId;
      this.baselineTransactionId = baselineTransactionId == null ? toTransactionId : baselineTransactionId;
      this.fromTransactionId = fromTransactionId;
      this.toTransactionId = toTransactionId;
      this.artId = artId;
      this.lastGoodTransactionId = lastGoodTransactionId;
   }

   /**
    * @return Returns the image for the artifact type overlayed with the modtype image.
    */
   public Image getImage() {
      return descriptor.getImage(getChangeType(), getModType());
   }

   /**
    * @return Returns the artifact. If the modtype is deleted, this will be the version just prior to deletion.
    * @throws SQLException
    */
   public Artifact getArtifact() throws SQLException {
      TransactionId transactionId = (getModType() == ModificationType.DELETE ? lastGoodTransactionId : toTransactionId);

      if (artifact == null && transactionId != null) {
         artifact = loadArtifact(transactionId, artifact, artId);
      }
      return artifact;
   }

   private Artifact loadArtifact(TransactionId transactionId, Artifact artifact, int artId) throws SQLException {
      // Let the transactionId be editable if possible
      transactionId =
            TransactionIdManager.getInstance().getPossiblyEditableTransactionId(transactionId.getTransactionNumber());

      // the memo is checked on the cached artifact in case our original artifact was editable,
      // modified, and no longer on the transaction
      try {
         if (artifact == null || artifact.getPersistenceMemo().getTransactionNumber() != transactionId.getTransactionNumber() || (artifact.getPersistenceMemo().getTransactionNumber() == transactionId.getTransactionNumber() && artifact.getPersistenceMemo().getTransactionId().isEditable() != transactionId.isEditable())) {
            artifact = artifactPersistenceManager.getArtifactFromId(artId, transactionId);
         }
      } catch (IllegalArgumentException ex) {
         throw new IllegalStateException(
               "Unexpected exception for transaction id " + transactionId + " and mod type " + getModType(), ex);
      }
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
      return name;
   }

   /**
    * @return Returns the artId.
    */
   public int getArtId() {
      return artId;
   }

   /**
    * @return true if conflictingModArtifact is not null else false.
    */
   public boolean hasConflictingModArtifact() {
      boolean hasConflictedArtifact = false;
      try {
         hasConflictedArtifact = getConflictingModArtifact() != null;
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
      return hasConflictedArtifact;
   }

   /**
    * @return Returns the conflictingModArtifact.
    */
   public Artifact getConflictingModArtifact() throws SQLException {
      if (conflictingModArtifact == null && conflictingArtTransactionId != null) {
         conflictingModArtifact = loadArtifact(conflictingArtTransactionId, conflictingModArtifact, conflictingArtId);
      }
      return conflictingModArtifact;
   }

   /**
    * @param conflictingModArtifact The conflictingModArtifact to set.
    */
   public void setConflictingModArtifact(Artifact conflictingModArtifact) {
      this.conflictingModArtifact = conflictingModArtifact;
      this.conflictingArtId = conflictingModArtifact.getArtId();
      this.conflictingArtTransactionId = conflictingModArtifact.getPersistenceMemo().getTransactionId();
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

   /**
    * @return the deletedTransactionId
    */
   public TransactionId getDeletedTransactionId() {
      return deletedTransactionId;
   }
}
