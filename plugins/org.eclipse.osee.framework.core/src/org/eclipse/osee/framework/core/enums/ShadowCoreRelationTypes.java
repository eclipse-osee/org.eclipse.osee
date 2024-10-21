/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.enums;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.AbstractSoftwareRequirement;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.AbstractTestResult;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.BranchView;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.CodeUnit;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Component;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Context;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.DesignMsWord;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.ExecutedCommandHistory;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Feature;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GitCommit;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GitRepository;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GroupArtifact;
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
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.ONE_TO_ONE;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

public interface ShadowCoreRelationTypes {

   //@formatter:off
   RelationTypeToken AllocationRel = osee.addNewRelationType(4400405161969600804L, "Allocation", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, Requirement, "requirement", Component, "component",CoreRelationTypes.Allocation);
   RelationTypeSide AllocationRel_Requirement = RelationTypeSide.create(AllocationRel, SIDE_A);
   RelationTypeSide AllocationRel_Component = RelationTypeSide.create(AllocationRel, SIDE_B);

   RelationTypeToken AssessmentRel = osee.addNewRelationType(4400405161969600805L, "Assessment", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, SafetyAssessment, "Assessments", SystemFunctionMsWord, "System Functions",CoreRelationTypes.Assessment);
   RelationTypeSide AssessmentRel_SafetyAssessmentRel = RelationTypeSide.create(AssessmentRel, SIDE_A);
   RelationTypeSide AssessmentRel_SystemFunctions = RelationTypeSide.create(AssessmentRel, SIDE_B);

   RelationTypeToken CodeRequirementRel = osee.addNewRelationType(4400405161969600806L, "Code-Requirement", MANY_TO_MANY, UNORDERED, CodeUnit, "code", Requirement, "requirement",CoreRelationTypes.CodeRequirement);
   RelationTypeSide CodeRequirementRel_CodeUnit = RelationTypeSide.create(CodeRequirementRel, SIDE_A);
   RelationTypeSide CodeRequirementRel_Requirement = RelationTypeSide.create(CodeRequirementRel, SIDE_B);


   RelationTypeToken ComponentRequirementRel = osee.addNewRelationType(4400405161969600807L, "Component-Requirement", ONE_TO_MANY, UNORDERED, Component, "component", Requirement, "requirement",CoreRelationTypes.ComponentRequirement);
   RelationTypeSide ComponentRequirementRel_Component = RelationTypeSide.create(ComponentRequirementRel, SIDE_A);
   RelationTypeSide ComponentRequirementRel_Requirement = RelationTypeSide.create(ComponentRequirementRel, SIDE_B);

   RelationTypeToken DefaultHierarchicalRel = osee.addNewRelationType(4400405161969600808L, "Default Hierarchical", ONE_TO_MANY, LEXICOGRAPHICAL_ASC, Artifact, "parent", Artifact, "child",CoreRelationTypes.DefaultHierarchical);
   RelationTypeSide DefaultHierarchicalRel_Parent = RelationTypeSide.create(DefaultHierarchicalRel, SIDE_A);
   RelationTypeSide DefaultHierarchicalRel_Child = RelationTypeSide.create(DefaultHierarchicalRel, SIDE_B);

   // Use same relation as DefaultHierarchical; This is here for readability and to document this in code
   RelationTypeToken DEFAULT_HIERARCHY_REL = DefaultHierarchicalRel_Parent;
   RelationSide IS_PARENT = SIDE_A;
   RelationSide IS_CHILD = SIDE_B;

   RelationTypeToken DependencyRel = osee.addNewRelationType(4400405161969600809L, "Dependency", MANY_TO_MANY, UNORDERED, Artifact, "artifact", Artifact, "dependency",CoreRelationTypes.Dependency);
   RelationTypeSide DependencyRel_Artifact = RelationTypeSide.create(DependencyRel, SIDE_A);
   RelationTypeSide DependencyRel_DependencyRel = RelationTypeSide.create(DependencyRel, SIDE_B);

   RelationTypeToken DesignRel = osee.addNewRelationType(4400405161969600810L, "Design", MANY_TO_MANY, UNORDERED, Requirement, "requirement", DesignMsWord, "design",CoreRelationTypes.Design);
   RelationTypeSide DesignRel_Requirement = RelationTypeSide.create(DesignRel, SIDE_A);
   RelationTypeSide DesignRel_DesignRel = RelationTypeSide.create(DesignRel, SIDE_B);

