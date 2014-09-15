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
package org.eclipse.osee.ats.client.demo.config;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.client.demo.DemoArtifactToken;
import org.eclipse.osee.ats.client.demo.DemoCISBuilds;
import org.eclipse.osee.ats.client.demo.DemoSawBuilds;
import org.eclipse.osee.ats.client.demo.DemoSubsystems;
import org.eclipse.osee.ats.client.demo.DemoUsers;
import org.eclipse.osee.ats.core.client.util.AtsGroup;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * Initialization class that will load configuration information for a sample DB.
 * 
 * @author Donald G. Dunne
 */
public class DemoDatabaseConfig implements IDbInitializationTask {

   @Override
   public void run() throws OseeCoreException {

      TestUtil.setDemoDb(true);

      // Create SAW_Bld_1 branch
      BranchManager.createTopLevelBranch(DemoSawBuilds.SAW_Bld_1);
      populateProgramBranch(DemoSawBuilds.SAW_Bld_1);

      // Create build one branch for CIS
      BranchManager.createTopLevelBranch(DemoCISBuilds.CIS_Bld_1);
      populateProgramBranch(DemoCISBuilds.CIS_Bld_1);

      AtsGroup.AtsTempAdmin.addMember(UserManager.getUser(DemoUsers.Joe_Smith));
      AtsGroup.AtsTempAdmin.getArtifact().persist("Set Joe as Temp Admin");

      // Create Work Packages
      createWorkPackages();
      createPrograms();
   }

   private void createPrograms() throws OseeCoreException {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), "Create Programs");

      Artifact sawSw = ArtifactQuery.getArtifactFromToken(DemoArtifactToken.SAW_SW, AtsUtilCore.getAtsBranch());
      Artifact sawProgram = ArtifactTypeManager.addArtifact(DemoArtifactToken.SAW_Program, AtsUtilCore.getAtsBranch());
      sawProgram.setSoleAttributeValue(AtsAttributeTypes.Namespace, "org.demo.saw");
      sawProgram.setSoleAttributeValue(AtsAttributeTypes.Description, "Program object for SAW Program");
      sawProgram.setSoleAttributeValue(AtsAttributeTypes.TeamDefinition, sawSw.getGuid());
      sawProgram.persist(transaction);

      Artifact cisSw = ArtifactQuery.getArtifactFromToken(DemoArtifactToken.CIS_SW, AtsUtilCore.getAtsBranch());
      Artifact cisProgram = ArtifactTypeManager.addArtifact(DemoArtifactToken.CIS_Program, AtsUtilCore.getAtsBranch());
      cisProgram.setSoleAttributeValue(AtsAttributeTypes.Namespace, "org.demo.cis");
      cisProgram.setSoleAttributeValue(AtsAttributeTypes.Description, "Program object for CIS Program");
      cisProgram.setSoleAttributeValue(AtsAttributeTypes.TeamDefinition, cisSw.getGuid());
      cisProgram.persist(transaction);

      transaction.execute();
   }

   private void createWorkPackages() throws OseeCoreException {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), "Create Work Packages");

      Artifact codeTeamArt = ArtifactQuery.getArtifactFromToken(DemoArtifactToken.SAW_Code, AtsUtilCore.getAtsBranch());

      Artifact workPkg1 = createWorkPackage(DemoArtifactToken.SAW_Code_Team_WorkPackage_01, "ASDHFA443");
      workPkg1.addRelation(AtsRelationTypes.WorkPackage_TeamDefOrAi, codeTeamArt);
      workPkg1.persist(transaction);

      Artifact workPkg2 = createWorkPackage(DemoArtifactToken.SAW_Code_Team_WorkPackage_02, "ASDHFA443");
      workPkg2.addRelation(AtsRelationTypes.WorkPackage_TeamDefOrAi, codeTeamArt);
      workPkg2.persist(transaction);

      Artifact workPkg3 = createWorkPackage(DemoArtifactToken.SAW_Code_Team_WorkPackage_03, "ASDHFA443");
      workPkg3.setSoleAttributeValue(AtsAttributeTypes.Active, false);
      workPkg3.addRelation(AtsRelationTypes.WorkPackage_TeamDefOrAi, codeTeamArt);
      workPkg3.persist(transaction);

      Artifact testTeamArt =
         ArtifactQuery.getArtifactFromToken(DemoArtifactToken.SAW_Test_AI, AtsUtilCore.getAtsBranch());

      Artifact workPkg11 = createWorkPackage(DemoArtifactToken.SAW_Test_AI_WorkPackage_0A, "AHESSH3");
      workPkg11.addRelation(AtsRelationTypes.WorkPackage_TeamDefOrAi, testTeamArt);
      workPkg11.persist(transaction);

      Artifact workPkg21 = createWorkPackage(DemoArtifactToken.SAW_Test_AI_WorkPackage_0B, "HAKSHD3");
      workPkg21.addRelation(AtsRelationTypes.WorkPackage_TeamDefOrAi, testTeamArt);
      workPkg21.persist(transaction);

      Artifact workPkg31 = createWorkPackage(DemoArtifactToken.SAW_Test_AI_WorkPackage_0C, "EHA4DS");
      workPkg31.setSoleAttributeValue(AtsAttributeTypes.Active, false);
      workPkg31.addRelation(AtsRelationTypes.WorkPackage_TeamDefOrAi, testTeamArt);
      workPkg31.persist(transaction);

      transaction.execute();
   }

   private Artifact createWorkPackage(IArtifactToken workPackageToken, String activityId) throws OseeCoreException {
      Artifact workPkg1 = ArtifactTypeManager.addArtifact(workPackageToken, AtsUtilCore.getAtsBranch());
      char charAt = workPackageToken.getName().charAt(workPackageToken.getName().length() - 1);
      workPkg1.addAttributeFromString(AtsAttributeTypes.WorkPackageId, "WP_0" + charAt);
      workPkg1.addAttributeFromString(AtsAttributeTypes.WorkPackageProgram, "Program A");
      workPkg1.addAttributeFromString(AtsAttributeTypes.WorkPackageType, "LOE");
      workPkg1.addAttributeFromString(AtsAttributeTypes.ActivityId, activityId);
      workPkg1.addAttributeFromString(AtsAttributeTypes.ActivityName, "HUF 2" + charAt + "0");
      return workPkg1;
   }

   private void populateProgramBranch(IOseeBranch programBranch) throws OseeCoreException {
      Artifact sawProduct =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.Component, programBranch, "SAW Product Decomposition");

      for (String subsystem : DemoSubsystems.getSubsystems()) {
         sawProduct.addChild(ArtifactTypeManager.addArtifact(CoreArtifactTypes.Component, programBranch, subsystem));
      }

      Artifact programRoot = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(programBranch);
      programRoot.addChild(sawProduct);

      for (String name : new String[] {
         Requirements.SYSTEM_REQUIREMENTS,
         Requirements.SUBSYSTEM_REQUIREMENTS,
         Requirements.SOFTWARE_REQUIREMENTS,
         Requirements.HARDWARE_REQUIREMENTS,
         "Verification Tests",
         "Validation Tests",
         "Integration Tests"}) {
         programRoot.addChild(ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, programBranch, name));
      }

      sawProduct.persist(getClass().getSimpleName());
      programRoot.persist(getClass().getSimpleName());

   }

}
