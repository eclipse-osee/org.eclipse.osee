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
package org.eclipse.osee.ats.core.workflow.hooks;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.core.internal.AtsApiService;

/**
 * Contributed via AtsWorkItemServiceImpl
 *
 * @author Donald G. Dunne
 */
public class AtsForceAssigneesToTeamLeadsWorkflowHook implements IAtsTransitionHook {

   public String getName() {
      return AtsForceAssigneesToTeamLeadsWorkflowHook.class.getSimpleName();
   }

   @Override
   public String getDescription() {
      return "Check if toState is configured to force assignees to leads and set leads accordingly.";
   }

   @Override
   public void transitioned(IAtsWorkItem workItem, IStateToken fromState, IStateToken toState, Collection<? extends IAtsUser> toAssignees, IAtsChangeSet changes) {
      if (workItem instanceof IAtsTeamWorkflow && isForceAssigneesToTeamLeads(
         AtsApiService.get().getWorkDefinitionService().getStateDefinitionByName(workItem, toState.getName()))) {
         Collection<IAtsUser> teamLeads =
            AtsApiService.get().getTeamDefinitionService().getLeads(((IAtsTeamWorkflow) workItem).getTeamDefinition());
         ;
         if (!teamLeads.isEmpty()) {
            workItem.getStateMgr().setAssignees(teamLeads);
            changes.add(workItem);
         }
      }
   }

   private boolean isForceAssigneesToTeamLeads(IAtsStateDefinition stateDefinition) {
      return stateDefinition.hasRule(RuleDefinitionOption.ForceAssigneesToTeamLeads.name());
   }

}
