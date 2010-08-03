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

import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.config.demo.DemoCISBuilds;
import org.eclipse.osee.ats.config.demo.DemoSubsystems;
import org.eclipse.osee.ats.config.demo.internal.OseeAtsConfigDemoActivator;
import org.eclipse.osee.ats.config.demo.util.DemoTeams;
import org.eclipse.osee.ats.config.demo.util.DemoTeams.Team;
import org.eclipse.osee.ats.config.demo.workflow.DemoCodeWorkFlowDefinition;
import org.eclipse.osee.ats.config.demo.workflow.DemoReqWorkFlowDefinition;
import org.eclipse.osee.ats.config.demo.workflow.DemoSWDesignWorkFlowDefinition;
import org.eclipse.osee.ats.config.demo.workflow.DemoTestWorkFlowDefinition;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workflow.vue.AtsDbConfig;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.OseeInfo;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition.WriteType;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.DemoUsers;

/**
 * Initialization class that will load configuration information for a sample DB.
 * 
 * @author Donald G. Dunne
 */
public class DemoDatabaseConfig extends AtsDbConfig implements IDbInitializationTask {

   @Override
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
      BranchManager.createTopLevelBranch(DemoSawBuilds.SAW_Bld_1);
      populateProgramBranch(DemoSawBuilds.SAW_Bld_1);

      // Create build one branch for CIS
      BranchManager.createTopLevelBranch(DemoCISBuilds.CIS_Bld_1);
      populateProgramBranch(DemoCISBuilds.CIS_Bld_1);

      // Map team definitions versions to their related branches
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Demo Database Config");
      mapTeamVersionToBranch(DemoTeams.getInstance().getTeamDef(Team.SAW_SW), DemoSawBuilds.SAW_Bld_1.getName(),
         DemoSawBuilds.SAW_Bld_1.getName(), transaction);
      mapTeamVersionToBranch(DemoTeams.getInstance().getTeamDef(Team.CIS_SW), DemoCISBuilds.CIS_Bld_1.getName(),
         DemoCISBuilds.CIS_Bld_1.getName(), transaction);

      // Set Joe Smith as Priviledged Member of SAW Test
      Artifact teamDef =
         ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.TeamDefinition, "SAW Test", AtsUtil.getAtsBranch());
      teamDef.addRelation(AtsRelationTypes.PrivilegedMember_Member, DemoDbUtil.getDemoUser(DemoUsers.Joe_Smith));
      teamDef.persist(transaction);

      transaction.execute();

      OseeInfo.putValue(OseeInfo.DB_TYPE_KEY, "demo");
   }

   public static void mapTeamVersionToBranch(TeamDefinitionArtifact teamDef, String versionName, String branchName, SkynetTransaction transaction) throws OseeCoreException {
      Branch branch = BranchManager.getBranch(branchName);
      VersionArtifact verArt = teamDef.getVersionArtifact(versionName, false);
      verArt.setSoleAttributeValue(AtsAttributeTypes.ATS_BASELINE_BRANCH_GUID, branch.getGuid());
      verArt.persist(transaction);
   }

   private void populateProgramBranch(IOseeBranch branch) throws OseeCoreException {
      Branch programBranch = BranchManager.getBranch(branch);
      Artifact sawProduct =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.Component, programBranch, "SAW Product Decomposition");

      for (String subsystem : DemoSubsystems.getSubsystems()) {
         sawProduct.addChild(ArtifactTypeManager.addArtifact(CoreArtifactTypes.Component, programBranch, subsystem));
      }

      Artifact programRoot = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(programBranch);
      programRoot.addChild(sawProduct);

      for (String name : new String[] {Requirements.SYSTEM_REQUIREMENTS, Requirements.SUBSYSTEM_REQUIREMENTS,
         Requirements.SOFTWARE_REQUIREMENTS, Requirements.HARDWARE_REQUIREMENTS, "Verification Tests",
         "Validation Tests", "Integration Tests"}) {
         programRoot.addChild(ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, programBranch, name));
      }

      sawProduct.persist();
      programRoot.persist();

   }

   private void createVersionArtifacts() throws OseeCoreException {
      IOseeBranch atsBranch = AtsUtil.getAtsBranch();
      // Setup some sample builds for Widget A
      createVersionArtifacts(atsBranch, Team.SAW_SW, DemoSawBuilds.SAW_Bld_1, DemoSawBuilds.SAW_Bld_2,
         DemoSawBuilds.SAW_Bld_3);

      // Setup some sample builds for Widget B
      createVersionArtifacts(atsBranch, Team.CIS_SW, DemoCISBuilds.CIS_Bld_1, DemoCISBuilds.CIS_Bld_2,
         DemoCISBuilds.CIS_Bld_3);
   }

   private void createVersionArtifacts(IOseeBranch atsBranch, Team team, IOseeBranch... demoBranches) throws OseeCoreException {
      TeamDefinitionArtifact teamDef = DemoTeams.getInstance().getTeamDef(team);
      for (IOseeBranch demoBranch : demoBranches) {
         String versionName = demoBranch.getName();
         VersionArtifact versionArtifact =
            (VersionArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.Version, atsBranch, versionName);

         if (DemoCISBuilds.CIS_Bld_1.equals(demoBranch) || DemoSawBuilds.SAW_Bld_1.equals(demoBranch)) {
            versionArtifact.setReleased(true);
         }
         if (DemoCISBuilds.CIS_Bld_2.equals(demoBranch) || DemoSawBuilds.SAW_Bld_2.equals(demoBranch)) {
            versionArtifact.setSoleAttributeValue(AtsAttributeTypes.ATS_NEXT_VERSION, true);
         }
         if (DemoSawBuilds.SAW_Bld_2.equals(demoBranch)) {
            versionArtifact.setSoleAttributeValue(AtsAttributeTypes.ATS_ALLOW_COMMIT_BRANCH, true);
            versionArtifact.setSoleAttributeValue(AtsAttributeTypes.ATS_ALLOW_CREATE_BRANCH, true);
         }
         teamDef.addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, versionArtifact);
         versionArtifact.persist();
      }
   }
}
