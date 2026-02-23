/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.api.data;

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AbstractReview;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AbstractWorkflowArtifact;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Action;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.ActionableItem;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AgileFeatureGroup;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AgileSprint;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AgileStory;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AgileTeam;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AtsArtifact;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AtsConfigArtifact;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.BuildImpactData;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Country;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Insertion;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.InsertionActivity;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Program;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.ReleaseArtifact;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Task;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamDefinition;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamWorkflow;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Version;
import static org.eclipse.osee.ats.api.data.AtsTypeTokenProvider.ats;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.User;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.UNORDERED;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.MANY_TO_MANY;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.MANY_TO_ONE;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.ONE_TO_MANY;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.ONE_TO_ONE;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;

public interface ShadowAtsRelationTypes {

   //@formatter:off

   // relation links
   RelationTypeToken ActionToWorkflowRel = ats.addNewRelationType(4400405161969600836L, "ActionToWorkflow", ONE_TO_MANY, UNORDERED, Action, "Action", TeamWorkflow, "Team Workflow",AtsRelationTypes.ActionToWorkflow);
   RelationTypeSide ActionToWorkflowRel_Action = RelationTypeSide.create(ActionToWorkflowRel, SIDE_A);
   RelationTypeSide ActionToWorkflowRel_TeamWorkflow = RelationTypeSide.create(ActionToWorkflowRel, SIDE_B);

   RelationTypeToken ActionableItemLeadRel = ats.addNewRelationType(4400405161969600837L, "ActionableItemLead", MANY_TO_MANY, UNORDERED, ActionableItem, "Actionable Item", User, "User",AtsRelationTypes.ActionableItemLead);
   RelationTypeSide ActionableItemLeadRel_AI = RelationTypeSide.create(ActionableItemLeadRel, SIDE_A);
   RelationTypeSide ActionableItemLeadRel_Lead = RelationTypeSide.create(ActionableItemLeadRel, SIDE_B);

   RelationTypeToken AgileFeatureToItemRel = ats.addNewRelationType(4400405161969600838L, "AgileFeatureToItem", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, AgileFeatureGroup, "Agile Feature Group", AbstractWorkflowArtifact, "ATS Item",AtsRelationTypes.AgileFeatureToItem);
   RelationTypeSide AgileFeatureToItemRel_AgileFeatureGroup = RelationTypeSide.create(AgileFeatureToItemRel, SIDE_A);
   RelationTypeSide AgileFeatureToItemRel_AtsItem = RelationTypeSide.create(AgileFeatureToItemRel, SIDE_B);

   RelationTypeToken AgileSprintToItemRel = ats.addNewRelationType(4400405161969600839L, "AgileSprintToItem", ONE_TO_MANY, LEXICOGRAPHICAL_ASC, AgileSprint, "Agile Sprint", AtsArtifact, "ATS Item",AtsRelationTypes.AgileSprintToItem);
   RelationTypeSide AgileSprintToItemRel_AgileSprint = RelationTypeSide.create(AgileSprintToItemRel, SIDE_A);
   RelationTypeSide AgileSprintToItemRel_AtsItem = RelationTypeSide.create(AgileSprintToItemRel, SIDE_B);

   RelationTypeToken AgileStoryToAgileTeamRel = ats.addNewRelationType(4400405161969600840L, "AgileStoryToAgileTeam", MANY_TO_ONE, LEXICOGRAPHICAL_ASC, AgileStory, "Agile Story", AgileTeam, "Agile Team",AtsRelationTypes.AgileStoryToAgileTeam);
   RelationTypeSide AgileStoryToAgileTeamRel_AgileStory = RelationTypeSide.create(AgileStoryToAgileTeamRel, SIDE_A);
   RelationTypeSide AgileStoryToAgileTeamRel_AgileTeam = RelationTypeSide.create(AgileStoryToAgileTeamRel, SIDE_B);

   RelationTypeToken AgileStoryToItemRel = ats.addNewRelationType(4400405161969600841L, "AgileStoryToItem", ONE_TO_MANY, LEXICOGRAPHICAL_ASC, AgileStory, "Agile Story", TeamWorkflow, "Team Workflow",AtsRelationTypes.AgileStoryToItem);
   RelationTypeSide AgileStoryToItemRel_AgileStory = RelationTypeSide.create(AgileStoryToItemRel, SIDE_A);
   RelationTypeSide AgileStoryToItemRel_TeamWorkflow = RelationTypeSide.create(AgileStoryToItemRel, SIDE_B);

