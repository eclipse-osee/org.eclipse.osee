/*
 * Created on Oct 20, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.mock;

import org.eclipse.osee.event.EventService;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelingService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.DataStoreTypeCache;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeClassResolver;
import org.eclipse.osee.orcs.db.mock.OsgiUtil;
import org.osgi.service.event.EventAdmin;

public final class Utility {

   private Utility() {
      // Utility Class
   }

   public static void checkRequiredServices() throws OseeCoreException {
      OsgiUtil.getService(Log.class);
      OsgiUtil.getService(SystemPreferences.class);
      OsgiUtil.getService(IdentityService.class);
      OsgiUtil.getService(IOseeDatabaseService.class);
      OsgiUtil.getService(IOseeModelFactoryService.class);
      OsgiUtil.getService(IOseeModelingService.class);
      OsgiUtil.getService(EventAdmin.class);
      OsgiUtil.getService(EventService.class);
      OsgiUtil.getService(IOseeCachingService.class);
      OsgiUtil.getService(QueryEngine.class);
      OsgiUtil.getService(DataStoreTypeCache.class);
      OsgiUtil.getService(DataLoader.class);
      OsgiUtil.getService(AttributeClassResolver.class);
   }

}
