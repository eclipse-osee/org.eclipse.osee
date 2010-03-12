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
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.actions.wizard.IAtsTeamWorkflow;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @author Donald G. Dunne
 */
public class DemoTeamWorkflows implements IAtsTeamWorkflow {

   private static List<? extends IArtifactType> workflowArtifactTypes;

   public DemoTeamWorkflows() {
   }

   public String getTeamWorkflowArtifactName(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> actionableItems) throws OseeCoreException {
      if (teamDef.getName().contains("Code")) {
         return DemoCodeTeamWorkflowArtifact.ARTIFACT_NAME;
      } else if (teamDef.getName().contains("Test")) {
         return DemoTestTeamWorkflowArtifact.ARTIFACT_NAME;
      } else if (teamDef.getName().contains("Requirements")) {
         return DemoReqTeamWorkflowArtifact.ARTIFACT_NAME;
      } else if (teamDef.getName().contains("SAW HW")) {
         return DemoReqTeamWorkflowArtifact.ARTIFACT_NAME;
      }
      return AtsArtifactTypes.TeamWorkflow.getName();
   }

   public boolean isResponsibleForTeamWorkflowCreation(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> actionableItems) throws OseeCoreException {
      return teamDef.getName().contains("SAW") || teamDef.getName().contains("CIS");
   }

   public void teamWorkflowCreated(TeamWorkFlowArtifact teamArt) {
      return;
   }

   public Collection<? extends IArtifactType> getTeamWorkflowArtifactNames() throws OseeCoreException {
      if (workflowArtifactTypes == null) {
         if (TestUtil.isDemoDb()) {
            workflowArtifactTypes =
                  Arrays.asList(DemoArtifactTypes.DemoCodeTeamWorkflow, DemoArtifactTypes.DemoTestTeamWorkflow,
                        DemoArtifactTypes.DemoReqTeamWorkflow);
         } else {
            workflowArtifactTypes = Collections.emptyList();
         }
      }
      return workflowArtifactTypes;
   }

   @Override
   public void teamWorkflowDuplicating(TeamWorkFlowArtifact teamArt, TeamWorkFlowArtifact dupTeamArt) throws OseeCoreException {
   }

}
