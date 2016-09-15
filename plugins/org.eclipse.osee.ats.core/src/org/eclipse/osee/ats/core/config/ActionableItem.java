/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G Dunne
 */
public class ActionableItem extends AtsConfigObject implements IAtsActionableItem {

   public ActionableItem(Log logger, IAtsServices services, ArtifactToken artifact) {
      super(logger, services, artifact);
   }

   @Override
   public Collection<IAtsActionableItem> getChildrenActionableItems() {
      Set<IAtsActionableItem> children = new HashSet<>();
      try {
         for (ArtifactId childArt : services.getRelationResolver().getChildren(artifact,
            AtsArtifactTypes.ActionableItem)) {
            IAtsActionableItem childTeamDef = services.getConfigItemFactory().getActionableItem(childArt);
            children.add(childTeamDef);
         }
      } catch (OseeCoreException ex) {
         getLogger().error(ex, "Error getting Children Actionable Items");
      }
      return children;
   }

   @Override
   public IAtsActionableItem getParentActionableItem() {
      IAtsActionableItem parent = null;
      try {
         Collection<ArtifactToken> related =
            services.getRelationResolver().getRelated(artifact, CoreRelationTypes.Default_Hierarchical__Parent);
         if (!related.isEmpty()) {
            parent = services.getConfigItemFactory().getActionableItem(related.iterator().next());
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
         ArtifactToken related =
            services.getRelationResolver().getRelatedOrNull(artifact, AtsRelationTypes.TeamActionableItem_Team);
         if (related != null) {
            teamDef = services.getConfigItemFactory().getTeamDef(related);
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
   public void setActionable(boolean actionable) {
      throw new UnsupportedOperationException("Error ActionableItem.setActionable not implemented");
   }

   @Override
   public String getTypeName() {
      return "Actionable Item";
   }

   @Override
   public boolean isAllowUserActionCreation() {
      return services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.AllowUserActionCreation,
         true);
   }

   @Override
   public Collection<String> getRules() {
      Collection<String> rules = new ArrayList<>();
      try {
         rules = services.getAttributeResolver().getAttributeValues(artifact, AtsAttributeTypes.RuleDefinition);
      } catch (OseeCoreException ex) {
         getLogger().error(ex, "Error getting rules");
      }
      return rules;
   }

   @Override
   public boolean hasRule(String rule) {
      return getRules().contains(rule);
   }

}
