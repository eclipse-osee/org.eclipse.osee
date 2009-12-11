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
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workflow.vue.AtsDbConfig;
import org.eclipse.osee.framework.core.data.IOseeBranch;
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
      BranchManager.createTopLevelBranch(DemoSawBuilds.SAW_Bld_1);
      populateProgramBranch(DemoSawBuilds.SAW_Bld_1);

      // Create build one branch for CIS
      BranchManager.createTopLevelBranch(DemoCISBuilds.CIS_Bld_1);
      populateProgramBranch(DemoCISBuilds.CIS_Bld_1);

      // Map team definitions versions to their related branches
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Demo Database Config");
      mapTeamVersionToBranch(DemoTeams.getInstance().getTeamDef(Team.SAW_SW), DemoSawBuilds.SAW_Bld_1.name(),
            DemoSawBuilds.SAW_Bld_1.name(), transaction);
      mapTeamVersionToBranch(DemoTeams.getInstance().getTeamDef(Team.CIS_SW), DemoCISBuilds.CIS_Bld_1.name(),
            DemoCISBuilds.CIS_Bld_1.name(), transaction);

      // Set Joe Smith as Priviledged Member of SAW Test
      Artifact teamDef =
            ArtifactQuery.getArtifactFromTypeAndName(TeamDefinitionArtifact.ARTIFACT_NAME, "SAW Test",
                  AtsUtil.getAtsBranch());
      teamDef.addRelation(AtsRelationTypes.PrivilegedMember_Member, DemoDbUtil.getDemoUser(DemoUsers.Joe_Smith));
      teamDef.persist(transaction);

      transaction.execute();

      OseeInfo.putValue(OseeInfo.DB_TYPE_KEY, "demo");
   }

   public static void mapTeamVersionToBranch(TeamDefinitionArtifact teamDef, String versionName, String branchName, SkynetTransaction transaction) throws OseeCoreException {
      Branch branch = BranchManager.getBranch(branchName);
      VersionArtifact verArt = teamDef.getVersionArtifact(versionName, false);
      verArt.setSoleAttributeValue(ATSAttributes.BASELINE_BRANCH_GUID_ATTRIBUTE.getStoreName(), branch.getGuid());
      verArt.persist(transaction);
   }

   private void populateProgramBranch(IOseeBranch branch) throws OseeCoreException {
      Branch programBranch = BranchManager.getBranch(branch);
      Artifact sawProduct =
            ArtifactTypeManager.addArtifact(Requirements.COMPONENT, programBranch, "SAW Product Decomposition");

      for (String subsystem : DemoSubsystems.getSubsystems()) {
         sawProduct.addChild(ArtifactTypeManager.addArtifact(Requirements.COMPONENT, programBranch, subsystem));
      }

      Artifact programRoot = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(programBranch);
      programRoot.addChild(sawProduct);

      for (String name : new String[] {Requirements.SYSTEM_REQUIREMENTS, Requirements.SUBSYSTEM_REQUIREMENTS,
            Requirements.SOFTWARE_REQUIREMENTS, Requirements.HARDWARE_REQUIREMENTS, "Verification Tests",
            "Validation Tests", "Integration Tests"}) {
         programRoot.addChild(ArtifactTypeManager.addArtifact("Folder", programBranch, name));
      }

      sawProduct.persist();
      programRoot.persist();

   }

   private void createVersionArtifacts() throws OseeCoreException {

      // Setup some sample builds for Widget A
      for (String verName : new String[] {DemoSawBuilds.SAW_Bld_1.name(), DemoSawBuilds.SAW_Bld_2.name(),
            DemoSawBuilds.SAW_Bld_3.name()}) {
         VersionArtifact ver =
               (VersionArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.Version, AtsUtil.getAtsBranch(),
                     verName);
         if (verName.contains("1")) {
            ver.setReleased(true);
         }
         if (verName.contains("2")) {
            ver.setSoleAttributeValue(ATSAttributes.NEXT_VERSION_ATTRIBUTE.getStoreName(), true);
            ver.setSoleAttributeValue(ATSAttributes.ALLOW_COMMIT_BRANCH.getStoreName(), true);
            ver.setSoleAttributeValue(ATSAttributes.ALLOW_CREATE_BRANCH.getStoreName(), true);
         }
         DemoTeams.getInstance().getTeamDef(Team.SAW_SW).addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version,
               ver);
         ver.persist();
      }

      // Setup some sample builds for Widget B
      for (String verName : new String[] {DemoCISBuilds.CIS_Bld_1.name(), DemoCISBuilds.CIS_Bld_2.name(),
            DemoCISBuilds.CIS_Bld_3.name()}) {
         VersionArtifact ver =
               (VersionArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.Version, AtsUtil.getAtsBranch(),
                     verName);
         if (verName.contains("1")) {
            ver.setReleased(true);
         }
         if (verName.contains("2")) {
            ver.setSoleAttributeValue(ATSAttributes.NEXT_VERSION_ATTRIBUTE.getStoreName(), true);
         }
         DemoTeams.getInstance().getTeamDef(Team.CIS_SW).addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version,
               ver);
         ver.persist();
      }
   }
}
