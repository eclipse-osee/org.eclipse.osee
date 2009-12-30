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
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

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
   Set<ReviewSMArtifact> reviewArts = new HashSet<ReviewSMArtifact>();
   Set<StateMachineArtifact> smas = new HashSet<StateMachineArtifact>();
   Set<User> assignees = new HashSet<User>();
   Set<User> assigneesAssignedOrCompleted = new HashSet<User>();

   private final HashCollection<User, Artifact> userToAssignedSmas =
         new HashCollection<User, Artifact>(true, HashSet.class, 100);
   private final HashCollection<User, Artifact> userToCompletedSmas =
         new HashCollection<User, Artifact>(true, HashSet.class, 100);
   private final double manHoursPerDay;

   public SMAMetrics(Collection<? extends Artifact> artifacts, VersionArtifact versionArtifact, double manHoursPerDay, Date estimatedReleaseDate) throws OseeCoreException {
      this.manHoursPerDay = manHoursPerDay;
      this.versionArtifact = versionArtifact;
      this.estimatedReleaseDate = estimatedReleaseDate;
      if (artifacts.size() == 0) return;
      for (Artifact art : artifacts) {
         if (art instanceof ActionArtifact) {
            actionArts.add((ActionArtifact) art);
         }
      }
      for (Artifact art : artifacts) {
         if (art instanceof TeamWorkFlowArtifact) {
            teamArts.add((TeamWorkFlowArtifact) art);
         } else if (art instanceof TaskArtifact) {
            taskArts.add((TaskArtifact) art);
         } else if (art instanceof ReviewSMArtifact) {
            reviewArts.add((ReviewSMArtifact) art);
         }
         if (art instanceof StateMachineArtifact) {
            smas.add((StateMachineArtifact) art);
            Collection<User> users = ((StateMachineArtifact) art).getStateMgr().getAssignees();
            assignees.addAll(users);
            assigneesAssignedOrCompleted.addAll(users);
            for (User user : users) {
               userToAssignedSmas.put(user, art);
            }
            if (((StateMachineArtifact) art).isCompleted()) {
               Collection<User> implementers = ((StateMachineArtifact) art).getImplementers();
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
      for (StateMachineArtifact team : smas) {
         hrsRemainFromEstimates += team.getWorldViewRemainHours();
         estHours += team.getWorldViewEstimatedHours();
         hrsSpent += team.getWorldViewHoursSpentTotal();
         manDaysNeeded += team.getWorldViewManDaysNeeded();
         cummulativeWorkflowPercentComplete += team.getWorldViewPercentCompleteTotal();
      }
      if (hrsRemainFromEstimates != 0) manDaysNeeded = hrsRemainFromEstimates / manHoursPerDay;
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
            String.format("TeamWFs: %s Tasks: %s EstHrs: %5.2f  %sCmp: %5.2f  RmnHrs: %5.2f  HrsSpnt: %5.2f  %s  %s",
                  getNumTeamWfs(), getNumTasks(), estHours, "%", percentCompleteByWorkflowPercents,
                  hrsRemainFromEstimates, hrsSpent, (manDaysNeeded > 0 ? String.format("ManDaysNeeded: %5.2f ",
                        manDaysNeeded) : ""),
                  (versionArtifact != null ? String.format("Version: %s  EstRelDate: %s DaysLeft: %d ",
                        versionArtifact.getName(), (estimatedReleaseDate == null ? "Not Set" : XDate.getDateStr(
                              estimatedReleaseDate, XDate.MMDDYY)), daysTillRel) : ""));
   }

   /**
    * @return the userToCompletedSmas
    */
   public HashCollection<User, Artifact> getUserToCompletedSmas() {
      return userToCompletedSmas;
   }

   public <A extends StateMachineArtifact> Collection<A> getUserToCompletedSmas(User user) {
      return getUserToCompletedSmas(user, null);
   }

   @SuppressWarnings("unchecked")
   public <A extends StateMachineArtifact> Collection<A> getUserToCompletedSmas(User user, Class<A> clazz) {
      if (!userToCompletedSmas.containsKey(user)) return Collections.emptyList();
      List<A> smas = new ArrayList<A>();
      for (Artifact art : userToCompletedSmas.getValues(user)) {
         if (clazz == null || art.getClass().isInstance(clazz)) {
            smas.add((A) art);
         }
      }
      return smas;
   }

   @SuppressWarnings("unchecked")
   public <A extends StateMachineArtifact> Collection<A> getUserToAssignedSmas(User user, Class<A> clazz) {
      if (!userToAssignedSmas.containsKey(user)) return Collections.emptyList();
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
         if (team.isCancelledOrCompleted()) {
            teams.add(team);
         }
      }
      return teams;
   }

   public Collection<StateMachineArtifact> getCompletedWorkflows() throws OseeCoreException {
      Set<StateMachineArtifact> completed = new HashSet<StateMachineArtifact>();
      for (StateMachineArtifact sma : smas) {
         if (sma.isCancelledOrCompleted()) {
            completed.add(sma);
         }
      }
      return completed;
   }

   public double getPercentCompleteByTeamWorkflow() throws OseeCoreException {
      if (getTeamArts().size() == 0) return 0;
      double completed = getCompletedTeamWorkflows().size();
      if (completed == 0) return 0;
      return completed / getTeamArts().size() * 100;
   }

   public double getPercentCompleteByWorkflow() throws OseeCoreException {
      if (smas.size() == 0) return 0;
      double completed = getCompletedWorkflows().size();
      if (completed == 0) return 0;
      return completed / smas.size() * 100;
   }

   public Collection<TaskArtifact> getCompletedTaskWorkflows() throws OseeCoreException {
      Set<TaskArtifact> tasks = new HashSet<TaskArtifact>();
      for (TaskArtifact team : getTaskArts()) {
         if (team.isCancelledOrCompleted()) {
            tasks.add(team);
         }
      }
      return tasks;
   }

   public double getPercentCompleteByTaskWorkflow() throws OseeCoreException {
      if (getTaskArts().size() == 0) return 0;
      double completed = getCompletedTaskWorkflows().size();
      if (completed == 0) return 0;
      return completed / getTaskArts().size() * 100;
   }

   /**
    * @return the str
    */
   @Override
   public String toString() {
      return str;
   }

   public static String getEstRemainMetrics(Collection<? extends Artifact> smas, VersionArtifact versionArtifact, double manHoursPerDay, Date estimatedrelDate) throws OseeCoreException {
      return new SMAMetrics(smas, versionArtifact, manHoursPerDay, estimatedrelDate).str;
   }

   /**
    * @return the assigneesAssignedOrCompleted
    */
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
            toStringObjectBreakout(), estHours, percentCompleteByWorkflowPercents, hrsRemainFromEstimates,
            manDaysNeeded, hrsSpent, (versionArtifact != null ? String.format(
                  "\nVersion: %s  Estimated Release Date: %s Days Left: %d ", versionArtifact.getName(),
                  (estimatedReleaseDate == null ? "Not Set" : XDate.getDateStr(estimatedReleaseDate, XDate.MMDDYY)),
                  daysTillRel) : ""));
   }

   /**
    * @return the estRelDate
    */
   public Date getEstRelDate() {
      return estimatedReleaseDate;
   }

   /**
    * @param estRelDate the estRelDate to set
    */
   public void setEstRelDate(Date estRelDate) {
      this.estimatedReleaseDate = estRelDate;
   }

   /**
    * @return the daysTillRel
    */
   public long getDaysTillRel() {
      return daysTillRel;
   }

   /**
    * @return the hours till release
    */
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

   /**
    * @return the versionArtifact
    */
   public VersionArtifact getVersionArtifact() {
      return versionArtifact;
   }

   /**
    * @param versionArtifact the versionArtifact to set
    */
   public void setVersionArtifact(VersionArtifact versionArtifact) {
      this.versionArtifact = versionArtifact;
   }

   /**
    * @return the numTeamWfs
    */
   public int getNumTeamWfs() {
      return teamArts.size();
   }

   /**
    * @return the numTasks
    */
   public int getNumTasks() {
      return taskArts.size();
   }

   public int getNumSMAs() {
      return smas.size();
   }

   public int getNumNotEstimated() throws OseeCoreException {
      int count = 0;
      for (StateMachineArtifact sma : smas) {
         if (sma.getWorldViewEstimatedHours() == 0) count++;
      }
      return count;
   }

   /**
    * @return the numTasks
    */
   public int getNumActions() {
      return actionArts.size();
   }

   /**
    * @return the numTasks
    */
   public int getNumReviews() {
      return reviewArts.size();
   }

   /**
    * @return the estHours
    */
   public double getEstHours() {
      return estHours;
   }

   /**
    * @param estHours the estHours to set
    */
   public void setEstHours(double estHours) {
      this.estHours = estHours;
   }

   /**
    * @return the hrsRemain
    */
   public double getHrsRemainFromEstimates() {
      return hrsRemainFromEstimates;
   }

   /**
    * @return the hrsSpent
    */
   public double getHrsSpent() {
      return hrsSpent;
   }

   /**
    * @return the manDaysNeeded
    */
   public double getManDaysNeeded() {
      return manDaysNeeded;
   }

   /**
    * @return the cummulativePercentComplete
    */
   public double getCummulativeWorkflowPercentComplete() {
      return cummulativeWorkflowPercentComplete;
   }

   /**
    * @return the percentComplete
    */
   public double getPercentCompleteByWorkflowPercents() {
      return percentCompleteByWorkflowPercents;
   }

   /**
    * @return the manDayHrs
    */
   public double getHoursPerManDay() {
      return manHoursPerDay;
   }

   /**
    * @return the teamArts
    */
   public Set<TeamWorkFlowArtifact> getTeamArts() {
      return teamArts;
   }

   /**
    * @return the actionArts
    */
   public Set<ActionArtifact> getActionArts() {
      return actionArts;
   }

   /**
    * @return the taskArts
    */
   public Set<TaskArtifact> getTaskArts() {
      return taskArts;
   }

   /**
    * @return the reviewArts
    */
   public Set<ReviewSMArtifact> getReviewArts() {
      return reviewArts;
   }

   /**
    * Return all SMAs including Review and Tasks
    * 
    * @return the userToSmas
    */
   public HashCollection<User, Artifact> getUserToAssignedSmas() {
      return userToAssignedSmas;
   }

   public <A extends StateMachineArtifact> Collection<A> getUserToAssignedSmas(User user) {
      return getUserToAssignedSmas(user, null);
   }

}
