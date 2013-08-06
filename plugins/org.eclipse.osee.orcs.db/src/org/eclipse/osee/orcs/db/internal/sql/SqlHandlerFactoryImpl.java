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
package org.eclipse.osee.orcs.db.internal.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.jdk.core.util.PriorityComparator;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.db.internal.search.tagger.HasTagProcessor;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagProcessor;

/**
 * @author Roberto E. Escobar
 */
public class SqlHandlerFactoryImpl implements SqlHandlerFactory {

   private static final PriorityComparator comparator = new PriorityComparator();

   private final Map<Class<? extends Criteria>, Class<? extends SqlHandler<?>>> handleMap;

   private final Log logger;
   private final IdentityService idService;
   private final TagProcessor tagProcessor;

   public SqlHandlerFactoryImpl(Log logger, IdentityService idService, Map<Class<? extends Criteria>, Class<? extends SqlHandler<?>>> handleMap) {
      this(logger, idService, null, handleMap);
   }

   public SqlHandlerFactoryImpl(Log logger, IdentityService idService, TagProcessor tagProcessor, Map<Class<? extends Criteria>, Class<? extends SqlHandler<?>>> handleMap) {
      this.logger = logger;
      this.idService = idService;
      this.handleMap = handleMap;
      this.tagProcessor = tagProcessor;
   }

   @Override
   public List<SqlHandler<?>> createHandlers(CriteriaSet criteriaSet) throws OseeCoreException {
      List<SqlHandler<?>> handlers = new ArrayList<SqlHandler<?>>();
      for (Criteria criteria : criteriaSet) {
         SqlHandler<?> handler = createHandler(criteria);
         if (handler != null) {
            handlers.add(handler);
         }
      }
      Collections.sort(handlers, comparator);
      return handlers;
   }

   @Override
   @SuppressWarnings({"unchecked", "rawtypes"})
   public SqlHandler<?> createHandler(Criteria criteria) throws OseeCoreException {
      Class<? extends Criteria> key = criteria.getClass();
      Class<? extends SqlHandler> item = handleMap.get(key);
      SqlHandler<?> toReturn = null;
      if (item != null) {
         toReturn = createHandler(criteria, item);
      }
      return toReturn;
   }

   private <C extends Criteria, H extends SqlHandler<C>> SqlHandler<C> createHandler(C criteria, Class<H> item) throws OseeCoreException {
      SqlHandler<C> handler = null;
      try {
         handler = item.newInstance();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }

      handler.setData(criteria);
      handler.setIdentityService(idService);
      handler.setLogger(logger);

      if (handler instanceof HasTagProcessor) {
         ((HasTagProcessor) handler).setTagProcessor(tagProcessor);
      }
      return handler;
   }

}
