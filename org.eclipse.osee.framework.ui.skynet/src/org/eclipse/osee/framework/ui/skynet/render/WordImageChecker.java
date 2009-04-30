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

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;

/**
 * @author Theron Virgin
 */
public class WordImageChecker {

   public static void restoreOriginalValue(Attribute attr, Pair<String, Boolean> originalValue) throws OseeCoreException {
      if (attr != null && originalValue != null) {
         attr.setValue(originalValue.getKey());
         if (!originalValue.getValue()) {
            attr.setNotDirty();
         }
      }
   }

   public static Pair<String, Boolean> checkForImageDiffs(Attribute oldAttr, Attribute newAttr) throws OseeCoreException {
      String downArrow = "";
      try {
         downArrow = new String(new byte[] {(byte) 0xE2, (byte) 0x86, (byte) 0x93}, "UTF-8");
      } catch (UnsupportedEncodingException ex) {
      }
      String MODIFIED_STRING =
            "<w:t>" + downArrow + " OSEE Detected Image Modification " + downArrow + "</w:t></w:r></w:p><w:p><w:r></w:r></w:p><w:p><w:r>";
      if (oldAttr != null && newAttr != null) {
         String oldValue = (String) oldAttr.getValue();
         String newValue = (String) newAttr.getValue();
         boolean attrDirty = newAttr.isDirty();
         String originalValue = new String(oldValue);
         List<WordmlPicture> oldPictures = createPictureList(oldValue, oldAttr);
         List<WordmlPicture> newPictures = createPictureList(newValue, newAttr);
         boolean modified = false;
         int count = 0;
         for (int y = 0; y < oldPictures.size(); y++) {
            if (y < newPictures.size() && oldPictures.get(y).getBinaryData() != null && newPictures.get(y).getBinaryData() != null) {
               if (!oldPictures.get(y).getBinaryData().equals(newPictures.get(y).getBinaryData())) {
                  int index = oldPictures.get(y).getStartIndex() + (MODIFIED_STRING.length() * count);
                  oldValue = oldValue.substring(0, index) + MODIFIED_STRING + oldValue.substring(index);
                  modified = true;
                  count++;
                  oldAttr.setValue(oldValue);
               }
            }
         }
         if (modified) {
            return new Pair<String, Boolean>(originalValue, attrDirty);
         }
      }
      return null;
   }

   /**
    * @param oldValue
    * @return
    */
   private static List<WordmlPicture> createPictureList(String wordml, Attribute attribute) {
      int startIndex = 0;
      List<WordmlPicture> pictures = new LinkedList<WordmlPicture>();
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
