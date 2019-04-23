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

import com.google.common.base.Supplier;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcMigrationOptions;
import org.eclipse.osee.jdbc.JdbcMigrationResource;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.DataStoreAdmin;
import org.eclipse.osee.orcs.core.ds.DataStoreConstants;
import org.eclipse.osee.orcs.core.ds.DataStoreInfo;
import org.eclipse.osee.orcs.core.ds.OrcsTypesDataStore;
import org.eclipse.osee.orcs.db.internal.callable.FetchDatastoreInfoCallable;
import org.eclipse.osee.orcs.db.internal.callable.MigrateDatastoreCallable;
import org.eclipse.osee.orcs.db.internal.resource.ResourceConstants;
import org.eclipse.osee.orcs.db.internal.util.DynamicSchemaResourceProvider;

/**
 * @author Roberto E. Escobar
 */
public class AdminModule {

   private final Log logger;
   private final JdbcClient jdbcClient;
   private final IdentityManager identityService;
   private final SystemPreferences preferences;
   private final OrcsTypesDataStore typesDataStore;

   public AdminModule(Log logger, JdbcClient jdbcClient, IdentityManager identityService, SystemPreferences preferences, OrcsTypesDataStore typesDataStore) {
      super();
      this.logger = logger;
      this.jdbcClient = jdbcClient;
      this.identityService = identityService;
      this.preferences = preferences;
      this.typesDataStore = typesDataStore;
   }

   public DataStoreAdmin createDataStoreAdmin() {
      return new DataStoreAdmin() {
         @Override
         public void createDataStore() {
            Supplier<Iterable<JdbcMigrationResource>> schemaProvider = new DynamicSchemaResourceProvider(logger);

            JdbcMigrationOptions options = new JdbcMigrationOptions(true, true);
            Conditions.checkExpressionFailOnTrue(jdbcClient.getConfig().isProduction(),
               "Error - attempting to initialize a production datastore.");

            jdbcClient.migrate(options, schemaProvider.get());

            String attributeDataPath = ResourceConstants.getAttributeDataPath(preferences);
            logger.info("Deleting application server binary data [%s]...", attributeDataPath);
            Lib.deleteDir(new File(attributeDataPath));

            preferences.putValue(DataStoreConstants.DATASTORE_ID_KEY, GUID.create());

            addDefaultPermissions();

            identityService.invalidateIds();
         }

         private void addDefaultPermissions() {
            List<Object[]> data = new LinkedList<>();
            for (PermissionEnum permission : PermissionEnum.values()) {
               data.add(new Object[] {permission.getPermId(), permission.getName()});
            }
            jdbcClient.runBatchUpdate("INSERT INTO OSEE_PERMISSION (PERMISSION_ID, PERMISSION_NAME) VALUES (?,?)",
               data);
         }

         @Override
         public Callable<DataStoreInfo> migrateDataStore(OrcsSession session) {
            Supplier<Iterable<JdbcMigrationResource>> schemaProvider = new DynamicSchemaResourceProvider(logger);
            JdbcMigrationOptions options = new JdbcMigrationOptions(false, false);

            return new MigrateDatastoreCallable(session, logger, jdbcClient, preferences, schemaProvider, options);
         }

         @Override
         public Callable<DataStoreInfo> getDataStoreInfo(OrcsSession session) {
            Supplier<Iterable<JdbcMigrationResource>> schemaProvider = new DynamicSchemaResourceProvider(logger);
            return new FetchDatastoreInfoCallable(logger, jdbcClient, schemaProvider, preferences);
         }

         @Override
         public boolean isDataStoreInitialized() {
            boolean initialized = false;
            try {
               String systemUuid = preferences.getSystemUuid();
               if (Strings.isValid(systemUuid)) {
                  IResource resource = typesDataStore.getOrcsTypesLoader(null);
                  if (resource != null) {
                     initialized = true;
                  }
               }
            } catch (Exception ex) {
               // do nothing;
            }
            return initialized;
         }

         @Override
         public JdbcClient getJdbcClient() {
            return jdbcClient;
         }
      };
   }
}
