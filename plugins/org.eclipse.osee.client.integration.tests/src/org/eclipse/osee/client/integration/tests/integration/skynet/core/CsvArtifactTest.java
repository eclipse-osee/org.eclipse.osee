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

package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.Collection;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.CsvArtifact;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public class CsvArtifactTest {

   private static String id = "org.csv.artifact.test";
   private static String csvData = "Name, Value1, Value2\narf,1,3\nbarn,3,5";
   private static String appendData = "snarf,6,3";

   private static CsvArtifact csv;

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      if (csv == null) {
         csv = CsvArtifact.getCsvArtifact(id, DemoSawBuilds.SAW_Bld_2, true);
      }
      Collection<Artifact> arts = ArtifactQuery.getArtifactListFromName(id, DemoSawBuilds.SAW_Bld_2, EXCLUDE_DELETED);
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(arts));
   }

   @org.junit.Test
   public void testCreateCsvArtifact() throws Exception {
      assertEquals(csv.getCsvData(), "");
      csv.getArtifact().setName(id);
      csv.setCsvData(csvData);
      csv.getArtifact().persist(getClass().getSimpleName());
   }

   @org.junit.Test
   public void testgetCsvArtifactAndAppendData() throws Exception {
      assertNotNull(csv);
      assertEquals(csvData, csv.getCsvData());
      csv.appendData(appendData);
      csv.getArtifact().persist(getClass().getSimpleName());
   }

   @org.junit.Test
   public void testCsvGetData() throws Exception {
      assertNotNull(csv);
      assertEquals(csvData + "\n" + appendData, csv.getCsvData());
   }

}