   RelationTypeToken AgileStoryToSprintRel = ats.addNewRelationType(4400405161969600842L, "AgileStoryToSprint", MANY_TO_ONE, LEXICOGRAPHICAL_ASC, AgileStory, "Agile Story", AgileSprint, "Agile Sprint",AtsRelationTypes.AgileStoryToSprint);
   RelationTypeSide AgileStoryToSprintRel_AgileStory = RelationTypeSide.create(AgileStoryToSprintRel, SIDE_A);
   RelationTypeSide AgileStoryToSprintRel_AgileSprint = RelationTypeSide.create(AgileStoryToSprintRel, SIDE_B);

   RelationTypeToken AgileTeamToAtsAisRel = ats.addNewRelationType(4400405161969600843L, "AgileTeamToAtsAis", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, AgileTeam, "Agile Team", ActionableItem, "ATS AIs",AtsRelationTypes.AgileTeamToAtsAis);
   RelationTypeSide AgileTeamToAtsAisRel_AgileTeam = RelationTypeSide.create(AgileTeamToAtsAisRel, SIDE_A);
   RelationTypeSide AgileTeamToAtsAisRel_AtsAis = RelationTypeSide.create(AgileTeamToAtsAisRel, SIDE_B);

   RelationTypeToken AgileTeamToAtsTeamRel = ats.addNewRelationType(4400405161969600844L, "AgileTeamToAtsTeam", ONE_TO_MANY, LEXICOGRAPHICAL_ASC, AgileTeam, "Agile Team", TeamDefinition, "ATS Team",AtsRelationTypes.AgileTeamToAtsTeam);
   RelationTypeSide AgileTeamToAtsTeamRel_AgileTeam = RelationTypeSide.create(AgileTeamToAtsTeamRel, SIDE_A);
   RelationTypeSide AgileTeamToAtsTeamRel_AtsTeam = RelationTypeSide.create(AgileTeamToAtsTeamRel, SIDE_B);

   RelationTypeToken AgileTeamToBacklogRel = ats.addNewRelationType(4400405161969600845L, "AgileTeamToBacklog", ONE_TO_ONE, LEXICOGRAPHICAL_ASC, AgileTeam, "Agile Team", AtsArtifactTypes.Goal, "Backlog",AtsRelationTypes.AgileTeamToBacklog);
   RelationTypeSide AgileTeamToBacklogRel_AgileTeam = RelationTypeSide.create(AgileTeamToBacklogRel, SIDE_A);
   RelationTypeSide AgileTeamToBacklogRel_Backlog = RelationTypeSide.create(AgileTeamToBacklogRel, SIDE_B);

   RelationTypeToken AgileTeamToFeatureGroupRel = ats.addNewRelationType(4400405161969600846L, "AgileTeamToFeatureGroup", ONE_TO_MANY, LEXICOGRAPHICAL_ASC, AgileTeam, "Agile Team", AgileFeatureGroup, "Agile Feature Group",AtsRelationTypes.AgileTeamToFeatureGroup);
   RelationTypeSide AgileTeamToFeatureGroupRel_AgileTeam = RelationTypeSide.create(AgileTeamToFeatureGroupRel, SIDE_A);
   RelationTypeSide AgileTeamToFeatureGroupRel_AgileFeatureGroup = RelationTypeSide.create(AgileTeamToFeatureGroupRel, SIDE_B);

   RelationTypeToken AgileTeamToSprintRel = ats.addNewRelationType(4400405161969600847L, "AgileTeamToSprint", ONE_TO_MANY, LEXICOGRAPHICAL_ASC, AgileTeam, "Agile Team", AgileSprint, "Sprint",AtsRelationTypes.AgileTeamToSprint);
   RelationTypeSide AgileTeamToSprintRel_AgileTeam = RelationTypeSide.create(AgileTeamToSprintRel, SIDE_A);
   RelationTypeSide AgileTeamToSprintRel_Sprint = RelationTypeSide.create(AgileTeamToSprintRel, SIDE_B);

   RelationTypeToken AutoAddActionToGoalRel = ats.addNewRelationType(4400405161969600848L, "AutoAddActionToGoal", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, AtsArtifactTypes.Goal, "Goal", AtsConfigArtifact, "Ats Config Object",AtsRelationTypes.AutoAddActionToGoal);
   RelationTypeSide AutoAddActionToGoalRel_GoalRel = RelationTypeSide.create(AutoAddActionToGoalRel, SIDE_A);
   RelationTypeSide AutoAddActionToGoalRel_AtsConfigObject = RelationTypeSide.create(AutoAddActionToGoalRel, SIDE_B);

