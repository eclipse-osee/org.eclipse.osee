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

package org.eclipse.osee.orcs.rest.internal.search.artifact;

import java.util.Collection;
import java.util.LinkedHashSet;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.PredicateHandlerUtil;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public interface PredicateHandler {
   QueryBuilder handle(OrcsApi orcsApi, QueryBuilder builder, Predicate predicate);

   default Collection<ArtifactTypeToken> getArtifactTypeTokens(OrcsTokenService tokenService, Predicate predicate) {
      Collection<String> types = predicate.getValues();
      Conditions.checkNotNull(types, "types");

      Collection<ArtifactTypeToken> artTypes = new LinkedHashSet<>();
      for (String value : types) {
         long uuid = PredicateHandlerUtil.parseUuid(value);
         if (uuid != -1L) {
            artTypes.add(tokenService.getArtifactType(uuid));
         }
      }
      return artTypes;
   }
}