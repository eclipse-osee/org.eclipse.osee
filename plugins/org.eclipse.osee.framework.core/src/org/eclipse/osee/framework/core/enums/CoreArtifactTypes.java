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

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * @author Ryan D. Brooks
 */
public final class CoreArtifactTypes {

   // @formatter:off
   public static final ArtifactTypeToken AccessControlModel = TokenFactory.createArtifactType(2, "Access Control Model");
   public static final ArtifactTypeToken AbstractSoftwareRequirement = TokenFactory.createArtifactType(23, "Abstract Software Requirement");
   public static final ArtifactTypeToken AbstractSpecRequirement = TokenFactory.createArtifactType(58551193202327573L, "Abstract Spec Requirement");
   public static final ArtifactTypeToken AbstractSubsystemRequirement = TokenFactory.createArtifactType(797, "Abstract Subsystem Requirement");
   public static final ArtifactTypeToken AbstractSystemRequirement = TokenFactory.createArtifactType(796, "Abstract System Requirement");
   public static final ArtifactTypeToken Artifact = TokenFactory.createArtifactType(1, "Artifact");
   public static final ArtifactTypeToken Breaker = TokenFactory.createArtifactType(188458869981236L, "Breaker");
   public static final ArtifactTypeToken BranchView = TokenFactory.createArtifactType(5849078277209560034L, "Branch View");
   public static final ArtifactTypeToken CodeUnit = TokenFactory.createArtifactType(58, "Code Unit");
   public static final ArtifactTypeToken Component = TokenFactory.createArtifactType(57, "Component");
   public static final ArtifactTypeToken Design = TokenFactory.createArtifactType(346, "Design");
   public static final ArtifactTypeToken DirectSoftwareRequirement = TokenFactory.createArtifactType(22, "Direct Software Requirement");
   public static final ArtifactTypeToken Feature = TokenFactory.createArtifactType(87, "Feature");
   public static final ArtifactTypeToken FeatureDefinition = TokenFactory.createArtifactType(5849078290088170402L, "Feature Definition");
   public static final ArtifactTypeToken Folder = TokenFactory.createArtifactType(11, "Folder");
   public static final ArtifactTypeToken GeneralData = TokenFactory.createArtifactType(12, "General Data");
   public static final ArtifactTypeToken GeneralDocument = TokenFactory.createArtifactType(14, "General Document");
   public static final ArtifactTypeToken GlobalPreferences = TokenFactory.createArtifactType(3, "Global Preferences");
   public static final ArtifactTypeToken HardwareRequirement = TokenFactory.createArtifactType(33, "Hardware Requirement");
   public static final ArtifactTypeToken HeadingMSWord = TokenFactory.createArtifactType(56, "Heading - MS Word");
   public static final ArtifactTypeToken HeadingHTML = TokenFactory.createArtifactType(804, "Heading - HTML");
   public static final ArtifactTypeToken HTMLArtifact = TokenFactory.createArtifactType(798, "HTML Artifact");
   public static final ArtifactTypeToken ImageArtifact = TokenFactory.createArtifactType(800, "Image Artifact");
   public static final ArtifactTypeToken IndirectSoftwareRequirement = TokenFactory.createArtifactType(25, "Indirect Software Requirement");
   public static final ArtifactTypeToken InterfaceRequirement = TokenFactory.createArtifactType(32, "Interface Requirement");
   public static final ArtifactTypeToken AbstractImplementationDetails = TokenFactory.createArtifactType(921211884, "Abstract Implementation Details");
   public static final ArtifactTypeToken ImplementationDetails = TokenFactory.createArtifactType(26, "Implementation Details");
   public static final ArtifactTypeToken ImplementationDetailsProcedure = TokenFactory.createArtifactType(69914, "Implementation Details Procedure");
   public static final ArtifactTypeToken ImplementationDetailsFunction = TokenFactory.createArtifactType(139802, "Implementation Details Function");
   public static final ArtifactTypeToken ImplementationDetailsDrawing = TokenFactory.createArtifactType(209690, "Implementation Details Drawing");
   public static final ArtifactTypeToken ImplementationDetailsDataDefinition = TokenFactory.createArtifactType(279578, "Implementation Details Data Definition");
   public static final ArtifactTypeToken ImplementationDetailsPlainText = TokenFactory.createArtifactType(638269899, "Implementation Details Plain Text");
   public static final ArtifactTypeToken ModelDiagram = TokenFactory.createArtifactType(98, "Model Diagram");
   public static final ArtifactTypeToken NativeArtifact = TokenFactory.createArtifactType(20, "Native Artifact");
   public static final ArtifactTypeToken WholeWord = TokenFactory.createArtifactType(18, "MS Word Whole Document");
   public static final ArtifactTypeToken OseeApp = TokenFactory.createArtifactType(89, "OSEE App");
   public static final ArtifactTypeToken OseeTypeDefinition = TokenFactory.createArtifactType(60, "Osee Type Definition");

