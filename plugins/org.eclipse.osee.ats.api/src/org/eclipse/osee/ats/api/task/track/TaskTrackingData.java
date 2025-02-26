/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.api.task.track;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class TaskTrackingData {

   // User initiating request.  Will author tx and be Originator
   String asUserArtId;
   String title;
   String description;
   ChangeTypes changeType;
   String priority;
   // Actionable Item for new Team Workflow
   String aiArtId;
   XResultData results = new XResultData();
   String transactionComment;
   Map<String, String> attrValues = new HashMap<>();
   TaskTrackItems trackItems = new TaskTrackItems();
   // Artifact on Common containing additional tasks to create
   String taskTrackArtId;
   String transitionTo;
   String assignees; // Comma-separated list of assignee Artifact IDs
   ArtifactToken teamWf = ArtifactToken.SENTINEL;
   boolean tasksReopen = false;

   public TaskTrackingData() {
      // jax-rs
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public ChangeTypes getChangeType() {
      return changeType;
   }

   public void setChangeType(ChangeTypes changeType) {
      this.changeType = changeType;
   }

   public String getPriority() {
      return priority;
   }

   public void setPriority(String priority) {
      this.priority = priority;
   }

   public String getTransactionComment() {
      return transactionComment;
   }

   public void setTransactionComment(String transactionComment) {
      this.transactionComment = transactionComment;
   }

   public Map<String, String> getAttrValues() {
      return attrValues;
   }

   public void setAttrValues(Map<String, String> attrValues) {
      this.attrValues = attrValues;
   }

   public String getAssignees() {
      return assignees;
   }

   public void setAssignees(String assignees) {
      this.assignees = assignees;
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

   public String getAsUserArtId() {
      return asUserArtId;
   }

   public void setAsUserArtId(String asUserArtId) {
      this.asUserArtId = asUserArtId;
   }

   public TaskTrackItems getTrackItems() {
      return trackItems;
   }

   public void setTrackItems(TaskTrackItems trackItems) {
      this.trackItems = trackItems;
   }

   public String getAiArtId() {
      return aiArtId;
   }

   public void setAiArtId(String aiArtId) {
      this.aiArtId = aiArtId;
   }

   public String getTaskTrackArtId() {
      return taskTrackArtId;
   }

   public void setTaskTrackArtId(String taskTrackArtId) {
      this.taskTrackArtId = taskTrackArtId;
   }

   public String getTransitionTo() {
      return transitionTo;
   }

   public void setTransitionTo(String transitionTo) {
      this.transitionTo = transitionTo;
   }

   public ArtifactToken getTeamWf() {
      return teamWf;
   }

   public void setTeamWf(ArtifactToken teamWf) {
      this.teamWf = teamWf;
   }

   public boolean getTasksReopen() {
      return tasksReopen;
   }

   public void setTasksReopen(boolean tasksReopen) {
      this.tasksReopen = tasksReopen;
   }

}
