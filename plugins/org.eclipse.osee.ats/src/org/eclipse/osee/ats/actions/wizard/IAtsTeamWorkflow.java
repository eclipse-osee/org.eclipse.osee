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
package org.eclipse.osee.ats.actions.wizard;

import java.util.Collection;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsTeamWorkflow {

   /**
    * Return true if this class/plugin is responsible for the creation of the Team Workflow that will be created for the
    * given Team Definition. This should be a light-weight check.
    * 
    * @param teamDef related to the workflow to be created
    * @param actionableItems that were selected for the creation
    * @return true if responsible, false if not
    */
   public boolean isResponsibleForTeamWorkflowCreation(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> actionableItems) throws OseeCoreException;

   /**
    * Same as @link getTeamWorkflowArtifactName() but returns the IArtifactType instead of String name.
    */
   public IArtifactType getTeamWorkflowArtifactType(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> actionableItems) throws OseeCoreException;

   /**
    * Notification that a teamWorkflow is being duplicated. This allows the extension to do necessary changes to
    * duplicated workflow.
    */
   public void teamWorkflowDuplicating(TeamWorkFlowArtifact teamArt, TeamWorkFlowArtifact dupTeamArt) throws OseeCoreException;

   /**
    * Notification that a teamWorkflow was created. This allows the extension to do necessary initial tasks after the
    * team workflow artifact is created. All changes made to dupTeamArt will be persisted after this call.
    */
   public void teamWorkflowCreated(TeamWorkFlowArtifact teamArt);

   /**
    * Return a collection of all team workflow artifact type names. These are used by ATS when searching is performed
    * since there is no "inheritance" in the DB model.
    * 
    * @return collection of all team workflow artifact type names
    */
   public Collection<? extends IArtifactType> getTeamWorkflowArtifactNames() throws OseeCoreException;

   public String getWorkflowDefinitionId(AbstractWorkflowArtifact artifact) throws OseeCoreException;

   public String getRelatedTaskWorkflowDefinitionId(AbstractWorkflowArtifact artifact) throws OseeCoreException;
}
