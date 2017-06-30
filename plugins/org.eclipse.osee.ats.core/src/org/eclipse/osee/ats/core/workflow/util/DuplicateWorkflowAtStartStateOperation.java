/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow.util;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.team.CreateTeamOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * Creates new workflow under same action with same actionable items as give team workflow. Workflows start in normal
 * start state for that Team.
 *
 * @author Donald G. Dunne
 */
public class DuplicateWorkflowAtStartStateOperation extends AbstractDuplicateWorkflowOperation {

   public DuplicateWorkflowAtStartStateOperation(Collection<IAtsTeamWorkflow> teamWfs, String title, IAtsUser asUser, IAtsServices services) {
      super(teamWfs, title, asUser, services);
   }

   @Override
   public XResultData run() throws OseeCoreException {
      XResultData results = validate();
      if (results.isErrors()) {
         return results;
      }
      oldToNewMap = new HashMap<>();

      IAtsChangeSet changes =
         services.getStoreService().createAtsChangeSet("Duplicate Workflow - At Start State", asUser);

      Date createdDate = new Date();
      IAtsUser createdBy = services.getUserService().getCurrentUser();
      for (IAtsTeamWorkflow teamWf : teamWfs) {

         // assignees == add in existing assignees, leads and originator (current user)
         List<IAtsUser> assignees = new LinkedList<>();
         assignees.addAll(teamWf.getStateMgr().getAssignees());
         IAtsTeamDefinition teamDef = teamWf.getTeamDefinition();
         assignees.addAll(teamDef.getLeads());
         IAtsUser user = services.getUserService().getCurrentUser();
         if (!assignees.contains(user)) {
            assignees.add(user);
         }

         IAtsTeamWorkflow newTeamWf = services.getActionFactory().createTeamWorkflow(teamWf.getParentAction(), teamDef,
            teamWf.getActionableItems(), assignees, changes, createdDate, createdBy, null,
            CreateTeamOption.Duplicate_If_Exists);

         String title = getTitle(teamWf);
         changes.setSoleAttributeValue(newTeamWf, CoreAttributeTypes.Name, title);
         changes.add(newTeamWf);
         oldToNewMap.put(teamWf, newTeamWf);
      }
      changes.execute();
      return results;
   }

}