/*
 * Created on Dec 3, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions.wizard;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.core.config.ActionableItemArtifact;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.ITeamWorkflowProvider;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public abstract class TeamWorkflowProviderAdapter implements ITeamWorkflowProvider {

   @SuppressWarnings("unused")
   @Override
   public boolean isResponsibleForTeamWorkflowCreation(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> actionableItems) throws OseeCoreException {
      return false;
   }

   @SuppressWarnings("unused")
   @Override
   public IArtifactType getTeamWorkflowArtifactType(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> actionableItems) throws OseeCoreException {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public void teamWorkflowDuplicating(TeamWorkFlowArtifact teamArt, TeamWorkFlowArtifact dupTeamArt) throws OseeCoreException {
      // provided for subclass implementation
   }

   @Override
   public void teamWorkflowCreated(TeamWorkFlowArtifact teamArt) {
      // provided for subclass implementation
   }

   @SuppressWarnings("unused")
   @Override
   public Collection<? extends IArtifactType> getTeamWorkflowArtifactTypes() throws OseeCoreException {
      return Collections.emptyList();
   }

   @SuppressWarnings("unused")
   @Override
   public String getWorkflowDefinitionId(AbstractWorkflowArtifact artifact) throws OseeCoreException {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public String getRelatedTaskWorkflowDefinitionId(AbstractWorkflowArtifact artifact) throws OseeCoreException {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public String getPcrId(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      return null;
   }

   @Override
   public String getArtifactTypeShortName(TeamWorkFlowArtifact teamArt) {
      return null;
   }

}
