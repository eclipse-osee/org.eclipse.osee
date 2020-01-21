/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.*;
import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.osee;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Ryan D. Brooks
 */
public interface CoreArtifactTypes {

   // @formatter:off
   ArtifactTypeToken Artifact = osee.add(osee.artifactType(1L, "Artifact", false)
      .any(Annotation, "")
      .zeroOrOne(ContentUrl, "")
      .zeroOrOne(Description, "")
      .exactlyOne(Name, "unnamed")
      .zeroOrOne(RelationOrder, "")
      .any(StaticId, ""));
   ArtifactTypeToken AbstractAccessControlled = osee.add(osee.artifactType(17L, "Abstract Access Controlled", true, Artifact)
      .any(AccessContextId, ""));
   ArtifactTypeToken AbstractHeading = osee.add(osee.artifactType(805L, "Abstract Heading", true, Artifact)
      .zeroOrOne(DoorsHierarchy, "")
      .any(DoorsId, "")
      .any(DoorsModId, ""));
   ArtifactTypeToken AbstractImplementationDetails = osee.add(osee.artifactType(921211884L, "Abstract Implementation Details", true, Artifact)
      .zeroOrOne(DataRightsBasis, "")
      .zeroOrOne(DataRightsClassification, "Unspecified", 3458764513820541445L)
      .atLeastOne(Partition, "Unspecified", 3458764513820541309L)
      .zeroOrOne(PotentialSecurityImpact, "false")
      .zeroOrOne(SubjectMatterExpert, "")
      .exactlyOne(Subsystem, "Unspecified", 3458764513820541310L));
   ArtifactTypeToken AbstractTestResult = osee.add(osee.artifactType(38L, "Abstract Test Result", true, Artifact));
   ArtifactTypeToken BranchView = osee.add(osee.artifactType(5849078277209560034L, "Branch View", false, Artifact));
   ArtifactTypeToken Breaker = osee.add(osee.artifactType(188458869981236L, "Breaker", false, Artifact)
      .exactlyOne(CircuitBreakerId, "0")
      .exactlyOne(DisplayText, "unset")
      .zeroOrOne(FunctionalGrouping, "", 3458969036723847402L)
      .exactlyOne(MaintainerText, "unset")
      .exactlyOne(RequireConfirmation, "false"));
   ArtifactTypeToken CertificationBaselineEvent = osee.add(osee.artifactType(99L, "Certification Baseline Event", false, Artifact)
      .zeroOrOne(BaselinedBy, "")
      .zeroOrOne(BaselinedTimestamp, "")
      .zeroOrOne(GitChangeId, "")
      .zeroOrOne(ReviewId, "")
      .zeroOrOne(ReviewStoryId, ""));
   ArtifactTypeToken CodeUnit = osee.add(osee.artifactType(58L, "Code Unit", false, Artifact)
      .zeroOrOne(DataRightsBasis, "")
      .zeroOrOne(DataRightsClassification, "Unspecified", 3458764513820541445L)
      .zeroOrOne(SubjectMatterExpert, ""));
   ArtifactTypeToken Component = osee.add(osee.artifactType(57L, "Component", false, Artifact)
      .exactlyOne(Developmental, "true")
      .exactlyOne(GfeCfe, "Unspecified", 3458764513820541321L)
      .exactlyOne(IDAL, "Unspecified", 3458764513820541316L));
   ArtifactTypeToken EnumeratedArtifact = osee.add(osee.artifactType(4619295485563766003L, "Enumerated Artifact", false, Artifact));
   ArtifactTypeToken Feature = osee.add(osee.artifactType(87L, "Feature", false, Artifact)
      .zeroOrOne(DefaultValue, "")
      .exactlyOne(FeatureMultivalued, "false")
      .exactlyOne(FeatureValueType, "String", 981613428814575859L)
      .any(Value, ""));
   ArtifactTypeToken Folder = osee.add(osee.artifactType(11L, "Folder", false, Artifact));
   ArtifactTypeToken GeneralData = osee.add(osee.artifactType(12L, "General Data", false, Artifact)
      .any(GeneralStringData, "")
      .zeroOrOne(PublishInline, ""));
   ArtifactTypeToken AccessControlModel = osee.add(osee.artifactType(2L, "Access Control Model", false, GeneralData));
   ArtifactTypeToken FeatureDefinition = osee.add(osee.artifactType(5849078290088170402L, "Feature Definition", false, GeneralData));
   ArtifactTypeToken GitCommit = osee.add(osee.artifactType(100L, "Git Commit", false, Artifact)
      .zeroOrOne(GitChangeId, "")
      .zeroOrOne(GitCommitAuthorDate, "")
      .zeroOrOne(GitCommitMessage, "")
      .zeroOrOne(GitCommitSha, "")
      .zeroOrOne(UserArtifactId, ""));
   ArtifactTypeToken GitRepository = osee.add(osee.artifactType(97L, "Git Repository", false, Artifact)
      .zeroOrOne(DefaultTrackingBranch, "")
      .any(ExcludePath, "")
      .zeroOrOne(FileSystemPath, "")
      .zeroOrOne(RepositoryUrl, ""));
   ArtifactTypeToken GlobalPreferences = osee.add(osee.artifactType(3L, "Global Preferences", false, Artifact)
      .zeroOrOne(DefaultMailServer, "")
      .zeroOrOne(Dictionary, "")
      .any(GeneralStringData, "")
      .zeroOrOne(WebPreferences, ""));
   ArtifactTypeToken GroupArtifact = osee.add(osee.artifactType(6L, "Group Artifact", false, Artifact));
   ArtifactTypeToken HtmlArtifact = osee.add(osee.artifactType(798L, "HTML Artifact", false, Artifact)
      .any(HtmlContent, "")
      .any(ImageContent, "")
      .zeroOrOne(ParagraphNumber, "")
      .zeroOrOne(PublishInline, ""));
   ArtifactTypeToken HeadingHtml = osee.add(osee.artifactType(804L, "Heading - HTML", false, AbstractHeading, HtmlArtifact));
   ArtifactTypeToken ImageArtifact = osee.add(osee.artifactType(800L, "Image Artifact", false, Artifact)
      .any(ImageContent, "")
      .zeroOrOne(ParagraphNumber, ""));
   ArtifactTypeToken ModelDiagram = osee.add(osee.artifactType(98L, "Model Diagram", false, Artifact)
      .exactlyOne(GraphitiDiagram, "<?xml version= \"1.0\" encoding= \"ASCII\"?><pi:Diagram xmi:version= \"2.0\" xmlns:xmi= \"http://www.omg.org/XMI\" xmlns:xsi= \"http://www.w3.org/2001/XMLSchema-instance\" xmlns:al= \"http://eclipse.org/graphiti/mm/algorithms\" xmlns:pi= \"http://eclipse.org/graphiti/mm/pictograms\" visible= \"true\" gridUnit= \"10\" diagramTypeId= \"mbse\" name= \"mbse basic\" snapToGrid= \"true\" version= \"0.11.0\"><graphicsAlgorithm xsi:type= \"al:Rectangle\" background= \"//@colors.1\" foreground= \"//@colors.0\" lineWidth= \"1\" transparency= \"0.0\" width= \"1000\" height= \"1000\"/><colors red= \"227\" green= \"238\" blue= \"249\"/><colors red= \"255\" green= \"255\" blue= \"255\"/></pi:Diagram>"));
   ArtifactTypeToken MsWord = osee.add(osee.artifactType(16L, "MS Word", true, Artifact).zeroOrOne(ParagraphNumber, ""));
   ArtifactTypeToken MsWordTemplate = osee.add(osee.artifactType(19L, "MS Word Template", false, MsWord)
      .zeroOrOne(PageOrientation, "Portrait", 3458764513820541305L)
      .zeroOrOne(PublishInline, "")
      .zeroOrOne(WordOleData, "")
      .zeroOrOne(WordTemplateContent, "<w:p xmlns:w= \"http://schemas.microsoft.com/office/word/2003/wordml\"><w:r><w:t></w:t></w:r></w:p>"));
   ArtifactTypeToken DesignMsWord = osee.add(osee.artifactType(346L, "Design - MS Word", false, MsWordTemplate)
      .zeroOrOne(DataRightsBasis, "")
      .zeroOrOne(DataRightsClassification, "Unspecified", 3458764513820541445L)
      .zeroOrOne(DoorsHierarchy, "")
      .any(DoorsId, "")
      .any(DoorsModId, "")
      .zeroOrOne(IaPlan, "false")
      .zeroOrOne(LegacyDal, "Unspecified",3458764513820541316L)
      .zeroOrOne(PotentialSecurityImpact, "false")
      .exactlyOne(SeverityCategory, "Unspecified", 3458764513820541312L)
      .zeroOrOne(SubjectMatterExpert, ""));
   ArtifactTypeToken DesignDescriptionMsWord = osee.add(osee.artifactType(810L, "Design Description - MS Word", false, MsWordTemplate, AbstractHeading));
   ArtifactTypeToken DocumentDescriptionMsWord = osee.add(osee.artifactType(806L, "Document Description - MS Word", false, MsWordTemplate, AbstractHeading));
   ArtifactTypeToken FunctionMsWord = osee.add(osee.artifactType(34L, "Function - MS Word", true, MsWordTemplate)
      .exactlyOne(FDAL, "Unspecified", 3458764513820541316L)
      .zeroOrOne(FdalRationale, "")
      .exactlyOne(SeverityCategory, "Unspecified",3458764513820541312L)
      .zeroOrOne(SoftwareSafetyImpact, ""));
   ArtifactTypeToken HeadingMsWord = osee.add(osee.artifactType(56L, "Heading - MS Word", false, MsWordTemplate, AbstractHeading)
      .zeroOrOne(DataRightsBasis, "")
      .zeroOrOne(DataRightsClassification, "Unspecified", 3458764513820541445L));
   ArtifactTypeToken ImplementationDetailsMsWord = osee.add(osee.artifactType(26L, "Implementation Details - MS Word", false, MsWordTemplate, AbstractImplementationDetails));
   ArtifactTypeToken ImplementationDetailsDataDefinitionMsWord = osee.add(osee.artifactType(279578L, "Implementation Details Data Definition - MS Word", false, ImplementationDetailsMsWord));
   ArtifactTypeToken ImplementationDetailsDrawingMsWord = osee.add(osee.artifactType(209690L, "Implementation Details Drawing - MS Word", false, ImplementationDetailsMsWord));
   ArtifactTypeToken ImplementationDetailsFunctionMsWord = osee.add(osee.artifactType(139802L, "Implementation Details Function - MS Word", false, ImplementationDetailsMsWord));
   ArtifactTypeToken ImplementationDetailsProcedureMsWord = osee.add(osee.artifactType(69914L, "Implementation Details Procedure - MS Word", false, ImplementationDetailsMsWord));
   ArtifactTypeToken MsWordWholeDocument = osee.add(osee.artifactType(18L, "MS Word Whole Document", false, MsWord)
      .zeroOrOne(WholeWordContent, "<?xml version= '1.0' encoding= 'UTF-8' standalone= 'yes'?><?mso-application progid= 'Word.Document'?><w:wordDocument xmlns:w= 'http://schemas.microsoft.com/office/word/2003/wordml' xmlns:v= 'urn:schemas-microsoft-com:vml' xmlns:w10= 'urn:schemas-microsoft-com:office:word' xmlns:sl= 'http://schemas.microsoft.com/schemaLibrary/2003/core' xmlns:aml= 'http://schemas.microsoft.com/aml/2001/core' xmlns:wx= 'http://schemas.microsoft.com/office/word/2003/auxHint' xmlns:o= 'urn:schemas-microsoft-com:office:office' xmlns:dt= 'uuid:C2F41010-65B3-11d1-A29F-00AA00C14882' xmlns:wsp= 'http://schemas.microsoft.com/office/word/2003/wordml/sp2' xmlns:ns0= 'http://www.w3.org/2001/XMLSchema' xmlns:ns1= 'http://eclipse.org/artifact.xsd' xmlns:st1= 'urn:schemas-microsoft-com:office:smarttags' w:macrosPresent= 'no' w:embeddedObjPresent= 'no' w:ocxPresent= 'no' xml:space= 'preserve'><w:body></w:body></w:wordDocument>"));
   ArtifactTypeToken MsWordStyles = osee.add(osee.artifactType(2578L, "MS Word Styles", false, MsWordWholeDocument));
   ArtifactTypeToken NativeArtifact = osee.add(osee.artifactType(20L, "Native Artifact", true, Artifact)
      .zeroOrOne(Extension, "")
      .zeroOrOne(NativeContent, ""));
   ArtifactTypeToken GeneralDocument = osee.add(osee.artifactType(14L, "General Document", false, NativeArtifact));
   ArtifactTypeToken OseeApp = osee.add(osee.artifactType(89L, "OSEE App", false, Artifact).zeroOrOne(OseeAppDefinition, ""));
   ArtifactTypeToken OseeTypeDefinition = osee.add(osee.artifactType(60L, "Osee Type Definition", false, Artifact)
      .exactlyOne(Active, "true")
      .any(UriGeneralStringData, ""));
   ArtifactTypeToken OseeTypeEnum = osee.add(osee.artifactType(5447805027409642344L, "Osee Type Enum", false, EnumeratedArtifact)
      .any(IdValue, ""));
   ArtifactTypeToken PlainText = osee.add(osee.artifactType(784L, "Plain Text", false, Artifact)
      .zeroOrOne(ParagraphNumber, "")
      .zeroOrOne(PlainTextContent, ""));
   ArtifactTypeToken AcronymPlainText = osee.add(osee.artifactType(5034328852220100337L, "Acronym Plain Text", false, PlainText));
   ArtifactTypeToken ImplementationDetailsPlainText = osee.add(osee.artifactType(638269899L, "Implementation Details Plain Text", false, PlainText, AbstractImplementationDetails));
   ArtifactTypeToken PlainTextDataRights = osee.add(osee.artifactType(4527862492986312222L, "Plain Text With Data Rights", false, PlainText)
      .zeroOrOne(DataRightsBasis, "")
      .zeroOrOne(DataRightsClassification, "Unspecified")
      .zeroOrOne(DoorsHierarchy, "")
      .any(DoorsId, "")
      .zeroOrOne(PageOrientation, "Portrait"));
   ArtifactTypeToken ReferenceDocument = osee.add(osee.artifactType(2084059074565751746L, "Reference Document", false, GeneralDocument)
      .any(DoorsId, ""));
   ArtifactTypeToken RendererTemplateWholeWord = osee.add(osee.artifactType(9L, "Renderer Template - Whole Word", false, MsWordWholeDocument)
      .exactlyOne(RendererOptions, "{\"ElementType\" : \"Artifact\", \"OutliningOptions\" : [ {\"Outlining\" : true, \"RecurseChildren\" : false, \"HeadingAttributeType\" : \"Name\", \"ArtifactName\" : \"Default\", \"OutlineNumber\" : \"\" }], \"AttributeOptions\" : [{\"AttrType\" : \"*\",  \"Label\" : \"\", \"FormatPre\" : \"\", \"FormatPost\" : \"\"}]}")
      .any(TemplateMatchCriteria, ""));
   ArtifactTypeToken Requirement = osee.add(osee.artifactType(21L, "Requirement", false, Artifact)
      .zeroOrOne(DataRightsBasis, "")
      .zeroOrOne(DataRightsClassification, "Unspecified", 3458764513820541445L)
      .zeroOrOne(SubjectMatterExpert, ""));
   ArtifactTypeToken AbstractSpecRequirement = osee.add(osee.artifactType(58551193202327573L, "Abstract Spec Requirement", false, Requirement)
      .zeroOrOne(DoorsHierarchy, "")
      .any(DoorsId, "")
      .any(DoorsModId, "")
      .zeroOrOne(LegacyDal, "Unspecified", 3458764513820541316L)
      .zeroOrOne(LegacyId, "")
      .zeroOrOne(PotentialSecurityImpact, "false")
      .atLeastOne(QualificationMethod, "Unspecified", 3458764513820541311L)
      .zeroOrOne(SafetyImpact, "")
      .zeroOrOne(SafetySeverity, "Unspecified", 8602198000748353178L)
      .zeroOrOne(SoftwareControlCategory, "Unspecified", 3766269001007852409L)
      .zeroOrOne(SoftwareControlCategoryRationale, "")
      .exactlyOne(Subsystem, "Unspecified", 3458764513820541310L)
      .exactlyOne(TechnicalPerformanceParameter, "false"));
   ArtifactTypeToken AbstractSoftwareRequirement = osee.add(osee.artifactType(23L, "Abstract Software Requirement", true, AbstractSpecRequirement)
      .atLeastOne(CSCI, "Unspecified", 3458764513820541319L)
      .zeroOrOne(Category, "")
      .atLeastOne(Partition, "Unspecified", 3458764513820541309L));
   ArtifactTypeToken AbstractSubsystemRequirement = osee.add(osee.artifactType(797L, "Abstract Subsystem Requirement", true, AbstractSpecRequirement)
      .zeroOrOne(Effectivity, "Unspecified")
      .exactlyOne(IDAL, "Unspecified", 3458764513820541316L)
      .zeroOrOne(IdalRationale, "")
      .zeroOrOne(LegacyId, "")
      .atLeastOne(VerificationEvent, "Unspecified", 3458764513820541317L)
      .zeroOrOne(VerificationLevel, "Unspecified", 3458764513820541313L));
   ArtifactTypeToken AbstractSystemRequirement = osee.add(osee.artifactType(796L, "Abstract System Requirement", true, AbstractSpecRequirement)
      .zeroOrOne(Effectivity, "Unspecified")
      .zeroOrOne(IaPlan, "false")
      .zeroOrOne(LegacyId, "")
      .zeroOrOne(VerificationAcceptanceCriteria, "")
      .atLeastOne(VerificationEvent, "Unspecified", 3458764513820541317L)
      .zeroOrOne(VerificationLevel, "Unspecified", 3458764513820541313L));
   ArtifactTypeToken CustomerRequirementMsWord = osee.add(osee.artifactType(809L, "Customer Requirement - MS Word", false, MsWordTemplate, AbstractSpecRequirement));
   ArtifactTypeToken DirectSoftwareRequirement = osee.add(osee.artifactType(22L, "Direct Software Requirement", true, AbstractSoftwareRequirement));
   ArtifactTypeToken HardwareRequirementMsWord = osee.add(osee.artifactType(33L, "Hardware Requirement - MS Word", false, MsWordTemplate, AbstractSpecRequirement));
   ArtifactTypeToken IndirectSoftwareRequirementMsWord = osee.add(osee.artifactType(25L, "Indirect Software Requirement - MS Word", false, MsWordTemplate, AbstractSoftwareRequirement));
   ArtifactTypeToken InterfaceRequirementMsWord = osee.add(osee.artifactType(32L, "Interface Requirement - MS Word", false, MsWordTemplate, AbstractSpecRequirement)
      .exactlyOne(CoreAttributeTypes.Component, "Unspecified", 3458764513820541318L));
   ArtifactTypeToken RootArtifact = osee.add(osee.artifactType(10L, "Root Artifact", false, Artifact));
   ArtifactTypeToken SafetyAssessment = osee.add(osee.artifactType(59L, "Safety Assessment", false, Artifact)
      .zeroOrOne(ParagraphNumber, "")
      .zeroOrOne(SFHA, ""));
   ArtifactTypeToken SoftwareDesignMsWord = osee.add(osee.artifactType(45L, "Software Design - MS Word", false, DesignMsWord));
   ArtifactTypeToken SoftwareRequirementMsWord = osee.add(osee.artifactType(24L, "Software Requirement - MS Word", false, MsWordTemplate, DirectSoftwareRequirement)
      .exactlyOne(IDAL, "Unspecified", 3458764513820541316L)
      .zeroOrOne(IaPlan, "false")
      .zeroOrOne(IdalRationale, "")
      .zeroOrOne(SoftwareControlCategory, "Unspecified", 3766269001007852409L)
      .zeroOrOne(SoftwareControlCategoryRationale, ""));
   ArtifactTypeToken SoftwareRequirementDataDefinitionMsWord = osee.add(osee.artifactType(793L, "Software Requirement Data Definition - MS Word", false, IndirectSoftwareRequirementMsWord));
   ArtifactTypeToken SoftwareRequirementDrawingMsWord = osee.add(osee.artifactType(29L, "Software Requirement Drawing - MS Word", false, IndirectSoftwareRequirementMsWord));
   ArtifactTypeToken SoftwareRequirementFunctionMsWord = osee.add(osee.artifactType(28L, "Software Requirement Function - MS Word", false, IndirectSoftwareRequirementMsWord));
   ArtifactTypeToken SoftwareRequirementHtml = osee.add(osee.artifactType(42L, "Software Requirement - HTML", false, AbstractSoftwareRequirement, HtmlArtifact));
   ArtifactTypeToken SoftwareRequirementPlainText = osee.add(osee.artifactType(792L, "Software Requirement Plain Text", false, PlainText, DirectSoftwareRequirement));
   ArtifactTypeToken SoftwareRequirementProcedureMsWord = osee.add(osee.artifactType(27L, "Software Requirement Procedure - MS Word", false, IndirectSoftwareRequirementMsWord));
   ArtifactTypeToken SubsystemDesignMsWord = osee.add(osee.artifactType(43L, "Subsystem Design - MS Word", false, DesignMsWord)
      .exactlyOne(Subsystem, "Unspecified", 3458764513820541310L));
   ArtifactTypeToken SubsystemFunctionMsWord = osee.add(osee.artifactType(36L, "Subsystem Function - MS Word", false, FunctionMsWord, SubsystemDesignMsWord));
   ArtifactTypeToken SubsystemRequirementHtml = osee.add(osee.artifactType(795L, "Subsystem Requirement - HTML", false, AbstractSubsystemRequirement, HtmlArtifact));
   ArtifactTypeToken SubsystemRequirementMsWord = osee.add(osee.artifactType(31L, "Subsystem Requirement - MS Word", false, MsWordTemplate, AbstractSubsystemRequirement));
   ArtifactTypeToken SupportDocumentMsWord = osee.add(osee.artifactType(13L, "Support Document - MS Word", false, MsWordTemplate)
      .zeroOrOne(DataRightsBasis, "")
      .zeroOrOne(DataRightsClassification, "Unspecified", 3458764513820541445L));
   ArtifactTypeToken SystemDesignMsWord = osee.add(osee.artifactType(44L, "System Design - MS Word", false, DesignMsWord));
   ArtifactTypeToken SystemFunctionMsWord = osee.add(osee.artifactType(35L, "System Function - MS Word", false, FunctionMsWord, SystemDesignMsWord).zeroOrOne(FunctionalCategory, ""));
   ArtifactTypeToken SystemRequirementHtml = osee.add(osee.artifactType(794L, "System Requirement - HTML", false, AbstractSystemRequirement, HtmlArtifact));
   ArtifactTypeToken SystemRequirementMsWord = osee.add(osee.artifactType(30L, "System Requirement - MS Word", false, MsWordTemplate, AbstractSystemRequirement));
   ArtifactTypeToken TestPlanElementMsWord = osee.add(osee.artifactType(37L, "Test Plan Element - MS Word", false, MsWordTemplate));
   ArtifactTypeToken TestResultNative = osee.add(osee.artifactType(39L, "Test Result Native", false, NativeArtifact, AbstractTestResult));
   ArtifactTypeToken TestResultWholeWord = osee.add(osee.artifactType(40L, "Test Result - Whole Word", false, MsWordWholeDocument, AbstractTestResult));
   ArtifactTypeToken TestRun = osee.add(osee.artifactType(85L, "Test Run", false, AbstractTestResult)
      .zeroOrOne(Extension, "")
      .zeroOrOne(TestScriptGuid, "")
      .zeroOrOne(UserId, ""));
   ArtifactTypeToken TestRunDisposition = osee.add(osee.artifactType(84L, "Test Run Disposition", false, Artifact));
   ArtifactTypeToken TestUnit = osee.add(osee.artifactType(4L, "Test Unit", true, Artifact));
   ArtifactTypeToken IntegrationTestProcedureWholeWord = osee.add(osee.artifactType(443398723457743216L, "Integration Test Procedure - Whole Word", false, MsWordWholeDocument, TestUnit)
      .zeroOrOne(DoorsHierarchy, "")
      .any(DoorsId, ""));
   ArtifactTypeToken SoftwareTestProcedurePlainText = osee.add(osee.artifactType(564397212436322878L, "Software Test Procedure Plain Text", false, PlainText, TestUnit)
      .zeroOrOne(DoorsHierarchy, "")
      .any(DoorsId, ""));
   ArtifactTypeToken TestCase = osee.add(osee.artifactType(82L, "Test Case", false, TestUnit));
   ArtifactTypeToken TestInformationSheetMsWord = osee.add(osee.artifactType(41L, "Test Information Sheet - MS Word", false, MsWordTemplate, TestUnit)
      .exactlyOne(TisTestCategory, "DEV", 3458764513820541315L)
      .zeroOrOne(TisTestNumber, "")
      .atLeastOne(TisTestType, "Stationary Vehicle", 3458764513820541314L));
   ArtifactTypeToken TestProcedure = osee.add(osee.artifactType(46L, "Test Procedure", false, TestUnit)
      .exactlyOne(Subsystem, "Unspecified", 3458764513820541310L)
      .zeroOrOne(TestProcedureStatus, "", 3458764513820541304L));
   ArtifactTypeToken TestProcedureMsWord = osee.add(osee.artifactType(2349L, "Test Procedure - MS Word", false, MsWordTemplate, TestProcedure));
   ArtifactTypeToken TestProcedureNative = osee.add(osee.artifactType(48L, "Test Procedure Native", false, NativeArtifact, TestProcedure));
   ArtifactTypeToken TestProcedureWholeWord = osee.add(osee.artifactType(47L, "Test Procedure - Whole Word", false, MsWordWholeDocument, TestProcedure));
   ArtifactTypeToken TestSupport = osee.add(osee.artifactType(83L, "Test Support", false, TestUnit));
   ArtifactTypeToken UniversalGroup = osee.add(osee.artifactType(8L, "Universal Group", false, GroupArtifact));
   ArtifactTypeToken Url = osee.add(osee.artifactType(15L, "Url", false, Artifact));
   ArtifactTypeToken SupportingContent = osee.add(osee.artifactType(49L, "Supporting Content", false, Url));
   ArtifactTypeToken User = osee.add(osee.artifactType(5L, "User", false, Artifact)
      .exactlyOne(Active, "true")
      .zeroOrOne(City, "")
      .zeroOrOne(Company, "")
      .zeroOrOne(CompanyTitle, "")
      .zeroOrOne(Country, "")
      .zeroOrOne(Dictionary, "")
      .zeroOrOne(Email, "")
      .any(FavoriteBranch, "")
      .zeroOrOne(FaxPhone, "")
      .zeroOrOne(MobilePhone, "")
      .any(Notes, "")
      .zeroOrOne(Phone, "")
      .zeroOrOne(State, "")
      .zeroOrOne(Street, "")
      .zeroOrOne(UserId, "")
      .zeroOrOne(UserSettings, "")
      .zeroOrOne(WebPreferences, "")
      .zeroOrOne(Website, "")
      .any(XViewerCustomization, "")
      .any(XViewerDefaults, "")
      .zeroOrOne(Zip, ""));
   ArtifactTypeToken UserGroup = osee.add(osee.artifactType(7L, "User Group", false, AbstractAccessControlled, GroupArtifact)
      .zeroOrOne(DefaultGroup, "false"));
   ArtifactTypeToken SoftwareTestProcedureMsWord = osee.add(osee.artifactType(554486323432951757L, "Software Test Procedure - MS Word", false, MsWordTemplate, TestUnit)
      .any(DoorsId, "")
      .zeroOrOne(DoorsHierarchy, ""));
   ArtifactTypeToken SoftwareTestProcedureWholeWord = osee.add(osee.artifactType(554486323432951758L, "Software Test Procedure - Whole Word", false, MsWordWholeDocument, TestUnit)
      .any(DoorsId, "")
      .zeroOrOne(DoorsHierarchy, ""));
   ArtifactTypeToken SubscriptionGroup = osee.add(osee.artifactType(6753071794573299176L, "Subscription Group", false, UserGroup));
   ArtifactTypeToken WorkItemDefinition = osee.add(osee.artifactType(50L, "Work Item Definition", true, Artifact)
      .any(WorkData, "")
      .zeroOrOne(WorkDescription, "")
      .zeroOrOne(WorkId, "")
      .zeroOrOne(WorkParentId, "")
      .zeroOrOne(WorkType, ""));
   ArtifactTypeToken WorkFlowDefinition = osee.add(osee.artifactType(52L, "Work Flow Definition", false, WorkItemDefinition)
      .zeroOrOne(StartPage, "")
      .any(WorkTransition, ""));
   ArtifactTypeToken XViewerGlobalCustomization = osee.add(osee.artifactType(55L, "XViewer Global Customization", false, Artifact)
      .any(XViewerCustomization, ""));
   // @formatter:on
}