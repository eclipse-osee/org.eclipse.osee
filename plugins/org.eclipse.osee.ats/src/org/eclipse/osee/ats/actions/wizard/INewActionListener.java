/*
 * Created on Dec 6, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions.wizard;

import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface INewActionListener {

   public void actionCreated(ActionArtifact actionArt) throws OseeCoreException;

   public void teamCreated(TeamWorkFlowArtifact teamArt) throws OseeCoreException;
}
