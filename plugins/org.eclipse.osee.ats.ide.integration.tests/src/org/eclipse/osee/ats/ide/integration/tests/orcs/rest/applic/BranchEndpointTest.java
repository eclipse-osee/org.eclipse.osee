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

package org.eclipse.osee.ats.ide.integration.tests.orcs.rest.applic;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import org.eclipse.osee.ats.ide.demo.DemoChoice;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchCategoryToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.JsonArtifact;
import org.eclipse.osee.framework.core.data.JsonRelation;
import org.eclipse.osee.framework.core.data.JsonRelations;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranchCategoryTokens;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.BranchCommitOptions;
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
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   private static BranchEndpoint branchEndpoint;
   private static JaxRsApi jaxRsApi;

   private static ArtifactEndpoint workingBranchArtifactEndpoint;

   @BeforeClass
   public static void testSetup() {
      branchEndpoint = ServiceUtil.getOseeClient().getBranchEndpoint();
      workingBranchArtifactEndpoint = ServiceUtil.getOseeClient().getArtifactEndpoint(DemoBranches.SAW_PL);
      jaxRsApi = ServiceUtil.getOseeClient().jaxRsApi();
   }

   private static NewBranch testDataInitialization(BranchToken branchToken) {
      NewBranch data = new NewBranch();
      data.setAssociatedArtifact(ArtifactId.SENTINEL);
      data.setBranchName("TestBranch");
      data.setBranchType(BranchType.WORKING);
      data.setCreationComment("For Test");
      data.setMergeAddressingQueryId(0L);
      data.setMergeDestinationBranchId(null);
      data.setParentBranchId(branchToken);
      data.setSourceTransactionId(TransactionManager.getHeadTransaction(branchToken));
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
      List<Branch> baselineBranches = branchEndpoint.getBranches("", "", "", false, false, "", "", null, null, null);
      List<BranchId> branchIds = new ArrayList<>();
      for (Branch branch : baselineBranches) {
         branchIds.add(BranchId.valueOf(branch.getId()));
      }
      List<BranchId> originalBranches = new ArrayList<>();
      //Selecting branches from CreateDemoBranches.java
      originalBranches.add(COMMON);
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
      List<Branch> catBranches =
         branchEndpoint.getBranches("", "", "", false, false, "", "", null, null, CoreBranchCategoryTokens.ATS);
      Assert.assertTrue(catBranches.size() >= 1);
   }

   @Test
   public void getBranchesByUUID() {
      List<BranchId> originalBranches = new ArrayList<>();
      //Selecting branches from CreateDemoBranches.java
      originalBranches.add(COMMON);
      originalBranches.add(DemoBranches.SAW_PL);

      String branchUuIds = String.format("%s,%s", COMMON.getIdString(), DemoBranches.SAW_PL.getIdString());
      List<Branch> baselineBranches =
         branchEndpoint.getBranches(branchUuIds, "", "", false, false, "", "", null, null, null);
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
         branchEndpoint.getBranches("", BranchType.WORKING.toString(), "", false, false, "", "", null, null, null);

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
      originalBranches.add(COMMON);
      originalBranches.add(DemoBranches.SAW_PL);

      List<Branch> baselineBranches =
         branchEndpoint.getBranches("", "BASELINE", "", false, false, "", "", null, null, null);
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
      originalBranches.add(COMMON);
      originalBranches.add(DemoBranches.SAW_PL);

      List<Branch> modifiedBranches =
         branchEndpoint.getBranches("", "", "MODIFIED", false, false, "", "", null, null, null);
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
      originalBranches.add(COMMON);
      originalBranches.add(DemoBranches.CIS_Bld_1);
      originalBranches.add(DemoBranches.SAW_Bld_1);
      originalBranches.add(DemoBranches.SAW_PL);

      List<Branch> deletedBranches = branchEndpoint.getBranches("", "", "", true, false, "", "", null, null, null);
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
      originalBranches.add(COMMON);
      originalBranches.add(DemoBranches.CIS_Bld_1);
      originalBranches.add(DemoBranches.SAW_Bld_1);
      originalBranches.add(DemoBranches.SAW_PL);

      List<Branch> archivedBranches = branchEndpoint.getBranches("", "", "", false, true, "", "", null, null, null);
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
      originalBranches.add(COMMON);

      List<Branch> commonBranches =
         branchEndpoint.getBranches("", "", "", false, false, "Common", "", null, null, null);
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
      List<Branch> baselineBranches =
         branchEndpoint.getBranches("", "", "", false, false, "", "SAW.*", null, null, null);
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
      if (baselineBranches.contains(COMMON)) {
         Assert.fail("Common branch should be filtered by the name selection, and wasn't");
      }
   }

   @Test
   public void getBranchesByChildOf() {
      BranchManager.createWorkingBranch(BranchId.valueOf(DemoBranches.SAW_PL.getId()), BranchType.WORKING.toString());
      List<Branch> childBranches =
         branchEndpoint.getBranches("", "", "", false, false, "", "", DemoBranches.SAW_PL.getId(), null, null);
      boolean isEmpty = true;
      if (!childBranches.isEmpty()) {
         isEmpty = false;
      }
      Assert.assertFalse(isEmpty);
   }

   @Test
   public void getBranchesByAncestorOf() {
      List<Branch> ancestorBranches =
         branchEndpoint.getBranches("", "", "", false, false, "", "", null, DemoBranches.SAW_PL.getId(), null);
      boolean isEmpty = true;
      if (!ancestorBranches.isEmpty()) {
         isEmpty = false;
      }
      Assert.assertFalse(isEmpty);
   }

   @Test
   public void getBranchesByCategory() {

      List<Branch> branches = branchEndpoint.getBranchesByCategory(CoreBranchCategoryTokens.PLE);
      boolean isEmpty = true;
      if (branches != null) {
         if (!branches.isEmpty()) {
            isEmpty = false;
         }
      }
      Assert.assertFalse(isEmpty);
   }

   @Test
   public void deleteBranchFromCategory() {
      XResultData deleteBranchCategory =
         branchEndpoint.deleteBranchCategory(DemoBranches.SAW_PL, CoreBranchCategoryTokens.PLE);
      Assert.assertFalse(deleteBranchCategory.isErrors());
      List<Branch> branches = branchEndpoint.getBranchesByCategory(CoreBranchCategoryTokens.PLE);
      boolean hasSAWPl = false;

      if (branches != null) {
         if (!branches.isEmpty() & branches.contains(DemoBranches.SAW_PL)) {
            hasSAWPl = true;
         }
      }
      Assert.assertFalse(hasSAWPl);
   }

   @Test
   public void setBranchFromCategory() {
      XResultData setBranchCategory =
         branchEndpoint.setBranchCategory(DemoBranches.SAW_Bld_1, CoreBranchCategoryTokens.ATS);
      Assert.assertFalse(setBranchCategory.isErrors());
      List<Branch> branches = branchEndpoint.getBranchesByCategory(CoreBranchCategoryTokens.ATS);

      if (branches != null) {
         Assert.assertTrue(branches.contains(DemoBranches.SAW_Bld_1));
      }
   }

   @Test
   public void getWorkingBranches() {
      List<Branch> workingBranchesList = branchEndpoint.getWorkingBranches();
      List<Branch> getBranches =
         branchEndpoint.getBranches("", BranchType.WORKING.toString(), "", false, false, "", "", null, null, null);

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
         branchEndpoint.getBranches("", BranchType.BASELINE.toString(), "", false, false, "", "", null, null, null);

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
      List<Branch> getBranches = branchEndpoint.getBranches("", "", "", false, false, "", "", null, null, null);

      Branch branchById = branchEndpoint.getBranchById(getBranches.get(0));
      for (Branch branch : getBranches) {
         if (branch.getId().equals(branchById.getId())) {
            Assert.assertTrue(branch.equals(branchById));
         }
      }
   }

   @Test
   public void setBranchName() {
      BranchId testBranch = branchEndpoint.createBranch(testDataInitialization(CoreBranches.SYSTEM_ROOT));

      if (testBranch != null) {
         Assert.assertTrue(branchEndpoint.getBranchById(testBranch).getName().equals("TestBranch"));

         Response res1 = branchEndpoint.setBranchName(testBranch, "TestBranchNameChanged");
         res1.close();

         Response res2 = branchEndpoint.purgeBranch(testBranch, false);
         res2.close();

      } else {
         Assert.fail("Test Branch not created");
      }

   }

   @Test
   public void setBranchState() {
      BranchId testBranch = branchEndpoint.createBranch(testDataInitialization(CoreBranches.SYSTEM_ROOT));

      if (testBranch != null) {
         Assert.assertTrue(branchEndpoint.getBranchById(testBranch).getBranchState().equals(BranchState.CREATED));

         Response res1 = branchEndpoint.setBranchState(testBranch, BranchState.MODIFIED);
         res1.close();

         Assert.assertTrue(branchEndpoint.getBranchById(testBranch).getBranchState().equals(BranchState.MODIFIED));

         // put db back to original state by purging the branch
         Response res2 = branchEndpoint.purgeBranch(testBranch, false);
         res2.close();
      } else {
         Assert.fail("Test Branch not created");
      }
   }

   @Test
   public void setBranchType() {
      BranchId testBranch = branchEndpoint.createBranch(testDataInitialization(CoreBranches.SYSTEM_ROOT));

      if (testBranch != null) {
         Assert.assertTrue(branchEndpoint.getBranchById(testBranch).getBranchType().equals(BranchType.WORKING));
         Response res1 = branchEndpoint.setBranchType(testBranch, BranchType.BASELINE);
         res1.close();

         Assert.assertTrue(branchEndpoint.getBranchById(testBranch).getBranchType().equals(BranchType.BASELINE));
         Response res2 = branchEndpoint.purgeBranch(testBranch, false);
         res2.close();
      } else {
         Assert.fail("Test Branch not created");
      }
   }

   @Test
   public void setAssociatedBranchToArtifact() {

      BranchId testBranch = branchEndpoint.createBranch(testDataInitialization(CoreBranches.SYSTEM_ROOT));
      Artifact testArtifact = new Artifact(CoreBranches.COMMON, "TestArtifact");

      if (testBranch != null) {
         Assert.assertTrue(branchEndpoint.getBranchById(testBranch).getAssociatedArtifact().notEqual(testArtifact));
         Response res = branchEndpoint.associateBranchToArtifact(testBranch, testArtifact);
         res.close();

         Assert.assertTrue(branchEndpoint.getBranchById(testBranch).getAssociatedArtifact().equals(testArtifact));
         // put db back to original state by purging the branch
         Response res2 = branchEndpoint.purgeBranch(testBranch, false);
         res2.close();
         testArtifact.delete();
      } else {
         Assert.fail("Test Branch not created");
      }
   }

   @Test
   public void createBranchValidation() {
      BranchId testBranch = branchEndpoint.createBranch(testDataInitialization(CoreBranches.SYSTEM_ROOT));
      Assert.assertTrue(branchEndpoint.getBranchById(testBranch).isValid());
      XResultData xResult = branchEndpoint.createBranchValidation(testDataInitialization(CoreBranches.SYSTEM_ROOT));

      Assert.assertFalse(xResult.isErrors());
      Response res = branchEndpoint.purgeBranch(testBranch, false);
      res.close();

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
      String relId = CoreRelationTypes.SupportingInfo.getIdString();
      JsonRelations relations = branchEndpoint.getRelationsByType(COMMON, relId);
      int numRels = relations.getRelations().size();

      UserToken joe = DemoUsers.Joe_Smith;
      ArtifactToken folder = CoreArtifactTokens.OseeConfiguration;
      String json = String.format(
         "{\"branch\": \"%s\", \"txComment\": \"BranchEndpointTest\", \"addRelations\": [{\"typeId\": \"%s\", \"aArtId\": \"%s\", \"bArtId\": \"%s\"}]}",
         COMMON.getIdString(), relId, joe.getIdString(), folder.getIdString());
      Response response = jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(json));
      assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());

      relations = branchEndpoint.getRelationsByType(COMMON, relId);
      List<JsonRelation> rels = relations.getRelations();
      Assert.assertEquals(numRels + 1, rels.size());
      JsonRelation foundRel = null;
      for (JsonRelation rel : rels) {
         if (rel.getArtA().equals(joe.getIdString())) {
            foundRel = rel;
            break;
         }
      }
      Assert.assertNotNull(foundRel);
      Assert.assertEquals(joe.getIdString(), foundRel.getArtA());
      Assert.assertEquals(joe.getName(), foundRel.getArtAName());
      Assert.assertEquals(folder.getIdString(), foundRel.getArtB());
      Assert.assertEquals(folder.getName(), foundRel.getArtBName());
   }

   @Test
   public void getOtherBranchesWithModifiedArtifacts() {
      //Create branches
      BranchId setUpBranchId = branchEndpoint.createBranch(testDataInitialization(DemoBranches.SAW_PL));
      //Create artifact
      OseeClient oseeclient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);
      workingBranchArtifactEndpoint = oseeclient.getArtifactEndpoint(setUpBranchId);

      ArtifactToken newArtifact = workingBranchArtifactEndpoint.createArtifact(setUpBranchId,
         CoreArtifactTypes.SoftwareDesignMsWord, CoreArtifactTokens.SoftwareRequirementsFolder, "SR A");

      BranchCommitOptions options = new BranchCommitOptions();
      options.setArchive(false);
      options.setCommitter(UserManager.getUser());

      //Check Modified State
      try (Response res1 = branchEndpoint.setBranchName(setUpBranchId, "setUpBranch")) {
         BranchState setUpBranchStateBefore = BranchManager.getState(setUpBranchId);
         if (!setUpBranchStateBefore.isModified()) {
            throw new OseeCoreException("unmodified branch");
         }
      }

      //Commit branch
      TransactionResult transactionResult = branchEndpoint.commitBranch(setUpBranchId, DemoBranches.SAW_PL, options);
      if (transactionResult.isFailed()) {
         throw new OseeCoreException(transactionResult.toString());
      }

      BranchId testBranchIdOne = branchEndpoint.createBranch(testDataInitialization(DemoBranches.SAW_PL));
      workingBranchArtifactEndpoint = oseeclient.getArtifactEndpoint(testBranchIdOne);

      try (Response res2 = branchEndpoint.setBranchName(testBranchIdOne, "testBranchIdOne")) {

         // check to see if the original committed branch shows up as a branch with a modified artifact  - should be no
         Collection<BranchId> branches =
            branchEndpoint.getOtherBranchesWithModifiedArtifacts(testBranchIdOne, ArtifactId.create(newArtifact));
         Assert.assertTrue(branches.isEmpty());

         // modify the artifact on the current branch, then check to see if another branch shows up as a branch with a modified artifact
         BranchId testBranchIdTwo = branchEndpoint.createBranch(testDataInitialization(DemoBranches.SAW_PL));
         workingBranchArtifactEndpoint = oseeclient.getArtifactEndpoint(testBranchIdTwo);

         try (Response res3 = branchEndpoint.setBranchName(testBranchIdTwo, "testBranchIdTwo")) {
            workingBranchArtifactEndpoint.deleteArtifact(testBranchIdTwo, newArtifact);

            branches =
               branchEndpoint.getOtherBranchesWithModifiedArtifacts(testBranchIdOne, ArtifactId.create(newArtifact));
            // since testBranchIdtwo has deleted the artifact, we expect to see it as a branch with a modified artifact
            Assert.assertFalse(branches.isEmpty());
         }

         Collection<BranchId> branchesModded =
            branchEndpoint.getOtherBranchesWithModifiedArtifacts(testBranchIdTwo, ArtifactId.create(newArtifact));
         // since the only change to the artifact is on branchIdTwo (given) we don't expect to see it as an other modified branch
         Assert.assertTrue(branchesModded.isEmpty());
         try (Response res = branchEndpoint.purgeBranch(setUpBranchId, false)) {
         }
         try (Response res = branchEndpoint.purgeBranch(testBranchIdOne, false)) {
         }
         try (Response res = branchEndpoint.purgeBranch(testBranchIdTwo, false)) {
         }
      }

   }

   @Test
   public void testForBranchCategories() {
      List<BranchCategoryToken> branchCategories =
         branchEndpoint.getBranchCategories(DemoBranches.SAW_PL_Working_Branch);
      Assert.assertTrue(branchCategories.contains(CoreBranchCategoryTokens.ATS));
   }

   public void testBranchCategoryAndTypeList() {
      List<BranchId> originalBranches = new ArrayList<>();
      originalBranches.add(DemoBranches.SAW_PL);
      List<Branch> foundBranches =
         branchEndpoint.getBranchesByCategoryAndType("baseline", BranchCategoryToken.valueOf((long) 2));
      List<BranchId> foundIds =
         foundBranches.stream().map(a -> BranchId.valueOf(a.getId())).collect(Collectors.toList());
      Assert.assertFalse(foundBranches.isEmpty());
      Assert.assertFalse(foundIds.isEmpty());
      boolean allBranchesContained = true;
      for (BranchId branch : originalBranches) {
         if (!foundIds.contains(branch)) {
            allBranchesContained = false;
         }
      }
      Assert.assertTrue(allBranchesContained);
   }
}