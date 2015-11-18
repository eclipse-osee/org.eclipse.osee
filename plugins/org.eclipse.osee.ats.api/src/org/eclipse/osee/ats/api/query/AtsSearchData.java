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
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class AtsSearchData {

   private long uuid;
   private String searchName = "";
   private String userId = "";
   private String title = "";
   private List<StateType> stateTypes;
   private AtsSearchUserType userType;
   private List<WorkItemType> workItemTypes;
   private List<Long> teamDefUuids;
   private List<Long> aiUuids;
   private Long versionUuid = 0L;
   private String state = "";
   private Long programUuid = 0L;
   private Long insertionUuid = 0L;
   private Long insertionActivityUuid = 0L;
   private Long workPackageUuid = 0L;
   private String colorTeam = "";
   private String namespace = "";

   public AtsSearchData() {
      // for jackson deserialization
      stateTypes = new LinkedList<>();
      workItemTypes = new LinkedList<>();
      teamDefUuids = new LinkedList<>();
      aiUuids = new LinkedList<>();
      uuid = Lib.generateUuid();
   }

   public AtsSearchData(String searchName) {
      this();
      this.searchName = searchName;
   }

   public AtsSearchData copy() {
      AtsSearchData item = new AtsSearchData(searchName);
      item.uuid = uuid;
      item.setTitle(getTitle());
      item.getStateTypes().addAll(getStateTypes());
      item.setUserType(getUserType());
      item.setUserId(getUserId());
      item.getWorkItemTypes().addAll(getWorkItemTypes());
      item.setTeamDefUuids(getTeamDefUuids());
      item.setAiUuids(getAiUuids());
      item.setVersionUuid(getVersionUuid());
      item.setState(getState());
      item.setProgramUuid(getProgramUuid());
      item.setInsertionUuid(getInsertionUuid());
      item.setInsertionActivityUuid(getInsertionActivityUuid());
      item.setWorkPackageUuid(getWorkPackageUuid());
      item.setColorTeam(getColorTeam());
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

   public long getUuid() {
      return uuid;
   }

   public void setUuid(long uuid) {
      Conditions.checkExpressionFailOnTrue(uuid <= 0, "Can't set uuid to 0");
      this.uuid = uuid;
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

   public List<Long> getTeamDefUuids() {
      return teamDefUuids;
   }

   public void setTeamDefUuids(List<Long> teamDefUuids) {
      this.teamDefUuids = teamDefUuids;
   }

   public Long getVersionUuid() {
      return versionUuid;
   }

   public void setVersionUuid(Long versionUuid) {
      this.versionUuid = versionUuid;
   }

   public List<Long> getAiUuids() {
      return aiUuids;
   }

   public void setAiUuids(List<Long> aiUuids) {
      this.aiUuids = aiUuids;
   }

   public String getState() {
      return state;
   }

   public void setState(String state) {
      this.state = state;
   }

   public Long getProgramUuid() {
      return programUuid;
   }

   public void setProgramUuid(Long programUuid) {
      this.programUuid = programUuid;
   }

   public Long getInsertionUuid() {
      return insertionUuid;
   }

   public void setInsertionUuid(Long insertionUuid) {
      this.insertionUuid = insertionUuid;
   }

   public Long getInsertionActivityUuid() {
      return insertionActivityUuid;
   }

   public void setInsertionActivityUuid(Long insertionActivityUuid) {
      this.insertionActivityUuid = insertionActivityUuid;
   }

   public Long getWorkPackageUuid() {
      return workPackageUuid;
   }

   public void setWorkPackageUuid(Long workPackageUuid) {
      this.workPackageUuid = workPackageUuid;
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

}
