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
import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.task.CreateTasksOption;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/***
 * @author Donald G. Dunne
 */
public class ChangeReportOptions {

   IAtsTeamDefinitionArtifactToken fromSiblingTeam;
   Collection<IAtsTeamDefinitionArtifactToken> toSiblingTeams = new ArrayList<>();
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

   public IAtsTeamDefinitionArtifactToken getFromSiblingTeam() {
      return fromSiblingTeam;
   }

   public void setFromSiblingTeam(IAtsTeamDefinitionArtifactToken fromSiblingTeam) {
      this.fromSiblingTeam = fromSiblingTeam;
   }

   public Collection<IAtsTeamDefinitionArtifactToken> getToSiblingTeams() {
      return toSiblingTeams;
   }

   public void setToSiblingTeams(Collection<IAtsTeamDefinitionArtifactToken> toSiblingTeams) {
      this.toSiblingTeams = toSiblingTeams;
   }

   public Collection<CreateTasksOption> getCreateOptions() {
      return createOptions;
   }

   public void setCreateOptions(Collection<CreateTasksOption> createOptions) {
      this.createOptions = createOptions;
   }

}
