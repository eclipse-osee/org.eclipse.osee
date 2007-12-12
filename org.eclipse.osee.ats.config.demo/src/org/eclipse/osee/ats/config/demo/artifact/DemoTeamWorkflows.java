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
package org.eclipse.osee.ats.config.demo.artifact;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.actions.wizard.IAtsTeamWorkflow;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;

/**
 * @author Donald G. Dunne
 */
public class DemoTeamWorkflows implements IAtsTeamWorkflow {

   private static List<String> workflowArtifactNames;

   /**
    * 
    */
   public DemoTeamWorkflows() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.actions.wizard.IAtsTeamWorflow#getTeamWorkflowArtifactName(org.eclipse.osee.ats.artifact.TeamDefinitionArtifact,
    *      java.util.Collection, java.util.Collection)
    */
   public String getTeamWorkflowArtifactName(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> actionableItems) throws Exception {
      if (teamDef.getDescriptiveName().contains("Code"))
         return DemoCodeTeamWorkflowArtifact.ARTIFACT_NAME;
      else if (teamDef.getDescriptiveName().contains("Test"))
         return DemoTestTeamWorkflowArtifact.ARTIFACT_NAME;
      else if (teamDef.getDescriptiveName().contains("Requirements"))
         return DemoReqTeamWorkflowArtifact.ARTIFACT_NAME;
      else if (teamDef.getDescriptiveName().contains("Design")) return TeamWorkFlowArtifact.ARTIFACT_NAME;
      throw new IllegalArgumentException("Unhandled teamDef => " + teamDef.getDescriptiveName());
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.actions.wizard.IAtsTeamWorflow#isResponsibleForTeamWorkflowCreation(org.eclipse.osee.ats.artifact.TeamDefinitionArtifact,
    *      java.util.Collection, java.util.Collection)
    */
   public boolean isResponsibleForTeamWorkflowCreation(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> actionableItems) throws Exception {
      return (teamDef.getDescriptiveName().contains("SAW") || teamDef.getDescriptiveName().contains("CIS"));
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.actions.wizard.IAtsTeamWorflow#teamWorkflowCreated(org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact)
    */
   public void teamWorkflowCreated(TeamWorkFlowArtifact teamArt) {
      return;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.actions.wizard.IAtsTeamWorkflow#getTeamWorkflowArtifactNames()
    */
   public Collection<String> getTeamWorkflowArtifactNames() {
      if (workflowArtifactNames == null) {
         workflowArtifactNames =
               Arrays.asList(new String[] {DemoCodeTeamWorkflowArtifact.ARTIFACT_NAME,
                     DemoTestTeamWorkflowArtifact.ARTIFACT_NAME, DemoReqTeamWorkflowArtifact.ARTIFACT_NAME});
      }
      return workflowArtifactNames;
   }

}
