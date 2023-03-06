/*********************************************************************
 * Copyright (c) 2019 Boeing
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

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.util.Arrays;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.config.OrganizePrograms;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * Initialization class that will load configuration information for a sample DB.
 *
 * @author Donald G. Dunne
 */
public class AtsDbConfigDemoOp {

   private final AtsApi atsApi;

   public AtsDbConfigDemoOp(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public XResultData run() {

      (new AtsDbConfigAIsAndTeamsDemoOp(atsApi)).run();
      configureForParallelCommit();

      IAtsChangeSet changes = atsApi.createChangeSet("Set ATS Admin");
      changes.relate(AtsUserGroups.AtsTempAdmin, CoreRelationTypes.Users_User, DemoUsers.Joe_Smith);
      changes.execute();

      (new OrganizePrograms(atsApi)).run();

      createDemoWebConfig();

      atsApi.setConfigValue(AtsUtil.SINGLE_SERVER_DEPLOYMENT, "true");

      createMarkdownRequirements();

      return new XResultData();
   }

   private void createDemoWebConfig() {
      ArtifactToken headingArt = atsApi.getQueryService().getArtifact(AtsArtifactToken.AtsTopFolder);

      IAtsChangeSet changes = atsApi.createChangeSet("Create Web Programs");
      ArtifactToken oseeWebArt = changes.createArtifact(headingArt, AtsArtifactToken.WebPrograms);

      ArtifactToken sawProgram = atsApi.getQueryService().getArtifact(DemoArtifactToken.SAW_PL_Program);
      changes.relate(oseeWebArt, CoreRelationTypes.UniversalGrouping_Members, sawProgram);

      changes.execute();
   }

   /**
    * Configure SAW_Bld_1 and SAW_Bld_2 for parallel commit, including recursive setup where SAW_Bld_1 needs to be
    * committed to SAW_Bld_1 and SAW_Bld_2 and SAW_Bld_2 needs to be committed to SAW_Bld_2 and SAW_Bld_1
    */
   private void configureForParallelCommit() {
      IAtsChangeSet changes = atsApi.createChangeSet("configureForParallelCommit");

      IAtsVersion sawBld1Ver = atsApi.getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_1);
      IAtsVersion sawBld2Ver = atsApi.getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_2);
      IAtsVersion sawBld3Ver = atsApi.getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_3);

      changes.relate(sawBld1Ver, AtsRelationTypes.ParallelVersion_Child, sawBld2Ver);
      changes.relate(sawBld2Ver, AtsRelationTypes.ParallelVersion_Child, sawBld1Ver);
      changes.relate(sawBld2Ver, AtsRelationTypes.ParallelVersion_Child, sawBld3Ver);

      changes.execute();
   }

   private void createMarkdownRequirements() {
      IAtsChangeSet changes = atsApi.createChangeSet("Populate Demo DB - Create Markdown", SAW_Bld_1);
      ArtifactToken parent =
         atsApi.getQueryService().getArtifact(CoreArtifactTokens.SoftwareRequirementsFolder, SAW_Bld_1);

      ArtifactToken robotArt = changes.createArtifact(parent, DemoArtifactToken.RobotApiSwMarkdown);

      for (String childName : Arrays.asList("CISST fundamental data types", "Events", "Functional Specification",
         "Interface Initialization", "Read-only Robots", "Robot Interfaces", "Robot collaboration",
         "Virtual fixtures")) {
         ArtifactToken artifact =
            changes.createArtifact(robotArt, CoreArtifactTypes.SoftwareRequirementMarkdown, childName);
         String markdown = String.format("## %s \n\nThe API shall generate %s to notify the user " //
            + "application about asynchronous actions detected by the lower level software.  " //
            + "The %s of the individual and collaborative robot objects shall be documented " //
            + "in an external database/document.", childName, childName, childName, childName);
         changes.setSoleAttributeValue(artifact, CoreAttributeTypes.MarkdownContent, markdown);
      }
      changes.execute();
   }

}
