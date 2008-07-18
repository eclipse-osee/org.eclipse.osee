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
package org.eclipse.osee.framework.search.engine.utility;

import java.util.Arrays;

/**
 * @author Roberto E. Escobar
 */
public class TagEncoder {

   private static final char[] tagChars =
         new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
               'l', 'm', 'n', 'o', 'p', 'r', 's', 't', 'u', 'v', 'w', 'y'};

   /**
    * Create a bit-packed tag that will fit in a 64-bit integer that can provide an extremely quick search mechanism for
    * for the first pass. The second pass will do a full text search to provide more exact matches. The tag will
    * represent up to 12 characters (all that can be stuffed into 64-bits). Longer search tags will be turned into
    * consecutive search tags
    * 
    * @param insertParameters
    * @param attribute
    * @param text
    */
   public static void encode(String text, ITagCollector collector) {
      int tagBitsPos = 0;
      long tagBits = 0;
      for (int index = 0; index < text.length(); index++) {
         char c = text.charAt(index);

         if (c == '\t' || c == '\n' || c == '\r' || tagBitsPos == 60) {
            if (tagBitsPos > 10) {
               collector.addTag(text, tagBits);
            }
            tagBits = 0;
            tagBitsPos = 0;
         } else {
            if (c >= 'A' && c <= 'Z') {
               c += 32;
            }
            int pos = Arrays.binarySearch(tagChars, c);
            if (pos < 0) {
               tagBits |= 0x3F << tagBitsPos;
            } else {
               tagBits |= pos << tagBitsPos;
            }
            tagBitsPos += 4;
         }
      }
      if (tagBits != 0) {
         collector.addTag(text, tagBits);
      }
   }
}
