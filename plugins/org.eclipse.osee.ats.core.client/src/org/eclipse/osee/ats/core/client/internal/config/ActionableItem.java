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
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.config.RuleManager;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.model.impl.AtsObject;

/**
 * @author Donald G. Dunne
 */
public class ActionableItem extends AtsObject implements IAtsActionableItem {

   private boolean actionable = true;
   private boolean allowUserActionCreation = true;
   private boolean active = true;
   private IAtsTeamDefinition teamDefinition;
   private IAtsActionableItem parentActionableItem;
   private final Set<String> staticIds = new HashSet<String>();
   private final Set<IAtsUser> leads = new HashSet<IAtsUser>();
   private final Set<IAtsUser> subscribed = new HashSet<IAtsUser>();
   private final Set<IAtsActionableItem> childrenActionableItems = new HashSet<IAtsActionableItem>();
   private final RuleManager ruleMgr = new RuleManager();

   public ActionableItem(String name, String guid, long uuid) {
      super(name, uuid);
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition() {
      return teamDefinition;
   }

   @Override
   public Collection<IAtsUser> getLeads() {
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
      return childrenActionableItems;
   }

   @Override
   public IAtsActionableItem getParentActionableItem() {
      return parentActionableItem;
   }

   @Override
   public Collection<String> getStaticIds() {
      return staticIds;
   }

   @Override
   public void setParentActionableItem(IAtsActionableItem parentActionableItem) {
      if (parentActionableItem.getUuid() == getUuid()) {
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
   public void removeRule(String rule) {
      ruleMgr.removeRule(rule);
   }

   @Override
   public List<String> getRules() {
      return ruleMgr.getRules();
   }

   @Override
   public void addRule(String rule) {
      ruleMgr.addRule(rule);
   }

   @Override
   public boolean hasRule(String rule) {
      return ruleMgr.hasRule(rule);
   }

}
