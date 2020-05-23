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

package org.eclipse.osee.framework.ui.skynet.renderer.imageDetection;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.render.WordImageChecker;
import org.eclipse.osee.framework.ui.skynet.render.WordmlPicture;
import org.eclipse.osee.framework.ui.skynet.render.imageDetection.WordImageCompare;
import org.junit.Assert;

/**
 * Test case for {@link WordImageCompare}.
 * 
 * @author Jeff C. Phillips
 */
public class WordImageCompareTest {
   private static final String A_MATCH = "support/A_Match.xml";
   private static final String B_MATCH = "support/B_Match.xml";
   private static final String B_NO_MATCH = "support/B_No_Match.xml";
   private static final String WMZ_MATCH = "support/WMZ_File.xml";
   private static final String WMZ_NO_MATCH = "support/WMZ_NO_MATCH.xml";

   @org.junit.Test
   public void testMatchedImages() throws Exception {
      Assert.assertTrue(compareFile(A_MATCH, B_MATCH));
   }

   @org.junit.Test
   public void testNoMatchChangedImages() throws Exception {
      Assert.assertFalse(compareFile(A_MATCH, B_NO_MATCH));
   }

   @org.junit.Test
   public void testWMZMatchedImages() throws Exception {
      Assert.assertTrue(compareFile(WMZ_MATCH, WMZ_MATCH));
   }

   @org.junit.Test
   public void testWMZNoMatchImages() throws Exception {
      Assert.assertFalse(compareFile(WMZ_MATCH, WMZ_NO_MATCH));
   }

   private boolean compareFile(String firstFileName, String secondFileName) throws IOException {
      boolean isEqual = false;
      String firstFile = Lib.fileToString(getClass(), firstFileName);
      String secondFile = Lib.fileToString(getClass(), secondFileName);

      List<WordmlPicture> firstFileImages = createPictureList(firstFile);
      List<WordmlPicture> secondFileImages = createPictureList(secondFile);
      WordImageCompare compare = new WordImageCompare();

      if (firstFileImages.size() > 0 && secondFileImages.size() == secondFileImages.size()) {
         isEqual = true;
         for (int i = 0; i < firstFileImages.size(); i++) {
            isEqual &=
               compare.compareFiles(firstFileImages.get(i).getBinaryData(), secondFileImages.get(i).getBinaryData());
         }
      }
      return isEqual;
   }

   private static List<WordmlPicture> createPictureList(String wordml) {
      int startIndex = 0;
      List<WordmlPicture> pictures = new LinkedList<>();
      while (wordml.indexOf("<w:pict>", startIndex) > 0) {
         int currentStartIndex = wordml.indexOf("<w:pict>", startIndex);
         int currentEndIndex = wordml.indexOf("</w:pict", currentStartIndex);
         if (currentEndIndex > 0) {
            try {
               pictures.add(new WordmlPicture(currentStartIndex, wordml.substring(currentStartIndex, currentEndIndex),
                  wordml, null));
            } catch (OseeCoreException ex) {
               OseeLog.log(WordImageChecker.class, Level.WARNING, ex);
            }
         }
         startIndex = currentEndIndex;
      }
      return pictures;
   }
}