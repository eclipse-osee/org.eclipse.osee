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
package org.eclipse.osee.framework.database.sql;

import org.eclipse.osee.framework.database.sql.datatype.DerbySqlDataType;
import org.eclipse.osee.framework.database.sql.datatype.FoxProDataType;
import org.eclipse.osee.framework.database.sql.datatype.MySqlDataType;
import org.eclipse.osee.framework.database.sql.datatype.OracleSqlDataType;
import org.eclipse.osee.framework.database.sql.datatype.PostgresqlDataType;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;

/**
 * @author Roberto E. Escobar
 */
public class SqlFactory {

   private SqlFactory() {
      super();
   }

   public static SqlManager getSqlManager() throws OseeDataStoreException {
      return getSqlManager(SupportedDatabase.getDatabaseType());
   }

   private static SqlManager getSqlManager(SupportedDatabase db) {
      SqlManager instance = null;
      switch (db) {
         case oracle:
            instance = new OracleSqlManager(new OracleSqlDataType());
            break;
         case derby:
            instance = new DerbySqlManager(new DerbySqlDataType());
            break;
         case foxpro:
            instance = new SqlManagerImpl(new FoxProDataType());
            break;
         case mysql:
            instance = new MysqlSqlManager(new MySqlDataType());
            break;
         case postgresql:
            instance = new PostgreSqlManager(new PostgresqlDataType());
            break;
         default:
            instance = null;
            break;
      }
      return instance;
   }
}
