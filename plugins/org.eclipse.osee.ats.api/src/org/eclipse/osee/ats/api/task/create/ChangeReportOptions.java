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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.task.CreateTasksOption;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/***
 * @author Donald G. Dunne
 */
public class ChangeReportOptions {

   ArtifactToken fromSiblingTeamDef;
   Map<String, String> toSiblingTeamAi = new HashMap<String, String>();
   Collection<ArtifactTypeToken> artifactTypes = new ArrayList<>();
   Collection<ArtifactTypeToken> notArtifactTypes = new ArrayList<>();
   Collection<AttributeTypeToken> attributeTypes = new ArrayList<>();
   Collection<AttributeTypeToken> notAttributeTypes = new ArrayList<>();
   Collection<CreateTasksOption> createOptions = new ArrayList<>();

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

   public Map<String, String> getToSiblingTeamAiMap() {
      return toSiblingTeamAi;
   }

   public void andToSiblingTeamAi(ArtifactToken toSiblingTeam, ArtifactToken toAi) {
      this.toSiblingTeamAi.put(toSiblingTeam.getIdString(), toAi.getIdString());
   }

   public Collection<CreateTasksOption> getCreateOptions() {
      return createOptions;
   }

   public void setCreateOptions(Collection<CreateTasksOption> createOptions) {
      this.createOptions = createOptions;
   }

   public Map<String, String> getToSiblingTeamAi() {
      return toSiblingTeamAi;
   }

   public void setToSiblingTeamAi(Map<String, String> toSiblingTeamAi) {
      this.toSiblingTeamAi = toSiblingTeamAi;
   }

   public ArtifactToken getFromSiblingTeamDef() {
      return fromSiblingTeamDef;
   }

   public void setFromSiblingTeamDef(ArtifactToken fromSiblingTeamDef) {
      this.fromSiblingTeamDef = fromSiblingTeamDef;
   }

}
