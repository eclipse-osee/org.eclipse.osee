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
package org.eclipse.osee.orcs.core.internal.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Andrew M. Finkbeiner
 */
public class CsvReader {

   private final InputStream inputStream;
   private final CsvRowHandler rowHandler;

   public CsvReader(InputStream inputStream, CsvRowHandler rowHandler) {
      this.inputStream = inputStream;
      this.rowHandler = rowHandler;
   }

   public void readFile() throws IOException {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      while ((line = reader.readLine()) != null) {
         line = line.trim();
         if (!line.startsWith("#")) {
            String[] values = line.split(",");
            rowHandler.onRow(values);
         }
      }
   }

}
