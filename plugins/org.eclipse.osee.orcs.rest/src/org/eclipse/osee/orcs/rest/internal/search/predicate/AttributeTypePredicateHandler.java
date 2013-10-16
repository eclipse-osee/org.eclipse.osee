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
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CaseType;
import org.eclipse.osee.framework.core.enums.MatchTokenCountType;
import org.eclipse.osee.framework.core.enums.Operator;
import org.eclipse.osee.framework.core.enums.TokenDelimiterMatch;
import org.eclipse.osee.framework.core.enums.TokenOrderType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.rest.internal.search.PredicateHandler;
import org.eclipse.osee.orcs.rest.model.search.Predicate;
import org.eclipse.osee.orcs.rest.model.search.SearchFlag;
import org.eclipse.osee.orcs.rest.model.search.SearchMethod;
import org.eclipse.osee.orcs.rest.model.search.SearchOp;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public class AttributeTypePredicateHandler implements PredicateHandler {

   @Override
   public QueryBuilder handle(QueryBuilder builder, Predicate predicate) throws OseeCoreException {
      if (predicate.getType() != SearchMethod.ATTRIBUTE_TYPE) {
         throw new OseeArgumentException("This predicate handler only supports [%s]", SearchMethod.ATTRIBUTE_TYPE);
      }
      List<SearchFlag> flags = predicate.getFlags();
      List<String> typeParameters = predicate.getTypeParameters();
      Collection<IAttributeType> attributeTypes = PredicateHandlerUtil.getIAttributeTypes(typeParameters);
      SearchOp op = predicate.getOp();
      Collection<String> values = predicate.getValues();
      Conditions.checkNotNull(values, "values");

      if (!containsAny(Collections.singleton(op), SearchOp.GREATER_THAN, SearchOp.LESS_THAN)) {
         CaseType ct = getCaseType(flags);
         TokenOrderType orderType = getTokenOrderType(flags);
         MatchTokenCountType countType = getMatchTokenCountType(flags);
         if (values.size() == 1) {
            builder =
               builder.and(attributeTypes, values.iterator().next(),
                  TokenDelimiterMatch.custom(predicate.getDelimiter()), ct, orderType, countType);
         } else {
            for (IAttributeType type : attributeTypes) {
               builder = builder.and(type, getOperator(op), values);
            }
         }
      } else {
         Operator operator = getOperator(op);
         for (IAttributeType type : attributeTypes) {
            builder = builder.and(type, operator, values);
         }
      }
      return builder;
   }

   private boolean containsAny(Collection<?> data, Object... values) {
      boolean result = false;
      for (Object object : values) {
         if (data.contains(object)) {
            result = true;
            break;
         }
      }
      return result;
   }

   private CaseType getCaseType(List<SearchFlag> flags) {
      if (flags != null && flags.contains(SearchFlag.MATCH_CASE)) {
         return CaseType.MATCH_CASE;
      }
      return CaseType.IGNORE_CASE;
   }

   private TokenOrderType getTokenOrderType(List<SearchFlag> flags) {
      if (flags != null && flags.contains(SearchFlag.MATCH_TOKEN_ORDER)) {
         return TokenOrderType.MATCH_ORDER;
      }
      return TokenOrderType.ANY_ORDER;
   }

   private MatchTokenCountType getMatchTokenCountType(List<SearchFlag> flags) {
      if (flags != null && flags.contains(SearchFlag.MATCH_TOKEN_COUNT)) {
         return MatchTokenCountType.MATCH_TOKEN_COUNT;
      }
      return MatchTokenCountType.IGNORE_TOKEN_COUNT;
   }

   //   EQUAL("="), // Exact Match as in Strings.equals
   //   NOT_EQUAL("<>"), // inverse of exact match - !Strings.equals
   //   LESS_THAN("<"),
   //   GREATER_THAN(">");
   private Operator getOperator(SearchOp op) {
      if (op.equals(SearchOp.GREATER_THAN)) {
         return Operator.GREATER_THAN;
      }
      if (op.equals(SearchOp.LESS_THAN)) {
         return Operator.LESS_THAN;
      }
      return Operator.EQUAL;
   }

}
