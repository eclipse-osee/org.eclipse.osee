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
package org.eclipse.osee.framework.skynet.core.relation;

import org.eclipse.osee.framework.skynet.core.artifact.Branch;

public enum RelationSide implements IRelationEnumeration {

   x(false, null), y(false, null),

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
   Users_Artifact(true, "Users"),
   Users_User(false, "Users"),

   //
   SubscribedUser_Artifact(true, "SubscribedUser"),
   SubscribedUser_User(false, "SubscribedUser"),
   FavoriteUser_Artifact(true, "FavoriteUser"),
   FavoriteUser_User(false, "FavoriteUser"),

   // Define relations
   Supercedes_Supercedes(true, "Supercedes"),
   Supercedes_Superceded(false, "Supercedes"),
   AddressesIssues_AddressesIssues(true, "Addresses Issues"),
   AddressesIssues_IssuedArtifact(false, "Addresses Issues"),
   SupportingInfo_SupportedBy(true, "Supporting Info"),
   SupportingInfo_SupportingInfo(false, "Supporting Info"),

   DEFAULT_STYLESHEET__RENDERER(true, "Default Stylesheet"),
   DEFAULT_STYLESHEET__STYLESHEET(false, "Default Stylesheet"),
   REQUIREMENT_TRACE__HIGHER_LEVEL(true, "Requirement Trace"),
   REQUIREMENT_TRACE__LOWER_LEVEL(false, "Requirement Trace"),
   DEFAULT_HIERARCHICAL__CHILD(false, "Default Hierarchical"),
   DEFAULT_HIERARCHICAL__PARENT(true, "Default Hierarchical"),

   UNIVERSAL_GROUPING__MEMBERS(false, "Universal Grouping"),
   UNIVERSAL_GROUPING__GROUP(true, "Universal Grouping"),

   ALLOCATION__REQUIREMENT(true, "Allocation"),
   ALLOCATION__COMPONENT(false, "Allocation"),
   TestConfigurationRelation_TestScript(true, "Test Configuration Relation"),
   TestConfigurationRelation_TestConfiguration(false, "Test Configuration Relation"),
   RunByRelation_User(true, "Run By Relation"),
   RunByRelation_TestRun(false, "Run By Relation"),
   TestRunConfigRelation_TestConfiguration(true, "Test Run Config Relation"),
   TestRunConfigRelation_TestRun(false, "Test Run Config Relation");

   private boolean sideA;
   private String typeName;
   private static final RelationPersistenceManager relationManager = RelationPersistenceManager.getInstance();

   private RelationSide(boolean sideA, String typeName) {
      this.sideA = sideA;
      this.typeName = typeName;
      RelationPersistenceManager.sideHash.put(typeName, sideA, this);
   }

   public static IRelationEnumeration getRelationSide(String relationType, String relationSide, Branch branch) {
      IRelationLinkDescriptor desc = relationManager.getIRelationLinkDescriptor(relationType, branch);
      boolean isSideA = (desc.getSideAName().equals(relationSide));
      return RelationPersistenceManager.sideHash.get(relationType, isSideA);
   }

   /**
    * @return Returns the sideName.
    */
   public boolean isSideA() {
      return sideA;
   }

   public String getSideName(Branch branch) {
      if (isSideA())
         return getDescriptor(branch).getSideAName();
      else
         return getDescriptor(branch).getSideBName();
   }

   /**
    * @return Returns the typeName.
    */
   public String getTypeName() {
      return typeName;
   }

   public IRelationLinkDescriptor getDescriptor(Branch branch) {
      return relationManager.getIRelationLinkDescriptor(typeName, branch);
   }

   public boolean isThisType(IRelationLink link) {
      return link.getLinkDescriptor().getName().equals(typeName);
   }
}
