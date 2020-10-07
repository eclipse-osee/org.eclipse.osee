/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.common.clientserver.dependent.datamodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.icteam.common.artifact.interfaces.ITransferableArtifact;

/**
 * This Class is having the properties related to task
 *
 * @author Ajay Chandrahasan
 */
public class TeamWorkFlowArtifact extends TransferableArtifact {

   // private OrcsApi orcsApi = OseeCoreData.getOrcsApi();
   private TransferableArtifact actionableItem;
   private TransferableArtifact version;
   private TransferableArtifact team;
   private TransferableArtifact buildArtifact;

   private String description;
   private String estimatedHours;
   private String priority;
   private String changeType;
   private String storyPointType;
   private String rank;
   private Date createdDate;
   private String currentState;
   private String totalEstimatedHours;
   private String totalHoursSpent;
   private String hoursSpent;
   private String percentageCompleted;
   private String remainingHours;
   private String totalRemainingHours;
   private String currentStateString;
   private List<ITransferableArtifact> assignee;
   private Date expectedDate;
   private Date completionDate;
   private String workFlowDefinition;
   private final List<ITransferableArtifact> listTeamMembersTeamLeads = new ArrayList<ITransferableArtifact>();
   private final List<TransferableArtifact> listComponents = new ArrayList<TransferableArtifact>();
   private final List<ITransferableArtifact> listVersions = new ArrayList<ITransferableArtifact>();
   private final List<TeamWorkFlowArtifact> agileTaskLinkList = new ArrayList<TeamWorkFlowArtifact>();
   private List<String> completedSates = new ArrayList<String>();
   private List<ITransferableArtifact> listVersionsDropDown = new ArrayList<ITransferableArtifact>();

   /**
    * gets build artifact
    *
    * @return
    */
   public TransferableArtifact getBuildArtifact() {
      return this.buildArtifact;
   }

   /**
    * set build artifact
    *
    * @param buildArtifact
    */
   public void setBuildArtifact(final TransferableArtifact buildArtifact) {
      this.buildArtifact = buildArtifact;
   }

   /**
    * get list of versions
    *
    * @return
    */
   public List<ITransferableArtifact> getListVersionsDropDown() {
      return this.listVersionsDropDown;
   }

   /**
    * set versions list
    *
    * @param listVersionsDropDown
    */
   public void setListVersionsDropDown(final List<ITransferableArtifact> listVersionsDropDown) {
      this.listVersionsDropDown = listVersionsDropDown;
   }

   /**
    * get components list
    *
    * @return
    */
   public List<ITransferableArtifact> getListCompoenntsDropDown() {
      return this.listCompoenntsDropDown;
   }

   /**
    * sets components list
    *
    * @param listCompoenntsDropDown
    */
   public void setListCompoenntsDropDown(final List<ITransferableArtifact> listCompoenntsDropDown) {
      this.listCompoenntsDropDown = listCompoenntsDropDown;
   }

   private List<ITransferableArtifact> listCompoenntsDropDown = new ArrayList<ITransferableArtifact>();

   private List<String> toStateAssignee;

   /**
    * To support Agile
    * 
    * @return the storyPointType
    */
   public String getStoryPointType() {
      return this.storyPointType;
   }

   /**
    * @param storyPointType the storyPointType to set
    */
   public void setStoryPointType(final String storyPointType) {
      this.storyPointType = storyPointType;
   }

   /**
    * To support Agile
    * 
    * @return the storyPointType
    */
   public String getRank() {
      return this.rank;
   }

   /**
    * @param storyPointType the storyPointType to set
    */
   public void setRank(final String rank) {
      this.rank = rank;
   }

   /**
    * gets to State of Assignee
    *
    * @return
    */

   public List<String> getToStateAssignee() {
      return this.toStateAssignee;
   }

   /**
    * sets to State of Assignee
    *
    * @param toStateAssignee
    */
   public void setToStateAssignee(final List<String> toStateAssignee) {
      this.toStateAssignee = toStateAssignee;
   }

