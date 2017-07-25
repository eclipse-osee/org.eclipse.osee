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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
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

   public ActionableItem(Log logger, IAtsServices services, JaxActionableItem jaxAI) {
      super(logger, services, ArtifactToken.valueOf(jaxAI.getUuid(), jaxAI.getGuid(), jaxAI.getName(),
         services.getAtsBranch(), AtsArtifactTypes.ActionableItem));
      this.jaxAI = jaxAI;
   }

   public ActionableItem(Log logger, IAtsServices services, ArtifactToken artifact) {
      super(logger, services, ArtifactToken.valueOf(artifact.getUuid(), artifact.getGuid(), artifact.getName(),
         services.getAtsBranch(), AtsArtifactTypes.ActionableItem));
   }

   @Override
   public String getTypeName() {
      return "Actionable Item";
   }

   @Override
   public Collection<String> getRules() throws OseeCoreException {
      Collection<String> rules = new ArrayList<>();
      try {
         rules = services.getAttributeResolver().getAttributeValues(artifact, AtsAttributeTypes.RuleDefinition);
      } catch (OseeCoreException ex) {
         getLogger().error(ex, "Error getting rules");
      }
      return rules;
   }

   @Override
   public boolean hasRule(String rule) throws OseeCoreException {
      return getRules().contains(rule);
   }

   @Override
   public Collection<IAtsActionableItem> getChildrenActionableItems() {
      List<IAtsActionableItem> children = new LinkedList<>();
      if (jaxAI != null) {
         for (Long aiId : jaxAI.getChildren()) {
            children.add(new ActionableItem(logger, services, services.getConfigurations().getIdToAi().get(aiId)));
         }
      } else {
         for (ArtifactToken artifact : services.getRelationResolver().getChildren(artifact)) {
            if (services.getStoreService().isOfType(artifact, AtsArtifactTypes.ActionableItem)) {
               children.add(new ActionableItem(logger, services, artifact));
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
            parent = services.getConfigItemFactory().getActionableItem(services.getArtifact(jaxAI.getParentId()));
         } else {
            ArtifactToken art = services.getRelationResolver().getRelatedOrNull(artifact,
               CoreRelationTypes.Default_Hierarchical__Parent);
            if (art != null) {
               parent = services.getConfigItemFactory().getActionableItem(art);
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
            teamDef = services.getConfigItemFactory().getTeamDef(services.getArtifact(jaxAI.getTeamDefId()));
         } else {
            ArtifactToken art =
               services.getRelationResolver().getRelatedOrNull(artifact, AtsRelationTypes.TeamActionableItem_Team);
            if (art != null) {
               teamDef = services.getConfigItemFactory().getTeamDef(art);
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
      return services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.AllowUserActionCreation,
         true);
   }

}
