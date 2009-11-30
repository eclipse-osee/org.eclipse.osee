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
package org.eclipse.osee.ats.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.world.search.ActionableItemWorldSearchItem;
import org.eclipse.osee.ats.world.search.TeamWorldSearchItem;
import org.eclipse.osee.ats.world.search.UserRelatedToAtsObjectSearch;
import org.eclipse.osee.ats.world.search.TeamWorldSearchItem.ReleasedOption;
import org.eclipse.osee.ats.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCheck;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;

/**
 * Check for certain conditions that must be met to delete an ATS object or User artifact.
 * 
 * @author Donald G. Dunne
 */
public class AtsArtifactChecks extends ArtifactCheck {
   @Override
   public IStatus isDeleteable(Collection<Artifact> artifacts) throws OseeCoreException {
      String result = checkActionableItems(artifacts);
      if (result != null) {
         return createStatus(result);
      }

      result = checkTeamDefinitions(artifacts);
      if (result != null) {
         return createStatus(result);
      }

      result = checkAtsWorkflows(artifacts);
      if (result != null) {
         return createStatus(result);
      }

      result = checkUsers(artifacts);
      if (result != null) {
         return createStatus(result);
      }

      return OK_STATUS;
   }

   private IStatus createStatus(String message) {
      return new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID, message);
   }

   private String checkActionableItems(Collection<Artifact> artifacts) throws OseeCoreException {
      Set<ActionableItemArtifact> aias = new HashSet<ActionableItemArtifact>();
      for (Artifact art : artifacts) {
         if (art instanceof ActionableItemArtifact) {
            aias.add((ActionableItemArtifact) art);
         }
      }
      if (aias.size() > 0) {
         ActionableItemWorldSearchItem srch = new ActionableItemWorldSearchItem("AI search", aias, true, true, false);
         if (srch.performSearchGetResults(false).size() > 0) {
            return "Actionable Items (or children AIs) selected to delete have related Team Workflows; Delete or re-assign Team Workflows first.";
         }
      }
      return null;
   }

   private String checkTeamDefinitions(Collection<Artifact> artifacts) throws OseeCoreException {
      Set<TeamDefinitionArtifact> teamDefs = new HashSet<TeamDefinitionArtifact>();
      for (Artifact art : artifacts) {
         if (art instanceof TeamDefinitionArtifact) {
            teamDefs.add((TeamDefinitionArtifact) art);
         }
      }
      if (teamDefs.size() > 0) {

         TeamWorldSearchItem srch =
               new TeamWorldSearchItem("Team Def search", teamDefs, true, false, true, null, null, ReleasedOption.Both);
         if (srch.performSearchGetResults(false).size() > 0) {
            return "Team Definition (or children Team Definitions) selected to delete have related Team Workflows; Delete or re-assign Team Workflows first.";
         }
      }
      return null;
   }

   private String checkAtsWorkflows(Collection<Artifact> artifacts) throws OseeCoreException {
      for (Artifact art : artifacts) {
         if (art.isOfType(CoreArtifactTypes.WorkFlowDefinition)) {
            if (art.getRelatedArtifacts(CoreRelationTypes.WorkItem__Parent).size() > 0) {
               return "ATS WorkFlowDefinition  [" + art + "] selected to delete has related Team Definition(s) via WorkItem__Parent; Re-assign Team Definitions to new WorkFlowDefinition first.";
            }
         }
         if (art.isOfType(WorkRuleDefinition.ARTIFACT_NAME)) {
            if (art.getRelatedArtifacts(CoreRelationTypes.WorkItem__Parent).size() > 0) {
               return "ATS WorkRuleDefinition [" + art + "] selected to delete has related Work Items via WorkItem__Parent that must be removed first.";
            }
         }
         if (art.isOfType(WorkWidgetDefinition.ARTIFACT_NAME)) {
            if (art.getRelatedArtifacts(CoreRelationTypes.WorkItem__Parent).size() > 0) {
               return "ATS WorkWidgetDefinition [" + art + "] selected to delete has related Work Items via WorkItem__Parent that must be removed first.";
            }
         }
      }
      return null;
   }

   private String checkUsers(Collection<Artifact> artifacts) throws OseeCoreException {
      Set<User> users = new HashSet<User>();
      for (Artifact art : artifacts) {
         if (art instanceof User) {
            users.add((User) art);
         }
      }
      for (User user : users) {
         UserRelatedToAtsObjectSearch srch =
               new UserRelatedToAtsObjectSearch("User search", user, false, LoadView.None);
         if (srch.performSearchGetResults().size() > 0) {
            return "User name: \"" + user.getName() + "\" userId: \"" + user.getUserId() + "\" selected to delete has related ATS Objects; Un-relate to ATS first before deleting.";
         }
      }
      return null;
   }
}