/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AtsTeamDefinitionOrAi;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.BuildImpactData;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Country;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Insertion;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.InsertionActivity;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Program;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Project;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.ReleaseArtifact;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Task;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamDefinition;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamWorkflow;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Version;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.WorkDefinition;
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

/**
 * @author Donald G. Dunne
 */
public interface AtsRelationTypes {

   //@formatter:off

   // relation links
   RelationTypeToken ActionToWorkflow = ats.add(2305843009213694317L, "ActionToWorkflow", ONE_TO_MANY, UNORDERED, Action, "Action", TeamWorkflow, "Team Workflow");
   RelationTypeSide ActionToWorkflow_Action = RelationTypeSide.create(ActionToWorkflow, SIDE_A);
   RelationTypeSide ActionToWorkflow_TeamWorkflow = RelationTypeSide.create(ActionToWorkflow, SIDE_B);

   RelationTypeToken ActionableItemLead = ats.add(2305843009213694329L, "ActionableItemLead", MANY_TO_MANY, UNORDERED, ActionableItem, "Actionable Item", User, "User");
   RelationTypeSide ActionableItemLead_AI = RelationTypeSide.create(ActionableItemLead, SIDE_A);
   RelationTypeSide ActionableItemLead_Lead = RelationTypeSide.create(ActionableItemLead, SIDE_B);

   RelationTypeToken AgileFeatureToItem = ats.add(6017077976601091441L, "AgileFeatureToItem", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, AgileFeatureGroup, "Agile Feature Group", AbstractWorkflowArtifact, "ATS Item");
   RelationTypeSide AgileFeatureToItem_AgileFeatureGroup = RelationTypeSide.create(AgileFeatureToItem, SIDE_A);
   RelationTypeSide AgileFeatureToItem_AtsItem = RelationTypeSide.create(AgileFeatureToItem, SIDE_B);

   RelationTypeToken AgileSprintToItem = ats.add(988214123009313457L, "AgileSprintToItem", ONE_TO_MANY, LEXICOGRAPHICAL_ASC, AgileSprint, "Agile Sprint", AtsArtifact, "ATS Item");
   RelationTypeSide AgileSprintToItem_AgileSprint = RelationTypeSide.create(AgileSprintToItem, SIDE_A);
   RelationTypeSide AgileSprintToItem_AtsItem = RelationTypeSide.create(AgileSprintToItem, SIDE_B);

   RelationTypeToken AgileStoryToAgileTeam = ats.add(7984323968228307345L, "AgileStoryToAgileTeam", MANY_TO_ONE, LEXICOGRAPHICAL_ASC, AgileStory, "Agile Story", AgileTeam, "Agile Team");
   RelationTypeSide AgileStoryToAgileTeam_AgileStory = RelationTypeSide.create(AgileStoryToAgileTeam, SIDE_A);
   RelationTypeSide AgileStoryToAgileTeam_AgileTeam = RelationTypeSide.create(AgileStoryToAgileTeam, SIDE_B);

   RelationTypeToken AgileStoryToItem = ats.add(98821417946551335L, "AgileStoryToItem", ONE_TO_MANY, LEXICOGRAPHICAL_ASC, AgileStory, "Agile Story", TeamWorkflow, "Team Workflow");
   RelationTypeSide AgileStoryToItem_AgileStory = RelationTypeSide.create(AgileStoryToItem, SIDE_A);
   RelationTypeSide AgileStoryToItem_TeamWorkflow = RelationTypeSide.create(AgileStoryToItem, SIDE_B);

   RelationTypeToken AgileStoryToSprint = ats.add(2639165674435679873L, "AgileStoryToSprint", MANY_TO_ONE, LEXICOGRAPHICAL_ASC, AgileStory, "Agile Story", AgileSprint, "Agile Sprint");
   RelationTypeSide AgileStoryToSprint_AgileStory = RelationTypeSide.create(AgileStoryToSprint, SIDE_A);
   RelationTypeSide AgileStoryToSprint_AgileSprint = RelationTypeSide.create(AgileStoryToSprint, SIDE_B);

