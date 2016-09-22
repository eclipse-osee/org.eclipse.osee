/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.data;

import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author Donald G. Dunne
 */
public final class AtsRelationTypes {

   //@formatter:off

   public static final RelationTypeSide ProgramToInsertion_Program = RelationTypeSide.create(RelationSide.SIDE_A, 0x7BD0963A0F884A2BL, "Program To Insertion");
   public static final RelationTypeSide ProgramToInsertion_Insertion = ProgramToInsertion_Program.getOpposite();

   public static final RelationTypeSide InsertionToInsertionActivity_Insertion = RelationTypeSide.create(RelationSide.SIDE_A, 0x128D9B3123EADEB1L, "Insertion To Insertion Activity");
   public static final RelationTypeSide InsertionToInsertionActivity_InsertionActivity = InsertionToInsertionActivity_Insertion.getOpposite();

   public static final RelationTypeSide InsertionActivityToWorkPackage_InsertionActivity = RelationTypeSide.create(RelationSide.SIDE_A, 0x7B681B61D762880FL, "Insertion Activity To Work Package");
   public static final RelationTypeSide InsertionActivityToWorkPackage_WorkPackage = InsertionActivityToWorkPackage_InsertionActivity.getOpposite();

   public static final RelationTypeSide ActionToWorkflow_Action = RelationTypeSide.create(RelationSide.SIDE_A, 0x200000000000016DL, "ActionToWorkflow");
   public static final RelationTypeSide ActionToWorkflow_WorkFlow = ActionToWorkflow_Action.getOpposite();

   public static final RelationTypeSide AgileTeamToFeatureGroup_AgileTeam = RelationTypeSide.create(RelationSide.SIDE_A, 0x0ECF8D3CF97C9112L, "AgileTeamToFeatureGroup");
   public static final RelationTypeSide AgileTeamToFeatureGroup_FeatureGroup = AgileTeamToFeatureGroup_AgileTeam.getOpposite();

   public static final RelationTypeSide AgileTeamToAtsTeam_AgileTeam = RelationTypeSide.create(RelationSide.SIDE_A, 0x7CED0706F811126CL, "AgileTeamToAtsTeam");
   public static final RelationTypeSide AgileTeamToAtsTeam_AtsTeam = AgileTeamToAtsTeam_AgileTeam.getOpposite();

   public static final RelationTypeSide AgileTeamToBacklog_AgileTeam = RelationTypeSide.create(RelationSide.SIDE_A, 0x7A5A06AAB20398F2L, "AgileTeamToBacklog");
   public static final RelationTypeSide AgileTeamToBacklog_Backlog = AgileTeamToBacklog_AgileTeam.getOpposite();

   public static final RelationTypeSide AgileTeamToSprint_AgileTeam = RelationTypeSide.create(RelationSide.SIDE_A, 0x61C047A5D52830F5L, "AgileTeamToSprint");
   public static final RelationTypeSide AgileTeamToSprint_Sprint = AgileTeamToSprint_AgileTeam.getOpposite();

   public static final RelationTypeSide AgileSprintToItem_Sprint= RelationTypeSide.create(RelationSide.SIDE_A, 0x0DB6D78253FE8AB1L, "AgileSprintToItems");
   public static final RelationTypeSide AgileSprintToItem_AtsItem = AgileSprintToItem_Sprint.getOpposite();

   public static final RelationTypeSide AgileFeatureToItem_FeatureGroup = RelationTypeSide.create(RelationSide.SIDE_A, 0x5380F48A35225D71L, "AgileFeatureToItem");
   public static final RelationTypeSide AgileFeatureToItem_AtsItem = AgileFeatureToItem_FeatureGroup.getOpposite();

   public static final RelationTypeSide Port_From = RelationTypeSide.create(RelationSide.SIDE_A, 0x200000000000017AL, "Port");
   public static final RelationTypeSide Port_To = Port_From.getOpposite();

   public static final RelationTypeSide Derive_From = RelationTypeSide.create(RelationSide.SIDE_A, 0x200000000000017BL, "Derive");
   public static final RelationTypeSide Derive_To = Derive_From.getOpposite();

   public static final RelationTypeSide FavoriteUser_Artifact = RelationTypeSide.create(RelationSide.SIDE_A, 0x2000000000000173L, "FavoriteUser");
   public static final RelationTypeSide FavoriteUser_User = FavoriteUser_Artifact.getOpposite();

   public static final RelationTypeSide Goal_Goal = RelationTypeSide.create(RelationSide.SIDE_A, 0x2000000000000175L, "Goal");
   public static final RelationTypeSide Goal_Member = Goal_Goal.getOpposite();

   public static final RelationTypeSide ParallelVersion_Parent = RelationTypeSide.create(RelationSide.SIDE_A, 0x2000000000000174L, "ParallelVersion");
   public static final RelationTypeSide ParallelVersion_Child = ParallelVersion_Parent.getOpposite();

