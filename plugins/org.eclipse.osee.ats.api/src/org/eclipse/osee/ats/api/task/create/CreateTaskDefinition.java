/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.task.create;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.RuleEventType;
import org.eclipse.osee.ats.api.workdef.RuleLocations;

/**
 * @author Donald G. Dunne
 */
public class CreateTaskDefinition {

   private String teamWf;
   private String relatedToState;
   public String name = "";
   public String title = "";
   public String description = "";
   public List<RuleLocations> ruleLocs = new ArrayList<>();
   public final List<IAtsUser> assignees = new ArrayList<>();
   private List<RuleEventType> ruleEvents;

   public void setRuleEvents(List<RuleEventType> ruleEvents) {
      this.ruleEvents = ruleEvents;
   }

   public String getTeamWf() {
      return teamWf;
   }

   public void setTeamWf(String teamWf) {
      this.teamWf = teamWf;
   }

   public String getRelatedToState() {
      return relatedToState;
   }

   public void setRelatedToState(String relatedToState) {
      this.relatedToState = relatedToState;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public List<RuleLocations> getRuleLocs() {
      return ruleLocs;
   }

   public void setRuleLocs(List<RuleLocations> ruleLocs) {
      this.ruleLocs = ruleLocs;
   }

   public List<RuleEventType> getRuleEvents() {
      return ruleEvents;
   }

   public List<IAtsUser> getAssignees() {
      return assignees;
   }

}
