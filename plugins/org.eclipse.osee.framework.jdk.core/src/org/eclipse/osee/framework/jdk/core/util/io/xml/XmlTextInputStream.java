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

package org.eclipse.osee.framework.jdk.core.util.io.xml;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.ReservedCharacters;

/**
 * @author Roberto E. Escobar
 */
public class XmlTextInputStream extends BufferedInputStream {
   private static final String START_PARAGRAPH_REGEX = "<w:p( .+)?>";
   private static final String STOP_PARAGRAPH = "</w:p>";
   private static final String START_WORDML_TEXT_REGEX = "<w:t( .+)?>";
   private static final String END_WORDML_TEXT = "</w:t>";
   private static final String LINE_BREAK = "<w:br/>";
   private static final String TAB_REGEX = "<w:tab( .+)?/>";
   private static final char[] OSEE_LINK_BEGIN = "OSEE_LINK(".toCharArray();
   private static final char OSEE_LINK_END = ')';

   private IReadHelper readHelper;

   public XmlTextInputStream(InputStream inputStream) {
      super(inputStream);

   }

   public XmlTextInputStream(String input) throws UnsupportedEncodingException {
      this(new ByteArrayInputStream(input.getBytes("UTF-8")));
   }

   @Override
   public synchronized int read() throws IOException {
      if (readHelper == null) {
         readHelper = Lib.isWordML(in) ? new WordMlReadHelper() : new XmlReadHelper();
      }
      int value = readHelper.process(super.read());
      value = checkForSpecialCharacters(value);
      return value;
   }

   private int checkForSpecialCharacters(int value) throws IOException {
      char currChar = (char) value;
      if (currChar == '&' && available() > 0) {

         final int readLimit = 10;
         boolean needsReset = true;
         super.mark(readLimit);
         readHelper.saveState();
         try {
            StringBuilder specialCharBuffer = new StringBuilder();
            specialCharBuffer.append(currChar);
            int readCount = 0;
            while (currChar != ';' && readCount < readLimit && super.available() > 0) {
               currChar = (char) readHelper.process(super.read());
               specialCharBuffer.append(currChar);
               readCount++;
            }

            Character reserved = ReservedCharacters.toCharacter(specialCharBuffer.toString());
            if (reserved != null) {
               needsReset = false;
               value = reserved;
            }
         } finally {
            if (needsReset) {
               super.reset();
               readHelper.restoreState();
            }
         }
      }
      return value;
   }

   @Override
   public synchronized int read(byte[] b, int off, int len) throws IOException {
      if (b == null) {
         throw new NullPointerException();
      } else if (off < 0 || len < 0 || len > b.length - off) {
         throw new IndexOutOfBoundsException();
      } else if (len == 0) {
         return 0;
      }

      int c = this.read();
      if (c == -1) {
         return -1;
      }
      b[off] = (byte) c;

      int i = 1;
      try {
         for (; i < len; i++) {
            c = this.read();
            if (c == -1) {
               break;
            }
            b[off + i] = (byte) c;
         }
      } catch (IOException ee) {
         // Do Nothing
      }
      return i;
   }

   private int readFromOriginalBuffer() throws IOException {
      return super.read();
   }

   private interface IReadHelper {
      public int process(int value) throws IOException;

      public void saveState();

      public void restoreState() throws IOException;
   }

   private final class XmlReadHelper implements IReadHelper {
      private boolean partOfTag;
      private boolean isCarriageReturn;

      private boolean wasSaved;
      private boolean lastPartOfTag;
      private boolean lastIsCarriageReturn;

      public XmlReadHelper() {
         partOfTag = false;
         isCarriageReturn = false;

         wasSaved = false;
         lastPartOfTag = false;
         lastIsCarriageReturn = false;
      }

      @Override
      public int process(int value) throws IOException {
         if ((char) value == '<') {
            this.partOfTag = true;
         }

         while ((this.partOfTag || this.isCarriageReturn) && available() > 0) {
            value = readFromOriginalBuffer();
            if (value == '\r' || value == '\n') {
               this.isCarriageReturn = true;
            } else {
               this.isCarriageReturn = false;
            }
            if ((char) value == '>') {
               this.partOfTag = false;
               value = available() > 0 ? ' ' : -1;
            }
         }
         return value;
      }

