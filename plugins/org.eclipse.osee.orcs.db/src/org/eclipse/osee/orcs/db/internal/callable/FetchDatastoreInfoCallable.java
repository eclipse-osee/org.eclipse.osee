/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.callable;

import com.google.common.base.Supplier;
import java.net.URL;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcMigrationResource;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.DataStoreConstants;
import org.eclipse.osee.orcs.core.ds.DataStoreInfo;
import org.eclipse.osee.orcs.db.internal.resource.ResourceConstants;
import org.eclipse.osee.orcs.db.internal.util.DataStoreInfoImpl;

/**
 * @author Roberto E. Escobar
 */
public class FetchDatastoreInfoCallable extends AbstractDatastoreCallable<DataStoreInfo> {

   private final Supplier<Iterable<JdbcMigrationResource>> schemaProvider;
   private final SystemPreferences preferences;

   public FetchDatastoreInfoCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, Supplier<Iterable<JdbcMigrationResource>> schemaProvider, SystemPreferences preferences) {
      super(logger, session, jdbcClient);
      this.schemaProvider = schemaProvider;
      this.preferences = preferences;
   }

   @Override
   public DataStoreInfo call() throws Exception {
      DataStoreInfoImpl dataStoreInfo = new DataStoreInfoImpl();

      Map<String, String> props = new HashMap<>();
      addInfoProperties(props);
      addDbMetaData(props);

      dataStoreInfo.setProperties(props);

      Set<URL> configResources = new HashSet<>();
      dataStoreInfo.setConfigurationResources(configResources);

      for (JdbcMigrationResource resource : schemaProvider.get()) {
         configResources.add(resource.getLocation());
      }
      return dataStoreInfo;
   }

   private void addInfoProperties(Map<String, String> props)  {
      for (String key : preferences.getKeys()) {
         String value = preferences.getValue(key);
         if (!Strings.isValid(value)) {
            value = "";
         }
         props.put(key, value);
      }
      props.put(DataStoreConstants.DATASTORE_ID_KEY, preferences.getSystemUuid());
      props.put("ds.binary.data.path", ResourceConstants.getBinaryDataPath(preferences));
      props.put("ds.attribute.data.path", ResourceConstants.getAttributeDataPath(preferences));
      props.put("ds.exchange.data.path", ResourceConstants.getExchangeDataPath(preferences));
   }

   private void addDbMetaData(Map<String, String> props)  {
      try (JdbcConnection connection = getJdbcClient().getConnection()) {
         DatabaseMetaData meta = connection.getMetaData();
         props.put("db.connection.url", meta.getURL());
         props.put("db.connection.username", meta.getUserName());

         props.put("db.product.name", meta.getDatabaseProductName());
         props.put("db.product.version", meta.getDatabaseProductVersion());

         props.put("db.driver.name", meta.getDriverName());
         props.put("db.driver.version", meta.getDriverVersion());

      } catch (SQLException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }
}