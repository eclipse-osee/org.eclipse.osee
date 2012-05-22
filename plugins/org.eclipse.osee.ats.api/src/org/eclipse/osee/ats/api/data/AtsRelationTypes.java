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

   public static final IRelationTypeSide ActionToWorkflow_Action = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, 0x200000000000016DL, "ActionToWorkflow");
   public static final IRelationTypeSide ActionToWorkflow_WorkFlow = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_B, 0x200000000000016DL, "ActionToWorkflow");
   public static final IRelationTypeSide FavoriteUser_Artifact = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, 0x2000000000000173L, "FavoriteUser");
   public static final IRelationTypeSide FavoriteUser_User = TokenFactory.createRelationTypeSide(RelationSide.SIDE_B,
      0x2000000000000173L, "FavoriteUser");
   public static final IRelationTypeSide Goal_Goal = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A,
      0x2000000000000175L, "Goal");
   public static final IRelationTypeSide Goal_Member = TokenFactory.createRelationTypeSide(RelationSide.SIDE_B,
      0x2000000000000175L, "Goal");
   public static final IRelationTypeSide ParallelVersion_Child = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_B, 0x2000000000000174L, "ParallelVersion");
   public static final IRelationTypeSide ParallelVersion_Parent = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, 0x2000000000000174L, "ParallelVersion");
   public static final IRelationTypeSide PrivilegedMember_Member = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_B, 0x200000000000016BL, "PrivilegedMember");
   public static final IRelationTypeSide PrivilegedMember_Team = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, 0x200000000000016BL, "PrivilegedMember");
   public static final IRelationTypeSide SmaToTask_Sma = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A,
      0x200000000000016EL, "SmaToTask");
   public static final IRelationTypeSide SmaToTask_Task = TokenFactory.createRelationTypeSide(RelationSide.SIDE_B,
      0x200000000000016EL, "SmaToTask");
   public static final IRelationTypeSide SubscribedUser_Artifact = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, 0x2000000000000172L, "SubscribedUser");
   public static final IRelationTypeSide SubscribedUser_User = TokenFactory.createRelationTypeSide(RelationSide.SIDE_B,
      0x2000000000000172L, "SubscribedUser");
   public static final IRelationTypeSide TeamActionableItem_ActionableItem = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_B, 0x200000000000016CL, "TeamActionableItem");
   public static final IRelationTypeSide TeamActionableItem_Team = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, 0x200000000000016CL, "TeamActionableItem");
   public static final IRelationTypeSide TeamDefinitionToVersion_TeamDefinition = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, 0x2000000000000170L, "TeamDefinitionToVersion");
   public static final IRelationTypeSide TeamDefinitionToVersion_Version = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_B, 0x2000000000000170L, "TeamDefinitionToVersion");
   public static final IRelationTypeSide TeamLead_Lead = TokenFactory.createRelationTypeSide(RelationSide.SIDE_B,
      0x2000000000000169L, "TeamLead");
   public static final IRelationTypeSide TeamLead_Team = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A,
      0x2000000000000169L, "TeamLead");
   public static final IRelationTypeSide TeamMember_Member = TokenFactory.createRelationTypeSide(RelationSide.SIDE_B,
      0x200000000000016AL, "TeamMember");
   public static final IRelationTypeSide TeamMember_Team = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A,
      0x200000000000016AL, "TeamMember");
   public static final IRelationTypeSide TeamWorkflowTargetedForVersion_Version = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_B, 0x200000000000016FL, "TeamWorkflowTargetedForVersion");
   public static final IRelationTypeSide TeamWorkflowTargetedForVersion_Workflow = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, 0x200000000000016FL, "TeamWorkflowTargetedForVersion");
   public static final IRelationTypeSide TeamWorkflowToReview_Review = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_B, 0x2000000000000171L, "TeamWorkflowToReview");
   public static final IRelationTypeSide TeamWorkflowToReview_Team = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, 0x2000000000000171L, "TeamWorkflowToReview");

   private AtsRelationTypes() {
      // Constants
   }
}