/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.api.workdef.model.web;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.WorkflowAttachment;
import org.eclipse.osee.framework.core.data.AttributePojo;
import org.eclipse.osee.framework.core.data.AttributePojoBaseId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.MaterialIcon;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * This class is intended as a light-weight pojo for use by external apps and tools to easily access the data from a
 * workflow. It is also intended as an abstraction of the workitem to keep a buffer between the refactorable Work Item
 * and Work Definition and the external API. It should not be refactored without testing these external apps.
 *
 * @author Donald G. Dunne
 */
public class WorkflowData extends NamedIdBase {

   // -----------------------------------------------------------------
   // Workflow (instance) data - identity and status
   // -----------------------------------------------------------------
   private boolean editable = true;

   private String artTypeName = "";
   private MaterialIcon artTypeIcon = MaterialIcon.SENTINEL;

   private final List<AttributePojoBaseId<?>> assigneeNames = new ArrayList<>();
   private AttributePojoBaseId<?> originator = AttributePojoBaseId.valueOf(Id.SENTINEL,
      AttributePojo.valueOf(Id.SENTINEL, AtsAttributeTypes.CreatedBy, GammaId.SENTINEL, "", "")); // art id of orig + attrs of orig
   private AttributePojoBaseId<?> teamDefinition = AttributePojoBaseId.valueOf(Id.SENTINEL,
      AttributePojo.valueOf(Id.SENTINEL, AtsAttributeTypes.TeamDefinitionReference, GammaId.SENTINEL, "", "")); // team artifact id + workflow's team name value
   private AttributePojoBaseId<?> targetedVersion = AttributePojoBaseId.valueOf(Id.SENTINEL,
      AttributePojo.valueOf(Id.SENTINEL, AtsAttributeTypes.TeamDefinitionReference, GammaId.SENTINEL, "", "")); // team artifact id + workflow's team name value
   private final List<AttributePojoBaseId<?>> aiNames = new ArrayList<>();

   private AttributePojo<?> actionId =
      AttributePojo.valueOf(Id.SENTINEL, AtsAttributeTypes.AtsId, GammaId.SENTINEL, "", "");
   private AttributePojo<?> atsId =
      AttributePojo.valueOf(Id.SENTINEL, AtsAttributeTypes.AtsId, GammaId.SENTINEL, "", "");
   private AttributePojo<?> parentWorkflowAtsId =
      AttributePojo.valueOf(Id.SENTINEL, AtsAttributeTypes.AtsId, GammaId.SENTINEL, "", "");
   private AttributePojo<?> createdDate =
      AttributePojo.valueOf(Id.SENTINEL, AtsAttributeTypes.CreatedDate, GammaId.SENTINEL, "", "");
   private AttributePojo<?> currentStateName =
      AttributePojo.valueOf(Id.SENTINEL, AtsAttributeTypes.CurrentStateName, GammaId.SENTINEL, "", "");
   private AttributePojo<?> currentStateType =
      AttributePojo.valueOf(Id.SENTINEL, AtsAttributeTypes.CurrentStateType, GammaId.SENTINEL, "", "");
   private AttributePojo<?> legacyPcrId =
      AttributePojo.valueOf(Id.SENTINEL, AtsAttributeTypes.LegacyPcrId, GammaId.SENTINEL, "", "");
   private AttributePojo<?> points =
      AttributePojo.valueOf(Id.SENTINEL, AtsAttributeTypes.Points, GammaId.SENTINEL, "", "");
   private AttributePojo<?> changeType =
      AttributePojo.valueOf(Id.SENTINEL, AtsAttributeTypes.ChangeType, GammaId.SENTINEL, "", "");

   private final List<AttributePojo<?>> pcrIds = new ArrayList<>();

   private final List<WorkflowAttachment> attachments = new ArrayList<>();

   // -----------------------------------------------------------------
   // Work Definition (structure) metadata
   // -----------------------------------------------------------------
   @JsonSerialize(using = ToStringSerializer.class)
   private Long workDefId = Id.SENTINEL;

   private String workDefName = "";
   private WfdWidgetComposite headerComposite = new WfdWidgetComposite("Header");
   private List<WfdState> workDefStates = new ArrayList<>();
   private List<String> priorities = new ArrayList<>();
   private List<String> workDefOption = new ArrayList<>();

   // -----------------------------------------------------------------
   // Raw Attribute Data
   // -----------------------------------------------------------------
   private final List<AttributePojo<?>> attributes = new ArrayList<>();

   // -----------------------------------------------------------------
   // Transaction
   // -----------------------------------------------------------------
   private TransactionId tx;

   public WorkflowData() {
      // for jax-rs
   }

   public WorkflowData(Long id, String name) {
      super(id, name);
   }

   public void addState(WfdState state) {
      this.workDefStates.add(state);
   }

   public boolean isEditable() {
      return editable;
   }

   public void setEditable(boolean editable) {
      this.editable = editable;
   }

   public AttributePojo<?> getAtsId() {
      return atsId;
   }

