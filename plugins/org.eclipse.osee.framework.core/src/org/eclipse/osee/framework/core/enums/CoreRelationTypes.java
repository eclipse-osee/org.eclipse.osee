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

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.AbstractSoftwareRequirement;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.AbstractTestResult;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.CodeUnit;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Component;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.DesignMsWord;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Feature;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GitCommit;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GitRepository;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.ImplementationDetailsMsWord;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Requirement;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.SafetyAssessment;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.SubsystemFunctionMsWord;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.SystemFunctionMsWord;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TestInformationSheetMsWord;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TestPlanElementMsWord;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TestProcedure;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TestUnit;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.UniversalGroup;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.User;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.UserGroup;
import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.osee;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.UNORDERED;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.MANY_TO_MANY;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.MANY_TO_ONE;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.ONE_TO_MANY;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/**
 * @author Ryan D. Brooks
 */
public interface CoreRelationTypes {

   //@formatter:off
   RelationTypeToken Allocation = osee.add(2305843009213694295L, "Allocation", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, Requirement, "requirement", Component, "component");
   RelationTypeSide Allocation_Requirement = RelationTypeSide.create(Allocation, SIDE_A);
   RelationTypeSide Allocation_Component = RelationTypeSide.create(Allocation, SIDE_B);

   RelationTypeToken Assessment = osee.add(2305843009213694336L, "Assessment", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, SafetyAssessment, "Assessments", SystemFunctionMsWord, "System Functions");
   RelationTypeSide Assessment_SafetyAssessment = RelationTypeSide.create(Assessment, SIDE_A);
   RelationTypeSide Assessment_SystemFunctions = RelationTypeSide.create(Assessment, SIDE_B);

   RelationTypeToken CodeRequirement = osee.add(2305843009213694296L, "Code-Requirement", MANY_TO_MANY, UNORDERED, CodeUnit, "code", Requirement, "requirement");
   RelationTypeSide CodeRequirement_CodeUnit = RelationTypeSide.create(CodeRequirement, SIDE_A);
   RelationTypeSide CodeRequirement_Requirement = RelationTypeSide.create(CodeRequirement, SIDE_B);

   RelationTypeToken ComponentRequirement = osee.add(2305843009213694297L, "Component-Requirement", ONE_TO_MANY, UNORDERED, Component, "component", Requirement, "requirement");
   RelationTypeSide ComponentRequirement_Component = RelationTypeSide.create(ComponentRequirement, SIDE_A);
   RelationTypeSide ComponentRequirement_Requirement = RelationTypeSide.create(ComponentRequirement, SIDE_B);

   RelationTypeToken DefaultHierarchical = osee.add(2305843009213694292L, "Default Hierarchical", ONE_TO_MANY, LEXICOGRAPHICAL_ASC, Artifact, "parent", Artifact, "child");
   RelationTypeSide DefaultHierarchical_Parent = RelationTypeSide.create(DefaultHierarchical, SIDE_A);
   RelationTypeSide DefaultHierarchical_Child = RelationTypeSide.create(DefaultHierarchical, SIDE_B);

   // Use same relation as DefaultHierarchical; This is here for readability and to document this in code
   RelationTypeToken DEFAULT_HIERARCHY = DefaultHierarchical_Parent;
   RelationSide IS_PARENT = SIDE_A;
   RelationSide IS_CHILD = SIDE_B;

   RelationTypeToken Dependency = osee.add(2305843009213694293L, "Dependency", MANY_TO_MANY, UNORDERED, Artifact, "artifact", Artifact, "dependency");
   RelationTypeSide Dependency_Artifact = RelationTypeSide.create(Dependency, SIDE_A);
   RelationTypeSide Dependency_Dependency = RelationTypeSide.create(Dependency, SIDE_B);

   RelationTypeToken Design = osee.add(2305843009213694298L, "Design", MANY_TO_MANY, UNORDERED, Requirement, "requirement", DesignMsWord, "design");
   RelationTypeSide Design_Requirement = RelationTypeSide.create(Design, SIDE_A);
   RelationTypeSide Design_Design = RelationTypeSide.create(Design, SIDE_B);

   RelationTypeToken DevelopmentalVerification = osee.add(2305843009214603611L, "Developmental Verification", MANY_TO_MANY, UNORDERED, AbstractSoftwareRequirement, "requirement", Artifact, "developmental verifier");
   RelationTypeSide DevelopmentalVerification_Requirement = RelationTypeSide.create(DevelopmentalVerification, SIDE_A);
   RelationTypeSide DevelopmentalVerification_DevelopmentalVerifier = RelationTypeSide.create(DevelopmentalVerification, SIDE_B);

