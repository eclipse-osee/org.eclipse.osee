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
package org.eclipse.osee.database.schema.internal.sql;

import java.sql.DatabaseMetaData;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.SupportedDatabase;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class SqlFactory {

   private SqlFactory() {
      super();
   }

   public static SqlManager getSqlManager(Log logger, DatabaseMetaData metaData) throws OseeCoreException {
      SupportedDatabase db = SupportedDatabase.getDatabaseType(metaData);
      return getSqlManager(logger, db);
   }

   private static SqlManager getSqlManager(Log logger, SupportedDatabase db) throws OseeCoreException {
      SqlManager instance = null;
      switch (db) {
         case oracle:
            instance = new OracleSqlManager(logger, new OracleSqlDataType());
            break;
         case foxpro:
            instance = new SqlManagerImpl(logger, new FoxProDataType());
            break;
         case mysql:
            instance = new MysqlSqlManager(logger, new MySqlDataType());
            break;
         case postgresql:
            instance = new PostgreSqlManager(logger, new PostgresqlDataType());
            break;
         case h2:
            instance = new H2SqlManager(logger, new H2DataType());
            break;
         case hsql:
            instance = new HyperSqlManager(logger, new HyperSqlDataType());
            break;
         default:
            throw new OseeDataStoreException("Unsupported database type [%s] ", db);
      }
      return instance;
   }
}
