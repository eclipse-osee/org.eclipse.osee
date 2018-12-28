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
package org.eclipse.osee.framework.ui.skynet.render;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.imageDetection.WordImageCompare;

/**
 * @author Theron Virgin
 */
public class WordImageChecker {

   public static void restoreOriginalValue(Attribute<String> attr, Pair<String, Boolean> originalValue) {
      if (attr != null && originalValue != null) {
         attr.setValue(originalValue.getFirst());
         if (!originalValue.getSecond()) {
            attr.setNotDirty();
         }
      }
   }

   public static Pair<String, Boolean> checkForImageDiffs(Attribute<String> oldAttr, Attribute<String> newAttr) {
      String downArrow = "";
      try {
         downArrow = new String(new byte[] {(byte) 0xE2, (byte) 0x86, (byte) 0x93}, "UTF-8");
      } catch (UnsupportedEncodingException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      String MODIFIED_STRING =
         "<w:t>" + downArrow + " OSEE Detected Image Modification " + downArrow + "</w:t></w:r></w:p><w:p><w:r></w:r></w:p><w:p><w:r>";
      if (oldAttr != null && newAttr != null) {
         String oldValue = oldAttr.getValue();
         String newValue = newAttr.getValue();
         boolean attrDirty = newAttr.isDirty();
         String originalValue = new String(oldValue);
         List<WordmlPicture> oldPictures = createPictureList(oldValue, oldAttr);
         List<WordmlPicture> newPictures = createPictureList(newValue, newAttr);
         boolean modified = false;
         int count = 0;
         for (int y = 0; y < oldPictures.size(); y++) {
            if (y < newPictures.size() && oldPictures.get(y).getBinaryData() != null && newPictures.get(
               y).getBinaryData() != null) {
               WordImageCompare compare = new WordImageCompare();
               try {
                  if (!compare.compareFiles(oldPictures.get(y).getBinaryData(), newPictures.get(y).getBinaryData())) {
                     int index = oldPictures.get(y).getStartIndex() + MODIFIED_STRING.length() * count;
                     oldValue = oldValue.substring(0, index) + MODIFIED_STRING + oldValue.substring(index);
                     modified = true;
                     count++;
                     oldAttr.setValue(oldValue);
                  }
               } catch (IOException ex) {
                  OseeLog.log(WordImageChecker.class, Level.WARNING, ex);
               }
            }
         }
         if (modified) {
            return new Pair<>(originalValue, attrDirty);
         }
      }
      return null;
   }

   private static List<WordmlPicture> createPictureList(String wordml, Attribute<String> attribute) {
      int startIndex = 0;
      List<WordmlPicture> pictures = new LinkedList<>();
      while (wordml.indexOf("<w:pict>", startIndex) > 0) {
         int currentStartIndex = wordml.indexOf("<w:pict>", startIndex);
         int currentEndIndex = wordml.indexOf("</w:pict", currentStartIndex);
         if (currentEndIndex > 0) {
            try {
               pictures.add(new WordmlPicture(currentStartIndex, wordml.substring(currentStartIndex, currentEndIndex),
                  wordml, attribute));
            } catch (OseeCoreException ex) {
               OseeLog.log(WordImageChecker.class, Level.WARNING, ex);
            }
         }
         startIndex = currentEndIndex;
      }
      return pictures;
   }
}
