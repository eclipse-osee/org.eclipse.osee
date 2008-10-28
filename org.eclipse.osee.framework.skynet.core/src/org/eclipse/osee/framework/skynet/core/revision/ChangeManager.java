package org.eclipse.osee.framework.skynet.core.revision;

import java.util.Collection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * Public API class for access to change data from branches and transactionIds
 * 
 * @author Jeff C. Phillips
 * @author Donald G. Dunne
 */
public class ChangeManager {

   /**
    * Acquires artifact, relation and attribute changes from a source branch since its creation.
    * 
    * @param sourceBranch
    * @param baselineTransactionId
    * @return changes
    * @throws OseeCoreException
    */
   public static Collection<Change> getChangesPerTransaction(TransactionId transactionId) throws OseeCoreException {
      return InternalChangeManager.getInstance().getChanges(null, transactionId);
   }

   public static ChangeData getChangeDataPerTransaction(TransactionId transactionId) throws OseeCoreException {
      return new ChangeData(InternalChangeManager.getInstance().getChanges(null, transactionId));
   }

   /**
    * Acquires artifact, relation and attribute changes from a source branch since its creation.
    * 
    * @param sourceBranch
    * @param baselineTransactionId
    * @return changes
    * @throws OseeCoreException
    */
   public static Collection<Change> getChangesPerBranch(Branch sourceBranch) throws OseeCoreException {
      return InternalChangeManager.getInstance().getChanges(sourceBranch, null);
   }

   public static ChangeData getChangeDataPerBranch(Branch sourceBranch) throws OseeCoreException {
      return new ChangeData(InternalChangeManager.getInstance().getChanges(sourceBranch, null));
   }

   /**
    * Acquires artifact, relation and attribute changes from a source branch since its creation.
    * 
    * @param sourceBranch
    * @param baselineTransactionId
    * @return changes
    * @throws OseeCoreException
    */
   public static Collection<Change> getChanges(Branch sourceBranch, TransactionId transactionId) throws OseeCoreException {
      return InternalChangeManager.getInstance().getChanges(sourceBranch, transactionId);
   }

   public static ChangeData getChangeData(Branch sourceBranch, TransactionId transactionId) throws OseeCoreException {
      return new ChangeData(getChanges(sourceBranch, transactionId));
   }

   /**
    * @return true changes exist
    * @throws OseeCoreException
    */
   public static Boolean isChangesOnWorkingBranch(Branch workingBranch) throws OseeCoreException {
      return InternalChangeManager.getInstance().isChangesOnWorkingBranch(workingBranch);
   }
}
