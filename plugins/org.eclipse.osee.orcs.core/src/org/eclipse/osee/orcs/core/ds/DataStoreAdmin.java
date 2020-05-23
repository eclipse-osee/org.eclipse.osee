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

package org.eclipse.osee.orcs.core.ds;

import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public interface DataStoreAdmin {

   public static final String SCHEMA_TABLE_DATA_NAMESPACE = "schema.table.data.namespace";
   public static final String SCHEMA_INDEX_DATA_NAMESPACE = "schema.index.data.namespace";
   public static final String SCHEMA_USER_FILE_SPECIFIED_NAMESPACE = "schema.user.file.specified.schema.names";

   void createDataStore();

   Callable<DataStoreInfo> getDataStoreInfo(OrcsSession session);

   boolean isDataStoreInitialized();

   Callable<DataStoreInfo> migrateDataStore(OrcsSession session);

   JdbcClient getJdbcClient();

   void updateBootstrapUser(UserId accountId);

}
