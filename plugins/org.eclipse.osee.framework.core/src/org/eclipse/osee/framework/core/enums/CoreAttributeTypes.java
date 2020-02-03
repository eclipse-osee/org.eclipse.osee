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

import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.osee;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeBranchId;
import org.eclipse.osee.framework.core.data.AttributeTypeDate;
import org.eclipse.osee.framework.core.data.AttributeTypeDouble;
import org.eclipse.osee.framework.core.data.AttributeTypeInputStream;
import org.eclipse.osee.framework.core.data.AttributeTypeInteger;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.DynamicEnumAttributeType;
import org.eclipse.osee.framework.core.enums.token.ComponentAttributeType;
import org.eclipse.osee.framework.core.enums.token.CsciAttributeType;
import org.eclipse.osee.framework.core.enums.token.DataRightsClassificationAttributeType;
import org.eclipse.osee.framework.core.enums.token.FdalAttributeType;
import org.eclipse.osee.framework.core.enums.token.FeatureValueAttributeType;
import org.eclipse.osee.framework.core.enums.token.FunctionalGroupingAttributeType;
import org.eclipse.osee.framework.core.enums.token.GfeCfeAttributeType;
import org.eclipse.osee.framework.core.enums.token.HazardSeverityAttributeType;
import org.eclipse.osee.framework.core.enums.token.IdalAttributeType;
import org.eclipse.osee.framework.core.enums.token.LegacyDalAttributeType;
import org.eclipse.osee.framework.core.enums.token.PageOrientationAttributeType;
import org.eclipse.osee.framework.core.enums.token.PartitionAttributeType;
import org.eclipse.osee.framework.core.enums.token.QualificationMethodAttributeType;
import org.eclipse.osee.framework.core.enums.token.SafetySeverityAttributeType;
import org.eclipse.osee.framework.core.enums.token.SeverityCategoryAttributeType;
import org.eclipse.osee.framework.core.enums.token.SoftwareControlCategoryAttributeType;
import org.eclipse.osee.framework.core.enums.token.SubsystemAttributeType;
import org.eclipse.osee.framework.core.enums.token.TestFrequencyAttributeType;
import org.eclipse.osee.framework.core.enums.token.TestProcedureStatusAttributeType;
import org.eclipse.osee.framework.core.enums.token.TisTestCategoryAttributeType;
import org.eclipse.osee.framework.core.enums.token.TisTestTypeAttributeType;
import org.eclipse.osee.framework.core.enums.token.VerificationEventAttributeType;
import org.eclipse.osee.framework.core.enums.token.VerificationLevelAttributeType;

/**
 * @author Roberto E. Escobar
 */
public interface CoreAttributeTypes {

