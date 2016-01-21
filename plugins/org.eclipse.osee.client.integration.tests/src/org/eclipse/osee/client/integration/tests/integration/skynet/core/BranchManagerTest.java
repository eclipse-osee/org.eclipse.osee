/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Donald G. Dunne
 */
public class BranchManagerTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo testInfo = new TestInfo();

   public String branchName;
   public String branchReNamed;

   private IOseeBranch testBranch;

   @Before
   public void setUp() throws Exception {
      branchName = testInfo.getQualifiedTestName();
      branchReNamed = String.format("%s - Renamed", branchName);
      testBranch = BranchManager.createWorkingBranch(CoreBranches.COMMON, branchName);
   }

   @After
   public void tearDown() throws Exception {
      if (testBranch != null) {
         BranchManager.purgeBranch(testBranch);
      }
   }

   @Test
   public void testBranch() throws Exception {
      Assert.assertEquals(testBranch, BranchManager.getBranch(branchName));

      BranchManager.setName(testBranch, branchReNamed);

      testBranch = BranchManager.getBranch(BranchManager.getBranch(branchReNamed));
      Assert.assertEquals(branchReNamed, testBranch.getName());

      thrown.expect(BranchDoesNotExist.class);
      thrown.expectMessage(String.format("No branch exists with the name: [%s]", branchName));
      BranchManager.getBranch(branchName);
   }

}