   RelationTypeToken Executes = osee.add(2305843009213694302L, "Executes", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, TestPlanElementMsWord, "test plan element", TestProcedure, "test procedure");
   RelationTypeSide Executes_TestPlanElement = RelationTypeSide.create(Executes, SIDE_A);
   RelationTypeSide Executes_TestProcedure = RelationTypeSide.create(Executes, SIDE_B);

   RelationTypeToken FunctionalDecomposition = osee.add(2305843009213694311L, "Functional decomposition ", ONE_TO_MANY, UNORDERED, Component, "higher-level component", Component, "lower-level component");
   RelationTypeSide FunctionalDecomposition_HigherLevelComponent = RelationTypeSide.create(FunctionalDecomposition, SIDE_A);
   RelationTypeSide FunctionalDecomposition_LowerLevelComponent = RelationTypeSide.create(FunctionalDecomposition, SIDE_B);

   RelationTypeToken GitRepositoryCommit = osee.add(2305843009213694290L, "Git Repository Commit", ONE_TO_MANY, UNORDERED, GitRepository, "Git Repository", GitCommit, "Git Commit");
   RelationTypeSide GitRepositoryCommit_GitRepository = RelationTypeSide.create(GitRepositoryCommit, SIDE_A);
   RelationTypeSide GitRepositoryCommit_GitCommit = RelationTypeSide.create(GitRepositoryCommit, SIDE_B);

   RelationTypeToken ImplementationInfo = osee.add(3094530921867614694L, "Implementation Info", MANY_TO_MANY, UNORDERED, AbstractSoftwareRequirement, "software requirement", ImplementationDetailsMsWord, "implementation details");
   RelationTypeSide ImplementationInfo_SoftwareRequirement = RelationTypeSide.create(ImplementationInfo, SIDE_A);
   RelationTypeSide ImplementationInfo_ImplementationDetails = RelationTypeSide.create(ImplementationInfo, SIDE_B);

   RelationTypeToken RelatedFeature = osee.add(88L, "Related Feature", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, Feature, "feature", Artifact, "artifact");
   RelationTypeSide RelatedFeature_Feature = RelationTypeSide.create(RelatedFeature, SIDE_A);
   RelationTypeSide RelatedFeature_Artifact = RelationTypeSide.create(RelatedFeature, SIDE_B);

   RelationTypeToken RequirementTrace = osee.add(2305843009213694303L, "Requirement Trace", MANY_TO_MANY, UNORDERED, Requirement, "higher-level requirement", Requirement, "lower-level requirement");
   RelationTypeSide RequirementTrace_HigherLevelRequirement = RelationTypeSide.create(RequirementTrace, SIDE_A);
   RelationTypeSide RequirementTrace_LowerLevelRequirement = RelationTypeSide.create(RequirementTrace, SIDE_B);

   RelationTypeToken ResultsData = osee.add(2305843009213694312L, "Results Data", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, TestUnit, "test unit", AbstractTestResult, "test result");
   RelationTypeSide ResultsData_TestUnit = RelationTypeSide.create(ResultsData, SIDE_A);
   RelationTypeSide ResultsData_TestResult = RelationTypeSide.create(ResultsData, SIDE_B);

   RelationTypeToken Specifying = osee.add(2305843009213694305L, "Specifying", MANY_TO_ONE, UNORDERED, SubsystemFunctionMsWord, "function", Requirement, "requirement");
   RelationTypeSide Specifying_Function = RelationTypeSide.create(Specifying, SIDE_A);
   RelationTypeSide Specifying_Requirement = RelationTypeSide.create(Specifying, SIDE_B);

   RelationTypeToken Supercedes = osee.add(2305843009213694309L, "Supercedes", MANY_TO_MANY, UNORDERED, Artifact, "supercedes", Artifact, "superceded by");
   RelationTypeSide Supercedes_Supercedes = RelationTypeSide.create(Supercedes, SIDE_A);
   RelationTypeSide Supercedes_SupercededBy = RelationTypeSide.create(Supercedes, SIDE_B);

   RelationTypeToken SupportingInfo = osee.add(2305843009213694310L, "Supporting Info", MANY_TO_MANY, UNORDERED, Artifact, "is supported by", Artifact, "supporting info");
   RelationTypeSide SupportingInfo_IsSupportedBy = RelationTypeSide.create(SupportingInfo, SIDE_A);
   RelationTypeSide SupportingInfo_SupportingInfo = RelationTypeSide.create(SupportingInfo, SIDE_B);

