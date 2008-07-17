/*
 * Created on Jul 17, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
   private static final String START_WORDML_TEXT = "<w:t>";
   private static final String END_WORDML_TEXT = "</w:t>";
   private IReadHelper readHelper;

   public XmlTextInputStream(InputStream inputStream) {
      super(inputStream);
   }

   public XmlTextInputStream(String input) throws UnsupportedEncodingException {
      this(new ByteArrayInputStream(input.getBytes("UTF-8")));
   }

   private boolean isWordML() {
      boolean toReturn = false;
      try {
         mark(1000);
         byte[] buffer = new byte[1042];
         int index = 0;
         for (; index < buffer.length; index++) {
            if (available() > 0) {
               buffer[index] = (byte) readFromOriginalBuffer();
            } else {
               break;
            }
         }
         if (index > 0) {
            String header = new String(buffer).toLowerCase();
            if (header.contains("word.document") || header.contains("worddocument")) {
               toReturn = true;
            }
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      } finally {
         try {
            reset();
         } catch (IOException ex) {
            // Do Nothing
         }
      }
      return toReturn;
   }

   /* (non-Javadoc)
    * @see java.io.BufferedInputStream#read()
    */
   @Override
   public synchronized int read() throws IOException {
      if (readHelper == null) {
         readHelper = isWordML() ? new WordMlReadHelper() : new XmlReadHelper();
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
      private boolean partOfTag = false;

      public XmlReadHelper() {
         this.partOfTag = false;
      }

      public int process(int value) throws IOException {
         if ((char) value == '<') {
            this.partOfTag = true;
         }
         while (this.partOfTag && available() > 0) {
            value = readFromOriginalBuffer();
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
      private StringBuilder buffer;

      public WordMlReadHelper() {
         this.buffer = new StringBuilder();
         this.partOfTag = false;
         this.collect = false;
      }

      public int process(int value) throws IOException {
         if ((char) value == '<') {
            partOfTag = true;
            buffer.append((char) value);
         }
         while ((partOfTag || collect != true) && available() > 0) {
            value = readFromOriginalBuffer();
            if ((char) value == '<') {
               partOfTag = true;
            }
            if (partOfTag) {
               buffer.append((char) value);
            }
            if ((char) value == '>') {
               partOfTag = false;
               String tag = buffer.toString();
               if (tag.equals(START_WORDML_TEXT)) {
                  this.collect = true;
               } else if (tag.equals(END_WORDML_TEXT)) {
                  this.collect = false;
               }
               buffer.delete(0, buffer.length());
               value = ' ';
            }
         }
         if (available() <= 0) {
            value = -1;
         }
         return value;
      }
   }
}
