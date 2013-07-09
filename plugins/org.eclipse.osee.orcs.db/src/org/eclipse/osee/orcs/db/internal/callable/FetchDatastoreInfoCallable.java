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

import java.io.InputStream;
import java.net.URI;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.database.schema.SchemaResource;
import org.eclipse.osee.database.schema.SchemaResourceProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.DataStoreConstants;
import org.eclipse.osee.orcs.core.ds.DataStoreInfo;
import org.eclipse.osee.orcs.db.internal.resource.ResourceConstants;
import org.eclipse.osee.orcs.db.internal.util.DataStoreInfoImpl;

/**
 * @author Roberto E. Escobar
 */
public class FetchDatastoreInfoCallable extends AbstractDatastoreCallable<DataStoreInfo> {

   private final SchemaResourceProvider schemaProvider;
   private final SystemPreferences preferences;

   public FetchDatastoreInfoCallable(Log logger, OrcsSession session, IOseeDatabaseService dbService, SchemaResourceProvider schemaProvider, SystemPreferences preferences) {
      super(logger, session, dbService);
      this.schemaProvider = schemaProvider;
      this.preferences = preferences;
   }

   @Override
   public DataStoreInfo call() throws Exception {
      DataStoreInfoImpl dataStoreInfo = new DataStoreInfoImpl();

      Map<String, String> props = new HashMap<String, String>();
      addInfoProperties(props);
      addDbMetaData(props);

      dataStoreInfo.setProperties(props);

      List<IResource> configResources = new ArrayList<IResource>();
      dataStoreInfo.setConfigurationResources(configResources);

      for (SchemaResource resource : schemaProvider.getSchemaResources()) {
         configResources.add(asResource(resource));
      }
      return dataStoreInfo;
   }

   private void addInfoProperties(Map<String, String> props) throws OseeCoreException {
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

   private void addDbMetaData(Map<String, String> props) throws OseeCoreException {
      OseeConnection connection = getDatabaseService().getConnection();
      try {
         DatabaseMetaData meta = connection.getMetaData();
         props.put("db.connection.url", meta.getURL());
         props.put("db.connection.username", meta.getUserName());

         props.put("db.product.name", meta.getDatabaseProductName());
         props.put("db.product.version", meta.getDatabaseProductVersion());

         props.put("db.driver.name", meta.getDriverName());
         props.put("db.driver.version", meta.getDriverVersion());

      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } finally {
         connection.close();
      }
   }

   private IResource asResource(SchemaResource resource) {
      return new SchemaResourceAdaptor(getLogger(), resource);
   }

   private static final class SchemaResourceAdaptor implements IResource {

      private final Log logger;
      private final SchemaResource resource;

      public SchemaResourceAdaptor(Log logger, SchemaResource resource) {
         super();
         this.logger = logger;
         this.resource = resource;
      }

      @Override
      public InputStream getContent() throws OseeCoreException {
         return resource.getContent();
      }

      @Override
      public URI getLocation() {
         URI toReturn = null;
         try {
            toReturn = resource.getLocation();
         } catch (OseeCoreException ex) {
            logger.error(ex, "Error finding resource");
         }
         return toReturn;
      }

      @Override
      public String getName() {
         return resource.getClass().getSimpleName();
      }

      @Override
      public boolean isCompressed() {
         return false;
      }

   }

}
