/*********************************************************************
 * Copyright (c) 2009 Boeing
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

import static org.eclipse.osee.framework.core.data.AttributeTypeToken.DEFAULT_DATE;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.*;
import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.osee;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.publishing.RequiredIndicator;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.core.util.toggles.CuiNamesConfiguration;
import org.eclipse.osee.framework.core.util.toggles.TogglesFactory;

/**
 * @author Ryan D. Brooks
 */
public interface CoreArtifactTypes {

   // @formatter:off

   /**
    * This artifact type is the base artifact type for all artifacts.
    * <dl>
    * <dt>Display Name:</dt>
    * <dd>Artifact</dd>
    * <dt>Artifact Type Identifier</dt>
    * <dd>1L</dd>
    * <dt>Attributes</dt>
    * <dd>
    * <ul>
    * <li>{@link CoreAttributeTypes#Annotation}</li>
    * <li>{@link CoreAttributeTypes#ContentUrl}</li>
    * <li>{@link CoreAttributeTypes#Description}</li>
    * <li>{@link CoreAttributeTypes#Name}</li>
    * <li>{@link CoreAttributeTypes#RelationOrder}</li>
    * <li>{@link CoreAttributeTypes#StaticId}</li>
    * </ul>
    * </dd>
    * </dl>
    */

   ArtifactTypeToken Artifact = osee.add(osee.artifactType(1L, "Artifact", false)
      .any(Annotation)
      .zeroOrOne(ContentUrl)
      .zeroOrOne(Description)
      .exactlyOne(Name, "unnamed")
      .zeroOrOne(RelationOrder)
      .any(StaticId));

   /**
    * This artifact type is used as base type for artifacts with specified CUI data rights.
    * <dl>
    * <dt>Display Name:</dt>
    * <dd>Controlled</dd>
    * <dt>Artifact Type Identifier:</dt>
    * <dd>7068197294137670241L</dd>
    * <dt>Super Types:</dt>
    * <dd>{@link CoreArtifactTypes#Artifact}</dd>
    * <dt>Attributes:</dt>
    * <dd>
    * <ul>
    * <li>{@link CoreAttributeTypes#DataClassification} (CUI Category and CUI Type)</li>
    * <li>{@link CoreAttributeTypes#DataClassificationRationale} (Rational for the CUI Category and CUI Type)</li>
    * <li>{@link CoreAttributeTypes#DataRightsCuiLimitedDisseminationControl}</li>
    * <li>{@link CoreAttributeTypes#DataRightsTrigraphCountryCode}</li>
    * <li>{@link CoreAttributeTypes#DataRightsClassification}(Required Indicators)</li>
    * <li>{@link CoreAttributeTypes#DataRightsBasis}(Rational for the Required Indicators)</li>
    * </ul>
    * </dd>
    * </dl>
    */

   ArtifactTypeToken Controlled =
      osee.add
         (
            osee
               .artifactType(7068197294137670241L, "Controlled", true, Artifact)
               .any(DataClassification)                                                      // CUI Category and CUI Type
               .zeroOrOne(DataClassificationRationale)                                       // Rational For the CUI Category and CUI Type designation
               .anyWhen
                  (
                     DataRightsCuiLimitedDisseminationControl,
                     () -> CuiNamesConfiguration.STANDARD.equals
                              (
                                 TogglesFactory
                                    .create
                                       (
                                          "CuiNamesConfiguration",
                                          CuiNamesConfiguration::convert,
                                          TogglesFactory.ToggleSource.BUNDLE_MANIFEST
                                       )
                                    .get()
                              )
                  )
               .anyWhen
                  (
                     DataRightsTrigraphCountryCode,
                     () -> CuiNamesConfiguration.STANDARD.equals
                     (
                        TogglesFactory
                           .create
                              (
                                 "CuiNamesConfiguration",
                                 CuiNamesConfiguration::convert,
                                 TogglesFactory.ToggleSource.BUNDLE_MANIFEST
                              )
                           .get()
                     )
                  )
               .any(DataRightsClassification, RequiredIndicator.UNSPECIFIED.getEnumToken() ) // Required Indicators
               .zeroOrOne(DataRightsBasis)                                                   // Rational for the Required Indicators
         );


   ArtifactTypeToken AbstractAccessControlled = osee.add(osee.artifactType(17L, "Abstract Access Controlled", true, Artifact)
      .any(AccessContextId));

   ArtifactTypeToken AbstractHeading = osee.add(osee.artifactType(805L, "Abstract Heading", true, Controlled)
      .zeroOrOne(DoorsHierarchy)
      .any(DoorsId)
      .any(DoorsModId)
      .zeroOrOne(MarkdownContent)
      .zeroOrOne(Extension));

   ArtifactTypeToken AbstractImplementationDetails = osee.add(osee.artifactType(921211884L, "Abstract Implementation Details", true, Controlled)
      .atLeastOne(Partition, Partition.Unspecified)
      .zeroOrOne(PotentialSecurityImpact)
      .zeroOrOne(SubjectMatterExpert)
      .exactlyOne(Subsystem, Subsystem.Unspecified)
      .zeroOrOne(MarkdownContent)
      .zeroOrOne(Extension));

   ArtifactTypeToken AbstractTestResult = osee.add(osee.artifactType(38L, "Abstract Test Result", true, Controlled));

   ArtifactTypeToken BranchView = osee.add(osee.artifactType(5849078277209560034L, "Branch View", false, Artifact)
      .any(ProductApplicability, "Unspecified"));

   ArtifactTypeToken Breaker = osee.add(osee.artifactType(188458869981236L, "Breaker", false, Controlled)
      .exactlyOne(CircuitBreakerId)
      .exactlyOne(DisplayText, "unset")
      .zeroOrOne(FunctionalGrouping, FunctionalGrouping.Avionics)
      .exactlyOne(MaintainerText, "unset")
      .exactlyOne(RequireConfirmation));

   ArtifactTypeToken CertificationBaselineEvent = osee.add(osee.artifactType(99L, "Certification Baseline Event", false, Controlled)
      .zeroOrOne(BaselinedBy)
      .zeroOrOne(BaselinedTimestamp)
      .zeroOrOne(GitChangeId)
      .zeroOrOne(ReviewId)
      .zeroOrOne(ReviewStoryId));

   ArtifactTypeToken CodeUnit = osee.add(osee.artifactType(58L, "Code Unit", false, Controlled)
      .exactlyOne(FileSystemPath)
      .zeroOrOne(SubjectMatterExpert));

   ArtifactTypeToken Component = osee.add(osee.artifactType(57L, "Component", false, Controlled)
      .exactlyOne(Developmental, Boolean.TRUE)
      .exactlyOne(GfeCfe, GfeCfe.Unspecified)
      .exactlyOne(IDAL, IDAL.Unspecified)
      .zeroOrOne(FACEProfile, FACEProfile.Unspecified)
      .zeroOrOne(FACESegment, FACESegment.Unspecified)
      .zeroOrOne(FACEVersion, FACEVersion.Unspecified));

