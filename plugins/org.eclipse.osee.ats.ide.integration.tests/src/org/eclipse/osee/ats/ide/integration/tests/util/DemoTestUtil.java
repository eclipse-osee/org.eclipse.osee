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

package org.eclipse.osee.ats.ide.integration.tests.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoActionableItems;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkType;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class DemoTestUtil {
   public static Map<DemoWorkType, IAtsTeamWorkflow> unCommittedWorkflows;
   public static Map<DemoWorkType, IAtsTeamWorkflow> committedWorkflows;
   public static TeamWorkFlowArtifact toolsTeamWorkflow;
   private static IAtsTeamWorkflow buttonSTeamWf, buttonWTeamWf;

   /**
    * Creates an action with the name title and demo code workflow
    */
   public static IAtsTeamWorkflow createSimpleAction(String title) {
      Set<IAtsActionableItem> aias = AtsApiService.get().getActionableItemService().getActionableItems(
         Arrays.asList(DemoActionableItems.SAW_Code.getName()));

      AtsApi atsApi = AtsApiService.get();
      NewActionData data = atsApi.getActionService() //
         .createActionData(title, title, "Description") //
         .andAis(aias).andChangeType(ChangeTypes.Improvement).andPriority("2");
      NewActionData newActionData = atsApi.getActionService().createAction(data);
      Conditions.assertTrue(newActionData.getRd().isSuccess(), newActionData.getRd().toString());

      IAtsTeamWorkflow teamWf = newActionData.getActResult().getAtsTeamWfs().iterator().next();
      return teamWf;
   }

   public static Set<IAtsActionableItem> getActionableItems(DemoActionableItems demoActionableItems) {
      return AtsApiService.get().getActionableItemService().getActionableItems(
         Arrays.asList(demoActionableItems.getName()));
   }

   public static IAtsActionableItem getActionableItem(DemoActionableItems demoActionableItems) {
      return getActionableItems(demoActionableItems).iterator().next();
   }

   /**
    * Create tasks named title + <num>
    */
   public static Collection<TaskArtifact> createSimpleTasks(TeamWorkFlowArtifact teamArt, String title, int numTasks,
      String relatedToState) throws Exception {
      List<String> names = new ArrayList<>();
      for (int x = 1; x < numTasks + 1; x++) {
         names.add(title + " " + x);
      }
      NewTaskData newTaskData =
         NewTaskData.create(teamArt, names, Arrays.asList(AtsApiService.get().getUserService().getCurrentUser()),
            new Date(), AtsApiService.get().getUserService().getCurrentUser(), relatedToState, null, null);
      NewTaskSet newTaskSet = NewTaskSet.create(newTaskData, "DemoTestUtil.creatSimpleTasks",
         AtsApiService.get().getUserService().getCurrentUserId());
      newTaskSet = AtsApiService.get().getTaskService().createTasks(newTaskSet);
      List<ArtifactToken> taskToks = new ArrayList<>();
      for (NewTaskData ntd : newTaskSet.getNewTaskDatas()) {
         for (JaxAtsTask task : ntd.getTasks()) {
            taskToks.add(task.getToken());
         }
      }
      Collection<? extends Artifact> arts = ArtifactQuery.reloadArtifacts(taskToks);
      return Collections.castAll(arts);
   }

   public static TeamWorkFlowArtifact getToolsTeamWorkflow() {
      if (toolsTeamWorkflow == null) {
         for (Artifact art : ArtifactQuery.getArtifactListFromName(
            DemoArtifactToken.ButtonSDoesntWorkOnHelp_TeamWf.getName(), AtsApiService.get().getAtsBranch())) {
            if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               toolsTeamWorkflow = (TeamWorkFlowArtifact) art;
            }
         }
      }
      return toolsTeamWorkflow;
   }

   public static IAtsTeamWorkflow getUncommittedActionWorkflow(DemoWorkType demoWorkType) {
      if (unCommittedWorkflows == null) {
         unCommittedWorkflows = new HashMap<>();
         for (IAtsTeamWorkflow teamWf : DemoUtil.getSawUnCommittedTeamWfs()) {
            if (teamWf.isOfType(AtsArtifactTypes.DemoCodeTeamWorkflow)) {
               unCommittedWorkflows.put(DemoWorkType.Code, teamWf);
            } else if (teamWf.isOfType(AtsArtifactTypes.DemoTestTeamWorkflow)) {
               unCommittedWorkflows.put(DemoWorkType.Test, teamWf);
            } else if (teamWf.isOfType(AtsArtifactTypes.DemoReqTeamWorkflow)) {
               unCommittedWorkflows.put(DemoWorkType.Requirements, teamWf);
            } else if (teamWf.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               unCommittedWorkflows.put(DemoWorkType.SW_Design, teamWf);
            }
         }
      }
      return unCommittedWorkflows.get(demoWorkType);
   }

   public static IAtsTeamWorkflow getCommittedActionWorkflow(DemoWorkType demoWorkType) {
      if (committedWorkflows == null) {
         committedWorkflows = new HashMap<>();
         for (IAtsTeamWorkflow teamWf : DemoUtil.getSawCommittedTeamWfs()) {
            if (teamWf.isOfType(AtsArtifactTypes.DemoCodeTeamWorkflow)) {
               committedWorkflows.put(DemoWorkType.Code, teamWf);
            } else if (teamWf.isOfType(AtsArtifactTypes.DemoTestTeamWorkflow)) {
               committedWorkflows.put(DemoWorkType.Test, teamWf);
            } else if (teamWf.isOfType(AtsArtifactTypes.DemoReqTeamWorkflow)) {
               committedWorkflows.put(DemoWorkType.Requirements, teamWf);
            } else if (teamWf.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               committedWorkflows.put(DemoWorkType.SW_Design, teamWf);
            }
         }
      }
      return committedWorkflows.get(demoWorkType);
   }

   public static IAtsTeamDefinition getTeamDef(ArtifactId artifactId) {
      IAtsTeamDefinition teamDef = null;
      // Add check to keep exception from occurring for OSEE developers running against production
      if (!ClientSessionManager.isProductionDataStore()) {
         try {
            teamDef = AtsApiService.get().getTeamDefinitionService().getTeamDefinitionById(artifactId);
         } catch (Exception ex) {
            OseeLog.log(DemoTestUtil.class, Level.SEVERE, ex);
         }
      }
      return teamDef;
   }

   public static void assertTypes(Collection<? extends Object> objects, int count, Class<?> clazz) {
      assertTypes(objects, count, clazz, "Expected %d; Found %d", count, numOfType(objects, clazz));
   }

   public static void assertTypes(Collection<? extends Object> objects, int count, Class<?> clazz, String message,
      Object... data) {
      int found = numOfType(objects, clazz);
      if (count != found) {
         throw new OseeStateException(message, data);
      }
   }

   public static int numOfType(Collection<? extends Object> objects, Class<?> clazz) {
      int num = 0;
      for (Object obj : objects) {
         if (clazz.isInstance(obj)) {
            num++;
         }
      }
      return num;
   }

   public static IAtsTeamWorkflow getButtonWTeamWf() {
      if (buttonWTeamWf == null) {
         buttonWTeamWf =
            AtsApiService.get().getQueryService().getTeamWf(DemoArtifactToken.ButtonWDoesntWorkOnSituationPage_TeamWf);
      }
      return buttonWTeamWf;
   }

   public static IAtsTeamWorkflow getButtonSTeamWf() {
      if (buttonSTeamWf == null) {
         buttonSTeamWf =
            AtsApiService.get().getQueryService().getTeamWf(DemoArtifactToken.ButtonSDoesntWorkOnHelp_TeamWf);
      }
      return buttonSTeamWf;
   }

}
