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
package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import junit.framework.Assert;
import org.eclipse.osee.client.demo.DemoBranches;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.render.compare.CompareData;
import org.eclipse.osee.framework.ui.skynet.util.VbaWordDiffGenerator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Megumi Telles
 */
public class WordTrackedChangesTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TemporaryFolder tempFolder = new TemporaryFolder();

   private static final String TEST_WORD_EDIT_FILE_NAME = "support/WordTrackedChangesTest.xml";
   private static final String EXPECTED_WORD_DIFF_FILE_NAME = "support/DiffExpected.xml";

   @Test
   public void testFindTrackChanges() throws Exception {
      String content = Lib.fileToString(getClass(), TEST_WORD_EDIT_FILE_NAME);
      assertTrue(WordUtil.containsWordAnnotations(content));
   }

   @Test
   public void testRemoveTrackChanges() throws Exception {
      String content = Lib.fileToString(getClass(), TEST_WORD_EDIT_FILE_NAME);
      content = WordUtil.removeAnnotations(content);
      assertFalse(WordUtil.containsWordAnnotations(content));
   }

   @Test
   public void testWholeWordSaveWithTrackChanges() throws Exception {
      String content = Lib.fileToString(getClass(), TEST_WORD_EDIT_FILE_NAME);
      LinkType linkType = LinkType.OSEE_SERVER_LINK;
      Artifact newArt = null;
      try {
         newArt =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestProcedureWML, DemoBranches.SAW_Bld_1,
               getClass().getSimpleName());
         newArt.persist(getClass().getSimpleName());
         String unlinkedContent = WordMlLinkHandler.unlink(linkType, newArt, content);
         assertTrue(WordUtil.containsWordAnnotations(unlinkedContent));
      } finally {
         if (newArt != null) {
            newArt.purgeFromBranch();
         }
      }
   }

   @Test
   public void testCompareWithTrackChanges() throws Exception {
      File tmpFile1 = tempFolder.newFile("File1.xml");
      File tmpFile2 = tempFolder.newFile("File2.xml");
      File diffFile = tempFolder.newFile("Diff.xml");
      File scriptFile = tempFolder.newFile("script.vbs");
      String content = Lib.fileToString(getClass(), TEST_WORD_EDIT_FILE_NAME);
      Lib.writeStringToFile(content, tmpFile1);
      content = content.replace("methods", "functions");
      Lib.writeStringToFile(content, tmpFile2);
      CompareData compareData = new CompareData(diffFile.getAbsolutePath(), scriptFile.getAbsolutePath());
      compareData.add(tmpFile1.getAbsolutePath(), tmpFile2.getAbsolutePath());
      VbaWordDiffGenerator diff = new VbaWordDiffGenerator(false, false, false, true, false);
      diff.generate(compareData);
      String theDiff = Lib.fileToString(diffFile);
      theDiff = removeDynamicInformation(theDiff);
      String expected = Lib.fileToString(getClass(), EXPECTED_WORD_DIFF_FILE_NAME);
      Assert.assertEquals(expected, theDiff);
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
      return (theReturn);
   }
}