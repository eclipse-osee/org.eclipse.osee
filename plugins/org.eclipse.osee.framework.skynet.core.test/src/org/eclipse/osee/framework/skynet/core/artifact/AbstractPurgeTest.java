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
package org.eclipse.osee.framework.skynet.core.artifact;

import static org.junit.Assert.assertFalse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * This test is intended to be run against a demo database. It tests the purge logic by counting the rows of the version
 * and txs tables, creating artifacts, changing them and then purging them. If it works properly, all rows should be
 * equal.
 * 
 * @author Jeff C. Phillips
 * @author Donald G. Dunne
 */
public abstract class AbstractPurgeTest {

   private static SevereLoggingMonitor monitorLog;
   protected Map<String, Integer> preCreateArtifactsCount;
   protected Map<String, Integer> postCreateArtifactsCount;
   protected Map<String, Integer> postPurgeCount;

   @BeforeClass
   public static void testInitialize() throws Exception {
      monitorLog = TestUtil.severeLoggingStart();
   }

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse(TestUtil.isProductionDb());
      preCreateArtifactsCount = new HashMap<String, Integer>();
      postCreateArtifactsCount = new HashMap<String, Integer>();
      postPurgeCount = new HashMap<String, Integer>();
   }

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
   public void testPurge() throws Exception {
      runPurgeOperation();

      // TODO Looks like attributes created after initial artifact creation are not getting purged.  Needs Fix.
      TestUtil.checkThatEqual(preCreateArtifactsCount, postPurgeCount);
   }

   protected void getPreTableCount() throws OseeCoreException {
      // Count rows in tables prior to purge
      DbUtil.getTableRowCounts(preCreateArtifactsCount, getTables());
   }

   protected void getPostTableCount() throws OseeCoreException {
      // Count rows and check that same as when began
      DbUtil.getTableRowCounts(postPurgeCount, getTables());
   }

   public abstract void runPurgeOperation() throws OseeCoreException;

   public abstract List<String> getTables();

}
