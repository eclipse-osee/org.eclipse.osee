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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.Arrays;

/**
 * @author Ryan D. Brooks
 */
public class CsvReader {
   private Reader reader;
   private StreamTokenizer streamTokenizer;
   private boolean[] fieldsUsed;
   private int fieldCount;
   private String[] nextRow;

   /**
    * @param file a comma separate value file
    * @param totalNumFields the largest number of fields on any row (whether they are used or not)
    * @param enabled whether to enable or disable all the feilds initially
    * @throws IOException
    */
   public CsvReader(File file, int totalNumFields, boolean enabled) throws IOException {
      this(new BufferedReader(new FileReader(file)), totalNumFields, enabled);
   }

   /**
    * All fields will be enabled initially
    * 
    * @param file a comma separate value file
    * @param totalNumFields the largest number of fields on any row (whether they are used or not)
    * @throws IOException
    */
   public CsvReader(File file, int totalNumFields) throws IOException {
      this(new BufferedReader(new FileReader(file)), totalNumFields);
   }

   public CsvReader(Reader reader, int totalNumFields) throws IOException {
      this(reader, totalNumFields, true);
   }

   public CsvReader(Reader reader, int totalNumFields, boolean enabled) throws IOException {
      this.reader = reader;
      this.streamTokenizer = new StreamTokenizer(reader);
      this.fieldsUsed = new boolean[totalNumFields];
      Arrays.fill(fieldsUsed, enabled);
      countFieldsUsed();

      streamTokenizer.resetSyntax();
      streamTokenizer.eolIsSignificant(true);
      streamTokenizer.whitespaceChars(0, ' ' - 1);
      streamTokenizer.wordChars(' ', 255); // make all non-white space characters part of the returned string
      streamTokenizer.ordinaryChar(','); // except the delimiter ','
      streamTokenizer.quoteChar('\"'); // and the quote char

      getRow(); //prime so hasNext might return true (and getRow will return the first row the next time it is called)
   }

   private void countFieldsUsed() {
      this.fieldCount = 0;
      for (int i = 0; i < fieldsUsed.length; i++) {
         if (fieldsUsed[i]) {
            fieldCount++;
         }
      }
   }

   /**
    * inclusive range
    * 
    * @param start
    * @param end
    * @param enable
    */
   public void setFieldsEnabled(int start, int end, boolean enable) {
      for (int i = start; i <= end; i++) {
         setFieldEnabled(i, enable);
      }
   }

   public void setFieldEnabled(int index, boolean enable) {
      fieldsUsed[index] = enable;
      countFieldsUsed();
   }

   public String[] getRow() throws IOException {
      String[] rowToReturn = nextRow;
      this.nextRow = getRowInternal();
      return rowToReturn;
   }

   public boolean hasNext() {
      return nextRow != null;
   }

   public void skipHeaderRow() throws IOException {
      getRow();
   }

   /**
    * @return an array
    * @throws IOException
    */
   private String[] getRowInternal() throws IOException {
      String[] values = new String[fieldCount];
      int fieldIndex = 0;
      int valuesIndex = 0;
      while (streamTokenizer.nextToken() != StreamTokenizer.TT_EOL) {
         if (streamTokenizer.ttype == ',') {
            if (fieldsUsed[fieldIndex]) {
               valuesIndex++; // accounts for fields that are used even if they are empty (i.e. two consecutive commas)
            }
            fieldIndex++;
         } else if (streamTokenizer.ttype == StreamTokenizer.TT_WORD || streamTokenizer.ttype == '\"') {
            if (fieldsUsed[fieldIndex]) {
               values[valuesIndex] = streamTokenizer.sval;
            }
         } else if (streamTokenizer.ttype == StreamTokenizer.TT_EOF) {
            return null;
         } else {
            throw new IllegalArgumentException("The token type was: " + streamTokenizer.ttype);
         }
      }
      return values;
   }

   public void close() {
      try {
         reader.close();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }
}