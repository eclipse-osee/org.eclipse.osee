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
package org.eclipse.osee.orcs.core.internal;

import java.util.concurrent.Callable;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsAdmin;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsMetaData;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.DataStoreAdmin;
import org.eclipse.osee.orcs.core.internal.admin.FetchDatastoreMetadataCallable;
import org.eclipse.osee.orcs.core.internal.admin.MigrateDatastoreAdminCallable;

/**
 * @author Roberto E. Escobar
 */
public class OrcsAdminImpl implements OrcsAdmin {
   private final OrcsApi orcsApi;
   private final Log logger;
   private final OrcsSession session;
   private final DataStoreAdmin dataStoreAdmin;

   public OrcsAdminImpl(OrcsApi orcsApi, Log logger, OrcsSession session, DataStoreAdmin dataStoreAdmin) {
      this.orcsApi = orcsApi;
      this.logger = logger;
      this.session = session;
      this.dataStoreAdmin = dataStoreAdmin;
   }

   @Override
   public void createDatastore(String typeModel) {
      dataStoreAdmin.createDataStore();
      orcsApi.getOrcsTypes().loadTypes(typeModel);
   }

   @Override
   public Callable<OrcsMetaData> migrateDatastore() {
      return new MigrateDatastoreAdminCallable(logger, session, dataStoreAdmin);
   }

   @Override
   public Callable<OrcsMetaData> createFetchOrcsMetaData() {
      return new FetchDatastoreMetadataCallable(logger, session, dataStoreAdmin);
   }

   @Override
   public boolean isDataStoreInitialized() {
      return dataStoreAdmin.isDataStoreInitialized();
   }
}