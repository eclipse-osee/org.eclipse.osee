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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.team.CreateTeamOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * Creates new workflow under same action with same actionable items as give team workflow. Workflows start in normal
 * start state for that Team.
 *
 * @author Donald G. Dunne
 */
public class DuplicateWorkflowAtStartStateOperation extends AbstractDuplicateWorkflowOperation {

   public DuplicateWorkflowAtStartStateOperation(Collection<IAtsTeamWorkflow> teamWfs, String title, AtsUser asUser, AtsApi atsApi) {
      super(teamWfs, title, asUser, atsApi);
   }

   @Override
   public XResultData run() {
      XResultData results = validate();
      if (results.isErrors()) {
         return results;
      }
      oldToNewMap = new HashMap<>();

      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet("Duplicate Workflow - At Start State", asUser);

      Date createdDate = new Date();
      AtsUser createdBy = atsApi.getUserService().getCurrentUser();
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

         IAtsTeamWorkflow newTeamWf =
            atsApi.getActionService().createTeamWorkflow(teamWf.getParentAction(), teamDef, teamWf.getActionableItems(),
               assignees, changes, createdDate, createdBy, null, CreateTeamOption.Duplicate_If_Exists);

         String title = getTitle(teamWf);
         changes.setSoleAttributeValue(newTeamWf, CoreAttributeTypes.Name, title);
         changes.add(newTeamWf);
         oldToNewMap.put(teamWf, newTeamWf);
      }
      changes.execute();
      return results;
   }

}