   /**
    * The artifact type is used to define the data rights configuration for a publishing template.
    * <p>
    * The {@link CoreArtifactTypes#DataRightsConfiguration} artifact is referenced by name from the
    * {@link CoreAttributeTypes#PublishingTemplateDataRightsConfigurationNameReference} attribute
    * in a {@link CoreArtifactTypes#RendererTemplateWholeWord} artifact.
    * <dl>
    * <dt>Display Name:</dt>
    * <dd>Data Rights Configuration</dd>
    * <dt>Artifact Type Identifier:</dt>
    * <dd>1679213104L</dd>
    * <dt>Super Types:</dt>
    * <dd>{@link CoreArtifactTypes#Artifact}</dd>
    * <dt>Attributes:</dt>
    * <dd>
    * <ul>
    * <li>{@link CoreAttributeTypes#DataClassification}</li>
    * <li>{@link CoreAttributeTypes#DataRightsCuiLimitedDisseminationControl}</li>
    * <li>{@link CoreAttributeTypes#DataRightsTrigraphCountryCode}</li>
    * <li>{@link CoreAttributeTypes#DataRightsRequiredIndicatorConfigurationByRequiredIndicatorMapEntry}</li>
    * <li>{@link CoreAttributeTypes#DataRightsRequiredIndicatorByCuiCategoryCuiTypeMapEntry}</li>
    * </ul>
    * </dl>
    */

   ArtifactTypeToken DataRightsConfiguration =
      osee.add
         (
            osee
               .artifactType( 1679213104L, "Data Rights Configuration", false, Artifact )
               .any( DataClassification                                                  )
               .any( DataRightsCuiLimitedDisseminationControl                            )
               .any( DataRightsTrigraphCountryCode                                       )
               .any( DataRightsRequiredIndicatorConfigurationByRequiredIndicatorMapEntry )
               .any( DataRightsRequiredIndicatorByCuiCategoryCuiTypeMapEntry             )
         );

   /**
    * The artifact type is used to define the headers, footers, and title page statements for a data rights required
    * indicator.
    * <p>
    * The {@link CoreArtifactTypes#DataRightsRequiredIndicatorConfiguration} artifacts are referenced by name from
    * the value associated with a Required Indicator in the
    * {@link CoreAttributeTypes#DataRightsRequiredIndicatorConfigurationByRequiredIndicatorMapEntry} attribute of a
    * {@link CoreArtifactTypes#DataRightsConfiguration} artifact.
    * <dl>
    * <dt>Display Name:</dt>
    * <dd>Required Indicator Configuration</dd>
    * <dt>Artifact Type Identifier:</dt>
    * <dd>1747427168L</dd>
    * <dt>Super Types:</dt>
    * <dd>{@link CoreArtifactTypes#Artifact}</dd>
    * <dt>Attributes:</dt>
    * <dd>
    * <ul>
    * <li>{@link CoreAttributeTypes#DataRightsRequiredIndicatorTitleStatement}</li>
    * <li>{@link CoreAttributeTypes#DataRightsRequiredIndicatorHeaderStatement}</li>
    * <li>{@link CoreAttributeTypes#DataRightsRequiredIndicatorFooterStatement}</li>
    * <li>{@link CoreAttributeTypes#DataRightsRequiredIndicatorFrequency}</li>
    * </ul>
    * </dl>
    */

   ArtifactTypeToken DataRightsRequiredIndicatorConfiguration =
      osee.add
         (
            osee
               .artifactType(1747427168L, "Required Indicator Configuration", false, Artifact)
               .exactlyOne( DataRightsRequiredIndicatorTitleStatement  )
               .exactlyOne( DataRightsRequiredIndicatorHeaderStatement )
               .exactlyOne( DataRightsRequiredIndicatorFooterStatement )
               .any( DataRightsRequiredIndicatorFrequencyIndicator )
         );

   /**
    * This artifact type is used to define the statements for a Required Indicator's header, footer,
    * or title page indicators.
    * <p>
    * The {@link DataRightsRequiredIndicatorStatement} artifacts are referenced by name from the
    * {@link CoreAttributeTypes#DataRightsRequiredIndicatorTitleStatement},
    * {@link CoreAttributeTypes#DataRightsRequiredIndicatorHeaderStatement},
    * and {@link CoreAttributeTypes#DataRightsRequiredIndicatorFooterStatement} attributes of a
    * {@link CoreArtifactTypes#DataRightsRequiredIndicatorConfiguration} artifact.<dl>
    * <dt>Display Name:</dt>
    * <dd>Required Indicator Statement</dd>
    * <dt>Artifact Type Identifier:</dt>
    * <dd>4906694777925150215L</dd>
    * <dt>Super Types:</dt>
    * <dd>{@link CoreArtifactTypes#Artifact}</dd>
    * <dt>Attributes:</dt>
    * <dd>
    * <ul>
    * <li>{@link CoreAttributeTypes#DataRightsRequiredIndicatorStatementEntry}</li>
    * </ul>
    * </dl>
    */

   ArtifactTypeToken DataRightsRequiredIndicatorStatement =
      osee.add
         (
             osee.artifactType( 4906694777925150215L, "Required Indicator Statement", false, Artifact )
                .any( DataRightsRequiredIndicatorStatementEntry )
         );

   ArtifactTypeToken EnumeratedArtifact = osee.add(osee.artifactType(4619295485563766003L, "Enumerated Artifact", false, Controlled));

   ArtifactTypeToken Feature = osee.add(osee.artifactType(87L, "Feature", false, Artifact)
      .zeroOrOne(DefaultValue)
      .any(ProductApplicability, "Unspecified")
      .exactlyOne(FeatureMultivalued)
      .exactlyOne(FeatureValueType, FeatureValueType.String)
      .any(Value));

   ArtifactTypeToken Folder = osee.add(osee.artifactType(11L, "Folder", false, Artifact));

   ArtifactTypeToken GeneralData = osee.add(osee.artifactType(12L, "General Data", false, Controlled)
      .any(GeneralStringData)
      .zeroOrOne(PublishInline));

   ArtifactTypeToken AccessControlModel = osee.add(osee.artifactType(2L, "Access Control Model", false, GeneralData));

   ArtifactTypeToken FeatureDefinition = osee.add(osee.artifactType(5849078290088170402L, "Feature Definition", false, GeneralData));

   ArtifactTypeToken GitCommit = osee.add(osee.artifactType(100L, "Git Commit", false, Controlled)
      .zeroOrOne(GitChangeId)
      .zeroOrOne(GitCommitAuthorDate)
      .zeroOrOne(GitCommitMessage)
      .zeroOrOne(GitCommitSha)
      .zeroOrOne(UserArtifactId));

   ArtifactTypeToken GitRepository = osee.add(osee.artifactType(97L, "Git Repository", false, Artifact)
      .zeroOrOne(DefaultTrackingBranch)
      .any(ExcludePath)
      .zeroOrOne(FileSystemPath)
      .zeroOrOne(RepositoryUrl));

   ArtifactTypeToken GlobalPreferences = osee.add(osee.artifactType(3L, "Global Preferences", false, Artifact)
      .zeroOrOne(DefaultMailServer)
      .zeroOrOne(Dictionary)
      .any(GeneralStringData)
      .zeroOrOne(ProductLinePreferences)
      .zeroOrOne(WebPreferences));

   ArtifactTypeToken GroupArtifact = osee.add(osee.artifactType(6L, "Group Artifact", false, Artifact));

   ArtifactTypeToken HtmlArtifact = osee.add(osee.artifactType(798L, "HTML Artifact", false, Controlled)
      .any(HtmlContent)
      .any(ImageContent)
      .zeroOrOne(ParagraphNumber)
      .zeroOrOne(PublishInline));

