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
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.rest.internal.search.PredicateHandler;
import org.eclipse.osee.orcs.rest.model.search.Predicate;
import org.eclipse.osee.orcs.rest.model.search.SearchMethod;
import org.eclipse.osee.orcs.rest.model.search.SearchOp;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public class ExistsTypePredicateHandler implements PredicateHandler {

   @Override
   public QueryBuilder handle(QueryBuilder builder, Predicate predicate) throws OseeCoreException {
      if (predicate.getType() != SearchMethod.EXISTS_TYPE) {
         throw new OseeArgumentException("This predicate handler only supports [%s]", SearchMethod.EXISTS_TYPE);
      }
      List<String> typeParameters = predicate.getTypeParameters();
      Collection<String> values = predicate.getValues();

      Conditions.checkNotNullOrEmpty(typeParameters, "typeParameters");
      Conditions.checkNotNull(values, "values");

      if (typeParameters.size() >= 1) {
         String existsType = typeParameters.get(0);
         if ("attrType".equals(existsType)) {
            Collection<IAttributeType> attributeTypes = PredicateHandlerUtil.getIAttributeTypes(values);
            if (!attributeTypes.isEmpty()) {
               builder.andExists(attributeTypes);
            }
         } else if ("relType".equals(existsType)) {
            SearchOp op = predicate.getOp();
            for (IRelationType rt : PredicateHandlerUtil.getIRelationTypes(values)) {
               if (op == SearchOp.EQUALS) {
                  builder.andExists(rt);
               } else {
                  builder.andNotExists(rt);
               }
            }
         } else if ("relTypeSide".equals(existsType)) {
            SearchOp op = predicate.getOp();
            RelationSide side = typeParameters.get(1).equals("A") ? RelationSide.SIDE_A : RelationSide.SIDE_B;
            for (IRelationType rt : PredicateHandlerUtil.getIRelationTypes(values)) {
               IRelationTypeSide rts = TokenFactory.createRelationTypeSide(side, rt.getGuid(), "SearchRelTypeSide");
               if (op == SearchOp.EQUALS) {
                  builder.andExists(rts);
               } else {
                  builder.andNotExists(rts);
               }
            }
         }
      }

      return builder;
   }

}
