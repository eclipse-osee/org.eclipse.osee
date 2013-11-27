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

import java.rmi.activation.Activator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.impl.internal.AtsServerService;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G Dunne
 */
public class ActionableItem extends AtsConfigObject implements IAtsActionableItem {

   public ActionableItem(ArtifactReadable artifact) {
      super(artifact);
   }

   @Override
   public Collection<IAtsActionableItem> getChildrenActionableItems() {
      Set<IAtsActionableItem> children = new HashSet<IAtsActionableItem>();
      try {
         for (ArtifactReadable childArt : artifact.getRelated(CoreRelationTypes.Default_Hierarchical__Child)) {
            IAtsActionableItem childTeamDef = AtsServerService.get().getWorkItemFactory().getActionableItem(childArt);
            children.add(childTeamDef);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return children;
   }

   @Override
   public IAtsActionableItem getParentActionableItem() {
      IAtsActionableItem parent = null;
      try {
         ResultSet<ArtifactReadable> related = artifact.getRelated(CoreRelationTypes.Default_Hierarchical__Parent);
         if (!related.isEmpty()) {
            parent = AtsServerService.get().getWorkItemFactory().getActionableItem(related.iterator().next());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return parent;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition() {
      IAtsTeamDefinition teamDef = null;
      try {
         ResultSet<ArtifactReadable> related = artifact.getRelated(AtsRelationTypes.TeamActionableItem_Team);
         if (!related.isEmpty()) {
            teamDef = AtsServerService.get().getWorkItemFactory().getTeamDef(related.iterator().next());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return teamDef;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinitionInherited() {
      return TeamDefinitions.getImpactedTeamDef(this);
   }

   @Override
   public void setParentActionableItem(IAtsActionableItem parentActionableItem) {
      OseeLog.log(TeamDefinition.class, Level.SEVERE, "ActionableItem.setParentActionableItem not implemented");
   }

   @Override
   public void setTeamDefinition(IAtsTeamDefinition teamDef) {
      OseeLog.log(TeamDefinition.class, Level.SEVERE, "ActionableItem.setTeamDefinition not implemented");
   }

   @Override
   public void setActionable(boolean actionable) {
      OseeLog.log(TeamDefinition.class, Level.SEVERE, "ActionableItem.setActionable not implemented");
   }

   @Override
   public void setActive(boolean active) {
      OseeLog.log(TeamDefinition.class, Level.SEVERE, "ActionableItem.setActive not implemented");
   }

   @Override
   public String getTypeName() {
      return "Actionable Item";
   }

}
