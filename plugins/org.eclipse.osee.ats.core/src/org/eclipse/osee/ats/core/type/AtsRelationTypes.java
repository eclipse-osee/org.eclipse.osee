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
package org.eclipse.osee.ats.core.type;

import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author Donald G. Dunne
 */
public final class AtsRelationTypes {

   public static final IRelationTypeSide ActionToWorkflow_Action = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, "AAMFE953ixQThusHUPwA", "ActionToWorkflow");
   public static final IRelationTypeSide ActionToWorkflow_WorkFlow = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_B, "AAMFE953ixQThusHUPwA", "ActionToWorkflow");
   public static final IRelationTypeSide FavoriteUser_Artifact = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, "AAMFE+NegDLK1g2ph+AA", "FavoriteUser");
   public static final IRelationTypeSide FavoriteUser_User = TokenFactory.createRelationTypeSide(RelationSide.SIDE_B,
      "AAMFE+NegDLK1g2ph+AA", "FavoriteUser");
   public static final IRelationTypeSide Goal_Goal = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A,
      "ABMn0wPKdyN+Mfo5nwgA", "Goal");
   public static final IRelationTypeSide Goal_Member = TokenFactory.createRelationTypeSide(RelationSide.SIDE_B,
      "ABMn0wPKdyN+Mfo5nwgA", "Goal");
   public static final IRelationTypeSide ParallelVersion_Child = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_B, "AAMFE_EJHSBGb9msPXQA", "ParallelVersion");
   public static final IRelationTypeSide ParallelVersion_Parent = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, "AAMFE_EJHSBGb9msPXQA", "ParallelVersion");
   public static final IRelationTypeSide PrivilegedMember_Member = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_B, "AAMFE9XfiibyK1x2FiwA", "PrivilegedMember");
   public static final IRelationTypeSide PrivilegedMember_Team = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, "AAMFE9XfiibyK1x2FiwA", "PrivilegedMember");
   public static final IRelationTypeSide SmaToTask_Sma = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A,
      "AAMFE97xw1BM5l+GxKAA", "SmaToTask");
   public static final IRelationTypeSide SmaToTask_Task = TokenFactory.createRelationTypeSide(RelationSide.SIDE_B,
      "AAMFE97xw1BM5l+GxKAA", "SmaToTask");
   public static final IRelationTypeSide SubscribedUser_Artifact = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, "AAMFE+LkSAkfUWoTHdwA", "SubscribedUser");
   public static final IRelationTypeSide SubscribedUser_User = TokenFactory.createRelationTypeSide(RelationSide.SIDE_B,
      "AAMFE+LkSAkfUWoTHdwA", "SubscribedUser");
   public static final IRelationTypeSide TeamActionableItem_ActionableItem = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_B, "AAMFE939Ul9Oenq9wWgA", "TeamActionableItem");
   public static final IRelationTypeSide TeamActionableItem_Team = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, "AAMFE939Ul9Oenq9wWgA", "TeamActionableItem");
   public static final IRelationTypeSide TeamDefinitionToDecisionReviewWorkflowDiagram_TeamDefinition =
      TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, "AAMFE+Fg4RmKrda_jJQA",
         "TeamDefinitionToDecisionReviewWorkflowDiagram");
   public static final IRelationTypeSide TeamDefinitionToDecisionReviewWorkflowDiagram_WorkflowDiagram =
      TokenFactory.createRelationTypeSide(RelationSide.SIDE_B, "AAMFE+Fg4RmKrda_jJQA",
         "TeamDefinitionToDecisionReviewWorkflowDiagram");
   public static final IRelationTypeSide TeamDefinitionToPeerToPeerReviewWorkflowDiagram_TeamDefinition =
      TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, "AAMFE+HqYUG262IxMFwA",
         "TeamDefinitionToPeerToPeerReviewWorkflowDiagram");
   public static final IRelationTypeSide TeamDefinitionToPeerToPeerReviewWorkflowDiagram_WorkflowDiagram =
      TokenFactory.createRelationTypeSide(RelationSide.SIDE_B, "AAMFE+HqYUG262IxMFwA",
         "TeamDefinitionToPeerToPeerReviewWorkflowDiagram");
   public static final IRelationTypeSide TeamDefinitionToTaskWorkflowDiagram_TeamDefinition =
      TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, "AAMFE+DkeQ9mRBPca0QA",
         "TeamDefinitionToTaskWorkflowDiagram");
   public static final IRelationTypeSide TeamDefinitionToTaskWorkflowDiagram_WorkflowDiagram =
      TokenFactory.createRelationTypeSide(RelationSide.SIDE_B, "AAMFE+DkeQ9mRBPca0QA",
         "TeamDefinitionToTaskWorkflowDiagram");
   public static final IRelationTypeSide TeamDefinitionToVersion_TeamDefinition = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, "AAMFE9_i7zG3lR1kGWQA", "TeamDefinitionToVersion");
   public static final IRelationTypeSide TeamDefinitionToVersion_Version = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_B, "AAMFE9_i7zG3lR1kGWQA", "TeamDefinitionToVersion");
   public static final IRelationTypeSide TeamDefinitionToWorkflowDiagram_TeamDefinition =
      TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, "AAMFE+BpKTGewbN8c3gA",
         "TeamDefinitionToWorkflowDiagram");
   public static final IRelationTypeSide TeamDefinitionToWorkflowDiagram_WorkflowDiagram =
      TokenFactory.createRelationTypeSide(RelationSide.SIDE_B, "AAMFE+BpKTGewbN8c3gA",
         "TeamDefinitionToWorkflowDiagram");
   public static final IRelationTypeSide TeamLead_Lead = TokenFactory.createRelationTypeSide(RelationSide.SIDE_B,
      "AAMFE90HyTZPyHuQWOQA", "TeamLead");
   public static final IRelationTypeSide TeamLead_Team = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A,
      "AAMFE90HyTZPyHuQWOQA", "TeamLead");
   public static final IRelationTypeSide TeamMember_Member = TokenFactory.createRelationTypeSide(RelationSide.SIDE_B,
      "AAMFE92A6gCO9WJ2ijQA", "TeamMember");
   public static final IRelationTypeSide TeamMember_Team = TokenFactory.createRelationTypeSide(RelationSide.SIDE_A,
      "AAMFE92A6gCO9WJ2ijQA", "TeamMember");
   public static final IRelationTypeSide TeamWorkflowTargetedForVersion_Version = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_B, "AAMFE99pzm4zSibDT9gA", "TeamWorkflowTargetedForVersion");
   public static final IRelationTypeSide TeamWorkflowTargetedForVersion_Workflow = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, "AAMFE99pzm4zSibDT9gA", "TeamWorkflowTargetedForVersion");
   public static final IRelationTypeSide TeamWorkflowToReview_Review = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_B, "AAMFE+JqDz+8tuRDdIwA", "TeamWorkflowToReview");
   public static final IRelationTypeSide TeamWorkflowToReview_Team = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, "AAMFE+JqDz+8tuRDdIwA", "TeamWorkflowToReview");

   private AtsRelationTypes() {
      // Constants
   }
}