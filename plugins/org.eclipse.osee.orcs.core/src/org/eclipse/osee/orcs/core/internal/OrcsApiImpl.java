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
package org.eclipse.osee.orcs.core.internal;

import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OseeApi;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeClassResolver;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.search.CriteriaFactory;
import org.eclipse.osee.orcs.core.internal.search.QueryFactoryImpl;
import org.eclipse.osee.orcs.core.internal.search.ResultSetFactory;
import org.eclipse.osee.orcs.core.internal.session.SessionContextImpl;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Roberto E. Escobar
 */
public class OrcsApiImpl implements OseeApi {

   private Log logger;
   private QueryEngine queryEngine;
   private DataLoader dataLoader;
   private AttributeClassResolver resolver;
   private IOseeCachingService cacheService;

   private OrcsObjectLoader objectLoader;
   private CriteriaFactory criteriaFctry;
   private ResultSetFactory rsetFctry;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setQueryEngine(QueryEngine queryEngine) {
      this.queryEngine = queryEngine;
   }

   public void setAttributeClassResolver(AttributeClassResolver resolver) {
      this.resolver = resolver;
   }

   public void setDataLoader(DataLoader dataLoader) {
      this.dataLoader = dataLoader;
   }

   public void setCacheService(IOseeCachingService cacheService) {
      this.cacheService = cacheService;
   }

   public void start() {
      ArtifactFactory artifactFactory = new ArtifactFactory();
      AttributeFactory attributeFactory = new AttributeFactory(logger, resolver, cacheService.getAttributeTypeCache());
      objectLoader =
         new OrcsObjectLoader(logger, dataLoader, artifactFactory, attributeFactory,
            cacheService.getArtifactTypeCache(), cacheService.getBranchCache());

      criteriaFctry = new CriteriaFactory();
      rsetFctry = new ResultSetFactory(queryEngine, objectLoader);
   }

   public void stop() {
      criteriaFctry = null;
      rsetFctry = null;
      objectLoader = null;
   }

   @Override
   public QueryFactory getQueryFactory(ApplicationContext context) {
      String sessionId = GUID.create(); // TODO context.getSessionId() attach to application context
      SessionContext sessionContext = getSessionContext(sessionId);
      return new QueryFactoryImpl(criteriaFctry, rsetFctry, sessionContext);
   }

   private SessionContext getSessionContext(String sessionId) {
      // TODO get sessions from a session context cache
      return new SessionContextImpl(sessionId);
   }

   @Override
   public BranchCache getBranchCache() {
      return cacheService.getBranchCache();
   }
}
