/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.disposition.rest.importer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.DispoDataFactory;
import org.eclipse.osee.disposition.rest.internal.importer.TmzImporter;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author John Misinco
 */
public class TmzImporterTest {

   @Rule
   public TemporaryFolder folder = new TemporaryFolder();

   private final DispoDataFactory factory = new DispoDataFactory();

   private JaxRsApi jaxRsApi;

   @Before
   public void setUp() {
      DispoConnector connector = new DispoConnector();
      factory.setDispoConnector(connector);
      jaxRsApi = OsgiUtil.getService(getClass(), JaxRsApi.class);
   }

   @Test
   public void testImportWithCheckGroups() throws IOException {
      File tmzFile = folder.newFile("CheckGroup.tmz");
      Lib.inputStreamToFile(getClass().getResourceAsStream("CheckGroup.tmz"), tmzFile);
      TmzImporter importer = new TmzImporter(null, factory, jaxRsApi);
      OperationReport report = new OperationReport();
      List<DispoItem> results = importer.importDirectory(new HashMap<String, DispoItem>(), folder.getRoot(), report);
      Assert.assertEquals(1, results.size());
      DispoItem result = results.get(0);
      Assert.assertEquals("CheckGroup", result.getName());
      Assert.assertEquals(2, result.getDiscrepanciesList().size());
      Assert.assertEquals("113054", result.getVersion());
      boolean firstFound = false, thirdFound = false;
      for (String key : result.getDiscrepanciesList().keySet()) {
         Discrepancy discrepancy = result.getDiscrepanciesList().get(key);
         if (discrepancy.getLocation().equals("1")) {
            firstFound = true;
            Assert.assertEquals(
               "Failure at Test Point 1. Check Group with Checkpoint Failures: Check Point: CODE. Expected: 1500. Actual: NULL. Check Point: STATE. Expected: TRUE. Actual: NULL. Check Point: IBOT. Expected: FALSE. Actual: NULL. Check Point: BOT_CODE. Expected: 0. Actual: NULL. Check Point: FILTER_TIME. Expected: 10000. Actual: NULL. ",
               discrepancy.getText());
         } else if (discrepancy.getLocation().equals("3")) {
            thirdFound = true;
            Assert.assertEquals(
               "Failure at Test Point 3. Check Point: CheckPoint_BOT. Expected: ORANGE = FALSE. Actual: ORANGE = NULL. ",
               discrepancy.getText());
         }
      }
      Assert.assertTrue(firstFound);
      Assert.assertTrue(thirdFound);
   }

   @Test
   public void testImportNoCheckGroups() throws IOException {
      File tmzFile = folder.newFile("NoCheckGroup.tmz");
      Lib.inputStreamToFile(getClass().getResourceAsStream("NoCheckGroup.tmz"), tmzFile);
      TmzImporter importer = new TmzImporter(null, factory, jaxRsApi);
      OperationReport report = new OperationReport();
      List<DispoItem> results = importer.importDirectory(new HashMap<String, DispoItem>(), folder.getRoot(), report);
      Assert.assertEquals(1, results.size());
      DispoItem result = results.get(0);
      Assert.assertEquals("NoCheckGroup", result.getName());
      Assert.assertEquals(1, result.getDiscrepanciesList().size());
      Assert.assertEquals("113054", result.getVersion());
      boolean secondFound = false;
      for (String key : result.getDiscrepanciesList().keySet()) {
         Discrepancy discrepancy = result.getDiscrepanciesList().get(key);
         if (Integer.valueOf(discrepancy.getLocation()) == 2) {
            secondFound = true;
            Assert.assertEquals(
               "Failure at Test Point 2. Check Point: CheckPoint_BOT. Expected: ORANGE = FALSE. Actual: ORANGE = NULL. ",
               discrepancy.getText());
         }
      }
      Assert.assertTrue(secondFound);
   }

}
