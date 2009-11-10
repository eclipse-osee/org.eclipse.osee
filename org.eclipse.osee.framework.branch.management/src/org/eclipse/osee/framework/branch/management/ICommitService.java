/*
 * Created on Jul 31, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Jeff C. Phillips
 * @author Megumi Telles		   
 */
public interface ICommitService {

   public void commitBranch(IProgressMonitor monitor, Branch sourceBranch, Branch destinationBranch, Branch mergeBranch, boolean archiveSourceBranch) throws OseeCoreException;
}
