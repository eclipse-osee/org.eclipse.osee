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

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaAttributeRaw extends Criteria {

   public static final List<QueryOption> VALID_OPTIONS =
      Arrays.asList(QueryOption.TOKEN_COUNT__MATCH, QueryOption.TOKEN_DELIMITER__EXACT,
         QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.CASE__IGNORE, QueryOption.CASE__MATCH);

   private final Collection<AttributeTypeToken> attributeTypes;
   private final Collection<String> values;
   private final List<QueryOption> options;

   public CriteriaAttributeRaw(Collection<AttributeTypeToken> attributeTypes, Collection<String> values, List<QueryOption> options) {
      this.attributeTypes = attributeTypes;
      this.values = values;
      this.options = options;
   }

   public CriteriaAttributeRaw(Collection<AttributeTypeToken> attributeTypes, Collection<String> values, QueryOption... options) {
      this(attributeTypes, values, Arrays.asList(options));
   }

   public CriteriaAttributeRaw(Collection<AttributeTypeToken> attributeTypes, Collection<String> values) {
      this(attributeTypes, values, java.util.Collections.emptyList());
   }

   public Collection<AttributeTypeToken> getAttributeTypes() {
      return attributeTypes;
   }

   public Collection<String> getValues() {
      return values;
   }

   public List<QueryOption> getOptions() {
      return options;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkNotNullOrEmptyOrContainNull(getAttributeTypes(), "attributeType");
      Conditions.checkExpressionFailOnTrue(getAttributeTypes().equals(QueryBuilder.ANY_ATTRIBUTE_TYPE),
         "Any attribute type is not allowed");

      List<QueryOption> unsupportedOptions = getUnsupportedOptions();
      if (unsupportedOptions.size() != 0) {
         throw new OseeArgumentException("Invalid QueryOptions present: [%s]",
            Collections.toString(",", unsupportedOptions));
      }
   }

   private List<QueryOption> getUnsupportedOptions() {
      ArrayList<QueryOption> selectedOptions = Lists.newArrayList(options);
      selectedOptions.removeAll(VALID_OPTIONS);
      return selectedOptions;
   }

   @Override
   public String toString() {
      return "CriteriaAttributeOther [attributeTypes=" + Collections.toString(",",
         attributeTypes) + ", values=" + values + ", options=" + Collections.toString(",", options) + "]";
   }

}
