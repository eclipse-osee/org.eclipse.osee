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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author Roberto E. Escobar
 */
public class XmlTextInputStream extends BufferedInputStream {
   private static final String START_PARAGRAPH = "<w:p";
   private static final String STOP_PARAGRAPH = "</w:p";
   private static final String START_WORDML_TEXT = "<w:t>";
   private static final String END_WORDML_TEXT = "</w:t>";

   private IReadHelper readHelper;

   public XmlTextInputStream(InputStream inputStream) {
      super(inputStream);
   }

   public XmlTextInputStream(String input) throws UnsupportedEncodingException {
      this(new ByteArrayInputStream(input.getBytes("UTF-8")));
   }

   /* (non-Javadoc)
    * @see java.io.BufferedInputStream#read()
    */
   @Override
   public synchronized int read() throws IOException {
      if (readHelper == null) {
         readHelper = WordsUtil.isWordML(in) ? new WordMlReadHelper() : new XmlReadHelper();
      }
      return readHelper.process(super.read());
   }

   /* (non-Javadoc)
    * @see java.io.BufferedInputStream#read(byte[], int, int)
    */
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
      }
      return i;
   }

   private int readFromOriginalBuffer() throws IOException {
      return super.read();
   }

   private interface IReadHelper {
      public int process(int value) throws IOException;
   }

   private final class XmlReadHelper implements IReadHelper {
      private boolean partOfTag;
      private boolean isCarriageReturn;

      public XmlReadHelper() {
         this.partOfTag = false;
         this.isCarriageReturn = false;
      }

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
   }

   private final class WordMlReadHelper implements IReadHelper {
      private boolean partOfTag;
      private boolean collect;
      private boolean isCarriageReturn;
      private boolean isStartOfParagraph;
      private StringBuilder buffer;

      public WordMlReadHelper() {
         this.buffer = new StringBuilder();
         this.partOfTag = false;
         this.collect = false;
         this.isStartOfParagraph = false;
         this.isCarriageReturn = false;
      }

      public int process(int value) throws IOException {
         this.isStartOfParagraph = false;
         if ((char) value == '<') {
            partOfTag = true;
            buffer.append((char) value);
         }
         while ((partOfTag || isCarriageReturn || (isStartOfParagraph != true && collect != true)) && available() > 0) {
            value = readFromOriginalBuffer();
            if ((char) value == '<') {
               partOfTag = true;
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
               if (tag.equals(START_WORDML_TEXT)) {
                  this.collect = true;
               } else if (tag.equals(END_WORDML_TEXT)) {
                  this.collect = false;
               } else if (tag.startsWith(START_PARAGRAPH)) {
                  this.isStartOfParagraph = true;
               } else if (tag.startsWith(STOP_PARAGRAPH)) {
                  this.isStartOfParagraph = false;
               }
               buffer.delete(0, buffer.length());
               value = ' ';
               if (this.isStartOfParagraph != true && available() > 0) {
                  value = process(readFromOriginalBuffer());
               }
            }
         }
         if (available() <= 0) {
            value = -1;
         }
         return value;
      }
   }
}
