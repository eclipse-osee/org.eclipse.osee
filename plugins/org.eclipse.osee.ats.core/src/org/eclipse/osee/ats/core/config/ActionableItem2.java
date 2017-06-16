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
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public class ActionableItem2 extends AtsConfigObject implements IAtsActionableItem {

   private final JaxActionableItem jaxAI;

   public ActionableItem2(Log logger, IAtsServices services, JaxActionableItem jaxAI) {
      super(logger, services, ArtifactToken.valueOf(jaxAI.getUuid(), jaxAI.getName(), services.getAtsBranch(),
         AtsArtifactTypes.ActionableItem));
      this.jaxAI = jaxAI;
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
      for (Long childId : jaxAI.getChildren()) {
         children.add(new ActionableItem2(logger, services, services.getConfigurations().getIdToAi().get(childId)));
      }
      return children;
   }

   @Override
   public IAtsActionableItem getParentActionableItem() {
      IAtsActionableItem parent = null;
      try {
         if (jaxAI.getParentId() != null) {
            parent = services.getConfigItemFactory().getActionableItem(services.getArtifact(jaxAI.getParentId()));
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
         if (jaxAI.getTeamDefId() != null) {
            teamDef = services.getConfigItemFactory().getTeamDef(services.getArtifact(jaxAI.getTeamDefId()));
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