   RelationTypeToken AgileTeamToAtsAis = ats.add(5336467317030669830L, "AgileTeamToAtsAis", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, AgileTeam, "Agile Team", ActionableItem, "ATS AIs");
   RelationTypeSide AgileTeamToAtsAis_AgileTeam = RelationTypeSide.create(AgileTeamToAtsAis, SIDE_A);
   RelationTypeSide AgileTeamToAtsAis_AtsAis = RelationTypeSide.create(AgileTeamToAtsAis, SIDE_B);

   RelationTypeToken AgileTeamToAtsTeam = ats.add(9001858956696556140L, "AgileTeamToAtsTeam", ONE_TO_MANY, LEXICOGRAPHICAL_ASC, AgileTeam, "Agile Team", TeamDefinition, "ATS Team");
   RelationTypeSide AgileTeamToAtsTeam_AgileTeam = RelationTypeSide.create(AgileTeamToAtsTeam, SIDE_A);
   RelationTypeSide AgileTeamToAtsTeam_AtsTeam = RelationTypeSide.create(AgileTeamToAtsTeam, SIDE_B);

   RelationTypeToken AgileTeamToBacklog = ats.add(8816366550731954418L, "AgileTeamToBacklog", ONE_TO_ONE, LEXICOGRAPHICAL_ASC, AgileTeam, "Agile Team", AtsArtifactTypes.Goal, "Backlog");
   RelationTypeSide AgileTeamToBacklog_AgileTeam = RelationTypeSide.create(AgileTeamToBacklog, SIDE_A);
   RelationTypeSide AgileTeamToBacklog_Backlog = RelationTypeSide.create(AgileTeamToBacklog, SIDE_B);

   RelationTypeToken AgileTeamToFeatureGroup = ats.add(1067226929733341458L, "AgileTeamToFeatureGroup", ONE_TO_MANY, LEXICOGRAPHICAL_ASC, AgileTeam, "Agile Team", AgileFeatureGroup, "Agile Feature Group");
   RelationTypeSide AgileTeamToFeatureGroup_AgileTeam = RelationTypeSide.create(AgileTeamToFeatureGroup, SIDE_A);
   RelationTypeSide AgileTeamToFeatureGroup_AgileFeatureGroup = RelationTypeSide.create(AgileTeamToFeatureGroup, SIDE_B);

   RelationTypeToken AgileTeamToSprint = ats.add(7043708594778812661L, "AgileTeamToSprint", ONE_TO_MANY, LEXICOGRAPHICAL_ASC, AgileTeam, "Agile Team", AgileSprint, "Sprint");
   RelationTypeSide AgileTeamToSprint_AgileTeam = RelationTypeSide.create(AgileTeamToSprint, SIDE_A);
   RelationTypeSide AgileTeamToSprint_Sprint = RelationTypeSide.create(AgileTeamToSprint, SIDE_B);

   RelationTypeToken AutoAddActionToGoal = ats.add(2305843009213694333L, "AutoAddActionToGoal", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, AtsArtifactTypes.Goal, "Goal", AtsConfigArtifact, "Ats Config Object");
   RelationTypeSide AutoAddActionToGoal_Goal = RelationTypeSide.create(AutoAddActionToGoal, SIDE_A);
   RelationTypeSide AutoAddActionToGoal_AtsConfigObject = RelationTypeSide.create(AutoAddActionToGoal, SIDE_B);

   RelationTypeToken CountryToProgram = ats.add(2305846526791909737L, "Country To Program", ONE_TO_MANY, UNORDERED, Country, "Country", Program, "Program");
   RelationTypeSide CountryToProgram_Country = RelationTypeSide.create(CountryToProgram, SIDE_A);
   RelationTypeSide CountryToProgram_Program = RelationTypeSide.create(CountryToProgram, SIDE_B);

