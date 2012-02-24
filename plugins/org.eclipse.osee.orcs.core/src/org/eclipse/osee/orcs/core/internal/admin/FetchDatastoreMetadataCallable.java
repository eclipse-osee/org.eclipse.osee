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

import java.util.concurrent.Callable;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsMetaData;
import org.eclipse.osee.orcs.core.ds.DataStoreAdmin;
import org.eclipse.osee.orcs.core.ds.DataStoreInfo;
import org.eclipse.osee.orcs.core.internal.SessionContext;

/**
 * @author Roberto E. Escobar
 */
public class FetchDatastoreMetadataCallable extends AbstractAdminCallable<OrcsMetaData> {

   private final DataStoreAdmin dataStoreAdmin;

   public FetchDatastoreMetadataCallable(Log logger, SessionContext sessionContext, DataStoreAdmin dataStoreAdmin) {
      super(logger, sessionContext);
      this.dataStoreAdmin = dataStoreAdmin;
   }

   @Override
   protected OrcsMetaData innerCall() throws Exception {
      String sessionId = getSessionContext().getSessionId();
      Callable<DataStoreInfo> callable = dataStoreAdmin.getDataStoreInfo(sessionId);
      DataStoreInfo dataStoreInfo = callAndCheckForCancel(callable);

      OrcsMetaData orcsMetaData = null;

      return orcsMetaData;
   }

}
