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
package org.eclipse.osee.framework.skynet.core.test2.cases;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.test2.util.FrameworkTestUtil;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * This test is intended to be run against a demo database. It tests the purge logic by counting the rows of the version
 * and txs tables, then adds an Action, Workflow and 30 Tasks, deletes these objects and compares the row count. If
 * purge works properly, all rows should be equal. This test is intended to be run against a demo database. It tests the
 * purge logic by counting the rows of the version and txs tables, createing artifacts, changing them and then purging
 * them. If it works properly, all rows should be equal.
 * 
 * @author Donald G. Dunne
 */
public class ArtifactPurgeTest extends TestCase {

   private final Map<String, Integer> preCreateActionCount = new HashMap<String, Integer>();
   private final Map<String, Integer> postCreateActionCount = new HashMap<String, Integer>();
   private final Map<String, Integer> postPurgeCount = new HashMap<String, Integer>();
   List<String> tables =
         Arrays.asList("osee_attribute", "osee_artifact", "osee_relation_link", "osee_tx_details", "osee_txs",
               "osee_artifact_version");

   /**
    * @throws java.lang.Exception
    */
   @Override
   protected void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse(TestUtil.isProductionDb());
   }

   public void testPurgeArtifacts() throws Exception {
      // Count rows in tables prior to purge
      DbUtil.getTableRowCounts(preCreateActionCount, tables);

      Set<Artifact> artsToPurge = new HashSet<Artifact>();

      // Create some software artifacts      
      Branch branch = BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_2.name());
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
         softArt.persistAttributesAndRelations(transaction);
      }

      // Count rows and check that increased
      DbUtil.getTableRowCounts(postCreateActionCount, tables);
      TestUtil.checkThatIncreased(preCreateActionCount, postCreateActionCount);

      // Purge Action, Workflow and Tasks
      ArtifactPersistenceManager.purgeArtifacts(artsToPurge);

      // Count rows and check that same as when began
      DbUtil.getTableRowCounts(postPurgeCount, tables);
      // TODO Looks like attributes created after initial artifact creation are not getting purged.  Needs Fix.
      TestUtil.checkThatEqual(preCreateActionCount, postPurgeCount);

   }

   public void testPurgeArtifactFromBranch() throws Exception {
      // Count rows in tables prior to purge
      DbUtil.getTableRowCounts(preCreateActionCount, tables);

      // Create some software artifacts      
      Branch branch = BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_2.name());
      SkynetTransaction transaction = new SkynetTransaction(branch);
      Artifact softArt =
            FrameworkTestUtil.createSimpleArtifact(Requirements.SOFTWARE_REQUIREMENT, getClass().getSimpleName(),
                  branch);
      softArt.persistAttributesAndRelations(transaction);
      transaction.execute();

      // make more changes to artifact
      softArt.addAttribute(StaticIdManager.STATIC_ID_ATTRIBUTE, getClass().getSimpleName());
      softArt.persistAttributesAndRelations(transaction);

      // Count rows and check that increased
      DbUtil.getTableRowCounts(postCreateActionCount, tables);
      TestUtil.checkThatIncreased(preCreateActionCount, postCreateActionCount);

      // Purge artifact
      ArtifactPersistenceManager.purgeArtifactFromBranch(OseeDbConnection.getConnection(), branch.getBranchId(),
            softArt.getArtId());

      // Count rows and check that same as when began
      DbUtil.getTableRowCounts(postPurgeCount, tables);
      // TODO Looks like attributes created after initial artifact creation are not getting purged.  Needs Fix.
      TestUtil.checkThatEqual(preCreateActionCount, postPurgeCount);
   }

}
