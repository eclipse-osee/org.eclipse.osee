/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.client.integration.tests.integration.orcs.rest;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.JsonArtifact;
import org.eclipse.osee.framework.core.data.JsonRelations;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.NewBranch;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Audrey Denk
 */
public class BranchEndpointTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   private static BranchEndpoint branchEndpoint;

   @BeforeClass
   public static void testSetup() {
      OseeClient oseeclient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);
      branchEndpoint = oseeclient.getBranchEndpoint();
   }

   private static NewBranch testDataInitialization() {
      NewBranch data = new NewBranch();
      data.setAssociatedArtifact(ArtifactId.SENTINEL);
      data.setAuthor(UserManager.getUser());
      data.setBranchName("TestBranch");
      data.setBranchType(BranchType.WORKING);
      data.setCreationComment("For Test");
      data.setMergeAddressingQueryId(0);
      data.setMergeDestinationBranchId(null);
      data.setParentBranchId(CoreBranches.SYSTEM_ROOT);
      data.setSourceTransactionId(TransactionManager.getHeadTransaction(CoreBranches.SYSTEM_ROOT));
      data.setTxCopyBranchType(false);

      return data;
   }

   @Test
   public void getTxs() {
      List<Branch> baselineBranches = branchEndpoint.getBaselineBranches();
      Assert.assertFalse(baselineBranches.isEmpty());
   }

   @Test
   public void getBranches() {
      List<Branch> baselineBranches = branchEndpoint.getBranches("", "", "", false, false, "", "", null, null);
      List<BranchId> branchIds = new ArrayList<>();
      for (Branch branch : baselineBranches) {
         branchIds.add(BranchId.valueOf(branch.getId()));
      }
      List<BranchId> originalBranches = new ArrayList<>();
      //Selecting branches from CreateDemoBranches.java
      originalBranches.add(CoreBranches.COMMON);
      originalBranches.add(DemoBranches.CIS_Bld_1);
      originalBranches.add(DemoBranches.SAW_Bld_1);
      originalBranches.add(DemoBranches.SAW_PL);
      Assert.assertFalse(baselineBranches.isEmpty());
      boolean allBranchesContained = true;
      for (BranchId branchID : originalBranches) {

         if (!branchIds.contains(branchID)) {
            allBranchesContained = false;
         }
      }
      Assert.assertTrue(allBranchesContained);
      originalBranches.add(BranchId.SENTINEL);
      for (BranchId branchID : originalBranches) {

         if (!branchIds.contains(branchID)) {
            allBranchesContained = false;
         }
      }
      Assert.assertFalse(allBranchesContained);

   }

   @Test
   public void getBranchesByUUID() {
      List<BranchId> originalBranches = new ArrayList<>();
      //Selecting branches from CreateDemoBranches.java
      originalBranches.add(CoreBranches.COMMON);
      originalBranches.add(DemoBranches.SAW_PL);

      String branchUuIds = String.format("%s,%s", CoreBranches.COMMON.getIdString(), DemoBranches.SAW_PL.getIdString());
      List<Branch> baselineBranches = branchEndpoint.getBranches(branchUuIds, "", "", false, false, "", "", null, null);
      List<BranchId> branchIds = new ArrayList<>();
      for (Branch branch : baselineBranches) {
         branchIds.add(BranchId.valueOf(branch.getId()));
      }
      Assert.assertFalse(baselineBranches.isEmpty());
      boolean allBranchesContained = true;
      for (BranchId branchID : originalBranches) {

         if (!branchIds.contains(branchID)) {
            allBranchesContained = false;
         }
      }
      Assert.assertTrue(allBranchesContained);
   }

   @Test
   public void getBranchesByTypeWorking() {
      BranchManager.createWorkingBranch(BranchId.valueOf(DemoBranches.SAW_PL.getId()), BranchType.WORKING.toString());
      List<Branch> workingBranches =
         branchEndpoint.getBranches("", BranchType.WORKING.toString(), "", false, false, "", "", null, null);

      boolean foundCreatedWorkingBranch = false;
      for (Branch branch : workingBranches) {
         Assert.assertTrue(branch.getBranchType().equals(BranchType.WORKING));
         if (branch.getName().equals(BranchType.WORKING.toString())) {
            foundCreatedWorkingBranch = true;
         }
      }
      Assert.assertTrue(foundCreatedWorkingBranch);
   }

   @Test
   public void getBranchesByTypeBaseline() {
      List<BranchId> originalBranches = new ArrayList<>();
      //Selecting branches from CreateDemoBranches.java
      originalBranches.add(CoreBranches.COMMON);
      originalBranches.add(DemoBranches.SAW_PL);

      List<Branch> baselineBranches = branchEndpoint.getBranches("", "BASELINE", "", false, false, "", "", null, null);
      List<BranchId> branchIds = new ArrayList<>();
      for (Branch branch : baselineBranches) {
         branchIds.add(BranchId.valueOf(branch.getId()));
      }
      Assert.assertFalse(baselineBranches.isEmpty());
      boolean allBranchesContained = true;
      for (BranchId branchID : originalBranches) {

         if (!branchIds.contains(branchID)) {
            allBranchesContained = false;
         }
      }
      Assert.assertTrue(allBranchesContained);
   }

   @Test
   public void getBranchesByStateModified() {
      List<BranchId> originalBranches = new ArrayList<>();
      //Selecting branches from CreateDemoBranches.java
      originalBranches.add(CoreBranches.COMMON);
      originalBranches.add(DemoBranches.SAW_PL);

      List<Branch> modifiedBranches = branchEndpoint.getBranches("", "", "MODIFIED", false, false, "", "", null, null);
      List<BranchId> branchIds = new ArrayList<>();
      for (Branch branch : modifiedBranches) {
         branchIds.add(BranchId.valueOf(branch.getId()));
      }
      Assert.assertFalse(modifiedBranches.isEmpty());
      boolean allBranchesContained = true;
      for (BranchId branchID : originalBranches) {

         if (!branchIds.contains(branchID)) {
            allBranchesContained = false;
         }
      }
      Assert.assertTrue(allBranchesContained);
   }

   @Test
   public void getBranchesByDeleted() {
      List<BranchId> originalBranches = new ArrayList<>();
      //Selecting branches from CreateDemoBranches.java
      originalBranches.add(CoreBranches.COMMON);
      originalBranches.add(DemoBranches.CIS_Bld_1);
      originalBranches.add(DemoBranches.SAW_Bld_1);
      originalBranches.add(DemoBranches.SAW_PL);

      List<Branch> deletedBranches = branchEndpoint.getBranches("", "", "", true, false, "", "", null, null);
      List<BranchId> branchIds = new ArrayList<>();
      for (Branch branch : deletedBranches) {
         branchIds.add(BranchId.valueOf(branch.getId()));
      }
      Assert.assertFalse(deletedBranches.isEmpty());
      boolean allBranchesContained = true;
      for (BranchId branchID : originalBranches) {

         if (!branchIds.contains(branchID)) {
            allBranchesContained = false;
         }
      }
      Assert.assertTrue(allBranchesContained);
   }

   @Test
   public void getBranchesByArchived() {
      List<BranchId> originalBranches = new ArrayList<>();
      //Selecting branches from CreateDemoBranches.java
      originalBranches.add(CoreBranches.COMMON);
      originalBranches.add(DemoBranches.CIS_Bld_1);
      originalBranches.add(DemoBranches.SAW_Bld_1);
      originalBranches.add(DemoBranches.SAW_PL);

      List<Branch> archivedBranches = branchEndpoint.getBranches("", "", "", false, true, "", "", null, null);
      List<BranchId> branchIds = new ArrayList<>();
      for (Branch branch : archivedBranches) {
         branchIds.add(BranchId.valueOf(branch.getId()));
      }
      Assert.assertFalse(archivedBranches.isEmpty());
      boolean allBranchesContained = true;
      for (BranchId branchID : originalBranches) {

         if (!branchIds.contains(branchID)) {
            allBranchesContained = false;
         }
      }
      Assert.assertTrue(allBranchesContained);
   }

   @Test
   public void getBranchesByNameEquals() {
      List<BranchId> originalBranches = new ArrayList<>();
      //Selecting branches from CreateDemoBranches.java
      originalBranches.add(CoreBranches.COMMON);

      List<Branch> commonBranches = branchEndpoint.getBranches("", "", "", false, false, "Common", "", null, null);
      List<BranchId> branchIds = new ArrayList<>();
      for (Branch branch : commonBranches) {
         branchIds.add(BranchId.valueOf(branch.getId()));
      }
      Assert.assertFalse(commonBranches.isEmpty());
      boolean allBranchesContained = true;
      for (BranchId branchID : originalBranches) {

         if (!branchIds.contains(branchID)) {
            allBranchesContained = false;
         }
      }
      Assert.assertTrue(allBranchesContained);
   }

   @Test
   public void getBranchByNamePattern() {
      List<Branch> baselineBranches = branchEndpoint.getBranches("", "", "", false, false, "", "SAW.*", null, null);
      List<BranchId> branchIds = new ArrayList<>();
      for (Branch branch : baselineBranches) {
         branchIds.add(BranchId.valueOf(branch.getId()));
      }
      List<BranchId> expectedBranches = new ArrayList<>();
      expectedBranches.add(DemoBranches.SAW_Bld_1);
      expectedBranches.add(DemoBranches.SAW_PL);
      Assert.assertFalse(baselineBranches.isEmpty());
      boolean allBranchesContained = true;
      for (BranchId branchID : expectedBranches) {
         if (!branchIds.contains(branchID)) {
            allBranchesContained = false;
         }
      }
      Assert.assertTrue(allBranchesContained);
      if (baselineBranches.contains(CoreBranches.COMMON)) {
         Assert.fail("Common branch should be filtered by the name selection, and wasn't");
      }
   }

   @Test
   public void getBranchesByChildOf() {
      BranchManager.createWorkingBranch(BranchId.valueOf(DemoBranches.SAW_PL.getId()), BranchType.WORKING.toString());
      List<Branch> childBranches =
         branchEndpoint.getBranches("", "", "", false, false, "", "", DemoBranches.SAW_PL.getId(), null);
      boolean isEmpty = true;
      if (!childBranches.isEmpty()) {
         isEmpty = false;
      }
      Assert.assertFalse(isEmpty);
   }

   @Test
   public void getBranchesByAncestorOf() {
      List<Branch> ancestorBranches =
         branchEndpoint.getBranches("", "", "", false, false, "", "", null, DemoBranches.SAW_PL.getId());
      boolean isEmpty = true;
      if (!ancestorBranches.isEmpty()) {
         isEmpty = false;
      }
      Assert.assertFalse(isEmpty);
   }

   @Test
   public void getWorkingBranches() {
      List<Branch> workingBranchesList = branchEndpoint.getWorkingBranches();
      List<Branch> getBranches =
         branchEndpoint.getBranches("", BranchType.WORKING.toString(), "", false, false, "", "", null, null);

      Assert.assertFalse(workingBranchesList.isEmpty());
      for (Branch branch : workingBranchesList) {
         Assert.assertTrue(branch.getBranchType().equals(BranchType.WORKING));
      }
      List<Branch> workingBranchesFromGetBranches = new ArrayList<>();
      for (Branch branch : getBranches) {
         if (branch.getBranchType().equals(BranchType.WORKING)) {
            workingBranchesFromGetBranches.add(branch);
         }
      }
      Assert.assertTrue(workingBranchesFromGetBranches.equals(workingBranchesList));
   }

   @Test
   public void getBaselineBranches() {
      List<Branch> baseLineBranchesList = branchEndpoint.getBaselineBranches();
      List<Branch> getBranches =
         branchEndpoint.getBranches("", BranchType.BASELINE.toString(), "", false, false, "", "", null, null);

      Assert.assertFalse(baseLineBranchesList.isEmpty());
      for (Branch branch : baseLineBranchesList) {
         Assert.assertTrue(branch.getBranchType().equals(BranchType.BASELINE));
      }
      List<Branch> baselineBranchesFromGetBranches = new ArrayList<>();
      for (Branch branch : getBranches) {
         if (branch.getBranchType().equals(BranchType.BASELINE)) {
            baselineBranchesFromGetBranches.add(branch);
         }
      }
      Assert.assertTrue(baselineBranchesFromGetBranches.equals(baseLineBranchesList));
   }

   @Test
   public void getBranchById() {
      List<Branch> getBranches = branchEndpoint.getBranches("", "", "", false, false, "", "", null, null);

      Branch branchById = branchEndpoint.getBranchById(getBranches.get(0));
      for (Branch branch : getBranches) {
         if (branch.getId().equals(branchById.getId())) {
            Assert.assertTrue(branch.equals(branchById));
         }
      }
   }

   @Test
   public void setBranchName() {
      BranchId testBranch = branchEndpoint.createBranch(testDataInitialization());

      if (testBranch != null) {
         Assert.assertTrue(branchEndpoint.getBranchById(testBranch).getName().equals("TestBranch"));
         branchEndpoint.setBranchName(testBranch, "TestBranchNameChanged");
         Assert.assertTrue(branchEndpoint.getBranchById(testBranch).getName().equals("TestBranchNameChanged"));
         // put db back to original state by purging the branch
         branchEndpoint.purgeBranch(testBranch, false);
      } else {
         Assert.fail("Test Branch not created");
      }
   }

   @Test
   public void setBranchState() {
      BranchId testBranch = branchEndpoint.createBranch(testDataInitialization());

      if (testBranch != null) {
         Assert.assertTrue(branchEndpoint.getBranchById(testBranch).getBranchState().equals(BranchState.CREATED));
         branchEndpoint.setBranchState(testBranch, BranchState.MODIFIED);
         Assert.assertTrue(branchEndpoint.getBranchById(testBranch).getBranchState().equals(BranchState.MODIFIED));
         // put db back to original state by purging the branch
         branchEndpoint.purgeBranch(testBranch, false);
      } else {
         Assert.fail("Test Branch not created");
      }
   }

   @Test
   public void setBranchType() {
      BranchId testBranch = branchEndpoint.createBranch(testDataInitialization());

      if (testBranch != null) {
         Assert.assertTrue(branchEndpoint.getBranchById(testBranch).getBranchType().equals(BranchType.WORKING));
         branchEndpoint.setBranchType(testBranch, BranchType.BASELINE);
         Assert.assertTrue(branchEndpoint.getBranchById(testBranch).getBranchType().equals(BranchType.BASELINE));
         // put db back to original state by purging the branch
         branchEndpoint.purgeBranch(testBranch, false);
      } else {
         Assert.fail("Test Branch not created");
      }
   }

   @Test
   public void setAssociatedBranchToArtifact() {
      BranchId testBranch = branchEndpoint.createBranch(testDataInitialization());
      Artifact testArtifact = new Artifact(CoreBranches.COMMON, "TestArtifact");

      if (testBranch != null) {
         Assert.assertTrue(branchEndpoint.getBranchById(testBranch).getAssociatedArtifact().notEqual(testArtifact));
         branchEndpoint.associateBranchToArtifact(testBranch, testArtifact);
         Assert.assertTrue(branchEndpoint.getBranchById(testBranch).getAssociatedArtifact().equals(testArtifact));
         // put db back to original state by purging the branch
         branchEndpoint.purgeBranch(testBranch, false);
         testArtifact.delete();
      } else {
         Assert.fail("Test Branch not created");
      }
   }

   @Test
   public void createBranchValidation() {
      BranchId testBranch = branchEndpoint.createBranch(testDataInitialization());
      Assert.assertTrue(branchEndpoint.getBranchById(testBranch).isValid());
      XResultData xResult = branchEndpoint.createBranchValidation(testDataInitialization());

      Assert.assertFalse(xResult.isErrors());
      branchEndpoint.purgeBranch(testBranch, false);
   }

   @Test
   public void getArtifactDetailsByTypeWithView() {
      List<JsonArtifact> artifacts =
         branchEndpoint.getArtifactDetailsByType(DemoBranches.SAW_Bld_1, ArtifactId.SENTINEL, "23");
      Assert.assertFalse(artifacts.isEmpty());
   }

   @Test
   public void getArtifactDetailsByType() {
      List<JsonArtifact> artifacts = branchEndpoint.getArtifactDetailsByType(DemoBranches.SAW_Bld_1, "23");
      Assert.assertFalse(artifacts.isEmpty());
   }

   @Test
   public void getRelationsByType() {
      JsonRelations relations =
         branchEndpoint.getRelationsByType(CoreBranches.COMMON, CoreRelationTypes.SupportingInfo.getIdString());
      Assert.assertTrue(relations.getRelations().isEmpty());

      User joe = UserManager.getUser();
      Artifact folder = ArtifactQuery.getArtifactFromId(CoreArtifactTokens.OseeConfiguration, CoreBranches.COMMON);
      joe.addRelation(CoreRelationTypes.SupportingInfo_SupportingInfo, folder);
      joe.persist(getClass().getSimpleName());

      relations =
         branchEndpoint.getRelationsByType(CoreBranches.COMMON, CoreRelationTypes.SupportingInfo.getIdString());
      Assert.assertEquals(1, relations.getRelations().size());
      Assert.assertEquals(joe.getIdString(), relations.getRelations().iterator().next().getArtA());
      Assert.assertEquals(joe.getName(), relations.getRelations().iterator().next().getArtAName());
      Assert.assertEquals(folder.getIdString(), relations.getRelations().iterator().next().getArtB());
      Assert.assertEquals(folder.getName(), relations.getRelations().iterator().next().getArtBName());
   }
}