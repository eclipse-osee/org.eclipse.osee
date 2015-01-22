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
package org.eclipse.osee.jdbc.internal.schema.ops;

import java.util.Map;
import java.util.Set;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.jdbc.JdbcTransaction;
import org.eclipse.osee.jdbc.internal.schema.data.SchemaData;
import org.eclipse.osee.jdbc.internal.schema.sql.SchemaSqlUtil;
import org.eclipse.osee.jdbc.internal.schema.sql.SqlFactory;
import org.eclipse.osee.jdbc.internal.schema.sql.SqlManager;

/**
 * @author Roberto E. Escobar
 */
public class CreateSchemaTx extends JdbcTransaction {

   private final Map<String, SchemaData> userSchema;
   private final Map<String, SchemaData> dbSchema;
   private final JdbcClient client;

   public CreateSchemaTx(JdbcClient client, Map<String, SchemaData> userSchema, Map<String, SchemaData> dbSchema) {
      this.client = client;
      this.userSchema = userSchema;
      this.dbSchema = dbSchema;
   }

   @Override
   public void handleTxWork(JdbcConnection connection) {
      SqlManager sqlManager = SqlFactory.getSqlManager(client, connection, client.getDatabaseType());
      SchemaSqlUtil dbInit = new SchemaSqlUtil(sqlManager);

      Set<String> schemas = userSchema.keySet();

      dbInit.dropIndices(schemas, userSchema, dbSchema);
      dbInit.dropTables(schemas, userSchema, dbSchema);

      JdbcDbType dbType = client.getDatabaseType();
      if (dbType == JdbcDbType.postgresql // 
         || dbType == JdbcDbType.h2 // 
         || dbType == JdbcDbType.hsql //
      ) {
         try {
            dbInit.dropSchema(schemas);
         } catch (Exception ex) {
            //
         }
         dbInit.createSchema(schemas);
      }
      dbInit.addTables(schemas, userSchema);
      dbInit.addIndices(schemas, userSchema);
   }

   @Override
   public void handleTxException(Exception ex) {
      //
   }

   @Override
   public void handleTxFinally() {
      //
   }
}