   /**
    * gets expected date
    *
    * @return
    */
   public Date getExpectedDate() {
      return this.expectedDate;
   }

   /**
    * setting expected date
    *
    * @param expectedDate
    */
   public void setExpectedDate(final Date expectedDate) {
      this.expectedDate = expectedDate;
   }

   /**
    * getting total remaining hours for a task
    *
    * @return
    */
   public String getTotalRemainingHours() {
      return this.totalRemainingHours;
   }

   /**
    * sets total remaining hours for a task
    *
    * @param totalRemainingHours
    */
   public void setTotalRemainingHours(final String totalRemainingHours) {
      this.totalRemainingHours = totalRemainingHours;
   }

   /**
    * gets hours spent on task
    *
    * @return
    */
   public String getHoursSpent() {
      return this.hoursSpent;
   }

   /**
    * sets hours spent on task
    *
    * @param hoursSpent
    */
   public void setHoursSpent(final String hoursSpent) {
      this.hoursSpent = hoursSpent;
   }

   /**
    * gets completion date of task
    *
    * @return
    */
   public Date getCompletionDate() {
      return this.completionDate;
   }

   /**
    * set completion date of the task
    *
    * @param completionDate
    */
   public void setCompletionDate(final Date completionDate) {
      this.completionDate = completionDate;
   }

   /**
    * gets team onfo
    *
    * @return
    */
   public TransferableArtifact getTeam() {
      return this.team;
   }

   /**
    * sets teams info
    *
    * @param team
    */
   public void setTeam(final TransferableArtifact team) {
      this.team = team;
   }

   /**
    * sets total estimated hours
    *
    * @param totalEstimatedHours
    */
   public void setTotalEstimatedHours(final String totalEstimatedHours) {
      this.totalEstimatedHours = totalEstimatedHours;
   }

   /**
    * gets workflow definition
    *
    * @return
    */
   public String getWorkFlowDefinition() {
      return this.workFlowDefinition;
   }

   /**
    * sets workflow definition
    *
    * @param workFlowDefinition
    */
   public void setWorkFlowDefinition(final String workFlowDefinition) {
      this.workFlowDefinition = workFlowDefinition;
   }

   /**
    * sets parent task
    *
    * @param parentTask
    */
   public void setParentTask(final TeamWorkFlowArtifact parentTask) {
      this.parentTask = parentTask;
   }

   /**
    * sets create date
    *
    * @param createdDate
    */
   public void setCreatedDate(final Date createdDate) {
      this.createdDate = createdDate;
   }

   /**
    * get verion
    *
    * @return
    */
   public TransferableArtifact getVersion() {
      return this.version;
   }

   /**
    * gets version
    *
    * @param version
    */
   public void setVersion(final TransferableArtifact version) {
      this.version = version;
   }

   /**
    * gets completed states
    *
    * @return
    */
   public List<String> getCompletedSates() {
      return this.completedSates;
   }

   /**
    * sets completed states
    *
    * @param completedSates
    */
   public void setCompletedSates(final List<String> completedSates) {
      this.completedSates = completedSates;
   }

   /**
    * get current states
    *
    * @return string value of current state
    */
   public String getCurrentStateString() {
      return this.currentStateString;
   }

   /**
    * Setting current state of type string
    *
    * @param currentStateString
    */
   public void setCurrentStateString(final String currentStateString) {
      this.currentStateString = currentStateString;
   }

   /**
    * get hours spent on a task
    *
    * @return
    */
   public String getTotalHoursSpent() {
      return this.totalHoursSpent;
   }

   /**
    * sets total total spent hours
    *
    * @param totalHoursSpent
    */
   public void setTotalHoursSpent(final String totalHoursSpent) {
      this.totalHoursSpent = totalHoursSpent;
   }

   /**
    * get list of team leads
    *
    * @return
    */
   public List<ITransferableArtifact> getTeamMembersLeads() {
      return this.listTeamMembersTeamLeads;
   }

