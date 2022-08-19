/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.api.task.create;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.task.related.AutoGenVersion;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class ChangeReportTaskTeamWfData {

   WorkType workType;
   ArtifactToken chgRptTeamWf;
   ArtifactToken destTeamWf;
   ArtifactToken destTeamDef;
   XResultData rd;
   boolean reportOnly;
   String partition;
   String wcafeType;
   AutoGenVersion autoGenVersion;
   NewTaskSet newTaskSet = new NewTaskSet();

   /**
    * Collection of task objects that are created for all tasks needed to be created and then later used to match any
    * tasks already created
    */
   Collection<ChangeReportTaskMatch> taskMatches = new ArrayList<ChangeReportTaskMatch>();

   public ChangeReportTaskTeamWfData() {
      // for jax-rs
   }

   public void addTaskMatch(ChangeReportTaskMatch taskMatch) {
      String taskName = taskMatch.getTaskName();
      for (ChangeReportTaskMatch existTaskMatch : taskMatches) {
         if (existTaskMatch.getTaskName().equals(taskName)) {
            rd.warningf("Duplicate task name found [%s]; skipping", taskName);
            return;
         }
      }
      this.taskMatches.add(taskMatch);
   }

   public ChangeReportTaskMatch addTaskMatch(ArtifactToken art, ChangeReportTaskMatchType changeReportTaskMatchType, boolean deleted, String format, Object... data) {
      ChangeReportTaskMatch taskMatch = new ChangeReportTaskMatch();
      String taskName = String.format(format, data);
      for (ChangeReportTaskMatch existTaskMatch : taskMatches) {
         if (existTaskMatch.getTaskName().equals(taskName)) {
            rd.logf("Duplicate task name found [%s]; skipping\n", taskName);
            return null;
         }
      }
      taskMatch.setTaskName(taskName);
      if (art != null) {
         taskMatch.setChgRptArt(art);
         taskMatch.setChgRptArtName(art.getName());
         taskMatch.setChgRptArtDeleted(deleted);
      }
      taskMatch.setType(changeReportTaskMatchType);
      taskMatches.add(taskMatch);
      return taskMatch;
   }

   public WorkType getWorkType() {
      return workType;
   }

   public void setWorkType(WorkType workType) {
      this.workType = workType;
   }

   public ArtifactToken getChgRptTeamWf() {
      return chgRptTeamWf;
   }

   public void setChgRptTeamWf(ArtifactToken chgRptTeamWf) {
      this.chgRptTeamWf = chgRptTeamWf;
   }

   public XResultData getRd() {
      return rd;
   }

   public void setRd(XResultData rd) {
      this.rd = rd;
   }

   public boolean isReportOnly() {
      return reportOnly;
   }

   public void setReportOnly(boolean reportOnly) {
      this.reportOnly = reportOnly;
   }

   public boolean isPersist() {
      return !reportOnly;
   }

   public ArtifactToken getDestTeamWf() {
      return destTeamWf;
   }

   public void setDestTeamWf(ArtifactToken destTeamWf) {
      this.destTeamWf = destTeamWf;
   }

   public Collection<ChangeReportTaskMatch> getTaskMatches() {
      return taskMatches;
   }

   public void setTaskMatches(Collection<ChangeReportTaskMatch> taskMatches) {
      this.taskMatches = taskMatches;
   }

   public Set<String> getTaskNames() {
      Set<String> names = new HashSet<>();
      for (ChangeReportTaskMatch taskMatch : getTaskMatches()) {
         if (taskMatch.getMatchType() == ChangeReportTaskMatchType.Match || taskMatch.getMatchType() == ChangeReportTaskMatchType.Manual) {
            names.add(taskMatch.getTaskName());
         }
      }
      return names;
   }

   public ArtifactId getToChgArt(String name) {
      for (ChangeReportTaskMatch taskMatch : getTaskMatches()) {
         if (taskMatch.getTaskName().equals(name)) {
            return taskMatch.getChgRptArt();
         }
      }
      return null;
   }

   public ArtifactToken getDestTeamDef() {
      return destTeamDef;
   }

   public void setDestTeamDef(ArtifactToken destTeamDef) {
      this.destTeamDef = destTeamDef;
   }

   public String getPartition() {
      return partition;
   }

   public void setPartition(String partition) {
      this.partition = partition;
   }

   public String getWcafeType() {
      return wcafeType;
   }

   public void setWcafeType(String wcafeType) {
      this.wcafeType = wcafeType;
   }

   public AutoGenVersion getAutoGenVersion() {
      return autoGenVersion;
   }

   public void setAutoGenVersion(AutoGenVersion autoGenVersion) {
      this.autoGenVersion = autoGenVersion;
   }

   public NewTaskSet getNewTaskSet() {
      return newTaskSet;
   }

   public void setNewTaskSet(NewTaskSet newTaskSet) {
      this.newTaskSet = newTaskSet;
   }

}