   RelationTypeToken Derive = ats.add(2305843009213694331L, "Derive", ONE_TO_MANY, UNORDERED, AbstractWorkflowArtifact, "From", AbstractWorkflowArtifact, "To");
   RelationTypeSide Derive_From = RelationTypeSide.create(Derive, SIDE_A);
   RelationTypeSide Derive_To = RelationTypeSide.create(Derive, SIDE_B);

   RelationTypeToken FavoriteUser = ats.add(2305843009213694323L, "FavoriteUser", MANY_TO_MANY, UNORDERED, Artifact, "Artifact", User, "User");
   RelationTypeSide FavoriteUser_Artifact = RelationTypeSide.create(FavoriteUser, SIDE_A);
   RelationTypeSide FavoriteUser_User = RelationTypeSide.create(FavoriteUser, SIDE_B);

   RelationTypeToken Goal = ats.add(2305843009213694325L, "Goal", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, AtsArtifactTypes.Goal, "Goal", AtsArtifact, "Member");
   RelationTypeSide Goal_Goal = RelationTypeSide.create(Goal, SIDE_A);
   RelationTypeSide Goal_Member = RelationTypeSide.create(Goal, SIDE_B);

   // Backlogs use same relation as Goal; This is here for readability and to document this in code
    RelationTypeSide AgileBacklog_AgileBacklog = Goal_Goal;
    RelationTypeSide AgileBacklog_Item = AgileBacklog_AgileBacklog.getOpposite();

   RelationTypeToken InsertionActivityToWorkPackage = ats.add(8892387571282380815L, "Insertion Activity To Work Package", ONE_TO_MANY, UNORDERED, InsertionActivity, "Insertion Activity", AtsArtifactTypes.WorkPackage, "Work Package");
   RelationTypeSide InsertionActivityToWorkPackage_InsertionActivity = RelationTypeSide.create(InsertionActivityToWorkPackage, SIDE_A);
   RelationTypeSide InsertionActivityToWorkPackage_WorkPackage = RelationTypeSide.create(InsertionActivityToWorkPackage, SIDE_B);

   RelationTypeToken InsertionToInsertionActivity = ats.add(1336895299757203121L, "Insertion To Insertion Activity", ONE_TO_MANY, UNORDERED, Insertion, "Insertion", InsertionActivity, "Insertion Activity");
   RelationTypeSide InsertionToInsertionActivity_Insertion = RelationTypeSide.create(InsertionToInsertionActivity, SIDE_A);
   RelationTypeSide InsertionToInsertionActivity_InsertionActivity = RelationTypeSide.create(InsertionToInsertionActivity, SIDE_B);

   RelationTypeToken Owner = ats.add(2305843009213694328L, "Owner", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, ActionableItem, "Actionable Item", User, "Owner");
   RelationTypeSide Owner_ActionableItem = RelationTypeSide.create(Owner, SIDE_A);
   RelationTypeSide Owner_Owner = RelationTypeSide.create(Owner, SIDE_B);

    // Use same relation as Owner; This is here for readability and to document this in code
    RelationTypeSide ActionableItem_Artifact = Owner_ActionableItem;
    RelationTypeSide ActionableItem_User = ActionableItem_Artifact.getOpposite();

   RelationTypeToken ParallelVersion = ats.add(2305843009213694324L, "ParallelVersion", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, Version, "Parent", Version, "Child");
   RelationTypeSide ParallelVersion_Parent = RelationTypeSide.create(ParallelVersion, SIDE_A);
   RelationTypeSide ParallelVersion_Child = RelationTypeSide.create(ParallelVersion, SIDE_B);

   RelationTypeToken Port = ats.add(2305843009213694330L, "Port", MANY_TO_MANY, UNORDERED, TeamWorkflow, "From", TeamWorkflow, "To");
   RelationTypeSide Port_From = RelationTypeSide.create(Port, SIDE_A);
   RelationTypeSide Port_To = RelationTypeSide.create(Port, SIDE_B);

