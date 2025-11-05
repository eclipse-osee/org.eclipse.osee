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
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * This class is intended as a light-weight pojo for use by external apps and tools to easily access the data from a
 * workflow. It is also intended as an abstraction of the workitem to keep a buffer between the refactorable Work Item
 * and Work Definition and the external API. It should not be refactored without testing these external apps.
 *
 * @author Donald G. Dunne
 */
public class WorkflowData extends NamedIdBase {

   // Workflow
   boolean editable = true;
   String atsId;
   String currentStateName;
   String assigneesStr;
   String originator;
   String creationDate;
   StateType currentStateType;
   String teamName;
   List<String> aiNames = new ArrayList<>();;
   String artTypeName;
   String artTypeImage;
   String parentAtsId;
   String actionId;
   String legacyPcrId;
   String pcrIds;
   List<WfdAttachment> attachments = new ArrayList<>();
   ArtifactToken targetedVersion = ArtifactToken.SENTINEL;

   // Work Definition
   @JsonSerialize(using = ToStringSerializer.class)
   Long workDefId;
   String workDefName;
   WfdWidgetComposite headerComposite = new WfdWidgetComposite("Header");
   List<WfdState> workDefStates = new ArrayList<>();
   List<String> changeTypes = new ArrayList<>();
   List<String> priorities = new ArrayList<>();
   List<String> workDefOption = new ArrayList<>();

   // Raw Data
   List<WfdAttribute> attributes = new ArrayList<>();

   // Transaction
   TransactionId tx;

   public WorkflowData() {
      // for jax-rs
   }

   public WorkflowData(Long id, String name) {
      super(id, name);
   }

   public void addState(WfdState state) {
      this.workDefStates.add(state);
   }

   public List<String> getChangeTypes() {
      return changeTypes;
   }

   public void setChangeTypes(List<String> changeTypes) {
      this.changeTypes = changeTypes;
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

   public List<WfdState> getWorkDefStates() {
      return workDefStates;
   }

   public void setWorkDefStates(List<WfdState> workDefStates) {
      this.workDefStates = workDefStates;
   }

   public String getArtTypeName() {
      return artTypeName;
   }

   public void setArtTypeName(String artTypeName) {
      this.artTypeName = artTypeName;
   }

   public TransactionId getTx() {
      return tx;
   }

   public void setTx(TransactionId tx) {
      this.tx = tx;
   }

   public String getTeamName() {
      return teamName;
   }

   public void setTeamName(String teamName) {
      this.teamName = teamName;
   }

   public String getCurrentStateName() {
      return currentStateName;
   }

   public void setCurrentStateName(String currentStateName) {
      this.currentStateName = currentStateName;
   }

   public String getAssigneesStr() {
      return assigneesStr;
   }

   public void setAssigneesStr(String assigneesStr) {
      this.assigneesStr = assigneesStr;
   }

   public StateType getCurrentStateType() {
      return currentStateType;
   }

   public void setCurrentStateType(StateType currentStateType) {
      this.currentStateType = currentStateType;
   }

   public String getArtTypeImage() {
      return artTypeImage;
   }

   public void setArtTypeImage(String artTypeImage) {
      this.artTypeImage = artTypeImage;
   }

   public String getAtsId() {
      return atsId;
   }

   public void setAtsId(String atsId) {
      this.atsId = atsId;
   }

   public String getParentAtsId() {
      return parentAtsId;
   }

   public void setParentAtsId(String parentAtsId) {
      this.parentAtsId = parentAtsId;
   }

   public String getActionId() {
      return actionId;
   }

   public void setActionId(String actionId) {
      this.actionId = actionId;
   }

   public String getLegacyPcrId() {
      return legacyPcrId;
   }

   public void setLegacyPcrId(String legacyPcrId) {
      this.legacyPcrId = legacyPcrId;
   }

   public String getPcrIds() {
      return pcrIds;
   }

   public void setPcrIds(String pcrIds) {
      this.pcrIds = pcrIds;
   }

   public String getOriginator() {
      return originator;
   }

   public void setOriginator(String originator) {
      this.originator = originator;
   }

   public String getCreationDate() {
      return creationDate;
   }

   public void setCreationDate(String creationDate) {
      this.creationDate = creationDate;
   }

   public List<WfdAttachment> getAttachments() {
      return attachments;
   }

   public void setAttachments(List<WfdAttachment> attachments) {
      this.attachments = attachments;
   }

   public List<WfdAttribute> getAttributes() {
      return attributes;
   }

   public void setAttributes(List<WfdAttribute> attributes) {
      this.attributes = attributes;
   }

   public ArtifactToken getTargetedVersion() {
      return targetedVersion;
   }

   public void setTargetedVersion(ArtifactToken targetedVersion) {
      this.targetedVersion = targetedVersion;
   }

   public List<String> getAiNames() {
      return aiNames;
   }

   public void setAiNames(List<String> aiNames) {
      this.aiNames = aiNames;
   }

   public WfdWidgetComposite getHeaderComposite() {
      return headerComposite;
   }

   public void setHeaderComposite(WfdWidgetComposite headerComposite) {
      this.headerComposite = headerComposite;
   }

   public boolean isEditable() {
      return editable;
   }

   public void setEditable(boolean editable) {
      this.editable = editable;
   }
}
