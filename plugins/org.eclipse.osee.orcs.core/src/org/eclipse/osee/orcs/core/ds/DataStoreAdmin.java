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
package org.eclipse.osee.orcs.core.ds;

import java.util.concurrent.Callable;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public interface DataStoreAdmin {

   public static final String SCHEMA_TABLE_DATA_NAMESPACE = "schema.table.data.namespace";
   public static final String SCHEMA_INDEX_DATA_NAMESPACE = "schema.index.data.namespace";
   public static final String SCHEMA_USER_FILE_SPECIFIED_NAMESPACE = "schema.user.file.specified.schema.names";

   DataStoreInfo createDataStore();

   Callable<DataStoreInfo> getDataStoreInfo(OrcsSession session);

   boolean isDataStoreInitialized();

   Callable<DataStoreInfo> migrateDataStore(OrcsSession session);

}