      @Override
      public void restoreState() throws IOException {
         if (wasSaved) {
            partOfTag = lastPartOfTag;
            isCarriageReturn = lastIsCarriageReturn;
            wasSaved = false;
         } else {
            throw new IOException("Save state was not called before restore.");
         }
      }

      @Override
      public void saveState() {
         wasSaved = true;
         lastPartOfTag = partOfTag;
         lastIsCarriageReturn = isCarriageReturn;
      }
   }

   private final class WordMlReadHelper implements IReadHelper {
      private boolean partOfTag;
      private boolean collect;
      private boolean isCarriageReturn;
      private boolean isStartOfParagraph;
      private boolean isBreak;
      private final StringBuilder buffer;

      private boolean wasSaved;
      private boolean lastPartOfTag;
      private boolean lastCollect;
      private boolean lastIsCarriageReturn;
      private boolean lastIsStartOfParagraph;
      private int linkIdx;
      private boolean foundLink;

      public WordMlReadHelper() {
         buffer = new StringBuilder();
         partOfTag = false;
         collect = false;
         isStartOfParagraph = false;
         isCarriageReturn = false;
         isBreak = false;

         wasSaved = false;
         lastPartOfTag = false;
         lastCollect = false;
         lastIsStartOfParagraph = false;
         lastIsCarriageReturn = false;
         foundLink = false;
         linkIdx = 0;
      }

      @Override
      public int process(int value) throws IOException {
         isStartOfParagraph = false;
         isBreak = false;

         if ((char) value == '<') {
            partOfTag = true;
            buffer.append((char) value);
         } else {
            linkIdx = (char) value == OSEE_LINK_BEGIN[linkIdx] ? linkIdx + 1 : 0;
         }

         while ((partOfTag || isCarriageReturn || isStartOfParagraph != true && collect != true && isBreak != true && foundLink != true) && available() > 0) {
            value = readFromOriginalBuffer();
            if ((char) value == '<') {
               partOfTag = true;
            } else if ((char) value == OSEE_LINK_BEGIN[linkIdx++]) {
               // check if we've matched all characters for OSEE_LINK
               if (OSEE_LINK_BEGIN.length == linkIdx) {
                  foundLink = true;
                  linkIdx = 0;
               }
            } else {
               linkIdx = 0;
            }

            if (partOfTag) {
               buffer.append((char) value);
            }
            if (value == '\r' || value == '\n') {
               this.isCarriageReturn = true;
            } else {
               this.isCarriageReturn = false;
            }
            if ((char) value == '>') {
               partOfTag = false;
               String tag = buffer.toString();
               if (tag.matches(START_WORDML_TEXT_REGEX)) {
                  collect = true;
               } else if (tag.equals(END_WORDML_TEXT)) {
                  collect = false;
               } else if (tag.matches(START_PARAGRAPH_REGEX)) {
                  isStartOfParagraph = true;
               } else if (tag.startsWith(STOP_PARAGRAPH)) {
                  isStartOfParagraph = false;
               } else if (tag.startsWith(LINE_BREAK)) {
                  isBreak = true;
               } else if (tag.matches(TAB_REGEX)) {
                  isBreak = true;
               }

               buffer.delete(0, buffer.length());
               value = ' ';
               if (isStartOfParagraph != true && isBreak != true && available() > 0) {
                  value = readFromOriginalBuffer();
                  if ((char) value == '<') {
                     partOfTag = true;
                     buffer.append((char) value);
                  } else {
                     if ((char) value != OSEE_LINK_BEGIN[linkIdx++]) {
                        linkIdx = 0;
                     }
                  }
               }
            }
         }

         if (foundLink && (char) value == OSEE_LINK_END) {
            // end of OSEE_LINK
            foundLink = false;
            linkIdx = 0;
         }

         if (available() <= 0) {
            value = -1;
         }
         return value;
      }

      @Override
      public void restoreState() throws IOException {
         if (wasSaved) {
            partOfTag = lastPartOfTag;
            collect = lastCollect;
            isStartOfParagraph = lastIsStartOfParagraph;
            isCarriageReturn = lastIsCarriageReturn;
            wasSaved = false;
         } else {
            throw new IOException("Save state was not called before restore.");
         }
      }

      @Override
      public void saveState() {
         wasSaved = true;
         lastPartOfTag = partOfTag;
         lastCollect = collect;
         lastIsStartOfParagraph = isStartOfParagraph;
         lastIsCarriageReturn = isCarriageReturn;
      }
   }

}
