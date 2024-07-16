/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.rest.internal.search.artifact.dsl;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.internal.search.artifact.PredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.AttributeTypePredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.ExistenceTypePredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.GuidsPredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.IdsPredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.IsOfTypePredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.RelatedRecursiveToPredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.RelatedToPredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.TransactionCommentPredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.TypeEqualsPredicateHandler;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMethod;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public class DslFactory {
   private static SearchQueryBuilder builder;

   public synchronized static SearchQueryBuilder createQueryBuilder(OrcsApi orcsApi) {
      if (builder == null) {
         Map<SearchMethod, PredicateHandler> handlers = DslFactory.getHandlers();
         builder = new SearchQueryBuilder(orcsApi, handlers);
      }
      return builder;
   }

   public static Map<SearchMethod, PredicateHandler> getHandlers() {
      Map<SearchMethod, PredicateHandler> handlers = new HashMap<>();
      handlers.put(SearchMethod.IDS, new IdsPredicateHandler());
      handlers.put(SearchMethod.GUIDS, new GuidsPredicateHandler());
      handlers.put(SearchMethod.IS_OF_TYPE, new IsOfTypePredicateHandler());
      handlers.put(SearchMethod.TYPE_EQUALS, new TypeEqualsPredicateHandler());
      handlers.put(SearchMethod.EXISTS_TYPE, new ExistenceTypePredicateHandler());
      handlers.put(SearchMethod.NOT_EXISTS_TYPE, new ExistenceTypePredicateHandler());
      handlers.put(SearchMethod.ATTRIBUTE_TYPE, new AttributeTypePredicateHandler());
      handlers.put(SearchMethod.RELATED_TO, new RelatedToPredicateHandler());
      handlers.put(SearchMethod.RELATED_RECURSIVE_TO, new RelatedRecursiveToPredicateHandler());
      handlers.put(SearchMethod.TRANSACTION_COMMENT, new TransactionCommentPredicateHandler());
      return handlers;
   }
}