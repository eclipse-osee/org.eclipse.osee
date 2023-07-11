/*********************************************************************
 * Copyright (c) 2018 Boeing
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
import static org.junit.Assert.fail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.Asserts;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NoPopUpsRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.PurgeTransactionOperation;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

public class ArtifactModificationTest {
   private static TransactionEndpoint transactionEndpoint;
   private static JaxRsApi jaxRsApi;
   
   // Working Branches for Test
   private static String DEST_BRANCH = "Test_Destination_Branch";
   private static String TEST_BRANCH = "Test_Write_Branch";
   
   @ClassRule
   public static TestRule classRuleChain =
      RuleChain
         .outerRule( new NotProductionDataStoreRule() )
         .around( new ExitDatabaseInitializationRule() )
         .around( TestUserRules.createInPublishingGroupTestRule() )
         .around( new NoPopUpsRule() )
         ;
   

   @BeforeClass
   public static void testSetup() {
      transactionEndpoint = ServiceUtil.getOseeClient().getTransactionEndpoint();
      ServiceUtil.getOseeClient().getArtifactEndpoint(CoreBranches.COMMON);
      jaxRsApi = ServiceUtil.getOseeClient().jaxRsApi();      
   }

   /**
    * 
    * Simple test for the testArtifactModification. <br/> Checking for the following cases <br/>
    * 1. Create a test branch (source/export) and a destination branch import)  <br/> 
    * 2. Copy an artifact from the source branch.<br/> 
    * 3. Post Copy, Edit/Make a change to artifact attribute(s) on the source branch copy those changes <br/>
    * 4. JSON Post data to export branch <br/>
    * 5. Test Cleanup <br/>
    * 
    * 
    */
   @Test
   public void testArtifactModification() throws Exception {      
      // JSON 
      String json = "";      

      // Change(s) we're going to observe
      String name = getClass().getSimpleName() + " testingArtifactNameChange"; 

      // Create Test and Destination Branches from SAW_PL_Hardening_Branch       
      BranchToken testBranch = BranchManager.createWorkingBranch(DemoBranches.SAW_PL_Hardening_Branch, TEST_BRANCH);
      BranchToken destBranch = BranchManager.createWorkingBranch(testBranch, DEST_BRANCH);
      
      // Obtain Artifact from Test Branch (Test Branch Has Read/Write Permissions)
      Artifact testArtifact = ArtifactQuery.getArtifactFromTypeAndName(Component, CoreArtifactTokens.SAW_PRODUCT_DECOMP, testBranch);
      
      // Post Copy - Make an edit/change to Source (Test) Branch Artifact
      TransactionToken sourceTx = ServiceUtil.getOseeClient().getArtifactEndpoint(testBranch).setSoleAttributeValue(testBranch, testArtifact, CoreAttributeTypes.Name, name);
      
      // Obtain Secondary Transaction Token for Diff to be Exported
      TransactionToken priorTxToken = TransactionManager.getPriorTransaction(sourceTx);
      TransactionBuilderData txData = transactionEndpoint.exportTxsDiff(priorTxToken, sourceTx);
      txData.setBranch(destBranch.getIdString());

      // Convert Data to JSON for Import      
      try {
         ObjectMapper mapper = new ObjectMapper();         
         json = mapper.writeValueAsString(txData);
         Response response = jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(json));
         assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());
      } catch (JsonProcessingException ex) {
         fail("Failed to import content: " + ex);
      }
           
      // Test CleanUp Remove Test and Destination Branches, Delete Transaction(s)
      try {         
         // Original Created Transaction Content to purge
         purge(sourceTx);             
         AtsApiService.get().getBranchService().deleteBranch(testBranch);
         AtsApiService.get().getBranchService().deleteBranch(destBranch);
      } catch (Exception ex) {
         fail("Failed test clean up: " + ex);
      }          
   }

   private void purge(TransactionToken transactionId) throws Exception {
      IOperation operation = PurgeTransactionOperation.getPurgeTransactionOperation(transactionId);
      Asserts.assertOperation(operation, IStatus.OK);
   }

}