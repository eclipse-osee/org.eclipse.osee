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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.ITeamWorkflowProvider;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public abstract class TeamWorkflowProviderAdapter implements ITeamWorkflowProvider {

   @SuppressWarnings("unused")
   @Override
   public boolean isResponsibleForTeamWorkflowCreation(IAtsTeamDefinition teamDef, Collection<IAtsActionableItem> actionableItems) throws OseeCoreException {
      return false;
   }

   @SuppressWarnings("unused")
   @Override
   public IArtifactType getTeamWorkflowArtifactType(IAtsTeamDefinition teamDef, Collection<IAtsActionableItem> actionableItems) throws OseeCoreException {
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
   public String getWorkflowDefinitionId(IAtsWorkItem workItem) throws OseeCoreException {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public String getRelatedTaskWorkflowDefinitionId(IAtsTeamWorkflow teamWf) throws OseeCoreException {
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