   public static final RelationTypeSide PrivilegedMember_Team = RelationTypeSide.create(RelationSide.SIDE_A, 0x200000000000016BL, "PrivilegedMember");
   public static final RelationTypeSide PrivilegedMember_Member = PrivilegedMember_Team.getOpposite();

   public static final RelationTypeSide TeamWfToTask_TeamWf = RelationTypeSide.create(RelationSide.SIDE_A, 0x200000000000016EL, "TeamWfToTask");
   public static final RelationTypeSide TeamWfToTask_Task = TeamWfToTask_TeamWf.getOpposite();

   public static final RelationTypeSide SubscribedUser_Artifact = RelationTypeSide.create(RelationSide.SIDE_A, 0x2000000000000172L, "SubscribedUser");
   public static final RelationTypeSide SubscribedUser_User = SubscribedUser_Artifact.getOpposite();

   public static final RelationTypeSide TeamActionableItem_Team = RelationTypeSide.create(RelationSide.SIDE_A, 0x200000000000016CL, "TeamActionableItem");
   public static final RelationTypeSide TeamActionableItem_ActionableItem = TeamActionableItem_Team.getOpposite();

   public static final RelationTypeSide TeamDefinitionToVersion_TeamDefinition = RelationTypeSide.create(RelationSide.SIDE_A, 0x2000000000000170L, "TeamDefinitionToVersion");
   public static final RelationTypeSide TeamDefinitionToVersion_Version = TeamDefinitionToVersion_TeamDefinition.getOpposite();

   public static final RelationTypeSide TeamDefinitionToAtsConfigObject_TeamDefinition = RelationTypeSide.create(RelationSide.SIDE_A, 0x2000000000000170L, "TeamDefinitionAtsConfigObject");
   public static final RelationTypeSide TeamDefinitionToAtsConfigObject_AtsConfigObject = TeamDefinitionToVersion_TeamDefinition.getOpposite();

   public static final RelationTypeSide CountryToProgram_Country = RelationTypeSide.create(RelationSide.SIDE_A, 0x2000033300000169L, "CountryToProgram");
   public static final RelationTypeSide CountryToProgram_Program = CountryToProgram_Country.getOpposite();

   public static final RelationTypeSide TeamLead_Team = RelationTypeSide.create(RelationSide.SIDE_A, 0x2000000000000169L, "TeamLead");
   public static final RelationTypeSide TeamLead_Lead = TeamLead_Team.getOpposite();

   public static final RelationTypeSide TeamMember_Team = RelationTypeSide.create(RelationSide.SIDE_A, 0x200000000000016AL, "TeamMember");
   public static final RelationTypeSide TeamMember_Member = TeamMember_Team.getOpposite();

   public static final RelationTypeSide TeamWorkflowTargetedForVersion_Workflow = RelationTypeSide.create(RelationSide.SIDE_A, 0x200000000000016FL, "TeamWorkflowTargetedForVersion");
   public static final RelationTypeSide TeamWorkflowTargetedForVersion_Version = TeamWorkflowTargetedForVersion_Workflow.getOpposite();

   public static final RelationTypeSide TeamWorkflowToReview_Team = RelationTypeSide.create(RelationSide.SIDE_A, 0x2000000000000171L, "TeamWorkflowToReview");
   public static final RelationTypeSide TeamWorkflowToReview_Review = TeamWorkflowToReview_Team.getOpposite();

   public static final RelationTypeSide ActionableItemLead_AI = RelationTypeSide.create(RelationSide.SIDE_A, 0x2000000000000179L, "ActionableItemLead");
   public static final RelationTypeSide ActionableItemLead_Lead = ActionableItemLead_AI.getOpposite();

   public static final RelationTypeSide ActionableItem_Artifact = RelationTypeSide.create(RelationSide.SIDE_A, 0x2000000000000178L, "ActionableItem Owner");
   public static final RelationTypeSide ActionableItem_User = ActionableItem_Artifact.getOpposite();

   public static final RelationTypeSide AutoAddActionToGoal_Goal = RelationTypeSide.create(RelationSide.SIDE_A, 0x200000000000017DL, "AutoAddActionToGoal");
   public static final RelationTypeSide AutoAddActionToGoal_ConfigObject = AutoAddActionToGoal_Goal.getOpposite();

   public static final RelationTypeSide WorkPackage_WorkPackage = RelationTypeSide.create(RelationSide.SIDE_A, 0x200000000000017EL, "Work Package");
   public static final RelationTypeSide WorkPackage_TeamDefOrAi = WorkPackage_WorkPackage.getOpposite();
   //@formatter:on

   private AtsRelationTypes() {
      // Constants
   }
}
