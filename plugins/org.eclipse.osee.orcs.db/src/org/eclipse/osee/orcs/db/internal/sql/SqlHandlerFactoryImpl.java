/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.db.internal.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.db.internal.search.tagger.HasTagProcessor;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagProcessor;

/**
 * @author Roberto E. Escobar
 */
public class SqlHandlerFactoryImpl implements SqlHandlerFactory {

   private static final SqlHandlerComparator HANDLER_COMPARATOR = new SqlHandlerComparator();

   private final Map<Class<? extends Criteria>, Class<? extends SqlHandler<?>>> handleMap;

   private final TagProcessor tagProcessor;

   public SqlHandlerFactoryImpl(TagProcessor tagProcessor, Map<Class<? extends Criteria>, Class<? extends SqlHandler<?>>> handleMap) {
      this.handleMap = handleMap;
      this.tagProcessor = tagProcessor;
   }

   @Override
   public List<SqlHandler<?>> createHandlers(QueryData queryData) {
      List<SqlHandler<?>> handlers = new ArrayList<>();
      int level = 0;
      for (List<Criteria> criteriaSet : queryData.getCriteriaSets()) {
         addHandlers(handlers, level, criteriaSet);
         level++;
      }
      Collections.sort(handlers, HANDLER_COMPARATOR);
      return handlers;
   }

   private void addHandlers(List<SqlHandler<?>> handlers, int index, List<Criteria> criteriaSet) {
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
      Class<? extends SqlHandler> handlerClass = handleMap.get(key);
      if (handlerClass == null) {
         throw new OseeStateException("No handler configured for criteria of %s", key);
      }
      return createHandler(criteria, handlerClass);
   }

   private <C extends Criteria, H extends SqlHandler<C>> SqlHandler<C> createHandler(C criteria, Class<H> item) {
      SqlHandler<C> handler = null;
      try {
         handler = item.newInstance();
         handler.setData(criteria);
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }

      if (handler instanceof HasTagProcessor) {
         ((HasTagProcessor) handler).setTagProcessor(tagProcessor);
      }
      return handler;
   }
}