   RelationTypeToken DevelopmentalVerificationRel = osee.addNewRelationType(4400405161969600811L, "Developmental Verification", MANY_TO_MANY, UNORDERED, AbstractSoftwareRequirement, "requirement", Artifact, "developmental verifier",CoreRelationTypes.DevelopmentalVerification);
   RelationTypeSide DevelopmentalVerificationRel_Requirement = RelationTypeSide.create(DevelopmentalVerificationRel, SIDE_A);
   RelationTypeSide DevelopmentalVerificationRel_DevelopmentalVerifier = RelationTypeSide.create(DevelopmentalVerificationRel, SIDE_B);

   RelationTypeToken ExecutesRel = osee.addNewRelationType(4400405161969600812L, "Executes", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, TestPlanElementMsWord, "test plan element", TestProcedure, "test procedure",CoreRelationTypes.Executes);
   RelationTypeSide ExecutesRel_TestPlanElement = RelationTypeSide.create(ExecutesRel, SIDE_A);
   RelationTypeSide ExecutesRel_TestProcedure = RelationTypeSide.create(ExecutesRel, SIDE_B);

   RelationTypeToken FunctionalDecompositionRel = osee.addNewRelationType(4400405161969600813L, "Functional decomposition", ONE_TO_MANY, UNORDERED, Component, "higher-level component", Component, "lower-level component",CoreRelationTypes.FunctionalDecomposition);
   RelationTypeSide FunctionalDecompositionRel_HigherLevelComponent = RelationTypeSide.create(FunctionalDecompositionRel, SIDE_A);
   RelationTypeSide FunctionalDecompositionRel_LowerLevelComponent = RelationTypeSide.create(FunctionalDecompositionRel, SIDE_B);

   RelationTypeToken GitRepositoryCommitRel = osee.addNewRelationType(4400405161969600814L, "Git Repository Commit", ONE_TO_MANY, UNORDERED, GitRepository, "Git Repository", GitCommit, "Git Commit",CoreRelationTypes.GitRepositoryCommit);
   RelationTypeSide GitRepositoryCommitRel_GitRepository = RelationTypeSide.create(GitRepositoryCommitRel, SIDE_A);
   RelationTypeSide GitRepositoryCommitRel_GitCommit = RelationTypeSide.create(GitRepositoryCommitRel, SIDE_B);

   RelationTypeToken ImplementationInfoRel = osee.addNewRelationType(4400405161969600815L, "Implementation Info", MANY_TO_MANY, UNORDERED, AbstractSoftwareRequirement, "software requirement", ImplementationDetailsMsWord, "implementation details",CoreRelationTypes.ImplementationInfo);
   RelationTypeSide ImplementationInfoRel_SoftwareRequirement = RelationTypeSide.create(ImplementationInfoRel, SIDE_A);
   RelationTypeSide ImplementationInfoRel_ImplementationDetails = RelationTypeSide.create(ImplementationInfoRel, SIDE_B);


   RelationTypeToken PlConfigurationGroupRel = osee.addNewRelationType(4400405161969600816L, "Product Line Configuration Group", MANY_TO_MANY, RelationSorter.LEXICOGRAPHICAL_ASC, GroupArtifact, "Group", BranchView, "BranchView",CoreRelationTypes.PlConfigurationGroup);
   RelationTypeSide PlConfigurationGroupRel_Group = RelationTypeSide.create(PlConfigurationGroupRel, SIDE_A);
   RelationTypeSide PlConfigurationGroupRel_BranchView = RelationTypeSide.create(PlConfigurationGroupRel, SIDE_B);

   RelationTypeToken RelatedFeatureRel = osee.addNewRelationType(4400405161969600817L, "Related Feature", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, Feature, "feature", Artifact, "artifact",CoreRelationTypes.RelatedFeature);
   RelationTypeSide RelatedFeatureRel_Feature = RelationTypeSide.create(RelatedFeatureRel, SIDE_A);
   RelationTypeSide RelatedFeatureRel_Artifact = RelationTypeSide.create(RelatedFeatureRel, SIDE_B);

   RelationTypeToken RequirementTraceRel = osee.addNewRelationType(4400405161969600818L, "Requirement Trace", MANY_TO_MANY, UNORDERED, Requirement, "higher-level requirement", Requirement, "lower-level requirement",CoreRelationTypes.RequirementTrace);
   RelationTypeSide RequirementTraceRel_HigherLevelRequirement = RelationTypeSide.create(RequirementTraceRel, SIDE_A);
   RelationTypeSide RequirementTraceRel_LowerLevelRequirement = RelationTypeSide.create(RequirementTraceRel, SIDE_B);

