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
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.rest.client.QueryBuilder;

/**
 * @author Ryan D. Brooks
 */
public class AttributeCriteria implements ArtifactSearchCriteria {

   private final AttributeTypeId attributeType;
   private String value;
   private Collection<String> values;
   private final QueryOption[] options;
   private final boolean existsSearch;

   public AttributeTypeId getAttributeType() {
      return attributeType;
   }

   public String getValue() {
      return value;
   }

   public Collection<String> getValues() {
      return values;
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
   public AttributeCriteria(AttributeTypeId attributeType, String value, QueryOption... options) {
      this(attributeType, value, null, false, options);
   }

   /**
    * Constructor for search criteria that finds an attribute of the given type and any value (i.e. checks for
    * existence)
    */
   public AttributeCriteria(AttributeTypeId attributeType, QueryOption... options) {
      this(attributeType, null, null, true, options);
   }

   /**
    * Constructor for search criteria that finds an attribute of the given type with its current value exactly equal (or
    * not equal) to any one of the given literal values. If the list only contains one value, then the search is
    * conducted exactly as if the single value constructor was called. This search does not support the wildcard for
    * multiple values.
    */
   public AttributeCriteria(AttributeTypeId attributeType, Collection<String> values)  {
      this(attributeType, null, validate(values), false);
   }

   private static Collection<String> validate(Collection<String> values)  {
      if (values == null || values.isEmpty()) {
         throw new OseeArgumentException("values provided to AttributeCriteria must not be null or empty");
      }
      return values;
   }

   private AttributeCriteria(AttributeTypeId attributeType, String value, Collection<String> values, boolean existsSearch, QueryOption... options) {
      this.attributeType = attributeType;
      this.existsSearch = existsSearch;

      if (!Conditions.hasValues(values)) {
         this.value = value;
      } else {
         if (values.size() == 1) {
            this.value = values.iterator().next();
         } else {
            this.values = values;
         }
      }
      this.options = options;
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
      if (value != null) {
         strB.append(value);
      }

      return strB.toString();
   }

   @Override
   public void addToQueryBuilder(QueryBuilder builder)  {
      if (existsSearch) {
         builder.andExists(getAttributeType());
      } else if (getValues() != null) {
         builder.and(getAttributeType(), getValues(), getOptions());
      } else {
         builder.and(getAttributeType(), getValue(), getOptions());
      }
   }
}