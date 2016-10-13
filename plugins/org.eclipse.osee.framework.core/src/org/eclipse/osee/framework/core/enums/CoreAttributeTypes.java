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

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * @author Roberto E. Escobar
 */
public final class CoreAttributeTypes {

   // @formatter:off
   public static final IAttributeType Afha = TokenFactory.createAttributeType(1152921504606847139L, "AFHA");
   public static final IAttributeType AccessContextId = TokenFactory.createAttributeType(1152921504606847102L, "Access Context Id");
   public static final IAttributeType Active = TokenFactory.createAttributeType(1152921504606847065L, "Active");
   public static final IAttributeType Annotation = TokenFactory.createAttributeType(1152921504606847094L, "Annotation");
   public static final IAttributeType ArtifactReference = TokenFactory.createAttributeType(1153126013769613560L, "Artifact Reference");
   public static final IAttributeType PlainTextContent = TokenFactory.createAttributeType(1152921504606847866L, "Plain Text Content");
   public static final IAttributeType BranchReference = TokenFactory.createAttributeType(1153126013769613563L, "Branch Reference");
   public static final IAttributeType Category = TokenFactory.createAttributeType(1152921504606847121L, "Category");
   public static final IAttributeType City = TokenFactory.createAttributeType(1152921504606847068L, "City");
   public static final IAttributeType CommonNalRequirement = TokenFactory.createAttributeType(1152921504606847105L, "Common NAL Requirement");
   public static final IAttributeType Company = TokenFactory.createAttributeType(1152921504606847066L, "Company");
   public static final IAttributeType CompanyTitle = TokenFactory.createAttributeType(1152921504606847067L, "Company Title");
   public static final IAttributeType Component = TokenFactory.createAttributeType(1152921504606847125L, "Component");
   public static final IAttributeType ContentUrl = TokenFactory.createAttributeType(1152921504606847100L, "Content URL");
   public static final IAttributeType Country = TokenFactory.createAttributeType(1152921504606847072L, "Country");
   public static final IAttributeType CrewInterfaceRequirement = TokenFactory.createAttributeType(1152921504606847106L, "Crew Interface Requirement");
   public static final IAttributeType Csci = TokenFactory.createAttributeType(1152921504606847136L, "CSCI");
   public static final IAttributeType DataRightsClassification = TokenFactory.createAttributeType(1152921504606847317L, "Data Rights Classification");
   public static final IAttributeType DataRightsBasis = TokenFactory.createAttributeType(72057594037928276L, "Data Rights Basis");
   public static final IAttributeType SubjectMatterExpert = TokenFactory.createAttributeType(72057594037928275L, "Subject Matter Expert");
   public static final IAttributeType DefaultMailServer = TokenFactory.createAttributeType(1152921504606847063L, "osee.config.Default Mail Server");
   public static final IAttributeType DefaultGroup = TokenFactory.createAttributeType(1152921504606847086L, "Default Group");
   public static final IAttributeType Description = TokenFactory.createAttributeType(1152921504606847090L, "Description");
   public static final IAttributeType Dictionary = TokenFactory.createAttributeType(1152921504606847083L, "Dictionary");
   public static final IAttributeType Effectivity = TokenFactory.createAttributeType(1152921504606847108L, "Effectivity");
   public static final IAttributeType Email = TokenFactory.createAttributeType(1152921504606847082L, "Email");
   public static final IAttributeType Extension = TokenFactory.createAttributeType(1152921504606847064L, "Extension");
   public static final IAttributeType FavoriteBranch = TokenFactory.createAttributeType(1152921504606847074L, "Favorite Branch");
   public static final IAttributeType FaxPhone = TokenFactory.createAttributeType(1152921504606847081L, "Fax Phone");
   public static final IAttributeType FunctionalCategory = TokenFactory.createAttributeType(1152921504606847871L, "Functional Category");
   public static final IAttributeType FunctionalDAL = TokenFactory.createAttributeType(8007959514939954596L, "Functional Development Assurance Level");
   public static final IAttributeType FunctionalDALRationale = TokenFactory.createAttributeType(926274413268034710L, "Functional Development Assurance Level Rationale");
   public static final IAttributeType GeneralStringData = TokenFactory.createAttributeType(1152921504606847096L, "General String Data");
   public static final IAttributeType GfeCfe = TokenFactory.createAttributeType(1152921504606847144L, "GFE / CFE");
   public static final IAttributeType GraphitiDiagram = TokenFactory.createAttributeType(1152921504606847319L, "Graphiti Diagram");
   public static final IAttributeType Hazard = TokenFactory.createAttributeType(1152921504606847138L, "Hazard");
   public static final IAttributeType HazardSeverity = TokenFactory.createAttributeType(1152921504606847141L, "Hazard Severity");
   public static final IAttributeType HTMLContent = TokenFactory.createAttributeType(1152921504606847869L, "HTML Content");
   public static final IAttributeType ImageContent = TokenFactory.createAttributeType(1152921504606847868L, "Image Content");
   public static final IAttributeType IdValue = TokenFactory.createAttributeType(72057896045641815L, "ID Value");
   public static final IAttributeType ItemDAL = TokenFactory.createAttributeType(2612838829556295211L, "Item Development Assurance Level");
   public static final IAttributeType ItemDALRationale = TokenFactory.createAttributeType(2517743638468399405L, "Item Development Assurance Level Rationale");
   public static final IAttributeType LegacyDAL = TokenFactory.createAttributeType(1152921504606847120L, "Legacy Development Assurance Level");
   public static final IAttributeType LegacyId = TokenFactory.createAttributeType(1152921504606847107L, "Legacy Id");
   public static final IAttributeType MobilePhone = TokenFactory.createAttributeType(1152921504606847080L, "Mobile Phone");
   public static final IAttributeType Name = TokenFactory.createAttributeType(1152921504606847088L, "Name");
   public static final IAttributeType NativeContent = TokenFactory.createAttributeType(1152921504606847097L, "Native Content");
   public static final IAttributeType Notes = TokenFactory.createAttributeType(1152921504606847085L, "Notes");
   public static final IAttributeType PageType = TokenFactory.createAttributeType(1152921504606847091L, "Page Type");
   public static final IAttributeType ParagraphNumber = TokenFactory.createAttributeType(1152921504606847101L, "Paragraph Number");
   public static final IAttributeType Partition = TokenFactory.createAttributeType(1152921504606847111L, "Partition");
   public static final IAttributeType Phone = TokenFactory.createAttributeType(1152921504606847079L, "Phone");
   public static final IAttributeType PublishInline = TokenFactory.createAttributeType(1152921504606847122L, "PublishInline");
   public static final IAttributeType QualificationMethod = TokenFactory.createAttributeType(1152921504606847113L, "Qualification Method");
   public static final IAttributeType RendererOptions = TokenFactory.createAttributeType(904, "Renderer Options");
   public static final IAttributeType RelationOrder = TokenFactory.createAttributeType(1152921504606847089L, "Relation Order");
   public static final IAttributeType Sfha = TokenFactory.createAttributeType(1152921504606847140L, "SFHA");
   public static final IAttributeType SeverityCategory = TokenFactory.createAttributeType(1152921504606847114L, "Severity Category");
   public static final IAttributeType SoftwareControlCategory = TokenFactory.createAttributeType(1958401980089733639L, "Software Control Category");
   public static final IAttributeType SoftwareControlCategoryRationale = TokenFactory.createAttributeType(750929222178534710L, "Software Control Category Rationale");
   public static final IAttributeType SoftwareSafetyImpact = TokenFactory.createAttributeType(8318805403746485981L, "Software Safety Impact");
   public static final IAttributeType State = TokenFactory.createAttributeType(1152921504606847070L, "State");
   public static final IAttributeType StaticId = TokenFactory.createAttributeType(1152921504606847095L, "Static Id");
   public static final IAttributeType Street = TokenFactory.createAttributeType(1152921504606847069L, "Street");
   public static final IAttributeType Subsystem = TokenFactory.createAttributeType(1152921504606847112L, "Subsystem");
   public static final IAttributeType TemplateMatchCriteria = TokenFactory.createAttributeType(1152921504606847087L, "Template Match Criteria");
   public static final IAttributeType TestProcedureStatus = TokenFactory.createAttributeType(1152921504606847075L, "Test Procedure Status");
   public static final IAttributeType TestScriptGuid = TokenFactory.createAttributeType(1152921504606847301L, "Test Script GUID");
   public static final IAttributeType UserId = TokenFactory.createAttributeType(1152921504606847073L, "User Id");
   public static final IAttributeType UriGeneralStringData = TokenFactory.createAttributeType(1152921504606847381L, "Uri General String Data");
   public static final IAttributeType UserSettings = TokenFactory.createAttributeType(1152921504606847076L, "User Settings");
   public static final IAttributeType VerificationEvent = TokenFactory.createAttributeType(1152921504606847124L, "Verification Event");
   public static final IAttributeType VerificationLevel = TokenFactory.createAttributeType(1152921504606847115L, "Verification Level");
   public static final IAttributeType VerificationCriteria = TokenFactory.createAttributeType(1152921504606847117L, "Verification Acceptance Criteria");
   public static final IAttributeType Website = TokenFactory.createAttributeType(1152921504606847084L, "Website");
   public static final IAttributeType WebPreferences = TokenFactory.createAttributeType(1152921504606847386L, "Web Preferences");
   public static final IAttributeType WholeWordContent = TokenFactory.createAttributeType(1152921504606847099L, "Whole Word Content");
   public static final IAttributeType WordOleData = TokenFactory.createAttributeType(1152921504606847092L, "Word Ole Data");
   public static final IAttributeType WordTemplateContent = TokenFactory.createAttributeType(1152921504606847098L, "Word Template Content");
   public static final IAttributeType WorkData = TokenFactory.createAttributeType(1152921504606847126L, "osee.wi.Work Data");
   public static final IAttributeType WorkTransition = TokenFactory.createAttributeType(1152921504606847133L, "osee.wi.Transition");
   public static final IAttributeType XViewerCustomization = TokenFactory.createAttributeType(1152921504606847077L, "XViewer Customization");
   public static final IAttributeType XViewerDefaults = TokenFactory.createAttributeType(1152921504606847078L, "XViewer Defaults");
   public static final IAttributeType Zip = TokenFactory.createAttributeType(1152921504606847071L, "Zip");

   // @formatter:on

   private CoreAttributeTypes() {
      // Constants
   }
}
