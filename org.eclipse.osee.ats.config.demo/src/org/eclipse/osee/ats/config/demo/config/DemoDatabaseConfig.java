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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.config.demo.internal.OseeAtsConfigDemoActivator;
import org.eclipse.osee.ats.config.demo.util.DemoTeams;
import org.eclipse.osee.ats.config.demo.util.DemoTeams.Team;
import org.eclipse.osee.ats.config.demo.workflow.DemoCodeWorkFlowDefinition;
import org.eclipse.osee.ats.config.demo.workflow.DemoReqWorkFlowDefinition;
import org.eclipse.osee.ats.config.demo.workflow.DemoSWDesignWorkFlowDefinition;
import org.eclipse.osee.ats.config.demo.workflow.DemoTestWorkFlowDefinition;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.workflow.vue.AtsDbConfig;
import org.eclipse.osee.framework.core.data.OseeInfo;
import org.eclipse.osee.framework.database.IDbInitializationTask;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition.WriteType;
import org.eclipse.osee.support.test.util.DemoCISBuilds;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.DemoSubsystems;
import org.eclipse.osee.support.test.util.DemoUsers;

/**
 * Initialization class that will load configuration information for a sample DB.
 * 
 * @author Donald G. Dunne
 */
public class DemoDatabaseConfig extends AtsDbConfig implements IDbInitializationTask {
   public void run() throws OseeCoreException {

      new DemoCodeWorkFlowDefinition().config(WriteType.New, null);
      new DemoTestWorkFlowDefinition().config(WriteType.New, null);
      new DemoReqWorkFlowDefinition().config(WriteType.New, null);
      new DemoSWDesignWorkFlowDefinition().config(WriteType.New, null);

      // Creates Actionable Items and Teams
      // Teams are related to workflow by id specified in team object in VUE diagram
      executeLoadAIsAndTeamsAction(OseeAtsConfigDemoActivator.PLUGIN_ID);

      // Create initial version artifacts for Widget teams
      createVersionArtifacts();

      // Create SAW_Bld_1 branch
      createProgramBranch(DemoSawBuilds.SAW_Bld_1.name());
      populateProgramBranch(DemoSawBuilds.SAW_Bld_1.name());

      // Create build one branch for CIS
      createProgramBranch(DemoCISBuilds.CIS_Bld_1.name());
      populateProgramBranch(DemoCISBuilds.CIS_Bld_1.name());

      // Map team definitions versions to their related branches
      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      mapTeamVersionToBranch(DemoTeams.getInstance().getTeamDef(Team.SAW_SW), DemoSawBuilds.SAW_Bld_1.name(),
            DemoSawBuilds.SAW_Bld_1.name(), transaction);
      mapTeamVersionToBranch(DemoTeams.getInstance().getTeamDef(Team.CIS_SW), DemoCISBuilds.CIS_Bld_1.name(),
            DemoCISBuilds.CIS_Bld_1.name(), transaction);

      // Set Joe Smith as Priviledged Member of SAW Test
      Artifact teamDef =
            ArtifactQuery.getArtifactFromTypeAndName(TeamDefinitionArtifact.ARTIFACT_NAME, "SAW Test",
                  AtsPlugin.getAtsBranch());
      teamDef.addRelation(AtsRelation.PrivilegedMember_Member, DemoDbUtil.getDemoUser(DemoUsers.Joe_Smith));
      teamDef.persistAttributesAndRelations(transaction);

      transaction.execute();

      OseeInfo.putValue("osee.db.type", "demo");
   }

   public static void mapTeamVersionToBranch(TeamDefinitionArtifact teamDef, String versionName, String branchName, SkynetTransaction transaction) throws OseeCoreException {
      Branch branch = BranchManager.getBranch(branchName);
      VersionArtifact verArt = teamDef.getVersionArtifact(versionName, false);
      verArt.setSoleAttributeValue(ATSAttributes.PARENT_BRANCH_ID_ATTRIBUTE.getStoreName(), branch.getBranchId());
      verArt.persistAttributes(transaction);
   }