   ArtifactTypeToken HeadingHtml = osee.add(osee.artifactType(804L, "Heading - HTML", false, AbstractHeading, HtmlArtifact));

   ArtifactTypeToken ImageArtifact = osee.add(osee.artifactType(800L, "Image Artifact", false, Controlled)
      .any(ImageContent)
      .zeroOrOne(ParagraphNumber));

   ArtifactTypeToken ModelDiagram = osee.add(osee.artifactType(98L, "Model Diagram", false, Controlled)
      .exactlyOne(GraphitiDiagram, "<?xml version= \"1.0\" encoding= \"ASCII\"?><pi:Diagram xmi:version= \"2.0\" xmlns:xmi= \"http://www.omg.org/XMI\" xmlns:xsi= \"http://www.w3.org/2001/XMLSchema-instance\" xmlns:al= \"http://eclipse.org/graphiti/mm/algorithms\" xmlns:pi= \"http://eclipse.org/graphiti/mm/pictograms\" visible= \"true\" gridUnit= \"10\" diagramTypeId= \"mbse\" name= \"mbse basic\" snapToGrid= \"true\" version= \"0.11.0\"><graphicsAlgorithm xsi:type= \"al:Rectangle\" background= \"//@colors.1\" foreground= \"//@colors.0\" lineWidth= \"1\" transparency= \"0.0\" width= \"1000\" height= \"1000\"/><colors red= \"227\" green= \"238\" blue= \"249\"/><colors red= \"255\" green= \"255\" blue= \"255\"/></pi:Diagram>"));

   ArtifactTypeToken Markdown = osee.add(osee.artifactType(3432564532423888L, "Markdown", true, Controlled)
      .exactlyOne(MarkdownContent, "## Enter Markdown Here\n")
      .exactlyOne(Extension, "md")
      .zeroOrOne(ParagraphNumber));

   ArtifactTypeToken MsWord = osee.add(osee.artifactType(16L, "MS Word", true, Controlled)
      .zeroOrOne(ParagraphNumber));

   ArtifactTypeToken MsWordTemplate = osee.add(osee.artifactType(19L, "MS Word Template", false, MsWord)
      .zeroOrOne(PageOrientation,PageOrientation.getPageOrientationEnum(WordCoreUtil.pageType.PORTRAIT))
      .zeroOrOne(PublishInline)
      .zeroOrOne(WordOleData)
      .zeroOrOne(WordTemplateContent, "<w:p xmlns:w= \"http://schemas.microsoft.com/office/word/2003/wordml\"><w:r><w:t></w:t></w:r></w:p>"));

   ArtifactTypeToken DesignMsWord = osee.add(osee.artifactType(346L, "Design - MS Word", false, MsWordTemplate)
      .zeroOrOne(DoorsHierarchy)
      .any(DoorsId)
      .any(DoorsModId)
      .zeroOrOne(IaPlan)
      .zeroOrOne(LegacyDal, LegacyDal.Unspecified)
      .zeroOrOne(PotentialSecurityImpact)
      .exactlyOne(SeverityCategory, SeverityCategory.Unspecified)
      .zeroOrOne(SubjectMatterExpert));

   ArtifactTypeToken DesignDescriptionMsWord = osee.add(osee.artifactType(810L, "Design Description - MS Word", false, MsWordTemplate, AbstractHeading));

   ArtifactTypeToken DocumentDescriptionMsWord = osee.add(osee.artifactType(806L, "Document Description - MS Word", false, MsWordTemplate, AbstractHeading));

   ArtifactTypeToken FunctionMsWord = osee.add(osee.artifactType(34L, "Function - MS Word", true, MsWordTemplate)
      .exactlyOne(FDAL, FDAL.Unspecified)
      .zeroOrOne(FdalRationale)
      .exactlyOne(SeverityCategory, SeverityCategory.Unspecified)
      .zeroOrOne(SoftwareSafetyImpact));

   ArtifactTypeToken HeadingMsWord = osee.add(osee.artifactType(56L, "Heading - MS Word", false, MsWordTemplate, AbstractHeading));

   ArtifactTypeToken ImplementationDetailsMsWord = osee.add(osee.artifactType(26L, "Implementation Details - MS Word", false, MsWordTemplate, AbstractImplementationDetails));

   ArtifactTypeToken ImplementationDetailsDataDefinitionMsWord = osee.add(osee.artifactType(279578L, "Implementation Details Data Definition - MS Word", false, ImplementationDetailsMsWord));

   ArtifactTypeToken ImplementationDetailsDrawingMsWord = osee.add(osee.artifactType(209690L, "Implementation Details Drawing - MS Word", false, ImplementationDetailsMsWord));

   ArtifactTypeToken ImplementationDetailsFunctionMsWord = osee.add(osee.artifactType(139802L, "Implementation Details Function - MS Word", false, ImplementationDetailsMsWord));

   ArtifactTypeToken ImplementationDetailsProcedureMsWord = osee.add(osee.artifactType(69914L, "Implementation Details Procedure - MS Word", false, ImplementationDetailsMsWord));

   ArtifactTypeToken MsWordWholeDocument = osee.add(osee.artifactType(18L, "MS Word Whole Document", false, MsWord)
      .zeroOrOne(IaPlan)
      .zeroOrOne(WholeWordContent, "<?xml version= '1.0' encoding= 'UTF-8' standalone= 'yes'?><?mso-application progid= 'Word.Document'?><w:wordDocument xmlns:w= 'http://schemas.microsoft.com/office/word/2003/wordml' xmlns:v= 'urn:schemas-microsoft-com:vml' xmlns:w10= 'urn:schemas-microsoft-com:office:word' xmlns:sl= 'http://schemas.microsoft.com/schemaLibrary/2003/core' xmlns:aml= 'http://schemas.microsoft.com/aml/2001/core' xmlns:wx= 'http://schemas.microsoft.com/office/word/2003/auxHint' xmlns:o= 'urn:schemas-microsoft-com:office:office' xmlns:dt= 'uuid:C2F41010-65B3-11d1-A29F-00AA00C14882' xmlns:wsp= 'http://schemas.microsoft.com/office/word/2003/wordml/sp2' xmlns:ns0= 'http://www.w3.org/2001/XMLSchema' xmlns:ns1= 'http://eclipse.org/artifact.xsd' xmlns:st1= 'urn:schemas-microsoft-com:office:smarttags' w:macrosPresent= 'no' w:embeddedObjPresent= 'no' w:ocxPresent= 'no' xml:space= 'preserve'><w:body></w:body></w:wordDocument>"));

   ArtifactTypeToken MsWordStyles = osee.add(osee.artifactType(2578L, "MS Word Styles", false, MsWordWholeDocument));

   ArtifactTypeToken NativeArtifact = osee.add(osee.artifactType(20L, "Native Artifact", true, Artifact)
      .zeroOrOne(Extension)
      .zeroOrOne(NativeContent));

   ArtifactTypeToken GeneralDocument = osee.add(osee.artifactType(14L, "General Document", false, NativeArtifact));

   ArtifactTypeToken OseeApp = osee.add(osee.artifactType(89L, "OSEE App", false, Artifact)
      .zeroOrOne(OseeAppDefinition));

   ArtifactTypeToken OseeTypeDefinition = osee.add(osee.artifactType(60L, "Osee Type Definition", false, Artifact)
      .exactlyOne(Active, Boolean.TRUE)
      .any(UriGeneralStringData));

