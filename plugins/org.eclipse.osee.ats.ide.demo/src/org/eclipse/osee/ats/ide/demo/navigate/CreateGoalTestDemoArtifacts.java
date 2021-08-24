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

package org.eclipse.osee.ats.ide.demo.navigate;

import java.util.Arrays;
import java.util.Date;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.goal.GoalManager;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class CreateGoalTestDemoArtifacts extends XNavigateItemAction {
   private Date createdDate;
   private AtsUser createdBy;

   public CreateGoalTestDemoArtifacts(XNavItemCat xNavItemCat) {
      super("Create Test Goal Artifacts - Demo", AtsImage.GOAL, xNavItemCat);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (AtsApiService.get().getStoreService().isProductionDb()) {
         AWorkbench.popup("Can't be run on production");
         return;
      }
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(), getName())) {
         return;
      }
      createdDate = new Date();
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getName());
      createdBy = AtsApiService.get().getUserService().getCurrentUser();
      GoalArtifact sawCodeGoal = GoalManager.createGoal("SAW Code", changes);
      GoalArtifact sawTestGoal = GoalManager.createGoal("SAW Test", changes);
      GoalArtifact toolsTeamGoal = GoalManager.createGoal("Tools Team", changes);
      GoalArtifact facilitiesGoal = GoalManager.createGoal("Facilities Team", changes);
      GoalArtifact cisReqGoal = GoalManager.createGoal("CIS Requirements", changes);

      IAtsTeamWorkflow teamArt = createAction1(changes, sawCodeGoal);
      createAction2(changes, sawCodeGoal, cisReqGoal);
      createAction3(changes, sawTestGoal, cisReqGoal);
      createAction7(changes, facilitiesGoal);
      changes.execute();

      teamArt = createAction456(sawCodeGoal, facilitiesGoal, teamArt);

      NewTaskSet newTaskSet = NewTaskSet.createWithData(teamArt, getName(), createdBy);
      NewTaskData newTaskData = newTaskSet.getTaskData();

      for (String name : Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
         "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "BB", "CC", "DD", "EE", "FF", "GG", "HH", "II", "JJ",
         "KK", "LL", "MM", "NN", "OO", "PP", "QQ", "RR")) {
         JaxAtsTask.create(newTaskData, "Task " + name, createdBy, createdDate);
      }

      newTaskSet = AtsApiService.get().getTaskService().createTasks(
         NewTaskSet.create(newTaskData, "CreateGoalTestDemoArtifacts", DemoUsers.Joe_Smith));

      changes = AtsApiService.get().createChangeSet(getName());
      for (JaxAtsTask task : newTaskSet.getNewTaskDatas().iterator().next().getTasks()) {
         Artifact taskArt = AtsApiService.get().getQueryServiceIde().getArtifact(task);
         toolsTeamGoal.addMember(taskArt);
         changes.relate(toolsTeamGoal, AtsRelationTypes.Goal_Member, taskArt);
      }
      changes.execute();

      WorldEditor.open(new WorldEditorSimpleProvider("Goals",
         Arrays.asList(sawCodeGoal, sawTestGoal, toolsTeamGoal, facilitiesGoal, cisReqGoal)));

   }

   private void createAction7(IAtsChangeSet changes, GoalArtifact facilitiesGoal) {
      ActionResult action = AtsApiService.get().getActionService().createAction(null, "Add the Improvement",
         "Description", ChangeType.Improvement, "4", false, null,
         AtsApiService.get().getActionableItemService().getActionableItems(Arrays.asList("Network")), createdDate,
         createdBy, null, changes);
      facilitiesGoal.addMember(action.getActionArt());
      changes.add(facilitiesGoal);
   }

   private IAtsTeamWorkflow createAction456(GoalArtifact sawCodeGoal, GoalArtifact facilitiesGoal, IAtsTeamWorkflow teamArt) {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getName());
      NewTaskSet newTaskSet = null;
      for (String msaTool : Arrays.asList("Backups", "Computers", "Network")) {
         ActionResult action = AtsApiService.get().getActionService().createAction(null, "Fix " + msaTool + " button",
            "Description", ChangeType.Problem, "4", false, null,
            AtsApiService.get().getActionableItemService().getActionableItems(Arrays.asList(msaTool)), createdDate,
            createdBy, null, changes);
         facilitiesGoal.addMember(AtsApiService.get().getWorkItemService().getFirstTeam(action).getStoreObject());
         teamArt = AtsApiService.get().getWorkItemService().getFirstTeam(action);
         newTaskSet = NewTaskSet.createWithData(teamArt, "createAction456", createdBy);
         NewTaskData newTaskData = newTaskSet.getTaskData();
         JaxAtsTask.create(newTaskData, "Task 1", createdBy, createdDate);
         JaxAtsTask.create(newTaskData, "Task 2", createdBy, createdDate);
      }
      changes.add(facilitiesGoal);
      changes.add(sawCodeGoal);
      changes.execute();

      newTaskSet = AtsApiService.get().getTaskService().createTasks(newTaskSet);

      changes = AtsApiService.get().createChangeSet(getName());
      for (NewTaskData ntd : newTaskSet.getNewTaskDatas()) {
         for (JaxAtsTask task : ntd.getTasks()) {
            facilitiesGoal.addMember(task.getStoreObject());
            sawCodeGoal.addMember(task.getStoreObject());
            changes.add(task);
         }
      }
      changes.execute();

      return teamArt;
   }

   private void createAction3(IAtsChangeSet changes, GoalArtifact sawCodeGoal, GoalArtifact cisReqGoal) {
      ActionResult action =
         AtsApiService.get().getActionService().createAction(null, "Remove Workflow button", "Description",
            ChangeType.Problem, "4", false, null, AtsApiService.get().getActionableItemService().getActionableItems(
               Arrays.asList("SAW Code", "CIS Requirements")),
            createdDate, createdBy, null, changes);
      sawCodeGoal.addMember(AtsApiService.get().getWorkItemService().getFirstTeam(action).getStoreObject());
      cisReqGoal.addMember(AtsApiService.get().getWorkItemService().getFirstTeam(action).getStoreObject());
   }

   private void createAction2(IAtsChangeSet changes, GoalArtifact sawCodeGoal, GoalArtifact cisReqGoal) {
      ActionResult action =
         AtsApiService.get().getActionService().createAction(null, "Add CDB Check Signals", "Description",
            ChangeType.Problem, "4", false, null, AtsApiService.get().getActionableItemService().getActionableItems(
               Arrays.asList("SAW Code", "CIS Requirements")),
            createdDate, createdBy, null, changes);
      sawCodeGoal.addMember(AtsApiService.get().getWorkItemService().getFirstTeam(action).getStoreObject());
      cisReqGoal.addMember(AtsApiService.get().getWorkItemService().getFirstTeam(action).getStoreObject());
   }

   private IAtsTeamWorkflow createAction1(IAtsChangeSet changes, GoalArtifact sawCodeGoal) {
      ActionResult action = AtsApiService.get().getActionService().createAction(null, "Fix this model", "Description",
         ChangeType.Problem, "2", false, null,
         AtsApiService.get().getActionableItemService().getActionableItems(Arrays.asList("SAW Code")), createdDate,
         createdBy, null, changes);
      sawCodeGoal.addMember(AtsApiService.get().getWorkItemService().getFirstTeam(action).getStoreObject());
      IAtsTeamWorkflow teamWf = AtsApiService.get().getWorkItemService().getFirstTeam(action);
      IAtsPeerToPeerReview peerReviewArt = AtsApiService.get().getReviewService().createNewPeerToPeerReview(
         (TeamWorkFlowArtifact) teamWf.getStoreObject(), "New Review", "Implement", changes);
      sawCodeGoal.addMember(peerReviewArt.getStoreObject());
      return teamWf;
   }
}
