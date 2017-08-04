/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.util;

import java.io.File;
import java.io.IOException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.model.change.CompareData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class VbaWordDiffGeneratorTest {

   private static final String TEST_WORD_EDIT_FILE_NAME = "VbaWordDiffWithTrackedChanges.xml";
   private static final String EXPECTED_WORD_DIFF_FILE_NAME = "VbaWordDiffExpected.xml";
   private static final String EXPECTED_VBS_SCRIPT = "VbaWordDiffScript.vbs";

   @Rule
   public TemporaryFolder tempFolder = new TemporaryFolder();

   @Test
   public void testCompareWithTrackChanges() throws Exception {
      File tmpFile1 = tempFolder.newFile("File1.xml");
      File tmpFile2 = tempFolder.newFile("File2.xml");
      File diffFile = tempFolder.newFile("Diff.xml");
      File scriptFile = tempFolder.newFile("script.vbs");

      String content = getContent(TEST_WORD_EDIT_FILE_NAME);
      Lib.writeStringToFile(content, tmpFile1);

      content = content.replace("methods", "functions");
      Lib.writeStringToFile(content, tmpFile2);

      CompareData compareData = new CompareData(diffFile.getAbsolutePath(), scriptFile.getAbsolutePath());
      compareData.add(tmpFile1.getAbsolutePath(), tmpFile2.getAbsolutePath());

      // Can only execute VBS file on windows
      boolean executeVbs = Lib.isWindows();

      VbaWordDiffGenerator diff = new VbaWordDiffGenerator(false, false, false, executeVbs, false, true);
      IProgressMonitor monitor = new NullProgressMonitor();
      diff.generate(monitor, compareData);

      String actualVbs = Lib.fileToString(scriptFile);
      String expectedVbs =
         getExpectedVbs(tmpFile1.getAbsolutePath(), tmpFile2.getAbsolutePath(), diffFile.getAbsolutePath());
      Assert.assertEquals(expectedVbs, actualVbs);

      if (executeVbs) {
         String actualWordDiff = Lib.fileToString(diffFile);
         actualWordDiff = removeDynamicInformation(actualWordDiff);

         String expectedWordDiff = getContent(EXPECTED_WORD_DIFF_FILE_NAME);
         Assert.assertEquals(expectedWordDiff, actualWordDiff);
      }
   }

   private String getExpectedVbs(String file1, String file2, String output) throws IOException {
      String expectedVbs = getContent(EXPECTED_VBS_SCRIPT);
      expectedVbs = expectedVbs.replace("##SRC_FILE1##", file1);
      expectedVbs = expectedVbs.replace("##SRC_FILE2##", file2);
      expectedVbs = expectedVbs.replace("##DIFF_OUTPUT##", output);
      return expectedVbs;
   }

   private String removeDynamicInformation(String theWordXML) {
      String theReturn = theWordXML;
      int iHeaderEnd = theReturn.indexOf("<w:t>The");
      if (iHeaderEnd > -1) {
         theReturn = theReturn.substring(iHeaderEnd);
      }
      int theDate = theReturn.indexOf("createdate=\"");
      while (theDate != -1) {
         int theClose = theReturn.substring(theDate + 12).indexOf('"');
         theReturn = theReturn.substring(0, theDate) + theReturn.substring(theClose + theDate + 13);
         theDate = theReturn.indexOf("createdate=\"");
      }
      int theRsid = theReturn.indexOf("rsidRDefault=\"");
      while (theRsid != -1) {
         int theClose = theReturn.substring(theRsid + 14).indexOf('"');
         theReturn = theReturn.substring(0, theRsid) + theReturn.substring(theClose + theRsid + 15);
         theRsid = theReturn.indexOf("rsidRDefault=\"");
      }

      int iTrailerEnd = theReturn.indexOf("OSEE_EDIT_END");
      if (iTrailerEnd > -1) {
         theReturn = theReturn.substring(0, iTrailerEnd + 13);
      }
      return theReturn;
   }

   private String getContent(String resourceName) throws IOException {
      String file = String.format("support/%s", resourceName);
      String content = Lib.fileToString(getClass(), file);
      return content;
   }

}
