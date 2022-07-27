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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * Needed matches would populate chgRptArt and taskName. If existing match, then task would be populated.
 *
 * @author Donald G. Dunne
 */
public class ChangeReportTaskMatch {

   ArtifactToken chgRptArt = ArtifactToken.SENTINEL;
   String chgRptArtName;
   boolean chgRptArtDeleted;
   String taskName;
   ArtifactToken taskTok;
   @JsonIgnore
   IAtsTask taskWf;
   StaticTaskDefinition createTaskDef;
   ChangeReportTaskMatchType matchType;
   ChangeReportTaskMatchResult matchResult;
   String autoTaskGenType;

   public String getAutoTaskGenType() {
      return autoTaskGenType;
   }

   public void setAutoTaskGenType(String autoTaskGenType) {
      this.autoTaskGenType = autoTaskGenType;
   }

   public ArtifactToken getChgRptArt() {
      return chgRptArt;
   }

   public void setChgRptArt(ArtifactToken chgRptArt) {
      this.chgRptArt = chgRptArt;
   }

   public String getTaskName() {
      return taskName;
   }

   public void setTaskName(String taskName) {
      this.taskName = taskName;
   }

   @Override
   public String toString() {
      return String.format("%s ChgRptArt %s task name [%s] mapped to task %s", matchType.name(), chgRptArt.toString(),
         taskName, (taskTok == null ? "none" : taskTok.toStringWithId()));
   }

   public void setTaskWf(IAtsTask taskWf) {
      this.taskWf = taskWf;
   }

   public ArtifactToken getTaskTok() {
      return taskTok;
   }

   public void setTaskTok(ArtifactToken taskTok) {
      this.taskTok = taskTok;
   }

   public IAtsTask getTaskWf() {
      return taskWf;
   }

   public void setType(ChangeReportTaskMatchType matchType) {
      this.matchType = matchType;
   }

   public ChangeReportTaskMatchType getMatchType() {
      return matchType;
   }

   public void setMatchType(ChangeReportTaskMatchType matchType) {
      this.matchType = matchType;
   }

   public StaticTaskDefinition getCreateTaskDef() {
      return createTaskDef;
   }

   public void setCreateTaskDef(StaticTaskDefinition createTaskDef) {
      this.createTaskDef = createTaskDef;
   }

   public String getChgRptArtName() {
      return chgRptArtName;
   }

   public void setChgRptArtName(String chgRptArtName) {
      this.chgRptArtName = chgRptArtName;
   }

   public boolean isChgRptArtDeleted() {
      return chgRptArtDeleted;
   }

   public void setChgRptArtDeleted(boolean chgRptArtDeleted) {
      this.chgRptArtDeleted = chgRptArtDeleted;
   }

   public boolean isChgRptArtValid() {
      return chgRptArt.isValid();
   }

   public ChangeReportTaskMatchResult getMatchResult() {
      return matchResult;
   }

   public void setMatchResult(ChangeReportTaskMatchResult matchResult) {
      this.matchResult = matchResult;
   }

}
