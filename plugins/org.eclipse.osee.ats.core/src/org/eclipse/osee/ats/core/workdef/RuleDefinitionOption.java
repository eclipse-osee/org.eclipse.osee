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
package org.eclipse.osee.ats.core.workdef;

public enum RuleDefinitionOption {

   RequireStateHourSpentPrompt("Work Page Option: Will popup a dialog to prompt user for time spent in this state."),
   AddDecisionValidateBlockingReview("Work Page Option: Will auto-create a blocking decision review for this state requesting validation for this workflow."),
   AddDecisionValidateNonBlockingReview("Work Page Option: Will auto-create a non blocking decision review requesting validation of workflow changes."),
   AllowTransitionWithWorkingBranch("Work Page Option: Will allow transition to next state without committing current working branch."),
   ForceAssigneesToTeamLeads("Work Page Option: Will force this state to be assigned back to the configured team leads.  Useful for authorization state."),
   RequireTargetedVersion("Work Page and Team Definition Option: Requires workflow to be targeted for version before transition is allowed."),
   AllowPriviledgedEditToTeamMember("Work Page and Team Definition Option: Allow team member to priviledged edit workflow assigned to team."),
   AllowPriviledgedEditToTeamMemberAndOriginator("Work Page and Team Definition Option: Allow team member to priviledged edit workflow assigned to team if user is originator."),
   AllowPriviledgedEditToAll("Work Page and Team Definition Option: Allow anyone to priviledged edit workflow assigned to team."),
   AllowEditToAll("Work Page and Team Definition Option: Allow anyone to edit workflow without being assignee."),
   AllowAssigneeToAll("Work Page and Team Definition Option: Allow anyone to change workflow assignee without being assignee.");

   public final String description;

   public String getDescription() {
      return description;
   }

   private RuleDefinitionOption(String description) {
      this.description = description;
   }
}
