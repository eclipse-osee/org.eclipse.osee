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
package org.eclipse.osee.orcs.db.internal;

import java.util.Map;
import java.util.concurrent.Callable;
import org.eclipse.osee.database.schema.SchemaOptions;
import org.eclipse.osee.database.schema.SchemaResourceProvider;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.ds.DataStoreAdmin;
import org.eclipse.osee.orcs.core.ds.DataStoreInfo;
import org.eclipse.osee.orcs.db.internal.callable.FetchDatastoreInfoCallable;
import org.eclipse.osee.orcs.db.internal.callable.InitializeDatastoreCallable;
import org.eclipse.osee.orcs.db.internal.util.DynamicSchemaResourceProvider;

/**
 * @author Roberto E. Escobar
 */
public class DataStoreAdminImpl implements DataStoreAdmin {

   private final Log logger;
   private final IOseeDatabaseService dbService;
   private final IdentityService identityService;
   private final BranchDataStore branchStore;
   private final SystemPreferences preferences;

   public DataStoreAdminImpl(Log logger, IOseeDatabaseService dbService, IdentityService identityService, BranchDataStore branchStore, SystemPreferences preferences) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.identityService = identityService;
      this.branchStore = branchStore;
      this.preferences = preferences;
   }

   @Override
   public Callable<DataStoreInfo> createDataStore(OrcsSession session, Map<String, String> parameters) {
      String tableDataSpace = getOption(parameters, DataStoreConfigConstants.SCHEMA_TABLE_DATA_NAMESPACE, "");
      String indexDataSpace = getOption(parameters, DataStoreConfigConstants.SCHEMA_INDEX_DATA_NAMESPACE, "");
      boolean useFileSpecifiedSchemas =
         getOption(parameters, DataStoreConfigConstants.SCHEMA_USER_FILE_SPECIFIED_NAMESPACE, false);

      SchemaResourceProvider schemaProvider = new DynamicSchemaResourceProvider(logger);

      SchemaOptions options = new SchemaOptions(tableDataSpace, indexDataSpace, useFileSpecifiedSchemas);
      return new InitializeDatastoreCallable(session, logger, dbService, identityService, branchStore, preferences,
         schemaProvider, options);
   }

   @Override
   public Callable<DataStoreInfo> getDataStoreInfo(OrcsSession session) {
      SchemaResourceProvider schemaProvider = new DynamicSchemaResourceProvider(logger);
      return new FetchDatastoreInfoCallable(logger, session, dbService, schemaProvider, preferences);
   }

   private boolean getOption(Map<String, String> parameters, String key, boolean defaultValue) {
      boolean toReturn = defaultValue;
      String value = parameters.get(key);
      if (Strings.isValid(value)) {
         toReturn = Boolean.parseBoolean(value);
      }
      return toReturn;
   }

   private String getOption(Map<String, String> parameters, String key, String defaultValue) {
      String toReturn = defaultValue;
      String value = parameters.get(key);
      if (Strings.isValid(value)) {
         toReturn = value;
      }
      return toReturn;
   }

}