   RelationTypeToken CountryToProgramRel = ats.addNewRelationType(4400405161969600849L, "Country To Program", ONE_TO_MANY, UNORDERED, Country, "Country", Program, "Program",AtsRelationTypes.CountryToProgram);
   RelationTypeSide CountryToProgramRel_Country = RelationTypeSide.create(CountryToProgramRel, SIDE_A);
   RelationTypeSide CountryToProgramRel_Program = RelationTypeSide.create(CountryToProgramRel, SIDE_B);

   RelationTypeToken DeriveRel = ats.addNewRelationType(4400405161969600850L, "Derive", ONE_TO_MANY, UNORDERED, AbstractWorkflowArtifact, "From", AbstractWorkflowArtifact, "To",AtsRelationTypes.Derive);
   RelationTypeSide DeriveRel_From = RelationTypeSide.create(DeriveRel, SIDE_A);
   RelationTypeSide DeriveRel_To = RelationTypeSide.create(DeriveRel, SIDE_B);

   RelationTypeToken FavoriteUserRel = ats.addNewRelationType(4400405161969600851L, "FavoriteUser", MANY_TO_MANY, UNORDERED, Artifact, "Artifact", User, "User",AtsRelationTypes.FavoriteUser);
   RelationTypeSide FavoriteUserRel_Artifact = RelationTypeSide.create(FavoriteUserRel, SIDE_A);
   RelationTypeSide FavoriteUserRel_User = RelationTypeSide.create(FavoriteUserRel, SIDE_B);

   RelationTypeToken GoalRel = ats.addNewRelationType(4400405161969600852L, "Goal", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, AtsArtifactTypes.Goal, "Goal", AtsArtifact, "Member",AtsRelationTypes.Goal);
   RelationTypeSide GoalRel_GoalRel = RelationTypeSide.create(GoalRel, SIDE_A);
   RelationTypeSide GoalRel_Member = RelationTypeSide.create(GoalRel, SIDE_B);

   // Backlogs use same relation as Goal; This is here for readability and to document this in code
    RelationTypeSide AgileBacklogRel_AgileBacklog = GoalRel_GoalRel;
    RelationTypeSide AgileBacklogRel_Item = AgileBacklogRel_AgileBacklog.getOpposite();


    RelationTypeToken NewGoalRel = ats.addNewRelationType(4408190126402163773L, "NewGoal", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, AtsArtifactTypes.Goal, "Goal", AtsArtifact, "Member");
    RelationTypeSide NewGoalRel_Goal = RelationTypeSide.create(NewGoalRel, SIDE_A);
    RelationTypeSide NewGoalRel_Member = RelationTypeSide.create(NewGoalRel, SIDE_B);

 // Backlogs use same relation as Goal; This is here for readability and to document this in code
    RelationTypeSide NewAgileBacklog_AgileBacklog = NewGoalRel_Goal;
    RelationTypeSide NewAgileBacklog_Item = NewAgileBacklog_AgileBacklog.getOpposite();

   RelationTypeToken InsertionToInsertionActivityRel = ats.addNewRelationType(4400405161969600854L, "Insertion To Insertion Activity", ONE_TO_MANY, UNORDERED, Insertion, "Insertion", InsertionActivity, "Insertion Activity",AtsRelationTypes.InsertionToInsertionActivity);
   RelationTypeSide InsertionToInsertionActivityRel_Insertion = RelationTypeSide.create(InsertionToInsertionActivityRel, SIDE_A);
   RelationTypeSide InsertionToInsertionActivityRel_InsertionActivity = RelationTypeSide.create(InsertionToInsertionActivityRel, SIDE_B);

   RelationTypeToken OwnerRel = ats.addNewRelationType(4400405161969600855L, "Owner", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, ActionableItem, "Actionable Item", User, "Owner",AtsRelationTypes.Owner);
   RelationTypeSide OwnerRel_ActionableItem = RelationTypeSide.create(OwnerRel, SIDE_A);
   RelationTypeSide OwnerRel_OwnerRel = RelationTypeSide.create(OwnerRel, SIDE_B);

    // Use same relation as Owner; This is here for readability and to document this in code
    RelationTypeSide ActionableItem_Artifact = OwnerRel_ActionableItem;
    RelationTypeSide ActionableItem_User = ActionableItem_Artifact.getOpposite();

