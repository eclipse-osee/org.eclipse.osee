/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.search.artifact.dsl;

import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.rest.internal.search.artifact.PredicateHandler;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMethod;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchRequest;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public class SearchQueryBuilder {

   private final Map<SearchMethod, PredicateHandler> handlers;

   public SearchQueryBuilder(Map<SearchMethod, PredicateHandler> handlers) {
      this.handlers = handlers;
   }

   public QueryBuilder build(QueryFactory queryFactory, SearchRequest params)  {
      Conditions.checkNotNull(queryFactory, "queryFactory");
      Conditions.checkNotNull(params, "params");
      QueryBuilder builder = queryFactory.fromBranch(params.getBranch());
      List<Predicate> predicates = params.getPredicates();
      if (predicates != null) {
         for (Predicate predicate : predicates) {
            SearchMethod method = predicate.getType();
            PredicateHandler handler = handlers.get(method);
            if (handler != null) {
               builder = handler.handle(builder, predicate);
            } else {
               throw new OseeArgumentException("Unable to find PredicateHandler for %s", method.name());
            }
         }
      }
      return builder;
   }

}
