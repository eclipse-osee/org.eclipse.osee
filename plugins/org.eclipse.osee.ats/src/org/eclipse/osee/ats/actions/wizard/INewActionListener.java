/*
 * Created on Dec 6, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions.wizard;

import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public interface INewActionListener {

   /**
    * Called after Action and team workflows are created and before persist of Action
    */
   public void actionCreated(Artifact actionArt) throws OseeCoreException;

   /**
    * Called after team workflow and initialized and before persist of Action
    */
   public void teamCreated(TeamWorkFlowArtifact teamArt) throws OseeCoreException;

   /**
    * @return workflow id to use instead of default configured id
    */
   public String getOverrideWorkDefinitionId(TeamWorkFlowArtifact teamArt) throws OseeCoreException;
}
