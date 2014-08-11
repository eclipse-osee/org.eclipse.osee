/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.artifact;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.goal.GoalLabelProvider;
import org.eclipse.osee.ats.goal.GoalViewerSorter;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactListDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public final class GoalManager {

   private GoalManager() {
      // private constructor
   }

   /**
    * change goal, prompt if member of two goals
    */
   public static GoalArtifact promptChangeGoalOrder(Artifact artifact) throws OseeCoreException {
      if (!isHasGoal(artifact)) {
         AWorkbench.popup(String.format("No Goal set for artifact [%s]", artifact));
         return null;
      }
      Collection<Artifact> goals = getGoals(artifact, false);
      GoalArtifact goal = null;
      if (goals.size() == 1) {
         goal = (GoalArtifact) goals.iterator().next();
      } else if (goals.size() > 1) {
         ArtifactListDialog dialog =
            new ArtifactListDialog(Displays.getActiveShell(), new GoalViewerSorter(), new GoalLabelProvider());
         dialog.setTitle("Select Goal");
         dialog.setMessage("Artifact has multiple Goals\n\nSelect Goal to change order");
         dialog.setArtifacts(goals);
         if (dialog.open() == 0) {
            goal = (GoalArtifact) dialog.getSelection();
         } else {
            return null;
         }
      }
      return promptChangeGoalOrder(goal, artifact);
   }

   public static boolean isHasGoal(Artifact artifact) throws OseeCoreException {
      return artifact.getRelatedArtifactsCount(AtsRelationTypes.Goal_Goal) > 0;
   }

   /**
    * change goal order for artifact within given goal
    */
   public static GoalArtifact promptChangeGoalOrder(GoalArtifact goalArtifact, Artifact artifact) throws OseeCoreException {
      return promptChangeGoalOrder(goalArtifact, Arrays.asList(artifact));
   }

   public static void getGoals(Artifact artifact, Set<Artifact> goals, boolean recurse) throws OseeCoreException {
      getGoals(Arrays.asList(artifact), goals, recurse);
   }

   public static Collection<Artifact> getGoals(Artifact artifact, boolean recurse) throws OseeCoreException {
      Set<Artifact> goals = new HashSet<Artifact>();
      getGoals(artifact, goals, recurse);
      return goals;
   }

   /**
    * change goal order for artifacts within given goal
    */
   public static GoalArtifact promptChangeGoalOrder(GoalArtifact goalArtifact, List<Artifact> artifacts) throws OseeCoreException {
      StringBuilder currentOrder = new StringBuilder("Current Order: ");
      for (Artifact artifact : artifacts) {
         if (artifacts.size() == 1 && !isHasGoal(artifact) || goalArtifact == null) {
            AWorkbench.popup(String.format("No Goal set for artifact [%s]", artifact));
            return null;
         }
         String currIndexStr = getGoalOrder(goalArtifact, artifact);
         currentOrder.append(currIndexStr + ", ");
      }

      List<Artifact> members = goalArtifact.getMembers();
      EntryDialog ed =
         new EntryDialog("Change Goal Order", String.format(
            "Goal: %s\n\n%s\n\nEnter New Order Number from 1..%d or %d for last.", goalArtifact,
            currentOrder.toString().replaceFirst(", $", ""), members.size(), members.size() + 1));
      ed.setNumberFormat(NumberFormat.getIntegerInstance());

      if (ed.open() == Window.OK) {
         String newIndexStr = ed.getEntry();
         Integer enteredIndex = Integer.valueOf(newIndexStr);
         boolean insertLast = enteredIndex > members.size();
         Integer membersIndex = insertLast ? members.size() - 1 : enteredIndex - 1;
         if (membersIndex > members.size()) {
            AWorkbench.popup(String.format("New Order Number [%s] out of range 1..%d", newIndexStr, members.size()));
            return null;
         }
         List<Artifact> reversed = new LinkedList<Artifact>(artifacts);
         Collections.reverse(reversed);
         for (Artifact artifact : reversed) {
            int currentIdx = members.indexOf(artifact);
            Artifact insertTarget = members.get(membersIndex);
            boolean insertAfter = membersIndex > currentIdx;
            goalArtifact.setRelationOrder(AtsRelationTypes.Goal_Member, insertTarget, insertAfter, artifact);
         }
         goalArtifact.persist(GoalManager.class.getSimpleName());
         return goalArtifact;
      }
      return null;
   }

   public static String getGoalOrder(Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.Goal)) {
         return "";
      }
      if (!isHasGoal(artifact)) {
         return "";
      }
      Collection<Artifact> goals = getGoals(artifact, false);
      if (goals.size() > 1) {
         List<Artifact> goalsSorted = new ArrayList<Artifact>(goals);
         Collections.sort(goalsSorted);
         StringBuffer sb = new StringBuffer();
         for (Artifact goal : goalsSorted) {
            sb.append(String.format("%s-[%s] ", getGoalOrder((GoalArtifact) goal, artifact), goal));
         }
         return sb.toString();
      }
      Artifact goal = goals.iterator().next();
      return getGoalOrder((GoalArtifact) goal, artifact);
   }

   public static String getGoalOrder(GoalArtifact goalArtifact, Artifact member) throws OseeCoreException {
      List<Artifact> members = goalArtifact.getMembers();
      if (!members.contains(member)) {
         return "";
      }
      try {
         return String.valueOf(members.indexOf(member) + 1);
      } catch (Exception ex) {
         return LogUtil.getCellExceptionString(ex);
      }
   }

   public static GoalArtifact createGoal(String title, IAtsChangeSet changes) throws OseeCoreException {
      GoalArtifact goalArt =
         (GoalArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.Goal, AtsUtilCore.getAtsBranch(), title);

      // Initialize state machine
      goalArt.initializeNewStateMachine(Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()),
         new Date(), AtsClientService.get().getUserService().getCurrentUser(), changes);
      AtsClientService.get().getUtilService().setAtsId(AtsClientService.get().getSequenceProvider(), goalArt,
         TeamDefinitions.getTopTeamDefinition(AtsClientService.get().getConfig()), changes);

      changes.add(goalArt);
      return goalArt;
   }

   public static void getGoals(Collection<Artifact> artifacts, Set<Artifact> goals, boolean recurse) throws OseeCoreException {
      for (Artifact art : artifacts) {
         if (art.isOfType(AtsArtifactTypes.Goal)) {
            goals.add(art);
         }
         goals.addAll(art.getRelatedArtifacts(AtsRelationTypes.Goal_Goal, GoalArtifact.class));
         if (recurse && art instanceof AbstractWorkflowArtifact && ((AbstractWorkflowArtifact) art).getParentAWA() != null) {
            getGoals(((AbstractWorkflowArtifact) art).getParentAWA(), goals, recurse);
         }
      }
   }

   public static GoalArtifact cast(Artifact artifact) {
      if (artifact instanceof GoalArtifact) {
         return (GoalArtifact) artifact;
      }
      return null;
   }

   public static boolean isGoalArtifact(Object object) {
      return object instanceof GoalArtifact;
   }
}
