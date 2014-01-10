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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
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
      Set<IAtsActionableItem> children = new HashSet<IAtsActionableItem>();
      try {
         for (ArtifactReadable childArt : artifact.getRelated(CoreRelationTypes.Default_Hierarchical__Child)) {
            IAtsActionableItem childTeamDef = getAtsServer().getConfigItemFactory().getActionableItem(childArt);
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
         ResultSet<ArtifactReadable> related = artifact.getRelated(CoreRelationTypes.Default_Hierarchical__Parent);
         if (!related.isEmpty()) {
            parent = getAtsServer().getConfigItemFactory().getActionableItem(related.iterator().next());
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
         ResultSet<ArtifactReadable> related = artifact.getRelated(AtsRelationTypes.TeamActionableItem_Team);
         if (!related.isEmpty()) {
            teamDef = getAtsServer().getConfigItemFactory().getTeamDef(related.iterator().next());
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
      getLogger().error("Error ActionableItem.setParentActionableItem not implemented");
   }

   @Override
   public void setTeamDefinition(IAtsTeamDefinition teamDef) {
      getLogger().error("Error ActionableItem.setTeamDefinition not implemented");
   }

   @Override
   public void setActionable(boolean actionable) {
      getLogger().error("Error ActionableItem.setActionable not implemented");
   }

   @Override
   public void setActive(boolean active) {
      getLogger().error("Error ActionableItem.setActive not implemented");
   }

   @Override
   public String getTypeName() {
      return "Actionable Item";
   }

}
