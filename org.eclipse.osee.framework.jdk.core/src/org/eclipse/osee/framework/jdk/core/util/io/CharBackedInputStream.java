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
package org.eclipse.osee.framework.jdk.core.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.LinkedList;

/**
 * @author Ryan D. Brooks
 */
public class CharBackedInputStream extends InputStream implements Appendable {
   //don't change!!!! this is for java 1.4 compatability
   //private LinkedList<ByteBuffer> backers;
   private LinkedList<ByteBuffer> backers;
   private ByteBuffer currentBacker;
   private CharsetEncoder encoder;
   private Writer writer;

   /**
    * @author Ryan D. Brooks
    */
   public class InputStreamWriter extends Writer {

      public InputStreamWriter() {
         super();
      }

      /**
       * @param lock
       */
      public InputStreamWriter(Object lock) {
         super(lock);
      }

      /* (non-Javadoc)
       * @see java.io.Writer#write(char[], int, int)
       */
      public void write(char[] cbuf, int off, int len) throws IOException {
         addBackingSource(cbuf, off, len);
      }

      /* (non-Javadoc)
       * @see java.io.Flushable#flush()
       */
      public void flush() throws IOException {
      }

      /* (non-Javadoc)
       * @see java.io.Closeable#close()
       */
      public void close() throws IOException {
      }

      public void write(CharSequence str) throws CharacterCodingException {
         addBackingSource(str);
      }

      public void write(String str, int off, int len) throws CharacterCodingException {
         addBackingSource(str, off, len);
      }
   }

   /**
    * @throws CharacterCodingException
    */
   public CharBackedInputStream(CharBuffer source, String encodingName) throws CharacterCodingException {
      super();
      this.encoder = Charset.forName(encodingName).newEncoder();
      this.backers = new LinkedList<ByteBuffer>();
      if (source != null) {
         addBackingSource(source);
      }
   }

   public CharBackedInputStream() throws CharacterCodingException {
      this((CharBuffer) null, "UTF-8");
   }

   public CharBackedInputStream(CharSequence backingStr) throws CharacterCodingException {
      this(backingStr, "UTF-8");
   }

   public CharBackedInputStream(char[] backingChars) throws CharacterCodingException {
      this(backingChars, "UTF-8");
   }

   public CharBackedInputStream(char[] backingChars, String encodingName) throws CharacterCodingException {
      this(CharBuffer.wrap(backingChars), encodingName);
   }

   public CharBackedInputStream(CharSequence backingStr, String encodingName) throws CharacterCodingException {
      this(CharBuffer.wrap(backingStr), encodingName);
   }

   public void addBackingSource(CharSequence backingStr) throws CharacterCodingException {
      addBackingSource(CharBuffer.wrap(backingStr));
   }

   public void addBackingSource(CharSequence backingStr, int off, int len) throws CharacterCodingException {
      addBackingSource(CharBuffer.wrap(backingStr.subSequence(off, len + off)));
   }

   public void addBackingSource(char[] chars, int off, int len) throws CharacterCodingException {
      addBackingSource(CharBuffer.wrap(chars, off, len));
   }

   public void addBackingSource(char[] chars) throws CharacterCodingException {
      addBackingSource(CharBuffer.wrap(chars));
   }

   public void addBackingSource(CharBuffer source) throws CharacterCodingException {
      ByteBuffer buffer = encoder.encode(source);
      if (currentBacker == null) {
         currentBacker = buffer;
      } else {
         backers.add(buffer);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.io.InputStream#read()
    */
   public int read() throws IOException {
      if (currentBacker == null) {
         return -1;
      }
      try {
         return currentBacker.get();
      } catch (BufferUnderflowException ex) {
         currentBacker = backers.poll();
         return read();
      }
   }

   /**
    * @return Returns the writer.
    */
   public Writer getWriter() {
      if (writer == null) {
         writer = new InputStreamWriter();
      }
      return writer;
   }

   /* (non-Javadoc)
    * @see java.lang.Appendable#append(java.lang.CharSequence)
    */
   public Appendable append(CharSequence csq) throws IOException {
      addBackingSource(csq);
      return this;
   }

   /* (non-Javadoc)
    * @see java.lang.Appendable#append(java.lang.CharSequence, int, int)
    */
   public Appendable append(CharSequence csq, int start, int end) throws IOException {
      addBackingSource(csq, start, end - start);
      return this;
   }

   /* (non-Javadoc)
    * @see java.lang.Appendable#append(char)
    */
   public Appendable append(char c) throws IOException {
      throw new UnsupportedOperationException(
            "doing this one character at a time would be so inefficient it would defeat the whole purpose of this class");
   }
}