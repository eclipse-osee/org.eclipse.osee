/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.api.task.create;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.task.CreateTasksOption;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/***
 * @author Donald G. Dunne
 */
public class ChangeReportOptions {

   ArtifactToken fromSiblingTeamDef;
   WorkType fromSiblingTeamDefWorkType;
   List<ChangeReportOptionsToTeam> toSiblingTeamDatas = new ArrayList<>();

   // These are to identify what to generate a task against
   Collection<ArtifactTypeToken> artifactTypes = new ArrayList<>();
   Collection<ArtifactTypeToken> notArtifactTypes = new ArrayList<>();
   Collection<AttributeTypeToken> attributeTypes = new ArrayList<>();
   Collection<AttributeTypeToken> notAttributeTypes = new ArrayList<>();
   Collection<RelationTypeToken> relationTypes = new ArrayList<>();
   Collection<RelationTypeToken> notRelationTypes = new ArrayList<>();
   Collection<CreateTasksOption> createOptions = new ArrayList<>();

   // This is to copy over these values if set
   Collection<AttributeTypeToken> copyAttrTypes = new ArrayList<>();

   public ChangeReportOptions() {
      // for jax-rs
   }

   public Collection<ArtifactTypeToken> getArtifactTypes() {
      return artifactTypes;
   }

   public void setArtifactTypes(Collection<ArtifactTypeToken> artifactTypes) {
      this.artifactTypes = artifactTypes;
   }

   public Collection<ArtifactTypeToken> getNotArtifactTypes() {
      return notArtifactTypes;
   }

   public void setNotArtifactTypes(Collection<ArtifactTypeToken> notArtifactTypes) {
      this.notArtifactTypes = notArtifactTypes;
   }

   public Collection<AttributeTypeToken> getAttributeTypes() {
      return attributeTypes;
   }

   public void setAttributeTypes(Collection<AttributeTypeToken> attributeTypes) {
      this.attributeTypes = attributeTypes;
   }

   public Collection<AttributeTypeToken> getNotAttributeTypes() {
      return notAttributeTypes;
   }

   public void setNotAttributeTypes(Collection<AttributeTypeToken> notAttributeTypes) {
      this.notAttributeTypes = notAttributeTypes;
   }

   public void andArtifactType(ArtifactTypeToken artType) {
      this.artifactTypes.add(artType);
   }

   public void andNotArtifactType(ArtifactTypeToken artType) {
      this.notArtifactTypes.add(artType);
   }

   public void andAttributeType(AttributeTypeToken attrType) {
      this.attributeTypes.add(attrType);
   }

   public void andNotAttributeType(AttributeTypeToken attrType) {
      this.notAttributeTypes.add(attrType);
   }

   public Collection<CreateTasksOption> getCreateOptions() {
      return createOptions;
   }

   public void setCreateOptions(Collection<CreateTasksOption> createOptions) {
      this.createOptions = createOptions;
   }

   public ArtifactToken getFromSiblingTeamDef() {
      return fromSiblingTeamDef;
   }

   public void setFromSiblingTeamDef(ArtifactToken fromSiblingTeamDef) {
      this.fromSiblingTeamDef = fromSiblingTeamDef;
   }

   public List<ChangeReportOptionsToTeam> getToSiblingTeamDatas() {
      return toSiblingTeamDatas;
   }

   public void setToSiblingTeamDatas(List<ChangeReportOptionsToTeam> toSiblingTeamDatas) {
      this.toSiblingTeamDatas = toSiblingTeamDatas;
   }

   /**
    * Add toSiblingTeam using default ChangeReportOptionsToTeam
    */
   public ChangeReportOptionsToTeam andToSiblingTeam(ArtifactToken toSiblingTeam, ArtifactToken toAi) {
      ChangeReportOptionsToTeam toTeamData = new ChangeReportOptionsToTeam();
      toTeamData.setTeamId(toSiblingTeam.getIdString());
      toTeamData.setAiId(toAi.getIdString());
      this.toSiblingTeamDatas.add(toTeamData);
      return toTeamData;
   }

   /**
    * Add toSiblingTeam using overridden ChangeReportOptionsToTeam
    */
   public ChangeReportOptionsToTeam andToSiblingTeam(ArtifactToken toSiblingTeam, ArtifactToken toAi, ChangeReportTaskNameProviderToken nameProviderId) {
      ChangeReportOptionsToTeam toTeamData = andToSiblingTeam(toSiblingTeam, toAi);
      toTeamData.setNameProviderId(nameProviderId);
      return toTeamData;
   }

   public ChangeReportOptionsToTeam andToSiblingWorkType(WorkType workType) {
      ChangeReportOptionsToTeam toTeamData = new ChangeReportOptionsToTeam();
      toTeamData.setWorkType(workType);
      this.toSiblingTeamDatas.add(toTeamData);
      return toTeamData;
   }

   public ChangeReportOptionsToTeam andToSiblingWorkType(WorkType workType, ChangeReportTaskNameProviderToken nameProviderId) {
      ChangeReportOptionsToTeam toTeamData = andToSiblingWorkType(workType);
      toTeamData.setNameProviderId(nameProviderId);
      return toTeamData;
   }

   public Collection<RelationTypeToken> getRelationTypes() {
      return relationTypes;
   }

   public void setRelationTypes(Collection<RelationTypeToken> relationTypes) {
      this.relationTypes = relationTypes;
   }

   public Collection<RelationTypeToken> getNotRelationTypes() {
      return notRelationTypes;
   }

   public void setNotRelationTypes(Collection<RelationTypeToken> notRelationTypes) {
      this.notRelationTypes = notRelationTypes;
   }

   public WorkType getFromSiblingTeamDefWorkType() {
      return fromSiblingTeamDefWorkType;
   }

   public void setFromSiblingTeamDefWorkType(WorkType fromSiblingTeamDefWorkType) {
      this.fromSiblingTeamDefWorkType = fromSiblingTeamDefWorkType;
   }

   public Collection<AttributeTypeToken> getCopyAttrTypes() {
      return copyAttrTypes;
   }

   public void setCopyAttrTypes(Collection<AttributeTypeToken> copyAttrTypes) {
      this.copyAttrTypes = copyAttrTypes;
   }

}