   RelationTypeToken PrivilegedMember = ats.add(2305843009213694315L, "PrivilegedMember", MANY_TO_MANY, UNORDERED, TeamDefinition, "Team Definition", User, "User");
   RelationTypeSide PrivilegedMember_Team = RelationTypeSide.create(PrivilegedMember, SIDE_A);
   RelationTypeSide PrivilegedMember_User = RelationTypeSide.create(PrivilegedMember, SIDE_B);

   RelationTypeToken ProgramToInsertion = ats.add(8921796037933812267L, "Program To Insertion", ONE_TO_MANY, UNORDERED, Program, "Program", Insertion, "Insertion");
   RelationTypeSide ProgramToInsertion_Program = RelationTypeSide.create(ProgramToInsertion, SIDE_A);
   RelationTypeSide ProgramToInsertion_Insertion = RelationTypeSide.create(ProgramToInsertion, SIDE_B);

   RelationTypeToken ResponsibleTeam = ats.add(7316843349212764388L, "ResponsibleTeam", MANY_TO_ONE, UNORDERED, TeamWorkflow, "Team Workflow", AtsArtifactTypes.ResponsibleTeam, "Responsible Team");
   RelationTypeSide ResponsibleTeam_TeamWorkflow = RelationTypeSide.create(ResponsibleTeam, SIDE_A);
   RelationTypeSide ResponsibleTeam_ResponsibleTeam = RelationTypeSide.create(ResponsibleTeam, SIDE_B);

   RelationTypeToken SubscribedUser = ats.add(2305843009213694322L, "SubscribedUser", MANY_TO_MANY, UNORDERED, Artifact, "Artifact", User, "User");
   RelationTypeSide SubscribedUser_Artifact = RelationTypeSide.create(SubscribedUser, SIDE_A);
   RelationTypeSide SubscribedUser_User = RelationTypeSide.create(SubscribedUser, SIDE_B);

   RelationTypeToken TeamActionableItem = ats.add(2305843009213694316L, "TeamActionableItem", ONE_TO_MANY, UNORDERED, TeamDefinition, "Team Definition", ActionableItem, "Actionable Item");
   RelationTypeSide TeamActionableItem_TeamDefinition = RelationTypeSide.create(TeamActionableItem, SIDE_A);
   RelationTypeSide TeamActionableItem_ActionableItem = RelationTypeSide.create(TeamActionableItem, SIDE_B);

   RelationTypeToken TeamDefinitionToVersion = ats.add(2305843009213694320L, "TeamDefinitionToVersion", ONE_TO_MANY, LEXICOGRAPHICAL_ASC, TeamDefinition, "Team Definition", Version, "Version");
   RelationTypeSide TeamDefinitionToVersion_TeamDefinition = RelationTypeSide.create(TeamDefinitionToVersion, SIDE_A);
   RelationTypeSide TeamDefinitionToVersion_Version = RelationTypeSide.create(TeamDefinitionToVersion, SIDE_B);

    // Use same relation as TeamDefinitionToVersion; This is here for readability and to document this in code
    RelationTypeSide TeamDefinitionToAtsConfigObject_TeamDefinition = TeamDefinitionToVersion_TeamDefinition;
    RelationTypeSide TeamDefinitionToAtsConfigObject_AtsConfigObject = TeamDefinitionToVersion_TeamDefinition.getOpposite();

   RelationTypeToken TeamLead = ats.add(2305843009213694313L, "TeamLead", MANY_TO_MANY, UNORDERED, AtsArtifactTypes.ResponsibleTeam, "Team Definition", User, "User");
   RelationTypeSide TeamLead_Team = RelationTypeSide.create(TeamLead, SIDE_A);
   RelationTypeSide TeamLead_Lead = RelationTypeSide.create(TeamLead, SIDE_B);

