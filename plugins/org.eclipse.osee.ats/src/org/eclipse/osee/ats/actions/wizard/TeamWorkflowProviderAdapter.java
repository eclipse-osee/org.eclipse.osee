/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.actions.wizard;

import java.util.Collection;
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
   public String getWorkflowDefinitionId(AbstractWorkflowArtifact artifact) throws OseeCoreException {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public String getRelatedTaskWorkflowDefinitionId(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
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
