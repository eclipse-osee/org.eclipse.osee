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
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.TokenFactory;

public final class CoreRelationTypes {

   //@formatter:off
   public static final IRelationTypeSide Allocation__Requirement = TokenFactory.createRelationTypeSide(SIDE_A, 0x2000000000000157L, "Allocation");
   public static final IRelationTypeSide Allocation__Component = Allocation__Requirement.getOpposite();

   public static final IRelationTypeSide CodeRequirement_CodeUnit = TokenFactory.createRelationTypeSide(SIDE_A, 0x2000000000000158L, "Code-Requirement");
   public static final IRelationTypeSide CodeRequirement_Requirement = CodeRequirement_CodeUnit.getOpposite();

   public static final IRelationTypeSide Default_Hierarchical__Parent = TokenFactory.createRelationTypeSide(SIDE_A, 0x2000000000000154L, "Default Hierarchical");
   public static final IRelationTypeSide Default_Hierarchical__Child = Default_Hierarchical__Parent.getOpposite();

   public static final IRelationTypeSide Dependency__Artifact = TokenFactory.createRelationTypeSide(SIDE_A, 0x2000000000000155L, "Dependency");
   public static final IRelationTypeSide Dependency__Dependency = Dependency__Artifact.getOpposite();

   public static final IRelationTypeSide Design__Requirement = TokenFactory.createRelationTypeSide(SIDE_A, 0x200000000000015AL, "Design");
   public static final IRelationTypeSide Design__Design = Design__Requirement.getOpposite();

   public static final IRelationTypeSide Executes__Test_Plan_Element = TokenFactory.createRelationTypeSide(SIDE_A, 0x200000000000015EL, "Executes");
   public static final IRelationTypeSide Executes__Test_Procedure = Executes__Test_Plan_Element.getOpposite();
   
   public static final IRelationTypeSide Requirement_Trace__Higher_Level = TokenFactory.createRelationTypeSide(SIDE_A, 0x200000000000015FL, "Requirement Trace");
   public static final IRelationTypeSide Requirement_Trace__Lower_Level = Requirement_Trace__Higher_Level.getOpposite();
   
   public static final IRelationTypeSide Safety__Safety_Assessment = TokenFactory.createRelationTypeSide(SIDE_A, 0x2000000000000180L, "Safety Assessment");
   public static final IRelationTypeSide Safety__System_Function = Safety__Safety_Assessment.getOpposite();
   
   public static final IRelationTypeSide Supercedes_Supercedes = TokenFactory.createRelationTypeSide(SIDE_A, 0x2000000000000165L, "Supercedes");
   public static final IRelationTypeSide Supercedes_Superceded = Supercedes_Supercedes.getOpposite();
   
   public static final IRelationTypeSide SupportingInfo_SupportedBy = TokenFactory.createRelationTypeSide(SIDE_A, 0x2000000000000166L, "Supporting Info");
   public static final IRelationTypeSide SupportingInfo_SupportingInfo = SupportingInfo_SupportedBy.getOpposite();
   
   public static final IRelationTypeSide SupportingRequirement_Supports = TokenFactory.createRelationTypeSide(SIDE_A, 0x200000000000017CL, "Supporting Requirement");
   public static final IRelationTypeSide SupportingRequirement_SupportingRequirement = SupportingInfo_SupportedBy.getOpposite();
   
   public static final IRelationTypeSide TeamMember_Team = TokenFactory.createRelationTypeSide(SIDE_A, 0x200000000000016AL, "TeamMember");
   public static final IRelationTypeSide TeamMember_Member = TeamMember_Team.getOpposite();
   
   public static final IRelationTypeSide Test_Unit_Result__Test_Unit = TokenFactory.createRelationTypeSide(SIDE_A, 0x2000000000000168L, "Results Data");
   public static final IRelationTypeSide Test_Unit_Result__Test_Result = Test_Unit_Result__Test_Unit.getOpposite();
  
   public static final IRelationTypeSide Universal_Grouping__Group = TokenFactory.createRelationTypeSide(SIDE_A, 0x2000000000000156L, "Universal Grouping");
   public static final IRelationTypeSide Universal_Grouping__Members = Universal_Grouping__Group.getOpposite();
   
   public static final IRelationTypeSide Users_Artifact = TokenFactory.createRelationTypeSide(SIDE_A, 0x2000000000000164L, "Users");
   public static final IRelationTypeSide Users_User = Users_Artifact.getOpposite();
   
   public static final IRelationTypeSide Uses__Requirement = TokenFactory.createRelationTypeSide(SIDE_A, 0x2000000000000177L, "Uses");
   public static final IRelationTypeSide Uses__TestUnit = Uses__Requirement.getOpposite();
   
   public static final IRelationTypeSide Validation__Requirement = TokenFactory.createRelationTypeSide(SIDE_A, 0x2000000000000160L, "Validation");
   public static final IRelationTypeSide Validation__Validator = Validation__Requirement.getOpposite();
   
   public static final IRelationTypeSide Verification__Requirement = TokenFactory.createRelationTypeSide(SIDE_A, 0x200000000000015BL, "Verification");
   public static final IRelationTypeSide Verification__Verifier = Verification__Requirement.getOpposite();
   
   public static final IRelationTypeSide Verification_Plan__Requirement = TokenFactory.createRelationTypeSide(SIDE_A, 0x200000000000015CL, "Verification Plan");
   public static final IRelationTypeSide Verification_Plan__Test_Plan_Element = Verification_Plan__Requirement.getOpposite();
   //@formatter:on

   private CoreRelationTypes() {
      // Constants
   }
}