   RelationTypeToken ResultsDataRel = osee.addNewRelationType(4400405161969600819L, "Results Data", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, TestUnit, "test unit", AbstractTestResult, "test result",CoreRelationTypes.ResultsData);
   RelationTypeSide ResultsDataRel_TestUnit = RelationTypeSide.create(ResultsDataRel, SIDE_A);
   RelationTypeSide ResultsDataRel_TestResult = RelationTypeSide.create(ResultsDataRel, SIDE_B);

   RelationTypeToken SpecifyingRel = osee.addNewRelationType(4400405161969600820L, "Specifying", MANY_TO_ONE, UNORDERED, SubsystemFunctionMsWord, "function", Requirement, "requirement",CoreRelationTypes.Specifying);
   RelationTypeSide SpecifyingRel_Function = RelationTypeSide.create(SpecifyingRel, SIDE_A);
   RelationTypeSide SpecifyingRel_Requirement = RelationTypeSide.create(SpecifyingRel, SIDE_B);

   RelationTypeToken SupercedesRel = osee.addNewRelationType(4400405161969600821L, "Supercedes", MANY_TO_MANY, UNORDERED, Artifact, "supercedes", Artifact, "superceded by",CoreRelationTypes.Supercedes);
   RelationTypeSide SupercedesRel_SupercedesRel = RelationTypeSide.create(SupercedesRel, SIDE_A);
   RelationTypeSide SupercedesRel_SupercededBy = RelationTypeSide.create(SupercedesRel, SIDE_B);

   RelationTypeToken SupportingInfoRel = osee.addNewRelationType(4400405161969600822L, "Supporting Info", MANY_TO_MANY, UNORDERED, Artifact, "is supported by", Artifact, "supporting info",CoreRelationTypes.SupportingInfo);
   RelationTypeSide SupportingInfoRel_IsSupportedBy = RelationTypeSide.create(SupportingInfoRel, SIDE_A);
   RelationTypeSide SupportingInfoRel_SupportingInfoRel = RelationTypeSide.create(SupportingInfoRel, SIDE_B);

   RelationTypeToken SupportingRequirementRel = osee.addNewRelationType(4400405161969600823L, "Supporting Requirement", MANY_TO_MANY, UNORDERED, Requirement, "higher-level requirement", Requirement, "lower-level requirement",CoreRelationTypes.SupportingRequirement);
   RelationTypeSide SupportingRequirementRel_HigherLevelRequirement = RelationTypeSide.create(SupportingRequirementRel, SIDE_A);
   RelationTypeSide SupportingRequirementRel_LowerLevelRequirement = RelationTypeSide.create(SupportingRequirementRel, SIDE_B);

   RelationTypeToken UniversalGroupingRel = osee.addNewRelationType(4400405161969600824L, "Universal Grouping", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, UniversalGroup, "group", Artifact, "members",CoreRelationTypes.UniversalGrouping);
   RelationTypeSide UniversalGroupingRel_Group = RelationTypeSide.create(UniversalGroupingRel, SIDE_A);
   RelationTypeSide UniversalGroupingRel_Members = RelationTypeSide.create(UniversalGroupingRel, SIDE_B);

   RelationTypeToken UsersRel = osee.addNewRelationType(4400405161969600825L, "Users", MANY_TO_MANY, UNORDERED, Artifact, "Artifact", User, "User",CoreRelationTypes.Users);
   RelationTypeSide UsersRel_Artifact = RelationTypeSide.create(UsersRel, SIDE_A);
   RelationTypeSide UsersRel_User = RelationTypeSide.create(UsersRel, SIDE_B);

   RelationTypeToken UserToContextRel = osee.addNewRelationType(4400405161969600826L, "User to Context", MANY_TO_MANY, UNORDERED, User, "User", Context, "Context",CoreRelationTypes.UserToContext);
   RelationTypeSide UserToContextRel_User = RelationTypeSide.create(UserToContextRel, SIDE_A);
   RelationTypeSide UserToContextRel_Context = RelationTypeSide.create(UserToContextRel, SIDE_B);

