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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.orcs.rest.internal.search.artifact.PredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.AttributeTypePredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.ExistsTypePredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.GuidsPredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.IdsPredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.IsOfTypePredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.TypeEqualsPredicateHandler;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMethod;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public class DslFactory {

   private static SearchQueryBuilder builder;

   public synchronized static SearchQueryBuilder createQueryBuilder() {
      if (builder == null) {
         Map<SearchMethod, PredicateHandler> handlers = DslFactory.getHandlers();
         builder = new SearchQueryBuilder(handlers);
      }
      return builder;
   }

   public static Map<SearchMethod, PredicateHandler> getHandlers() {
      Map<SearchMethod, PredicateHandler> handlers = new HashMap<SearchMethod, PredicateHandler>();
      handlers.put(SearchMethod.IDS, new IdsPredicateHandler());
      handlers.put(SearchMethod.GUIDS, new GuidsPredicateHandler());
      handlers.put(SearchMethod.IS_OF_TYPE, new IsOfTypePredicateHandler());
      handlers.put(SearchMethod.TYPE_EQUALS, new TypeEqualsPredicateHandler());
      handlers.put(SearchMethod.EXISTS_TYPE, new ExistsTypePredicateHandler());
      handlers.put(SearchMethod.ATTRIBUTE_TYPE, new AttributeTypePredicateHandler());
      return handlers;
   }
}