/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.server.admin.management;

import java.util.Arrays;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.server.admin.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class UpdateCachesOperation extends AbstractOperation {
   private final Set<OseeCacheEnum> cacheIds;
   private final boolean reload;
   private final String verb;

   public UpdateCachesOperation(OperationLogger logger, Set<OseeCacheEnum> cacheIds, boolean reload) {
      super("Clear Cache(s)", Activator.PLUGIN_ID, logger);
      this.cacheIds = cacheIds;
      this.reload = reload;
      this.verb = reload ? "Reloaded" : "Cleared";
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      IOseeCachingService service = Activator.getOseeCachingService();
      if (cacheIds.isEmpty()) {
         if (reload) {
            service.reloadAll();
         } else {
            service.clearAll();
         }
         logf("%s the following caches: %s", verb,
            Arrays.deepToString(OseeCacheEnum.values()).replaceAll(", SESSION_CACHE", ""));
      } else {
         for (OseeCacheEnum cacheId : cacheIds) {
            IOseeCache<?> cache = service.getCache(cacheId);
            if (reload) {
               cache.reloadCache();
            } else {
               cache.decacheAll();
            }
         }
         logf("%s %s", verb, cacheIds);
      }
   }
}