   ArtifactTypeToken OseeTypeEnum = osee.add(osee.artifactType(5447805027409642344L, "Osee Type Enum", false, EnumeratedArtifact)
      .any(IdValue));

   ArtifactTypeToken PlainText = osee.add(osee.artifactType(784L, "Plain Text", false, Artifact)
      .zeroOrOne(ParagraphNumber)
      .zeroOrOne(PlainTextContent));

   ArtifactTypeToken AcronymPlainText = osee.add(osee.artifactType(5034328852220100337L, "Acronym Plain Text", false, PlainText)
      .zeroOrOne(Acronym)
      .any(DoorsId));

   ArtifactTypeToken ImplementationDetailsPlainText = osee.add(osee.artifactType(638269899L, "Implementation Details Plain Text", false, PlainText, AbstractImplementationDetails));

   ArtifactTypeToken PlainTextDataRights = osee.add(osee.artifactType(4527862492986312222L, "Plain Text With Data Rights", false, PlainText, Controlled)
      .zeroOrOne(DoorsHierarchy)
      .any(DoorsId)
      .zeroOrOne(PageOrientation, PageOrientation.getPageOrientationEnum(WordCoreUtil.pageType.PORTRAIT)));

   ArtifactTypeToken ReferenceDocument = osee.add(osee.artifactType(2084059074565751746L, "Reference Document", false, GeneralDocument)
      .any(DoorsId));

   /**
    * The artifact type is used to define a publishing template.
    * <dl>
    * <dt>Display Name:</dt>
    * <dd>Renderer Template - Whole Word</dd>
    * <dt>Artifact Type Identifier:</dt>
    * <dd>9L</dd>
    * <dt>Super Types:</dt>
    * <dd>{@link CoreArtifactTypes#Artifact}</dd>
    * <dt>Attributes</dt>
    * <dd>
    * <ul>
    * <li>{@link CoreAttributeTyeps#RendererOptions}</li>
    * <li>{@link CoreAttributeTyeps#TemplateMatchCriteria}</li>
    * <li>{@link CoreAttributeTypes#WholeWordContent}</li>
    * <li>{@link CoreAttributeTypes#PublishingTemplateContentByFormatMapEntry}</li>
    * <li>{@link CoreAttributeTyeps#PublishingTemplateDataRightsConfigurationNameReference}</li>
    * </ul>
    * </dd>
    * </dl>
    */

   ArtifactTypeToken RendererTemplateWholeWord =
      osee.add
         (
            osee.artifactType(9L, "Renderer Template - Whole Word", false, Artifact)
               .exactlyOne( RendererOptions, "{\"ElementType\" : \"Artifact\", \"OutliningOptions\" : [ {\"Outlining\" : true, \"RecurseChildren\" : false, \"HeadingAttributeType\" : \"Name\", \"ArtifactName\" : \"Default\", \"OutlineNumber\" : \"\" }], \"AttributeOptions\" : [{\"AttrType\" : \"*\",  \"Label\" : \"\", \"FormatPre\" : \"\", \"FormatPost\" : \"\"}]}" )
               .any( TemplateMatchCriteria )
               .zeroOrOne( WholeWordContent, "<w:p xmlns:w= \"http://schemas.microsoft.com/office/word/2003/wordml\"><w:r><w:t></w:t></w:r></w:p>" )
               .any( PublishingTemplateContentByFormatMapEntry )
               .zeroOrOne( PublishingTemplateDataRightsConfigurationNameReference )
         );

   ArtifactTypeToken ReportTemplate = osee.add(osee.artifactType(63228787744062L, "Report Template", false, Artifact)
      .zeroOrOne(CoreAttributeTypes.JavaCode));

   ArtifactTypeToken Requirement = osee.add(osee.artifactType(21L, "Requirement", false, Controlled)
      .zeroOrOne(SubjectMatterExpert)
      .zeroOrOne(MarkdownContent)
      .zeroOrOne(Extension));

   ArtifactTypeToken AbstractSpecRequirement = osee.add(osee.artifactType(58551193202327573L, "Abstract Spec Requirement", false, Requirement)
      .zeroOrOne(DoorsHierarchy)
      .any(DoorsId)
      .any(DoorsModId)
      .zeroOrOne(LegacyDal, LegacyDal.Unspecified)
      .zeroOrOne(LegacyId)
      .zeroOrOne(PotentialSecurityImpact)
      .atLeastOne(QualificationMethod,QualificationMethod.Unspecified)
      .zeroOrOne(SafetyImpact)
      .zeroOrOne(SafetySeverity,SafetySeverity.Unspecified)
      .zeroOrOne(SoftwareControlCategory,SoftwareControlCategory.Unspecified)
      .zeroOrOne(SoftwareControlCategoryRationale)
      .zeroOrOne(SwCI)
      .exactlyOne(Subsystem, Subsystem.Unspecified)
      .zeroOrOne(TechnicalPerformanceParameter)
      .computed(CoreAttributeTypes.SoftwareCriticalityIndex));

   ArtifactTypeToken AbstractSoftwareRequirement = osee.add(osee.artifactType(23L, "Abstract Software Requirement", true, AbstractSpecRequirement)
      .any(CSCI,CSCI.Unspecified)
      .zeroOrOne(Category)
      .atLeastOne(Partition, Partition.Unspecified));

   ArtifactTypeToken AbstractSubsystemRequirement = osee.add(osee.artifactType(797L, "Abstract Subsystem Requirement", true, AbstractSpecRequirement)
      .zeroOrOne(Effectivity, "Unspecified")
      .exactlyOne(IDAL, IDAL.Unspecified)
      .zeroOrOne(IdalRationale)
      .zeroOrOne(LegacyId)
      .atLeastOne(VerificationEvent, VerificationEvent.Unspecified)
      .zeroOrOne(VerificationLevel,VerificationLevel.Unspecified));

   ArtifactTypeToken AbstractSystemRequirement = osee.add(osee.artifactType(796L, "Abstract System Requirement", true, AbstractSpecRequirement)
      .zeroOrOne(Effectivity, "Unspecified")
      .zeroOrOne(IaPlan)
      .zeroOrOne(LegacyId)
      .zeroOrOne(VerificationAcceptanceCriteria)
      .atLeastOne(VerificationEvent, VerificationEvent.Unspecified)
      .zeroOrOne(VerificationLevel, VerificationLevel.Unspecified));

   ArtifactTypeToken CustomerRequirementMsWord = osee.add(osee.artifactType(809L, "Customer Requirement - MS Word", false, MsWordTemplate, AbstractSpecRequirement));

   ArtifactTypeToken DirectSoftwareRequirement = osee.add(osee.artifactType(22L, "Direct Software Requirement", true, AbstractSoftwareRequirement));

   ArtifactTypeToken SoftwareRequirement = osee.add(osee.artifactType(22000000000L, "Software Requirement", false, DirectSoftwareRequirement)
      .exactlyOne(IDAL,IDAL.Unspecified)
      .zeroOrOne(IaPlan)
      .zeroOrOne(IdalRationale)
      .zeroOrOne(SoftwareControlCategory,SoftwareControlCategory.Unspecified)
      .zeroOrOne(SoftwareControlCategoryRationale));

   ArtifactTypeToken HardwareRequirementMsWord = osee.add(osee.artifactType(33L, "Hardware Requirement - MS Word", false, MsWordTemplate, AbstractSpecRequirement));

