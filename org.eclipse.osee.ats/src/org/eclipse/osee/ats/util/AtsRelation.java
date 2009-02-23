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

import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

/**
 * @author Donald G. Dunne
 */
public enum AtsRelation implements IRelationEnumeration {

   // ATS Relations
   ActionToWorkflow_Action(true, "ActionToWorkflow"),
   ActionToWorkflow_WorkFlow(false, "ActionToWorkflow"),
   //
   TeamActionableItem_Team(true, "TeamActionableItem"),
   TeamActionableItem_ActionableItem(false, "TeamActionableItem"),
   //
   TeamLead_Team(true, "TeamLead"),
   TeamLead_Lead(false, "TeamLead"),
   //
   TeamMember_Team(true, "TeamMember"),
   TeamMember_Member(false, "TeamMember"),
   //
   PrivilegedMember_Team(true, "PrivilegedMember"),
   PrivilegedMember_Member(false, "PrivilegedMember"),
   //
   SmaToTask_Sma(true, "SmaToTask"),
   SmaToTask_Task(false, "SmaToTask"),
   //
   TeamWorkflowTargetedForVersion_Workflow(true, "TeamWorkflowTargetedForVersion"),
   TeamWorkflowTargetedForVersion_Version(false, "TeamWorkflowTargetedForVersion"),
   //
   TeamDefinitionToVersion_TeamDefinition(true, "TeamDefinitionToVersion"),
   TeamDefinitionToVersion_Version(false, "TeamDefinitionToVersion"),
   //
   TeamDefinitionToWorkflowDiagram_TeamDefinition(true, "TeamDefinitionToWorkflowDiagram"),
   TeamDefinitionToWorkflowDiagram_WorkflowDiagram(false, "TeamDefinitionToWorkflowDiagram"),
   //
   TeamDefinitionToTaskWorkflowDiagram_TeamDefinition(true, "TeamDefinitionToTaskWorkflowDiagram"),
   TeamDefinitionToTaskWorkflowDiagram_WorkflowDiagram(false, "TeamDefinitionToTaskWorkflowDiagram"),
   //
   TeamDefinitionToDecisionReviewWorkflowDiagram_TeamDefinition(true, "TeamDefinitionToDecisionReviewWorkflowDiagram"),
   TeamDefinitionToDecisionReviewWorkflowDiagram_WorkflowDiagram(false, "TeamDefinitionToDecisionReviewWorkflowDiagram"),
   //
   TeamDefinitionToPeerToPeerReviewWorkflowDiagram_TeamDefinition(true, "TeamDefinitionToPeerToPeerReviewWorkflowDiagram"),
   TeamDefinitionToPeerToPeerReviewWorkflowDiagram_WorkflowDiagram(false, "TeamDefinitionToPeerToPeerReviewWorkflowDiagram"),
   //
   TeamWorkflowToReview_Team(true, "TeamWorkflowToReview"),
   TeamWorkflowToReview_Review(false, "TeamWorkflowToReview"),
   //
   Supercedes_Supercedes(true, "Supercedes"),
   Supercedes_Superceded(false, "Supercedes"),
   AddressesIssues_AddressesIssues(true, "Addresses Issues"),
   AddressesIssues_IssuedArtifact(false, "Addresses Issues"),
   SupportingInfo_SupportedBy(true, "Supporting Info"),
   SupportingInfo_SupportingInfo(false, "Supporting Info"),
   //
   WorkItem__Parent(true, "Work Item"),
   WorkItem__Child(false, "Work Item"),
   //   
   Dependency__Dependency(false, "Dependency"),
   Dependency__Artifact(true, "Dependency"),
   SubscribedUser_Artifact(true, "SubscribedUser"),
   SubscribedUser_User(false, "SubscribedUser"),
   FavoriteUser_Artifact(true, "FavoriteUser"),
   FavoriteUser_User(false, "FavoriteUser");

   private final RelationSide relationSide;
   private String typeName;

   private AtsRelation(boolean sideA, String typeName) {
      this.relationSide = sideA ? RelationSide.SIDE_A : RelationSide.SIDE_B;
      this.typeName = typeName;
   }

   /**
    * @return Returns the sideName.
    */
   @Deprecated
   public boolean isSideA() {
      return relationSide.isSideA();
   }

   public String getSideName() throws OseeTypeDoesNotExist, OseeDataStoreException {
      return getRelationType().getSideName(relationSide);
   }

   /**
    * @return Returns the typeName.
    */
   public String getTypeName() {
      return typeName;
   }

   public RelationType getRelationType() throws OseeTypeDoesNotExist, OseeDataStoreException {
      return RelationTypeManager.getType(typeName);
   }

   public boolean isThisType(RelationLink link) {
      return link.getRelationType().getTypeName().equals(typeName);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration#getSide()
    */
   @Override
   public RelationSide getSide() {
      return relationSide;
   }
}
