/*******************************************************************************
 * Copyright (c) 2011 Boeing.
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
import org.eclipse.osee.ats.api.workdef.RuleLocations;

/**
 * @author Donald G. Dunne
 */
public enum RuleDefinitionOption {

   RequireStateHourSpentPrompt("StateDefinition Option: Will popup a dialog to prompt user for time spent in this state.", RuleLocations.StateDefinition),
   AddDecisionValidateBlockingReview("StateDefinition Option: Will auto-create a blocking decision review for this state requesting validation for this workflow.", RuleLocations.StateDefinition),
   AddDecisionValidateNonBlockingReview("StateDefinition Option: Will auto-create a non blocking decision review requesting validation of workflow changes.", RuleLocations.StateDefinition),
   AllowTransitionWithWorkingBranch("StateDefinition Option: Will allow transition to this state without committing current working branch.", RuleLocations.StateDefinition),
   ForceAssigneesToTeamLeads("StateDefinition Option: Will force this state to be assigned back to the configured team leads.  Useful for authorization state.", RuleLocations.StateDefinition),
   RequireTargetedVersion("StateDefinition and Team Definition Option: Requires workflow to be targeted for version before transition is allowed.", RuleLocations.StateDefinition, RuleLocations.TeamDefinition),
   AllowPrivilegedEditToTeamMember("StateDefinition and Team Definition Option: Allow team member to privileged edit workflow assigned to team.", RuleLocations.StateDefinition, RuleLocations.TeamDefinition),
   AllowPrivilegedEditToTeamMemberAndOriginator("StateDefinition and Team Definition Option: Allow team member to privileged edit workflow assigned to team if user is originator.", RuleLocations.StateDefinition, RuleLocations.TeamDefinition),
   AllowPrivilegedEditToAll("StateDefinition and Team Definition Option: Allow anyone to privileged edit workflow assigned to team.", RuleLocations.StateDefinition, RuleLocations.TeamDefinition),
   AllowEditToAll("StateDefinition and Team Definition Option: Allow anyone to edit workflow without being assignee.", RuleLocations.StateDefinition, RuleLocations.TeamDefinition),
   AllowAssigneeToAll("StateDefinition and Team Definition Option: Allow anyone to change workflow assignee without being assignee.", RuleLocations.StateDefinition, RuleLocations.TeamDefinition),
   AllowTransitionWithoutTaskCompletion("StateDefinition Option: Allow tasks to transition to other InWork states without completion.", RuleLocations.StateDefinition);

   public final String description;
   public List<RuleLocations> ruleLocs = new ArrayList<>();

   public String getDescription() {
      return description;
   }

   public List<RuleLocations> getRuleLocations() {
      return ruleLocs;
   }

   private RuleDefinitionOption(String description, RuleLocations location, RuleLocations... locations) {
      this.description = description;
      this.ruleLocs.add(location);
      for (int i = 0; i < locations.length; i++) {
         this.ruleLocs.add(locations[i]);
      }
   }
}
