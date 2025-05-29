/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.core.workflow.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.team.CreateOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionDatas;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * Creates new workflow under same action with same actionable items as give team workflow. Workflows start in normal
 * start state for that Team.
 *
 * @author Donald G. Dunne
 */
public class DuplicateWorkflowAtStartStateOperation extends AbstractDuplicateWorkflowOperation {

   public DuplicateWorkflowAtStartStateOperation(Collection<IAtsTeamWorkflow> teamWfs, String title, AtsUser asUser, AtsApi atsApi) {
      super(teamWfs, title, asUser, atsApi);
      Conditions.assertNotNullOrEmpty(title,
         "Title can not be null; This will be for error handling and commit comment.");
   }

   @Override
   public XResultData run() {
      XResultData results = validate();
      if (results.isErrors()) {
         return results;
      }

      NewActionDatas datas = new NewActionDatas(title, atsApi.user());
      for (IAtsTeamWorkflow teamWf : teamWfs) {

         // assignees == add in existing assignees, leads and originator (current user)
         List<AtsUser> assignees = new LinkedList<>();
         assignees.addAll(teamWf.getAssignees());
         IAtsTeamDefinition teamDef = teamWf.getTeamDefinition();
         assignees.addAll(AtsApiService.get().getTeamDefinitionService().getLeads(teamDef));
         AtsUser user = atsApi.getUserService().getCurrentUser();
         if (!assignees.contains(user)) {
            assignees.add(user);
         }

         String newTitle = getTitle(teamWf);
         NewActionData data = atsApi.getActionService() //
            .createTeamWfData(title, teamWf.getParentAction(), teamDef) //
            .andAis(teamWf.getActionableItems()) //
            .andTitle(newTitle) //
            .andAssignees(assignees) //
            .andCreateOption(CreateOption.Duplicate_If_Exists);
         datas.add(data);

      }

      NewActionDatas newDatas = atsApi.getActionService().createActions(datas);
      results.merge(newDatas.getRd());

      return results;
   }

}