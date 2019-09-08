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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class ChangeReportData {

   HashMap<String, ArtifactId> taskNamesToReqId = new HashMap<>();
   Collection<ArtifactId> addedModifiedArts = new HashSet<>();
   Collection<ArtifactId> deletedArts = new HashSet<>();
   WorkType workType;
   IAtsTeamWorkflow sourceTeamWf;
   XResultData rd;
   Map<ArtifactId, String> taskedArtToName = new HashMap<>();
   Map<ArtifactId, IAtsTask> referencedArtsToTasks = new HashMap<ArtifactId, IAtsTask>();
   boolean reportOnly;
   NewTaskData newTaskData = new NewTaskData();

   public ChangeReportData() {
   }

   public HashMap<String, ArtifactId> getTaskNamesToReqId() {
      return taskNamesToReqId;
   }

   public void setTaskNamesToReqId(HashMap<String, ArtifactId> taskNamesToReqId) {
      this.taskNamesToReqId = taskNamesToReqId;
   }

   public Collection<ArtifactId> getAddedModifiedArts() {
      return addedModifiedArts;
   }

   public void setAddedModifiedArts(Collection<ArtifactId> addedModifiedArts) {
      this.addedModifiedArts = addedModifiedArts;
   }

   public Collection<ArtifactId> getDeletedArts() {
      return deletedArts;
   }

   public void setDeletedArts(Collection<ArtifactId> deletedArts) {
      this.deletedArts = deletedArts;
   }

   public WorkType getWorkType() {
      return workType;
   }

   public void setWorkType(WorkType workType) {
      this.workType = workType;
   }

   public IAtsTeamWorkflow getSourceTeamWf() {
      return sourceTeamWf;
   }

   public void setSourceTeamWf(IAtsTeamWorkflow sourceTeamWf) {
      this.sourceTeamWf = sourceTeamWf;
   }

   public XResultData getRd() {
      return rd;
   }

   public void setRd(XResultData rd) {
      this.rd = rd;
   }

   public Map<ArtifactId, String> getTaskedArtToName() {
      return taskedArtToName;
   }

   public void setTaskedArtToName(Map<ArtifactId, String> taskedArtToName) {
      this.taskedArtToName = taskedArtToName;
   }

   public void addTaskedArtToName(ArtifactId art, String name) {
      this.taskedArtToName.put(art, name);
   }

   public Map<ArtifactId, IAtsTask> getReferencedArtsToTasks() {
      return referencedArtsToTasks;
   }

   public void setReferencedArtsToTasks(Map<ArtifactId, IAtsTask> referencedArtsToTasks) {
      this.referencedArtsToTasks = referencedArtsToTasks;
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

   public NewTaskData getNewTaskData() {
      return newTaskData;
   }

   public void setNewTaskData(NewTaskData newTaskData) {
      this.newTaskData = newTaskData;
   }

}
