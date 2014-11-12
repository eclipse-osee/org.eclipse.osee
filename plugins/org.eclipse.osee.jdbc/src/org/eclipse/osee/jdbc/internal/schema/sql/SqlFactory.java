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
package org.eclipse.osee.jdbc.internal.schema.sql;

import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.jdbc.JdbcException;
import org.eclipse.osee.jdbc.internal.schema.JdbcWriter;

/**
 * @author Roberto E. Escobar
 */
public class SqlFactory {

   private SqlFactory() {
      super();
   }

   public static SqlManager getSqlManager(JdbcClient client, JdbcConnection connection, JdbcDbType databaseType) {
      JdbcWriter writer = new JdbcWriterImpl(client, connection);
      SqlManager instance = null;
      switch (databaseType) {
         case oracle:
            instance = new OracleSqlManager(writer, new OracleSqlDataType());
            break;
         case foxpro:
            instance = new SqlManagerImpl(writer, new FoxProDataType());
            break;
         case mysql:
            instance = new MysqlSqlManager(writer, new MySqlDataType());
            break;
         case postgresql:
            instance = new PostgreSqlManager(writer, new PostgresqlDataType());
            break;
         case h2:
            instance = new H2SqlManager(writer, new H2DataType());
            break;
         case hsql:
            instance = new HyperSqlManager(writer, new HyperSqlDataType());
            break;
         default:
            throw JdbcException.newJdbcException("Unsupported database type [%s] ", databaseType);
      }
      return instance;
   }

   private static final class JdbcWriterImpl implements JdbcWriter {

      private final JdbcClient client;
      private final JdbcConnection txConnection;

      public JdbcWriterImpl(JdbcClient client, JdbcConnection txConnection) {
         super();
         this.client = client;
         this.txConnection = txConnection;
      }

      @Override
      public int runBatchUpdate(String query, Iterable<Object[]> dataList) {
         return client.runBatchUpdate(txConnection, query, dataList);
      }

      @Override
      public int runPreparedUpdate(String query, Object... data) {
         return client.runPreparedUpdate(txConnection, query, data);
      }

   }
}
