/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.ats.core.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.core.workflow.state.SimpleTeamState;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.type.Named;

/**
 * @author Donald G. Dunne
 */
public class MockWorkItem implements IAtsWorkItem {

   private Long id;
   private String name;
   private String atsId;
   private List<AtsUser> assignees = new ArrayList<>();
   private List<AtsUser> implementers = new ArrayList<>();
   private IAtsTeamWorkflow parentTeamWorkflow;
   private IAtsLog log;
   private WorkDefinition workDefinition;
   private StateDefinition stateDefinition;
   private AtsUser createdBy;
   private Date createdDate;
   private AtsUser completedBy;
   private AtsUser cancelledBy;
   private String completedFromState;
   private String cancelledFromState;
   private String artifactTypeName;
   private Date completedDate;
   private Date cancelledDate;
   private String cancelledReason;
   private IAtsAction parentAction;
   private AtsApi atsApi;
   private Collection<WorkType> workTypes = new ArrayList<>();
   private Collection<String> tags = new ArrayList<>();
   private String currentStateName;
   private StateType currentStateType;
   private ArtifactTypeToken artifactType;

   public MockWorkItem(Long id, String name) {
      this.id = id;
      this.name = name;
      this.atsId = name;
   }

   @Override
   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   @Override
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String getAtsId() {
      return atsId;
   }

   public void setAtsId(String atsId) {
      this.atsId = atsId;
   }

   @Override
   public List<AtsUser> getAssignees() {
      return assignees;
   }

   public void setAssignees(List<AtsUser> assignees) {
      this.assignees = assignees;
   }

   @Override
   public Collection<AtsUser> getImplementers() {
      return implementers;
   }

   public void setImplementers(List<AtsUser> implementers) {
      this.implementers = implementers;
   }

   public void addImplementer(AtsUser user) {
      implementers.add(user);
   }

   @Override
   public IAtsTeamWorkflow getParentTeamWorkflow() {
      return parentTeamWorkflow;
   }

   public void setParentTeamWorkflow(IAtsTeamWorkflow parentTeamWorkflow) {
      this.parentTeamWorkflow = parentTeamWorkflow;
   }

   @Override
   public IAtsLog getLog() {
      return log;
   }

   public void setLog(IAtsLog log) {
      this.log = log;
   }

   @Override
   public WorkDefinition getWorkDefinition() {
      return workDefinition;
   }

   public void setWorkDefinition(WorkDefinition workDefinition) {
      this.workDefinition = workDefinition;
   }

   @Override
   public StateDefinition getStateDefinition() {
      return stateDefinition;
   }

   public void setStateDefinition(StateDefinition stateDefinition) {
      this.stateDefinition = stateDefinition;
   }

   @Override
   public AtsUser getCreatedBy() {
      return createdBy;
   }

   public void setCreatedBy(AtsUser createdBy) {
      this.createdBy = createdBy;
   }

   @Override
   public Date getCreatedDate() {
      return createdDate;
   }

   public void setCreatedDate(Date createdDate) {
      this.createdDate = createdDate;
   }

   @Override
   public AtsUser getCompletedBy() {
      return completedBy;
   }

   public void setCompletedBy(AtsUser completedBy) {
      this.completedBy = completedBy;
   }

   @Override
   public AtsUser getCancelledBy() {
      return cancelledBy;
   }

   public void setCancelledBy(AtsUser cancelledBy) {
      this.cancelledBy = cancelledBy;
   }

   @Override
   public String getCompletedFromState() {
      return completedFromState;
   }

   public void setCompletedFromState(String completedFromState) {
      this.completedFromState = completedFromState;
   }

   @Override
   public String getCancelledFromState() {
      return cancelledFromState;
   }

   public void setCancelledFromState(String cancelledFromState) {
      this.cancelledFromState = cancelledFromState;
   }

   @Override
   public String getArtifactTypeName() {
      return artifactTypeName;
   }

   public void setArtifactTypeName(String artifactTypeName) {
      this.artifactTypeName = artifactTypeName;
   }

   @Override
   public Date getCompletedDate() {
      return completedDate;
   }

   public void setCompletedDate(Date completedDate) {
      this.completedDate = completedDate;
   }

   @Override
   public Date getCancelledDate() {
      return cancelledDate;
   }

   public void setCancelledDate(Date cancelledDate) {
      this.cancelledDate = cancelledDate;
   }

   @Override
   public String getCancelledReason() {
      return cancelledReason;
   }

   public void setCancelledReason(String cancelledReason) {
      this.cancelledReason = cancelledReason;
   }

   @Override
   public IAtsAction getParentAction() {
      return parentAction;
   }

   public void setParentAction(IAtsAction parentAction) {
      this.parentAction = parentAction;
   }

   @Override
   public void clearCaches() {
      // do nothing
   }

   @Override
   public AtsApi getAtsApi() {
      return atsApi;
   }

   public void setAtsApi(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public Collection<WorkType> getWorkTypes() {
      return workTypes;
   }

   public void setWorkTypes(Collection<WorkType> workTypes) {
      this.workTypes = workTypes;
   }

   @Override
   public boolean isWorkType(WorkType workType) {
      return workTypes != null && workTypes.contains(workType);
   }

   @Override
   public Collection<String> getTags() {
      return tags;
   }

   public void setTags(Collection<String> tags) {
      this.tags = tags;
   }

   @Override
   public boolean hasTag(String tag) {
      return tags != null && tags.contains(tag);
   }

   @Override
   public String getCurrentStateName() {
      return currentStateName != null ? currentStateName : "";
   }

   public void setCurrentStateName(String currentStateName) {
      this.currentStateName = currentStateName;
   }

   @Override
   public StateType getCurrentStateType() {
      return currentStateType;
   }

   public void setCurrentStateType(StateType currentStateType) {
      this.currentStateType = currentStateType;
   }

   @Override
   public IStateToken getCurrentState() {
      return new SimpleTeamState(getCurrentStateName(), getCurrentStateType());
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return artifactType;
   }

   public void setArtifactType(ArtifactTypeToken artifactType) {
      this.artifactType = artifactType;
   }

   @Override
   public AtsUser getUserByUserId(String userId) {
      return null;
   }

   @Override
   public void reload() {
      // do nothing
   }

   @Override
   public List<String> getAttributesToStringList(AttributeTypeId attributeType) {
      return Collections.emptyList();
   }

   @Override
   public TransactionToken setSoleAttributeValue(AttributeTypeString attrType, Object value, String txComment) {
      return TransactionToken.SENTINEL;
   }

   @Override
   public int getAttributeCount(AttributeTypeId attributeType) {
      return 0;
   }

   @Override
   public int compareTo(Named o) {
      if (name != null && o != null) {
         return name.compareTo(o.getName());
      }
      return 0;
   }

   @Override
   public boolean isSprint() {
      return false;
   }

   @Override
   public String toStringWithId() {
      return String.format("[%s]-[%s]-[%s]", getName(), getAtsId(), getIdString());
   }

}