   RelationTypeToken SupportingRequirement = osee.add(2305843009213694332L, "Supporting Requirement", MANY_TO_MANY, UNORDERED, Requirement, "higher-level requirement", Requirement, "lower-level requirement");
   RelationTypeSide SupportingRequirement_HigherLevelRequirement = RelationTypeSide.create(SupportingRequirement, SIDE_A);
   RelationTypeSide SupportingRequirement_LowerLevelRequirement = RelationTypeSide.create(SupportingRequirement, SIDE_B);

   RelationTypeToken UniversalGrouping = osee.add(2305843009213694294L, "Universal Grouping", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, UniversalGroup, "group", Artifact, "members");
   RelationTypeSide UniversalGrouping_Group = RelationTypeSide.create(UniversalGrouping, SIDE_A);
   RelationTypeSide UniversalGrouping_Members = RelationTypeSide.create(UniversalGrouping, SIDE_B);

   RelationTypeToken UserGrouping = osee.add(2305843009213694307L, "User Grouping", MANY_TO_MANY, UNORDERED, UserGroup, "group", CoreArtifactTypes.User, "users");
   RelationTypeSide UserGrouping_Group = RelationTypeSide.create(UserGrouping, SIDE_A);
   RelationTypeSide UserGrouping_Users = RelationTypeSide.create(UserGrouping, SIDE_B);

   RelationTypeToken Users = osee.add(2305843009213694308L, "Users", MANY_TO_MANY, UNORDERED, Artifact, "Artifact", User, "User");
   RelationTypeSide Users_Artifact = RelationTypeSide.create(Users, SIDE_A);
   RelationTypeSide Users_User = RelationTypeSide.create(Users, SIDE_B);

   RelationTypeToken Uses = osee.add(2305843009213694327L, "Uses", MANY_TO_MANY, UNORDERED, Requirement, "requirement", TestUnit, "Test Unit");
   RelationTypeSide Uses_Requirement = RelationTypeSide.create(Uses, SIDE_A);
   RelationTypeSide Uses_TestUnit = RelationTypeSide.create(Uses, SIDE_B);

   RelationTypeToken Validation = osee.add(2305843009213694304L, "Validation", MANY_TO_MANY, UNORDERED, Requirement, "requirement", Artifact, "validator");
   RelationTypeSide Validation_Requirement = RelationTypeSide.create(Validation, SIDE_A);
   RelationTypeSide Validation_Validator = RelationTypeSide.create(Validation, SIDE_B);

   RelationTypeToken Verification = osee.add(2305843009213694299L, "Verification", MANY_TO_MANY, UNORDERED, Requirement, "requirement", TestUnit, "verifier");
   RelationTypeSide Verification_Requirement = RelationTypeSide.create(Verification, SIDE_A);
   RelationTypeSide Verification_Verifier = RelationTypeSide.create(Verification, SIDE_B);

   RelationTypeToken VerificationPlan = osee.add(2305843009213694300L, "Verification Plan", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, Requirement, "requirement", TestPlanElementMsWord, "test plan element");
   RelationTypeSide VerificationPlan_Requirement = RelationTypeSide.create(VerificationPlan, SIDE_A);
   RelationTypeSide VerificationPlan_TestPlanElement = RelationTypeSide.create(VerificationPlan, SIDE_B);

   RelationTypeToken VerificationProcedure = osee.add(2305843009213694301L, "Verification Procedure", MANY_TO_MANY, UNORDERED, TestInformationSheetMsWord, "test information sheet", TestProcedure, "test procedure");
   RelationTypeSide VerificationProcedure_TestInformationSheet = RelationTypeSide.create(VerificationProcedure, SIDE_A);
   RelationTypeSide VerificationProcedure_TestProcedure = RelationTypeSide.create(VerificationProcedure, SIDE_B);

   RelationTypeToken WorkItem = osee.add(2305843009213694306L, "Work Item", MANY_TO_MANY, RelationSorter.LEXICOGRAPHICAL_ASC, Artifact, "Parent", Artifact, "Child");
   RelationTypeSide WorkItem_Parent = RelationTypeSide.create(WorkItem, SIDE_A);
   RelationTypeSide WorkItem_Child = RelationTypeSide.create(WorkItem, SIDE_B);
   //@formatter:on
}