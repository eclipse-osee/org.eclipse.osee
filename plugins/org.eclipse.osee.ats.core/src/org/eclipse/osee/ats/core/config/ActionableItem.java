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
package org.eclipse.osee.ats.core.config;

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.ActionableItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.JaxActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public class ActionableItem extends AtsConfigObject implements IAtsActionableItem {

   private JaxActionableItem jaxAI;

   public ActionableItem(Log logger, AtsApi atsApi, JaxActionableItem jaxAI) {
      super(logger, atsApi,
         ArtifactToken.valueOf(jaxAI.getId(), jaxAI.getGuid(), jaxAI.getName(), atsApi.getAtsBranch(), ActionableItem),
         ActionableItem);
      this.jaxAI = jaxAI;
   }

   public ActionableItem(Log logger, AtsApi atsApi, ArtifactToken artifact) {
      super(logger, atsApi, ArtifactToken.valueOf(artifact.getId(), artifact.getGuid(), artifact.getName(),
         atsApi.getAtsBranch(), ActionableItem), ActionableItem);
   }

   @Override
   public Collection<String> getRules() {
      Collection<String> rules = new ArrayList<>();
      try {
         rules = atsApi.getAttributeResolver().getAttributeValues(artifact, AtsAttributeTypes.RuleDefinition);
      } catch (OseeCoreException ex) {
         getLogger().error(ex, "Error getting rules");
      }
      return rules;
   }

   @Override
   public boolean hasRule(String rule) {
      return getRules().contains(rule);
   }

   @Override
   public Collection<IAtsActionableItem> getChildrenActionableItems() {
      List<IAtsActionableItem> children = new LinkedList<>();
      if (jaxAI != null) {
         for (Long aiId : jaxAI.getChildren()) {
            children.add(new ActionableItem(logger, atsApi,
               atsApi.getConfigService().getConfigurations().getIdToAi().get(aiId)));
         }
      } else {
         for (ArtifactToken artifact : atsApi.getRelationResolver().getChildren(artifact)) {
            if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.ActionableItem)) {
               children.add(new ActionableItem(logger, atsApi, artifact));
            }
         }
      }
      return children;
   }

   @Override
   public IAtsActionableItem getParentActionableItem() {
      IAtsActionableItem parent = null;
      try {
         if (jaxAI != null && jaxAI.getParentId() != null) {
            parent = atsApi.getConfigItemFactory().getActionableItem(atsApi.getArtifact(jaxAI.getParentId()));
         } else {
            ArtifactToken art =
               atsApi.getRelationResolver().getRelatedOrNull(artifact, CoreRelationTypes.Default_Hierarchical__Parent);
            if (art != null) {
               parent = atsApi.getConfigItemFactory().getActionableItem(art);
            }
         }
      } catch (OseeCoreException ex) {
         getLogger().error(ex, "Error getParentActionableItem");
      }
      return parent;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition() {
      IAtsTeamDefinition teamDef = null;
      try {
         if (jaxAI != null && jaxAI.getTeamDefId() != null) {
            teamDef = atsApi.getConfigItemFactory().getTeamDef(atsApi.getArtifact(jaxAI.getTeamDefId()));
         } else {
            ArtifactToken art =
               atsApi.getRelationResolver().getRelatedOrNull(artifact, AtsRelationTypes.TeamActionableItem_Team);
            if (art != null) {
               teamDef = atsApi.getConfigItemFactory().getTeamDef(art);
            }
         }
      } catch (OseeCoreException ex) {
         getLogger().error(ex, "Error getTeamDefinition");
      }
      return teamDef;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinitionInherited() {
      return TeamDefinitions.getImpactedTeamDef(this);
   }

   @Override
   public boolean isAllowUserActionCreation() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.AllowUserActionCreation,
         true);
   }

}