   private void populateProgramBranch(String branchName) throws OseeCoreException {

      Branch programBranch = BranchManager.getKeyedBranch(branchName);
      Artifact sawProduct =
            ArtifactTypeManager.addArtifact(Requirements.COMPONENT, programBranch, "SAW Product Decomposition");

      for (String subsystem : DemoSubsystems.getSubsystems()) {
         sawProduct.addChild(ArtifactTypeManager.addArtifact(Requirements.COMPONENT, programBranch, subsystem));
      }

      Artifact programRoot = ArtifactQuery.getDefaultHierarchyRootArtifact(programBranch);
      programRoot.addChild(sawProduct);

      for (String name : new String[] {Requirements.SYSTEM_REQUIREMENTS, Requirements.SUBSYSTEM_REQUIREMENTS,
            Requirements.SOFTWARE_REQUIREMENTS, Requirements.HARDWARE_REQUIREMENTS, "Verification Tests",
            "Validation Tests", "Integration Tests"}) {
         programRoot.addChild(ArtifactTypeManager.addArtifact("Folder", programBranch, name));
      }

      sawProduct.persistAttributesAndRelations();
      programRoot.persistAttributesAndRelations();

   }

   private void createProgramBranch(String branchName) throws OseeCoreException {

      List<String> skynetTypeImport = new ArrayList<String>();
      skynetTypeImport.add("org.eclipse.osee.framework.skynet.core.OseeTypes_ProgramAndCommon");
      skynetTypeImport.add("org.eclipse.osee.framework.skynet.core.OseeTypes_ProgramBranch");
      skynetTypeImport.add("org.eclipse.osee.ats.config.demo.OseeTypes_DemoProgram");

      BranchManager.createTopLevelBranch(branchName, branchName, skynetTypeImport, true);
   }

   private void createVersionArtifacts() throws OseeCoreException {

      // Setup some sample builds for Widget A
      for (String verName : new String[] {DemoSawBuilds.SAW_Bld_1.name(), DemoSawBuilds.SAW_Bld_2.name(),
            DemoSawBuilds.SAW_Bld_3.name()}) {
         VersionArtifact ver =
               (VersionArtifact) ArtifactTypeManager.addArtifact(VersionArtifact.ARTIFACT_NAME,
                     AtsPlugin.getAtsBranch(), verName);
         if (verName.contains("1")) {
            ver.setReleased(true);
         }
         if (verName.contains("2")) {
            ver.setSoleAttributeValue(ATSAttributes.NEXT_VERSION_ATTRIBUTE.getStoreName(), true);
            ver.setSoleAttributeValue(ATSAttributes.ALLOW_COMMIT_BRANCH.getStoreName(), true);
            ver.setSoleAttributeValue(ATSAttributes.ALLOW_CREATE_BRANCH.getStoreName(), true);
         }
         DemoTeams.getInstance().getTeamDef(Team.SAW_SW).addRelation(AtsRelation.TeamDefinitionToVersion_Version, ver);
         ver.persistAttributesAndRelations();
      }

      // Setup some sample builds for Widget B
      for (String verName : new String[] {DemoCISBuilds.CIS_Bld_1.name(), DemoCISBuilds.CIS_Bld_2.name(),
            DemoCISBuilds.CIS_Bld_3.name()}) {
         VersionArtifact ver =
               (VersionArtifact) ArtifactTypeManager.addArtifact(VersionArtifact.ARTIFACT_NAME,
                     AtsPlugin.getAtsBranch(), verName);
         if (verName.contains("1")) {
            ver.setReleased(true);
         }
         if (verName.contains("2")) {
            ver.setSoleAttributeValue(ATSAttributes.NEXT_VERSION_ATTRIBUTE.getStoreName(), true);
         }
         DemoTeams.getInstance().getTeamDef(Team.CIS_SW).addRelation(AtsRelation.TeamDefinitionToVersion_Version, ver);
         ver.persistAttributesAndRelations();
      }
   }
}
