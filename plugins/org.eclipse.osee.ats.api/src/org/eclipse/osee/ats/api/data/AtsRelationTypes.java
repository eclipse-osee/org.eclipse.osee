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
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.TupleFamilyId;
import org.eclipse.osee.framework.core.data.TupleTypeId;
import org.eclipse.osee.framework.core.enums.CoreTupleFamilyTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author Donald G. Dunne
 */
public final class AtsRelationTypes {

   //@formatter:off

   // tuple relations
   public static final Tuple2Type<TupleFamilyId, TupleTypeId> WorkItem_To_WorkDefinition =  Tuple2Type.valueOf(CoreTupleFamilyTypes.AttribueFamily, 1457L);

   // relation links

   public static final RelationTypeSide ProgramToInsertion_Program = RelationTypeSide.create(RelationSide.SIDE_A, 8921796037933812267L, "Program To Insertion");
   public static final RelationTypeSide ProgramToInsertion_Insertion = ProgramToInsertion_Program.getOpposite();

   public static final RelationTypeSide InsertionToInsertionActivity_Insertion = RelationTypeSide.create(RelationSide.SIDE_A, 1336895299757203121L, "Insertion To Insertion Activity");
   public static final RelationTypeSide InsertionToInsertionActivity_InsertionActivity = InsertionToInsertionActivity_Insertion.getOpposite();

   public static final RelationTypeSide InsertionActivityToWorkPackage_InsertionActivity = RelationTypeSide.create(RelationSide.SIDE_A, 8892387571282380815L, "Insertion Activity To Work Package");
   public static final RelationTypeSide InsertionActivityToWorkPackage_WorkPackage = InsertionActivityToWorkPackage_InsertionActivity.getOpposite();

