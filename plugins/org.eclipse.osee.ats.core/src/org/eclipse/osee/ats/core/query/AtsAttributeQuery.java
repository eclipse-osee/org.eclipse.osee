/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.query;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.QueryOption;

/**
 * @author Donald G. Dunne
 */
public class AtsAttributeQuery {

   private AttributeTypeId attrType;
   private Collection<String> values;
   private QueryOption[] queryOption;

   public AtsAttributeQuery(AttributeTypeId attrType, Collection<String> values, QueryOption... queryOption) {
      this.attrType = attrType;
      this.values = values;
      this.setQueryOption(queryOption);
   }

   public AtsAttributeQuery(AttributeTypeId attrType, String value, QueryOption... queryOption) {
      this(attrType, Arrays.asList(value), queryOption);
      this.setQueryOption(queryOption);
   }

   public AttributeTypeId getAttrType() {
      return attrType;
   }

   public void setAttrType(AttributeTypeId attrType) {
      this.attrType = attrType;
   }

   public Collection<String> getValues() {
      return values;
   }

   public void setValues(Collection<String> values) {
      this.values = values;
   }

   public QueryOption[] getQueryOption() {
      return queryOption;
   }

   public void setQueryOption(QueryOption[] queryOption) {
      this.queryOption = queryOption;
   }

}
