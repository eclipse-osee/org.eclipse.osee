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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.Asserts;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.PurgeTransactionOperation;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.junit.BeforeClass;
import org.junit.Test;

public class ActionEndpointTest {

   private static TransactionEndpoint transactionEndpoint;
   private static JaxRsApi jaxRsApi;

   @BeforeClass
   public static void testSetup() {
      transactionEndpoint = ServiceUtil.getOseeClient().getTransactionEndpoint();
      ServiceUtil.getOseeClient().getArtifactEndpoint(CoreBranches.COMMON);
      jaxRsApi = ServiceUtil.getOseeClient().jaxRsApi();
   }

   /** 
    * Simple test for the testActionHistory rest call. Checking for the following cases <br/>
    *  1. Creating an action <br/>
    *  2. Making a comment change to the action <br/>
    *  3. Export the diff to JSON (for the creation and the change) <br/>
    *  4. Purge the transactions <br/>
    *  5. Import the diff <br/>
    *  6. Verify the action is back in OSEE  
    *  7. Purge the action for cleanup
    * 
    */
   @Test
   public void testActionHistory() throws Exception {
      // Comment to Verify
      String comment = getClass().getSimpleName() + " testCreateActionTest"; 
      // JSON to test
      String testAction =  "";      
      // Title Used for Comment 
      String title = comment;
      
      // Create Collection 
      Collection<IAtsActionableItem> aias = new HashSet<>();
      aias.add(AtsApiService.get().getActionableItemService().getActionableItemById(DemoArtifactToken.SAW_Test_AI));
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(title);

      ActionResult resultToTest =
         AtsApiService.get().getActionService().createAction(null, title, title, ChangeTypes.Improvement, "1", false,
            null, aias, new Date(), AtsApiService.get().getUserService().getCurrentUser(), null, changes);
           
      Artifact art = ArtifactQuery.getArtifactFromId(resultToTest.getTeamWfArts().iterator().next(),CoreBranches.COMMON);
      AttributeTypeArtifactId attrType = AtsAttributeTypes.TeamDefinitionReference;

      // String to Return for Comparison for final check and purge
      String teamDefRefName = art.getSoleAttributeValue(attrType).toString();
      
      // Transaction Token for the diff
      TransactionToken tx = changes.execute();      
      TransactionToken startingTx = TransactionManager.getPriorTransaction(tx);
      TransactionRecord record = TransactionManager.getTransaction(tx); 
      comment = record.getComment();

      // Obtain Data to Convert to JSON
      TransactionBuilderData txData = transactionEndpoint.exportTxsDiff(startingTx, tx);      
      txData.setTxComment(comment);                  

      // JSON String Conversion
      try {
         ObjectMapper mapper = new ObjectMapper();
         testAction = mapper.writeValueAsString(txData);
        } catch (JsonProcessingException ex) {
         throw new OseeCoreException("Failed to write txData as json in testActionHistory: " + ex);
      }    

      // Original Created Transaction Content to purge
      try {         
         purge(tx);          
      } catch (Exception ex) {
         fail("Error purging transactions: " + ex);
      }     
      
      // Import the content back in 
      Response response =
         jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(testAction));
      assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());
      
      try {
         
         // Parse the data via readTree
         JsonNode readTree = jaxRsApi.readTree(Lib.inputStreamToString((InputStream) response.getEntity()));
         ArtifactId artId = ArtifactId.valueOf(readTree.get("results").get("ids").get(0).asLong());
         BranchId branchId = BranchId.valueOf(readTree.get("tx").get("branchId").asLong());
         TransactionId txId = TransactionId.valueOf(readTree.get("tx").get("id").asLong());
         
         // Query the Artifact and Team Definition Reference Name for Compare 
         Artifact artifactToCompare = ArtifactQuery.getArtifactFromId(artId, branchId);
         AttributeTypeArtifactId attributeType = AtsAttributeTypes.TeamDefinitionReference;
         String teamDefinitionReferenceName = artifactToCompare.getSoleAttributeValue(attributeType).toString();
         
         if(teamDefRefName.equalsIgnoreCase(teamDefinitionReferenceName)) {
            // Cleanup and purge the Transaction on the new branch
            purge(TransactionToken.valueOf(txId, branchId));  
         }   
         else {
            fail("Team Defintion Reference Names do not match between JSON: " + teamDefinitionReferenceName + " and Action Created: " + teamDefRefName);
         }
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to Purge the Action after import: " + ex);
      }  
   }
   
   private void purge(TransactionToken transactionId) throws Exception {
      IOperation operation = PurgeTransactionOperation.getPurgeTransactionOperation(transactionId);
      Asserts.assertOperation(operation, IStatus.OK);
   }

}