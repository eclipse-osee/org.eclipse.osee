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
package org.eclipse.osee.database.schema.internal.callable;

import java.sql.DatabaseMetaData;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.database.schema.DatabaseTxCallable;
import org.eclipse.osee.database.schema.internal.data.SchemaData;
import org.eclipse.osee.database.schema.internal.sql.SchemaSqlUtil;
import org.eclipse.osee.database.schema.internal.sql.SqlFactory;
import org.eclipse.osee.database.schema.internal.sql.SqlManager;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.SupportedDatabase;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class CreateSchemaCallable extends DatabaseTxCallable<Object> {

   private final Log logger;
   private final Map<String, SchemaData> userSchema;
   private final Map<String, SchemaData> dbSchema;

   public CreateSchemaCallable(Log logger, IOseeDatabaseService dbService, Map<String, SchemaData> userSchema, Map<String, SchemaData> dbSchema) {
      super(logger, dbService, "Create Schema");
      this.logger = logger;
      this.userSchema = userSchema;
      this.dbSchema = dbSchema;
   }

   @Override
   protected Object handleTxWork(OseeConnection connection) throws OseeCoreException {
      DatabaseMetaData metaData = connection.getMetaData();

      SupportedDatabase dbType = SupportedDatabase.getDatabaseType(metaData);
      SqlManager sqlManager = SqlFactory.getSqlManager(logger, metaData);
      SchemaSqlUtil dbInit = new SchemaSqlUtil(sqlManager);

      Set<String> schemas = userSchema.keySet();

      dbInit.dropIndices(schemas, userSchema, dbSchema);
      dbInit.dropTables(schemas, userSchema, dbSchema);

      if (dbType == SupportedDatabase.postgresql || dbType == SupportedDatabase.h2) {
         try {
            dbInit.dropSchema(schemas);
         } catch (Exception ex) {
            //
         }
         dbInit.createSchema(schemas);
      }
      dbInit.addTables(schemas, userSchema);
      dbInit.addIndices(schemas, userSchema);
      return null;
   }
}
