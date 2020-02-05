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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinition extends JaxAtsConfigObject implements IAtsTeamDefinition {

   @JsonSerialize(using = ToStringSerializer.class)
   Long parentId;
   Set<Long> ais = new HashSet<>();
   Set<Long> versions = new HashSet<>();
   Set<Long> children = new HashSet<>();
   String workType;

   public TeamDefinition() {
      // for jax-rs
   }

   public TeamDefinition(ArtifactToken artifact, AtsApi atsApi) {
      super(artifact.getId(), artifact.getName());
      setStoreObject(artifact);
      setAtsApi(atsApi);
   }

   public Long getParentId() {
      return parentId;
   }

   public void setParentId(Long parentId) {
      this.parentId = parentId;
   }

   public Set<Long> getAis() {
      return ais;
   }

   public void setAis(Collection<Long> ais) {
      this.ais.addAll(ais);
   }

   public Set<Long> getVersions() {
      return versions;
   }

   public void setVersions(Collection<Long> versions) {
      this.versions.addAll(versions);
   }

   public Set<Long> getChildren() {
      return children;
   }

   public void setChildren(Collection<Long> children) {
      this.children.addAll(children);
   }

   public void addChild(TeamDefinition child) {
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
   public ArtifactTypeToken getArtifactType() {
      return AtsArtifactTypes.TeamDefinition;
   }

   @Override
   @JsonIgnore
   public Collection<ActionableItem> getActionableItems() {
      List<ActionableItem> ais = new LinkedList<>();
      for (Long id : getAis()) {
         ais.add(atsApi.getConfigService().getConfigurations().getIdToAi().get(id));
      }
      return ais;
   }

   @Override
   @JsonIgnore
   public Collection<TeamDefinition> getChildrenTeamDefs() {
      List<TeamDefinition> teamDefs = new LinkedList<>();
      for (Long id : getChildren()) {
         teamDefs.add(atsApi.getConfigService().getConfigurations().getIdToTeamDef().get(id));
      }
      return teamDefs;
   }

   public void addAi(ArtifactToken ai) {
      if (ai.getId().equals(66382940L)) {
         System.err.println("here2");
      }
      if (ais.contains(ai.getId())) {
         System.err.println("here2");
      } else {
         ais.add(ai.getId());
      }
   }

}