   RelationTypeToken UserGroupToContextRel = osee.addNewRelationType(4400405161969600827L, "User Group to Context", MANY_TO_MANY, UNORDERED, UserGroup, "User Group", Context, "Context",CoreRelationTypes.UserGroupToContext);
   RelationTypeSide UserGroupToContextRel_UserGroup = RelationTypeSide.create(UserGroupToContextRel, SIDE_A);
   RelationTypeSide UserGroupToContextRel_Context = RelationTypeSide.create(UserGroupToContextRel, SIDE_B);

   RelationTypeToken UserToHistoryRel = osee.addNewRelationType(4400405161969600828L, "User to History", ONE_TO_ONE, UNORDERED, User, "User", ExecutedCommandHistory, "Executed Command History",CoreRelationTypes.UserToHistory);
   RelationTypeSide UserToHistoryRel_User = RelationTypeSide.create(UserToHistoryRel, SIDE_A);
   RelationTypeSide UserToHistoryRel_ExecutedCommandHistory = RelationTypeSide.create(UserToHistoryRel, SIDE_B);

   RelationTypeToken ContextToCommandRel = osee.addNewRelationType(4400405161969600829L, "Context to Command", MANY_TO_MANY, UNORDERED, Context, "Context", Artifact, "Context or Command",CoreRelationTypes.ContextToCommand);
   RelationTypeSide ContextToCommandRel_Context = RelationTypeSide.create(ContextToCommandRel, SIDE_A);
   RelationTypeSide ContextToCommandRel_Artifact = RelationTypeSide.create(ContextToCommandRel, SIDE_B);

   RelationTypeToken UsesRel = osee.addNewRelationType(4400405161969600830L, "Uses", MANY_TO_MANY, UNORDERED, Requirement, "requirement", TestUnit, "Test Unit",CoreRelationTypes.Uses);
   RelationTypeSide UsesRel_Requirement = RelationTypeSide.create(UsesRel, SIDE_A);
   RelationTypeSide UsesRel_TestUnit = RelationTypeSide.create(UsesRel, SIDE_B);

   RelationTypeToken ValidationRel = osee.addNewRelationType(4400405161969600831L, "Validation", MANY_TO_MANY, UNORDERED, Requirement, "requirement", Artifact, "validator",CoreRelationTypes.Validation);
   RelationTypeSide ValidationRel_Requirement = RelationTypeSide.create(ValidationRel, SIDE_A);
   RelationTypeSide ValidationRel_Validator = RelationTypeSide.create(ValidationRel, SIDE_B);

   RelationTypeToken VerificationRel = osee.addNewRelationType(4400405161969600832L, "Verification", MANY_TO_MANY, UNORDERED, Requirement, "requirement", TestUnit, "verifier",CoreRelationTypes.Verification);
   RelationTypeSide VerificationRel_Requirement = RelationTypeSide.create(VerificationRel, SIDE_A);
   RelationTypeSide VerificationRel_Verifier = RelationTypeSide.create(VerificationRel, SIDE_B);

   RelationTypeToken VerificationPlanRel = osee.addNewRelationType(4400405161969600833L, "Verification Plan", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, Requirement, "requirement", TestPlanElementMsWord, "test plan element",CoreRelationTypes.VerificationPlan);
   RelationTypeSide VerificationPlanRel_Requirement = RelationTypeSide.create(VerificationPlanRel, SIDE_A);
   RelationTypeSide VerificationPlanRel_TestPlanElement = RelationTypeSide.create(VerificationPlanRel, SIDE_B);

   RelationTypeToken VerificationProcedureRel = osee.addNewRelationType(4400405161969600834L, "Verification Procedure", MANY_TO_MANY, UNORDERED, TestInformationSheetMsWord, "test information sheet", TestProcedure, "test procedure",CoreRelationTypes.VerificationProcedure);
   RelationTypeSide VerificationProcedureRel_TestInformationSheet = RelationTypeSide.create(VerificationProcedureRel, SIDE_A);
   RelationTypeSide VerificationProcedureRel_TestProcedure = RelationTypeSide.create(VerificationProcedureRel, SIDE_B);

   RelationTypeToken WorkItemRel = osee.addNewRelationType(4400405161969600835L, "Work Item", MANY_TO_MANY, RelationSorter.LEXICOGRAPHICAL_ASC, Artifact, "Parent", Artifact, "Child",CoreRelationTypes.WorkItem);
   RelationTypeSide WorkItemRel_Parent = RelationTypeSide.create(WorkItemRel, SIDE_A);
   RelationTypeSide WorkItemRel_Child = RelationTypeSide.create(WorkItemRel, SIDE_B);

   //@formatter:on
}