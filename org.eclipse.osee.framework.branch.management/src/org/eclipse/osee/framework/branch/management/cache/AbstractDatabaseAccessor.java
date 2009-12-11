/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.cache;

import org.eclipse.osee.framework.core.cache.IOseeDataAccessor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.IOseeStorable;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.IOseeSequence;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractDatabaseAccessor<T extends IOseeStorable> implements IOseeDataAccessor<T> {

   private final IOseeDatabaseServiceProvider databaseProvider;
   private final IOseeModelFactoryServiceProvider factoryProvider;

   protected AbstractDatabaseAccessor(IOseeDatabaseServiceProvider databaseProvider, IOseeModelFactoryServiceProvider factoryProvider) {
      this.databaseProvider = databaseProvider;
      this.factoryProvider = factoryProvider;
   }

   protected IOseeDatabaseServiceProvider getDatabaseServiceProvider() {
      return databaseProvider;
   }

   protected IOseeDatabaseService getDatabaseService() throws OseeDataStoreException {
      return getDatabaseServiceProvider().getOseeDatabaseService();
   }

   protected IOseeSequence getSequence() throws OseeDataStoreException {
      return getDatabaseService().getSequence();
   }

   protected IOseeModelFactoryService getFactoryService() throws OseeCoreException {
      return factoryProvider.getOseeFactoryService();
   }
}
