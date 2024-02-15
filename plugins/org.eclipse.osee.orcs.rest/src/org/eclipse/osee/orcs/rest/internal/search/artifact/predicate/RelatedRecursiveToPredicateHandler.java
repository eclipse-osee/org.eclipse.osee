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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.internal.search.artifact.PredicateHandler;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMethod;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * Implementation of a {@link PredicateHandler} for finding artifacts that are recursively related.
 *
 * @author Loren K. Ashley
 */

public class RelatedRecursiveToPredicateHandler implements PredicateHandler {

   @Override
   public QueryBuilder handle(OrcsApi orcsApi, QueryBuilder builder, Predicate predicate) {

      if (predicate.getType() != SearchMethod.RELATED_RECURSIVE_TO) {
         throw new OseeArgumentException("This predicate handler only supports [%s]",
            SearchMethod.RELATED_RECURSIVE_TO);
      }

      var typeParameters = predicate.getTypeParameters();
      var values = predicate.getValues();

      Conditions.checkNotNull(typeParameters, "typeParameters");
      Conditions.checkNotNull(values, "values");

      if ((typeParameters.size() != 1) || (values.size() != 1)) {
         return builder;
      }

      var relationTypeSide =
         PredicateHandlerUtil.getRelationTypeSides(typeParameters, orcsApi.tokenService()).iterator().next();

      var value = values.get(0);

      if (GUID.isValid(value)) {
         throw new UnsupportedOperationException();
      }

      var artifactIdentifier = ArtifactId.valueOf(value);

      return builder.andRelatedRecursive(relationTypeSide, artifactIdentifier);
   }
}

/* EOF */
