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
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.client.QueryBuilder;

/**
 * @author Ryan D. Brooks
 */
public class AttributeCriteria implements ArtifactSearchCriteria {

   private final IAttributeType attributeType;
   private String value;
   private Collection<String> values;
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
      this(attributeType, value, null, options);
   }

   /**
    * Constructor for search criteria that finds an attribute of the given type and any value (i.e. checks for
    * existence)
    */
   public AttributeCriteria(IAttributeType attributeType, QueryOption... options) {
      this(attributeType, null, null, options);
   }

   /**
    * Constructor for search criteria that finds an attribute of the given type with its current value exactly equal (or
    * not equal) to any one of the given literal values. If the list only contains one value, then the search is
    * conducted exactly as if the single value constructor was called. This search does not support the wildcard for
    * multiple values.
    */
   public AttributeCriteria(IAttributeType attributeType, Collection<String> values) throws OseeCoreException {
      this(attributeType, null, validate(values));
   }

   private static Collection<String> validate(Collection<String> values) throws OseeArgumentException {
      if (values == null || values.isEmpty()) {
         throw new OseeArgumentException("values provided to AttributeCriteria must not be null or empty");
      }
      return values;
   }

   public AttributeCriteria(IAttributeType attributeType, String value, Collection<String> values, QueryOption... options) {
      this.attributeType = attributeType;

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
   public void addToQueryBuilder(QueryBuilder builder) throws OseeCoreException {
      if (Strings.isValid(getValue())) {
         builder.and(getAttributeType(), getValue(), getOptions());
      } else if (getValues() != null) {
         builder.and(getAttributeType(), getValues(), getOptions());
      } else {
         builder.andExists(getAttributeType());
      }
   }
}