   // @formatter:off
   AttributeTypeString AFHA = osee.createString(1152921504606847139L, "AFHA", MediaType.TEXT_PLAIN, "");
   AttributeTypeString AccessContextId = osee.createString(1152921504606847102L, "Access Context Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Acronym = osee.createString(4723834159825897915L, "Acronym", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean Active = osee.createBoolean(1152921504606847065L, "Active", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Annotation = osee.createString(1152921504606847094L, "Annotation", MediaType.TEXT_PLAIN, "");
   AttributeTypeArtifactId ArtifactReference = osee.createArtifactId(1153126013769613560L, "Artifact Reference", AttributeTypeToken.MODEL_OSEE, "Light-weight artifact reference");
   AttributeTypeArtifactId BaselinedBy = osee.createArtifactId(1152921504606847247L, "Baselined By", MediaType.TEXT_PLAIN, "");
   AttributeTypeDate BaselinedTimestamp = osee.createDate(1152921504606847244L, "Baselined Timestamp", AttributeTypeToken.TEXT_CALENDAR, "");
   AttributeTypeBranchId BranchReference = osee.createBranchId(1153126013769613563L, "Branch Reference", AttributeTypeToken.MODEL_OSEE, "Light-weight branch reference");
   CsciAttributeType CSCI = osee.createEnum(CsciAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString Category = osee.createString(1152921504606847121L, "Category", MediaType.TEXT_PLAIN, "");
   AttributeTypeInteger CircuitBreakerId = osee.createInteger(188458869981238L, "Circuit Breaker Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString City = osee.createString(1152921504606847068L, "City", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean CommonNalRequirement = osee.createBoolean(1152921504606847105L, "Common NAL Requirement", MediaType.TEXT_PLAIN, "Requirement that is common to all NCORE (Networked Common Operating Real-time Environment) Application Layers");
   AttributeTypeString Company = osee.createString(1152921504606847066L, "Company", MediaType.TEXT_PLAIN, "");
   AttributeTypeString CompanyTitle = osee.createString(1152921504606847067L, "Company Title", MediaType.TEXT_PLAIN, "");
   ComponentAttributeType Component = osee.createEnum(ComponentAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString ContentUrl = osee.createString(1152921504606847100L, "Content URL", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Country = osee.createString(1152921504606847072L, "Country", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean CrewInterfaceRequirement = osee.createBoolean(1152921504606847106L, "Crew Interface Requirement", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DataRightsBasis = osee.createString(72057594037928276L, "Data Rights Basis", MediaType.TEXT_PLAIN, "The basis or rationale for the Data Rights Classification selected such as developed under program X");
   DataRightsClassificationAttributeType DataRightsClassification = osee.createEnum(DataRightsClassificationAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeBoolean DefaultGroup = osee.createBoolean(1152921504606847086L, "Default Group", MediaType.TEXT_PLAIN, "Specifies whether to automatically add new users into this group");
   AttributeTypeString DefaultMailServer = osee.createString(1152921504606847063L, "osee.config.Default Mail Server", MediaType.TEXT_PLAIN, "fully qualified name of the machine running the SMTP server which will be used by default for sending email");
   AttributeTypeString DefaultTrackingBranch = osee.createString(1152921504606847709L, "Default Tracking Branch", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DefaultValue = osee.createString(2221435335730390044L, "Default Value", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Description = osee.createString(1152921504606847090L, "Description", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean Developmental = osee.createBoolean(1152921504606847137L, "Developmental", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Dictionary = osee.createString(1152921504606847083L, "Dictionary", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DisplayText = osee.createString(188458869981237L, "Display Text", MediaType.TEXT_PLAIN, "");
   DynamicEnumAttributeType DoorsHierarchy = osee.createEnum(1873562488122323009L, "Doors Hierarchy", MediaType.TEXT_PLAIN, "");
   DynamicEnumAttributeType DoorsId = osee.createEnum(8243262488122393232L, "Doors ID", MediaType.TEXT_PLAIN, "External doors id for import support");
   DynamicEnumAttributeType DoorsModId = osee.createEnum(5326122488147393161L, "Doors Mod ID", MediaType.TEXT_PLAIN, "Modified External doors id for import support");
   AttributeTypeString Effectivity = osee.createString(1152921504606847108L, "Effectivity", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Email = osee.createString(1152921504606847082L, "Email", MediaType.TEXT_PLAIN, "");
   AttributeTypeString ExcludePath = osee.createString(1152921504606847708L, "Exclude Path", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Extension = osee.createString(1152921504606847064L, "Extension", MediaType.TEXT_PLAIN, "");
   FdalAttributeType FDAL = osee.createEnum(FdalAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString FavoriteBranch = osee.createString(1152921504606847074L, "Favorite Branch", MediaType.TEXT_PLAIN, "");
   AttributeTypeString FaxPhone = osee.createString(1152921504606847081L, "Fax Phone", MediaType.TEXT_PLAIN, "");
   AttributeTypeString FdalRationale = osee.createString(926274413268034710L, "FDAL Rationale", MediaType.TEXT_PLAIN, "Functional Development Assurance Level Rationale");
   AttributeTypeBoolean FeatureMultivalued = osee.createBoolean(3641431177461038717L, "Feature Multivalued", MediaType.TEXT_PLAIN, "");
   FeatureValueAttributeType FeatureValueType = osee.createEnum(FeatureValueAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString FileSystemPath = osee.createString(1152921504606847707L, "File System Path", MediaType.TEXT_PLAIN, "");
   AttributeTypeDouble FtaResults = osee.createDouble(1152921504606847143L, "FTA Results", MediaType.TEXT_PLAIN, "");
   AttributeTypeString FunctionalCategory = osee.createString(1152921504606847871L, "Functional Category", MediaType.TEXT_PLAIN, "Functional Category in support of System Safety Report");
   FunctionalGroupingAttributeType FunctionalGrouping = osee.createEnum(FunctionalGroupingAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString GeneralStringData = osee.createString(1152921504606847096L, "General String Data", MediaType.TEXT_PLAIN, "");
   GfeCfeAttributeType GfeCfe = osee.createEnum(GfeCfeAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString GitChangeId = osee.createString(1152921504606847702L, "Git Change-Id", MediaType.TEXT_PLAIN, "Change-Id embedded in Git commit message that is intended to be immutable even during rebase and amending the commit");
   AttributeTypeDate GitCommitAuthorDate = osee.createDate(1152921504606847704L, "Git Commit Author Date", MediaType.TEXT_PLAIN, "when this commit was originally made");
   AttributeTypeString GitCommitMessage = osee.createString(1152921504606847705L, "Git Commit Message", MediaType.TEXT_PLAIN, "Full message minus Change-Id");
   AttributeTypeString GitCommitSha = osee.createString(1152921504606847703L, "Git Commit SHA", MediaType.TEXT_PLAIN, "SHA-1 checksum of the Git commit's content and header");
   AttributeTypeString GraphitiDiagram = osee.createString(1152921504606847319L, "Graphiti Diagram", MediaType.TEXT_XML, "xml definition of an Eclipse Graphiti Diagram");
   AttributeTypeString Hazard = osee.createString(1152921504606847138L, "Hazard", MediaType.TEXT_PLAIN, "");
   HazardSeverityAttributeType HazardSeverity = osee.createEnum(HazardSeverityAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString HtmlContent = osee.createString(1152921504606847869L, "HTML Content", MediaType.TEXT_HTML, "HTML format text must be a valid xhtml file");
   IdalAttributeType IDAL = osee.createEnum(IdalAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeBoolean IaPlan = osee.createBoolean(1253931514616857210L, "IA Plan", MediaType.TEXT_PLAIN, "");
   AttributeTypeString IdValue = osee.createString(72057896045641815L, "Id Value", MediaType.TEXT_PLAIN, "Key-Value attribute where key (attribute id) is supplied by framework and value is supplied by user.");
   AttributeTypeString IdalRationale = osee.createString(2517743638468399405L, "IDAL Rationale", MediaType.TEXT_PLAIN, "Item Development Assurance Level Rationale");
   AttributeTypeInputStream ImageContent = osee.createInputStream(1152921504606847868L, "Image Content", AttributeTypeToken.IMAGE, "Binary Image content");
   LegacyDalAttributeType LegacyDal = osee.createEnum(LegacyDalAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString LegacyId = osee.createString(1152921504606847107L, "Legacy Id", MediaType.TEXT_PLAIN, "unique identifier from an external system");
   AttributeTypeString LoginId = osee.createString(239475839435799L, "Login Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString MaintainerText = osee.createString(188458874335285L, "Maintainer Text", MediaType.TEXT_PLAIN, "");
   AttributeTypeString MobilePhone = osee.createString(1152921504606847080L, "Mobile Phone", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Name = osee.createString(1152921504606847088L, "Name", MediaType.TEXT_PLAIN, "Descriptive Name");
   AttributeTypeInputStream NativeContent = osee.createInputStream(1152921504606847097L, "Native Content", MediaType.APPLICATION_OCTET_STREAM, "content that will be edited by a native program");
   AttributeTypeString Notes = osee.createString(1152921504606847085L, "Notes", MediaType.TEXT_PLAIN, "");
   AttributeTypeString OseeAppDefinition = osee.createString(1152921504606847380L, "Osee App Definition", MediaType.APPLICATION_JSON, "Json that defines the parameters, action(s), and metadata of an OSEE Single Page App");
   PageOrientationAttributeType PageOrientation = osee.createEnum(PageOrientationAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString ParagraphNumber = osee.createString(1152921504606847101L, "Paragraph Number", MediaType.TEXT_PLAIN, "This is the corresponding section number from the outline of document from which this artifact was imported");
   PartitionAttributeType Partition = osee.createEnum(PartitionAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString Phone = osee.createString(1152921504606847079L, "Phone", MediaType.TEXT_PLAIN, "");
   AttributeTypeString PlainTextContent = osee.createString(1152921504606847866L, "Plain Text Content", MediaType.TEXT_PLAIN, "plain text file");
   AttributeTypeBoolean PotentialSecurityImpact = osee.createBoolean(1152921504606847109L, "Potential Security Impact", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean PublishInline = osee.createBoolean(1152921504606847122L, "PublishInline", MediaType.TEXT_PLAIN, "");
   QualificationMethodAttributeType QualificationMethod = osee.createEnum(QualificationMethodAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString RelationOrder = osee.createString(1152921504606847089L, "Relation Order", MediaType.TEXT_PLAIN, "Defines relation ordering information");
   AttributeTypeString RendererOptions = osee.createString(904L, "Renderer Options", MediaType.APPLICATION_JSON, "");
   AttributeTypeString RepositoryUrl = osee.createString(1152921504606847700L, "Repository URL", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean RequireConfirmation = osee.createBoolean(188458869981239L, "Require Confirmation", MediaType.TEXT_PLAIN, "");
   AttributeTypeInteger ReviewId = osee.createInteger(1152921504606847245L, "Review Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString ReviewStoryId = osee.createString(1152921504606847246L, "Review Story Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString SFHA = osee.createString(1152921504606847140L, "SFHA", MediaType.TEXT_PLAIN, "");
   AttributeTypeString SafetyImpact = osee.createString(1684721504606847095L, "Safety Impact", MediaType.TEXT_PLAIN, "");
   AttributeTypeDouble SafetyObjective = osee.createDouble(1152921504606847142L, "Safety Objective", MediaType.TEXT_PLAIN, "");
   SafetySeverityAttributeType SafetySeverity = osee.createEnum(SafetySeverityAttributeType::new, MediaType.TEXT_PLAIN);
   SeverityCategoryAttributeType SeverityCategory = osee.createEnum(SeverityCategoryAttributeType::new, MediaType.TEXT_PLAIN);
   SoftwareControlCategoryAttributeType SoftwareControlCategory = osee.createEnum(SoftwareControlCategoryAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString SoftwareControlCategoryRationale = osee.createString(750929222178534710L, "Software Control Category Rationale", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean SoftwareSafetyImpact = osee.createBoolean(8318805403746485981L, "Software Safety Impact", MediaType.TEXT_PLAIN, "Software Safety Impact");
   AttributeTypeString StartPage = osee.createString(1152921504606847135L, "osee.wi.Start Page", MediaType.TEXT_PLAIN, "");
   AttributeTypeString State = osee.createString(1152921504606847070L, "State", MediaType.TEXT_PLAIN, "");
   AttributeTypeString StaticId = osee.createString(1152921504606847095L, "Static Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Street = osee.createString(1152921504606847069L, "Street", MediaType.TEXT_PLAIN, "");
   AttributeTypeString SubjectMatterExpert = osee.createString(72057594037928275L, "Subject Matter Expert", MediaType.TEXT_PLAIN, "Name of the Subject Matter Expert");
   SubsystemAttributeType Subsystem = osee.createEnum(SubsystemAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeBoolean TechnicalPerformanceParameter = osee.createBoolean(1152921504606847123L, "Technical Performance Parameter", MediaType.TEXT_PLAIN, "");
   AttributeTypeString TemplateMatchCriteria = osee.createString(1152921504606847087L, "Template Match Criteria", MediaType.TEXT_PLAIN, "Criteria that determines what template is selected ie: 'Render Artifact PresentationType Option'");
   TestFrequencyAttributeType TestFrequency = osee.createEnum(TestFrequencyAttributeType::new, MediaType.TEXT_PLAIN);
   TestProcedureStatusAttributeType TestProcedureStatus = osee.createEnum(TestProcedureStatusAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString TestScriptGuid = osee.createString(1152921504606847301L, "Test Script GUID", MediaType.TEXT_PLAIN, "Test Case GUID");
   TisTestCategoryAttributeType TisTestCategory = osee.createEnum(TisTestCategoryAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString TisTestNumber = osee.createString(1152921504606847116L, "TIS Test Number", MediaType.TEXT_PLAIN, "Test Number");
   TisTestTypeAttributeType TisTestType = osee.createEnum(TisTestTypeAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeBoolean TrainingEffectivity = osee.createBoolean(1152921504606847110L, "Training Effectivity", MediaType.TEXT_PLAIN, "");
   AttributeTypeString UriGeneralStringData = osee.createString(1152921504606847381L, "Uri General String Data", AttributeTypeToken.TEXT_URI_LIST, "");
   AttributeTypeArtifactId UserArtifactId = osee.createArtifactId(1152921504606847701L, "User Artifact Id", MediaType.TEXT_PLAIN, "Artifact id of an artifact of type User");
   AttributeTypeString UserId = osee.createString(1152921504606847073L, "User Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString UserSettings = osee.createString(1152921504606847076L, "User Settings", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Value = osee.createString(861995499338466438L, "Value", MediaType.TEXT_PLAIN, "");
   AttributeTypeString VerificationAcceptanceCriteria = osee.createString(1152921504606847117L, "Verification Acceptance Criteria", MediaType.TEXT_PLAIN, "");
   VerificationEventAttributeType VerificationEvent = osee.createEnum(VerificationEventAttributeType::new, MediaType.TEXT_PLAIN);
   VerificationLevelAttributeType VerificationLevel = osee.createEnum(VerificationLevelAttributeType::new, MediaType.TEXT_PLAIN);
   AttributeTypeString WebPreferences = osee.createString(1152921504606847386L, "Web Preferences", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Website = osee.createString(1152921504606847084L, "Website", AttributeTypeToken.TEXT_URI_LIST, "");
   AttributeTypeString WholeWordContent = osee.createString(1152921504606847099L, "Whole Word Content", AttributeTypeToken.APPLICATION_MSWORD, "value must comply with WordML xml schema");
   AttributeTypeString WordOleData = osee.createString(1152921504606847092L, "Word Ole Data", AttributeTypeToken.APPLICATION_MSWORD, "Word Ole Data");
   AttributeTypeString WordTemplateContent = osee.createString(1152921504606847098L, "Word Template Content", AttributeTypeToken.APPLICATION_MSWORD, "value must comply with WordML xml schema");
   AttributeTypeString WorkData = osee.createString(1152921504606847126L, "osee.wi.Work Data", MediaType.TEXT_XML, "");
   AttributeTypeString WorkDescription = osee.createString(1152921504606847129L, "osee.wi.Work Description", MediaType.TEXT_PLAIN, "");
   AttributeTypeString WorkId = osee.createString(1152921504606847127L, "osee.wi.Work Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString WorkPageName = osee.createString(1152921504606847131L, "osee.wi.Work Page Name", MediaType.TEXT_PLAIN, "");
   AttributeTypeInteger WorkPageOrdinal = osee.createInteger(1152921504606847132L, "osee.wi.Work Page Ordinal", MediaType.TEXT_PLAIN, "");
   AttributeTypeString WorkPageOrientation = osee.createString(1152921504606847134L, "osee.wi.Work Page Orientation", MediaType.TEXT_PLAIN, "");
   AttributeTypeString WorkParentId = osee.createString(1152921504606847130L, "osee.wi.Work Parent Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString WorkTransition = osee.createString(1152921504606847133L, "osee.wi.Work Transition", MediaType.TEXT_PLAIN, "");
   AttributeTypeString WorkType = osee.createString(1152921504606847128L, "osee.wi.Work Type", MediaType.TEXT_PLAIN, "");
   AttributeTypeString XViewerCustomization = osee.createString(1152921504606847077L, "XViewer Customization", MediaType.TEXT_XML, "");
   AttributeTypeString XViewerDefaults = osee.createString(1152921504606847078L, "XViewer Defaults", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Zip = osee.createString(1152921504606847071L, "Zip", MediaType.TEXT_PLAIN, "");
   // @formatter:on
}