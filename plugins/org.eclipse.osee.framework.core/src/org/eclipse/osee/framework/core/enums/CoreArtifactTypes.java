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

/**
 * @author Ryan D. Brooks
 */
public final class CoreArtifactTypes {

   // @formatter:off
   public static final ArtifactTypeToken AccessControlModel = ArtifactTypeToken.valueOf(2, "Access Control Model");
   public static final ArtifactTypeToken AbstractSoftwareRequirement = ArtifactTypeToken.valueOf(23, "Abstract Software Requirement");
   public static final ArtifactTypeToken AbstractSpecRequirement = ArtifactTypeToken.valueOf(58551193202327573L, "Abstract Spec Requirement");
   public static final ArtifactTypeToken AbstractSubsystemRequirement = ArtifactTypeToken.valueOf(797, "Abstract Subsystem Requirement");
   public static final ArtifactTypeToken AbstractSystemRequirement = ArtifactTypeToken.valueOf(796, "Abstract System Requirement");
   public static final ArtifactTypeToken Artifact = ArtifactTypeToken.valueOf(1, "Artifact");
   public static final ArtifactTypeToken Breaker = ArtifactTypeToken.valueOf(188458869981236L, "Breaker");
   public static final ArtifactTypeToken BranchView = ArtifactTypeToken.valueOf(5849078277209560034L, "Branch View");
   public static final ArtifactTypeToken CertificationBaselineEvent = ArtifactTypeToken.valueOf(99, "Certification Baseline Event");
   public static final ArtifactTypeToken CodeUnit = ArtifactTypeToken.valueOf(58, "Code Unit");
   public static final ArtifactTypeToken Component = ArtifactTypeToken.valueOf(57, "Component");
   public static final ArtifactTypeToken Design = ArtifactTypeToken.valueOf(346, "Design");
   public static final ArtifactTypeToken DirectSoftwareRequirement = ArtifactTypeToken.valueOf(22, "Direct Software Requirement");
   public static final ArtifactTypeToken Feature = ArtifactTypeToken.valueOf(87, "Feature");
   public static final ArtifactTypeToken FeatureDefinition = ArtifactTypeToken.valueOf(5849078290088170402L, "Feature Definition");
   public static final ArtifactTypeToken Folder = ArtifactTypeToken.valueOf(11, "Folder");
   public static final ArtifactTypeToken GeneralData = ArtifactTypeToken.valueOf(12, "General Data");
   public static final ArtifactTypeToken GeneralDocument = ArtifactTypeToken.valueOf(14, "General Document");
   public static final ArtifactTypeToken GitCommit = ArtifactTypeToken.valueOf(100, "Git Commit");
   public static final ArtifactTypeToken GitRepository = ArtifactTypeToken.valueOf(97, "Git Repository");
   public static final ArtifactTypeToken GlobalPreferences = ArtifactTypeToken.valueOf(3, "Global Preferences");
   public static final ArtifactTypeToken HardwareRequirement = ArtifactTypeToken.valueOf(33, "Hardware Requirement");
   public static final ArtifactTypeToken HeadingMsWord = ArtifactTypeToken.valueOf(56, "Heading - MS Word");
   public static final ArtifactTypeToken HeadingHtml = ArtifactTypeToken.valueOf(804, "Heading - HTML");
   public static final ArtifactTypeToken HtmlArtifact = ArtifactTypeToken.valueOf(798, "HTML Artifact");
   public static final ArtifactTypeToken ImageArtifact = ArtifactTypeToken.valueOf(800, "Image Artifact");
   public static final ArtifactTypeToken IndirectSoftwareRequirement = ArtifactTypeToken.valueOf(25, "Indirect Software Requirement");
   public static final ArtifactTypeToken InterfaceRequirement = ArtifactTypeToken.valueOf(32, "Interface Requirement");
   public static final ArtifactTypeToken AbstractImplementationDetails = ArtifactTypeToken.valueOf(921211884, "Abstract Implementation Details");
   public static final ArtifactTypeToken ImplementationDetails = ArtifactTypeToken.valueOf(26, "Implementation Details");
   public static final ArtifactTypeToken ImplementationDetailsProcedure = ArtifactTypeToken.valueOf(69914, "Implementation Details Procedure");
   public static final ArtifactTypeToken ImplementationDetailsFunction = ArtifactTypeToken.valueOf(139802, "Implementation Details Function");
   public static final ArtifactTypeToken ImplementationDetailsDrawing = ArtifactTypeToken.valueOf(209690, "Implementation Details Drawing");
   public static final ArtifactTypeToken ImplementationDetailsDataDefinition = ArtifactTypeToken.valueOf(279578, "Implementation Details Data Definition");
   public static final ArtifactTypeToken ImplementationDetailsPlainText = ArtifactTypeToken.valueOf(638269899, "Implementation Details Plain Text");
   public static final ArtifactTypeToken ModelDiagram = ArtifactTypeToken.valueOf(98, "Model Diagram");
   public static final ArtifactTypeToken NativeArtifact = ArtifactTypeToken.valueOf(20, "Native Artifact");
   public static final ArtifactTypeToken MsWordWholeDocument = ArtifactTypeToken.valueOf(18, "MS Word Whole Document");
   public static final ArtifactTypeToken OseeApp = ArtifactTypeToken.valueOf(89, "OSEE App");
   public static final ArtifactTypeToken OseeTypeDefinition = ArtifactTypeToken.valueOf(60, "Osee Type Definition");

