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

import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.db.connection.exception.BranchMergeException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.swt.graphics.Image;

/**
 * @author Theron Virgin
 */
public class RelationConflict extends Conflict {

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
   public RelationConflict(int sourceGamma, int destGamma, int artId, TransactionId toTransactionId, Branch mergeBranch, Branch sourceBranch, Branch destBranch) {
      super(sourceGamma, destGamma, artId, toTransactionId, null, mergeBranch, sourceBranch, destBranch);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#clearAttributeValue()
    */
   @Override
   public boolean clearValue() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#computeStatus()
    */
   public ConflictStatus computeStatus() {
      return null;
   }

   public int getObjectId() throws OseeCoreException {
      return 0;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getChangeItem()
    */
   @Override
   public String getChangeItem() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getConflictType()
    */
   @Override
   public ConflictType getConflictType() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getDestDisplayData()
    */
   @Override
   public String getDestDisplayData() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getImage()
    */
   @Override
   public Image getImage() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getMergeDisplayData()
    */
   @Override
   public String getMergeDisplayData() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getMergeValue()
    */
   public String getMergeValue() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getSourceDisplayData()
    */
   @Override
   public String getSourceDisplayData() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#mergeEqualsDestination()
    */
   @Override
   public boolean mergeEqualsDestination() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#mergeEqualsSource()
    */
   @Override
   public boolean mergeEqualsSource() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#setAttributeToDest()
    */
   @Override
   public boolean setToDest() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#setAttributeToSource()
    */
   @Override
   public boolean setToSource() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#sourceEqualsDestination()
    */
   @Override
   public boolean sourceEqualsDestination() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @Override
   public Object getAdapter(Class adapter) {
      return null;
   }

   public int getMergeGammaId() throws BranchMergeException {
      throw new BranchMergeException("Relation Conflicts are not implemented yet");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#applyPreviousMerge(int)
    */
   @Override
   public boolean applyPreviousMerge(int mergeBranchId, int destBranchId) throws OseeCoreException {
      return false;
   }
}
