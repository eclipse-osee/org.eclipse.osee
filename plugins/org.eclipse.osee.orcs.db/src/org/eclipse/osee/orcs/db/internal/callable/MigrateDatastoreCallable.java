/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.orcs.db.internal.callable;

import com.google.common.base.Supplier;
import java.util.concurrent.Callable;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcMigrationOptions;
import org.eclipse.osee.jdbc.JdbcMigrationResource;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.SystemProperties;
import org.eclipse.osee.orcs.core.ds.DataStoreInfo;

/**
 * @author Angel Avila
 */
public class MigrateDatastoreCallable extends AbstractDatastoreCallable<DataStoreInfo> {

   private final SystemProperties preferences;
   private final Supplier<Iterable<JdbcMigrationResource>> schemaProvider;
   private final JdbcMigrationOptions options;

   public MigrateDatastoreCallable(OrcsSession session, Log logger, JdbcClient jdbcClient, SystemProperties preferences, Supplier<Iterable<JdbcMigrationResource>> schemaProvider, JdbcMigrationOptions options) {
      super(logger, session, jdbcClient);
      this.preferences = preferences;
      this.schemaProvider = schemaProvider;
      this.options = options;
   }

   @Override
   public DataStoreInfo call() throws Exception {
      getJdbcClient().migrate(options, schemaProvider.get());

      Callable<DataStoreInfo> fetchCallable =
         new FetchDatastoreInfoCallable(getLogger(), getJdbcClient(), schemaProvider, preferences);
      DataStoreInfo dataStoreInfo = callAndCheckForCancel(fetchCallable);
      return dataStoreInfo;
   }
}