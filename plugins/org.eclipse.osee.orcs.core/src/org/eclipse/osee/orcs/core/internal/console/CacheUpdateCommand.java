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
package org.eclipse.osee.orcs.core.internal.console;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.services.IOseeCachingService;

/**
 * @author Roberto E. Escobar
 */
public class CacheUpdateCommand implements ConsoleCommand {

   private static final OseeCacheEnum[] reloadableCaches = {
      OseeCacheEnum.ARTIFACT_TYPE_CACHE,
      OseeCacheEnum.BRANCH_CACHE,
      OseeCacheEnum.TRANSACTION_CACHE};

   private IOseeCachingService cachingService;

   public IOseeCachingService getCachingService() {
      return cachingService;
   }

   public void setCachingService(IOseeCachingService cachingService) {
      this.cachingService = cachingService;
   }

   @Override
   public String getName() {
      return "cache_update";
   }

   @Override
   public String getDescription() {
      return "Updates server caches";
   }

   @Override
   public String getUsage() {
      StringBuilder builder = new StringBuilder();
      builder.append("                 - Update all caches.\n");
      builder.append("cache=<");
      builder.append(Arrays.deepToString(reloadableCaches).replaceAll(",", " | "));
      builder.append("> - Caches to update.\n");
      return builder.toString();
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      Set<OseeCacheEnum> cacheIds = new HashSet<OseeCacheEnum>();
      for (String cacheId : params.getArray("cacheId")) {
         cacheIds.add(OseeCacheEnum.valueOf(cacheId.toUpperCase()));
      }
      if (cacheIds.isEmpty()) {
         cacheIds.addAll(Arrays.asList(reloadableCaches));
      }
      return new CacheUpdateCallable(console, getCachingService(), cacheIds, true);
   }

   private final static class CacheUpdateCallable extends CancellableCallable<Boolean> {

      private final Console console;
      private final IOseeCachingService cachingService;
      private final Set<OseeCacheEnum> cacheIds;
      private final boolean reload;
      private final String verb;

      public CacheUpdateCallable(Console console, IOseeCachingService cachingService, Set<OseeCacheEnum> cacheIds, boolean reload) {
         this.console = console;
         this.cachingService = cachingService;
         this.cacheIds = cacheIds;
         this.reload = reload;
         this.verb = reload ? "Reloaded" : "Cleared";
      }

      @Override
      public Boolean call() throws OseeCoreException {
         if (cacheIds.isEmpty()) {
            if (reload) {
               cachingService.reloadAll();
            } else {
               cachingService.clearAll();
            }
            console.writeln("%s the following caches: %s", verb,
               Arrays.deepToString(OseeCacheEnum.values()).replaceAll(", SESSION_CACHE", ""));
         } else {
            for (OseeCacheEnum cacheId : cacheIds) {
               IOseeCache<?, ?> cache = cachingService.getCache(cacheId);
               if (reload) {
                  cache.reloadCache();
               } else {
                  cache.decacheAll();
               }
            }
            console.writeln("%s %s", verb, cacheIds);
         }
         return Boolean.TRUE;
      }
   }

}