   ArtifactTypeToken InterfaceArtifact = osee.add(osee.artifactType(54733032508193943L, "Interface Artifact", true, Artifact));

   ArtifactTypeToken InterfaceNode = osee.add(osee.artifactType(6039606571486514295L, "Interface Node", false, InterfaceArtifact)
      .zeroOrOne(InterfaceNodeAddress)
      .exactlyOne(InterfaceNodeNumber)
      .zeroOrOne(InterfaceNodeGroupId)
      .zeroOrOne(NameAbbrev)
      .zeroOrOne(InterfaceNodeCodeGenName)
      .zeroOrOne(InterfaceNodeType)
      .zeroOrOne(InterfaceNodeBackgroundColor)
      .zeroOrOne(InterfaceNodeCodeGen)
      .zeroOrOne(InterfaceNodeBuildCodeGen)
      .zeroOrOne(InterfaceNodeToolUse)
      .zeroOrOne(Notes));

   ArtifactTypeToken InterfaceConnection = osee.add(osee.artifactType(126164394421696910L, "Interface Connection", false, InterfaceArtifact)
      .exactlyOne(InterfaceTransportType));

   ArtifactTypeToken InterfaceMessage = osee.add(osee.artifactType(2455059983007225775L, "Interface Message", false, InterfaceArtifact)
      .exactlyOne(InterfaceMessageNumber)
      .exactlyOne(InterfaceMessagePeriodicity)
      .zeroOrOne(InterfaceMessageRate)
      .exactlyOne(InterfaceMessageWriteAccess)
      .exactlyOne(InterfaceMessageType)
      .zeroOrOne(InterfaceMessageExclude)
      .zeroOrOne(InterfaceMessageIoMode)
      .zeroOrOne(InterfaceMessageModeCode)
      .zeroOrOne(InterfaceMessageRateVer)
      .zeroOrOne(InterfaceMessagePriority)
      .zeroOrOne(InterfaceMessageProtocol)
      .zeroOrOne(InterfaceMessageRptWordCount)
      .zeroOrOne(InterfaceMessageRptCmdWord)
      .zeroOrOne(InterfaceMessageRunBeforeProc)
      .zeroOrOne(InterfaceMessageVer));

   ArtifactTypeToken InterfaceSubMessage = osee.add(osee.artifactType(126164394421696908L, "Interface SubMessage", false, InterfaceArtifact)
      .exactlyOne(InterfaceSubMessageNumber));

   ArtifactTypeToken InterfaceStructure = osee.add(osee.artifactType(2455059983007225776L, "Interface Structure", false, InterfaceArtifact)
      .zeroOrOne(NameAbbrev)
      .zeroOrOne(InterfaceStructureCategory)
      .zeroOrOne(InterfaceMinSimultaneity)
      .zeroOrOne(InterfaceMaxSimultaneity)
      .zeroOrOne(InterfaceTaskFileType)
      .zeroOrOne(GeneralStringData));

   ArtifactTypeToken InterfaceDataElement = osee.add(osee.artifactType(2455059983007225765L, "Interface DataElement", false, InterfaceArtifact)
      .zeroOrOne(InterfaceDefaultValue)
      .zeroOrOne(InterfaceElementAlterable)
      .zeroOrOne(InterfaceElementArrayHeader)
      .zeroOrOne(InterfaceElementWriteArrayHeaderName)
      .zeroOrOne(Notes)
      .zeroOrOne(InterfaceElementEnumLiteral)
      .zeroOrOne(InterfaceElementIndexStart)
      .zeroOrOne(InterfaceElementIndexEnd));

   ArtifactTypeToken InterfaceDataElementArray = osee.add(osee.artifactType(6360154518785980502L, "Interface DataElement Array", false, InterfaceDataElement));

   ArtifactTypeToken InterfacePlatformType = osee.add(osee.artifactType(6360154518785980503L, "Interface Platform Type", false, InterfaceArtifact)
      .exactlyOne(InterfaceLogicalType)
      .exactlyOne(InterfacePlatformTypeBitSize)
      .exactlyOne(InterfacePlatformType2sComplement)
      .zeroOrOne(InterfacePlatformTypeMinval)
      .zeroOrOne(InterfacePlatformTypeMaxval)
      .zeroOrOne(InterfacePlatformTypeUnits)
      .zeroOrOne(InterfaceDefaultValue)
      .zeroOrOne(InterfacePlatformTypeMsbValue)
      .zeroOrOne(InterfacePlatformTypeBitsResolution)
      .zeroOrOne(InterfacePlatformTypeCompRate)
      .zeroOrOne(InterfacePlatformTypeAnalogAccuracy)
      .zeroOrOne(InterfacePlatformTypeValidRangeDescription));

   ArtifactTypeToken InterfaceEnum = osee.add(osee.artifactType(2455059983007225793L, "Interface Enumeration", false, InterfaceArtifact)
      .exactlyOne(InterfaceEnumOrdinal)
      .exactlyOne(InterfaceEnumOrdinalType));

   ArtifactTypeToken InterfaceEnumSet = osee.add(osee.artifactType(2455059983007225791L, "Interface Enumeration Set", false, InterfaceArtifact));

   ArtifactTypeToken InterfaceRate = osee.add(osee.artifactType(2434833974449324686L, "Interface Transmission Rate", false, InterfaceArtifact));

   ArtifactTypeToken InterfaceUnit = osee.add(osee.artifactType(8314622128828217343L, "Interface Unit", false, InterfaceArtifact)
      .exactlyOne(InterfaceUnitMeasurement));

   ArtifactTypeToken InterfaceMessageTypeEnum = osee.add(osee.artifactType(8893121862470164649L, "Interface Message Type Enum", false, InterfaceArtifact));

   ArtifactTypeToken CrossReference = osee.add(osee.artifactType(5198407772394384771L, "Cross Reference", false, Artifact)
      .exactlyOne(CrossReferenceValue)
      .zeroOrOne(CrossReferenceArrayValues)
      .zeroOrOne(CrossReferenceAdditionalContent));

   ArtifactTypeToken TransportType = osee.add(osee.artifactType(6663383168705248989L, "Transport Type", false, Artifact)
      .exactlyOne(ByteAlignValidation)
      .zeroOrOne(ByteAlignValidationSize)
      .exactlyOne(MessageGeneration)
      .zeroOrOne(MessageGenerationType)
      .any(MessageGenerationPosition)
      .zeroOrOne(MinimumSubscriberMultiplicity)
      .zeroOrOne(MaximumSubscriberMultiplicity)
      .zeroOrOne(MinimumPublisherMultiplicity)
      .zeroOrOne(MaximumPublisherMultiplicity)
      .zeroOrOne(InterfaceLevelsToUse)
      .zeroOrOne(AvailableMessageHeaders)
      .zeroOrOne(AvailableSubmessageHeaders)
      .zeroOrOne(AvailableStructureHeaders)
      .zeroOrOne(AvailableElementHeaders)
      .zeroOrOne(DashedPresentation)
      .zeroOrOne(SpareAutoNumbering));

   ArtifactTypeToken IndirectSoftwareRequirementMsWord = osee.add(osee.artifactType(25L, "Indirect Software Requirement - MS Word", false, MsWordTemplate, AbstractSoftwareRequirement));

   ArtifactTypeToken InterfaceRequirementMsWord = osee.add(osee.artifactType(32L, "Interface Requirement - MS Word", false, MsWordTemplate, AbstractSpecRequirement)
      .exactlyOne(CoreAttributeTypes.Component, CoreAttributeTypes.Component.Unspecified));

