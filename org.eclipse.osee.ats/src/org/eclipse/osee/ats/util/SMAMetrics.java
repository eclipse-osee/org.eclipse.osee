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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

/**
 * @author Donald G. Dunne
 */
public class SMAMetrics {

   private static double manDayHrs = 7.2;
   private static int MILLISECS_PER_DAY = (1000 * 60 * 60 * 24);

   double estHours = 0;
   double hrsRemain = 0;
   double hrsSpent = 0;
   double manDaysNeeded = 0;
   double cummulativeTeamPercentComplete = 0;
   double percentCompleteByTeamPercents = 0;
   double percentCompleteByTeamWorkflow = 0;
   double cummulativeTaskPercentComplete = 0;
   double percentCompleteByTaskPercents = 0;
   double percentCompleteByTaskWorkflow = 0;

   Date estRelDate;
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
   Set<StateMachineArtifact> smasInherited = new HashSet<StateMachineArtifact>();

   private final HashCollection<User, Artifact> userToAssignedSmas =
         new HashCollection<User, Artifact>(true, HashSet.class, 100);
   private final HashCollection<User, Artifact> userToCompletedSmas =
         new HashCollection<User, Artifact>(true, HashSet.class, 100);

   /**
    * @return the userToCompletedSmas
    */
   public HashCollection<User, Artifact> getUserToCompletedSmas() {
      return userToCompletedSmas;
   }

