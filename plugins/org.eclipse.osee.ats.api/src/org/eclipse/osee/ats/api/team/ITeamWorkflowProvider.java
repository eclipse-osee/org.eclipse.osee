/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
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
public interface ITeamWorkflowProvider {

   /**
    * Return true if this class/plugin is responsible for the creation of the Team Workflow that will be created for the
    * given Team Definition. This should be a light-weight check.
    * 
    * @param teamDef related to the workflow to be created
    * @param actionableItems that were selected for the creation
    * @return true if responsible, false if not
    */
   public boolean isResponsibleForTeamWorkflowCreation(IAtsTeamDefinition teamDef, Collection<IAtsActionableItem> actionableItems) throws OseeCoreException;

   /**
    * Same as @link getTeamWorkflowArtifactName() but returns the IArtifactType instead of String name.
    */
   public IArtifactType getTeamWorkflowArtifactType(IAtsTeamDefinition teamDef, Collection<IAtsActionableItem> actionableItems) throws OseeCoreException;

   /**
    * Notification that a teamWorkflow is being duplicated. This allows the extension to do necessary changes to
    * duplicated workflow.
    */
   public void teamWorkflowDuplicating(IAtsTeamWorkflow teamWf, IAtsTeamWorkflow dupTeamWf) throws OseeCoreException;

   /**
    * Notification that a teamWorkflow was created. This allows the extension to do necessary initial tasks after the
    * team workflow artifact is created. All changes made to dupTeamArt will be persisted after this call.
    */
   public void teamWorkflowCreated(IAtsTeamWorkflow teamWf);

   public String getWorkflowDefinitionId(IAtsWorkItem workItem) throws OseeCoreException;

   public String getRelatedTaskWorkflowDefinitionId(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   /**
    * Assigned or computed Id that will show at the top of the editor
    */
   public String getPcrId(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   /**
    * 5-9 character short name for UI and display purposes
    */
   public String getArtifactTypeShortName(IAtsTeamWorkflow teamWf);

   public String getBranchName(IAtsTeamWorkflow teamWf);

   public boolean isResponsibleFor(IAtsWorkItem workItem);
}
