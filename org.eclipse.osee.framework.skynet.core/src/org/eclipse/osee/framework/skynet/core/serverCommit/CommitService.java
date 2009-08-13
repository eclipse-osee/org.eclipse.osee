/*
 * Created on Aug 12, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.serverCommit;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.CommitDbTx;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;

/**
 * @author Jeff C. Phillips
 *
 */
public class CommitService implements ICommitService{

   @Override
   public void commitBranch(ConflictManagerExternal conflictManager, boolean archiveSourceBranch) throws OseeCoreException {
      new CommitDbTx(conflictManager, archiveSourceBranch).execute();
   }
}
