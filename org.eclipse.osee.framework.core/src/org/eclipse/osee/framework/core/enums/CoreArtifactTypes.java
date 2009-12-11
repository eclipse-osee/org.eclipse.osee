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

/**
 * @author Ryan D. Brooks
 */
public enum CoreArtifactTypes implements IArtifactType {
   AbstractSoftwareRequirement("Abstract Software Requirement", "ABNAYPwV6H4EkjQ3+QQA"),
   AbstractTestResult("Abstract Test Result", "ATkaanWmHH3PkhGNVjwA"),
   Artifact("Artifact", "AAMFDh6S7gRLupAMwywA"),
   CodeUnit("Code Unit", "AAMFDkEh216dzK1mTZgA"),
   Folder("Folder", "AAMFDg_wmiYHHY5swJwA"),
   GeneralDocument("General Document", "AAMFDhCjkTvP+VBpBCQA"),
   GlobalPreferences("Global Preferences", "AAMFDho2kBqyoOZEw+gA"),
   Heading("Heading", "AAMFDhEzni8FpFb5yHwA"),
   IndirectSoftwareRequirement("Indirect Software Requirement", "AAMFDiC7HRQMqr5S0QwA"),
   Requirement("Requirement", "ABM_vxEEowY+8i2_q9gA"),
   RootArtifact("Root Artifact", "AAMFDhHDqlbzKcIxcsAA"),
   SoftwareRequirement("Software Requirement", "AAMFDiAwhRFXwIyapJAA"),
   SoftwareRequirementDrawing("Software Requirement Drawing", "ABNClhgUfwj6A3EAArQA"),
   SubsystemRequirement("Subsystem Requirement", "AAMFDiN9KiAkhuLqOhQA"),
   TestPlanElement("Test Plan Element", "ATi_kUpvPBiW2upYC_wA"),
   TestProcedure("Test Procedure", "AAMFDjsjiGhoWpqM4PQA"),
   TestResultNative("Test Result Native", "ATkaanWmHH3PkhGNVjwA"),
   TestResultWML("Test Result WML", "ATk6NKFFmD_zg1b_eaQA"),
   TestUnit("Test Unit", "ABM2d6uxUw66aSdo0LwA"),
   UniversalGroup("Universal Group", "AAMFDhLY2TnADPA_EQQA"),
   User("User", "AAMFDhmr+Dqqe5pn3kAA"),
   UserGroup("User Group", "AAMFDhrEbXqZKPfWkwAA"),
   WorkFlowDefinition("Work Flow Definition", "AAMFDh16eQ1GIHPWlYQA"),
   WorkPageDefinition("Work Page Definition", "AAMFDhzuyizN4qu7tXgA"),
   WorkRuleDefinition("Work Rule Definition", "AAMFDhxjHC2RUV2RkcQA"),
   WorkWidgetDefinition("Work Widget Definition", "AAMFDh4IVzqPgVTpLrwA"),
   XViewerGlobalCustomization("XViewer Global Customization", "AAMFDhtN7T4of30iYhAA");

   private final String name;
   private final String guid;

   private CoreArtifactTypes(String name, String guid) {
      this.name = name;
      this.guid = guid;
   }

   public String getName() {
      return this.name;
   }

   public String getGuid() {
      return guid;
   }
}
