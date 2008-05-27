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

package org.eclipse.osee.framework.skynet.core.conflict;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.util.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleArtifactsExist;
import org.eclipse.swt.graphics.Image;

/**
 * @author Theron Virgin
 */
public class ArtifactConflict extends Conflict {
   private static final BranchPersistenceManager branchPersistenceManager = BranchPersistenceManager.getInstance();
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactConflict.class);
   private static final String CHANGE_ITEM = "Artifact";
   private static final String ARTIFACT_DELETED = "DELETED";
   private static final Image ARTIFACT_IMAGE = SkynetActivator.getInstance().getImage("artifact.gif");
   private int sourceTxType;
   private int destTxType;
   private int artTypeId;

   /**
    * @param sourceGamma
    * @param destGamma
    * @param artId
    * @param toTransactionId
    * @param fromTransactionId
    * @param transactionType
    * @param changeType
    * @param mergeBranch
    * @param sourceBranch
    * @param destBranch
    */
   public ArtifactConflict(int sourceGamma, int destGamma, int artId, TransactionId toTransactionId, TransactionId fromTransactionId, ModificationType modType, ChangeType changeType, Branch mergeBranch, Branch sourceBranch, Branch destBranch, int sourceTxType, int destTxType, int artTypeId) {
      super(sourceGamma, destGamma, artId, toTransactionId, fromTransactionId, modType, changeType, mergeBranch,
            sourceBranch, destBranch);
      this.sourceTxType = sourceTxType;
      this.destTxType = destTxType;
      this.artTypeId = artTypeId;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (adapter == null) throw new IllegalArgumentException("adapter can not be null");

      if (adapter.isInstance(this)) {
         return this;
      }

      try {
         Artifact artifact;
         Branch defaultBranch = branchPersistenceManager.getDefaultBranch();
         if (defaultBranch.equals(sourceBranch)) {
            artifact = getSourceArtifact();
            if (adapter.isInstance(artifact)) {
               return artifact;
            }
         }
         if (defaultBranch.equals(destBranch)) {
            artifact = getDestArtifact();
            if (adapter.isInstance(artifact)) {
               return artifact;
            }
         }
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
         return null;
      }

      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#clearValue()
    */
   @Override
   public boolean clearValue() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#computeStatus()
    */
   @Override
   public Status computeStatus() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, Exception {
      return super.computeStatus(getArtifact().getArtId(), Status.NOT_RESOLVABLE);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getChangeItem()
    */
   @Override
   public String getChangeItem() throws SQLException {
      return CHANGE_ITEM;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getConflictType()
    */
   @Override
   public ConflictType getConflictType() {
      return ConflictType.ARTIFACT;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getDestDisplayData()
    */
   @Override
   public String getDestDisplayData() throws SQLException {
      return ARTIFACT_DELETED;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getImage()
    */
   @Override
   public Image getImage() throws SQLException {
      return ARTIFACT_IMAGE;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getMergeDisplayData()
    */
   @Override
   public String getMergeDisplayData() throws SQLException {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getMergeValue()
    */
   protected Object getMergeValue() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException {
      return getArtifact();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getSourceDisplayData()
    */
   @Override
   public String getSourceDisplayData() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException {
      return getSourceArtifact().getDescriptiveName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#mergeEqualsDestination()
    */
   @Override
   public boolean mergeEqualsDestination() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException {
      return getDestArtifact().equals(getMergeValue());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#mergeEqualsSource()
    */
   @Override
   public boolean mergeEqualsSource() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException {
      return getSourceArtifact().equals(getMergeValue());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#setToDest()
    */
   @Override
   public boolean setToDest() throws SQLException {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#setToSource()
    */
   @Override
   public boolean setToSource() throws SQLException {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#sourceEqualsDestination()
    */
   @Override
   public boolean sourceEqualsDestination() throws SQLException {
      return false;
   }

   public void revertSourceArtifact() throws Exception {
      ArtifactPersistenceManager.getInstance().revertArtifact(getSourceArtifact());
      getSourceArtifact().revert();
   }

   public int getMergeGammaId() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, Exception {
      throw new Exception("Artifact Conflicts can not be handled they must be reverted on the Source Branch");
   }

}
