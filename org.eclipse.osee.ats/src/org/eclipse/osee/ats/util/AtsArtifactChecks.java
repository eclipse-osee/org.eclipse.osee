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
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.world.search.ActionableItemWorldSearchItem;
import org.eclipse.osee.ats.world.search.TeamWorldSearchItem;
import org.eclipse.osee.ats.world.search.UserRelatedToAtsObjectSearch;
import org.eclipse.osee.ats.world.search.TeamWorldSearchItem.ReleasedOption;
import org.eclipse.osee.ats.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCheck;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsArtifactChecks extends ArtifactCheck {

   /**
    * Check for certain conditions that must be met to delete an ATS object or User artifact.
    */
   public AtsArtifactChecks() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.IArtifactOperation#isDeleteable(java.util.Collection)
    */
   @Override
   public Result isDeleteable(Collection<Artifact> artifacts) throws OseeCoreException {
      // Check Actionable Items
      Result result = checkActionableItems(artifacts);
      if (result.isFalse()) return result;
      // Check Team Definitions
      result = checkTeamDefinitions(artifacts);
      if (result.isFalse()) return result;
      // Check VUE Workflow General Documents
      result = checkAtsWorkflows(artifacts);
      if (result.isFalse()) return result;
      // Check User artifacts related to ATS SMAs
      result = checkUsers(artifacts);
      if (result.isFalse()) return result;

      return Result.TrueResult;
   }

   public Result checkActionableItems(Collection<Artifact> artifacts) throws OseeCoreException {
      Set<ActionableItemArtifact> aias = new HashSet<ActionableItemArtifact>();
      for (Artifact art : artifacts) {
         if (art instanceof ActionableItemArtifact) aias.add((ActionableItemArtifact) art);
      }
      if (aias.size() > 0) {
         ActionableItemWorldSearchItem srch = new ActionableItemWorldSearchItem("AI search", aias, true, false, true);
         if (srch.performSearchGetResults(false).size() > 0) {
            return new Result(
                  "Actionable Items (or children AIs) selected to delete have related Team Workflows; Delete or re-assign Team Workflows first.");
         }
      }
      return Result.TrueResult;
   }

   public Result checkTeamDefinitions(Collection<Artifact> artifacts) throws OseeCoreException {
      Set<TeamDefinitionArtifact> teamDefs = new HashSet<TeamDefinitionArtifact>();
      for (Artifact art : artifacts) {
         if (art instanceof TeamDefinitionArtifact) teamDefs.add((TeamDefinitionArtifact) art);
      }
      if (teamDefs.size() > 0) {

         TeamWorldSearchItem srch =
               new TeamWorldSearchItem("Team Def search", teamDefs, true, false, true, null, null, ReleasedOption.Both);
         if (srch.performSearchGetResults(false).size() > 0) {
            return new Result(
                  "Team Definition (or children Team Definitions) selected to delete have related Team Workflows; Delete or re-assign Team Workflows first.");
         }
      }
      return Result.TrueResult;
   }

   public Result checkAtsWorkflows(Collection<Artifact> artifacts) throws OseeCoreException {
      for (Artifact art : artifacts) {
         if (art.getArtifactTypeName().equals(WorkFlowDefinition.ARTIFACT_NAME)) {
            if (art.getRelatedArtifacts(AtsRelation.WorkItem__Parent).size() > 0) return new Result(
                  "ATS Team Workflow selected to delete has related Team Definition(s); Re-assign Team Definitions to new Team Workflows first.");
         }
         if (art.getArtifactTypeName().equals(WorkRuleDefinition.ARTIFACT_NAME)) {
            if (art.getRelatedArtifacts(AtsRelation.WorkItem__Parent).size() > 0) return new Result(
                  "ATS Workflow Rule selected to delete has related Work Items that must be removed first.");
         }
         if (art.getArtifactTypeName().equals(WorkWidgetDefinition.ARTIFACT_NAME)) {
            if (art.getRelatedArtifacts(AtsRelation.WorkItem__Parent).size() > 0) return new Result(
                  "ATS Workflow Widget selected to delete has related Work Items that must be removed first.");
         }
      }
      return Result.TrueResult;
   }

   public Result checkUsers(Collection<Artifact> artifacts) throws OseeCoreException {
      Set<User> users = new HashSet<User>();
      for (Artifact art : artifacts) {
         if (art instanceof User) users.add((User) art);
      }
      for (User user : users) {
         UserRelatedToAtsObjectSearch srch =
               new UserRelatedToAtsObjectSearch("User search", user, false, LoadView.None);
         if (srch.performSearchGetResults().size() > 0) return new Result(
               "User name: \"" + user.getDescriptiveName() + "\" userId: \"" + user.getUserId() + "\" selected to delete has related ATS Objects; Un-relate to ATS first before deleting.");

      }
      return Result.TrueResult;
   }

}
