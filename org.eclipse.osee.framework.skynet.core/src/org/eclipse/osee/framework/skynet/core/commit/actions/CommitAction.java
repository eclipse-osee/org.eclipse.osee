/*
 * Created on Feb 17, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.commit.actions;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author TheronVirgin
 */
public interface CommitAction {
   public void runCommitAction(Branch branch) throws OseeCoreException;

}
