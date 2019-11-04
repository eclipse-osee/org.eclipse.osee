/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.task.create;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * Needed matches would populate chgRptArt and taskName. If existing match, then task would be populated.
 *
 * @author Donald G. Dunne
 */
public class ChangeReportTaskMatch {

   ArtifactId chgRptArt;
   String taskName;
   ArtifactToken taskTok;
   @JsonIgnore
   IAtsTask taskWf;
   private ChangeReportTaskMatchType matchType;

   public ArtifactId getChgRptArt() {
      return chgRptArt;
   }

   public void setChgRptArt(ArtifactId chgRptArt) {
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
      return String.format("%s ChgRptArt %s for task name [%s] mapped to task %s", matchType.name(),
         chgRptArt.toString(), taskName, (taskTok == null ? "none" : taskTok.toStringWithId()));
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
}
