/*
 * Created on Apr 16, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.widgets.commit;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public interface ICommitConfigArtifact {

   public Branch getParentBranch() throws OseeCoreException;

   public Result isCommitBranchAllowed() throws OseeCoreException;

   public Result isCreateBranchAllowed() throws OseeCoreException;

   public String getFullDisplayName() throws OseeCoreException;

}
