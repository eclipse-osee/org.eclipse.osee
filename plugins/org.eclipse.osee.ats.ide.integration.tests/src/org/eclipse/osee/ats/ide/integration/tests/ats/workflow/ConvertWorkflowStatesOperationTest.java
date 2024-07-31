/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.ConvertWorkflowStatesOperation;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case for {@link ConvertWorkflowStatesOperation}
 *
 * @author Donald G. Dunne
 */
public class ConvertWorkflowStatesOperationTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() {
      SkynetTransaction transaction = TransactionManager.createTransaction(AtsApiService.get().getAtsBranch(),
         "ConvertWorkflowStatesOperationTest.cleanup");
      for (Artifact art : ArtifactQuery.getArtifactListFromTypeAndName(AtsArtifactTypes.TeamWorkflow,
         "ConvertWorkflowStatesOperationTest", AtsApiService.get().getAtsBranch(),
         QueryOption.CONTAINS_MATCH_OPTIONS)) {
         art.deleteAndPersist(transaction);
      }
      transaction.execute();
   }

   @Test
   public void testDoWork_errorChecking() throws Exception {
      Map<String, String> fromStateToStateMap = new HashMap<>();
      List<AbstractWorkflowArtifact> workflows = new ArrayList<>();
      boolean persist = false;
      XResultData rd = new XResultData(false);
      ConvertWorkflowStatesOperation operation =
         new ConvertWorkflowStatesOperation(fromStateToStateMap, workflows, persist, rd);
      Operations.executeWorkAndCheckStatus(operation);

      Assert.assertEquals("Error: Must enter FromToState pairs\n", rd.toString());

      fromStateToStateMap.put("Endor;se", "New\"StateName");
      rd.clear();

      Operations.executeWorkAndCheckStatus(operation);
      Assert.assertEquals("Should be two state name errors\n", 2, rd.getNumErrors());

      fromStateToStateMap.clear();
      fromStateToStateMap.put("Endorse", "NewStateName");

      rd.clear();

      Operations.executeWorkAndCheckStatus(operation);
      Assert.assertEquals("Error: No workflows entered\n", rd.toString());
   }

   @Test
   public void testDoWork() throws Exception {
      Map<String, String> fromStateToStateMap = new HashMap<>();
      fromStateToStateMap.put("Endorse", "NewEndorse");
      fromStateToStateMap.put("Analyze", "NewAnalyze");

      List<AbstractWorkflowArtifact> workflows = new ArrayList<>();
      Artifact teamWf = ArtifactTypeManager.addArtifact(AtsArtifactTypes.TeamWorkflow,
         AtsApiService.get().getAtsBranch(), "ConvertWorkflowStatesOperationTest.testDoWork");
      teamWf.addAttribute(AtsAttributeTypes.CurrentStateName, "Endorse");
      teamWf.addAttribute(AtsAttributeTypes.CompletedFromState, "Analyze");
      teamWf.addAttribute(AtsAttributeTypes.CancelledFromState, "Endorse");
      teamWf.addAttribute(AtsAttributeTypes.Log, "log state=\"Endorse\", state=\"Analyze\"");
      workflows.add((AbstractWorkflowArtifact) teamWf);

      boolean persist = false;
      XResultData rd = new XResultData(false);

      ConvertWorkflowStatesOperation operation =
         new ConvertWorkflowStatesOperation(fromStateToStateMap, workflows, persist, rd);
      Operations.executeWorkAndCheckStatus(operation);

      Assert.assertFalse(rd.isErrors());
      Assert.assertEquals("NewEndorse", teamWf.getSoleAttributeValue(AtsAttributeTypes.CurrentStateName, ""));
      Assert.assertEquals("NewAnalyze", teamWf.getSoleAttributeValue(AtsAttributeTypes.CompletedFromState, ""));
      Assert.assertEquals("NewEndorse", teamWf.getSoleAttributeValue(AtsAttributeTypes.CancelledFromState, ""));
      Assert.assertEquals("log state=\"NewEndorse\", state=\"NewAnalyze\"",
         teamWf.getSoleAttributeValue(AtsAttributeTypes.Log, ""));

      Assert.assertTrue(teamWf.isDirty());
      // decache to cleanup test, cause artifact is not persisted
      ArtifactCache.deCache(teamWf);
   }

   @Test
   public void testDoWork_persist() throws Exception {
      Map<String, String> fromStateToStateMap = new HashMap<>();
      fromStateToStateMap.put("Endorse", "NewEndorse");

      List<AbstractWorkflowArtifact> workflows = new ArrayList<>();
      Artifact teamWf = ArtifactTypeManager.addArtifact(AtsArtifactTypes.TeamWorkflow,
         AtsApiService.get().getAtsBranch(), "ConvertWorkflowStatesOperationTest.testDoWork_persist");
      teamWf.addAttribute(AtsAttributeTypes.CurrentStateName, "Endorse");
      workflows.add((AbstractWorkflowArtifact) teamWf);

      boolean persist = true;
      XResultData rd = new XResultData(false);

      ConvertWorkflowStatesOperation operation =
         new ConvertWorkflowStatesOperation(fromStateToStateMap, workflows, persist, rd);
      Operations.executeWorkAndCheckStatus(operation);

      Assert.assertFalse(rd.isErrors());
      Assert.assertEquals("NewEndorse", teamWf.getSoleAttributeValue(AtsAttributeTypes.CurrentStateName, ""));

      Assert.assertFalse(teamWf.isDirty());
   }

}
