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

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Component;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import org.eclipse.core.runtime.IStatus;
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
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
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
      ArtifactId sawProductDecomp = ArtifactQuery.getArtifactFromTypeAndName(Component,
         CoreArtifactTokens.SAW_PRODUCT_DECOMP, DemoBranches.SAW_PL);

      List<ChangeItem> changeItems = transactionEndpoint.getArtifactHistory(sawProductDecomp, DemoBranches.SAW_PL);

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
      Artifact artifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Requirement,
         DemoBranches.SAW_PL_Working_Branch, "Artifact for Applicability Change");
      artifact.internalSetApplicablityId(ApplicabilityId.valueOf(applic.getId()));
      transaction.addArtifact(artifact);
      //Test to ensure applicabilityId was actually changed successfully
      assertTrue(artifact.getApplicablityId().equals(ApplicabilityId.valueOf(applic.getId())));

      //Complete transfer of transaction to a new branch
      TransactionToken createTx = transaction.execute();
      TransactionToken priorTxToken = TransactionManager.getPriorTransaction(createTx);
      TransactionBuilderData txData = transactionEndpoint.exportTxsDiff(priorTxToken, createTx);
      txData.setBranch(DemoBranches.SAW_PL_Hardening_Branch.getIdString());
      ObjectMapper mapper = new ObjectMapper();
      String json;
      try {
         json = mapper.writeValueAsString(txData);
      } catch (JsonProcessingException ex) {
         throw new OseeCoreException("Failed to write txData as json in TransactionEndpointTest");
      }
      Response response = jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(json));
      assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());

      //Ensure Artifact is created on SAW_PL_Hardening_Branch with the correct ApplicabilityId
      assertTrue(artifact.getApplicablityId().equals(
         ArtifactQuery.getArtifactFromId(artifact, DemoBranches.SAW_PL_Hardening_Branch).getApplicablityId()));

      //Delete the applicability on the SAW_PL_Working_Branch and transfer it to SAW_PL_Hardening_Branch
      SkynetTransaction deleteTransaction = TransactionManager.createTransaction(DemoBranches.SAW_PL_Working_Branch,
         TransactionEndpointTest.class.getName() + ": Set Applicability to Base");
      deleteTransaction.addArtifact(artifact);
      artifact.internalSetApplicablityId(ApplicabilityId.BASE);
      artifact.setName("Changed ApplicabilityId and Name");
      artifact.persist(deleteTransaction);
      TransactionToken deleteTx = deleteTransaction.execute();
      priorTxToken = TransactionManager.getPriorTransaction(deleteTx);
      TransactionBuilderData txDataDelete = transactionEndpoint.exportTxsDiff(priorTxToken, deleteTx);
      txDataDelete.setBranch(DemoBranches.SAW_PL_Hardening_Branch.getIdString());
      ObjectMapper mapperDeleteArt = new ObjectMapper();
      String deleteJson;
      try {
         deleteJson = mapperDeleteArt.writeValueAsString(txDataDelete);
      } catch (JsonProcessingException ex) {
         throw new OseeCoreException("Failed to write txData as json for deleting a applicability");
      }
      Response responseDeleteArt =
         jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(deleteJson));
      assertEquals(Family.SUCCESSFUL, responseDeleteArt.getStatusInfo().getFamily());
      //Need to decache the branch because it was previously getting old unchanged artifact from the cache
      ArtifactCache.deCache(DemoBranches.SAW_PL_Hardening_Branch);

      //Ensure Artifact is changed on SAW_PL_Hardening_Branch with the Base ApplicabilityId
      assertTrue(ApplicabilityId.BASE.equals(
         ArtifactQuery.getArtifactFromId(artifact, DemoBranches.SAW_PL_Hardening_Branch).getApplicablityId()));

      //Test Cleanup
      try {
         purge(createTx);
         purge(deleteTx);
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to purge Transaction in testArtifactDeletionTransfer");
      }
   }

   @Test
   public void testArtifactDeletionTransfer() {
      //Create an artifact on SAW_PL_Working_Branch and transfer it to SAW_PL_Hardening_Branch
      SkynetTransaction addArtTransaction = TransactionManager.createTransaction(DemoBranches.SAW_PL_Working_Branch,
         TransactionEndpointTest.class.getName() + ": Create Artifact for Deletion");
      Artifact artifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Requirement,
         DemoBranches.SAW_PL_Working_Branch, "Artifact for DeletionTestTransfer");
      addArtTransaction.addArtifact(artifact);
      TransactionToken addArtTx = addArtTransaction.execute();
      TransactionToken priorTxToken = TransactionManager.getPriorTransaction(addArtTx);
      TransactionBuilderData txDataAddArt = transactionEndpoint.exportTxsDiff(priorTxToken, addArtTx);
      txDataAddArt.setBranch(DemoBranches.SAW_PL_Hardening_Branch.getIdString());
      ObjectMapper mapperAddArt = new ObjectMapper();
      String addArtJson;
      try {
         addArtJson = mapperAddArt.writeValueAsString(txDataAddArt);
      } catch (JsonProcessingException ex) {
         throw new OseeCoreException("Failed to write txData as json in for creating a artifact");
      }
      Response responseAddArt =
         jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(addArtJson));
      assertEquals(Family.SUCCESSFUL, responseAddArt.getStatusInfo().getFamily());

      //Ensure Artifact is created on SAW_PL_Hardening_Branch
      assertTrue(artifact.getId().equals(
         ArtifactQuery.getArtifactFromId(artifact, DemoBranches.SAW_PL_Hardening_Branch).getId()));

      //Delete the Artifact on the SAW_PL_Working_Branch and transfer it to SAW_PL_Hardening_Branch
      SkynetTransaction deleteArtTransaction = TransactionManager.createTransaction(DemoBranches.SAW_PL_Working_Branch,
         TransactionEndpointTest.class.getName() + ": Delete Artifact Transaction");
      artifact.deleteAndPersist(deleteArtTransaction);
      TransactionToken deleteArtTx = deleteArtTransaction.execute();
      priorTxToken = TransactionManager.getPriorTransaction(deleteArtTx);
      TransactionBuilderData txDataDeleteArt = transactionEndpoint.exportTxsDiff(priorTxToken, deleteArtTx);
      txDataDeleteArt.setBranch(DemoBranches.SAW_PL_Hardening_Branch.getIdString());
      ObjectMapper mapperDeleteArt = new ObjectMapper();
      String deleteArtJson;
      try {
         deleteArtJson = mapperDeleteArt.writeValueAsString(txDataDeleteArt);
      } catch (JsonProcessingException ex) {
         throw new OseeCoreException("Failed to write txData as json for deleting a artifact");
      }
      Response responseDeleteArt =
         jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(deleteArtJson));
      assertEquals(Family.SUCCESSFUL, responseDeleteArt.getStatusInfo().getFamily());
      //Need to decache the branch because it was previously getting the undeleted artifact from the cache
      ArtifactCache.deCache(DemoBranches.SAW_PL_Hardening_Branch);

      //Ensure Artifact is deleted on SAW_PL_Hardening_Branch
      assertTrue(ArtifactQuery.getArtifactFromId(artifact, DemoBranches.SAW_PL_Hardening_Branch,
         DeletionFlag.allowDeleted(true)).isDeleted());

      //Test Cleanup
      try {
         purge(addArtTx);
         purge(deleteArtTx);
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to purge Transaction in testArtifactDeletionTransfer");
      }
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
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to purge Transaction:" + currentTx.getIdString());
      }
      return json;
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
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to purge Transaction:" + currentTx.getIdString());
      }
      return json;
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
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to convert response to Strings");
      }
   }

   private void purge(TransactionToken transactionId) throws Exception {
      IOperation operation = PurgeTransactionOperation.getPurgeTransactionOperation(transactionId);
      Asserts.assertOperation(operation, IStatus.OK);
   }
}