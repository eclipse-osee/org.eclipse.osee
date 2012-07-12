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
package org.eclipse.osee.orcs.rest.internal.search.dsl;

import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.rest.internal.search.Predicate;
import org.eclipse.osee.orcs.rest.internal.search.PredicateHandler;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public class SearchDsl {

   public static interface DslTranslator {

      List<Predicate> translate(String rawString) throws OseeCoreException;

   }

   private final Map<SearchMethod, PredicateHandler> handlers;
   private final DslTranslator translator;
   private List<Predicate> predicates;

   public SearchDsl(Map<SearchMethod, PredicateHandler> handlers, DslTranslator translator) {
      this.handlers = handlers;
      this.translator = translator;
   }

   public QueryBuilder build(QueryFactory queryFactory, IOseeBranch branch, String rawQuery) throws OseeCoreException {
      Conditions.checkNotNull(queryFactory, "queryFactory");
      Conditions.checkNotNull(branch, "branch");
      Conditions.checkNotNull(rawQuery, "rawQuery");
      predicates = translator.translate(rawQuery);
      QueryBuilder builder = queryFactory.fromBranch(branch);
      for (Predicate predicate : predicates) {
         SearchMethod method = predicate.getType();
         if (handlers.containsKey(method)) {
            PredicateHandler handler = handlers.get(method);
            builder = handler.handle(builder, predicate);
         }
      }
      return builder;
   }

   public List<Predicate> getPredicates() {
      return predicates;
   }

}