   /**
    * @return the manDayHrs
    */
   public static double getManDayHrs() {
      return manDayHrs;
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
    * @return the userToSmas
    */
   public HashCollection<User, Artifact> getUserToAssignedSmas() {
      return userToAssignedSmas;
   }

   /**
    * @return the estRelDate
    */
   public Date getEstRelDate() {
      return estRelDate;
   }

   /**
    * @param estRelDate the estRelDate to set
    */
   public void setEstRelDate(Date estRelDate) {
      this.estRelDate = estRelDate;
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
      return daysTillRel * manDayHrs;
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
   public double getHrsRemain() {
      return hrsRemain;
   }

   /**
    * @param hrsRemain the hrsRemain to set
    */
   public void setHrsRemain(double hrsRemain) {
      this.hrsRemain = hrsRemain;
   }

   /**
    * @return the hrsSpent
    */
   public double getHrsSpent() {
      return hrsSpent;
   }

   /**
    * @param hrsSpent the hrsSpent to set
    */
   public void setHrsSpent(double hrsSpent) {
      this.hrsSpent = hrsSpent;
   }

   /**
    * @return the cummulativeTaskPercentComplete
    */
   public double getCummulativeTaskPercentComplete() {
      return cummulativeTaskPercentComplete;
   }

   /**
    * @return the percentCompleteByTaskPercents
    */
   public double getPercentCompleteByTaskPercents() {
      return percentCompleteByTaskPercents;
   }

   /**
    * @return the manDaysNeeded
    */
   public double getManDaysNeeded() {
      return manDaysNeeded;
   }

   /**
    * @param manDaysNeeded the manDaysNeeded to set
    */
   public void setManDaysNeeded(double manDaysNeeded) {
      this.manDaysNeeded = manDaysNeeded;
   }

   /**
    * @return the cummulativePercentComplete
    */
   public double getCummulativeTeamPercentComplete() {
      return cummulativeTeamPercentComplete;
   }

   /**
    * @param cummulativePercentComplete the cummulativePercentComplete to set
    */
   public void setCummulativePercentComplete(double cummulativePercentComplete) {
      this.cummulativeTeamPercentComplete = cummulativePercentComplete;
   }

   /**
    * @return the percentComplete
    */
   public double getPercentCompleteByTeamPercents() {
      return percentCompleteByTeamPercents;
   }

   public Collection<TeamWorkFlowArtifact> getCompletedTeamWorkflows() throws OseeCoreException {
      Set<TeamWorkFlowArtifact> teams = new HashSet<TeamWorkFlowArtifact>();
      for (TeamWorkFlowArtifact team : getTeamArts()) {
         if (team.getSmaMgr().isCancelledOrCompleted()) {
            teams.add(team);
         }
      }
      return teams;
   }

   public double getPercentCompleteByTeamWorkflow() throws OseeCoreException {
      if (getTeamArts().size() == 0) return 0;
      double completed = getCompletedTeamWorkflows().size();
      if (completed == 0) return 0;
      return completed / getTeamArts().size() * 100;
   }

   public Collection<TaskArtifact> getCompletedTaskWorkflows() throws OseeCoreException {
      Set<TaskArtifact> tasks = new HashSet<TaskArtifact>();
      for (TaskArtifact team : getTaskArts()) {
         if (team.getSmaMgr().isCancelledOrCompleted()) {
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

   public static String getEstRemainMetrics(Collection<? extends Artifact> smas) throws OseeCoreException {
      return new SMAMetrics(smas).str;
   }

   public SMAMetrics(Collection<? extends Artifact> smas) throws OseeCoreException {
      this(smas, null);
   }

   public SMAMetrics(Collection<? extends Artifact> artifacts, VersionArtifact versionArtifact) throws OseeCoreException {
      this.versionArtifact = versionArtifact;
      if (artifacts.size() == 0) return;
      getInheritedSmasRecursive(artifacts, smasInherited);
      for (Artifact art : artifacts) {
         if (art instanceof ActionArtifact) {
            actionArts.add((ActionArtifact) art);
         }
      }
      for (Artifact art : smasInherited) {
         if (art instanceof TeamWorkFlowArtifact) {
            teamArts.add((TeamWorkFlowArtifact) art);
         } else if (art instanceof TaskArtifact) {
            taskArts.add((TaskArtifact) art);
         } else if (art instanceof ReviewSMArtifact) {
            reviewArts.add((ReviewSMArtifact) art);
         }
         if (art instanceof StateMachineArtifact) {
            smas.add((StateMachineArtifact) art);
            Collection<User> users = ((StateMachineArtifact) art).getSmaMgr().getStateMgr().getAssignees();
            assignees.addAll(users);
            assigneesAssignedOrCompleted.addAll(users);
            for (User user : users) {
               userToAssignedSmas.put(user, art);
            }
            if (((StateMachineArtifact) art).getSmaMgr().isCompleted()) {
               Collection<User> implementers = ((StateMachineArtifact) art).getImplementers();
               assigneesAssignedOrCompleted.addAll(implementers);
               for (User user : implementers) {
                  userToCompletedSmas.put(user, art);
               }
            }
         }
      }
      estHours = 0;
      hrsRemain = 0;
      hrsSpent = 0;
      manDaysNeeded = 0;
      cummulativeTeamPercentComplete = 0;
      manDaysNeeded = 0;
      for (TeamWorkFlowArtifact team : teamArts) {
         hrsRemain += team.getWorldViewRemainHours();
         estHours += team.getWorldViewEstimatedHours();
         hrsSpent += team.getWorldViewHoursSpentTotal();
         manDaysNeeded += team.getWorldViewManDaysNeeded();
         cummulativeTeamPercentComplete += team.getWorldViewPercentCompleteTotal();
      }
      if (hrsRemain != 0) manDaysNeeded = hrsRemain / manDayHrs;
      percentCompleteByTeamPercents = 0;
      if (getNumTeamWfs() > 0 && cummulativeTeamPercentComplete > 0) {
         percentCompleteByTeamPercents = cummulativeTeamPercentComplete / getNumTeamWfs();
      }
      for (TaskArtifact task : taskArts) {
         cummulativeTaskPercentComplete += task.getWorldViewPercentCompleteTotal();
      }
      percentCompleteByTaskPercents = 0;
      if (getNumTasks() > 0 && cummulativeTaskPercentComplete > 0) {
         percentCompleteByTaskPercents = cummulativeTaskPercentComplete / getNumTasks();
      }

      estRelDate = null;
      Date today = new Date();
      daysTillRel = 0;
      if (versionArtifact != null) {
         estRelDate = versionArtifact.getEstimatedReleaseDate();
         if (estRelDate != null && estRelDate.after(today)) {
            daysTillRel = (estRelDate.getTime() - today.getTime()) / MILLISECS_PER_DAY;
         }
      }
      str =
            String.format("TeamWFs: %s Tasks: %s EstHrs: %5.2f  %sCmp: %5.2f  RmnHrs: %5.2f  HrsSpnt: %5.2f  %s  %s",
                  getNumTeamWfs(), getNumTasks(), estHours, "%", percentCompleteByTeamPercents, hrsRemain, hrsSpent,
                  (manDaysNeeded > 0 ? String.format("ManDaysNeeded: %5.2f ", manDaysNeeded) : ""),
                  (versionArtifact != null ? String.format("Version: %s  EstRelDate: %s DaysLeft: %d ",
                        versionArtifact.getDescriptiveName(), (estRelDate == null ? "Not Set" : XDate.getDateStr(
                              estRelDate, XDate.MMDDYY)), daysTillRel) : ""));
   }

   /**
    * @return the assigneesAssignedOrCompleted
    */
   public Set<User> getAssigneesAssignedOrCompleted() {
      return assigneesAssignedOrCompleted;
   }

   public void getInheritedSmasRecursive(Collection<? extends Artifact> artifacts, Set<StateMachineArtifact> smas) throws OseeCoreException {
      for (Artifact art : artifacts) {
         if (art instanceof StateMachineArtifact) {
            StateMachineArtifact sma = (StateMachineArtifact) art;
            smas.add(sma);
            smas.addAll(sma.getSmaMgr().getReviewManager().getReviews());
            getInheritedSmasRecursive(sma.getSmaMgr().getReviewManager().getReviews(), smas);
            smas.addAll(sma.getSmaMgr().getTaskMgr().getTaskArtifacts());
         } else if (art instanceof ActionArtifact) {
            smas.addAll(((ActionArtifact) art).getTeamWorkFlowArtifacts());
            getInheritedSmasRecursive(((ActionArtifact) art).getTeamWorkFlowArtifacts(), smas);
         }
      }
   }

   public String toStringObjectBreakout() {
      return String.format("Actions: %s  - Team Workflows: %s - Tasks: %s - Reviews: %s ", getNumActions(),
            getNumTeamWfs(), getNumTasks(), getNumReviews());
   }

   public String toStringLong() {
      return String.format(
            "%s\nEstimated Hours: %5.2f  Percent Complete: %5.2f  Remaining Hours: %5.2f  ManDaysNeeded: %5.2f \nHours Spent: %5.2f  %s",
            toStringObjectBreakout(), estHours, percentCompleteByTeamPercents, hrsRemain, manDaysNeeded, hrsSpent,
            (versionArtifact != null ? String.format("\nVersion: %s  Estimated Release Date: %s Days Left: %d ",
                  versionArtifact.getDescriptiveName(), (estRelDate == null ? "Not Set" : XDate.getDateStr(estRelDate,
                        XDate.MMDDYY)), daysTillRel) : ""));
   }
}
