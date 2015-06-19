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

import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author Donald G. Dunne
 */
public final class AtsRelationTypes {

   //@formatter:off

   public static final IRelationTypeSide ProgramToInsertion_Program = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x7BD0963A0F884A2BL, "Program To Insertion");
   public static final IRelationTypeSide ProgramToInsertion_Insertion = ProgramToInsertion_Program.getOpposite();

   public static final IRelationTypeSide InsertionToInsertionActivity_Insertion = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x128D9B3123EADEB1L, "Insertion To Insertion Activity");
   public static final IRelationTypeSide InsertionToInsertionActivity_InsertionActivity = InsertionToInsertionActivity_Insertion.getOpposite();

   public static final IRelationTypeSide InsertionActivityToWorkPackage_InsertionActivity = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x7B681B61D762880FL, "Insertion Activity To Work Package");
   public static final IRelationTypeSide InsertionActivityToWorkPackage_WorkPackage = InsertionActivityToWorkPackage_InsertionActivity.getOpposite();

   public static final IRelationTypeSide ActionToWorkflow_Action = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x200000000000016DL, "ActionToWorkflow");
   public static final IRelationTypeSide ActionToWorkflow_WorkFlow = ActionToWorkflow_Action.getOpposite();

   public static final IRelationTypeSide AgileTeamToFeatureGroup_AgileTeam = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x0ECF8D3CF97C9112L, "AgileTeamToFeatureGroup");
   public static final IRelationTypeSide AgileTeamToFeatureGroup_FeatureGroup = AgileTeamToFeatureGroup_AgileTeam.getOpposite();

   public static final IRelationTypeSide AgileTeamToAtsTeam_AgileTeam = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x7CED0706F811126CL, "AgileTeamToAtsTeam");
   public static final IRelationTypeSide AgileTeamToAtsTeam_AtsTeam = AgileTeamToAtsTeam_AgileTeam.getOpposite();

   public static final IRelationTypeSide AgileTeamToBacklog_AgileTeam = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x7A5A06AAB20398F2L, "AgileTeamToBacklog");
   public static final IRelationTypeSide AgileTeamToBacklog_Backlog = AgileTeamToBacklog_AgileTeam.getOpposite();

   public static final IRelationTypeSide AgileTeamToSprint_AgileTeam = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x61C047A5D52830F5L, "AgileTeamToSprint");
   public static final IRelationTypeSide AgileTeamToSprint_Sprint = AgileTeamToSprint_AgileTeam.getOpposite();

   public static final IRelationTypeSide AgileSprintToItem_Sprint= TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x0DB6D78253FE8AB1L, "AgileSprintToItems");
   public static final IRelationTypeSide AgileSprintToItem_AtsItem = AgileSprintToItem_Sprint.getOpposite();

   public static final IRelationTypeSide AgileFeatureToItem_FeatureGroup = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x5380F48A35225D71L, "AgileFeatureToItem");
   public static final IRelationTypeSide AgileFeatureToItem_AtsItem = AgileFeatureToItem_FeatureGroup.getOpposite();

   public static final IRelationTypeSide Port_From = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x200000000000017AL, "Port");
   public static final IRelationTypeSide Port_To = Port_From.getOpposite();

   public static final IRelationTypeSide Derive_From = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x200000000000017BL, "Derive");
   public static final IRelationTypeSide Derive_To = Derive_From.getOpposite();

   public static final IRelationTypeSide FavoriteUser_Artifact = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x2000000000000173L, "FavoriteUser");
   public static final IRelationTypeSide FavoriteUser_User = FavoriteUser_Artifact.getOpposite();

   public static final IRelationTypeSide Goal_Goal = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x2000000000000175L, "Goal");
   public static final IRelationTypeSide Goal_Member = Goal_Goal.getOpposite();

   public static final IRelationTypeSide ParallelVersion_Parent = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x2000000000000174L, "ParallelVersion");
   public static final IRelationTypeSide ParallelVersion_Child = ParallelVersion_Parent.getOpposite();

   public static final IRelationTypeSide PrivilegedMember_Team = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x200000000000016BL, "PrivilegedMember");
   public static final IRelationTypeSide PrivilegedMember_Member = PrivilegedMember_Team.getOpposite();

   public static final IRelationTypeSide TeamWfToTask_TeamWf = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x200000000000016EL, "TeamWfToTask");
   public static final IRelationTypeSide TeamWfToTask_Task = TeamWfToTask_TeamWf.getOpposite();

   public static final IRelationTypeSide SubscribedUser_Artifact = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x2000000000000172L, "SubscribedUser");
   public static final IRelationTypeSide SubscribedUser_User = SubscribedUser_Artifact.getOpposite();

   public static final IRelationTypeSide TeamActionableItem_Team = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x200000000000016CL, "TeamActionableItem");
   public static final IRelationTypeSide TeamActionableItem_ActionableItem = TeamActionableItem_Team.getOpposite();

   public static final IRelationTypeSide TeamDefinitionToVersion_TeamDefinition = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x2000000000000170L, "TeamDefinitionToVersion");
   public static final IRelationTypeSide TeamDefinitionToVersion_Version = TeamDefinitionToVersion_TeamDefinition.getOpposite();

   public static final IRelationTypeSide TeamDefinitionToAtsConfigObject_TeamDefinition = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x2000000000000170L, "TeamDefinitionAtsConfigObject");
   public static final IRelationTypeSide TeamDefinitionToAtsConfigObject_AtsConfigObject = TeamDefinitionToVersion_TeamDefinition.getOpposite();

   public static final IRelationTypeSide CountryToProgram_Country = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x2000033300000169L, "CountryToProgram");
   public static final IRelationTypeSide CountryToProgram_Program = CountryToProgram_Country.getOpposite();

   public static final IRelationTypeSide TeamLead_Team = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x2000000000000169L, "TeamLead");
   public static final IRelationTypeSide TeamLead_Lead = TeamLead_Team.getOpposite();

   public static final IRelationTypeSide TeamMember_Team = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x200000000000016AL, "TeamMember");
   public static final IRelationTypeSide TeamMember_Member = TeamMember_Team.getOpposite();

   public static final IRelationTypeSide TeamWorkflowTargetedForVersion_Workflow = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x200000000000016FL, "TeamWorkflowTargetedForVersion");
   public static final IRelationTypeSide TeamWorkflowTargetedForVersion_Version = TeamWorkflowTargetedForVersion_Workflow.getOpposite();

   public static final IRelationTypeSide TeamWorkflowToReview_Team = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x2000000000000171L, "TeamWorkflowToReview");
   public static final IRelationTypeSide TeamWorkflowToReview_Review = TeamWorkflowToReview_Team.getOpposite();

   public static final IRelationTypeSide ActionableItemLead_AI = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x2000000000000179L, "ActionableItemLead");
   public static final IRelationTypeSide ActionableItemLead_Lead = ActionableItemLead_AI.getOpposite();

   public static final IRelationTypeSide ActionableItem_Artifact = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x2000000000000178L, "ActionableItem Owner");
   public static final IRelationTypeSide ActionableItem_User = ActionableItem_Artifact.getOpposite();

   public static final IRelationTypeSide AutoAddActionToGoal_Goal = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x200000000000017DL, "AutoAddActionToGoal");
   public static final IRelationTypeSide AutoAddActionToGoal_ConfigObject = AutoAddActionToGoal_Goal.getOpposite();

   public static final IRelationTypeSide WorkPackage_WorkPackage = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x200000000000017EL, "Work Package");
   public static final IRelationTypeSide WorkPackage_TeamDefOrAi = WorkPackage_WorkPackage.getOpposite();
   //@formatter:on

   private AtsRelationTypes() {
      // Constants
   }
}
