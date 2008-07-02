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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author Roberto E. Escobar
 */
public class WordChunker implements Iterable<String>, Iterator<String> {

   private Vector<String> words = new Vector<String>();
   private BufferedReader bufferedReader;

   public WordChunker(InputStream inputStream) throws UnsupportedEncodingException {
      bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
   }

   /* (non-Javadoc)
    * @see java.lang.Iterable#iterator()
    */
   @Override
   public Iterator<String> iterator() {
      return this;
   }

   /* (non-Javadoc)
    * @see java.util.Iterator#hasNext()
    */
   @Override
   public boolean hasNext() {
      try {
         if (words.isEmpty() != true || (bufferedReader != null && bufferedReader.ready())) {
            return true;
         }
      } catch (IOException ioex) {
         // Do Nothing;
      }
      return false;
   }

   /* (non-Javadoc)
    * @see java.util.Iterator#next()
    */
   @Override
   public String next() {
      if (words.isEmpty()) {
         String line = null;
         try {
            line = bufferedReader.readLine();
            for (String item : line.split("\\s+")) {
               item = item.trim();
               if (item.length() > 0) {
                  words.add(item);
               }
            }
         } catch (IOException ioex) {
            // Do Nothing
         }
         if (line == null) {
            try {
               bufferedReader.close();
            } catch (IOException ioex) {
               // Do Nothing
            }
            bufferedReader = null;
            line = "";
         }
      }
      return words.isEmpty() != true ? words.remove(0) : "";
   }

   /* (non-Javadoc)
    * @see java.util.Iterator#remove()
    */
   @Override
   public void remove() {
      throw new UnsupportedOperationException();
   }
}
