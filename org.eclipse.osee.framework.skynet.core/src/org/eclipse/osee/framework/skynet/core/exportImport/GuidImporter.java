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
package org.eclipse.osee.framework.skynet.core.exportImport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.schema.Table;

/**
 * @author Robert A. Fisher
 */
public class GuidImporter {
   private final String INSERT_SQL;
   private final File file;

   /**
    * @param file
    * @param table
    */
   public GuidImporter(File file, Table table, String idColumn) {
      super();
      this.file = file;
      this.INSERT_SQL = "INSERT INTO " + table + " (" + idColumn + ",guid) VALUES (?,?)";
   }

   public void importGuids() throws IOException, NumberFormatException, SQLException {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String line;
      String[] values;
      while ((line = reader.readLine()) != null) {
         values = line.split("\t");
         System.out.println("id " + values[0] + " guid " + values[1]);
         ConnectionHandler.runPreparedUpdate(INSERT_SQL, Integer.parseInt(values[0]), values[1]);
      }
   }

}
