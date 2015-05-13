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
package org.eclipse.osee.ats.api.team;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public abstract class TeamWorkflowProviderAdapter implements ITeamWorkflowProvider {

   @Override
   public boolean isResponsibleForTeamWorkflowCreation(IAtsTeamDefinition teamDef, Collection<IAtsActionableItem> actionableItems) throws OseeCoreException {
      return false;
   }

   @Override
   public IArtifactType getTeamWorkflowArtifactType(IAtsTeamDefinition teamDef, Collection<IAtsActionableItem> actionableItems) throws OseeCoreException {
      return null;
   }

   @Override
   public void teamWorkflowDuplicating(IAtsTeamWorkflow teamWf, IAtsTeamWorkflow dupTeamArt) throws OseeCoreException {
      // provided for subclass implementation
   }

   @Override
   public String getWorkflowDefinitionId(IAtsWorkItem workItem) throws OseeCoreException {
      return null;
   }

   @Override
   public String getRelatedTaskWorkflowDefinitionId(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      return null;
   }

   @Override
   public String getPcrId(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      return null;
   }

   @Override
   public String getArtifactTypeShortName(IAtsTeamWorkflow teamWf) {
      return null;
   }

}
