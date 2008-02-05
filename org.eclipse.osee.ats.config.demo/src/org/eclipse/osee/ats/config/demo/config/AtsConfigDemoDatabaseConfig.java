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
package org.eclipse.osee.ats.config.demo.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.config.ImportWorkflowAction;
import org.eclipse.osee.ats.config.LoadAIsAndTeamsAction;
import org.eclipse.osee.ats.config.demo.OseeAtsConfigDemoPlugin;
import org.eclipse.osee.ats.config.demo.util.DemoTeams;
import org.eclipse.osee.ats.config.demo.util.DemoTeams.Team;
import org.eclipse.osee.framework.database.initialize.tasks.DbInitializationTask;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;

/**
 * Initialization class that will load configuration information for a sample DB.
 * 
 * @author Donald G. Dunne
 */
public class AtsConfigDemoDatabaseConfig extends DbInitializationTask {

   private Artifact systemReq;

   public void run(Connection connection) throws Exception {
      // Imports ATS workflow vue diagrams as id specified in extension point
      (new ImportWorkflowAction(false, AtsPlugin.PLUGIN_ID)).run();
      // Imports workflow vue diagrams as id specified in extension point
      (new ImportWorkflowAction(false, OseeAtsConfigDemoPlugin.PLUGIN_ID)).run();
      // Creates Actionable Items and Teams
      // Teams are related to workflow by id specified in team object in VUE diagram
      (new LoadAIsAndTeamsAction(false, OseeAtsConfigDemoPlugin.PLUGIN_ID)).run();
      // Create initial version artifacts for Widget teams
      createVersionArtifacts();
      // Create SAW_Bld_1 branch
      createProgramBranch(SawBuilds.SAW_Bld_1.name());
      populateProgramBranch(SawBuilds.SAW_Bld_1.name());
      // Create build one branch for CIS
      createProgramBranch(CISBuilds.CIS_Bld_1.name());
      populateProgramBranch(CISBuilds.CIS_Bld_1.name());

      // Map team definitions versions to their related branches
      mapTeamVersionToBranch(DemoTeams.getInstance().getTeamDef(Team.SAW_SW), SawBuilds.SAW_Bld_1.name(),
            SawBuilds.SAW_Bld_1.name());
      mapTeamVersionToBranch(DemoTeams.getInstance().getTeamDef(Team.CIS_SW), CISBuilds.CIS_Bld_1.name(),
            CISBuilds.CIS_Bld_1.name());
   }

   public static void mapTeamVersionToBranch(TeamDefinitionArtifact teamDef, String versionName, String branchName) throws Exception {
      Branch branch = BranchPersistenceManager.getInstance().getBranch(branchName);
      VersionArtifact verArt = teamDef.getVersionArtifact(versionName, false);
      verArt.setSoleAttributeValue(ATSAttributes.PARENT_BRANCH_ID_ATTRIBUTE.getStoreName(),
            String.valueOf(branch.getBranchId()));
      verArt.persistAttributes();
   }

   public static enum SawBuilds {
      SAW_Bld_1, SAW_Bld_2, SAW_Bld_3;
   };

   public static enum CISBuilds {
      CIS_Bld_1, CIS_Bld_2, CIS_Bld_3;
   };

