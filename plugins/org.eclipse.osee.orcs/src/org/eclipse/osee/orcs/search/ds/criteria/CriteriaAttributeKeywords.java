/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.search.ds.criteria;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.search.ds.Criteria;
import org.eclipse.osee.orcs.search.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaAttributeKeywords extends Criteria {

   private final OrcsTokenService tokenService;
   private Collection<AttributeTypeToken> attributeTypes;
   private Collection<String> values;
   private QueryOption[] options;
   private boolean includeAllTypes;

   public CriteriaAttributeKeywords() {
      // for jax-rs
      this(false, null, null, (String) null);
   }

   public CriteriaAttributeKeywords(boolean includeAllTypes, Collection<AttributeTypeToken> attributeTypes, OrcsTokenService tokenService, Collection<String> values, QueryOption... options) {
      super();
      this.includeAllTypes = includeAllTypes;
      this.tokenService = tokenService;
      this.attributeTypes = attributeTypes;
      this.values = values;
      this.options = options;
   }

   public CriteriaAttributeKeywords(boolean includeAllTypes, Collection<AttributeTypeToken> attributeTypes, OrcsTokenService tokenService, String value, QueryOption... options) {
      this(includeAllTypes, attributeTypes, tokenService, java.util.Collections.singleton(value), options);
   }

   public boolean isIncludeAllTypes() {
      return includeAllTypes;
   }

   public Collection<AttributeTypeToken> getAttributeTypes() {
      return attributeTypes;
   }

   public void setAttributeTypes(Collection<AttributeTypeToken> attributeTypes) {
      this.attributeTypes = attributeTypes;
   }

   public Collection<String> getValues() {
      return values;
   }

   public QueryOption[] getOptions() {
      return options;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkNotNullOrEmpty(getValues(), "search value");
      Conditions.checkNotNullOrEmpty(getAttributeTypes(), "attribute types");
      checkMultipleValues();
      checkNotTaggable();
   }

   @Override
   public String toString() {
      return String.format("CriteriaAttributeKeyword [attributeType=%s, value=%s, options=%s]", attributeTypes,
         Collections.toString(",", values), Collections.toString(",", Arrays.asList(options)));
   }

   private void checkMultipleValues() {
      if (getAttributeTypes().size() > 1 && getValues().size() > 1) {
         throw new OseeArgumentException("Multiple values is not valid with multiple types");
      }
   }

   public void checkNotTaggable() {
      if (!includeAllTypes) {
         ArrayList<String> notTaggable = new ArrayList<>();
         if (tokenService != null) {
            for (AttributeTypeToken type : attributeTypes) {
               if (!tokenService.getAttributeType(type.getId()).isTaggable()) {
                  notTaggable.add(type.toString());
               }
            }
            if (!notTaggable.isEmpty()) {
               throw new OseeArgumentException("Attribute types [%s] is not taggable", notTaggable.toString());
            }
         }
      }
   }

   @JsonIgnore
   public OrcsTokenService getTokenService() {
      return tokenService;
   }

   public void setValues(Collection<String> values) {
      this.values = values;
   }

   public void setOptions(QueryOption[] options) {
      this.options = options;
   }

   public void setIncludeAllTypes(boolean includeAllTypes) {
      this.includeAllTypes = includeAllTypes;
   }
}
