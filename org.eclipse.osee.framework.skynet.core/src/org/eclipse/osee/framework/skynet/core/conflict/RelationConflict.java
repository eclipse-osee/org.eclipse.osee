/*
 * Created on Apr 4, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.conflict;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.util.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleArtifactsExist;
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
   public RelationConflict(int sourceGamma, int destGamma, int artId, TransactionId toTransactionId, TransactionId fromTransactionId, ModificationType modType, ChangeType changeType, Branch mergeBranch, Branch sourceBranch, Branch destBranch) {
      super(sourceGamma, destGamma, artId, toTransactionId, fromTransactionId, modType, changeType, mergeBranch,
            sourceBranch, destBranch);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#clearAttributeValue()
    */
   @Override
   public boolean clearValue() throws SQLException {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#computeStatus()
    */
   @Override
   public Status computeStatus() throws SQLException {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getChangeItem()
    */
   @Override
   public String getChangeItem() throws SQLException {
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
   public String getMergeDisplayData() throws SQLException {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getMergeValue()
    */
   public String getMergeValue() throws SQLException {
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
   public boolean mergeEqualsDestination() throws SQLException {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#mergeEqualsSource()
    */
   @Override
   public boolean mergeEqualsSource() throws SQLException {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#setAttributeToDest()
    */
   @Override
   public boolean setToDest() throws SQLException {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#setAttributeToSource()
    */
   @Override
   public boolean setToSource() throws SQLException {
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

   public int getMergeGammaId() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, Exception {
      throw new Exception("Relation Conflicts are not implemented yet");
   }
}
