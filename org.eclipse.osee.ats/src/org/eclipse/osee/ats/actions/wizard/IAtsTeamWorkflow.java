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

import java.sql.SQLException;
import java.util.Collection;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;

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
    * @throws Exception TODO
    */
   public boolean isResponsibleForTeamWorkflowCreation(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> actionableItems) throws OseeCoreException, SQLException;

   /**
    * Return the artifact type name for the given parameters. This method will only be called if
    * isResponsibleForTeamWorkflowCreation returns true.
    * 
    * @param teamDef related to the workflow to be created
    * @param actionableItems that were selected for the creation
    * @return string artifact type name
    * @throws Exception TODO
    */
   public String getTeamWorkflowArtifactName(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> actionableItems) throws OseeCoreException, SQLException;

   /**
    * Notification that a teamWorkflow was created. This allows the extension to do necessary initial tasks after the
    * team workflow artifact is created.
    * 
    * @param teamArt
    */
   public void teamWorkflowCreated(TeamWorkFlowArtifact teamArt);

   /**
    * Return a collection of all team workflow artifact type names. These are used by ATS when searching is performed
    * since there is no "inheritance" in the DB model.
    * 
    * @return collection of all team workflow artifact type names
    */
   public Collection<String> getTeamWorkflowArtifactNames();
}
