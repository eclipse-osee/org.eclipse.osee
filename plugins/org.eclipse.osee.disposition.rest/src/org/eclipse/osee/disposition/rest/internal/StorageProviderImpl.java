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
package org.eclipse.osee.disposition.rest.internal;

import java.net.URI;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.util.Resource;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Angel Avila
 */
public class StorageProviderImpl extends LazyObject<Storage> implements StorageProvider {

   private static final String DISPOSITION_TYPE_DEFINITIONS = "orcsTypes/OseeTypes_Dispo.osee";
   private Log logger;
   private OrcsApi orcsApi;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void start() {
      logger.trace("Starting StorageProviderImpl...");
   }

   public void stop() {
      logger.trace("Stopping StorageProviderImpl...");
      invalidate();
   }

   @Override
   protected FutureTask<Storage> createLoaderTask() {
      Callable<Storage> callable = new Callable<Storage>() {

         @Override
         public Storage call() throws Exception {
            return createStorage();
         }

      };
      return new FutureTask<>(callable);
   }

   private Storage createStorage() throws Exception {
      Storage storage = new OrcsStorageImpl(logger, orcsApi);
      if (!storage.typesExist()) {
         URL resource = OseeInf.getResourceAsUrl(DISPOSITION_TYPE_DEFINITIONS, getClass());
         URI uri = resource.toURI();
         IResource typesResource = new Resource(uri, false);
         storage.storeTypes(typesResource);
      }
      return storage;
   }
}
