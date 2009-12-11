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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.eclipse.osee.framework.skynet.core.utility.CsvArtifact;
import org.eclipse.osee.support.test.util.DemoSawBuilds;

/**
 * @author Donald G. Dunne
 */
public class CsvArtifactTest {

   private static String id = "org.csv.artifact.test";
   private static String csvData = "Name, Value1, Value2\narf,1,3\nbarn,3,5";
   private static String appendData = "snarf,6,3";

   @org.junit.Test
   public void testCleanupPre() throws Exception {
      cleanup();
   }

   @org.junit.Test
   public void testCreateCsvArtifact() throws Exception {
      CsvArtifact csv = CsvArtifact.getCsvArtifact(id, BranchManager.getBranch(DemoSawBuilds.SAW_Bld_1), true);
      assertEquals(csv.getCsvData(), "");
      csv.getArtifact().setName(id);
      csv.setCsvData(csvData);
      csv.getArtifact().persist();
   }

   @org.junit.Test
   public void testgetCsvArtifactAndAppendData() throws Exception {
      CsvArtifact csvArt = CsvArtifact.getCsvArtifact(id, BranchManager.getBranch(DemoSawBuilds.SAW_Bld_1), false);
      assertNotNull(csvArt);
      assertEquals(csvData, csvArt.getCsvData());
      csvArt.appendData(appendData);
      csvArt.getArtifact().persist();
   }

   @org.junit.Test
   public void testCsvGetData() throws Exception {
      CsvArtifact csvArt = CsvArtifact.getCsvArtifact(id, BranchManager.getBranch(DemoSawBuilds.SAW_Bld_1), false);
      assertNotNull(csvArt);
      assertEquals(csvData + "\n" + appendData, csvArt.getCsvData());
   }

   @org.junit.Test
   public void testCleanupPost() throws Exception {
      cleanup();
   }

   private void cleanup() throws Exception {
      Collection<Artifact> arts =
            ArtifactQuery.getArtifactListFromName(id, BranchManager.getBranch(DemoSawBuilds.SAW_Bld_1), false);
      FrameworkTestUtil.purgeArtifacts(arts);
   }
}