   private TeamWorkFlowArtifact parentTask;

   /**
    * @return the listTeamMembersTeamLeads
    */
   public List<ITransferableArtifact> getListTeamMembersTeamLeads() {
      return this.listTeamMembersTeamLeads;
   }

   /**
    * @return the listComponents
    */
   public List<TransferableArtifact> getListComponents() {
      return this.listComponents;
   }

   /**
    * @return the listVersions
    */
   public List<ITransferableArtifact> getListVersions() {
      return this.listVersions;
   }

   /**
    * @param percentageCompleted the percentageCompleted to set
    */
   public void setPercentageCompleted(final String percentageCompleted) {
      this.percentageCompleted = percentageCompleted;
   }

   /**
    * @param remainingHours the remainingHours to set
    */
   public void setRemainingHours(final String remainingHours) {
      this.remainingHours = remainingHours;
   }

   /**
    * get parent of a current task
    *
    * @return
    */
   public TeamWorkFlowArtifact getParentTask() {
      return this.parentTask;
   }

   /**
    * get agile task list
    *
    * @return
    */
   public List<TeamWorkFlowArtifact> getAgileTaskLinkList() {
      return this.agileTaskLinkList;
   }

   /**
    * set asignee
    *
    * @param assigneeUser
    */
   public void setAssignee(final List<ITransferableArtifact> assigneeUser) {
      this.assignee = assigneeUser;
   }

   /**
    * get total estimated hours
    *
    * @return
    */
   public String getTotalEstimatedHours() {
      return this.totalEstimatedHours;
   }

   /**
    * get completion percentage of task
    *
    * @return
    */
   public String getPercentageCompleted() {
      return this.percentageCompleted;
   }

   /**
    * set completion persentage
    *
    * @param percenatgeComplete
    */
   public void setPercentageComplete(final String percenatgeComplete) {
      this.percentageCompleted = percenatgeComplete;
   }

   /**
    * get remaining hours
    *
    * @return
    */
   public String getRemainingHours() {
      return this.remainingHours;
   }

   /**
    * set current state
    *
    * @param currentState
    */

   public void setCurrentState(final String currentState) {
      this.currentState = currentState;
   }

   /**
    * get current state
    *
    * @return
    */
   public String getCurrentState() {
      return this.currentState;
   }

   /**
    * get created date
    *
    * @return
    */
   public Date getCreatedDate() {
      return this.createdDate;
   }

   /**
    * get description of the task
    *
    * @return
    */
   public String getDescription() {
      return this.description;
   }

   /**
    * get estimated
    *
    * @return
    */
   public String getEstimatedHours() {
      return this.estimatedHours;
   }

   /**
    * get priority
    *
    * @return
    */
   public String getPriority() {
      return this.priority;
   }

   /**
    * get change type
    *
    * @return
    */
   public String getChangeType() {
      return this.changeType;
   }

   /**
    * get actionable item
    *
    * @return
    */
   public TransferableArtifact getActionableItem() {
      return this.actionableItem;
   }

   /**
    * set actionable item
    *
    * @param item
    */
   public void setActionableItem(final TransferableArtifact item) {
      this.actionableItem = item;

   }

   /**
    * get assignee
    *
    * @return
    */
   public List<ITransferableArtifact> getAssignee() {
      return this.assignee;
   }

   /**
    * set task description
    *
    * @param description
    */
   public void setDescription(final String description) {
      this.description = description;
   }

   /**
    * set estimated hours
    *
    * @param estimatedHours
    */
   public void setEstimatedHours(final String estimatedHours) {
      this.estimatedHours = estimatedHours;
   }

   /**
    * set priority
    *
    * @param priority
    */
   public void setPriority(final String priority) {
      this.priority = priority;
   }

   /**
    * set change type
    *
    * @param changeType
    */
   public void setChangeType(final String changeType) {
      this.changeType = changeType;
   }

}
