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
package org.eclipse.osee.ats.client.demo.navigate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.task.JaxAtsTaskFactory;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskDataFactory;
import org.eclipse.osee.ats.api.task.NewTaskDatas;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.artifact.GoalManager;
import org.eclipse.osee.ats.client.demo.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class CreateGoalTestDemoArtifacts extends XNavigateItemAction {
   private Date createdDate;
   private IAtsUser createdBy;

   public CreateGoalTestDemoArtifacts(XNavigateItem parent) {
      super(parent, "Create Test Goal Artifacts - Demo", AtsImage.GOAL);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (AtsClientService.get().getStoreService().isProductionDb()) {
         AWorkbench.popup("Can't be run on production");
         return;
      }
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(), getName())) {
         return;
      }
      createdDate = new Date();
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getName());
      createdBy = AtsClientService.get().getUserService().getCurrentUser();
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

      NewTaskData newTaskData = NewTaskDataFactory.get(getName(), createdBy, teamArt);

      for (String name : Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
         "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "BB", "CC", "DD", "EE", "FF", "GG", "HH", "II", "JJ",
         "KK", "LL", "MM", "NN", "OO", "PP", "QQ", "RR")) {
         JaxAtsTaskFactory.get(newTaskData, "Task " + name, createdBy, createdDate);
      }

      Collection<IAtsTask> createTasks =
         AtsClientService.get().getTaskService().createTasks(new NewTaskDatas(newTaskData));

      changes = AtsClientService.get().createChangeSet(getName());
      for (IAtsTask task : createTasks) {
         Artifact taskArt = AtsClientService.get().getArtifact(task);
         toolsTeamGoal.addMember(taskArt);
         changes.relate(toolsTeamGoal, AtsRelationTypes.Goal_Member, taskArt);
      }
      changes.execute();

      WorldEditor.open(new WorldEditorSimpleProvider("Goals",
         Arrays.asList(sawCodeGoal, sawTestGoal, toolsTeamGoal, facilitiesGoal, cisReqGoal)));

   }

   private void createAction7(IAtsChangeSet changes, GoalArtifact facilitiesGoal) {
      ActionResult action = AtsClientService.get().getActionFactory().createAction(null, "Add the Improvement",
         "Description", ChangeType.Improvement, "4", false, null,
         ActionableItems.getActionableItems(Arrays.asList("Network"), AtsClientService.get().getServices()),
         createdDate, createdBy, null, changes);
      facilitiesGoal.addMember(action.getActionArt());
      changes.add(facilitiesGoal);
   }

   private IAtsTeamWorkflow createAction456(GoalArtifact sawCodeGoal, GoalArtifact facilitiesGoal, IAtsTeamWorkflow teamArt) {
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getName());
      NewTaskDatas newTaskDatas = new NewTaskDatas();
      for (String msaTool : Arrays.asList("Backups", "Computers", "Network")) {
         ActionResult action = AtsClientService.get().getActionFactory().createAction(null,
            "Fix " + msaTool + " button", "Description", ChangeType.Problem, "4", false, null,
            ActionableItems.getActionableItems(Arrays.asList(msaTool), AtsClientService.get().getServices()),
            createdDate, createdBy, null, changes);
         facilitiesGoal.addMember(AtsClientService.get().getWorkItemService().getFirstTeam(action).getStoreObject());
         teamArt = AtsClientService.get().getWorkItemService().getFirstTeam(action);
         NewTaskData newTaskData = NewTaskDataFactory.get("createAction456", createdBy, teamArt);
         newTaskDatas.add(newTaskData);
         JaxAtsTaskFactory.get(newTaskData, "Task 1", createdBy, createdDate);
         JaxAtsTaskFactory.get(newTaskData, "Task 2", createdBy, createdDate);
      }
      changes.add(facilitiesGoal);
      changes.add(sawCodeGoal);
      changes.execute();

      Collection<IAtsTask> createTasks = AtsClientService.get().getTaskService().createTasks(newTaskDatas);

      changes = AtsClientService.get().createChangeSet(getName());
      for (IAtsTask task : createTasks) {
         facilitiesGoal.addMember(task.getStoreObject());
         sawCodeGoal.addMember(task.getStoreObject());
         changes.add(task);
      }
      changes.execute();

      return teamArt;
   }

   private void createAction3(IAtsChangeSet changes, GoalArtifact sawCodeGoal, GoalArtifact cisReqGoal) {
      ActionResult action = AtsClientService.get().getActionFactory().createAction(null, "Remove Workflow button",
         "Description", ChangeType.Problem, "4", false, null,
         ActionableItems.getActionableItems(Arrays.asList("SAW Code", "CIS Requirements"),
            AtsClientService.get().getServices()),
         createdDate, createdBy, null, changes);
      sawCodeGoal.addMember(AtsClientService.get().getWorkItemService().getFirstTeam(action).getStoreObject());
      cisReqGoal.addMember(AtsClientService.get().getWorkItemService().getFirstTeam(action).getStoreObject());
   }

   private void createAction2(IAtsChangeSet changes, GoalArtifact sawCodeGoal, GoalArtifact cisReqGoal) {
      ActionResult action = AtsClientService.get().getActionFactory().createAction(null, "Add CDB Check Signals",
         "Description", ChangeType.Problem, "4", false, null,
         ActionableItems.getActionableItems(Arrays.asList("SAW Code", "CIS Requirements"),
            AtsClientService.get().getServices()),
         createdDate, createdBy, null, changes);
      sawCodeGoal.addMember(AtsClientService.get().getWorkItemService().getFirstTeam(action).getStoreObject());
      cisReqGoal.addMember(AtsClientService.get().getWorkItemService().getFirstTeam(action).getStoreObject());
   }

   private IAtsTeamWorkflow createAction1(IAtsChangeSet changes, GoalArtifact sawCodeGoal) {
      ActionResult action = AtsClientService.get().getActionFactory().createAction(null, "Fix this model",
         "Description", ChangeType.Problem, "2", false, null,
         ActionableItems.getActionableItems(Arrays.asList("SAW Code"), AtsClientService.get().getServices()),
         createdDate, createdBy, null, changes);
      sawCodeGoal.addMember(AtsClientService.get().getWorkItemService().getFirstTeam(action).getStoreObject());
      IAtsTeamWorkflow teamWf = AtsClientService.get().getWorkItemService().getFirstTeam(action);
      PeerToPeerReviewArtifact peerReviewArt = PeerToPeerReviewManager.createNewPeerToPeerReview(
         (TeamWorkFlowArtifact) teamWf.getStoreObject(), "New Review", "Implement", changes);
      sawCodeGoal.addMember(peerReviewArt);
      return teamWf;
   }
}
