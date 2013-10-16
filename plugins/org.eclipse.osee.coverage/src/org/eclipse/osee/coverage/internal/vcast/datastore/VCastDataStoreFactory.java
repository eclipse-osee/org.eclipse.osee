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
package org.eclipse.osee.coverage.internal.vcast.datastore;

import java.util.Properties;
import org.eclipse.osee.coverage.internal.vcast.VCastDataStore;
import org.eclipse.osee.coverage.internal.vcast.datastore.VCastDataStoreImpl.StatementProvider;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public class VCastDataStoreFactory {

   private VCastDataStoreFactory() {
      // Static Factory
   }

   private static IOseeDatabaseService getDatabaseService() throws OseeCoreException {
      Bundle bundle = FrameworkUtil.getBundle(VCastDataStoreFactory.class);
      Conditions.checkNotNull(bundle, "bundle", "Error getting bundle to access IOseeDatabaseService");

      BundleContext bundleContext = bundle.getBundleContext();
      Conditions.checkNotNull(bundleContext, "bundleContext",
         "Error getting bundleContext to access IOseeDatabaseService");

      ServiceReference<IOseeDatabaseService> serviceReference =
         bundleContext.getServiceReference(IOseeDatabaseService.class);
      Conditions.checkNotNull(serviceReference, "serviceReference",
         "Error getting serviceReference to access IOseeDatabaseService");
      return bundleContext.getService(serviceReference);
   }

   public static VCastDataStore createDataStore(String dbPath) throws OseeCoreException {
      IOseeDatabaseService dbService = getDatabaseService();
      Conditions.checkNotNull(dbService, "dbService", "Error accessing IOseeDatabaseService");

      String connectionId = GUID.create();
      SqliteDbInfo dbInfo = new SqliteDbInfo(connectionId, dbPath, new Properties());
      StatementProvider provider = new SqliteStatementProvider(dbService, dbInfo);
      return new VCastDataStoreImpl(provider);
   }

}
