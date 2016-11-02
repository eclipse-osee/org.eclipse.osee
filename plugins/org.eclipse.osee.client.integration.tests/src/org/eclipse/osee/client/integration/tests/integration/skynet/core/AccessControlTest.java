/*******************************************************************************
 * Copyright (c) 2017 Boeing.
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
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Megumi Telles
 */
public class AccessControlTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo testInfo = new TestInfo();

   private static Artifact artifact1;
   private static Artifact artifact2;
   private static Artifact artifact3;
   private static Artifact artifact4;
   private static Artifact artifact5;

   private static BranchId branch;

   @Test(expected = OseeStateException.class)
   public void testNoWriteOnReadAccessOnBranch() {
      AccessControlManager.setPermission(UserManager.getUser(), branch, PermissionEnum.READ);
      artifact1 = new Artifact(branch, "New Name");
      artifact1.persist(testInfo.getTestName());
   }

   @Test(expected = OseeStateException.class)
   public void testNoWriteOnNoneAccessOnBranch() {
      AccessControlManager.setPermission(UserManager.getUser(), branch, PermissionEnum.NONE);
      artifact2 = new Artifact(branch, "New Name");
      artifact2.persist(testInfo.getTestName());
   }

   @Test
   public void testWriteAccessOnBranch() {
      AccessControlManager.setPermission(UserManager.getUser(), branch, PermissionEnum.WRITE);
      artifact3 = new Artifact(branch, "New Name");
      artifact3.persist(testInfo.getTestName());
   }

   @Test
   public void testWriteOnFullAccessOnBranch() {
      AccessControlManager.setPermission(UserManager.getUser(), branch, PermissionEnum.FULLACCESS);
      artifact4 = new Artifact(branch, "New Name");
      artifact4.persist(testInfo.getTestName());
   }

   @Test(expected = OseeStateException.class)
   public void testNoWriteOnDenyAccessOnBranch() {
      BranchId branch = BranchManager.createTopLevelBranch(testInfo.getTestName() + " branch");
      AccessControlManager.setPermission(UserManager.getUser(), branch, PermissionEnum.DENY);
      artifact5 = new Artifact(branch, "New Name");
      artifact5.persist(testInfo.getTestName());
   }

   @Before
   public void setUp() throws Exception {
      branch = BranchManager.createTopLevelBranch("AccessControlTestBranch");
   }

   @After
   public void tearDown() throws Exception {

      if (artifact1 != null) {
         artifact1.deleteAndPersist();
      }

      if (artifact2 != null) {
         artifact2.deleteAndPersist();
      }

      if (artifact3 != null) {
         artifact3.deleteAndPersist();
      }

      if (artifact4 != null) {
         artifact4.deleteAndPersist();
      }

      if (artifact5 != null) {
         artifact5.deleteAndPersist();
      }
      if (branch != null) {
         BranchManager.purgeBranch(branch);
      }
   }
}