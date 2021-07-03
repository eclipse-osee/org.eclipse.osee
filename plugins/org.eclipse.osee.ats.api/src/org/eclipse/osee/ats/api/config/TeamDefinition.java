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
   boolean hasWorkPackages = false;

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
         TeamDefinition teamDef = atsApi.getConfigService().getConfigurations().getIdToTeamDef().get(id);
         teamDefs.add(teamDef);
      }
      return teamDefs;
   }

   public void addAi(ArtifactToken ai) {
      ais.add(ai.getId());
   }

   @Override
   public boolean hasWorkPackages() {
      return hasWorkPackages;
   }

   public void setHasWorkPackages(boolean hasWorkPackages) {
      this.hasWorkPackages = hasWorkPackages;
   }

   public boolean isHasWorkPackages() {
      return hasWorkPackages;
   }

}