   public static final ArtifactTypeToken OseeTypesEnum = TokenFactory.createArtifactType(5447805027409642344L, "Osee Type Enum");
   public static final ArtifactTypeToken PlainText = TokenFactory.createArtifactType(784L, "Plain Text");
   public static final ArtifactTypeToken RendererTemplate = TokenFactory.createArtifactType(9, "Renderer Template");
   public static final ArtifactTypeToken Requirement = TokenFactory.createArtifactType(21, "Requirement");
   public static final ArtifactTypeToken RootArtifact = TokenFactory.createArtifactType(10, "Root Artifact");
   public static final ArtifactTypeToken SoftwareDesign = TokenFactory.createArtifactType(45, "Software Design");
   public static final ArtifactTypeToken SoftwareRequirement = TokenFactory.createArtifactType(24, "Software Requirement");
   public static final ArtifactTypeToken SoftwareRequirementDataDefinition = TokenFactory.createArtifactType(793, "Software Requirement Data Definition");
   public static final ArtifactTypeToken SoftwareRequirementDrawing = TokenFactory.createArtifactType(29, "Software Requirement Drawing");
   public static final ArtifactTypeToken SoftwareRequirementFunction = TokenFactory.createArtifactType(28, "Software Requirement Function");
   public static final ArtifactTypeToken SoftwareRequirementHtml = TokenFactory.createArtifactType(42, "Software Requirement - HTML");
   public static final ArtifactTypeToken SoftwareRequirementPlainText = TokenFactory.createArtifactType(792, "Software Requirement Plain Text");
   public static final ArtifactTypeToken SoftwareRequirementProcedure = TokenFactory.createArtifactType(27, "Software Requirement Procedure");
   public static final ArtifactTypeToken SubscriptionGroup = TokenFactory.createArtifactType(6753071794573299176L, "Subscription Group");
   public static final ArtifactTypeToken SubsystemDesign = TokenFactory.createArtifactType(43, "Subsystem Design");
   public static final ArtifactTypeToken SubsystemFunction = TokenFactory.createArtifactType(36, "Subsystem Function");
   public static final ArtifactTypeToken SubsystemRequirementMSWord = TokenFactory.createArtifactType(31, "Subsystem Requirement - MS Word");
   public static final ArtifactTypeToken SubsystemRequirementHTML = TokenFactory.createArtifactType(795, "Subsystem Requirement - HTML");
   public static final ArtifactTypeToken SupportingContent = TokenFactory.createArtifactType(49, "Supporting Content");
   public static final ArtifactTypeToken SupportDocument = TokenFactory.createArtifactType(13, "Support Document");;
   public static final ArtifactTypeToken SystemDesign = TokenFactory.createArtifactType(44, "System Design");
   public static final ArtifactTypeToken SystemFunction = TokenFactory.createArtifactType(35, "System Function");
   public static final ArtifactTypeToken SystemRequirementMSWord = TokenFactory.createArtifactType(30, "System Requirement - MS Word");
   public static final ArtifactTypeToken SystemRequirementHTML = TokenFactory.createArtifactType(794, "System Requirement - HTML");
   public static final ArtifactTypeToken TestCase = TokenFactory.createArtifactType(82, "Test Case");
   public static final ArtifactTypeToken TestInformationSheet = TokenFactory.createArtifactType(41, "Test Information Sheet");
   public static final ArtifactTypeToken TestPlanElement = TokenFactory.createArtifactType(37, "Test Plan Element");
   public static final ArtifactTypeToken TestProcedure = TokenFactory.createArtifactType(46, "Test Procedure");
   public static final ArtifactTypeToken TestProcedureWML = TokenFactory.createArtifactType(47, "Test Procedure WML");
   public static final ArtifactTypeToken TestResultNative = TokenFactory.createArtifactType(39, "Test Result Native");
   public static final ArtifactTypeToken TestResultWML = TokenFactory.createArtifactType(40, "Test Result WML");
   public static final ArtifactTypeToken TestRun = TokenFactory.createArtifactType(85, "Test Run");
   public static final ArtifactTypeToken TestRunDisposition = TokenFactory.createArtifactType(84, "Test Run Disposition");
   public static final ArtifactTypeToken TestSupport = TokenFactory.createArtifactType(83, "Test Support");
   public static final ArtifactTypeToken TestUnit = TokenFactory.createArtifactType(4, "Test Unit");
   public static final ArtifactTypeToken UniversalGroup = TokenFactory.createArtifactType(8, "Universal Group");
   public static final ArtifactTypeToken Url = TokenFactory.createArtifactType(15, "Url");
   public static final ArtifactTypeToken User = TokenFactory.createArtifactType(5, "User");
   public static final ArtifactTypeToken UserGroup = TokenFactory.createArtifactType(7, "User Group");
   public static final ArtifactTypeToken XViewerGlobalCustomization = TokenFactory.createArtifactType(55, "XViewer Global Customization");

   public static final ArtifactTypeToken DocumentDescriptionMSWord = TokenFactory.createArtifactType(806, "Document Description - MS Word");
   public static final ArtifactTypeToken DesignDescriptionMSWord = TokenFactory.createArtifactType(807, "Design Description - MS Word");
   public static final ArtifactTypeToken CustomerRequirementMSWord = TokenFactory.createArtifactType(809, "Customer Requirement - MS Word");
   // @formatter:on

   private CoreArtifactTypes() {
      // Constants
   }
}