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

import org.eclipse.osee.framework.core.enums.IRelationEnumeration;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

/**
 * @author Donald G. Dunne
 */
public enum AtsRelationTypes implements IRelationEnumeration {
   ActionToWorkflow_Action(true, "ActionToWorkflow", "AAMFE953ixQThusHUPwA"),
   ActionToWorkflow_WorkFlow(false, "ActionToWorkflow", "AAMFE953ixQThusHUPwA"),
      //
   TeamActionableItem_Team(true, "TeamActionableItem", "AAMFE939Ul9Oenq9wWgA"),
   TeamActionableItem_ActionableItem(false, "TeamActionableItem", "AAMFE939Ul9Oenq9wWgA"),
      //
   TeamLead_Team(true, "TeamLead", "AAMFE90HyTZPyHuQWOQA"),
   TeamLead_Lead(false, "TeamLead", "AAMFE90HyTZPyHuQWOQA"),
      //
   TeamMember_Team(true, "TeamMember", "AAMFE92A6gCO9WJ2ijQA"),
   TeamMember_Member(false, "TeamMember", "AAMFE92A6gCO9WJ2ijQA"),
      //
   Goal_Member(false, "Goal", "ABMn0wPKdyN+Mfo5nwgA"),
   Goal_Goal(true, "Goal", "ABMn0wPKdyN+Mfo5nwgA"),
      //
   ParallelVersion_Parent(true, "ParallelVersion", "AAMFE_EJHSBGb9msPXQA"),
   ParallelVersion_Child(false, "ParallelVersion", "AAMFE_EJHSBGb9msPXQA"),
      //
   PrivilegedMember_Team(true, "PrivilegedMember", "AAMFE9XfiibyK1x2FiwA"),
   PrivilegedMember_Member(false, "PrivilegedMember", "AAMFE9XfiibyK1x2FiwA"),
      //
   SmaToTask_Sma(true, "SmaToTask", "AAMFE97xw1BM5l+GxKAA"),
   SmaToTask_Task(false, "SmaToTask", "AAMFE97xw1BM5l+GxKAA"),
      //
   TeamWorkflowTargetedForVersion_Workflow(true, "TeamWorkflowTargetedForVersion", "AAMFE99pzm4zSibDT9gA"),
   TeamWorkflowTargetedForVersion_Version(false, "TeamWorkflowTargetedForVersion", "AAMFE99pzm4zSibDT9gA"),
      //
   TeamDefinitionToVersion_TeamDefinition(true, "TeamDefinitionToVersion", "AAMFE9_i7zG3lR1kGWQA"),
   TeamDefinitionToVersion_Version(false, "TeamDefinitionToVersion", "AAMFE9_i7zG3lR1kGWQA"),
      //
   TeamDefinitionToWorkflowDiagram_TeamDefinition(true, "TeamDefinitionToWorkflowDiagram", "AAMFE+BpKTGewbN8c3gA"),
   TeamDefinitionToWorkflowDiagram_WorkflowDiagram(false, "TeamDefinitionToWorkflowDiagram", "AAMFE+BpKTGewbN8c3gA"),
      //
   TeamDefinitionToTaskWorkflowDiagram_TeamDefinition(true, "TeamDefinitionToTaskWorkflowDiagram", "AAMFE+DkeQ9mRBPca0QA"),
   TeamDefinitionToTaskWorkflowDiagram_WorkflowDiagram(false, "TeamDefinitionToTaskWorkflowDiagram", "AAMFE+DkeQ9mRBPca0QA"),
      //
   TeamDefinitionToDecisionReviewWorkflowDiagram_TeamDefinition(true, "TeamDefinitionToDecisionReviewWorkflowDiagram", "AAMFE+Fg4RmKrda_jJQA"),
   TeamDefinitionToDecisionReviewWorkflowDiagram_WorkflowDiagram(false, "TeamDefinitionToDecisionReviewWorkflowDiagram", "AAMFE+Fg4RmKrda_jJQA"),
      //
   TeamDefinitionToPeerToPeerReviewWorkflowDiagram_TeamDefinition(true,
         "TeamDefinitionToPeerToPeerReviewWorkflowDiagram", "AAMFE+HqYUG262IxMFwA"),
   TeamDefinitionToPeerToPeerReviewWorkflowDiagram_WorkflowDiagram(false,
         "TeamDefinitionToPeerToPeerReviewWorkflowDiagram", "AAMFE+HqYUG262IxMFwA"),
      //
   TeamWorkflowToReview_Team(true, "TeamWorkflowToReview", "AAMFE+JqDz+8tuRDdIwA"),
   TeamWorkflowToReview_Review(false, "TeamWorkflowToReview", "AAMFE+JqDz+8tuRDdIwA"),
      //
   SubscribedUser_Artifact(true, "SubscribedUser", "AAMFE+LkSAkfUWoTHdwA"),
   SubscribedUser_User(false, "SubscribedUser", "AAMFE+LkSAkfUWoTHdwA"),
   FavoriteUser_Artifact(true, "FavoriteUser", "AAMFE+NegDLK1g2ph+AA"),
   FavoriteUser_User(false, "FavoriteUser", "AAMFE+NegDLK1g2ph+AA");

   private final RelationSide relationSide;
   private final String typeName;
   private final String guid;

   private AtsRelationTypes(boolean sideA, String typeName, String guid) {
      this.relationSide = sideA ? RelationSide.SIDE_A : RelationSide.SIDE_B;
      this.typeName = typeName;
      this.guid = guid;
   }

   /**
    * @return Returns the sideName.
    */
   @Deprecated
   public boolean isSideA() {
      return relationSide.isSideA();
   }

   /**
    * @return Returns the typeName.
    */
   public String getName() {
      return typeName;
   }

   public RelationType getRelationType() throws OseeCoreException {
      return RelationTypeManager.getType(typeName);
   }

   @Override
   public RelationSide getSide() {
      return relationSide;
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.core.data.IOseeType#getGuid()
    */
   @Override
   public String getGuid() {
      return guid;
   }
}