   ArtifactTypeToken HelpPage = osee.add(osee.artifactType(5728426866732962911L, "Help Page", false, Artifact)
      .exactlyOne(CoreAttributeTypes.MarkdownContent)
      .exactlyOne(CoreAttributeTypes.ApplicationName)
      .exactlyOne(CoreAttributeTypes.IsHelpPageHeader)
      .exactlyOne(CoreAttributeTypes.IsTrainingPage));

   ArtifactTypeToken OseeReport = osee.add(osee.artifactType(3379299200905896452L, "OSEE Report", false, Artifact)
      .exactlyOne(CoreAttributeTypes.HttpMethod)
      .exactlyOne(CoreAttributeTypes.FileExtension)
      .exactlyOne(CoreAttributeTypes.FileNamePrefix)
      .exactlyOne(CoreAttributeTypes.ProducesMediaType)
      .exactlyOne(CoreAttributeTypes.DiffAvailable)
      .exactlyOne(CoreAttributeTypes.EndpointUrl)
      .exactlyOne(CoreAttributeTypes.RequiresValidation));

   ArtifactTypeToken MimReport = osee.add(osee.artifactType(1112907634879895453L, "MIM Report", false, OseeReport));

   ArtifactTypeToken MimImport = osee.add(osee.artifactType(2807814791345263165L, "MIM Import", false, Artifact)
      .zeroOrOne(ImportTransportType)
      .exactlyOne(EndpointUrl)
      .exactlyOne(ConnectionRequired));

   ArtifactTypeToken DiffReportEndPoint = osee.add(osee.artifactType(8247460208362207697L, "DIFF Report End Point", false, Artifact)
      .exactlyOne(EndpointUrl));

   ArtifactTypeToken ProductType = osee.add(osee.artifactType(7274800616985881194L, "Product Type", false, Artifact));

   ArtifactTypeToken RootArtifact = osee.add(osee.artifactType(10L, "Root Artifact", false, Artifact));

   ArtifactTypeToken SafetyAssessment = osee.add(osee.artifactType(59L, "Safety Assessment", false, Controlled)
      .zeroOrOne(ParagraphNumber)
      .zeroOrOne(SFHA));

   ArtifactTypeToken SoftwareDesignMsWord = osee.add(osee.artifactType(45L, "Software Design - MS Word", false, DesignMsWord));

   ArtifactTypeToken SoftwareRequirementMarkdown = osee.add(osee.artifactType(3525883482323L, "Software Requirement - Markdown", false, SoftwareRequirement, Markdown));

   ArtifactTypeToken SoftwareRequirementMsWord = osee.add(osee.artifactType(24L, "Software Requirement - MS Word", false, MsWordTemplate, SoftwareRequirement));

   ArtifactTypeToken SoftwareRequirementDataDefinitionMsWord = osee.add(osee.artifactType(793L, "Software Requirement Data Definition - MS Word", false, IndirectSoftwareRequirementMsWord));

   ArtifactTypeToken SoftwareRequirementDrawingMsWord = osee.add(osee.artifactType(29L, "Software Requirement Drawing - MS Word", false, IndirectSoftwareRequirementMsWord));

   ArtifactTypeToken SoftwareRequirementFunctionMsWord = osee.add(osee.artifactType(28L, "Software Requirement Function - MS Word", false, IndirectSoftwareRequirementMsWord));

   ArtifactTypeToken SoftwareRequirementHtml = osee.add(osee.artifactType(42L, "Software Requirement - HTML", false, AbstractSoftwareRequirement, HtmlArtifact));

   ArtifactTypeToken SoftwareRequirementPlainText = osee.add(osee.artifactType(792L, "Software Requirement Plain Text", false, PlainText, DirectSoftwareRequirement));

   ArtifactTypeToken SoftwareRequirementProcedureMsWord = osee.add(osee.artifactType(27L, "Software Requirement Procedure - MS Word", false, IndirectSoftwareRequirementMsWord));

   ArtifactTypeToken SubsystemDesignMsWord = osee.add(osee.artifactType(43L, "Subsystem Design - MS Word", false, DesignMsWord)
      .exactlyOne(Subsystem, Subsystem.Unspecified));

   ArtifactTypeToken SubsystemFunctionMsWord = osee.add(osee.artifactType(36L, "Subsystem Function - MS Word", false, FunctionMsWord, SubsystemDesignMsWord));

   ArtifactTypeToken SubsystemRequirementHtml = osee.add(osee.artifactType(795L, "Subsystem Requirement - HTML", false, AbstractSubsystemRequirement, HtmlArtifact));

   ArtifactTypeToken SystemRequirementMarkdown = osee.add(osee.artifactType(4135720862433165231L, "System Requirement - Markdown", false, AbstractSystemRequirement, Markdown));

   ArtifactTypeToken SystemRequirementMsWord = osee.add(osee.artifactType(30L, "System Requirement - MS Word", false, MsWordTemplate, AbstractSystemRequirement)
      .zeroOrOne(CoreAttributeTypes.Hazard));

   ArtifactTypeToken SubsystemRequirementMarkdown = osee.add(osee.artifactType(2257709917691903205L, "Subsystem Requirement - Markdown", false, AbstractSubsystemRequirement, Markdown));

   ArtifactTypeToken SubsystemRequirementMsWord = osee.add(osee.artifactType(31L, "Subsystem Requirement - MS Word", false, MsWordTemplate, AbstractSubsystemRequirement)
      .zeroOrOne(CoreAttributeTypes.Hazard));

   ArtifactTypeToken SupportDocumentMsWord = osee.add(osee.artifactType(13L, "Support Document - MS Word", false, MsWordTemplate));

   ArtifactTypeToken SystemDesignMsWord = osee.add(osee.artifactType(44L, "System Design - MS Word", false, DesignMsWord));

   ArtifactTypeToken SystemFunctionMsWord = osee.add(osee.artifactType(35L, "System Function - MS Word", false, FunctionMsWord, SystemDesignMsWord)
      .zeroOrOne(FunctionalCategory));

   ArtifactTypeToken SystemRequirementHtml = osee.add(osee.artifactType(794L, "System Requirement - HTML", false, AbstractSystemRequirement, HtmlArtifact));

   ArtifactTypeToken TestPlanElementMsWord = osee.add(osee.artifactType(37L, "Test Plan Element - MS Word", false, MsWordTemplate));

   ArtifactTypeToken TestResultNative = osee.add(osee.artifactType(39L, "Test Result Native", false, NativeArtifact, AbstractTestResult));

   ArtifactTypeToken TestResultWholeWord = osee.add(osee.artifactType(40L, "Test Result - Whole Word", false, MsWordWholeDocument, AbstractTestResult));

   ArtifactTypeToken TestUnit = osee.add(osee.artifactType(4L, "Test Unit", true, Controlled));

   ArtifactTypeToken IntegrationTestProcedureMsWord = osee.add(osee.artifactType(443398723457743215L, "Integration Test Procedure - MS Word", false, MsWordTemplate, TestUnit)
      .zeroOrOne(DoorsHierarchy)
      .any(DoorsId));

   ArtifactTypeToken SoftwareTestProcedurePlainText = osee.add(osee.artifactType(564397212436322878L, "Software Test Procedure Plain Text", false, PlainText, TestUnit)
      .zeroOrOne(DoorsHierarchy)
      .any(DoorsId));

