/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.query;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.review.ReviewFormalType;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class AtsSearchData {

   private long id;
   private String searchName = "";
   private String userId = "";
   private String title = "";
   private List<StateType> stateTypes;
   private AtsSearchUserType userType;
   private List<WorkItemType> workItemTypes;
   private List<Long> teamDefIds;
   private List<Long> aiIds;
   private Long versionId = 0L;
   private String state = "";
   private Long programId = 0L;
   private Long insertionId = 0L;
   private Long insertionActivityId = 0L;
   private Long workPackageId = 0L;
   private String colorTeam = "";
   private String namespace = "";
   private ReviewFormalType reviewType;
   private ReleasedOption releasedOption;

   public AtsSearchData() {
      // for jackson deserialization
      stateTypes = new LinkedList<>();
      workItemTypes = new LinkedList<>();
      teamDefIds = new LinkedList<>();
      aiIds = new LinkedList<>();
      id = Lib.generateId();
   }

   public AtsSearchData(String searchName) {
      this();
      this.searchName = searchName;
   }

   public AtsSearchData copy() {
      AtsSearchData item = new AtsSearchData(searchName);
      return copy(item);
   }

   protected AtsSearchData copy(AtsSearchData item) {
      item.id = id;
      item.setTitle(getTitle());
      item.getStateTypes().addAll(getStateTypes());
      item.setUserType(getUserType());
      item.setUserId(getUserId());
      item.getWorkItemTypes().addAll(getWorkItemTypes());
      item.setTeamDefIds(getTeamDefIds());
      item.setAiIds(getAiIds());
      item.setVersionId(getVersionId());
      item.setState(getState());
      item.setProgramId(getProgramId());
      item.setInsertionId(getInsertionId());
      item.setInsertionActivityId(getInsertionActivityId());
      item.setWorkPackageId(getWorkPackageId());
      item.setColorTeam(getColorTeam());
      item.setReviewType(getReviewType());
      return item;
   }

   public AtsSearchUserType getUserType() {
      return userType;
   }

   public void setUserType(AtsSearchUserType userType) {
      this.userType = userType;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getSearchName() {
      return searchName;
   }

   public void setSearchName(String searchName) {
      this.searchName = searchName;
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
      Conditions.checkExpressionFailOnTrue(id <= 0, "Can't set id to 0");
      this.id = id;
   }

   @Override
   public String toString() {
      return searchName;
   }

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public List<StateType> getStateTypes() {
      return stateTypes;
   }

   public void setStateTypes(List<StateType> stateTypes) {
      this.stateTypes = stateTypes;
   }

   public List<WorkItemType> getWorkItemTypes() {
      return workItemTypes;
   }

   public void setWorkItemTypes(List<WorkItemType> workItemTypes) {
      this.workItemTypes = workItemTypes;
   }

   public List<Long> getTeamDefIds() {
      return teamDefIds;
   }

   public void setTeamDefIds(List<Long> teamDefIds) {
      this.teamDefIds = teamDefIds;
   }

   public Long getVersionId() {
      return versionId;
   }

   public void setVersionId(Long versionId) {
      this.versionId = versionId;
   }

   public List<Long> getAiIds() {
      return aiIds;
   }

   public void setAiIds(List<Long> aiIds) {
      this.aiIds = aiIds;
   }

   public String getState() {
      return state;
   }

   public void setState(String state) {
      this.state = state;
   }

   public Long getProgramId() {
      return programId;
   }

   public void setProgramId(Long programId) {
      this.programId = programId;
   }

   public Long getInsertionId() {
      return insertionId;
   }

   public void setInsertionId(Long insertionId) {
      this.insertionId = insertionId;
   }

   public Long getInsertionActivityId() {
      return insertionActivityId;
   }

   public void setInsertionActivityId(Long insertionActivityId) {
      this.insertionActivityId = insertionActivityId;
   }

   public Long getWorkPackageId() {
      return workPackageId;
   }

   public void setWorkPackageId(Long workPackageId) {
      this.workPackageId = workPackageId;
   }

   public String getColorTeam() {
      return colorTeam;
   }

   public void setColorTeam(String colorTeam) {
      this.colorTeam = colorTeam;
   }

   public String getNamespace() {
      return namespace;
   }

   public void setNamespace(String namespace) {
      this.namespace = namespace;
   }

   public ReviewFormalType getReviewType() {
      return reviewType;
   }

   public void setReviewType(ReviewFormalType reviewType) {
      this.reviewType = reviewType;
   }

   public ReleasedOption getReleasedOption() {
      return releasedOption;
   }

   public void setReleasedOption(ReleasedOption releasedOption) {
      this.releasedOption = releasedOption;
   }

}