   public static final RelationTypeSide ActionToWorkflow_Action = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694317L, "ActionToWorkflow");
   public static final RelationTypeSide ActionToWorkflow_WorkFlow = ActionToWorkflow_Action.getOpposite();

   public static final RelationTypeSide AgileTeamToFeatureGroup_AgileTeam = RelationTypeSide.create(RelationSide.SIDE_A, 1067226929733341458L, "AgileTeamToFeatureGroup");
   public static final RelationTypeSide AgileTeamToFeatureGroup_FeatureGroup = AgileTeamToFeatureGroup_AgileTeam.getOpposite();

   public static final RelationTypeSide AgileTeamToAtsTeam_AgileTeam = RelationTypeSide.create(RelationSide.SIDE_A, 9001858956696556140L, "AgileTeamToAtsTeam");
   public static final RelationTypeSide AgileTeamToAtsTeam_AtsTeam = AgileTeamToAtsTeam_AgileTeam.getOpposite();

   public static final RelationTypeSide AgileTeamToAtsAtsAis_AgileTeam = RelationTypeSide.create(RelationSide.SIDE_A, 5336467317030669830L, "AgileTeamToAtsAtsAis");
   public static final RelationTypeSide AgileTeamToAtsAtsAis_AtsAis = AgileTeamToAtsAtsAis_AgileTeam.getOpposite();

   public static final RelationTypeSide AgileTeamToBacklog_AgileTeam = RelationTypeSide.create(RelationSide.SIDE_A, 8816366550731954418L, "AgileTeamToBacklog");
   public static final RelationTypeSide AgileTeamToBacklog_Backlog = AgileTeamToBacklog_AgileTeam.getOpposite();

   public static final RelationTypeSide AgileTeamToSprint_AgileTeam = RelationTypeSide.create(RelationSide.SIDE_A, 7043708594778812661L, "AgileTeamToSprint");
   public static final RelationTypeSide AgileTeamToSprint_Sprint = AgileTeamToSprint_AgileTeam.getOpposite();

   public static final RelationTypeSide AgileSprintToItem_Sprint= RelationTypeSide.create(RelationSide.SIDE_A, 988214123009313457L, "AgileSprintToItems");
   public static final RelationTypeSide AgileSprintToItem_AtsItem = AgileSprintToItem_Sprint.getOpposite();

   public static final RelationTypeSide AgileFeatureToItem_FeatureGroup = RelationTypeSide.create(RelationSide.SIDE_A, 6017077976601091441L, "AgileFeatureToItem");
   public static final RelationTypeSide AgileFeatureToItem_AtsItem = AgileFeatureToItem_FeatureGroup.getOpposite();

   public static final RelationTypeSide Port_From = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694330L, "Port");
   public static final RelationTypeSide Port_To = Port_From.getOpposite();

   public static final RelationTypeSide Derive_From = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694331L, "Derive");
   public static final RelationTypeSide Derive_To = Derive_From.getOpposite();

   public static final RelationTypeSide FavoriteUser_Artifact = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694323L, "FavoriteUser");
   public static final RelationTypeSide FavoriteUser_User = FavoriteUser_Artifact.getOpposite();

   public static final RelationTypeSide Goal_Goal = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694325L, "Goal");
   public static final RelationTypeSide Goal_Member = Goal_Goal.getOpposite();

   public static final RelationTypeSide ParallelVersion_Parent = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694324L, "ParallelVersion");
   public static final RelationTypeSide ParallelVersion_Child = ParallelVersion_Parent.getOpposite();

   public static final RelationTypeSide PrivilegedMember_Team = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694315L, "PrivilegedMember");
   public static final RelationTypeSide PrivilegedMember_Member = PrivilegedMember_Team.getOpposite();

   public static final RelationTypeSide TeamWfToTask_TeamWf = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694318L, "TeamWfToTask");
   public static final RelationTypeSide TeamWfToTask_Task = TeamWfToTask_TeamWf.getOpposite();

   public static final RelationTypeSide SubscribedUser_Artifact = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694322L, "SubscribedUser");
   public static final RelationTypeSide SubscribedUser_User = SubscribedUser_Artifact.getOpposite();

   public static final RelationTypeSide TeamActionableItem_Team = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694316L, "TeamActionableItem");
   public static final RelationTypeSide TeamActionableItem_ActionableItem = TeamActionableItem_Team.getOpposite();

   public static final RelationTypeSide TeamDefinitionToVersion_TeamDefinition = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694320L, "TeamDefinitionToVersion");
   public static final RelationTypeSide TeamDefinitionToVersion_Version = TeamDefinitionToVersion_TeamDefinition.getOpposite();

   public static final RelationTypeSide TeamDefinitionToAtsConfigObject_TeamDefinition = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694320L, "TeamDefinitionAtsConfigObject");
   public static final RelationTypeSide TeamDefinitionToAtsConfigObject_AtsConfigObject = TeamDefinitionToVersion_TeamDefinition.getOpposite();

   public static final RelationTypeSide CountryToProgram_Country = RelationTypeSide.create(RelationSide.SIDE_A, 2305846526791909737L, "CountryToProgram");
   public static final RelationTypeSide CountryToProgram_Program = CountryToProgram_Country.getOpposite();

   public static final RelationTypeSide TeamLead_Team = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694313L, "TeamLead");
   public static final RelationTypeSide TeamLead_Lead = TeamLead_Team.getOpposite();

   public static final RelationTypeSide TeamMember_Team = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694314L, "TeamMember");
   public static final RelationTypeSide TeamMember_Member = TeamMember_Team.getOpposite();

   public static final RelationTypeSide TeamWorkflowTargetedForVersion_Workflow = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694319L, "TeamWorkflowTargetedForVersion");
   public static final RelationTypeSide TeamWorkflowTargetedForVersion_Version = TeamWorkflowTargetedForVersion_Workflow.getOpposite();

   public static final RelationTypeSide TeamWorkflowToReview_Team = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694321L, "TeamWorkflowToReview");
   public static final RelationTypeSide TeamWorkflowToReview_Review = TeamWorkflowToReview_Team.getOpposite();

   public static final RelationTypeSide ActionableItemLead_AI = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694329L, "ActionableItemLead");
   public static final RelationTypeSide ActionableItemLead_Lead = ActionableItemLead_AI.getOpposite();

   public static final RelationTypeSide ActionableItem_Artifact = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694328L, "ActionableItem Owner");
   public static final RelationTypeSide ActionableItem_User = ActionableItem_Artifact.getOpposite();

   public static final RelationTypeSide AutoAddActionToGoal_Goal = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694333L, "AutoAddActionToGoal");
   public static final RelationTypeSide AutoAddActionToGoal_ConfigObject = AutoAddActionToGoal_Goal.getOpposite();

   public static final RelationTypeSide WorkPackage_WorkPackage = RelationTypeSide.create(RelationSide.SIDE_A, 2305843009213694334L, "Work Package");
   public static final RelationTypeSide WorkPackage_TeamDefOrAi = WorkPackage_WorkPackage.getOpposite();
   //@formatter:on

   private AtsRelationTypes() {
      // Constants
   }
}
