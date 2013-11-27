/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds.criteria;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.Operator;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaAttributeOther extends Criteria {

   private final IAttributeType attributeType;
   private final Collection<String> values;
   private final Operator operator;

   public CriteriaAttributeOther(IAttributeType attributeType, Collection<String> values, Operator operator) {
      super();
      this.attributeType = attributeType;
      this.values = values;
      this.operator = operator;
   }

   public IAttributeType getAttributeType() {
      return attributeType;
   }

   public Collection<String> getValues() {
      return values;
   }

   public Operator getOperator() {
      return operator;
   }

   @Override
   public void checkValid(Options options) throws OseeCoreException {
      super.checkValid(options);
      Conditions.checkNotNull(getAttributeType(), "attributeType");
      Conditions.checkExpressionFailOnTrue(getAttributeType().equals(QueryBuilder.ANY_ATTRIBUTE_TYPE),
         "Any attribute type is not allowed");

      Operator operator = getOperator();
      for (String value : getValues()) {
         if (value != null && value.contains("%") && operator.isGreaterThanOrLessThan()) {
            throw new OseeArgumentException(
               "When value contains %%, one of the following operators must be used: %s, %s", Operator.EQUAL,
               Operator.NOT_EQUAL);
         }
      }
   }

   @Override
   public String toString() {
      return "CriteriaAttributeOther [attributeType=" + attributeType + ", values=" + values + ", operator=" + operator + "]";
   }

}
