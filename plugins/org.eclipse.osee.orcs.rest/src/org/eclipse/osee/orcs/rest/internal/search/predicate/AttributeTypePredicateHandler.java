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
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.rest.internal.search.Predicate;
import org.eclipse.osee.orcs.rest.internal.search.PredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchFlag;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchMethod;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchOp;
import org.eclipse.osee.orcs.search.CaseType;
import org.eclipse.osee.orcs.search.Operator;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.StringOperator;

/**
 * @author John Misinco
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
      List<String> values = predicate.getValues();
      Conditions.checkNotNull(values, "values");

      if (isAttributeTokenSearch(op, flags) && !containsAny(Collections.singleton(op), SearchOp.GREATER_THAN,
         SearchOp.LESS_THAN)) {
         StringOperator operator = getStringOperator(op, flags);
         Conditions.checkNotNull(operator, "string operator",
            "Query error - cannot determine string operator from [%s]:[%s]", op, flags);
         CaseType ct = getCaseType(flags);
         for (String value : values) {
            builder = and(builder, attributeTypes, operator, ct, value);
         }
      } else {
         Operator operator = getOperator(op);
         for (IAttributeType type : attributeTypes) {
            builder = and(builder, type, operator, values);
         }
      }
      return builder;
   }

   protected QueryBuilder and(QueryBuilder builder, Collection<IAttributeType> attributeTypes, StringOperator operator, CaseType ct, String value) throws OseeCoreException {
      return builder.and(attributeTypes, operator, ct, value);
   }

   protected QueryBuilder and(QueryBuilder builder, IAttributeType type, Operator operator, List<String> values) throws OseeCoreException {
      return builder.and(type, operator, values);
   }

   private boolean isAttributeTokenSearch(SearchOp op, List<SearchFlag> flags) {
      return containsAny(flags, SearchFlag.TOKENIZED, SearchFlag.TOKENIZED_ANY, SearchFlag.TOKENIZED_ORDERED,
         SearchFlag.IGNORE_CASE) || containsAny(Collections.singleton(op), SearchOp.IN);
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
      if (flags.contains(SearchFlag.MATCH_CASE)) {
         return CaseType.MATCH_CASE;
      }
      return CaseType.IGNORE_CASE;
   }

   //   EQUALS(), // Exact Match as in Strings.equals
   //   NOT_EQUALS(), // inverse of exact match - !Strings.equals
   //   CONTAINS,
   //   TOKENIZED_ANY_ORDER,
   //   TOKENIZED_MATCH_ORDER

   private StringOperator getStringOperator(SearchOp op, List<SearchFlag> flags) {
      StringOperator toReturn;

      if (flags.contains(SearchFlag.TOKENIZED_ANY)) {
         toReturn = StringOperator.TOKENIZED_ANY_ORDER;
      } else if (flags.contains(SearchFlag.TOKENIZED_ORDERED)) {
         toReturn = StringOperator.TOKENIZED_MATCH_ORDER;
      } else {
         switch (op) {
            case EQUALS:
               toReturn = StringOperator.EQUALS;
               break;
            case NOT_EQUALS:
               toReturn = StringOperator.NOT_EQUALS;
               break;
            case IN:
               toReturn = StringOperator.CONTAINS;
               break;
            default:
               toReturn = null;
               break;

         }
      }
      return toReturn;
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
      if (op.equals(SearchOp.NOT_EQUALS)) {
         return Operator.NOT_EQUAL;
      }
      return Operator.EQUAL;
   }

}
