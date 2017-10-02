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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;
import org.eclipse.osee.orcs.db.internal.search.tagger.HasTagProcessor;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagProcessor;

/**
 * @author Roberto E. Escobar
 */
public class SqlHandlerFactoryImpl implements SqlHandlerFactory {

   private static final SqlHandlerComparator HANDLER_COMPARATOR = new SqlHandlerComparator();

   private final Map<Class<? extends Criteria>, Class<? extends SqlHandler<?>>> handleMap;

   private final Log logger;
   private final IdentityLocator idService;
   private final TagProcessor tagProcessor;

   public SqlHandlerFactoryImpl(Log logger, IdentityLocator idService, TagProcessor tagProcessor, Map<Class<? extends Criteria>, Class<? extends SqlHandler<?>>> handleMap) {
      this.logger = logger;
      this.idService = idService;
      this.handleMap = handleMap;
      this.tagProcessor = tagProcessor;
   }

   @Override
   public List<SqlHandler<?>> createHandlers(CriteriaSet... criteriaSet) {
      return createHandlers(Arrays.asList(criteriaSet));
   }

   @Override
   public List<SqlHandler<?>> createHandlers(Iterable<CriteriaSet> criteriaSets) {
      List<SqlHandler<?>> handlers = new ArrayList<>();
      int level = 0;
      for (CriteriaSet criteriaSet : criteriaSets) {
         addHandlers(handlers, level, criteriaSet);
         level++;
      }
      Collections.sort(handlers, HANDLER_COMPARATOR);
      return handlers;
   }

   private void addHandlers(List<SqlHandler<?>> handlers, int index, CriteriaSet criteriaSet) {
      for (Criteria criteria : criteriaSet) {
         SqlHandler<?> handler = createHandler(criteria);
         if (handler != null) {
            handler.setLevel(index);
            handlers.add(handler);
         }
      }
   }

   @Override
   @SuppressWarnings({"unchecked", "rawtypes"})
   public SqlHandler<?> createHandler(Criteria criteria) {
      Class<? extends Criteria> key = criteria.getClass();
      Class<? extends SqlHandler> item = handleMap.get(key);
      SqlHandler<?> toReturn = null;
      if (item != null) {
         toReturn = createHandler(criteria, item);
      }
      return toReturn;
   }

   private <C extends Criteria, H extends SqlHandler<C>> SqlHandler<C> createHandler(C criteria, Class<H> item) {
      SqlHandler<C> handler = null;
      try {
         handler = item.newInstance();
         handler.setData(criteria);
         handler.setIdentityService(idService);
         handler.setLogger(logger);
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }

      if (handler instanceof HasTagProcessor) {
         ((HasTagProcessor) handler).setTagProcessor(tagProcessor);
      }
      return handler;
   }

}
