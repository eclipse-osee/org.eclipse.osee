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

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import com.google.common.io.InputSupplier;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OrcsTypesData;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

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
            BranchId storageBranch = CoreBranches.COMMON;
            ClientStorage clientStorage = new ClientStorage(logger, orcsApi, storageBranch);

            if (!clientStorage.typesExist()) {
               InputSupplier<InputStream> newTypesSupplier = newTypesSupplier();
               ArtifactReadable typeArt = (ArtifactReadable) clientStorage.storeTypes(newTypesSupplier);

               TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(COMMON, SystemUser.OseeSystem,
                  "Add OseeTypeDef OAuth Tuple to Common Branch");
               tx.addTuple2(CoreTupleTypes.OseeTypeDef, OrcsTypesData.OSEE_TYPE_VERSION,
                  typeArt.getAttributes(CoreAttributeTypes.UriGeneralStringData).iterator().next());

               tx.commit();
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