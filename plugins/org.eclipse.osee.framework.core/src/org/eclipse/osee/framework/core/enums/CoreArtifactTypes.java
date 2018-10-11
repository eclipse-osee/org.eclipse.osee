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

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * @author Ryan D. Brooks
 */
public final class CoreArtifactTypes {

   // @formatter:off
   public static final IArtifactType AccessControlModel = TokenFactory.createArtifactType(2, "Access Control Model");
   public static final IArtifactType AbstractSoftwareRequirement = TokenFactory.createArtifactType(23, "Abstract Software Requirement");
   public static final IArtifactType AbstractSpecRequirement = TokenFactory.createArtifactType(58551193202327573L, "Abstract Spec Requirement");
   public static final IArtifactType AbstractSubsystemRequirement = TokenFactory.createArtifactType(797, "Abstract Subsystem Requirement");
   public static final IArtifactType AbstractSystemRequirement = TokenFactory.createArtifactType(796, "Abstract System Requirement");
   public static final IArtifactType Artifact = TokenFactory.createArtifactType(1, "Artifact");
   public static final IArtifactType Breaker = TokenFactory.createArtifactType(188458869981236L, "Breaker");
   public static final IArtifactType BranchView = TokenFactory.createArtifactType(5849078277209560034L, "Branch View");
   public static final IArtifactType CodeUnit = TokenFactory.createArtifactType(58, "Code Unit");
   public static final IArtifactType Component = TokenFactory.createArtifactType(57, "Component");
   public static final IArtifactType Design = TokenFactory.createArtifactType(346, "Design");
   public static final IArtifactType DirectSoftwareRequirement = TokenFactory.createArtifactType(22, "Direct Software Requirement");
   public static final IArtifactType Feature = TokenFactory.createArtifactType(87, "Feature");
   public static final IArtifactType FeatureDefinition = TokenFactory.createArtifactType(5849078290088170402L, "Feature Definition");
   public static final IArtifactType Folder = TokenFactory.createArtifactType(11, "Folder");
   public static final IArtifactType GeneralData = TokenFactory.createArtifactType(12, "General Data");
   public static final IArtifactType GeneralDocument = TokenFactory.createArtifactType(14, "General Document");
   public static final IArtifactType GlobalPreferences = TokenFactory.createArtifactType(3, "Global Preferences");
   public static final IArtifactType HardwareRequirement = TokenFactory.createArtifactType(33, "Hardware Requirement");
   public static final IArtifactType HeadingMSWord = TokenFactory.createArtifactType(56, "Heading - MS Word");
   public static final IArtifactType HeadingHTML = TokenFactory.createArtifactType(804, "Heading - HTML");
   public static final IArtifactType HTMLArtifact = TokenFactory.createArtifactType(798, "HTML Artifact");
   public static final IArtifactType ImageArtifact = TokenFactory.createArtifactType(800, "Image Artifact");
   public static final IArtifactType IndirectSoftwareRequirement = TokenFactory.createArtifactType(25, "Indirect Software Requirement");
   public static final IArtifactType InterfaceRequirement = TokenFactory.createArtifactType(32, "Interface Requirement");
   public static final IArtifactType AbstractImplementationDetails = TokenFactory.createArtifactType(921211884, "Abstract Implementation Details");
   public static final IArtifactType ImplementationDetails = TokenFactory.createArtifactType(26, "Implementation Details");
   public static final IArtifactType ImplementationDetailsProcedure = TokenFactory.createArtifactType(69914, "Implementation Details Procedure");
   public static final IArtifactType ImplementationDetailsFunction = TokenFactory.createArtifactType(139802, "Implementation Details Function");
   public static final IArtifactType ImplementationDetailsDrawing = TokenFactory.createArtifactType(209690, "Implementation Details Drawing");
   public static final IArtifactType ImplementationDetailsDataDefinition = TokenFactory.createArtifactType(279578, "Implementation Details Data Definition");
   public static final IArtifactType ImplementationDetailsPlainText = TokenFactory.createArtifactType(638269899, "Implementation Details Plain Text");
   public static final IArtifactType ModelDiagram = TokenFactory.createArtifactType(98, "Model Diagram");
   public static final IArtifactType NativeArtifact = TokenFactory.createArtifactType(20, "Native Artifact");
   public static final IArtifactType WholeWord = TokenFactory.createArtifactType(18, "MS Word Whole Document");
   public static final IArtifactType OseeApp = TokenFactory.createArtifactType(89, "OSEE App");
   public static final IArtifactType OseeTypeDefinition = TokenFactory.createArtifactType(60, "Osee Type Definition");

