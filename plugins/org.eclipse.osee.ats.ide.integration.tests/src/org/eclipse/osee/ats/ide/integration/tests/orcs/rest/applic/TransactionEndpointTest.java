/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.Asserts;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.PurgeTransactionOperation;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Branden W. Phillips
 */
public class TransactionEndpointTest {

   private static TransactionEndpoint transactionEndpoint;
   private static ApplicabilityEndpoint applicEndpoint;
   private static JaxRsApi jaxRsApi;

   @BeforeClass
   public static void testSetup() {
      transactionEndpoint = ServiceUtil.getOseeClient().getTransactionEndpoint();
      applicEndpoint = ServiceUtil.getOseeClient().getApplicabilityEndpoint(DemoBranches.SAW_PL_Working_Branch);
      jaxRsApi = ServiceUtil.getOseeClient().jaxRsApi();
   }

   /**
    * Simple test for the getArtifactHistory rest call. Checking for the following cases <br/>
    * 1. Artifact changes are being found <br/>
    * 2. Attribute changes are being found <br/>
    * 3. Relation changes are being found <br/>
    * 4. Changes are coming from multiple branches since the query is following recursive branches. We know that the
    * Demo PL branch has parents for this artifact. With this, we want to be sure that multiple branches have been found
    * on the list of changes by checking that the set size is greater than 1 at the end of processing.
    */
   @Test
   public void testGetArtifactHistory() {
      ArtifactId productDecomp =
         ArtifactQuery.getArtifactFromId(CoreArtifactTokens.ProductDecomposition, DemoBranches.SAW_PL);

      List<ChangeItem> changeItems = transactionEndpoint.getArtifactHistory(productDecomp, DemoBranches.SAW_PL);

      assertFalse(changeItems.isEmpty());

      boolean hasArtifactChanges = false, hasAttributeChanges = false, hasRelationChanges = false;
      Set<BranchId> branches = new HashSet<>();

      for (ChangeItem change : changeItems) {
         ChangeType changeType = change.getChangeType();
         if (changeType.isArtifactChange()) {
            hasArtifactChanges = true;
         } else if (changeType.isAttributeChange()) {
            hasAttributeChanges = true;
         } else if (changeType.isRelationChange()) {
            hasRelationChanges = true;
         }
         BranchId branch = change.getCurrentVersion().getTransactionToken().getBranch();
         if (!branches.contains(branch)) {
            branches.add(branch);
         }
      }

      assertTrue(hasArtifactChanges);
      assertTrue(hasAttributeChanges);
      assertTrue(hasRelationChanges);
      assertTrue(branches.size() > 1);
   }

