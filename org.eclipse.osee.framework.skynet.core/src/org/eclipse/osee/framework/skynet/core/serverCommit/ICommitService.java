/*
 * Created on Jul 31, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.serverCommit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;

/**
 * @author Jeff C. Phillips
 */
public interface ICommitService {

   public void commitBranch(IProgressMonitor monitor, ConflictManagerExternal conflictManager, boolean archiveSourceBranch) throws OseeCoreException;
}