   RelationTypeToken ParallelVersionRel = ats.addNewRelationType(4400405161969600856L, "ParallelVersion", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, Version, "Parent", Version, "Child",AtsRelationTypes.ParallelVersion);
   RelationTypeSide ParallelVersionRel_Parent = RelationTypeSide.create(ParallelVersionRel, SIDE_A);
   RelationTypeSide ParallelVersionRel_Child = RelationTypeSide.create(ParallelVersionRel, SIDE_B);

   RelationTypeToken PortRel = ats.addNewRelationType(4400405161969600857L, "Port", MANY_TO_MANY, UNORDERED, TeamWorkflow, "From", TeamWorkflow, "To",AtsRelationTypes.Port);
   RelationTypeSide PortRel_From = RelationTypeSide.create(PortRel, SIDE_A);
   RelationTypeSide PortRel_To = RelationTypeSide.create(PortRel, SIDE_B);

   RelationTypeToken PrivilegedMemberRel = ats.addNewRelationType(4400405161969600858L, "PrivilegedMember", MANY_TO_MANY, UNORDERED, TeamDefinition, "Team Definition", User, "User",AtsRelationTypes.PrivilegedMember);
   RelationTypeSide PrivilegedMemberRel_Team = RelationTypeSide.create(PrivilegedMemberRel, SIDE_A);
   RelationTypeSide PrivilegedMemberRel_User = RelationTypeSide.create(PrivilegedMemberRel, SIDE_B);

   RelationTypeToken ProgramToInsertionRel = ats.addNewRelationType(4400405161969600859L, "Program To Insertion", ONE_TO_MANY, UNORDERED, Program, "Program", Insertion, "Insertion",AtsRelationTypes.ProgramToInsertion);
   RelationTypeSide ProgramToInsertionRel_Program = RelationTypeSide.create(ProgramToInsertionRel, SIDE_A);
   RelationTypeSide ProgramToInsertionRel_Insertion = RelationTypeSide.create(ProgramToInsertionRel, SIDE_B);

   RelationTypeToken ResponsibleTeamRel = ats.addNewRelationType(4400405161969600860L, "ResponsibleTeam", MANY_TO_ONE, UNORDERED, TeamWorkflow, "Team Workflow", AtsArtifactTypes.ResponsibleTeam, "Responsible Team",AtsRelationTypes.ResponsibleTeam);
   RelationTypeSide ResponsibleTeamRel_TeamWorkflow = RelationTypeSide.create(ResponsibleTeamRel, SIDE_A);
   RelationTypeSide ResponsibleTeamRel_ResponsibleTeamRel = RelationTypeSide.create(ResponsibleTeamRel, SIDE_B);

   RelationTypeToken SubscribedUserRel = ats.addNewRelationType(4400405161969600861L, "SubscribedUser", MANY_TO_MANY, UNORDERED, Artifact, "Artifact", User, "User",AtsRelationTypes.SubscribedUser);
   RelationTypeSide SubscribedUserRel_Artifact = RelationTypeSide.create(SubscribedUserRel, SIDE_A);
   RelationTypeSide SubscribedUserRel_User = RelationTypeSide.create(SubscribedUserRel, SIDE_B);

   RelationTypeToken TeamActionableItemRel = ats.addNewRelationType(4400405161969600862L, "TeamActionableItem", ONE_TO_MANY, UNORDERED, TeamDefinition, "Team Definition", ActionableItem, "Actionable Item",AtsRelationTypes.TeamActionableItem);
   RelationTypeSide TeamActionableItemRel_TeamDefinition = RelationTypeSide.create(TeamActionableItemRel, SIDE_A);
   RelationTypeSide TeamActionableItemRel_ActionableItem = RelationTypeSide.create(TeamActionableItemRel, SIDE_B);

   RelationTypeToken TeamDefinitionToVersionRel = ats.addNewRelationType(4400405161969600863L, "TeamDefinitionToVersion", ONE_TO_MANY, LEXICOGRAPHICAL_ASC, TeamDefinition, "Team Definition", Version, "Version",AtsRelationTypes.TeamDefinitionToVersion);
   RelationTypeSide TeamDefinitionToVersionRel_TeamDefinition = RelationTypeSide.create(TeamDefinitionToVersionRel, SIDE_A);
   RelationTypeSide TeamDefinitionToVersionRel_Version = RelationTypeSide.create(TeamDefinitionToVersionRel, SIDE_B);