   RelationTypeToken TeamMember = ats.add(2305843009213694314L, "TeamMember", MANY_TO_MANY, UNORDERED, AtsArtifactTypes.ResponsibleTeam, "Team Definition", User, "User");
   RelationTypeSide TeamMember_Team = RelationTypeSide.create(TeamMember, SIDE_A);
   RelationTypeSide TeamMember_Member = RelationTypeSide.create(TeamMember, SIDE_B);

   RelationTypeToken TeamWfToTask = ats.add(2305843009213694318L, "TeamWfToTask", ONE_TO_MANY, LEXICOGRAPHICAL_ASC, TeamWorkflow, "Team Workflow", Task, "Task");
   RelationTypeSide TeamWfToTask_TeamWorkflow = RelationTypeSide.create(TeamWfToTask, SIDE_A);
   RelationTypeSide TeamWfToTask_Task = RelationTypeSide.create(TeamWfToTask, SIDE_B);

   RelationTypeToken TeamWorkflowTargetedForVersion = ats.add(2305843009213694319L, "TeamWorkflowTargetedForVersion", MANY_TO_ONE, UNORDERED, TeamWorkflow, "Team Workflow", Version, "Version");
   RelationTypeSide TeamWorkflowTargetedForVersion_TeamWorkflow = RelationTypeSide.create(TeamWorkflowTargetedForVersion, SIDE_A);
   RelationTypeSide TeamWorkflowTargetedForVersion_Version = RelationTypeSide.create(TeamWorkflowTargetedForVersion, SIDE_B);

   RelationTypeToken TeamWorkflowToFoundInVersion = ats.add(8432547963397826929L, "TeamWorkflowToFoundInVersion", MANY_TO_ONE, UNORDERED, TeamWorkflow, "Team Workflow", Version, "Version");
   RelationTypeSide TeamWorkflowToFoundInVersion_TeamWorkflow = RelationTypeSide.create(TeamWorkflowToFoundInVersion, SIDE_A);
   RelationTypeSide TeamWorkflowToFoundInVersion_Version = RelationTypeSide.create(TeamWorkflowToFoundInVersion, SIDE_B);

   RelationTypeToken TeamWorkflowToIntroducedInVersion = ats.add(144658762017629160L, "TeamWorkflowToIntroducedInVersion", MANY_TO_ONE, UNORDERED, TeamWorkflow, "Team Workflow", Version, "Version");
   RelationTypeSide TeamWorkflowToIntroducedInVersion_TeamWorkflow = RelationTypeSide.create(TeamWorkflowToIntroducedInVersion, SIDE_A);
   RelationTypeSide TeamWorkflowToIntroducedInVersion_Version = RelationTypeSide.create(TeamWorkflowToIntroducedInVersion, SIDE_B);

   RelationTypeToken TeamWorkflowToRelease = ats.add(2531996123289575340L, "TeamWorkflowToRelease", MANY_TO_MANY, UNORDERED, TeamWorkflow, "Team Workflow", ReleaseArtifact, "Release Artifact");
   RelationTypeSide TeamWorkflowToRelease_TeamWorkflow = RelationTypeSide.create(TeamWorkflowToRelease, SIDE_A);
   RelationTypeSide TeamWorkflowToRelease_Release = RelationTypeSide.create(TeamWorkflowToRelease, SIDE_B);

   RelationTypeToken TeamWorkflowToReview = ats.add(2305843009213694321L, "TeamWorkflowToReview", MANY_TO_MANY, UNORDERED, TeamWorkflow, "Team Workflow", AbstractReview, "Review");
   RelationTypeSide TeamWorkflowToReview_TeamWorkflow = RelationTypeSide.create(TeamWorkflowToReview, SIDE_A);
   RelationTypeSide TeamWorkflowToReview_Review = RelationTypeSide.create(TeamWorkflowToReview, SIDE_B);

