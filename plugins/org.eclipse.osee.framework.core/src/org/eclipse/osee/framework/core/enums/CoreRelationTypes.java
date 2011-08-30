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
      0x2000000000000157L, "Allocation");
   public static final IRelationTypeSide Allocation__Component = TokenFactory.createRelationTypeSide(SIDE_B,
      0x2000000000000157L, "Allocation");
   public static final IRelationTypeSide CodeRequirement_Requirement = TokenFactory.createRelationTypeSide(SIDE_B,
      0x2000000000000158L, "Code-Requirement");
   public static final IRelationTypeSide CodeRequirement_CodeUnit = TokenFactory.createRelationTypeSide(SIDE_A,
      0x2000000000000158L, "Code-Requirement");
   public static final IRelationTypeSide Default_Hierarchical__Parent = TokenFactory.createRelationTypeSide(SIDE_A,
      0x2000000000000154L, "Default Hierarchical");
   public static final IRelationTypeSide Default_Hierarchical__Child = TokenFactory.createRelationTypeSide(SIDE_B,
      0x2000000000000154L, "Default Hierarchical");
   public static final IRelationTypeSide Dependency__Dependency = TokenFactory.createRelationTypeSide(SIDE_B,
      0x2000000000000155L, "Dependency");
   public static final IRelationTypeSide Dependency__Artifact = TokenFactory.createRelationTypeSide(SIDE_A,
      0x2000000000000155L, "Dependency");
   public static final IRelationTypeSide Design__Design = TokenFactory.createRelationTypeSide(SIDE_B,
      0x200000000000015AL, "Design");
   public static final IRelationTypeSide Design__Requirement = TokenFactory.createRelationTypeSide(SIDE_A,
      0x200000000000015AL, "Design");
   public static final IRelationTypeSide Executes__Test_Plan_Element = TokenFactory.createRelationTypeSide(SIDE_A,
      0x200000000000015EL, "Executes");
   public static final IRelationTypeSide Executes__Test_Procedure = TokenFactory.createRelationTypeSide(SIDE_B,
      0x200000000000015EL, "Executes");
   public static final IRelationTypeSide Requirement_Trace__Higher_Level = TokenFactory.createRelationTypeSide(SIDE_A,
      0x200000000000015FL, "Requirement Trace");
   public static final IRelationTypeSide Requirement_Trace__Lower_Level = TokenFactory.createRelationTypeSide(SIDE_B,
      0x200000000000015FL, "Requirement Trace");
   public static final IRelationTypeSide Supercedes_Supercedes = TokenFactory.createRelationTypeSide(SIDE_A,
      0x2000000000000165L, "Supercedes");
   public static final IRelationTypeSide Supercedes_Superceded = TokenFactory.createRelationTypeSide(SIDE_B,
      0x2000000000000165L, "Supercedes");
   public static final IRelationTypeSide SupportingInfo_SupportedBy = TokenFactory.createRelationTypeSide(SIDE_A,
      0x2000000000000166L, "Supporting Info");
   public static final IRelationTypeSide SupportingInfo_SupportingInfo = TokenFactory.createRelationTypeSide(SIDE_B,
      0x2000000000000166L, "Supporting Info");
   public static final IRelationTypeSide TeamMember_Team = TokenFactory.createRelationTypeSide(SIDE_A,
      0x200000000000016AL, "TeamMember");
   public static final IRelationTypeSide TeamMember_Member = TokenFactory.createRelationTypeSide(SIDE_B,
      0x200000000000016AL, "TeamMember");
   public static final IRelationTypeSide Test_Unit_Result__Test_Unit = TokenFactory.createRelationTypeSide(SIDE_A,
      0x2000000000000168L, "Results Data");
   public static final IRelationTypeSide Test_Unit_Result__Test_Result = TokenFactory.createRelationTypeSide(SIDE_B,
      0x2000000000000168L, "Results Data");
   public static final IRelationTypeSide Universal_Grouping__Members = TokenFactory.createRelationTypeSide(SIDE_B,
      0x2000000000000156L, "Universal Grouping");
   public static final IRelationTypeSide Universal_Grouping__Group = TokenFactory.createRelationTypeSide(SIDE_A,
      0x2000000000000156L, "Universal Grouping");
   public static final IRelationTypeSide Users_Artifact = TokenFactory.createRelationTypeSide(SIDE_A,
      0x2000000000000164L, "Users");
   public static final IRelationTypeSide Users_User = TokenFactory.createRelationTypeSide(SIDE_B, 0x2000000000000164L,
      "Users");
   public static final IRelationTypeSide Uses__Requirement = TokenFactory.createRelationTypeSide(SIDE_A,
      0x2000000000000177L, "Uses");
   public static final IRelationTypeSide Uses__TestUnit = TokenFactory.createRelationTypeSide(SIDE_B,
      0x2000000000000177L, "Uses");
   public static final IRelationTypeSide Validation__Requirement = TokenFactory.createRelationTypeSide(SIDE_A,
      0x2000000000000160L, "Validation");
   public static final IRelationTypeSide Validation__Validator = TokenFactory.createRelationTypeSide(SIDE_B,
      0x2000000000000160L, "Validation");
   public static final IRelationTypeSide Verification__Requirement = TokenFactory.createRelationTypeSide(SIDE_A,
      0x200000000000015BL, "Verification");
   public static final IRelationTypeSide Verification__Verifier = TokenFactory.createRelationTypeSide(SIDE_B,
      0x200000000000015BL, "Verification");
   public static final IRelationTypeSide Verification_Plan__Requirement = TokenFactory.createRelationTypeSide(SIDE_A,
      0x200000000000015CL, "Verification Plan");
   public static final IRelationTypeSide Verification_Plan__Test_Plan_Element = TokenFactory.createRelationTypeSide(
      SIDE_B, 0x200000000000015CL, "Verification Plan");

   private CoreRelationTypes() {
      // Constants
   }
}