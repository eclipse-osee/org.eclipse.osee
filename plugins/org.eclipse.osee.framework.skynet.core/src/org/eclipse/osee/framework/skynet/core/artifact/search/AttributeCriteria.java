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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.Operator;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.client.QueryBuilder;

/**
 * @author Ryan D. Brooks
 */
public class AttributeCriteria implements ArtifactSearchCriteria {

   private final IAttributeType attributeType;
   private String value;
   private Collection<String> values;
   private final Operator operator;
   private final QueryOption[] options;

   public IAttributeType getAttributeType() {
      return attributeType;
   }

   public String getValue() {
      return value;
   }

   public Collection<String> getValues() {
      return values;
   }

   public Operator getOperator() {
      return operator;
   }

   public QueryOption[] getOptions() {
      return options;
   }

   /**
    * Constructor for search criteria that finds an attribute of the given type with its current value equal to the
    * given value.
    * 
    * @param value to search;
    */
   public AttributeCriteria(IAttributeType attributeType, String value, QueryOption... options) {
      this(attributeType, value, null, Operator.EQUAL, options);
   }

   /**
    * Constructor for search criteria that finds an attribute of the given type and any value (i.e. checks for
    * existence)
    */
   public AttributeCriteria(IAttributeType attributeType) {
      this(attributeType, null, null, Operator.EQUAL);
   }

   /**
    * Constructor for search criteria that finds an attribute of the given type with its current value exactly equal to
    * any one of the given literal values. If the list only contains one value, then the search is conducted exactly as
    * if the single value constructor was called. This search does not support the wildcard for multiple values. Throws
    * OseeArgumentException values is empty or null
    */
   public AttributeCriteria(IAttributeType attributeType, Collection<String> values) throws OseeCoreException {
      this(attributeType, null, validate(values), Operator.EQUAL);
   }

   /**
    * Constructor for search criteria that finds an attribute of the given type with its current value relative to the
    * given value based on the operator provided.
    */
   public AttributeCriteria(IAttributeType attributeType, String value, Operator operator) {
      this(attributeType, value, null, operator);
   }

   /**
    * Constructor for search criteria that finds an attribute of the given type with its current value exactly equal (or
    * not equal) to any one of the given literal values. If the list only contains one value, then the search is
    * conducted exactly as if the single value constructor was called. This search does not support the wildcard for
    * multiple values.
    */
   public AttributeCriteria(IAttributeType attributeType, Collection<String> values, Operator operator) throws OseeCoreException {
      this(attributeType, null, validate(values), operator);
   }

   private static Collection<String> validate(Collection<String> values) throws OseeArgumentException {
      if (values == null || values.isEmpty()) {
         throw new OseeArgumentException("values provided to AttributeCriteria must not be null or empty");
      }
      return values;
   }

   public AttributeCriteria(IAttributeType attributeType, String value, Collection<String> values, Operator operator, QueryOption... options) {
      this.attributeType = attributeType;

      if (values == null) {
         this.value = value;
      } else {
         if (values.size() == 1) {
            this.value = values.iterator().next();
         } else {
            this.values = values;
         }
      }
      this.operator = operator;

      if (this.value != null && operator == Operator.EQUAL && options.length == 0) {
         this.options = QueryOptions.EXACT_MATCH_OPTIONS;
      } else {
         this.options = options;
      }
   }

   @Override
   public String toString() {
      StringBuilder strB = new StringBuilder();
      if (attributeType != null) {
         strB.append(attributeType);
      } else {
         strB.append("*");
      }
      strB.append(" ");
      strB.append(operator);
      strB.append(" ");
      if (value != null) {
         strB.append(value);
      }

      return strB.toString();
   }

   @Override
   public void addToQueryBuilder(QueryBuilder builder) throws OseeCoreException {
      if (Strings.isValid(getValue())) {
         if (getOperator() == Operator.EQUAL) {
            builder.and(getAttributeType(), getValue(), getOptions());
         } else {
            builder.and(getAttributeType(), getOperator(), getValue());
         }
      } else if (getValues() != null) {
         builder.and(getAttributeType(), getOperator(), getValues());
      } else {
         builder.andExists(getAttributeType());
      }
   }
}