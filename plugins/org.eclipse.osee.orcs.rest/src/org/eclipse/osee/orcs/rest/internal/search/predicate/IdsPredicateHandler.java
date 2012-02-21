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
package org.eclipse.osee.orcs.rest.internal.search.predicate;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.rest.internal.search.Predicate;
import org.eclipse.osee.orcs.rest.internal.search.PredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchMethod;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public class IdsPredicateHandler implements PredicateHandler {

   @Override
   public QueryBuilder handle(QueryBuilder builder, Predicate predicate) throws OseeCoreException {
      if (predicate.getType() != SearchMethod.IDS) {
         throw new OseeArgumentException("This predicate handler only supports [%s]", SearchMethod.IDS);
      }
      QueryBuilder theBuilder = builder;
      List<String> values = predicate.getValues();

      Conditions.checkNotNull(values, "values");

      Collection<String> guids = new HashSet<String>();
      Collection<Integer> rawIds = new HashSet<Integer>();
      for (String value : values) {
         if (value.matches("\\d+")) {
            rawIds.add(Integer.parseInt(value));
         } else {
            guids.add(value);
         }
      }

      if (!guids.isEmpty()) {
         theBuilder = addGuids(builder, guids);
      }

      if (!rawIds.isEmpty()) {
         theBuilder = addIds(builder, rawIds);
      }
      return theBuilder;
   }

   protected QueryBuilder addGuids(QueryBuilder builder, Collection<String> guids) throws OseeCoreException {
      return builder.andGuidsOrHrids(guids);
   }

   protected QueryBuilder addIds(QueryBuilder builder, Collection<Integer> rawIds) throws OseeCoreException {
      return builder.andLocalIds(rawIds);
   }
}
