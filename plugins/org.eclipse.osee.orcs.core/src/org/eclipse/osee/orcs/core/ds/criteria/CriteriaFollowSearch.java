/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Audrey Denk
 */
public class CriteriaFollowSearch extends Criteria {

   private final Collection<AttributeTypeId> attributeTypes;
   private final Collection<String> values;
   private final QueryOption[] options;
   private final boolean includeAllTypes;

   public CriteriaFollowSearch(boolean includeAllTypes, Collection<AttributeTypeId> attributeTypes, Collection<String> values, QueryOption... options) {
      super();
      this.includeAllTypes = includeAllTypes;
      this.attributeTypes = attributeTypes;
      this.values = values;
      this.options = options;

   }

   public CriteriaFollowSearch(boolean includeAllTypes, Collection<AttributeTypeId> attributeTypes, String value, QueryOption... options) {
      this(includeAllTypes, attributeTypes, java.util.Collections.singleton(value), options);
   }

   public boolean isIncludeAllTypes() {
      return includeAllTypes;
   }

   public Collection<AttributeTypeId> getTypes() {
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
}
