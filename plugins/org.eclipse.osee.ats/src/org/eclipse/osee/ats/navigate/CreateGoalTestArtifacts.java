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

package org.eclipse.osee.ats.navigate;

import java.util.Arrays;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.ActionManager;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.GoalManager;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class CreateGoalTestArtifacts extends XNavigateItemAction {

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
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), getName());
      GoalArtifact oteGoal = GoalManager.createGoal("OTE");
      GoalArtifact atsGoal = GoalManager.createGoal("ATS");
      GoalArtifact defineGoal = GoalManager.createGoal("Define");
      GoalArtifact msaGoal = GoalManager.createGoal("MSA Tools");
      GoalArtifact cdbGoal = GoalManager.createGoal("Integrate CDB signal diff into OSEE");
      ActionArtifact action =
         ActionManager.createAction(null, "Fix this model", "Description", ChangeType.Problem, PriorityType.Priority_2,
            false, null, ActionableItemArtifact.getActionableItems(Arrays.asList("OTE_SW")), transaction);
      action.persist(transaction);
      oteGoal.addMember(action.getTeamWorkFlowArtifacts().iterator().next());
      TeamWorkFlowArtifact teamArt = action.getTeamWorkFlowArtifacts().iterator().next();
      PeerToPeerReviewArtifact peerReviewArt =
         ReviewManager.createNewPeerToPeerReview(teamArt, "New Review", "Implement", transaction);
      oteGoal.addMember(peerReviewArt);
      teamArt.persist(transaction);

      action =
         ActionManager.createAction(null, "Add CDB Check Signals", "Description", ChangeType.Problem,
            PriorityType.Priority_4, false, null,
            ActionableItemArtifact.getActionableItems(Arrays.asList("OTE_SW", "CDB")), transaction);
      action.persist(transaction);
      oteGoal.addMember(action.getTeamWorkFlowArtifacts().iterator().next());
      cdbGoal.addMember(action.getTeamWorkFlowArtifacts().iterator().next());
      for (TeamWorkFlowArtifact teamArt2 : action.getTeamWorkFlowArtifacts()) {
         teamArt2.persist(transaction);
      }

      action =
         ActionManager.createAction(null, "Remove Workflow button", "Description", ChangeType.Problem,
            PriorityType.Priority_4, false, null,
            ActionableItemArtifact.getActionableItems(Arrays.asList("ATS", "CDB")), transaction);
      action.persist(transaction);
      atsGoal.addMember(action.getTeamWorkFlowArtifacts().iterator().next());
      cdbGoal.addMember(action.getTeamWorkFlowArtifacts().iterator().next());
      action.getTeamWorkFlowArtifacts().iterator().next().persist(transaction);

      for (String msaTool : Arrays.asList("TRAX", "REQ", "RCS")) {
         action =
            ActionManager.createAction(null, "Fix " + msaTool + " button", "Description", ChangeType.Problem,
               PriorityType.Priority_4, false, null, ActionableItemArtifact.getActionableItems(Arrays.asList(msaTool)),
               transaction);
         action.persist(transaction);
         msaGoal.addMember(action.getTeamWorkFlowArtifacts().iterator().next());
         teamArt = action.getTeamWorkFlowArtifacts().iterator().next();
         TaskArtifact taskArt = teamArt.createNewTask("Task 1");
         oteGoal.addMember(taskArt);
         taskArt.persist(transaction);
         taskArt = teamArt.createNewTask("Task 2");
         msaGoal.addMember(taskArt);
         taskArt.persist(transaction);
      }

      action =
         ActionManager.createAction(null, "Add the Improvement", "Description", ChangeType.Improvement,
            PriorityType.Priority_4, false, null, ActionableItemArtifact.getActionableItems(Arrays.asList("REQ")),
            transaction);
      action.persist(transaction);
      msaGoal.addMember(action);
      msaGoal.persist(transaction);

      for (String name : Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
         "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "BB", "CC", "DD", "EE", "FF", "GG", "HH", "II", "JJ",
         "KK", "LL", "MM", "NN", "OO", "PP", "QQ", "RR")) {
         TaskArtifact taskArt = teamArt.createNewTask("Task " + name);
         defineGoal.addMember(taskArt);
         taskArt.persist(transaction);
      }

      defineGoal.persist(transaction);

      transaction.execute();
      WorldEditor.open(new WorldEditorSimpleProvider("Goals", Arrays.asList(oteGoal, atsGoal, defineGoal, msaGoal,
         cdbGoal)));

   }
}
