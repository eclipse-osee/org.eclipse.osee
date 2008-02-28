/*
 * Created on Feb 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.world.search.ActionableItemWorldSearchItem;
import org.eclipse.osee.ats.world.search.TeamWorldSearchItem;
import org.eclipse.osee.ats.world.search.UserRelatedToAtsObjectSearch;
import org.eclipse.osee.ats.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IArtifactCheck;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class AtsArtifactChecks implements IArtifactCheck {

   /**
    * Check for certain conditions that must be met to delete an ATS object or User artifact.
    */
   public AtsArtifactChecks() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.IArtifactOperation#isDeleteable(java.util.Collection)
    */
   @Override
   public Result isDeleteable(Collection<Artifact> artifacts) throws Exception {
      // Check Actionable Items
      Result result = checkActionableItems(artifacts);
      if (result.isFalse()) return result;
      // Check Team Definitions
      result = checkTeamDefinitions(artifacts);
      if (result.isFalse()) return result;
      // Check VUE Workflow General Documents
      result = checkAtsVueWorkflows(artifacts);
      if (result.isFalse()) return result;
      // Check User artifacts related to ATS SMAs
      result = checkUsers(artifacts);
      if (result.isFalse()) return result;

      return Result.TrueResult;
   }

   public Result checkActionableItems(Collection<Artifact> artifacts) throws Exception {
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

   public Result checkTeamDefinitions(Collection<Artifact> artifacts) throws Exception {
      Set<TeamDefinitionArtifact> aias = new HashSet<TeamDefinitionArtifact>();
      for (Artifact art : artifacts) {
         if (art instanceof TeamDefinitionArtifact) aias.add((TeamDefinitionArtifact) art);
      }
      if (aias.size() > 0) {

         TeamWorldSearchItem srch = new TeamWorldSearchItem("Team Def search", aias, true, false, true);
         if (srch.performSearchGetResults(false).size() > 0) {
            return new Result(
                  "Team Definition (or children Team Definitions) selected to delete have related Team Workflows; Delete or re-assign Team Workflows first.");
         }
      }
      return Result.TrueResult;
   }

   public Result checkAtsVueWorkflows(Collection<Artifact> artifacts) throws Exception {
      for (Artifact art : artifacts) {
         if (art.getArtifactTypeName().equals("General Document")) {
            String ext = art.getSoleAttributeValue("Extension");
            if (ext != null && ext.equals("vue")) {
               if (art.getArtifacts(RelationSide.TeamDefinitionToTaskWorkflowDiagram_TeamDefinition).size() > 0) return new Result(
                     "Team Workflow selected to delete has related Team Definition(s); Team Definitions to new Team Workflows first.");
            }
         }
      }
      return Result.TrueResult;
   }

   public Result checkUsers(Collection<Artifact> artifacts) throws Exception {
      Set<User> users = new HashSet<User>();
      for (Artifact art : artifacts) {
         if (art instanceof User) users.add((User) art);
      }
      for (User user : users) {
         UserRelatedToAtsObjectSearch srch = new UserRelatedToAtsObjectSearch("User search", user, LoadView.None);
         if (srch.performSearchGetResults().size() > 0) return new Result(
               "User name: \"" + user.getDescriptiveName() + "\" userId: \"" + user.getUserId() + "\" selected to delete has related ATS Objects; Un-relate to ATS first before deleting.");

      }
      return Result.TrueResult;
   }

}
