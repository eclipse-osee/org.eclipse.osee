/*
 * Created on Sep 30, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal.relation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
