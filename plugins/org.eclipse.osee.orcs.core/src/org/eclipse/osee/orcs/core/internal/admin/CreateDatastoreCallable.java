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
public class CreateDatastoreCallable extends AbstractAdminCallable<OrcsMetaData> {

   private final DataStoreAdmin dataStoreAdmin;
   private final Map<String, String> parameters;

   public CreateDatastoreCallable(Log logger, OrcsSession session, DataStoreAdmin dataStoreAdmin, Map<String, String> parameters) {
      super(logger, session);
      this.dataStoreAdmin = dataStoreAdmin;
      this.parameters = parameters;
   }

   @Override
   protected OrcsMetaData innerCall() throws Exception {
      Callable<DataStoreInfo> callable = dataStoreAdmin.createDataStore(getSession(), parameters);
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
