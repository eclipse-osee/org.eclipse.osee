/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.internal.search.artifact.PredicateHandler;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMethod;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * Implementation of a {@link PredicateHandler} for finding artifacts that have a matching transaction comment.
 *
 * @author Loren K. Ashley
 */

public class TransactionCommentPredicateHandler implements PredicateHandler {

   @Override
   public QueryBuilder handle(OrcsApi orcsApi, QueryBuilder builder, Predicate predicate) {

      if (predicate.getType() != SearchMethod.TRANSACTION_COMMENT) {
         throw new OseeArgumentException("This predicate handler only supports [%s]", SearchMethod.TRANSACTION_COMMENT);
      }

      var values = predicate.getValues();

      Conditions.checkNotNull(values, "values");

      if (values.size() != 1) {
         return builder;
      }

      return builder.andTxComment(values.get(0), CoreAttributeTypes.NameWord);
   }

}
