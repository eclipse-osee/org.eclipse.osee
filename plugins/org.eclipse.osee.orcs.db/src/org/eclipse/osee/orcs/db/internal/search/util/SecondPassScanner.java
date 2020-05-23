/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.db.internal.search.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * This class was created instead of using java.util.Scanner only due to the fact that the locations returned from the
 * Scanner's match() function are relative to the Scanner's internal buffer. There are no methods provided to determine
 * how many bytes have been read to get an offset.
 * 
 * @author John Misinco
 * @author Mark Joy
 */
public final class SecondPassScanner {

   private InputStream input;
   private final QueryOption delimiter;
   private String next;
   private int start, end = -1, bytesRead = 0;
   private boolean eof = false;
   private final StringBuilder buffer = new StringBuilder();

   public SecondPassScanner(InputStream input, QueryOption delimiter) {
      this.input = input;
      this.delimiter = delimiter;
   }

   public SecondPassScanner(String input, QueryOption delimiter) {
      try {
         this.input = Lib.stringToInputStream(input);
      } catch (UnsupportedEncodingException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }

      this.delimiter = delimiter;
   }

   /**
    * <pre>
    *    UTF-8 definition:
    *    00..7F - plain old ASCII
    *    80..BF - non-initial bytes of multibyte code
    *    C2..FD - initial bytes of multibyte code (C0, C1 are not legal!)
    *    FE, FF - never used (so, free for byte-order marks).
    * </pre>
    */
   private boolean isMultibyteStart(int read) {
      return read >= 0xC2 && read <= 0xFD;
   }

   private boolean isMultibyte(int read) {
      return read >= 0x80 && read <= 0xBF;
   }

   public boolean hasNext() {
      int read = -1;
      buffer.delete(0, buffer.length());
      while (!eof) {
         try {
            read = input.read();
         } catch (IOException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }

         if (!isMultibyte(read)) { // increment byte count for anything that's not a non-initial multibyte char
            if (read == -1) {
               eof = true;
            } else {
               bytesRead++;
            }
            if (!isMultibyteStart(read) && processChar(read)) { // only process chars that are not multibyte
               break;
            }
         }
      }

      next = buffer.toString();
      return Strings.isValid(next);
   }

   private boolean processChar(int read) {
      boolean done = false;
      switch (delimiter) {
         case TOKEN_DELIMITER__ANY:
            if (Character.isLetterOrDigit(read)) {
               buffer.append((char) read);
            } else {
               if (buffer.length() != 0) {
                  end = start + buffer.length();
                  done = true;
               }
            }
            break;
         case TOKEN_DELIMITER__EXACT:
            if (read != -1) {
               end = bytesRead;
               buffer.append((char) read);
            }
            done = true;
            break;
         case TOKEN_DELIMITER__WHITESPACE:
            if (Character.isWhitespace((char) read) || read == -1) {
               if (buffer.length() != 0) {
                  end = start + buffer.length();
                  done = true;
               }
            } else {
               buffer.append((char) read);
            }
            break;
         default:
            break;
      }

      if (buffer.length() == 1) {
         start = bytesRead - 1;
      }

      return done;
   }

   public String next() {
      return next;
   }

   public MatchLocation match() {
      return new MatchLocation(start, end);
   }

   public void close() {
      Lib.close(input);
   }
}