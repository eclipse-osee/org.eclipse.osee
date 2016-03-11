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
   public static final IAttributeType Afha = TokenFactory.createAttributeType(0x10000000000000A3L, "AFHA");
   public static final IAttributeType AccessContextId = TokenFactory.createAttributeType(0x100000000000007EL, "Access Context Id");
   public static final IAttributeType Active = TokenFactory.createAttributeType(0x1000000000000059L, "Active");
   public static final IAttributeType Annotation = TokenFactory.createAttributeType(0x1000000000000076L, "Annotation");
   public static final IAttributeType ArtifactReference = TokenFactory.createAttributeType(0x1000BA00000000F8L, "Artifact Reference");
   public static final IAttributeType PlainTextContent = TokenFactory.createAttributeType(0x100000000000037AL, "Plain Text Content");
   public static final IAttributeType BranchReference = TokenFactory.createAttributeType(0x1000BA00000000FBL, "Branch Reference");
   public static final IAttributeType Category = TokenFactory.createAttributeType(0x1000000000000091L, "Category");
   public static final IAttributeType City = TokenFactory.createAttributeType(0x100000000000005CL, "City");
   public static final IAttributeType CommonNalRequirement = TokenFactory.createAttributeType(0x1000000000000081L, "Common NAL Requirement");
   public static final IAttributeType Company = TokenFactory.createAttributeType(0x100000000000005AL, "Company");
   public static final IAttributeType CompanyTitle = TokenFactory.createAttributeType(0x100000000000005BL, "Company Title");
   public static final IAttributeType Component = TokenFactory.createAttributeType(0x1000000000000095L, "Component");
   public static final IAttributeType ContentUrl = TokenFactory.createAttributeType(0x100000000000007CL, "Content URL");
   public static final IAttributeType Country = TokenFactory.createAttributeType(0x1000000000000060L, "Country");
   public static final IAttributeType CrewInterfaceRequirement = TokenFactory.createAttributeType(0x1000000000000082L, "Crew Interface Requirement");
   public static final IAttributeType Csci = TokenFactory.createAttributeType(0x10000000000000A0L, "CSCI");
   public static final IAttributeType DataRightsClassification = TokenFactory.createAttributeType(0x1000000000000155L, "Data Rights Classification");
   public static final IAttributeType DataRightsBasis = TokenFactory.createAttributeType(0x100000000000154L, "Data Rights Basis");
   public static final IAttributeType SubjectMatterExpert = TokenFactory.createAttributeType(0x100000000000153L, "Subject Matter Expert");
   public static final IAttributeType DefaultMailServer = TokenFactory.createAttributeType(0x1000000000000057L, "osee.config.Default Mail Server");
   public static final IAttributeType DefaultGroup = TokenFactory.createAttributeType(0x100000000000006EL, "Default Group");
   public static final IAttributeType Description = TokenFactory.createAttributeType(0x1000000000000072L, "Description");
   public static final IAttributeType Dictionary = TokenFactory.createAttributeType(0x100000000000006BL, "Dictionary");
   public static final IAttributeType Effectivity = TokenFactory.createAttributeType(0x1000000000000084L, "Effectivity");
   public static final IAttributeType Email = TokenFactory.createAttributeType(0x100000000000006AL, "Email");
   public static final IAttributeType Extension = TokenFactory.createAttributeType(0x1000000000000058L, "Extension");
   public static final IAttributeType FavoriteBranch = TokenFactory.createAttributeType(0x1000000000000062L, "Favorite Branch");
   public static final IAttributeType FaxPhone = TokenFactory.createAttributeType(0x1000000000000069L, "Fax Phone");
   public static final IAttributeType FunctionalCategory = TokenFactory.createAttributeType(0x100000000000037FL, "Functional Category");
   public static final IAttributeType FunctionalDAL = TokenFactory.createAttributeType(0x6F21FCC037B495A4L, "Functional Development Assurance Level");
   public static final IAttributeType FunctionalDALRational = TokenFactory.createAttributeType(0x0CDAC9ABD75FE496L, "Functional Development Assurance Level Rational");
   public static final IAttributeType GeneralStringData = TokenFactory.createAttributeType(0x1000000000000078L, "General String Data");
   public static final IAttributeType GfeCfe = TokenFactory.createAttributeType(0x10000000000000A8L, "GFE / CFE");
   public static final IAttributeType GraphitiDiagram = TokenFactory.createAttributeType(0x1000000000000157L, "Graphiti Diagram");
   public static final IAttributeType Hazard = TokenFactory.createAttributeType(0x10000000000000A2L, "Hazard");
   public static final IAttributeType HazardSeverity = TokenFactory.createAttributeType(0x10000000000000A5L, "Hazard Severity");
   public static final IAttributeType HTMLContent = TokenFactory.createAttributeType(0x100000000000037DL, "HTML Content");
   public static final IAttributeType ImageContent = TokenFactory.createAttributeType(0x100000000000037CL, "Image Content");
   public static final IAttributeType IdValue = TokenFactory.createAttributeType(0x100004651100057L, "ID Value");
   public static final IAttributeType ItemDAL = TokenFactory.createAttributeType(0x2442AB12730CE62BL, "Item Development Assurance Level");
   public static final IAttributeType ItemDALRational = TokenFactory.createAttributeType(0x22F0D27FE668912DL, "Item Development Assurance Level Rational");
   public static final IAttributeType LegacyDAL = TokenFactory.createAttributeType(0x1000000000000090L, "Legacy Development Assurance Level");
   public static final IAttributeType LegacyId = TokenFactory.createAttributeType(0x1000000000000083L, "Legacy Id");
   public static final IAttributeType MobilePhone = TokenFactory.createAttributeType(0x1000000000000068L, "Mobile Phone");
   public static final IAttributeType Name = TokenFactory.createAttributeType(0x1000000000000070L, "Name");
   public static final IAttributeType NativeContent = TokenFactory.createAttributeType(0x1000000000000079L, "Native Content");
   public static final IAttributeType Notes = TokenFactory.createAttributeType(0x100000000000006DL, "Notes");
   public static final IAttributeType PageType = TokenFactory.createAttributeType(0x1000000000000073L, "Page Type");
   public static final IAttributeType ParagraphNumber = TokenFactory.createAttributeType(0x100000000000007DL, "Paragraph Number");
   public static final IAttributeType Partition = TokenFactory.createAttributeType(0x1000000000000087L, "Partition");
   public static final IAttributeType Phone = TokenFactory.createAttributeType(0x1000000000000067L, "Phone");
   public static final IAttributeType PublishInline = TokenFactory.createAttributeType(0x1000000000000092L, "PublishInline");
   public static final IAttributeType QualificationMethod = TokenFactory.createAttributeType(0x1000000000000089L, "Qualification Method");
   public static final IAttributeType RendererOptions = TokenFactory.createAttributeType(0x0000000000000388L, "Renderer Options");
   public static final IAttributeType RelationOrder = TokenFactory.createAttributeType(0x1000000000000071L, "Relation Order");
   public static final IAttributeType Sfha = TokenFactory.createAttributeType(0x10000000000000A4L, "SFHA");
   public static final IAttributeType SeverityCategory = TokenFactory.createAttributeType(0x100000000000008AL, "Severity Category");
   public static final IAttributeType SoftwareControlCategory = TokenFactory.createAttributeType(0x1B2DA4398EAB9207L, "Software Control Category");
   public static final IAttributeType SoftwareControlCategoryRational = TokenFactory.createAttributeType(0x0A6BD62623697536L, "Software Control Category Rational");
   public static final IAttributeType SoftwareSafetyImpact = TokenFactory.createAttributeType(0x7372557095E8C6DDL, "Software Safety Impact");
   public static final IAttributeType State = TokenFactory.createAttributeType(0x100000000000005EL, "State");
   public static final IAttributeType StaticId = TokenFactory.createAttributeType(0x1000000000000077L, "Static Id");
   public static final IAttributeType Street = TokenFactory.createAttributeType(0x100000000000005DL, "Street");
   public static final IAttributeType Subsystem = TokenFactory.createAttributeType(0x1000000000000088L, "Subsystem");
   public static final IAttributeType TemplateMatchCriteria = TokenFactory.createAttributeType(0x100000000000006FL, "Template Match Criteria");
   public static final IAttributeType TestProcedureStatus = TokenFactory.createAttributeType(0x1000000000000063L, "Test Procedure Status");
   public static final IAttributeType TestScriptGuid = TokenFactory.createAttributeType(0x1000000000000145L, "Test Script GUID");
   public static final IAttributeType UserId = TokenFactory.createAttributeType(0x1000000000000061L, "User Id");
   public static final IAttributeType UriGeneralStringData = TokenFactory.createAttributeType(0x1000000000000195L, "Uri General String Data");
   public static final IAttributeType UserSettings = TokenFactory.createAttributeType(0x1000000000000064L, "User Settings");
   public static final IAttributeType VerificationEvent = TokenFactory.createAttributeType(0x1000000000000094L, "Verification Event");
   public static final IAttributeType VerificationLevel = TokenFactory.createAttributeType(0x100000000000008BL, "Verification Level");
   public static final IAttributeType VerificationCriteria = TokenFactory.createAttributeType(0x100000000000008DL, "Verification Acceptance Criteria");
   public static final IAttributeType Website = TokenFactory.createAttributeType(0x100000000000006CL, "Website");
   public static final IAttributeType WebPreferences = TokenFactory.createAttributeType(0x100000000000019AL, "Web Preferences");
   public static final IAttributeType WholeWordContent = TokenFactory.createAttributeType(0x100000000000007BL, "Whole Word Content");
   public static final IAttributeType WordOleData = TokenFactory.createAttributeType(0x1000000000000074L, "Word Ole Data");
   public static final IAttributeType WordTemplateContent = TokenFactory.createAttributeType(0x100000000000007AL, "Word Template Content");
   public static final IAttributeType WorkData = TokenFactory.createAttributeType(0x1000000000000096L, "osee.wi.Work Data");
   public static final IAttributeType WorkTransition = TokenFactory.createAttributeType(0x100000000000009DL, "osee.wi.Transition");
   public static final IAttributeType XViewerCustomization = TokenFactory.createAttributeType(0x1000000000000065L, "XViewer Customization");
   public static final IAttributeType XViewerDefaults = TokenFactory.createAttributeType(0x1000000000000066L, "XViewer Defaults");
   public static final IAttributeType Zip = TokenFactory.createAttributeType(0x100000000000005FL, "Zip");

   // @formatter:on

   private CoreAttributeTypes() {
      // Constants
   }
}
