/*
 * Created on May 5, 2016
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.db.internal.callable;

import com.google.common.base.Supplier;
import java.util.concurrent.Callable;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcMigrationOptions;
import org.eclipse.osee.jdbc.JdbcMigrationResource;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.DataStoreInfo;

public class MigrateDatastoreCallable extends AbstractDatastoreCallable<DataStoreInfo> {

   private final SystemPreferences preferences;
   private final Supplier<Iterable<JdbcMigrationResource>> schemaProvider;
   private final JdbcMigrationOptions options;

   public MigrateDatastoreCallable(OrcsSession session, Log logger, JdbcClient jdbcClient, SystemPreferences preferences, Supplier<Iterable<JdbcMigrationResource>> schemaProvider, JdbcMigrationOptions options) {
      super(logger, session, jdbcClient);
      this.preferences = preferences;
      this.schemaProvider = schemaProvider;
      this.options = options;
   }

   @Override
   public DataStoreInfo call() throws Exception {
      getJdbcClient().migrate(options, schemaProvider.get());

      Callable<DataStoreInfo> fetchCallable =
         new FetchDatastoreInfoCallable(getLogger(), getSession(), getJdbcClient(), schemaProvider, preferences);
      DataStoreInfo dataStoreInfo = callAndCheckForCancel(fetchCallable);
      return dataStoreInfo;
   }

}
