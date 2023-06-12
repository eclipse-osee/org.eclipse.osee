/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.core.workflow.hooks;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;

/**
 * Contributed via AtsWorkItemServiceImpl
 *
 * @author Donald G. Dunne
 */
public class AtsForceAssigneesToTeamLeadsWorkItemHook implements IAtsTransitionHook {

   public String getName() {
      return AtsForceAssigneesToTeamLeadsWorkItemHook.class.getSimpleName();
   }

   @Override
   public String getDescription() {
      return "Check if toState is configured to force assignees to leads and set leads accordingly.";
   }

   @Override
   public void transitioned(IAtsWorkItem workItem, IStateToken fromState, IStateToken toState,
      Collection<AtsUser> toAssignees, AtsUser asUser, IAtsChangeSet changes, AtsApi atsApi) {
      if (workItem.isTeamWorkflow() && isForceAssigneesToTeamLeads(
         atsApi.getWorkDefinitionService().getStateDefinitionByName(workItem, toState.getName()))) {
         Collection<AtsUser> teamLeads =
            atsApi.getTeamDefinitionService().getLeads(((IAtsTeamWorkflow) workItem).getTeamDefinition());
         if (!teamLeads.isEmpty()) {
            workItem.getStateMgr().setAssignees(teamLeads);
            changes.add(workItem);
         }
      }
   }

   private boolean isForceAssigneesToTeamLeads(StateDefinition stateDefinition) {
      return stateDefinition.hasRule(RuleDefinitionOption.ForceAssigneesToTeamLeads.name());
   }

}
