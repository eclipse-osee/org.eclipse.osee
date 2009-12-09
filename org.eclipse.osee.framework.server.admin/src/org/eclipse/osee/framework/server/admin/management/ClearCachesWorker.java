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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.server.admin.Activator;
import org.eclipse.osee.framework.server.admin.BaseServerCommand;

/**
 * @author Roberto E. Escobar
 */
public class ClearCachesWorker extends BaseServerCommand {

   protected ClearCachesWorker() {
      super("Clear Cache(s)");
   }

   @Override
   protected void doCommandWork(IProgressMonitor monitor) throws Exception {
      IOseeCachingService service = Activator.getInstance().getOseeCachingService();
      Collection<OseeCacheEnum> cacheIds = getSelectedCaches();
      if (cacheIds.isEmpty()) {
         service.clearAll();
         println(String.format("Cleared the following caches: %s", Arrays.deepToString(OseeCacheEnum.values())));
      } else {
         for (OseeCacheEnum cacheId : cacheIds) {
            service.getCache(cacheId).decacheAll();
         }
         println(String.format("Cleared %s", cacheIds));
      }
   }

   private Collection<OseeCacheEnum> getSelectedCaches() {
      Set<OseeCacheEnum> caches = new HashSet<OseeCacheEnum>();

      boolean hasArgument = true;
      while (hasArgument) {
         String value = getCommandInterpreter().nextArgument();
         if (Strings.isValid(value)) {
            caches.add(OseeCacheEnum.valueOf(value));
         } else {
            hasArgument = false;
         }
      }
      return caches;
   }
}
