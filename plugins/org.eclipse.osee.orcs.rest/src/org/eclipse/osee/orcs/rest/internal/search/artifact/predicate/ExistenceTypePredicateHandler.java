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
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.RelationSide;
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
public class ExistenceTypePredicateHandler implements PredicateHandler {

   @Override
   public QueryBuilder handle(QueryBuilder builder, Predicate predicate) {
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
            Collection<AttributeTypeId> attributeTypes = PredicateHandlerUtil.getAttributeTypes(values);
            if (!attributeTypes.isEmpty()) {
               if (checkExists(predicate.getType())) {
                  builder.andExists(attributeTypes);
               } else {
                  builder.andNotExists(attributeTypes);
               }
            }
         } else if ("relType".equals(existsType)) {
            Collection<IRelationType> iRelationTypes = PredicateHandlerUtil.getIRelationTypes(values);
            for (IRelationType rt : iRelationTypes) {
               if (checkExists(predicate.getType())) {
                  builder.andExists(rt);
               } else {
                  builder.andNotExists(rt);
               }
            }
         } else if ("relTypeSide".equals(existsType)) {
            RelationSide side = typeParameters.get(1).equals("A") ? RelationSide.SIDE_A : RelationSide.SIDE_B;
            for (IRelationType rt : PredicateHandlerUtil.getIRelationTypes(values)) {
               RelationTypeSide rts = RelationTypeSide.create(side, rt.getId(), "SearchRelTypeSide");
               if (checkExists(predicate.getType())) {
                  builder.andExists(rts);
               } else {
                  builder.andNotExists(rts);
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
