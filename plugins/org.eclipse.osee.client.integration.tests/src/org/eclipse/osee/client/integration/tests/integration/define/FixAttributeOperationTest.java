/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.define;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.define.blam.operation.FixAttributeOperation;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.NullOperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.httpRequests.PurgeBranchHttpRequestOperation;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Angel Avila
 */
public class FixAttributeOperationTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   private static final String WORKING_BRANCH_NAME = "BranchWorking";
   private IOseeBranch branchWorking;
   private String itemId;
   private List<String[]> data;

   @Before
   public void setUp()  {
      branchWorking = BranchManager.createWorkingBranch(SAW_Bld_1, WORKING_BRANCH_NAME);
      BranchId branch1 = editBranch(branchWorking, "branch1");
      BranchId branch2 = editBranch(branchWorking, "branch2");

      commit(branch1, branchWorking);
      commit(branch2, branchWorking);
   }

   @After
   public void tearDown()  {
      boolean isPending = OseeEventManager.getPreferences().isPendRunning();
      try {
         OseeEventManager.getPreferences().setPendRunning(true);
         Operations.executeWorkAndCheckStatus(new PurgeBranchHttpRequestOperation(branchWorking, true));
      } finally {
         OseeEventManager.getPreferences().setPendRunning(isPending);
      }
   }

   @Test
   public void testNullBranchCheck() throws Exception {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("branch cannot be null");

      executeOp(null, false);
   }

   @Test
   public void testNonWorkingBranchCheck() throws Exception {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Invalid branch selected [Common]. Only working branches are allowed.");

      executeOp(CoreBranches.COMMON, false);
   }

   @Test
   public void testDetectDuplicatesButDontFix()  {
      // test multiple runs without committing fixes
      for (int i = 0; i < 2; i++) {
         executeOp(branchWorking, false);

         assertRow(data, 0, branchWorking.getName(), itemId, "Robot API", CoreAttributeTypes.Partition.getName(),
            "Unspecified, Navigation, Navigation", "Unspecified, Navigation");

         Artifact testRobotAPI =
            ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.SoftwareRequirement, "Robot API", branchWorking);

         List<String> values = testRobotAPI.getAttributesToStringList(CoreAttributeTypes.Partition);
         Collections.sort(values);
         assertEquals("Navigation", values.get(0));
         assertEquals("Navigation", values.get(1));
         assertEquals("Unspecified", values.get(2));
      }
   }

   @Test
   public void testTestFix()  {
      executeOp(branchWorking, true);
      assertRow(data, 0, branchWorking.getName(), itemId, "Robot API", CoreAttributeTypes.Partition.getName(),
         "Unspecified, Navigation, Navigation", "Unspecified, Navigation");

      Artifact testRobotAPI =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.SoftwareRequirement, "Robot API", branchWorking);

      List<String> values = testRobotAPI.getAttributesToStringList(CoreAttributeTypes.Partition);
      Collections.sort(values);
      assertEquals("Navigation", values.get(0));
      assertEquals("Unspecified", values.get(1));

      // Run Again Empty Report should result
      executeOp(branchWorking, true);

      String expectedString = "-- no duplicates found --";
      assertRow(data, 0, expectedString, expectedString, expectedString, expectedString, expectedString,
         expectedString);
   }

   private static void assertRow(List<String[]> data, int index, String... expecteds) {
      assertNotNull(data);
      assertTrue(data.size() > index);
      String[] actuals = data.get(index);
      assertEquals(expecteds.length, actuals.length);

      for (int i = 0; i < expecteds.length; i++) {
         assertEquals(expecteds[i], actuals[i]);
      }
   }

   private void executeOp(IOseeBranch branch, boolean commitChangesBool)  {
      IOperation operation = new FixAttributeOperation(NullOperationLogger.getSingleton(),
         (String reportName, List<String[]> values) -> data = values, branch, commitChangesBool);
      Operations.executeWorkAndCheckStatus(operation);
   }

   private BranchId editBranch(BranchId parentBranch, String workingBranchName)  {
      String branchName = String.format("%s_%s", FixAttributeOperationTest.class.getSimpleName(), workingBranchName);
      BranchId branch = BranchManager.createWorkingBranch(parentBranch, branchName);

      Artifact robotAPI =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.SoftwareRequirement, "Robot API", branch);
      robotAPI.addAttribute(CoreAttributeTypes.Partition, "Navigation");

      itemId = robotAPI.getIdString();

      SkynetTransaction transaction = TransactionManager.createTransaction(branch, "Adding Attribute");
      transaction.addArtifact(robotAPI);
      transaction.execute();
      return branch;
   }

   private void commit(BranchId source, BranchId destination)  {
      boolean archiveSourceBranch = false;
      boolean overwriteUnresolvedConflicts = true;
      ConflictManagerExternal conflictManager = new ConflictManagerExternal(destination, source);
      BranchManager.commitBranch(new NullProgressMonitor(), conflictManager, archiveSourceBranch,
         overwriteUnresolvedConflicts);
   }
}
