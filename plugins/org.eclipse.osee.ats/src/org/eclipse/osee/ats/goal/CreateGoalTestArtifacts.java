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

package org.eclipse.osee.ats.goal;

import java.util.Arrays;
import java.util.Date;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.artifact.ActionManager;
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.artifact.GoalManager;
import org.eclipse.osee.ats.core.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.review.ReviewManager;
import org.eclipse.osee.ats.core.task.TaskArtifact;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workflow.ActionArtifact;
import org.eclipse.osee.ats.core.workflow.ActionableItemManagerCore;
import org.eclipse.osee.ats.core.workflow.ChangeType;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class CreateGoalTestArtifacts extends XNavigateItemAction {
   private Date createdDate;
   private User createdBy;

   public CreateGoalTestArtifacts(XNavigateItem parent) {
      super(parent, "Create Test Goal Artifacts", AtsImage.GOAL);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      if (AtsUtil.isProductionDb()) {
         AWorkbench.popup("Can't be run on production");
         return;
      }
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(), getName())) {
         return;
      }
      createdDate = new Date();
      createdBy = UserManager.getUser();
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), getName());
      GoalArtifact oteGoal = GoalManager.createGoal("OTE");
      GoalArtifact atsGoal = GoalManager.createGoal("ATS");
      GoalArtifact defineGoal = GoalManager.createGoal("Define");
      GoalArtifact msaGoal = GoalManager.createGoal("MSA Tools");
      GoalArtifact cdbGoal = GoalManager.createGoal("Integrate CDB signal diff into OSEE");

      TeamWorkFlowArtifact teamArt = createAction1(transaction, oteGoal);

      createAction2(transaction, oteGoal, cdbGoal);

      createAction3(transaction, atsGoal, cdbGoal);

      teamArt = createAction456(transaction, oteGoal, msaGoal, teamArt);

      createAction7(transaction, msaGoal);

      for (String name : Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
         "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "BB", "CC", "DD", "EE", "FF", "GG", "HH", "II", "JJ",
         "KK", "LL", "MM", "NN", "OO", "PP", "QQ", "RR")) {
         TaskArtifact taskArt = teamArt.createNewTask("Task " + name, createdDate, createdBy);
         defineGoal.addMember(taskArt);
         taskArt.persist(transaction);
      }

      defineGoal.persist(transaction);

      transaction.execute();
      WorldEditor.open(new WorldEditorSimpleProvider("Goals", Arrays.asList(oteGoal, atsGoal, defineGoal, msaGoal,
         cdbGoal)));

   }

   private void createAction7(SkynetTransaction transaction, GoalArtifact msaGoal) throws OseeCoreException {
      Artifact action =
         ActionManager.createAction(null, "Add the Improvement", "Description", ChangeType.Improvement, "4", false,
            null, ActionableItemManagerCore.getActionableItems(Arrays.asList("REQ")), createdDate, createdBy, null,
            transaction);
      action.persist(transaction);
      msaGoal.addMember(action);
      msaGoal.persist(transaction);
   }

   private TeamWorkFlowArtifact createAction456(SkynetTransaction transaction, GoalArtifact oteGoal, GoalArtifact msaGoal, TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      for (String msaTool : Arrays.asList("TRAX", "REQ", "RCS")) {
         Artifact action =
            ActionManager.createAction(null, "Fix " + msaTool + " button", "Description", ChangeType.Problem, "4",
               false, null, ActionableItemManagerCore.getActionableItems(Arrays.asList(msaTool)), createdDate,
               createdBy, null, transaction);
         action.persist(transaction);
         msaGoal.addMember(ActionManager.getFirstTeam(action));
         teamArt = ActionManager.getFirstTeam(action);
         TaskArtifact taskArt = teamArt.createNewTask("Task 1", createdDate, createdBy);
         oteGoal.addMember(taskArt);
         taskArt.persist(transaction);
         taskArt = teamArt.createNewTask("Task 2", createdDate, createdBy);
         msaGoal.addMember(taskArt);
         taskArt.persist(transaction);
      }
      return teamArt;
   }

   private void createAction3(SkynetTransaction transaction, GoalArtifact atsGoal, GoalArtifact cdbGoal) throws OseeCoreException {
      Artifact action =
         ActionManager.createAction(null, "Remove Workflow button", "Description", ChangeType.Problem, "4", false,
            null, ActionableItemManagerCore.getActionableItems(Arrays.asList("ATS", "CDB")), createdDate, createdBy,
            null, transaction);
      action.persist(transaction);
      atsGoal.addMember(ActionManager.getFirstTeam(action));
      cdbGoal.addMember(ActionManager.getFirstTeam(action));
      ActionManager.getFirstTeam(action).persist(transaction);
   }

   private void createAction2(SkynetTransaction transaction, GoalArtifact oteGoal, GoalArtifact cdbGoal) throws OseeCoreException {
      ActionArtifact action =
         ActionManager.createAction(null, "Add CDB Check Signals", "Description", ChangeType.Problem, "4", false, null,
            ActionableItemManagerCore.getActionableItems(Arrays.asList("OTE_SW", "CDB")), createdDate, createdBy, null,
            transaction);
      action.persist(transaction);
      oteGoal.addMember(ActionManager.getFirstTeam(action));
      cdbGoal.addMember(ActionManager.getFirstTeam(action));
      for (TeamWorkFlowArtifact teamArt2 : action.getTeams()) {
         teamArt2.persist(transaction);
      }
   }

   private TeamWorkFlowArtifact createAction1(SkynetTransaction transaction, GoalArtifact oteGoal) throws OseeCoreException {
      Artifact action =
         ActionManager.createAction(null, "Fix this model", "Description", ChangeType.Problem, "2", false, null,
            ActionableItemManagerCore.getActionableItems(Arrays.asList("OTE_SW")), createdDate, createdBy, null,
            transaction);
      action.persist(transaction);
      oteGoal.addMember(ActionManager.getFirstTeam(action));
      TeamWorkFlowArtifact teamArt = ActionManager.getFirstTeam(action);
      PeerToPeerReviewArtifact peerReviewArt =
         ReviewManager.createNewPeerToPeerReview(teamArt, "New Review", "Implement", transaction);
      oteGoal.addMember(peerReviewArt);
      teamArt.persist(transaction);
      return teamArt;
   }
}
