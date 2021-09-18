/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal;

import com.google.common.base.Supplier;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcMigrationResource;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.SystemProperties;
import org.eclipse.osee.orcs.core.ds.DataStoreAdmin;
import org.eclipse.osee.orcs.core.ds.DataStoreConstants;
import org.eclipse.osee.orcs.core.ds.DataStoreInfo;
import org.eclipse.osee.orcs.db.internal.callable.FetchDatastoreInfoCallable;
import org.eclipse.osee.orcs.db.internal.resource.ResourceConstants;
import org.eclipse.osee.orcs.db.internal.util.DynamicSchemaResourceProvider;

/**
 * @author Roberto E. Escobar
 */
public class DataStoreAdminImpl implements DataStoreAdmin {

   private final Log logger;
   private final JdbcClient jdbcClient;
   private final SystemProperties properties;

   public DataStoreAdminImpl(Log logger, JdbcClient jdbcClient, SystemProperties properties) {
      this.logger = logger;
      this.jdbcClient = jdbcClient;
      this.properties = properties;
   }

   @Override
   public void createDataStore(UserToken superUser) {
      Conditions.checkExpressionFailOnTrue(jdbcClient.getConfig().isProduction(),
         "Error - attempting to initialize a production datastore.");

      new DatabaseCreation(jdbcClient).createDataStore();

      jdbcClient.runPreparedUpdate("UPDATE osee_tx_details SET author = ? WHERE author <= 0 OR author = ?", superUser,
         SystemUser.BootStrap);

      String attributeDataPath = ResourceConstants.getAttributeDataPath(properties);
      logger.info("Deleting application server binary data [%s]...", attributeDataPath);
      Lib.deleteDir(new File(attributeDataPath));

      properties.putValue(DataStoreConstants.DATASTORE_ID_KEY, GUID.create());

      addDefaultPermissions();

      jdbcClient.invalidateSequences();
   }

   private void addDefaultPermissions() {
      List<Object[]> data = new LinkedList<>();
      for (PermissionEnum permission : PermissionEnum.values()) {
         data.add(new Object[] {permission.getPermId(), permission.getName()});
      }
      jdbcClient.runBatchUpdate(OseeDb.OSEE_PERMISSION_TABLE.getInsertSql(), data);
   }

   @Override
   public Callable<DataStoreInfo> getDataStoreInfo(OrcsSession session) {
      Supplier<Iterable<JdbcMigrationResource>> schemaProvider = new DynamicSchemaResourceProvider(logger);
      return new FetchDatastoreInfoCallable(logger, jdbcClient, schemaProvider, properties);
   }

   @Override
   public JdbcClient getJdbcClient() {
      return jdbcClient;
   }
}