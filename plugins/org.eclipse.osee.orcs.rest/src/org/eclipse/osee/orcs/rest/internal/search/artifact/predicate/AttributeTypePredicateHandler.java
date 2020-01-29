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
public class AttributeTypePredicateHandler implements PredicateHandler {

   @Override
   public QueryBuilder handle(OrcsApi orcsApi, QueryBuilder builder, Predicate predicate) {
      if (predicate.getType() != SearchMethod.ATTRIBUTE_TYPE) {
         throw new OseeArgumentException("This predicate handler only supports [%s]", SearchMethod.ATTRIBUTE_TYPE);
      }
      List<String> typeParameters = predicate.getTypeParameters();
      Collection<AttributeTypeId> attributeTypes = PredicateHandlerUtil.getAttributeTypes(typeParameters);
      Collection<String> values = predicate.getValues();
      Conditions.checkNotNull(values, "values");

      if (values.size() == 1) {
         builder = builder.and(attributeTypes, values.iterator().next(), predicate.getOptions());
      } else {
         for (AttributeTypeId type : attributeTypes) {
            builder = builder.and(type, values, predicate.getOptions());
         }
      }
      return builder;
   }

}
