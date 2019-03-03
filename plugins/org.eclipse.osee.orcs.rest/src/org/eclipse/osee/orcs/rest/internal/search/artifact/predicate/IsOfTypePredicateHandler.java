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
package org.eclipse.osee.orcs.rest.internal.search.artifact.predicate;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.rest.internal.search.artifact.PredicateHandler;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMethod;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public class IsOfTypePredicateHandler implements PredicateHandler {

   @Override
   public QueryBuilder handle(QueryBuilder builder, Predicate predicate) {
      if (predicate.getType() != SearchMethod.IS_OF_TYPE) {
         throw new OseeArgumentException("This predicate handler only supports [%s]", SearchMethod.IS_OF_TYPE);
      }
      Collection<String> values = predicate.getValues();
      Conditions.checkNotNull(values, "values");
      Collection<ArtifactTypeToken> artTypes = PredicateHandlerUtil.getArtifactTypeTokens(values);
      if (!artTypes.isEmpty()) {
         builder.andIsOfType(artTypes);
      }
      return builder;
   }

}
