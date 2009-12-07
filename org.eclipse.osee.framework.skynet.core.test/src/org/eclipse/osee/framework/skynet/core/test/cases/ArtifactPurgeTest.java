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
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * This test is intended to be run against a demo database. It tests the purge logic by counting the rows of the version
 * and txs tables, createing artifacts, changing them and then purging them. If it works properly, all rows should be
 * equal.
 * 
 * @author Donald G. Dunne
 */
public class ArtifactPurgeTest {

   private static SevereLoggingMonitor monitorLog;
   private Map<String, Integer> preCreateArtifactsCount;
   private Map<String, Integer> postCreateArtifactsCount;
   private Map<String, Integer> postPurgeCount;

   private static final List<String> tables =
         Arrays.asList("osee_attribute", "osee_artifact", "osee_relation_link", "osee_tx_details", "osee_txs",
               "osee_artifact_version");

   @BeforeClass
   public static void testInitialize() throws Exception {
      monitorLog = TestUtil.severeLoggingStart();
   }

   /**
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse(TestUtil.isProductionDb());
      preCreateArtifactsCount = new HashMap<String, Integer>();
      postCreateArtifactsCount = new HashMap<String, Integer>();
      postPurgeCount = new HashMap<String, Integer>();
   }

   /**
    * @throws java.lang.Exception
    */
   @After
   public void tearDown() throws Exception {
      if (preCreateArtifactsCount != null) {
         preCreateArtifactsCount.clear();
         preCreateArtifactsCount = null;
      }
      if (postCreateArtifactsCount != null) {
         postCreateArtifactsCount.clear();
         postCreateArtifactsCount = null;
      }
      if (postPurgeCount != null) {
         postPurgeCount.clear();
         postPurgeCount = null;
      }
   }

   @AfterClass
   public static void testCleanup() throws Exception {
      TestUtil.severeLoggingEnd(monitorLog);
   }

   @org.junit.Test
   public void testPurgeArtifacts() throws Exception {
      // Count rows in tables prior to purge
      DbUtil.getTableRowCounts(preCreateArtifactsCount, tables);

      // Create some software artifacts      
      Branch branch = BranchManager.getBranch(DemoSawBuilds.SAW_Bld_2.name());
      SkynetTransaction transaction = new SkynetTransaction(branch, "Test purge artifacts");
      Collection<Artifact> softArts =
            FrameworkTestUtil.createSimpleArtifacts(Requirements.SOFTWARE_REQUIREMENT, 10, getClass().getSimpleName(),
                  branch);
      for (Artifact softArt : softArts) {
         softArt.persist(transaction);
      }
      transaction.execute();

      // make more changes to artifacts
      for (Artifact softArt : softArts) {
         softArt.addAttribute(StaticIdManager.STATIC_ID_ATTRIBUTE, getClass().getSimpleName());
         softArt.persist();
      }

      // Count rows and check that increased
      DbUtil.getTableRowCounts(postCreateArtifactsCount, tables);
      TestUtil.checkThatIncreased(preCreateArtifactsCount, postCreateArtifactsCount);

      // Purge
      for (Artifact art : softArts) {
         art.purgeFromBranch();
      }

      // Count rows and check that same as when began
      DbUtil.getTableRowCounts(postPurgeCount, tables);
      // TODO Looks like attributes created after initial artifact creation are not getting purged.  Needs Fix.
      TestUtil.checkThatEqual(preCreateArtifactsCount, postPurgeCount);

   }

}
