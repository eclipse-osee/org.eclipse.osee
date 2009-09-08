/*
 * Created on Aug 12, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.serverCommit;

import java.rmi.activation.Activator;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.CommitDbTx;
import org.eclipse.osee.framework.skynet.core.commit.ChangeCache;
import org.eclipse.osee.framework.skynet.core.commit.ChangeDatabaseDataAccessor;
import org.eclipse.osee.framework.skynet.core.commit.ChangeLocator;
import org.eclipse.osee.framework.skynet.core.commit.CommitDbOperation;
import org.eclipse.osee.framework.skynet.core.commit.ComputeNetChangeOperation;
import org.eclipse.osee.framework.skynet.core.commit.OseeChange;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;

/**
 * @author Jeff C. Phillips
 */
public class CommitService implements ICommitService {

   @Override
   public void commitBranch(ConflictManagerExternal conflictManager, boolean archiveSourceBranch) throws OseeCoreException {
      if (false) {
         IProgressMonitor monitor = new NullProgressMonitor();
         ChangeLocator locator =
               new ChangeLocator(conflictManager.getSourceBranch(), conflictManager.getDestinationBranch());
         List<OseeChange> changes;
         try {
            changes = new ChangeCache(new ChangeDatabaseDataAccessor()).getRawChangeData(monitor, locator);
            Operations.executeWork(new ComputeNetChangeOperation(changes, conflictManager, null), monitor, -1);
            Operations.executeWork(new CommitDbOperation(conflictManager, changes), monitor, -1);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      } else {
         new CommitDbTx(conflictManager, archiveSourceBranch).execute();
      }
   }
}
