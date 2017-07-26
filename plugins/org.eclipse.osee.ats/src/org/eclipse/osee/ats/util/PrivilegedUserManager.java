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
package org.eclipse.osee.ats.util;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class PrivilegedUserManager {

   public static Set<IAtsUser> getPrivilegedUsers(AbstractWorkflowArtifact workflow) throws OseeCoreException {
      Set<IAtsUser> users = new HashSet<>();
      if (workflow.getParentTeamWorkflow() != null) {
         users.addAll(getPrivilegedUsers(workflow.getParentTeamWorkflow()));
      } else {
         for (IAtsActionableItem aia : AtsClientService.get().getWorkItemService().getActionableItemService().getActionableItems(
            workflow)) {
            addPrivilegedUsersUpTeamDefinitionTree(aia.getTeamDefinitionInherited(), users);
         }
      }
      AbstractWorkflowArtifact parentSma = workflow.getParentAWA();
      if (parentSma != null) {
         users.addAll(parentSma.getStateMgr().getAssignees());
      }
      if (AtsClientService.get().getUserService().isAtsAdmin()) {
         users.add(AtsClientService.get().getUserService().getCurrentUser());
      }
      return users;
   }

   public static Set<IAtsUser> getPrivilegedUsers(TeamWorkFlowArtifact teamArt) {
      Set<IAtsUser> users = new HashSet<>();
      try {
         addPrivilegedUsersUpTeamDefinitionTree(teamArt.getTeamDefinition(), users);

         IAtsStateDefinition stateDefinition = teamArt.getStateDefinition();

         // Add user if allowing privileged edit to all users
         if (!users.contains(AtsClientService.get().getUserService().getCurrentUser()) && (stateDefinition.hasRule(
            RuleDefinitionOption.AllowPrivilegedEditToAll.name()) || teamArt.getTeamDefinition().hasRule(
               RuleDefinitionOption.AllowPrivilegedEditToAll.name()))) {
            users.add(AtsClientService.get().getUserService().getCurrentUser());
         }

         // Add user if user is team member and rule exists
         boolean workPageToTeamMember =
            stateDefinition.hasRule(RuleDefinitionOption.AllowPrivilegedEditToTeamMember.name());
         boolean teamDefToTeamMember =
            teamArt.getTeamDefinition().hasRule(RuleDefinitionOption.AllowPrivilegedEditToTeamMember.name());
         if (!users.contains(
            AtsClientService.get().getUserService().getCurrentUser()) && (workPageToTeamMember || teamDefToTeamMember) && //
            teamArt.getTeamDefinition().getMembers().contains(
               AtsClientService.get().getUserService().getCurrentUser())) {
            users.add(AtsClientService.get().getUserService().getCurrentUser());
         }

         // Add user if team member is originator and rule exists
         boolean workPageToMemberAndOriginator =
            stateDefinition.hasRule(RuleDefinitionOption.AllowPrivilegedEditToTeamMemberAndOriginator.name());
         boolean teamDefToMemberAndOriginator = teamArt.getTeamDefinition().hasRule(
            RuleDefinitionOption.AllowPrivilegedEditToTeamMemberAndOriginator.name());
         if (!users.contains(
            AtsClientService.get().getUserService().getCurrentUser()) && (workPageToMemberAndOriginator || teamDefToMemberAndOriginator) && //
            teamArt.getCreatedBy().equals(
               AtsClientService.get().getUserService().getCurrentUser()) && teamArt.getTeamDefinition().getMembers().contains(
                  AtsClientService.get().getUserService().getCurrentUser())) {
            users.add(AtsClientService.get().getUserService().getCurrentUser());
         }

      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return users;
   }

   protected static void addPrivilegedUsersUpTeamDefinitionTree(IAtsTeamDefinition tda, Set<IAtsUser> users) throws OseeCoreException {
      users.addAll(tda.getLeads());
      users.addAll(tda.getPrivilegedMembers());

      // Walk up tree to get other editors
      if (tda.getParentTeamDef() != null) {
         addPrivilegedUsersUpTeamDefinitionTree(tda.getParentTeamDef(), users);
      }
   }

}
