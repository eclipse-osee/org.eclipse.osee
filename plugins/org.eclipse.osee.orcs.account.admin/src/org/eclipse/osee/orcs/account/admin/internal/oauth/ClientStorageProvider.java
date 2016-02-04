/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.account.admin.internal.oauth;

import com.google.common.io.InputSupplier;
import com.google.gson.GsonBuilder;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Roberto E. Escobar
 */
public class ClientStorageProvider extends LazyObject<ClientStorage> {

   private static final String OAUTH_TYPES_DEFITIONS = "types/OseeTypes_OAuth.osee";

   private Log logger;
   private OrcsApi orcsApi;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   protected FutureTask<ClientStorage> createLoaderTask() {
      Callable<ClientStorage> callable = new Callable<ClientStorage>() {

         @Override
         public ClientStorage call() throws Exception {
            GsonBuilder builder = new GsonBuilder();
            BranchId storageBranch = CoreBranches.COMMON;
            ClientStorage clientStorage = new ClientStorage(logger, builder, orcsApi, storageBranch);

            if (!clientStorage.typesExist()) {
               clientStorage.storeTypes(newTypesSupplier());
            }
            return clientStorage;
         }

      };
      return new FutureTask<ClientStorage>(callable);
   }

   private InputSupplier<InputStream> newTypesSupplier() {
      return new InputSupplier<InputStream>() {

         @Override
         public InputStream getInput() throws IOException {
            URL resource = getClass().getResource(OAUTH_TYPES_DEFITIONS);
            return new BufferedInputStream(resource.openStream());
         }
      };
   }
}