/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.api.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.CreateOption;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.BooleanState;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class NewActionData {

   ArtifactId asUser = ArtifactId.SENTINEL;
   ArtifactId originator = ArtifactId.SENTINEL;
   String title;
   String description;
   String priority;
   ChangeTypes changeType;
   String agileTeam;
   String assigneesArtIds; // Comma-separated list
   String createdByUserArtId; // User Art ID
   String createdDateLong;
   String featureGroup;
   String needByDate;
   String needByDateLong;
   String opName;
   String points;
   String sprint;
   String transactionComment;
   String workPackage;
   ArtifactId parentAction = ArtifactId.SENTINEL;
   ArtifactId teamDef = ArtifactId.SENTINEL;
   ArtifactId versionId = ArtifactId.SENTINEL;
   ArtifactTypeToken artifactType = ArtifactTypeToken.SENTINEL;
   AtsWorkDefinitionToken workDef;
   Collection<String> aiIds = new ArrayList<>();
   List<CreateOption> createOptions = new ArrayList<>();
   List<NewActionRel> relations = new ArrayList<>();
   List<NewActionTeamData> teamData = new ArrayList<>();
   Map<ArtifactId, ArtifactToken> aiToArtToken = new HashMap<>();
   Map<String, String> attrValues = new HashMap<>();
   Map<WorkType, ArtifactTypeToken> workTypeToArtType = new HashMap<>();
   Map<WorkType, AtsWorkDefinitionToken> workTypeToWorkDef = new HashMap<>();
   NewActionMemberData memberData = null;
   boolean unplanned;
   boolean validationRequired = false;
   boolean inDebug = false;
   NewActionResult actResult = new NewActionResult();
   XResultData rd = new XResultData();
   XResultData debugRd = new XResultData();

   public NewActionData() {
      // for jax-rs
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

   public boolean isValidationRequired() {
      return validationRequired;
   }

   public void setValidationRequired(boolean validationRequired) {
      this.validationRequired = validationRequired;
   }

   public String getTransactionComment() {
      return transactionComment;
   }

   public void setTransactionComment(String transactionComment) {
      this.transactionComment = transactionComment;
   }

   public String getNeedByDateLong() {
      return needByDateLong;
   }

   public void setNeedByDateLong(String needByDateLong) {
      this.needByDateLong = needByDateLong;
   }

   public String getCreatedByUserArtId() {
      return createdByUserArtId;
   }

   public void setCreatedByUserArtId(String createdByUserArtId) {
      this.createdByUserArtId = createdByUserArtId;
   }

   public Collection<String> getAiIds() {
      return aiIds;
   }

   public void setAiIds(Collection<String> aiIds) {
      this.aiIds = aiIds;
   }

   public String getCreatedDateLong() {
      return createdDateLong;
   }

   public void setCreatedDateLong(String createdDateLong) {
      this.createdDateLong = createdDateLong;
   }

   public Map<String, String> getAttrValues() {
      return attrValues;
   }

   public void setAttrValues(Map<String, String> attrValues) {
      this.attrValues = attrValues;
   }

   public void addAttrValue(AttributeTypeId type, String value) {
      if (Strings.isValidAndNonBlank(value)) {
         attrValues.put(type.getIdString(), value);
      }
   }

   public String getNeedByDate() {
      return needByDate;
   }

   public void setNeedByDate(String needByDate) {
      if (needByDate != null) {
         this.needByDate = needByDate;
      }
   }

   public String getPoints() {
      return points;
   }

   public void setPoints(String points) {
      if (Strings.isValid(points)) {
         this.points = points;
      }
   }

   public boolean isUnplanned() {
      return unplanned;
   }

   public void setUnplanned(boolean unplanned) {
      this.unplanned = unplanned;
   }

   public String getSprint() {
      return sprint;
   }

   public void setSprint(String sprint) {
      this.sprint = sprint;
   }

   public String getFeatureGroup() {
      return featureGroup;
   }

   public void setFeatureGroup(String featureGroup) {
      this.featureGroup = featureGroup;
   }

   public String getWorkPackage() {
      return workPackage;
   }

   public void setWorkPackage(String workPackage) {
      this.workPackage = workPackage;
   }

   public String getAgileTeam() {
      return agileTeam;
   }

   public void setAgileTeam(String agileTeam) {
      this.agileTeam = agileTeam;
   }

   public ArtifactId getOriginator() {
      return originator;
   }

   /**
    * @param originatorStr - originator id (not userId)
    */
   public void setOriginator(ArtifactId originator) {
      this.originator = originator;
   }

   public String getAssigneesArtIds() {
      return assigneesArtIds;
   }

   /**
    * @param assigneesArtIds - comma delimited assignee ids (not userId)
    */
   public void setAssigneesArtIds(String assigneesArtIds) {
      this.assigneesArtIds = assigneesArtIds;
   }

   public ArtifactId getVersionId() {
      return versionId;
   }

   public void setVersionId(ArtifactId versionId) {
      this.versionId = versionId;
   }

   public ArtifactId getParentAction() {
      return parentAction;
   }

   public void setParentAction(ArtifactId parentAction) {
      this.parentAction = parentAction;
   }

   public ArtifactId getTeamDef() {
      return teamDef;
   }

   public void setTeamDef(ArtifactId teamDef) {
      this.teamDef = teamDef;
   }

   public ArtifactTypeToken getArtifactType() {
      return artifactType;
   }

   public void setArtifactType(ArtifactTypeToken artifactType) {
      this.artifactType = artifactType;
   }

   public List<CreateOption> getCreateOptions() {
      return createOptions;
   }

   public void setCreateOptions(List<CreateOption> createOptions) {
      this.createOptions = createOptions;
   }

   public List<NewActionTeamData> getTeamData() {
      return teamData;
   }

   public void setTeamData(List<NewActionTeamData> teamData) {
      this.teamData = teamData;
   }

   public void addTeamData(NewActionTeamData teamData) {
      this.getTeamData().add(teamData);
   }

   public void addTeamData(ArtifactId teamId, AttributeTypeToken attrType, List<Object> objs) {
      addTeamData(new NewActionTeamData(teamId, attrType, objs));
   }

   public void addTeamData(ArtifactId teamId, AttributeTypeToken attrType, Object obj) {
      addTeamData(new NewActionTeamData(teamId, attrType, Arrays.asList(obj)));
   }

   public NewActionMemberData getMemberData() {
      return memberData;
   }

   public void setMemberData(NewActionMemberData memberData) {
      this.memberData = memberData;
   }

   public NewActionData andAi(IAtsActionableItem... actionableItems) {
      for (IAtsActionableItem ai : actionableItems) {
         getAiIds().add(ai.getIdString());
      }
      return this;
   }

   public NewActionData andAis(Collection<IAtsActionableItem> actionableItems) {
      for (IAtsActionableItem ai : actionableItems) {
         getAiIds().add(ai.getIdString());
      }
      return this;
   }

   public NewActionData andAssignees(Collection<AtsUser> assignees) {
      this.assigneesArtIds = "";
      for (AtsUser user : assignees) {
         this.assigneesArtIds += user.getIdString() + ",";
      }
      this.assigneesArtIds = this.assigneesArtIds.replaceFirst(",$", "");
      return this;
   }

   public NewActionData andNeedBy(Date needBy) {
      if (needBy != null) {
         this.needByDateLong = String.valueOf(needBy.getTime());
      }
      return this;
   }

   public NewActionData andTitle(String title) {
      if (Strings.isValidAndNonBlank(title)) {
         this.title = title;
      }
      return this;
   }

   public NewActionData andDescription(String desc) {
      if (Strings.isValidAndNonBlank(desc)) {
         this.description = desc;
      }
      return this;
   }

   public NewActionData andChangeType(ChangeTypes changeType) {
      if (changeType != null) {
         this.changeType = changeType;
      }
      return this;
   }

   public NewActionData andPriority(Priorities priority) {
      if (priority != null) {
         this.priority = priority.toString();
      }
      return this;
   }

   public NewActionData andPriority(String priority) {
      if (Strings.isValidAndNonBlank(priority)) {
         this.priority = priority;
      }
      return this;
   }

   public XResultData getRd() {
      return rd;
   }

   public void setRd(XResultData rd) {
      this.rd = rd;
   }

   public NewActionData andAsUser(AtsUser user) {
      if (user != null) {
         this.asUser = user.getArtifactId();
      }
      return this;
   }

   public NewActionData andCreatedBy(AtsUser user) {
      if (user != null) {
         this.createdByUserArtId = user.getIdString();
      }
      return this;
   }

   public NewActionData andCreatedDate(Date date) {
      if (date != null) {
         this.createdDateLong = String.valueOf(date.getTime());
      }
      return this;
   }

   public NewActionData andRd(XResultData results) {
      this.rd = results;
      return this;
   }

   public NewActionResult getActResult() {
      return actResult;
   }

   public void setActResult(NewActionResult actResult) {
      this.actResult = actResult;
   }

   public NewActionData andCreateOption(CreateOption... createOption) {
      for (CreateOption opt : createOption) {
         this.createOptions.add(opt);
      }
      return this;
   }

   public NewActionData andMemberData(ArtifactToken memberArt, RelationTypeToken relationType,
      ArtifactToken dropTarget) {
      NewActionMemberData mData = new NewActionMemberData();
      mData.setMemberArt(ArtifactId.valueOf(memberArt.getId()));
      mData.setRelationType(relationType);
      mData.setDropTargetArt(ArtifactId.valueOf(dropTarget.getId()));
      setMemberData(mData);
      return this;
   }

   public String getOpName() {
      return opName;
   }

   public void setOpName(String opName) {
      this.opName = opName;
   }

   public NewActionData andOpName(String opName) {
      Conditions.assertNotNullOrEmpty(opName, "Operation Name must be specified");
      this.opName = opName;
      return this;
   }

   public NewActionData andSupportingInfo(ArtifactId artifactId) {
      return andRelation(CoreRelationTypes.SupportingInfo_SupportingInfo, artifactId);
   }

   public NewActionData andRelation(RelationTypeSide relationType, ArtifactId artifactId) {
      relations.add(new NewActionRel(relationType, artifactId, NewActionRelOp.Add));
      return this;
   }

   public List<NewActionRel> getRelations() {
      return relations;
   }

   public void setRelations(List<NewActionRel> relations) {
      this.relations = relations;
   }

   public NewActionData andAttr(AttributeTypeId attrType, String value) {
      addAttrValue(attrType, value);
      return this;
   }

   public NewActionData andAttr(AttributeTypeId attrType, Collection<String> values) {
      for (String value : values) {
         andAttr(attrType, value);
      }
      return this;
   }

   public NewActionData andVersion(ArtifactId versionId) {
      if (versionId != null && versionId.isValid()) {
         this.versionId = versionId;
      }
      return this;
   }

   /**
    * Map an ActionableItem with the ArtifactToken to use when creating the Team Workflow. Only used for tests.
    */
   public NewActionData andAiAndToken(ArtifactToken aiTok, ArtifactToken newArtToken) {
      aiToArtToken.put(ArtifactId.valueOf(aiTok.getId()), newArtToken);
      return this;
   }

   public Map<ArtifactId, ArtifactToken> getAiToArtToken() {
      return aiToArtToken;
   }

   public void setAiToArtToken(Map<ArtifactId, ArtifactToken> aiToArtToken) {
      this.aiToArtToken = aiToArtToken;
   }

   public NewActionData andWorkDef(AtsWorkDefinitionToken workDef) {
      this.workDef = workDef;
      return this;
   }

   public AtsWorkDefinitionToken getWorkDef() {
      return workDef;
   }

   public void setWorkDef(AtsWorkDefinitionToken workDef) {
      this.workDef = workDef;
   }

   public NewActionData andArtType(ArtifactTypeToken artType) {
      if (artType != null && artType.isValid()) {
         this.artifactType = artType;
      }
      return this;
   }

   public NewActionData andWorkTypeToWorkDef(WorkType workType, AtsWorkDefinitionToken workDef) {
      workTypeToWorkDef.put(workType, workDef);
      return this;
   }

   public Map<WorkType, AtsWorkDefinitionToken> getWorkTypeToWorkDef() {
      return workTypeToWorkDef;
   }

   public void setWorkTypeToWorkDef(Map<WorkType, AtsWorkDefinitionToken> workTypeToWorkDef) {
      this.workTypeToWorkDef = workTypeToWorkDef;
   }

   public NewActionData andWorkTypeToArtType(WorkType workType, ArtifactTypeToken artType) {
      workTypeToArtType.put(workType, artType);
      return this;
   }

   public Map<WorkType, ArtifactTypeToken> getWorkTypeToArtType() {
      return workTypeToArtType;
   }

   public void setWorkTypeToArtType(Map<WorkType, ArtifactTypeToken> workTypeToArtType) {
      this.workTypeToArtType = workTypeToArtType;
   }

   public NewActionData andAttr(AttributeTypeToken attrType, Date date) {
      if (date != null) {
         andAttr(attrType, String.valueOf(date.getTime()));
      }
      return this;
   }

   public NewActionData andAttr(AttributeTypeToken attrType, Boolean set) {
      if (set != null) {
         andAttr(attrType, set.toString());
      }
      return this;
   }

   public NewActionData andAttr(AttributeTypeToken attrType, BooleanState state) {
      if (state != null && !state.isUnSet()) {
         andAttr(attrType, state.isYes());
      }
      return this;
   }

   public NewActionData andVersion(Version version) {
      if (version != null && version.isValid()) {
         andVersion(version.getArtifactId());
      }
      return this;
   }

   public NewActionData andAttrName(AttributeTypeToken attrType, ArtifactToken artTok) {
      if (attrType != null && attrType.isValid()) {
         andAttr(attrType, artTok.getName());
      }
      return this;
   }

   public NewActionData andAttrNoSelect(AttributeTypeToken attrType, String value) {
      if (Strings.isValid(value) && !"--select--".equals(value)) {
         andAttr(attrType, value);
      }
      return this;
   }

   public NewActionData andRelation(RelationTypeSide relType, Version version) {
      if (version != null && version.isValid()) {
         andRelation(relType, version.getArtifactId());
      }
      return this;
   }

   // If parent action is valid, this is just to create a new workflow in same action
   public boolean isCreateTeamWf() {
      return parentAction.isValid();
   }

   public boolean isCreateAction() {
      return !isCreateTeamWf();
   }

   public NewActionData andValadation() {
      return andAttr(AtsAttributeTypes.ValidationRequired, true);
   }

   @Override
   public String toString() {
      return "NewActionData [asUser=" + asUser.getIdString() + ", aiIds=" + aiIds + ", actResult=" + actResult + ", op=" + opName + "]";
   }

   public ArtifactId getAsUser() {
      return asUser;
   }

   public void setAsUser(ArtifactId asUser) {
      this.asUser = asUser;
   }

   public boolean isInDebug() {
      return inDebug;
   }

   public void setInDebug(boolean inDebug) {
      this.inDebug = inDebug;
   }

   public XResultData getDebugRd() {
      return debugRd;
   }

   public void setDebugRd(XResultData debugRd) {
      this.debugRd = debugRd;
   }

}