   RelationTypeToken TeamDefinitionToWorkPackage = ats.add(2305843009213694334L, "TeamDefinitionToWorkPackage", MANY_TO_MANY, UNORDERED, AtsArtifactTypes.WorkPackage, "Work Package", AtsTeamDefinitionOrAi, "ATS Team Def or AI");
   RelationTypeSide TeamDefinitionToWorkPackage_WorkPackage = RelationTypeSide.create(TeamDefinitionToWorkPackage, SIDE_A);
   RelationTypeSide TeamDefinitionToWorkPackage_AtsTeamDefOrAi = RelationTypeSide.create(TeamDefinitionToWorkPackage, SIDE_B);

   RelationTypeToken UserGroupToActionableItem = ats.add(23875102986153L, "UserGroupToActionableItem", MANY_TO_ONE, UNORDERED, CoreArtifactTypes.UserGroup, "User Group", ActionableItem, "ActionableItem");
   RelationTypeSide UserGroupToActionableItem_UserGroup = RelationTypeSide.create(UserGroupToActionableItem, SIDE_A);
   RelationTypeSide UserGroupToActionableItem_AI = RelationTypeSide.create(UserGroupToActionableItem, SIDE_B);

   // Program uses supporting info to relate to team.  Use different name for readability and understandability
   RelationTypeSide TeamDefinitionToProgram_TeamDefinition = CoreRelationTypes.SupportingInfo_IsSupportedBy;
   RelationTypeSide TeamDefinitionToProgram_Program = TeamDefinitionToProgram_TeamDefinition.getOpposite();

 //iCTeam Types
   RelationTypeToken ActionableItemWorkFlow = ats.add(2305843009213694467L, "ActionableItemWorkFlow", MANY_TO_MANY, UNORDERED, ActionableItem, "Actionable Item", TeamWorkflow, "Team Workflow");
   RelationTypeSide ActionableItemWorkFlow_ActionableItem = RelationTypeSide.create(ActionableItemWorkFlow, SIDE_A);
   RelationTypeSide ActionableItemWorkFlow_TeamWorkflow = RelationTypeSide.create(ActionableItemWorkFlow, SIDE_B);

   RelationTypeToken AgileTaskLink = ats.add(2305843009213694352L, "AgileTaskLink", MANY_TO_MANY, UNORDERED, TeamWorkflow, "Team WorkFlowA", TeamWorkflow, "Team WorkFlowB");
   RelationTypeSide AgileTaskLink_TeamWorkflowA = RelationTypeSide.create(AgileTaskLink, SIDE_A);
   RelationTypeSide AgileTaskLink_TeamWorkflowB = RelationTypeSide.create(AgileTaskLink, SIDE_B);

   RelationTypeToken ProjectToAction = ats.add(2305843009213694464L, "ProjectToAction", MANY_TO_MANY, UNORDERED, Project, "Project", Action, "Action");
   RelationTypeSide ProjectToAction_Project = RelationTypeSide.create(ProjectToAction, SIDE_A);
   RelationTypeSide ProjectToAction_Action = RelationTypeSide.create(ProjectToAction, SIDE_B);

   RelationTypeToken ProjectToActionableItem = ats.add(2305843009213694465L, "ProjectToActionableItem", MANY_TO_MANY, UNORDERED, Project, "Project", ActionableItem, "Actionable Item");
   RelationTypeSide ProjectToActionableItem_Project = RelationTypeSide.create(ProjectToActionableItem, SIDE_A);
   RelationTypeSide ProjectToActionableItem_ActionableItem = RelationTypeSide.create(ProjectToActionableItem, SIDE_B);

   RelationTypeToken ProjectToTeamDefinition = ats.add(2305843009213694361L, "ProjectToTeamDefinition", MANY_TO_MANY, UNORDERED, Project, "Project", TeamDefinition, "Team Definition");
   RelationTypeSide ProjectToTeamDefinition_Project = RelationTypeSide.create(ProjectToTeamDefinition, SIDE_A);
   RelationTypeSide ProjectToTeamDefinition_TeamDefinition = RelationTypeSide.create(ProjectToTeamDefinition, SIDE_B);

