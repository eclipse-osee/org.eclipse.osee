/*
 * Created on Mar 1, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.config;

import java.util.Collection;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface IAtsProgramManager {

   public String getName(TeamWorkFlowArtifact teamArt);

   public boolean isApplicable(TeamWorkFlowArtifact teamArt);

   public void validateReqChanges(TeamWorkFlowArtifact teamArt) throws OseeCoreException;

   public String getName();

   public Collection<? extends IAtsProgram> getPrograms() throws OseeCoreException;

   public void reloadCache() throws OseeCoreException;

   public String getXProgramComboWidgetName();

   public IAtsProgram getProgram(TeamWorkFlowArtifact teamArt) throws OseeCoreException;

   public IArtifactToken getWcafeReviewAssigneeUserGroup() throws OseeCoreException;

}
