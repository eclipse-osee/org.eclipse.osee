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
package org.eclipse.osee.ats.api.ai;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.JaxAtsConfigObject;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public class ActionableItem extends JaxAtsConfigObject implements IAtsActionableItem {

   @JsonSerialize(using = ToStringSerializer.class)
   Long parentId;
   @JsonSerialize(using = ToStringSerializer.class)
   Long teamDefId;
   List<Long> children = new ArrayList<>();
   boolean actionable = false;
   boolean allowUserActionCreation = true;

   public ActionableItem() {
      // for jax-rs
   }

   public ActionableItem(ArtifactToken artifact, AtsApi atsApi) {
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

   public Long getTeamDefId() {
      return teamDefId;
   }

   public void setTeamDefId(Long teamDefId) {
      this.teamDefId = teamDefId;
   }

   public List<Long> getChildren() {
      return children;
   }

   public void setChildren(List<Long> children) {
      this.children = children;
   }

   public void addChild(ActionableItem child) {
      children.add(child.getId());
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return AtsArtifactTypes.ActionableItem;
   }

   @Override
   @JsonIgnore
   public IAtsActionableItem getParentActionableItem() {
      Long parentId = getParentId();
      if (parentId != null) {
         return atsApi.getConfigService().getConfigurations().getIdToAi().get(parentId);
      }
      return null;
   }

   @Override
   @JsonIgnore
   public Collection<IAtsActionableItem> getChildrenActionableItems() {
      List<IAtsActionableItem> aias = new ArrayList<>();
      for (Long childId : getChildren()) {
         aias.add(atsApi.getConfigService().getConfigurations().getIdToAi().get(childId));
      }
      return aias;
   }

   @Override
   @JsonIgnore
   public IAtsTeamDefinition getTeamDefinition() {
      Long teamDefId = getTeamDefId();
      if (teamDefId != null) {
         return atsApi.getConfigService().getConfigurations().getIdToTeamDef().get(teamDefId);
      }
      return null;
   }

   @Override
   public boolean isActionable() {
      return actionable;
   }

   public void setActionable(boolean actionable) {
      this.actionable = actionable;
   }

   @Override
   public boolean isAllowUserActionCreation() {
      return allowUserActionCreation;
   }

   public void setAllowUserActionCreation(boolean allowUserActionCreation) {
      this.allowUserActionCreation = allowUserActionCreation;
   }

}