   @Test
   public void testCreateTransaction() {
      // test transactionEndpoint.create(tx);
      String json = OseeInf.getResourceContents("create_tx.json", getClass());
      Response response = jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(json));
      assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());
   }

   @Test
   public void testAddDateAttribute() {
      Date currentDate = new Date();
      String json = setupTransferJson(CoreArtifactTypes.GitCommit,
         AttributeTypeId.valueOf(CoreAttributeTypes.GitCommitAuthorDate.getId()), currentDate);
      testWrapUp(json, CoreAttributeTypes.GitCommitAuthorDate, currentDate);
   }

   @Test
   public void testModifyDateAttribute() {
      Date currentDate = new Date();
      Long time = currentDate.getTime();
      time += 10000;
      Date nextDate = new Date(time);
      List<String> jsons = setupTransferModificationJsons(CoreArtifactTypes.GitCommit,
         AttributeTypeId.valueOf(CoreAttributeTypes.GitCommitAuthorDate.getId()), currentDate, nextDate);

      List<TransactionToken> txs = new ArrayList<>();
      // simpler to get the art_id that is changing when we create it -
      JsonNode readTree = jaxRsApi.readTree(jsons.get(0));
      ArtifactId artId = ArtifactId.valueOf(readTree.get("createArtifacts").get(0).get("id").asLong());
      txs.add(testWrapUpFromDate(jsons.get(0), CoreAttributeTypes.GitCommitAuthorDate, currentDate, artId));
      txs.add(testWrapUpFromDate(jsons.get(1), CoreAttributeTypes.GitCommitAuthorDate, nextDate, artId));
      for (TransactionToken tx : txs) {
         try {
            purge(tx);
            purgeArts(Arrays.asList(artId.getId()));
         } catch (Exception ex) {
            throw new OseeCoreException("couldn't clean up in testModifyDateAttribute");
         }
      }

   }

   @Test
   public void testArtifactForCorrectJson() {
      String uniqueString = "22222222";
      String deleteAndAddJson = setupTransferJsonFromDeleteAddAttribute(CoreArtifactTypes.Artifact,
         CoreAttributeTypes.Description, uniqueString);
      assertTrue(deleteAndAddJson.contains("deleteAttributes"));
      assertTrue(deleteAndAddJson.contains("addAttributes"));
      assertTrue(deleteAndAddJson.contains(uniqueString));
   }

   @Test
   public void testAddBooleanAttribute() {
      boolean testBoolean = true;
      String json = setupTransferJson(CoreArtifactTypes.OseeTypeDefinition,
         AttributeTypeId.valueOf(CoreAttributeTypes.Active.getId()), testBoolean);
      testWrapUp(json, CoreAttributeTypes.Active, testBoolean);
   }

   @Test
   public void testAddStringAttribute() {
      String testString = "Test Add String Attribute";
      String json = setupTransferJson(CoreArtifactTypes.Artifact,
         AttributeTypeId.valueOf(CoreAttributeTypes.Annotation.getId()), testString);
      testWrapUp(json, CoreAttributeTypes.Annotation, testString);
   }

   @Test
   public void testAddMultiStringsAttribute() {
      List<String> values = new ArrayList<>();
      values.add("Test Add String Attribute1");
      values.add("Test Add String Attribute2");
      values.add("Test Add String Attribute3");
      String json = setupTransferJsonFromStrings(CoreArtifactTypes.Artifact,
         AttributeTypeId.valueOf(CoreAttributeTypes.Annotation.getId()), values);
      testWrapUpFromStrings(json, CoreAttributeTypes.Annotation, values);
   }

   @Test
   public void testAddIntegerAttribute() {
      int testInt = 10;
      String json = setupTransferJson(CoreArtifactTypes.Breaker,
         AttributeTypeId.valueOf(CoreAttributeTypes.CircuitBreakerId.getId()), testInt);
      testWrapUp(json, CoreAttributeTypes.CircuitBreakerId, testInt);
   }

   @Test
   public void testAddArtifactIdAttribute() {
      ArtifactId testArtId = ArtifactId.valueOf(1234321L);
      String json = setupTransferJson(CoreArtifactTypes.CertificationBaselineEvent,
         AttributeTypeId.valueOf(CoreAttributeTypes.BaselinedBy.getId()), ArtifactId.valueOf(testArtId));
      testWrapUp(json, CoreAttributeTypes.BaselinedBy, testArtId);
   }

   //Still need to add ability to handle binary data
   /**
    * @Test public void testAddBinaryDataAttribute() { String json; try { InputStream testBinaryData =
    * Lib.stringToInputStream("Test Binary Data Attribute"); json = setupTransferJson(CoreArtifactTypes.ImageArtifact,
    * AttributeTypeId.valueOf(CoreAttributeTypes.ImageContent.getId()), testBinaryData); } catch
    * (UnsupportedEncodingException ex) { throw new OseeCoreException("Failed to add attribute during
    * testAddBinaryDataAttribute"); } Response response =
    * jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(json));
    * assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily()); }
    */

   @Test
   public void testApplicabilityChange() {
      //Create a transaction and add a artifact with a changed applicabilityId
      SkynetTransaction transaction = TransactionManager.createTransaction(DemoBranches.SAW_PL_Working_Branch,
         TransactionEndpointTest.class.getName() + ": Create Applicability Change");
      ApplicabilityToken applic =
         applicEndpoint.getApplicabilityTokens().stream().findFirst().orElse(ApplicabilityToken.SENTINEL);
      Artifact artifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Requirement,
         DemoBranches.SAW_PL_Working_Branch, "Artifact for Applicability Change");
      artifact.internalSetApplicablityId(ApplicabilityId.valueOf(applic.getId()));
      //Test to ensure applicabilityId was actually changed successfully
      assertTrue(artifact.getApplicablityId().equals(ApplicabilityId.valueOf(applic.getId())));

      //Complete transfer of transaction to a new branch
      transaction.addArtifact(artifact);
      TransactionToken currentTx = transaction.execute();
      TransactionToken startingTx = TransactionManager.getPriorTransaction(currentTx);
      TransactionBuilderData txData = transactionEndpoint.exportTxsDiff(startingTx, currentTx);
      txData.setBranch(DemoBranches.SAW_PL_Hardening_Branch.getIdString());
      ObjectMapper mapper = new ObjectMapper();
      String json;
      try {
         json = mapper.writeValueAsString(txData);
      } catch (JsonProcessingException ex) {
         throw new OseeCoreException("Failed to write txData as json in TransactionEndpointTest");
      }

      purgeArts(Arrays.asList(artifact.getId()));

      Response response = jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(json));
      assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());
      try {
         //Pull all the important bits out of the readTree
         JsonNode readTree = jaxRsApi.readTree(Lib.inputStreamToString((InputStream) response.getEntity()));
         ArtifactId artId = ArtifactId.valueOf(readTree.get("results").get("ids").get(0).asLong());
         BranchId branchId = BranchId.valueOf(readTree.get("tx").get("branchId").asLong());
         TransactionId txId = TransactionId.valueOf(readTree.get("tx").get("id").asLong());
         Artifact transferArt = ArtifactQuery.getArtifactFromId(artId, branchId);
         //Ensure the applicabilityId was transferred correctly
         assertEquals(artifact.getApplicablityId(), transferArt.getApplicablityId());
         //Cleanup and purge the transactions
         purge(currentTx);
         purge(TransactionToken.valueOf(txId, branchId));
         purgeArts(Arrays.asList(artId.getId()));
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to convert response to String");
      }
   }

   @Test
   public void testApplicabilityDeletion() {
      //Create a transaction and add a artifact with a changed applicabilityId
      SkynetTransaction transaction = TransactionManager.createTransaction(DemoBranches.SAW_PL_Working_Branch,
         TransactionEndpointTest.class.getName() + ": Create Applicability Change");
      ApplicabilityToken applic =
         applicEndpoint.getApplicabilityTokens().stream().findFirst().orElse(ApplicabilityToken.SENTINEL);
      Artifact sampleArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Requirement,
         DemoBranches.SAW_PL_Working_Branch, "Artifact for Applicability Change");
      sampleArtifact.internalSetApplicablityId(ApplicabilityId.valueOf(applic.getId()));
      transaction.addArtifact(sampleArtifact);
      //Test to ensure applicabilityId was actually changed successfully
      assertTrue(sampleArtifact.getApplicablityId().equals(ApplicabilityId.valueOf(applic.getId())));

      //Complete transfer of transacartifacttion to a new branch
      ArtifactId testArtId = ArtifactId.valueOf(88888889L);

      TransactionToken createTx = transaction.execute();
      TransactionToken priorTxToken = TransactionManager.getPriorTransaction(createTx);
      TransactionBuilderData txDataAddApplic = transactionEndpoint.exportTxsDiff(priorTxToken, createTx);
      // Change the branch so it mimics porting from one db to another
      txDataAddApplic.setBranch(DemoBranches.SAW_PL_Hardening_Branch.getIdString());
      // Change the art_id so we don't duplicate art_ids
      txDataAddApplic.getCreateArtifacts().iterator().next().setId(testArtId.getIdString());

      ObjectMapper mapper = new ObjectMapper();
      String json;
      try {
         json = mapper.writeValueAsString(txDataAddApplic);
      } catch (JsonProcessingException ex) {
         throw new OseeCoreException("Failed to write txData as json in TransactionEndpointTest");
      }

      Response response = jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(json));
      assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());

      //Ensure Artifact is created on SAW_PL_Hardening_Branch with the correct ApplicabilityId
      Artifact testArt = ArtifactQuery.getArtifactFromId(testArtId, DemoBranches.SAW_PL_Hardening_Branch);
      assertTrue(testArt.getApplicablityId().equals(
         ArtifactQuery.getArtifactFromId(testArt, DemoBranches.SAW_PL_Hardening_Branch).getApplicablityId()));

      //Delete the applicability on the SAW_PL_Working_Branch and transfer it to SAW_PL_Hardening_Branch
      SkynetTransaction deleteTransaction = TransactionManager.createTransaction(DemoBranches.SAW_PL_Working_Branch,
         TransactionEndpointTest.class.getName() + ": Set Applicability to Base");
      deleteTransaction.addArtifact(sampleArtifact);
      sampleArtifact.internalSetApplicablityId(ApplicabilityId.BASE);
      sampleArtifact.setName("Changed ApplicabilityId and Name");
      sampleArtifact.persist(deleteTransaction);
      TransactionToken deleteTx = deleteTransaction.execute();
      priorTxToken = TransactionManager.getPriorTransaction(deleteTx);

      TransactionBuilderData txDataDeleteApplic = transactionEndpoint.exportTxsDiff(priorTxToken, deleteTx);
      // Change to SAW_PL_Hardening_Branch
      txDataDeleteApplic.setBranch(DemoBranches.SAW_PL_Hardening_Branch.getIdString());
      // Change the Test art_id that we used above
      txDataDeleteApplic.getModifyArtifacts().iterator().next().setId(testArtId.getIdString());

      ObjectMapper mapperDeleteArt = new ObjectMapper();
      String deleteJson;
      try {
         deleteJson = mapperDeleteArt.writeValueAsString(txDataDeleteApplic);
      } catch (JsonProcessingException ex) {
         throw new OseeCoreException("Failed to write txData as json for deleting a applicability");
      }

      purgeArts(Arrays.asList(sampleArtifact.getId()));

      Response responseDeleteArt =
         jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(deleteJson));
      assertEquals(Family.SUCCESSFUL, responseDeleteArt.getStatusInfo().getFamily());
      //Need to decache the branch because it was previously getting old unchanged artifact from the cache
      ArtifactCache.deCache(DemoBranches.SAW_PL_Hardening_Branch);

      //Ensure Artifact is changed on SAW_PL_Hardening_Branch with the Base ApplicabilityId
      assertTrue(ApplicabilityId.BASE.equals(
         ArtifactQuery.getArtifactFromId(testArt, DemoBranches.SAW_PL_Hardening_Branch).getApplicablityId()));

      //Test Cleanup
      try {
         purge(createTx);
         purge(deleteTx);
         purgeArts(Arrays.asList(sampleArtifact.getId()));
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to purge Transaction in testArtifactDeletionTransfer");
      }
   }

   @Test
   public void testArtifactDeletionTransfer() {

      // Create an artifact on SAW_PL_Working_Branch to get a valid TransactionBuilderData for add
      SkynetTransaction addArtTransaction = TransactionManager.createTransaction(DemoBranches.SAW_PL_Working_Branch,
         TransactionEndpointTest.class.getName() + ": Create Artifact for Deletion");
      Artifact sampleArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Requirement,
         DemoBranches.SAW_PL_Working_Branch, "Artifact for DeletionTestTransfer");
      addArtTransaction.addArtifact(sampleArtifact);
      TransactionToken addArtTx = addArtTransaction.execute();

      TransactionToken priorTxToken = TransactionManager.getPriorTransaction(addArtTx);

      ArtifactId testArtId = ArtifactId.valueOf(88888888L);
      TransactionBuilderData txDataAdd = transactionEndpoint.exportTxsDiff(priorTxToken, addArtTx);
      // Change the branch so it mimics porting from one db to another
      txDataAdd.setBranch(DemoBranches.SAW_PL_Hardening_Branch.getIdString());
      // Change the art_id so we don't duplicate art_ids
      txDataAdd.getCreateArtifacts().iterator().next().setId(testArtId.getIdString());
      ObjectMapper mapperAddArt = new ObjectMapper();
      String addArtJson;
      try {
         addArtJson = mapperAddArt.writeValueAsString(txDataAdd);
      } catch (JsonProcessingException ex) {
         throw new OseeCoreException("Failed to write txData as json in for creating a artifact");
      }

      Response responseArtAdd =
         jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(addArtJson));
      assertEquals(Family.SUCCESSFUL, responseArtAdd.getStatusInfo().getFamily());

      // Ensure Test Artifact is created on SAW_PL_Hardening_Branch
      Artifact testArt = ArtifactQuery.getArtifactFromId(testArtId, DemoBranches.SAW_PL_Hardening_Branch);
      assertTrue(testArt.getId().equals(
         ArtifactQuery.getArtifactFromId(testArt, DemoBranches.SAW_PL_Hardening_Branch).getId()));

      // Delete an artifact on SAW_PL_Working_Branch to get a valid TransactionBuilderData for add
      SkynetTransaction deleteArtTransaction = TransactionManager.createTransaction(DemoBranches.SAW_PL_Working_Branch,
         TransactionEndpointTest.class.getName() + ": Delete Artifact Transaction");
      sampleArtifact.deleteAndPersist(deleteArtTransaction);
      TransactionToken deleteArtTx = deleteArtTransaction.execute();
      priorTxToken = TransactionManager.getPriorTransaction(deleteArtTx);
      TransactionBuilderData txDataDelete = transactionEndpoint.exportTxsDiff(priorTxToken, deleteArtTx);
      // Change to SAW_PL_Hardening_Branch
      txDataDelete.setBranch(DemoBranches.SAW_PL_Hardening_Branch.getIdString());
      // Change the Test art_id that we used above
      txDataDelete.getDeleteArtifacts().clear();
      txDataDelete.getDeleteArtifacts().add(testArtId.getId());
      ObjectMapper mapperDeleteArt = new ObjectMapper();
      String deleteArtJson;
      try {
         deleteArtJson = mapperDeleteArt.writeValueAsString(txDataDelete);
      } catch (JsonProcessingException ex) {
         throw new OseeCoreException("Failed to write txData as json for deleting a artifact");
      }

      Response responseArtDelete =
         jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(deleteArtJson));
      assertEquals(Family.SUCCESSFUL, responseArtDelete.getStatusInfo().getFamily());

      //Need to decache the branch because it was previously getting the undeleted artifact from the cache
      ArtifactCache.deCache(DemoBranches.SAW_PL_Hardening_Branch);

      //Ensure Artifact is deleted on SAW_PL_Hardening_Branch
      assertTrue(ArtifactQuery.getArtifactFromId(testArt, DemoBranches.SAW_PL_Hardening_Branch,
         DeletionFlag.allowDeleted(true)).isDeleted());

      //Test Cleanup
      try {
         purgeArts(Arrays.asList(testArt.getId()));
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to purge Transaction in testArtifactDeletionTransfer");
      }
   }

   @Test
   public void testTransferLocking() {
      TransactionId export1 = TransactionId.valueOf(98724); // just meant to be unique
      TransactionId export2 = TransactionId.valueOf(2356347);
      XResultData results = transactionEndpoint.lock(export1);
      assertTrue(results.isSuccess());
      results = transactionEndpoint.unlock(export1);
      assertTrue(results.isSuccess());
      assertTrue(checkResultsString(results, (String.format("Export ID unlocked: %s", export1.toString()))));
      results = transactionEndpoint.lock(export1);
      assertTrue(results.isSuccess());
      results = transactionEndpoint.lock(export2);
      assertTrue(results.isSuccess());
      results = transactionEndpoint.unlock(export1);
      assertTrue(results.isSuccess());
      assertTrue(checkResultsString(results, (String.format("Export ID unlocked: %s", export1.toString()))));
      results = transactionEndpoint.unlock(export2);
      assertTrue(results.isSuccess());
      assertTrue(checkResultsString(results, (String.format("Export ID unlocked: %s", export2.toString()))));
      // check is locked
      results = transactionEndpoint.isLocked(export1);
      assertTrue(results.isSuccess());
      assertTrue(checkResultsString(results, (String.format("%s not locked", export1.toString()))));
      results = transactionEndpoint.lock(export1);
      results = transactionEndpoint.isLocked(export1);
      assertTrue(results.isSuccess());
      assertTrue(checkResultsString(results, (String.format("Export ID locked: %s", export1.toString()))));

      // lock while locked
      results = transactionEndpoint.lock(export1);
      assertFalse(results.isSuccess());
      assertTrue(checkResultsString(results, (String.format("%s already locked", export1.toString()))));

      // unlock while unlocked
      results = transactionEndpoint.unlock(export2);
      assertFalse(results.isSuccess());
      assertTrue(checkResultsString(results, (String.format("%s already not locked", export2.toString()))));

   }

   private boolean checkResultsString(XResultData results, String toCheck) {
      for (String entry : results.getResults()) {
         if (entry.contains(toCheck)) {
            return true;
         }
      }
      return false;
   }

   /**
    * Helper method that will create a new Artifact with a single added attribute and return the Transfer Json created
    * from the exportTxsDiff
    */
   private String setupTransferJson(ArtifactTypeToken artType, AttributeTypeId attrType, Object value) {
      //Create a new Artifact of type artType on SAWL_PL_Working_Branch
      SkynetTransaction transaction = TransactionManager.createTransaction(DemoBranches.SAW_PL_Working_Branch,
         TransactionEndpointTest.class.getName() + attrType.getIdString());
      Artifact artifact = ArtifactTypeManager.addArtifact(artType, DemoBranches.SAW_PL_Working_Branch);
      //Check if the artifact already has an attribute of the supplied type, then create a new Attribute on the Artifact of type attrType with supplied value
      if (!artifact.getAttributes(attrType).isEmpty()) {
         artifact.deleteAttributes(attrType);
      }
      artifact.addAttribute(attrType, value);
      //Commit transaction and return the newly created TransactionToken
      transaction.addArtifact(artifact);
      TransactionToken currentTx = transaction.execute();
      TransactionToken startingTx = TransactionManager.getPriorTransaction(currentTx);
      TransactionBuilderData txData = transactionEndpoint.exportTxsDiff(startingTx, currentTx);
      //Switch branches in TransactionBuilderData so the transfer will be onto a different branch
      txData.setBranch(DemoBranches.SAW_PL_Hardening_Branch.getIdString());
      ObjectMapper mapper = new ObjectMapper();
      String json;
      try {
         json = mapper.writeValueAsString(txData);
      } catch (JsonProcessingException ex) {
         throw new OseeCoreException("Failed to write txData as json in TransactionEndpointTest");
      }
      try {
         purge(currentTx);
         purgeArts(Arrays.asList(artifact.getId()));
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to purge Transaction:" + currentTx.getIdString());
      }
      return json;
   }

   /**
    * Helper method that will create a new Artifact with a single added attribute and return two different Transfer Json
    * created from the exportTxsDiff from the original value and the next value
    */
   private List<String> setupTransferModificationJsons(ArtifactTypeToken artType, AttributeTypeId attrType,
      Object value, Object later) {
      //Create a new Artifact of type artType on SAWL_PL_Working_Branch
      SkynetTransaction transaction = TransactionManager.createTransaction(DemoBranches.SAW_PL_Working_Branch,
         TransactionEndpointTest.class.getName() + attrType.getIdString());
      Artifact artifact = ArtifactTypeManager.addArtifact(artType, DemoBranches.SAW_PL_Working_Branch);
      //Check if the artifact already has an attribute of the supplied type, then create a new Attribute on the Artifact of type attrType with supplied value
      if (!artifact.getAttributes(attrType).isEmpty()) {
         artifact.deleteAttributes(attrType);
      }
      artifact.addAttribute(attrType, value);
      //Commit transaction and return the newly created TransactionToken
      transaction.addArtifact(artifact);
      TransactionToken originalTx = transaction.execute();

      SkynetTransaction transactionMod = TransactionManager.createTransaction(DemoBranches.SAW_PL_Working_Branch,
         TransactionEndpointTest.class.getName() + attrType.getIdString());

      //Check if the artifact already has an attribute of the supplied type, then create a new Attribute on the Artifact of type attrType with supplied value
      if (!artifact.getAttributes(attrType).isEmpty()) {
         artifact.setSoleAttributeValue(attrType, later);
         artifact.persist(transactionMod);
      } else {
         throw new OseeCoreException("artifact expected to already have a value");
      }

      //Commit transaction and return the newly created TransactionToken
      TransactionToken currentTx = transactionMod.execute();
      TransactionToken startingTx = TransactionManager.getPriorTransaction(originalTx);
      TransactionBuilderData txData = transactionEndpoint.exportTxsDiff(startingTx, originalTx);
      TransactionBuilderData txDataMod = transactionEndpoint.exportTxsDiff(originalTx, currentTx);
      //Switch branches in TransactionBuilderData so the transfer will be onto a different branch
      txData.setBranch(DemoBranches.SAW_PL_Hardening_Branch.getIdString());
      txDataMod.setBranch(DemoBranches.SAW_PL_Hardening_Branch.getIdString());
      ObjectMapper mapper = new ObjectMapper();
      List<String> jsons = new ArrayList<>();
      String json;
      try {
         json = mapper.writeValueAsString(txData);
         jsons.add(json);
         json = mapper.writeValueAsString(txDataMod);
         jsons.add(json);
      } catch (JsonProcessingException ex) {
         throw new OseeCoreException("Failed to write txData as json in TransactionEndpointTest");
      }
      try {
         purge(currentTx);
         purge(originalTx);
         purgeArts(Arrays.asList(artifact.getId()));
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to purge Transaction:" + currentTx.getIdString());
      }
      return jsons;
   }

   /**
    * Cleanup osee_artifact so there is no constraint error when import back in. This can be replaced by
    * purgeTxsAndBackingData when available
    */
   private void purgeArts(Collection<Long> artIds) {
      AtsApi atsApi = AtsApiService.get();
      for (Long artId : artIds) {
         atsApi.getJdbcService().getClient().runPreparedUpdate("delete from osee_artifact where art_id = ?", artId);
      }
   }

   /**
    * Helper method that will create a new Artifact with a single added attribute with multiple Strings and return the
    * Transfer Json created from the exportTxsDiff
    */
   private String setupTransferJsonFromStrings(ArtifactTypeToken artType, AttributeTypeId attrType,
      List<String> values) {
      //Create a new Artifact of type artType on SAWL_PL_Working_Branch
      SkynetTransaction transaction = TransactionManager.createTransaction(DemoBranches.SAW_PL_Working_Branch,
         TransactionEndpointTest.class.getName() + attrType.getIdString());
      Artifact artifact = ArtifactTypeManager.addArtifact(artType, DemoBranches.SAW_PL_Working_Branch);
      //Check if the artifact already has an attribute of the supplied type, then create a new Attribute on the Artifact of type attrType with supplied value
      if (!artifact.getAttributes(attrType).isEmpty()) {
         artifact.deleteAttributes(attrType);
      }

      artifact.setAttributeFromValues(attrType, values);

      //Commit transaction and return the newly created TransactionToken
      transaction.addArtifact(artifact);
      TransactionToken currentTx = transaction.execute();
      TransactionToken startingTx = TransactionManager.getPriorTransaction(currentTx);
      TransactionBuilderData txData = transactionEndpoint.exportTxsDiff(startingTx, currentTx);
      //Switch branches in TransactionBuilderData so the transfer will be onto a different branch
      txData.setBranch(DemoBranches.SAW_PL_Hardening_Branch.getIdString());
      ObjectMapper mapper = new ObjectMapper();
      String json;
      try {
         json = mapper.writeValueAsString(txData);
      } catch (JsonProcessingException ex) {
         throw new OseeCoreException("Failed to write txData as json in TransactionEndpointTest");
      }
      try {
         purge(currentTx);
         purgeArts(Arrays.asList(artifact.getId()));
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to purge Transaction:" + currentTx.getIdString());
      }
      return json;
   }

   /**
    * Create a test that first creates an Artifact in a known folder. Add an description to that artifact. Commit the
    * changes and keep the transaction ID. Create a new transaction that deletes the description from the artifact
    * created in the previous transactino. Add another description to the same artifact. Commit the transaction. Get the
    * json from exportTxsDiff for the description deletion then addition. Test the json to make sure it has both the
    * deletion and the addition of the description attribute. Consider purging the transaction that did the delete and
    * re-addition, then using the api to apply the json.
    */
   private String setupTransferJsonFromDeleteAddAttribute(ArtifactTypeToken artType, AttributeTypeId attrType,
      String uniqueString) {
      //Create a new Artifact of type artType on SAWL_PL_Working_Branch
      SkynetTransaction transaction = TransactionManager.createTransaction(DemoBranches.SAW_PL_Working_Branch,
         TransactionEndpointTest.class.getName() + attrType.getIdString());
      Artifact artifact = ArtifactTypeManager.addArtifact(artType, DemoBranches.SAW_PL_Working_Branch);
      artifact.addAttributeFromString(CoreAttributeTypes.Description, "UniqueID 11111111");
      //Commit transaction and return the newly created TransactionToken
      transaction.addArtifact(artifact);
      TransactionToken setupTx = transaction.execute();

      //Delete then add string attribute
      SkynetTransaction deleteAndAdd = TransactionManager.createTransaction(DemoBranches.SAW_PL_Working_Branch,
         TransactionEndpointTest.class.getName() + attrType.getIdString());

      artifact.deleteAttributes(CoreAttributeTypes.Description);
      artifact.addAttributeFromString(CoreAttributeTypes.Description, uniqueString);
      deleteAndAdd.addArtifact(artifact);
      TransactionToken deleteAndAddTx = deleteAndAdd.execute();

      TransactionBuilderData deleteAndAddData = transactionEndpoint.exportTxsDiff(setupTx, deleteAndAddTx);
      ObjectMapper mapper = new ObjectMapper();
      String deleteAndAddJson;
      try {
         deleteAndAddJson = mapper.writeValueAsString(deleteAndAddData);
      } catch (JsonProcessingException ex) {
         throw new OseeCoreException("Failed to write txData as json in setupTransferJsonFromDeleteAddAttribute");
      }

      try {
         purge(deleteAndAddTx);
         purge(setupTx);
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to purge Transaction:" + deleteAndAddTx.getIdString());
      }

      return deleteAndAddJson;
   }

   /**
    * Helper method that tests to ensure the new attribute is on the artifact from the created transaction and then
    * cleans up the newly added transaction
    */
   private void testWrapUp(String json, AttributeTypeToken attrType, Object expectedValue) {
      Response response = jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(json));
      assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());
      try {
         //Pull all the important bits out of the readTree
         JsonNode readTree = jaxRsApi.readTree(Lib.inputStreamToString((InputStream) response.getEntity()));
         ArtifactId artId = ArtifactId.valueOf(readTree.get("results").get("ids").get(0).asLong());
         BranchId branchId = BranchId.valueOf(readTree.get("tx").get("branchId").asLong());
         TransactionId txId = TransactionId.valueOf(readTree.get("tx").get("id").asLong());
         //Find the artifact with the new Attribute and ensure the expectedValue is on the artifact
         Artifact art = ArtifactQuery.getArtifactFromId(artId, branchId);
         List<Attribute<Object>> attributes = art.getAttributesByValue(attrType, expectedValue);
         assertTrue(!attributes.isEmpty());
         //Cleanup and purge the Transaction on the new branch
         purge(TransactionToken.valueOf(txId, branchId));
         purgeArts(Arrays.asList(artId.getId()));
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to convert response to String");
      }
   }

   /**
    * Helper method that tests to ensure the new attributes are on the artifact from the created transaction and then
    * cleans up the newly added transaction
    */
   private void testWrapUpFromStrings(String json, AttributeTypeToken attrType, List<String> expectedValues) {
      Response response = jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(json));
      assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());
      try {
         //Pull all the important bits out of the readTree
         JsonNode readTree = jaxRsApi.readTree(Lib.inputStreamToString((InputStream) response.getEntity()));
         ArtifactId artId = ArtifactId.valueOf(readTree.get("results").get("ids").get(0).asLong());
         BranchId branchId = BranchId.valueOf(readTree.get("tx").get("branchId").asLong());
         TransactionId txId = TransactionId.valueOf(readTree.get("tx").get("id").asLong());
         //Find the artifact with the new Attribute and ensure the expectedValues are on the artifact
         Artifact art = ArtifactQuery.getArtifactFromId(artId, branchId);
         List<Attribute<Object>> attributes;
         for (String value : expectedValues) {
            attributes = art.getAttributesByValue(attrType, value);
            assertTrue(!attributes.isEmpty());
         }

         //Cleanup and purge the Transaction on the new branch
         purge(TransactionToken.valueOf(txId, branchId));
         purgeArts(Arrays.asList(artId.getId()));
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to convert response to Strings");
      }
   }

   private TransactionToken testWrapUpFromDate(String json, AttributeTypeToken attrType, Date expectedValue,
      ArtifactId artId) {
      Response response = jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(json));
      assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());
      try {
         JsonNode readTree = jaxRsApi.readTree(Lib.inputStreamToString((InputStream) response.getEntity()));
         BranchId branchId = BranchId.valueOf(readTree.get("tx").get("branchId").asLong());
         TransactionId txId = TransactionId.valueOf(readTree.get("tx").get("id").asLong());
         ArtifactQuery.reloadArtifactFromId(artId, branchId);
         Artifact art = ArtifactQuery.getArtifactFromId(artId, branchId);
         List<Attribute<Date>> attributes = art.getAttributes(attrType);
         if (attributes.get(0).getValue().getTime() != expectedValue.getTime()) {
            long t1 = attributes.get(0).getValue().getTime();
            long t2 = expectedValue.getTime();
            long delta = t1 - t2;
            throw new OseeCoreException("Dates values do not match %d", delta);
         }
         return TransactionToken.valueOf(txId, branchId);
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to convert response to String");
      }
   }

   private void purge(TransactionToken transactionId) throws Exception {
      IOperation operation = PurgeTransactionOperation.getPurgeTransactionOperation(transactionId);
      Asserts.assertOperation(operation, IStatus.OK);
   }

   @Test
   public void testPurgeUnusedBackingData() {
      
      JdbcClient client = AtsApiService.get().getJdbcService().getClient();
      client.runPreparedUpdate("insert into osee_attribute(attr_id, gamma_id, art_id, attr_type_id, value) values (1,-1,1,1,'x')");
      int initialRowCount = client.fetch(0, "SELECT count(1) FROM osee_attribute where gamma_id = -1");
      try (Response response = transactionEndpoint.purgeUnusedBackingDataAndTransactions(10)) {
         //
      }
      int afterRowCount = client.fetch(0, "SELECT count(1) FROM osee_attribute where gamma_id = -1");
      Assert.assertNotEquals(initialRowCount, afterRowCount);
   }
}