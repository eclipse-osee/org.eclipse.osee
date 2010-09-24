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
package org.eclipse.osee.framework.core.datastore.schema.operations;

import java.sql.DatabaseMetaData;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.datastore.internal.Activator;
import org.eclipse.osee.framework.core.datastore.schema.data.SchemaData;
import org.eclipse.osee.framework.core.datastore.schema.sql.SchemaSqlUtil;
import org.eclipse.osee.framework.core.datastore.schema.sql.SqlFactory;
import org.eclipse.osee.framework.core.datastore.schema.sql.SqlManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.SupportedDatabase;

/**
 * @author Roberto E. Escobar
 */
public class CreateSchemaOperation extends AbstractDbTxOperation {
   private final Map<String, SchemaData> userSchema;
   private final Map<String, SchemaData> dbSchema;

   public CreateSchemaOperation(IOseeDatabaseService databaseService, Map<String, SchemaData> userSchema, Map<String, SchemaData> dbSchema) {
      super(databaseService, "Create Schema", Activator.PLUGIN_ID);
      this.userSchema = userSchema;
      this.dbSchema = dbSchema;
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      DatabaseMetaData metaData = connection.getMetaData();
      SqlManager sqlManager = SqlFactory.getSqlManager(metaData);
      SchemaSqlUtil dbInit = new SchemaSqlUtil(sqlManager);

      Set<String> schemas = userSchema.keySet();
      dbInit.dropIndices(schemas, userSchema, dbSchema);
      dbInit.dropTables(schemas, userSchema, dbSchema);
      if (SupportedDatabase.isDatabaseType(metaData, SupportedDatabase.postgresql)) {
         dbInit.dropSchema(schemas);
         dbInit.createSchema(schemas);
      }
      dbInit.addTables(schemas, userSchema);
      dbInit.addIndices(schemas, userSchema);
   }

}
