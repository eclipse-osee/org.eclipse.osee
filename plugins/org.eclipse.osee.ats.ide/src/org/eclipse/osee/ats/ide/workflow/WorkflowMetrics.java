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
package org.eclipse.osee.ats.ide.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.util.HoursSpentUtil;
import org.eclipse.osee.ats.core.util.PercentCompleteTotalUtil;
import org.eclipse.osee.ats.ide.column.RemainingHoursColumn;
import org.eclipse.osee.ats.ide.column.WorkDaysNeededColumn;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class WorkflowMetrics {

   double estHours = 0;
   double hrsRemainFromEstimates = 0;
   double hrsSpent = 0;
   double manDaysNeeded = 0;
   double cummulativeWorkflowPercentComplete = 0;
   double percentCompleteByWorkflowPercents = 0;
   double points = 0, pointsNumeric = 0;

   Date estimatedReleaseDate;
   long daysTillRel = 0;
   IAtsVersion version = null;
   String str = "";
   Set<TeamWorkFlowArtifact> teamArts = new HashSet<>();
   Set<Artifact> actionArts = new HashSet<>();
   Set<TaskArtifact> taskArts = new HashSet<>();
   Set<AbstractReviewArtifact> reviewArts = new HashSet<>();
   Set<GoalArtifact> goalArts = new HashSet<>();
   Set<AbstractWorkflowArtifact> awas = new HashSet<>();
   Set<IAtsUser> assignees = new HashSet<>();
   Set<IAtsUser> assigneesAssignedOrCompleted = new HashSet<>();

   private final HashCollectionSet<IAtsUser, Artifact> userToAssignedSmas =
      new HashCollectionSet<>(false, 100, HashSet::new);
   private final HashCollectionSet<IAtsUser, Artifact> userToCompletedSmas =
      new HashCollectionSet<>(false, 100, HashSet::new);
   private final double manHoursPerDay;

   public WorkflowMetrics(Collection<? extends Artifact> artifacts, IAtsVersion version, double manHoursPerDay, Date estimatedReleaseDate) {
      this.manHoursPerDay = manHoursPerDay;
      this.version = version;
      this.estimatedReleaseDate = estimatedReleaseDate;
      if (artifacts.isEmpty()) {
         return;
      }
      for (Artifact art : artifacts) {
         if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            teamArts.add((TeamWorkFlowArtifact) art);
         } else if (art.isOfType(AtsArtifactTypes.Task)) {
            taskArts.add((TaskArtifact) art);
         } else if (art.isOfType(AtsArtifactTypes.AbstractReview)) {
            reviewArts.add((AbstractReviewArtifact) art);
         } else if (art.isOfType(AtsArtifactTypes.Goal)) {
            goalArts.add((GoalArtifact) art);
         } else if (art.isOfType(AtsArtifactTypes.Action)) {
            actionArts.add(art);
         }
         if (art instanceof AbstractWorkflowArtifact) {
            awas.add((AbstractWorkflowArtifact) art);
            Collection<IAtsUser> users = new HashSet<>();
            users.addAll(((AbstractWorkflowArtifact) art).getStateMgr().getAssignees());
            assignees.addAll(users);
            assigneesAssignedOrCompleted.addAll(users);
            for (IAtsUser user : users) {
               userToAssignedSmas.put(user, art);
            }
            if (((AbstractWorkflowArtifact) art).isCompleted()) {
               Collection<IAtsUser> implementers = ((AbstractWorkflowArtifact) art).getImplementers();
               assigneesAssignedOrCompleted.addAll(implementers);
               for (IAtsUser user : implementers) {
                  userToCompletedSmas.put(user, art);
               }
            }
         }
      }
      estHours = 0;
      hrsRemainFromEstimates = 0;
      hrsSpent = 0;
      manDaysNeeded = 0;
      cummulativeWorkflowPercentComplete = 0;
      manDaysNeeded = 0;
      points = 0;
      pointsNumeric = 0;
      for (AbstractWorkflowArtifact team : awas) {
         hrsRemainFromEstimates += RemainingHoursColumn.getRemainingHours(team);
         estHours += EstimatedHoursUtil.getEstimatedHours(team);
         hrsSpent += HoursSpentUtil.getHoursSpentTotal(team, AtsClientService.get().getServices());
         manDaysNeeded += WorkDaysNeededColumn.getWorldViewManDaysNeeded(team);
         cummulativeWorkflowPercentComplete +=
            PercentCompleteTotalUtil.getPercentCompleteTotal(team, AtsClientService.get().getServices());
         String ptsStr = team.getSoleAttributeValue(AtsAttributeTypes.Points, null);
         points += Strings.isNumeric(ptsStr) ? Integer.valueOf(ptsStr) : 0;
         pointsNumeric += team.getSoleAttributeValue(AtsAttributeTypes.PointsNumeric, 0.0);
      }
      if (hrsRemainFromEstimates != 0) {
         manDaysNeeded = hrsRemainFromEstimates / manHoursPerDay;
      }
      percentCompleteByWorkflowPercents = 0;
      if (getNumSMAs() > 0 && cummulativeWorkflowPercentComplete > 0) {
         percentCompleteByWorkflowPercents = cummulativeWorkflowPercentComplete / getNumSMAs();
      }

      Date today = new Date();
      daysTillRel = 0;
      if (version != null && estimatedReleaseDate == null) {
         estimatedReleaseDate = version.getEstimatedReleaseDate();
      }
      if (estimatedReleaseDate != null && estimatedReleaseDate.after(today)) {
         daysTillRel = DateUtil.getWorkingDaysBetween(today, estimatedReleaseDate);
      }
      str = String.format(
         "TeamWFs: %s Tasks: %s Revs: %s EstHrs: %5.2f  %sCmp: %5.2f  RmnHrs: %5.2f  HrsSpnt: %5.2f  Pts: %s  PtsNum: %s  %s  %s",
         getNumTeamWfs(), getNumTasks(), getNumReviews(), estHours, "%", percentCompleteByWorkflowPercents,
         hrsRemainFromEstimates, hrsSpent, points, pointsNumeric,
         manDaysNeeded > 0 ? String.format("ManDaysNeeded: %5.2f ", manDaysNeeded) : "",
         version != null ? String.format("Version: %s  EstRelDate: %s DaysLeft: %d ", version.getName(),
            estimatedReleaseDate == null ? "Not Set" : DateUtil.getMMDDYY(estimatedReleaseDate), daysTillRel) : "");
   }

   public HashCollectionSet<IAtsUser, Artifact> getUserToCompletedSmas() {
      return userToCompletedSmas;
   }

   public <A extends AbstractWorkflowArtifact> Collection<A> getUserToCompletedSmas(IAtsUser user) {
      return getUserToCompletedSmas(user, null);
   }

   @SuppressWarnings("unchecked")
   public <A extends AbstractWorkflowArtifact> Collection<A> getUserToCompletedSmas(IAtsUser user, Class<A> clazz) {
      if (!userToCompletedSmas.containsKey(user)) {
         return Collections.emptyList();
      }
      List<A> awas = new ArrayList<>();
      for (Artifact art : userToCompletedSmas.getValues(user)) {
         if (clazz == null || art.getClass().isInstance(clazz)) {
            awas.add((A) art);
         }
      }
      return awas;
   }

   @SuppressWarnings("unchecked")
   public <A extends AbstractWorkflowArtifact> Collection<A> getUserToAssignedSmas(IAtsUser user, Class<A> clazz) {
      if (!userToAssignedSmas.containsKey(user)) {
         return Collections.emptyList();
      }
      List<A> awas = new ArrayList<>();
      for (Artifact art : userToAssignedSmas.getValues(user)) {
         if (clazz == null || art.getClass().equals(clazz)) {
            awas.add((A) art);
         }
      }
      return awas;
   }

   public Collection<TeamWorkFlowArtifact> getCompletedTeamWorkflows() {
      Set<TeamWorkFlowArtifact> teams = new HashSet<>();
      for (TeamWorkFlowArtifact team : getTeamArts()) {
         if (team.isCompletedOrCancelled()) {
            teams.add(team);
         }
      }
      return teams;
   }

   public Collection<AbstractWorkflowArtifact> getCompletedWorkflows() {
      Set<AbstractWorkflowArtifact> completed = new HashSet<>();
      for (AbstractWorkflowArtifact awa : awas) {
         if (awa.isCompletedOrCancelled()) {
            completed.add(awa);
         }
      }
      return completed;
   }

   public double getPercentCompleteByWorkflow() {
      if (awas.isEmpty()) {
         return 0;
      }
      double completed = getCompletedWorkflows().size();
      if (completed == 0) {
         return 0;
      }
      return completed / awas.size() * 100;
   }

   public Collection<TaskArtifact> getCompletedTaskWorkflows() {
      Set<TaskArtifact> tasks = new HashSet<>();
      for (TaskArtifact team : getTaskArts()) {
         if (team.isCompletedOrCancelled()) {
            tasks.add(team);
         }
      }
      return tasks;
   }

   @Override
   public String toString() {
      return str;
   }

   public static String getEstRemainMetrics(Collection<? extends Artifact> awas, IAtsVersion versionArtifact, double manHoursPerDay, Date estimatedrelDate) {
      return new WorkflowMetrics(awas, versionArtifact, manHoursPerDay, estimatedrelDate).str;
   }

   public Set<IAtsUser> getAssigneesAssignedOrCompleted() {
      return assigneesAssignedOrCompleted;
   }

   public String toStringObjectBreakout() {
      return String.format(
         "Actions: %s  - Team Workflows: %s - Task Workflows: %s - Review Workflows: %s  - Goal Workflows: %s ",
         getNumActions(), getNumTeamWfs(), getNumTasks(), getNumReviews(), getGoals());
   }

   public Date getEstRelDate() {
      return estimatedReleaseDate;
   }

   public double getHoursTillRel() {
      return daysTillRel * manHoursPerDay;
   }

   public String getHoursTillRelStr() {
      return String.format("%5.2f hours = %d days till release * %5.2f Man Hours Per Day", getHoursTillRel(),
         daysTillRel, manHoursPerDay);
   }

   public String getDaysTillRelStr() {
      return String.format("%d workdays (M-F) till release", daysTillRel);
   }

   public int getNumTeamWfs() {
      return teamArts.size();
   }

   public int getNumTasks() {
      return taskArts.size();
   }

   public int getNumSMAs() {
      return awas.size();
   }

   public int getNumNotEstimated() {
      int count = 0;
      for (AbstractWorkflowArtifact awa : awas) {
         if (EstimatedHoursUtil.getEstimatedHours(awa) == 0) {
            count++;
         }
      }
      return count;
   }

   public int getNumActions() {
      return actionArts.size();
   }

   public int getNumReviews() {
      return reviewArts.size();
   }

   public int getGoals() {
      return goalArts.size();
   }

   public double getEstHours() {
      return estHours;
   }

   public double getHrsRemainFromEstimates() {
      return hrsRemainFromEstimates;
   }

   public double getHrsSpent() {
      return hrsSpent;
   }

   public double getManDaysNeeded() {
      return manDaysNeeded;
   }

   public double getCummulativeWorkflowPercentComplete() {
      return cummulativeWorkflowPercentComplete;
   }

   public double getPercentCompleteByWorkflowPercents() {
      return percentCompleteByWorkflowPercents;
   }

   public double getHoursPerManDay() {
      return manHoursPerDay;
   }

   public Set<TeamWorkFlowArtifact> getTeamArts() {
      return teamArts;
   }

   public Set<TaskArtifact> getTaskArts() {
      return taskArts;
   }

   public HashCollectionSet<IAtsUser, Artifact> getUserToAssignedSmas() {
      return userToAssignedSmas;
   }

   public <A extends AbstractWorkflowArtifact> Collection<A> getUserToAssignedSmas(IAtsUser user) {
      return getUserToAssignedSmas(user, null);
   }

}
