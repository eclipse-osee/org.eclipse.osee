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
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.services.TempCachingService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsTypes;

/**
 * @author Roberto E. Escobar
 */
public class CacheUpdateCommand implements ConsoleCommand {

   private static enum CacheType {
      BRANCH_CACHE,
      TRANSACTION_CACHE,
      TYPE_CACHE;
   }

   private OrcsApi orcsApi;
   private TempCachingService cachingService;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public OrcsApi getOrcsApi() {
      return orcsApi;
   }

   public TempCachingService getCachingService() {
      return cachingService;
   }

   public void setCachingService(TempCachingService cachingService) {
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
      builder.append(Arrays.deepToString(CacheType.values()).replaceAll(",", " | "));
      builder.append("> - Caches to update.\n");
      return builder.toString();
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      return new CacheUpdateCallable(console, params, true);
   }

   private class CacheUpdateCallable extends CancellableCallable<Boolean> {

      private final Console console;
      private final ConsoleParameters params;
      private final boolean reload;
      private final String verb;

      public CacheUpdateCallable(Console console, ConsoleParameters params, boolean reload) {
         this.console = console;
         this.params = params;
         this.reload = reload;
         this.verb = reload ? "Reloaded" : "Cleared";
      }

      private Set<CacheType> getSelectedType() {
         Set<CacheType> cacheIds = new HashSet<CacheType>();
         for (String cacheId : params.getArray("cacheId")) {
            cacheIds.add(CacheType.valueOf(cacheId.toUpperCase()));
         }
         if (cacheIds.isEmpty()) {
            for (CacheType type : CacheType.values()) {
               cacheIds.add(type);
            }
         }
         return cacheIds;
      }

      private OrcsTypes getOrcTypes() {
         return getOrcsApi().getOrcsTypes(new ApplicationContext() {

            @Override
            public String getSessionId() {
               return "Update Cache Console Command";
            }
         });
      }

      @Override
      public Boolean call() throws OseeCoreException {
         OrcsTypes orcsTypes = getOrcTypes();

         Set<CacheType> selectedType = getSelectedType();
         for (CacheType type : selectedType) {
            OseeCacheEnum cacheId = null;

            if (CacheType.BRANCH_CACHE == type) {
               cacheId = OseeCacheEnum.BRANCH_CACHE;
            } else if (CacheType.TRANSACTION_CACHE == type) {
               cacheId = OseeCacheEnum.TRANSACTION_CACHE;
            }

            if (cacheId != null) {
               IOseeCache<?, ?> cache = cachingService.getCache(cacheId);
               if (reload) {
                  cache.reloadCache();
               } else {
                  cache.decacheAll();
               }
            } else {
               orcsTypes.invalidateAll();
            }
         }
         console.writeln("%s %s", verb, selectedType);
         return Boolean.TRUE;
      }
   }

}