   ArtifactTypeToken IntegrationTestProcedureWholeWord = osee.add(osee.artifactType(443398723457743216L, "Integration Test Procedure - Whole Word", false, MsWordWholeDocument, TestUnit)
      .zeroOrOne(DoorsHierarchy)
      .any(DoorsId));

   ArtifactTypeToken TestCase = osee.add(osee.artifactType(82L, "Test Case", false, TestUnit));

   ArtifactTypeToken TestInformationSheetMsWord = osee.add(osee.artifactType(41L, "Test Information Sheet - MS Word", false, MsWordTemplate, TestUnit)
      .exactlyOne(TisTestCategory, TisTestCategory.DEV)
      .zeroOrOne(TisTestNumber)
      .atLeastOne(TisTestType,TisTestType.StationaryVehicle));

   ArtifactTypeToken TestProcedure = osee.add(osee.artifactType(46L, "Test Procedure", false, TestUnit)
      .exactlyOne(Subsystem, Subsystem.Unspecified)
      .zeroOrOne(TestProcedureStatus, TestProcedureStatus.NotPerformed));

   ArtifactTypeToken TestProcedureMsWord = osee.add(osee.artifactType(2349L, "Test Procedure - MS Word", false, MsWordTemplate, TestProcedure));

   ArtifactTypeToken SoftwareTestProcedureMsWord = osee.add(osee.artifactType(554486323432951758L, "Software Test Procedure - MS Word", false, MsWordTemplate, TestUnit)
      .any(DoorsId)
      .zeroOrOne(DoorsHierarchy));

   ArtifactTypeToken TestProcedureNative = osee.add(osee.artifactType(48L, "Test Procedure Native", false, NativeArtifact, TestProcedure));

   ArtifactTypeToken TestProcedureWholeWord = osee.add(osee.artifactType(47L, "Test Procedure - Whole Word", false, MsWordWholeDocument, TestProcedure));

   ArtifactTypeToken TestSupport = osee.add(osee.artifactType(83L, "Test Support", false, TestUnit));

   ArtifactTypeToken UniversalGroup = osee.add(osee.artifactType(8L, "Universal Group", false, GroupArtifact));

   ArtifactTypeToken Url = osee.add(osee.artifactType(15L, "Url", false, Artifact));

   ArtifactTypeToken SupportingContent = osee.add(osee.artifactType(49L, "Supporting Content", false, Url));

   ArtifactTypeToken User = osee.add(osee.artifactType(5L, "User", false, Artifact)
      .exactlyOne(Active, Boolean.TRUE)
      .any(AtsActionSearch)
      .any(AtsUserConfig)
      .zeroOrOne(City)
      .zeroOrOne(Company)
      .zeroOrOne(CompanyTitle)
      .zeroOrOne(Country)
      .zeroOrOne(Dictionary)
      .zeroOrOne(Email)
      .any(FavoriteBranch)
      .zeroOrOne(FaxPhone)
      .zeroOrOne(MobilePhone)
      .any(LoginId)
      .any(Notes)
      .zeroOrOne(Phone)
      .zeroOrOne(State)
      .zeroOrOne(Street)
      .zeroOrOne(UserId)
      .zeroOrOne(UserSettings)
      .zeroOrOne(WebPreferences)
      .zeroOrOne(Website)
      .any(XViewerCustomization)
      .any(XViewerDefaults)
      .zeroOrOne(Zip)
      .any(MimBranchPreferences)
      .any(MimColumnPreferences));

   ArtifactTypeToken UserGroup = osee.add(osee.artifactType(7L, "User Group", false, AbstractAccessControlled, GroupArtifact)
      .zeroOrOne(DefaultGroup)
      .any(Email));

   ArtifactTypeToken MimUserGlobalPreferences = osee.add(osee.artifactType(5935321910901176667L, "MIM User Global Preferences", false, Artifact)
      .exactlyOne(MimSettingWordWrap));

   ArtifactTypeToken SoftwareTestProcedureWholeWord = osee.add(osee.artifactType(554486323432951757L, "Software Test Procedure - Whole Word", false, MsWordWholeDocument, TestUnit)
      .any(DoorsId)
      .zeroOrOne(DoorsHierarchy));

   ArtifactTypeToken SubscriptionGroup = osee.add(osee.artifactType(6753071794573299176L, "Subscription Group", false, UserGroup));

   ArtifactTypeToken WorkItemDefinition = osee.add(osee.artifactType(50L, "Work Item Definition", true, Controlled)
      .any(WorkData)
      .zeroOrOne(WorkDescription)
      .zeroOrOne(WorkId)
      .zeroOrOne(WorkParentId)
      .zeroOrOne(WorkType));

   ArtifactTypeToken WorkFlowDefinition = osee.add(osee.artifactType(52L, "Work Flow Definition", false, WorkItemDefinition)
      .zeroOrOne(StartPage)
      .any(WorkTransition));

   ArtifactTypeToken XViewerGlobalCustomization = osee.add(osee.artifactType(55L, "XViewer Global Customization", false, Artifact)
      .any(XViewerCustomization));

   ArtifactTypeToken Context = osee.add(osee.artifactType(3962411134691320126L, "Context", false, Artifact)
      .zeroOrOne(Description, "", "A context hint that will be visible in the UI"));

   ArtifactTypeToken Command = osee.add(osee.artifactType(3605711044364389729L, "Command", false, Artifact)
      .exactlyOne(CustomCommand, false, "This is a custom command")
      .zeroOrOne(Description, "", "A description of the command")
      .zeroOrOne(ContentUrl, "", "A Rest Call endpoint to call to execute templated with parameters")
      .zeroOrOne(HttpMethod, HttpMethod.Get, "HTTP method for request"));

   ArtifactTypeToken ExecutedCommand = osee.add(osee.artifactType(3605721345366379123L, "ExecutedCommand", false, Artifact)
      .exactlyOne(ExecutionFrequency, 0, "Frequency of parameterized command execution")
      .exactlyOne(CommandTimestamp, DEFAULT_DATE, "The timestamp of the last execution of this command")
      .exactlyOne(ParameterizedCommand, "", "The JSON representation of the command with its parameterized attributes/values")
      .exactlyOne(Favorite, false, "Is executed command a favorite")
      .exactlyOne(IsValidated, true, "Flags if the executed command was valid or not"));

   ArtifactTypeToken ExecutedCommandHistory = osee.add(osee.artifactType(3102324341367389724L, "ExecutedCommand History", false, Artifact));

   ArtifactTypeToken Parameter = osee.add(osee.artifactType(5334063606392099440L, "Parameter", true, Artifact)
      .zeroOrOne(DefaultValue)
      .zeroOrOne(Description, "", "A description of valid parameter inputs")
      .zeroOrOne(UseValidator, false, "Is a validator used for this parameter")
      .zeroOrOne(ValidatorType, "", "Type of validator"));

   ArtifactTypeToken ParameterInteger = osee.add(osee.artifactType(3007766441141267760L, "ParameterInteger", false, Parameter));

   ArtifactTypeToken ParameterBranch = osee.add(osee.artifactType(4683538775178036503L, "ParameterBranch", false, Parameter));

   ArtifactTypeToken ParameterBoolean = osee.add(osee.artifactType(9092244262700990331L, "ParameterBoolean", false, Parameter));

   ArtifactTypeToken ParameterString = osee.add(osee.artifactType(6057500041616318960L, "ParameterString", false, Parameter));

