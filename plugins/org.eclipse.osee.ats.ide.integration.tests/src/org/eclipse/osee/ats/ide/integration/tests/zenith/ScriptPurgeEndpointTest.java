/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.zenith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.NewBranch;
import org.eclipse.osee.orcs.rest.model.transaction.AddRelation;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.eclipse.osee.testscript.ResultToPurge;
import org.eclipse.osee.testscript.ScriptConfigToken;
import org.eclipse.osee.testscript.ScriptDefToken;
import org.eclipse.osee.testscript.ScriptResultToken;
import org.eclipse.osee.testscript.TimelineStatsToken;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class ScriptPurgeEndpointTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   private static BranchEndpoint branchEndpoint;
   private static JaxRsApi jaxRsApi;

   private final String CISETID = "4444";

   @BeforeClass
   public static void testSetup() {
      branchEndpoint = ServiceUtil.getOseeClient().getBranchEndpoint();
      jaxRsApi = ServiceUtil.getOseeClient().jaxRsApi();
   }

   @Test
   public void testPurgeResults() {
      BranchId testBranch = branchEndpoint.createBranch(testDataInitialization(DemoBranches.SAW_PL));

      // Create config artifact and set results to keep to 5
      createZenithConfig(testBranch, 5);

      // Create test data
      String scriptName = "Test Script";
      ArtifactId scriptId = createScriptArtifact(testBranch, scriptName);
      createScriptResults(testBranch, scriptName, scriptId, 10, "2025-01-01");

      // Check that there is no timeline
      Response res =
         jaxRsApi.newTarget("script/dashboard/" + testBranch.getIdString() + "/" + CISETID + "/timeline").request(
            MediaType.APPLICATION_JSON).get();
      assertEquals(200, res.getStatus());
      TimelineStatsToken timeline = res.readEntity(TimelineStatsToken.class);
      res.close();
      assertEquals(ArtifactId.SENTINEL, timeline.getId());

      // Check that there are no deleted results
      res = jaxRsApi.newTarget("script/purge/results/deleted/" + testBranch.getIdString()).request(
         MediaType.APPLICATION_JSON).get();
      Collection<ResultToPurge> results = res.readEntity(new GenericType<Collection<ResultToPurge>>() {//
      });
      assertEquals(200, res.getStatus());
      res.close();
      assertEquals(0, results.size());

      // Delete results
      res = jaxRsApi.newTarget("script/purge/results/" + testBranch.getIdString() + "?deleteonly=true").request(
         MediaType.APPLICATION_JSON).post(null);
      assertEquals(200, res.getStatus());
      res.close();

      // Check that results were deleted
      res = jaxRsApi.newTarget("script/purge/results/deleted/" + testBranch.getIdString()).request(
         MediaType.APPLICATION_JSON).get();
      assertEquals(200, res.getStatus());
      results = res.readEntity(new GenericType<Collection<ResultToPurge>>() {//
      });
      res.close();
      assertEquals(5, results.size());

      // Check that timeline was updated
      res = jaxRsApi.newTarget("script/dashboard/" + testBranch.getIdString() + "/" + CISETID + "/timeline").request(
         MediaType.APPLICATION_JSON).get();
      assertEquals(200, res.getStatus());
      timeline = res.readEntity(TimelineStatsToken.class);
      res.close();
      assertTrue("Timeline ID is not valid", timeline.getId().isValid());
      assertEquals(CISETID, timeline.getSetId().getIdString());
      assertTrue("Deleted script was not added to timeline",
         timeline.getDays().get(0).getScripts().containsKey(scriptName));

      // Run purge
      res = jaxRsApi.newTarget("script/purge/results/" + testBranch.getIdString()).request(
         MediaType.APPLICATION_JSON).post(null);
      assertEquals(200, res.getStatus());
      res.close();

      // Check that there are no deleted results
      res = jaxRsApi.newTarget("script/purge/results/deleted/" + testBranch.getIdString()).request(
         MediaType.APPLICATION_JSON).get();
      assertEquals(200, res.getStatus());
      results = res.readEntity(new GenericType<Collection<ResultToPurge>>() {//
      });
      res.close();
      assertEquals(0, results.size());

      res = branchEndpoint.purgeBranch(testBranch, false);
      res.close();
   }

   private void createZenithConfig(BranchId branch, int resultsToKeep) {
      TransactionBuilderData txData = new TransactionBuilderData();
      txData.setBranch(branch.getIdString());
      txData.setTxComment("Create Zenith config artifact");
      txData.setCreateArtifacts(new LinkedList<>());

      ScriptConfigToken config = new ScriptConfigToken();
      config.setName("Zenith Config");
      config.setTestResultsToKeep(resultsToKeep);
      txData.getCreateArtifacts().add(config.createArtifact("config"));

      sendTx(txData);
   }

   private ArtifactId createScriptArtifact(BranchId branch, String scriptName) {
      TransactionBuilderData txData = new TransactionBuilderData();
      txData.setBranch(branch.getIdString());
      txData.setTxComment("Create test script artifact");
      txData.setCreateArtifacts(new LinkedList<>());

      ScriptDefToken script = new ScriptDefToken(123L, scriptName);
      script.setFullScriptName(scriptName);
      txData.getCreateArtifacts().add(script.createArtifact(script.getArtifactId().getIdString()));

      TransactionResult txResult = sendTx(txData);
      assertEquals(1, txResult.getResults().getIds().size());
      ArtifactId scriptId = ArtifactId.valueOf(txResult.getResults().getIds().get(0));

      return scriptId;
   }

   private void createScriptResults(BranchId branch, String scriptName, ArtifactId scriptId, int numResults,
      String startDate) {

      TransactionBuilderData txData = new TransactionBuilderData();
      txData.setBranch(branch.getIdString());
      txData.setTxComment("Create test script results");
      txData.setCreateArtifacts(new LinkedList<>());
      txData.setAddRelations(new LinkedList<>());

      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      Calendar cal = Calendar.getInstance();

      try {
         cal.setTime(format.parse(startDate));
      } catch (ParseException ex1) {
         assertTrue("Invalid start date", false);
      }

      Date executionDate = cal.getTime();
      Long id = 5555L;

      for (int i = 0; i < numResults; i++) {
         ScriptResultToken result = new ScriptResultToken(id, scriptName);
         result.setFileUrl("");
         result.setExecutionDate(executionDate);
         result.setSetId(CISETID);

         txData.getCreateArtifacts().add(result.createArtifact(Long.toString(id)));
         AddRelation rel = new AddRelation();
         rel.setaArtId(scriptId.getIdString());
         rel.setbArtId(result.getArtifactId().getIdString());
         rel.setTypeId(CoreRelationTypes.TestScriptDefToTestScriptResults.getIdString());
         txData.getAddRelations().add(rel);

         id++;
         cal.add(Calendar.DATE, 1);
         executionDate = cal.getTime();
      }

      TransactionResult txResult = sendTx(txData);
      assertEquals(numResults, txResult.getResults().getIds().size());
   }

   private TransactionResult sendTx(TransactionBuilderData txData) {
      ObjectMapper mapper = new ObjectMapper();
      String json = "{}";
      try {
         json = mapper.writeValueAsString(txData);
      } catch (JsonProcessingException ex) {
         assertTrue("Error parsing tx builder json", false);
      }

      Response res = jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(json));
      assertEquals(200, res.getStatus());
      TransactionResult txResult = res.readEntity(TransactionResult.class);
      res.close();
      return txResult;
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

}
