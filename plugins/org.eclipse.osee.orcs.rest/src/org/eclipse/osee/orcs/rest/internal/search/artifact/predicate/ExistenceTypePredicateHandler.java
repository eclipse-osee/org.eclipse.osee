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
import java.util.List;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
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
public class ExistenceTypePredicateHandler implements PredicateHandler {

   @Override
   public QueryBuilder handle(OrcsApi orcsApi, QueryBuilder builder, Predicate predicate) {
      OrcsTokenService tokenService = orcsApi.tokenService();
      if (!predicate.getType().isOfType(SearchMethod.EXISTS_TYPE, SearchMethod.NOT_EXISTS_TYPE)) {
         throw new OseeArgumentException("This predicate handler only supports [%s] and [%s]", SearchMethod.EXISTS_TYPE,
            SearchMethod.NOT_EXISTS_TYPE);
      }
      List<String> typeParameters = predicate.getTypeParameters();
      Collection<String> values = predicate.getValues();

      Conditions.checkNotNullOrEmpty(typeParameters, "typeParameters");
      Conditions.checkNotNull(values, "values");

      if (typeParameters.size() >= 1) {
         String existsType = typeParameters.get(0);
         if ("attrType".equals(existsType)) {
            Collection<AttributeTypeToken> attributeTypes = PredicateHandlerUtil.getAttributeTypes(values);
            if (!attributeTypes.isEmpty()) {
               if (checkExists(predicate.getType())) {
                  builder.andExists(attributeTypes);
               } else {
                  builder.andNotExists(attributeTypes);
               }
            }
         } else if ("relType".equals(existsType)) {
            for (RelationTypeToken rt : PredicateHandlerUtil.getIRelationTypes(values, tokenService)) {
               if (checkExists(predicate.getType())) {
                  builder.andRelationExists(rt);
               } else {
                  builder.andRelationNotExists(rt);
               }
            }
         } else if ("relTypeSide".equals(existsType)) {
            RelationSide side = typeParameters.get(1).equals("A") ? RelationSide.SIDE_A : RelationSide.SIDE_B;
            for (RelationTypeToken rt : PredicateHandlerUtil.getIRelationTypes(values, tokenService)) {
               RelationTypeSide relationTypeSide = new RelationTypeSide(rt, side);
               if (checkExists(predicate.getType())) {
                  builder.andRelationExists(relationTypeSide);
               } else {
                  builder.andRelationNotExists(rt);
               }
            }
         }
      }

      return builder;
   }

   private boolean checkExists(SearchMethod method) {
      return method == SearchMethod.EXISTS_TYPE;
   }

}
