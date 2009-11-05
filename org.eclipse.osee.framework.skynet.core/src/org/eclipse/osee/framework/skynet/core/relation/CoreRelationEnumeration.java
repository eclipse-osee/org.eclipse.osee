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

import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public enum CoreRelationEnumeration implements IRelationEnumeration {

   Users_Artifact(SIDE_A, "Users"), Users_User(SIDE_B, "Users"),

   // Define relations
   Supercedes_Supercedes(SIDE_A, "Supercedes"),
   Supercedes_Superceded(SIDE_B, "Supercedes"),
   AddressesIssues_AddressesIssues(SIDE_A, "Addresses Issues"),
   AddressesIssues_IssuedArtifact(SIDE_B, "Addresses Issues"),
   SupportingInfo_SupportedBy(SIDE_A, "Supporting Info"),
   SupportingInfo_SupportingInfo(SIDE_B, "Supporting Info"),

   DEFAULT_STYLESHEET__RENDERER(SIDE_A, "Default Stylesheet"),
   DEFAULT_STYLESHEET__STYLESHEET(SIDE_B, "Default Stylesheet"),
   REQUIREMENT_TRACE__HIGHER_LEVEL(SIDE_A, "Requirement Trace"),
   REQUIREMENT_TRACE__LOWER_LEVEL(SIDE_B, "Requirement Trace"),
   DEFAULT_HIERARCHICAL__CHILD(SIDE_B, "Default Hierarchical"),
   DEFAULT_HIERARCHICAL__PARENT(SIDE_A, "Default Hierarchical"),

   Dependency__Dependency(SIDE_B, "Dependency"),
   Dependency__Artifact(SIDE_A, "Dependency"),

   TeamMember_Team(SIDE_A, "TeamMember"),
   TeamMember_Member(SIDE_B, "TeamMember"),

   WorkItem__Parent(SIDE_A, "Work Item"),
   WorkItem__Child(SIDE_B, "Work Item"),

   Design__Design(SIDE_B, "Design"),
   Design__Requirement(SIDE_A, "Design"),

   UNIVERSAL_GROUPING__MEMBERS(SIDE_B, "Universal Grouping"),
   UNIVERSAL_GROUPING__GROUP(SIDE_A, "Universal Grouping"),

   ALLOCATION__REQUIREMENT(SIDE_A, "Allocation"),
   ALLOCATION__COMPONENT(SIDE_B, "Allocation"),
   Validation__Requirement(SIDE_A, "Validation"),
   Validation__Validator(SIDE_B, "Validation"),

   Verification__Requirement(SIDE_A, "Verification"),
   Verification__Verifier(SIDE_B, "Verification"),

   Uses__Requirement(SIDE_A, "Uses"),
   Uses__TestUnit(SIDE_B, "Uses"),

   CodeRequirement_Requirement(SIDE_B, "Code-Requirement"),
   CodeRequirement_CodeUnit(SIDE_A, "Code-Requirement"),

   TestConfigurationRelation_TestScript(SIDE_A, "Test Configuration Relation"),
   TestConfigurationRelation_TestConfiguration(SIDE_B, "Test Configuration Relation"),
   RunByRelation_User(SIDE_A, "Run By Relation"),
   RunByRelation_TestRun(SIDE_B, "Run By Relation"),
   TestRunConfigRelation_TestConfiguration(SIDE_A, "Test Run Config Relation"),
   TestRunConfigRelation_TestRun(SIDE_B, "Test Run Config Relation");

   private RelationSide relationSide;
   private String typeName;

   private CoreRelationEnumeration(RelationSide side, String typeName) {
      this.relationSide = side;
      this.typeName = typeName;
   }

   @Deprecated
   public boolean isSideA() {
      return relationSide.isSideA();
   }

   public String getSideName() throws OseeCoreException {
      return getRelationType().getSideName(relationSide);
   }

   public String getName() {
      return typeName;
   }

   public RelationType getRelationType() throws OseeCoreException {
      return RelationTypeManager.getType(typeName);
   }

   public boolean isThisType(RelationLink link) {
      return link.getRelationType().getName().equals(typeName);
   }

   @Override
   public RelationSide getSide() {
      return relationSide;
   }
}
