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
package org.eclipse.osee.framework.jdk.core.util;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

/**
 * @author Robert A. Fisher
 */
public final class Readers {

   /**
    * Forward a reader until just after the balanced close of the given xml element name. It is assumed that the
    * balanced open end of the element was just read off of the reader.
    *
    * @param reader The reader to pull the data from
    * @param appendable If supplied, all data read from the reader is appended to the appendable
    * @param elementName The name of the element, including the namespace if applicable
    * @throws IllegalArgumentException If reader is null
    * @throws IllegalArgumentException If elementName is null
    * @throws IllegalStateException If the balanced closing tag is not found before the reader is emptied
    */
   public static final void xmlForward(Reader reader, Appendable appendable, CharSequence elementName) throws IOException {
      if (reader == null) {
         throw new IllegalArgumentException("reader can not be null");
      }
      if (elementName == null) {
         throw new IllegalArgumentException("elementName can not be null");
      }

      final String CLOSE_TAG = "</" + elementName + ">";
      final String EMPTY_TAG = "<" + elementName + "/>";
      final String OPEN_TAG = "<" + elementName + ">";
      final String OPEN_TAG_WITH_ATTR = "<" + elementName + " ";
      final CharSequence[] TAGS = {CLOSE_TAG, EMPTY_TAG, OPEN_TAG, OPEN_TAG_WITH_ATTR};

      int elementDepthCount = 1;
      StringBuilder read = null;
      if (appendable != null) {
         read = new StringBuilder();
      }

      CharSequence stopTag;

      while (elementDepthCount > 0) {
         if ((stopTag = forward(reader, appendable, TAGS)) == null) {
            throw new IllegalStateException("end of reader met when expecting an end of a tag");
         }

         if (stopTag.equals(CLOSE_TAG)) {
            elementDepthCount--;
         } else if (stopTag.equals(OPEN_TAG)) {
            elementDepthCount++;
         } else if (stopTag.equals(OPEN_TAG_WITH_ATTR)) {
            if (forward(reader, (Appendable) read, ">") == null) {
               throw new IllegalStateException("end of reader met when expecting >");
            }

            if (read != null && !read.toString().endsWith("/>")) {
               elementDepthCount++;
            }

            if (appendable != null && read != null) {
               appendable.append(read);
               read.setLength(0);
            }
         } else if (stopTag.equals(EMPTY_TAG)) {
            // no effect on the stack count
         } else {
            throw new IllegalStateException("unexpected element returned");
         }
      }

   }

   /**
    * Forward a reader to just after the specified CharSequence.
    *
    * @return The sequence that was found which stopped the forwarding. A null is returned if no sequence was found
    * @throws IllegalArgumentException if any parameter is null
    * @throws IllegalArgumentException if any of the sequences elements are length zero
    */
   public static final CharSequence forward(Reader reader, CharSequence... sequences) throws IOException {
      return forward(reader, null, sequences);
   }

   /**
    * Forward a reader to just after the specified CharSequence. If an appendable is supplied then all characters
    * consumed from the reader will be appended to the appendable.
    *
    * @return The sequence that was found which stopped the forwarding. A null is returned if no sequence was found
    * @throws IllegalArgumentException if reader is null
    * @throws IllegalArgumentException if sequences is null
    * @throws IllegalArgumentException if any of the sequences elements are length zero
    */
   public static final CharSequence forward(Reader reader, Appendable appendable, CharSequence... sequences) throws IOException {
      if (reader == null) {
         throw new IllegalArgumentException("reader can not be null");
      }
      if (sequences == null) {
         throw new IllegalArgumentException("sequences can not be null");
      }
      if (sequences.length == 0) {
         throw new IllegalArgumentException("must provide at least one sequence");
      }

      // Precalculate all of the lengths and check for unacceptable data
      int[] lengths = new int[sequences.length];
      for (int x = 0; x < sequences.length; x++) {
         if (sequences[x] == null) {
            throw new IllegalArgumentException("character sequence can not be null");
         }

         lengths[x] = sequences[x].length();

         if (lengths[x] == 0) {
            throw new IllegalArgumentException("character sequence can not have length zero");
         }
      }

      int[] seqIndex = new int[sequences.length];
      Arrays.fill(seqIndex, 0);

      char[] buffer = new char[1];

      while (reader.read(buffer) != -1) {
         if (appendable != null) {
            appendable.append(buffer[0]);
         }

         for (int x = 0; x < sequences.length; x++) {
            // Check if the last value read is inline with the next valid character for the sequence
            if (sequences[x].charAt(seqIndex[x]) == buffer[0]) {
               seqIndex[x]++;
               // Check if the whole sequence has been detected in the stream
               if (seqIndex[x] == lengths[x]) {
                  return sequences[x];
               }
            } else {
               seqIndex[x] = 0;
            }
         }
      }

      return null;
   }
}
