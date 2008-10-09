package org.eclipse.osee.framework.skynet.core.revision;

import java.sql.SQLException;
import java.util.Collection;
import org.eclipse.osee.framework.db.connection.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.TransactionDoesNotExist;
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
    * @throws SQLException
    * @throws OseeCoreException
    */
   public static Collection<Change> getChangesPerTransaction(TransactionId transactionId) throws OseeCoreException {
      try {
         return InternalChangeManager.getInstance().getChanges(null, transactionId);
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   public static ChangeData getChangeDataPerTransaction(TransactionId transactionId) throws OseeCoreException {
      try {
         return new ChangeData(InternalChangeManager.getInstance().getChanges(null, transactionId));
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   /**
    * Acquires artifact, relation and attribute changes from a source branch since its creation.
    * 
    * @param sourceBranch
    * @param baselineTransactionId
    * @return changes
    * @throws SQLException
    * @throws OseeCoreException
    */
   public static Collection<Change> getChangesPerBranch(Branch sourceBranch) throws OseeCoreException {
      try {
         return InternalChangeManager.getInstance().getChanges(sourceBranch, null);
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   public static ChangeData getChangeDataPerBranch(Branch sourceBranch) throws OseeCoreException {
      try {
         return new ChangeData(InternalChangeManager.getInstance().getChanges(sourceBranch, null));
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   /**
    * Acquires artifact, relation and attribute changes from a source branch since its creation.
    * 
    * @param sourceBranch
    * @param baselineTransactionId
    * @return changes
    * @throws SQLException
    * @throws OseeCoreException
    */
   public static Collection<Change> getChanges(Branch sourceBranch, TransactionId transactionId) throws OseeCoreException {
      try {
         return InternalChangeManager.getInstance().getChanges(sourceBranch, transactionId);
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   public static ChangeData getChangeData(Branch sourceBranch, TransactionId transactionId) throws OseeCoreException {
      try {
         return new ChangeData(getChanges(sourceBranch, transactionId));
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   /**
    * @return true changes exist
    * @throws SQLException
    * @throws TransactionDoesNotExist
    * @throws BranchDoesNotExist
    */
   public static Boolean isChangesOnWorkingBranch(Branch workingBranch) throws OseeCoreException {
      return InternalChangeManager.getInstance().isChangesOnWorkingBranch(workingBranch);
   }
}
