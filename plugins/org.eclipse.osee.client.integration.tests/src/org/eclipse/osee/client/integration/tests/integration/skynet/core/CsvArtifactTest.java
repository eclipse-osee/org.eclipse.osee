/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import static org.junit.Assert.assertEquals;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.skynet.core.utility.CsvArtifact;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class CsvArtifactTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo testInfo = new TestInfo();

   private static String id = "org.csv.artifact.test";
   private static final String CSV_DATA = "Name, Value1, Value2\narf,1,3\nbarn,3,5";
   private static final String APPEND_DATA = "snarf,6,3";
   private static final String EXPECTED_APPEND_DATA = CSV_DATA + "\n" + APPEND_DATA;

   private CsvArtifact csv;

   @Before
   public void setup() throws Exception {
      csv = CsvArtifact.getCsvArtifact(id, SAW_Bld_2, true);
   }

   @After
   public void tearDown() throws Exception {
      if (csv != null) {
         csv.getArtifact().purgeFromBranch();
      }
   }

   @Test
   public void testCsvArtifact() throws Exception {
      assertEquals(csv.getCsvData(), "");

      csv.getArtifact().setName(id);
      csv.setCsvData(CSV_DATA);
      csv.getArtifact().persist(testInfo.getQualifiedTestName());

      assertEquals(CSV_DATA, csv.getCsvData());

      csv.appendData(APPEND_DATA);
      csv.getArtifact().persist(testInfo.getQualifiedTestName());

      assertEquals(EXPECTED_APPEND_DATA, csv.getCsvData());
   }

}