   RelationTypeToken ProjectToTeamWorkFlow = ats.add(2305843009214812512L, "ProjectToTeamWorkFlow", MANY_TO_MANY, UNORDERED, Project, "Project", TeamWorkflow, "Team Workflow");
   RelationTypeSide ProjectToTeamWorkFlow_Project = RelationTypeSide.create(ProjectToTeamWorkFlow, SIDE_A);
   RelationTypeSide ProjectToTeamWorkFlow_TeamWorkflow = RelationTypeSide.create(ProjectToTeamWorkFlow, SIDE_B);

   RelationTypeToken ProjectToUser = ats.add(144115188075925782L, "ProjectToUser", MANY_TO_MANY, UNORDERED, Project, "Project", User, "User");
   RelationTypeSide ProjectToUser_Project = RelationTypeSide.create(ProjectToUser, SIDE_A);
   RelationTypeSide ProjectToUser_User = RelationTypeSide.create(ProjectToUser, SIDE_B);

   RelationTypeToken ProjectToVersion = ats.add(2305843009214812513L, "ProjectToVersion", MANY_TO_MANY, UNORDERED, Project, "Project", Version, "Version");
   RelationTypeSide ProjectToVersion_Project = RelationTypeSide.create(ProjectToVersion, SIDE_A);
   RelationTypeSide ProjectToVersion_Version = RelationTypeSide.create(ProjectToVersion, SIDE_B);

   RelationTypeToken ProjectToWorkDefinition = ats.add(2305843009213694466L, "ProjectToWorkDefinition", MANY_TO_MANY, UNORDERED, Project, "Project", WorkDefinition, "Work Definition");
   RelationTypeSide ProjectToWorkDefinition_Project = RelationTypeSide.create(ProjectToWorkDefinition, SIDE_A);
   RelationTypeSide ProjectToWorkDefinition_WorkDefinition = RelationTypeSide.create(ProjectToWorkDefinition, SIDE_B);

   RelationTypeToken TaskLink = ats.add(144115188075855994L, "TaskLink", MANY_TO_MANY, UNORDERED, TeamWorkflow, "From", TeamWorkflow, "To");
   RelationTypeSide TaskLink_From = RelationTypeSide.create(TaskLink, SIDE_A);
   RelationTypeSide TaskLink_To = RelationTypeSide.create(TaskLink, SIDE_B);

   RelationTypeToken BuildImpactTableToData = ats.add(3882602607123235820L, "BuildImpactTableToData", ONE_TO_MANY, UNORDERED, TeamWorkflow, "CR/PR TeamWf", BuildImpactData, "BuildImpactData");
   RelationTypeSide BuildImpactTableToData_TeamWf = RelationTypeSide.create(BuildImpactTableToData, SIDE_A);
   RelationTypeSide BuildImpactTableToData_Bid = RelationTypeSide.create(BuildImpactTableToData, SIDE_B);

   RelationTypeToken BuildImpactDataToTeamWf = ats.add(353139807960131327L, "BuildImpactDataToTeamWf", ONE_TO_MANY, UNORDERED, BuildImpactData, "BuildImpactData", TeamWorkflow, "TeamWf" );
   RelationTypeSide BuildImpactDataToTeamWf_Bid = RelationTypeSide.create(BuildImpactDataToTeamWf, SIDE_A);
   RelationTypeSide BuildImpactDataToTeamWf_TeamWf = RelationTypeSide.create(BuildImpactDataToTeamWf, SIDE_B);

   RelationTypeToken BuildImpactDataToVer = ats.add(6805617497107248393L, "BuildImpactDataToVer", MANY_TO_ONE, UNORDERED, BuildImpactData, "BuildImpactdata", Version, "Version");
   RelationTypeSide BuildImpactDataToVer_Bid = RelationTypeSide.create(BuildImpactDataToVer, SIDE_A);
   RelationTypeSide BuildImpactDataToVer_Version = RelationTypeSide.create(BuildImpactDataToVer, SIDE_B);

   //@formatter:on
}