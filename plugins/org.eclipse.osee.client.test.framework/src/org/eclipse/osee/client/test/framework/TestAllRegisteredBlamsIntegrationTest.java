/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.client.test.framework;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import org.eclipse.osee.framework.core.util.result.XResultPageBase;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.BlamService;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * For all Registered BLAMs, this will run the test method in AbstractBlamTest to run generic tests like title,
 * description and XWidget rendering. It will also inject the widgets with dummy data and test that it's available
 * during run from the variableMap. This test is run automatically during all integration test suites and can be run
 * manually through the OSEE Navigator "Test All Registered BLAMs" in both demo and production environments.<br>
 * <br>
 * Additional tests, like running the operation on demo or production data should be added to the individual BlamTest
 * files.
 *
 * @author Donald G. Dunne
 */
@RunWith(Parameterized.class)
public class TestAllRegisteredBlamsIntegrationTest {

   private final AbstractBlam blam;
   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();
   private static boolean debug = false;
   private static XResultData rd;
   private final AbstractBlamTest blamTest;

   public TestAllRegisteredBlamsIntegrationTest(AbstractBlam blam, String name, String blamClassName, AbstractBlamTest blamTest, String blamPackageName) {
      this.blam = blam;
      this.blamTest = blamTest;
   }

   @BeforeClass
   public static void setup() {
      rd = new XResultData(TestAllRegisteredBlamsIntegrationTest.class.getSimpleName());
      rd.logf("\n\n");
   }

   @AfterClass
   public static void cleanup() throws IOException {
      if (debug) {
         String html = XResultPageBase.getReport(rd, TestAllRegisteredBlamsIntegrationTest.class.getSimpleName(),
            Manipulations.HTML_MANIPULATIONS, Manipulations.CONVERT_NEWLINES,
            Manipulations.ERROR_WARNING_HEADER).getManipulatedHtml();
         String fName = System.getProperty("user.home") + File.separator + "TestAllRegisteredBlams.html";
         File outFile = new File(fName);
         Lib.writeStringToFile(html, outFile);
         java.awt.Desktop.getDesktop().open(outFile);
      }
   }

   @Test
   public void test() throws Exception {
      // Used to log error for just this test
      XResultData thisRd = new XResultData();
      try {
         String simpleName = blam.getClass().getSimpleName();
         if (blamTest == null) {
            Assert.fail(String.format("All BLAMS must have Test; Can't find %sTest", simpleName));
         } else {
            blamTest.test(thisRd);
         }
      } catch (Exception ex) {
         thisRd.errorf("Exception %s", Lib.exceptionToString(ex));
      }
      // Only fail this test if thisRd has error; Allows other tests to succeed
      if (thisRd.isErrors()) {
         Assert.fail(thisRd.toString());
      }
      // Merge this results with master results
      rd.merge(thisRd);
   }

   @Parameters(name = "BLAM Test: {index} - {2} - {1} - {4}")
   public static Collection<Object[]> data() {
      return getData(packageLabel -> true);
   }

   /**
    * Builds the parameter set for a product-specific BLAM test class.
    *
    * @param packageMatcher selects BLAMs using their display package label
    */
   protected static Collection<Object[]> getData(Predicate<String> packageMatcher) {
      List<Object[]> data = new ArrayList<>();
      List<AbstractBlam> sortedBlams = new ArrayList<>();
      sortedBlams.addAll(BlamService.getBlams());
      sortedBlams.sort(new Comparator<AbstractBlam>() {

         @Override
         public int compare(AbstractBlam o1, AbstractBlam o2) {
            return o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName());
         }

      });
      for (AbstractBlam blam : sortedBlams) {
         String packageLabel = AbstractBlam.getPackageLabel(blam);
         boolean matchPackage = packageMatcher.test(packageLabel);
         if (matchPackage) {
            AbstractBlamTest blamTest = BlamService.getBlamsToTestMap().get(blam);
            if (blamTest == null) {
               System.err.println("No BLAM Test for  " + blam.getClass().getSimpleName());
            } else {
               data.add(new Object[] {blam, blam.getName(), blam.getClass().getSimpleName(), blamTest, packageLabel});
            }
         }
      }
      return data;
   }

}