    // Use same relation as TeamDefinitionToVersion; This is here for readability and to document this in code
    RelationTypeSide TeamDefinitionToAtsConfigObject_TeamDefinition = TeamDefinitionToVersionRel_TeamDefinition;
    RelationTypeSide TeamDefinitionToAtsConfigObject_AtsConfigObject = TeamDefinitionToVersionRel_TeamDefinition.getOpposite();

   RelationTypeToken TeamLeadRel = ats.addNewRelationType(4400405161969600864L, "TeamLead", MANY_TO_MANY, UNORDERED, AtsArtifactTypes.ResponsibleTeam, "Team Definition", User, "User");
   RelationTypeSide TeamLeadRel_Team = RelationTypeSide.create(TeamLeadRel, SIDE_A);
   RelationTypeSide TeamLeadRel_Lead = RelationTypeSide.create(TeamLeadRel, SIDE_B);

   RelationTypeToken TeamMemberRel = ats.addNewRelationType(4400405161969600865L, "TeamMember", MANY_TO_MANY, UNORDERED, AtsArtifactTypes.ResponsibleTeam, "Team Definition", User, "User");
   RelationTypeSide TeamMemberRel_Team = RelationTypeSide.create(TeamMemberRel, SIDE_A);
   RelationTypeSide TeamMemberRel_Member = RelationTypeSide.create(TeamMemberRel, SIDE_B);

   RelationTypeToken TeamWfToTaskRel = ats.addNewRelationType(4400405161969600866L, "TeamWfToTask", ONE_TO_MANY, LEXICOGRAPHICAL_ASC, TeamWorkflow, "Team Workflow", Task, "Task",AtsRelationTypes.TeamWfToTask);
   RelationTypeSide TeamWfToTaskRel_TeamWorkflow = RelationTypeSide.create(TeamWfToTaskRel, SIDE_A);
   RelationTypeSide TeamWfToTaskRel_Task = RelationTypeSide.create(TeamWfToTaskRel, SIDE_B);

   RelationTypeToken ResolvedByRel = ats.addNewRelationType(4400405161969600867L, "ResolvedBy", MANY_TO_MANY, UNORDERED, TeamWorkflow, "Team Workflow", TeamWorkflow, "Resolved By",AtsRelationTypes.ResolvedBy);
   RelationTypeSide ResolvedByRel_TeamWorkflow = RelationTypeSide.create(ResolvedByRel, SIDE_A);
   RelationTypeSide ResolvedByRel_ResolvedByRel = RelationTypeSide.create(ResolvedByRel, SIDE_B);

   RelationTypeToken TeamWorkflowTargetedForVersionRel = ats.addNewRelationType(4400405161969600868L, "TeamWorkflowTargetedForVersion", MANY_TO_ONE, UNORDERED, TeamWorkflow, "Team Workflow", Version, "Version",AtsRelationTypes.TeamWorkflowTargetedForVersion);
   RelationTypeSide TeamWorkflowTargetedForVersionRel_TeamWorkflow = RelationTypeSide.create(TeamWorkflowTargetedForVersionRel, SIDE_A);
   RelationTypeSide TeamWorkflowTargetedForVersionRel_Version = RelationTypeSide.create(TeamWorkflowTargetedForVersionRel, SIDE_B);

   RelationTypeToken TeamWorkflowToFoundInVersionRel = ats.addNewRelationType(4400405161969600869L, "TeamWorkflowToFoundInVersion", MANY_TO_ONE, UNORDERED, TeamWorkflow, "Team Workflow", Version, "Version",AtsRelationTypes.TeamWorkflowToFoundInVersion);
   RelationTypeSide TeamWorkflowToFoundInVersionRel_TeamWorkflow = RelationTypeSide.create(TeamWorkflowToFoundInVersionRel, SIDE_A);
   RelationTypeSide TeamWorkflowToFoundInVersionRel_Version = RelationTypeSide.create(TeamWorkflowToFoundInVersionRel, SIDE_B);

   RelationTypeToken TeamWorkflowToIntroducedInVersionRel = ats.addNewRelationType(4400405161969600870L, "TeamWorkflowToIntroducedInVersion", MANY_TO_ONE, UNORDERED, TeamWorkflow, "Team Workflow", Version, "Version",AtsRelationTypes.TeamWorkflowToIntroducedInVersion);
   RelationTypeSide TeamWorkflowToIntroducedInVersionRel_TeamWorkflow = RelationTypeSide.create(TeamWorkflowToIntroducedInVersionRel, SIDE_A);
   RelationTypeSide TeamWorkflowToIntroducedInVersionRel_Version = RelationTypeSide.create(TeamWorkflowToIntroducedInVersionRel, SIDE_B);

