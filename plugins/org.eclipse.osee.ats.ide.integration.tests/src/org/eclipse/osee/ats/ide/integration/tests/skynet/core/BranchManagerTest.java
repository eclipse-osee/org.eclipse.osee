/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core;

import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeHousekeepingRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.MethodRule;

/**
 * @author Donald G. Dunne
 */
public class BranchManagerTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public MethodRule oseeHousekeepingRule = new OseeHousekeepingRule();

   @Rule
   public TestInfo testInfo = new TestInfo();

   @Test
   public void testBranchName() {
      String branchName = testInfo.getQualifiedTestName();
      String branchReNamed = String.format("%s - Renamed", branchName);
      BranchId testBranch = BranchManager.createWorkingBranch(DemoBranches.SAW_Bld_3, branchName);

      Assert.assertEquals(branchName, BranchManager.getBranchName(testBranch));

      BranchManager.setName(testBranch, branchReNamed);
      Assert.assertEquals(branchReNamed, BranchManager.getBranchName(testBranch));

      thrown.expect(BranchDoesNotExist.class);
      thrown.expectMessage(String.format("No branch exists with the name: [%s]", branchName));
      BranchManager.getBranch(branchName);
   }
}