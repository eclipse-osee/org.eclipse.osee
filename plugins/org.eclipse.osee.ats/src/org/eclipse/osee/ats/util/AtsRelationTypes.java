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
package org.eclipse.osee.ats.util;

import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.enums.IRelationEnumeration;
import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author Donald G. Dunne
 */
public class AtsRelationTypes extends NamedIdentity implements IRelationEnumeration {
   private final RelationSide relationSide;

   public static final AtsRelationTypes ActionToWorkflow_Action = new AtsRelationTypes(RelationSide.SIDE_A,
      "AAMFE953ixQThusHUPwA", "ActionToWorkflow");
   public static final AtsRelationTypes ActionToWorkflow_WorkFlow = new AtsRelationTypes(RelationSide.SIDE_B,
      "AAMFE953ixQThusHUPwA", "ActionToWorkflow");
   public static final AtsRelationTypes FavoriteUser_Artifact = new AtsRelationTypes(RelationSide.SIDE_A,
      "AAMFE+NegDLK1g2ph+AA", "FavoriteUser");
   public static final AtsRelationTypes FavoriteUser_User = new AtsRelationTypes(RelationSide.SIDE_B,
      "AAMFE+NegDLK1g2ph+AA", "FavoriteUser");
   public static final AtsRelationTypes Goal_Goal = new AtsRelationTypes(RelationSide.SIDE_A, "ABMn0wPKdyN+Mfo5nwgA",
      "Goal");
   public static final AtsRelationTypes Goal_Member = new AtsRelationTypes(RelationSide.SIDE_B, "ABMn0wPKdyN+Mfo5nwgA",
      "Goal");
   public static final AtsRelationTypes ParallelVersion_Child = new AtsRelationTypes(RelationSide.SIDE_B,
      "AAMFE_EJHSBGb9msPXQA", "ParallelVersion");
   public static final AtsRelationTypes ParallelVersion_Parent = new AtsRelationTypes(RelationSide.SIDE_A,
      "AAMFE_EJHSBGb9msPXQA", "ParallelVersion");
   public static final AtsRelationTypes PrivilegedMember_Member = new AtsRelationTypes(RelationSide.SIDE_B,
      "AAMFE9XfiibyK1x2FiwA", "PrivilegedMember");
   public static final AtsRelationTypes PrivilegedMember_Team = new AtsRelationTypes(RelationSide.SIDE_A,
      "AAMFE9XfiibyK1x2FiwA", "PrivilegedMember");
   public static final AtsRelationTypes SmaToTask_Sma = new AtsRelationTypes(RelationSide.SIDE_A,
      "AAMFE97xw1BM5l+GxKAA", "SmaToTask");
   public static final AtsRelationTypes SmaToTask_Task = new AtsRelationTypes(RelationSide.SIDE_B,
      "AAMFE97xw1BM5l+GxKAA", "SmaToTask");
   public static final AtsRelationTypes SubscribedUser_Artifact = new AtsRelationTypes(RelationSide.SIDE_A,
      "AAMFE+LkSAkfUWoTHdwA", "SubscribedUser");
   public static final AtsRelationTypes SubscribedUser_User = new AtsRelationTypes(RelationSide.SIDE_B,
      "AAMFE+LkSAkfUWoTHdwA", "SubscribedUser");
   public static final AtsRelationTypes TeamActionableItem_ActionableItem = new AtsRelationTypes(RelationSide.SIDE_B,
      "AAMFE939Ul9Oenq9wWgA", "TeamActionableItem");
   public static final AtsRelationTypes TeamActionableItem_Team = new AtsRelationTypes(RelationSide.SIDE_A,
      "AAMFE939Ul9Oenq9wWgA", "TeamActionableItem");
   public static final AtsRelationTypes TeamDefinitionToDecisionReviewWorkflowDiagram_TeamDefinition =
      new AtsRelationTypes(RelationSide.SIDE_A, "AAMFE+Fg4RmKrda_jJQA", "TeamDefinitionToDecisionReviewWorkflowDiagram");
   public static final AtsRelationTypes TeamDefinitionToDecisionReviewWorkflowDiagram_WorkflowDiagram =
      new AtsRelationTypes(RelationSide.SIDE_B, "AAMFE+Fg4RmKrda_jJQA", "TeamDefinitionToDecisionReviewWorkflowDiagram");
   public static final AtsRelationTypes TeamDefinitionToPeerToPeerReviewWorkflowDiagram_TeamDefinition =
      new AtsRelationTypes(RelationSide.SIDE_A, "AAMFE+HqYUG262IxMFwA",
         "TeamDefinitionToPeerToPeerReviewWorkflowDiagram");
   public static final AtsRelationTypes TeamDefinitionToPeerToPeerReviewWorkflowDiagram_WorkflowDiagram =
      new AtsRelationTypes(RelationSide.SIDE_B, "AAMFE+HqYUG262IxMFwA",
         "TeamDefinitionToPeerToPeerReviewWorkflowDiagram");
   public static final AtsRelationTypes TeamDefinitionToTaskWorkflowDiagram_TeamDefinition = new AtsRelationTypes(
      RelationSide.SIDE_A, "AAMFE+DkeQ9mRBPca0QA", "TeamDefinitionToTaskWorkflowDiagram");
   public static final AtsRelationTypes TeamDefinitionToTaskWorkflowDiagram_WorkflowDiagram = new AtsRelationTypes(
      RelationSide.SIDE_B, "AAMFE+DkeQ9mRBPca0QA", "TeamDefinitionToTaskWorkflowDiagram");
   public static final AtsRelationTypes TeamDefinitionToVersion_TeamDefinition = new AtsRelationTypes(
      RelationSide.SIDE_A, "AAMFE9_i7zG3lR1kGWQA", "TeamDefinitionToVersion");
   public static final AtsRelationTypes TeamDefinitionToVersion_Version = new AtsRelationTypes(RelationSide.SIDE_B,
      "AAMFE9_i7zG3lR1kGWQA", "TeamDefinitionToVersion");
   public static final AtsRelationTypes TeamDefinitionToWorkflowDiagram_TeamDefinition = new AtsRelationTypes(
      RelationSide.SIDE_A, "AAMFE+BpKTGewbN8c3gA", "TeamDefinitionToWorkflowDiagram");
   public static final AtsRelationTypes TeamDefinitionToWorkflowDiagram_WorkflowDiagram = new AtsRelationTypes(
      RelationSide.SIDE_B, "AAMFE+BpKTGewbN8c3gA", "TeamDefinitionToWorkflowDiagram");
   public static final AtsRelationTypes TeamLead_Lead = new AtsRelationTypes(RelationSide.SIDE_B,
      "AAMFE90HyTZPyHuQWOQA", "TeamLead");
   public static final AtsRelationTypes TeamLead_Team = new AtsRelationTypes(RelationSide.SIDE_A,
      "AAMFE90HyTZPyHuQWOQA", "TeamLead");
   public static final AtsRelationTypes TeamMember_Member = new AtsRelationTypes(RelationSide.SIDE_B,
      "AAMFE92A6gCO9WJ2ijQA", "TeamMember");
   public static final AtsRelationTypes TeamMember_Team = new AtsRelationTypes(RelationSide.SIDE_A,
      "AAMFE92A6gCO9WJ2ijQA", "TeamMember");
   public static final AtsRelationTypes TeamWorkflowTargetedForVersion_Version = new AtsRelationTypes(
      RelationSide.SIDE_B, "AAMFE99pzm4zSibDT9gA", "TeamWorkflowTargetedForVersion");
   public static final AtsRelationTypes TeamWorkflowTargetedForVersion_Workflow = new AtsRelationTypes(
      RelationSide.SIDE_A, "AAMFE99pzm4zSibDT9gA", "TeamWorkflowTargetedForVersion");
   public static final AtsRelationTypes TeamWorkflowToReview_Review = new AtsRelationTypes(RelationSide.SIDE_B,
      "AAMFE+JqDz+8tuRDdIwA", "TeamWorkflowToReview");
   public static final AtsRelationTypes TeamWorkflowToReview_Team = new AtsRelationTypes(RelationSide.SIDE_A,
      "AAMFE+JqDz+8tuRDdIwA", "TeamWorkflowToReview");

   private AtsRelationTypes(RelationSide relationSide, String guid, String name) {
      super(guid, name);
      this.relationSide = relationSide;
   }

   @Override
   public RelationSide getSide() {
      return relationSide;
   }

   @Override
   public boolean equals(Object obj) {
      return super.equals(obj);
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

}