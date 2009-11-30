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
package org.eclipse.osee.framework.core.enums;

import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;

public enum CoreRelationTypes implements IRelationEnumeration {
   Allocation__Requirement(SIDE_A, "Allocation", "AAMFE+RS8gRV2BXaCtQA"),
   Allocation__Component(SIDE_B, "Allocation", "AAMFE+RS8gRV2BXaCtQA"),
   CodeRequirement_Requirement(SIDE_B, "Code-Requirement", "AAMFE+TNKxG5ytcGHqgA"),
   CodeRequirement_CodeUnit(SIDE_A, "Code-Requirement", "AAMFE+TNKxG5ytcGHqgA"),
   Default_Hierarchical__Parent(SIDE_A, "Default Hierarchical", "AAMFE9skWhPMi6I1WdAA"),
   Default_Hierarchical__Child(SIDE_B, "Default Hierarchical", "AAMFE9skWhPMi6I1WdAA"),
   Dependency__Dependency(SIDE_B, "Dependency", "AAMFE9uek34jqArVRuAA"),
   Dependency__Artifact(SIDE_A, "Dependency", "AAMFE9uek34jqArVRuAA"),
   Design__Design(SIDE_B, "Design", "AAMFE+XjcSaqnXfOXVQA"),
   Design__Requirement(SIDE_A, "Design", "AAMFE+XjcSaqnXfOXVQA"),
   Executes__Test_Plan_Element(SIDE_A, "Executes", "AWo0+Hkh+2qNMeAlUwQA"),
   Executes__Test_Procedure(SIDE_B, "Executes", "AWo0+Hkh+2qNMeAlUwQA"),
   Requirement_Trace__Higher_Level(SIDE_A, "Requirement Trace", "AAMFE+dRBVFN6fAPXdwA"),
   Requirement_Trace__Lower_Level(SIDE_B, "Requirement Trace", "AAMFE+dRBVFN6fAPXdwA"),
   Supercedes_Supercedes(SIDE_A, "Supercedes", "AAMFE9qJZC_qegaByjgA"),
   Supercedes_Superceded(SIDE_B, "Supercedes", "AAMFE9qJZC_qegaByjgA"),
   SupportingInfo_SupportedBy(SIDE_A, "Supporting Info", "AAMFE9wWnVhFW0wtOPAA"),
   SupportingInfo_SupportingInfo(SIDE_B, "Supporting Info", "AAMFE9wWnVhFW0wtOPAA"),
   TeamMember_Team(SIDE_A, "TeamMember", "AAMFE92A6gCO9WJ2ijQA"),
   TeamMember_Member(SIDE_B, "TeamMember", "AAMFE92A6gCO9WJ2ijQA"),
   Test_Unit_Result__Test_Unit(SIDE_A, "Results Data", "AQm9ouX6JwBD29WrFoAA"),
   Test_Unit_Result__Test_Result(SIDE_B, "Results Data", "AQm9ouX6JwBD29WrFoAA"),
   Universal_Grouping__Members(SIDE_B, "Universal Grouping", "AAMFE9yOp2hIrXwZ13wA"),
   Universal_Grouping__Group(SIDE_A, "Universal Grouping", "AAMFE9yOp2hIrXwZ13wA"),
   Users_Artifact(SIDE_A, "Users", "AAMFE9nibXSCqcoXkMAA"),
   Users_User(SIDE_B, "Users", "AAMFE9nibXSCqcoXkMAA"),
   Uses__Requirement(SIDE_A, "Uses", "AAMFE_GV43rvba32QqwA"),
   Uses__TestUnit(SIDE_B, "Uses", "AAMFE_GV43rvba32QqwA"),
   Validation__Requirement(SIDE_A, "Validation", "AAMFE+fMVRM8f9BUD0AA"),
   Validation__Validator(SIDE_B, "Validation", "AAMFE+fMVRM8f9BUD0AA"),
   Verification__Requirement(SIDE_A, "Verification", "AAMFE+ZdqgB+D5zP5+gA"),
   Verification__Verifier(SIDE_B, "Verification", "AAMFE+ZdqgB+D5zP5+gA"),
   Verification_Plan__Requirement(SIDE_A, "Verification Plan", "ATmJsVc28wU57zFAi7AA"),
   Verification_Plan__Test_Plan_Element(SIDE_B, "Verification Plan", "ATmJsVc28wU57zFAi7AA"),
   WorkItem__Parent(SIDE_A, "Work Item", "AAMFE9jvEzcbzwAfjzwA"),
   WorkItem__Child(SIDE_B, "Work Item", "AAMFE9jvEzcbzwAfjzwA");

   private RelationSide relationSide;
   private String typeName;
   private String guid;

   private CoreRelationTypes(RelationSide side, String typeName, String guid) {
      this.relationSide = side;
      this.typeName = typeName;
      this.guid = guid;
   }

   @Deprecated
   public boolean isSideA() {
      return relationSide.isSideA();
   }

   public String getName() {
      return typeName;
   }

   @Override
   public RelationSide getSide() {
      return relationSide;
   }

   public String getGuid() {
      return guid;
   }
}