   private void populateProgramBranch(String branchName) throws SQLException {
      ConfigurationPersistenceManager configurationManager = ConfigurationPersistenceManager.getInstance();
      BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();

      String[] subsystems =
            new String[] {"Video Processing", "Robot API", "Other Device API", "Calibration", "Registration",
                  "Tool Tracking", "Telesurgery", "Volume", "Hardware", "Imaging", "Electrical", "Sensors",
                  "Hydraulics", "Navigation", "Backup", "Accuracy", "Propulsion", "Unknown"};

      Branch programBranch = branchManager.getKeyedBranch(branchName);
      ArtifactSubtypeDescriptor descriptor =
            configurationManager.getArtifactSubtypeDescriptor("Component", programBranch);
      Artifact sawProduct = descriptor.makeNewArtifact();
      sawProduct.setDescriptiveName("SAW Product Decomposition");

      for (String subsystem : subsystems) {
         Artifact artifact = descriptor.makeNewArtifact();
         artifact.setDescriptiveName(subsystem);
         sawProduct.addChild(artifact);
      }

      Artifact programRoot =
            ArtifactPersistenceManager.getInstance().getDefaultHierarchyRootArtifact(programBranch, true);
      programRoot.addChild(sawProduct);

      descriptor = configurationManager.getArtifactSubtypeDescriptor("Folder", programBranch);
      systemReq = descriptor.makeNewArtifact();
      systemReq.setDescriptiveName("System Requirements");
      programRoot.addChild(systemReq);

      descriptor = configurationManager.getArtifactSubtypeDescriptor("Folder", programBranch);
      Artifact subsystem = descriptor.makeNewArtifact();
      subsystem.setDescriptiveName("Subsystem Requirements");
      programRoot.addChild(subsystem);

      descriptor = configurationManager.getArtifactSubtypeDescriptor("Folder", programBranch);
      Artifact software = descriptor.makeNewArtifact();
      software.setDescriptiveName("Software Requirements");
      programRoot.addChild(software);

      descriptor = configurationManager.getArtifactSubtypeDescriptor("Folder", programBranch);
      Artifact hardware = descriptor.makeNewArtifact();
      hardware.setDescriptiveName("Hardware Requirements");
      programRoot.addChild(hardware);

      programRoot.persist(true);

   }

   private void createProgramBranch(String branchName) throws Exception {

      List<String> skynetTypeImport = new ArrayList<String>();
      skynetTypeImport.add("org.eclipse.osee.framework.skynet.core.ProgramAndCommon");
      skynetTypeImport.add("org.eclipse.osee.framework.skynet.core.ProgramBranch");
      skynetTypeImport.add("org.eclipse.osee.ats.config.demo.Demo_Program_Skynet_Types");

      BranchPersistenceManager.getInstance().createRootBranch(null, branchName, branchName, skynetTypeImport, true);
   }

   private void createVersionArtifacts() throws Exception {

      // Setup some sample builds for Widget A
      for (String verName : new String[] {SawBuilds.SAW_Bld_1.name(), SawBuilds.SAW_Bld_2.name(),
            SawBuilds.SAW_Bld_3.name()}) {
         VersionArtifact ver =
               (VersionArtifact) ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                     VersionArtifact.ARTIFACT_NAME, BranchPersistenceManager.getInstance().getAtsBranch()).makeNewArtifact();
         ver.setDescriptiveName(verName);
         if (verName.contains("1")) ver.setReleased(true);
         if (verName.contains("2")) ver.setSoleBooleanAttributeValue(
               ATSAttributes.NEXT_VERSION_ATTRIBUTE.getStoreName(), true);
         DemoTeams.getInstance().getTeamDef(Team.SAW_SW).relate(RelationSide.TeamDefinitionToVersion_Version, ver);
         ver.persist(true);
      }

      // Setup some sample builds for Widget B
      for (String verName : new String[] {CISBuilds.CIS_Bld_1.name(), CISBuilds.CIS_Bld_2.name(),
            CISBuilds.CIS_Bld_3.name()}) {
         VersionArtifact ver =
               (VersionArtifact) ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                     VersionArtifact.ARTIFACT_NAME, BranchPersistenceManager.getInstance().getAtsBranch()).makeNewArtifact();
         ver.setDescriptiveName(verName);
         if (verName.contains("1")) ver.setReleased(true);
         if (verName.contains("2")) ver.setSoleBooleanAttributeValue(
               ATSAttributes.NEXT_VERSION_ATTRIBUTE.getStoreName(), true);
         DemoTeams.getInstance().getTeamDef(Team.CIS_SW).relate(RelationSide.TeamDefinitionToVersion_Version, ver);
         ver.persist(true);
      }
   }
}
