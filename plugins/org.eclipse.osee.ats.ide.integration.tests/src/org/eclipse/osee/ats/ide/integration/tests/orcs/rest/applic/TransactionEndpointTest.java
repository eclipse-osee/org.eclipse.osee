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

import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.DefaultHierarchyRoot;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Component;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Branden W. Phillips
 */
public class TransactionEndpointTest {

   private static TransactionEndpoint transactionEndpoint;
   private static ArtifactEndpoint artifactEndpoint;
   private static JaxRsApi jaxRsApi;

   @BeforeClass
   public static void testSetup() {
      transactionEndpoint = ServiceUtil.getOseeClient().getTransactionEndpoint();
      artifactEndpoint = ServiceUtil.getOseeClient().getArtifactEndpoint(DemoBranches.SAW_PL);
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
      //Create a new Date/Time Attribute on a new Artifact
      ArtifactId parentArtifact = DefaultHierarchyRoot;
      String name = getClass().getSimpleName() + " Date";
      String currentTime = Long.valueOf(new Date().getTime()).toString();
      ArtifactToken artifactToken =
         artifactEndpoint.createArtifact(DemoBranches.SAW_PL, CoreArtifactTypes.GitCommit, parentArtifact, name);
      artifactEndpoint.setSoleAttributeValue(DemoBranches.SAW_PL, artifactToken, CoreAttributeTypes.GitCommitAuthorDate,
         currentTime);

      //Get all changes done to the artifact and find the latest tx and the tx before the artifact was created
      List<ChangeItem> changes = transactionEndpoint.getArtifactHistory(artifactToken, DemoBranches.SAW_PL);
      if (!changes.isEmpty()) {
         TransactionToken startingTx =
            TransactionManager.getPriorTransaction(changes.get(0).getCurrentVersion().getTransactionToken());
         TransactionToken currentTx = getMaxTransaction(changes);
         TransactionBuilderData txData = transactionEndpoint.exportTxsDiff(startingTx, currentTx);
         txData.setBranch(DemoBranches.SAW_PL_Hardening_Branch.getIdString());
         ObjectMapper mapper = new ObjectMapper();
         try {
            //Write out the diff in create_tx.json format and read/import it in on a different branch
            String json = mapper.writeValueAsString(txData);
            Response response =
               jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(json));
            assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());
         } catch (JsonProcessingException ex) {
            throw new OseeCoreException("Failed to write txData as json in testAddDateAttribute");
         }
      } else {
         throw new OseeCoreException("Test transfer date attribute failed getting changes");
      }
   }

   @Test
   public void testAddBooleanAttribute() {
      //Create a new Boolean Attribute on a new Artifact
      ArtifactId parentArtifact = DefaultHierarchyRoot;
      String name = getClass().getSimpleName() + " Boolean";
      ArtifactToken artifactToken = artifactEndpoint.createArtifact(DemoBranches.SAW_PL,
         CoreArtifactTypes.OseeTypeDefinition, parentArtifact, name);
      artifactEndpoint.setSoleAttributeValue(DemoBranches.SAW_PL, artifactToken, CoreAttributeTypes.Active,
         Boolean.TRUE.toString());

      //Get all changes done to the artifact and find the latest tx and the tx before the artifact was created
      List<ChangeItem> changes = transactionEndpoint.getArtifactHistory(artifactToken, DemoBranches.SAW_PL);
      if (!changes.isEmpty()) {
         TransactionToken startingTx =
            TransactionManager.getPriorTransaction(changes.get(0).getCurrentVersion().getTransactionToken());
         TransactionToken currentTx = getMaxTransaction(changes);
         TransactionBuilderData txData = transactionEndpoint.exportTxsDiff(startingTx, currentTx);
         txData.setBranch(DemoBranches.SAW_PL_Hardening_Branch.getIdString());
         ObjectMapper mapper = new ObjectMapper();
         try {
            //Write out the diff in create_tx.json format and read/import it in on a different branch
            String json = mapper.writeValueAsString(txData);
            Response response =
               jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(json));
            assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());
         } catch (JsonProcessingException ex) {
            throw new OseeCoreException("Failed to write txData as json in testAddBooleanAttribute");
         }
      } else {
         throw new OseeCoreException("Test transfer boolean attribute failed getting changes");
      }
   }

   @Test
   public void testAddStringAttribute() {
      //Create a new String Attribute on a new Artifact
      ArtifactId parentArtifact = DefaultHierarchyRoot;
      String name = getClass().getSimpleName() + " String";
      ArtifactToken artifactToken =
         artifactEndpoint.createArtifact(DemoBranches.SAW_PL, CoreArtifactTypes.Artifact, parentArtifact, name);
      artifactEndpoint.setSoleAttributeValue(DemoBranches.SAW_PL, artifactToken, CoreAttributeTypes.Annotation,
         "Test Add String Attribute");

      //Get all changes done to the artifact and find the latest tx and the tx before the artifact was created
      List<ChangeItem> changes = transactionEndpoint.getArtifactHistory(artifactToken, DemoBranches.SAW_PL);
      if (!changes.isEmpty()) {
         TransactionToken startingTx =
            TransactionManager.getPriorTransaction(changes.get(0).getCurrentVersion().getTransactionToken());
         TransactionToken currentTx = getMaxTransaction(changes);
         TransactionBuilderData txData = transactionEndpoint.exportTxsDiff(startingTx, currentTx);
         txData.setBranch(DemoBranches.SAW_PL_Hardening_Branch.getIdString());
         ObjectMapper mapper = new ObjectMapper();
         try {
            //Write out the diff in create_tx.json format and read/import it in on a different branch
            String json = mapper.writeValueAsString(txData);
            Response response =
               jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(json));
            assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());
         } catch (JsonProcessingException ex) {
            throw new OseeCoreException("Failed to write txData as json in testAddStringAttribute");
         }
      } else {
         throw new OseeCoreException("Test transfer string attribute failed getting changes");
      }
   }

   @Test
   public void testAddIntegerAttribute() {
      //Create a new Integer Attribute on a new Artifact
      ArtifactId parentArtifact = DefaultHierarchyRoot;
      String name = getClass().getSimpleName() + " Integer";
      ArtifactToken artifactToken =
         artifactEndpoint.createArtifact(DemoBranches.SAW_PL, CoreArtifactTypes.Breaker, parentArtifact, name);
      artifactEndpoint.setSoleAttributeValue(DemoBranches.SAW_PL, artifactToken, CoreAttributeTypes.CircuitBreakerId,
         Integer.valueOf(10).toString());

      //Get all changes done to the artifact and find the latest tx and the tx before the artifact was created
      List<ChangeItem> changes = transactionEndpoint.getArtifactHistory(artifactToken, DemoBranches.SAW_PL);
      if (!changes.isEmpty()) {
         TransactionToken startingTx =
            TransactionManager.getPriorTransaction(changes.get(0).getCurrentVersion().getTransactionToken());
         TransactionToken currentTx = getMaxTransaction(changes);
         TransactionBuilderData txData = transactionEndpoint.exportTxsDiff(startingTx, currentTx);
         txData.setBranch(DemoBranches.SAW_PL_Hardening_Branch.getIdString());
         ObjectMapper mapper = new ObjectMapper();
         try {
            //Write out the diff in create_tx.json format and read/import it in on a different branch
            String json = mapper.writeValueAsString(txData);
            Response response =
               jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(json));
            assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());
         } catch (JsonProcessingException ex) {
            throw new OseeCoreException("Failed to write txData as json in testAddIntegerAttribute");
         }
      } else {
         throw new OseeCoreException("Test transfer integer attribute failed getting changes");
      }
   }

   @Test
   public void testAddArtifactIdAttribute() {
      //Create a new ArtifactId Attribute on a new Artifact
      ArtifactId parentArtifact = DefaultHierarchyRoot;
      String name = getClass().getSimpleName() + " ArtifactId";
      ArtifactToken artifactToken = artifactEndpoint.createArtifact(DemoBranches.SAW_PL,
         CoreArtifactTypes.CertificationBaselineEvent, parentArtifact, name);
      artifactEndpoint.setSoleAttributeValue(DemoBranches.SAW_PL, artifactToken, CoreAttributeTypes.BaselinedBy,
         ArtifactId.valueOf(1234321L).getIdString());

      //Get all changes done to the artifact and find the latest tx and the tx before the artifact was created
      List<ChangeItem> changes = transactionEndpoint.getArtifactHistory(artifactToken, DemoBranches.SAW_PL);
      if (!changes.isEmpty()) {
         TransactionToken startingTx =
            TransactionManager.getPriorTransaction(changes.get(0).getCurrentVersion().getTransactionToken());
         TransactionToken currentTx = getMaxTransaction(changes);
         TransactionBuilderData txData = transactionEndpoint.exportTxsDiff(startingTx, currentTx);
         txData.setBranch(DemoBranches.SAW_PL_Hardening_Branch.getIdString());
         ObjectMapper mapper = new ObjectMapper();
         try {
            //Write out the diff in create_tx.json format and read/import it in on a different branch
            String json = mapper.writeValueAsString(txData);
            Response response =
               jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(json));
            assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());
         } catch (JsonProcessingException ex) {
            throw new OseeCoreException("Failed to write txData as json in testAddArtifactIdAttribute");
         }
      } else {
         throw new OseeCoreException("Test transfer ArtifactId attribute failed getting changes");
      }
   }

   private TransactionToken getMaxTransaction(List<ChangeItem> changes) {
      TransactionToken maxTx = changes.get(0).getCurrentVersion().getTransactionToken();
      for (ChangeItem change : changes) {
         if (change.getCurrentVersion().getTransactionToken().isGreaterThan(maxTx)) {
            maxTx = change.getCurrentVersion().getTransactionToken();
         }
      }
      return maxTx;
   }
}