/*
 * Created on Aug 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.access;

import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface IAtsAccessControlService {

   /**
    * Return the AccessContextId associated with the object given. null otherwise
    */
   public AccessContextId getBranchAccessContextIdFromWorkflow(TeamWorkFlowArtifact teamWorkflow) throws OseeCoreException;

}
