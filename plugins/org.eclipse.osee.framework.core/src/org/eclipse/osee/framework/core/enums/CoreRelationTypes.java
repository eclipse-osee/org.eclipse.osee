/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.CrossReference;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.DesignMsWord;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.ExecutedCommandHistory;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Feature;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GitCommit;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GitRepository;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GroupArtifact;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.HelpPage;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.ImplementationDetailsMsWord;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.InterfaceArtifact;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.InterfaceConnection;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.InterfaceDataElement;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.InterfaceEnum;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.InterfaceEnumSet;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.InterfaceMessage;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.InterfaceNode;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.InterfacePlatformType;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.InterfaceStructure;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.InterfaceSubMessage;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.MimUserGlobalPreferences;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Requirement;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.SafetyAssessment;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.SubsystemFunctionMsWord;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.SystemFunctionMsWord;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TestInformationSheetMsWord;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TestPlanElementMsWord;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TestProcedure;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TestUnit;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TransportType;
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

   // @Deprecated - Will be replaced by InterfaceConnectionNode
   @Deprecated RelationTypeToken InterfaceConnectionPrimary = osee.addNewRelationType(6039606571486514296L, "Interface Connection Primary Node", MANY_TO_ONE, RelationSorter.LEXICOGRAPHICAL_ASC, InterfaceConnection, "Interface Connection", InterfaceNode, "Interface Node");
   @Deprecated RelationTypeSide InterfaceConnectionPrimary_Connection = RelationTypeSide.create(InterfaceConnectionPrimary, SIDE_A);
   @Deprecated RelationTypeSide InterfaceConnectionPrimary_Node = RelationTypeSide.create(InterfaceConnectionPrimary, SIDE_B);

   @Deprecated RelationTypeToken InterfaceConnectionSecondary = osee.addNewRelationType(6039606571486514297L, "Interface Connection Secondary Node", MANY_TO_ONE, RelationSorter.LEXICOGRAPHICAL_ASC, InterfaceConnection, "Interface Connection", InterfaceNode, "Interface Node");
   @Deprecated RelationTypeSide InterfaceConnectionSecondary_Connection = RelationTypeSide.create(InterfaceConnectionSecondary, SIDE_A);
   @Deprecated RelationTypeSide InterfaceConnectionSecondary_Node = RelationTypeSide.create(InterfaceConnectionSecondary, SIDE_B);

   RelationTypeToken InterfaceConnectionNode = osee.addNewRelationType(6039606571486514300L, "Interface Connection Node", MANY_TO_MANY, RelationSorter.LEXICOGRAPHICAL_ASC, InterfaceConnection, "Interface Connection", InterfaceNode, "Interface Node");
   RelationTypeSide InterfaceConnectionNode_Connection = RelationTypeSide.create(InterfaceConnectionNode, SIDE_A);
   RelationTypeSide InterfaceConnectionNode_Node = RelationTypeSide.create(InterfaceConnectionNode, SIDE_B);

   RelationTypeToken InterfaceConnectionMessage = osee.addNewRelationType(6039606571486514298L, "Interface Connection Message", MANY_TO_MANY, RelationSorter.LEXICOGRAPHICAL_ASC, InterfaceConnection, "Interface Connection", InterfaceMessage, "Interface Message");
   RelationTypeSide InterfaceConnectionMessage_Connection = RelationTypeSide.create(InterfaceConnectionMessage, SIDE_A);
   RelationTypeSide InterfaceConnectionMessage_Message = RelationTypeSide.create(InterfaceConnectionMessage, SIDE_B);

   // @Deprecated - Will be replaced by InterfaceMessagePubNode and InterfaceMessageSubNode
   @Deprecated RelationTypeToken InterfaceMessageSendingNode = osee.addNewRelationType(6039606571486514299L, "Interface Message Sending Node", MANY_TO_ONE, RelationSorter.LEXICOGRAPHICAL_ASC, InterfaceMessage, "Interface Message", InterfaceNode, "Interface Node");
   @Deprecated RelationTypeSide InterfaceMessageSendingNode_Message = RelationTypeSide.create(InterfaceMessageSendingNode, SIDE_A);
   @Deprecated RelationTypeSide InterfaceMessageSendingNode_Node = RelationTypeSide.create(InterfaceMessageSendingNode, SIDE_B);

   RelationTypeToken InterfaceMessagePubNode = osee.addNewRelationType(6039606571486514301L, "Interface Message Publisher Node", MANY_TO_MANY, RelationSorter.LEXICOGRAPHICAL_ASC, InterfaceMessage, "Interface Message", InterfaceNode, "Interface Node");
   RelationTypeSide InterfaceMessagePubNode_Message = RelationTypeSide.create(InterfaceMessagePubNode, SIDE_A);
   RelationTypeSide InterfaceMessagePubNode_Node = RelationTypeSide.create(InterfaceMessagePubNode, SIDE_B);

   RelationTypeToken InterfaceMessageSubNode = osee.addNewRelationType(6039606571486514302L, "Interface Message Subscriber Node", MANY_TO_MANY, RelationSorter.LEXICOGRAPHICAL_ASC, InterfaceMessage, "Interface Message", InterfaceNode, "Interface Node");
   RelationTypeSide InterfaceMessageSubNode_Message = RelationTypeSide.create(InterfaceMessageSubNode, SIDE_A);
   RelationTypeSide InterfaceMessageSubNode_Node = RelationTypeSide.create(InterfaceMessageSubNode, SIDE_B);

   RelationTypeToken InterfaceConnectionHeaderStructure = osee.addNewRelationType(126164394421696912L, "Interface Connection Header Structure", MANY_TO_MANY, RelationSorter.USER_DEFINED, InterfaceConnection, "Interface Connection", InterfaceStructure, "Interface Structure");
   RelationTypeSide InterfaceConnectionHeaderStructure_Connection = RelationTypeSide.create(InterfaceConnectionHeaderStructure, SIDE_A);
   RelationTypeSide InterfaceConnectionHeaderStructure_Structure = RelationTypeSide.create(InterfaceConnectionHeaderStructure, SIDE_B);

   RelationTypeToken InterfaceMessageSubMessageContent = osee.addNewRelationType(2455059983007225780L, "Interface Message SubMessage Content", MANY_TO_MANY, RelationSorter.USER_DEFINED, InterfaceMessage, "Interface Message", InterfaceSubMessage, "Interface SubMessage");
   RelationTypeSide InterfaceMessageSubMessageContent_Message = RelationTypeSide.create(InterfaceMessageSubMessageContent, SIDE_A);
   RelationTypeSide InterfaceMessageSubMessageContent_SubMessage = RelationTypeSide.create(InterfaceMessageSubMessageContent, SIDE_B);

   RelationTypeToken InterfaceMessageStructureContent = osee.addNewRelationType(3899709087455064780L, "Interface Message Structure Content", MANY_TO_MANY, RelationSorter.USER_DEFINED, InterfaceMessage, "Interface Message", InterfaceStructure, "Interface Structure");
   RelationTypeSide InterfaceMessageStructureContent_Message = RelationTypeSide.create(InterfaceMessageStructureContent, SIDE_A);
   RelationTypeSide InterfaceMessageStructureContent_Structure = RelationTypeSide.create(InterfaceMessageStructureContent, SIDE_B);

   RelationTypeToken InterfaceSubMessageContent = osee.addNewRelationType(126164394421696914L, "Interface SubMessage Content", MANY_TO_MANY, RelationSorter.USER_DEFINED, InterfaceSubMessage, "Interface SubMessage", InterfaceStructure, "Interface Structure");
   RelationTypeSide InterfaceSubMessageContent_SubMessage = RelationTypeSide.create(InterfaceSubMessageContent, SIDE_A);
   RelationTypeSide InterfaceSubMessageContent_Structure = RelationTypeSide.create(InterfaceSubMessageContent, SIDE_B);

   RelationTypeToken InterfaceStructureContent = osee.addNewRelationType(2455059983007225781L, "Interface Structure Content", MANY_TO_MANY, RelationSorter.USER_DEFINED, InterfaceStructure, "Interface Structure", InterfaceDataElement, "Interface Data Element");
   RelationTypeSide InterfaceStructureContent_Structure = RelationTypeSide.create(InterfaceStructureContent, SIDE_A);
   RelationTypeSide InterfaceStructureContent_DataElement = RelationTypeSide.create(InterfaceStructureContent, SIDE_B);

   RelationTypeToken InterfaceElementPlatformType = osee.addNewRelationType(3899709087455064781L, "Interface Element Platform Type", MANY_TO_ONE, RelationSorter.LEXICOGRAPHICAL_ASC, InterfaceDataElement, "Interface Data Element", InterfacePlatformType, "Interface Platform Type");
   RelationTypeSide InterfaceElementPlatformType_Element = RelationTypeSide.create(InterfaceElementPlatformType, SIDE_A);
   RelationTypeSide InterfaceElementPlatformType_PlatformType = RelationTypeSide.create(InterfaceElementPlatformType, SIDE_B);

   RelationTypeToken InterfacePlatformTypeEnumeration = osee.addNewRelationType(2455059983007225794L, "Interface Platform Type Enumeration Set", MANY_TO_ONE, RelationSorter.USER_DEFINED, InterfacePlatformType, "Interface Platform Type", InterfaceEnumSet, "Interface Enumeration Set");
   RelationTypeSide InterfacePlatformTypeEnumeration_Element = RelationTypeSide.create(InterfacePlatformTypeEnumeration, SIDE_A);
   RelationTypeSide InterfacePlatformTypeEnumeration_EnumerationSet = RelationTypeSide.create(InterfacePlatformTypeEnumeration, SIDE_B);

   RelationTypeToken InterfaceEnumeration = osee.addNewRelationType(2455059983007225795L, "Interface Enumeration Definition", MANY_TO_MANY, RelationSorter.USER_DEFINED, InterfaceEnumSet, "Interface Enumeration Set", InterfaceEnum, "Interface Enumeration");
   RelationTypeSide InterfaceEnumeration_EnumerationSet = RelationTypeSide.create(InterfaceEnumeration, SIDE_A);
   RelationTypeSide InterfaceEnumeration_EnumerationState = RelationTypeSide.create(InterfaceEnumeration, SIDE_B);

   RelationTypeToken TransportTypeHeader = osee.addNewRelationType(8734224778892840579L, "Transport Type Header", ONE_TO_MANY, RelationSorter.LEXICOGRAPHICAL_ASC, TransportType, "Transport Type", InterfaceArtifact, "Interface Artifact");
   RelationTypeSide InterfaceTransportTypeHeader_TransportType = RelationTypeSide.create(TransportTypeHeader, SIDE_A);
   RelationTypeSide InterfaceTransportTypeHeader_Artifact = RelationTypeSide.create(TransportTypeHeader, SIDE_B);

   RelationTypeToken InterfaceConnectionTransportType = osee.addNewRelationType(1859749228181133209L, "Interface Connection Transport Type", MANY_TO_ONE, RelationSorter.LEXICOGRAPHICAL_ASC,InterfaceConnection, "Interface Connection", TransportType, "Transport Type");
   RelationTypeSide InterfaceConnectionTransportType_InterfaceConnection = RelationTypeSide.create(InterfaceConnectionTransportType, SIDE_A);
   RelationTypeSide InterfaceConnectionTransportType_TransportType = RelationTypeSide.create(InterfaceConnectionTransportType, SIDE_B);

   RelationTypeToken InterfaceConnectionCrossReference = osee.addNewRelationType(2283114833979032380L, "Interface Connection Cross Reference", MANY_TO_MANY, RelationSorter.LEXICOGRAPHICAL_ASC,InterfaceConnection, "Interface Connection", CrossReference, "Cross Reference");
   RelationTypeSide InterfaceConnectionCrossReference_InterfaceConnection = RelationTypeSide.create(InterfaceConnectionCrossReference, SIDE_A);
   RelationTypeSide InterfaceConnectionCrossReference_CrossReference = RelationTypeSide.create(InterfaceConnectionCrossReference, SIDE_B);

   RelationTypeToken RequirementsToInterface = osee.addNewRelationType(9109538440842134345L, "Requirements to Interface", MANY_TO_MANY, RelationSorter.LEXICOGRAPHICAL_ASC, Artifact, "Requirement Artifact", InterfaceArtifact, "Artifact");
   RelationTypeSide RequirementsToInterface_Artifact = RelationTypeSide.create(RequirementsToInterface, SIDE_A);
   RelationTypeSide RequirementsToInterface_InterfaceArtifact = RelationTypeSide.create(RequirementsToInterface, SIDE_B);

   RelationTypeToken HelpToHelp = osee.addNewRelationType(8012520859985251534L, "Help Page to Help Page Child", MANY_TO_MANY, RelationSorter.LEXICOGRAPHICAL_ASC, HelpPage, "Help Page", HelpPage, "Help Page Child");
   RelationTypeSide HelpToHelp_Help = RelationTypeSide.create(HelpToHelp, SIDE_A);
   RelationTypeSide HelpToHelp_Child = RelationTypeSide.create(HelpToHelp, SIDE_B);

   RelationTypeToken PlConfigurationGroup = osee.add(674505523757332017L, "Product Line Configuration Group", MANY_TO_MANY, RelationSorter.LEXICOGRAPHICAL_ASC, GroupArtifact, "Group", BranchView, "BranchView");
   RelationTypeSide PlConfigurationGroup_Group = RelationTypeSide.create(PlConfigurationGroup, SIDE_A);
   RelationTypeSide PlConfigurationGroup_BranchView = RelationTypeSide.create(PlConfigurationGroup, SIDE_B);

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

   RelationTypeToken Users = osee.add(2305843009213694308L, "Users", MANY_TO_MANY, UNORDERED, Artifact, "Artifact", User, "User");
   RelationTypeSide Users_Artifact = RelationTypeSide.create(Users, SIDE_A);
   RelationTypeSide Users_User = RelationTypeSide.create(Users, SIDE_B);

   RelationTypeToken UserToContext = osee.add(3588536741885708579L, "User to Context", MANY_TO_MANY, UNORDERED, User, "User", Context, "Context");
   RelationTypeSide UserToContext_User = RelationTypeSide.create(UserToContext, SIDE_A);
   RelationTypeSide UserToContext_Context = RelationTypeSide.create(UserToContext, SIDE_B);

   RelationTypeToken UserGroupToContext = osee.add(6518538741815208374L, "User Group to Context", MANY_TO_MANY, UNORDERED, UserGroup, "User Group", Context, "Context");
   RelationTypeSide UserGroupToContext_UserGroup = RelationTypeSide.create(UserGroupToContext, SIDE_A);
   RelationTypeSide UserGroupToContext_Context = RelationTypeSide.create(UserGroupToContext, SIDE_B);

   RelationTypeToken UserMimGlobalPreferences = osee.addNewRelationType(2600664754080134468L, "User to MIM User Global Preferences", ONE_TO_ONE, RelationSorter.LEXICOGRAPHICAL_ASC, User, "User", MimUserGlobalPreferences, "MIM User Global Preferences");
   RelationTypeSide UserMimGlobalPreferences_User = RelationTypeSide.create(UserMimGlobalPreferences, SIDE_A);
   RelationTypeSide UserMimGlobalPreferences_MimGlobalPreferences = RelationTypeSide.create(UserMimGlobalPreferences, SIDE_B);


   RelationTypeToken UserToHistory = osee.add(6360156234301395903L, "User to History", ONE_TO_ONE, UNORDERED, User, "User", ExecutedCommandHistory, "Executed Command History");
   RelationTypeSide UserToHistory_User = RelationTypeSide.create(UserToHistory, SIDE_A);
   RelationTypeSide UserToHistory_ExecutedCommandHistory = RelationTypeSide.create(UserToHistory, SIDE_B);

   RelationTypeToken ContextToCommand = osee.add(3568736811283748971L, "Context to Command", MANY_TO_MANY, UNORDERED, Context, "Context", Artifact, "Context or Command");
   RelationTypeSide ContextToCommand_Context = RelationTypeSide.create(ContextToCommand, SIDE_A);
   RelationTypeSide ContextToCommand_Artifact = RelationTypeSide.create(ContextToCommand, SIDE_B);

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