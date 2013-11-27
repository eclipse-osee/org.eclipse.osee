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
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.rest.internal.search.PredicateHandler;
import org.eclipse.osee.orcs.rest.model.search.Predicate;
import org.eclipse.osee.orcs.rest.model.search.SearchMethod;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public class IdsPredicateHandler implements PredicateHandler {

   @Override
   public QueryBuilder handle(QueryBuilder builder, Predicate predicate) throws OseeCoreException {
      if (predicate.getType() != SearchMethod.IDS) {
         throw new OseeArgumentException("This predicate handler only supports [%s]", SearchMethod.IDS);
      }
      Collection<String> values = predicate.getValues();

      Conditions.checkNotNull(values, "values");

      Collection<Integer> rawIds = new HashSet<Integer>();
      for (String value : values) {
         if (value.matches("\\d+")) {
            rawIds.add(Integer.parseInt(value));
         } else {
            throw new OseeArgumentException("Non integer value passed for IDS search: [%s]", value);
         }
      }

      if (!rawIds.isEmpty()) {
         builder.andLocalIds(rawIds);
      }
      return builder;
   }

}
