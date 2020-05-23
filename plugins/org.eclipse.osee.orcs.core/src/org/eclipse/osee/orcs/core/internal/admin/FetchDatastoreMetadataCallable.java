/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.core.internal.admin;

import java.util.Map;
import java.util.concurrent.Callable;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsMetaData;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.DataStoreAdmin;
import org.eclipse.osee.orcs.core.ds.DataStoreInfo;

/**
 * @author Roberto E. Escobar
 */
public class FetchDatastoreMetadataCallable extends AbstractAdminCallable<OrcsMetaData> {

   private final DataStoreAdmin dataStoreAdmin;

   public FetchDatastoreMetadataCallable(Log logger, OrcsSession session, DataStoreAdmin dataStoreAdmin) {
      super(logger, session);
      this.dataStoreAdmin = dataStoreAdmin;
   }

   @Override
   protected OrcsMetaData innerCall() throws Exception {
      Callable<DataStoreInfo> callable = dataStoreAdmin.getDataStoreInfo(getSession());
      final DataStoreInfo dataStoreInfo = callAndCheckForCancel(callable);

      OrcsMetaData orcsMetaData = new OrcsMetaData() {

         @Override
         public Map<String, String> getProperties() {
            return dataStoreInfo.getProperties();
         }

      };
      return orcsMetaData;
   }

}
