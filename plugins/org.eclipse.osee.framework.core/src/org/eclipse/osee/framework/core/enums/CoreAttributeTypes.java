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

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeBranchId;
import org.eclipse.osee.framework.core.data.AttributeTypeDate;
import org.eclipse.osee.framework.core.data.AttributeTypeDouble;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeInputStream;
import org.eclipse.osee.framework.core.data.AttributeTypeInteger;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.OrcsTokenService;
import org.eclipse.osee.framework.core.data.OrcsTypeTokenProvider;
import org.eclipse.osee.framework.core.data.OrcsTypeTokens;

/**
 * @author Roberto E. Escobar
 */
public final class CoreAttributeTypes implements OrcsTypeTokenProvider {
   private static final OrcsTypeTokens tokens = new OrcsTypeTokens();

   // @formatter:off
   public static final AttributeTypeString AFHA = tokens.add(AttributeTypeToken.createString(1152921504606847139L, NamespaceToken.OSEE, "AFHA", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString AccessContextId = tokens.add(AttributeTypeToken.createString(1152921504606847102L, NamespaceToken.OSEE, "Access Context Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean Active = tokens.add(AttributeTypeToken.createBoolean(1152921504606847065L, NamespaceToken.OSEE, "Active", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Annotation = tokens.add(AttributeTypeToken.createString(1152921504606847094L, NamespaceToken.OSEE, "Annotation", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeArtifactId ArtifactReference = tokens.add(AttributeTypeToken.createArtifactId(1153126013769613560L, NamespaceToken.OSEE, "Artifact Reference", AttributeTypeToken.MODEL_OSEE, "Light-weight artifact reference"));
   public static final AttributeTypeArtifactId BaselinedBy = tokens.add(AttributeTypeToken.createArtifactId(1152921504606847247L, NamespaceToken.OSEE, "Baselined By", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeDate BaselinedTimestamp = tokens.add(AttributeTypeToken.createDate(1152921504606847244L, NamespaceToken.OSEE, "Baselined Timestamp", AttributeTypeToken.TEXT_CALENDAR, ""));
   public static final AttributeTypeBranchId BranchReference = tokens.add(AttributeTypeToken.createBranchId(1153126013769613563L, NamespaceToken.OSEE, "Branch Reference", AttributeTypeToken.MODEL_OSEE, "Light-weight branch reference"));
   public static final AttributeTypeEnum CSCI = tokens.add(AttributeTypeToken.createEnum(1152921504606847136L, NamespaceToken.OSEE, "CSCI", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Category = tokens.add(AttributeTypeToken.createString(1152921504606847121L, NamespaceToken.OSEE, "Category", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeInteger CircuitBreakerId = tokens.add(AttributeTypeToken.createInteger(188458869981238L, NamespaceToken.OSEE, "Circuit Breaker Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString City = tokens.add(AttributeTypeToken.createString(1152921504606847068L, NamespaceToken.OSEE, "City", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean CommonNalRequirement = tokens.add(AttributeTypeToken.createBoolean(1152921504606847105L, NamespaceToken.OSEE, "Common NAL Requirement", MediaType.TEXT_PLAIN, "Requirement that is common to all NCORE (Networked Common Operating Real-time Environment) Application Layers"));
   public static final AttributeTypeString Company = tokens.add(AttributeTypeToken.createString(1152921504606847066L, NamespaceToken.OSEE, "Company", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString CompanyTitle = tokens.add(AttributeTypeToken.createString(1152921504606847067L, NamespaceToken.OSEE, "Company Title", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum Component = tokens.add(AttributeTypeToken.createEnum(1152921504606847125L, NamespaceToken.OSEE, "Component", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString ContentUrl = tokens.add(AttributeTypeToken.createString(1152921504606847100L, NamespaceToken.OSEE, "Content URL", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Country = tokens.add(AttributeTypeToken.createString(1152921504606847072L, NamespaceToken.OSEE, "Country", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean CrewInterfaceRequirement = tokens.add(AttributeTypeToken.createBoolean(1152921504606847106L, NamespaceToken.OSEE, "Crew Interface Requirement", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DataRightsBasis = tokens.add(AttributeTypeToken.createString(72057594037928276L, NamespaceToken.OSEE, "Data Rights Basis", MediaType.TEXT_PLAIN, "The basis or rationale for the Data Rights Classification selected such as developed under program X"));
   public static final AttributeTypeEnum DataRightsClassification = tokens.add(AttributeTypeToken.createEnum(1152921504606847317L, NamespaceToken.OSEE, "Data Rights Classification", MediaType.TEXT_PLAIN, "Restricted Rights:  Rights are retained by the company\n\nRestricted Rights Mixed:  contains some Restricted Rights that need separation of content with other rights\n\nOther:  does not contain content with Restricted Rights\n\nUnspecified: not yet specified"));
   public static final AttributeTypeBoolean DefaultGroup = tokens.add(AttributeTypeToken.createBoolean(1152921504606847086L, NamespaceToken.OSEE, "Default Group", MediaType.TEXT_PLAIN, "Specifies whether to automatically add new users into this group"));
   public static final AttributeTypeString DefaultMailServer = tokens.add(AttributeTypeToken.createString(1152921504606847063L, NamespaceToken.OSEE, "osee.config.Default Mail Server", MediaType.TEXT_PLAIN, "fully qualified name of the machine running the SMTP server which will be used by default for sending email"));
   public static final AttributeTypeString DefaultTrackingBranch = tokens.add(AttributeTypeToken.createString(1152921504606847709L, NamespaceToken.OSEE, "Default Tracking Branch", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DefaultValue = tokens.add(AttributeTypeToken.createString(2221435335730390044L, NamespaceToken.OSEE, "Default Value", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Description = tokens.add(AttributeTypeToken.createString(1152921504606847090L, NamespaceToken.OSEE, "Description", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean Developmental = tokens.add(AttributeTypeToken.createBoolean(1152921504606847137L, NamespaceToken.OSEE, "Developmental", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Dictionary = tokens.add(AttributeTypeToken.createString(1152921504606847083L, NamespaceToken.OSEE, "Dictionary", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DisplayText = tokens.add(AttributeTypeToken.createString(188458869981237L, NamespaceToken.OSEE, "Display Text", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum DoorsHierarchy = tokens.add(AttributeTypeToken.createEnum(1873562488122323009L, NamespaceToken.OSEE, "Doors Hierarchy", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum DoorsId = tokens.add(AttributeTypeToken.createEnum(8243262488122393232L, NamespaceToken.OSEE, "Doors ID", MediaType.TEXT_PLAIN, "External doors id for import support"));
   public static final AttributeTypeEnum DoorsModId = tokens.add(AttributeTypeToken.createEnum(5326122488147393161L, NamespaceToken.OSEE, "Doors Mod ID", MediaType.TEXT_PLAIN, "Modified External doors id for import support"));
   public static final AttributeTypeString Effectivity = tokens.add(AttributeTypeToken.createString(1152921504606847108L, NamespaceToken.OSEE, "Effectivity", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Email = tokens.add(AttributeTypeToken.createString(1152921504606847082L, NamespaceToken.OSEE, "Email", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString ExcludePath = tokens.add(AttributeTypeToken.createString(1152921504606847708L, NamespaceToken.OSEE, "Exclude Path", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Extension = tokens.add(AttributeTypeToken.createString(1152921504606847064L, NamespaceToken.OSEE, "Extension", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum FDAL = tokens.add(AttributeTypeToken.createEnum(8007959514939954596L, NamespaceToken.OSEE, "FDAL", MediaType.TEXT_PLAIN, "Functional Development Assurance Level"));
   public static final AttributeTypeString FavoriteBranch = tokens.add(AttributeTypeToken.createString(1152921504606847074L, NamespaceToken.OSEE, "Favorite Branch", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString FaxPhone = tokens.add(AttributeTypeToken.createString(1152921504606847081L, NamespaceToken.OSEE, "Fax Phone", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString FdalRationale = tokens.add(AttributeTypeToken.createString(926274413268034710L, NamespaceToken.OSEE, "FDAL Rationale", MediaType.TEXT_PLAIN, "Functional Development Assurance Level Rationale"));
   public static final AttributeTypeBoolean FeatureMultivalued = tokens.add(AttributeTypeToken.createBoolean(3641431177461038717L, NamespaceToken.OSEE, "Feature Multivalued", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum FeatureValueType = tokens.add(AttributeTypeToken.createEnum(31669009535111027L, NamespaceToken.OSEE, "Feature Value Type", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString FileSystemPath = tokens.add(AttributeTypeToken.createString(1152921504606847707L, NamespaceToken.OSEE, "File System Path", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeDouble FtaResults = tokens.add(AttributeTypeToken.createDouble(1152921504606847143L, NamespaceToken.OSEE, "FTA Results", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString FunctionalCategory = tokens.add(AttributeTypeToken.createString(1152921504606847871L, NamespaceToken.OSEE, "Functional Category", MediaType.TEXT_PLAIN, "Functional Category in support of System Safety Report"));
   public static final AttributeTypeEnum FunctionalGrouping = tokens.add(AttributeTypeToken.createEnum(1741310787702764470L, NamespaceToken.OSEE, "Functional Grouping", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString GeneralStringData = tokens.add(AttributeTypeToken.createString(1152921504606847096L, NamespaceToken.OSEE, "General String Data", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum GfeCfe = tokens.add(AttributeTypeToken.createEnum(1152921504606847144L, NamespaceToken.OSEE, "GFE / CFE", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString GitChangeId = tokens.add(AttributeTypeToken.createString(1152921504606847702L, NamespaceToken.OSEE, "Git Change-Id", MediaType.TEXT_PLAIN, "Change-Id embedded in Git commit message that is intended to be immutable even during rebase and amending the commit"));
   public static final AttributeTypeDate GitCommitAuthorDate = tokens.add(AttributeTypeToken.createDate(1152921504606847704L, NamespaceToken.OSEE, "Git Commit Author Date", MediaType.TEXT_PLAIN, "when this commit was originally made"));
   public static final AttributeTypeString GitCommitMessage = tokens.add(AttributeTypeToken.createString(1152921504606847705L, NamespaceToken.OSEE, "Git Commit Message", MediaType.TEXT_PLAIN, "Full message minus Change-Id"));
   public static final AttributeTypeString GitCommitSha = tokens.add(AttributeTypeToken.createString(1152921504606847703L, NamespaceToken.OSEE, "Git Commit SHA", MediaType.TEXT_PLAIN, "SHA-1 checksum of the Git commit's content and header"));
   public static final AttributeTypeString GraphitiDiagram = tokens.add(AttributeTypeToken.createString(1152921504606847319L, NamespaceToken.OSEE, "Graphiti Diagram", MediaType.TEXT_XML, "xml definition of an Eclipse Graphiti Diagram"));
   public static final AttributeTypeString Hazard = tokens.add(AttributeTypeToken.createString(1152921504606847138L, NamespaceToken.OSEE, "Hazard", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum HazardSeverity = tokens.add(AttributeTypeToken.createEnum(1152921504606847141L, NamespaceToken.OSEE, "Hazard Severity", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString HtmlContent = tokens.add(AttributeTypeToken.createString(1152921504606847869L, NamespaceToken.OSEE, "HTML Content", MediaType.TEXT_HTML, "HTML format text must be a valid xhtml file"));
   public static final AttributeTypeEnum IDAL = tokens.add(AttributeTypeToken.createEnum(2612838829556295211L, NamespaceToken.OSEE, "IDAL", MediaType.TEXT_PLAIN, "Item Development Assurance Level"));
   public static final AttributeTypeBoolean IaPlan = tokens.add(AttributeTypeToken.createBoolean(1253931514616857210L, NamespaceToken.OSEE, "IA Plan", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString IdValue = tokens.add(AttributeTypeToken.createString(72057896045641815L, NamespaceToken.OSEE, "Id Value", MediaType.TEXT_PLAIN, "Key-Value attribute where key (attribute id) is supplied by framework and value is supplied by user."));
   public static final AttributeTypeString IdalRationale = tokens.add(AttributeTypeToken.createString(2517743638468399405L, NamespaceToken.OSEE, "IDAL Rationale", MediaType.TEXT_PLAIN, "Item Development Assurance Level Rationale"));
   public static final AttributeTypeInputStream ImageContent = tokens.add(AttributeTypeToken.createInputStream(1152921504606847868L, NamespaceToken.OSEE, "Image Content", AttributeTypeToken.IMAGE, "Binary Image content"));
   public static final AttributeTypeEnum LegacyDal = tokens.add(AttributeTypeToken.createEnum(1152921504606847120L, NamespaceToken.OSEE, "Legacy DAL", MediaType.TEXT_PLAIN, "Legacy Development Assurance Level (original DAL)"));
   public static final AttributeTypeString LegacyId = tokens.add(AttributeTypeToken.createString(1152921504606847107L, NamespaceToken.OSEE, "Legacy Id", MediaType.TEXT_PLAIN, "unique identifier from an external system"));
   public static final AttributeTypeString LoginId = AttributeTypeToken.createString(239475839435799L, NamespaceToken.OSEE, "Login Id", MediaType.TEXT_PLAIN, "");
   public static final AttributeTypeString MaintainerText = tokens.add(AttributeTypeToken.createString(188458874335285L, NamespaceToken.OSEE, "Maintainer Text", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString MobilePhone = tokens.add(AttributeTypeToken.createString(1152921504606847080L, NamespaceToken.OSEE, "Mobile Phone", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Name = tokens.add(AttributeTypeToken.createString(1152921504606847088L, NamespaceToken.OSEE, "Name", MediaType.TEXT_PLAIN, "Descriptive Name"));
   public static final AttributeTypeInputStream NativeContent = tokens.add(AttributeTypeToken.createInputStream(1152921504606847097L, NamespaceToken.OSEE, "Native Content", MediaType.APPLICATION_OCTET_STREAM, "content that will be edited by a native program"));
   public static final AttributeTypeString Notes = tokens.add(AttributeTypeToken.createString(1152921504606847085L, NamespaceToken.OSEE, "Notes", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString OseeAppDefinition = tokens.add(AttributeTypeToken.createString(1152921504606847380L, NamespaceToken.OSEE, "Osee App Definition", MediaType.APPLICATION_JSON, "Json that defines the parameters, action(s), and metadata of an OSEE Single Page App"));
   public static final AttributeTypeEnum PageOrientation = tokens.add(AttributeTypeToken.createEnum(1152921504606847091L, NamespaceToken.OSEE, "Page Orientation", MediaType.TEXT_PLAIN, "Page Orientation: Landscape/Portrait"));
   public static final AttributeTypeString ParagraphNumber = tokens.add(AttributeTypeToken.createString(1152921504606847101L, NamespaceToken.OSEE, "Paragraph Number", MediaType.TEXT_PLAIN, "This is the corresponding section number from the outline of document from which this artifact was imported"));
   public static final AttributeTypeEnum Partition = tokens.add(AttributeTypeToken.createEnum(1152921504606847111L, NamespaceToken.OSEE, "Partition", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Phone = tokens.add(AttributeTypeToken.createString(1152921504606847079L, NamespaceToken.OSEE, "Phone", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString PlainTextContent = tokens.add(AttributeTypeToken.createString(1152921504606847866L, NamespaceToken.OSEE, "Plain Text Content", MediaType.TEXT_PLAIN, "plain text file"));
   public static final AttributeTypeBoolean PotentialSecurityImpact = tokens.add(AttributeTypeToken.createBoolean(1152921504606847109L, NamespaceToken.OSEE, "Potential Security Impact", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean PublishInline = tokens.add(AttributeTypeToken.createBoolean(1152921504606847122L, NamespaceToken.OSEE, "PublishInline", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum QualificationMethod = tokens.add(AttributeTypeToken.createEnum(1152921504606847113L, NamespaceToken.OSEE, "Qualification Method", MediaType.TEXT_PLAIN, "Demonstration:  The operation of the CSCI, or a part of the CSCI, that relies on observable functional operation not requiring the use of instrumentation, special test equipment, or subsequent analysis.\n\nTest:  The operation of the CSCI, or a part of the CSCI, using instrumentation or other special test equipment to collect data for later analysis.\n\nAnalysis:  The processing of accumulated data obtained from other qualification methods.  Examples are reduction, interpretation, or extrapolation of test results.\n\nInspection:  The visual examination of CSCI code, documentation, etc.\n\nSpecial Qualification Methods:  Any special qualification methods for the CSCI, such as special tools, techniques, procedures, facilities, and acceptance limits.\n\nLegacy:  Requirement, design, or implementation has not changed since last qualification (use sparingly - Not to be used with functions implemented in internal software).\n\nUnspecified:  The qualification method has yet to be set."));
   public static final AttributeTypeString RelationOrder = tokens.add(AttributeTypeToken.createString(1152921504606847089L, NamespaceToken.OSEE, "Relation Order", MediaType.TEXT_PLAIN, "Defines relation ordering information"));
   public static final AttributeTypeString RendererOptions = tokens.add(AttributeTypeToken.createString(904L, NamespaceToken.OSEE, "Renderer Options", MediaType.APPLICATION_JSON, ""));
   public static final AttributeTypeString RepositoryUrl = tokens.add(AttributeTypeToken.createString(1152921504606847700L, NamespaceToken.OSEE, "Repository URL", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean RequireConfirmation = tokens.add(AttributeTypeToken.createBoolean(188458869981239L, NamespaceToken.OSEE, "Require Confirmation", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeInteger ReviewId = tokens.add(AttributeTypeToken.createInteger(1152921504606847245L, NamespaceToken.OSEE, "Review Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString ReviewStoryId = tokens.add(AttributeTypeToken.createString(1152921504606847246L, NamespaceToken.OSEE, "Review Story Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString SFHA = tokens.add(AttributeTypeToken.createString(1152921504606847140L, NamespaceToken.OSEE, "SFHA", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString SafetyImpact = tokens.add(AttributeTypeToken.createString(1684721504606847095L, NamespaceToken.OSEE, "Safety Impact", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeDouble SafetyObjective = tokens.add(AttributeTypeToken.createDouble(1152921504606847142L, NamespaceToken.OSEE, "Safety Objective", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum SafetySeverity = tokens.add(AttributeTypeToken.createEnum(846763346271224762L, NamespaceToken.OSEE, "Safety Severity", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum SeverityCategory = tokens.add(AttributeTypeToken.createEnum(1152921504606847114L, NamespaceToken.OSEE, "Severity Category", MediaType.TEXT_PLAIN, "Severity Category Classification"));
   public static final AttributeTypeEnum SoftwareControlCategory = tokens.add(AttributeTypeToken.createEnum(1958401980089733639L, NamespaceToken.OSEE, "Software Control Category", MediaType.TEXT_PLAIN, "Software Control Category Classification"));
   public static final AttributeTypeString SoftwareControlCategoryRationale = tokens.add(AttributeTypeToken.createString(750929222178534710L, NamespaceToken.OSEE, "Software Control Category Rationale", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean SoftwareSafetyImpact = tokens.add(AttributeTypeToken.createBoolean(8318805403746485981L, NamespaceToken.OSEE, "Software Safety Impact", MediaType.TEXT_PLAIN, "Software Safety Impact"));
   public static final AttributeTypeString StartPage = tokens.add(AttributeTypeToken.createString(1152921504606847135L, NamespaceToken.OSEE, "osee.wi.Start Page", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString State = tokens.add(AttributeTypeToken.createString(1152921504606847070L, NamespaceToken.OSEE, "State", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString StaticId = tokens.add(AttributeTypeToken.createString(1152921504606847095L, NamespaceToken.OSEE, "Static Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Street = tokens.add(AttributeTypeToken.createString(1152921504606847069L, NamespaceToken.OSEE, "Street", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString SubjectMatterExpert = tokens.add(AttributeTypeToken.createString(72057594037928275L, NamespaceToken.OSEE, "Subject Matter Expert", MediaType.TEXT_PLAIN, "Name of the Subject Matter Expert"));
   public static final AttributeTypeEnum Subsystem = tokens.add(AttributeTypeToken.createEnum(1152921504606847112L, NamespaceToken.OSEE, "Subsystem", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean TechnicalPerformanceParameter = tokens.add(AttributeTypeToken.createBoolean(1152921504606847123L, NamespaceToken.OSEE, "Technical Performance Parameter", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString TemplateMatchCriteria = tokens.add(AttributeTypeToken.createString(1152921504606847087L, NamespaceToken.OSEE, "Template Match Criteria", MediaType.TEXT_PLAIN, "Criteria that determines what template is selected ie: 'Render Artifact PresentationType Option'"));
   public static final AttributeTypeEnum TestFrequency = tokens.add(AttributeTypeToken.createEnum(1152921504606847103L, NamespaceToken.OSEE, "Test Frequency", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum TestProcedureStatus = tokens.add(AttributeTypeToken.createEnum(1152921504606847075L, NamespaceToken.OSEE, "Test Procedure Status", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString TestScriptGuid = tokens.add(AttributeTypeToken.createString(1152921504606847301L, NamespaceToken.OSEE, "Test Script GUID", MediaType.TEXT_PLAIN, "Test Case GUID"));
   public static final AttributeTypeEnum TisTestCategory = tokens.add(AttributeTypeToken.createEnum(1152921504606847119L, NamespaceToken.OSEE, "TIS Test Category", MediaType.TEXT_PLAIN, "TIS Test Category"));
   public static final AttributeTypeString TisTestNumber = tokens.add(AttributeTypeToken.createString(1152921504606847116L, NamespaceToken.OSEE, "TIS Test Number", MediaType.TEXT_PLAIN, "Test Number"));
   public static final AttributeTypeEnum TisTestType = tokens.add(AttributeTypeToken.createEnum(1152921504606847118L, NamespaceToken.OSEE, "TIS Test Type", MediaType.TEXT_PLAIN, "TIS Test Type"));
   public static final AttributeTypeBoolean TrainingEffectivity = tokens.add(AttributeTypeToken.createBoolean(1152921504606847110L, NamespaceToken.OSEE, "Training Effectivity", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString UriGeneralStringData = tokens.add(AttributeTypeToken.createString(1152921504606847381L, NamespaceToken.OSEE, "Uri General String Data", AttributeTypeToken.TEXT_URI_LIST, ""));
   public static final AttributeTypeArtifactId UserArtifactId = tokens.add(AttributeTypeToken.createArtifactId(1152921504606847701L, NamespaceToken.OSEE, "User Artifact Id", MediaType.TEXT_PLAIN, "Artifact id of an artifact of type User"));
   public static final AttributeTypeString UserId = tokens.add(AttributeTypeToken.createString(1152921504606847073L, NamespaceToken.OSEE, "User Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString UserSettings = tokens.add(AttributeTypeToken.createString(1152921504606847076L, NamespaceToken.OSEE, "User Settings", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Value = tokens.add(AttributeTypeToken.createString(861995499338466438L, NamespaceToken.OSEE, "Value", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString VerificationAcceptanceCriteria = tokens.add(AttributeTypeToken.createString(1152921504606847117L, NamespaceToken.OSEE, "Verification Acceptance Criteria", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum VerificationEvent = tokens.add(AttributeTypeToken.createEnum(1152921504606847124L, NamespaceToken.OSEE, "Verification Event", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum VerificationLevel = tokens.add(AttributeTypeToken.createEnum(1152921504606847115L, NamespaceToken.OSEE, "Verification Level", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString WebPreferences = tokens.add(AttributeTypeToken.createString(1152921504606847386L, NamespaceToken.OSEE, "Web Preferences", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Website = tokens.add(AttributeTypeToken.createString(1152921504606847084L, NamespaceToken.OSEE, "Website", AttributeTypeToken.TEXT_URI_LIST, ""));
   public static final AttributeTypeString WholeWordContent = tokens.add(AttributeTypeToken.createString(1152921504606847099L, NamespaceToken.OSEE, "Whole Word Content", AttributeTypeToken.APPLICATION_MSWORD, "value must comply with WordML xml schema"));
   public static final AttributeTypeString WordOleData = tokens.add(AttributeTypeToken.createString(1152921504606847092L, NamespaceToken.OSEE, "Word Ole Data", AttributeTypeToken.APPLICATION_MSWORD, "Word Ole Data"));
   public static final AttributeTypeString WordTemplateContent = tokens.add(AttributeTypeToken.createString(1152921504606847098L, NamespaceToken.OSEE, "Word Template Content", AttributeTypeToken.APPLICATION_MSWORD, "value must comply with WordML xml schema"));
   public static final AttributeTypeString WorkData = tokens.add(AttributeTypeToken.createString(1152921504606847126L, NamespaceToken.OSEE, "osee.wi.Work Data", MediaType.TEXT_XML, ""));
   public static final AttributeTypeString WorkDescription = tokens.add(AttributeTypeToken.createString(1152921504606847129L, NamespaceToken.OSEE, "osee.wi.Work Description", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString WorkId = tokens.add(AttributeTypeToken.createString(1152921504606847127L, NamespaceToken.OSEE, "osee.wi.Work Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString WorkPageName = tokens.add(AttributeTypeToken.createString(1152921504606847131L, NamespaceToken.OSEE, "osee.wi.Work Page Name", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeInteger WorkPageOrdinal = tokens.add(AttributeTypeToken.createInteger(1152921504606847132L, NamespaceToken.OSEE, "osee.wi.Work Page Ordinal", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString WorkPageOrientation = tokens.add(AttributeTypeToken.createString(1152921504606847134L, NamespaceToken.OSEE, "osee.wi.Work Page Orientation", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString WorkParentId = tokens.add(AttributeTypeToken.createString(1152921504606847130L, NamespaceToken.OSEE, "osee.wi.Work Parent Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString WorkTransition = tokens.add(AttributeTypeToken.createString(1152921504606847133L, NamespaceToken.OSEE, "osee.wi.Work Transition", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString WorkType = tokens.add(AttributeTypeToken.createString(1152921504606847128L, NamespaceToken.OSEE, "osee.wi.Work Type", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString XViewerCustomization = tokens.add(AttributeTypeToken.createString(1152921504606847077L, NamespaceToken.OSEE, "XViewer Customization", MediaType.TEXT_XML, ""));
   public static final AttributeTypeString XViewerDefaults = tokens.add(AttributeTypeToken.createString(1152921504606847078L, NamespaceToken.OSEE, "XViewer Defaults", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Zip = tokens.add(AttributeTypeToken.createString(1152921504606847071L, NamespaceToken.OSEE, "Zip", MediaType.TEXT_PLAIN, ""));
   // @formatter:on

   @Override
   public void registerTypes(OrcsTokenService tokenService) {
      tokens.registerTypes(tokenService);
   }
}