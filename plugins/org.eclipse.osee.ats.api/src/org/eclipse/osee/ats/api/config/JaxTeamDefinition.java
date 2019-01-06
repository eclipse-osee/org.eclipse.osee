/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;

/**
 * @author Donald G. Dunne
 */
public class JaxTeamDefinition extends JaxAtsConfigObject {

   @JsonSerialize(using = ToStringSerializer.class)
   Long parentId;
   List<Long> ais = new ArrayList<>();
   List<Long> versions = new ArrayList<>();
   List<Long> children = new ArrayList<>();
   String workType;

   public Long getParentId() {
      return parentId;
   }

   public void setParentId(Long parentId) {
      this.parentId = parentId;
   }

   public List<Long> getAis() {
      return ais;
   }

   public void setAis(List<Long> ais) {
      this.ais = ais;
   }

   public List<Long> getVersions() {
      return versions;
   }

   public void setVersions(List<Long> versions) {
      this.versions = versions;
   }

   public List<Long> getChildren() {
      return children;
   }

   public void setChildren(List<Long> children) {
      this.children = children;
   }

   public void addChild(JaxTeamDefinition child) {
      children.add(child.getId());
   }

   public void addVersion(Long version) {
      versions.add(version);
   }

   public void addAi(Long aiId) {
      ais.add(aiId);
   }

   public String getWorkType() {
      return workType;
   }

   public void setWorkType(String workType) {
      this.workType = workType;
   }

   @Override
   public ArtifactTypeId getArtifactType() {
      return AtsArtifactTypes.TeamDefinition;
   }

}
