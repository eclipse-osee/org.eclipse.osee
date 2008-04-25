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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
import org.eclipse.osee.framework.database.data.SchemaData;
import org.eclipse.osee.framework.database.data.TableElement;
import org.eclipse.osee.framework.database.sql.SqlFactory;
import org.eclipse.osee.framework.database.sql.SqlManager;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;

/**
 * @author Roberto E. Escobar
 */
public class DbFactory {

   private SchemaData schemaData;
   protected SqlManager sqlManager;
   protected Connection connection;
   protected Logger logger;

   public DbFactory(Connection connection, SupportedDatabase databaseType, SchemaData schemaData) {
      this.schemaData = schemaData;
      this.connection = connection;
      this.sqlManager = SqlFactory.getSqlManager(databaseType);
      this.logger = ConfigUtil.getConfigFactory().getLogger(DbFactory.class);
   }

   public void createTables() throws SQLException, Exception {
      List<TableElement> tableDefs = schemaData.getTablesOrderedByDependency();
      for (TableElement tableDef : tableDefs) {
         sqlManager.createTable(connection, tableDef);
      }
   }

   public void dropTables() throws SQLException, Exception {
      List<TableElement> tableDefs = schemaData.getTablesOrderedByDependency();
      for (int index = (tableDefs.size() - 1); index >= 0; index--) {
         TableElement tableDef = tableDefs.get(index);
         sqlManager.dropTable(connection, tableDef);
      }
   }

   public void createIndeces() throws SQLException, Exception {
      for (TableElement tableDef : schemaData.getTableMap().values()) {
         sqlManager.createIndex(connection, tableDef);
      }
   }

   public void dropIndeces() throws SQLException, Exception {
      for (TableElement tableDef : schemaData.getTableMap().values()) {
         sqlManager.dropIndex(connection, tableDef);
      }
   }
}
