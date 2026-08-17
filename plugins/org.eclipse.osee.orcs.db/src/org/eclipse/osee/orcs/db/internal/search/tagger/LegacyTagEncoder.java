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

package org.eclipse.osee.orcs.db.internal.search.tagger;

import java.util.Arrays;

/**
 * Original bit-packing encoder preserved for dual-write compatibility during the transition period. Produces the same
 * multi-row tags that the legacy release search handler expects to find in osee_search_tags.
 * <p>
 * Remove this class once all release tracks have migrated to hash-based search via osee_search_tags_hash.
 *
 * @author Roberto E. Escobar
 */
public class LegacyTagEncoder {

   private static final char[] tagChars = new char[] {
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
      'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'l',
      'm', 'n', 'o', 'p', 'r', 's', 't', 'u', 'v', 'w', 'y'};

   /**
    * Create bit-packed tags that fit in 64-bit integers. Each tag represents up to 12 characters (60 bits at 4 bits per
    * character). Longer words produce multiple consecutive tags.
    */
   public void encode(String text, TagCollector collector) {
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
               tagBits |= 0x3F << (long) tagBitsPos;
            } else {
               tagBits |= pos << (long) tagBitsPos;
            }
            tagBitsPos += 4;
         }
      }
      if (tagBits != 0) {
         collector.addTag(text, tagBits);
      }
   }
}