   ArtifactTypeToken ParameterSingleSelect = osee.add(osee.artifactType(9154803029373920500L, "ParameterSingleSelect", false, Parameter));

   ArtifactTypeToken ParameterMultipleSelect = osee.add(osee.artifactType(1937883426323978299L, "ParameterMultipleSelect", false, Parameter));

   ArtifactTypeToken ScriptSet = osee.add(osee.artifactType(8756764536L, "Script Set", false, Artifact)
      .zeroOrOne(Active));

   ArtifactTypeToken TestScriptDef = osee.add(osee.artifactType(8756764537L, "Test Script Definition", false, Artifact)
      .exactlyOne(ScriptName)
      .zeroOrOne(ExecutionDate)
      .zeroOrOne(ExecutionEnvironment)
      .exactlyOne(MachineName)
      .exactlyOne(Revision)
      .exactlyOne(RepositoryType)
      .zeroOrOne(TeamName)
      .exactlyOne(LastAuthor)
      .exactlyOne(LastModifiedDate)
      .exactlyOne(ModifiedFlag)
      .exactlyOne(RepositoryUrl)
      .atLeastOne(PropertyKey)
      .zeroOrOne(Notes)
      .zeroOrOne(Safety, Boolean.FALSE)
      .zeroOrOne(Scheduled, Boolean.FALSE)
      .zeroOrOne(ScheduledTime)
      .zeroOrOne(ScheduledMachine)
      .zeroOrOne(StatusBy)
      .zeroOrOne(StatusDate)
      .exactlyOne(ScriptSubsystem)
      );

   ArtifactTypeToken TestScriptResults = osee.add(osee.artifactType(8756764538L, "Test Script Results", false, Artifact)
      .exactlyOne(SetId)
      .exactlyOne(ProcessorId)
      .atLeastOne(RuntimeVersion)
      .exactlyOne(ExecutionDate)
      .zeroOrOne(ExecutionEnvironment)
      .exactlyOne(MachineName)
      .exactlyOne(PassedCount)
      .exactlyOne(FailedCount)
      .zeroOrOne(InteractiveCount)
      .exactlyOne(ScriptAborted)
      .exactlyOne(ElapsedTime)
      .zeroOrOne(StartDate)
      .zeroOrOne(EndDate)
      .exactlyOne(OsArchitecture)
      .exactlyOne(OsName)
      .exactlyOne(OsVersion)
      .exactlyOne(OseeServerJarVersion)
      .exactlyOne(OseeServerTitle)
      .exactlyOne(OseeVersion)
      .exactlyOne(JavaVersion)
      .zeroOrOne(Result)
      .zeroOrOne(ScriptHealth)
      .exactlyOne(UserId)
      .exactlyOne(UserName)
      .exactlyOne(Email)
      .exactlyOne(QualificationLevel)
      .zeroOrOne(ExecutedBy)
      .any(Witness));

   ArtifactTypeToken TestCaseInitial = osee.add(osee.artifactType(1017100809L, "Test Case Initial", false, TestCase));

   ArtifactTypeToken ScriptTestCase = osee.add(osee.artifactType(572676456L, "Script Test Case", false, TestCase)
      .exactlyOne(TestNumber));

   ArtifactTypeToken TestCaseCleanup = osee.add(osee.artifactType(1086040624L, "Test Case Cleanup", false, TestCase));

   ArtifactTypeToken TestPoint = osee.add(osee.artifactType(8756764539L, "Test Point", false, Artifact)
      .exactlyOne(TestNumber)
      .zeroOrOne(Result)
      .zeroOrOne(OverallResult)
      .zeroOrOne(ResultType)
      .zeroOrOne(Interactive, Boolean.FALSE)
      .zeroOrOne(TestPointGroupName)
      .zeroOrOne(TestPointGroupType)
      .zeroOrOne(TestPointGroupOperator)
      .zeroOrOne(Expected)
      .zeroOrOne(Actual)
      .any(TestPointRequirement)
      .zeroOrOne(ElapsedTime)
      .zeroOrOne(TransmissionCount)
      .zeroOrOne(Notes));

   ArtifactTypeToken ScriptLog = osee.add(osee.artifactType(1588700579L, "Script Log", false, Artifact)
      .exactlyOne(LogLevel)
      .exactlyOne(Logger)
      .exactlyOne(LogMessage)
      .zeroOrOne(LogThrowable));

   ArtifactTypeToken StackTrace = osee.add(osee.artifactType(1769887640L, "Stack Trace", false, Artifact)
      .exactlyOne(StackTraceLine)
      .exactlyOne(StackTraceSource));

   ArtifactTypeToken AttentionLocation = osee.add(osee.artifactType(671245081L, "Attention Location", false, Artifact)
      .zeroOrOne(LocationId)
      .zeroOrOne(LocationTime)
      .zeroOrOne(AttentionMessage));

   ArtifactTypeToken LoggingSummary = osee.add(osee.artifactType(1851691794L, "Logging Summary", false, Artifact)
      .exactlyOne(SummaryId)
      .exactlyOne(StartNumber)
      .exactlyOne(InformationalCount)
      .exactlyOne(MinorCount)
      .exactlyOne(SeriousCount)
      .exactlyOne(CriticalCount)
      .exactlyOne(ExceptionCount));

   ArtifactTypeToken ErrorEntry = osee.add(osee.artifactType(53515786L, "Error Entry", false, Artifact)
      .exactlyOne(SummaryId)
      .exactlyOne(ErrorSeverity)
      .exactlyOne(ErrorVersion)
      .exactlyOne(ErrorCount));

   ArtifactTypeToken VersionInformation = osee.add(osee.artifactType(1513031087L, "Version Information", false, Artifact)
      .exactlyOne(VersionInfo)
      .exactlyOne(VersionUnit)
      .exactlyOne(UnderTest));

   ArtifactTypeToken Trace = osee.add(osee.artifactType(693558761L, "Trace", false, Artifact)
      .any(ObjectName)
      .any(MethodName)
      .any(TraceEnd));

   ArtifactTypeToken TraceArgument = osee.add(osee.artifactType(377337235L, "Trace Argument", false, Artifact)
      .zeroOrOne(Value)
      .zeroOrOne(ArgumentType));

   ArtifactTypeToken InfoGroup = osee.add(osee.artifactType(2734942363158076063L, "Info Group", false, Artifact)
      .exactlyOne(GroupType));

   ArtifactTypeToken Info = osee.add(osee.artifactType(91126474931227736L, "Info", false, Artifact)
      .exactlyOne(Value));

   ArtifactTypeToken ScriptBatch = osee.add(osee.artifactType(2616144796145104213L, "Script Batch", false, Artifact)
      .exactlyOne(BatchId)
      .exactlyOne(ExecutionDate)
      .exactlyOne(MachineName));

   ArtifactTypeToken ScriptResultTypes = osee.add(osee.artifactType(8756764540L, "Script Result Types", false, Artifact));

   ArtifactTypeToken ResolutionTypes = osee.add(osee.artifactType(8756764541L, "Resolution Types", false, Artifact));

   ArtifactTypeToken TestPointGroupTypes = osee.add(osee.artifactType(8756764542L, "Test Point Group Types", false, Artifact));

   ArtifactTypeToken MachineNames = osee.add(osee.artifactType(2044731485L, "MachineNames", false, Artifact));
   // @formatter:on
}
