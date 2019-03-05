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
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/**
 * @author Ryan D. Brooks
 */
public final class CoreRelationTypes {

   //@formatter:off
   public static final RelationTypeSide Allocation__Requirement = RelationTypeSide.create(SIDE_A, 0x2000000000000157L, "Allocation");
   public static final RelationTypeSide Allocation__Component = Allocation__Requirement.getOpposite();

   public static final RelationTypeSide CodeRequirement_CodeUnit = RelationTypeSide.create(SIDE_A, 0x2000000000000158L, "Code-Requirement");
   public static final RelationTypeSide CodeRequirement_Requirement = CodeRequirement_CodeUnit.getOpposite();

   public static final RelationTypeSide Default_Hierarchical__Parent = RelationTypeSide.create(SIDE_A, 2305843009213694292L, "Default Hierarchical");
   public static final RelationTypeSide Default_Hierarchical__Child = Default_Hierarchical__Parent.getOpposite();
   public static final RelationTypeToken DEFAULT_HIERARCHY = Default_Hierarchical__Parent;
   public static final RelationSide IS_PARENT = SIDE_A;
   public static final RelationSide IS_CHILD = SIDE_B;

   public static final RelationTypeSide Dependency__Artifact = RelationTypeSide.create(SIDE_A, 0x2000000000000155L, "Dependency");
   public static final RelationTypeSide Dependency__Dependency = Dependency__Artifact.getOpposite();

   public static final RelationTypeSide Design__Requirement = RelationTypeSide.create(SIDE_A, 0x200000000000015AL, "Design");
   public static final RelationTypeSide Design__Design = Design__Requirement.getOpposite();

   public static final RelationTypeSide Executes__Test_Plan_Element = RelationTypeSide.create(SIDE_A, 0x200000000000015EL, "Executes");
   public static final RelationTypeSide Executes__Test_Procedure = Executes__Test_Plan_Element.getOpposite();

   public static final RelationTypeSide Git_Repository_Commit = RelationTypeSide.create(SIDE_B, 2305843009213694290L, "Git Repository Commit");

   public static final RelationTypeSide Implementation_Info__Requirement = RelationTypeSide.create(SIDE_A, 3094530921867614694L, "Implementation_Info");
   public static final RelationTypeSide Implementation_Info__ImpD = Implementation_Info__Requirement.getOpposite();

   public static final RelationTypeSide Related_Feature__Feature = RelationTypeSide.create(SIDE_A, 0x0000000000000058L, "Related Feature");
   public static final RelationTypeSide Related_Feature__Requirement = Related_Feature__Feature.getOpposite();

   public static final RelationTypeSide Requirement_Trace__Higher_Level = RelationTypeSide.create(SIDE_A, 0x200000000000015FL, "Requirement Trace");
   public static final RelationTypeSide Requirement_Trace__Lower_Level = Requirement_Trace__Higher_Level.getOpposite();

   public static final RelationTypeSide Safety__Safety_Assessment = RelationTypeSide.create(SIDE_A, 0x2000000000000180L, "Safety Assessment");
   public static final RelationTypeSide Safety__System_Function = Safety__Safety_Assessment.getOpposite();

   public static final RelationTypeSide Supercedes_Supercedes = RelationTypeSide.create(SIDE_A, 0x2000000000000165L, "Supercedes");
   public static final RelationTypeSide Supercedes_Superceded = Supercedes_Supercedes.getOpposite();

   public static final RelationTypeSide SupportingInfo_SupportedBy = RelationTypeSide.create(SIDE_A, 0x2000000000000166L, "Supporting Info");
   public static final RelationTypeSide SupportingInfo_SupportingInfo = SupportingInfo_SupportedBy.getOpposite();

   public static final RelationTypeSide SupportingRequirement__Higher_Level = RelationTypeSide.create(SIDE_A, 0x200000000000017CL, "Supporting Requirement");
   public static final RelationTypeSide SupportingRequirement__Lower_Level = SupportingRequirement__Higher_Level.getOpposite();

   public static final RelationTypeSide TeamMember_Team = RelationTypeSide.create(SIDE_A, 0x200000000000016AL, "TeamMember");
   public static final RelationTypeSide TeamMember_Member = TeamMember_Team.getOpposite();

   public static final RelationTypeSide Test_Unit_Result__Test_Unit = RelationTypeSide.create(SIDE_A, 0x2000000000000168L, "Results Data");
   public static final RelationTypeSide Test_Unit_Result__Test_Result = Test_Unit_Result__Test_Unit.getOpposite();

   public static final RelationTypeSide Universal_Grouping__Group = RelationTypeSide.create(SIDE_A, 0x2000000000000156L, "Universal Grouping");
   public static final RelationTypeSide Universal_Grouping__Members = Universal_Grouping__Group.getOpposite();

   public static final RelationTypeSide Users_Artifact = RelationTypeSide.create(SIDE_A, 0x2000000000000164L, "Users");
   public static final RelationTypeSide Users_User = Users_Artifact.getOpposite();

   public static final RelationTypeSide Uses__Requirement = RelationTypeSide.create(SIDE_A, 0x2000000000000177L, "Uses");
   public static final RelationTypeSide Uses__TestUnit = Uses__Requirement.getOpposite();

   public static final RelationTypeSide Validation__Requirement = RelationTypeSide.create(SIDE_A, 0x2000000000000160L, "Validation");
   public static final RelationTypeSide Validation__Validator = Validation__Requirement.getOpposite();

   public static final RelationTypeSide Verification__Requirement = RelationTypeSide.create(SIDE_A, 0x200000000000015BL, "Verification");
   public static final RelationTypeSide Verification__Verifier = Verification__Requirement.getOpposite();

   public static final RelationTypeSide Verification_Plan__Requirement = RelationTypeSide.create(SIDE_A, 0x200000000000015CL, "Verification Plan");
   public static final RelationTypeSide Verification_Plan__Test_Plan_Element = Verification_Plan__Requirement.getOpposite();
   //@formatter:on

   private CoreRelationTypes() {
      // Constants
   }
}
