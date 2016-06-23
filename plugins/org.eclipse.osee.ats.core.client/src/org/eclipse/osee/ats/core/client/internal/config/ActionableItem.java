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
package org.eclipse.osee.ats.core.client.internal.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.model.impl.AtsObject;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ActionableItem extends AtsObject implements IAtsActionableItem {

   private boolean actionable = true;
   private boolean allowUserActionCreation = true;
   private boolean active = true;
   private IAtsTeamDefinition teamDefinition;
   private IAtsActionableItem parentActionableItem;
   private Set<String> staticIds;
   private Set<IAtsUser> leads;
   private Set<IAtsUser> subscribed;
   private Set<IAtsActionableItem> childrenActionableItems;

   public ActionableItem(String name, String guid, long uuid) {
      super(name, uuid);
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition() {
      if (teamDefinition == null) {
         if (getArtifact() != null) {
            Artifact teamDefArt = getArtifact().getRelatedArtifactOrNull(AtsRelationTypes.TeamActionableItem_Team);
            if (teamDefArt != null) {
               teamDefinition = AtsClientService.get().getCache().getAtsObject(teamDefArt.getUuid());
            }
         }
      }
      return teamDefinition;
   }

   @Override
   public Collection<IAtsUser> getLeads() {
      if (leads == null) {
         leads = new HashSet<>();
         if (getArtifact() != null) {
            for (Artifact userArt : getArtifact().getRelatedArtifacts(AtsRelationTypes.ActionableItemLead_Lead)) {
               IAtsUser user = AtsClientService.get().getUserServiceClient().getUserFromOseeUser((User) userArt);
               leads.add(user);
            }
         }
      }
      return leads;
   }

   @Override
   public boolean isActionable() {
      return actionable;
   }

   @Override
   public boolean isActive() {
      return active;
   }

   @Override
   public void setActive(boolean active) {
      this.active = active;
   }

   @Override
   public void setTeamDefinition(IAtsTeamDefinition teamDef) {
      this.teamDefinition = teamDef;
   }

   @Override
   public void setActionable(boolean actionable) {
      this.actionable = actionable;
   }

   @Override
   public Collection<IAtsActionableItem> getChildrenActionableItems() {
      if (childrenActionableItems == null) {
         childrenActionableItems = new HashSet<>();
         if (getArtifact() != null) {
            for (Artifact child : getArtifact().getChildren()) {
               if (child.isOfType(AtsArtifactTypes.ActionableItem)) {
                  IAtsActionableItem childAi = AtsClientService.get().getCache().getAtsObject(child.getUuid());
                  childrenActionableItems.add(childAi);
               }
            }
         }
      }
      return childrenActionableItems;
   }

   @Override
   public IAtsActionableItem getParentActionableItem() {
      if (parentActionableItem == null && getArtifact() != null) {
         Artifact parentArt = getArtifact().getParent();
         if (parentArt != null && parentArt.isOfType(AtsArtifactTypes.ActionableItem)) {
            IAtsActionableItem parent = AtsClientService.get().getCache().getAtsObject(parentArt.getUuid());
            this.parentActionableItem = parent;
         }
      }
      return parentActionableItem;
   }

   @Override
   public Collection<String> getStaticIds() {
      if (staticIds == null) {
         staticIds = new HashSet<>();
         if (getArtifact() != null) {
            for (String staticId : getArtifact().getAttributesToStringList(CoreAttributeTypes.StaticId)) {
               staticIds.add(staticId);
            }
         }
      }
      return staticIds;
   }

   private Artifact getArtifact() {
      return (Artifact) getStoreObject();
   }

   @Override
   public void setParentActionableItem(IAtsActionableItem parentActionableItem) {
      if (parentActionableItem.getUuid().equals(getUuid())) {
         throw new IllegalArgumentException("Can't set parent to self");
      }
      this.parentActionableItem = parentActionableItem;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinitionInherited() {
      return TeamDefinitions.getImpactedTeamDef(this);
   }

   @Override
   public Collection<IAtsUser> getSubscribed() {
      if (subscribed == null) {
         subscribed = new HashSet<>();
         if (getArtifact() != null) {
            for (Artifact userArt : getArtifact().getRelatedArtifacts(AtsRelationTypes.SubscribedUser_User)) {
               IAtsUser user = AtsClientService.get().getUserServiceClient().getUserFromOseeUser((User) userArt);
               subscribed.add(user);
            }
         }
      }
      return subscribed;
   }

   @Override
   public boolean isAllowUserActionCreation() {
      return allowUserActionCreation;
   }

   @Override
   public void setAllowUserActionCreation(boolean allowUserActionCreation) {
      this.allowUserActionCreation = allowUserActionCreation;
   }

   /**
    * Rules
    */
   @Override
   public List<String> getRules() {
      return ((Artifact) getStoreObject()).getAttributesToStringList(AtsAttributeTypes.RuleDefinition);
   }

   @Override
   public boolean hasRule(String rule) {
      return getRules().contains(rule);
   }

}
