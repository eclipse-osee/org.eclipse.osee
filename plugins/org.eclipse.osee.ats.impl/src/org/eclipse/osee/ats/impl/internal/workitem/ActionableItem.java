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
package org.eclipse.osee.ats.impl.internal.workitem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G Dunne
 */
public class ActionableItem extends AtsConfigObject implements IAtsActionableItem {

   public ActionableItem(Log logger, IAtsServer atsServer, ArtifactReadable artifact) {
      super(logger, atsServer, artifact);
   }

   @Override
   public Collection<IAtsActionableItem> getChildrenActionableItems() {
      Set<IAtsActionableItem> children = new HashSet<>();
      try {
         for (ArtifactReadable childArt : getArtifact().getRelated(CoreRelationTypes.Default_Hierarchical__Child)) {
            IAtsActionableItem childTeamDef = atsServices.getConfigItemFactory().getActionableItem(childArt);
            children.add(childTeamDef);
         }
      } catch (OseeCoreException ex) {
         getLogger().error(ex, "Error getting Children Actionable Items");
      }
      return children;
   }

   private ArtifactReadable getArtifact() {
      return (ArtifactReadable) artifact;
   }

   @Override
   public IAtsActionableItem getParentActionableItem() {
      IAtsActionableItem parent = null;
      try {
         ResultSet<ArtifactReadable> related = getArtifact().getRelated(CoreRelationTypes.Default_Hierarchical__Parent);
         if (!related.isEmpty()) {
            parent = atsServices.getConfigItemFactory().getActionableItem(related.iterator().next());
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
         ResultSet<ArtifactReadable> related = getArtifact().getRelated(AtsRelationTypes.TeamActionableItem_Team);
         if (!related.isEmpty()) {
            teamDef = atsServices.getConfigItemFactory().getTeamDef(related.iterator().next());
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
   public void setParentActionableItem(IAtsActionableItem parentActionableItem) {
      throw new UnsupportedOperationException("Error ActionableItem.setParentActionableItem not implemented");
   }

   @Override
   public void setTeamDefinition(IAtsTeamDefinition teamDef) {
      throw new UnsupportedOperationException("Error ActionableItem.setTeamDefinition not implemented");
   }

   @Override
   public void setActionable(boolean actionable) {
      throw new UnsupportedOperationException("Error ActionableItem.setActionable not implemented");
   }

   @Override
   public void setActive(boolean active) {
      throw new UnsupportedOperationException("Error ActionableItem.setActive not implemented");
   }

   @Override
   public String getTypeName() {
      return "Actionable Item";
   }

   @Override
   public boolean isAllowUserActionCreation() {
      return getArtifact().getSoleAttributeValue(AtsAttributeTypes.AllowUserActionCreation, true);
   }

   @Override
   public void setAllowUserActionCreation(boolean allowUserActionCreation) {
      throw new UnsupportedOperationException("Error ActionableItem.setAllowUserActionCreation not implemented");
   }

   @Override
   public void addRule(String rule) {
      throw new UnsupportedOperationException("ActionableItem.addRule not implemented");
   }

   @Override
   public Collection<String> getRules() {
      Collection<String> rules = new ArrayList<>();
      try {
         rules = getArtifact().getAttributeValues(AtsAttributeTypes.RuleDefinition);
      } catch (OseeCoreException ex) {
         getLogger().error(ex, "Error getting rules");
      }
      return rules;
   }

   @Override
   public boolean hasRule(String rule) {
      boolean result = false;
      for (String rule2 : getRules()) {
         if (rule.equals(rule2)) {
            result = true;
            break;
         }
      }
      return result;
   }

   @Override
   public void removeRule(String rule) {
      throw new UnsupportedOperationException("ActionableItem.setActionable not implemented");
   }

}
