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
import org.eclipse.osee.framework.core.data.NamedIdentity;

/**
 * @author Ryan D. Brooks
 */
public final class CoreArtifactTypes extends NamedIdentity implements IArtifactType {

   // @formatter:off
   public static final IArtifactType AccessControlModel = new CoreArtifactTypes("CJr1bPfZQkbiuTA4FewA", "Access Control Model");
   public static final IArtifactType AbstractAccessControlled = new CoreArtifactTypes("AAFVUREVAxmEmLW2KkAA", "Abstract Access Controlled");
   public static final IArtifactType AbstractSoftwareRequirement = new CoreArtifactTypes("ABNAYPwV6H4EkjQ3+QQA", "Abstract Software Requirement");
   public static final IArtifactType AbstractTestResult = new CoreArtifactTypes("ATkaanWmHH3PkhGNVjwA", "Abstract Test Result");
   public static final IArtifactType AbstractTestUnit = new CoreArtifactTypes("AISIbSI6wzEUfPdCQaAA", "Abstract Test Unit");
   public static final IArtifactType Artifact = new CoreArtifactTypes("AAMFDh6S7gRLupAMwywA", "Artifact");
   public static final IArtifactType CodeUnit = new CoreArtifactTypes("AAMFDkEh216dzK1mTZgA", "Code Unit");
   public static final IArtifactType Component = new CoreArtifactTypes("AAMFDkG6omAsD6dXPYgA", "Component");
   public static final IArtifactType DirectSoftwareRequirement = new CoreArtifactTypes("BtMwyalHkHkrRo7D0aAA", "Direct Software Requirement");
   public static final IArtifactType Folder = new CoreArtifactTypes("AAMFDg_wmiYHHY5swJwA", "Folder");
   public static final IArtifactType GeneralData = new CoreArtifactTypes("AAMFDhQXfyb2m+jCwlwA", "General Data");
   public static final IArtifactType GeneralDocument = new CoreArtifactTypes("AAMFDhCjkTvP+VBpBCQA", "General Document");
   public static final IArtifactType GlobalPreferences = new CoreArtifactTypes("AAMFDho2kBqyoOZEw+gA", "Global Preferences");
   public static final IArtifactType HardwareRequirement = new CoreArtifactTypes("AAMFDh8dhUflUdK9FdgA", "Hardware Requirement");
   public static final IArtifactType Heading = new CoreArtifactTypes("AAMFDhEzni8FpFb5yHwA", "Heading");
   public static final IArtifactType IndirectSoftwareRequirement = new CoreArtifactTypes("AAMFDiC7HRQMqr5S0QwA", "Indirect Software Requirement");
   public static final IArtifactType RendererTemplate = new CoreArtifactTypes("AAMFDhvZnHKgSeFKMXgA", "Renderer Template");
   public static final IArtifactType Requirement = new CoreArtifactTypes("ABM_vxEEowY+8i2_q9gA", "Requirement");
   public static final IArtifactType RootArtifact = new CoreArtifactTypes("AAMFDhHDqlbzKcIxcsAA", "Root Artifact");
   public static final IArtifactType SoftwareRequirement = new CoreArtifactTypes("AAMFDiAwhRFXwIyapJAA", "Software Requirement");
   public static final IArtifactType SoftwareRequirementDrawing = new CoreArtifactTypes("ABNClhgUfwj6A3EAArQA", "Software Requirement Drawing");
   public static final IArtifactType SoftwareRequirementFunction = new CoreArtifactTypes("ABNBwZMdFgEDTVQ7pTAA", "Software Requirement Function");
   public static final IArtifactType SoftwareRequirementProcedure = new CoreArtifactTypes("ABNBLPY4LnIKtcON0mgA", "Software Requirement Procedure");
   public static final IArtifactType SubsystemDesign = new CoreArtifactTypes("AAMFDiHVwBo+Yx73BoQA", "Subsystem Design");
   public static final IArtifactType SubsystemRequirement = new CoreArtifactTypes("AAMFDiN9KiAkhuLqOhQA", "Subsystem Requirement");
   public static final IArtifactType SystemFunction = new CoreArtifactTypes("AAMFDjisx2s6BUTDo3wA", "System Function");
   public static final IArtifactType SystemRequirement = new CoreArtifactTypes("AAMFDiSTcDGdUd9+tHAA", "System Requirement");
   public static final IArtifactType TestCase = new CoreArtifactTypes("AAMFDikEi0TGK27TKPgA", "Test Case");
   public static final IArtifactType TestInformationSheet = new CoreArtifactTypes("AAMFDjnM3wQxCjwatKAA", "Test Information Sheet");
   public static final IArtifactType TestPlanElement = new CoreArtifactTypes("ATi_kUpvPBiW2upYC_wA", "Test Plan Element");
   public static final IArtifactType TestProcedure = new CoreArtifactTypes("AAMFDjsjiGhoWpqM4PQA", "Test Procedure");
   public static final IArtifactType TestProcedureNative = new CoreArtifactTypes("AAMFDiWs_HdDJTbPPQgA", "Test Procedure Native");
   public static final IArtifactType TestProcedureWML = new CoreArtifactTypes("AAMFDiUeCG3KWx5XqeQA", "Test Procedure WML");
   public static final IArtifactType TestProcedureXL = new CoreArtifactTypes("AAn_QG1xVGGhvAzxVMQA", "Test Procedure XL");
   public static final IArtifactType TestResultNative = new CoreArtifactTypes("ATkaanWmHH3PkhGNVjwA", "Test Result Native");
   public static final IArtifactType TestResultWML = new CoreArtifactTypes("ATk6NKFFmD_zg1b_eaQA", "Test Result WML");
   public static final IArtifactType TestRun = new CoreArtifactTypes("AAMFDjqDHWo+orlSpaQA", "Test Run");
   public static final IArtifactType TestRunDisposition = new CoreArtifactTypes("AAMFDjeNxhi0KmXZcKQA", "Test Run Disposition");
   public static final IArtifactType TestSupport = new CoreArtifactTypes("AAMFDj+FW0f_Ut72ocQA", "Test Support");
   public static final IArtifactType TestUnit = new CoreArtifactTypes("ABM2d6uxUw66aSdo0LwA", "Test Unit");
   public static final IArtifactType UniversalGroup = new CoreArtifactTypes("AAMFDhLY2TnADPA_EQQA", "Universal Group");
   public static final IArtifactType User = new CoreArtifactTypes("AAMFDhmr+Dqqe5pn3kAA", "User");
   public static final IArtifactType UserGroup = new CoreArtifactTypes("AAMFDhrEbXqZKPfWkwAA", "User Group");
   public static final IArtifactType WorkFlowDefinition = new CoreArtifactTypes("AAMFDh16eQ1GIHPWlYQA", "Work Flow Definition");
   public static final IArtifactType WorkItemDefinition = new CoreArtifactTypes("ABNM6pIn_x_3e6a3aIgA", "Work Item Definition");
   public static final IArtifactType WorkPageDefinition = new CoreArtifactTypes("AAMFDhzuyizN4qu7tXgA", "Work Page Definition");
   public static final IArtifactType WorkRuleDefinition = new CoreArtifactTypes("AAMFDhxjHC2RUV2RkcQA", "Work Rule Definition");
   public static final IArtifactType WorkWidgetDefinition = new CoreArtifactTypes("AAMFDh4IVzqPgVTpLrwA", "Work Widget Definition");
   public static final IArtifactType XViewerGlobalCustomization = new CoreArtifactTypes("AAMFDhtN7T4of30iYhAA", "XViewer Global Customization");
   // @formatter:on

   private CoreArtifactTypes(String guid, String name) {
      super(guid, name);
   }
}