   public static final ArtifactTypeToken OseeTypeEnum = ArtifactTypeToken.valueOf(5447805027409642344L, "Osee Type Enum");
   public static final ArtifactTypeToken PlainText = ArtifactTypeToken.valueOf(784L, "Plain Text");
   public static final ArtifactTypeToken RendererTemplate = ArtifactTypeToken.valueOf(9, "Renderer Template");
   public static final ArtifactTypeToken Requirement = ArtifactTypeToken.valueOf(21, "Requirement");
   public static final ArtifactTypeToken RootArtifact = ArtifactTypeToken.valueOf(10, "Root Artifact");
   public static final ArtifactTypeToken SoftwareDesign = ArtifactTypeToken.valueOf(45, "Software Design");
   public static final ArtifactTypeToken SoftwareRequirement = ArtifactTypeToken.valueOf(24, "Software Requirement");
   public static final ArtifactTypeToken SoftwareRequirementDataDefinition = ArtifactTypeToken.valueOf(793, "Software Requirement Data Definition");
   public static final ArtifactTypeToken SoftwareRequirementDrawing = ArtifactTypeToken.valueOf(29, "Software Requirement Drawing");
   public static final ArtifactTypeToken SoftwareRequirementFunction = ArtifactTypeToken.valueOf(28, "Software Requirement Function");
   public static final ArtifactTypeToken SoftwareRequirementHtml = ArtifactTypeToken.valueOf(42, "Software Requirement - HTML");
   public static final ArtifactTypeToken SoftwareRequirementPlainText = ArtifactTypeToken.valueOf(792, "Software Requirement Plain Text");
   public static final ArtifactTypeToken SoftwareRequirementProcedure = ArtifactTypeToken.valueOf(27, "Software Requirement Procedure");
   public static final ArtifactTypeToken SoftwareTestProcedureMsWord = ArtifactTypeToken.valueOf(554486323432951758L, "Software Test Procedure - MS Word");
   public static final ArtifactTypeToken SoftwareTestProcedurePlainText = ArtifactTypeToken.valueOf(564397212436322878L, "Software Test Procedure Plain Text");
   public static final ArtifactTypeToken SubscriptionGroup = ArtifactTypeToken.valueOf(6753071794573299176L, "Subscription Group");
   public static final ArtifactTypeToken SubsystemDesign = ArtifactTypeToken.valueOf(43, "Subsystem Design");
   public static final ArtifactTypeToken SubsystemFunction = ArtifactTypeToken.valueOf(36, "Subsystem Function");
   public static final ArtifactTypeToken SubsystemRequirementMsWord = ArtifactTypeToken.valueOf(31, "Subsystem Requirement - MS Word");
   public static final ArtifactTypeToken SubsystemRequirementHtml = ArtifactTypeToken.valueOf(795, "Subsystem Requirement - HTML");
   public static final ArtifactTypeToken SupportingContent = ArtifactTypeToken.valueOf(49, "Supporting Content");
   public static final ArtifactTypeToken SupportDocument = ArtifactTypeToken.valueOf(13, "Support Document");;
   public static final ArtifactTypeToken SystemDesign = ArtifactTypeToken.valueOf(44, "System Design");
   public static final ArtifactTypeToken SystemFunction = ArtifactTypeToken.valueOf(35, "System Function");
   public static final ArtifactTypeToken SystemRequirementMsWord = ArtifactTypeToken.valueOf(30, "System Requirement - MS Word");
   public static final ArtifactTypeToken SystemRequirementHtml = ArtifactTypeToken.valueOf(794, "System Requirement - HTML");
   public static final ArtifactTypeToken TestCase = ArtifactTypeToken.valueOf(82, "Test Case");
   public static final ArtifactTypeToken TestInformationSheet = ArtifactTypeToken.valueOf(41, "Test Information Sheet");
   public static final ArtifactTypeToken TestPlanElement = ArtifactTypeToken.valueOf(37, "Test Plan Element");
   public static final ArtifactTypeToken TestProcedure = ArtifactTypeToken.valueOf(46, "Test Procedure");
   public static final ArtifactTypeToken TestProcedureWml = ArtifactTypeToken.valueOf(47, "Test Procedure WML");
   public static final ArtifactTypeToken TestResultNative = ArtifactTypeToken.valueOf(39, "Test Result Native");
   public static final ArtifactTypeToken TestResultWml = ArtifactTypeToken.valueOf(40, "Test Result WML");
   public static final ArtifactTypeToken TestRun = ArtifactTypeToken.valueOf(85, "Test Run");
   public static final ArtifactTypeToken TestRunDisposition = ArtifactTypeToken.valueOf(84, "Test Run Disposition");
   public static final ArtifactTypeToken TestSupport = ArtifactTypeToken.valueOf(83, "Test Support");
   public static final ArtifactTypeToken TestUnit = ArtifactTypeToken.valueOf(4, "Test Unit");
   public static final ArtifactTypeToken UniversalGroup = ArtifactTypeToken.valueOf(8, "Universal Group");
   public static final ArtifactTypeToken Url = ArtifactTypeToken.valueOf(15, "Url");
   public static final ArtifactTypeToken User = ArtifactTypeToken.valueOf(5, "User");
   public static final ArtifactTypeToken UserGroup = ArtifactTypeToken.valueOf(7, "User Group");
   public static final ArtifactTypeToken XViewerGlobalCustomization = ArtifactTypeToken.valueOf(55, "XViewer Global Customization");
   public static final ArtifactTypeToken DocumentDescriptionMsWord = ArtifactTypeToken.valueOf(806, "Document Description - MS Word");
   public static final ArtifactTypeToken DesignDescriptionMsWord = ArtifactTypeToken.valueOf(810, "Design Description - MS Word");
   public static final ArtifactTypeToken CustomerRequirementMsWord = ArtifactTypeToken.valueOf(809, "Customer Requirement - MS Word");
   // @formatter:on

   private CoreArtifactTypes() {
      // Constants
   }
}