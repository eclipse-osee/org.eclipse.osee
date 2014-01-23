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
package org.eclipse.osee.ats.api.workdef;

/**
 * @author Donald G. Dunne
 */
public enum RuleDefinitionOption {

   RequireStateHourSpentPrompt("StateDefinition Option: Will popup a dialog to prompt user for time spent in this state."),
   AddDecisionValidateBlockingReview("StateDefinition Option: Will auto-create a blocking decision review for this state requesting validation for this workflow."),
   AddDecisionValidateNonBlockingReview("StateDefinition Option: Will auto-create a non blocking decision review requesting validation of workflow changes."),
   AllowTransitionWithWorkingBranch("StateDefinition Option: Will allow transition to this state without committing current working branch."),
   ForceAssigneesToTeamLeads("StateDefinition Option: Will force this state to be assigned back to the configured team leads.  Useful for authorization state."),
   RequireTargetedVersion("StateDefinition and Team Definition Option: Requires workflow to be targeted for version before transition is allowed."),
   AllowPrivilegedEditToTeamMember("StateDefinition and Team Definition Option: Allow team member to privileged edit workflow assigned to team."),
   AllowPrivilegedEditToTeamMemberAndOriginator("StateDefinition and Team Definition Option: Allow team member to privileged edit workflow assigned to team if user is originator."),
   AllowPrivilegedEditToAll("StateDefinition and Team Definition Option: Allow anyone to privileged edit workflow assigned to team."),
   AllowEditToAll("StateDefinition and Team Definition Option: Allow anyone to edit workflow without being assignee."),
   AllowAssigneeToAll("StateDefinition and Team Definition Option: Allow anyone to change workflow assignee without being assignee."),
   AllowTransitionWithoutTaskCompletion("StateDefinition Option: Allow tasks to transition to other InWork states without completion.");

   public final String description;

   public String getDescription() {
      return description;
   }

   private RuleDefinitionOption(String description) {
      this.description = description;
   }
}
