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

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.client.demo.DemoArtifactToken;
import org.eclipse.osee.ats.client.demo.DemoCISBuilds;
import org.eclipse.osee.ats.client.demo.DemoSawBuilds;
import org.eclipse.osee.ats.client.demo.DemoSubsystems;
import org.eclipse.osee.ats.client.demo.DemoUsers;
import org.eclipse.osee.ats.core.client.util.AtsGroup;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
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
   }

   private void createWorkPackages() throws OseeCoreException {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtilCore.getAtsBranchToken(), "Create Work Packages");

      Artifact codeTeamArt =
         ArtifactQuery.getArtifactFromToken(DemoArtifactToken.SAW_Code, AtsUtilCore.getAtsBranchToken());

      Artifact workPkg1 = createWorkPackage("1", "ASDHFA443");
      workPkg1.addRelation(AtsRelationTypes.WorkPackage_TeamDefOrAi, codeTeamArt);
      workPkg1.persist(transaction);

      Artifact workPkg2 = createWorkPackage("2", "ASDHFA443");
      workPkg2.addRelation(AtsRelationTypes.WorkPackage_TeamDefOrAi, codeTeamArt);
      workPkg2.persist(transaction);

      Artifact testTeamArt =
         ArtifactQuery.getArtifactFromToken(DemoArtifactToken.SAW_Test_AI, AtsUtilCore.getAtsBranchToken());

      Artifact workPkg11 = createWorkPackage("A", "AHESSH3");
      workPkg11.addRelation(AtsRelationTypes.WorkPackage_TeamDefOrAi, testTeamArt);
      workPkg11.persist(transaction);

      Artifact workPkg21 = createWorkPackage("B", "HAKSHD3");
      workPkg21.addRelation(AtsRelationTypes.WorkPackage_TeamDefOrAi, testTeamArt);
      workPkg21.persist(transaction);

      transaction.execute();
   }

   private Artifact createWorkPackage(String id, String activityId) throws OseeCoreException {
      Artifact workPkg1 =
         ArtifactTypeManager.addArtifact(AtsArtifactTypes.WorkPackage, AtsUtilCore.getAtsBranchToken(), "WP 0" + id);
      workPkg1.addAttributeFromString(AtsAttributeTypes.WorkPackageId, "WP_0" + id);
      workPkg1.addAttributeFromString(AtsAttributeTypes.WorkPackageProgram, "Program A");
      workPkg1.addAttributeFromString(AtsAttributeTypes.WorkPackageType, "LOE");
      workPkg1.addAttributeFromString(AtsAttributeTypes.ActivityId, activityId);
      workPkg1.addAttributeFromString(AtsAttributeTypes.ActivityName, "HUF 2" + id + "0");
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
