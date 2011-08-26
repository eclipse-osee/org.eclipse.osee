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
   public static final IArtifactType AccessControlModel = TokenFactory.createArtifactType("CJr1bPfZQkbiuTA4FewA", "Access Control Model");
   public static final IArtifactType AbstractSoftwareRequirement = TokenFactory.createArtifactType("ABNAYPwV6H4EkjQ3+QQA", "Abstract Software Requirement");
   public static final IArtifactType AbstractTestResult = TokenFactory.createArtifactType("ATkaanWmHH3PkhGNVjwA", "Abstract Test Result");
   public static final IArtifactType Artifact = TokenFactory.createArtifactType("AAMFDh6S7gRLupAMwywA", "Artifact");
   public static final IArtifactType CodeUnit = TokenFactory.createArtifactType("AAMFDkEh216dzK1mTZgA", "Code Unit");
   public static final IArtifactType Component = TokenFactory.createArtifactType("AAMFDkG6omAsD6dXPYgA", "Component");
   public static final IArtifactType DirectSoftwareRequirement = TokenFactory.createArtifactType("BtMwyalHkHkrRo7D0aAA", "Direct Software Requirement");
   public static final IArtifactType Folder = TokenFactory.createArtifactType("AAMFDg_wmiYHHY5swJwA", "Folder");
   public static final IArtifactType GeneralData = TokenFactory.createArtifactType("AAMFDhQXfyb2m+jCwlwA", "General Data");
   public static final IArtifactType GeneralDocument = TokenFactory.createArtifactType("AAMFDhCjkTvP+VBpBCQA", "General Document");
   public static final IArtifactType GlobalPreferences = TokenFactory.createArtifactType("AAMFDho2kBqyoOZEw+gA", "Global Preferences");
   public static final IArtifactType HardwareRequirement = TokenFactory.createArtifactType("AAMFDh8dhUflUdK9FdgA", "Hardware Requirement");
   public static final IArtifactType Heading = TokenFactory.createArtifactType("AAMFDhEzni8FpFb5yHwA", "Heading");
   public static final IArtifactType IndirectSoftwareRequirement = TokenFactory.createArtifactType("AAMFDiC7HRQMqr5S0QwA", "Indirect Software Requirement");
   public static final IArtifactType InterfaceRequirement = TokenFactory.createArtifactType("AAMFDjgcukv7xEsPf2QA", "Interface Requirement");
   public static final IArtifactType ImplementationDetails = TokenFactory.createArtifactType("AMyewS0I5ADnjgL63tQA", "Implementation Details");
   public static final IArtifactType OseeTypeDefinition = TokenFactory.createArtifactType("AFGVEpSssxutyEAP0twA", "Osee Type Definition");
   public static final IArtifactType RendererTemplate = TokenFactory.createArtifactType("AAMFDhvZnHKgSeFKMXgA", "Renderer Template");
   public static final IArtifactType Requirement = TokenFactory.createArtifactType("ABM_vxEEowY+8i2_q9gA", "Requirement");
   public static final IArtifactType RootArtifact = TokenFactory.createArtifactType("AAMFDhHDqlbzKcIxcsAA", "Root Artifact");
   public static final IArtifactType SoftwareDesign = TokenFactory.createArtifactType("AAMFDh+nBRDS2smKPLAA", "Software Design");
   public static final IArtifactType SoftwareRequirement = TokenFactory.createArtifactType("AAMFDiAwhRFXwIyapJAA", "Software Requirement");
   public static final IArtifactType SoftwareRequirementDrawing = TokenFactory.createArtifactType("ABNClhgUfwj6A3EAArQA", "Software Requirement Drawing");
   public static final IArtifactType SoftwareRequirementFunction = TokenFactory.createArtifactType("ABNBwZMdFgEDTVQ7pTAA", "Software Requirement Function");
   public static final IArtifactType SoftwareRequirementProcedure = TokenFactory.createArtifactType("ABNBLPY4LnIKtcON0mgA", "Software Requirement Procedure");
   public static final IArtifactType SubsystemDesign = TokenFactory.createArtifactType("AAMFDiHVwBo+Yx73BoQA", "Subsystem Design");
   public static final IArtifactType SubsystemFunction = TokenFactory.createArtifactType("AAMFDjk6pAAd3tpGEqwA", "Subsystem Function");
   public static final IArtifactType SubsystemRequirement = TokenFactory.createArtifactType("AAMFDiN9KiAkhuLqOhQA", "Subsystem Requirement");
   public static final IArtifactType SupportingContent = TokenFactory.createArtifactType("AAMFDiQI2QuheFY71jgA", "Supporting Content");
   public static final IArtifactType SystemDesign = TokenFactory.createArtifactType("AAMFDiFI+lLm46F3HdQA", "System Design");
   public static final IArtifactType SystemFunction = TokenFactory.createArtifactType("AAMFDjisx2s6BUTDo3wA", "System Function");
   public static final IArtifactType SystemRequirement = TokenFactory.createArtifactType("AAMFDiSTcDGdUd9+tHAA", "System Requirement");
   public static final IArtifactType TestCase = TokenFactory.createArtifactType("AAMFDikEi0TGK27TKPgA", "Test Case");
   public static final IArtifactType TestInformationSheet = TokenFactory.createArtifactType("AAMFDjnM3wQxCjwatKAA", "Test Information Sheet");
   public static final IArtifactType TestPlanElement = TokenFactory.createArtifactType("ATi_kUpvPBiW2upYC_wA", "Test Plan Element");
   public static final IArtifactType TestProcedure = TokenFactory.createArtifactType("AAMFDjsjiGhoWpqM4PQA", "Test Procedure");
   public static final IArtifactType TestProcedureNative = TokenFactory.createArtifactType("AAMFDiWs_HdDJTbPPQgA", "Test Procedure Native");
   public static final IArtifactType TestProcedureWML = TokenFactory.createArtifactType("AAMFDiUeCG3KWx5XqeQA", "Test Procedure WML");
   public static final IArtifactType TestResultNative = TokenFactory.createArtifactType("ATkaanWmHH3PkhGNVjwA", "Test Result Native");
   public static final IArtifactType TestResultWML = TokenFactory.createArtifactType("ATk6NKFFmD_zg1b_eaQA", "Test Result WML");
   public static final IArtifactType TestRun = TokenFactory.createArtifactType("AAMFDjqDHWo+orlSpaQA", "Test Run");
   public static final IArtifactType TestRunDisposition = TokenFactory.createArtifactType("AAMFDjeNxhi0KmXZcKQA", "Test Run Disposition");
   public static final IArtifactType TestSupport = TokenFactory.createArtifactType("AAMFDj+FW0f_Ut72ocQA", "Test Support");
   public static final IArtifactType TestUnit = TokenFactory.createArtifactType("ABM2d6uxUw66aSdo0LwA", "Test Unit");
   public static final IArtifactType UniversalGroup = TokenFactory.createArtifactType("AAMFDhLY2TnADPA_EQQA", "Universal Group");
   public static final IArtifactType User = TokenFactory.createArtifactType("AAMFDhmr+Dqqe5pn3kAA", "User");
   public static final IArtifactType UserGroup = TokenFactory.createArtifactType("AAMFDhrEbXqZKPfWkwAA", "User Group");
   public static final IArtifactType XViewerGlobalCustomization = TokenFactory.createArtifactType("AAMFDhtN7T4of30iYhAA", "XViewer Global Customization");
   // @formatter:on

   private CoreArtifactTypes() {
      // Constants
   }
}