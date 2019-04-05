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

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.search.ArtifactQueryBuilder;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaAttributeRaw extends Criteria {

   public static final List<QueryOption> VALID_OPTIONS =
      Arrays.asList(QueryOption.TOKEN_COUNT__MATCH, QueryOption.TOKEN_DELIMITER__EXACT,
         QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.CASE__IGNORE, QueryOption.CASE__MATCH);

   private final Collection<AttributeTypeId> attributeTypes;
   private final Collection<String> values;
   private final List<QueryOption> options;

   public CriteriaAttributeRaw(Collection<AttributeTypeId> attributeTypes, Collection<String> values, QueryOption... options) {
      super();
      this.attributeTypes = attributeTypes;
      this.values = values;
      this.options = Lists.newArrayList(options);
   }

   public Collection<AttributeTypeId> getAttributeTypes() {
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
      Conditions.checkExpressionFailOnTrue(getAttributeTypes().equals(ArtifactQueryBuilder.ANY_ATTRIBUTE_TYPE),
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
