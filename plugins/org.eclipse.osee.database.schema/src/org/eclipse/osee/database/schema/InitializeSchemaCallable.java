/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.database.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.eclipse.osee.database.schema.internal.callable.CreateSchemaCallable;
import org.eclipse.osee.database.schema.internal.callable.ExtractSchemaCallable;
import org.eclipse.osee.database.schema.internal.callable.LoadUserSchemasCallable;
import org.eclipse.osee.database.schema.internal.data.SchemaData;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;

public class InitializeSchemaCallable extends DatabaseCallable<Object> {

   private final SchemaResourceProvider schemaProvider;
   private final SchemaOptions options;

   public InitializeSchemaCallable(Log logger, IOseeDatabaseService service, SchemaResourceProvider schemaProvider, SchemaOptions options) {
      super(logger, service);
      this.schemaProvider = schemaProvider;
      this.options = options;
   }

   @Override
   public Object call() throws Exception {
      Map<String, SchemaData> userSpecifiedConfig = new HashMap<String, SchemaData>();
      Map<String, SchemaData> currentDatabaseConfig = new HashMap<String, SchemaData>();

      List<Callable<?>> ops = new ArrayList<Callable<?>>();
      ops.add(new LoadUserSchemasCallable(getLogger(), getDatabaseService(), userSpecifiedConfig, schemaProvider,
         options));
      ops.add(new ExtractSchemaCallable(getLogger(), getDatabaseService(), userSpecifiedConfig.keySet(),
         currentDatabaseConfig));
      ops.add(new CreateSchemaCallable(getLogger(), getDatabaseService(), userSpecifiedConfig, currentDatabaseConfig));
      for (Callable<?> op : ops) {
         op.call();
      }
      return null;
   }
}
