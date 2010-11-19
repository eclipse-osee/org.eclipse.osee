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
package org.eclipse.osee.ats.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.artifact.AbstractReviewArtifact;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.column.EstimatedHoursColumn;
import org.eclipse.osee.ats.column.RemainingHoursColumn;
import org.eclipse.osee.ats.column.WorkDaysNeededColumn;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class SMAMetrics {

   double estHours = 0;
   double hrsRemainFromEstimates = 0;
   double hrsSpent = 0;
   double manDaysNeeded = 0;
   double cummulativeWorkflowPercentComplete = 0;
   double percentCompleteByWorkflowPercents = 0;

   Date estimatedReleaseDate;
   long daysTillRel = 0;
   VersionArtifact versionArtifact = null;
   String str = "";
   Set<TeamWorkFlowArtifact> teamArts = new HashSet<TeamWorkFlowArtifact>();
   Set<ActionArtifact> actionArts = new HashSet<ActionArtifact>();
   Set<TaskArtifact> taskArts = new HashSet<TaskArtifact>();
   Set<AbstractReviewArtifact> reviewArts = new HashSet<AbstractReviewArtifact>();
   Set<AbstractWorkflowArtifact> smas = new HashSet<AbstractWorkflowArtifact>();
   Set<User> assignees = new HashSet<User>();
   Set<User> assigneesAssignedOrCompleted = new HashSet<User>();

   private final HashCollection<User, Artifact> userToAssignedSmas = new HashCollection<User, Artifact>(true,
      HashSet.class, 100);
   private final HashCollection<User, Artifact> userToCompletedSmas = new HashCollection<User, Artifact>(true,
      HashSet.class, 100);
   private final double manHoursPerDay;

   public SMAMetrics(Collection<? extends Artifact> artifacts, VersionArtifact versionArtifact, double manHoursPerDay, Date estimatedReleaseDate) throws OseeCoreException {
      this.manHoursPerDay = manHoursPerDay;
      this.versionArtifact = versionArtifact;
      this.estimatedReleaseDate = estimatedReleaseDate;
      if (artifacts.isEmpty()) {
         return;
      }
      Set<Artifact> resolvedArts = new HashSet<Artifact>(artifacts);
      for (Artifact art : artifacts) {
         if (art instanceof GoalArtifact) {
            resolvedArts.addAll(((GoalArtifact) art).getMembers());
         }
      }

      for (Artifact art : resolvedArts) {
         if (art instanceof ActionArtifact) {
            actionArts.add((ActionArtifact) art);
         }
      }
      for (Artifact art : resolvedArts) {
         if (art instanceof TeamWorkFlowArtifact) {
            teamArts.add((TeamWorkFlowArtifact) art);
         } else if (art instanceof TaskArtifact) {
            taskArts.add((TaskArtifact) art);
         } else if (art instanceof AbstractReviewArtifact) {
            reviewArts.add((AbstractReviewArtifact) art);
         }
         if (art instanceof AbstractWorkflowArtifact) {
            smas.add((AbstractWorkflowArtifact) art);
            Collection<User> users = ((AbstractWorkflowArtifact) art).getStateMgr().getAssignees();
            assignees.addAll(users);
            assigneesAssignedOrCompleted.addAll(users);
            for (User user : users) {
               userToAssignedSmas.put(user, art);
            }
            if (((AbstractWorkflowArtifact) art).isCompleted()) {
               Collection<User> implementers = ((AbstractWorkflowArtifact) art).getImplementers();
               assigneesAssignedOrCompleted.addAll(implementers);
               for (User user : implementers) {
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
      for (AbstractWorkflowArtifact team : smas) {
         hrsRemainFromEstimates += RemainingHoursColumn.getRemainingHours(team);
         estHours += EstimatedHoursColumn.getEstimatedHours(team);
         hrsSpent += team.getWorldViewHoursSpentTotal();
         manDaysNeeded += WorkDaysNeededColumn.getWorldViewManDaysNeeded(team);
         cummulativeWorkflowPercentComplete += team.getWorldViewPercentCompleteTotal();
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
      if (versionArtifact != null && estimatedReleaseDate == null) {
         estimatedReleaseDate = versionArtifact.getEstimatedReleaseDate();
      }
      if (estimatedReleaseDate != null && estimatedReleaseDate.after(today)) {
         daysTillRel = DateUtil.getWorkingDaysBetween(today, estimatedReleaseDate);
      }
      str =
         String.format(
            "TeamWFs: %s Tasks: %s EstHrs: %5.2f  %sCmp: %5.2f  RmnHrs: %5.2f  HrsSpnt: %5.2f  %s  %s",
            getNumTeamWfs(),
            getNumTasks(),
            estHours,
            "%",
            percentCompleteByWorkflowPercents,
            hrsRemainFromEstimates,
            hrsSpent,
            (manDaysNeeded > 0 ? String.format("ManDaysNeeded: %5.2f ", manDaysNeeded) : ""),
            (versionArtifact != null ? String.format("Version: %s  EstRelDate: %s DaysLeft: %d ",
               versionArtifact.getName(),
               (estimatedReleaseDate == null ? "Not Set" : DateUtil.getMMDDYY(estimatedReleaseDate)), daysTillRel) : ""));
   }

   public HashCollection<User, Artifact> getUserToCompletedSmas() {
      return userToCompletedSmas;
   }

   public <A extends AbstractWorkflowArtifact> Collection<A> getUserToCompletedSmas(User user) {
      return getUserToCompletedSmas(user, null);
   }

   @SuppressWarnings("unchecked")
   public <A extends AbstractWorkflowArtifact> Collection<A> getUserToCompletedSmas(User user, Class<A> clazz) {
      if (!userToCompletedSmas.containsKey(user)) {
         return Collections.emptyList();
      }
      List<A> smas = new ArrayList<A>();
      for (Artifact art : userToCompletedSmas.getValues(user)) {
         if (clazz == null || art.getClass().isInstance(clazz)) {
            smas.add((A) art);
         }
      }
      return smas;
   }

   @SuppressWarnings("unchecked")
   public <A extends AbstractWorkflowArtifact> Collection<A> getUserToAssignedSmas(User user, Class<A> clazz) {
      if (!userToAssignedSmas.containsKey(user)) {
         return Collections.emptyList();
      }
      List<A> smas = new ArrayList<A>();
      for (Artifact art : userToAssignedSmas.getValues(user)) {
         if (clazz == null || art.getClass().equals(clazz)) {
            smas.add((A) art);
         }
      }
      return smas;
   }

   public Collection<TeamWorkFlowArtifact> getCompletedTeamWorkflows() throws OseeCoreException {
      Set<TeamWorkFlowArtifact> teams = new HashSet<TeamWorkFlowArtifact>();
      for (TeamWorkFlowArtifact team : getTeamArts()) {
         if (team.isCompletedOrCancelled()) {
            teams.add(team);
         }
      }
      return teams;
   }

   public Collection<AbstractWorkflowArtifact> getCompletedWorkflows() throws OseeCoreException {
      Set<AbstractWorkflowArtifact> completed = new HashSet<AbstractWorkflowArtifact>();
      for (AbstractWorkflowArtifact sma : smas) {
         if (sma.isCompletedOrCancelled()) {
            completed.add(sma);
         }
      }
      return completed;
   }

   public double getPercentCompleteByTeamWorkflow() throws OseeCoreException {
      if (getTeamArts().isEmpty()) {
         return 0;
      }
      double completed = getCompletedTeamWorkflows().size();
      if (completed == 0) {
         return 0;
      }
      return completed / getTeamArts().size() * 100;
   }

   public double getPercentCompleteByWorkflow() throws OseeCoreException {
      if (smas.isEmpty()) {
         return 0;
      }
      double completed = getCompletedWorkflows().size();
      if (completed == 0) {
         return 0;
      }
      return completed / smas.size() * 100;
   }

   public Collection<TaskArtifact> getCompletedTaskWorkflows() throws OseeCoreException {
      Set<TaskArtifact> tasks = new HashSet<TaskArtifact>();
      for (TaskArtifact team : getTaskArts()) {
         if (team.isCompletedOrCancelled()) {
            tasks.add(team);
         }
      }
      return tasks;
   }

   public double getPercentCompleteByTaskWorkflow() throws OseeCoreException {
      if (getTaskArts().isEmpty()) {
         return 0;
      }
      double completed = getCompletedTaskWorkflows().size();
      if (completed == 0) {
         return 0;
      }
      return completed / getTaskArts().size() * 100;
   }

   @Override
   public String toString() {
      return str;
   }

   public static String getEstRemainMetrics(Collection<? extends Artifact> smas, VersionArtifact versionArtifact, double manHoursPerDay, Date estimatedrelDate) throws OseeCoreException {
      return new SMAMetrics(smas, versionArtifact, manHoursPerDay, estimatedrelDate).str;
   }

   public Set<User> getAssigneesAssignedOrCompleted() {
      return assigneesAssignedOrCompleted;
   }

   public String toStringObjectBreakout() {
      return String.format("Actions: %s  - Team Workflows: %s - Task Workflows: %s - Review Workflows: %s ",
         getNumActions(), getNumTeamWfs(), getNumTasks(), getNumReviews());
   }

   public String toStringLong() {
      return String.format(
         "%s\nEstimated Hours: %5.2f  Percent Complete: %5.2f  Remaining Hours: %5.2f  ManDaysNeeded: %5.2f \nHours Spent: %5.2f  %s",
         toStringObjectBreakout(),
         estHours,
         percentCompleteByWorkflowPercents,
         hrsRemainFromEstimates,
         manDaysNeeded,
         hrsSpent,
         (versionArtifact != null ? String.format("\nVersion: %s  Estimated Release Date: %s Days Left: %d ",
            versionArtifact.getName(),
            (estimatedReleaseDate == null ? "Not Set" : DateUtil.getMMDDYY(estimatedReleaseDate)), daysTillRel) : ""));
   }

   public Date getEstRelDate() {
      return estimatedReleaseDate;
   }

   public void setEstRelDate(Date estRelDate) {
      this.estimatedReleaseDate = estRelDate;
   }

   public long getDaysTillRel() {
      return daysTillRel;
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

   /**
    * @param daysTillRel the daysTillRel to set
    */
   public void setDaysTillRel(int daysTillRel) {
      this.daysTillRel = daysTillRel;
   }

   public VersionArtifact getVersionArtifact() {
      return versionArtifact;
   }

   public void setVersionArtifact(VersionArtifact versionArtifact) {
      this.versionArtifact = versionArtifact;
   }

   public int getNumTeamWfs() {
      return teamArts.size();
   }

   public int getNumTasks() {
      return taskArts.size();
   }

   public int getNumSMAs() {
      return smas.size();
   }

   public int getNumNotEstimated() throws OseeCoreException {
      int count = 0;
      for (AbstractWorkflowArtifact sma : smas) {
         if (EstimatedHoursColumn.getEstimatedHours(sma) == 0) {
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

   public double getEstHours() {
      return estHours;
   }

   public void setEstHours(double estHours) {
      this.estHours = estHours;
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

   public Set<ActionArtifact> getActionArts() {
      return actionArts;
   }

   public Set<TaskArtifact> getTaskArts() {
      return taskArts;
   }

   public Set<AbstractReviewArtifact> getReviewArts() {
      return reviewArts;
   }

   /**
    * Return all SMAs including Review and Tasks
    */
   public HashCollection<User, Artifact> getUserToAssignedSmas() {
      return userToAssignedSmas;
   }

   public <A extends AbstractWorkflowArtifact> Collection<A> getUserToAssignedSmas(User user) {
      return getUserToAssignedSmas(user, null);
   }

}
