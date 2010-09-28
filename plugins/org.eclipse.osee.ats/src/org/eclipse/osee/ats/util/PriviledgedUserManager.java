/*
 * Created on Sep 28, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions.RuleWorkItemId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

public class PriviledgedUserManager {

   public static Set<User> getPrivilegedUsers(AbstractWorkflowArtifact workflow) throws OseeCoreException {
      Set<User> users = new HashSet<User>();
      if (workflow.getParentTeamWorkflow() != null) {
         users.addAll(getPrivilegedUsers(workflow.getParentTeamWorkflow()));
      } else {
         for (ActionableItemArtifact aia : workflow.getParentTeamWorkflow().getActionableItemsDam().getActionableItems()) {
            for (TeamDefinitionArtifact teamDef : aia.getImpactedTeamDefs()) {
               addPriviledgedUsersUpTeamDefinitionTree(teamDef, users);
            }
         }
      }
      AbstractWorkflowArtifact parentSma = workflow.getParentSMA();
      if (parentSma != null) {
         users.addAll(parentSma.getStateMgr().getAssignees());
      }
      if (AtsUtil.isAtsAdmin()) {
         users.add(UserManager.getUser());
      }
      return users;
   }

   public static Set<User> getPrivilegedUsers(TeamWorkFlowArtifact teamArt) {
      Set<User> users = new HashSet<User>();
      try {
         addPriviledgedUsersUpTeamDefinitionTree(teamArt.getTeamDefinition(), users);

         WorkPageDefinition workPageDefinition = teamArt.getWorkPageDefinition();

         // Add user if allowing privileged edit to all users
         if (!users.contains(UserManager.getUser()) && (workPageDefinition.hasWorkRule(RuleWorkItemId.atsAllowPriviledgedEditToAll.name()) || teamArt.getTeamDefinition().hasWorkRule(
            RuleWorkItemId.atsAllowPriviledgedEditToAll.name()))) {
            users.add(UserManager.getUser());
         }

         // Add user if user is team member and rule exists
         if (!users.contains(UserManager.getUser()) && (workPageDefinition.hasWorkRule(RuleWorkItemId.atsAllowPriviledgedEditToTeamMember.name()) || teamArt.getTeamDefinition().hasWorkRule(
            RuleWorkItemId.atsAllowPriviledgedEditToTeamMember.name()))) {
            if (teamArt.getTeamDefinition().getMembers().contains(UserManager.getUser())) {
               users.add(UserManager.getUser());
            }
         }

         // Add user if team member is originator and rule exists
         if (!users.contains(UserManager.getUser()) && (workPageDefinition.hasWorkRule(RuleWorkItemId.atsAllowPriviledgedEditToTeamMemberAndOriginator.name()) || teamArt.getTeamDefinition().hasWorkRule(
            RuleWorkItemId.atsAllowPriviledgedEditToTeamMemberAndOriginator.name()))) {
            if (teamArt.getOriginator().equals(UserManager.getUser()) && teamArt.getTeamDefinition().getMembers().contains(
               UserManager.getUser())) {
               users.add(UserManager.getUser());
            }
         }

      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return users;
   }

   protected static void addPriviledgedUsersUpTeamDefinitionTree(TeamDefinitionArtifact tda, Set<User> users) throws OseeCoreException {
      users.addAll(tda.getLeads());
      users.addAll(tda.getPrivilegedMembers());

      // Walk up tree to get other editors
      if (tda.getParent() instanceof TeamDefinitionArtifact) {
         addPriviledgedUsersUpTeamDefinitionTree((TeamDefinitionArtifact) tda.getParent(), users);
      }
   }

}
