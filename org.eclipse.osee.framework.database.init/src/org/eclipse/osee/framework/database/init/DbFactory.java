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
package org.eclipse.osee.framework.database.init;

import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;

/**
 * @author Roberto E. Escobar
 */
public class DbFactory {
   private SchemaData schemaData;
   protected SqlManager sqlManager = SqlFactory.getSqlManager();

   public DbFactory(SchemaData schemaData) throws OseeDataStoreException {
      this.schemaData = schemaData;
   }

   public void createTables() throws OseeDataStoreException {
      List<TableElement> tableDefs = schemaData.getTablesOrderedByDependency();
      for (TableElement tableDef : tableDefs) {
         sqlManager.createTable(tableDef);
      }
   }

   public void dropTables() throws OseeDataStoreException {
      List<TableElement> tableDefs = schemaData.getTablesOrderedByDependency();
      for (int index = (tableDefs.size() - 1); index >= 0; index--) {
         TableElement tableDef = tableDefs.get(index);
         sqlManager.dropTable(tableDef);
      }
   }

   public void createIndeces() throws OseeDataStoreException {
      for (TableElement tableDef : schemaData.getTableMap().values()) {
         sqlManager.createIndex(tableDef);
      }
   }

   public void dropIndeces() throws OseeDataStoreException {
      for (TableElement tableDef : schemaData.getTableMap().values()) {
         sqlManager.dropIndex(tableDef);
      }
   }
}
