/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workdef.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsRuleDefinition;
import org.eclipse.osee.ats.api.workdef.RuleEventType;
import org.eclipse.osee.ats.api.workdef.RuleLocations;

/**
 * @author Mark Joy
 */
public class RuleDefinition implements IAtsRuleDefinition {
   public String name = "";
   public String title = "";
   public String description = "";
   public List<RuleLocations> ruleLocs = new ArrayList<>();
   public List<RuleEventType> ruleEvents = new ArrayList<>();
   public final List<IAtsUser> assignees = new ArrayList<>();

   @Override
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   @Override
   public List<IAtsUser> getAssignees() {
      return assignees;
   }

   @Override
   public List<RuleLocations> getRuleLocs() {
      return ruleLocs;
   }

   public void setRuleLocs(List<RuleLocations> ruleLocs) {
      this.ruleLocs = ruleLocs;
   }

   @Override
   public void addRuleEvent(RuleEventType ruleEventType) {
      ruleEvents.add(ruleEventType);
   }

   @Override
   public List<RuleEventType> getRuleEvents() {
      return ruleEvents;
   }

   @Override
   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

}
