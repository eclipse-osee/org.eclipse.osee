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

import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.osee;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeDate;
import org.eclipse.osee.framework.core.data.AttributeTypeInputStream;
import org.eclipse.osee.framework.core.data.AttributeTypeInteger;
import org.eclipse.osee.framework.core.data.AttributeTypeJoin;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.core.data.computed.ComputedSoftwareCriticalityIndex;
import org.eclipse.osee.framework.core.enums.token.ComponentAttributeType;
import org.eclipse.osee.framework.core.enums.token.CsciAttributeType;
import org.eclipse.osee.framework.core.enums.token.DataRightsClassificationAttributeType;
import org.eclipse.osee.framework.core.enums.token.FdalAttributeType;
import org.eclipse.osee.framework.core.enums.token.FeatureValueAttributeType;
import org.eclipse.osee.framework.core.enums.token.FunctionalGroupingAttributeType;
import org.eclipse.osee.framework.core.enums.token.GfeCfeAttributeType;
import org.eclipse.osee.framework.core.enums.token.IdalAttributeType;
import org.eclipse.osee.framework.core.enums.token.InterfaceLogicalTypeAttribute;
import org.eclipse.osee.framework.core.enums.token.InterfaceMessagePeriodicityAttributeType;
import org.eclipse.osee.framework.core.enums.token.InterfaceMessageRateAttributeType;
import org.eclipse.osee.framework.core.enums.token.InterfaceMessageTypeAttributeType;
import org.eclipse.osee.framework.core.enums.token.InterfacePlatformTypeUnitsAttribute;
import org.eclipse.osee.framework.core.enums.token.InterfaceStructureCategoryAttribute;
import org.eclipse.osee.framework.core.enums.token.LegacyDalAttributeType;
import org.eclipse.osee.framework.core.enums.token.PageOrientationAttributeType;
import org.eclipse.osee.framework.core.enums.token.PartitionAttributeType;
import org.eclipse.osee.framework.core.enums.token.ProductTypeAttributeType;
import org.eclipse.osee.framework.core.enums.token.QualificationMethodAttributeType;
import org.eclipse.osee.framework.core.enums.token.SafetySeverityAttributeType;
import org.eclipse.osee.framework.core.enums.token.SeverityCategoryAttributeType;
import org.eclipse.osee.framework.core.enums.token.SoftwareControlCategoryAttributeType;
import org.eclipse.osee.framework.core.enums.token.SoftwareCriticalityIndexAttributeType;
import org.eclipse.osee.framework.core.enums.token.SubsystemAttributeType;
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
   AttributeTypeString AccessContextId = osee.createString(1152921504606847102L, "Access Context Id", MediaType.TEXT_PLAIN, "", DisplayHint.SingleLine);
   AttributeTypeString Acronym = osee.createString(4723834159825897915L, "Acronym", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean Active = osee.createBoolean(1152921504606847065L, "Active", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Annotation = osee.createString(1152921504606847094L, "Annotation", MediaType.TEXT_PLAIN, "");
   AttributeTypeString AtsActionSearch = osee.createString(72063457009467643L, "ATS Action Search", MediaType.TEXT_PLAIN, "Saved ATS Quick Searches.");
   AttributeTypeString AtsUserConfig = osee.createString(2348752981434455L, "ATS User Config", MediaType.TEXT_PLAIN, "Saved ATS Configures");
   AttributeTypeArtifactId BaselinedBy = osee.createArtifactIdNoTag(1152921504606847247L, "Baselined By", MediaType.TEXT_PLAIN, "");
   AttributeTypeDate BaselinedTimestamp = osee.createDateNoTag(1152921504606847244L, "Baselined Timestamp", AttributeTypeToken.TEXT_CALENDAR, "");
   CsciAttributeType CSCI = osee.createEnum(new CsciAttributeType());
   AttributeTypeString Category = osee.createString(1152921504606847121L, "Category", MediaType.TEXT_PLAIN, "");
   AttributeTypeInteger CircuitBreakerId = osee.createIntegerNoTag(188458869981238L, "Circuit Breaker Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString City = osee.createString(1152921504606847068L, "City", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Company = osee.createString(1152921504606847066L, "Company", MediaType.TEXT_PLAIN, "");
   AttributeTypeString CompanyTitle = osee.createString(1152921504606847067L, "Company Title", MediaType.TEXT_PLAIN, "");
   ComponentAttributeType Component = osee.createEnum(new ComponentAttributeType());
   AttributeTypeString ContentUrl = osee.createString(1152921504606847100L, "Content URL", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Country = osee.createString(1152921504606847072L, "Country", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DataRightsBasis = osee.createString(72057594037928276L, "Data Rights Basis", MediaType.TEXT_PLAIN, "The basis or rationale for the Data Rights Classification selected such as developed under program X");
   DataRightsClassificationAttributeType DataRightsClassification = osee.createEnum(new DataRightsClassificationAttributeType());
   AttributeTypeBoolean DefaultGroup = osee.createBoolean(1152921504606847086L, "Default Group", MediaType.TEXT_PLAIN, "Specifies whether to automatically add new users into this group");
   AttributeTypeString DefaultMailServer = osee.createString(1152921504606847063L, "osee.Default Mail Server", MediaType.TEXT_PLAIN, "fully qualified name of the machine running the SMTP server which will be used by default for sending email");
   AttributeTypeString DefaultTrackingBranch = osee.createString(1152921504606847709L, "Default Tracking Branch", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DefaultValue = osee.createString(2221435335730390044L, "Default Value", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Description = osee.createString(1152921504606847090L, "Description", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean Developmental = osee.createBooleanNoTag(1152921504606847137L, "Developmental", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Dictionary = osee.createString(1152921504606847083L, "Dictionary", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DisplayText = osee.createStringNoTag(188458869981237L, "Display Text", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DoorsHierarchy = osee.createString(1873562488122323009L, "Doors Hierarchy", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DoorsId = osee.createString(8243262488122393232L, "Doors ID", MediaType.TEXT_PLAIN, "External doors id for import support", DisplayHint.SingleLine);
   AttributeTypeString DoorsModId = osee.createString(5326122488147393161L, "Doors Mod ID", MediaType.TEXT_PLAIN, "Modified External doors id for import support", DisplayHint.SingleLine );
   AttributeTypeString Effectivity = osee.createStringNoTag(1152921504606847108L, "Effectivity", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Email = osee.createString(1152921504606847082L, "Email", MediaType.TEXT_PLAIN, "");
   AttributeTypeString ExcludePath = osee.createString(1152921504606847708L, "Exclude Path", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Extension = osee.createString(1152921504606847064L, "Extension", MediaType.TEXT_PLAIN, "");
   AttributeTypeString FavoriteBranch = osee.createStringNoTag(1152921504606847074L, "Favorite Branch", MediaType.TEXT_PLAIN, "");
   AttributeTypeString FaxPhone = osee.createString(1152921504606847081L, "Fax Phone", MediaType.TEXT_PLAIN, "");
   FdalAttributeType FDAL = osee.createEnum(new FdalAttributeType());
   AttributeTypeString FdalRationale = osee.createStringNoTag(926274413268034710L, "FDAL Rationale", MediaType.TEXT_PLAIN, "Functional Development Assurance Level Rationale");
   AttributeTypeBoolean FeatureMultivalued = osee.createBoolean(3641431177461038717L, "Feature Multivalued", MediaType.TEXT_PLAIN, "");
   FeatureValueAttributeType FeatureValueType = osee.createEnum(new FeatureValueAttributeType());
   ProductTypeAttributeType ProductApplicability = osee.createEnum(new ProductTypeAttributeType());
   AttributeTypeString FileSystemPath = osee.createString(1152921504606847707L, "File System Path", MediaType.TEXT_PLAIN, "");
   AttributeTypeString FunctionalCategory = osee.createString(1152921504606847871L, "Functional Category", MediaType.TEXT_PLAIN, "Functional Category in support of System Safety Report");
   FunctionalGroupingAttributeType FunctionalGrouping = osee.createEnum(new FunctionalGroupingAttributeType());
   AttributeTypeString GeneralStringData = osee.createStringNoTag(1152921504606847096L, "General String Data", MediaType.TEXT_PLAIN, "");
   GfeCfeAttributeType GfeCfe = osee.createEnum(new GfeCfeAttributeType());
   AttributeTypeString GitBuildId = osee.createString(1714059195608838442L, "Git Build-Id", MediaType.TEXT_PLAIN, "Build-Id embedded in Git commit message that is intended to be immutable even during rebase and amending the commit");
   AttributeTypeString GitChangeId = osee.createString(1152921504606847702L, "Git Change-Id", MediaType.TEXT_PLAIN, "Change-Id embedded in Git commit message that is intended to be immutable even during rebase and amending the commit");
   AttributeTypeDate GitCommitAuthorDate = osee.createDate(1152921504606847704L, "Git Commit Author Date", MediaType.TEXT_PLAIN, "when this commit was originally made");
   AttributeTypeString GitCommitMessage = osee.createString(1152921504606847705L, "Git Commit Message", MediaType.TEXT_PLAIN, "Full message minus Change-Id");
   AttributeTypeString GitCommitSha = osee.createString(1152921504606847703L, "Git Commit SHA", MediaType.TEXT_PLAIN, "SHA-1 checksum of the Git commit's content and header");
   AttributeTypeString GitRepoName = osee.createString(1152921504606847706L, "Git Repo Name", MediaType.TEXT_PLAIN, "Name of Relevant Git Repository");
   AttributeTypeString GitBranchName = osee.createString(1152921504606847819L, "Git Branch Name", MediaType.TEXT_PLAIN, "Name of Relevant Branch on Git Repository");
   AttributeTypeString GraphitiDiagram = osee.createStringNoTag(1152921504606847319L, "Graphiti Diagram", MediaType.TEXT_XML, "xml definition of an Eclipse Graphiti Diagram", "diagram");
   AttributeTypeString Hazard = osee.createString(1152921504606847138L, "Hazard", MediaType.TEXT_PLAIN, "");
   AttributeTypeString HtmlContent = osee.createString(1152921504606847869L, "HTML Content", MediaType.TEXT_HTML, "HTML format text must be a valid xhtml file");
   AttributeTypeBoolean IaPlan = osee.createBoolean(1253931514616857210L, "IA Plan", MediaType.TEXT_PLAIN, "");
   IdalAttributeType IDAL = osee.createEnum(new IdalAttributeType());
   AttributeTypeString IdalRationale = osee.createStringNoTag(2517743638468399405L, "IDAL Rationale", MediaType.TEXT_PLAIN, "Item Development Assurance Level Rationale");
   AttributeTypeString IdValue = osee.createString(72057896045641815L, "Id Value", MediaType.TEXT_PLAIN, "Key-Value attribute where key (attribute id) is supplied by framework and value is supplied by user.", DisplayHint.SingleLine);
   AttributeTypeString InterfaceMessageNumber = osee.createString(2455059983007225768L, "Interface Message Number", MediaType.TEXT_PLAIN, "");
   InterfaceMessagePeriodicityAttributeType InterfaceMessagePeriodicity = osee.createEnum(new InterfaceMessagePeriodicityAttributeType());
   InterfaceMessageRateAttributeType InterfaceMessageRate = osee.createEnum(new InterfaceMessageRateAttributeType());
   InterfaceMessageTypeAttributeType InterfaceMessageType = osee.createEnum(new InterfaceMessageTypeAttributeType());
   AttributeTypeBoolean InterfaceMessageWriteAccess = osee.createBoolean(2455059983007225754L, "Interface Message Write Access", MediaType.TEXT_PLAIN, "Message has write access");
   AttributeTypeString InterfaceNodeAddress= osee.createString(5726596359647826656L,"Interface Node Address",MediaType.TEXT_PLAIN,"");
   AttributeTypeString InterfaceNodeBackgroundColor = osee.createString(5221290120300474048L,"Interface Node Bg Color",MediaType.TEXT_PLAIN,"");
   InterfaceStructureCategoryAttribute InterfaceStructureCategory = osee.createEnum(new InterfaceStructureCategoryAttribute());
   AttributeTypeInteger InterfaceSubMessageNumber = osee.createInteger(2455059983007225769L, "Interface Sub Message Number", MediaType.TEXT_PLAIN, "");
   AttributeTypeString InterfaceMinSimultaneity = osee.createString(2455059983007225755L, "Interface Minimum Simultaneity", MediaType.TEXT_PLAIN, "");
   AttributeTypeString InterfaceMaxSimultaneity = osee.createString(2455059983007225756L, "Interface Maximum Simultaneity", MediaType.TEXT_PLAIN, "");
   AttributeTypeString InterfaceMinBytesPerSecond = osee.createString(2455059983007225757L, "Interface Minimum Bytes Per Second", MediaType.TEXT_PLAIN, "");
   AttributeTypeString InterfaceMaxBytesPerSecond = osee.createString(2455059983007225758L, "Interface Maximum Bytes Per Second", MediaType.TEXT_PLAIN, "");
   AttributeTypeInteger InterfaceTaskFileType = osee.createInteger(2455059983007225760L, "Interface Task File Type", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean InterfaceElementAlterable = osee.createBoolean(2455059983007225788L, "Interface Element Alterable", MediaType.TEXT_PLAIN, "Element can be altered after creation.");
   AttributeTypeInteger InterfaceElementIndexStart = osee.createInteger(2455059983007225801L, "Interface Element Index Start", MediaType.TEXT_PLAIN, "");
   AttributeTypeInteger InterfaceElementIndexEnd = osee.createInteger(2455059983007225802L, "Interface Element Index End", MediaType.TEXT_PLAIN, "");
   InterfaceLogicalTypeAttribute InterfaceLogicalType = osee.createEnum(new InterfaceLogicalTypeAttribute());
   AttributeTypeInteger InterfaceEnumOrdinal = osee.createInteger(2455059983007225790L, "Interface Enum State", MediaType.TEXT_PLAIN, "");
   AttributeTypeString InterfacePlatformTypeEnumLiteral = osee.createString(2455059983007225803L, "Interface Platform Type Enum Literal", MediaType.TEXT_PLAIN, "");
   InterfacePlatformTypeUnitsAttribute InterfacePlatformTypeUnits = osee.createEnum(new InterfacePlatformTypeUnitsAttribute());
   AttributeTypeString InterfacePlatformTypeValidRangeDescription = osee.createString(2121416901992068417L, "Interface Platform Type Valid Range Desc", MediaType.TEXT_PLAIN, "");
   AttributeTypeString InterfacePlatformTypeMinval = osee.createString(3899709087455064782L, "Interface Platform Type Minval", MediaType.TEXT_PLAIN, "");
   AttributeTypeString InterfacePlatformTypeMaxval = osee.createString(3899709087455064783L, "Interface Platform Type Maxval", MediaType.TEXT_PLAIN, "");
   AttributeTypeString InterfacePlatformTypeBitSize = osee.createString(2455059983007225786L, "Interface Platform Type Bit Size", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean InterfacePlatformType2sComplement = osee.createBoolean(3899709087455064784L, "Interface Platform Type 2sComplement", MediaType.TEXT_PLAIN, "Platform Type is 2's Complement");
   AttributeTypeString InterfacePlatformTypeDefaultValue = osee.createString(2886273464685805413L, "Interface Platform Type Default Value", MediaType.TEXT_PLAIN, "");
   AttributeTypeString InterfacePlatformTypeMsbValue = osee.createString(3899709087455064785L, "Interface Platform Type Msb Value", MediaType.TEXT_PLAIN, "");
   AttributeTypeString InterfacePlatformTypeBitsResolution = osee.createString(3899709087455064786L, "Interface Platform Type Bits Resolution", MediaType.TEXT_PLAIN, "");
   AttributeTypeString InterfacePlatformTypeCompRate = osee.createString(3899709087455064787L, "Interface Platform Type Comp Rate", MediaType.TEXT_PLAIN, "");
   AttributeTypeString InterfacePlatformTypeAnalogAccuracy = osee.createString(3899709087455064788L, "Interface Platform Type Analog Accuracy", MediaType.TEXT_PLAIN, "");
   AttributeTypeString InterfaceTransportType = osee.createString(4522496963078776538L, "Interface Transport Type", MediaType.TEXT_PLAIN, "Transport Type of Interface Connection");
   AttributeTypeInputStream ImageContent = osee.createInputStreamNoTag(1152921504606847868L, "Image Content", AttributeTypeToken.IMAGE, "Binary Image content");
   AttributeTypeString JavaCode = osee.createString(1253931606616948117L, "Java Code", MediaType.TEXT_PLAIN, "code that can be compiled into java");
   LegacyDalAttributeType LegacyDal = osee.createEnum(new LegacyDalAttributeType());
   AttributeTypeString LegacyId = osee.createStringNoTag(1152921504606847107L, "Legacy Id", MediaType.TEXT_PLAIN, "unique identifier from an external system");
   AttributeTypeString LoginId = osee.createString(239475839435799L, "Login Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString MaintainerText = osee.createStringNoTag(188458874335285L, "Maintainer Text", MediaType.TEXT_PLAIN, "");
   AttributeTypeString MimBranchPreferences = osee.createString(6600561480190271962L,"MIM Branch Preferences",MediaType.TEXT_PLAIN,"");
   AttributeTypeString MimColumnPreferences = osee.createString(5383153557691494043L,"MIM Column Preferences",MediaType.TEXT_PLAIN,"");
   AttributeTypeString MobilePhone = osee.createString(1152921504606847080L, "Mobile Phone", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Name = osee.createString(1152921504606847088L, "Name", MediaType.TEXT_PLAIN, "Descriptive Name");
   AttributeTypeInputStream NativeContent = osee.createInputStreamNoTag(1152921504606847097L, "Native Content", MediaType.APPLICATION_OCTET_STREAM, "content that will be edited by a native program");
   AttributeTypeString Notes = osee.createString(1152921504606847085L, "Notes", MediaType.TEXT_PLAIN, "");
   AttributeTypeString OseeAppDefinition = osee.createStringNoTag(1152921504606847380L, "Osee App Definition", MediaType.APPLICATION_JSON, "Json that defines the parameters, action(s), and metadata of an OSEE Single Page App");
   PageOrientationAttributeType PageOrientation = osee.createEnum(new PageOrientationAttributeType());
   AttributeTypeString ParagraphNumber = osee.createString(1152921504606847101L, "Paragraph Number", MediaType.TEXT_PLAIN, "This is the corresponding section number from the outline of document from which this artifact was imported");
   PartitionAttributeType Partition = osee.createEnum(new PartitionAttributeType());
   AttributeTypeString Phone = osee.createString(1152921504606847079L, "Phone", MediaType.TEXT_PLAIN, "");
   AttributeTypeString PlainTextContent = osee.createString(1152921504606847866L, "Plain Text Content", MediaType.TEXT_PLAIN, "plain text file");
   AttributeTypeBoolean PotentialSecurityImpact = osee.createBoolean(1152921504606847109L, "Potential Security Impact", MediaType.TEXT_PLAIN, "");
   AttributeTypeString ProductLinePreferences = osee.createStringNoTag(582562585958993670L, "Product Line Preferences", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean PublishInline = osee.createBoolean(1152921504606847122L, "PublishInline", MediaType.TEXT_PLAIN, "");
   QualificationMethodAttributeType QualificationMethod = osee.createEnum(new QualificationMethodAttributeType());
   AttributeTypeString RelationOrder = osee.createStringNoTag(1152921504606847089L, "Relation Order", MediaType.TEXT_PLAIN, "Defines relation ordering information", DisplayHint.MultiLine);
   AttributeTypeString RendererOptions = osee.createString(904L, "Renderer Options", MediaType.APPLICATION_JSON, "", "txt");
   AttributeTypeString RepositoryUrl = osee.createString(1152921504606847700L, "Repository URL", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean RequireConfirmation = osee.createBooleanNoTag(188458869981239L, "Require Confirmation", MediaType.TEXT_PLAIN, "");
   AttributeTypeInteger ReviewId = osee.createInteger(1152921504606847245L, "Review Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString ReviewStoryId = osee.createString(1152921504606847246L, "Review Story Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString SFHA = osee.createString(1152921504606847140L, "SFHA", MediaType.TEXT_PLAIN, "");
   AttributeTypeString SafetyImpact = osee.createString(1684721504606847095L, "Safety Impact", MediaType.TEXT_PLAIN, "");
   SafetySeverityAttributeType SafetySeverity = osee.createEnum(new SafetySeverityAttributeType());
   SeverityCategoryAttributeType SeverityCategory = osee.createEnum(new SeverityCategoryAttributeType());
   SoftwareControlCategoryAttributeType SoftwareControlCategory = osee.createEnum(new SoftwareControlCategoryAttributeType());
   AttributeTypeString SoftwareControlCategoryRationale = osee.createStringNoTag(750929222178534710L, "Software Control Category Rationale", MediaType.TEXT_PLAIN, "");
   SoftwareCriticalityIndexAttributeType SwCI = osee.createEnum(new SoftwareCriticalityIndexAttributeType());
   ComputedSoftwareCriticalityIndex SoftwareCriticalityIndex = osee.createComp(ComputedSoftwareCriticalityIndex::new, 1152921504606847725L, "Safety Criticality Index", "Calculation of SwCI using Safety Severity and Software Control Category", SoftwareControlCategory, SafetySeverity);
   AttributeTypeBoolean SoftwareSafetyImpact = osee.createBooleanNoTag(8318805403746485981L, "Software Safety Impact", MediaType.TEXT_PLAIN, "Software Safety Impact");
   AttributeTypeString StartPage = osee.createStringNoTag(1152921504606847135L, "osee.wi.Start Page", MediaType.TEXT_PLAIN, "");
   AttributeTypeString State = osee.createString(1152921504606847070L, "State", MediaType.TEXT_PLAIN, "");
   AttributeTypeString StaticId = osee.createString(1152921504606847095L, "Static Id", MediaType.TEXT_PLAIN, "", DisplayHint.SingleLine);
   AttributeTypeString Street = osee.createString(1152921504606847069L, "Street", MediaType.TEXT_PLAIN, "");
   AttributeTypeString SubjectMatterExpert = osee.createString(72057594037928275L, "Subject Matter Expert", MediaType.TEXT_PLAIN, "Name of the Subject Matter Expert");
   SubsystemAttributeType Subsystem = osee.createEnum(new SubsystemAttributeType());
   AttributeTypeBoolean TechnicalPerformanceParameter = osee.createBooleanNoTag(1152921504606847123L, "Technical Performance Parameter", MediaType.TEXT_PLAIN, "");
   AttributeTypeString TemplateMatchCriteria = osee.createString(1152921504606847087L, "Template Match Criteria", MediaType.TEXT_PLAIN, "Criteria that determines what template is selected ie: 'Render Artifact PresentationType Option'");
   TestProcedureStatusAttributeType TestProcedureStatus = osee.createEnum(new TestProcedureStatusAttributeType());
   AttributeTypeString TestScriptGuid = osee.createString(1152921504606847301L, "Test Script GUID", MediaType.TEXT_PLAIN, "Test Case GUID");
   TisTestCategoryAttributeType TisTestCategory = osee.createEnumNoTag(new TisTestCategoryAttributeType());
   AttributeTypeString TisTestNumber = osee.createStringNoTag(1152921504606847116L, "TIS Test Number", MediaType.TEXT_PLAIN, "Test Number");
   TisTestTypeAttributeType TisTestType = osee.createEnumNoTag(new TisTestTypeAttributeType());
   AttributeTypeString UriGeneralStringData = osee.createStringNoTag(1152921504606847381L, "Uri General String Data", AttributeTypeToken.TEXT_URI_LIST, "");
   AttributeTypeArtifactId UserArtifactId = osee.createArtifactIdNoTag(1152921504606847701L, "User Artifact Id", MediaType.TEXT_PLAIN, "Artifact id of an artifact of type User");
   AttributeTypeString UserId = osee.createString(1152921504606847073L, "User Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString UserSettings = osee.createString(1152921504606847076L, "User Settings", MediaType.TEXT_PLAIN, "", "xml");
   AttributeTypeString Value = osee.createString(861995499338466438L, "Value", MediaType.TEXT_PLAIN, "");
   AttributeTypeString VerificationAcceptanceCriteria = osee.createStringNoTag(1152921504606847117L, "Verification Acceptance Criteria", MediaType.TEXT_PLAIN, "");
   VerificationEventAttributeType VerificationEvent = osee.createEnum(new VerificationEventAttributeType());
   VerificationLevelAttributeType VerificationLevel = osee.createEnum(new VerificationLevelAttributeType());
   AttributeTypeString WebPreferences = osee.createString(1152921504606847386L, "Web Preferences", MediaType.TEXT_PLAIN, "", "xml");
   AttributeTypeString Website = osee.createString(1152921504606847084L, "Website", AttributeTypeToken.TEXT_URI_LIST, "");
   AttributeTypeString WholeWordContent = osee.createString(1152921504606847099L, "Whole Word Content", AttributeTypeToken.APPLICATION_MSWORD, "value must comply with WordML xml schema", DisplayHint.NoGeneralRender);
   AttributeTypeString WordOleData = osee.createStringNoTag(1152921504606847092L, "Word Ole Data", AttributeTypeToken.APPLICATION_MSWORD, "Word Ole Data");
   AttributeTypeString WordTemplateContent = osee.createString(1152921504606847098L, "Word Template Content", AttributeTypeToken.APPLICATION_MSWORD, "value must comply with WordML xml schema", DisplayHint.MultiLine);
   AttributeTypeString WorkData = osee.createStringNoTag(1152921504606847126L, "osee.wi.Work Data", MediaType.TEXT_XML, "");
   AttributeTypeString WorkDescription = osee.createStringNoTag(1152921504606847129L, "osee.wi.Work Description", MediaType.TEXT_PLAIN, "");
   AttributeTypeString WorkId = osee.createStringNoTag(1152921504606847127L, "osee.wi.Work Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString WorkParentId = osee.createStringNoTag(1152921504606847130L, "osee.wi.Work Parent Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString WorkTransition = osee.createStringNoTag(1152921504606847133L, "osee.wi.Work Transition", MediaType.TEXT_PLAIN, "");
   AttributeTypeString WorkType = osee.createStringNoTag(1152921504606847128L, "osee.wi.Work Type", MediaType.TEXT_PLAIN, "");
   AttributeTypeString XViewerCustomization = osee.createString(1152921504606847077L, "XViewer Customization", MediaType.TEXT_XML, "");
   AttributeTypeString XViewerDefaults = osee.createString(1152921504606847078L, "XViewer Defaults", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Zip = osee.createString(1152921504606847071L, "Zip", MediaType.TEXT_PLAIN, "");
   // @formatter:on

   AttributeTypeJoin NameWord = osee.attributeTypeJoin("Name and Word", Name, WordTemplateContent);
}