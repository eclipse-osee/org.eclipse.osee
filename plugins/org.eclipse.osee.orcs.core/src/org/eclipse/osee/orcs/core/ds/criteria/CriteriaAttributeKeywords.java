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

package org.eclipse.osee.orcs.core.ds.criteria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaAttributeKeywords extends Criteria {

   private final OrcsTokenService tokenService;
   private final Collection<AttributeTypeToken> attributeTypes;
   private final Collection<String> values;
   private final QueryOption[] options;
   private final boolean includeAllTypes;

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

   public Collection<AttributeTypeToken> getTypes() {
      return attributeTypes;
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
      Conditions.checkNotNullOrEmpty(getTypes(), "attribute types");
      checkMultipleValues();
      checkNotTaggable();
   }

   @Override
   public String toString() {
      return String.format("CriteriaAttributeKeyword [attributeType=%s, value=%s, options=%s]", attributeTypes,
         Collections.toString(",", values), Collections.toString(",", Arrays.asList(options)));
   }

   private void checkMultipleValues() {
      if (getTypes().size() > 1 && getValues().size() > 1) {
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
}
