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
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.rest.internal.search.PredicateHandler;
import org.eclipse.osee.orcs.rest.model.search.Predicate;
import org.eclipse.osee.orcs.rest.model.search.SearchMethod;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public class GuidsPredicateHandler implements PredicateHandler {

   @Override
   public QueryBuilder handle(QueryBuilder builder, Predicate predicate) throws OseeCoreException {
      if (predicate.getType() != SearchMethod.GUIDS) {
         throw new OseeArgumentException("This predicate handler only supports [%s]", SearchMethod.GUIDS);
      }
      Collection<String> values = predicate.getValues();

      Conditions.checkNotNull(values, "values");

      if (!values.isEmpty()) {
         builder.andGuids(values);
      }

      return builder;
   }

}
