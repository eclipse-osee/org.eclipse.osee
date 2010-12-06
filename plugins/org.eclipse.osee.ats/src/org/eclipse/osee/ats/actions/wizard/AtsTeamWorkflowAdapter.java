/*
 * Created on Dec 3, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions.wizard;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public abstract class AtsTeamWorkflowAdapter implements IAtsTeamWorkflow {

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
   public Collection<? extends IArtifactType> getTeamWorkflowArtifactNames() throws OseeCoreException {
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

}
