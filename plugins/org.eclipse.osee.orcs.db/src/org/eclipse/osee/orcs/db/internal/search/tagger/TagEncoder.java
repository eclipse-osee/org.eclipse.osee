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

/**
 * Encodes text tokens into 64-bit hash values for storage in the search tags table. Uses a high-quality hash function
 * to eliminate collisions that existed in the previous bit-packing approach.
 *
 * @author Roberto E. Escobar
 */
public class TagEncoder {

   private static final long FNV1A_64_OFFSET_BASIS = 0xcbf29ce484222325L;
   private static final long FNV1A_64_PRIME = 0x100000001b3L;

   /**
    * Encode a text token into a 64-bit hash tag. Each unique word produces a unique tag (collision probability is
    * negligible with 64-bit hash space). This replaces the previous bit-packing approach which had collision bugs due to
    * 5-bit values being packed into 4-bit slots.
    */
   public void encode(String text, TagCollector collector) {
      if (text == null || text.isEmpty()) {
         return;
      }
      String normalized = text.toLowerCase();
      long hash = fnv1a64(normalized);
      collector.addTag(text, hash);
   }

   private static long fnv1a64(String text) {
      long hash = FNV1A_64_OFFSET_BASIS;
      for (int i = 0; i < text.length(); i++) {
         hash ^= text.charAt(i);
         hash *= FNV1A_64_PRIME;
      }
      return hash;
   }

   public static final void main(String[] args) {
      new TagEncoder().encode("1", new TagCollector() {

         @Override
         public void addTag(String word, Long codedTag) {
            System.out.printf("%s %s\n", word, codedTag);
         }
      });
   }
}
