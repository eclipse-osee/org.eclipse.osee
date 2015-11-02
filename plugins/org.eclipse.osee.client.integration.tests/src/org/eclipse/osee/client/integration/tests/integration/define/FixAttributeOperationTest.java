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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.define.blam.operation.FixAttributeOperation;
import org.eclipse.osee.define.blam.operation.FixAttributeOperation.Display;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Angel Avila
 */

public class FixAttributeOperationTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private static final String WORKING_BRANCH_NAME = "BranchWorking";

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private OperationLogger logger;
   @Mock private Display display;

   @Captor private ArgumentCaptor<List<String[]>> captor;
   @Captor private ArgumentCaptor<List<String[]>> captor2;

   // @formatter:on

   private IOseeBranch branchWorking;
   private String itemId;

   @Before
   public void setUp() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);

      branchWorking = BranchManager.createWorkingBranch(SAW_Bld_1, WORKING_BRANCH_NAME);
      BranchId branch1 = editBranch(branchWorking, "branch1");
      BranchId branch2 = editBranch(branchWorking, "branch2");

      commit(branch1, branchWorking);
      commit(branch2, branchWorking);
   }

   @After
   public void tearDown() throws OseeCoreException {
      boolean isPending = OseeEventManager.getPreferences().isPendRunning();
      try {
         OseeEventManager.getPreferences().setPendRunning(true);
         Operations.executeWorkAndCheckStatus(new PurgeBranchHttpRequestOperation(branchWorking, true));
      } finally {
         OseeEventManager.getPreferences().setPendRunning(isPending);
      }
   }

   @Test
   public void testNullBranchCheck() throws OseeCoreException {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("branch cannot be null");

      executeOp(null, false);
   }

   @Test
   public void testNonWorkingBranchCheck() throws OseeCoreException {
      Branch mockBranch = Mockito.mock(Branch.class);
      when(mockBranch.toString()).thenReturn("mock branch");
      when(mockBranch.getBranchType()).thenReturn(BranchType.BASELINE);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Invalid branch selected [mock branch]. Only working branches are allowed.");

      executeOp(mockBranch, false);
   }

   @Test
   public void testDetectDuplicatesButDontFix() throws OseeCoreException {
      // test multiple runs without committing fixes
      for (int i = 0; i < 2; i++) {
         reset(display);

         executeOp(branchWorking, false);

         verify(display).displayReport(eq("Fix Duplicate Report"), captor.capture());

         List<String[]> data = captor.getValue();
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
   public void testTestFix() throws OseeCoreException {
      executeOp(branchWorking, true);

      verify(display).displayReport(eq("Fix Duplicate Report"), captor.capture());

      //@formatter:off
      assertRow(captor.getValue(), 0, branchWorking.getName(), itemId, "Robot API", CoreAttributeTypes.Partition.getName(), "Unspecified, Navigation, Navigation", "Unspecified, Navigation");
      //@formatter: on

      Artifact testRobotAPI =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.SoftwareRequirement, "Robot API", branchWorking);

      List<String> values = testRobotAPI.getAttributesToStringList(CoreAttributeTypes.Partition);
      Collections.sort(values);
      assertEquals("Navigation", values.get(0));
      assertEquals("Unspecified", values.get(1));

      // Run Again Empty Report should result
      reset(display);
      executeOp(branchWorking, true);

      verify(display).displayReport(eq("Fix Duplicate Report"), captor2.capture());

      String expectedString = "-- no duplicates found --";
      assertRow(captor2.getValue(), 0, expectedString, expectedString, expectedString, expectedString, expectedString, expectedString);
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

   private void executeOp(IOseeBranch branch, boolean commitChangesBool) throws OseeCoreException {
      IOperation operation = new FixAttributeOperation(logger, display, branch, commitChangesBool);
      Operations.executeWorkAndCheckStatus(operation);
   }

   private BranchId editBranch(BranchId parentBranch, String workingBranchName) throws OseeCoreException {
      String branchName = String.format("%s_%s", FixAttributeOperationTest.class.getSimpleName(), workingBranchName);
      Branch branch = BranchManager.createWorkingBranch(parentBranch, branchName);

      Artifact robotAPI =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.SoftwareRequirement, "Robot API", branch);
      robotAPI.addAttribute(CoreAttributeTypes.Partition, "Navigation");

      itemId = robotAPI.getGuid();

      SkynetTransaction transaction = TransactionManager.createTransaction(branch, "Adding Attribute");
      transaction.addArtifact(robotAPI);
      transaction.execute();
      return branch;
   }

   private void commit(BranchId source, BranchId destination) throws OseeCoreException {
      boolean archiveSourceBranch = false;
      boolean overwriteUnresolvedConflicts = true;
      ConflictManagerExternal conflictManager = new ConflictManagerExternal(destination, source);
      BranchManager.commitBranch(new NullProgressMonitor(), conflictManager, archiveSourceBranch,
         overwriteUnresolvedConflicts);
   }
}
