/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow.cr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.CreateOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.cr.TaskEstDefinition;
import org.eclipse.osee.ats.api.workflow.cr.TaskEstUtil;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class CreateSiblingOffTaskEstOperation {

   private final IAtsTeamWorkflow teamWf;
   private final AtsApi atsApi;
   private final Collection<TaskEstDefinition> taskEstDefs;
   private boolean debug = false;

   public CreateSiblingOffTaskEstOperation(IAtsTeamWorkflow teamWf, Collection<TaskEstDefinition> taskEstDefs) {
      this.teamWf = teamWf;
      this.taskEstDefs = taskEstDefs;
      this.atsApi = AtsApiService.get();
   }

   // Need to get TaskEstDefs and reduce those to figure out what wfs to create
   public XResultData run() {
      XResultData rd = new XResultData();
      try {

         List<TaskEstDefinition> teds = new ArrayList<>();
         teds.addAll(taskEstDefs);

         // if TED and no task, do nothing
         for (TaskEstDefinition ted : new CopyOnWriteArrayList<>(teds)) {

            if (!TaskEstUtil.hasTask(teamWf, ted, atsApi)) {
               teds.remove(ted);
               if (isDebug()) {
                  rd.logf("Task Est Def [%s] has no estimating task\n", ted.toStringWithId());
               }
            }

            // if TED and task and estimate <= 0, do nothing
            IAtsTask task = TaskEstUtil.getTask(teamWf, ted, atsApi);
            if (task.isValid()) {
               if (task.isCancelled()) {
                  teds.remove(ted);
                  continue;
               }
               String ptsStr = atsApi.getAgileService().getPointsStr(task);
               if (!Strings.isValid(ptsStr)) {
                  rd.errorf("No estimated points for task %s\n", task.toStringWithId());
                  teds.remove(ted);
                  continue;
               } else if (!Strings.isNumeric(ptsStr)) {
                  rd.errorf("Estimated points non-numeric for task %s\n", task.toStringWithId());
                  teds.remove(ted);
                  continue;
               } else {
                  Double pts = Double.valueOf(ptsStr);
                  if (pts > 0) {
                     if (TaskEstUtil.hasWorkflow(teamWf, ted, atsApi)) {
                        if (isDebug()) {
                           rd.logf("Workflow exists for task %s; skipping\n", task.toStringWithId());
                        }
                        teds.remove(ted);
                     }
                  } else {
                     if (isDebug()) {
                        rd.logf("Estimate 0 for task %s; skipping\n", task.toStringWithId());
                     }
                     teds.remove(ted);
                  }
               }
            }
         }

         // Validate AI for TED
         for (TaskEstDefinition ted : new CopyOnWriteArrayList<>(teds)) {
            ArtifactToken aiTok = ted.getActionableItem();
            if (aiTok == null || aiTok.isInvalid()) {
               rd.errorf("Configured AI invalid for Task Def [%s]\n", ted.toStringWithId());
               teds.remove(ted);
            }
         }

         // Ensure remaining teds to create
         if (teds.isEmpty()) {
            rd.errorf("No Workflows To Create\n");
            return rd;
         }

         // Leftover TEDs need workflow
         createWorkflowsOfTeds(teds, rd);

      } catch (Exception ex) {
         rd.error("Error Creating Sibling workflows " + Lib.exceptionToString(ex));
      }
      return rd;
   }

   private void createWorkflowsOfTeds(List<TaskEstDefinition> teds, XResultData rd) {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Create Workflows off CR");
      Date createdDate = new Date();
      List<IAtsTeamWorkflow> workflows = new ArrayList<>();

      for (TaskEstDefinition ted : teds) {
         IAtsTask task = TaskEstUtil.getTask(teamWf, ted, atsApi);
         ArtifactToken aiTok = ted.getActionableItem();
         IAtsActionableItem ai = atsApi.getActionableItemService().getActionableItemById(aiTok);
         IAtsTeamDefinition teamDef = atsApi.getActionableItemService().getTeamDefinitionInherited(ai);
         Collection<AtsUser> assignees = task.getImplementers();
         IAtsTeamWorkflow newTeamWf = createTaskEstSiblingWorkflow(rd, changes, createdDate, task, ai, teamDef,
            assignees, teamWf.getParentAction(), atsApi);
         workflows.add(newTeamWf);
      }

      changes.executeIfNeeded();
   }

   public static IAtsTeamWorkflow createTaskEstSiblingWorkflow(XResultData rd, IAtsChangeSet changes, Date createdDate,
      IAtsTask task, //
      IAtsActionableItem ai, IAtsTeamDefinition teamDef, Collection<AtsUser> assignees, IAtsAction action,
      AtsApi atsApi) {

      NewActionData data = atsApi.getActionService() //
         .createTeamWfData(changes.getComment(), action, teamDef) //
         .andAssignees(assignees) //
         .andCreateOption(CreateOption.Duplicate_If_Exists);
      NewActionData newData = atsApi.getActionService().createAction(data);
      rd.merge(newData.getRd());
      if (rd.isErrors()) {
         return null;
      }
      IAtsTeamWorkflow teamWf = newData.getActResult().getAtsTeamWfs().iterator().next();

      rd.logf("Created new Team Workflow for task %s\n", task.toStringWithId());

      AttributeTypeToken pointsAttrType = atsApi.getAgileService().getPointsAttrType(teamWf);
      String ptsStr = atsApi.getAttributeResolver().getSoleAttributeValueAsString(task, pointsAttrType, "");

      if (pointsAttrType.isDouble()) {
         changes.setSoleAttributeValue(teamWf, pointsAttrType, Double.valueOf(ptsStr));
      } else {
         changes.setSoleAttributeValue(teamWf, pointsAttrType, ptsStr);
      }

      changes.relate(task, AtsRelationTypes.Derive_To, teamWf);

      changes.addAttribute(task, CoreAttributeTypes.StaticId, TaskEstUtil.TASK_EST_STATIC_ID);
      changes.addAttribute(teamWf, CoreAttributeTypes.StaticId, TaskEstUtil.TASK_EST_STATIC_ID);
      return teamWf;
   }

   public boolean isDebug() {
      return debug;
   }

   public void setDebug(boolean debug) {
      this.debug = debug;
   }

}
