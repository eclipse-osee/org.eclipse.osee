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

import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Roberto E. Escobar
 */
public final class CoreAttributeTypes {

   // @formatter:off
   public static final AttributeTypeToken Afha = AttributeTypeToken.valueOf(1152921504606847139L, "AFHA");
   public static final AttributeTypeToken AccessContextId = AttributeTypeToken.valueOf(1152921504606847102L, "Access Context Id");
   public static final AttributeTypeToken Active = AttributeTypeToken.valueOf(1152921504606847065L, "Active");
   public static final AttributeTypeToken Annotation = AttributeTypeToken.valueOf(1152921504606847094L, "Annotation");
   public static final AttributeTypeToken ArtifactReference = AttributeTypeToken.valueOf(1153126013769613560L, "Artifact Reference");
   public static final AttributeTypeToken PlainTextContent = AttributeTypeToken.valueOf(1152921504606847866L, "Plain Text Content");
   public static final AttributeTypeToken BranchReference = AttributeTypeToken.valueOf(1153126013769613563L, "Branch Reference");
   public static final AttributeTypeToken Category = AttributeTypeToken.valueOf(1152921504606847121L, "Category");
   public static final AttributeTypeToken CircuitBreakerId = AttributeTypeToken.valueOf(188458869981238L, "Circuit Breaker ID");
   public static final AttributeTypeToken City = AttributeTypeToken.valueOf(1152921504606847068L, "City");
   public static final AttributeTypeToken CommonNalRequirement = AttributeTypeToken.valueOf(1152921504606847105L, "Common NAL Requirement");
   public static final AttributeTypeToken Company = AttributeTypeToken.valueOf(1152921504606847066L, "Company");
   public static final AttributeTypeToken CompanyTitle = AttributeTypeToken.valueOf(1152921504606847067L, "Company Title");
   public static final AttributeTypeToken Component = AttributeTypeToken.valueOf(1152921504606847125L, "Component");
   public static final AttributeTypeToken ContentUrl = AttributeTypeToken.valueOf(1152921504606847100L, "Content URL");
   public static final AttributeTypeToken Country = AttributeTypeToken.valueOf(1152921504606847072L, "Country");
   public static final AttributeTypeToken CrewInterfaceRequirement = AttributeTypeToken.valueOf(1152921504606847106L, "Crew Interface Requirement");
   public static final AttributeTypeToken Csci = AttributeTypeToken.valueOf(1152921504606847136L, "CSCI");
   public static final AttributeTypeToken DataRightsClassification = AttributeTypeToken.valueOf(1152921504606847317L, "Data Rights Classification");
   public static final AttributeTypeToken DataRightsBasis = AttributeTypeToken.valueOf(72057594037928276L, "Data Rights Basis");
   public static final AttributeTypeToken SubjectMatterExpert = AttributeTypeToken.valueOf(72057594037928275L, "Subject Matter Expert");
   public static final AttributeTypeToken DefaultMailServer = AttributeTypeToken.valueOf(1152921504606847063L, "osee.config.Default Mail Server");
   public static final AttributeTypeToken DefaultGroup = AttributeTypeToken.valueOf(1152921504606847086L, "Default Group");
   public static final AttributeTypeToken DefaultTrackingBranch = AttributeTypeToken.valueOf(1152921504606847709L, "Default Tracking Branch");
   public static final AttributeTypeToken Description = AttributeTypeToken.valueOf(1152921504606847090L, "Description");
   public static final AttributeTypeToken Dictionary = AttributeTypeToken.valueOf(1152921504606847083L, "Dictionary");
   public static final AttributeTypeToken DoorsID = AttributeTypeToken.valueOf(8243262488122393232L, "Doors ID");
   public static final AttributeTypeToken DoorsHierarchy = AttributeTypeToken.valueOf(1873562488122323009L, "Doors Hierarchy");
   public static final AttributeTypeToken Effectivity = AttributeTypeToken.valueOf(1152921504606847108L, "Effectivity");
   public static final AttributeTypeToken Email = AttributeTypeToken.valueOf(1152921504606847082L, "Email");
   public static final AttributeTypeToken ExcludePath = AttributeTypeToken.valueOf(1152921504606847708L, "Exclude Path");
   public static final AttributeTypeToken Extension = AttributeTypeToken.valueOf(1152921504606847064L, "Extension");
   public static final AttributeTypeToken FavoriteBranch = AttributeTypeToken.valueOf(1152921504606847074L, "Favorite Branch");
   public static final AttributeTypeToken FaxPhone = AttributeTypeToken.valueOf(1152921504606847081L, "Fax Phone");
   public static final AttributeTypeToken FeatureMultivalued = AttributeTypeToken.valueOf(3641431177461038717L, "Feature Multivalued");
   public static final AttributeTypeToken FeatureValueType = AttributeTypeToken.valueOf(31669009535111027L, "Feature Value Type");
   public static final AttributeTypeToken FileSystemPath = AttributeTypeToken.valueOf(1152921504606847707L, "File System Path");
   public static final AttributeTypeToken FunctionalCategory = AttributeTypeToken.valueOf(1152921504606847871L, "Functional Category");
   public static final AttributeTypeToken FunctionalDAL = AttributeTypeToken.valueOf(8007959514939954596L, "Functional Development Assurance Level");
   public static final AttributeTypeToken FunctionalDALRationale = AttributeTypeToken.valueOf(926274413268034710L, "Functional Development Assurance Level Rationale");
   public static final AttributeTypeToken GeneralStringData = AttributeTypeToken.valueOf(1152921504606847096L, "General String Data");
   public static final AttributeTypeToken GfeCfe = AttributeTypeToken.valueOf(1152921504606847144L, "GFE / CFE");
   public static final AttributeTypeToken GitChangeId = AttributeTypeToken.valueOf(1152921504606847702L, "Git Change-Id");
   public static final AttributeTypeToken GitCommitSHA = AttributeTypeToken.valueOf(1152921504606847703L, "Git Commit SHA");
   public static final AttributeTypeToken GitCommitAuthorDate = AttributeTypeToken.valueOf(1152921504606847704L, "Git Commit Author Date");
   public static final AttributeTypeToken GitCommitMessage = AttributeTypeToken.valueOf(1152921504606847705L, "Git Commit Message");
   public static final AttributeTypeToken GitRepositoryReference = AttributeTypeToken.valueOf(1152921504606847706L, "Git Repository Reference");
   public static final AttributeTypeToken GraphitiDiagram = AttributeTypeToken.valueOf(1152921504606847319L, "Graphiti Diagram");
   public static final AttributeTypeToken Hazard = AttributeTypeToken.valueOf(1152921504606847138L, "Hazard");
   public static final AttributeTypeToken HazardSeverity = AttributeTypeToken.valueOf(1152921504606847141L, "Hazard Severity");
   public static final AttributeTypeToken HTMLContent = AttributeTypeToken.valueOf(1152921504606847869L, "HTML Content");
   public static final AttributeTypeToken ImageContent = AttributeTypeToken.valueOf(1152921504606847868L, "Image Content");
   public static final AttributeTypeToken IdValue = AttributeTypeToken.valueOf(72057896045641815L, "ID Value");
   public static final AttributeTypeToken ItemDAL = AttributeTypeToken.valueOf(2612838829556295211L, "Item Development Assurance Level");
   public static final AttributeTypeToken ItemDALRationale = AttributeTypeToken.valueOf(2517743638468399405L, "Item Development Assurance Level Rationale");
   public static final AttributeTypeToken LegacyDAL = AttributeTypeToken.valueOf(1152921504606847120L, "Legacy Development Assurance Level");
   public static final AttributeTypeToken LegacyId = AttributeTypeToken.valueOf(1152921504606847107L, "Legacy Id");
   public static final AttributeTypeToken MobilePhone = AttributeTypeToken.valueOf(1152921504606847080L, "Mobile Phone");
   public static final AttributeTypeToken Name = AttributeTypeToken.valueOf(1152921504606847088L, "Name");
   public static final AttributeTypeToken NativeContent = AttributeTypeToken.valueOf(1152921504606847097L, "Native Content");
   public static final AttributeTypeToken Notes = AttributeTypeToken.valueOf(1152921504606847085L, "Notes");
   public static final AttributeTypeToken OseeAppDefinition = AttributeTypeToken.valueOf(1152921504606847380L, "Osee App Definition");
   public static final AttributeTypeToken PageType = AttributeTypeToken.valueOf(1152921504606847091L, "Page Type");
   public static final AttributeTypeToken ParagraphNumber = AttributeTypeToken.valueOf(1152921504606847101L, "Paragraph Number");
   public static final AttributeTypeToken Partition = AttributeTypeToken.valueOf(1152921504606847111L, "Partition");
   public static final AttributeTypeToken Phone = AttributeTypeToken.valueOf(1152921504606847079L, "Phone");
   public static final AttributeTypeToken PublishInline = AttributeTypeToken.valueOf(1152921504606847122L, "PublishInline");
   public static final AttributeTypeToken QualificationMethod = AttributeTypeToken.valueOf(1152921504606847113L, "Qualification Method");
   public static final AttributeTypeToken RelationOrder = AttributeTypeToken.valueOf(1152921504606847089L, "Relation Order");
   public static final AttributeTypeToken RendererOptions = AttributeTypeToken.valueOf(904, "Renderer Options");
   public static final AttributeTypeToken RepositoryUrl = AttributeTypeToken.valueOf(1152921504606847700L, "Repository URL");
   public static final AttributeTypeToken RequireConfirmation = AttributeTypeToken.valueOf(188458869981239L, "Require Confirmation");
   public static final AttributeTypeToken Sfha = AttributeTypeToken.valueOf(1152921504606847140L, "SFHA");
   public static final AttributeTypeToken SafetySeverity = AttributeTypeToken.valueOf(846763346271224762L, "Safety Severity");
   public static final AttributeTypeToken SeverityCategory = AttributeTypeToken.valueOf(1152921504606847114L, "Severity Category");
   public static final AttributeTypeToken SoftwareControlCategory = AttributeTypeToken.valueOf(1958401980089733639L, "Software Control Category");
   public static final AttributeTypeToken SoftwareControlCategoryRationale = AttributeTypeToken.valueOf(750929222178534710L, "Software Control Category Rationale");
   public static final AttributeTypeToken SoftwareSafetyImpact = AttributeTypeToken.valueOf(8318805403746485981L, "Software Safety Impact");
   public static final AttributeTypeToken SafetyImpact = AttributeTypeToken.valueOf(1684721504606847095L, "Safety Impact");
   public static final AttributeTypeToken State = AttributeTypeToken.valueOf(1152921504606847070L, "State");
   public static final AttributeTypeToken StaticId = AttributeTypeToken.valueOf(1152921504606847095L, "Static Id");
   public static final AttributeTypeToken Street = AttributeTypeToken.valueOf(1152921504606847069L, "Street");
   public static final AttributeTypeToken Subsystem = AttributeTypeToken.valueOf(1152921504606847112L, "Subsystem");
   public static final AttributeTypeToken TechnicalPerformanceParameter =AttributeTypeToken.valueOf(1152921504606847123L, "Techinical Performance Parameter");
   public static final AttributeTypeToken TemplateMatchCriteria = AttributeTypeToken.valueOf(1152921504606847087L, "Template Match Criteria");
   public static final AttributeTypeToken TestProcedureStatus = AttributeTypeToken.valueOf(1152921504606847075L, "Test Procedure Status");
   public static final AttributeTypeToken TestScriptGuid = AttributeTypeToken.valueOf(1152921504606847301L, "Test Script GUID");
   public static final AttributeTypeToken UserId = AttributeTypeToken.valueOf(1152921504606847073L, "User Id");
   public static final AttributeTypeToken UriGeneralStringData = AttributeTypeToken.valueOf(1152921504606847381L, "Uri General String Data");
   public static final AttributeTypeToken UserArtifactId = AttributeTypeToken.valueOf(1152921504606847701L, "User Artifact Id");
   public static final AttributeTypeToken UserSettings = AttributeTypeToken.valueOf(1152921504606847076L, "User Settings");
   public static final AttributeTypeToken VerificationEvent = AttributeTypeToken.valueOf(1152921504606847124L, "Verification Event");
   public static final AttributeTypeToken VerificationLevel = AttributeTypeToken.valueOf(1152921504606847115L, "Verification Level");
   public static final AttributeTypeToken VerificationCriteria = AttributeTypeToken.valueOf(1152921504606847117L, "Verification Acceptance Criteria");
   public static final AttributeTypeToken Website = AttributeTypeToken.valueOf(1152921504606847084L, "Website");
   public static final AttributeTypeToken WebPreferences = AttributeTypeToken.valueOf(1152921504606847386L, "Web Preferences");
   public static final AttributeTypeToken WholeWordContent = AttributeTypeToken.valueOf(1152921504606847099L, "Whole Word Content");
   public static final AttributeTypeToken WordOleData = AttributeTypeToken.valueOf(1152921504606847092L, "Word Ole Data");
   public static final AttributeTypeToken WordTemplateContent = AttributeTypeToken.valueOf(1152921504606847098L, "Word Template Content");
   public static final AttributeTypeToken WorkData = AttributeTypeToken.valueOf(1152921504606847126L, "osee.wi.Work Data");
   public static final AttributeTypeToken WorkTransition = AttributeTypeToken.valueOf(1152921504606847133L, "osee.wi.Transition");
   public static final AttributeTypeToken XViewerCustomization = AttributeTypeToken.valueOf(1152921504606847077L, "XViewer Customization");
   public static final AttributeTypeToken XViewerDefaults = AttributeTypeToken.valueOf(1152921504606847078L, "XViewer Defaults");
   public static final AttributeTypeToken Zip = AttributeTypeToken.valueOf(1152921504606847071L, "Zip");
   public static final AttributeTypeToken DefaultValue = AttributeTypeToken.valueOf(2221435335730390044L, "Default Value");
   public static final AttributeTypeToken Value = AttributeTypeToken.valueOf(861995499338466438L, "Value");

   // @formatter:on

   private CoreAttributeTypes() {
      // Constants
   }
}