   public void setAtsId(AttributePojo<?> atsId) {
      this.atsId = atsId;
   }

   public String getArtTypeName() {
      return artTypeName;
   }

   public void setArtTypeName(String artTypeName) {
      this.artTypeName = artTypeName;
   }

   public AttributePojo<?> getCurrentStateName() {
      return currentStateName;
   }

   public void setCurrentStateName(AttributePojo<?> currentStateName) {
      this.currentStateName = currentStateName;
   }

   public AttributePojo<?> getCreatedDate() {
      return createdDate;
   }

   public void setCreatedDate(AttributePojo<?> createdDate) {
      this.createdDate = createdDate;
   }

   public AttributePojo<?> getCurrentStateType() {
      return currentStateType;
   }

   public void setCurrentStateType(AttributePojo<?> currentStateType) {
      this.currentStateType = currentStateType;
   }

   public List<AttributePojoBaseId<?>> getAssigneeNames() {
      return assigneeNames;
   }

   public void addAssigneeName(AttributePojoBaseId<?> assigneeName) {
      this.assigneeNames.add(assigneeName);
   }

   public AttributePojoBaseId<?> getOriginator() {
      return originator;
   }

   public void setOriginator(AttributePojoBaseId<?> originator) {
      this.originator = originator;
   }

   public AttributePojoBaseId<?> getTeamDefinition() {
      return teamDefinition;
   }

   public void setTeamDefinition(AttributePojoBaseId<?> teamDefinition) {
      this.teamDefinition = teamDefinition;
   }

   public List<AttributePojoBaseId<?>> getAiNames() {
      return aiNames;
   }

   public void addAiName(AttributePojoBaseId<?> aiName) {
      this.aiNames.add(aiName);
   }

   public AttributePojo<?> getParentWorkflowAtsId() {
      return parentWorkflowAtsId;
   }

   public void setParentWorkflowAtsId(AttributePojo<?> parentWorkflowAtsId) {
      this.parentWorkflowAtsId = parentWorkflowAtsId;
   }

   public AttributePojo<?> getActionId() {
      return actionId;
   }

   public void setActionId(AttributePojo<?> actionId) {
      this.actionId = actionId;
   }

   public AttributePojo<?> getLegacyPcrId() {
      return legacyPcrId;
   }

   public void setLegacyPcrId(AttributePojo<?> legacyPcrId) {
      this.legacyPcrId = legacyPcrId;
   }

   public List<AttributePojo<?>> getPcrIds() {
      return pcrIds;
   }

   public void addPcrId(AttributePojo<?> pcrId) {
      this.pcrIds.add(pcrId);
   }

   public AttributePojoBaseId<?> getTargetedVersion() {
      return targetedVersion;
   }

   public void setTargetedVersion(AttributePojoBaseId<?> targetedVersion) {
      this.targetedVersion = targetedVersion;
   }

   public List<WorkflowAttachment> getAttachments() {
      return attachments;
   }

   public void addAttachment(WorkflowAttachment attachment) {
      this.attachments.add(attachment);
   }

   public Long getWorkDefId() {
      return workDefId;
   }

   public void setWorkDefId(Long workDefId) {
      this.workDefId = workDefId;
   }

   public String getWorkDefName() {
      return workDefName;
   }

   public void setWorkDefName(String workDefName) {
      this.workDefName = workDefName;
   }

   public WfdWidgetComposite getHeaderComposite() {
      return headerComposite;
   }

   public void setHeaderComposite(WfdWidgetComposite headerComposite) {
      this.headerComposite = headerComposite;
   }

   public List<WfdState> getWorkDefStates() {
      return workDefStates;
   }

   public void setWorkDefStates(List<WfdState> workDefStates) {
      this.workDefStates = workDefStates;
   }

   public List<String> getPriorities() {
      return priorities;
   }

   public void setPriorities(List<String> priorities) {
      this.priorities = priorities;
   }

   public List<String> getWorkDefOption() {
      return workDefOption;
   }

   public void setWorkDefOption(List<String> workDefOption) {
      this.workDefOption = workDefOption;
   }

   public List<AttributePojo<?>> getAttributes() {
      return attributes;
   }

   public void addAttribute(AttributePojo<?> attribute) {
      this.attributes.add(attribute);
   }

   public TransactionId getTx() {
      return tx;
   }

   public void setTx(TransactionId tx) {
      this.tx = tx;
   }

   public AttributePojo<?> getPoints() {
      return this.points;
   }

   public void setPoints(AttributePojo<?> points) {
      this.points = points;
   }

   public AttributePojo<?> getChangeType() {
      return this.changeType;
   }

   public void setChangeType(AttributePojo<?> changeType) {
      this.changeType = changeType;
   }

   public void setArtifactTypeIcon(MaterialIcon artTypeIcon) {
      this.artTypeIcon = artTypeIcon;
   }

   public MaterialIcon getArtifactTypeIcon() {
      return this.artTypeIcon;
   }
}