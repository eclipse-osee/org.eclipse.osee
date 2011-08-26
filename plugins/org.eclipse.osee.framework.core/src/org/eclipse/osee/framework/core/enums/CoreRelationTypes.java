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
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.TokenFactory;

public final class CoreRelationTypes {

   public static final IRelationTypeSide Allocation__Requirement = TokenFactory.createRelationTypeSide(SIDE_A,
      "AAMFE+RS8gRV2BXaCtQA", "Allocation");
   public static final IRelationTypeSide Allocation__Component = TokenFactory.createRelationTypeSide(SIDE_B,
      "AAMFE+RS8gRV2BXaCtQA", "Allocation");
   public static final IRelationTypeSide CodeRequirement_Requirement = TokenFactory.createRelationTypeSide(SIDE_B,
      "AAMFE+TNKxG5ytcGHqgA", "Code-Requirement");
   public static final IRelationTypeSide CodeRequirement_CodeUnit = TokenFactory.createRelationTypeSide(SIDE_A,
      "AAMFE+TNKxG5ytcGHqgA", "Code-Requirement");
   public static final IRelationTypeSide Default_Hierarchical__Parent = TokenFactory.createRelationTypeSide(SIDE_A,
      "AAMFE9skWhPMi6I1WdAA", "Default Hierarchical");
   public static final IRelationTypeSide Default_Hierarchical__Child = TokenFactory.createRelationTypeSide(SIDE_B,
      "AAMFE9skWhPMi6I1WdAA", "Default Hierarchical");
   public static final IRelationTypeSide Dependency__Dependency = TokenFactory.createRelationTypeSide(SIDE_B,
      "AAMFE9uek34jqArVRuAA", "Dependency");
   public static final IRelationTypeSide Dependency__Artifact = TokenFactory.createRelationTypeSide(SIDE_A,
      "AAMFE9uek34jqArVRuAA", "Dependency");
   public static final IRelationTypeSide Design__Design = TokenFactory.createRelationTypeSide(SIDE_B,
      "AAMFE+XjcSaqnXfOXVQA", "Design");
   public static final IRelationTypeSide Design__Requirement = TokenFactory.createRelationTypeSide(SIDE_A,
      "AAMFE+XjcSaqnXfOXVQA", "Design");
   public static final IRelationTypeSide Executes__Test_Plan_Element = TokenFactory.createRelationTypeSide(SIDE_A,
      "AWo0+Hkh+2qNMeAlUwQA", "Executes");
   public static final IRelationTypeSide Executes__Test_Procedure = TokenFactory.createRelationTypeSide(SIDE_B,
      "AWo0+Hkh+2qNMeAlUwQA", "Executes");
   public static final IRelationTypeSide Requirement_Trace__Higher_Level = TokenFactory.createRelationTypeSide(SIDE_A,
      "AAMFE+dRBVFN6fAPXdwA", "Requirement Trace");
   public static final IRelationTypeSide Requirement_Trace__Lower_Level = TokenFactory.createRelationTypeSide(SIDE_B,
      "AAMFE+dRBVFN6fAPXdwA", "Requirement Trace");
   public static final IRelationTypeSide Supercedes_Supercedes = TokenFactory.createRelationTypeSide(SIDE_A,
      "AAMFE9qJZC_qegaByjgA", "Supercedes");
   public static final IRelationTypeSide Supercedes_Superceded = TokenFactory.createRelationTypeSide(SIDE_B,
      "AAMFE9qJZC_qegaByjgA", "Supercedes");
   public static final IRelationTypeSide SupportingInfo_SupportedBy = TokenFactory.createRelationTypeSide(SIDE_A,
      "AAMFE9wWnVhFW0wtOPAA", "Supporting Info");
   public static final IRelationTypeSide SupportingInfo_SupportingInfo = TokenFactory.createRelationTypeSide(SIDE_B,
      "AAMFE9wWnVhFW0wtOPAA", "Supporting Info");
   public static final IRelationTypeSide TeamMember_Team = TokenFactory.createRelationTypeSide(SIDE_A,
      "AAMFE92A6gCO9WJ2ijQA", "TeamMember");
   public static final IRelationTypeSide TeamMember_Member = TokenFactory.createRelationTypeSide(SIDE_B,
      "AAMFE92A6gCO9WJ2ijQA", "TeamMember");
   public static final IRelationTypeSide Test_Unit_Result__Test_Unit = TokenFactory.createRelationTypeSide(SIDE_A,
      "AQm9ouX6JwBD29WrFoAA", "Results Data");
   public static final IRelationTypeSide Test_Unit_Result__Test_Result = TokenFactory.createRelationTypeSide(SIDE_B,
      "AQm9ouX6JwBD29WrFoAA", "Results Data");
   public static final IRelationTypeSide Universal_Grouping__Members = TokenFactory.createRelationTypeSide(SIDE_B,
      "AAMFE9yOp2hIrXwZ13wA", "Universal Grouping");
   public static final IRelationTypeSide Universal_Grouping__Group = TokenFactory.createRelationTypeSide(SIDE_A,
      "AAMFE9yOp2hIrXwZ13wA", "Universal Grouping");
   public static final IRelationTypeSide Users_Artifact = TokenFactory.createRelationTypeSide(SIDE_A,
      "AAMFE9nibXSCqcoXkMAA", "Users");
   public static final IRelationTypeSide Users_User = TokenFactory.createRelationTypeSide(SIDE_B,
      "AAMFE9nibXSCqcoXkMAA", "Users");
   public static final IRelationTypeSide Uses__Requirement = TokenFactory.createRelationTypeSide(SIDE_A,
      "AAMFE_GV43rvba32QqwA", "Uses");
   public static final IRelationTypeSide Uses__TestUnit = TokenFactory.createRelationTypeSide(SIDE_B,
      "AAMFE_GV43rvba32QqwA", "Uses");
   public static final IRelationTypeSide Validation__Requirement = TokenFactory.createRelationTypeSide(SIDE_A,
      "AAMFE+fMVRM8f9BUD0AA", "Validation");
   public static final IRelationTypeSide Validation__Validator = TokenFactory.createRelationTypeSide(SIDE_B,
      "AAMFE+fMVRM8f9BUD0AA", "Validation");
   public static final IRelationTypeSide Verification__Requirement = TokenFactory.createRelationTypeSide(SIDE_A,
      "AAMFE+ZdqgB+D5zP5+gA", "Verification");
   public static final IRelationTypeSide Verification__Verifier = TokenFactory.createRelationTypeSide(SIDE_B,
      "AAMFE+ZdqgB+D5zP5+gA", "Verification");
   public static final IRelationTypeSide Verification_Plan__Requirement = TokenFactory.createRelationTypeSide(SIDE_A,
      "ATmJsVc28wU57zFAi7AA", "Verification Plan");
   public static final IRelationTypeSide Verification_Plan__Test_Plan_Element = TokenFactory.createRelationTypeSide(
      SIDE_B, "ATmJsVc28wU57zFAi7AA", "Verification Plan");

   private CoreRelationTypes() {
      // Constants
   }
}