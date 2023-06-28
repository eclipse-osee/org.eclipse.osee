/*********************************************************************
 * Copyright (c) 2015 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.core.query;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.QueryOption;

/**
 * @author Donald G. Dunne
 */
public class AtsAttributeQuery {

   private AttributeTypeToken attrType;
   private Collection<String> values;
   private QueryOption[] queryOption;

   public AtsAttributeQuery(AttributeTypeToken attrType, Collection<String> values, QueryOption... queryOption) {
      this.attrType = attrType;
      this.values = values;
      if (queryOption.length > 0) {
         this.setQueryOption(queryOption);
      } else {
         this.setQueryOption(QueryOption.CONTAINS_MATCH_OPTIONS);
      }
   }

   public AtsAttributeQuery(AttributeTypeToken attrType, String value, QueryOption... queryOption) {
      this(attrType, Arrays.asList(value), queryOption);
      this.setQueryOption(queryOption);
   }

   public AttributeTypeToken getAttrType() {
      return attrType;
   }

   public void setAttrType(AttributeTypeToken attrType) {
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
