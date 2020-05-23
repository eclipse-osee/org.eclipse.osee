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

package org.eclipse.osee.orcs.rest.internal.search.artifact.predicate;

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.internal.search.artifact.PredicateHandler;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMethod;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public class GuidsPredicateHandler implements PredicateHandler {

   @Override
   public QueryBuilder handle(OrcsApi orcsApi, QueryBuilder builder, Predicate predicate) {
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
