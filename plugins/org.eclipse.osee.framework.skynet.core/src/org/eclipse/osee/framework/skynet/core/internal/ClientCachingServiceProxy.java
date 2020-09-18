/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.skynet.core.internal;

import com.google.common.io.ByteSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.skynet.core.internal.accessors.DatabaseBranchAccessor;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.rest.client.OseeClient;

/**
 * @author Roberto E. Escobar
 */
public class ClientCachingServiceProxy implements IOseeCachingService {

   public static interface TypesLoader {
      void loadTypes(IOseeCachingService service, ByteSource supplier);
   }

   private JdbcService jdbcService;
   private OseeClient oseeClient;
   private OrcsTokenService tokenService;
   private BranchCache branchCache;

   private List<IOseeCache<?>> caches;

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   public void setOseeClient(OseeClient oseeClient) {
      this.oseeClient = oseeClient;
   }

   public void setOrcsTokenService(OrcsTokenService tokenService) {
      this.tokenService = tokenService;
   }

   public void start() {
      JdbcClient jdbcClient = jdbcService.getClient();
      branchCache = new BranchCache(new DatabaseBranchAccessor(jdbcClient));
      caches = new ArrayList<>();
      caches.add(branchCache);
   }

   public void stop() {
      caches.clear();
      branchCache = null;
   }

   @Override
   public BranchCache getBranchCache() {
      return branchCache;
   }

   @Override
   public Collection<?> getCaches() {
      return caches;
   }

   @Override
   public OrcsTokenService getTokenService() {
      return tokenService;
   }

   @Override
   public void clearAll() {
      getBranchCache().decacheAll();
   }
}