   public static final IArtifactType OseeTypesEnum = TokenFactory.createArtifactType(5447805027409642344L, "Osee Type Enum");
   public static final IArtifactType PlainText = TokenFactory.createArtifactType(784L, "Plain Text");
   public static final IArtifactType RendererTemplate = TokenFactory.createArtifactType(9, "Renderer Template");
   public static final IArtifactType Requirement = TokenFactory.createArtifactType(21, "Requirement");
   public static final IArtifactType RootArtifact = TokenFactory.createArtifactType(10, "Root Artifact");
   public static final IArtifactType SoftwareDesign = TokenFactory.createArtifactType(45, "Software Design");
   public static final IArtifactType SoftwareRequirement = TokenFactory.createArtifactType(24, "Software Requirement");
   public static final IArtifactType SoftwareRequirementDataDefinition = TokenFactory.createArtifactType(793, "Software Requirement Data Definition");
   public static final IArtifactType SoftwareRequirementDrawing = TokenFactory.createArtifactType(29, "Software Requirement Drawing");
   public static final IArtifactType SoftwareRequirementFunction = TokenFactory.createArtifactType(28, "Software Requirement Function");
   public static final IArtifactType SoftwareRequirementHtml = TokenFactory.createArtifactType(42, "Software Requirement - HTML");
   public static final IArtifactType SoftwareRequirementPlainText = TokenFactory.createArtifactType(792, "Software Requirement Plain Text");
   public static final IArtifactType SoftwareRequirementProcedure = TokenFactory.createArtifactType(27, "Software Requirement Procedure");
   public static final IArtifactType SubscriptionGroup = TokenFactory.createArtifactType(6753071794573299176L, "Subscription Group");
   public static final IArtifactType SubsystemDesign = TokenFactory.createArtifactType(43, "Subsystem Design");
   public static final IArtifactType SubsystemFunction = TokenFactory.createArtifactType(36, "Subsystem Function");
   public static final IArtifactType SubsystemRequirementMSWord = TokenFactory.createArtifactType(31, "Subsystem Requirement - MS Word");
   public static final IArtifactType SubsystemRequirementHTML = TokenFactory.createArtifactType(795, "Subsystem Requirement - HTML");
   public static final IArtifactType SupportingContent = TokenFactory.createArtifactType(49, "Supporting Content");
   public static final IArtifactType SupportDocument = TokenFactory.createArtifactType(13, "Support Document");;
   public static final IArtifactType SystemDesign = TokenFactory.createArtifactType(44, "System Design");
   public static final IArtifactType SystemFunction = TokenFactory.createArtifactType(35, "System Function");
   public static final IArtifactType SystemRequirementMSWord = TokenFactory.createArtifactType(30, "System Requirement - MS Word");
   public static final IArtifactType SystemRequirementHTML = TokenFactory.createArtifactType(794, "System Requirement - HTML");
   public static final IArtifactType TestCase = TokenFactory.createArtifactType(82, "Test Case");
   public static final IArtifactType TestInformationSheet = TokenFactory.createArtifactType(41, "Test Information Sheet");
   public static final IArtifactType TestPlanElement = TokenFactory.createArtifactType(37, "Test Plan Element");
   public static final IArtifactType TestProcedure = TokenFactory.createArtifactType(46, "Test Procedure");
   public static final IArtifactType TestProcedureWML = TokenFactory.createArtifactType(47, "Test Procedure WML");
   public static final IArtifactType TestResultNative = TokenFactory.createArtifactType(39, "Test Result Native");
   public static final IArtifactType TestResultWML = TokenFactory.createArtifactType(40, "Test Result WML");
   public static final IArtifactType TestRun = TokenFactory.createArtifactType(85, "Test Run");
   public static final IArtifactType TestRunDisposition = TokenFactory.createArtifactType(84, "Test Run Disposition");
   public static final IArtifactType TestSupport = TokenFactory.createArtifactType(83, "Test Support");
   public static final IArtifactType TestUnit = TokenFactory.createArtifactType(4, "Test Unit");
   public static final IArtifactType UniversalGroup = TokenFactory.createArtifactType(8, "Universal Group");
   public static final IArtifactType Url = TokenFactory.createArtifactType(15, "Url");
   public static final IArtifactType User = TokenFactory.createArtifactType(5, "User");
   public static final IArtifactType UserGroup = TokenFactory.createArtifactType(7, "User Group");
   public static final IArtifactType XViewerGlobalCustomization = TokenFactory.createArtifactType(55, "XViewer Global Customization");

   public static final IArtifactType DocumentDescriptionMSWord = TokenFactory.createArtifactType(806, "Document Description - MS Word");
   public static final IArtifactType DesignDescriptionMSWord = TokenFactory.createArtifactType(807, "Design Description - MS Word");
   // @formatter:on

   private CoreArtifactTypes() {
      // Constants
   }
}