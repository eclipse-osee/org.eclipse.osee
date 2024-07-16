/*********************************************************************
 * Copyright (c) 2017 Boeing
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

import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo testInfo = new TestInfo();

   private static Artifact artifact1;
   private static Artifact artifact2;
   private static Artifact artifact3;
   private static Artifact artifact4;
   private static Artifact artifact5;

   private static BranchToken branch;

   @Test(expected = OseeCoreException.class)
   public void testNoWriteOnReadAccessOnBranch() {
      ServiceUtil.getOseeClient().getAccessControlService().setPermission(UserManager.getUser(), branch,
         PermissionEnum.READ);
      artifact1 = new Artifact(branch, "New Name");
      artifact1.persist(testInfo.getTestName());
   }

   @Test(expected = OseeCoreException.class)
   public void testNoWriteOnNoneAccessOnBranch() {
      ServiceUtil.getOseeClient().getAccessControlService().setPermission(UserManager.getUser(), branch,
         PermissionEnum.NONE);
      artifact2 = new Artifact(branch, "New Name");
      artifact2.persist(testInfo.getTestName());
   }

   @Test
   public void testWriteAccessOnBranch() {
      ServiceUtil.getOseeClient().getAccessControlService().setPermission(UserManager.getUser(), branch,
         PermissionEnum.WRITE);
      artifact3 = new Artifact(branch, "New Name");
      artifact3.persist(testInfo.getTestName());
   }

   @Test
   public void testWriteOnFullAccessOnBranch() {
      ServiceUtil.getOseeClient().getAccessControlService().setPermission(UserManager.getUser(), branch,
         PermissionEnum.FULLACCESS);
      artifact4 = new Artifact(branch, "New Name");
      artifact4.persist(testInfo.getTestName());
   }

   @Test(expected = OseeCoreException.class)
   public void testNoWriteOnDenyAccessOnBranch() {
      BranchToken branch = BranchManager.createTopLevelBranch(testInfo.getTestName() + " branch");
      ServiceUtil.getOseeClient().getAccessControlService().setPermission(UserManager.getUser(), branch,
         PermissionEnum.DENY);
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
         artifact1.deleteAndPersist(getClass().getSimpleName());
      }

      if (artifact2 != null) {
         artifact2.deleteAndPersist(getClass().getSimpleName());
      }

      if (artifact3 != null) {
         artifact3.deleteAndPersist(getClass().getSimpleName());
      }

      if (artifact4 != null) {
         artifact4.deleteAndPersist(getClass().getSimpleName());
      }

      if (artifact5 != null) {
         artifact5.deleteAndPersist(getClass().getSimpleName());
      }
      if (branch != null) {
         BranchManager.purgeBranch(branch);
      }
   }
}