   RelationTypeToken TeamWorkflowToReleaseRel = ats.addNewRelationType(4400405161969600871L, "TeamWorkflowToRelease", MANY_TO_MANY, UNORDERED, TeamWorkflow, "Team Workflow", ReleaseArtifact, "Release Artifact",AtsRelationTypes.TeamWorkflowToRelease);
   RelationTypeSide TeamWorkflowToReleaseRel_TeamWorkflow = RelationTypeSide.create(TeamWorkflowToReleaseRel, SIDE_A);
   RelationTypeSide TeamWorkflowToReleaseRel_Release = RelationTypeSide.create(TeamWorkflowToReleaseRel, SIDE_B);

   RelationTypeToken TeamWorkflowToReviewRel = ats.addNewRelationType(4400405161969600872L, "TeamWorkflowToReview", MANY_TO_MANY, UNORDERED, TeamWorkflow, "Team Workflow", AbstractReview, "Review",AtsRelationTypes.TeamWorkflowToReview);
   RelationTypeSide TeamWorkflowToReviewRel_TeamWorkflow = RelationTypeSide.create(TeamWorkflowToReviewRel, SIDE_A);
   RelationTypeSide TeamWorkflowToReviewRel_Review = RelationTypeSide.create(TeamWorkflowToReviewRel, SIDE_B);

   RelationTypeToken UserGroupToActionableItemRel = ats.addNewRelationType(4400405161969600874L, "UserGroupToActionableItem", MANY_TO_ONE, UNORDERED, CoreArtifactTypes.UserGroup, "User Group", ActionableItem, "ActionableItem",AtsRelationTypes.UserGroupToActionableItem);
   RelationTypeSide UserGroupToActionableItemRel_UserGroup = RelationTypeSide.create(UserGroupToActionableItemRel, SIDE_A);
   RelationTypeSide UserGroupToActionableItemRel_AI = RelationTypeSide.create(UserGroupToActionableItemRel, SIDE_B);

   // Program uses supporting info to relate to team.  Use different name for readability and understandability
   RelationTypeSide TeamDefinitionToProgram_TeamDefinition = CoreRelationTypes.SupportingInfo_IsSupportedBy;
   RelationTypeSide TeamDefinitionToProgram_Program = TeamDefinitionToProgram_TeamDefinition.getOpposite();

   RelationTypeToken BuildImpactTableToDataRel = ats.addNewRelationType(4400405161969600875L, "BuildImpactTableToData", ONE_TO_MANY, UNORDERED, TeamWorkflow, "CR/PR TeamWf", BuildImpactData, "BuildImpactData");
   RelationTypeSide BuildImpactTableToDataRel_TeamWf = RelationTypeSide.create(BuildImpactTableToDataRel, SIDE_A);
   RelationTypeSide BuildImpactTableToDataRel_Bid = RelationTypeSide.create(BuildImpactTableToDataRel, SIDE_B);

   RelationTypeToken BuildImpactDataToTeamWfRel = ats.addNewRelationType(4400405161969600876L, "BuildImpactDataToTeamWf", ONE_TO_MANY, UNORDERED, BuildImpactData, "BuildImpactData", TeamWorkflow, "TeamWf" ,AtsRelationTypes.BuildImpactDataToTeamWf);
   RelationTypeSide BuildImpactDataToTeamWfRel_Bid = RelationTypeSide.create(BuildImpactDataToTeamWfRel, SIDE_A);
   RelationTypeSide BuildImpactDataToTeamWfRel_TeamWf = RelationTypeSide.create(BuildImpactDataToTeamWfRel, SIDE_B);

   RelationTypeToken BuildImpactDataToVerRel = ats.addNewRelationType(4400405161969600877L, "BuildImpactDataToVer", MANY_TO_ONE, UNORDERED, BuildImpactData, "BuildImpactdata", Version, "Version",AtsRelationTypes.BuildImpactDataToVer);
   RelationTypeSide BuildImpactDataToVerRel_Bid = RelationTypeSide.create(BuildImpactDataToVerRel, SIDE_A);
   RelationTypeSide BuildImpactDataToVerRel_Version = RelationTypeSide.create(BuildImpactDataToVerRel, SIDE_B);

   //@formatter:on
}