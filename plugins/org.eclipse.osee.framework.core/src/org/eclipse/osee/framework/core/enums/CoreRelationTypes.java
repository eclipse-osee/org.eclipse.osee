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
   public static final RelationTypeSide Allocation_Requirement = RelationTypeSide.create(SIDE_A, 2305843009213694295L, "Allocation");
   public static final RelationTypeSide Allocation_Component = Allocation_Requirement.getOpposite();

   public static final RelationTypeSide CodeRequirement_CodeUnit = RelationTypeSide.create(SIDE_A, 2305843009213694296L, "Code-Requirement");
   public static final RelationTypeSide CodeRequirement_Requirement = CodeRequirement_CodeUnit.getOpposite();

   public static final RelationTypeSide DefaultHierarchical_Parent = RelationTypeSide.create(SIDE_A, 2305843009213694292L, "Default Hierarchical");
   public static final RelationTypeSide DefaultHierarchical_Child = DefaultHierarchical_Parent.getOpposite();
   public static final RelationTypeToken DEFAULT_HIERARCHY = DefaultHierarchical_Parent;
   public static final RelationSide IS_PARENT = SIDE_A;
   public static final RelationSide IS_CHILD = SIDE_B;

   public static final RelationTypeSide Dependency_Artifact = RelationTypeSide.create(SIDE_A, 2305843009213694293L, "Dependency");
   public static final RelationTypeSide Dependency_Dependency = Dependency_Artifact.getOpposite();

   public static final RelationTypeSide Design_Requirement = RelationTypeSide.create(SIDE_A, 2305843009213694298L, "Design");
   public static final RelationTypeSide Design_Design = Design_Requirement.getOpposite();

   public static final RelationTypeSide Executes_TestPlanElement = RelationTypeSide.create(SIDE_A, 2305843009213694302L, "Executes");
   public static final RelationTypeSide Executes_TestProcedure = Executes_TestPlanElement.getOpposite();

   public static final RelationTypeSide GitRepositoryCommit_GitRepository = RelationTypeSide.create(SIDE_A, 2305843009213694290L, "Git Repository Commit");
   public static final RelationTypeSide GitRepositoryCommit_GitCommit = GitRepositoryCommit_GitRepository.getOpposite();

   public static final RelationTypeSide ImplementationInfo_SoftwareRequirement = RelationTypeSide.create(SIDE_A, 3094530921867614694L, "Implementation_Info");
   public static final RelationTypeSide ImplementationInfo_ImplementationDetails = ImplementationInfo_SoftwareRequirement.getOpposite();

   public static final RelationTypeSide RelatedFeature_Feature = RelationTypeSide.create(SIDE_A, 88L, "Related Feature");
   public static final RelationTypeSide RelatedFeature_Artifact = RelatedFeature_Feature.getOpposite();

   public static final RelationTypeSide RequirementTrace_HigherLevelRequirement = RelationTypeSide.create(SIDE_A, 2305843009213694303L, "Requirement Trace");
   public static final RelationTypeSide RequirementTrace_LowerLevelRequirement = RequirementTrace_HigherLevelRequirement.getOpposite();

   public static final RelationTypeSide Assessment_SafetyAssessment = RelationTypeSide.create(SIDE_A, 2305843009213694336L, "Safety Assessment");
   public static final RelationTypeSide Assessment_SystemFunctions = Assessment_SafetyAssessment.getOpposite();

   public static final RelationTypeSide Supercedes_Supercedes = RelationTypeSide.create(SIDE_A, 2305843009213694309L, "Supercedes");
   public static final RelationTypeSide Supercedes_SupercededBy = Supercedes_Supercedes.getOpposite();

   public static final RelationTypeSide SupportingInfo_IsSupportedBy = RelationTypeSide.create(SIDE_A, 2305843009213694310L, "Supporting Info");
   public static final RelationTypeSide SupportingInfo_SupportingInfo = SupportingInfo_IsSupportedBy.getOpposite();

   public static final RelationTypeSide SupportingRequirement_HigherLevelRequirement = RelationTypeSide.create(SIDE_A, 2305843009213694332L, "Supporting Requirement");
   public static final RelationTypeSide SupportingRequirement_LowerLevelRequirement = SupportingRequirement_HigherLevelRequirement.getOpposite();

   public static final RelationTypeSide TeamMember_TeamDefinition = RelationTypeSide.create(SIDE_A, 2305843009213694314L, "TeamMember");
   public static final RelationTypeSide TeamMember_User = TeamMember_TeamDefinition.getOpposite();

   public static final RelationTypeSide ResultsData_TestUnit = RelationTypeSide.create(SIDE_A, 2305843009213694312L, "Results Data");
   public static final RelationTypeSide ResultsData_TestResult = ResultsData_TestUnit.getOpposite();

   public static final RelationTypeSide UniversalGrouping_Group = RelationTypeSide.create(SIDE_A, 2305843009213694294L, "Universal Grouping");
   public static final RelationTypeSide UniversalGrouping_Members = UniversalGrouping_Group.getOpposite();

   public static final RelationTypeSide Users_Artifact = RelationTypeSide.create(SIDE_A, 2305843009213694308L, "Users");
   public static final RelationTypeSide Users_User = Users_Artifact.getOpposite();

   public static final RelationTypeSide Uses_Requirement = RelationTypeSide.create(SIDE_A, 2305843009213694327L, "Uses");
   public static final RelationTypeSide Uses_TestUnit = Uses_Requirement.getOpposite();

   public static final RelationTypeSide Validation_Requirement = RelationTypeSide.create(SIDE_A, 2305843009213694304L, "Validation");
   public static final RelationTypeSide Validation_Validator = Validation_Requirement.getOpposite();

   public static final RelationTypeSide Verification_Requirement = RelationTypeSide.create(SIDE_A, 2305843009213694299L, "Verification");
   public static final RelationTypeSide Verification_Verifier = Verification_Requirement.getOpposite();

   public static final RelationTypeSide VerificationPlan_Requirement = RelationTypeSide.create(SIDE_A, 2305843009213694300L, "Verification Plan");
   public static final RelationTypeSide VerificationPlan_TestPlanElement = VerificationPlan_Requirement.getOpposite();
   //@formatter:on

}
