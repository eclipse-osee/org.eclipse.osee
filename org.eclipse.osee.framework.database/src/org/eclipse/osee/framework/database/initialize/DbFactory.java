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
package org.eclipse.osee.framework.database.initialize;

import java.util.List;
import org.eclipse.osee.framework.database.data.SchemaData;
import org.eclipse.osee.framework.database.data.TableElement;
import org.eclipse.osee.framework.database.sql.SqlFactory;
import org.eclipse.osee.framework.database.sql.SqlManager;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;

/**
 * @author Roberto E. Escobar
 */
public class DbFactory {

   private SchemaData schemaData;
   protected SqlManager sqlManager;
   protected OseeConnection connection;

   public DbFactory(OseeConnection connection, SupportedDatabase databaseType, SchemaData schemaData) {
      this.schemaData = schemaData;
      this.connection = connection;
      this.sqlManager = SqlFactory.getSqlManager(databaseType);
   }

   public void createTables() throws OseeDataStoreException {
      List<TableElement> tableDefs = schemaData.getTablesOrderedByDependency();
      for (TableElement tableDef : tableDefs) {
         sqlManager.createTable(connection, tableDef);
      }
   }

   public void dropTables() throws OseeDataStoreException {
      List<TableElement> tableDefs = schemaData.getTablesOrderedByDependency();
      for (int index = (tableDefs.size() - 1); index >= 0; index--) {
         TableElement tableDef = tableDefs.get(index);
         sqlManager.dropTable(connection, tableDef);
      }
   }

   public void createIndeces() throws OseeDataStoreException {
      for (TableElement tableDef : schemaData.getTableMap().values()) {
         sqlManager.createIndex(connection, tableDef);
      }
   }

   public void dropIndeces() throws OseeDataStoreException {
      for (TableElement tableDef : schemaData.getTableMap().values()) {
         sqlManager.dropIndex(connection, tableDef);
      }
   }
}
