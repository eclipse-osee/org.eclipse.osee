/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.test.cases;

import static org.junit.Assert.assertFalse;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.Before;

/**
 * This test is intended to be run against a demo database. It tests the branch purge logic by counting the rows of the
 * version and txs tables, creating a branch, making changes and then purging the branch. If it works properly, all rows
 * should be equal.
 * 
 * @author Donald G. Dunne
 */
public class BranchPurgeTest {

   private final Map<String, Integer> preCreateCount = new HashMap<String, Integer>();
   private final Map<String, Integer> postCreateBranchCount = new HashMap<String, Integer>();
   private final Map<String, Integer> postPurgeCount = new HashMap<String, Integer>();
   List<String> tables =
         Arrays.asList("osee_attribute", "osee_artifact", "osee_relation_link", "osee_tx_details", "osee_txs",
               "osee_artifact_version");

   /**
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse(TestUtil.isProductionDb());
      cleanup();
   }

   @org.junit.Test
   public void testPurgeBranch() throws Exception {
      // Count rows in tables prior to purge
      DbUtil.getTableRowCounts(preCreateCount, tables);

      // create a new working branch
      Branch branch =
            BranchManager.createWorkingBranch(BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_2.name()),
                  getClass().getSimpleName(), UserManager.getUser(SystemUser.OseeSystem));

      TestUtil.sleep(4000);

      // create some software artifacts      
      SkynetTransaction transaction = new SkynetTransaction(branch);
      Collection<Artifact> softArts =
            FrameworkTestUtil.createSimpleArtifacts(Requirements.SOFTWARE_REQUIREMENT, 10, getClass().getSimpleName(),
                  branch);
      for (Artifact softArt : softArts) {
         softArt.persistAttributesAndRelations(transaction);
      }
      transaction.execute();

      // make more changes to artifacts
      for (Artifact softArt : softArts) {
         softArt.addAttribute(StaticIdManager.STATIC_ID_ATTRIBUTE, getClass().getSimpleName());
         softArt.persistAttributesAndRelations();
      }

      // Count rows and check that increased
      DbUtil.getTableRowCounts(postCreateBranchCount, tables);
      TestUtil.checkThatIncreased(preCreateCount, postCreateBranchCount);

      // Purge branch
      BranchManager.purgeBranch(branch);

      TestUtil.sleep(4000);

      // Count rows and check that same as when began
      DbUtil.getTableRowCounts(postPurgeCount, tables);
      // TODO looks like artifacts are not being removed when purge a branch
      TestUtil.checkThatEqual(preCreateCount, postPurgeCount);

   }

   @After
   public void testCleanupPost() throws Exception {
      cleanup();
   }

   private static void cleanup() throws Exception {
      FrameworkTestUtil.purgeWorkingBranches(Arrays.asList(BranchPurgeTest.class.getSimpleName()));
   }
}
