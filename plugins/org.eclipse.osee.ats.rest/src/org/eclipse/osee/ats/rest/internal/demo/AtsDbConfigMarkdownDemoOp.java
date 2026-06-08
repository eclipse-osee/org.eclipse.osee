/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.rest.internal.demo;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_PL;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Lightweight operation that creates markdown demo artifacts and a working branch for Playwright e2e testing.
 * Populates the "- Markdown" requirement folders on SAW Product Line with representative artifacts:
 * <ul>
 *    <li>System Requirements - Markdown: system requirement artifacts</li>
 *    <li>SubSystem Requirements - Markdown: subsystem requirement artifacts</li>
 *    <li>Software Requirements - Markdown: software requirement artifacts under a "Robot API - Markdown" heading</li>
 *    <li>SAWTSR: an Image artifact with native PNG content for native content editor testing</li>
 * </ul>
 * Then creates the {@link DemoBranches#SAW_PL_Working_Branch_Markdown} working branch from SAW Product Line.
 */
public class AtsDbConfigMarkdownDemoOp {

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   /** Minimal valid 1x1 white PNG (67 bytes). */
   private static final byte[] MINIMAL_PNG = new byte[] {
      (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG signature
      0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52, // IHDR chunk
      0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x08, 0x02, 0x00, 0x00, 0x00,
      (byte) 0x90, 0x77, 0x53, (byte) 0xDE, // CRC
      0x00, 0x00, 0x00, 0x0C, 0x49, 0x44, 0x41, 0x54, // IDAT chunk
      0x08, (byte) 0xD7, 0x63, (byte) 0xF8, (byte) 0xCF, (byte) 0xC0, 0x00, 0x00, 0x00, 0x02, 0x00, 0x01,
      (byte) 0xE2, 0x21, (byte) 0xBC, 0x33, // CRC
      0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, // IEND chunk
      (byte) 0xAE, 0x42, 0x60, (byte) 0x82 // CRC
   };

   public AtsDbConfigMarkdownDemoOp(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public void run(XResultData rd) {
      try {
         createSystemRequirements(rd);
         createSubsystemRequirements(rd);
         createSoftwareRequirements(rd);
         createImageArtifact(rd);
         createWorkingBranch(rd);
      } catch (Exception ex) {
         rd.errorf("Error in markdownDemoInit: %s", Lib.exceptionToString(ex));
      }
   }

   private void createSystemRequirements(XResultData rd) {
      IAtsChangeSet changes =
         atsApi.createChangeSet("Markdown Demo Init - Create System Requirement Markdown Artifacts", SAW_PL);
      ArtifactToken parent =
         atsApi.getQueryService().getArtifact(CoreArtifactTokens.SystemRequirementsFolderMarkdown, SAW_PL);

      if (parent == null || parent.isInvalid()) {
         rd.errorf("Could not find System Requirements - Markdown folder on branch %s",
            DemoBranches.SAW_PL.getName());
         return;
      }

      for (String childName : Arrays.asList("Robot API Communication Protocol",
         "Robot API Safety Requirements", "Robot API Performance Requirements")) {
         ArtifactToken artifact =
            changes.createArtifact(parent, CoreArtifactTypes.SystemRequirementMarkdown, childName);
         String markdown = String.format("## %s \n\nThe system shall provide %s capabilities " //
            + "to ensure safe and reliable operation of all connected robot subsystems.", childName, childName);
         changes.setSoleAttributeValue(artifact, CoreAttributeTypes.MarkdownContent, markdown);
      }
      changes.execute();
      rd.log("Created system requirement markdown artifacts on " + DemoBranches.SAW_PL.getName());
   }

   private void createSubsystemRequirements(XResultData rd) {
      IAtsChangeSet changes =
         atsApi.createChangeSet("Markdown Demo Init - Create Subsystem Requirement Markdown Artifacts", SAW_PL);
      ArtifactToken parent =
         atsApi.getQueryService().getArtifact(CoreArtifactTokens.SubSystemRequirementsFolderMarkdown, SAW_PL);

      if (parent == null || parent.isInvalid()) {
         rd.errorf("Could not find SubSystem Requirements - Markdown folder on branch %s",
            DemoBranches.SAW_PL.getName());
         return;
      }

      for (String childName : Arrays.asList("Robot Motion Control Subsystem",
         "Robot Sensor Integration Subsystem", "Robot Communication Subsystem")) {
         ArtifactToken artifact =
            changes.createArtifact(parent, CoreArtifactTypes.SubsystemRequirementMarkdown, childName);
         String markdown = String.format("## %s \n\nThe %s shall interface with the robot API " //
            + "to provide real-time data exchange and control signaling.", childName, childName);
         changes.setSoleAttributeValue(artifact, CoreAttributeTypes.MarkdownContent, markdown);
      }
      changes.execute();
      rd.log("Created subsystem requirement markdown artifacts on " + DemoBranches.SAW_PL.getName());
   }

   private void createSoftwareRequirements(XResultData rd) {
      IAtsChangeSet changes =
         atsApi.createChangeSet("Markdown Demo Init - Create Software Requirement Markdown Artifacts", SAW_PL);
      ArtifactToken parent =
         atsApi.getQueryService().getArtifact(CoreArtifactTokens.SoftwareRequirementsFolderMarkdown, SAW_PL);

      if (parent == null || parent.isInvalid()) {
         rd.errorf("Could not find Software Requirements - Markdown folder on branch %s",
            DemoBranches.SAW_PL.getName());
         return;
      }

      ArtifactToken robotArt = changes.createArtifact(parent, CoreArtifactTypes.SoftwareRequirementMarkdown,
         "Robot API - Markdown");

      for (String childName : Arrays.asList("CISST fundamental data types", "Events", "Functional Specification",
         "Interface Initialization", "Read-only Robots", "Robot Interfaces", "Robot collaboration",
         "Virtual fixtures")) {
         ArtifactToken artifact =
            changes.createArtifact(robotArt, CoreArtifactTypes.SoftwareRequirementMarkdown, childName);
         String markdown = String.format("## %s \n\nThe API shall generate %s to notify the user " //
            + "application about asynchronous actions detected by the lower level software.  " //
            + "The %s of the individual and collaborative robot objects shall be documented " //
            + "in an external database/document.", childName, childName, childName);
         changes.setSoleAttributeValue(artifact, CoreAttributeTypes.MarkdownContent, markdown);
      }
      changes.execute();
      rd.log("Created software requirement markdown artifacts on " + DemoBranches.SAW_PL.getName());
   }

   private void createImageArtifact(XResultData rd) {
      IAtsChangeSet changes = atsApi.createChangeSet("Markdown Demo Init - Create Image Artifact", SAW_PL);
      ArtifactToken parent =
         atsApi.getQueryService().getArtifact(CoreArtifactTokens.SystemRequirementsFolderMarkdown, SAW_PL);

      if (parent == null || parent.isInvalid()) {
         rd.errorf("Could not find System Requirements - Markdown folder for image on branch %s",
            DemoBranches.SAW_PL.getName());
         return;
      }

      ArtifactToken imageArt = changes.createArtifact(parent, CoreArtifactTypes.Image, "SAWTSR");
      changes.setSoleAttributeValue(imageArt, CoreAttributeTypes.Extension, "png");

      InputStream pngStream = new ByteArrayInputStream(MINIMAL_PNG);
      changes.setSoleAttributeValue(imageArt, CoreAttributeTypes.NativeContent, pngStream);

      changes.execute();
      rd.log("Created SAWTSR image artifact on " + DemoBranches.SAW_PL.getName());
   }

   private void createWorkingBranch(XResultData rd) {
      BranchToken parentBranch = DemoBranches.SAW_PL;
      BranchToken workingBranch = DemoBranches.SAW_PL_Working_Branch_Markdown;

      orcsApi.getBranchOps().createWorkingBranch(workingBranch, parentBranch, ArtifactToken.SENTINEL);
      rd.log("Created working branch: " + workingBranch.getName());
   }
}
